package com.foodwastemanagement;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.foodwastemanagement.Utility.PrefManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class ResturentAddFoodRequest extends AppCompatActivity implements  View.OnClickListener {
EditText etPickDate,etPickTime,etFoodType,etFoodCount,etMessage;
Toolbar toolbar;
Intent intent;

    TextView tvDatee,tvTimee;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private final static String TAG = "ActivityAddEvent";
    FirebaseFirestore mydb;
    Button save, Delete, Cancel, SaveEdit;
    LinearLayout linearLayoutAdd, linearLayoutEdit;
    String Typ,prodID,strDate,strFoodtype,strPicktime,strFoodCount,ProductMessage;
    PrefManager pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.resturent_add_food_request);
        mydb = FirebaseFirestore.getInstance();
        pref = new PrefManager(this);
        Log.d(TAG,"#033 FirebaseFirestore");
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        /*edit text initalization*/
        etPickDate = (EditText) findViewById(R.id.et_pick_date);
        etPickTime = (EditText) findViewById(R.id.et_pick_time);
        etFoodType = (EditText) findViewById(R.id.et_food_type);
        etFoodCount = (EditText) findViewById(R.id.et_food_count);
        etMessage = (EditText) findViewById(R.id.et_msg);

        Delete = (Button) findViewById(R.id.dl);
        SaveEdit = (Button) findViewById(R.id.sv);
        save = (Button) findViewById(R.id.savv);
        Cancel = (Button) findViewById(R.id.cncl);
        linearLayoutEdit = findViewById(R.id.edititem);
        linearLayoutAdd = findViewById(R.id.newitem);

/*textview initalisation*/
        tvDatee = (TextView) findViewById(R.id.tv_date);
        tvTimee = (TextView) findViewById(R.id.tv_time);
        tvDatee.setOnClickListener(this);
        tvTimee.setOnClickListener(this);


        intent = getIntent();
        Log.d(TAG,"#033 intent");
            setSupportActionBar(toolbar);
            setTitle("Food Request");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ResturentAddFoodRequest.this, ResturentDashboard.class);
                startActivity(i);
            }
        });

        Log.d(TAG,"#033 FirebaseFirestore");
         Typ = intent.getStringExtra("type");
        if (Typ.equals("add_data")) {
            setSupportActionBar(toolbar);
            setTitle("Add Event");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            linearLayoutAdd.setVisibility(View.VISIBLE);
            linearLayoutEdit.setVisibility(LinearLayout.GONE);
            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
/*validation check status*/
                    if (etPickDate.getText().length() == 0|| etFoodType.getText().length() == 0|| etPickTime.getText().length() == 0||etFoodCount.getText().length()==0 ||etMessage.getText().length()==0){
                        etPickDate.setError(" Required.");
                        etFoodType.setError(" Required.");
                        etPickTime.setError(" Required.");
                        etFoodCount.setError(" Required.");
                        etMessage.setError(" Required.");
                        Log.d(TAG,"#033 Required");
                    }





                    else {
                        //inset into firestore database....
                        String inputString = etPickDate.getText().toString();
                        String inputString1 = etPickTime.getText().toString();
                        String inputString2 = etFoodType.getText().toString();
                        String inputString3 = etFoodCount.getText().toString();
                        String inputString4 = etMessage.getText().toString();

                        Log.d(TAG,"#033 fistwordintent");
                        Date date = new Date();
                        String dateTime = DateUtils.formatDateTime(ResturentAddFoodRequest.this, date.getTime(), DateUtils.FORMAT_NUMERIC_DATE);
                        String time = DateUtils.formatDateTime(ResturentAddFoodRequest.this, date.getTime(), DateUtils.FORMAT_SHOW_TIME);
                        String finalString = inputString + dateTime +  time;
                        finalString = finalString.replaceAll("[^a-zA-Z0-9]", "");
                        Log.d("#033 ", "Final string " + finalString);
                        Map<String, Object> user = new HashMap<>();
                        user.put("eId", finalString);
                        user.put("eImage", "");
                        user.put("eDate", etPickDate.getText().toString());
                        user.put("eStatus", "W");
                        user.put("eOrder", "1");
                        user.put("e_id", "");
                        user.put("eType", inputString2);
                        user.put("eTime",etPickTime.getText().toString());
                        user.put("eCount", inputString3);
                        user.put("eMessage", inputString4);
                        user.put("eRemark", "");
                        user.put("eResID", pref.getUserId());
                        user.put("eUserID", "");
                        user.put("eEmpName", "");
                        user.put("eEmpMobile", "");
                        user.put("eEmpIDProof", "");
                        user.put("eEmpMsg", "");
                        user.put("eNgoToken", "");
                        user.put("eResToken", "");
                        user.put("eCdate", FieldValue.serverTimestamp());

                        mydb.collection("Event").document(finalString).set(user)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(ResturentAddFoodRequest.this, "Upload successfully", Toast.LENGTH_LONG).show();
                                        etPickDate.setText("");
                                        etPickTime.setText("");
                                        etFoodType.setText("");
                                        etFoodCount.setText("");
                                        etMessage.setText("");
                                        Intent i = new Intent(ResturentAddFoodRequest.this, ResturentDashboard.class);
                                        startActivity(i);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w("111", "Error adding document", e);
                                    }
                                });
                        Cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent i = new Intent(ResturentAddFoodRequest.this, ResturentDashboard.class);
                                startActivity(i);

                            }
                        });
                    } Log.d(TAG,"#033 Cancel");
                }
            });

        }


        else if (Typ.equals("edit")) {
            linearLayoutEdit.setVisibility(LinearLayout.VISIBLE);
            linearLayoutAdd.setVisibility(View.GONE);
            setSupportActionBar(toolbar);
            setTitle("Edit Event");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            prodID = intent.getStringExtra("foodid");
            strDate = intent.getStringExtra("date");
            etPickDate.setText(strDate);
            Log.d("#033","date");
            strFoodtype = intent.getStringExtra("foodtype");
            etFoodType.setText(strFoodtype);
            Log.d("#033","FoodType"+strFoodtype);

            strPicktime = intent.getStringExtra("time");
            etPickTime.setText(strPicktime);
            Log.d("#033","etPickTime");
            strFoodCount = intent.getStringExtra("foodcount");
            etFoodCount.setText(strFoodCount);
            Log.d("#033","count"+strFoodCount);
            ProductMessage = intent.getStringExtra("message");
            etMessage.setText(ProductMessage);

            Delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mydb.collection("Event").document(prodID).delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Intent i = new Intent(ResturentAddFoodRequest.this, ResturentDashboard.class);
                                    startActivity(i);
                                }


                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "#033 Error adding document", e);
                                }
                            });

                }
            });

            SaveEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    mydb.collection("Event")
                            .document(prodID).update("eDate", etPickDate.getText().toString(),"eTime",etPickTime.getText().toString(),"eType",etFoodType.getText().toString(),"eCount",etFoodCount.getText().toString(),"eMessage",etMessage.getText().toString())


                            .addOnSuccessListener(new OnSuccessListener<Void>() {

                                @Override
                                public void onSuccess(Void aVoid) {
                                    Intent i = new Intent(ResturentAddFoodRequest.this, ResturentDashboard.class);
                                    startActivity(i);
                                }


                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "#033 Error adding document", e);
                                }
                            });


                }
            });


        }



        }



               @Override
                public void onBackPressed() {
                    Intent i = new Intent(ResturentAddFoodRequest.this, ResturentDashboard.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                    finish();
                }

    @Override
    public void onClick(View v) {

        if (v == tvDatee) {

            // Get Current Date
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);


            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {

                            etPickDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
        }
        if (v == tvTimee) {

            // Get Current Time
            final Calendar c = Calendar.getInstance();
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);

            // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {

                            etPickTime.setText(hourOfDay + ":" + minute);
                        }
                    }, mHour, mMinute, false);
            timePickerDialog.show();
        }
    }
}


