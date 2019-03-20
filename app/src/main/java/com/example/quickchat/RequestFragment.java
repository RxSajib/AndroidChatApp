package com.example.quickchat;


import android.content.ContentUris;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class RequestFragment extends Fragment {

    private View RequestView;
    private RecyclerView MrecylearView;
    private DatabaseReference Mdatabase;
    private FirebaseAuth Mauth;
    private String Currentuser;
    private DatabaseReference MuserDatabase;
     String userid, val;


    public RequestFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        RequestView = inflater.inflate(R.layout.fragment_request, container, false);
        MrecylearView = RequestView.findViewById(R.id.RequestRecylearID);
        MrecylearView.setHasFixedSize(true);
        MuserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        MrecylearView.setLayoutManager(new LinearLayoutManager(getContext()));
        Mauth = FirebaseAuth.getInstance();
        FirebaseUser Muser = Mauth.getCurrentUser();
         Currentuser = Muser.getUid();
        Mdatabase = FirebaseDatabase.getInstance().getReference().child("Friends request");





        return RequestView;
    }


    @Override
    public void onStart() {
        super.onStart();


        FirebaseRecyclerAdapter<RequestGetSet, FriendsReqAdapter> adapterFirebaseRecyclerAdapter = new FirebaseRecyclerAdapter<RequestGetSet, FriendsReqAdapter>(
                RequestGetSet.class, R.layout.friend_request_layout,
                FriendsReqAdapter.class,
                Mdatabase.child(Currentuser)
        ) {
            @Override
            protected void populateViewHolder(final FriendsReqAdapter viewHolder, RequestGetSet model, int position) {

               val  = getRef(position).getKey();
                DatabaseReference Mref = getRef(position).child("request_type").getRef();
                //  Toast.makeText(getActivity(), val, Toast.LENGTH_LONG).show();


                Mref.addValueEventListener(new ValueEventListener() {
                  @Override
                  public void onDataChange(DataSnapshot dataSnapshot) {

                      viewHolder.Mview.findViewById(R.id.AccepectButtonID).setVisibility(View.VISIBLE);
                      viewHolder.Mview.findViewById(R.id.DelectedButtonID).setVisibility(View.VISIBLE);

                      if(dataSnapshot.exists()){
                          final String value = dataSnapshot.getValue().toString();
                       //   Toast.makeText(getActivity(), value, Toast.LENGTH_LONG).show();

                          if(value.equals("recived")){

                              MuserDatabase.child(val).addValueEventListener(new ValueEventListener() {
                                  @Override
                                  public void onDataChange(DataSnapshot dataSnapshot) {

                                     if (dataSnapshot.hasChild("image")){
                                         String namefil = dataSnapshot.child("name").getValue().toString();
                                         viewHolder.setnameref(namefil);

                                         String statssfil = dataSnapshot.child("statas").getValue().toString();
                                         viewHolder.setstatsafled(statssfil);
                                         String imagefil = dataSnapshot.child("image").getValue().toString();
                                         viewHolder.setimagestat(imagefil);
                                     }
                                     else {
                                         String namefil = dataSnapshot.child("name").getValue().toString();
                                         viewHolder.setnameref(namefil);

                                         String statssfil = dataSnapshot.child("statas").getValue().toString();
                                         viewHolder.setstatsafled(statssfil);
                                     }


                                  }

                                  @Override
                                  public void onCancelled(DatabaseError databaseError) {

                                  }
                              });
                          }
                      }
                      else {

                      }

                  }

                  @Override
                  public void onCancelled(DatabaseError databaseError) {

                  }
              });

            }
        };

        MrecylearView.setAdapter(adapterFirebaseRecyclerAdapter);
        adapterFirebaseRecyclerAdapter.notifyDataSetChanged();
        MrecylearView.setSaveEnabled(true);


    }



    public static class FriendsReqAdapter extends RecyclerView.ViewHolder{


        private View Mview;
        private TextView name, statas;
        private Button confrim, delected;
        private ImageView Mimage;
        private Context context;

        public FriendsReqAdapter(@NonNull View itemView) {
            super(itemView);

            Mview = itemView;
            name = Mview.findViewById(R.id.RequestnameID);
            statas = Mview.findViewById(R.id.RequestStatasID);
            confrim = Mview.findViewById(R.id.AccepectButtonID);
            delected = Mview.findViewById(R.id.DelectedButtonID);
            Mimage = Mview.findViewById(R.id.RequestImageID);
            context = itemView.getContext();

        }

        public void setnameref(String nam){
            name.setText(nam);
        }

        public void setstatsafled(String stat){
            statas.setText(stat);
        }

        public void setimagestat(String img){
            Glide.with(context).load(img).into(Mimage);
        }
    }
}
