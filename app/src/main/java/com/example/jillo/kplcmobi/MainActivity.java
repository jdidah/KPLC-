package com.example.jillo.kplcmobi;

/*
Sign in anims well done. Now for the sign up fragment switch
 */

/*
May need a fragment tracker, to know which fragment to load incase lifecycle affects activity.

 */

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

//for the fragments
import android.app.Fragment;
import android.app.FragmentManager;

//For loading our background drawables
import android.graphics.drawable.Drawable;
import android.graphics.Bitmap;
import android.widget.Toast;

//For firebase
import com.example.jillo.kplcmobi.LoadBitmaps;
import com.example.jillo.kplcmobi.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.AuthResult;

public class MainActivity<AppCompatActivity> extends AppCompatActivity{

    //Member variables holding our entry screen animation plus widgets
    //Entry animation
    private Animation mAnimFadeIn; //For both the logo
    //private Animation mAnimBottomTop;

    //Widgets
    //Entry animation
    private ImageView mOpenLogo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mOpenLogo = findViewById(R.id.openLogo);
        LoadBitmaps load = new LoadBitmaps(mOpenLogo,getResources(),1000,1000);
        load.execute(R.drawable.kplc_logo);

        /*
        ViewTreeObserver observer = mOpenLogo.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                LoadBitmaps load = new LoadBitmaps(mOpenLogo,getResources(),mOpenLogo.getWidth(),mOpenLogo.getHeight());
                load.execute(R.drawable.kplc_logo);
            }
        });*/

        mAnimFadeIn = AnimationUtils.loadAnimation(this,R.anim.fade_in);
        //mAnimBottomTop = AnimationUtils.loadAnimation(this,R.anim.bottom_top);

        //New bounce
        //mAnimBounce = AnimationUtils.loadAnimation(this, R.anim.bounce);

        loadEntryAnim();

    }

    private void setContentView(int activity_main) {
    }

    //Need a method to handle start of app/entry animation
    private void loadEntryAnim(){

        //set the animations
        //Listener for fade in. Need to start with sign in widgets at alpha 0
        mAnimFadeIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                getFragmentManager().beginTransaction().replace(R.id.fragmentHolder,new LogInFragment()).commit();

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mOpenLogo.startAnimation(mAnimFadeIn);

    }


    //To handle pressing the back button
    @Override
    public void onBackPressed(){

        //Get the current running fragment
        Fragment f = getFragmentManager().findFragmentById(R.id.fragmentHolder);

        if (!(f instanceof MainScreenFragment)&& (!(f instanceof LogInFragment) && !(f instanceof SignUpFragment))){

            //go back to mainscreen
            MainScreenFragment frg = new MainScreenFragment();
            getFragmentManager().beginTransaction().replace(R.id.fragmentHolder,frg).commit();
        }else if (f instanceof SignUpFragment){

            //go to login fragment
            getFragmentManager().beginTransaction().replace(R.id.fragmentHolder,new LogInFragment()).commit();
        }else{

            finish();
            System.exit(0);

        }
    }

}
