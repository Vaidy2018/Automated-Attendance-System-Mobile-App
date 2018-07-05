package com.internshala.helloworld.ongcattendance;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

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
import java.net.NetworkInterface;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class Register extends AppCompatActivity {

   // This getMacAddr method fetches the mac address of user during registeration process.
    public static String getMacAddr() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all)
            {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "1";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    String y;
                    String x=Integer.toHexString(b & 0xFF).toLowerCase();
                    if (x.length()==1)
                        y="0"+x;
                    else
                        y=x;
                    res1.append(y + "-");
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
            //handle exception
        }
        return "2";
    }
//Initializing Variables;
    Button b1;
    EditText e1,e2,e4,e5,e6;
    ProgressDialog pDialog;
    Spinner s1,s2;
    public String[] items =null;
    public String[] items1;
    String orgSel,teamSel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        items=new String[100];
        items1=new String[100];
        items[0]=new String();
        items1[0]=new String();

        //new AsyncOrg().execute();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        String x2=Login.getPortal();
        Toast.makeText(Register.this,x2,Toast.LENGTH_LONG).show();
        String x=getMacAddr();
        final String mac=x.replace(':', '-'); //This will replace mac id (:) into mac id(-).

        s1=(Spinner)findViewById(R.id.spinner);
        s2=(Spinner)findViewById(R.id.spinner2);

     // This Spinner is for selecting one organization from given list of organization.
        s1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                orgSel=items[position];

                Toast.makeText(parent.getContext(),orgSel,Toast.LENGTH_LONG).show();//This will toast the selected organization.

                new AsyncTeam().execute();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
//After selecting organization from list of organization, the list of teams from the selected organization is automatically comes.

        s2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                teamSel=items1[position];
                Toast.makeText(parent.getContext(),teamSel,Toast.LENGTH_LONG).show();
                //this will display the message of the selected team from the already selected organization.
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
//cascading all the editText and Button
        b1=(Button)findViewById(R.id.button5);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                e1 = (EditText) findViewById(R.id.editText);
                e2 = (EditText) findViewById(R.id.editText5);
                e4 = (EditText) findViewById(R.id.editText3);
                e5 = (EditText) findViewById(R.id.editText4);
                e6 = (EditText) findViewById(R.id.editText9);
                e1.setText("");
                e2.setText("");
                e4.setText("");
                e5.setText("");
                e6.setText("");
            }
        });
        Button b1=(Button)findViewById(R.id.button4);//register
        Button b2=(Button)findViewById(R.id.button5);//reset

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText e1 = (EditText) findViewById(R.id.editText);
                EditText e2 = (EditText) findViewById(R.id.editText5);
                EditText e4 = (EditText) findViewById(R.id.editText3);
                EditText e5 = (EditText) findViewById(R.id.editText4);
                EditText e6 = (EditText) findViewById(R.id.editText9);
                final String name = e1.getText().toString();
                final String cpf = e2.getText().toString();
                final String mac1 = mac;
                final String gender1 = "M";
                final String pass1 = e4.getText().toString();
                final String pass2 = e5.getText().toString();
                final String mobile =e6.getText().toString();
                final String org=orgSel;
                if(teamSel==null)
                    teamSel=new String("");
                final String team=teamSel;

                if (pass1.compareTo(pass2) == 0)
                    //If the user passes same password in password1 and password2 field then asyncRegister() will execute.
                    new AsyncRegister().execute(name, cpf, mac1, pass1,org,team,mobile);
                else
                    /*If the user passes different password in password1 and password2 field then it will display wrong
                     retype password.*/
                    Toast.makeText(Register.this, "Wrong Retype Password", Toast.LENGTH_LONG).show();
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
    private class AsyncRegister extends AsyncTask<String, String, String>
    {
        ProgressDialog pdLoading = new ProgressDialog(Register.this);
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
            pdLoading.setMessage("\tRegistering...");
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
                url = new URL(getResources().getString(R.string.Folder)+"register1.php");

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
                        .appendQueryParameter("name", params[0])
                        .appendQueryParameter("cpf", params[1])
                        .appendQueryParameter("mac", params[2])
                        .appendQueryParameter("password", params[3])
             // Params, the type of the parameters sent to the task upon execution.
                        .appendQueryParameter("org", params[4])
                        .appendQueryParameter("team", params[5])
                        .appendQueryParameter("mobile", params[6]);
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
            Toast.makeText(Register.this, "Registered !! "+result, Toast.LENGTH_LONG).show();
            if(result.equalsIgnoreCase("true"))
            {
                /* Here launching another activity when login successful. If you persist login state
                use sharedPreferences of Android. and logout button to clear sharedPreferences.
                 */
                Intent i=new Intent(Register.this,Login.class);
                startActivity(i);

            }else if (result.equalsIgnoreCase("false")){

                // If username and password does not match display a error message
                Toast.makeText(Register.this, "Invalid cpf or password", Toast.LENGTH_LONG).show();

            } else if (result.equalsIgnoreCase("exception") || result.equalsIgnoreCase("unsuccessful")) {

                Toast.makeText(Register.this, "OOPs! Something went wrong. Connection Problem.", Toast.LENGTH_LONG).show();
            }
        }

    }

    /*
    AsyncTask must be subclassed to be used. The subclass will override at least one method (doInBackground(Params...)),
    and most often will override a second one (onPostExecute(Result).)
    */
    //this AsyncOrg Class loaded all the organization from the database.

    /**
     * Async task class to get json by making HTTP call
     */

    private class AsyncOrg extends AsyncTask<Void, Void, Void> {
        ArrayList<HashMap<String, String>> sqlRow = new ArrayList<>();
        String x1;

        @Override
        /*
        onPreExecute(), invoked on the UI thread before the task is executed.
        This step is normally used to setup the task, for instance by showing a progress bar in the user interface.
         */
        protected void onPreExecute()
        //In onPreExecute() method progress dialog is shown before making the http call.
        {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(Register.this);
            pDialog.setMessage("Org Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
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
        protected Void doInBackground(Void... params)
        //In doInBackground() method, makeServiceCall() is called to get the json from url.
        // Once the json is fetched, it is parsed and each organization is added to array list.
        {
            String URL = getResources().getString(R.string.Folder)+"organization1.php";
            Uri builtUri = Uri.parse(URL).buildUpon()

                    .build();
            HttpHandler handler = new HttpHandler();
            // Making a request to url and getting response
            String jsonstring = handler.makeServiceCall(builtUri.toString());
            x1=jsonstring;
            System.out.println(jsonstring);
            if (jsonstring != null) {
                try {
                      /*
                    If your JSON node starts with [, then we should use getJSONArray() method.
                    Same as if the node starts with {, then we should use getJSONObject() method.
                    */
                    JSONObject jsonObject1 = new JSONObject(jsonstring);
                    //JSONObject jsonObject1 = jsonObject.getJSONObject("result");
                    // Getting JSON Array node
                    JSONArray jsonArray = jsonObject1.getJSONArray("result");
                    // looping through All Organization
                    for(int i=0;i<jsonArray.length();i++)
                    {
                        JSONObject js = jsonArray.getJSONObject(i);
                        // tmp hash map for single organization
                        HashMap<String,String> is = new HashMap<>();
                        String val = js.getString("val");
                        // adding each child node to HashMap key => value
                        is.put("val",val);
                        // adding organization to organization list
                        sqlRow.add(is);
                    }

                } catch (final JSONException e) {
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
        //till here
        @Override
        /*
        onPostExecute(Result), invoked on the UI thread after the background computation finishes.
         The result of the background computation is passed to this step as a parameter.
         */
        protected void onPostExecute(Void aVoid)
        /*
        In onPostExecute() method the progress dialog is dismissed and
        the array list data is displayed in list view using an adapter.
         */
        {
            super.onPostExecute(aVoid);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            /**
             * Updating parsed JSON data into ListView
             * */
            items=new String[sqlRow.size()];
            System.out.print("ok"+sqlRow.size()+"\n");

            for (int a =0; a<sqlRow.size(); a++) {
                HashMap<String, String> tmpData = (HashMap<String, String>) sqlRow.get(a);
                Set<String> key = tmpData.keySet();
                Iterator it = key.iterator();

                while (it.hasNext()) {
                    String hmKey = (String)it.next();
                    String hmData = (String) tmpData.get(hmKey);
                    items[a]=new String(hmData);

                    System.out.println("Key: " + hmKey +" & Data: "+hmData+" "+items[a]+" "+a);
                    it.remove(); // avoids a ConcurrentModificationException
                }
            }
            List<String> orglist = new ArrayList<>(Arrays.asList(items));

            ArrayAdapter<String> adap = new ArrayAdapter<String>(Register.this, R.layout.spinner_item, R.id.name3, orglist);
            adap.setDropDownViewResource(R.layout.spinner_item);
            s1.setAdapter(adap);
        }
    }
    /*
        AsyncTask must be subclassed to be used. The subclass will override at least one method (doInBackground(Params...)),
        and most often will override a second one (onPostExecute(Result).)
        */
    //this AsyncTeam Class loaded all the team from the selected organization from the database.
    private class AsyncTeam extends AsyncTask<Void, Void, Void> {
        ArrayList<HashMap<String, String>> sqlRow = new ArrayList<>();
        String x1;

        @Override
        protected void onPreExecute()
        //In onPreExecute() method progress dialog is shown before making the http call.
        {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(Register.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
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
        protected Void doInBackground(Void... params)
        //In doInBackground() method, makeServiceCall() is called to get the json from url.
        // Once the json is fetched, it is parsed and each organization is added to array list.
        {
            String URL = getResources().getString(R.string.Folder)+"team1.php";
            Uri builtUri = Uri.parse(URL).buildUpon()
                    .appendQueryParameter("org",orgSel)
                    .build();
            HttpHandler handler = new HttpHandler();
            // Making a request to url and getting response
            String jsonstring = handler.makeServiceCall(builtUri.toString());
            x1=jsonstring;
            System.out.println(jsonstring);
            if (jsonstring != null) {
                try {
                    /*
                    If your JSON node starts with [, then we should use getJSONArray() method.
                    Same as if the node starts with {, then we should use getJSONObject() method.
                    */
                    JSONObject jsonObject1 = new JSONObject(jsonstring);
                    //JSONObject jsonObject1 = jsonObject.getJSONObject("result");
                    // Getting JSON Array node
                    JSONArray jsonArray = jsonObject1.getJSONArray("result");
                    // looping through All Teams
                    for(int i=0;i<jsonArray.length();i++)
                    {
                        JSONObject js = jsonArray.getJSONObject(i);
                        // tmp hash map for single team
                        HashMap<String,String> is = new HashMap<>();
                        String val = js.getString("val");
                        is.put("val",val);
                        // adding team to team list
                        sqlRow.add(is);
                    }

                } catch (final JSONException e) {
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
        //till here
        @Override
        protected void onPostExecute(Void aVoid)
        //In onPostExecute() method the progress dialog is dismissed and
        // the array list data is displayed in list view using an adapter.
        {
            super.onPostExecute(aVoid);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();

            /**
             * Updating parsed JSON data into ListView
             * */

            items1=new String[sqlRow.size()];
            for (int a =0; a<sqlRow.size(); a++) {
                HashMap<String, String> tmpData = (HashMap<String, String>) sqlRow.get(a);
                Set<String> key = tmpData.keySet();
                Iterator it = key.iterator();

                while (it.hasNext()) {
                    String hmKey = (String)it.next();
                    String hmData = (String) tmpData.get(hmKey);
                    items1[a]=new String(hmData);
                    System.out.println("Key: " + hmKey +" & Data: "+hmData);
                    it.remove(); // avoids a ConcurrentModificationException
                }
            }
            List<String> orglist1=new ArrayList<>(Arrays.asList(items1));
            ArrayAdapter<String> adap1=new ArrayAdapter<String>(Register.this,R.layout.spinner_item,R.id.name3,orglist1);
            adap1.setDropDownViewResource(R.layout.spinner_item);
            s2.setAdapter(adap1);
        }
    }
}
