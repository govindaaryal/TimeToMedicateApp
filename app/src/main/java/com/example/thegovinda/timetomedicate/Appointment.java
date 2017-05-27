package com.example.thegovinda.timetomedicate;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

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

public class Appointment extends AppCompatActivity {

    ProgressDialog mProgress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appointment);
        final String tag = getIntent().getStringExtra("tag");
        //textView = (TextView)findViewById(R.id.tview);
        //expandableListView = (ExpandableListView)findViewById(R.id.lvExpandable);

        mProgress = new ProgressDialog(Appointment.this);
        mProgress.setMessage("Fetching Data ...");
        mProgress.show();

        SharedPreferences sharedPreferences = getSharedPreferences("userInfo",MODE_PRIVATE);
        String savedUser = sharedPreferences.getString("useremail","");

        if(tag.equals("past")){
            //new JSONTask().execute("http://192.168.0.102/ttm/json-past-appointment.php?user="+savedUser);
            new JSONTask().execute("http://aaryanwires.com.np/ttm/json-past-appointment.php?user="+savedUser);
        }else if(tag.equals("future")){
            new JSONTask().execute("http://myagrapalace.com/ttm/json-future-appointment.php?user="+savedUser);
        }

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
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
                String finalJson = buffer.toString();

                JSONObject parentObject = new JSONObject(finalJson);
                JSONArray parentArray = parentObject.getJSONArray("past");
                StringBuffer finalPatient = new StringBuffer();

                patientList = new ArrayList<String>(parentArray.length());
                patientIdList = new ArrayList<String>(parentArray.length());

                int events = parentArray.length();

                if (events < 1) {
                    /*AlertDialog alert = new AlertDialog.Builder(Appointment.this).create();
                    alert.setTitle("No Appointments Found");
                    alert.setMessage("There are no appoinments available");
                    alert.setButton(Dialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                            System.exit(0);
                        }
                    });

                    mProgress.dismiss();

                    alert.show();*/
                    mProgress.dismiss();

                    patientList.add("No Data Found");
                }

                else{
                    for (int i = 0; i < parentArray.length(); i++) {
                        JSONObject finalObject = parentArray.getJSONObject(i);
                        String patientName = finalObject.getString("patient_name");
                        String patientId = finalObject.getString("doctor_name");
                        String doctorName = finalObject.getString("doctor_name");
                        String a_date = finalObject.getString("a_date");
                        String remarks = finalObject.getString("remarks");
                        mProgress.dismiss();
                        patientList.add(patientName + "\nDoctor Name: " + doctorName + "\nDate: " + a_date + "\nRemarks: " + remarks);
                        patientIdList.add(patientId);
                    }
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
            ListView myList = (ListView)findViewById(R.id.lv);
            myList.setAdapter(new ArrayAdapter<String>(Appointment.this,android.R.layout.simple_list_item_1,patientList));
        }
    }
}
