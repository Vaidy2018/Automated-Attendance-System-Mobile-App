package com.internshala.helloworld.ongcattendance;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
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
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;



public class Team1 extends AppCompatActivity {

    ListView mDrawerList;
    RelativeLayout mDrawerPane;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    ArrayList<NavItem> mNavItems = new ArrayList<NavItem>();
    String cpf;
    ProgressDialog pDialog;
    ListView list;

    private String selectItemFromDrawer(int position) {
        Fragment fragment = new PreferencesFragment();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout1);
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.mainContent1, fragment)
                .commit();

        mDrawerList.setItemChecked(position, true);
        setTitle(mNavItems.get(position).mTitle);

        // Close the drawer

        mDrawerLayout.closeDrawer(mDrawerPane);
        return mNavItems.get(position).mTitle;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team1);
        list=(ListView)findViewById(R.id.listView2);
        cpf= getIntent().getStringExtra("cpf");
        new CheckLeader().execute();
        mNavItems.add(new NavItem("Home", "Meetup destination", R.drawable.ic_action_home));

        mNavItems.add(new NavItem("Team","For Group Leaders",R.drawable.ic_people_black_24dp));
        mNavItems.add(new NavItem("Developers","Those who built it from Scratch",R.drawable.ic_copyright_black_24dp));
        mNavItems.add(new NavItem("Logout", "", R.drawable.ic_exit_to_app_black_24dp));


        // DrawerLayout
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout1);





        // Populate the Navigtion Drawer with options
        mDrawerPane = (RelativeLayout) findViewById(R.id.drawerPane1);
        mDrawerList = (ListView) findViewById(R.id.navList);
        DrawerListAdapter adapter = new DrawerListAdapter(this, mNavItems);
        mDrawerList.setAdapter(adapter);

        // Drawer Item click listeners
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String x = selectItemFromDrawer(position);
                if (x.compareTo("Home") == 0) {
                    Intent i = new Intent(Team1.this, UserPortal.class);
                    i.putExtra("cpf", cpf);
                    startActivity(i);
                }
                if (x.compareTo("Logout") == 0) {
                    Intent i = new Intent(Team1.this, Login.class);

                    startActivity(i);
                }


            }
        });

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close)
        {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                Log.d("Drawer", "onDrawerClosed: " + getTitle());

                invalidateOptionsMenu();
            }
        };

        mDrawerLayout.addDrawerListener(mDrawerToggle);


        


    }



    private class CheckLeader extends AsyncTask<String, String, String>
    {
        ProgressDialog pdLoading = new ProgressDialog(Team1.this);
        HttpURLConnection conn;
        URL url = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //this method will be running on UI thread
            pdLoading.setMessage("\tLoading...");
            pdLoading.setCancelable(false);
            pdLoading.show();

        }
        @Override
        protected String doInBackground(String... params) {
            try {

                // Enter URL address where your php file resides
                url = new URL(getResources().getString(R.string.Folder)+"TeamCheck1.php");

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
            if (result.equalsIgnoreCase("exception") || result.equalsIgnoreCase("unsuccessful")) {

                Toast.makeText(Team1.this, "OOPs! Something went wrong. Connection Problem.", Toast.LENGTH_LONG).show();

            }
            else if (result.equalsIgnoreCase("false")){

                // If username and password does not match display a error message
                Toast.makeText(Team1.this, "You are not a Team Leader!!", Toast.LENGTH_LONG).show();
                Intent i=new Intent(Team1.this,UserPortal.class);
                i.putExtra("cpf",cpf);
                startActivity(i);

            } else  {
                /* Here launching another activity when login successful. If you persist login state
                use sharedPreferences of Android. and logout button to clear sharedPreferences.
                 */
//call other async task

                new UserList().execute();


            }
        }

    }

    private class UserList extends AsyncTask<Void, Void, Void> {
        ArrayList<HashMap<String, String>> Attendance = new ArrayList<>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Team1.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... params) {
            String URL = getResources().getString(R.string.Folder) + "TeamMem1.php";
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
                        String name = js.getString("name");
                        String cpf1 = js.getString("cpf");
                        String mobile = js.getString("mobile");
                        String days = js.getString("days")+"%";
                        is.put("name",name);
                        is.put("cpf",cpf1);
                        is.put("mobile",mobile);
                        is.put("days",days);
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


            ListAdapter adapter = new SimpleAdapter(
                    Team1.this, Attendance,
                    R.layout.layout1, new String[]{"name","cpf", "mobile","days"}, new int[]{R.id.name3,R.id.cpf3,
                    R.id.mobile3,R.id.day3});
            list.setAdapter(adapter);
        }

    }
}
