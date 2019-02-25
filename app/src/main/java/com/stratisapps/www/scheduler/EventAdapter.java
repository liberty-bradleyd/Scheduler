package com.stratisapps.www.scheduler;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Handler;
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

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.ExecutionException;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {

    private Context context = null;
    private ArrayList<ArrayList<String>> listOfEvents = null;
    private Activity activity = null;
    private SharedPreferences sharedPreferences = null;
    private SharedPreferences.Editor editor = null;
    private ArrayList<Button> listTitleViews = new ArrayList<>();

    public EventAdapter(Context context, Activity activity){
        this.context = context;
        listOfEvents = new ArrayList<>();
        this.activity = activity;
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
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup viewGroup, int pos) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.event_custom_layout, viewGroup, false);
        sharedPreferences = viewGroup.getContext().getSharedPreferences("AppData", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final EventAdapter.ViewHolder viewHolder, int pos) {

        ArrayList<String> tempEvent = listOfEvents.get(pos);

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

        listTitleViews.add(viewHolder.category);
        final ArrayList<String> finalTempEvent = tempEvent;
        final int finalPos = pos;
        setOnClick(viewHolder.itemView, finalTempEvent, finalPos);
        setOnClick(viewHolder.category, finalTempEvent, finalPos);
        setOnClick(viewHolder.title, finalTempEvent, finalPos);
        setOnClick(viewHolder.date, finalTempEvent, finalPos);
        setTitleColor(viewHolder.category, tempEvent);
        viewHolder.category.setText(tempEvent.get(0));
        viewHolder.title.setText(tempEvent.get(1));
        viewHolder.date.setText(month + "\n" + day);
    }

    public void setOnClick(View v, final ArrayList<String> finalTempEvent, final int finalPos){
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean initialChange = true;
                for(int i = 0; i < listTitleViews.size(); i++){
                    if(listTitleViews.get(i).getText().equals(listTitleViews.get(finalPos).getText())){
                        if(initialChange){
                            editor.putString(finalTempEvent.get(0), "").apply();
                            initialChange = false;
                        }
                        setTitleColor(listTitleViews.get(i), finalTempEvent);
                    }
                }
            }
        });
    }

    public void setTitleColor(Button button, ArrayList<String> tempEvent){
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
        button.setBackgroundTintList(ColorStateList.valueOf(Color.argb(255,randRVal, randGVal, randBVal)));
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
        listTitleViews = new ArrayList<>();
        notifyDataSetChanged();
    }

    public void remove(ArrayList<ArrayList<String>> arrayList, int pos){
        listOfEvents = arrayList;
        notifyItemRemoved(pos);
        notifyItemRangeChanged(pos, listOfEvents.size());

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                listTitleViews = new ArrayList<>();
                notifyDataSetChanged();
            }
        }, 800);
        try {
            CountEvents countEvents = new CountEvents(context, activity);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

