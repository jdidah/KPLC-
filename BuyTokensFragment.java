package com.example.ian.kplcmobi;

/*
Created by Ian Omondi Cornelius
 */

import android.app.Fragment;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Button;
import android.widget.ImageView;

import android.os.Bundle;

import android.view.View;
import android.view.LayoutInflater;

//for loading the imageview logo bitmap
import android.view.ViewTreeObserver;


public class BuyTokensFragment extends Fragment {

    //private members for my UI widgets, from which I'll get relevant input from, and send relevant output
    private TextView mTxtShowStanding;
    private TextView mTxtShowSuggested;
    private TextView mTxtHistory;
    private EditText mEditAmount;
    private EditText mEditTokens;
    private Button mBtnPurchase;
    private ImageView mImgLogo;

    //Now, to override the relevant methods
    @Override
    public void onCreate (Bundle savedInstanceState){

        super.onCreate(savedInstanceState);

        //do nothing
    }

    //Our layout manenos
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        //remove previous view
        container.removeAllViews();

        //inflate view
        View view = inflater.inflate(R.layout.buytokens_layout,container,false);

        //get our widget references
        mTxtShowStanding = view.findViewById(R.id.txtShowStanding);
        mTxtShowSuggested = view.findViewById(R.id.txtShowSuggested);
        mTxtHistory = view.findViewById(R.id.txtHistory);
        mEditAmount = view.findViewById(R.id.editAmount);
        mEditTokens = view.findViewById(R.id.editTokens);
        mBtnPurchase = view.findViewById(R.id.btnPurchase);
        mImgLogo = view.findViewById(R.id.imgLogo);

        //Add our ViewTree observer, then load bitmap when views have been created
        ViewTreeObserver observer = mImgLogo.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                //load our bitmap
                if (isAdded()){
                    LoadBitmaps loadBitmaps = new LoadBitmaps(mImgLogo, getResources(),mImgLogo.getWidth(),mImgLogo.getHeight());
                    loadBitmaps.execute(R.drawable.kplc_logo2);
                }
            }
        });

        //listener for button click and click on history
        mBtnPurchase.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v){

                //show success message on successful purchase, if possible show tokens at the bottom of the success message
                //or load fragment for inbox for user to see
                //Bounce animation for success message. Just a small constraint layout within the main layout,
                //with visibility set to view.gone
                //Or dialog box. Will see which one will work
            }
        });

        //listen for click on text view for history
        mTxtHistory.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v){

                //open purchase history fragment
            }
        });

        return view;
    }

    //Methods to calculate tokens based on amount/amount based on tokens
    //Method to calculate suggested. Called in onCreateView, then value placed in textView widget

}
