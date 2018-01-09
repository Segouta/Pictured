# SnapThat

![visual sketch](https://github.com/Segouta/Pictured/blob/master/doc/SnapThat.png)

# DESIGN

This file elaborates on the design of the SnapThat-app.

### *Advanced sketch*

![visual sketch](https://github.com/Segouta/Pictured/blob/master/doc/visual_sketch.jpg)

### *Additional conceptual images*

![login screen](https://github.com/Segouta/Pictured/blob/master/doc/login.PNG)
![main activity](https://github.com/Segouta/Pictured/blob/master/doc/main.PNG)

### *Explanation*

#### **1. Login activity**
    * Users can login or sign in.
    * Uses firebase to manage users.
    
    *Methods:*
    * createAccount(): Creates a user account in firebase. After that, the user is redirected to the next activity.
    * logIn(): Logs a user in, does all the checks for that etc.
    * goToMainActivity(): Redirects to main activity.
    * onStart() and onStop(): Will contain code that checks if already logged in.
    * addUserToDB(): Adds the user to the database, creates a place where his/her data is stored such as friends, accomplishments etc.
    * doesUsernameExist(): Checks if the filled in Username is already in use or not.

#### **2. Main activity**
    * Users can scroll through recent finds using a horizontal scrollable listview with adapter that manages the layout of each "tile".
    * Users can click on the large "FIND" button, then a random object's name is shown.

    *Methods:*
    * GPS methods such as onLocationChanged(), onStatusChanged(), onProviderEnabled(), onProviderDisabled().
    * getUsername(): Retrieves username from database to display welcome message.
    * getLocation(): Gets current gps location.
    * logOut(): Logges the user out when log out is clicked.
    * goToLoginActivity(): Brings the login activity back up screen.
    * goToPlayActivity(): Brings the play/camera activity up.
    * goToObjectActivity(): Brings the object summary up in a new activity.
    * safeTimeAndLocationStart(): When the user clicks on start, the time and location are saved of that moment.
      
#### **3. Camera activity**
    * Shows the name of a random object the user has to find. Does this via a html request that is stripped to the wanted part.
    * Shows a very basic camera screen, uses android's camera API.
    * Once a picture is taken, on to the next activity.
    
    *Methods:*
    * safeImage(): Saves taken image to firebase.
    * goToCheckActivity(): Goes to the next activity.
    * safeTimeAndLocationEnd(): When the user clicks on the photo button, the time and location are saved of that moment.

#### **4. Checking activity**
    * Check if the picture is graded as valid for the requested object using Google Vision API.
    * Shows a spinner during waiting time for Vision to check.
    
    *Methods:*
    * checkCorrectness(): Uses Google Vision API to check if a photographed item is indeed what was asked.
    * goToRewardActivity(): goes to the reward activity.

#### **5. Point rewarding activity**
    * Being able to challenge friends to try the same object in a better time then yours.
    * Uses firebase to search other friends.
    
    *Methods:*
    * rewardPoints(): Calculates how much points a user has earned based on the time spend to find the object and distance travelled to find it.
    * goToMainAcitvity(): Goes back to the main activity.
    
#### **6. Summary activity**
    * Summarizes recent found of the user.
    
    *Methods:*
    * getObjectInfo(): retrieves object info of that user such as when was it found, what was the amount of points that were given, what was the time and distance etc.
   
   
### *API'S AND LIBRARIES*

* [JSON Google Vision API for object check](https://cloud.google.com/vision/?utm_source=google&utm_medium=cpc&utm_campaign=emea-nl-all-nl-dr-bkws-all-all-trial-e-gcp-1003963&utm_content=text-ad-none-any-DEV_c-CRE_170512857568-ADGP_Desk+%7C+AW+SEM+%7C+BKWS+~+EXA_1%3A1_NL_NL_ML_Vision+API_google+vision+api-KWID_43700016973722688-kwd-203288731687-userloc_9064817&utm_term=KW_google%20vision%20api-ST_google+vision+api&ds_rl=1245734&gclid=Cj0KCQiAyszSBRDJARIsAHAqQ4pR8oo2cGZfocML-IIAcj9TMGbFpLQvhIGmITbpbAr9nqz_kU3C7tsaAjr-EALw_wcB&dclid=CJSVn6eoydgCFdQ44AodzhkHTA)

* [Camera API](https://developer.android.com/guide/topics/media/camera.html)

* [Google Maps GPS API](https://developers.google.com/maps/documentation/android-api/)

* [Firebase](https://firebase.google.com/)

### *Data sources*

* [Random object generator](https://www.randomlists.com/things)
* If this does not work, I will create a list with random object in the firebase database.


### *Database structure*

This project uses Firebase as a database. The basic structure is the following:
```
* SnapThat
            * Users
                      * Christian
                                  * Current toFind
                                  * Found objects
                                  * Points
                                  * Friends
                                  * E-mail
                      * David
                      * Britt
                      * etc.
            * Objects
                      * Key
                                  * Synoniemen
                                  * How many people searched this
                                  * List of dates it was found
                                  * etc.
                      * Ball
                      * Car
                      * etc.
```
            



