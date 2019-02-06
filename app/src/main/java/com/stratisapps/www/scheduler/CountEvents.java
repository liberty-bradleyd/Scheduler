package com.stratisapps.www.scheduler;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class CountEvents {

    private Context context = null;

    public CountEvents(Context context) throws IOException {
        this.context = context;
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
