package eu.wanielista.notepadplus.signin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import eu.wanielista.notepadplus.MainActivity;
import eu.wanielista.notepadplus.R;

public class Register extends AppCompatActivity {
    Button register_btn;
    EditText mail_edit, pwd_edit, confirm_pwd_edit;
    FirebaseAuth firebaseAuth;
    String userEmail, userPassword, confirmPassword;
    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try
        {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException e){}
        setContentView(R.layout.activity_register);

        register_btn = (Button) findViewById(R.id.register_btn);
        mail_edit = (EditText) findViewById(R.id.email_input);
        pwd_edit = (EditText) findViewById(R.id.pass_input);
        confirm_pwd_edit = (EditText) findViewById(R.id.confirm_pass_input);
        firebaseAuth = FirebaseAuth.getInstance();


        mDatabase = FirebaseDatabase.getInstance().getReference("users");

        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //register user
                userEmail = mail_edit.getText().toString();
                userPassword = pwd_edit.getText().toString();
                confirmPassword = confirm_pwd_edit.getText().toString();

                if(userPassword.equals(confirmPassword)){
                    firebaseAuth.createUserWithEmailAndPassword(userEmail,userPassword)
                            .addOnCompleteListener(Register.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (!task.isSuccessful()) {
                                        Log.d("E:", "error: "  + task.getException());
                                        Toast toast = Toast.makeText(getApplicationContext(), "error:"+ task.getException(), Toast.LENGTH_SHORT);
                                        toast.show();
                                    } else {

                                        Toast toast = Toast.makeText(getApplicationContext(), "Registration succesfull", Toast.LENGTH_SHORT);
                                        toast.show();




                                        Register.this.startActivity(new Intent(Register.this, MainActivity.class));
                                        Register.this.finish();
                                    }
                                }
                            });
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), R.string.passwordNotMatch, Toast.LENGTH_SHORT);
                    toast.show();
                }


            }
        });
    }

}
