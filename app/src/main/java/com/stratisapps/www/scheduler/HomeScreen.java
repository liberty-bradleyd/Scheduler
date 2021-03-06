package com.stratisapps.www.scheduler;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
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
    private SwipeRefreshLayout refreshLayout = null;

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
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        recyclerView = findViewById(R.id.recycleView);
        mLayoutManager = new LinearLayoutManager(this);
        refreshLayout = findViewById(R.id.refreshLayout);
        recyclerView.setLayoutManager(mLayoutManager);
        eventAdapter = new EventAdapter(getApplicationContext(), this);
        recyclerView.setAdapter(eventAdapter);
        swipeController = new SwipeController(getApplicationContext(),this, eventAdapter);
        itemTouchHelper = new ItemTouchHelper(swipeController);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        if(sharedPreferences.getBoolean("NeedsInitialization", true)){
            String root = getApplicationContext().getFilesDir().toString();
            File directory = new File(root);
            if (!directory.exists()) {
                directory.mkdirs();
            }
            File file = new File(directory, "events.txt");
            editor.putString("FilePath", file.getAbsolutePath());
            editor.putBoolean("NeedsInitialization", false).apply();
        }

        refreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadPage();
            }
        });

        loadPage();
    }

    public void loadPage(){
        if(sharedPreferences.getBoolean("EventCondition", false)){
            new SaveEventTask(getApplicationContext(), this, eventAdapter).execute();
            editor.putBoolean("EventCondition", false).apply();
        }
        else {
            new LoadEventsTask(getApplicationContext(),this, eventAdapter).execute();
        }
        calendar = Calendar.getInstance();
        setCurrentDate();
        refreshLayout.setRefreshing(false);
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
}
