# Report

By Christian Bijvoets in the context of the Minor Programmeren at the UvA, january 2018.

[![BCH compliance](https://bettercodehub.com/edge/badge/Segouta/Pictured?branch=master)](https://bettercodehub.com/)

## Description

SnapThat is a game in what players have to find objects in real life and photograph (snap) them.
Every 30-120 minutes, a new object becomes available in a present in the play-screen.
This is regulated by a server script that is written in node.js and runs on my own computer but will run on a raspberry pi in the futur, so that the game can be played whenever you want.
Moreover, the user will receive a notification when this happens.
This present can be opened for a limited time, 25 minutes.
When the user opens the present, the user sees what object needs to be found.
The camera can be opened, and when returning from the camera, the image will be analyzed.
If the tags from the analysis contain the thing that needed to be found, the timer stops, and the time it took the user to accomplish this since the present was opened is saved as the score.
The user now can see how he is doing on the social page.
Not only are the amount of played games available, but also the score from last game, and the average score of the last 10 games.
This average is called the SnapStreak, and keeping this number low is difficult. You can share your SnapStreak over various social media to compare with friends.

In the user screen, users can logout, and in the settings screen, users can see some credits.
When a user loses internet connection, the app will redirect the user to the main activity, and disable options that require internet connection.


The mainactivity:

![mainactivity](https://github.com/Segouta/Pictured/blob/master/doc/main_screenshot.jpeg)

## Technical design

The app consists out of **6** activities:

### Login Activity

Description:

Here, the user can log in with a Google account. Firebase supports this login, so the functions are pretty straight forward. Also, a check is performed to see if the user has already logged in some time, and if not, the user is created in Firebase database to store game data etc.


Related classes and notable features:

* When a user logs in for the first time, a piece of Firebase database is reserved for the information of this user. This information is put there using an instance of UserData, a class containing e-mail, username, subscriptionDate, amount of games played, a list with the last 10 scores, and an instance of GameData. GameData contains a few more items: the layout state, the timestamp of when the last opened present will expire (to check if the user is playing the game that is currently running), the last score time, the time when the last thing was found, and the time when the last present was opened.


### Main Activity

Description:

In this activity, players can navigate to the different screens of the app. There are animations on each button, and the user button has a special feature: it contains the profile picture of the current user logged in. Also, the first name is displayed below that picture.


Related classes and notable features:

* UserActivity is called when the authorization has become invalid. It calls the signOut() and goToLoginActivity() from the Useractivity when this happens.
* A listener is set to check wheather the internet connection is lost. This listener stays active when navigating to another activity, because the main acitivity is not closed when navigating. When connection is lost, the app returns to MainActivity when not already there, and then disables the buttons to prevent from going to screens that require internet to load data. This listener activates the finish() method of the foreground activities using the WifiCheckInterface interface.
 

### User Activity

Description:

Here, the user can simply log out. In this activity, the users profile picture will be displayed, along with the user's first name from its Google account.


Related classes and notable features:

* The logout button too has an animation.
* When you log out, all affinity to other activities (activity history) will be cleared, so that one cannot navigate back into logged in pages.


### Play Activity

Description:

In this activity, the whole playing of the game happens. There are different states that the layout can be in, when this screen is opened the correct state will be loaded from firebase and set. These are the most important states:
* When a present has arrived but not yet opened, it displays a present with some animations and a timer how long the user can still open the present and participate in the game. 
* When the user enters the PlayActivity and the endtime of the game has been reached and no new game has been started by the server, just a message will show up.
* When the user has opened the present however, the name of the object and the remaining time will be revealed and the user will be able to click a camera button, that brings the user with an intent to the devices camera-app. When returning, the token image will be analyzed using Microsoft Azure. Points are rewarded if the correct thing was found in the image.
* When the user has failed, the camera button will stay there. This state is called "attempted".


Related classes and notable features:

* A whole bunch of animations have been created to provide the user with an intuitive, responsive layout.
* An instance of CameraManager has been created. The CameraManager manages the camera and saves the image on the right location in the device's storage. It also provides the filePath and URI.
* An instance of MSVisionManager is created to provide the checking and tagging functionality of the taken picture.
* An instance of UserServerManager is created to retrieve the information needed to display the correct times and layouts in the playactivity.
* An instance of ServerManager is created to retrieve the information about the current thing to find and endTime of the current game.


### Social Activity

Description:

In this activity, one can see the current SnapStreak, the last played game's score time and the amount of games played. Also, there is a little info button with information about the meaning of a SnapStreak. Moreover, one can share the last results with friends via an intent to various different social media.


Related classes and notable features:

* An instance of SocialServerManager is created to retrieve the information like SnapStreak, amount of games played and last game's score time.
* Buttens are animated when clicked.


### Settings Activity

Description:

In this activity, one can see information about the creator of the app for now. This activity is ready for future development.




Next to the activities, there are some things one should know about the **server**:

A server is written, to release new objects every 30-120 minutes. When this happens, the server script changes the index of the current word users have to find, and changes the endtime to 25 minutes from now. Immediately after this, every user of the app is notified..
The idea is that this can be run on any small server that is attached to the internet, such as a Raspberry Pi.
This is what the server script looks like while executed:

![server preview](https://github.com/Segouta/Pictured/blob/master/doc/serverpreview.PNG)


## Changes and development changes

Changes:

A lot has changed since the first week of this project. A lot of functionalities have changed, but moreover, the nature of the game has changed. The former idea was that people would play this game in groups. There would be one group manager who create the game and assign objects to be found by the group. Now, every user of the app has to find the same item, and a server will update the to be found item and the endtime every so much time.
Features that have been changed:
* Replaced Google Vision by [Microsoft Azure computer vision](https://azure.microsoft.com/nl-nl/services/cognitive-services/computer-vision/), because Google wanted my payment details. Microsoft Azure is not as good as Google Vision, but good enough for the current needs of the app.
* The words are not generated from the Random Word API, instead I created a large list of words in the database. This way the words can be managed better (not showing words twice within 100 games for example.
* Points are only calculated based on time, not on distance travelled. This might be a feature for future development though.
* The database structure has changed quite a bit, because there are no groups or friends.
* Players can not scroll through recent snaps they made, because the decision was made to limit the storage use of this app to the bare minimum.


Challenges:

* Since Android 8, background services are [no longer a thing](https://blog.klinkerapps.com/android-o-background-services/) one can easily use in apps. I created a background service that listened for changes in the thing-child in firebase, and would send the user a notification automatically, but when testing on different devices, only for Android versions lower than 8, it worked. Since my own phone had Android 8, I wanted to make this work. This is where the decision was made that Firebase Cloud Messaging had to be implemented. But for that, a server script had to be created. This was quite a challenge, because that script had to be written in node.js, something unknown to me.
* Saving PlayActivity's Layout and all the timers and states was quite challenging too. I implemented a state variable in Firebase, and based on this, the correct layout is displayed.
* Preventing the user from getting to activities where an internet connection is required was another challenge. It required me to create an Interface that lets the MainActivity close other activities from the background if internet connection loss happens.
* All the animations and smooth transitions were the last challenge. Since we had not learned anything about them, using them was an interesting decision.


## Trade-offs

Looking back to this project, I think there are a few things that could be improved, but I think this is a very good starting point of an app that could potentially become a funny game. The biggest decision was to use a server to update the database and notify all users when new items are available. This decision was made fairly late in the design process, but I think it is the base of a much stronger game concept. In an ideal world with more time, I would definately upgrade the server, so that he would also produce ranking lists and make the option available to see who of your friends is the best, and reward people who keep their SnapStreak low etc.
Also, the UI and UX can be improved a lot. Although I think the current interface is pretty user friendly, there are some conventions I think can be implemented to make it better.
