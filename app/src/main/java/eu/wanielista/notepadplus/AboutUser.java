package eu.wanielista.notepadplus;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class AboutUser extends AppCompatActivity {

    private FirebaseDatabase database;
    private DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_user);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        toolBarLayout.setTitle(getTitle());

        TextView userEmail = (TextView) findViewById(R.id.useremail);
        TextView currentCountOfNotes = (TextView) findViewById(R.id.current_notes_count_TV);
        final TextView totalCountOfNotes = (TextView) findViewById(R.id.total_notes_count_TV);
        Button deleteAllNotesBtn = (Button) findViewById(R.id.delete_all_notes);
        Button deleteAccountBtn = (Button) findViewById(R.id.delete_account);


        userEmail.setText(getUserEmail());
        currentCountOfNotes.setText(getIntent().getStringExtra("max_id"));
        final String currentUID = getIntent().getStringExtra("userUID");


        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

        databaseReference = firebaseDatabase.getReference().child("TotalNotesCount").child(currentUID).child("Total");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    totalCountOfNotes.setText(dataSnapshot.getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AboutUser.this, "DB Error", Toast.LENGTH_SHORT);
            }
        });

        deleteAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch(i){
                            case DialogInterface.BUTTON_POSITIVE:
                                Intent intent = new Intent(AboutUser.this, DeleteAccount.class);
                                intent.putExtra("uid", currentUID);
                                startActivity(intent);
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                Toast.makeText(AboutUser.this, "Uffff :)", Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(AboutUser.this);
                builder.setMessage(R.string.areYouSureDeleteAccount).setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
            }
        });

        deleteAllNotesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch(i){
                            case DialogInterface.BUTTON_POSITIVE:
                                FirebaseDatabase dat = FirebaseDatabase.getInstance();
                                DatabaseReference ref = dat.getReference().child("notes").child(currentUID);
                                ref.removeValue();
                                Intent intent = new Intent(AboutUser.this, MainActivity.class);
                                startActivity(intent);


                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                Toast.makeText(AboutUser.this, R.string.greatDay, Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(AboutUser.this);
                builder.setMessage(R.string.areYouSureDeleteNotes).setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
            }
        });

    }
    
    String getUserEmail() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser != null){
            return firebaseUser.getEmail();
        }
        return "No user logged";
    }


}