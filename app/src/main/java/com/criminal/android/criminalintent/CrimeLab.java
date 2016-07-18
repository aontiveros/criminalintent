package com.criminal.android.criminalintent;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

/**
 * Singleton CrimeLab, holds all available crimes and can retrieve by UUID of a crime
 * Created by anton on 7/16/2016.
 */
public class CrimeLab {

    private static CrimeLab sCrimeLab;
    private List<Crime> mCrimes;

    /**
     * Retrieves a crimelab singleton from the respective context, otherwise creates one
     * @param context
     * @return CrimeLab singleton
     */
    public static CrimeLab get(Context context){
        if(sCrimeLab == null){
            sCrimeLab = new CrimeLab();
        }
        return sCrimeLab;
    }

    private CrimeLab(){
        mCrimes = new ArrayList<>();
        for(int i = 0; i < 100; i++){
            Crime crime = new Crime();
            crime.setTitle("Crime #" + i);
            crime.setSolved(i % 2 == 0);
            mCrimes.add(crime);
        }
    }

    /**
     * Get the list of crimes contained in the crime lab
     * @return List of crimes
     */
    public List<Crime> getCrimes(){
        return mCrimes;
    }

    /**
     * Get a specific crime based on its UUID
     * @param id
     * @return Crime based on UUID or Null
     */
    public Crime getCrimeById(UUID id){
        for(Crime c : mCrimes){
            if(c.getId().equals(id))
                return c;
        }
        return null;
    }

    /**
     * Gets the index of a specific id
     * @param id
     * @return
     */
    public int getCrimeIndex(UUID id){
        for(int i = 0; i < mCrimes.size(); i++){
            if(mCrimes.get(i).getId().equals(id)){
                return i;
            }
        }
        throw new NoSuchElementException();
    }
}
