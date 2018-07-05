package com.internshala.helloworld.ongcattendance;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.internshala.helloworld.ongcattendance.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import java.util.ArrayList;
import java.util.HashMap;

public class Profile extends AppCompatActivity {

    ProgressDialog pDialog;
    String cpf;
    private static final int img1=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        cpf=getIntent().getStringExtra("cpf");
        new FetchProfile().execute();
        Button b=(Button)findViewById(R.id.button11);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Profile.this, com.internshala.helloworld.ongcattendance.UserPortal.class);
                i.putExtra("cpf", cpf);
                startActivity(i);
            }
        });

        Button b1=(Button)findViewById(R.id.button12);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(Profile.this,changePassword.class);
                i.putExtra("cpf",cpf);
                startActivity(i);
            }
        });

        Button b2=(Button)findViewById(R.id.button14);
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AsyncUpdate().execute();
            }
        });
    }

    private class FetchProfile extends AsyncTask<Void, Void, Void> {
        ArrayList<HashMap<String, String>> Attendance = new ArrayList<>();
        String name,mobile,group,team;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Profile.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... params) {
            String URL = getResources().getString(R.string.Folder) + "profile1.php";
            Uri builtUri = Uri.parse(URL).buildUpon()
                    .appendQueryParameter("cpf", cpf)
                    .build();
            HttpHandler handler = new HttpHandler();
            String jsonstring = handler.makeServiceCall(builtUri.toString());
            System.out.println(jsonstring);
            if(jsonstring!=null) {
                try {
                    JSONObject jsonObject = new JSONObject(jsonstring);

                    JSONArray jsonArray = jsonObject.getJSONArray("result");
                    for(int i=0;i<jsonArray.length();i++)
                    {
                        JSONObject js = jsonArray.getJSONObject(i);

                        HashMap<String,String> is = new HashMap<>();
                         name = js.getString("name");
                        mobile = js.getString("mobile");
                         group = js.getString("group");
                         team = js.getString("team");
                        is.put("name",name);
                        is.put("mobile",mobile);
                        is.put("group",group);
                        is.put("team",team);
                        //is.put("duration", duration);
                        Attendance.add(is);
                    }

                }
                catch (final JSONException e)
                {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Couldn't get json from server. Check LogCat for possible errors!",
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (pDialog.isShowing())
                pDialog.dismiss();
            EditText t1,t5;
            TextView t6,t7,t4;
            t1=(EditText)findViewById(R.id.textView31);t1.setText(name);
            t4=(TextView)findViewById(R.id.textView34);t4.setText(cpf);
            t5=(EditText)findViewById(R.id.textView35);t5.setText(mobile);
            t6=(TextView)findViewById(R.id.textView36);t6.setText(group);
            t7=(TextView)findViewById(R.id.textView37);t7.setText(team);

        }

    }


    private class AsyncUpdate extends AsyncTask<String, String, String>
    {
        ProgressDialog pdLoading = new ProgressDialog(Profile.this);
        HttpURLConnection conn;
        URL url = null;
        String x1,x2;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //this method will be running on UI thread
            pdLoading.setMessage("\tLoading...");
            pdLoading.setCancelable(false);
            pdLoading.show();
            EditText t1,t5;
            t1=(EditText)findViewById(R.id.textView31);x1=t1.getText().toString();
            t5=(EditText)findViewById(R.id.textView35);x2=t5.getText().toString();

        }
        @Override
        protected String doInBackground(String... params) {
            try {

                // Enter URL address where your php file resides
                url = new URL(getResources().getString(R.string.Folder)+"userUpdate1.php");

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
                        .appendQueryParameter("name", x1)
                        .appendQueryParameter("mobile", x2)
                        .appendQueryParameter("cpf", cpf);
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
        protected void onPostExecute(String result) {

            //this method will be running on UI thread

            pdLoading.dismiss();
            //
            if(result.equalsIgnoreCase("true"))
            {
                /* Here launching another activity when login successful. If you persist login state
                use sharedPreferences of Android. and logout button to clear sharedPreferences.
                 */
                Toast.makeText(Profile.this, "Profile Updated", Toast.LENGTH_LONG).show();
                Intent i=new Intent(Profile.this,UserPortal.class);
                i.putExtra("cpf",cpf);
                startActivity(i);


            }else if (result.equalsIgnoreCase("false")){

                // If username and password does not match display a error message
                Toast.makeText(Profile.this, "Invalid ", Toast.LENGTH_LONG).show();

            } else if (result.equalsIgnoreCase("exception") || result.equalsIgnoreCase("unsuccessful")) {

                Toast.makeText(Profile.this, "OOPs! Something went wrong. Connection Problem.", Toast.LENGTH_LONG).show();

            }
        }

    }

}


