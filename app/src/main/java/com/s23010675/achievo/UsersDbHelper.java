package com.s23010675.achievo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class UsersDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "achievo.db";
    private static final int DATABASE_VERSION = 1;

    //user table
    private static final String TABLE_USERS = "users";
    private static final String ID = "id";
    private static final String USERNAME = "username";
    private static final String EMAIL = "email";
    private static final String PASSWORD = "password";
    private static final String PROFILE_PIC = "profile_pic";


    public UsersDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        //user database
        String createTable = "CREATE TABLE " + TABLE_USERS + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + USERNAME + " TEXT," + EMAIL + " TEXT UNIQUE," + PASSWORD + " TEXT," + PROFILE_PIC + " TEXT" + ")";

        db.execSQL(createTable);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop old and recreate for now
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    //signup method
    public boolean signup(String username, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();

        //check email address exist
        Cursor cursor = db.query(TABLE_USERS, null, EMAIL + "=?", new String[]{email}, null, null, null);
        if (cursor.moveToFirst()) {
            cursor.close();
            return false;
        }
        cursor.close();

        ContentValues cv = new ContentValues();
        cv.put(USERNAME, username);
        cv.put(EMAIL, email);
        cv.put(PASSWORD, password);

        long result = db.insert(TABLE_USERS, null, cv);
        return result != -1;
    }

    //login method
    public String login(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_USERS, new String[]{USERNAME},
                EMAIL + "=? AND " + PASSWORD + "=?", new String[]{email, password}, null, null, null);

        if (cursor.moveToFirst()) {
            String username = cursor.getString(cursor.getColumnIndexOrThrow(USERNAME));
            cursor.close();
            return username;
        }
        cursor.close();
        return null;
    }

    //check email exist method for reset password
    public boolean checkEmailExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE email = ?", new String[]{email});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    //get user profile data
    public User getUserProfile(String email) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_USERS,
                new String[]{USERNAME, PROFILE_PIC},
                EMAIL + "=?",
                new String[]{email},
                null, null, null);

        if (cursor.moveToFirst()) {
            String username = cursor.getString(cursor.getColumnIndexOrThrow(USERNAME));
            String picUri = cursor.getString(cursor.getColumnIndexOrThrow(PROFILE_PIC));
            cursor.close();
            return new User(username, email, picUri);
        }
        cursor.close();
        return null;
    }

    //update profile picture
    public boolean updateProfilePic(String email, String picUri) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(PROFILE_PIC, picUri);

        int updated = db.update(TABLE_USERS, cv, EMAIL + "=?", new String[]{email});
        return updated > 0;
    }

    //update password method
    public boolean updatePassword(String email, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("password", newPassword);
        int rows = db.update("users", values, "email=?", new String[]{email});
        return rows > 0;
    }


    //user class hold data
    public static class User {
        public String username, email, profilePicUri;

        public User(String username, String email, String profilePicUri) {
            this.username = username;
            this.email = email;
            this.profilePicUri = profilePicUri;
        }
    }
}

