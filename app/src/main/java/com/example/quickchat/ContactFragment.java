package com.example.quickchat;


import android.content.Context;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import javax.xml.validation.ValidatorHandler;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ContactFragment extends Fragment {

    private View ContactView;
    private RecyclerView MRecylearview;
    private DatabaseReference ContactsRef;
    private DatabaseReference Usersdatabase;
    private FirebaseAuth Mauth;
    private String CurrentUserID;

    public ContactFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ContactView = inflater.inflate(R.layout.fragment_contact, container, false);
        Mauth = FirebaseAuth.getInstance();
        CurrentUserID = Mauth.getCurrentUser().getUid();

        ContactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts").child(CurrentUserID);

        MRecylearview = ContactView.findViewById(R.id.ContactReViewID);
        Usersdatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        MRecylearview.setHasFixedSize(true);
        MRecylearview.setLayoutManager(new LinearLayoutManager(getContext()));


        return ContactView;
    }


    @Override
    public void onStart() {

         FirebaseRecyclerAdapter<Contact_GetSet , ContactViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Contact_GetSet, ContactViewHolder>(
                Contact_GetSet.class,
                R.layout.contact_samplt_layout,
                ContactViewHolder.class,
                ContactsRef
        ) {
            @Override
            protected void populateViewHolder(final ContactViewHolder viewHolder, Contact_GetSet model, int position) {

                String UsersID = getRef(position).getKey();

                Usersdatabase.child(UsersID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if(dataSnapshot.hasChild("image")){
                            String name = dataSnapshot.child("name").getValue().toString();
                            String statas = dataSnapshot.child("statas").getValue().toString();
                            String image = dataSnapshot.child("image").getValue().toString();

                            viewHolder.setNameFildes(name);
                            viewHolder.setStatsFildes(statas);
                            viewHolder.setImage(image);
                        }
                        else {

                            String name = dataSnapshot.child("name").getValue().toString();
                            String statas = dataSnapshot.child("statas").getValue().toString();
                          //  String image = dataSnapshot.child("image").getValue().toString();

                            viewHolder.setNameFildes(name);
                            viewHolder.setStatsFildes(statas);
                           // viewHolder.setImage(image);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }

                });



            }

        };

        MRecylearview.setAdapter(firebaseRecyclerAdapter);
        super.onStart();
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder {

        private View Mview;
        private ImageView imageref;
        private TextView Nameref;
        private TextView Statasref;
        private Context context;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);

            Mview = itemView;
            imageref = Mview.findViewById(R.id.ContactImageID);
            Nameref = Mview.findViewById(R.id.ContactnameID);
            Statasref = Mview.findViewById(R.id.ContactStatasID);
            context = itemView.getContext();
        }

        public void setNameFildes(String Nam){
            Nameref.setText(Nam);
        }

        public void setStatsFildes(String Stat){
            Statasref.setText(Stat);
        }

        public void setImage(String img){
            Glide.with(context).load(img).into(imageref);
        }
    }
}
