package com.foodwastemanagement;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.foodwastemanagement.Adapters.ResFoodRequestRecyclerAdapter;
import com.foodwastemanagement.Adapters.ResFoodRequestHistoryRecyclerAdapter;
import com.foodwastemanagement.Model.Event;
import com.foodwastemanagement.Utility.PrefManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ResturentHistory extends AppCompatActivity implements SearchView.OnQueryTextListener{
    private static final String TAG = "ActivityEventList";
    RecyclerView recyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    RecyclerView.Adapter adapter;
    FirebaseFirestore myDB;
    ArrayList<Event> datalist;
    Event evModel;
    MenuItem itemCount;
    PrefManager pref;
    boolean doubleBackToExitPressedOnce = false;
    TextView tvTitle,tvSubTitle,tvAll,tvPending,tvAccepted,tvConfirm;
    ImageView imgResturent;
    Intent iRes;
    String userType,userID;
    ProgressDialog pBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.resturent_food_history);
        pref = new PrefManager(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("History");
        setSupportActionBar(toolbar);
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        pBar=new ProgressDialog(this);
        recyclerView = (RecyclerView) findViewById(R.id.recycleview);
        datalist = new ArrayList<Event>();
        tvTitle=findViewById(R.id.tv_res_title);
        tvSubTitle=findViewById(R.id.tv_res_subtitle);
        imgResturent=findViewById(R.id.img_banner);
        tvAll=findViewById(R.id.tv_all);
        tvPending=findViewById(R.id.tv_pending);
        tvAccepted=findViewById(R.id.tv_accepted);
        tvConfirm=findViewById(R.id.tv_confirm);
        tvAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datalist.clear();
                adapter.notifyDataSetChanged();
                tvAll.setBackgroundColor(Color.parseColor("#2CA831"));
                tvPending.setBackgroundColor(Color.parseColor("#ffffff"));
                tvAccepted.setBackgroundColor(Color.parseColor("#ffffff"));
                tvConfirm.setBackgroundColor(Color.parseColor("#ffffff"));
                getResult("");
            }
        });
        tvAccepted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datalist.clear();
                adapter.notifyDataSetChanged();
                tvAll.setBackgroundColor(Color.parseColor("#ffffff"));
                tvPending.setBackgroundColor(Color.parseColor("#ffffff"));
                tvAccepted.setBackgroundColor(Color.parseColor("#2CA831"));
                tvConfirm.setBackgroundColor(Color.parseColor("#ffffff"));
                getResult("AU");
            }
        });
tvPending.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        datalist.clear();
        adapter.notifyDataSetChanged();
        tvAll.setBackgroundColor(Color.parseColor("#ffffff"));
        tvPending.setBackgroundColor(Color.parseColor("#2CA831"));
        tvAccepted.setBackgroundColor(Color.parseColor("#ffffff"));
        tvConfirm.setBackgroundColor(Color.parseColor("#ffffff"));
        getResult("A");

    }
});
        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datalist.clear();
                adapter.notifyDataSetChanged();
                tvAll.setBackgroundColor(Color.parseColor("#ffffff"));
                tvPending.setBackgroundColor(Color.parseColor("#ffffff"));
                tvAccepted.setBackgroundColor(Color.parseColor("#ffffff"));
                tvConfirm.setBackgroundColor(Color.parseColor("#2CA831"));
                getResult("C");
            }
        });
        myDB = FirebaseFirestore.getInstance();
        iRes=getIntent();
        userType = iRes.getStringExtra("utype");
        userID = iRes.getStringExtra("userid");
        if(userID!=null && !userID.equals("")){

            tvAll.setBackgroundColor(Color.parseColor("#2CA831"));
            DocumentReference docRef = myDB.collection("ResturentReg").document(userID);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot doc = task.getResult();
                        tvTitle.setText(doc.getData().get("uLastName").toString());
                        tvSubTitle.setText("( "+doc.getData().get("uAddress").toString()+" )");

                        if(!doc.getData().get("uImage").toString().equals("")){
                            Glide.with(ResturentHistory.this)
                                    .load(doc.getData().get("uImage").toString())
                                    .into(imgResturent);
                        }
                    }

                }
            });

            getResult("");

        }




    }

   void getResult(String keySearch){
        if(keySearch.equals("")){
            pBar.setMessage("Data Loading Progress...");
            pBar.setCancelable(false);
            pBar.show();
            myDB.collection("Event").whereEqualTo("eResID", userID)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot doc : task.getResult()) {
                                        evModel = new Event();
                                        evModel.seteDate(doc.getData().get("eDate").toString());
                                        evModel.seteType(doc.getData().get("eType").toString());
                                        evModel.seteTime(doc.getData().get("eTime").toString());
                                        evModel.seteCount(doc.getData().get("eCount").toString());
                                        evModel.seteMsg(doc.getData().get("eMessage").toString());
                                        evModel.seteId(doc.getData().get("eId").toString());
                                        evModel.seteStatus(doc.getData().get("eStatus").toString());
                                        evModel.seteRemark(doc.getData().get("eRemark").toString());
                                        evModel.seteUserID(doc.getData().get("eUserID").toString());
                                        evModel.seteResID(doc.getData().get("eResID").toString());
                                        evModel.seteImage(doc.getData().get("eImage").toString());
                                        datalist.add(evModel);

                                    Log.d(TAG, "#22 all datalist the data is being set");
                                }

                                if (datalist.size() >= 1) {
                                    recyclerView.setHasFixedSize(true);
                                    mLayoutManager = new LinearLayoutManager(ResturentHistory.this);
                                    recyclerView.setLayoutManager(mLayoutManager);
                                    Log.d(TAG, "#22 all datalist");
                                    adapter = new ResFoodRequestHistoryRecyclerAdapter(datalist, ResturentHistory.this);
                                    recyclerView.setAdapter(adapter);
                                    pBar.hide();
                                }else{
                                    pBar.hide();
                                }
                              /*  }

                            }*/
                            } else {
                                pBar.hide();
                                Log.d("111", "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }else {
            pBar.setMessage("Data Loading Progress...");
            pBar.setCancelable(false);
            pBar.show();
            myDB.collection("Event").whereEqualTo("eResID", userID)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot doc : task.getResult()) {
                                    if (doc.getData().get("eStatus").toString().equals(keySearch)) {


                                        evModel = new Event();
                                        evModel.seteDate(doc.getData().get("eDate").toString());
                                        evModel.seteType(doc.getData().get("eType").toString());
                                        evModel.seteTime(doc.getData().get("eTime").toString());
                                        evModel.seteCount(doc.getData().get("eCount").toString());
                                        evModel.seteMsg(doc.getData().get("eMessage").toString());
                                        evModel.seteId(doc.getData().get("eId").toString());
                                        evModel.seteStatus(doc.getData().get("eStatus").toString());
                                        evModel.seteRemark(doc.getData().get("eRemark").toString());
                                        evModel.seteUserID(doc.getData().get("eUserID").toString());
                                        evModel.seteResID(doc.getData().get("eResID").toString());
                                        evModel.seteImage(doc.getData().get("eImage").toString());
                                        datalist.add(evModel);
                                    }
                                    Log.d(TAG, "#22 all datalist the data is being set");
                                }

                                if (datalist.size() >= 1) {
                                    pBar.hide();
                                    recyclerView.setHasFixedSize(true);
                                    mLayoutManager = new LinearLayoutManager(ResturentHistory.this);
                                    recyclerView.setLayoutManager(mLayoutManager);
                                    Log.d(TAG, "#22 all datalist");
                                    adapter = new ResFoodRequestHistoryRecyclerAdapter(datalist, ResturentHistory.this);
                                    recyclerView.setAdapter(adapter);

                                }else{
                                    pBar.hide();
                                }
                              /*  }

                            }*/
                            } else {
                                pBar.hide();
                                Log.d("111", "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }
    }
    @Override
    public void onBackPressed() {
            finish();
    }



   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
       MenuInflater inflater = getMenuInflater();
       inflater.inflate(R.menu.menu_event, menu);
       final MenuItem item = menu.findItem(R.id.action_search);
       itemCount=menu.findItem(R.id.action_count);

       final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
       searchView.setOnQueryTextListener(this);
       MenuItemCompat.setOnActionExpandListener(item,
               new MenuItemCompat.OnActionExpandListener() {
                   @Override
                   public boolean onMenuItemActionCollapse(MenuItem item) {
                       // Do something when collapsed
                       // adapter.setFilter(mCountryModel);
                       return true; // Return true to collapse action view
                   }

                   @Override
                   public boolean onMenuItemActionExpand(MenuItem item) {
                       // Do something when expanded
                       return true; // Return true to expand action view
                   }
               });
       return true;
   }

    @Override
    public boolean onQueryTextChange(String newText) {

        final List<Event> filteredModelList = filter(datalist, newText);
        recyclerView.setLayoutManager(new LinearLayoutManager(ResturentHistory.this));
        adapter = new ResFoodRequestRecyclerAdapter((ArrayList<Event>) filteredModelList, ResturentHistory.this);
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
        itemCount.setTitle("("+filteredModelList.size() + ")");
        return true;
    }

    private List<Event> filter(List<Event> models, String query) {
        query = query.toLowerCase();
        final List<Event> filteredModelList = new ArrayList<>();
        for (Event model : models) {
            final String text = model.geteDate().toLowerCase();
            if (text.contains(query)) {

                filteredModelList.add(model);
            }

        }
        return filteredModelList;
    }
    private List<Event> saerchfilter(List<Event> models, String query) {
        query = query.toLowerCase();
        final List<Event> filteredModelList = new ArrayList<>();
        for (Event model : models) {
            final String text = model.geteStatus().toLowerCase();
            if (text.contains(query)) {

                filteredModelList.add(model);
            }

        }
        return filteredModelList;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.nav_logout:
                PrefManager pref=new PrefManager(ResturentHistory.this);

                if(!pref.getUserId().equals("")) {
                    Log.d("#111", "user data string is not empty" );
                    SharedPreferences pref1= getSharedPreferences("AppPref", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref1.edit();
                    editor.clear();
                    editor.commit();
                    Intent intent=new Intent(ResturentHistory.this,ActivityLogin.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();

                }
                else{
                    Log.d("#111", "user data string is empty" );
                }

            case R.id.nav_profile:
                Intent iProfile=new Intent(ResturentHistory.this,ResturentProfile.class);
                startActivity(iProfile);
                break;
            case R.id.nav_password:

                break;
            default:
                break;
        }
        return true;
    }









}