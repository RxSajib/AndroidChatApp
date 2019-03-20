package com.example.quickchat;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class ChatActivity extends AppCompatActivity {

    private final List<Message>  messageList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private MessageAdapter messageAdapter;
    private String name, ReciveUid;
    private DatabaseReference MuserDatabase;
    private Toolbar Mtoolbar;
    private TextView nameFil, statasFil;
    private ImageView imagefil;
    private FirebaseAuth Mauth;
    private String CurrentID;
    private DatabaseReference Messegedatabase;


    private RecyclerView Mrecylear;
    private EditText chatfild;
    private ImageView chatimage;
    private DatabaseReference Mref;
  //  private String Currentuser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        MuserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        ReciveUid = getIntent().getStringExtra("userID");
        Mauth = FirebaseAuth.getInstance();
        CurrentID = Mauth.getCurrentUser().getUid();
      //  Currentuser =
        Toast.makeText(ChatActivity.this, CurrentID, Toast.LENGTH_LONG).show();

        Messegedatabase = FirebaseDatabase.getInstance().getReference().child("Message");

        Mrecylear = findViewById(R.id.ChatRecylearID);
        chatfild = findViewById(R.id.ChatEdittextID);
        chatimage = findViewById(R.id.ChatSentButtonID);
        Mref = FirebaseDatabase.getInstance().getReference();
        Mrecylear.setHasFixedSize(true);
        Mrecylear.setLayoutManager(new LinearLayoutManager(ChatActivity.this));

        SetToolbar();

        chatimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startPosting();
            }
        });

    }

    public void startPosting(){

        String message = chatfild.getText().toString();

        if(message.isEmpty()){
            Toasty.info(ChatActivity.this, "Enter your message first", Toast.LENGTH_LONG).show();
        }
        else {

            String messegesenderref = "Message/"+CurrentID +"/"+ReciveUid;
            String messegereciverref = "Message/"+ReciveUid+"/"+CurrentID;

            DatabaseReference usermessegeref = Mref.child("Message").child(CurrentID)
                    .child(ReciveUid).push();

            String MessegePushID = usermessegeref.getKey();

            Map messegetext = new HashMap();
            messegetext.put("message", message);
            messegetext.put("type", "text");
            messegetext.put("from", CurrentID);

            Map messgebodydetails = new HashMap();
            messgebodydetails.put(messegesenderref+"/"+ MessegePushID, messegetext);
            messgebodydetails.put(messegereciverref+"/"+MessegePushID, messegetext);

            Mref.updateChildren(messgebodydetails).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {

                    if(task.isSuccessful()){
                      //  Toast.makeText(ChatActivity.this, "Message sent success", Toast.LENGTH_LONG).show();
                    }
                    else {
                        Toasty.error(ChatActivity.this, "Message didn't sent", Toast.LENGTH_LONG).show();
                    }
                }
            });

            chatfild.setText(null);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public void SetToolbar(){

        Mtoolbar = findViewById(R.id.ChatToolbarID);
        setSupportActionBar(Mtoolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.backbutton_icon);

        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.costom_toolbar_profile, null);
        actionBar.setCustomView(view);

        nameFil = findViewById(R.id.ComsTomNameID);
        statasFil = findViewById(R.id.CostomStatasID);
        imagefil = findViewById(R.id.CostomImageID);


        MuserDatabase.child(ReciveUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){
                    String name = dataSnapshot.child("name").getValue().toString();
//                    String image = dataSnapshot.child("image").getValue().toString();

                    nameFil.setText(name);
                 //   Glide.with(ChatActivity.this).load(image).into(imagefil);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        messageAdapter = new MessageAdapter(messageList);

        linearLayoutManager = new LinearLayoutManager(this);
        Mrecylear.setAdapter(messageAdapter);
        messageAdapter.notifyDataSetChanged();
        Mrecylear.smoothScrollToPosition(Mrecylear.getAdapter().getItemCount());
    }

    @Override
    protected void onStart() {

        Messegedatabase.child(CurrentID).child(ReciveUid).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Message message  = dataSnapshot.getValue(Message.class);
                messageList.add(message);
                messageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

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


    /*
    @Override
    protected void onStart() {


        FirebaseRecyclerAdapter<Message, MessageHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Message, MessageHolder>(
                Message.class,
                R.layout.messege_desian,
                MessageHolder.class,
                Messegedatabase

        ) {
            @Override
            protected void populateViewHolder(final MessageHolder viewHolder, Message model, int position) {

               Messegedatabase.child(CurrentID).child(ReciveUid).addValueEventListener(new ValueEventListener() {
                   @Override
                   public void onDataChange(DataSnapshot dataSnapshot) {


                           String messege = dataSnapshot.child("message").getValue().toString();
                           viewHolder.setreciverfil(messege);

                   }

                   @Override
                   public void onCancelled(DatabaseError databaseError) {

                   }
               });
            }
        };

        Mrecylear.setAdapter(firebaseRecyclerAdapter);


        super.onStart();
    } */

 /*   public class MessageHolder extends RecyclerView.ViewHolder{


        private View Mview;
    //    private CircleImageView Mimage;
        private TextView Mreciver;
        private TextView Msender;
        private Context context;

        public MessageHolder(@NonNull View itemView) {
            super(itemView);

            Mview = itemView;
     //       Mimage = Mview.findViewById(R.id.ReciverImageID);
            Mreciver = Mview.findViewById(R.id.ReciverID);
            Msender = Mview.findViewById(R.id.SenderID);
            context = itemView.getContext();
        }

       // public void setimagefil(String img){
         //   Glide.with(context).load(img).into(Mimage);
     //   }

        public void setreciverfil(String recive){
            Mreciver.setText(recive);
        }

        public void setsenderfil(String sender){
            Msender.setText(sender);
        }

    }*/
}
