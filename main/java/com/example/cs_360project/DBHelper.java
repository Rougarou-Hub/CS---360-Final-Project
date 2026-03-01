package com.example.cs_360project;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "cs360.db";
    private static final int DB_VERSION = 1;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE users (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "username TEXT UNIQUE NOT NULL," +
                        "password TEXT NOT NULL" +
                        ")"
        );

        db.execSQL(
                "CREATE TABLE inventory (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "name TEXT NOT NULL," +
                        "qty INTEGER NOT NULL DEFAULT 0" +
                        ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS inventory");
        onCreate(db);
    }

    // ---------- LOGIN ----------
    public boolean createUser(String username, String password) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("username", username);
        cv.put("password", password);
        long res = db.insert("users", null, cv);
        return res != -1;
    }

    public boolean validateLogin(String username, String password) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT id FROM users WHERE username=? AND password=?",
                new String[]{username, password}
        );
        boolean ok = c.moveToFirst();
        c.close();
        return ok;
    }

    public boolean userExists(String username) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT id FROM users WHERE username=?",
                new String[]{username}
        );
        boolean exists = c.moveToFirst();
        c.close();
        return exists;
    }

    // ---------- INVENTORY CRUD ----------
    public long addItem(String name, int qty) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("name", name);
        cv.put("qty", qty);
        return db.insert("inventory", null, cv);
    }

    public int updateItem(long id, String name, int qty) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("name", name);
        cv.put("qty", qty);
        return db.update("inventory", cv, "id=?", new String[]{String.valueOf(id)});
    }

    public int deleteItem(long id) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete("inventory", "id=?", new String[]{String.valueOf(id)});
    }

    public Cursor getAllItems() {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery("SELECT id, name, qty FROM inventory ORDER BY name", null);
    }
}