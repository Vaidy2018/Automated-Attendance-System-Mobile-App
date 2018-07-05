package com.internshala.helloworld.ongcattendance;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class changePassword extends AppCompatActivity {
//Initializing all the variables
    Button b1;
    EditText e1,e2,e3;
    String cpf;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        Intent i=getIntent();
        //Retrieve extended data from the intent.
        cpf=i.getStringExtra("cpf");

        e1=(EditText)findViewById(R.id.editText6);
        e2=(EditText)findViewById(R.id.editText7);
        e3=(EditText)findViewById(R.id.editText8);

        b1=(Button)findViewById(R.id.button10);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String old=e1.getText().toString();
                String new1=e2.getText().toString();
                String new2=e3.getText().toString();
                if(new1.compareTo(new2)==0)
                {
                    //If the user passes same password in password1 and password2 field then asynChange() will execute.
                    new AsyncChange().execute(old,new1);
                }
                else
                {
                    /*If the user passes different password in password1 and password2 field then it will display wrong
                     retype password.*/
                    Toast.makeText(changePassword.this,"Re-Type Password",Toast.LENGTH_LONG).show();
                }

            }
        });
    }
    /*
    AsyncTask must be subclassed to be used. The subclass will override at least one method (doInBackground(Params...)),
    and most often will override a second one (onPostExecute(Result).)
    */
    /**
     * Async task class to pass data into database through background process which user entered.
     */
    private class AsyncChange extends AsyncTask<String, String, String>
    {
        ProgressDialog pdLoading = new ProgressDialog(changePassword.this);
        HttpURLConnection conn;
        URL url = null;

        @Override
         /*
        onPreExecute(), invoked on the UI thread before the task is executed.
        This step is normally used to setup the task, for instance by showing a progress bar in the user interface.
         */
        protected void onPreExecute() {
            super.onPreExecute();

            //this method will be running on UI thread
            pdLoading.setMessage("\tLoading...");
            pdLoading.setCancelable(false);
            pdLoading.show();

        }
        @Override
           /*
        doInBackground(Params...), invoked on the background thread immediately after onPreExecute() finishes executing.
        This step is used to perform background computation that can take a long time.
        The parameters of the asynchronous task are passed to this step.
        The result of the computation must be returned by this step and will be passed back to the last step.
        This step can also use publishProgress(Progress...) to publish one or more units of progress.
        These values are published on the UI thread, in the onProgressUpdate(Progress...) step.
         */
        protected String doInBackground(String... params) {
            try {

                // Enter URL address where your php file resides
                url = new URL(getResources().getString(R.string.Folder)+"changePass1.php");

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return "exception";
            }
            try {
                // Setup HttpURLConnection class to send and receive data from php and mysql
                conn = (HttpURLConnection)url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");

                // setDoInput and setDoOutput method depict handling of both send and receive
                conn.setDoInput(true);
                conn.setDoOutput(true);

                // Append parameters to URL
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("cpf", cpf)
                        .appendQueryParameter("new1", params[1])
                        .appendQueryParameter("old", params[0]);
                String query = builder.build().getEncodedQuery();

                // Open connection for sending data
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();
                conn.connect();

            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                return "exception";
            }

            try {

                int response_code = conn.getResponseCode();

                // Check if successful connection made
                if (response_code == HttpURLConnection.HTTP_OK) {

                    // Read data sent from server
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    // Pass data to onPostExecute method
                    return(result.toString());

                }else{

                    return("unsuccessful");
                }

            } catch (IOException e) {
                e.printStackTrace();
                return "exception";
            } finally {
                conn.disconnect();
            }
        }

        @Override
         /*
        onPostExecute(Result), invoked on the UI thread after the background computation finishes.
        The result of the background computation is passed to this step as a parameter
         */
        protected void onPostExecute(String result)
         /*
        In onPostExecute() method the progress dialog is dismissed and
        the array list data is displayed in list view using an adapter.
         */
         {
             //this method will be running on UI thread
             pdLoading.dismiss();
            // Toast.makeText(changePassword.this, result, Toast.LENGTH_LONG).show();
            if(result.equalsIgnoreCase("wrong"))
            {
                /* Here launching another activity when changePassword successful. If you persist changePassword state
                use sharedPreferences of Android. and logout button to clear sharedPreferences.
                 */
                Toast.makeText(changePassword.this, "Wrong Old Password !!", Toast.LENGTH_LONG).show();
            }
            //
            if(result.equalsIgnoreCase("true"))
            {
                /* Here launching another activity when changePassword successful. If you persist changePassword state
                use sharedPreferences of Android. and logout button to clear sharedPreferences.
                 */
                Toast.makeText(changePassword.this, "Password Updated!!", Toast.LENGTH_LONG).show();
                Intent i=new Intent(changePassword.this,UserPortal.class);
                i.putExtra("cpf",cpf);
                startActivity(i);
            }
            else if (result.equalsIgnoreCase("false")) {
                // If username and password does not match display a error message
                Toast.makeText(changePassword.this, "Check new Password!!", Toast.LENGTH_LONG).show();
            }
            else if (result.equalsIgnoreCase("exception") || result.equalsIgnoreCase("unsuccessful")) {

                Toast.makeText(changePassword.this, "OOPs! Something went wrong. Connection Problem.", Toast.LENGTH_LONG).show();

            }
        }
    }
}
