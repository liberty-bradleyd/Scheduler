package com.stratisapps.www.scheduler;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Environment;
import android.preference.Preference;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class LoadEventsTask extends AsyncTask<Void, Void, Void> {

    private Context context = null;
    private Activity activity = null;
    private EventAdapter eventAdapter = null;

    public LoadEventsTask(Context context, Activity activity, EventAdapter eventAdapter){
        this.context = context;
        this.activity = activity;
        this.eventAdapter = eventAdapter;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("AppData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String path = sharedPreferences.getString("FilePath", "");
        final ArrayList<ArrayList<String>> listOfEvents = new ArrayList<>();
        ArrayList<String> tempEvent = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(path));
            String next = reader.readLine();
            while(next != null){
                if(next.equals("new")){
                    listOfEvents.add(tempEvent);
                    tempEvent = new ArrayList<>();
                }
                else {
                    tempEvent.add(next);
                }
                next = reader.readLine();
            }
            reader.close();
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(listOfEvents.size() > 0){
                        eventAdapter.setInitialData(listOfEvents);
                        eventAdapter.notifyDataSetChanged();
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
