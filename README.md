pusher_cordova_issue
====================

This is an example to demonstrate issues with cordova, threads and pusher.

A detailed description of this issue can be found at http://stackoverflow.com/questions/18718156/cant-connect-to-pusher-in-phonegap-cordova-on-android-after-calling-callback-fr

Note: I also committed all IDE related file changes to provide a full trace of what I've done.

**Update**: The post on stackoverflow was removed so I just add it here:

First let me explain the basics:

I’m using Cordova 3.0 for iOS and Android. In the JS code of the app I use Pusher v2.1.2 to receive realtime notifications. There are a lot of other SDKs and frameworks in use, but I managed to isolate my problem by building a super simple Android app based on Cordova 3.0 and Pusher v2.1.2

I can connect to Pusher without any issues. However due to other plugins, I need to perform some time consuming tasks in the background by using the following structure (as described in the cordova docs):

    cordova.getThreadPool().execute(new Runnable() {
      public void run() {
        ...
        callbackContext.success(); // Thread-safe.
      }
    });

As soon as I call a success callback from a plugin like this from another thread, the Pusher client somehow can’t connect to the server anymore and remains in the “unavailable” connection state.

Here are the debug logs I get when I just call callbackContext.success() in the current thread:

    com.test I/Web Console: Received Event: deviceready:59
    com.test I/Web Console: calling native plugin..:38
    com.test I/Web Console: initializing pusher with 0cc63f47cc68c3c2283a:41
    com.test I/Web Console: binding to pusher connection state change:44
    com.test I/Web Console: pusher connection is connecting:46
    com.test I/Web Console: called native plugin:48
    com.test I/Web Console: pusher connection changed: connected:45

So everything seems to work, pusher is connected at the end.

As soon as I call the callback like this:

    cordova.getThreadPool().execute(new Runnable() {
      public void run() {
        callbackContext.success();
    });

I get the following debug logs:

    com.test I/Web Console: Received Event: deviceready:59
    com.test I/Web Console: calling native plugin..:38
    com.test I/Web Console: initializing pusher with 0cc63f47cc68c3c2283a:41
    com.test I/Web Console: binding to pusher connection state change:44
    com.test I/Web Console: pusher connection is unavailable:46
    com.test I/Web Console: called native plugin:48

So Pusher can’t establish the connection anymore. The only difference is, that I called the callback from another thread before trying to connect to Pusher.

I don’t have any clue what’s the reason for that. I thought it might be an issue caused by the complex authentication system with several callbacks to external services. But I built a complete new cordova app for android only using pusher. Same problem here.

I created a git repo, that documents every single step I performed to build this super simple demo app. I used Android Studio (IntelliJ) on my Mac and a Samsung Galaxy S3 Mini for testing and debugging the app.

Here is the complete source code:

https://github.com/mikrobi/pusher_cordova_issue

Here is where I connect to Pusher:

https://github.com/mikrobi/pusher_cordova_issue/blob/master/platforms/android/assets/www/js/index.js#L41-L46' defer='defer

Here is the commit where I call the callback from another thread (which results in Pusher not being able to connect anymore):

https://github.com/mikrobi/pusher_cordova_issue/commit/696fd7c7062c874611554d0abe8006e2ad845fee

And here is the commit where I change it back again and after rebuilding the app, Pusher is able to connect:

https://github.com/mikrobi/pusher_cordova_issue/commit/dbc33eca4658bb1c94ebfbdfc95904acbb73a9c0

I’m clueless…
