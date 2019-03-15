package com.example.quickchat;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

public class GroupChatActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageView send;
    private TextView messgetext;
    private EditText inputmessge;
    private FirebaseAuth Mauth;

    private String currentuserID, currentusername;
    private DatabaseReference Mref;
    private DatabaseReference groupdatabase, groupnamekey;
    String catchString;
    private ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);
        catchString  = getIntent().getStringExtra("messege");

        scrollView = findViewById(R.id.ScrallID);
        groupdatabase = FirebaseDatabase.getInstance().getReference().child("Group").child(catchString);
        Mauth = FirebaseAuth.getInstance();
        currentuserID = Mauth.getCurrentUser().getUid();
        toolbar = findViewById(R.id.GroupChatToolbarID);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(catchString);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.backbutton_icon);


        send = findViewById(R.id.SendButtonID);
        messgetext = findViewById(R.id.MessegeTextID);
        inputmessge = findViewById(R.id.MessegeInputID);
        Mref = FirebaseDatabase.getInstance().getReference().child("Users");



        currentcusername();
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sentmessege_server();
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public void currentcusername(){

        Mref.child(currentuserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){
                     currentusername = dataSnapshot.child("name").getValue().toString();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void sentmessege_server(){

        String messegekey = groupdatabase.push().getKey();

        String messegetrxt = inputmessge.getText().toString();
        if(messegetrxt.isEmpty()){
            Toast.makeText(GroupChatActivity.this, "You must type any messege", Toast.LENGTH_LONG).show();
        }
        else {

            inputmessge.setText(null);
            scrollView.fullScroll(ScrollView.FOCUS_DOWN);

            Calendar calendardate = Calendar.getInstance();
            SimpleDateFormat simpleDatedateFormat = new SimpleDateFormat("MMM dd, yyyy");
            String date = simpleDatedateFormat.format(calendardate.getTime());

            Calendar calendardtime = Calendar.getInstance();
            SimpleDateFormat simpleDatetimeFormat = new SimpleDateFormat("hh:mm a");
            String time = simpleDatetimeFormat.format(calendardtime.getTime());

            HashMap<String, Object> setmap = new HashMap<>();
            groupdatabase.updateChildren(setmap);

            groupnamekey = groupdatabase.child(messegekey);

            HashMap<String, Object> setmapallmessege = new HashMap<>();
            setmapallmessege.put("Name: ", currentusername);
            setmapallmessege.put("Messege: ", messegetrxt);
            setmapallmessege.put("Time: ", time);
            setmapallmessege.put("Date: ", date);
            groupnamekey.updateChildren(setmapallmessege);


        }
    }

    @Override
    protected void onStart() {

        groupdatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                read_data( dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                read_data( dataSnapshot);

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        super.onStart();
    }

    public void read_data(DataSnapshot dataSnapshot){

        Iterator iterator = dataSnapshot.getChildren().iterator();

        while (iterator.hasNext()){
            String Datetext = ((DataSnapshot)iterator.next()).getValue().toString();
            String Messgetext = ((DataSnapshot) iterator.next()).getValue().toString();
            String Nametext = ((DataSnapshot)iterator.next()).getValue().toString();
            String Timetext  = ((DataSnapshot)iterator.next()).getValue().toString();

            messgetext.append(Datetext+"\n"+Messgetext+"\n"+Nametext+"\n"+Timetext+"\n\n\n\n");
            scrollView.fullScroll(ScrollView.FOCUS_DOWN);
        }
    }
}
