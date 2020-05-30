package com.example.jillo.kplcmobi;

import android.app.Fragment;
import android.renderscript.Sampler;
import android.support.annotation.NonNull;
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

//For our db manenos
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class ReportFragment extends Fragment {

    //Our widgets
    private ImageView mLogo;
    private EditText mDescription;
    private Button mSend;
    private CheckBox mCheckLocation;

    //for db
    private FirebaseAuth mAuth;
    private DatabaseReference mDbRef;
    private ValueEventListener dbListener;

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

        LoadBitmaps load = new LoadBitmaps(mLogo, getResources(), 100, 100);
        load.execute(R.drawable.kplc_logo2);

        /*
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
        });*/

        //Listeners for button clicks and check box selected
        mSend.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick (View v){

                //extract message and location variables then send

                //first, initialize firebase variables
                mAuth = FirebaseAuth.getInstance();
                mDbRef = FirebaseDatabase.getInstance().getReference();

                //Put the thread details kwanza
                HashMap<String, Object> threadDetails = new HashMap<>();

                threadDetails.put("new",true);
                threadDetails.put("reply",true);
                threadDetails.put("customer",true);
                threadDetails.put("KPLC",false);
                threadDetails.put("Description","Power report 7_8_2018");

                //now, save this is db
                mDbRef.child("Messages").child(mAuth.getCurrentUser().getUid() + "_threads").child("thread7_8_2018")
                        .setValue(threadDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });

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
