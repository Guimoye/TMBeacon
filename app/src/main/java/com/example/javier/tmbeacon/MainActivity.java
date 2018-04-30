package com.example.javier.tmbeacon;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.auth0.android.jwt.JWT;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.Collection;

// https://altbeacon.github.io/android-beacon-library/samples.html
// https://altbeacon.github.io/android-beacon-library/eddystone-how-to.html

public class MainActivity extends AppCompatActivity implements BeaconConsumer {
    protected static final String TAG = "RangingActivity";
    private BeaconManager beaconManager;

    String jwtt= "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6MjAsInV1aWQiOiJhMGVlYmM5OS05YzBiLTRlZjgtYmI2ZC02MTVlY2EzNTE5Y2IiLCJ1c2VybmFtZSI6Imphdmllcmd1aW1AZ21haWwuY29tIiwiZW1haWwiOiJqYXZpZXJndWltQGdtYWlsLmNvbSIsInN0YXR1cyI6dHJ1ZSwiY3JlYXRlZEF0IjoiMjAxNy0wOS0yOFQyMDozNzoxMS4yNDlaIiwidXBkYXRlZEF0IjoiMjAxNy0wOS0yOFQyMDozNzoxMS4yNDlaIiwicm9sZUlkIjpudWxsLCJjb21wYW55SWQiOm51bGwsImxvZ2luRGF0ZSI6IjIwMTctMDktMjhUMjA6NDg6MDIuMTQ3WiIsImlhdCI6MTUwNjYzMTY4MiwiZXhwIjoxNTA2NzE4MDgyfQ.6UcWuJUQjbe_MDdVa7qJcmI5jwFAjR3uppmHBQJMhrI";
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        beaconManager = BeaconManager.getInstanceForApplication(this);
        // To detect proprietary beacons, you must add a line like below corresponding to your beacon
        // type.  Do a web search for "setBeaconLayout" to get the proper expression.
       //  beaconManager.getBeaconParsers().add(new BeaconParser().
        //       setBeaconLayout("m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
        beaconManager.getBeaconParsers().add(new BeaconParser()
               .setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
        beaconManager.bind(this);

        checkPermissions();
        verifyBluetooth();

       // Log.e("siii ","eee "+decodeUsername(jwtt));


    }


    @TargetApi(23)
    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android M Permission check
            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                final Dialog permDialog = DialogBuilder.createSimpleOkErrorDialog(
                        this,
                        getString(R.string.dialog_error_ble_not_enabled),
                        getString(R.string.dialog_error_ble_not_enabled)
                );
                permDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @TargetApi(23)
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                PERMISSION_REQUEST_COARSE_LOCATION);
                    }
                });
                permDialog.show();
            }
        }
    }

    @TargetApi(18)
    private void verifyBluetooth() {

        try {
            if (!beaconManager.checkAvailability()) {

                final Dialog bleDialog = DialogBuilder.createSimpleOkErrorDialog(
                        this,
                        getString(R.string.dialog_error_ble_not_enabled),
                        getString(R.string.dialog_error_ble_not_enabled)
                );
                bleDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        finish();
                        System.exit(0);
                    }
                });
                bleDialog.show();

            }
        } catch (RuntimeException e) {

            final Dialog bleDialog = DialogBuilder.createSimpleOkErrorDialog(
                    this,
                    getString(R.string.dialog_error_ble_not_enabled),
                    getString(R.string.dialog_error_ble_not_enabled)
            );
            bleDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    finish();
                    System.exit(0);
                }
            });
            bleDialog.show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e(TAG, "coarse location permission granted");
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Functionality limited");
                    builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons when in the background.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                        @Override
                        public void onDismiss(DialogInterface dialog) {
                        }

                    });
                    builder.show();
                }
                return;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
    }



    private String decodeUsername(String token) {
        JWT jwt = new JWT(token);
        try {

            if (jwt.getClaim("loginDate") != null) {
                return jwt.getClaim("loginDate").asString();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onBeaconServiceConnect() {

        beaconManager.addRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                if (beacons.size() > 0) {
                    Log.e(TAG, "El primer faro que veo es sobre "+beacons.iterator().next().getBluetoothName()+" con distancia de "+beacons.iterator().next().getDistance());
                }
            }
        });

        try {
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
        } catch (RemoteException e) {    }
    }
}
