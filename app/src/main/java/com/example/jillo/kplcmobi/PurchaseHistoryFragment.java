package com.example.jillo.kplcmobi;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

//To get our purchase history from database

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ValueEventListener;
import java.util.Map;

import java.util.ArrayList;
import android.app.ProgressDialog;



public class PurchaseHistoryFragment extends Fragment {

    //Need a db reference FirebaseAuth explicit instance not needed as it is needed only once to get Uid
    private DatabaseReference mDbRef;


    //Widgets for layout, and listview adapter
    private ImageView logoImg;
    private ListView historyList;

    //To show progress of loading data
    private ProgressDialog progress;

    //For our list view adapter
    private HistoryAdapter historyAdapter;
    private ValueEventListener dbListener;

    //now to override the necessary methods

    @Override
    public void onCreate (Bundle savedInstanceState){

        super.onCreate(savedInstanceState);


        //May use bundle here to get token number? Yes! From buy tokens fragment if possible
    }

    //Where we inflate our view
    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        //remove previous view
        container.removeAllViews();

        //Initialize our progress dialog here
        progress = new ProgressDialog(getActivity());
        if (progress != null) {
            //Toast.makeText(getActivity(), "Already insantiated progress dialog", Toast.LENGTH_LONG).show();
        }

        //inflate our layout
        View view = inflater.inflate(R.layout.purchasehistory_layout,container,false);

        //get our necessary UI references. Only need for logoImage to load its bitmap, and ListView for setting it to the adapter
        logoImg = view.findViewById(R.id.logoImg);
        historyList = view.findViewById(R.id.historyList);
        LoadBitmaps loadBitmaps = new LoadBitmaps(logoImg,getResources(),100, 100);
        loadBitmaps.execute(R.drawable.aboutus);

        /*
        //load our bitmap
        ViewTreeObserver observer = logoImg.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                if (isAdded()){

                    LoadBitmaps loadBitmaps = new LoadBitmaps(logoImg,getResources(),logoImg.getWidth(), logoImg.getHeight());
                    loadBitmaps.execute(R.drawable.aboutus);


                }
            }
        });*/

        //Initialize list adapter here
        historyAdapter = new HistoryAdapter ();

        //get list from db
        historyAdapter.getListFromDb();
        //set our historyList adapter;
        historyList.setAdapter(historyAdapter);


        return view;
    }

    /*
    Idea is, in our adapter class, in the constructor, once we are setting up the adapter, we load this info from the db.
    Just like in note to self, we were sourcing the notes from the device memory
     */


    //For our inner Adapter Class
    private class HistoryAdapter extends BaseAdapter{

        //Need a db reference FirebaseAuth explicit instance not needed as it is needed only once to get Uid
        //private DatabaseReference mDbRef;

        //To store the retrieved data
        //Problem is, we need an array list of objects to hold this data, and have it make sense.
        //Trying to use where its snap code
        private List<PurchaseHistory> purchaseList = new ArrayList<PurchaseHistory>();

        //Now, for our widgets
        private TextView shwDate, shwCash, shwTokens, shwTokenNo;

        PurchaseHistory purchase;

        /*
        //Need for a constructor. Where we'll populate our array list with data from the database
        public HistoryAdapter(){

            //populate purchaseList with data from db
            Toast.makeText(getActivity(),"Trying to source from firebase",Toast.LENGTH_LONG).show();
            //getListFromDb();
            Toast.makeText(getActivity(),"Done getting list",Toast.LENGTH_LONG).show();

        }*/

        //Override necessary methods
        //Returns size of arraylist (I guess for system to know number of views needed
        @Override
        public int getCount(){
            return purchaseList.size();
        }

        //Returns item from array list (I guess the one to be inflated next
        @Override
        public PurchaseHistory getItem(int whichItem){
            return purchaseList.get(whichItem);
        }

        //Returns the intenal ID of the item in the list in the BaseAdapter, not that given by the programmer
        @Override
        public long getItemId(int whichItem){
            return whichItem;
        }

        //Key method. Has the view object of the View class as a parameter. This is the List Item which needs to be prepared for display
        //to the user. We prepare the list item here using techniques of handling layouts and widget, starting with the inflater.
        //view is actually an instance of the list item layout (remember its an object at runtime). whichItem indexes our PurchaseHistory object
        //in the ArrayList which needs to be displayed in the list item and (making a guess), viewGroup will reference the listview
        //we will send the list item layout to, to show.
        /*
        All we thus need to do in the code below is write code that will transfer the data held in the Purchase History object into the widgets
        of the listitem.xml layout, for display.
         */

        @Override
        public View getView(int whichItem, View view, ViewGroup viewGroup){

            //stop the loading message dialog
            progress.dismiss();

            //check if view has been inflated. If not, inflate
            if(view == null){

                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                //now inflate the layout for purchase_history_list_item into view
                view = inflater.inflate(R.layout.purchase_list_item,viewGroup,false);
            }

            //Grab a reference to all our textView widgets
            shwDate = view.findViewById(R.id.showDate);
            shwCash = view.findViewById(R.id.showCash);
            shwTokens = view.findViewById(R.id.shwTokens);
            shwTokenNo = view.findViewById(R.id.shwTokenNumber);

            //Now, get the specific PurchaseHistory object whose details we'll put in this view
            purchase = purchaseList.get(whichItem);

            //add the relevant text widgets data
            shwDate.setText(purchase.getDate());
            shwCash.setText(purchase.getCash());
            shwTokens.setText(purchase.getAmount());
            shwTokenNo.setText(purchase.getTokenNumber());

            //Toast.makeText(getActivity(),"Get View is running",Toast.LENGTH_LONG).show();

            return view;

        }

        //Method to get our purchase list from db
        public void getListFromDb(){

            //use the db reference
            mDbRef = FirebaseDatabase.getInstance().getReference().child("Tokens")
            .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

            //We'll ignore the transactions id
            //Now, get into collecting the details using the listener. Remember to remove this listener once fragment is destroyed.
            //Might cause issues. Not so sure

            dbListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    //First show a message that you are loading the transactions
                    progress.setTitle("Loading");
                    progress.setMessage("Retrieving your transactions. Please Wait");
                    progress.setCanceledOnTouchOutside(false);
                    progress.show();

                    //Get a map of all the transactions in dataSnapshot
                    //Have it as the return value
                    collectPurchases((Map<String, Object>) dataSnapshot.getValue());
                    mDbRef.removeEventListener(this);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                    //handle database error
                    Toast.makeText(getActivity(),"Failed to load your transactions from database",Toast.LENGTH_LONG).show();

                }
            };
            //handle a case where there is no transaction record
            mDbRef.addValueEventListener(dbListener);

        }

        private void collectPurchases(Map<String, Object> transactions){

            //List to hold our purchase history instances
            //List<PurchaseHistory> list = new ArrayList<PurchaseHistory>();

            //For our purchase history objects
            //Toast.makeText(getActivity(),"Now collecting from db",Toast.LENGTH_LONG).show();
            PurchaseHistory pHistory = null;

            if (transactions.isEmpty()){
                Toast.makeText(getActivity(),"Transcations is empty",Toast.LENGTH_LONG).show();
            }
            //Toast.makeText(getActivity(),"Transcations OKAY",Toast.LENGTH_LONG).show();


            //int counter = 0;
            //Iterate through each transaction, ignoring transcation id
            //Simply put, this loop will run all through the length of the transactions set, with each value being put in entry
            for (Map.Entry<String, Object> entry: transactions.entrySet()){

                //Get new Purchase History object, add the values then add it to list
                pHistory = new PurchaseHistory();

                //add the necessary values from our map
                //First, get the specific transactions map
                Map tMap = (Map) entry.getValue();
                //Get the necessary fields, append to history object then add to arraylist
                pHistory.setAmount((String) tMap.get("Tokens Amount"));
                pHistory.setCash((String) tMap.get("Cash Value"));
                pHistory.setDate(String.valueOf(tMap.get("Date")));
                pHistory.setTokenNumber((String) tMap.get("Token Number"));

                //Append final object to the list, which is in this class
                purchaseList.add(pHistory);

                //counter++;
            }

            //Toast.makeText(getActivity(),"Looped " + String.valueOf(counter),Toast.LENGTH_LONG).show();
            notifyDataSetChanged();
        }

    }

    //class for Purchase History. Our node
    private class PurchaseHistory{

        //member variables are four Strings to hold the month, date, cash and amount values
        //Idea is, we'll be creating an instance of this class with every data set we get off the db.
        //Then, add it to the array list in adapter, after which we'll show. Using Note to Self Logic

        private String date, cash, amount, tokenNumber;

        //setter and getter methods for the member variables

        public String getDate(){
            return date;
        }

        public String getCash(){
            return cash;
        }

        public String getAmount(){
            return amount;
        }

        public String getTokenNumber(){
            return tokenNumber;
        }

        public void setDate(String date1){
            this.date = date1;
        }

        public void setCash(String cash1){
            this.cash = cash1;
        }

        public void setAmount (String amount1){
            this.amount = amount1;
        }

        public void setTokenNumber (String tokenNumber1){
            this.tokenNumber = tokenNumber1;
        }

    }

    @Override
    public void onDetach(){
        super.onDetach();

        mDbRef.removeEventListener(dbListener);
    }

}
