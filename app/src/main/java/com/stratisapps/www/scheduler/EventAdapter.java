package com.stratisapps.www.scheduler;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {

    private Context context = null;
    private ArrayList<ArrayList<String>> listOfEvents = null;
    private SharedPreferences sharedPreferences = null;
    private SharedPreferences.Editor editor = null;

    public EventAdapter(Context context){
        this.context = context;
        listOfEvents = new ArrayList<>();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public Button category = null;
        public TextView title = null;
        public Button date = null;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            category = itemView.findViewById(R.id.categoryCustomView);
            title = itemView.findViewById(R.id.titleCustomView);
            date = itemView.findViewById(R.id.dateCustomView);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.event_custom_layout, viewGroup, false);
        sharedPreferences = viewGroup.getContext().getSharedPreferences("AppData", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull EventAdapter.ViewHolder viewHolder, int i) {

        Toast.makeText(context, Integer.toString(listOfEvents.size()), Toast.LENGTH_SHORT).show();
        ArrayList<String> tempEvent = listOfEvents.get(i);

        SimpleDateFormat originalFormat = new SimpleDateFormat("MM/dd/yyyy");
        String month = null;
        String day = null;
        try {
            Date dateValue = originalFormat.parse(tempEvent.get(3));
            month = (String) DateFormat.format("MMM",  dateValue);
            day = (String) DateFormat.format("dd",   dateValue);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String categoryColorVal = sharedPreferences.getString(tempEvent.get(0), "");
        Random randomColorVal = new Random();
        int randRVal = randomColorVal.nextInt(256);
        int randGVal = randomColorVal.nextInt(256);
        int randBVal = randomColorVal.nextInt(256);
        if(categoryColorVal.equals("")){
            while((randRVal < 100 || randGVal < 100 || randBVal < 100) || (randRVal >= 245 || randGVal >= 245 || randBVal >= 245)){
                randRVal = randomColorVal.nextInt(256);
                randGVal = randomColorVal.nextInt(256);
                randBVal = randomColorVal.nextInt(256);
            }
            editor.putString(tempEvent.get(0), randRVal + " " + randGVal + " " + randBVal).apply();
        }
        else {
            String[] split = categoryColorVal.split("\\s+");
            randRVal = Integer.parseInt(split[0]);
            randGVal = Integer.parseInt(split[1]);
            randBVal = Integer.parseInt(split[2]);
        }

        viewHolder.category.setBackgroundTintList(ColorStateList.valueOf(Color.argb(255,randRVal, randGVal, randBVal)));

        viewHolder.category.setText(tempEvent.get(0));
        viewHolder.title.setText(tempEvent.get(1));
        viewHolder.date.setText(month + "\n" + day);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return listOfEvents.size();
    }

    public void setInitialData(ArrayList<ArrayList<String>> arrayList){
        listOfEvents = arrayList;
    }

}

