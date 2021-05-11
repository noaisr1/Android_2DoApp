

package com.example.taskslistfinalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.NavigableMap;
import java.util.Objects;

/**
 * MainActivity class for 2DoList app.
 * @author Noa Israeli
 */

public class MainActivity extends AppCompatActivity{
    private static final int ADD_NEW_TASK_RES = 1;
    static ListView lvTodayTasks;
    static ListView lvUpcomingTasks;
    public static MyTaskAdapter todayTasksAdapter,upcomingTasksAdapter;

    static TextView tvTodayEmpty;
    static TextView tvUpcomingEmpty;

    private static ArrayList<TaskModel> todayTaskArr;
    private static ArrayList<TaskModel> upcomingTaskArr;

    private static DatabaseHelper databaseHelper;
    private static Cursor myCursor;
    Button btnAdd;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavView_id);
        bottomNavigationView.setSelectedItemId(R.id.homeFragment);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.doneFragment:
                        startActivity(new Intent(getApplicationContext(),DoneTasks.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.homeFragment:
                       return true;
                }
                return false;
            }
        });

        // Initializing components
        databaseHelper = new DatabaseHelper(this);

        btnAdd = findViewById(R.id.btnAdd_id);

        lvTodayTasks = findViewById(R.id.lvTodayTasks_id);
        lvUpcomingTasks = findViewById(R.id.lvUpcomingTasks_id);

        todayTaskArr = new ArrayList<>();
        upcomingTaskArr = new ArrayList<>();

        tvTodayEmpty = findViewById(R.id.tvEmptyTodayTasks_id);
        tvUpcomingEmpty = findViewById(R.id.tvEmptyUpcomingTasks_id);

        // Updates lists
        refreshTodayTasks();
        refreshUpcomingTasks();

        // Set ListViews
        todayTasksAdapter = new MyTaskAdapter(this,R.layout.task_layout,todayTaskArr);
        lvTodayTasks.setAdapter(todayTasksAdapter);
        upcomingTasksAdapter = new MyTaskAdapter(this,R.layout.task_layout,upcomingTaskArr);
        lvUpcomingTasks.setAdapter(upcomingTasksAdapter);

        // Presents ListViews
        setListView(lvTodayTasks);
        setListView(lvUpcomingTasks);

        lvTodayTasks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Go to delete and update task

            }
        });
        lvUpcomingTasks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Go to delete and update task
            }
        });

        // Adds new task
        btnAdd.setOnClickListener(v -> {
            Intent i = new Intent (MainActivity.this,AddNewTask.class);
            startActivity(i);

            refreshTodayTasks();
            refreshUpcomingTasks();
            onBackPressed();

        });

    }

    /**
     * refreshUpcomingTasks refreshes the list of tasks that their due date is after today.
     * gets Cursor from databaseHelper.getTodayTasks() id there are any tasks and
     * sets an empty list if there aren't any tasks.
     * @return true if succeeded and false else
     */
    static void refreshUpcomingTasks() {
        upcomingTaskArr.clear();
        myCursor = databaseHelper.getUpcomingTasks();


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

                TaskModel newTask = new TaskModel(id,title,desc,cal,urg,isDone);
                upcomingTaskArr.add(newTask);
            }while(myCursor.moveToNext());
            tvUpcomingEmpty.setVisibility(View.GONE);

        }else{
            // No records. Present an empty list.
            tvUpcomingEmpty.setVisibility(View.VISIBLE);
        }

        lvUpcomingTasks.invalidateViews();
        setListView(lvUpcomingTasks);


    }


    /**
     * refreshTodayTasks refreshes the list of tasks that their due date is for today.
     * gets Cursor from databaseHelper.getTodayTasks() id there are any tasks and
     * sets an empty list if there aren't any tasks.
     * @return true if succeeded and false else
     */
    public static void refreshTodayTasks(){
        todayTaskArr.clear();
        myCursor = databaseHelper.getTodayTasks();

        if(myCursor.moveToFirst()){   // Loop through the cursor (result set) and create new task obj.
            // Put them into the return list.
            do{
                int id = myCursor.getInt(0);
                String title = myCursor.getString(1);
                String desc = myCursor.getString(2);
                String date = myCursor.getString(3);
                int urg = myCursor.getInt(4);
                //boolean isDone = myCursor.getInt(5) == 1; // Converting int to boolean

                // Converting String to Calendar object
                Calendar cal = Calendar.getInstance(Locale.getDefault());
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                try {
                    cal.setTime(sdf.parse(date));// all done
                } catch (ParseException pe) {
                    pe.printStackTrace();
                }

                TaskModel newTask = new TaskModel(id,title,desc,cal,urg,false);
                todayTaskArr.add(newTask);
            }while(myCursor.moveToNext());
            tvTodayEmpty.setVisibility(View.GONE);


        }else{
            // No records. Present an empty list.
            tvTodayEmpty.setVisibility(View.VISIBLE);
        }
        lvTodayTasks.invalidateViews();
        setListView(lvTodayTasks);

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

