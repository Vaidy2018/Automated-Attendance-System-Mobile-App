package com.internshala.helloworld.ongcattendance;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class AttendanceHistory extends AppCompatActivity {
    //Initializing all the variables here
    ProgressDialog pDialog;
    String cpf;
    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_history);
        Intent intent = getIntent();
        //Retrieve extended data from the intent.
        cpf = intent.getStringExtra("cpf");
        listView = (ListView)findViewById(R.id.list);
       new FetchAttendance().execute();
    }

     /*
    AsyncTask must be subclassed to be used. The subclass will override at least one method (doInBackground(Params...)),
    and most often will override a second one (onPostExecute(Result).)
    */
    /**
     * Async task class to get json by making HTTP call
     */

    private class FetchAttendance extends AsyncTask<Void, Void, Void> {
        ArrayList<HashMap<String, String>> Attendance = new ArrayList<>();

        @Override
        /*
        onPreExecute(), invoked on the UI thread before the task is executed.
        This step is normally used to setup the task, for instance by showing a progress bar in the user interface.
         */
        protected void onPreExecute() {
            super.onPreExecute();
            //In onPreExecute() method progress dialog is shown before making the http call.
            pDialog = new ProgressDialog(AttendanceHistory.this);
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
        // Once the json is fetched, it is parsed and each data is added to array list.
        {
            String URL = getResources().getString(R.string.Folder) + "attendanceHistory1.php";
            Uri builtUri = Uri.parse(URL).buildUpon()
                    .appendQueryParameter("cpf", cpf)
                    .build();
            HttpHandler handler = new HttpHandler();
            // Making a request to url and getting response
            String jsonstring = handler.makeServiceCall(builtUri.toString());
            System.out.println(jsonstring);
            if(jsonstring!=null) {
                try {
                     /*
                    If your JSON node starts with [, then we should use getJSONArray() method.
                    Same as if the node starts with {, then we should use getJSONObject() method.
                    */
                    JSONObject jsonObject = new JSONObject(jsonstring);
                    // Getting JSON Array node
                    JSONArray jsonArray = jsonObject.getJSONArray("result");
                    // looping through All details
                    for(int i=0;i<jsonArray.length();i++)
                    {
                        JSONObject js = jsonArray.getJSONObject(i);
                        HashMap<String,String> is = new HashMap<>();
                        // adding each child node to HashMap key => value
                        String date = js.getString("date");
                        String time = js.getString("time");
                        String day = js.getString("day");
                        
                        is.put("date",date);
                        is.put("time",time);
                        is.put("day",day);
                        
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
              */
            ListAdapter adapter = new SimpleAdapter(
                    AttendanceHistory.this, Attendance,
                    R.layout.layout, new String[]{"date","time", "day"}, new int[]{R.id.name3,R.id.cpf3,
                    R.id.mobile3});
            listView.setAdapter(adapter);
        }
    }
}

