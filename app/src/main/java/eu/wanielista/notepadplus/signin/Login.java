package eu.wanielista.notepadplus.signin;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import eu.wanielista.notepadplus.MainActivity;
import eu.wanielista.notepadplus.R;

public class Login extends AppCompatActivity {

    Button login_btn, register_b;
    EditText mail_edit, pwd_edit;
    FirebaseAuth firebaseAuth;
    String useremail, userpassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try
        {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException e){}
        setContentView(R.layout.activity_login);


        login_btn = findViewById(R.id.login_btn);
        register_b = findViewById(R.id.register_btn);
        mail_edit = findViewById(R.id.email_input);
        pwd_edit = findViewById(R.id.pass_input);
        firebaseAuth = FirebaseAuth.getInstance();


        //autologin if user is logged
        if (firebaseAuth.getCurrentUser() != null) {
            startActivity(new Intent(Login.this, MainActivity.class));
            finish();
        }

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //login user
                useremail = mail_edit.getText().toString();
                userpassword = pwd_edit.getText().toString();

                if(!TextUtils.isEmpty(useremail) && !TextUtils.isEmpty(userpassword)) {
                    firebaseAuth.signInWithEmailAndPassword(useremail,userpassword)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    if (!task.isSuccessful()) {
                                        Toast.makeText(Login.this, "Auth failed", Toast.LENGTH_SHORT).show();

                                    } else {
                                        Toast.makeText(Login.this, "Auth success", Toast.LENGTH_SHORT).show();
                                        Login.this.startActivity(new Intent(Login.this, MainActivity.class));
                                        Login.this.finish();
                                    }
                                }
                            });
                } else {
                    Toast.makeText(Login.this, "Auth failed, No data in fields", Toast.LENGTH_SHORT).show();
                }
            }
        });

        register_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, Register.class);
                startActivity(intent);
            }
        });


    }
}
