# SnapThat

![visual sketch](https://github.com/Segouta/Pictured/blob/master/doc/SnapThat.png)

# DESIGN

This file elaborates on the design of the SnapThat-app.

### *Advanced sketch*

![visual sketch](https://github.com/Segouta/Pictured/blob/master/doc/visual_sketch.jpg)

### *Additional conceptual images*

![login screen](https://github.com/Segouta/Pictured/blob/master/doc/login.png)
![main activity](https://github.com/Segouta/Pictured/blob/master/doc/main.png)

### *Explanation*

1. Login activity
    * Users can login or sign in.
    * Uses firebase to manage users.
    
    _Methods:_
    * 

2. Main activity
    * Users can scroll through recent finds using a horizontal scrollable listview with adapter that manages the layout of each "tile".
    * Users can click on the large "FIND" button, then a random object's name is shown.

3. Camera activity
    * Shows a very basic camera screen, uses android's camera API.
    * Once a picture is taken, on to the next activity.

4. Checking activity
    * Check if the picture is graded as valid for the requested object using Google Vision API.
    * Shows a spinner during waiting time for Vision to check.

5. Point rewarding activity
    * Being able to challenge friends to try the same object in a better time then yours.
    * Uses firebase to search other friends.
    
Summary activity
    * Summarizes recent found of the user.

### *API'S AND LIBRARIES*

* [JSON Google Vision API for object check](https://cloud.google.com/vision/?utm_source=google&utm_medium=cpc&utm_campaign=emea-nl-all-nl-dr-bkws-all-all-trial-e-gcp-1003963&utm_content=text-ad-none-any-DEV_c-CRE_170512857568-ADGP_Desk+%7C+AW+SEM+%7C+BKWS+~+EXA_1%3A1_NL_NL_ML_Vision+API_google+vision+api-KWID_43700016973722688-kwd-203288731687-userloc_9064817&utm_term=KW_google%20vision%20api-ST_google+vision+api&ds_rl=1245734&gclid=Cj0KCQiAyszSBRDJARIsAHAqQ4pR8oo2cGZfocML-IIAcj9TMGbFpLQvhIGmITbpbAr9nqz_kU3C7tsaAjr-EALw_wcB&dclid=CJSVn6eoydgCFdQ44AodzhkHTA)

* [Camera API](https://developer.android.com/guide/topics/media/camera.html)

* [Google Maps GPS API](https://developers.google.com/maps/documentation/android-api/)

* [Firebase](https://firebase.google.com/)

### *Data sources*

* [Random object generator](https://www.randomlists.com/things)


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
            



