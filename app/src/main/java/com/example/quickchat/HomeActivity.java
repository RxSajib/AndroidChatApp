package com.example.quickchat;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.audiofx.DynamicsProcessing;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import es.dmoral.toasty.Toasty;

public class HomeActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private AppBarLayout appBarLayout;
    private TabLayout tabLayout;
    private ViewPager Mviewpager;
    private FragmentAdapter fragmentAdapter;
    private DrawerLayout Mdrawerlayout;
    private NavigationView Nav;
    private FirebaseAuth Mauth;
    private DatabaseReference Mref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Mref = FirebaseDatabase.getInstance().getReference();
        toolbar = findViewById(R.id.HomeToolbarID);
        appBarLayout = findViewById(R.id.AppbarID);
        tabLayout = findViewById(R.id.TabLAyoutID);
        Mviewpager = findViewById(R.id.ViewPagerID);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Home");
        Nav = findViewById(R.id.NavagID);
        Mdrawerlayout = findViewById(R.id.DrawerID);
        Mauth = FirebaseAuth.getInstance();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.navigaction_icon);

        fragmentAdapter = new FragmentAdapter(getSupportFragmentManager());
        Mviewpager.setAdapter(fragmentAdapter);
        tabLayout.setupWithViewPager(Mviewpager);

        Nav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                if(menuItem.getItemId() == R.id.SettingsId){
                    Mdrawerlayout.setClickable(true);
                    Mdrawerlayout.closeDrawer(Gravity.START);

                }
                if(menuItem.getItemId() == R.id.LogoutID){
                    Mdrawerlayout.setClickable(true);
                    Mdrawerlayout.closeDrawer(Gravity.START);
                    Mauth.signOut();
                    Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }

                if(menuItem.getItemId() == R.id.SettingsId){
                    Mdrawerlayout.closeDrawer(Gravity.START);
                    Mdrawerlayout.setClickable(true);
                    Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
                    startActivity(intent);
                }

                if(menuItem.getItemId() == R.id.FindFreindsID){
                    Mdrawerlayout.closeDrawer(Gravity.START);
                    Mdrawerlayout.setClickable(true);
                    Intent intent = new Intent(HomeActivity.this, FindFriendsActivity.class);
                    startActivity(intent);
                }

                if(menuItem.getItemId() == R.id.GroupID){
                    Mdrawerlayout.closeDrawer(Gravity.START);
                    Mdrawerlayout.setClickable(true);

                    AlertDialog.Builder mbuilder = new AlertDialog.Builder(HomeActivity.this);
                    mbuilder.setTitle("Create New Group");
                    mbuilder.setMessage("Create your friends group you chat to friends faster");
                    final EditText editText = new EditText(HomeActivity.this);

                    mbuilder.setView(editText);
                    editText.setHint("Enter Your Group Name ...");

                    mbuilder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            String name = editText.getText().toString();
                            if(name.isEmpty()){
                                Toast.makeText(HomeActivity.this, "You Input GroupName", Toast.LENGTH_LONG).show();
                            }
                            else {
                                Mref.child("Group").child(name).setValue("").addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){

                                            Toasty.success(HomeActivity.this, "Group Created Success", Toast.LENGTH_LONG).show();
                                        }
                                        else {
                                            String errormessege = task.getException().getMessage();
                                            Toasty.error(HomeActivity.this, errormessege, Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            }

                        }
                    });

                    mbuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();
                        }
                    });

                    mbuilder.show();
                }

                return false;
            }
        });
    }

    @Override
    protected void onStart() {

        FirebaseUser Muser = FirebaseAuth.getInstance().getCurrentUser();

        if(Muser == null){
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
        else{

            String currentuserid = Mauth.getCurrentUser().getUid();
            Mref.child("Users").child(currentuserid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if(dataSnapshot.child("name").exists()){
                       // Toast.makeText(HomeActivity.this, "Welcome", Toast.LENGTH_LONG).show();
                    }
                    else {
                        Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        super.onStart();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home){
            Mdrawerlayout.openDrawer(Gravity.START);
        }

        return super.onOptionsItemSelected(item);
    }
}
