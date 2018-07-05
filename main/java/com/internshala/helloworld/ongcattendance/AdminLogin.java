package com.internshala.helloworld.ongcattendance;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


//This code is for admin Login:-
public class AdminLogin extends AppCompatActivity {

    //initializing variables
    EditText e1;
    Button b1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);
  //cascading Buttons and EditText
        e1=(EditText)findViewById(R.id.editText3);
        b1=(Button)findViewById(R.id.button3);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pass=e1.getText().toString();
                //if admin passes "admin123" as password, then app will redirect to adminpanel activity.
                if(pass.compareTo("admin123")==0)
                {
                    Intent i=new Intent(AdminLogin.this,AdminPanel.class);
                    startActivity(i);
                }
                else
                {
                    Toast.makeText(v.getContext(),"Wrong Password !!",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
