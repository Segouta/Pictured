'use strict';

// [START imports]
var firebase = require('firebase-admin');
// [END imports]

var serviceAccount = require("./key.json");
firebase.initializeApp({
    credential: firebase.credential.cert(serviceAccount),
    databaseURL: "https://snapthat-39447.firebaseio.com"
}); // [END initialize]

const playTime = 25 * 60; //s
const nextGameTimeMin = 5 * 60; //s
const nextGameTimeMax = 90 * 60; //s

const minimumInterval = playTime + nextGameTimeMin; //s
const maximumInterval = playTime + nextGameTimeMax; //s

function writeStuff(time, index) {
    var updates = {};
    updates['/endMillis'] = new Date(time).getTime();
    updates['/index'] = index;
    console.log("Writing: new values", updates);

    firebase.database().ref('/currentThing/').update(updates, function(err) {
        if (err) {
            console.log(err);
        } else {
            sendMessage();
        }
    });
}
write();

function write() {
    var allThings = firebase.database().ref('/things/').once('value').then(function(value) {
        var things = value.val();
        var thingsLength = things.length;
        var currentThing = firebase.database().ref('/currentThing/').once('value').then(function(currentValue) {
            console.log("Reading: current values", currentValue.val());
            var currentIndex = currentValue.val().index;
            currentIndex++;
            currentIndex = currentIndex % thingsLength;
            writeStuff(new Date().getTime() + playTime * 1000, currentIndex);
        });
    });
}

function sendMessage() {
    // The topic name can be optionally prefixed with "/topics/".
    var topic = "news";

    // See the "Defining the message payload" section below for details
    // on how to define a message payload.
    var payload = {
        notification: {
            title: "A new thing has dropped!",
            body: "Click here to check it out!"
        }
    };
    // Send a message to devices subscribed to the provided topic.
    firebase.messaging().sendToTopic(topic, payload)
        .then(function(response) {
            // See the MessagingTopicResponse reference documentation for the
            // contents of response.
            console.log("Successfully sent message:", response);
        })
        .catch(function(error) {
            console.log("Error sending message:", error);
        });
}
// write();

(function interval() {
    var randomInterval = (Math.floor(Math.random() * (maximumInterval - minimumInterval)) + minimumInterval) * 1000;
    var nu = new Date();
    console.log("---------------------------------------------------------")
    console.log("Current time: " + nu.getHours() + ":" + nu.getMinutes());
    var dan = new Date(nu.getTime() + randomInterval)
    console.log("Dropping new object at: " + dan.getHours() + ":" + dan.getMinutes());
    console.log("Dropping object in %s seconds", randomInterval / 1000);
    console.log("That is %s minutes from now", randomInterval / 60000);
    setTimeout(function() {
        write();
        interval();
    }, randomInterval)
}());
