package com.example.jillo.kplcmobi;

/*
Created to allow for efficient loading of bitmap images in our backgrounds, and avoid out of memory errors
 */

import android.graphics.BitmapFactory;
import android.graphics.Bitmap;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.Nullable;
import android.util.Log;
import android.os.AsyncTask;

import java.lang.ref.WeakReference;
import android.widget.ImageView;
import android.support.constraint.ConstraintLayout;

//Extended AsyncTask so as to do UI loading outside the main UI thread

public class LoadBitmaps extends AsyncTask<Integer, Void, Bitmap>{

    //weak reference of our widgets
    private WeakReference<ImageView> mImgViewReference = null;
    private WeakReference<ConstraintLayout> mConstraintLayout = null;

    //for the resources reference
    private Resources mResources = null;

    //for the width and height references
    private int mHeight;
    private int mWidth;

    private int data = 0;

    //public constructor
    //Need two constructors. One is for the constraint layout, other is for image view
    public LoadBitmaps(ConstraintLayout constraintLayout, Resources resources, int width, int height){

        //we are using a weak reference to ensure they can be garbage collected if UI widget already gone
        mConstraintLayout = new WeakReference<ConstraintLayout>(constraintLayout);

        //put in reference to the resources
        mResources = resources;

        //width and height values
        mHeight = height;
        mWidth = width;
    }

    //public constructor for image view
    public LoadBitmaps (ImageView imageView, Resources resources, int width, int height){

        mImgViewReference = new WeakReference<ImageView>(imageView);

        mResources = resources;

        mHeight = height;
        mWidth = width;
    }

    //decode image in background. This is presumably the method the thread will run
    @Override
    protected Bitmap doInBackground(Integer... params){
        data = params[0];

        //return value based on whether reference is null or not
        if (mImgViewReference != null){

            return decodeSampledBitmapFromResource(mResources, data, mWidth, mHeight);
        }else {
            return decodeSampledBitmapFromResource(mResources, data, mWidth, mHeight);
        }
    }

    //once complete, see if widget is still available and put bitmap
    @Override
    public void onPostExecute(Bitmap bitmap){
         if (mImgViewReference != null && bitmap != null){

             final ImageView imageView = mImgViewReference.get();

             if(imageView != null){
                 imageView.setImageBitmap(bitmap);
             }
         }
         else{
             final ConstraintLayout constraintLayout = mConstraintLayout.get();

             if (constraintLayout != null){
                 constraintLayout.setBackground(new BitmapDrawable(mResources,bitmap));
             }
         }
    }

    //will use static methods
    //Methods to assist in loading bitmaps
    //It gets the appropriate sample size to use
    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight){

        //get the raw width and height that was obtained before calling this method, when we set options.inDecodeBounds to true
        final int height = options.outHeight;
        final int width = options.outWidth;
        //sample size default value. Will calculate required size shortly.
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth){

            //This means the image to be loaded is larger than the widget.
            final int halfHeight = height/2;
            final int halfWidth = width/2;

           /*
            if (inSampleSize == 0){

                Toast.makeText(new MainActivity(),"Sample size 0",Toast.LENGTH_LONG);
                return 1;
            }
            */
            //We half coz samplesize in options.inSampleSize will be rounded down to nearest power of two
            //In effect, we double the initial inSampleSize value
            //Do this using the while loop

            //What if it passes with a sample size of 1 yet we had halfed the dimensions before? Don't really know. Below is just a guess
            //Well, the default value of 1, in the options object will be rounded down to two (the nearest power of 2). Or so I think;
            while ((halfHeight/inSampleSize) >= reqHeight && (halfWidth/inSampleSize) >= reqWidth){
                //multiply inSampleSize by 2
                //This is because the sample size does not yet drop us to the required height and width of our imageview widget
                inSampleSize*= 2;
            }
        }

        return inSampleSize;
    }

    //Now, the method to load the whole image
    public Bitmap decodeSampledBitmapFromResource(Resources resources, int resID, int reqWidth, int reqHeight){

        //First, decode with inJustDecodeBounds set to true, to just get the size of the image
        final BitmapFactory.Options options = new BitmapFactory.Options();

        //if reqWidth is zero
        if (reqHeight == 0 || reqWidth == 0){
            Log.e("Error","Getting zeros");
        }

        // to get the height and width of image we want to load
        options.inJustDecodeBounds = true;

        //decode the resource.
        BitmapFactory.decodeResource(resources,resID,options);

        //calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options,reqWidth,reqHeight);
        //options.inSampleSize = 1;

        //now decode the Bitmap with inSampleSize set
        //inJustDecodeBounds has to be set to false so that a bitmap object is returned
        options.inJustDecodeBounds = false;

        // /return our final, sampled bitmap
        return BitmapFactory.decodeResource(resources, resID, options);

    }

}
