package com.criminal.android.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import java.util.UUID;

public class CrimeActivity extends SingleFragmentActivity {

    private static final String EXTA_CRIME_ID = "com.criminal.android.criminalintent.crime_id";

    @Override
    public Fragment createFragment(){
        UUID crimeId = (UUID) getIntent().getSerializableExtra(EXTA_CRIME_ID);
        return CrimeFragment.newInstance(crimeId);
    }

    public static Intent newIntent(Context context, UUID criminalUUID){
        Intent intent = new Intent(context, CrimeActivity.class);
        intent.putExtra(EXTA_CRIME_ID, criminalUUID);
        return intent;
    }
}
