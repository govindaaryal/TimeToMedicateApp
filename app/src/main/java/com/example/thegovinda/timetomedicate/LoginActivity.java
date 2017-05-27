package com.example.thegovinda.timetomedicate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by The Govinda on 4/22/2017.
 */

public class LoginActivity extends AppCompatActivity {
    Button btn;
    EditText uname, password;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        btn = (Button)findViewById(R.id.btn_login);
        uname = (EditText)findViewById(R.id.useremail);
        password = (EditText)findViewById(R.id.password);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String usernameS = uname.getText().toString();
                String passwordS = password.getText().toString();


                /*Check Username and password through JSON*/

                Intent i = new Intent(LoginActivity.this,Validate.class);
                i.putExtra("useremail",usernameS);
                i.putExtra("password",passwordS);
                startActivity(i);
            }
        });

    }
}
