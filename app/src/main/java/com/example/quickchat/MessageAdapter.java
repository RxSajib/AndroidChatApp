package com.example.quickchat;


import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Scroller;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageVewHolder> {


    private List<Message> usermessagelist;
    private DatabaseReference Muserref;
    private FirebaseAuth Mauth;
    private Context context;
    private CircleImageView reciverprofileimage;

    public MessageAdapter(List<Message> usermessagelist){

        this.usermessagelist = usermessagelist;
    }

    public class MessageVewHolder extends RecyclerView.ViewHolder{

        public TextView sendermessegetext, recivermessegetext;




        public MessageVewHolder(@NonNull View itemView) {
            super(itemView);
            context = itemView.getContext();
            sendermessegetext = itemView.findViewById(R.id.SenderID);
            recivermessegetext = itemView.findViewById(R.id.ReciverID);
            reciverprofileimage = itemView.findViewById(R.id.ReciverImageID);
        }
    }


    @NonNull
    @Override
    public MessageVewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.messege_desian, viewGroup, false);

        Mauth = FirebaseAuth.getInstance();

        return new MessageVewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageVewHolder messageVewHolder, int i) {

        String messegesenderid = Mauth.getCurrentUser().getUid();
        Message message = usermessagelist.get(i);
        String fromuserid = message.getFrom();
        String fromtype = message.getType();


        Muserref = FirebaseDatabase.getInstance().getReference().child("Users").child(fromuserid);

        Muserref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.hasChild("image")){
                    String imageoick = dataSnapshot.child("image").getValue().toString();
                    Glide.with(context).load(imageoick).into(reciverprofileimage);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if(fromtype.equals("text")){
            messageVewHolder.sendermessegetext.setVisibility(View.VISIBLE);
            messageVewHolder.recivermessegetext.setVisibility(View.INVISIBLE);


            if(fromuserid.equals(messegesenderid)) {
          //      messageVewHolder.sendermessegetext.setVisibility(View.VISIBLE);
          //      messageVewHolder.recivermessegetext.setVisibility(View.VISIBLE);
                messageVewHolder.sendermessegetext.setBackgroundResource(R.drawable.messege_reciver_desian);
                messageVewHolder.recivermessegetext.setBackgroundResource(R.drawable.sentmessege_desian);

                messageVewHolder.sendermessegetext.setText(message.getMessage());
            }
            else {
                messageVewHolder.sendermessegetext.setVisibility(View.VISIBLE);
                messageVewHolder.recivermessegetext.setVisibility(View.VISIBLE);

                messageVewHolder.recivermessegetext.setTextColor(Color.BLACK);
                messageVewHolder.recivermessegetext.setBackgroundResource(R.drawable.sentmessege_desian);

                messageVewHolder.recivermessegetext.setText(message.getMessage());
            }
        }
    }

    @Override
    public int getItemCount() {
        return usermessagelist.size();
    }



}