package com.example.quickchat;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {


    private View ChatView;
    private RecyclerView Mchat;
    private DatabaseReference MContactDatabase;
    private FirebaseAuth Mauth;
    String CurrentUser, name;
    private DatabaseReference Muser;

    public ChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ChatView = inflater.inflate(R.layout.fragment_chat, container, false);

//        CurrentUser = Muser.getUid();
        Mauth = FirebaseAuth.getInstance();
        CurrentUser = Mauth.getCurrentUser().getUid();
        MContactDatabase = FirebaseDatabase.getInstance().getReference().child("Contacts").child(CurrentUser);
        Mchat = ChatView.findViewById(R.id.FiregemtChatRecylearID);
        Muser = FirebaseDatabase.getInstance().getReference().child("Users");
        Mchat.setHasFixedSize(true);
        Mchat.setLayoutManager(new LinearLayoutManager(getActivity()));



        return ChatView;
    }

    @Override
    public void onStart() {

        final FirebaseRecyclerAdapter<MgetSet, ChatViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<MgetSet, ChatViewHolder>(
                MgetSet.class,
                R.layout.contact_samplt_layout,
                ChatViewHolder.class,
                MContactDatabase
        ) {
            @Override
            protected void populateViewHolder(final ChatViewHolder viewHolder, MgetSet model, int position) {

                final String Uid = getRef(position).getKey();
                Muser.child(Uid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if(dataSnapshot.exists()){

                            if(dataSnapshot.hasChild("image")){

                                String image = dataSnapshot.child("image").getValue().toString();
                                 name = dataSnapshot.child("name").getValue().toString();
                                String statas = dataSnapshot.child("statas").getValue().toString();

                                viewHolder.setnamefil(name);
                                viewHolder.setstatsfil(statas);
                                viewHolder.setimagefil(image);
                            }
                            else {

                                 name = dataSnapshot.child("name").getValue().toString();
                                String statas = dataSnapshot.child("statas").getValue().toString();

                                viewHolder.setnamefil(name);
                                viewHolder.setstatsfil(statas);
                            }

                            viewHolder.Mview.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    Intent intent = new Intent(getActivity(), ChatActivity.class);
                                    intent.putExtra("userID", Uid);
                                    intent.putExtra("name", name);
                                    startActivity(intent);
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }

        };
        Mchat.setAdapter(firebaseRecyclerAdapter);


        super.onStart();
    }

    private static class ChatViewHolder extends RecyclerView.ViewHolder{

        private View Mview;
        private ImageView Mimage;
        private TextView Mtitle, Mdes;
        private Context context;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);

            Mview = itemView;
            Mimage = Mview.findViewById(R.id.ContactImageID);
            Mtitle = Mview.findViewById(R.id.ContactnameID);
            Mdes = Mview.findViewById(R.id.ContactStatasID);
            context = itemView.getContext();
        }

        public void setnamefil(String nam){
            Mtitle.setText(nam);
        }

        public void setstatsfil(String stat){
            Mdes.setText(stat);
        }

        public void setimagefil(String img){
            Glide.with(context).load(img).into(Mimage);
        }
    }
}
