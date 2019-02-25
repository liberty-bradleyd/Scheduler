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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutionException;

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
        ArrayList<ArrayList<String>> listOfEvents = new ArrayList<>();
        final ArrayList<ArrayList<String>> listOfUpcomingEvents = new ArrayList<>();
        ArrayList<String> tempEvent = new ArrayList<>();
        SimpleDateFormat originalFormat = new SimpleDateFormat("MM/dd/yyyy");
        Date currentDate = null;
        try {
            // Java Date API months are structured 0-11 rather than 1-12 so all instances must be changed to fit accepted format
            Calendar calendar = Calendar.getInstance();
            int month = calendar.get(Calendar.MONTH) + 1;
            String date = month + "/" + calendar.get(Calendar.DAY_OF_MONTH) + "/" + calendar.get(Calendar.YEAR);
            currentDate = originalFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
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

            for(int i = 0; i < listOfEvents.size(); i++){
                Date test = null;
                try {
                    test = originalFormat.parse(listOfEvents.get(i).get(3));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if(currentDate.before(test) || currentDate.equals(test)){
                    listOfUpcomingEvents.add(listOfEvents.get(i));
                }
            }

            BufferedWriter writer = new BufferedWriter(new FileWriter(path));
            writer.write("");
            writer = new BufferedWriter(new FileWriter(path, true));
            for(int i = 0; i < listOfUpcomingEvents.size(); i++){
                for(int j = 0; j < listOfUpcomingEvents.get(i).size(); j++){
                    writer.write(listOfEvents.get(i).get(j).toString());
                    writer.newLine();
                }
                writer.write("new");
                writer.newLine();
            }
            writer.flush();
            writer.close();

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(listOfUpcomingEvents.size() > 0){
                        eventAdapter.setInitialData(listOfUpcomingEvents);
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
