package com.example.thegovinda.timetomedicate;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ToggleButton;

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
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {
    Button btn_patient;
    private ToggleButton toggleButton;
    Button btn_test;
    Calendar calendar;
    PendingIntent pendingIntent;
    AlarmManager alarmManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        int status = 1;

        TimeZone timeZone = TimeZone.getTimeZone("Asia/Kathmandu");
        calendar = Calendar.getInstance(timeZone);


        /*calendar.set(Calendar.HOUR_OF_DAY, 6);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);*/

        Intent intent = new Intent(getApplicationContext(), Notification_receiver.class);

        pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        btn_test = (Button)findViewById(R.id.btn_test);

        btn_test.setTag(1);
        btn_test.setText("ON");
        btn_test.setBackgroundColor(Color.GREEN);

        SharedPreferences sharedPref = getSharedPreferences("setting",MODE_PRIVATE);
        String  notify = sharedPref.getString("onoroff", "");

        if(notify.equalsIgnoreCase("1")){
            btn_test.setTag(1);
            btn_test.setText("ON");
            btn_test.setBackgroundColor(Color.GREEN);
        }else if(notify.equalsIgnoreCase("0")){
            btn_test.setText("OFF");
            btn_test.setBackgroundColor(Color.RED);
            btn_test.setTag(1);
        }

        btn_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int status = (Integer)v.getTag();
                if(status==1){
                    btn_test.setText("ON");
                    btn_test.setBackgroundColor(Color.GREEN);
                    v.setTag(0);
                    Toast.makeText(getApplicationContext(),"Notification ON",Toast.LENGTH_SHORT).show();
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),2000, pendingIntent);

                    SharedPreferences setting = getSharedPreferences("setting",MODE_PRIVATE);
                    SharedPreferences.Editor editor = setting.edit();
                    editor.putString("onoroff","1");
                    editor.commit();


                }else {
                    btn_test.setText("OFF");
                    btn_test.setBackgroundColor(Color.RED);
                    v.setTag(1);
                    Toast.makeText(getApplicationContext(),"Notification OFF",Toast.LENGTH_SHORT).show();
                    //alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),1000, pendingIntent);
                    SharedPreferences setting = getSharedPreferences("setting",MODE_PRIVATE);
                    SharedPreferences.Editor editor = setting.edit();
                    editor.putString("onoroff","0");
                    editor.commit();
                    alarmManager.cancel(pendingIntent);
                }
            }
        });



        SharedPreferences sharedPreferences = getSharedPreferences("userInfo",MODE_PRIVATE);
        String savedUser = sharedPreferences.getString("useremail","");

        new MainActivity.JSONTask().execute("http://myagrapalace.com/ttm/json-medication-notify.php?user="+savedUser);
        //new MainActivity.JSONTask().execute("http://192.168.0.102/ttm/json-medication-notify.php?user="+savedUser);

}
    public void btn_patient_click(View v) {
        Intent i = new Intent(getApplicationContext(), PatientList.class);
        startActivity(i);
    }
    public void btn_doctor_click(View v) {
        Intent i = new Intent(getApplicationContext(), DoctorList.class);
        startActivity(i);
    }
    public void btn_schedule(View v) {
        Intent i = new Intent(getApplicationContext(), Pop.class);
        startActivity(i);
    }
    public void btn_medication(View v) {
        Intent i = new Intent(getApplicationContext(), Medication.class);
        startActivity(i);
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
                JSONArray parentArray = parentObject.getJSONArray("medicate");
                StringBuffer finalPatient = new StringBuffer();



                patientList = new ArrayList<String>(parentArray.length());
                patientIdList = new ArrayList<String>(parentArray.length());

                for(int i=0; i<parentArray.length();i++) {
                    JSONObject finalObject = parentArray.getJSONObject(i);
                    String patientId = finalObject.getString("patient_id");
                    String patientName = finalObject.getString("patient_name");
                    String medicineName = finalObject.getString("medicine_name");
                    String medicineDose = finalObject.getString("dose");
                    String medicineTime1 = finalObject.getString("time1");
                    String medicineTime2 = finalObject.getString("time2");
                    String medicineTime3 = finalObject.getString("time3");
                    String medicineTime4 = finalObject.getString("time4");

                    patientIdList.add(patientId);
                    patientList.add(patientName+"\nMedicine Name: "+medicineName+"\nDose: "+medicineDose+"\nTime: "+medicineTime1+" | "+medicineTime2+" | "+medicineTime3+" | "+medicineTime4 );
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
            ListView myList = (ListView)findViewById(R.id.lv_notify);
            myList.setAdapter(new ArrayAdapter<String>(MainActivity.this,R.layout.notifylist,patientList));

            myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent i = new Intent(view.getContext(),SingleMedication.class);
                    i.putExtra("id",patientIdList.get(position));
                    startActivity(i);
                }
            });
        }
    }
    /*private boolean isChecked = false;

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_notify);
        item.setChecked(isChecked);
        return true;
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            SharedPreferences sharedPreferences = getSharedPreferences("userInfo",MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove("useremail");
            editor.remove("password");
            editor.commit();

            Intent i = new Intent(this,LoginActivity.class);
            startActivity(i);

        }

        return super.onOptionsItemSelected(item);
    }


}