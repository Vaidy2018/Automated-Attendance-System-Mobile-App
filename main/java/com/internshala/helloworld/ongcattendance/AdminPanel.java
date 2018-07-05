package com.internshala.helloworld.ongcattendance;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

//This code is for admin after login.

public class AdminPanel extends AppCompatActivity {

  //initializing variables
    ListView mDrawerList;
    RelativeLayout mDrawerPane;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    ArrayList<NavItem> mNavItems = new ArrayList<NavItem>();
    ProgressDialog pDialog;
    Button b1,b2, b3, b4;
    ListView listView;
    TextView t1,t2,t3;

    /** Swaps fragments in the main content view */
    private String selectItemFromDrawer(int position) {
        // // Create a new fragment
        Fragment fragment = new PreferencesFragment();

        /*
        To make fragment transactions in our activity (such as add, remove, or replace a fragment),
        we must use APIs from FragmentTransaction.
        we can get an instance of FragmentTransaction from our Activity like this:
         */
        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.mainContent, fragment)
                .commit();
        /*Once we've made your changes with FragmentTransaction,
         we must call commit() for the changes to take effect.
        */
       // Highlight the selected item, update the title, and close the drawer
        mDrawerList.setItemChecked(position, true);
        setTitle(mNavItems.get(position).mTitle);
        // Close the drawer
        mDrawerLayout.closeDrawer(mDrawerPane);
        return mNavItems.get(position).mTitle;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_panel);

        b1 = (Button) findViewById(R.id.button6);
        b2 = (Button) findViewById(R.id.button7);
        b3 = (Button) findViewById(R.id.button9);
        b4 = (Button) findViewById(R.id.button8);
        t1=(TextView) findViewById(R.id.textView19);
        t2=(TextView) findViewById(R.id.textView21);
        t3=(TextView) findViewById(R.id.textView20);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                t1.setText("Name");
                t3.setText("Cpf");
                t2.setText("");
                new FetchTodays().execute();
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                t1.setText("Name");
                t3.setText("MAC");
                t2.setText("CPF");
                new FetchUsers().execute();
            }
        });

        t1.setText("Name");t3.setText("Cpf");t2.setText("");
        new FetchTodays().execute();

        listView=(ListView)findViewById(R.id.listView);

        mNavItems.add(new NavItem("Developers","Those who built it from Scratch",R.drawable.ic_copyright_black_24dp));
        mNavItems.add(new NavItem("Logout", "", R.drawable.ic_exit_to_app_black_24dp));

        // DrawerLayout
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);

        // Populate the Navigtion Drawer with options
        mDrawerPane = (RelativeLayout) findViewById(R.id.drawerPane);
        mDrawerList = (ListView) findViewById(R.id.navList);
        DrawerListAdapter adapter = new DrawerListAdapter(this, mNavItems);
        mDrawerList.setAdapter(adapter);

        // Drawer Item click listeners
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String x= selectItemFromDrawer(position);

                if(x.compareTo("Logout")==0)
                {
                    Intent i=new Intent(AdminPanel.this,Login.class);

                    startActivity(i);
                }

                if(x.compareTo("Developers")==0)
                {
                    Intent i=new Intent(AdminPanel.this,Developer.class);

                    startActivity(i);
                }

            }
        });

        /*
        Users can open and close the navigation drawer with a swipe gesture from or
        towards the left edge of the screen, but if we're using the action bar,
        we should also allow users to open and close it by touching the app icon.
        And the app icon should also indicate the presence of the navigation drawer with a special icon.
        we can implement all this behavior by using the ActionBarDrawerToggle
         */
        mDrawerToggle = new ActionBarDrawerToggle (this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */
                )
        {
            @Override
            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

                invalidateOptionsMenu();
            }

            @Override
            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                Log.d("Drawer", "onDrawerClosed: " + getTitle());

                invalidateOptionsMenu();
            }
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
    }

    /*
     /*
    AsyncTask must be subclassed to be used. The subclass will override at least one method (doInBackground(Params...)),
    and most often will override a second one (onPostExecute(Result).)
    */

    /**
     * Async task class to get json by making HTTP call
     */
    private class FetchTodays extends AsyncTask<Void, Void, Void> {
        ArrayList<HashMap<String, String>> Attendance = new ArrayList<>();

        @Override
        /*
        onPreExecute(), invoked on the UI thread before the task is executed.
        This step is normally used to setup the task, for instance by showing a progress bar in the user interface.
         */
        protected void onPreExecute() {
            super.onPreExecute();
            //In onPreExecute() method progress dialog is shown before making the http call.
            pDialog = new ProgressDialog(AdminPanel.this);
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
            String URL = getResources().getString(R.string.Folder) + "userPresent1.php";
            Uri builtUri = Uri.parse(URL).buildUpon()
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
                        String name = js.getString("name");
                        String cpf = js.getString("cpf");
                       // adding each child node to HashMap key => value
                        is.put("name",name);
                        is.put("cpf",cpf);
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
            if (pDialog.isShowing())
                // Dismiss the progress dialog
                pDialog.dismiss();
            /**
            * Updating parsed JSON data into ListView
             */
            ListAdapter adapter = new SimpleAdapter(
                    AdminPanel.this, Attendance,
                    R.layout.todays, new String[]{"name","cpf"}, new int[]{R.id.name2,R.id.cpf2});
            listView.setAdapter(adapter);
        }

    }

     /*
    AsyncTask must be subclassed to be used. The subclass will override at least one method (doInBackground(Params...)),
    and most often will override a second one (onPostExecute(Result).)
    */

    /**
     * Async task class to get json by making HTTP call
     */
    private class FetchUsers extends AsyncTask<Void, Void, Void> {
        ArrayList<HashMap<String, String>> Attendance = new ArrayList<>();

        @Override
          /*
        onPreExecute(), invoked on the UI thread before the task is executed.
        This step is normally used to setup the task, for instance by showing a progress bar in the user interface.
         */
          protected void onPreExecute() {
            super.onPreExecute();
            //In onPreExecute() method progress dialog is shown before making the http call
            pDialog = new ProgressDialog(AdminPanel.this);
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
            String URL = getResources().getString(R.string.Folder) + "userList1.php";
            Uri builtUri = Uri.parse(URL).buildUpon()
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
                        String name = js.getString("name");
                        String cpf = js.getString("cpf");
                        String mac = js.getString("mac");
                        // adding each child node to HashMap key => value
                        is.put("name",name);
                        is.put("cpf",cpf);
                        is.put("mac",mac);

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
            if (pDialog.isShowing())
                pDialog.dismiss();
            /**
            * Updating parsed JSON data into ListView
            */
            ListAdapter adapter = new SimpleAdapter(
                    AdminPanel.this, Attendance,
                    R.layout.users, new String[]{"name","cpf","mac"}, new int[]{R.id.name3,R.id.cpf3,R.id.mac3});
            listView.setAdapter(adapter);
        }

    }
}
