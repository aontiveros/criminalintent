package com.criminal.android.criminalintent;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.criminal.android.criminalintent.database.CrimeBaseHelper;
import com.criminal.android.criminalintent.database.CrimeCursorWrapper;
import com.criminal.android.criminalintent.database.CrimeDbSchema;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Singleton CrimeLab, holds all available crimes and can retrieve by UUID of a crime
 * Created by anton on 7/16/2016.
 */
public class CrimeLab {

    private static CrimeLab sCrimeLab;
    private Context mContext;
    private SQLiteDatabase mSQLiteDatabase;

    /**
     * Retrieves a crimelab singleton from the respective context, otherwise creates one
     * @param context
     * @return CrimeLab singleton
     */
    public static CrimeLab get(Context context){
        if(sCrimeLab == null){
            sCrimeLab = new CrimeLab(context);
        }
        return sCrimeLab;
    }

    private CrimeLab(Context context){
        mContext = context.getApplicationContext();
        mSQLiteDatabase = new CrimeBaseHelper(mContext).getWritableDatabase();

    }

    /**
     * Get the list of crimes contained in the crime lab
     * @return List of crimes
     */
    public List<Crime> getCrimes(){
        List<Crime> crimeList = new ArrayList<>();
        CrimeCursorWrapper cursor = queryCrimes(null, null);
        try{
            cursor.moveToFirst();
            while(!cursor.isAfterLast()){
                crimeList.add(cursor.getCrime());
                cursor.moveToNext();
            }
        }
        finally{
            cursor.close();
        }
        return crimeList;
    }

    /**
     * Get a specific crime based on its UUID
     * @param id
     * @return Crime based on UUID or Null
     */
    public Crime getCrimeById(UUID id){
        CrimeCursorWrapper cursor = queryCrimes(CrimeDbSchema.CrimeTable.Cols.UUID + " = ?", new String[]{id.toString()});
        try{
            if(cursor.getCount() == 0)
                return null;
            cursor.moveToFirst();
            return cursor.getCrime();
        }
        finally {
            cursor.close();
        }
    }

    public void removeCrimeByUUID(UUID crime){
        mSQLiteDatabase.delete(CrimeDbSchema.CrimeTable.NAME,
                CrimeDbSchema.CrimeTable.Cols.UUID + " = ?",
                new String[]{crime.toString()});
    }

    public void addCrime(Crime newCrime){
        ContentValues values = getContentValues(newCrime);
        mSQLiteDatabase.insert(CrimeDbSchema.CrimeTable.NAME, null, values);
    }

    public void updateCrime(Crime crime){
        String uuidString = crime.getId().toString();
        ContentValues values = getContentValues(crime);

        mSQLiteDatabase.update(CrimeDbSchema.CrimeTable.NAME, values,
                CrimeDbSchema.CrimeTable.Cols.UUID + " = ?",
                new String[]{uuidString});
    }


    private static ContentValues getContentValues(Crime crime){
        ContentValues values = new ContentValues();
        values.put(CrimeDbSchema.CrimeTable.Cols.UUID, crime.getId().toString());
        values.put(CrimeDbSchema.CrimeTable.Cols.TITLE, crime.getTitle());
        values.put(CrimeDbSchema.CrimeTable.Cols.DATE, crime.getDate().getTime());
        values.put(CrimeDbSchema.CrimeTable.Cols.SOLVED, crime.isSolved() ? 1 : 0);
        values.put(CrimeDbSchema.CrimeTable.Cols.SUSPECT, crime.getSuspect());

        return values;
    }

    private CrimeCursorWrapper queryCrimes(String whereClause, String[] whereArgs){
        Cursor cursor = mSQLiteDatabase.query(CrimeDbSchema.CrimeTable.NAME, //table name
                null, // columns - null means all columns
                whereClause, //where clause
                whereArgs, //where args
                null,  //groupBy
                null, //Having
                null); //orderBy
        return new CrimeCursorWrapper(cursor);
    }

    public File getPhotoFile(Crime crime){
        //get the directory path to the user's photo location
        File externalFilesDir = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        //if it doesn't exist we can't really do anything.
        if(externalFilesDir == null){
            return null;
        }
        return new File(externalFilesDir, crime.getPhotoFilename());
    }

}
