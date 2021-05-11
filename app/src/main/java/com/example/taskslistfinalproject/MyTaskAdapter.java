package com.example.taskslistfinalproject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;


/**
 * MyTaskAdapter class for 2DoList app.
 * adapts the list of tasks to the ListView given.
 *
 * @author Noa Israeli
 */

public class MyTaskAdapter extends ArrayAdapter<TaskModel> {
    DatabaseHelper databaseHelper;
    private final Context context;
    private final int resource;



    public MyTaskAdapter(@NonNull Context context, int resource, @NonNull ArrayList<TaskModel> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        databaseHelper = new DatabaseHelper(context);
    }



    /**
     * getView adds a record to database
     * @param position position of task in an Arraylist.
     * @param convertView the layout of the ListView.
     * @param parent parent of View.
     * @return true if succeeded and false else
     */

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        convertView = inflater.inflate(this.resource,parent,false);

        TextView tvTitle, tvUrgency,tvDueDate;
        SwitchCompat swDone;
        ImageButton btnUpdate;
        Button btnDelete;

        tvTitle = convertView.findViewById(R.id.TaskTitleTV_id);
        tvUrgency = convertView.findViewById(R.id.tvUrgVal_id);
        tvDueDate = convertView.findViewById(R.id.tvDueDate_id);
        btnUpdate = convertView.findViewById(R.id.btnUpdate_id);
        btnDelete = convertView.findViewById(R.id.btnDelete_id);
        swDone = convertView.findViewById(R.id.swDone_id);

        tvTitle.setText(getItem(position).getName());
        switch(getItem(position).getUrgency()){
            case 0:
                tvUrgency.setText("Low");
                tvUrgency.setTextColor(context.getResources().getColor(R.color.green));
                break;
            case 1:
                tvUrgency.setText("Medium");
                tvUrgency.setTextColor(context.getResources().getColor(R.color.yellow_orange));
                break;
            case 2:
                tvUrgency.setText("High");
                tvUrgency.setTextColor(context.getResources().getColor(R.color.red));
                break;
        }

        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.add(Calendar.DATE,1);
        SimpleDateFormat format1 = new SimpleDateFormat("dd/MM/yy");
        String dateStr = format1.format(getItem(position).getDate().getTime());
        tvDueDate.setText(dateStr);

        swDone.setOnClickListener(v -> {
            databaseHelper.isDoneCheck(getItem(position).getId()+"", getItem(position).intIsDone());
            MainActivity.refreshTodayTasks();
            MainActivity.refreshUpcomingTasks();
        });

        // If task is already done
        if(getItem(position).isDone()) swDone.setChecked(true);


        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToUpdateActivity(getItem(position));
                MainActivity.refreshTodayTasks();
                MainActivity.refreshUpcomingTasks();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context).setTitle("Delete task \""+ tvTitle.getText().toString()+ "\"")
                        .setMessage("Are you sure you want to delete this task?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                databaseHelper.deleteOne(getItem(position));
                                Toast.makeText(context,"Task deleted",Toast.LENGTH_LONG).show();
                                MainActivity.refreshTodayTasks();
                                MainActivity.refreshUpcomingTasks();
                            }
                        }).setNegativeButton("No",null).show();
            }
        });


        return convertView;
    }

    public void moveToUpdateActivity(TaskModel taskModel){

        Gson gson = new Gson();
        String target = gson.toJson(taskModel);
        Intent i = new Intent(context,UpdateTask.class);
        i.putExtra("selected_task",target);
        context.startActivity(i);
    }


}
