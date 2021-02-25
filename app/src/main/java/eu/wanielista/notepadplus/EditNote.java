package eu.wanielista.notepadplus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.HashMap;

import eu.wanielista.notepadplus.adapter.RecyclerAdapter;

public class EditNote extends AppCompatActivity {


    Button save_btn, delete_btn;
    EditText title_edit, content_edit;
    int noteID, lastID;

    String currentUserUID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        save_btn = findViewById(R.id.save_edited_note);
        delete_btn = findViewById(R.id.delete_note);
        title_edit = findViewById(R.id.new_note_title);
        content_edit = findViewById(R.id.new_note_content);
        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        String title = getIntent().getStringExtra("title");
        final String note = getIntent().getStringExtra("note");
        String idString = getIntent().getStringExtra("noteID");
        final int id = Integer.parseInt(idString);
        title_edit.setText(title);
        content_edit.setText(note);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        //get user uid
        if (user != null)  currentUserUID =user.getUid();



        Calendar calendar = Calendar.getInstance();
        final String currentDate = DateFormat.getDateInstance().format(calendar.getTime());

        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatabaseReference databaseReference;
                String title = title_edit.getText().toString().trim();
                String contnent = content_edit.getText().toString().trim();
                if(!TextUtils.isEmpty(title) && !TextUtils.isEmpty(contnent)){
                    databaseReference = database.getReference().child("notes").child(currentUserUID).child(String.valueOf(id));
                    HashMap<String,String> note=new HashMap<>();
                    note.put("Title",title);
                    note.put("Content",contnent);
                    note.put("Time",currentDate);
                    databaseReference.setValue(note);

                    Toast.makeText(EditNote.this, "Note udpated succesfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(EditNote.this, MainActivity.class);
                    startActivity(intent);

                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), "Write something", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
        noteID = Integer.valueOf(getIntent().getStringExtra("noteID"));
        lastID = Integer.valueOf(getIntent().getStringExtra("lastID"));


        delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference databaseReference;

                if(noteID != lastID){


                    String newTitle = getIntent().getStringExtra("titleLast");
                    String newNote = getIntent().getStringExtra("noteLast");
                    String newDate = getIntent().getStringExtra("dateLast");


                    databaseReference = database.getReference().child("notes").child(currentUserUID).child(String.valueOf(noteID));
                    HashMap<String,String> note=new HashMap<>();
                    note.put("Title",newTitle);
                    note.put("Content",newNote);
                    note.put("Time",newDate);
                    databaseReference.setValue(note);

                    databaseReference = database.getInstance().getReference().child("notes").child(currentUserUID).child(String.valueOf(lastID));
                    databaseReference.removeValue();

                    Toast.makeText(EditNote.this, "Note deleted succesfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(EditNote.this, MainActivity.class);
                    startActivity(intent);

                } else {
                    //it works well

                    databaseReference = database.getInstance().getReference().child("notes").child(currentUserUID).child(String.valueOf(lastID));
                    databaseReference.removeValue();

                    Toast.makeText(EditNote.this, "Note deleted succesfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(EditNote.this, MainActivity.class);
                    startActivity(intent);

                }
            }
        });
    }
}
