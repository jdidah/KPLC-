package com.example.ian.kplcmobi;

/*
Created by Ian Omondi Cornelius
 */

/*
Sign in anims well done. Now for the sign up fragment switch
 */

/*
May need a fragment tracker, to know which fragment to load incase lifecycle affects activity.

NO NEEEEED!!!!
 */

import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;
import android.widget.EditText;

import android.app.ProgressDialog;

import android.support.constraint.ConstraintLayout;

//for delaying animation
import android.os.Handler;

//for button clicks listeners
import android.view.View;

//for the fragments
import android.app.Fragment;
import android.app.FragmentManager;

//For loading our background drawables
import android.graphics.drawable.Drawable;
import android.graphics.Bitmap;
import android.widget.Toast;

//For firebase
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.AuthResult;

public class MainActivity extends AppCompatActivity{

    //For my constraint layout
    private ConstraintLayout mConstraintLayout;

    //Member variables holding our entry screen animation plus widgets
    //Entry animation
    private Animation mAnimFadeIn; //For both the logo
    //private Animation mAnimBottomTop;

    //New, for the app name/top banner
    private Animation mAnimBounce;

    //SignUp Animation
    private Animation mAnimFadeOut; //Fading out the logo a little. Say by half
    //private Animation mAnimTopBottom; //For the app name
    private Animation mAnimFadeInSign;//for the sign in widgets

    //Widgets
    //Entry animation
    private ImageView mOpenLogo;
    private TextView mOpenText;

    //Sign up. In top down order, left to right
    private TextView mSignInTxt;
    private TextView mEmailTxt;
    private EditText mEditEmail;
    private TextView mPasswTxt;
    private EditText mEditPassw;
    private Button mLogInBtn;
    private TextView mSignUpPromptTxt;
    private Button mSignUpBtn; //will lead us to sign up fragment
    private ImageView mImageLogo;

    private ProgressDialog progressDialog;

    //Firebase authentication
    private FirebaseAuth mAuth;

    //Called when activity is being created
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //reference to our layout
        //mConstraintLayout = findViewById(R.id.fragmentHolder);

        //get references to our widgets
        //entry animation widgets
        mOpenLogo = findViewById(R.id.openLogo);
        mOpenText = findViewById(R.id.openText);

        //Sign up widgets
        mSignInTxt = findViewById(R.id.signInText);
        mEmailTxt = findViewById(R.id.emailText);
        mEditEmail = findViewById(R.id.editEmail);
        mPasswTxt = findViewById(R.id.passwText);
        mEditPassw = findViewById(R.id.editPassw);
        mLogInBtn = findViewById(R.id.logInButton);
        mSignUpPromptTxt = findViewById(R.id.signUpPromptTxt);
        mSignUpBtn = findViewById(R.id.signUpBtn);
        mImageLogo = findViewById(R.id.imageLogo);

        mConstraintLayout = findViewById(R.id.fragmentHolder);

        progressDialog = new ProgressDialog(this);

        //instantiate mAuth
        mAuth = FirebaseAuth.getInstance();

        //Load our background images and image view bitmaps efficiently, here
        //To the constraint layout, needs drawable, thus the weird code
        //mConstraintLayout.setBackgroundResource(R.drawable.loginbg2);

        final ViewTreeObserver observer = mOpenLogo.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                //mOpenLogo.setImageBitmap(LoadBitmaps.decodeSampledBitmapFromResource(getResources(),R.drawable.kplc_logo,mOpenLogo.getWidth(),mOpenLogo.getHeight()));
                //mImageLogo.setImageBitmap(LoadBitmaps.decodeSampledBitmapFromResource(getResources(),R.drawable.kplc_logo2,mImageLogo.getWidth(),mImageLogo.getHeight()));
                //mConstraintLayout.setBackground(new BitmapDrawable(getResources(),LoadBitmaps.decodeSampledBitmapFromResource(getResources(),R.drawable.loginbg2,mConstraintLayout.getWidth(),mConstraintLayout.getHeight())));

               /*
                //remove listeners
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
                    observer.removeOnGlobalLayoutListener(this);
                }else{
                    observer.removeGlobalOnLayoutListener(this);
                }
                */

               //Now, using AsyncTask, create objects for each
                LoadBitmaps load = new LoadBitmaps(mOpenLogo, getResources(), mOpenLogo.getWidth(), mOpenLogo.getHeight());
                load.execute(R.drawable.kplc_logo);

                LoadBitmaps load2 = new LoadBitmaps(mImageLogo, getResources(), mImageLogo.getWidth(), mImageLogo.getHeight());
                load2.execute(R.drawable.kplc_logo2);

                LoadBitmaps loadBitmaps = new LoadBitmaps(mConstraintLayout, getResources(), mConstraintLayout.getWidth(), mConstraintLayout.getHeight());
                loadBitmaps.execute(R.drawable.loginbg2);
            }
        });

        //get a reference to our animations, by linking them to the XML files
        //Entry animations
        mAnimFadeIn = AnimationUtils.loadAnimation(this,R.anim.fade_in);
        //mAnimBottomTop = AnimationUtils.loadAnimation(this,R.anim.bottom_top);

        //New bounce
        mAnimBounce = AnimationUtils.loadAnimation(this, R.anim.bounce);

        //Sign up
        mAnimFadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out); //for the logo. Half fade out
        //mAnimTopBottom = AnimationUtils.loadAnimation(this, R.anim.top_bottom);//for the app name
        mAnimFadeInSign = AnimationUtils.loadAnimation(this,R.anim.fade_in_signin);//for the sign_in widgets

        //set up the listener for the log in and sign up buttons, that will trigger the main page fragment or the sign up fragment
        //respectively
        mLogInBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick (View v){

                //First, get the email and password string values and validate
                String email = mEditEmail.getText().toString();
                String passw = mEditPassw.getText().toString();

                //Ensure the strings are not empty
                if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(passw)){

                    //Tutaweka progress dialog. Toast at the moment
                    //Toast.makeText(getApplicationContext(), "Please wait as we log you in", Toast.LENGTH_LONG).show();
                    progressDialog.setTitle("Logging in");
                    progressDialog.setMessage("Please wait as we log you in");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();

                    //custom log in method for our firebase
                    logIn(email,passw);
                }

            }
        });
        mSignUpBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v){

                //load the sign up fragment here
                SignUpFragment fragment = new SignUpFragment();
                FragmentManager manager = getFragmentManager();
                manager.beginTransaction().replace(R.id.fragmentHolder,fragment,"Sign Up").commit();
            }
        });

        //invoke the entry animations
        loadEntryAnim();

    }

    //Need a method to handle start of app/entry animation
    private void loadEntryAnim(){

        //set the animations
        //Listener for fade in. Need to start with sign in widgets at alpha 0
        mAnimFadeIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

                //set alpha value of app name to zero
                mOpenText.setAlpha(0f);

                /*
                //set alpha values of all sign in widgets to zero

                mSignInTxt.setAlpha(0f);
                mEmailTxt.setAlpha(0f);
                mEditEmail.setAlpha(0f);
                mPasswTxt.setAlpha(0f);
                mEditPassw.setAlpha(0f);
                mLogInBtn.setAlpha(0f);
                mSignUpPromptTxt.setAlpha(0f);
                mSignUpBtn.setAlpha(0f);

                //Didn't work. Using visibility now
                */

                //set visibility to gone
                mSignInTxt.setVisibility(View.GONE);
                mEmailTxt.setVisibility(View.GONE);
                mEditEmail.setVisibility(View.GONE);
                mPasswTxt.setVisibility(View.GONE);
                mEditPassw.setVisibility(View.GONE);
                mLogInBtn.setVisibility(View.GONE);
                mSignUpPromptTxt.setVisibility(View.GONE);
                mSignUpBtn.setVisibility(View.GONE);
                mImageLogo.setVisibility(View.GONE);
                //mConstraintLayout.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

                //New. Start animation of app name here
                //mOpenText.startAnimation(mAnimBottomTop);

                //New bounce animation
                mOpenText.startAnimation(mAnimBounce);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mOpenLogo.startAnimation(mAnimFadeIn);


        //New. Listener for bounce
        mAnimBounce.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

                //reset alpha back to 1
                mOpenText.setAlpha(1f);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

                //start our sign up animation here
                //loadSignUpAnim();
                //get our handler object
                Handler ourHandler = new Handler();

                //now delay
                ourHandler.postDelayed(new Runnable(){
                    public void run(){
                        loadSignUpAnim();
                    }
                },1500);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }

    //A method to handle SignUp animation after entry animation
    private void loadSignUpAnim(){

        //Start the partial fade out of logo
        mAnimFadeOut.setAnimationListener(new Animation.AnimationListener(){

            //setting final widget alpha value
            @Override
            public void onAnimationEnd(Animation animation){
                //mOpenLogo.setAlpha(0.8f);

                //New. tried on start

                //Back to end


                //use this code to set the background when animation of bringing in login things runs

                //mConstraintLayout.setBackgroundResource(R.drawable.lightbulb_bg);

                //Overriden by extending logo white bg

            }

            @Override
            public void onAnimationRepeat(Animation animation){

            }

            @Override
            public void onAnimationStart(Animation animation){

                //set final alpha value. to avoid that shake in UI
                //mOpenLogo.setAlpha(0.1f);
                mOpenLogo.setVisibility(View.GONE);

            }
        });

        mOpenLogo.startAnimation(mAnimFadeOut);


        //set the fading in animation of the sign up widgets. Then start the animation on all widgets at once
        mSignInTxt.setAnimation(mAnimFadeInSign);
        mEmailTxt.setAnimation(mAnimFadeInSign);
        mEditEmail.setAnimation(mAnimFadeInSign);
        mPasswTxt.setAnimation(mAnimFadeInSign);
        mEditPassw.setAnimation(mAnimFadeInSign);
        mLogInBtn.setAnimation(mAnimFadeInSign);
        mSignUpPromptTxt.setAnimation(mAnimFadeInSign);
        mSignUpBtn.setAnimation(mAnimFadeInSign);
        mImageLogo.setAnimation(mAnimFadeInSign);
        //mConstraintLayout.setAnimation(mAnimFadeInSign);

        //Set the listener
        mAnimFadeInSign.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

                //set the visibilities back to visible
                mSignInTxt.setVisibility(View.VISIBLE);
                mEmailTxt.setVisibility(View.VISIBLE);
                mEditEmail.setVisibility(View.VISIBLE);
                mPasswTxt.setVisibility(View.VISIBLE);
                mEditPassw.setVisibility(View.VISIBLE);
                mLogInBtn.setVisibility(View.VISIBLE);
                mSignUpPromptTxt.setVisibility(View.VISIBLE);
                mSignUpBtn.setVisibility(View.VISIBLE);
                mImageLogo.setVisibility(View.VISIBLE);
                //mConstraintLayout.setVisibility(View.VISIBLE);

            }

            //set final alpha values
            @Override
            public void onAnimationEnd(Animation animation) {

                /*
                //Set final values of alpha to 1

                mSignInTxt.setAlpha(1f);
                mEmailTxt.setAlpha(1f);
                mEditEmail.setAlpha(1f);
                mPasswTxt.setAlpha(1f);
                mEditPassw.setAlpha(1f);
                mLogInBtn.setAlpha(1f);
                mSignUpPromptTxt.setAlpha(1f);
                mSignUpBtn.setAlpha(1f);*/
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        //start the animation of the sign in widgets
        mAnimFadeInSign.start();

    }

    //For our login
    private void logIn (String email, String passw){

        //start authentication, then set a listener for when it is complete, then we'll
        //check if successful and proceed to next fragment
        mAuth.signInWithEmailAndPassword(email, passw).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                //check if task is successful
                if (task.isSuccessful()){

                    progressDialog.dismiss();
                    //load the main page fragment

                    //I am also required to do a little memory management here. I think if I pass in the widget's instances,
                    //on attachment of the fragment, I'll be able to remove the bitmaps and manage memory better. Probably use the getter methods
                    //Done in onCreateView, at the fragment, having gotten the widgets using the getter methods
                    MainScreenFragment fragment = new MainScreenFragment();
                    FragmentManager manager = getFragmentManager();
                    manager.beginTransaction().replace(R.id.fragmentHolder,fragment).commit();
                }else{
                    //Toast.makeText(getApplicationContext(),"Failed to log in. Please check your details and try again",Toast.LENGTH_LONG).show();

                    progressDialog.setMessage("Failed to log in. Please check your details");
                    progressDialog.setCanceledOnTouchOutside(true);
                }
            }
        });

    }

}
