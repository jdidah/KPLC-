package com.example.ian.kplcmobi;

/*
Created by Ian Omondi Cornelius
 */

//for extending fragment
import android.app.Fragment;

//For our onClick listener, since clicking both image and text associated with image should open screen for that function
import android.app.FragmentManager;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;

//For onCreate
import android.os.Bundle;

//For onCreateView
import android.view.ViewGroup;

//For our widgets
import android.widget.TextView;
import android.widget.ImageView;

//For our color
import android.graphics.Color;

//For our bounce animation
import android.view.animation.AnimationUtils;
import android.view.animation.Animation;

import android.graphics.drawable.BitmapDrawable;
import android.view.ViewTreeObserver;

public class MainScreenFragment extends Fragment implements View.OnClickListener {

    //member variables
    //basically, all our UI widgets associated with clicks and animation
    private TextView mTxtBuy, mTxtReport, mTxtApply, mTxtInbox, mTxtAbout, mTxtMsgCount;
    private ImageView mImageBuy, mImageReport, mImageApply, mImageInbox, mImageAbout;

    //For our animation
    Animation mAnimBounce;

    //Keeping tabs with our message count. Initially at 0.
    private static int msgCount = 0;

    //Now to the onCreate method for the fragment. Just override
    @Override
    public void onCreate (Bundle savedInstanceState){

        super.onCreate(savedInstanceState);

    }

    //Now, to the important method - the onCreateView
    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        //clear the previous UI contents. To avoid runtime error I heard before
        container.removeAllViews();

        //Now inflate our view
        View view = inflater.inflate(R.layout.mainpage_layout,container,false);

        //Get references to our UI widgets
        mTxtBuy = view.findViewById(R.id.txtBuy);
        mTxtReport = view.findViewById(R.id.txtReport);
        mTxtApply = view.findViewById(R.id.txtApply);
        mTxtInbox = view.findViewById(R.id.txtInbox);
        mTxtAbout = view.findViewById(R.id.txtAbout);
        mTxtMsgCount = view.findViewById(R.id.txtMsgCount);
        mImageBuy = view.findViewById(R.id.imageBuyTokens);
        mImageReport = view.findViewById(R.id.imageReport);
        mImageApply = view.findViewById(R.id.imageApply);
        mImageInbox = view.findViewById(R.id.imageInbox);
        mImageAbout = view.findViewById(R.id.imageAbout);

        //set the listeners to all the widgets save for mTxtMsgCount
        mTxtBuy.setOnClickListener(this);
        mTxtReport.setOnClickListener(this);
        mTxtApply.setOnClickListener(this);
        mTxtInbox.setOnClickListener(this);
        mTxtAbout.setOnClickListener(this);
        mImageBuy.setOnClickListener(this);
        mImageReport.setOnClickListener(this);
        mImageApply.setOnClickListener(this);
        mImageInbox.setOnClickListener(this);

        //set the mTxtMsgCount value, and color based on value
        if (msgCount > 0){

            //set text color to red
            mTxtMsgCount.setTextColor(Color.rgb(255,0,0));

            //set text to value
            mTxtMsgCount.setText(String.valueOf(msgCount));

        }else{

            //set to black
            mTxtMsgCount.setTextColor(Color.rgb(0,0,0));

            //set text to value
            mTxtMsgCount.setText(String.valueOf(msgCount));
        }


        final ViewTreeObserver observer = view.getViewTreeObserver();

        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                if (isAdded()) {

                    /*
                    mImageBuy.setImageBitmap(LoadBitmaps.decodeSampledBitmapFromResource(getResources(), R.drawable.buytokens2, mImageBuy.getWidth(), mImageBuy.getHeight()));
                    mImageReport.setImageBitmap(LoadBitmaps.decodeSampledBitmapFromResource(getResources(), R.drawable.report2, mImageReport.getWidth(), mImageReport.getHeight()));
                    mImageApply.setImageBitmap(LoadBitmaps.decodeSampledBitmapFromResource(getResources(), R.drawable.apply, mImageApply.getWidth(), mImageApply.getHeight()));
                    mImageInbox.setImageBitmap(LoadBitmaps.decodeSampledBitmapFromResource(getResources(), R.drawable.msgicon, mImageInbox.getWidth(), mImageInbox.getHeight()));
                    mImageAbout.setImageBitmap(LoadBitmaps.decodeSampledBitmapFromResource(getResources(), R.drawable.aboutus, mImageAbout.getWidth(),mImageAbout.getHeight()));
                    */

                    LoadBitmaps load1 = new LoadBitmaps(mImageBuy, getResources(), mImageBuy.getWidth(), mImageBuy.getHeight());
                    load1.execute(R.drawable.buytokens2);

                    LoadBitmaps load2 = new LoadBitmaps(mImageReport, getResources(), mImageReport.getWidth(), mImageReport.getHeight());
                    load2.execute(R.drawable.report2);

                    LoadBitmaps load3 = new LoadBitmaps(mImageApply, getResources(), mImageApply.getWidth(), mImageApply.getHeight());
                    load3.execute(R.drawable.apply);

                    LoadBitmaps load4 = new LoadBitmaps(mImageInbox, getResources(), mImageInbox.getWidth(), mImageInbox.getHeight());
                    load4.execute(R.drawable.msgicon);

                    LoadBitmaps loadBitmaps = new LoadBitmaps(mImageAbout, getResources(),mImageAbout.getWidth(), mImageAbout.getHeight());
                    loadBitmaps.execute(R.drawable.aboutus);
                }
            }
        });

        //load and set the bounce animation for our text views
        mAnimBounce = AnimationUtils.loadAnimation(getActivity(),R.anim.bounce);

        //set the animations
        mTxtBuy.setAnimation(mAnimBounce);
        mTxtReport.setAnimation(mAnimBounce);
        mTxtApply.setAnimation(mAnimBounce);
        mTxtInbox.setAnimation(mAnimBounce);

        //run the animation
        mAnimBounce.start();

        return view;
    }

    //Now for our onClickListener method
    @Override
    public void onClick (View v){

        //use if-else chain of conditional statements to see which widget has been clicked, to load appropriate fragment
        if (v.getId() == R.id.txtBuy || v.getId() == R.id.imageBuyTokens){

            //load the buy tokens fragment
            BuyTokensFragment fragment = new BuyTokensFragment();
            FragmentManager manager = getFragmentManager();
            manager.beginTransaction().replace(R.id.fragmentHolder,fragment).commit();
        }

        else if (v.getId() == R.id.txtReport || v.getId() == R.id.imageReport){

            //load the report fragment
            ReportFragment fragment = new ReportFragment();
            FragmentManager manager = getFragmentManager();
            manager.beginTransaction().replace(R.id.fragmentHolder,fragment).commit();
        }

        else if (v.getId() == R.id.txtApply || v.getId() == R.id.imageApply){

            //load the apply fragment
        }
        else if (v.getId() == R.id.txtInbox || v.getId() == R.id.imageInbox){

            //load the inbox fragment
        }
        else if (v.getId() == R.id.txtAbout){

            //load the about us fragment
        }
        else{
            //do nothing
        }
    }

    public void onDetach(){
        super.onDetach();

        //recycle bitmaps
        ((BitmapDrawable)mImageAbout.getDrawable()).getBitmap().recycle();
        ((BitmapDrawable)mImageBuy.getDrawable()).getBitmap().recycle();
        ((BitmapDrawable)mImageReport.getDrawable()).getBitmap().recycle();
        ((BitmapDrawable)mImageApply.getDrawable()).getBitmap().recycle();
        ((BitmapDrawable)mImageInbox.getDrawable()).getBitmap().recycle();
    }

}
