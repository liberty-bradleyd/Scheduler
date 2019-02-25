package com.stratisapps.www.scheduler;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class CountEvents {

    private Context context = null;
    private Activity activity = null;

    public CountEvents(Context context, Activity activity) throws IOException {
        this.context = context;
        this.activity = activity;
        startProcess();
    }

    public void startProcess() throws IOException {
        Button numOfEventsView = activity.findViewById(R.id.numOfEvents);
        int numOfEvents = count();
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

    public int count() throws IOException {
        SharedPreferences sharedPreferences = context.getSharedPreferences("AppData", Context.MODE_PRIVATE);
        String path = sharedPreferences.getString("FilePath", "");
        final ArrayList<ArrayList<String>> listOfEvents = new ArrayList<>();
        ArrayList<String> tempEvent = new ArrayList<>();
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
        return listOfEvents.size();
    }
}
