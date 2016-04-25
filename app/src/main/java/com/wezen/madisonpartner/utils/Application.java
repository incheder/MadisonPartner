package com.wezen.madisonpartner.utils;

import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.PushService;

/**
 * Created by eder on 10/28/15.
 */
public class Application extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "xJzgkwGEHuFFIBazGyGWeHmbn9cSWaQ2F9oPHFhb", "dSdG1ACby08fUT6oCxxxZxWyXq5sTzm3zW51YNNS");
        ParseInstallation.getCurrentInstallation().saveInBackground();
    }
}
