package com.example.jillo.kplcmobi;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.annotation.NonNull;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.app.ProgressDialog;

import android.text.TextUtils;

import android.os.Bundle;
import android.widget.Toast;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

//For firebase 
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.AuthResult;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.OnCompleteListener;

import org.w3c.dom.Text;

public class LogInFragment extends Fragment {

    //For our widgets. No animation. Just transition for fragment
    //Sign up button not needed anymore. Replaced by text
    private EditText mEditEmail, mEditPassw;
    private Button mLogInBtn;
    private ImageView mImgLogo, mImgShowPass,mImgBg;
    private TextView mTxtEmailNtEmpty, mTxtPassNtEmpty, mSignUpPrompt, mLogInAnim, mTxtFail;

    //private ProgressDialog progressDialog; - replaced by custom anim
    private Animation mExpandLR;
    private Animation mFlashingAlpha;
    private Animation mCloseAlongX;

    private Animation mTopBot;

    private Boolean repeat = false; //For flashing to repeat or not

    //For firebase
    private FirebaseAuth mAuth;

    //Now to the overriden methods
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        //do nothing. Will try to load images at this point for now. Failed

    }

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        //initialize mAuth
        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser()!=null){

            //We have a logged in user. Transition to mainscreenfragment
            getFragmentManager().beginTransaction().replace(R.id.fragmentHolder,new MainScreenFragment()).commit();
        }

        //remove previous views
        container.removeAllViews();

        //inflate new view
        View view = inflater.inflate(R.layout.login_layout,container,false);

        //get references to widgets
        mEditEmail = view.findViewById(R.id.editEmail);
        mEditPassw = view.findViewById(R.id.editPassw);
        mLogInBtn = view.findViewById(R.id.logInButton);
        mSignUpPrompt = view.findViewById(R.id.signUpPromptTxt);
        mLogInAnim = view.findViewById(R.id.logInAnim);
        mTxtFail = view.findViewById(R.id.txtFail);
        //mSignUpBtn = view.findViewById(R.id.signUpBtn);
        mImgLogo = view.findViewById(R.id.imageLogo);
        mImgBg = view.findViewById(R.id.imgBg);
        mTxtEmailNtEmpty = view.findViewById(R.id.txtEmailNtEmpty);
        mTxtPassNtEmpty = view.findViewById(R.id.txtPassNtEmpty);
        mImgShowPass = view.findViewById(R.id.imgShowPass);

        LoadBitmaps load = new LoadBitmaps(mImgLogo, getResources(), 100, 100);
        load.execute(R.drawable.kplc_logo2);
        LoadBitmaps loadBitmaps = new LoadBitmaps(mImgBg,getResources(),300,1000);
        loadBitmaps.execute(R.drawable.bulb4);

        /*
        ViewTreeObserver observer = mImgLogo.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                //Load bitmaps
                if (isAdded()) {
                    LoadBitmaps load = new LoadBitmaps(mImgLogo, getResources(), mImgLogo.getWidth(), mImgLogo.getHeight());
                    load.execute(R.drawable.kplc_logo2);
                }
            }
        });*/

        //Set visibility of Not Empty warning text views to gone
        mTxtPassNtEmpty.setVisibility(View.GONE);
        mTxtEmailNtEmpty.setVisibility(View.GONE);
        mTxtFail.setVisibility(View.GONE);
        //mLogInAnim.setVisibility(View.GONE);
        mLogInAnim.setVisibility(View.GONE);

        //progressDialog = new ProgressDialog(getActivity()); - Replaced by custom anim
        mExpandLR = AnimationUtils.loadAnimation(getActivity(), R.anim.expand_left_right);
        mFlashingAlpha = AnimationUtils.loadAnimation(getActivity(),R.anim.flashing_alpha);
        mCloseAlongX = AnimationUtils.loadAnimation(getActivity(),R.anim.close_along_x);

        mTopBot = AnimationUtils.loadAnimation(getActivity(),R.anim.top_bottom);

        //Loaded bitmaps at onCreate. Will see whether it will work

        //mLogInAnim.startAnimation(mFlashingAlpha);

        mFlashingAlpha.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

                //mLogInBtn.clearAnimation();
                mLogInBtn.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

                //mLogInAnim.setAlpha(0.1f);
                //mLogInBtn.setVisibility(View.VISIBLE);

                mLogInAnim.setVisibility(View.GONE);

                //set visibility or repeat based on repeat value
                if (repeat){
                    mLogInAnim.setVisibility(View.VISIBLE);
                    mLogInAnim.startAnimation(mFlashingAlpha);
                }else{
                    mLogInBtn.setVisibility(View.VISIBLE);
                    mLogInAnim.setVisibility(View.GONE);
                }

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        //Listener for view password icon
        mImgShowPass.setOnTouchListener(new View.OnTouchListener(){

            @Override
            public boolean onTouch (View v, MotionEvent event){

                //look at the event done
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:

                        //Pressed. Show password
                        //mEditPassw.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                        //new code
                        mEditPassw.setTransformationMethod(null);
                        break;
                    case MotionEvent.ACTION_UP:

                        //Released. Hide password
                        //mEditPassw.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        //New code
                        mEditPassw.setTransformationMethod(new PasswordTransformationMethod());
                        break;
                }
                return true; //Not handles action_cancel, cause I on
            }
        });

        //Listener for signup button
        mSignUpPrompt.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v){

                //load the sign up fragment here
                SignUpFragment fragment = new SignUpFragment();
                FragmentManager manager = getFragmentManager();
                manager.beginTransaction().replace(R.id.fragmentHolder,fragment,"Sign Up").commit();
            }
        });


        mCloseAlongX.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

                //mLogInAnim.setAlpha(1.0f);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

                //set login button invisible
                //mLogInBtn.setVisibility(View.GONE);

                //set the mAnimLogIn visible and start its animation
                //startLogInAnim();
                //mLogInBtn.setVisibility(View.GONE);
                mLogInAnim.setVisibility(View.VISIBLE);
                mLogInAnim.startAnimation(mFlashingAlpha);
                //resetButtonAlpha(1);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        //listeners for btn clicks
        mLogInBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v){

                //Handle the log in process.
                //Ensure password and email are not empty
                final String email = mEditEmail.getText().toString();
                final String passw = mEditPassw.getText().toString();

                //Ensure the strings are not empty
                if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(passw)){

                    //Tutaweka progress dialog. Toast at the moment - changed to custom anim
                    //Toast.makeText(getApplicationContext(), "Please wait as we log you in", Toast.LENGTH_LONG).show();
                    //progressDialog.setTitle("Logging in");
                    //progressDialog.setMessage("Please wait as we log you in");
                    //progressDialog.setCanceledOnTouchOutside(false);
                    //progressDialog.show();

                    //set repeat to true
                    repeat = true;

                    mLogInBtn.startAnimation(mCloseAlongX);
                    //mLogInAnim.setAlpha(1.0f);

                    //custom log in method for our firebase
                    logIn(email,passw);
                }
                if(TextUtils.isEmpty(email)){

                    mTxtEmailNtEmpty.setVisibility(View.VISIBLE);

                    //start the animations
                    mTxtEmailNtEmpty.startAnimation(mExpandLR);

                }
                if (TextUtils.isEmpty(passw)){

                    mTxtPassNtEmpty.setVisibility(View.VISIBLE);

                    //Password is empty. Show warning
                    mTxtPassNtEmpty.startAnimation(mExpandLR);

                }

            }
        });

        mEditEmail.setAnimation(mTopBot);
        mEditPassw.setAnimation(mTopBot);
        mLogInBtn.setAnimation(mTopBot);

        mTopBot.start();

        return view;

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

                    //progressDialog.dismiss(); - no need to dismiss the logInAnim


                    //load the main page fragment

                    //I am also required to do a little memory management here. I think if I pass in the widget's instances,
                    //on attachment of the fragment, I'll be able to remove the bitmaps and manage memory better. Probably use the getter methods
                    //Done in onCreateView, at the fragment, having gotten the widgets using the getter methods
                    MainScreenFragment fragment = new MainScreenFragment();
                    FragmentManager manager = getFragmentManager();
                    manager.beginTransaction().replace(R.id.fragmentHolder,fragment).commit();
                }else{
                    //Toast.makeText(getApplicationContext(),"Failed to log in. Please check your details and try again",Toast.LENGTH_LONG).show();

                    //progressDialog.setMessage("Failed to log in. Please check your details");
                    //progressDialog.setCanceledOnTouchOutside(true);

                    //Stop logInAnim and reshow loginbutton
                    //mLogInAnim.clearAnimation();
                    //stopLogInAnim();

                    //set repeat to false
                    repeat = false;

                    mTxtFail.setVisibility(View.VISIBLE);
                    mTxtFail.startAnimation(mExpandLR);

                }
            }
        });

    }

    /*
    private void startLogInAnim(){

        mLogInBtn.setVisibility(View.GONE);
        mLogInAnim.startAnimation(mFlashingAlpha);
    }
    private void stopLogInAnim(){

        mLogInAnim.setAlpha(0.1f);
        //resetButtonAlpha(0);

    }

    private void resetButtonAlpha(int set){

        if (set == 0) {
            mLogInBtn.setVisibility(View.VISIBLE);
        }
        if (set == 1){
            mLogInBtn.setVisibility(View.GONE);
        }
    }
    */

    @Override
    public void onDetach(){
        super.onDetach();

        //remove mAuth Listener
    }
}
