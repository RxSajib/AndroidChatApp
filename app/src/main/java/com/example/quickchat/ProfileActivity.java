package com.example.quickchat;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private CircleImageView cimage;
    private ImageView imageView;
    private TextView name, statas;
    private Button messegeButtoon,CancelRequest;
    private DatabaseReference Mdatabase, sendFriendRequest, ContactDatabase;
    private FirebaseAuth Mauth;

    private String FriendsuserID, currentstate, CurrentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        FriendsuserID = getIntent().getStringExtra("intentextra");
         Mauth = FirebaseAuth.getInstance();
         CancelRequest = findViewById(R.id.CancelMessegeButtonID);

         Toast.makeText(ProfileActivity.this, FriendsuserID, Toast.LENGTH_LONG).show();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        Toast.makeText(ProfileActivity.this, FriendsuserID, Toast.LENGTH_LONG).show();

        currentstate = "new";

        Mdatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        ContactDatabase = FirebaseDatabase.getInstance().getReference().child("Contacts");
        sendFriendRequest  = FirebaseDatabase.getInstance().getReference().child("Friends request");


        CurrentUserID = Mauth.getCurrentUser().getUid();
        cimage = findViewById(R.id.ProfileCircleImageID);
        imageView = findViewById(R.id.ProfileRoundImageID);
        name = findViewById(R.id.ProfileNameID);
        statas = findViewById(R.id.ProfileStatasID);
        messegeButtoon = findViewById(R.id.SendMessegeButtonID);


        Mdatabase.child(FriendsuserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                if(dataSnapshot.exists()){

                    if(dataSnapshot.hasChild("image")){

                        String namefile = dataSnapshot.child("name").getValue().toString();
                        String statasfile = dataSnapshot.child("statas").getValue().toString();
                        String imagepath = dataSnapshot.child("image").getValue().toString();
                        Glide.with(ProfileActivity.this).load(imagepath).into(imageView);
                               Glide.with(ProfileActivity.this).load(imagepath).into(cimage);
                        name.setText(namefile);
                        statas.setText(statasfile);

                        ManageRequest();
                    }
                    else if(dataSnapshot.hasChild("name") || dataSnapshot.hasChild("statas")){
                        String namefile = dataSnapshot.child("name").getValue().toString();
                        String statasfile = dataSnapshot.child("statas").getValue().toString();
                        name.setText(namefile);
                        statas.setText(statasfile);


                        ManageRequest();
                    }
                }
                else {
                    Toast.makeText(ProfileActivity.this, "Your Info is not exists", Toast.LENGTH_LONG).show();
                }




            }

            @Override
            public void onCancelled(DatabaseError databaseError) {


            }
        });


    }

    public void ManageRequest(){

        if(!FriendsuserID.equals(CurrentUserID)){

            messegeButtoon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    messegeButtoon.setEnabled(false);

                    if(currentstate.equals("new")){
                        sentrequest();
                    }
                    if(currentstate.equals("sent_request")){
                        CancelFriendsRequest();
                    }
                    if(currentstate.equals("request_revived")){
                        AccepectCharRequest();
                    }
                    if(currentstate.equals("friends")){

                        DeletedSpafqContact();
                    }
                }
            });
        }
        else {
            messegeButtoon.setVisibility(View.INVISIBLE);
        }
    }

    public void CancelFriendsRequest(){

        sendFriendRequest.child(CurrentUserID).child(FriendsuserID).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()){
                            sendFriendRequest.child(FriendsuserID).child(CurrentUserID).removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if(task.isSuccessful()){
                                                messegeButtoon.setEnabled(true);
                                                currentstate  ="new";
                                                messegeButtoon.setText("Send Friend Request");
                                                messegeButtoon.setBackgroundResource(R.drawable.sentmessege_desian);

                                                CancelRequest.setVisibility(View.INVISIBLE);
                                                CancelRequest.setEnabled(false);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    public void sentrequest(){






        sendFriendRequest.child(FriendsuserID).child(CurrentUserID).child("request_type").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isComplete()){
                    sendFriendRequest.child(CurrentUserID).child(FriendsuserID)
                            .child("request_type").setValue("recived").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()){
                                messegeButtoon.setEnabled(true);
                                currentstate = "sent_request";
                                messegeButtoon.setText("Cancel friend request");
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onStart() {

        sendFriendRequest.child(FriendsuserID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if(dataSnapshot.hasChild(CurrentUserID)){

                            String reqtype = dataSnapshot.child(CurrentUserID).child("request_type").getValue().toString();
                     //       Toast.makeText(ProfileActivity.this, reqtype, Toast.LENGTH_LONG).show();

                            if(reqtype.equals("sent")){
                                currentstate = "sent_request";
                                messegeButtoon.setText("Cancel friend request");
                                messegeButtoon.setBackgroundResource(R.drawable.cancel_messege_desian);


                            }
                            else if(reqtype.equals("recived")){
                                currentstate = "request_revived";
                                messegeButtoon.setText("Accepect Friend Request");
                                CancelRequest.setVisibility(View.VISIBLE);
                                CancelRequest.setEnabled(true);
                                messegeButtoon.setBackgroundResource(R.drawable.sentmessege_desian);


                                CancelRequest.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        CancelFriendsRequest();
                                    }
                                });
                            }
                        }
                        else {
                            ContactDatabase.child(CurrentUserID).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    if(dataSnapshot.hasChild(FriendsuserID)){
                                        currentstate = "friends";
                                        messegeButtoon.setText("Removed This Contacts");
                                        messegeButtoon.setBackgroundResource(R.drawable.cancel_messege_desian);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
        super.onStart();
    }

    ///Accepect chat request

    public void AccepectCharRequest(){

        ContactDatabase.child(CurrentUserID).child(FriendsuserID).child("Contacts")
                .setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){

                    ContactDatabase.child(FriendsuserID).child(CurrentUserID)
                            .child("Contacts").setValue("Saved")
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful()){

                                        sendFriendRequest.child(CurrentUserID).child(FriendsuserID)
                                                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                if(task.isSuccessful()){

                                                    sendFriendRequest.child(FriendsuserID).child(CurrentUserID)
                                                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {


                                                            messegeButtoon.setEnabled(true);
                                                            currentstate = "friends";
                                                            messegeButtoon.setText("Remove This Contacts");
                                                            CancelRequest.setVisibility(View.INVISIBLE);
                                                            CancelRequest.setEnabled(false);
                                                            messegeButtoon.setBackgroundResource(R.drawable.cancel_messege_desian);
                                                        }
                                                    });
                                                }
                                            }
                                        });
                                    }
                                }
                            });
                }
            }
        });

    }

    public void DeletedSpafqContact(){


        ContactDatabase.child(CurrentUserID).child(FriendsuserID).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()){
                            ContactDatabase.child(FriendsuserID).child(CurrentUserID).removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if(task.isSuccessful()){
                                                messegeButtoon.setEnabled(true);
                                                currentstate  ="new";
                                                messegeButtoon.setText("Send Friend Request");

                                                CancelRequest.setVisibility(View.INVISIBLE);
                                                CancelRequest.setEnabled(false);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }
}
