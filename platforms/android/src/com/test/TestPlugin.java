package com.test;

import android.util.Log;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONException;

public class TestPlugin extends CordovaPlugin {

    @Override
    public boolean execute(String action, CordovaArgs args,
                           CallbackContext callbackContext) throws JSONException {
        Log.d("TestPlugin", "received call");
        if (action.equals("test")) {
            test(callbackContext);
        } else {
            return false;
        }
        return true;
    }

    private void test(final CallbackContext callbackContext) {
        Log.d("TestPlugin", "executing test");
        callbackContext.success("0cc63f47cc68c3c2283a");
    }
}