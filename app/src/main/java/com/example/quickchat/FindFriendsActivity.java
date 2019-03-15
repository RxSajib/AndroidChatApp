package com.example.quickchat;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class FindFriendsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private DatabaseReference Mdatabase;
    private FirebaseAuth Mauth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);

        toolbar = findViewById(R.id.FindFriToolbarID);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Find Friends");
       // String userid = Mauth.getCurrentUser().getUid();

        Mdatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        recyclerView = findViewById(R.id.FindFriendRecylearVID);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(FindFriendsActivity.this));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.backbutton_icon);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    ///runtime retrive so we need onstart


    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<MgetSet, MviewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<MgetSet, MviewHolder>(
                MgetSet.class,
                R.layout.findfriends_layout,
                MviewHolder.class,
                Mdatabase
        ) {
            @Override
            protected void populateViewHolder(MviewHolder viewHolder, MgetSet model, final int position) {

                viewHolder.setNametext(model.getName());
                viewHolder.setdetails(model.getStatas());
                viewHolder.setimage(model.getImage());


               final String positionID = getRef(position).getKey();
                viewHolder.Mview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(FindFriendsActivity.this, ProfileActivity.class);
                        intent.putExtra("intentextra", positionID);
                        startActivity(intent);

                    }
                });
            }
        };
        recyclerView.setAdapter(firebaseRecyclerAdapter);


    }

    public static class MviewHolder extends RecyclerView.ViewHolder{

        private View Mview;
        private TextView nametext, detailstext;
        private CircleImageView image;
       private Context context;

        public MviewHolder(@NonNull View itemView) {
            super(itemView);
            Mview = itemView;
            nametext = Mview.findViewById(R.id.HeaddingID);
            detailstext = Mview.findViewById(R.id.DescptrionID);
            image = Mview.findViewById(R.id.Cimage);
            context = Mview.getContext();
        }

        public void setNametext(String name){
            nametext.setText(name);
        }
        public void setdetails(String details){
            detailstext.setText(details);
        }

        public void setimage(String downloadurl){
            Picasso.with(context).load(downloadurl).placeholder(R.drawable.defaultimage).into(image);
        }

    }



}
