package com.stratisapps.www.scheduler;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import studio.carbonylgroup.textfieldboxes.ExtendedEditText;
import studio.carbonylgroup.textfieldboxes.TextFieldBoxes;

public class AddEvent extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener{

    private ExtendedEditText category = null;
    private TextFieldBoxes categoryBox = null;
    private ExtendedEditText title = null;
    private TextFieldBoxes titleBox = null;
    private ExtendedEditText date = null;
    private TextFieldBoxes dateBox = null;
    private ExtendedEditText time = null;
    private TextFieldBoxes timeBox = null;
    private DatePickerDialog datePickerDialog = null;
    private TimePickerDialog timePickerDialog = null;
    private boolean passedFormatTest = true;
    private Calendar calendar = Calendar.getInstance();
    private SharedPreferences sharedPreferences = null;
    private SharedPreferences.Editor editor = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        sharedPreferences = getSharedPreferences("AppData", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        ImageView back = findViewById(R.id.back);
        Button add = findViewById(R.id.addEvent);
        category = findViewById(R.id.category);
        categoryBox = findViewById(R.id.categoryBox);
        title = findViewById(R.id.title);
        titleBox = findViewById(R.id.titleBox);
        date = findViewById(R.id.date);
        dateBox = findViewById(R.id.dateBox);
        time = findViewById(R.id.time);
        timeBox = findViewById(R.id.timeBox);
        datePickerDialog = new DatePickerDialog(this, R.style.DialogColor,this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        timePickerDialog = new TimePickerDialog(this, R.style.DialogColor,this, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
        fillDateBox(calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.YEAR));
        category.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(!hasFocus){
                    if(!category.getText().toString().trim().isEmpty()){
                        if(category.getText().toString().length() > 16){
                            categoryBox.setSecondaryColor(getResources().getColor(R.color.colorError));
                        }
                        else {
                            categoryBox.setSecondaryColor(getResources().getColor(R.color.colorPrimary));
                        }
                    }
                }
            }
        });
        title.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(!hasFocus){
                    if(!title.getText().toString().trim().isEmpty()){
                        titleBox.setSecondaryColor(getResources().getColor(R.color.colorPrimary));
                    }
                }
            }
        });
        date.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(!hasFocus){
                    if(!date.getText().toString().trim().isEmpty() && passedFormatTest){
                        dateBox.setSecondaryColor(getResources().getColor(R.color.colorPrimary));
                    }
                    date.setHint(null);
                }
                else{
                    date.setHint("mm/dd/yyyy");
                }
            }
        });
        dateBox.getEndIconImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog.show();
            }
        });
        time.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(!hasFocus){
                    if(!time.getText().toString().trim().isEmpty() && passedFormatTest){
                        timeBox.setSecondaryColor(getResources().getColor(R.color.colorPrimary));
                    }
                    time.setHint(null);
                }
                else{
                    time.setHint("hh:mm");
                }
            }
        });
        timeBox.getEndIconImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timePickerDialog.show();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent backIntent = new Intent(AddEvent.this, HomeScreen.class);
                startActivity(backIntent);
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String categoryString = category.getText().toString();
                String titleString = title.getText().toString();
                String dateString = date.getText().toString();
                String timeString = time.getText().toString();
                boolean passedAllTests = true;
                if(timeString.isEmpty()){
                    timeBox.setSecondaryColor(getResources().getColor(R.color.colorError));
                    timeBox.setErrorColor(getResources().getColor(R.color.colorError));
                    timeBox.setError("Please enter a time", true);
                    passedAllTests = false;
                }
                else {
                    SimpleDateFormat originalFormat = new SimpleDateFormat("hh:mm");
                    Date test = null;
                    try{
                        test = originalFormat.parse(timeString);
                        passedFormatTest = true;
                    }
                    catch (Exception e ){
                        passedFormatTest = false;
                    }
                    if(passedFormatTest && timeString.length() <= 5 && timeString.substring(timeString.indexOf(":") + 1, timeString.length()).length() == 2 &&
                            Integer.valueOf(timeString.substring(0, timeString.indexOf(":"))) <= 12 && Integer.valueOf(timeString.substring(timeString.indexOf(":") + 1, timeString.length())) < 60){
                        Date currentDate = null;
                        try {
                            currentDate = originalFormat.parse(String.valueOf(calendar.get(Calendar.HOUR_OF_DAY)) + ":" + String.valueOf(calendar.get(Calendar.MINUTE)));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        if(currentDate.after(test)){
                            timeBox.setSecondaryColor(getResources().getColor(R.color.colorError));
                            timeBox.setErrorColor(getResources().getColor(R.color.colorError));
                            timeBox.setError("Please enter a time that hasn't occurred yet", true);
                            passedAllTests = false;
                        }
                        else {
                            timeBox.setPrimaryColor(getResources().getColor(R.color.colorPrimary));
                            timeBox.removeError();
                        }
                    }
                    else{
                        timeBox.setSecondaryColor(getResources().getColor(R.color.colorError));
                        timeBox.setErrorColor(getResources().getColor(R.color.colorError));
                        timeBox.setError("Please enter a time in the correct format", true);
                        passedAllTests = false;
                    }
                }
                if(dateString.isEmpty()){
                    dateBox.setSecondaryColor(getResources().getColor(R.color.colorError));
                    dateBox.setErrorColor(getResources().getColor(R.color.colorError));
                    dateBox.setError("Please enter a date", true);
                    passedAllTests = false;
                }
                else {
                    SimpleDateFormat originalFormat = new SimpleDateFormat("MM/dd/yyyy");
                    Date test = null;
                    try{
                        test = originalFormat.parse(dateString);
                        passedFormatTest = true;
                    }
                    catch (Exception e ){
                        passedFormatTest = false;
                    }
                    if(passedFormatTest){
                        Date currentDate = null;
                        try {
                            // Java Date API months are structured 0-11 rather than 1-12 so all instances must be changed to fit accepted format
                            int month = calendar.get(Calendar.MONTH) + 1;
                            String date = month + "/" + calendar.get(Calendar.DAY_OF_MONTH) + "/" + calendar.get(Calendar.YEAR);
                            currentDate = originalFormat.parse(date);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        if(currentDate.after(test)){
                            dateBox.setSecondaryColor(getResources().getColor(R.color.colorError));
                            dateBox.setErrorColor(getResources().getColor(R.color.colorError));
                            dateBox.setError("Please enter a date that hasn't occurred yet", true);
                            passedAllTests = false;
                        }
                        else {
                            dateBox.setPrimaryColor(getResources().getColor(R.color.colorPrimary));
                            dateBox.removeError();
                        }
                    }
                    else{
                        dateBox.setSecondaryColor(getResources().getColor(R.color.colorError));
                        dateBox.setErrorColor(getResources().getColor(R.color.colorError));
                        dateBox.setError("Please enter a date in the correct format", true);
                        passedAllTests = false;
                    }
                }
                if(titleString.isEmpty()){
                    titleBox.setSecondaryColor(getResources().getColor(R.color.colorError));
                    titleBox.setErrorColor(getResources().getColor(R.color.colorError));
                    titleBox.setError("Please enter a title", true);
                    passedAllTests = false;
                }
                else {
                    titleBox.setPrimaryColor(getResources().getColor(R.color.colorPrimary));
                    titleBox.removeError();
                }
                if(categoryString.isEmpty()){
                    categoryBox.setSecondaryColor(getResources().getColor(R.color.colorError));
                    categoryBox.setErrorColor(getResources().getColor(R.color.colorError));
                    categoryBox.setError("Please enter a category", true);
                    passedAllTests = false;
                }
                else if(categoryString.length() > 16){
                    categoryBox.setErrorColor(getResources().getColor(R.color.colorError));
                    categoryBox.setError("Please reduce the number of characters", true);
                    passedAllTests = false;
                }
                else {
                    categoryBox.setPrimaryColor(getResources().getColor(R.color.colorPrimary));
                    categoryBox.removeError();
                }
                if(passedAllTests){
                    editor.putBoolean("EventCondition", true).apply();
                    editor.putString("CategoryData", category.getText().toString()).apply();
                    editor.putString("TitleData", title.getText().toString()).apply();
                    editor.putString("ReminderData", "0").apply();
                    editor.putString("DateData", date.getText().toString()).apply();

                    Intent returnIntent = new Intent(AddEvent.this, HomeScreen.class);
                    startActivity(returnIntent);
                    finish();
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                }
            }
        });
    }

    public void fillDateBox(int month, int dayOfMonth, int year){
        month += 1;
        if(String.valueOf(month).length() < 2 && String.valueOf(dayOfMonth).length() < 2){
            date.setText("0" + month + "/0" + dayOfMonth + "/" + year);
        }
        else if(String.valueOf(month).length() < 2 && String.valueOf(dayOfMonth).length() >= 2){
            date.setText("0" + month + "/" + dayOfMonth + "/" + year);
        }
        else if(String.valueOf(month).length() >= 2 && String.valueOf(dayOfMonth).length() < 2){
            date.setText(month + "/0" + dayOfMonth + "/" + year);
        }
        else {
            date.setText(month + "/" + dayOfMonth + "/" + year);
        }
    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        fillDateBox(datePicker.getMonth(), datePicker.getDayOfMonth(), datePicker.getYear());
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        if(String.valueOf(hourOfDay).length() < 2 && String.valueOf(minute).length() < 2){
            time.setText("0" + hourOfDay + ":0" + minute);
        }
        else if(String.valueOf(hourOfDay).length() < 2 && String.valueOf(minute).length() >= 2){
            time.setText("0" + hourOfDay + ":" + minute);
        }
        else if(String.valueOf(hourOfDay).length() >= 2 && String.valueOf(minute).length() < 2){
            time.setText("" + hourOfDay + ":0" + minute);
        }
        else {
            time.setText(hourOfDay + ":" + minute);
        }
    }
}
