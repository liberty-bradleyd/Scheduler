package com.stratisapps.www.scheduler;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class RemoveEventTask extends AsyncTask<Void, Void, Void> {

    private Context context = null;
    private Activity activity = null;
    private EventAdapter eventAdapter = null;
    private int pos = 0;

    public RemoveEventTask(Context context, Activity activity, EventAdapter eventAdapter, int pos){
        this.context = context;
        this.activity = activity;
        this.eventAdapter = eventAdapter;
        this.pos = pos;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("AppData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String path = sharedPreferences.getString("FilePath", "");
        final ArrayList<ArrayList<String>> listOfEvents = new ArrayList<>();
        ArrayList<ArrayList<String>> listOfEventsTemp = new ArrayList<>();
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

            listOfEvents.remove(pos);

            BufferedWriter writer = new BufferedWriter(new FileWriter(path));
            writer.write("");
            writer = new BufferedWriter(new FileWriter(path, true));
            for(int i = 0; i < listOfEvents.size(); i++){
                for(int j = 0; j < listOfEvents.get(i).size(); j++){
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
                    eventAdapter.remove(listOfEvents, pos);
                    try {
                        CountEvents countEvents = new CountEvents(context, activity);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
