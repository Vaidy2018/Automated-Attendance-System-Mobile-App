package com.internshala.helloworld.ongcattendance;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;


class NavItem {
    String mTitle;
    String mSubtitle;
    int mIcon;

    public NavItem(String title, String subtitle, int icon) {
        mTitle = title;
        mSubtitle = subtitle;
        mIcon = icon;
    }
}

class DrawerListAdapter extends BaseAdapter {

    Context mContext;
    ArrayList<NavItem> mNavItems;



    public DrawerListAdapter(Context context, ArrayList<NavItem> navItems) {
        mContext = context;
        mNavItems = navItems;
    }

    @Override
    public int getCount() {
        return mNavItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mNavItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.usermenu, null);
        }
        else {
            view = convertView;
        }

        TextView titleView = (TextView) view.findViewById(R.id.title);
        TextView subtitleView = (TextView) view.findViewById(R.id.subTitle);
        ImageView iconView = (ImageView) view.findViewById(R.id.icon);

        titleView.setText( mNavItems.get(position).mTitle );
        subtitleView.setText( mNavItems.get(position).mSubtitle );
        iconView.setImageResource(mNavItems.get(position).mIcon);

        return view;
    }
}

public class UserPortal extends AppCompatActivity {

    ListView mDrawerList;
    RelativeLayout mDrawerPane;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;

    ArrayList<NavItem> mNavItems = new ArrayList<NavItem>();
    String cpf;
    TextView t1,t2,t3,t4,t5,t6;
    ProgressDialog pDialog;
    Button b1;
    float x,y;

    private String selectItemFromDrawer(int position) {
        Fragment fragment = new PreferencesFragment();

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.mainContent, fragment)
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
        setContentView(R.layout.activity_user_portal);





        mNavItems.add(new NavItem("Home", "Meetup destination", R.drawable.ic_action_home));
        mNavItems.add(new NavItem("Settings", "Change your Password", R.drawable.ic_action_settings));
        mNavItems.add(new NavItem("Team","For Group Leaders",R.drawable.ic_people_black_24dp));
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
                if(x.compareTo("Settings")==0)
                {
                    Intent i=new Intent(UserPortal.this,changePassword.class);
                    i.putExtra("cpf",cpf);
                    startActivity(i);
                }
                if(x.compareTo("Logout")==0)
                {
                    Intent i=new Intent(UserPortal.this,Login.class);

                    startActivity(i);
                }
                if(x.compareTo("Team")==0)
                {
                    Intent i=new Intent(UserPortal.this,Team1.class);
                    i.putExtra("cpf",cpf);
                    startActivity(i);
                }
                if(x.compareTo("Developers")==0)
                {
                    Intent i=new Intent(UserPortal.this,Developer.class);
                    i.putExtra("cpf",cpf);
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
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        new AsyncDays().execute();
        cpf = getIntent().getStringExtra("cpf");
        t1 = (TextView) findViewById(R.id.textView9);
        t2 = (TextView) findViewById(R.id.textView12);
        t3 = (TextView) findViewById(R.id.textView10);
        t4 = (TextView) findViewById(R.id.textView13);
        t5 = (TextView) findViewById(R.id.userName);
        t6 = (TextView) findViewById(R.id.desc);
        b1=(Button)findViewById(R.id.button2);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UserPortal.this, AttendanceHistory.class);
                i.putExtra("cpf", cpf);
                startActivity(i);
            }
        });

        t6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UserPortal.this, Profile.class);
                i.putExtra("cpf", cpf);
                startActivity(i);
            }
        });


        new FetchUserFromJSON().execute();


    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    private class AsyncDays extends AsyncTask<String, String, String>
    {
        ProgressDialog pdLoading = new ProgressDialog(UserPortal.this);
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
                url = new URL(getResources().getString(R.string.Folder)+"totalDays1.php");

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
            t4.setText(result);
            y=Float.parseFloat(result);
        }

    }

    private class FetchUserFromJSON extends AsyncTask<Void, Void, Void> {
        ArrayList<HashMap<String, String>> sqlRow = new ArrayList<>();
        String x1;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(UserPortal.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            String URL = getResources().getString(R.string.Folder)+"userInfo1.php";
            Uri builtUri = Uri.parse(URL).buildUpon()
                    .appendQueryParameter("cpf", cpf)
                    .build();
            HttpHandler handler = new HttpHandler();
            String jsonstring = handler.makeServiceCall(builtUri.toString());
            x1=jsonstring;
            System.out.println(jsonstring);
            if (jsonstring != null) {
                try {
                    JSONObject jsonObject1 = new JSONObject(jsonstring);
                    //JSONObject jsonObject1 = jsonObject.getJSONObject("result");


                    JSONArray jsonArray = jsonObject1.getJSONArray("result");
                    for(int i=0;i<jsonArray.length();i++)
                    {
                        JSONObject js = jsonArray.getJSONObject(i);

                        HashMap<String,String> is = new HashMap<>();
                        String name = js.getString("name");
                        String cpf2 = js.getString("cpf");
                        String days = js.getString("present");


                        is.put("name",name);
                        is.put("cpf2",cpf2);
                        is.put("days", days);


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
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (pDialog.isShowing())
                pDialog.dismiss();
            String s1[]=new String[3];

            for (int a =0; a<sqlRow.size(); a++) {
                HashMap<String, String> tmpData = (HashMap<String, String>) sqlRow.get(a);
                Set<String> key = tmpData.keySet();
                Iterator it = key.iterator();
                int i=0;
                while (it.hasNext()) {
                    String hmKey = (String)it.next();
                    String hmData = (String) tmpData.get(hmKey);
                    s1[i]=hmData;i++;
                    System.out.println("Key: " + hmKey +" & Data: "+hmData);
                    it.remove(); // avoids a ConcurrentModificationException
                }

            }

            t1.setText(s1[0]);
            t2.setText(s1[1]);
            t3.setText(s1[2]);
            t5.setText(s1[0]);
            x=Float.parseFloat(s1[1]);
            PieChart pieChart = (PieChart) findViewById(R.id.piechart);
            pieChart.setUsePercentValues(true);
            ArrayList<Entry> yvalues = new ArrayList<Entry>();
            yvalues.add(new Entry(x, 0));
            yvalues.add(new Entry(y - x, 1));

            PieDataSet dataSet = new PieDataSet(yvalues, "Attendance Results");
            //dataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);
            int color[]={Color.GREEN,Color.RED};
            dataSet.setColors(color);

            ArrayList<String> xVals = new ArrayList<String>();

            xVals.add("Present");
            xVals.add("Absent");

            pieChart.setDrawHoleEnabled(false);

            PieData data = new PieData(xVals, dataSet);
            data.setValueFormatter(new PercentFormatter());
            pieChart.setData(data);
            /**
             * Updating parsed JSON data into ListView
             * */
            // tx1.setText(src+"-"+dest);
           /* ListAdapter adapter = new SimpleAdapter(
                    FlightDisplayActivity.this, sqlRow,
                    R.layout.flight_display_item, new String[]{"source_destination","flightcode", "flightname",
                    "duration","cost","time","depdate"}, new int[]{R.id.sourcedestination1,R.id.flightcode1,
                    R.id.flightname1, R.id.flightduration1,R.id.cost1,R.id.time1,R.id.dep});
            listView.setAdapter(adapter);
        }*/
        }
    }
}



