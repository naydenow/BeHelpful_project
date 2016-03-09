package biz.coddo.behelpful;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import biz.coddo.behelpful.DTO.ResponseDTO;

public class DBConnector {

    private static final String TAG="DBConnector";
    private static final String DATABASE_NAME = "behelpful.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME_RESPONSE = "Response";
    private static final String RESPONSE_COLUMN_ID = "_id";
    private static final String RESPONSE_COLUMN_USER_NAME = "UserName";
    private static final String RESPONSE_COLUMN_USER_PHONE = "UserPhone";
    private static final String RESPONSE_COLUMN_DATE = "Date";

    private static final int NUMBER_COLUMN_ID = 0;
    private static final int NUMBER_COLUMN_USER_NAME = 1;
    private static final int NUMBER_COLUMN_USER_PHONE = 2;
    private static final int NUMBER_COLUMN_DATE = 3;


    private SQLiteDatabase mDataBase;

    public DBConnector(Context context){
        OpenHelper mOpenHelper = new OpenHelper(context);
        mDataBase = mOpenHelper.getWritableDatabase();
    }

    public long putResponse(ResponseDTO mRespond) {
        Log.i(TAG, "putResponse");
        ContentValues cv = new ContentValues();
        Log.i(TAG, "PutResponse: " + mRespond.getUserName() + ", " + mRespond.getUserPhone() + ", " + mRespond.getDate());
        cv.put(RESPONSE_COLUMN_USER_NAME, mRespond.getUserName());
        cv.put(RESPONSE_COLUMN_USER_PHONE, mRespond.getUserPhone());
        cv.put(RESPONSE_COLUMN_DATE, mRespond.getDate());
        long i = mDataBase.insert(TABLE_NAME_RESPONSE, null, cv);
        Log.i(TAG, "response_id = " + i);
        return i;
    }

    // Метод редактирования строки в БД
    /*public int updateResponse(int id) {
        ContentValues cv=new ContentValues();
        cv.put(TMARKER_COLUMN_RESPOND, true);
        return mDataBase.update(TABLE_NAME_MARKER, cv, TMARKER_COLUMN_USER_ID + " = ?",
                new String[] {String.valueOf(id) });
    }*/

    // Метод удаления всех записей из БД
    public int deleteAllResponses() {
        return mDataBase.delete(TABLE_NAME_RESPONSE, null, null);
    }

    // Метод удаления записи
    public void deleteResponse(int id) {
        mDataBase.delete(TABLE_NAME_RESPONSE, RESPONSE_COLUMN_ID + " = ?", new String[] { String.valueOf(id) });
    }

    // Метод выборки одной записи
    public ResponseDTO selectResponse(long id) {
        Cursor mCursor = mDataBase.query(TABLE_NAME_RESPONSE, null, RESPONSE_COLUMN_ID + " = ?", new String[]{String.valueOf(id)}, null, null, RESPONSE_COLUMN_DATE);

        mCursor.moveToFirst();
        String userName = mCursor.getString(mCursor.getColumnIndex(RESPONSE_COLUMN_USER_NAME));
        String userPhone = mCursor.getString(mCursor.getColumnIndex(RESPONSE_COLUMN_USER_PHONE));
        String date = mCursor.getString(mCursor.getColumnIndex(RESPONSE_COLUMN_DATE));
        mCursor.close();
        return new ResponseDTO(userName, userPhone, date);
    }

    // Метод выборки всех записей
    public ArrayList<ResponseDTO> getAllResponses() {
        Log.i(TAG, "getAllResponse");
        Cursor mCursor = mDataBase.query(TABLE_NAME_RESPONSE, null, null, null, null, null, null);
        Log.i(TAG, "mCursor = " + mCursor.toString());
        ArrayList<ResponseDTO> arr = new ArrayList<ResponseDTO>();
        Log.i(TAG, "getCount " + mCursor.getCount());
        if (mCursor.moveToLast()) {
            Log.i(TAG, "moveToFirst = true");
            do {
                String userName = mCursor.getString(NUMBER_COLUMN_USER_NAME);
                String userPhone = mCursor.getString(NUMBER_COLUMN_USER_PHONE);
                String date = mCursor.getString(NUMBER_COLUMN_DATE);
                ResponseDTO response = new ResponseDTO(userName, userPhone, date);
                response.setDbID(mCursor.getInt(NUMBER_COLUMN_ID));
                arr.add(response);
                Log.i(TAG, "UserName " + response.getUserName() + " UserPhone " +response.getUserPhone());
            } while (mCursor.moveToPrevious());
        }
        mCursor.close();
        return arr;
    }

    class OpenHelper extends SQLiteOpenHelper {

        public OpenHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            String respondTableCreate = "CREATE TABLE " + TABLE_NAME_RESPONSE + " (" +
                    RESPONSE_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    RESPONSE_COLUMN_USER_NAME + " TEXT, " +
                    RESPONSE_COLUMN_USER_PHONE + " TEXT, " +
                    RESPONSE_COLUMN_DATE + " TEXT);";
            db.execSQL(respondTableCreate);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            //TODO Write smth here when it need
        }
    }
}
