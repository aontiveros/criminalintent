package com.criminal.android.criminalintent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import java.util.Date;
import java.util.UUID;

/**
 * Created by anton on 7/16/2016.
 */
public class CrimeFragment extends Fragment {

    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_TIME = 10;

    private static final String ARG_CRIME_ID = "crime_id";
    private static final String EXTRA_CRIME_CHANGE = "com.criminal.android.criminalintent.crimefragment.change_id";
    private static final String DIALOG_DATE = "DialogDate";
    private static final String DIALOG_TIME = "DialogTime";

    private Crime mCrime;

    //Components
    private EditText mTitleField;
    private Button mDateButton;
    private Button mTimeButton;
    private CheckBox mSolvedCheckBox;


    @Override
    public void onCreate(Bundle savedInstanceBundle) {
        super.onCreate(savedInstanceBundle);
        UUID crimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
        mCrime = CrimeLab.get(getActivity()).getCrimeById(crimeId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceBundle) {
        View v = inflater.inflate(R.layout.fragment_crime, container, false);

        mTitleField = (EditText) v.findViewById(R.id.crime_title);
        mTitleField.setText(mCrime.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                //ignore for now
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                mCrime.setTitle(charSequence.toString());
                returnResult();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                //also ignore for now
            }
        });

        mDateButton = (Button) v.findViewById(R.id.crime_date);
        updateDate();
        mDateButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                FragmentManager fm = getFragmentManager();
                DatePickerFragment dateDialog = DatePickerFragment.newInstance(mCrime.getDate());
                dateDialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                dateDialog.show(fm, DIALOG_DATE);
                returnResult();
            }
        });

        mTimeButton = (Button) v.findViewById(R.id.crime_time);
        updateDate();
        mTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getFragmentManager();
                TimePickerFragment timeDialog = TimePickerFragment.newInstance(mCrime.getDate());
                timeDialog.setTargetFragment(CrimeFragment.this, REQUEST_TIME);
                timeDialog.show(fm, DIALOG_TIME);
                returnResult();
            }
        });

        mSolvedCheckBox = (CheckBox) v.findViewById(R.id.crime_solved);
        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mCrime.setSolved(true);
                returnResult();
            }
        });

        return v;
    }

    public void returnResult(){
        Intent intent = new Intent();
        intent.putExtra(EXTRA_CRIME_CHANGE, mCrime.getId());
        getActivity().setResult(Activity.RESULT_OK, intent);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent dateIntent){
        if(resultCode != Activity.RESULT_OK || dateIntent == null)
            return;
        if(requestCode == REQUEST_DATE){
            Date date = (Date) dateIntent.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mCrime.setDate(date);
            updateDate();
        }
        if(requestCode == REQUEST_TIME){
            Date date = (Date) dateIntent.getSerializableExtra(TimePickerFragment.ARG_TIME);
            mCrime.setDate(date);
            updateDate();
        }
    }

    /**
     * Create a CrimeFragment based on a Crimes UUID for lookup later
     *
     * @param crimeId
     * @return A new CrimeFragment with arguments referencing a UUID
     */
    public static CrimeFragment newInstance(UUID crimeId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, crimeId);

        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;

    }

    private void updateDate(){
        if(mDateButton != null)
            mDateButton.setText(DateFormat.format("E, MMMM d, yyyy", mCrime.getDate()));
        if(mTimeButton != null)
            mTimeButton.setText(DateFormat.format("hh:mm a", mCrime.getDate()));
    }

    public static UUID getChangedCrimeForIntent(Intent data){
        Object obj = data.getSerializableExtra(EXTRA_CRIME_CHANGE);
        if(obj != null)
            return (UUID) obj;
        return null;
    }
}

