package com.foodwastemanagement;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.foodwastemanagement.Adapters.ResFoodRequestRecyclerAdapter;
import com.foodwastemanagement.Model.Event;
import com.foodwastemanagement.Utility.PrefManager;

import java.util.ArrayList;
import java.util.List;
public class ResturentDashboard extends AppCompatActivity implements SearchView.OnQueryTextListener{
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
    TextView tvTitle,tvSubTitle,tvResList;
    ImageView imgResturent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.resturent_dashboard);
        pref = new PrefManager(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Dashboard Restaurant");
        setSupportActionBar(toolbar);
        recyclerView = (RecyclerView) findViewById(R.id.recycleview);
        datalist = new ArrayList<Event>();
        tvTitle=findViewById(R.id.tv_res_title);
        tvSubTitle=findViewById(R.id.tv_res_subtitle);
        imgResturent=findViewById(R.id.img_banner);

        tvResList=findViewById(R.id.tv_res_list);

        tvResList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iResList=new Intent(ResturentDashboard.this,ResList.class);
                startActivity(iResList);
            }
        });
        myDB = FirebaseFirestore.getInstance();
        DocumentReference docRef = myDB.collection("ResturentReg").document(pref.getUserId());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {

                    DocumentSnapshot doc = task.getResult();
                    tvTitle.setText(doc.getData().get("uLastName").toString());
                    tvSubTitle.setText("( "+doc.getData().get("uAddress").toString()+" )");

                    if(!doc.getData().get("uImage").toString().equals("")){
                        Glide.with(ResturentDashboard.this)
                                .load(doc.getData().get("uImage").toString())
                                .into(imgResturent);
                    }
                }

            }
        });

        imgResturent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iImage = new Intent(ResturentDashboard.this, ResProductImg.class);
                startActivity(iImage);
            }
        });

        myDB.collection("Event").whereEqualTo("eResID",pref.getUserId())
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
                                evModel.seteEmpName(doc.getData().get("eEmpName").toString());
                                evModel.seteEmpMobile(doc.getData().get("eEmpMobile").toString());
                                evModel.seteEmpIDProof(doc.getData().get("eEmpIDProof").toString());
                                evModel.seteEmpMsg(doc.getData().get("eEmpMsg").toString());
                                evModel.seteNgoToken(doc.getData().get("eNgoToken").toString());
                                evModel.seteResToken(doc.getData().get("eResToken").toString());
                                datalist.add(evModel);
                                Log.d(TAG, "#22 all datalist the data is being set");
                            }

                            if(datalist.size()>=1) {
                                recyclerView.setHasFixedSize(true);
                                mLayoutManager = new LinearLayoutManager(ResturentDashboard.this);
                                recyclerView.setLayoutManager(mLayoutManager);
                                Log.d(TAG, "#22 all datalist");
                                adapter = new ResFoodRequestRecyclerAdapter(datalist, ResturentDashboard.this);
                                recyclerView.setAdapter(adapter);
                            }
                              /*  }

                            }*/
                        } else {
                            Log.d("111", "Error getting documents: ", task.getException());
                        }
                    }
                });


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ResturentDashboard.this, ResturentAddFoodRequest.class);
                intent.putExtra("type", "add_data");
                startActivity(intent);
            }
        });

    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {

            super.onBackPressed();
            finish();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
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
        recyclerView.setLayoutManager(new LinearLayoutManager(ResturentDashboard.this));
        adapter = new ResFoodRequestRecyclerAdapter((ArrayList<Event>) filteredModelList, ResturentDashboard.this);
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

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.nav_logout:
                PrefManager pref=new PrefManager(ResturentDashboard.this);

                if(!pref.getUserId().equals("")) {
                    Log.d("#111", "user data string is not empty" );
                    SharedPreferences pref1= getSharedPreferences("AppPref", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref1.edit();
                    editor.clear();
                    editor.commit();
                    Intent intent=new Intent(ResturentDashboard.this,ActivityLogin.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();

                }
                else{
                    Log.d("#111", "user data string is empty" );
                }

            case R.id.nav_profile:
                Intent iProfile=new Intent(ResturentDashboard.this,ResturentProfile.class);
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