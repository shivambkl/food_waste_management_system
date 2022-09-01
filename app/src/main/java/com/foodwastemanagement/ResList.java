package com.foodwastemanagement;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.foodwastemanagement.Adapters.ResListRecyclerAdapter;
import com.foodwastemanagement.Model.User_Accounts;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class ResList extends AppCompatActivity {
    private static final String TAG = "ActivityUserlist";
    RecyclerView recyclerView;
    String Typ;
    RecyclerView.LayoutManager mLayoutManager;
    RecyclerView.Adapter adapter;
    FirebaseFirestore myDB;
    ArrayList<User_Accounts> datalist;
   // Intent i;
    User_Accounts uaList;
    private ProgressBar mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.res_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.recycleview);
        datalist = new ArrayList<User_Accounts>();

        myDB = FirebaseFirestore.getInstance();
        myDB.collection("ResturentReg")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot doc : task.getResult()) {

                                uaList = new User_Accounts();
                                uaList.setUsername(doc.getData().get("uUsername").toString());
                                uaList.setPassword(doc.getData().get("uPassword").toString());
                                uaList.setFirstName(doc.getData().get("uFirstName").toString());
                                uaList.setLastName(doc.getData().get("uLastName").toString());
                                uaList.setAddress(doc.getData().get("uAddress").toString());
                                uaList.setMobile(doc.getData().get("uMobile").toString());
                                uaList.setEmail(doc.getData().get("uEmail").toString());
                                uaList.setUserImage(doc.getData().get("uImage").toString());
                                uaList.setUType(doc.getData().get("uType").toString());

                                datalist.add(uaList);
                                Log.d(TAG, "#22 all datalist the data is being set");
                            }

                         //   Toast.makeText(getApplicationContext(), "data is  there", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "#22 order_items is greatet than one " + datalist.size());
                            if(datalist.size()>=1) {
                                recyclerView.setHasFixedSize(true);
                                mLayoutManager = new LinearLayoutManager(ResList.this);
                                recyclerView.setLayoutManager(mLayoutManager);
                                adapter = new ResListRecyclerAdapter(datalist, ResList.this);
                                recyclerView.setAdapter(adapter);
                            }

                        } else {
                            Log.d("111", "Error getting documents: ", task.getException());
                        }
                    }
                });




    }

    @Override
    public void onBackPressed() {
        finish();
    }



}