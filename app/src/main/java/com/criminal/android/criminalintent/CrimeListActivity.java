package com.criminal.android.criminalintent;

import android.content.Intent;
import android.support.v4.app.Fragment;

public class CrimeListActivity extends SingleFragmentActivity
        implements  CrimeListFragment.Callbacks, CrimeFragment.Callbacks {

    @Override
    public Fragment createFragment(){
        return new CrimeListFragment();
    }

    @Override
    protected int getLayoutRes(){
        return R.layout.activity_masterdetail;
    }

    @Override
    public void onCrimeUpdate(Crime crime){
        CrimeListFragment crimeListFragment = (CrimeListFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_container);
        if(crimeListFragment != null)
            crimeListFragment.updateUI();
    }

    @Override
    public void onCrimeSelected(Crime crime){
        if(findViewById(R.id.detail_fragment_container) == null){
            Intent intent = CrimePagerActivity.newIntent(this, crime.getId());
            startActivity(intent);
        }
        else{
            Fragment newDetail = CrimeFragment.newInstance(crime.getId());

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_fragment_container, newDetail)
                    .commit();
        }
    }
}
