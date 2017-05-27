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
import android.widget.AdapterView;
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

public class Patient extends AppCompatActivity {
    TextView tv;
    ProgressDialog mProgress;
    ListView lv;
    Button btn_medicate;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patient);
        lv = (ListView)findViewById(R.id.listView);
        btn_medicate = (Button)findViewById(R.id.btn_medicate);
        mProgress = new ProgressDialog(Patient.this);
        mProgress.setMessage("Fetching Patients...");
        mProgress.show();
        tv = (TextView)findViewById(R.id.patientName);
        final String id = getIntent().getStringExtra("id");
        SharedPreferences sharedPreferences = getSharedPreferences("userInfo",MODE_PRIVATE);
        String savedUser = sharedPreferences.getString("useremail","");
        new Patient.JSONTask().execute("http://myagrapalace.com/ttm/json-patients-single.php?id="+id+"&&user="+savedUser);
        //new Patient.JSONTask().execute("http://192.168.0.102/ttm/json-patients-single.php?id="+id+"&&user="+savedUser);

        btn_medicate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(v.getContext(),SingleMedication.class);
                in.putExtra("id",id);
                startActivity(in);
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    public class JSONTask extends AsyncTask<String,String,List<PatientModel>> {

        List<String > patientList;
        List<String > patientIdList;

        @Override
        protected List<PatientModel> doInBackground(String... params) {
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
                JSONArray parentArray = parentObject.getJSONArray("patient");
                StringBuffer finalPatient = new StringBuffer();

                patientList = new ArrayList<String>(parentArray.length());
                patientIdList = new ArrayList<String>(parentArray.length());

                List<PatientModel> patientModelList = new ArrayList<>();

                for(int i=0; i<parentArray.length();i++) {
                    JSONObject finalObject = parentArray.getJSONObject(i);
                    PatientModel patientModel = new PatientModel();

                    patientModel.setName(finalObject.getString("patient_name"));
                    patientModel.setGender(finalObject.getString("patient_gender"));
                    patientModel.setDob(finalObject.getString("patient_dob"));
                    patientModel.setPhone(finalObject.getString("patient_phone"));
                    patientModel.setRelation(finalObject.getString("relation"));
                    patientModel.setAddress(finalObject.getString("patient_address"));
                    String patientId = finalObject.getString("patient_id");

                    mProgress.dismiss();
                    patientIdList.add(patientId);
                    //patientList.add(patientId + patientName + patientPhone );
                    patientModelList.add(patientModel);
                }
                return patientModelList;

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
        protected void onPostExecute(List<PatientModel> s) {
            super.onPostExecute(s);

            PatientAdapter adapter = new PatientAdapter(getApplicationContext(),R.layout.patient_description,s);
            lv.setAdapter(adapter);
        }
    }

    public class PatientAdapter extends ArrayAdapter{
        private List<PatientModel> patientModelList;
        private int resource;
        private LayoutInflater inflater;
        public PatientAdapter(Context context, int resource, List<PatientModel> objects) {
            super(context, resource, objects);
            patientModelList = objects;
            this.resource=resource;
            inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView==null){
                convertView = inflater.inflate(resource,null);
            }
            TextView patient_name;
            TextView patient_address;
            TextView patient_phone;
            TextView patient_dob;
            TextView patient_gender;
            TextView relation;

            patient_name = (TextView)convertView.findViewById(R.id.patient_name);
            patient_address = (TextView)convertView.findViewById(R.id.patient_address);
            patient_gender = (TextView)convertView.findViewById(R.id.patient_gender);
            patient_phone = (TextView)convertView.findViewById(R.id.patient_phone);
            patient_dob = (TextView)convertView.findViewById(R.id.patient_dob);
            relation = (TextView)convertView.findViewById(R.id.relation);

            patient_name.setText(patientModelList.get(position).getName());
            patient_address.setText(patientModelList.get(position).getAddress());
            patient_gender.setText(patientModelList.get(position).getGender());
            patient_phone.setText(patientModelList.get(position).getPhone());
            patient_dob.setText(patientModelList.get(position).getDob());
            relation.setText(patientModelList.get(position).getRelation());

            return convertView;
        }
    }


}
