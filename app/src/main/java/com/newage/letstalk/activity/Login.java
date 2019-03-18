package com.newage.letstalk.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.newage.letstalk.HttpParse;
import com.newage.letstalk.R;
import com.newage.letstalk.SessionManager;
import com.newage.letstalk.api.ApiInterface;
import com.newage.letstalk.api.RetrofitService;
import com.newage.letstalk.model.request.ClearContactRequest;
import com.newage.letstalk.model.request.LoginRequest;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.newage.letstalk.utils.Utility.isNumberValid;

public class Login extends AppCompatActivity {
    EditText Phone;
    String Phone_Holder;
    SessionManager session;
    String HttpURLd = "https://globeexservices.com/letstalk/clear_contacts.php";
    private static final int REQUEST_READ_CONTACTS = 444;
    private ProgressDialog pDialog;
    String HttpURL = "https://globeexservices.com/letstalk/login.php";
    ProgressDialog progressDialog;
    HashMap<String, String> hashMap = new HashMap<>();
    HttpParse httpParse = new HttpParse();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setIcon(R.mipmap.logowhite);
        }

        session = new SessionManager(getApplicationContext());

        Phone = findViewById(R.id.phone_login);
        Button Login = findViewById(R.id.login);
        TextView register_here = findViewById(R.id.login_here);

        pDialog = new ProgressDialog(Login.this);
        Handler updateBarHandler = new Handler();

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkEditTextIsEmptyOrNot()) {
                   // UserRegisterFunction(Phone_Holder, "Null");
                    UserLoginFunction(Phone_Holder, "Null");
                } else {
                    Toast.makeText(Login.this, "Please fill all form fields.", Toast.LENGTH_LONG).show();
                }
            }
        });

        register_here.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this, Register.class));
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                finish();
            }

        });
    }

    public boolean checkEditTextIsEmptyOrNot() {
        Phone_Holder = Phone.getText().toString();

        if(TextUtils.isEmpty(Phone_Holder) || !Patterns.PHONE.matcher(Phone_Holder).matches() || !isNumberValid("234", Phone_Holder)){
            Phone.setError("Invalid Phone number");
            Phone.requestFocus();
            return false;
        }

        return true;
    }

    public void UserLoginFunction(final String Phone, final String Otp) {
        progressDialog = ProgressDialog.show(Login.this, "Loading Data", null, true, true);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);

        LoginRequest request = new LoginRequest(Phone, Otp);

        ApiInterface api = RetrofitService.initializer();
        Call<String> reg = api.loginUser(request);
        reg.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                progressDialog.dismiss();

                if (response.isSuccessful()) {
                    String resp = response.body();
                    Toast.makeText(getBaseContext(), resp, Toast.LENGTH_LONG).show();

                    assert resp != null;

                    if (resp.equalsIgnoreCase("Exists")) {
                        AlertDialog alertDialog = new AlertDialog.Builder(Login.this).create();
                        alertDialog.setTitle("Notice");
                        alertDialog.setMessage("Letstalk will send an sms to " + Phone_Holder + " to verify its yours");
                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        UserLoginFunction(Phone_Holder, "Check");
                                    }


                                });
                        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                        alertDialog.show();
                        alertDialog.setCancelable(false);
                        alertDialog.setCanceledOnTouchOutside(false);
                    } else if (resp.equalsIgnoreCase("check otp")) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
                        builder.setTitle("OTP");
                        builder.setMessage("put OTP below to verify " + Phone_Holder + "");
                        final EditText input = new EditText(Login.this);
                        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                        input.setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)});
                        builder.setView(input);
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String otp = input.getText().toString();

                                if (TextUtils.isEmpty(otp)) {
                                    Toast.makeText(getBaseContext(), "OTP cannot be empty", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                UserLoginFunction(Phone_Holder, otp);
                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        builder.setCancelable(false);
                        builder.show();
                    } else if (resp.equalsIgnoreCase("Matched")) {
                        ClearContactsFunction(Phone_Holder);
                        //TODO get the new contacts and send

                    } else if (resp.equalsIgnoreCase("Wrong verification code")) {
                        AlertDialog alertDialog = new AlertDialog.Builder(Login.this).create();
                        alertDialog.setTitle("Wrong OTP");
                        alertDialog.setMessage("wrong verification code...try again");
                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }


                                });
                        alertDialog.show();
                        alertDialog.setCancelable(false);
                        alertDialog.setCanceledOnTouchOutside(false);
                    }
                } else if (response.code() == 400) {
                    Toast.makeText(getBaseContext(), "400", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                progressDialog.dismiss();

                if (call.isCanceled()) {

                } else {
                    Log.e("TAG_LOGIN", t.getMessage());
                }
            }
        });
    }

    public void ClearContactsFunction(final String Phone) {
        progressDialog = ProgressDialog.show(Login.this, "Reading contacts...", null, true, true);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);

        ClearContactRequest request = new ClearContactRequest(Phone);

        ApiInterface api = RetrofitService.initializer();
        Call<String> reg = api.clearContacts(request);
        reg.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful()) {
                    String resp = response.body();
                    Toast.makeText(getBaseContext(), resp, Toast.LENGTH_LONG).show();

                    assert resp != null;

                    if (resp.equalsIgnoreCase("phone book is not registered")) {
                        //session.createLoginSession(Phone_Holder, Phone_Holder);
                        startActivity(new Intent(Login.this, Dashboard.class));
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        finish();

                    } else if (resp.equalsIgnoreCase("matched already")) {
                        //session.createLoginSession(Phone_Holder, Phone_Holder);
                        startActivity(new Intent(Login.this, Dashboard.class));
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        finish();
                    } else {
                       // session.createLoginSession(Phone_Holder, Phone_Holder);
                        startActivity(new Intent(Login.this, Dashboard.class));
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        finish();
                    }
                } else if (response.code() == 400) {
                    Toast.makeText(getBaseContext(), "400", Toast.LENGTH_LONG).show();
                }

                progressDialog.dismiss();
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                progressDialog.dismiss();

                if (call.isCanceled()) {

                } else {
                    Log.e("TAG_LOGIN", t.getMessage());
                }
            }
        });
    }








    @Deprecated
    public void UserRegisterFunction(final String Phone, final String OTP) {

        class UserRegisterFunctionClass extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                progressDialog = ProgressDialog.show(Login.this, "Loading Data", null, true, true);
            }

            @Override
            protected void onPostExecute(String httpResponseMsg) {

                super.onPostExecute(httpResponseMsg);

                progressDialog.dismiss();

                if (httpResponseMsg.equalsIgnoreCase("Exists")) {

                    AlertDialog alertDialog = new AlertDialog.Builder(Login.this).create();
                    alertDialog.setTitle("Notice");
                    alertDialog.setMessage("Letstalk will send an sms to " + Phone_Holder + " to verify its yours");
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    UserRegisterFunction(Phone_Holder, "Check");
                                }


                            });
                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
                    alertDialog.show();
                    alertDialog.setCancelable(false);
                    alertDialog.setCanceledOnTouchOutside(false);
                } else if (httpResponseMsg.equalsIgnoreCase("check otp")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
                    builder.setTitle("OTP");
                    builder.setMessage("put OTP below to verify " + Phone_Holder + "");
                    final EditText input = new EditText(Login.this);
                    input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    input.setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)});
                    builder.setView(input);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String otp = input.getText().toString();
                            UserRegisterFunction(Phone_Holder, otp);
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.setCancelable(false);
                    builder.show();
                } else if (httpResponseMsg.equalsIgnoreCase("Matched")) {
                    UserRegisterFunctiondelete(Phone_Holder);
                    //getContacts();
                } else if (httpResponseMsg.equalsIgnoreCase("Wrong verification code")) {
                    AlertDialog alertDialog = new AlertDialog.Builder(Login.this).create();
                    alertDialog.setTitle("Wrong OTP");
                    alertDialog.setMessage("wrong verification code...try again");
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }


                            });
                    alertDialog.show();
                    alertDialog.setCancelable(false);
                    alertDialog.setCanceledOnTouchOutside(false);
                }

                Toast.makeText(Login.this, httpResponseMsg.toString(), Toast.LENGTH_LONG).show();
            }

            @Override
            protected String doInBackground(String... params) {
                hashMap.put("phone", params[0]);
                hashMap.put("otp", params[1]);
                return httpParse.postRequest(hashMap, HttpURL);
            }
        }

        UserRegisterFunctionClass userRegisterFunctionClass = new UserRegisterFunctionClass();
        userRegisterFunctionClass.execute(Phone, OTP);
    }

    @Deprecated
    public void UserRegisterFunctiondelete(final String Phone) {

        class UserRegisterFunctionClassdelete extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                pDialog = new ProgressDialog(Login.this);
                pDialog.setMessage("Reading contacts...");
                pDialog.setCancelable(false);
                pDialog.show();
                pDialog.cancel();

//                progressDialog = ProgressDialog.show(Register.this,"Connecting...",null,true,true);
//                progressDialog.setCanceledOnTouchOutside(false);
//                progressDialog.setCancelable(false);
            }

            @Override
            protected void onPostExecute(String httpResponseMsg) {

                super.onPostExecute(httpResponseMsg);
                if (httpResponseMsg.equalsIgnoreCase("phone book is not registered")) {

                    pDialog.cancel();

                    progressDialog.dismiss();
                    session.createLoginSession(Phone_Holder, Phone_Holder);
                    finish();
                    startActivity(new Intent(Login.this, Dashboard.class));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                } else if (httpResponseMsg.equalsIgnoreCase("matched already")) {
                    Toast.makeText(Login.this, httpResponseMsg.toString(), Toast.LENGTH_LONG).show();
                    pDialog.cancel();
                    progressDialog.dismiss();
                    session.createLoginSession(Phone_Holder, Phone_Holder);
                    finish();
                    startActivity(new Intent(Login.this, Dashboard.class));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                } else {

                    Toast.makeText(Login.this, httpResponseMsg.toString(), Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                    session.createLoginSession(Phone_Holder, Phone_Holder);
                    finish();
                    startActivity(new Intent(Login.this, Dashboard.class));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }

                progressDialog.dismiss();
            }

            @Override
            protected String doInBackground(String... params) {
                hashMap.put("user", params[0]);
                return httpParse.postRequest(hashMap, HttpURLd);
            }
        }

        UserRegisterFunctionClassdelete userRegisterFunctionClassdelete = new UserRegisterFunctionClassdelete();

        userRegisterFunctionClassdelete.execute(Phone);
    }

}