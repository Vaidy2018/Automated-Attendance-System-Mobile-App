package com.internshala.helloworld.ongcattendance;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;


public class Home extends AppCompatActivity {
;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //This is for splash Screen
        Handler mHandler = new Handler();
         /*
        A Handler allows you to send and process Message and Runnable objects associated with a thread's MessageQueue.
        Each Handler instance is associated with a single thread and that thread's message queue. When you create a new Handler,
        it is bound to the thread / message queue of the thread that is creating it -- from that point on,
        it will deliver messages and runnables to that message queue and execute them as they come out of the message queue.
         */
        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                /*
               Causes the Runnable r to be added to the message queue, to be run after the specified amount of time elapses.
                */
                //start your activity here
                Intent intent = new Intent(Home.this, Login.class);
                startActivity(intent);
                finish();
            }

        }, 2000L);//2000L is for 2 sec delay to start new activity after splash screen
    }
}
