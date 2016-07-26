package com.criminal.android.criminalintent;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;
import java.util.UUID;

/**
 * Created by anton on 7/16/2016.
 */
public class CrimeListFragment extends Fragment{

    private RecyclerView mCrimeRecyclerView;
    private CrimeAdapter mCrimeAdapter;
    private TextView mNoCrimeTextView;

    private boolean mSubtitleVisibility;

    private static final int REQUEST_CRIME = 1;
    private static final String SAVED_SUBTITLE = "subtitle";



    @Override
    public View onCreateView(LayoutInflater inflator, ViewGroup viewGroup, Bundle savedInstanceState){
        View v = inflator.inflate(R.layout.fragment_crime_list, viewGroup, false);
        mCrimeRecyclerView = (RecyclerView) v.findViewById(R.id.crime_recycle_viewer);

        mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mNoCrimeTextView = (TextView) v.findViewById(R.id.no_items_view);
        mNoCrimeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Crime crime = new Crime();
                CrimeLab.get(getActivity()).addCrime(crime);
                Intent intent = new CrimePagerActivity().newIntent(getActivity(), crime.getId());
                startActivity(intent);
            }
        });

        updateUI();

        return v;
    }

    @Override
    public void onResume(){
        super.onResume();
        updateUI();
    }

    /**
     * Lets the system know that this fragment has menu options
     * @param savedInstanceState The state for the fragment
     */
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null){
            mSubtitleVisibility = savedInstanceState.getBoolean(SAVED_SUBTITLE, false);
        }
        setHasOptionsMenu(true);
    }

    /**
     * Create the adapter, give it the list of crimes, and assign it to the recycle view to work with.
     */
    private void updateUI(){
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        List<Crime> crimes = crimeLab.getCrimes();

        if(crimes.size() > 0){
            mNoCrimeTextView.setVisibility(View.GONE);
            mCrimeRecyclerView.setVisibility(View.VISIBLE);
        }
        else{
            mNoCrimeTextView.setVisibility(View.VISIBLE);
            mCrimeRecyclerView.setVisibility(View.GONE);
        }
        if(mCrimeAdapter == null) {
            mCrimeAdapter = new CrimeAdapter(crimes);
            mCrimeRecyclerView.setAdapter(mCrimeAdapter);
        }
        else{
            mCrimeAdapter.setCrimes(crimes);
            mCrimeAdapter.notifyDataSetChanged();
        }

        updateSubtitle();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater){
        //call super just for convention. It doesn't actually do anything!
        super.onCreateOptionsMenu(menu, menuInflater);
        menuInflater.inflate(R.menu.fragment_crime_list, menu);

        MenuItem subtitleItem = menu.findItem(R.id.menu_item_show_subtitle);
        if(mSubtitleVisibility)
            subtitleItem.setTitle(R.string.hide_subtitle);
        else
            subtitleItem.setTitle(R.string.show_subtitle);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == CrimeListFragment.REQUEST_CRIME && data != null){
            UUID changedCrime = CrimeFragment.getChangedCrimeForIntent(data);
            if(changedCrime != null)
                mCrimeAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.menu_item_new_crime:
                Crime crime = new Crime();
                CrimeLab.get(getActivity()).addCrime(crime);
                Intent intent = new CrimePagerActivity().newIntent(getActivity(), crime.getId());
                startActivity(intent);
                return true;
            case R.id.menu_item_show_subtitle:
                mSubtitleVisibility = !mSubtitleVisibility;
                getActivity().invalidateOptionsMenu();
                updateSubtitle();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Updates subtitle to the correct corresponding # of elements
     */
    private void updateSubtitle(){
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        int crimeCount = crimeLab.getCrimes().size();
        String subtitle = getResources().getQuantityString(R.plurals.subtitle_plural, crimeCount, crimeCount);

        if(!mSubtitleVisibility){
            subtitle = null;
        }

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);  
    }

    /**
     * Save the state of the subtitle visibility upon update
     * @param bundle
     */
    public void onSaveInstanceState(Bundle bundle){
        super.onSaveInstanceState(bundle);
        bundle.putBoolean(SAVED_SUBTITLE, mSubtitleVisibility);
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

        public void setCrimes(List<Crime> crimeList){
            mCrimes = crimeList;
        }
    }
}
