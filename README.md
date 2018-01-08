# Project Proposal SNAPTHAT

This app is written by Christian in the context of the minor programming at the UvA, january of 2018.

## *The "problem"*

People nowadays spend hours and hours behind different kinds of screens.
Pictured takes advantage of the technology that phones provide, and makes users go outside and explore the world a bit with a simple yet fun game.

## *The solution*

### *One line description*
The whole app is built around the principle of providing the user with a random (but restricted to certain areas) noun, and after the user has photographed that object in real life, the player gets points for time and distance traveled.

Some more information: To do so, the user might have to travel a bit, and that takes time. The more time, the "worse" that user performed. Players can share, compare and group their results. Friends can challenge each other to outtime the other.

### *Visual sketch*

![visual sketch](https://github.com/Segouta/Pictured/blob/master/doc/visual_sketch.jpeg)

### *Features*

MVP:

* Players can log in with an account, and collect points that are stored on that account.
* Players can take photo's with an in-app camera.
* The photo is checked using Google's Vision API, and only if it is indeed the stated object, points are rewarded
* A highscore of every object is available.

Optional:

* Players can friend each other.
* Anti-cheat. The moment an object is revealed, the phone retrieves a timestamp, but not from the phone itself, because that can be adjusted by the user. Rather from an API. When the object is photographed, that API is called again, and the times are compared.
* Players can poule up. This way, people get the same objects to photograph, and therefore compete in a more honest way.
* A quickest find is available per player, if that player is friended with you you can see it.

## *Prerequisites*

Data sources:

* [Google Vision API for photo check](https://cloud.google.com/vision/?utm_source=google&utm_medium=cpc&utm_campaign=emea-nl-all-nl-dr-bkws-all-all-trial-e-gcp-1003963&utm_content=text-ad-none-any-DEV_c-CRE_170512857568-ADGP_Desk+%7C+AW+SEM+%7C+BKWS+~+EXA_1%3A1_NL_NL_ML_Vision+API_google+vision+api-KWID_43700016973722688-kwd-203288731687-userloc_9064817&utm_term=KW_google%20vision%20api-ST_google+vision+api&ds_rl=1245734&gclid=Cj0KCQiAyszSBRDJARIsAHAqQ4pR8oo2cGZfocML-IIAcj9TMGbFpLQvhIGmITbpbAr9nqz_kU3C7tsaAjr-EALw_wcB&dclid=CJSVn6eoydgCFdQ44AodzhkHTA)

* [Random object generator](https://www.randomlists.com/things)


External Libraries:

* Firebase
* Camera API


Similar:

DO NOT EXIST AS FAR AS I HAVE BEEN ABLE TO FIND THEM


Hardest parts:

* Camera API
* Friending other people
* A nice, intuitive design
