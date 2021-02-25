package eu.wanielista.notepadplus;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import eu.wanielista.notepadplus.adapter.RecyclerAdapter;
import eu.wanielista.notepadplus.signin.Login;


public class MainActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private String currentUID;
    private FirebaseUser user;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private DatabaseReference myRef;
    private RecyclerView recyclerView;
    private InterstitialAd interstitialAd;

    private long max_id;
    String[] items;
    String[] itemsDesc;
    String[] itemsDescShow;
    String[] itemsDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddNewNote.class);
                startActivity(intent);
                

            }
        });

        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null){
            Intent intent = new Intent(MainActivity.this, Login.class);
            startActivity(intent);
        }
        firebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        getUserNotes();

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {

            }
        });
        AdView adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(getString(R.string.fullscreen_key));
        interstitialAd.loadAd(adRequest);

    }

    private void showInterstital(){
        if(interstitialAd.isLoaded()){
            interstitialAd.show();
        }
    }

    private void getUserNotes() {
        //get user uid
        if (user != null) {
            currentUID =user.getUid();
        }


        myRef = firebaseDatabase.getReference().child("notes").child(currentUID);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                    setMax_id((dataSnapshot.getChildrenCount()));
                showData(dataSnapshot);
            }

            private void showData(DataSnapshot dataSnapshot) {
                int arraySize = (int)getMax_id();

                items = new String[arraySize];
                itemsDesc = new String[arraySize];
                itemsDescShow = new String[arraySize];
                itemsDate = new String[arraySize];
                for (int i = 0; i < getMax_id(); i++) {
                    items[i] = dataSnapshot.child(String.valueOf(i)).child("Title").getValue().toString();
                    itemsDesc[i] = dataSnapshot.child(String.valueOf(i)).child("Content").getValue().toString();
                    //cut string length to display on main screen
                    if(itemsDesc[i].length() <= 20){
                        itemsDescShow[i] = dataSnapshot.child(String.valueOf(i)).child("Content").getValue().toString();
                    } else {
                        itemsDescShow[i] = dataSnapshot.child(String.valueOf(i)).child("Content").getValue().toString().substring(0,20) + "...";
                    }
                    itemsDate[i] = dataSnapshot.child(String.valueOf(i)).child("Time").getValue().toString();
                    fillRecyclerView();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "DB Error", Toast.LENGTH_SHORT);
            }
        });



    }

    private void fillRecyclerView() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new RecyclerAdapter(this, items, itemsDesc,itemsDescShow, itemsDate));
    }

    @Override
    public void onStart() {
        super.onStart();
//        firebaseAuth.addAuthStateListener(mAuthStateListener);
    }
    @Override
    public void onStop() {
        super.onStop();
        if(mAuthStateListener != null) {
            firebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.top_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.profile_info:
                showInterstital();
                Intent about = new Intent(MainActivity.this,  AboutUser.class);
                about.putExtra("max_id",String.valueOf(max_id));
                about.putExtra("userUID", currentUID);
                startActivity(about);
                return true;
            case R.id.logout:
                    FirebaseAuth.getInstance().signOut();
                    Toast.makeText(this, "Logout successfully", Toast.LENGTH_SHORT).show();
                    Intent login = new Intent(MainActivity.this, Login.class);
                    login.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(login);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public long getMax_id() {
        return max_id;
    }

    public void setMax_id(long max_id) {
        this.max_id = (int)max_id;
    }

}
