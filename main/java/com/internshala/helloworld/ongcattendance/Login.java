package com.internshala.helloworld.ongcattendance;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.Formatter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
import java.net.NetworkInterface;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;



public class Login extends AppCompatActivity {


    public static String ip,ip1="http://192.168.1.100/naya2/autoLogin1.php";
    public static String x=null;
    public static String getPortal()
    {

        return ip;
    }

    // This getMacAddr method fetches the mac address of user during registeration process.
    //this code for fetching mac address taken from stackoverflow.com.

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

    WifiReceiver receiverWifi;

    StringBuilder sb = new StringBuilder();


    class WifiReceiver extends BroadcastReceiver
    {
        public void onReceive(Context c, Intent intent)
        {

            ArrayList<String> connections=new ArrayList<String>();
            ArrayList<Float> Signal_Strenth= new ArrayList<Float>();

            sb = new StringBuilder();
            List<ScanResult> wifiList;
            wifiList = wifiManager.getScanResults();
            for(int i = 0; i < wifiList.size(); i++)
            {

                connections.add(wifiList.get(i).SSID);
            }
        }
    }

    int flag=0;
    WifiManager wifiManager;

    List<ScanResult> mScanResults;
    TextView t1,t2;
    String cpf1,mac1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Button b1,b3,b2;

        //Toast.makeText(Login.this, getMacAddr(), Toast.LENGTH_LONG).show();
        mac1=getMacAddr();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        new AsyncIP().execute();
       // new AsyncVer().execute();



        /*final String networkSSID = "MyLaptop";
        String networkPass = "12345678";

        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID = "\"" + networkSSID + "\"";
        conf.preSharedKey = "\""+ networkPass +"\"";


        wifiManager = (WifiManager)this.getSystemService(Context.WIFI_SERVICE);

        receiverWifi = new WifiReceiver();
        registerReceiver(receiverWifi, new IntentFilter(
                WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        if(wifiManager.isWifiEnabled()==false)
        {
            wifiManager.setWifiEnabled(true);
        }
        wifiManager.addNetwork(conf);
        wifiManager.startScan();
        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
        for( WifiConfiguration i : list ) {
            if(i.SSID != null && i.SSID.equals("\"" + networkSSID + "\"")) {
                wifiManager.disconnect();
                wifiManager.enableNetwork(i.networkId, true);
                wifiManager.reconnect();

                break;
            }
        }
*/
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


//if user clicked on forgot Password textview, then the activity switches to activity_forgot_pass.xml
        t1=(TextView)findViewById(R.id.textView16);
        t1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Login.this, ForgotPass.class);
                startActivity(i);
            }
        });

//if user clicked on adminLogin, then the activity switches to activity_admin_login.xml
        t2=(TextView)findViewById(R.id.textView17);
        t2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(Login.this,AdminLogin.class);
                startActivity(i);
            }
        });

//if user clicked on AutoLogin button, then the AsyncAutoLogin() task will be execute.
        b2=(Button)findViewById(R.id.button13);
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AsyncAutoLogin().execute();
            }
        });


//if user clicked on Register button, then the activity switches to activity_register.xml.
        b3=(Button)findViewById(R.id.button3);
        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(Login.this,Register.class);
                startActivity(i);
            }
        });
        //if user clicked on LOGIN button, then
        b1=(Button)findViewById(R.id.button);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText e1, e2;
                e1 = (EditText) findViewById(R.id.editText1);
                e2 = (EditText) findViewById(R.id.editText2);
                final String cpf = e1.getText().toString();
                final String pass1 = e2.getText().toString();
                cpf1 = cpf;
                new AsyncLogin().execute(cpf, pass1);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
       /*
    AsyncTask must be subclassed to be used. The subclass will override at least one method (doInBackground(Params...)),
    and most often will override a second one (onPostExecute(Result).)
    */

    /**
     * Async task class to get ip address
     */

    private class AsyncIP extends AsyncTask <String,String,String>
    {
        ProgressDialog pdLoading = new ProgressDialog(Login.this);
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
            @SuppressLint("WifiManagerLeak")
            WifiManager wifiMgr = (WifiManager) getSystemService(WIFI_SERVICE);
            WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
            int ip = wifiInfo.getIpAddress();//store ip address into variable ip.
            @SuppressWarnings("deprecation")
            String ipAddress = Formatter.formatIpAddress(ip);//format ip address data type integer into String.
            MyClient mc=new MyClient();
            mc.run1(ipAddress);
            return mc.getIP();
        }

        @Override
         /*
        onPostExecute(Result), invoked on the UI thread after the background computation finishes.
        The result of the background computation is passed to this step as a parameter
         */
        protected void onPostExecute(String result) {
            pdLoading.dismiss();
            //this method will be running on UI thread
            Toast.makeText(Login.this,result,Toast.LENGTH_LONG).show();
            // This will display the ip address into the Screen.
            ip=result;
            //new AsyncVer().execute();
        }
    }

     /*
    AsyncTask must be subclassed to be used. The subclass will override at least one method (doInBackground(Params...)),
    and most often will override a second one (onPostExecute(Result).)
    */

    /**
     * Async task class to get data automatically from same MAC
     */

    private class AsyncAutoLogin extends AsyncTask<String, String, String>
    {
        ProgressDialog pdLoading = new ProgressDialog(Login.this);
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

                String x1=getPortal();
                // Enter URL address where your php file resides
                url = new URL(getResources().getString(R.string.Folder)+"autoLogin1.php");

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return "exception1";
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
                        .appendQueryParameter("mac", mac1);

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
                return "exception2";
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
        protected void onPostExecute(String result) {
             /*
        In onPostExecute() method the progress dialog is dismissed and
        the array list data is displayed in list view using an adapter.
         */

            //this method will be running on UI thread

            pdLoading.dismiss();
            //
            if (result.equalsIgnoreCase("false")){

                // If username and password does not match display a error message
                Toast.makeText(Login.this, "Your Device is not Registered", Toast.LENGTH_LONG).show();

            } else if (result.equalsIgnoreCase("exception1") || result.equalsIgnoreCase("unsuccessful")) {

                Toast.makeText(Login.this, "OOPs! Something went wrong. Connection Problem."+result, Toast.LENGTH_LONG).show();

            }
            else
            {
                //Toast.makeText(Login.this, ""+result+"Y", Toast.LENGTH_LONG).show();
                Intent i=new Intent(Login.this,UserPortal.class);
                i.putExtra("cpf",result);
                startActivity(i);
            }
        }

    }
    /*
    AsyncTask must be subclassed to be used. The subclass will override at least one method (doInBackground(Params...)),
    and most often will override a second one (onPostExecute(Result).)
    */

    /**
     * Async task class to get information for LOGIN
     */

    private class AsyncLogin extends AsyncTask<String, String, String>
    {
        ProgressDialog pdLoading = new ProgressDialog(Login.this);
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
                url = new URL(ip+"/Login1.php");

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
                        .appendQueryParameter("username", params[0])
                        .appendQueryParameter("password", params[1]);
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
        protected void onPostExecute(String result) {
            //  In onPostExecute() method the progress dialog is dismissed

            //this method will be running on UI thread

            pdLoading.dismiss();
            //
            if(result.equalsIgnoreCase("true"))
            {
                /* Here launching another activity when login successful. If you persist login state
                use sharedPreferences of Android. and logout button to clear sharedPreferences.
                 */
                Intent i=new Intent(Login.this,UserPortal.class);
                i.putExtra("cpf",cpf1);
                startActivity(i);


            }else if (result.equalsIgnoreCase("false")){

                // If username and password does not match display a error message
                Toast.makeText(Login.this, "Invalid email or password", Toast.LENGTH_LONG).show();

            } else if (result.equalsIgnoreCase("exception") || result.equalsIgnoreCase("unsuccessful")) {

                Toast.makeText(Login.this, "OOPs! Something went wrong. Connection Problem.", Toast.LENGTH_LONG).show();

            }
        }

    }
     /*
    AsyncTask must be subclassed to be used. The subclass will override at least one method (doInBackground(Params...)),
    and most often will override a second one (onPostExecute(Result).)
    */

    /**
     * Async task class to get version of this App
     */
    private class AsyncVer extends AsyncTask<String, String, String>
    {
        ProgressDialog pdLoading = new ProgressDialog(Login.this);
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
        /*
        doInBackground(Params...), invoked on the background thread immediately after onPreExecute() finishes executing.
        This step is used to perform background computation that can take a long time.
        The parameters of the asynchronous task are passed to this step.
        The result of the computation must be returned by this step and will be passed back to the last step.
        This step can also use publishProgress(Progress...) to publish one or more units of progress.
        These values are published on the UI thread, in the onProgressUpdate(Progress...) step.
         */
        @Override
        protected String doInBackground(String... params) {
            try {

                // Enter URL address where your php file resides
                url = new URL(getResources().getString(R.string.Folder)+"apkver.php");

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
                /*conn.setRequestMethod("POST");

                // setDoInput and setDoOutput method depict handling of both send and receive
                conn.setDoInput(true);
                conn.setDoOutput(true);

                // Append parameters to URL
                Uri.Builder builder = new Uri.Builder();

                String query = builder.build().getEncodedQuery();

                // Open connection for sending data
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();*/
                conn.connect();

            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                return "exception1";
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
                return "exception2";
            } finally {
                conn.disconnect();
            }


        }

        @Override
        /*
        onPostExecute(Result), invoked on the UI thread after the background computation finishes.
        The result of the background computation is passed to this step as a parameter
         */
        protected void onPostExecute(String result) {

            //this method will be running on UI thread

            pdLoading.dismiss();
            //
            String vc=new String(""+BuildConfig.VERSION_CODE);
            //Toast.makeText(Login.this,"y"+result+"y"+vc,Toast.LENGTH_LONG).show();
//Here, it compare the version of the app and if current version is not equal to app version then,
            //it shows the Alert "New Updates Available!! Please Update Your App" message on Screen.
            if(vc.compareTo(result)!=0) {
                AlertDialog.Builder a = new AlertDialog.Builder(Login.this);
                a.setMessage("New Updates Available!! Please Update Your App")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });


                AlertDialog alert = a.create();
                alert.setTitle("Important App Updates!!");
                alert.show();
            }
        }

    }
}
