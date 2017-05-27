package com.example.thegovinda.timetomedicate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by The Govinda on 4/22/2017.
 */

public class Validate extends AppCompatActivity {
    //Getting Intent Data form Login Screen

    String userName,upass;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final String usernameS = getIntent().getStringExtra("useremail") ;
        final String passwordS = getIntent().getStringExtra("password");

        new Validate.JSONTask().execute("http://myagrapalace.com/ttm/json-login.php?email="+usernameS+"&password="+passwordS);
        //new Validate.JSONTask().execute("http://192.168.0.102/ttm/json-login.php?email="+usernameS+"&password="+passwordS);

    }

    public class JSONTask extends AsyncTask<String,String,String>{

        @Override
        protected String doInBackground(String... params) {

            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection)url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line=reader.readLine())!=null){
                    buffer.append(line);
                }
                String finalJson = buffer.toString();

                JSONObject parentObject = new JSONObject(finalJson);
                JSONArray parentArray = parentObject.getJSONArray("users");

                StringBuffer finalUser = new StringBuffer();
                    JSONObject finalObject = parentArray.getJSONObject(0);
                    userName = finalObject.getString("email");
                    upass = finalObject.getString("password");


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if((userName.equalsIgnoreCase(userName))&&(upass.equalsIgnoreCase(upass))){

                SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("useremail",userName);
                editor.putString("password",upass);
                editor.apply();

                Intent i = new Intent(Validate.this,MainActivity.class);
                startActivity(i);

            }else if(userName==""&&upass==""){
                Intent i = new Intent(Validate.this,LoginActivity.class);
                startActivity(i);
            }


        }
    }
}
