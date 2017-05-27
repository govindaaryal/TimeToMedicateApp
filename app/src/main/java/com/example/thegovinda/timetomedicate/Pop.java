package com.example.thegovinda.timetomedicate;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;

/**
 * Created by The Govinda on 4/21/2017.
 */

public class Pop extends Activity {
    Button past,future;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pop);

        past = (Button)findViewById(R.id.btn_past);
        future = (Button)findViewById(R.id.btn_future);


        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width*.8), (int) (height*0.3));

    }
    public void btn_past_click(View v) {
        Intent i = new Intent(getApplicationContext(), Appointment.class);
        i.putExtra("tag","past");
        startActivity(i);
    }
    public void btn_future_click(View v) {
        Intent i = new Intent(getApplicationContext(), Appointment.class);
        i.putExtra("tag","future");
        startActivity(i);
    }
}
