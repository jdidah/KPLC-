package com.example.ian.kplcmobi;

/*
Created by Ian Omondi Cornelius
 */

//for the onCreate method
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

//for onCreateView method
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;

//for onAttach method
import android.app.Activity;

//For extending fragment
import android.app.Fragment;

//For calling MainScreenFragment on successful registration
import android.app.FragmentManager;

//For our UI widgets
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.Button;

//for our animation
import android.view.animation.AnimationUtils;
import android.view.animation.Animation;

/*
Only Edit Text in right to left animations
 */

//For our UI activity widget references
import android.widget.ImageView;
import android.widget.Toast;

import android.app.ProgressDialog;

//For Firebase Auth and Firebase database
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.HashMap;

public class SignUpFragment extends Fragment {

    //Our member variables. Basically the UI widgets, plus Animation instance
    private EditText mEditName, mEditMtrNo, mEditPhoneNo,mEditEmail, mEditPassw, mEditConfirmPassw;
    private Button mBtnRegister;

    //Animation instance
    private Animation mAnimRightLeft;

    //For the layout image
    private ConstraintLayout constraintLayout;

    //For firebase
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private DatabaseReference mDbReference;

    private ProgressDialog progressDialog;


    //Now, override the relevant onCreate method. But do nothing

    @Override
    public void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);

        //Instantiate the FirebaseAuth
        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(getActivity());
    }

    //Now, override the most important method: onCreateView. Inflate our UI here, put in the register button listener
    //and set up the animations that I believe should be triggered by the onAttach method

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        //Inflate the layout file and get references to our edit text widgets and button
        container.removeAllViews();//Keep seeing some widgets from previous UI. Removing them

        View view = inflater.inflate(R.layout.signup_layout,container,false);

        //get references to our UI widgets
        mEditName = view.findViewById(R.id.editName);
        mEditMtrNo = view.findViewById(R.id.editMtrNo);
        mEditPhoneNo = view.findViewById(R.id.editPhoneNo);
        mEditEmail = view.findViewById(R.id.editEmail);
        mEditPassw = view.findViewById(R.id.editPassw);
        mEditConfirmPassw = view.findViewById(R.id.editConfirmPassw);
        mBtnRegister = view.findViewById(R.id.btnRegister);
        constraintLayout = view.findViewById(R.id.constraint);


        //load the bg image of layout efficiently
        final ViewTreeObserver observer = constraintLayout.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                if (isAdded()) {

                    //New Asynchronous code
                    LoadBitmaps loadBitmaps = new LoadBitmaps(constraintLayout, getResources(), constraintLayout.getWidth(), constraintLayout.getHeight());
                    loadBitmaps.execute(R.drawable.log_in_bg);
                }
            }
        });

        //load our animation
        mAnimRightLeft = AnimationUtils.loadAnimation(getActivity(),R.anim.right_left);

        //set the animation
        mEditName.setAnimation(mAnimRightLeft);
        mEditMtrNo.setAnimation(mAnimRightLeft);
        mEditPhoneNo.setAnimation(mAnimRightLeft);
        mEditEmail.setAnimation(mAnimRightLeft);
        mEditPassw.setAnimation(mAnimRightLeft);
        mEditConfirmPassw.setAnimation(mAnimRightLeft);

        //Animation will be loaded onAttach
        //now start our UI animation
        mAnimRightLeft.start();

        //set the btnRegister listener
        mBtnRegister.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v){

                //Extract details from form
                String name = mEditName.getText().toString();
                String mtrNo = mEditMtrNo.getText().toString();
                String phoneNo = mEditPhoneNo.getText().toString();
                String email = mEditEmail.getText().toString();
                String passw = mEditPassw.getText().toString();
                String confirmPassw = mEditConfirmPassw.getText().toString();

                //First check if all of them are filled
                if (TextUtils.isEmpty(name)){
                    Toast.makeText(getActivity(),"Please fill in your name",Toast.LENGTH_SHORT).show();

                }else if(TextUtils.isEmpty(mtrNo)){
                    Toast.makeText(getActivity(),"Please fill in your meter number",Toast.LENGTH_SHORT).show();
                }else if(TextUtils.isEmpty(phoneNo)){
                    Toast.makeText(getActivity(),"Please fill in your phone number",Toast.LENGTH_SHORT).show();
                }else if(TextUtils.isEmpty(email)){
                    Toast.makeText(getActivity(),"Please fill in your email address",Toast.LENGTH_SHORT).show();
                }else if(TextUtils.isEmpty(passw)){
                    Toast.makeText(getActivity(),"Please fill in your password",Toast.LENGTH_SHORT).show();
                }else if(TextUtils.isEmpty(confirmPassw)){
                    Toast.makeText(getActivity(),"Please confirm your password",Toast.LENGTH_SHORT).show();
                }else if(!passw.equals(confirmPassw)){
                    Toast.makeText(getActivity(),"Your passwords do not match",Toast.LENGTH_SHORT).show();
                }else{

                    progressDialog.setTitle("Signing Up");
                    progressDialog.setMessage("Please wait as we sign you up");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();

                    //log in user
                    registerUser(name, mtrNo, phoneNo, email, passw);
                }

            }
        });

        return view;
    }

    //Now, invoke our animation on attach
    public void onAttach(Activity activity){

        super.onAttach(activity);

    }

    public void onDetach(){
        super.onDetach();

        //remove our drawable image from memory, to avoid memory loss
        BitmapDrawable drawable = (BitmapDrawable) constraintLayout.getBackground();
        drawable.getBitmap().recycle();
    }

    private void registerUser(final String name, final String mtrNo, final String phoneNo, final String email, final String passw){

        //connect to firebase and register the user
        mAuth.createUserWithEmailAndPassword(email, passw).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                //get the instance of the current user
                mUser = mAuth.getCurrentUser();

                //get the current user ID as a string
                String uID = mUser.getUid();

                //create a database reference from the root, to the users, to the current user
                mDbReference = FirebaseDatabase.getInstance().getReference().child("Users").child(uID);

                //To add more than one value to the current user reference, use a key, value HashMap to add the values
                HashMap<String, String> userMap = new HashMap<>();

                //add the other registration details
                userMap.put("Name",name);
                userMap.put("Meter No", mtrNo);
                userMap.put("Phone No", phoneNo);

                //commit this data to the database
                //Add listener to start mainpage fragment on success
                mDbReference.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()){

                            progressDialog.dismiss();

                            //load up the main page fragment, after validating details
                            MainScreenFragment fragment = new MainScreenFragment();
                            FragmentManager manager = getFragmentManager();
                            manager.beginTransaction().replace(R.id.fragmentHolder,fragment).commit();
                        }else{
                            progressDialog.setMessage("Error. We could not Sign You Up. Please check your network connection");
                            progressDialog.setCanceledOnTouchOutside(true);
                        }
                    }
                });

            }
        });
    }
}
