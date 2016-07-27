package com.criminal.android.criminalintent;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

/**
 * Created by aontivero on 7/26/2016.
 */
public class CrimeImageFragment extends DialogFragment{

    private static final String EXTRA_IMAGE_PATH = "com.criminal.android.criminalintent.CrimeImageFragment.image";


    public static CrimeImageFragment newInstance(String photoPath){
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_IMAGE_PATH, photoPath);

        CrimeImageFragment crimeImageFragment = new CrimeImageFragment();
        crimeImageFragment.setArguments(bundle);

        return crimeImageFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        final String photoImage = getArguments().getString(EXTRA_IMAGE_PATH);

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_image, null);

        final ImageView imageView = (ImageView) v.findViewById(R.id.image_dialog_fragment);
        ViewTreeObserver viewTreeObserver = imageView.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Bitmap image = PictureUtils.getScaledBitmap(photoImage, imageView.getMaxWidth() * 2, imageView.getMaxHeight() * 2);
                imageView.setImageBitmap(image);
            }
        });

        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.image_viewer_title)
                .setPositiveButton(android.R.string.ok, null)
                .setView(v)
                .create();
    }

}
