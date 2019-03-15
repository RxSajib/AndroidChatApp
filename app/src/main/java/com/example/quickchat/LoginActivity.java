package com.example.quickchat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.projection.MediaProjection;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText email, password;
    private Button loginbutton;
    private TextView newacc, loginphone;
    private FirebaseAuth Mauth;
    private ProgressDialog Mrogress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Mrogress = new ProgressDialog(LoginActivity.this);
        Mauth = FirebaseAuth.getInstance();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        email = findViewById(R.id.EmailID);
        password  = findViewById(R.id.PasswordID);
        loginbutton = findViewById(R.id.LoginButtonID);
        newacc = findViewById(R.id.NewAccountID);
        loginphone = findViewById(R.id.PhoneID);


        newacc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisisterActivity.class);
                startActivity(intent);
            }
        });


        loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailtext, passwordtext;

                emailtext = email.getText().toString();
                passwordtext = password.getText().toString();

                if(emailtext.isEmpty() || passwordtext.isEmpty()){
                    Toast.makeText(LoginActivity.this, "your fildes is empty", Toast.LENGTH_LONG).show();
                }
                else {
                   Mrogress.setTitle("Login your account");
                   Mrogress.setMessage("Please wait we are checking your email and password");
                   Mrogress.setCanceledOnTouchOutside(false);
                   Mrogress.show();
                    Mauth.signInWithEmailAndPassword(emailtext, passwordtext).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if(task.isSuccessful()){
                                Mrogress.dismiss();
                                Toast.makeText(LoginActivity.this, "Login success", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                startActivity(intent);
                            }
                            else {
                                Mrogress.dismiss();
                                String errormessge = task.getException().getMessage();
                                Toast.makeText(LoginActivity.this, errormessge, Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });
    }

}
