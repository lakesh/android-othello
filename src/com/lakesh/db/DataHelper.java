package com.lakesh.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.lakesh.util.DateUtils;

import java.util.ArrayList;
import java.util.List;

public class DataHelper {

   private static final String DATABASE_NAME = "Othello.db";
   private static final int DATABASE_VERSION = 2;
   private static final String TABLE_NAME = "score_board";

   private Context context;
   private SQLiteDatabase db;

   private SQLiteStatement insertStmt;
   private static final String INSERT = "insert into " 
      + TABLE_NAME + "(name,black_score,white_score, date) values (?,?,?,?)";

   public DataHelper(Context context) {
      this.context = context;
      OpenHelper openHelper = new OpenHelper(this.context);
      this.db = openHelper.getWritableDatabase();
      this.insertStmt = this.db.compileStatement(INSERT);
   }

   public long insert(String name, long black_score, long white_score) {
      this.insertStmt.bindString(1, name);
      this.insertStmt.bindLong(2, black_score);
      this.insertStmt.bindLong(3, white_score);
      this.insertStmt.bindString(4, DateUtils.now("yyyy-MM-dd HH:mm:ss"));
      Log.i("debug", DateUtils.now("yyyy-MM-dd HH:mm:ss"));
      return this.insertStmt.executeInsert();
   }

   public void deleteAll() {
      this.db.delete(TABLE_NAME, null, null);
   }

   public List<List> selectAll() {
      List<List> list = new ArrayList<List>();
      Cursor cursor = this.db.query(TABLE_NAME, new String[] { "name", "black_score", "white_score" }, 
        null, null, null, null, "white_score desc");
      if (cursor.moveToFirst()) {
         do {
        	 List<String> record = new ArrayList<String>();
        	 record.add(cursor.getString(0));
        	 record.add(String.valueOf(cursor.getLong(1)));
        	 record.add(String.valueOf(cursor.getLong(2)));
        	 list.add(record);             
         } while (cursor.moveToNext());
      }
      if (cursor != null && !cursor.isClosed()) {
         cursor.close();
      }
      return list;
   }

   private static class OpenHelper extends SQLiteOpenHelper {

      OpenHelper(Context context) {
         super(context, DATABASE_NAME, null, DATABASE_VERSION);
      }

      @Override
      public void onCreate(SQLiteDatabase db) {
    	  Log.i("debug","Creating the database");
         db.execSQL("CREATE TABLE " + TABLE_NAME + "(id INTEGER PRIMARY KEY, name TEXT, black_score INTEGER, white_score INTEGER, date DATETIME)");
      }

      @Override
      public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
         Log.w("Example", "Upgrading database, this will drop tables and recreate.");
         db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
         onCreate(db);
      }
   }
}
