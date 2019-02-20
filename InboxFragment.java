package com.example.ian.kplcmobi;

/*
Created by Ian Cornelius
 */

/*
Okay, serious code here. First, I need to sort my messages in terms of the correct order of appearance, based on some time stamp
or sorting sequence note in the db fields. The messages will be under the main child of messages, then user id, then each message
thread as a node with messageid - just a title with time stamp, then textual fields for all the texts under that thread. The thread
may be have a field for sorting id in db. Proly save these messages offline for later view, even for purchase history

Then, I need interfaces for communication with message_view fragment on which message node/thread has been clicked.
Pass the thread, then display, in correct order, each message in the thread.

A lot of sorting needed here. Seen this problem in purchase history

Marking of whether it is read or not is done by a field in db

Changes -- will not store full thread here. Opened in message view. Just id is enough, then we use that message id to open full
thread in message view

Each message under thread child set by key "#" where # is a non-zero positive integer, showing the order of messages.

Then, another value under key "msgsNO", will tell us the total number of messages in thread, to help us loop through all threads

Finally, we'll have the value under key "new", a boolean set to true when the message is sent, set to false by receiver

We need under a child, to have values and a child. Then the sentence above can be implemented under values of message thread child

Ikikataa, have message threads and actual messages under a thread in different tables

Can go crazy and have all messages under a thread (child), as values. Just playing with keys to distinguish messages. Simplistic approach

 */

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//For our db work
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class InboxFragment extends Fragment {

    //For our UI widgets
    private ListView mInboxList;
    private ImageView mInboxBg;

    //Four our adapter
    private InboxListAdapter listAdapter;

    //For our db work
    private FirebaseAuth mAuth;
    private DatabaseReference mDbRef;
    private ValueEventListener dbListener;

    //Overriding
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        //do nothing
    }

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        //remove all previous views
        container.removeAllViews();

        //inflate our layout
        View view = inflater.inflate(R.layout.inbox_layout,container,false);

        //get reference to Inbox List
        mInboxList = view.findViewById(R.id.inboxList);
        mInboxBg = view.findViewById(R.id.imgInbxBg);

        LoadBitmaps l = new LoadBitmaps(mInboxBg,getResources(),300,1000);
        l.execute(R.drawable.inboxbg);

        //set our adapter
        listAdapter = new InboxListAdapter();
        listAdapter.getMsgThreadsFromDb();
        mInboxList.setAdapter(listAdapter);

        mInboxList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                //get our clicked message thread
                MessageThreads tempThread = listAdapter.getItem(i);

                //setup an instance of message fragment, then pass the specific thread clicked
                MessageHandlerFragment messageHandler = new MessageHandlerFragment();

                //pass our thread
                messageHandler.getMsgThread(tempThread);

                //show our thread
                getFragmentManager().beginTransaction().replace(R.id.fragmentHolder,messageHandler).commit();
            }
        });

        return view;
    }

    private class InboxListAdapter extends BaseAdapter{

        //For our widgets
        private TextView mCoId, mUserId, mTxtMsgDesc, mTxtNew, mTxtNoReply;
        private ImageView mCoImg;

        //our node
        private MessageThreads threads;

        //Our data holder
        private List<MessageThreads> messageThreadsList = new ArrayList<>();

        @Override
        public int getCount(){
            return messageThreadsList.size();
        }

        @Override
        public MessageThreads getItem (int whichItem){
            return messageThreadsList.get(whichItem);
        }

        @Override
        public long getItemId(int whichItem){
            return whichItem;
        }

        /*
        //construct our fake list when constructor is called
        public InboxListAdapter(){
            //our node
            threads = new MessageThreads();
            threads.setDescription("Concerning Power Outage Report");
            threads.setCustomer(true);
            threads.setKPLC(true);
            threads.setRecent(false);
            threads.setReply(true);
            threads.setId("1");
            messageThreadsList.add(threads);
            threads = new MessageThreads();
            threads.setDescription("Power Outage Notifications");
            threads.setCustomer(false);
            threads.setReply(false);
            threads.setKPLC(true);
            threads.setRecent(true);
            threads.setId("1");
            messageThreadsList.add(threads);

        }*/

        //create our view
        @Override
        public View getView (int whichItem, View view, ViewGroup viewGroup){

            if (view == null){

                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.inbox_item,viewGroup,false);

            }
            mCoId = view.findViewById(R.id.coId);
            mUserId = view.findViewById(R.id.userId);
            mTxtMsgDesc = view.findViewById(R.id.txtMsgDesc);
            mTxtNew = view.findViewById(R.id.txtNew);
            mTxtNoReply = view.findViewById(R.id.txtNoReply);
            mCoImg = view.findViewById(R.id.coLogo);

            //everything set to invisible
            mCoId.setVisibility(View.GONE);
            mUserId.setVisibility(View.GONE);
            mTxtNew.setVisibility(View.GONE);
            mTxtNoReply.setVisibility(View.GONE);

            LoadBitmaps l = new LoadBitmaps(mCoImg, getResources(),45,45);
            l.execute(R.drawable.aboutus);

            threads = messageThreadsList.get(whichItem);
            if (threads.getKPLC()){
                mCoId.setVisibility(View.VISIBLE);
            }
            if (threads.getCustomer()){
                mUserId.setVisibility(View.VISIBLE);
            }
            if (!threads.getReply()){
                mTxtNoReply.setVisibility(View.VISIBLE);
            }
            if (threads.getRecent()){
                mTxtNew.setVisibility(View.VISIBLE);
            }
            mTxtMsgDesc.setText(threads.getDescription());

            return view;
        }


        //Getting our messages from db. Set it to the arraylist
        public void getMsgThreadsFromDb(){

            //get Firebase references
            mAuth = FirebaseAuth.getInstance();
            mDbRef = FirebaseDatabase.getInstance().getReference();
            dbListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    //now get the message threads
                    collectThreads((Map<String, Object>) dataSnapshot.getValue());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };

            //So, getting the message threads, using listener
            mDbRef.child("Messages").child(mAuth.getCurrentUser().getUid() + "_threads").addValueEventListener(dbListener);

        }

        //Now the method itself
        public void collectThreads (Map<String, Object> threads){

            //For our message threads node
            MessageThreads msgThread;

            for (Map.Entry<String, Object> entry: threads.entrySet()){

                //get a new MessageThreadInstance
                msgThread = new MessageThreads();

                //our messageThread id is the key/ name of the thread
                msgThread.setId(entry.getKey());

                //Now, our specific values for the thread. Temporarily save it in a map
                Map tMap = (Map) entry.getValue();

                //Put these values in our node
                msgThread.setRecent((Boolean)tMap.get("new"));
                msgThread.setReply((Boolean) tMap.get("reply"));
                msgThread.setCustomer((Boolean) tMap.get("customer"));
                msgThread.setKPLC((Boolean) tMap.get("KPLC"));
                msgThread.setDescription((String) tMap.get("Description"));

                //add to list
                messageThreadsList.add(msgThread);
            }

            //notify data set changed
            notifyDataSetChanged();
        }

    }
    public class MessageThreads{

        /*public MessageThreads(){

        }*/

        private String description, id;
        private Boolean recent, customer, kplc, reply;

        public Boolean getReply() {
            return reply;
        }

        public Boolean getCustomer() {
            return customer;
        }

        public Boolean getRecent() {
            return recent;
        }

        public Boolean getKPLC() {
            return kplc;
        }

        public String getDescription() {
            return description;
        }

        public String getId() {
            return id;
        }

        public void setCustomer(Boolean customer) {
            this.customer = customer;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public void setId(String id) {
            this.id = id;
        }

        public void setRecent(Boolean recent) {
            this.recent = recent;
        }

        public void setKPLC(Boolean kplc) {
            this.kplc = kplc;
        }

        public void setReply(Boolean reply) {
            this.reply = reply;
        }
    }

    @Override
    public void onDetach(){
        super.onDetach();

        //remove listener
        mDbRef.removeEventListener(dbListener);
    }
}
