package com.example.ian.kplcmobi;

/*
Created by Ian Omondi Cornelius
 */

import android.app.Fragment;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.EditText;
import android.widget.Button;
import android.widget.CheckBox;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import android.widget.CompoundButton;

public class ReportFragment extends Fragment {

    //Our widgets
    private ImageView mLogo;
    private EditText mDescription;
    private Button mSend;
    private CheckBox mCheckLocation;

    //Override our methods
    @Override
    public void onCreate (Bundle savedInstanceState){

        super.onCreate(savedInstanceState);

        //do nothing
    }

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        //remove previous views
        container.removeAllViews();

        //inflate our view
        View view = inflater.inflate(R.layout.report_layout, container,false);

        //get our UI references
        mLogo = view.findViewById(R.id.logo);
        mDescription = view.findViewById(R.id.editDescription);
        mSend = view.findViewById(R.id.btnSend);
        mCheckLocation = view.findViewById(R.id.checkLocation);

        //Location checkbox not checked by default
        mCheckLocation.setChecked(false);

        //Load our logo image efficiently
        ViewTreeObserver observer = mLogo.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

               if (isAdded()) {

                   LoadBitmaps load = new LoadBitmaps(mLogo, getResources(), mLogo.getWidth(), mLogo.getHeight());
                   load.execute(R.drawable.kplc_logo2);

               }
            }
        });

        //Listeners for button clicks and check box selected
        mSend.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick (View v){

                //extract message and location variables then send
            }
        });

        mCheckLocation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                //start checking for location data
            }
        });

        return view;
    }

    //method to stream data to network and firebase, plus keep a db copy in device.

}
