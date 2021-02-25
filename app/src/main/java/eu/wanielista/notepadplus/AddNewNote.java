package eu.wanielista.notepadplus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class AddNewNote extends AppCompatActivity {

    Button add_btn;
    EditText title_edit, content_edit;
    long max_id;

    int total;


    String currentUserUID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_note);

        add_btn = findViewById(R.id.save_edited_note);
        title_edit = findViewById(R.id.new_note_title);
        content_edit = findViewById(R.id.new_note_content);
        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reff = FirebaseDatabase.getInstance().getReference();
        reff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                max_id = (dataSnapshot.child("notes").child(currentUserUID).getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //get user uid
        if (user != null) {
            currentUserUID =user.getUid();
        }

        Calendar calendar = Calendar.getInstance();
        final String currentDate = DateFormat.getDateInstance().format(calendar.getTime());
        totalNotesCount();
        setTotal(getTotal() + 1);


        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //setTotal(getTotal());

                final DatabaseReference databaseReference;
                final DatabaseReference databaseReferenceTotalCount;
                String title = title_edit.getText().toString().trim();
                String contnent = content_edit.getText().toString().trim();
                if(!TextUtils.isEmpty(title) && !TextUtils.isEmpty(contnent)){
                    databaseReference = database.getReference().child("notes").child(currentUserUID).child(String.valueOf(max_id));
                    databaseReferenceTotalCount = database.getReference().child("TotalNotesCount").child(currentUserUID).child("Total");
                    HashMap<String,String> note=new HashMap<>();
                    note.put("Title",title);
                    note.put("Content",contnent);
                    note.put("Time",currentDate);
                    databaseReference.setValue(note);


                    databaseReferenceTotalCount.setValue(getTotal());

                    Toast.makeText(AddNewNote.this, "Note added succesfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(AddNewNote.this, MainActivity.class);
                    startActivity(intent);

                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), "Write something", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
    }

    private void totalNotesCount() {

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference;
        databaseReference = firebaseDatabase.getReference().child("TotalNotesCount").child(currentUserUID).child("Total");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    setTotal((Integer.parseInt(dataSnapshot.getValue().toString()))+1);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AddNewNote.this, "DB Error", Toast.LENGTH_SHORT);
            }
        });

    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }




}
