package eu.wanielista.notepadplus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import eu.wanielista.notepadplus.signin.Login;

public class DeleteAccount extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_account);

        Button confirmDelete = (Button) findViewById(R.id.delete_account);
        final EditText emailED = (EditText) findViewById(R.id.confirm_email);
        final EditText passwordED = (EditText) findViewById(R.id.confirm_password);

        confirmDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String email = emailED.getText().toString();
                final String password = passwordED.getText().toString();
                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                final FirebaseUser user = firebaseAuth.getCurrentUser();
                final String currentUID = getIntent().getStringExtra("uid");


                AuthCredential credential = EmailAuthProvider
                        .getCredential(email, password);

                user.reauthenticate(credential)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    //remove all user data
                                    FirebaseDatabase dat = FirebaseDatabase.getInstance();
                                    DatabaseReference ref = dat.getReference().child("notes").child(currentUID);
                                    DatabaseReference refTotalNotes = dat.getReference().child("TotalNotesCount").child(currentUID);
                                    ref.removeValue();
                                    refTotalNotes.removeValue();


                                    user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Toast.makeText(DeleteAccount.this, R.string.deleteSuccess, Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(DeleteAccount.this, Login.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(intent);
                                            } else {
                                                Toast.makeText(DeleteAccount.this, "Error", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                } else {
                                    Toast.makeText(DeleteAccount.this, "Wrong credentials", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }

        });

    }
}