package com.example.thegovinda.timetomedicate;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;

/**
 * Created by The Govinda on 1/30/2017.
 */

public class SplashScreen extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.splash_screen);

        if(isWorkingInternetPersent()){
            splash();
        }
        else{
            AlertDialog alert = new AlertDialog.Builder(this).create();
            alert.setTitle("No Internet Connection");
            alert.setMessage("Please connect your device to internet");
            alert.setButton(Dialog.BUTTON_POSITIVE,"OK",new DialogInterface.OnClickListener(){

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                    System.exit(0);
                }
            });

            alert.show();
        }
    }
    public void splash() {
        Thread timer = new Thread(){
            @Override
            public void run() {
                try {
                    sleep(3000);

                    SharedPreferences sharedPreferences = getSharedPreferences("userInfo",MODE_PRIVATE);
                    String savedUser = sharedPreferences.getString("useremail",""); //Get value from file
                    String savedPassword = sharedPreferences.getString("password","");

                    if(savedUser!="" && savedPassword!="") {
                        Intent i = new Intent(SplashScreen.this, MainActivity.class);
                        startActivity(i);
                    }else{
                        Intent i = new Intent(SplashScreen.this, LoginActivity.class);
                        startActivity(i);
                    }
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        };
        timer.start();
    }
    public boolean isWorkingInternetPersent() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getBaseContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }

        }
        return false;
    }

}



