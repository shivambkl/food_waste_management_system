package com.foodwastemanagement;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.foodwastemanagement.Utility.PrefManager;

import java.util.HashMap;
import java.util.Map;

public class ResturentRegistrationActivity extends AppCompatActivity implements View.OnClickListener {
    TextInputLayout ilayUserNameReg, ilayUserPasswordReg, ilayConformPasswordReg, ilayFastNameReg, ilayLastNameReg,ilayAddressReg,ilayCityReg,ilayDobReg;
    EditText etUserNameReg, etPasswordReg, etConformPasswordReg, etFirstNameReg, etLastNameReg,etAddressReg, etMobileReg,etDescription;
    Button btnRegistration;
    ConnectionDetector cd;
    Boolean isInternetPresent = false;
    ProgressDialog pBar;

    private final static String TAG = "Registration Resturent";
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    FirebaseFirestore myDB;
    PrefManager pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.resturent_registration);
        firebaseAuth = FirebaseAuth.getInstance();
        myDB = FirebaseFirestore.getInstance();
        pref = new PrefManager(ResturentRegistrationActivity.this);
        initialise();
        cd=new ConnectionDetector(this);
        pBar=new ProgressDialog(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.registration:
                if (validateRegistration()) {
                    isInternetPresent = cd.isConnectingToInternet();
                    if (isInternetPresent) {
                        Log.d("#00", "inside Intenet present");
                        pBar.setMessage("Registration in Progress...");
                        pBar.setCancelable(false);
                        pBar.show();
                        String name = etFirstNameReg.getText().toString().trim();
                        String surname = etLastNameReg.getText().toString();
                        String phone = etMobileReg.getText().toString().trim();
                        String email = etUserNameReg.getText().toString().trim();
                        String password = etPasswordReg.getText().toString().trim();
                        String address=etAddressReg.getText().toString();
                        String description=etDescription.getText().toString();
                        registerUser(name,surname,phone,email,password,address,description);

                    }


                }
                else {
                    showInternetAlert();
                }
                break;



        }
    }

    void initialise() {
        ilayUserNameReg = (TextInputLayout) findViewById(R.id.lay_user_name_reg);
        ilayUserPasswordReg = (TextInputLayout) findViewById(R.id.lay_password_reg);
        ilayConformPasswordReg = (TextInputLayout) findViewById(R.id.lay_conform_password_reg);
        ilayFastNameReg = (TextInputLayout) findViewById(R.id.lay_first_name_reg);
        ilayLastNameReg = (TextInputLayout) findViewById(R.id.lay_last_name_reg);
        ilayAddressReg = (TextInputLayout) findViewById(R.id.lay_address_reg);
       /* ilayCityReg = (TextInputLayout) findViewById(R.id.lay_city_reg);*/
       /* ilayDobReg = (TextInputLayout) findViewById(R.id.lay_dob_reg);
*/
        etUserNameReg = (EditText) findViewById(R.id.user_name_reg);
        etPasswordReg = (EditText) findViewById(R.id.password_reg);
        etConformPasswordReg = (EditText) findViewById(R.id.conformPassword_reg);
        etFirstNameReg = (EditText) findViewById(R.id.first_name_reg);
        etLastNameReg = (EditText) findViewById(R.id.last_name_reg);
        etAddressReg = (EditText) findViewById(R.id.address_reg);
        etMobileReg = (EditText) findViewById(R.id.mobilno_reg);
        etDescription = (EditText) findViewById(R.id.description_reg);

        btnRegistration = (Button) findViewById(R.id.registration);
        btnRegistration.setOnClickListener(this);

        etUserNameReg.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Is_Valid_Sign_Number_Validation(etUserNameReg, ilayUserNameReg);

            }
        });


        etPasswordReg.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (etPasswordReg.getText().toString().equals("")) {
                    ilayUserPasswordReg.setError("Password Required.");
                } else {
                    ilayUserPasswordReg.setError(null);
                    ilayUserPasswordReg.setErrorEnabled(false);
                }
            }
        });

        etConformPasswordReg.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (etConformPasswordReg.getText().toString().equals("")) {
                    ilayConformPasswordReg.setError("Conform Password Required.");
                } else {
                    ilayConformPasswordReg.setError(null);
                    ilayConformPasswordReg.setErrorEnabled(false);
                }
            }
        });

        etFirstNameReg.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (etFirstNameReg.getText().toString().equals("")) {
                    ilayFastNameReg.setError("First Name Required.");
                } else if (!etFirstNameReg.getText().toString().matches("[a-zA-Z ]+")) {
                    ilayFastNameReg.setError("Accept Alphabets Only.");
                } else {
                    ilayFastNameReg.setError(null);
                    ilayFastNameReg.setErrorEnabled(false);
                }
            }
        });


        etAddressReg.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (etAddressReg.getText().toString().equals("")) {
                    ilayAddressReg.setError("Address Name Required.");
                } else if (!etAddressReg.getText().toString().matches("[a-zA-Z ]+")) {
                    ilayAddressReg.setError("Accept Alphabets Only.");
                } else {
                    ilayAddressReg.setError(null);
                    ilayAddressReg.setErrorEnabled(false);
                }

            }
        });

    }


    boolean validateRegistration() {
        boolean result = true;

        if (etUserNameReg.getText().toString().equals("")) {
            ilayUserNameReg.setError("EmailID as Username Required");
            result = false;
        } else {
            if(!Patterns.EMAIL_ADDRESS.matcher(etUserNameReg.getText().toString()).matches()){
                ilayUserNameReg.setError("Please provide a valid Email");
                result = false;;
            }else{
                ilayUserNameReg.setError(null);
                ilayUserNameReg.setErrorEnabled(false);
            }
        }

        if (etPasswordReg.getText().toString().equals("")) {
            ilayUserPasswordReg.setError("Password Required.");
            result = false;

        } else {
            ilayUserPasswordReg.setError(null);
            ilayUserPasswordReg.setErrorEnabled(false);
        }

        if (etConformPasswordReg.getText().toString().equals("")) {
            ilayConformPasswordReg.setError("Conform Password Required.");
            result = false;
        } else if (!etPasswordReg.getText().toString().equals(etConformPasswordReg.getText().toString())) {
            ilayConformPasswordReg.setError("Password Not matched");
            etConformPasswordReg.requestFocus();
            result = false;

        } else {
            ilayConformPasswordReg.setError(null);
            ilayConformPasswordReg.setErrorEnabled(false);
        }

        if (etFirstNameReg.getText().toString().equals("")) {
            ilayFastNameReg.setError("First Name Required...");

        } else {
            ilayFastNameReg.setError(null);
            ilayFastNameReg.setErrorEnabled(false);
        }
        if (etLastNameReg.getText().toString().equals("")) {
            ilayLastNameReg.setError("Resturent Name Required...");

        } else {
            ilayLastNameReg.setError(null);
            ilayLastNameReg.setErrorEnabled(false);
        }

        if (etAddressReg.getText().toString().equals("")) {
            ilayAddressReg.setError("Address  Required...");

        } else {
            ilayAddressReg.setError(null);
            ilayAddressReg.setErrorEnabled(false);
        }
        return result;
    }

    public void Is_Valid_Sign_Number_Validation(EditText edt, TextInputLayout edtLayout) throws NumberFormatException {
        if (edt.getText().toString().length()<=0) {
            edtLayout.setError("Required Number Only");
        } else {
            edtLayout.setError(null);
            edtLayout.setErrorEnabled(false);

        }
    } // END OF Edittext validation

    private void registerUser(String name, String surname, String phone, String email, String password,String address,String description) {

            firebaseAuth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            firebaseUser = firebaseAuth.getCurrentUser();
                            Toast.makeText(ResturentRegistrationActivity.this,"An email has been sent to activate your account!", Toast.LENGTH_LONG).show();
                            firebaseUser.sendEmailVerification();
                            registerInFirebase(name,surname,phone,email,password,address,description,firebaseUser.getUid());
                        }
                        else {
                            Toast.makeText(ResturentRegistrationActivity.this,"Failed to register! Try again!", Toast.LENGTH_LONG).show();
                           // progressBar_Register.setVisibility(View.GONE);
                            return;
                        }
                    });


    }
    private void registerInFirebase(String name, String surname, String phone, String email, String password,String address,String description, String userID) {

        final Map<String, Object> user = new HashMap<>();
        user.put("uUsername", userID);
        user.put("uPassword", password);
        user.put("uFirstName",name);
        user.put("uLastName", surname);
        user.put("uAboutUs", description);
        user.put("uFatherHusband", "");
        user.put("uMaritalStatus", "");
        user.put("uGender", "");
        user.put("uDOB", "");
        user.put("uType", "Res");
        user.put("uPusername", "");
        user.put("uRefBy", "");
        user.put("uEmail", email);
        user.put("uMobile", phone);
        user.put("uPasskey", "");
        user.put("uAddress",address);
        user.put("uAddress_line2", "");
        user.put("uAddress_line3", "");
        user.put("uCity", "");
        user.put("uState", "");
        user.put("uCountry", "");
        user.put("uPincode", "");
        user.put("uDesignation", "");
        user.put("uPhone", "");
        user.put("uLandMark", "");
        user.put("uAuthtype", "");
        user.put("uImage", "");
        user.put("uStatus", "");
        user.put("uValidate", "");
        user.put("uValidateBy", "");
        user.put("uValidM", "");
        user.put("uValidE", "");
        user.put("uRegdate", "");
        user.put("OuAddress", "");
        user.put("OuAddress_line3", "");
        user.put("OuAddress_line2", "");
        user.put("OuLandMark", "");
        user.put("OuCity", "");
        user.put("OuState", "");
        user.put("Oulatitude", "");
        user.put("Oulongitude", "");
        user.put("OuPincode", "");
        user.put("uRegBy", "");
        user.put("cdate", "");
        user.put("mdate", "");
        user.put("mby", "");
        user.put("ulatitude", "");
        user.put("ulongitude", "");

        myDB.collection("ResturentReg").document(userID).set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        DocumentReference user = myDB.collection("ResturentReg").document(userID);
                        user.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot doc = task.getResult();
                                    pBar.hide();
                                    Toast.makeText(ResturentRegistrationActivity.this,"Registeration successfully!", Toast.LENGTH_LONG).show();
                                    FirebaseAuth.getInstance().signOut();
                                    Intent i = new Intent(ResturentRegistrationActivity.this, ResturentLoginActivity.class);
                                    startActivity(i);
                                    finish();

                                }
                            }
                        })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                       // progressBar_Register.setVisibility(View.GONE);
                                        Toast.makeText(ResturentRegistrationActivity.this,"Failed to register! Try again!", Toast.LENGTH_LONG).show();
                                    }
                                });
                    }


                });

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
}


