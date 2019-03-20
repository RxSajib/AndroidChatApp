package com.example.quickchat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.audiofx.EnvironmentalReverb;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Magnifier;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

public class SettingsActivity extends AppCompatActivity {

    private ImageView profileimage;
    private TextView name, statas;
    private EditText inputname, inputstats;
    private Button updatebutton;
    private DatabaseReference Mref;
    private FirebaseAuth Mauth;
    private Button imagebutton;
    private static final int IMAGECODE = 1;
    private Uri imageuri;
    private StorageReference Mstore;
    private String currentuser;
    private String downloadurl;

    private DatabaseReference imagedatabase;
    private ProgressDialog Mprogress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        imagedatabase = FirebaseDatabase.getInstance().getReference();

        Mstore = FirebaseStorage.getInstance().getReference().child("Profile_image");
        imagebutton = findViewById(R.id.updateIMAGEButtonID);
        Mauth = FirebaseAuth.getInstance();
        Mprogress = new ProgressDialog(SettingsActivity.this);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        Mref = FirebaseDatabase.getInstance().getReference().child("Users");
        profileimage = findViewById(R.id.ProfileImageID);
        name = findViewById(R.id.PersonnameID);
        statas = findViewById(R.id.StatasID);
        inputname = findViewById(R.id.nameedittextID);
        inputstats = findViewById(R.id.statasedittextID);
        updatebutton = findViewById(R.id.updateButtonID);


        updatebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String nametext = inputname.getText().toString();
                final String statastext = inputstats.getText().toString();

                if(nametext.isEmpty() || statastext.isEmpty()){
                    Toast.makeText(SettingsActivity.this, "Fildes is empty", Toast.LENGTH_LONG).show();
                }
                else {

                    String currentuser = Mauth.getCurrentUser().getUid();
                    HashMap<String, String> map = new HashMap<>();
                    map.put("name", nametext);
                    map.put("statas", statastext);
                    map.put("key", currentuser);

                    Mref.child(currentuser).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){

                                Toast.makeText(SettingsActivity.this, "Profile update is success", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(SettingsActivity.this, HomeActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            }
                            else {
                                String errormessege = task.getException().getMessage();
                                Toast.makeText(SettingsActivity.this, errormessege, Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });

        imagebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                   if(ContextCompat.checkSelfPermission(SettingsActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){

                        ActivityCompat.requestPermissions(SettingsActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                       //Toast.makeText(SettingsActivity.this, "permission denie", Toast.LENGTH_LONG).show();
                   }
                   else {

                       CropImage.activity()
                               .setGuidelines(CropImageView.Guidelines.ON)
                               .start(SettingsActivity.this);
                   }
               }
               else{

                   CropImage.activity()
                           .setGuidelines(CropImageView.Guidelines.ON)
                           .start(SettingsActivity.this);
               }

            }
        });

        readfordatabase();
    }

    public void readfordatabase(){

         currentuser = Mauth.getCurrentUser().getUid();

        Mref.child(currentuser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {



//
               if(dataSnapshot.exists()){
                   if(dataSnapshot.hasChild("image")){

                       String img = dataSnapshot.child("image").getValue().toString();
                       String namefil = dataSnapshot.child("name").getValue().toString();
                       String statsfil = dataSnapshot.child("statas").getValue().toString();

                       name.setText(namefil);
                       statas.setText(statsfil);
                       Glide.with(SettingsActivity.this).load(img).into(profileimage);

                   }
                    if(dataSnapshot.hasChild("name") || dataSnapshot.hasChild("statas")){

                       String namefil = dataSnapshot.child("name").getValue().toString();
                       String statsfil = dataSnapshot.child("statas").getValue().toString();
                       name.setText(namefil);
                       statas.setText(statsfil);
                   }
               }
               else {
                   Toast.makeText(SettingsActivity.this, "Please Logout or restart app", Toast.LENGTH_LONG).show();
               }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                Mprogress.setTitle("Your profile is updating ...");
                Mprogress.setMessage("Please wait your profile details is updating");
                Mprogress.setCanceledOnTouchOutside(false);
                Mprogress.show();

                 imageuri = result.getUri();


                 StorageReference imagepath = Mstore.child(currentuser+".jpg");
                 imagepath.putFile(imageuri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                     @Override
                     public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                         if(task.isSuccessful()){

                              downloadurl = task.getResult().getDownloadUrl().toString();
                             imagedatabase.child("Users").child(currentuser).child("image").setValue(downloadurl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                 @Override
                                 public void onComplete(@NonNull Task<Void> task) {
                                     if(task.isSuccessful()){
                                         Mprogress.dismiss();
                                         Toast.makeText(SettingsActivity.this, "Profile image update successfully", Toast.LENGTH_LONG).show();
                                     }
                                     else {
                                         Mprogress.dismiss();
                                         String errormessege = task.getException().getMessage();
                                         Toast.makeText(SettingsActivity.this, errormessege, Toast.LENGTH_LONG).show();
                                     }
                                 }
                             });
                         }
                         else {
                             Mprogress.dismiss();
                             String errormessege = task.getException().getMessage();
                             Toast.makeText(SettingsActivity.this, errormessege, Toast.LENGTH_LONG).show();
                         }
                     }
                 });

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
