package com.foodwastemanagement;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.foodwastemanagement.Utility.PrefManager;

import java.util.ArrayList;
import java.util.List;


public class ResturentLoginActivity extends AppCompatActivity {
    TextInputLayout ilayusername,ilaypassword;
    EditText username, password;
    ConnectionDetector cd;
    Boolean isInternetPresent = false;
    TextView LoginBtn, SignupBtn, forgetPassword;
    Button btnLogin;
    ProgressDialog pBar;
    TextView tvUserSignUp,tvResturentSignUp;
    private FirebaseAuth firebaseAuth;
    FirebaseFirestore myDB;
    PrefManager pref;
    String[] permissions = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.MANAGE_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET
    };
    private final static String TAG = "Login User";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.resturent_login);
        tvUserSignUp=findViewById(R.id.tv_usersignup);
        tvResturentSignUp=findViewById(R.id.res_create_account);
        btnLogin=findViewById(R.id.btn_login);

        pref = new PrefManager(ResturentLoginActivity.this);
        myDB = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        checkPermissions();
        cd=new ConnectionDetector(this);
        pBar=new ProgressDialog(this);
        initialise();



        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(username.getText().equals("")){
                    ilayusername.setError("Enter Username");
                    return;
                }

                isInternetPresent = cd.isConnectingToInternet();
                if (isInternetPresent) {

                    pBar.setMessage("Forget Password in Progress...");
                    pBar.setCancelable(false);
                    pBar.show();
                    myDB = FirebaseFirestore.getInstance();
                    DocumentReference docRef = myDB.collection("ResturentReg").document(username.getText().toString());
                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    pBar.hide();
                                    Log.d("#00", " document exist " + document.getId().toString());
                                    Log.d("#00", "DocumentSnapshot data: " + document.getData());
                                    if (document.getData().get("uUsername").toString().equals(username.getText().toString())) {
                                        Log.d("#00", " the exist ");
                                    /*    Intent forget = new Intent(ResturentLoginActivity.this, ActivityPhoneAuthForget.class);
                                        forget.putExtra("type", "forget");
                                        forget.putExtra("phone", username.getText().toString());
                                        startActivity(forget);*/


                                    } else {
                                        Log.d("#00", "Not exist");
                                        Toast.makeText(ResturentLoginActivity.this, "Number Not registered", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    pBar.hide();
                                    Log.d("#00", "No such document");
                                    Toast.makeText(ResturentLoginActivity.this, "Number Not registered", Toast.LENGTH_SHORT).show();


                                }


                            }else{
                                pBar.hide();
                            }
                        }
                    });

                }else{
                    showInternetAlert();
                }
            }
        });
        tvResturentSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent forget = new Intent(ResturentLoginActivity.this, ResturentRegistrationActivity.class);
                startActivity(forget);
            }
        });
        username.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
                Is_Valid_Email(username);
            }
        });
        password.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
                Is_Valid_Password(password);
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (validateLogin()) {
                    isInternetPresent = cd.isConnectingToInternet();
                    if (isInternetPresent) {
                        pBar.setMessage("Login in Progress...");
                        pBar.setCancelable(false);
                        pBar.show();
                        String strEmail = username.getText().toString().trim();
                        String strPassword = password.getText().toString().trim();
                        login(strEmail,strPassword);
              /*  myDB = FirebaseFirestore.getInstance();
                myDB.collection("ResturentReg")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot doc : task.getResult()) {
                                        Log.d("#44", "uername P" + doc.getData().get("uUsername").toString());
                                        Log.d("#44", "password P" + doc.getData().get("uPassword").toString());
                                        if (doc.getData().get("uUsername").toString().equals(username.getText().toString()) && doc.getData().get("uPassword").toString().equals(password.getText().toString())) {

                                            pref.setUserId(username.getText().toString());
                                            pref.setUserType("Res");
                                            pBar.hide();
                                            Intent i = new Intent(ResturentLoginActivity.this, ActivityFoodReqResList.class);
                                            startActivity(i);

                                            finish();



                                        }
                                        else{
                                            pBar.hide();
                                            Toast.makeText(getBaseContext(), "Invalid userName or Password", Toast.LENGTH_LONG).show();

                                        }

                                    }

                                } else {
                                    Log.d("", "Error getting documents: ", task.getException());
                                }
                            }
                        });*/
                    }
                    else{
                        showInternetAlert();
                    }
                }
            }
        });
    }



    private void login(String email, String password) {

        firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                btnLogin.setEnabled(false);

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            /*    if(cb_Remeber.isChecked()){
                    saveRemeberData(email,password);
                }
                else{
                    saveRemeberData("","");
                }*/

                if(user.isEmailVerified()){

                    pBar.hide();
                    verifyTypeUser(user.getUid());
                }
                else{
                    pBar.hide();
                    user.sendEmailVerification();
                    Toast.makeText(ResturentLoginActivity.this,"Check your email to verify your account!", Toast.LENGTH_LONG).show();
                    btnLogin.setEnabled(true);
                }
            }
            else {
                Toast.makeText(ResturentLoginActivity.this,"Failed to login! Please check your credentials",Toast.LENGTH_LONG).show();
                btnLogin.setEnabled(true);
                pBar.hide();
            }
        });
    }
    private void verifyTypeUser(String userId){
        DocumentReference user = myDB.collection("ResturentReg").document(userId);
        user.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    pref.setUserId(userId);
                    pref.setUserEmail(doc.getString("uEmail"));
                    pref.setUserFirstName(doc.getString("uFirstName"));
                    pref.setUserLastName(doc.getString("uLastName"));
                    pref.setUserType("Res");
                 //   progressBar_Login.setVisibility(View.GONE);
                    Toast.makeText(ResturentLoginActivity.this,"User has ben registered successfully!", Toast.LENGTH_LONG).show();
                    Intent i = new Intent(ResturentLoginActivity.this, ResturentDashboard.class);
                    startActivity(i);
                    finish();

                }
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pBar.hide();
                        Toast.makeText(ResturentLoginActivity.this,"Failed to Login! Try again!", Toast.LENGTH_LONG).show();
                    }
                });

    }
    void initialise() {
        LoginBtn = (TextView) findViewById(R.id.login_btn);
        SignupBtn = (TextView) findViewById(R.id.signup_btn);
        ilayusername = (TextInputLayout) findViewById(R.id.lay_username);
        ilaypassword = (TextInputLayout) findViewById(R.id.lay_password);
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        forgetPassword = (TextView) findViewById(R.id.forget_password);
    }


    boolean validateLogin() {
        boolean result = true;

        if (username.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), "Please enter Username as EmailID", Toast.LENGTH_SHORT).show();
            ilayusername.setError("Username Required as EmailID");
            result = false;

        }
        if(!Patterns.EMAIL_ADDRESS.matcher(username.getText().toString()).matches()){
            ilayusername.setError("Please provide a valid email address");
            result = false;
        }
        if (password.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), "Please enter your Password", Toast.LENGTH_SHORT).show();
            ilaypassword.setError("Password Required");
            result = false;
        }

        return result;
    }

    public void Is_Valid_Email(EditText edt) throws NumberFormatException {

        if(!Patterns.EMAIL_ADDRESS.matcher(edt.getText().toString()).matches()){
            edt.setError("Please enter valid email");

        }

    }
    public void Is_Valid_Password(EditText edt) throws NumberFormatException {
        if (edt.getText().toString().length() <= 0) {
            edt.setError("Enter Password");
        }
    }
    void showInternetAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Check Your Internet Connection!!")
                .setCancelable(false)
                .setPositiveButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        })
                .setNegativeButton("Retry",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // Creating dialog box
        AlertDialog alert = builder.create();
        // Setting the title manually
        alert.setTitle("No Internet");
        alert.show();
    }
    private boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();

        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(ResturentLoginActivity.this, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(ResturentLoginActivity.this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 124);
            return false;
        }
        return true;
    }


    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 124: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {  // permissions granted.
                    Log.e("Permissions Garated", "All"); // Now you call here what ever you want :)
                } else {
                    String perStr = "";
                    for (String per : permissions) {
                        perStr += "\n" + per;
                    }   // permissions list of don't granted permission
                    Log.e("Permissions Denied", perStr);
                }
                return;
            }
        }
    }

}


