package com.example.thegovinda.timetomedicate;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.thegovinda.timetomedicate.models.PatientModel;

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
import java.util.ArrayList;
import java.util.List;

/**
 * Created by The Govinda on 4/21/2017.
 */

public class Doctor extends AppCompatActivity {
    TextView tv;
    ProgressDialog mProgress;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.doctor);

        mProgress = new ProgressDialog(Doctor.this);
        mProgress.setMessage("Fetching Patients...");
        mProgress.show();
        tv = (TextView)findViewById(R.id.doctor_desc);
        final String id = getIntent().getStringExtra("id");
        SharedPreferences sharedPreferences = getSharedPreferences("userInfo",MODE_PRIVATE);
        String savedUser = sharedPreferences.getString("useremail","");
        new Doctor.JSONTask().execute("http://myagrapalace.com/ttm/json-doctors-single.php?id="+id+"&&user="+savedUser);
        //new Doctor.JSONTask().execute("http://192.168.0.102/ttm/json-doctors-single.php?id="+id+"&&user="+savedUser);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    public class JSONTask extends AsyncTask<String,String,String> {

        @Override
        protected String doInBackground(String... params) {
            //ServerConnection
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                //URL url = new URL("http://192.168.0.105/ttm/patients");
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();//Opening Connection
                connection.connect();//Connecting to the link

                InputStream stream = connection.getInputStream();//Getting Data from the url
                reader = new BufferedReader(new InputStreamReader(stream)); //Read data line by line

                StringBuffer buffer = new StringBuffer();
                String line = "";
                while((line=reader.readLine())!=null){
                    buffer.append(line);
                }
                String finalJson = buffer.toString();

                JSONObject parentObject = new JSONObject(finalJson);
                JSONArray parentArray = parentObject.getJSONArray("doctor");
                StringBuffer finalPatient = new StringBuffer();

                StringBuffer doctorDesc = new StringBuffer();

                for(int i=0; i<parentArray.length();i++) {
                    JSONObject finalObject = parentArray.getJSONObject(i);

                    String doctorId = finalObject.getString("doctor_id");
                    String doctorName = finalObject.getString("doctor_name");
                    String doctorPhone = finalObject.getString("doctor_phone");
                    String doctorAddress = finalObject.getString("doctor_address");
                    String doctoremail = finalObject.getString("doctor_email");
                    String doctorgender = finalObject.getString("doctor_gender");
                    String doctornmc = finalObject.getString("doctor_nmc");
                    String doctorspecial = finalObject.getString("doctor_special_at");
                    String doctorassociate = finalObject.getString("doctor_associate");

                    mProgress.dismiss();

                    doctorDesc.append("Name: "+doctorName +"\nGender: "+ doctorgender+"\nPhone: "+ doctorPhone+"\nE-Mail: "+ doctoremail +"\nAddress: "+ doctorAddress );
                    doctorDesc.append("\nNMC No. : "+ doctornmc+"\nSpeciality: "+ doctorspecial+"\nAssociated with: "+ doctorassociate);
                }
                return doctorDesc.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if(connection!=null) {
                    connection.disconnect();
                }
                try {
                    if (reader!=null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            tv.setText(s);

            /*ListView myList = (ListView)findViewById(R.id.listView);
            myList.setAdapter(new ArrayAdapter<String>(Patient.this,android.R.layout.simple_list_item_1,patientList));*/
        }
    }


}
