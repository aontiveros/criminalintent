package com.criminal.android.criminalintent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.UUID;

/**
 * Created by anton on 7/16/2016.
 */
public class CrimeListFragment extends Fragment{

    private RecyclerView mCrimeRecyclerView;
    private CrimeAdapter mCrimeAdapter;

    private static final int REQUEST_CRIME = 1;


    @Override
    public View onCreateView(LayoutInflater inflator, ViewGroup viewGroup, Bundle savedInstanceState){
        View v = inflator.inflate(R.layout.fragment_crime_list, viewGroup, false);
        mCrimeRecyclerView = (RecyclerView) v.findViewById(R.id.crime_recycle_viewer);

        mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        updateUI();

        return v;
    }

    @Override
    public void onResume(){
        super.onResume();
        updateUI();
    }

    /**
     * Create the adapter, give it the list of crimes, and assign it to the recycle view to work with.
     */
    private void updateUI(){
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        List<Crime> crimes = crimeLab.getCrimes();

        if(mCrimeAdapter == null) {
            mCrimeAdapter = new CrimeAdapter(crimes);
            mCrimeRecyclerView.setAdapter(mCrimeAdapter);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == CrimeListFragment.REQUEST_CRIME && data != null){
            UUID changedCrime = CrimeFragment.getChangedCrimeForIntent(data);
            if(changedCrime != null)
                mCrimeAdapter.notifyItemChanged(CrimeLab.get(getActivity()).getCrimeIndex(changedCrime));
        }
    }

    /**
     * Crime Holder class that holds the views for a respective crime in the RecycleView
     */
    private class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private Crime mCrime;

        private TextView mTitleTextView;
        private CheckBox mSolvedCheckBox;
        private TextView mDateTextBox;

        /**
         * Create a CrimeHolder with the expected view
         * @param itemView
         */
        public CrimeHolder(View itemView){
            super(itemView);
            itemView.setOnClickListener(this);
            mTitleTextView = (TextView) itemView.findViewById(R.id.list_item_crime_title_text_view);
            mSolvedCheckBox = (CheckBox) itemView.findViewById(R.id.list_item_crime_solved_check_box);
            mDateTextBox = (TextView) itemView.findViewById(R.id.list_item_crime_date_text_view);

        }

        /**
         * Assign a Holder a crime to display
         * @param crime
         */
        public void bindCrime(Crime crime){
            mCrime = crime;
            mTitleTextView.setText(mCrime.getTitle());
            mDateTextBox.setText(mCrime.getDate().toString());
            mSolvedCheckBox.setChecked(mCrime.isSolved());
            mSolvedCheckBox.setEnabled(false);
        }

        @Override
        public void onClick(View v){
            startActivityForResult(CrimePagerActivity.newIntent(getActivity(), mCrime.getId()), CrimeListFragment.REQUEST_CRIME);
        }
    }

    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder> {
        private List<Crime> mCrimes;

        public CrimeAdapter(List<Crime> crimes){
            mCrimes = crimes;
        }

        /**
         * Get the amount of crimes held by the adapter
         * @return
         */
        @Override
        public int getItemCount(){
            return mCrimes.size();
        }

        /**
         * Create a holder based on a request from the Recycle Viewer
         * @param parent
         * @param viewType
         * @return
         */
        @Override
        public CrimeHolder onCreateViewHolder(ViewGroup parent, int viewType){
            LayoutInflater inflator = LayoutInflater.from(getActivity());
            View view = inflator.inflate(R.layout.list_item_crime, parent, false);
            return new CrimeHolder(view);
        }

        /**
         * Assign a crime to a holder when called on.
         * @param holder
         * @param position
         */
        @Override
        public void onBindViewHolder(CrimeHolder holder, int position){
            Crime crime = mCrimes.get(position);
            holder.bindCrime(crime);
        }
    }
}
