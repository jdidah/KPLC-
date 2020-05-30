package com.example.jillo.kplcmobi;

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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class MainScreenFragment extends Fragment implements View.OnClickListener {

    //member variables
    //basically, all our UI widgets associated with clicks and animation
    private TextView mTxtBuy, mTxtReport, mTxtApply, mTxtInbox, mTxtAbout, mTxtMsgCount, mTxtBuyTokens, mTxtPurchaseHistory, mTxtUserName, mTxtLogout;
    private ImageView mImageBuy, mImageReport, mImageApply, mImageInbox, mImageAbout, mImagePurchaseHistory, mImgMsBg;

    //For our animation
    Animation mAnimBounce;

    FirebaseAuth mAuth;
    FirebaseUser mUser;
    DatabaseReference mDbRef;

    ValueEventListener dbListener;

    //Keeping tabs with our message count. Initially at 0.
    private static int msgCount = 0;

    //Now to the onCreate method for the fragment. Just override
    @Override
    public void onCreate (Bundle savedInstanceState){

        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

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
        mTxtBuyTokens = view.findViewById(R.id.txtBuyTokens);
        mTxtPurchaseHistory = view.findViewById(R.id.txtPurchaseHistory);
        mTxtUserName = view.findViewById(R.id.txtUserName);
        mTxtLogout = view.findViewById(R.id.txtLogout);
        mImageReport = view.findViewById(R.id.imageReport);
        mImageApply = view.findViewById(R.id.imageApply);
        mImageInbox = view.findViewById(R.id.imageInbox);
        mImageAbout = view.findViewById(R.id.imageAbout);
        mImageBuy = view.findViewById(R.id.imageBuyTokens);
        mImagePurchaseHistory = view.findViewById(R.id.imgPurchaseHistory);
        mImgMsBg = view.findViewById(R.id.imgMsBg);

        //set the listeners to all the widgets save for mTxtMsgCount
        mTxtBuy.setOnClickListener(this);
        mTxtReport.setOnClickListener(this);
        mTxtApply.setOnClickListener(this);
        mTxtInbox.setOnClickListener(this);
        mTxtAbout.setOnClickListener(this);
        mTxtBuyTokens.setOnClickListener(this);
        mTxtPurchaseHistory.setOnClickListener(this);
        mImageReport.setOnClickListener(this);
        mImageApply.setOnClickListener(this);
        mImageInbox.setOnClickListener(this);
        mImagePurchaseHistory.setOnClickListener(this);

        mTxtLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Log out the current user
                FirebaseAuth.getInstance().signOut();

                //Go back to login fragment
                getFragmentManager().beginTransaction().replace(R.id.fragmentHolder, new LogInFragment()).commit();
            }
        });

        //Show the username
        mDbRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mUser.getUid());
        dbListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mTxtUserName.setText("Hi " + dataSnapshot.child("Name").getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mDbRef.addValueEventListener(dbListener);

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

        /*
        final ViewTreeObserver observer = view.getViewTreeObserver();

        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                if (isAdded()) {

                    LoadBitmaps load1 = new LoadBitmaps(mImagePurchaseHistory, getResources(), mImagePurchaseHistory.getWidth(), mImagePurchaseHistory.getHeight());
                    load1.execute(R.drawable.history);

                    LoadBitmaps load2 = new LoadBitmaps(mImageReport, getResources(), mImageReport.getWidth(), mImageReport.getHeight());
                    load2.execute(R.drawable.report2);

                    LoadBitmaps load3 = new LoadBitmaps(mImageApply, getResources(), mImageApply.getWidth(), mImageApply.getHeight());
                    load3.execute(R.drawable.apply);

                    LoadBitmaps load4 = new LoadBitmaps(mImageInbox, getResources(), mImageInbox.getWidth(), mImageInbox.getHeight());
                    load4.execute(R.drawable.msgicon);

                    LoadBitmaps loadBitmaps = new LoadBitmaps(mImageAbout, getResources(),mImageAbout.getWidth(), mImageAbout.getHeight());
                    loadBitmaps.execute(R.drawable.aboutus);

                    LoadBitmaps l = new LoadBitmaps(mImgMsBg,getResources(),300,1000);
                    l.execute(R.drawable.mainscreenbg);

                }
            }
        });*/

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
            getFragmentManager().beginTransaction().replace(R.id.fragmentHolder,new InboxFragment()).commit();
        }
        else if (v.getId() == R.id.txtAbout){

            //load the about us fragment
        }else if(v.getId() == R.id.txtPurchaseHistory || v.getId() == R.id.imgPurchaseHistory){
            //load purchase history fragment
            PurchaseHistoryFragment fragment = new PurchaseHistoryFragment();
            FragmentManager manager = getFragmentManager();
            manager.beginTransaction().replace(R.id.fragmentHolder,fragment).commit();
        }
        else{
            //do nothing
        }
    }

    public void onDetach(){
        super.onDetach();

        //recycle bitmaps
        if ((mImageAbout.getDrawable())!= null){

            ((BitmapDrawable)mImageAbout.getDrawable()).getBitmap().recycle();

        }
        if((mImageBuy.getDrawable()) != null){

            ((BitmapDrawable)mImageBuy.getDrawable()).getBitmap().recycle();

        }
        if ((mImageReport.getDrawable()) != null){

            ((BitmapDrawable)mImageReport.getDrawable()).getBitmap().recycle();

        }
        if ((mImageApply.getDrawable()) != null){

            ((BitmapDrawable)mImageApply.getDrawable()).getBitmap().recycle();

        }
        if ((mImageInbox.getDrawable()) != null){

            ((BitmapDrawable)mImageInbox.getDrawable()).getBitmap().recycle();
        }
        if ((mImgMsBg.getDrawable() != null)){

            ((BitmapDrawable)mImgMsBg.getDrawable()).getBitmap().recycle();
        }

        //Remove valueEventListener
        mDbRef.removeEventListener(dbListener);
    }


    public void onResume(){
        super.onResume();
        LoadBitmaps load1 = new LoadBitmaps(mImagePurchaseHistory, getResources(), 200, 200);
        load1.execute(R.drawable.history);

        LoadBitmaps load2 = new LoadBitmaps(mImageReport, getResources(), 200, 200);
        load2.execute(R.drawable.report2);

        LoadBitmaps load3 = new LoadBitmaps(mImageApply, getResources(), 200, 200);
        load3.execute(R.drawable.apply);

        LoadBitmaps load4 = new LoadBitmaps(mImageInbox, getResources(), 200, 200);
        load4.execute(R.drawable.msgicon);

        LoadBitmaps loadBitmaps = new LoadBitmaps(mImageAbout, getResources(),200, 200);
        loadBitmaps.execute(R.drawable.aboutus);

        LoadBitmaps l = new LoadBitmaps(mImgMsBg,getResources(),300,1000);
        l.execute(R.drawable.mainscreenbg);
    }


}
