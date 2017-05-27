package com.example.thegovinda.timetomedicate;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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
 * Created by The Govinda on 1/30/2017.
 */

public class DoctorList extends AppCompatActivity {
    ProgressDialog mProgress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patient_list);
        mProgress = new ProgressDialog(DoctorList.this);
        mProgress.setMessage("Fetching Doctors...");
        mProgress.show();

        SharedPreferences sharedPreferences = getSharedPreferences("userInfo",MODE_PRIVATE);
        String savedUser = sharedPreferences.getString("useremail","");

        new JSONTask().execute("http://myagrapalace.com/ttm/json-doctors.php?user="+savedUser);
        //new JSONTask().execute("http://192.168.0.102/ttm/json-doctors.php?user="+savedUser);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    public class JSONTask extends AsyncTask<String,String,String>{

        List<String > patientList;
        List<String > patientIdList;

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
                JSONArray parentArray = parentObject.getJSONArray("doctors");
                StringBuffer finalPatient = new StringBuffer();

                patientList = new ArrayList<String>(parentArray.length());
                patientIdList = new ArrayList<String>(parentArray.length());

                for(int i=0; i<parentArray.length();i++) {
                    JSONObject finalObject = parentArray.getJSONObject(i);
                    String patientId = finalObject.getString("doctor_id");
                    String patientName = finalObject.getString("doctor_name");
                    mProgress.dismiss();
                    patientIdList.add(patientId);
                    patientList.add(patientName);
                }
                return finalPatient.toString();

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
            ListView myList = (ListView)findViewById(R.id.patientListView);
            myList.setAdapter(new ArrayAdapter<String>(DoctorList.this,android.R.layout.simple_list_item_1,patientList));

            myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                   //Toast.makeText(getApplicationContext(),"You Clicked "+position,Toast.LENGTH_SHORT).show();

                    Intent i = new Intent(view.getContext(),Doctor.class);
                    i.putExtra("id",patientIdList.get(position));
                    startActivity(i);
                }
            });
        }
    }
}