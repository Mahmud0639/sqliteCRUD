package com.manuni.sqlitewithimage;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import androidx.annotation.Nullable;

public class SqliteHelper extends SQLiteOpenHelper {
    public SqliteHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    //creating table
    public void queryData(String sql){
        SQLiteDatabase database = getWritableDatabase();
        database.execSQL(sql);
    }

    //insert data
    public void insertData(String name, String age, String phone, byte[] image){
        SQLiteDatabase database = getWritableDatabase();

        String sql = "INSERT INTO RECORD VALUES(NULL,?,?,?,?)";

        SQLiteStatement statement = database.compileStatement(sql);
        statement.clearBindings();


        statement.bindString(1,name);
        statement.bindString(2,age);
        statement.bindString(3,phone);
        statement.bindBlob(4,image);

        statement.executeInsert();
    }

    //update data
    public void updateData(String name, String age, String phone, byte[] image, int id){

        SQLiteDatabase database = getWritableDatabase();

        String sql = "UPDATE RECORD SET name=?,age=?,phone=?,image=? WHERE id=?";
       SQLiteStatement statement = database.compileStatement(sql);

       statement.bindString(1,name);
       statement.bindString(2,age);
       statement.bindString(3,phone);
       statement.bindBlob(4,image);
       statement.bindDouble(5,(double) id);


       statement.execute();

       database.close();




    }

    //delete data
    public void deleteData(int id){
        SQLiteDatabase database = getWritableDatabase();
        String sql = "DELETE FROM RECORD WHERE id=?";
       SQLiteStatement statement = database.compileStatement(sql);
       statement.clearBindings();

       statement.bindDouble(1,(double) id);
       statement.execute();

       database.close();
    }

    public Cursor getData(String sql){

        SQLiteDatabase database = getReadableDatabase();
        return database.rawQuery(sql,null);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
