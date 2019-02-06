package com.stratisapps.www.scheduler;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.ArrayList;

public class AddEvent extends AppCompatActivity {

    private EditText category = null;
    private EditText title = null;
    private EditText link = null;
    private EditText reminder = null;
    private EditText date = null;
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
        Button add = findViewById(R.id.add);
        category = findViewById(R.id.category);
        title = findViewById(R.id.title);
        reminder = findViewById(R.id.reminder);
        date = findViewById(R.id.date);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.putBoolean("backPressed", true).apply();
                Intent backIntent = new Intent(AddEvent.this, HomeScreen.class);
                startActivity(backIntent);
                finish();
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                editor.putBoolean("EventCondition", true).apply();
                editor.putString("CategoryData", category.getText().toString()).apply();
                editor.putString("TitleData", title.getText().toString()).apply();
                editor.putString("ReminderData", reminder.getText().toString()).apply();
                editor.putString("DateData", date.getText().toString()).apply();

                Intent returnIntent = new Intent(AddEvent.this, HomeScreen.class);
                startActivity(returnIntent);
                finish();
            }
        });
    }
}
