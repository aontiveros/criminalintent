package com.criminal.android.criminalintent;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.io.File;
import java.util.Date;
import java.util.UUID;

/**
 * Created by anton on 7/16/2016.
 */
public class CrimeFragment extends Fragment {

    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_TIME = 10;
    private static final int REQUEST_CONTACT = 20;
    private static final int REQUEST_PHOTO = 30;

    private static final String ARG_CRIME_ID = "crime_id";
    private static final String EXTRA_CRIME_CHANGE = "com.criminal.android.criminalintent.crimefragment.change_id";
    private static final String DIALOG_DATE = "DialogDate";
    private static final String DIALOG_TIME = "DialogTime";
    private static final String DIALOG_IMAGE = "DialogImage";

    //Members
    private Crime mCrime;
    private File mPhotoFile;

    //Components
    private EditText mTitleField;
    private Button mDateButton;
    private Button mTimeButton;
    private CheckBox mSolvedCheckBox;
    private Button mSuspectButton;
    private Button mReportButton;
    private ImageView mImageView;
    private ImageButton mImageButton;


    @Override
    public void onCreate(Bundle savedInstanceBundle) {
        super.onCreate(savedInstanceBundle);
        UUID crimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
        mCrime = CrimeLab.get(getActivity()).getCrimeById(crimeId);
        setHasOptionsMenu(true); //notify that there are menu items to be created
        mPhotoFile = CrimeLab.get(getActivity()).getPhotoFile(mCrime);
    }

    /**
     * Create an option menu
     * @param menu The menu to create
     * @param menuInflater The menu to inflate
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater){
        //call super just for convention. It doesn't actually do anything!
        super.onCreateOptionsMenu(menu, menuInflater);
        menuInflater.inflate(R.menu.fragment_crime, menu); //notify creation of menu items
    }

    /**
     * Create the options menu to delete a selected item
     * @param item The item that was selected
     * @return the statuc
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case R.id.menu_crime_delete_item:
                new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.confirm_delete_crime)
                        .setMessage(R.string.delete_crime_question)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                CrimeLab.get(getActivity()).removeCrimeByUUID(mCrime.getId());
                                getActivity().finishActivity(Activity.RESULT_OK);
                                getActivity().finish();
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
                mCrime.setSolved(!mCrime.isSolved());
                returnResult();
            }
        });

        //update the report crime listener with the prepared message intent
        mReportButton = (Button) v.findViewById(R.id.send_crime_button);
        mReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = ShareCompat.IntentBuilder.from(getActivity())
                        .setType("text/plain")
                        .setSubject(getString(R.string.send_report))
                        .setText(getCrimeReport())
                        .getIntent();
                startActivity(i);
            }
        });

        final Intent pickContactIntent = new Intent(Intent.ACTION_PICK,
                ContactsContract.Contacts.CONTENT_URI);
        mSuspectButton = (Button) v.findViewById(R.id.choose_suspect_button);
        mSuspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(pickContactIntent, REQUEST_CONTACT);

            }
        });
        if(mCrime.getSuspect() != null)
            mSuspectButton.setText(mCrime.getSuspect());

        final PackageManager packageManager = getActivity().getPackageManager();
        if(packageManager.resolveActivity(pickContactIntent, packageManager.MATCH_DEFAULT_ONLY) == null){
            mSuspectButton.setEnabled(false);
        }

        //Create the image button listener and prepare the implicit intent for the camera
        mImageButton = (ImageButton) v.findViewById(R.id.crime_camera);
        final Intent photoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        boolean canTakePicture = mPhotoFile != null && photoIntent.resolveActivity(packageManager) != null;
        if(canTakePicture){
            Uri photoUri = Uri.fromFile(mPhotoFile);
            photoIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        }
        mImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(photoIntent, REQUEST_PHOTO);
            }
        });

        //Update the image view if their exists and image
        mImageView = (ImageView) v.findViewById(R.id.crime_photo);
        ViewTreeObserver viewTreeObserver = mImageView.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                updatePhotoView();
            }
        });
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mPhotoFile.exists()){
                    FragmentManager fm = getFragmentManager();
                    CrimeImageFragment crimeImageFragment = CrimeImageFragment.newInstance(mPhotoFile.getAbsolutePath());
                    crimeImageFragment.show(fm, DIALOG_IMAGE);
                }
            }
        });


        return v;
    }

    private void updatePhotoView(){
        if(mPhotoFile == null || !mPhotoFile.exists()){
            mImageView.setImageDrawable(null);
        }
        else{
            Bitmap photoMap = PictureUtils.getScaledBitmap(mPhotoFile.getAbsolutePath(),
                    mImageView.getMaxWidth(), mImageView.getMaxHeight());
            mImageView.setImageBitmap(photoMap);
        }
    }
    public void returnResult(){
        Intent intent = new Intent();
        intent.putExtra(EXTRA_CRIME_CHANGE, mCrime.getId());
        getActivity().setResult(Activity.RESULT_OK, intent);

    }

    /**
     * Handle the resulting activity based on the code received.
     * Either the user has changed the date, time, contact, or the photo corresponding to the
     * crime for this particular fragment
     * @param requestCode The requested code sent
     * @param resultCode The result from the activity (should be successful)
     * @param dateIntent The intent passed from the activity
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent dateIntent){
        if(resultCode != Activity.RESULT_OK || dateIntent == null)
            return;
        if(requestCode == REQUEST_DATE){
            Date date = (Date) dateIntent.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mCrime.setDate(date);
            updateDate();
        }
        else if(requestCode == REQUEST_TIME){
            Date date = (Date) dateIntent.getSerializableExtra(TimePickerFragment.ARG_TIME);
            mCrime.setDate(date);
            updateDate();
        }
        else if(requestCode == REQUEST_PHOTO){
            updatePhotoView();
        }
        else if(requestCode == REQUEST_CONTACT){
            Uri contactUri = dateIntent.getData();

            String[] queryField = new String[]{
                    ContactsContract.Contacts.DISPLAY_NAME
            };

            Cursor c = getActivity().getContentResolver().query(contactUri, queryField, null, null, null);
            try{
                if(c.getCount() == 0)
                    return;
                c.moveToFirst();
                String suspect = c.getString(0);
                mCrime.setSuspect(suspect);
                mSuspectButton.setText(suspect);
            }
            finally {
                c.close();
            }
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

    /**
     * Update the date and time based on the respective format
     */
    private void updateDate(){
        if(mDateButton != null)
            mDateButton.setText(DateFormat.format("E, MMMM d, yyyy", mCrime.getDate()));
        if(mTimeButton != null)
            mTimeButton.setText(DateFormat.format("hh:mm a", mCrime.getDate()));
    }

    /**
     * Resolve the crime report message based on the belonging crime.
     * @return The crime report
     */
    private String getCrimeReport(){
        String solved = null;
        if(mCrime.isSolved())
            solved = getString(R.string.crime_report_solved);
        else
            solved = getString(R.string.crime_report_unsolved);

        String dateFormat = "EEE, MM dd";
        String dateString = DateFormat.format(dateFormat, mCrime.getDate()).toString();

        String suspect = mCrime.getSuspect();
        if(suspect == null){
            suspect = getString(R.string.crime_report_no_suspect);
        }
        else{
            suspect = getString(R.string.crime_report_suspect, mCrime.getSuspect());
        }

        String report = getString(R.string.crime_report, mCrime.getTitle(), dateString, solved, suspect);
        return report;
    }

    public static UUID getChangedCrimeForIntent(Intent data){
        Object obj = data.getSerializableExtra(EXTRA_CRIME_CHANGE);
        if(obj != null)
            return (UUID) obj;
        return null;
    }

    @Override
    public void onPause(){
        super.onPause();

        CrimeLab.get(getActivity())
                .updateCrime(mCrime);
    }
}

