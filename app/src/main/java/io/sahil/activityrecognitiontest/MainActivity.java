package io.sahil.activityrecognitiontest;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.location.DetectedActivity;

public class MainActivity extends AppCompatActivity {

    Button start, stop, requestPermission;
    TextView still, onFoot, walking, running, inVehicle, onBicycle, tilting, unknown;

    ActivityResultLauncher<String> resultLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
        @Override
        public void onActivityResult(Boolean result) {
            Log.e("TAG", "onActivityResult: "+result);
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        start = findViewById(R.id.start);
        stop = findViewById(R.id.stop);
        requestPermission = findViewById(R.id.permission);
        still = findViewById(R.id.still);
        onFoot = findViewById(R.id.on_foot);
        walking = findViewById(R.id.walking);
        running = findViewById(R.id.running);
        inVehicle = findViewById(R.id.in_a_vehicle);
        onBicycle = findViewById(R.id.on_bicycle);
        tilting = findViewById(R.id.tilting);
        unknown = findViewById(R.id.unknown);

        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals("ACTIVITY_DETECTED")){
                    int type = intent.getIntExtra("NAME", -1);
                    int confidence = intent.getIntExtra("CONFIDENCE", -1);

                    switch (type){
                        case DetectedActivity.STILL:
                            still.setText(Integer.toString(confidence));
                            break;
                        case DetectedActivity.ON_FOOT:
                            onFoot.setText(Integer.toString(confidence));
                            break;
                        case DetectedActivity.WALKING:
                            walking.setText(Integer.toString(confidence));
                            break;
                        case DetectedActivity.RUNNING:
                            running.setText(Integer.toString(confidence));
                            break;
                        case DetectedActivity.IN_VEHICLE:
                            inVehicle.setText(Integer.toString(confidence));
                            break;
                        case DetectedActivity.ON_BICYCLE:
                            onBicycle.setText(Integer.toString(confidence));
                            break;
                        case DetectedActivity.TILTING:
                            tilting.setText(Integer.toString(confidence));
                            break;
                        case DetectedActivity.UNKNOWN:
                            unknown.setText(Integer.toString(confidence));
                            break;

                    }
                }
            }
        };

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(receiver, new IntentFilter("ACTIVITY_DETECTED"));
                startService(new Intent(getApplicationContext(), MyService.class));
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(receiver);
                stopService(new Intent(getApplicationContext(), MyService.class));
            }
        });

        requestPermission.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.Q)
            @Override
            public void onClick(View view) {
                resultLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION);
            }
        });

    }


}