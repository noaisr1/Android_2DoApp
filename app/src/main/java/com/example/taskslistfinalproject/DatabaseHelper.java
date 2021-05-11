package com.example.taskslistfinalproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


/**
 * DatabaseHelper class to apply the actions on SQLite database.
 *
 * @author Noa Israeli
 */


public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String TABLE_NAME = "Tasks";

    // Columns:
    public static final String COL1_ID = "ID";
    public static final String COL2_TITLE = "TITLE";
    public static final String COL3_DESC = "DESC";
    public static final String COL4_DUE_DATE = "DUE_DATE";
    public static final String COL5_URG = "URG";
    public static final String COL6_IS_DONE = "IS_DONE";


    public DatabaseHelper(@Nullable Context context) {
        super(context, "tasks.db", null, 1);
    }

    /**
     * onCreate is automatically called when the app requests or inputs new data.
     * We need to create a new table inside this method.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableStatement = // The String defines a SQL statement to create a new table.
                "CREATE TABLE " + TABLE_NAME + // Table name
                        "(" + COL1_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + COL2_TITLE + " TEXT, "
                        + COL3_DESC + " TEXT, "
                        + COL4_DUE_DATE + " date(10), "
                        + COL5_URG + " INT, "
                        + COL6_IS_DONE + " BOOL)";

        db.execSQL(createTableStatement);
    }

    /**
     * onUpgrade is called if the database version number changes.
     * It prevents previous users apps from breaking when you change the database design
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS" + TABLE_NAME);
        onCreate(db);
    }

    /**
     * addOne adds a record to database
     * @param taskModel record to add
     * @return true if succeeded and false else
     */
    public boolean addOne(TaskModel taskModel){
        SQLiteDatabase db = this.getWritableDatabase(); // For insert action
        ContentValues cv = new ContentValues(); // ContentValues stores data in pairs like a HashMap or  an AssociativeArray
                                                // Also similar to Put Extras in an Intent.

        // It is not necessary to provide id because its auto incrementing
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String dateStr = sdf.format(taskModel.getDate().getTime());


        cv.put(COL2_TITLE,taskModel.getName());
        cv.put(COL3_DESC,taskModel.getDesc());
        cv.put(COL4_DUE_DATE,dateStr);
        cv.put(COL5_URG,taskModel.getUrgency());
        cv.put(COL6_IS_DONE,taskModel.isDone());

        long insert = db.insert(TABLE_NAME, null, cv);
        return insert != -1;
    }


    /**
     * getTodayTasks is used get all records from database that their due date is for today.
     * Fill it from the database query.
     * @return Cursor object.
     */
    public Cursor getTodayTasks()  {

        // Get data from the database
        Calendar c = Calendar.getInstance(Locale.getDefault());

        SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String todayDate = sdfDate.format(c.getTime());
        String queryStr = "SELECT * FROM " + TABLE_NAME +" WHERE IS_DONE == '" + 0 + "' AND " + " DUE_DATE == '" + todayDate + "' ORDER BY ID DESC ";

        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery(queryStr,null);
    }


    /**
     * getTodayTasks is used get all records from database that their due date is for after today.
     * Fill it from the database query.
     * @return Cursor object.
     */
    public Cursor getUpcomingTasks(){
        Calendar c = Calendar.getInstance(Locale.getDefault());
        SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String todayDate = sdfDate.format(c.getTime());

        String queryStr = "SELECT * FROM " + TABLE_NAME + " WHERE DUE_DATE >'" + todayDate + "' ORDER BY ID DESC ";

        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery(queryStr,null);
    }

    /**
     * isDoneCheck is used to put a switch value to the isDone Switch object.
     * @param id of a task to handle
     * @param isDone boolean value to change
     */
    public void isDoneCheck(String id, int isDone){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL6_IS_DONE,isDone);
        db.update(TABLE_NAME, cv, "ID = ?", new String[]{id});

    }

    /**
     * deleteOne is used to delete a given record.
     * Find taskModel in the database. If it found, delete it and return true.
     * If it is not found, return false.
     * @param taskModel of TaskModel object to delete.
     * @return true if succeeded and false if not.
     */
    public boolean deleteOne(TaskModel taskModel){
        SQLiteDatabase db = getWritableDatabase();

        String queryStr = " DELETE FROM " + TABLE_NAME + " WHERE " + COL1_ID + " = " + taskModel.getId();
        Cursor cursor = db.rawQuery(queryStr,null);
        return cursor.moveToFirst();
    }

    public boolean updateData(TaskModel taskModel){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String dateStr = sdf.format(taskModel.getDate().getTime());

        cv.put(COL2_TITLE,taskModel.getName());
        cv.put(COL3_DESC,taskModel.getDesc());
        cv.put(COL4_DUE_DATE,dateStr);
        cv.put(COL5_URG,taskModel.getUrgency());
        cv.put(COL6_IS_DONE,taskModel.isDone());

        String id = String.valueOf(taskModel.getId());

        db.update(TABLE_NAME, cv, "ID = ?", new String[]{id});
        return true;

    }

    public Cursor getDoneTasks() {
        String queryStr = "SELECT * FROM " + TABLE_NAME + " WHERE IS_DONE ='" + 1 + "' ORDER BY ID DESC ";
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(queryStr,null);
    }
}
