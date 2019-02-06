package com.stratisapps.www.scheduler;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class HomeScreen extends AppCompatActivity {

    private Calendar calendar = null;
    private SimpleDateFormat fullMonthName = null;
    private SimpleDateFormat monthDay = null;
    private SharedPreferences sharedPreferences = null;
    private SharedPreferences.Editor editor = null;
    private RecyclerView recyclerView = null;
    private EventAdapter eventAdapter = null;
    private RecyclerView.LayoutManager mLayoutManager = null;
    private SwipeController swipeController = null;
    private ItemTouchHelper itemTouchHelper = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        startService(new Intent(getBaseContext(), OnAppClose.class));
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        sharedPreferences = getSharedPreferences("AppData", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        FloatingActionButton addButton = findViewById(R.id.add);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addIntent = new Intent(HomeScreen.this, AddEvent.class);
                startActivity(addIntent);
                finish();
            }
        });
        recyclerView = findViewById(R.id.recycleView);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        eventAdapter = new EventAdapter(this);
        recyclerView.setAdapter(eventAdapter);
        swipeController = new SwipeController();
        itemTouchHelper = new ItemTouchHelper(swipeController);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        if(sharedPreferences.getBoolean("backPressed", false)){
            new LoadEventsTask(getApplicationContext(),this, eventAdapter).execute();
            editor.putBoolean("backPressed", false).apply();
        }
        if(sharedPreferences.getBoolean("NeedsInitialization", true)){
            String root = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString();
            File directory = new File(root);
            if (!directory.exists()) {
                directory.mkdirs();
            }
            File file = new File(directory, "events.txt");
            editor.putString("FilePath", file.getAbsolutePath());

            // Starts the process of loading the events, if any, into the ListView while processing on the bg thread
            new LoadEventsTask(getApplicationContext(),this, eventAdapter).execute();

            editor.putBoolean("NeedsInitialization", false).apply();
        }
        saveEvent();
        calendar = Calendar.getInstance();
        setCurrentDate();
        try {
            checkForEvents();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveEvent(){
        if(sharedPreferences.getBoolean("EventCondition", false)){
            new SaveEventTask(getApplicationContext(), this, eventAdapter).execute();
            editor.putBoolean("EventCondition", false).apply();
        }
    }

    public void setCurrentDate(){
        // Sets the current month
        fullMonthName = new SimpleDateFormat("MMMM");
        TextView monthNameView = findViewById(R.id.monthName);
        monthNameView.setText(fullMonthName.format(calendar.getTime()));
        // Sets the current day of the month
        monthDay = new SimpleDateFormat("dd");
        TextView monthDayView = findViewById(R.id.monthDay);
        monthDayView.setText(monthDay.format(calendar.getTime()));
    }

    public void checkForEvents() throws IOException {
        // Scans file in the background thread to check for the number of events the user currently has
        Button numOfEventsView = findViewById(R.id.numOfEvents);
        CountEvents countEvents = new CountEvents(getApplicationContext());
        int numOfEvents = countEvents.count();
        if(numOfEvents > 0){
            if(numOfEvents < 2) {
                numOfEventsView.setText(numOfEvents + " event");
            }
            else {
                numOfEventsView.setText(numOfEvents + " events");
            }
            numOfEventsView.setVisibility(View.VISIBLE);
        }
        else{
            numOfEventsView.setVisibility(View.INVISIBLE);
        }
    }
}
