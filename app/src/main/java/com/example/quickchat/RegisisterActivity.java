package com.example.quickchat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import es.dmoral.toasty.Toasty;

public class RegisisterActivity extends AppCompatActivity {

    private EditText email, password, copassword;
    private Button newaccBtn, noneedacc;
    private FirebaseAuth Mauth;
    private ProgressDialog Mprogress;
    private DatabaseReference Mref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regisister);

        Mref = FirebaseDatabase.getInstance().getReference();
        Mprogress = new ProgressDialog(RegisisterActivity.this);
        Mauth = FirebaseAuth.getInstance();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        email = findViewById(R.id.NemailID);
        password = findViewById(R.id.NpasswordlID);
        copassword = findViewById(R.id.NCpasswordID);
        newaccBtn = findViewById(R.id.NnewaccountID);
        noneedacc = findViewById(R.id.NoNeedaccountID);

        noneedacc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        newaccBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String emailtext, passwordtext, copasswordtext;

                emailtext = email.getText().toString();
                passwordtext  = password.getText().toString();
                copasswordtext = copassword.getText().toString();

                if(emailtext.isEmpty() || passwordtext.isEmpty() || copasswordtext.isEmpty()){
                    Toasty.info(RegisisterActivity.this, "Enter new email and password", Toast.LENGTH_LONG).show();
                }
                else {
                    if(!passwordtext.equals(copasswordtext)){
                        Toasty.error(RegisisterActivity.this, "your password didn't match", Toast.LENGTH_LONG).show();
                    }
                    else {

                        Mprogress.setTitle("Creating New Account");
                        Mprogress.setMessage("Please wait we are creating new account ...");
                        Mprogress.setCanceledOnTouchOutside(false);
                        Mprogress.show();

                        Mauth.createUserWithEmailAndPassword(emailtext, passwordtext).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if(task.isSuccessful()){

                                    String currentusers = Mauth.getCurrentUser().getUid();

                                    Mref.child("Users").child(currentusers).setValue("");

                                    Mprogress.dismiss();
                                    Toasty.success(RegisisterActivity.this, "Account Created Success", Toast.LENGTH_LONG).show();

                                    Intent intent = new Intent(RegisisterActivity.this, HomeActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();
                                }
                                else {
                                    Mprogress.dismiss();
                                    String errormessege = task.getException().getMessage();
                                    Toasty.error(RegisisterActivity.this, errormessege, Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                }
            }
        });
    }
}
