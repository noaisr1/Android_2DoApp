package com.example.taskslistfinalproject;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class UpdateTask extends AppCompatActivity {
    private  DatabaseHelper database;
    private TaskModel taskModel;
    private EditText etTitle, etDate, etDesc;
    private int year,month,day;
    private int retUrg;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_task_layout);

        Gson gson = new Gson();
        String target = getIntent().getStringExtra("selected_task");
        taskModel = gson.fromJson(target,TaskModel.class);

        // Title
        etTitle = findViewById(R.id.etTitle_id);
        etTitle.setText(taskModel.getName());

        // Description
        etDesc = findViewById(R.id.etDesc_id);
        etDesc.setText(taskModel.getDesc());
        // Date
        SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String dateStr= sdfDate.format(taskModel.getDate().getTime());

        etDate = findViewById(R.id.etDate_id);
        etDate.setText(dateStr);

        setYear(taskModel.getDate().get(Calendar.YEAR));
        setMonth(taskModel.getDate().get(Calendar.MONTH));
        setDay(taskModel.getDate().get(Calendar.DAY_OF_MONTH));


        etDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(UpdateTask.this,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                            String tmpStr = dayOfMonth+"/"+(month+1)+"/"+year;
                            String date_time = null;
                            try {
                                date_time = sdfDate.format(sdfDate.parse(tmpStr));
                                setYear(year);
                                setMonth(month);
                                setDay(dayOfMonth);

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            etDate.setText(date_time);
                        }
                    },year,month,day);
            datePickerDialog.show();
        });



        // Urgency spinner
        Spinner spinner = findViewById(R.id.spDropdown_id);
        //"Hint to be displayed
        ArrayAdapter<String> spAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item) {
            @Override
            public int getCount() {
                return super.getCount()-1;
            }

            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                if (position == getCount()) {
                    ((TextView) v.findViewById(android.R.id.text1)).setText(getUrgStr(taskModel.getUrgency()));
                    switch(taskModel.getUrgency()){
                        case 0:
                            ((TextView) v.findViewById(android.R.id.text1))
                                    .setTextColor(getApplicationContext().getResources().getColor(R.color.green));
                            break;
                        case 1:
                            ((TextView) v.findViewById(android.R.id.text1))
                                    .setTextColor(getApplicationContext().getResources().getColor(R.color.yellow_orange));
                            break;
                        case 2:
                            ((TextView) v.findViewById(android.R.id.text1))
                                    .setTextColor(getApplicationContext().getResources().getColor(R.color.red));
                            break;
                    }
                }
                return v;
            }

        };
        spAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spAdapter.add("Low");
        spAdapter.add("Medium");
        spAdapter.add("High");
        spAdapter.add("Select urgency");

        spinner.setAdapter(spAdapter);
        spinner.setSelection(spAdapter.getCount());

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        retUrg = 0;
                        ((TextView) view.findViewById(android.R.id.text1))
                                .setTextColor(getApplicationContext().getResources().getColor(R.color.green));
                        break;
                    case 1:
                        retUrg = 1;
                        ((TextView) view.findViewById(android.R.id.text1))
                                .setTextColor(getApplicationContext().getResources().getColor(R.color.yellow_orange));
                        break;
                    case 2:
                        retUrg = 2;
                        ((TextView) view.findViewById(android.R.id.text1))
                                .setTextColor(getApplicationContext().getResources().getColor(R.color.red));
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                retUrg = taskModel.getUrgency();
            }
        });


        // Save
        Button btnSaveChanges = findViewById(R.id.btnSaveChanges_id);
        btnSaveChanges.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            calendar.set(getYear(),getMonth(),getDay());

            database= new DatabaseHelper(UpdateTask.this);
            boolean success = database.updateData
                    (new TaskModel(taskModel.getId(),etTitle.getText().toString(),
                            etDesc.getText().toString(),
                            calendar,retUrg,taskModel.isDone()));

            if (success) Toast.makeText(UpdateTask.this, taskModel.getName()+ " Updated",Toast.LENGTH_SHORT).show();
            else Toast.makeText(UpdateTask.this,"Error: update failed",Toast.LENGTH_SHORT).show();
        });

        ImageButton btnBack;
        btnBack = findViewById(R.id.btnBack_id);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.refreshTodayTasks();
                MainActivity.refreshUpcomingTasks();
                onBackPressed();
            }
        });




    }


    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public String getUrgStr(int urgVal) {
        String urgStr = "Low";
        switch (taskModel.getUrgency()) {
            case 0:
                urgStr = "Low";
                break;
            case 1:
                urgStr = "Medium";
                break;
            case 2:
                urgStr = "High";
                break;
        }

        return urgStr;
    }
}
