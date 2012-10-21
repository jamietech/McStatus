McStatus
-------

An IRC bot that checks the current Minecraft status every minute. Currently, McStatus pings the following services and checks for a HTTP 200 reply:

* session.minecraft.net (*authenticates server joins*)
* login.minecraft.net (*authenticates app logins*)
* skin server (*handles skins, obviously*)

####Dependencies
The bot currently depends on the following API's:

* PircBot