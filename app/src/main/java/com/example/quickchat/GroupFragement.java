package com.example.quickchat;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 */
public class GroupFragement extends Fragment {

    private ArrayAdapter<String> arrayAdapter;
    private View groupview;
    private ListView listView;
    private ArrayList<String> arrayList = new ArrayList<>();
    private DatabaseReference Mdatabase;

    public GroupFragement() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        groupview = inflater.inflate(R.layout.fragment_group_fragement, container, false);
        Mdatabase = FirebaseDatabase.getInstance().getReference().child("Group");

        Mdatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Set<String> set = new HashSet<>();

                Iterator iterator = dataSnapshot.getChildren().iterator();
                while (iterator.hasNext()){

                    set.add(((DataSnapshot)iterator.next()).getKey());
                    arrayList.clear();
                    arrayList.addAll(set);
                    arrayAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        inistalvalue();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                String pos = parent.getItemAtPosition(position).toString();
                Intent intent = new Intent(getContext(), GroupChatActivity.class);
                intent.putExtra("messege", pos);
                startActivity(intent);

            }
        });

        return groupview;
    }

    public void inistalvalue(){
        listView = groupview.findViewById(R.id.ListViewID);
        arrayAdapter = new ArrayAdapter<String>(getContext(), R.layout.list_layout,R.id.sampletetxID,  arrayList);
        listView.setAdapter(arrayAdapter);
    }

}
