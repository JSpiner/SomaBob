package net.jspiner.somabob.Service;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

/**
 * Created by Secret on 2015. 6. 28..
 */
public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {

    /**
     * Copyright 2016 JSpiner. All rights reserved.
     *
     * @author JSpiner (jspiner@naver.com)
     * @project SomaBob
     * @since 2016. 7. 18.
     */

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d("YESS", "Receive");
        ComponentName comp = new ComponentName(context.getPackageName(),
                GcmIntentService.class.getName());
        startWakefulService(context, (intent.setComponent(comp)));
        setResultCode(Activity.RESULT_OK);
    }
}