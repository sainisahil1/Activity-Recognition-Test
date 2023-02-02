package io.sahil.activityrecognitiontest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.List;

public class ActivityResult extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (ActivityRecognitionResult.hasResult(intent)){
            Log.e("TAG", "onReceive: ");
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
            if (result != null) {
                List<DetectedActivity> activities = result.getProbableActivities();
                for (DetectedActivity detectedActivity: activities){
                    Intent i = new Intent("ACTIVITY_DETECTED");
                    i.putExtra("NAME", detectedActivity.getType());
                    i.putExtra("CONFIDENCE", detectedActivity.getConfidence());
                    LocalBroadcastManager.getInstance(context).sendBroadcast(i);
                }
            }
        }
    }

}
