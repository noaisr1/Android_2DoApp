package com.example.taskslistfinalproject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class DoneTasks extends AppCompatActivity {

    static ListView lvDoneTasks;
    public static MyTaskAdapter doneTasksAdapter;

    static TextView tvDoneEmpty;

    private static ArrayList<TaskModel> doneTaskArr;

    private static DatabaseHelper databaseHelper;
    private static Cursor myCursor;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_done_tasks);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavView_id);
        bottomNavigationView.setSelectedItemId(R.id.doneFragment);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.homeFragment:
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        overridePendingTransition(0, 0);
                    case R.id.doneFragment:
                        refreshDoneTasks();
                        return true;
                }
                return false;
            }
        });

        databaseHelper = new DatabaseHelper(this);

        lvDoneTasks = findViewById(R.id.lvDoneTasks_id);

        doneTaskArr = new ArrayList<>();

        tvDoneEmpty = findViewById(R.id.tvEmptyDoneTasks_id);

        // Updates lists
        refreshDoneTasks();

        // Set ListViews
        doneTasksAdapter = new MyTaskAdapter(this,R.layout.task_layout,doneTaskArr);
        lvDoneTasks.setAdapter(doneTasksAdapter);

        // Presents ListViews
        setListView(lvDoneTasks);

        lvDoneTasks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Go to delete and update task

            }
        });





    }

        public static void refreshDoneTasks(){
            doneTaskArr.clear();
            myCursor = databaseHelper.getDoneTasks();

            if(myCursor.moveToFirst()){   // Loop through the cursor (result set) and create new task obj.
                // Put them into the return list.
                do{
                    int id = myCursor.getInt(0);
                    String title = myCursor.getString(1);
                    String desc = myCursor.getString(2);
                    String date = myCursor.getString(3);
                    int urg = myCursor.getInt(4);
                    boolean isDone = myCursor.getInt(5) == 1; // Converting int to boolean

                    // Converting String to Calendar object
                    Calendar cal = Calendar.getInstance(Locale.getDefault());
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    try {
                        cal.setTime(sdf.parse(date));// all done
                    } catch (ParseException pe) {
                        pe.printStackTrace();
                    }

                    TaskModel newTask = new TaskModel(id,title,desc,cal,urg,false);
                    doneTaskArr.add(newTask);
                }while(myCursor.moveToNext());
                tvDoneEmpty.setVisibility(View.GONE);


            }else{
                // No records. Present an empty list.
                tvDoneEmpty.setVisibility(View.VISIBLE);
            }
            lvDoneTasks.invalidateViews();
            setListView(lvDoneTasks);

        }

        public static void setListView(ListView listView){
            int height = 0,
                    width = View.MeasureSpec.makeMeasureSpec(listView.getWidth(),View.MeasureSpec.UNSPECIFIED);
            View tmpView = null;
            ListAdapter la = listView.getAdapter();
            if(la == null) return;

            for( int i=0 ; i < la.getCount(); i++){
                tmpView = la.getView(i,tmpView,listView);

                if(i==0){
                    tmpView.setLayoutParams(new ViewGroup.LayoutParams(width,ViewGroup.LayoutParams.MATCH_PARENT));
                }
                tmpView.measure(width,View.MeasureSpec.UNSPECIFIED);
                height += tmpView.getMeasuredHeight();
            }

            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = height + ((listView.getDividerHeight()) * la.getCount());

            listView.setLayoutParams(params);
            listView.requestLayout();


        }

    }
