package com.stratisapps.www.scheduler;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutionException;

public class SaveEventTask extends AsyncTask<Void, Void, Void> {

    private Context context = null;
    private Activity activity = null;
    private EventAdapter eventAdapter = null;
    SimpleDateFormat originalFormat = new SimpleDateFormat("MM/dd/yyyy");

    public SaveEventTask(Context context, Activity activity, EventAdapter eventAdapter){
        this.context = context;
        this.activity = activity;
        this.eventAdapter = eventAdapter;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("AppData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        final ArrayList<String> eventList = new ArrayList<>();
        eventList.add(sharedPreferences.getString("CategoryData", "").trim());
        eventList.add(sharedPreferences.getString("TitleData", "").trim());
        eventList.add(sharedPreferences.getString("ReminderData", ""));
        eventList.add(sharedPreferences.getString("DateData", ""));
        eventList.add(sharedPreferences.getString("TimeData", ""));
        String path = sharedPreferences.getString("FilePath", "");

        try {

            BufferedWriter writer = new BufferedWriter(new FileWriter(path, true));
            for(int i = 0; i < eventList.size(); i++){
                writer.write(eventList.get(i).toString());
                writer.newLine();
            }
            writer.write("new");
            writer.newLine();
            writer.flush();
            writer.close();

            final ArrayList<ArrayList<String>> listOfEvents = new ArrayList<>();
            ArrayList<ArrayList<String>> listOfEventsTemp = new ArrayList<>();
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

            ArrayList<ArrayList<String>> listWithoutOriginalRef = new ArrayList<>();
            for(int i = 0; i < listOfEvents.size(); i++){
                listWithoutOriginalRef.add(listOfEvents.get(i));
            }

            OrganizeListTask organizeListTask = new OrganizeListTask();
            listOfEventsTemp = organizeListTask.startProcess(listWithoutOriginalRef);

            listOfEvents.clear();

            for(int i = 0; i < listOfEventsTemp.size(); i++){
                listOfEvents.add(listOfEventsTemp.get(i));
            }

            BufferedWriter writer2 = new BufferedWriter(new FileWriter(path));
            writer2.write("");
            writer2 = new BufferedWriter(new FileWriter(path, true));
            for(int i = 0; i < listOfEvents.size(); i++){
                for(int j = 0; j < listOfEvents.get(i).size(); j++){
                    writer2.write(listOfEvents.get(i).get(j).toString());
                    writer2.newLine();
                }
                writer2.write("new");
                writer2.newLine();
            }
            writer2.flush();
            writer2.close();

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(listOfEvents.size() > 0){
                        eventAdapter.setInitialData(listOfEvents);
                    }
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        try {
            new CountEvents(context, activity);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
