package com.example.ian.kplcmobi;

/*
Created by Ian Omondi Cornelius
 */

/* TODO
Link tokens to purchase history
Put a text view to hold the tokens in this fragment once bought
Messages on planned power outages and power problems reported by user

Integrating with administrator app
 */

import android.app.Fragment;
import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextUtils;
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

import android.text.TextWatcher;
import android.widget.Toast;

import org.w3c.dom.Text;

//Authentication is for getting the user. So, its unnecessary here

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import java.util.Date;
import java.util.Map;


public class BuyTokensFragment extends Fragment {

    //private members for my UI widgets, from which I'll get relevant input from, and send relevant output
    private TextView mTxtShowStanding;
    private TextView mTxtShowSuggested;
    private TextView mTxtHistory;
    private EditText mEditAmount;
    private EditText mEditTokens;
    private Button mBtnPurchase;
    private ImageView mImgLogo;

    ProgressDialog progressDialog;

    //For the token rate, in ksh per KWh Unit
    private double tokenRate;

    private DatabaseReference mDbRef;

    //For the edit texts
    private TextWatcher tokensWatcher = null;
    private TextWatcher amntWatcher = null;

    //Now, to override the relevant methods
    @Override
    public void onCreate (Bundle savedInstanceState){

        super.onCreate(savedInstanceState);

        mDbRef = FirebaseDatabase.getInstance().getReference();

         progressDialog = new ProgressDialog(getActivity());

        //Get the rates stored in the database
        FirebaseDatabase.getInstance().getReference().child("TokenRates").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                tokenRate = Double.valueOf(dataSnapshot.child("rate").getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

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

        LoadBitmaps loadBitmaps = new LoadBitmaps(mImgLogo, getResources(),100,100);
        loadBitmaps.execute(R.drawable.kplc_logo2);

        /*
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
        });*/

        //listener for button click and click on history
        mBtnPurchase.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                //check if amount is not empty. If empty, show toast, else go to buy tokens process
                if (TextUtils.isEmpty(mEditAmount.getText().toString())) {
                    Toast.makeText(getActivity(), "Please enter amount", Toast.LENGTH_LONG).show();
                } else {

                    progressDialog.setTitle("Buying Tokens");
                    progressDialog.setMessage("Please wait as we process your purchase");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();

                    //show success message on successful purchase, if possible show tokens at the bottom of the success message
                    //or load fragment for inbox for user to see
                    //Bounce animation for success message. Just a small constraint layout within the main layout,
                    //with visibility set to view.gone
                    //Or dialog box. Will see which one will work

                    //In the table for Tokens, under the user id, under transactions, update tokens amount, cash, token number, and date
                    Map<String, Object> transactionRcrd = new HashMap<>();

                    //update these values
                    //First get the token Number
                    final String tokenNo = getTokenNumber();

                    transactionRcrd.put("Tokens Amount", mEditTokens.getText().toString());
                    transactionRcrd.put("Cash Value", mEditAmount.getText().toString());
                    transactionRcrd.put("Token Number", tokenNo);
                    transactionRcrd.put("Date", ServerValue.TIMESTAMP);

                    mDbRef.child("Tokens").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child("Transactions" + String.valueOf(new Date().getTime())).setValue(transactionRcrd)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()) {

                                        progressDialog.setTitle("Success");
                                        progressDialog.setMessage("Token Number:" + tokenNo);
                                        progressDialog.setCanceledOnTouchOutside(true);

                                    } else {
                                        progressDialog.setTitle("Failure");
                                        progressDialog.setMessage("We could not process your purchase. Please try again");
                                        progressDialog.setCanceledOnTouchOutside(true);
                                    }
                                }
                            });

                }
            }
        });

        //listen for click on text view for history
        mTxtHistory.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v){

                //open purchase history fragment
            }
        });

        amntWatcher = new TextWatcher() {

            int inAmount;
            double inTokens;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mEditTokens.removeTextChangedListener(tokensWatcher);

                //check if the amount string is not empty
                if (!charSequence.toString().equals("")){

                    //The maths of tokens, in reference to amount
                    //assume the cost per kwh unit is 12.6

                    inAmount = Integer.valueOf(mEditAmount.getText().toString());

                    inTokens = (double) inAmount/tokenRate;

                    mEditTokens.setText(String.valueOf(inTokens));
                    //mTxtHistory.setText(String.valueOf(amount));

                }else{

                    //set the tokens widget to empty since nothing is in there
                    mEditTokens.setText("");
                }
                mEditTokens.addTextChangedListener(tokensWatcher);


            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };

        //Use anonymous classes
        tokensWatcher = new TextWatcher() {

            int amount;
            double tokens;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                mEditAmount.removeTextChangedListener(amntWatcher);

                //check if the amount string is not empty
                if (!charSequence.toString().equals("")){

                    //The maths of tokens, in reference to amount
                    //assume the cost per kwh unit is 12.69

                    tokens = Double.valueOf(mEditTokens.getText().toString());

                    amount = (int) (tokens * tokenRate);

                    mEditAmount.setText(String.valueOf(amount));
                    //mTxtHistory.setText(String.valueOf(amount));

                }else{

                    //set the tokens widget to empty since nothing is in there
                    mEditAmount.setText("");
                }

                mEditAmount.addTextChangedListener(amntWatcher);

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };

        mEditTokens.addTextChangedListener(tokensWatcher);
        mEditAmount.addTextChangedListener(amntWatcher);


        return view;
    }

    private String getTokenNumber(){

        //to get random number
        double rand = Math.random();
        String tokenNumber = "";

        //our counter to run four times
        int counter = 5;

        //multiply the random number by 10,000, four times, to get 4 digit 5 set token number
        while (counter>0){

            //check if random number (rand) > 0.1, then multiply by 10000
            if (rand > 0.1){

                //get first set of token number
                tokenNumber += String.valueOf((int)(rand*10000.0)) + " ";

                //update counter
                counter--;

                //update random number
                rand = Math.random();

            }else{
                rand = Math.random();
            }
        }
        return tokenNumber;
    }


    //Methods to calculate tokens based on amount/amount based on tokens
    //Method to calculate suggested. Called in onCreateView, then value placed in textView widget

}
