package com.newage.letstalk.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Context;
import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import android.text.TextUtils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import androidx.core.app.NotificationCompat;

import java.util.ArrayList;
import java.util.Map;

import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;

import com.newage.letstalk.HttpParse;
import com.newage.letstalk.R;
import com.newage.letstalk.SessionManager;
import com.newage.letstalk.api.ApiInterface;
import com.newage.letstalk.api.RetrofitService;
import com.newage.letstalk.model.request.ClearContactRequest;
import com.newage.letstalk.model.request.RegisterRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.newage.letstalk.utils.Utility.isNetworkAvailable;
import static com.newage.letstalk.utils.Utility.isNumberValid;

public class Register extends AppCompatActivity {
    private static final int REQUEST_READ_CONTACTS = 444;
    private ProgressDialog pDialog, pDialogr, pDialogrd;
    private Handler updateBarHandler;
    int counter;

    EditText Name, Phone;
    String Name_Holder, Phone_Holder, phonesync, namesync;
    String finalResult;
    String HttpURLr = "https://globeexservices.com/letstalk/register.php";
    public static String HttpURLp = "";
    String HttpURLd = "https://globeexservices.com/letstalk/clear_contacts.php";
    ProgressDialog progressDialog;
    HashMap<String, String> hashMap = new HashMap<>();
    HttpParse httpParse = new HttpParse();

    HttpResponse httpResponse;
    TextView countrycode;
    JSONObject jsonObject = null;
    String StringHolder = "";
    String HttpURL = "https://globeexservices.com/letstalk/countrycode.php";
    boolean doubleBackToExitPressedOnce = false;
    SessionManager session;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            moveTaskToBack(true);
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        pDialog = new ProgressDialog(Register.this);
        updateBarHandler = new Handler();

        session = new SessionManager(getApplicationContext());
        Toast.makeText(getApplicationContext(), "ChatList Login Status: " + session.isLoggedIn(), Toast.LENGTH_LONG).show();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setIcon(R.mipmap.logowhite);
        }

        addNotification();

        Name = (EditText) findViewById(R.id.name_register);
        Phone = (EditText) findViewById(R.id.phone_register);

        Button Signup = findViewById(R.id.signup);
        TextView Terms = findViewById(R.id.termsandcondition);
        TextView Already_registered = findViewById(R.id.already_registered);
        TextView Login_here = findViewById(R.id.login_here);
        countrycode = (TextView) findViewById(R.id.countrycode);
        TextView countrycode = (TextView) findViewById(R.id.countrycode);
        countrycode.setPaintFlags(countrycode.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        countrycode.setText("+00");

        Signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkEditTextIsEmptyOrNot()) {
                    //UserRegisterFunction2(Name_Holder, Phone_Holder, "Check");
                    UserRegisterFunction(Name_Holder, Phone_Holder, "Check");
                } else {
                    Toast.makeText(Register.this, "Please fill all form fields.", Toast.LENGTH_LONG).show();
                }
            }
        });

        Terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Register.this, com.newage.letstalk.activity.Terms.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        Login_here.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Register.this, Login.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
    }

    public boolean checkEditTextIsEmptyOrNot() {
        Name_Holder = Name.getText().toString();
        Phone_Holder = Phone.getText().toString();

        if (TextUtils.isEmpty(Name_Holder)) {
            Name.setError("Field can not be empty");
            Name.requestFocus();
            return false;
        }

        if(TextUtils.isEmpty(Phone_Holder) || !Patterns.PHONE.matcher(Phone_Holder).matches() || !isNumberValid("234", Phone_Holder)){
            Phone.setError("Invalid Phone number");
            Phone.requestFocus();
            return false;
        }

        return true;
    }

    public void UserRegisterFunction2(final String Name, final String Phone, final String Otp) {

        if(!isNetworkAvailable(this)){
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog = ProgressDialog.show(Register.this, "Connecting...", null, true, true);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);

        RegisterRequest request = new RegisterRequest(Name, Phone, Otp);

        Map<String, String> map = new HashMap<>();
        map.put("name", Name);
        map.put("phone", Phone);
        map.put("otp", Otp);

        ApiInterface api = RetrofitService.initializer();
//      Call<String> reg = api.registerUser(request);
        Call<String> reg = api.registerUser2(map);

        reg.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful()) {
                    String resp = response.body();
                    Toast.makeText(getBaseContext(), resp, Toast.LENGTH_LONG).show();

                    assert resp != null;

                    if (resp.equalsIgnoreCase("That phone number is already registered")) {
                        AlertDialog alertDialog = new AlertDialog.Builder(Register.this).create();
                        alertDialog.setTitle("Number Exists");
                        alertDialog.setMessage(resp);
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.show();
                    } else if (resp.equalsIgnoreCase("Not yet registered")) {
                        AlertDialog alertDialog = new AlertDialog.Builder(Register.this).create();
                        alertDialog.setTitle("Notice");
                        alertDialog.setMessage("Letstalk will send an sms to " + Phone_Holder + " to verify its yours");
                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        UserRegisterFunction2(Name_Holder, Phone_Holder, "Null");
                                        //dialog.dismiss();
                                    }
                                });
                        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.show();
                        alertDialog.setCancelable(false);
                        alertDialog.setCanceledOnTouchOutside(false);
                    } else if (resp.equalsIgnoreCase("Registration successful")) {

                        //TODO read phone contact

                        session.createLoginSession(Name_Holder, Phone_Holder);
                        //UserRegisterFunctiondelete(Phone_Holder);
                        startActivity(new Intent(getBaseContext(), Dashboard.class));

                    } else if (resp.equalsIgnoreCase("Wrong verification code")) {
                        AlertDialog alertDialog = new AlertDialog.Builder(Register.this).create();
                        alertDialog.setTitle("Wrong OTP");
                        alertDialog.setMessage("Wrong Verification Code");
                        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.show();
                        alertDialog.setCancelable(false);
                        alertDialog.setCanceledOnTouchOutside(false);
                    } else if (resp.equalsIgnoreCase("otp sent")) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(Register.this);
                        builder.setTitle("OTP");
                        builder.setMessage("put OTP below to verify " + Phone_Holder + "");
                        final EditText input = new EditText(Register.this);
                        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                        input.setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)});
                        builder.setView(input);
                        builder.setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String otp = input.getText().toString();
                                        if (TextUtils.isEmpty(otp)) {
                                            Toast.makeText(getBaseContext(), "OTP cannot be empty", Toast.LENGTH_SHORT).show();
                                            return;
                                        }

                                        UserRegisterFunction2(Name_Holder, Phone_Holder, otp);
                                    }
                                });
                        builder.setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                        builder.setCancelable(false);
                        builder.show();
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
                    Log.e("TAG_REGISTER", t.getMessage());
                }
            }
        });
    }

    public void ClearContactsFunction(final String Phone) {
        progressDialog = ProgressDialog.show(Register.this, "Reading contacts...", null, true, true);
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

                    } else if (resp.equalsIgnoreCase("matched already")) {

                    } else {

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
    public void UserRegisterFunction(final String Name, final String Phone, final String Otp) {

        if(!isNetworkAvailable(this)){
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
            return;
        }

        class UserRegisterFunctionClass extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = ProgressDialog.show(Register.this, "Connecting...", null, true, true);
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setCancelable(false);
            }

            @Override
            protected void onPostExecute(String httpResponseMsg) {
                Toast.makeText(getBaseContext(), "seen", Toast.LENGTH_LONG).show();
                super.onPostExecute(httpResponseMsg);
                if (httpResponseMsg.equalsIgnoreCase("That phone number is already registered")) {
                    AlertDialog alertDialog = new AlertDialog.Builder(Register.this).create();
                    alertDialog.setTitle("Number Exists");
                    alertDialog.setMessage(httpResponseMsg);
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                } else if (httpResponseMsg.equalsIgnoreCase("Not yet registered")) {
                    AlertDialog alertDialog = new AlertDialog.Builder(Register.this).create();
                    alertDialog.setTitle("Notice");
                    alertDialog.setMessage("Letstalk will send an sms to " + Phone_Holder + " to verify its yours");
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    UserRegisterFunction(Name_Holder, Phone_Holder, "Null");
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
                } else if (httpResponseMsg.equalsIgnoreCase("Registration successful")) {

                    // Since reading contacts takes more time, let's run it on a separate thread.
                    session.createLoginSession(Phone_Holder, Phone_Holder);

//                    pDialogr = new ProgressDialog(Register.this);
//                    pDialogr.setMessage("Synchronizing please wait...");
//                    pDialogr.setCancelable(false);
//                    pDialogr.show();

                    UserRegisterFunctiondelete(Phone_Holder);
                    startActivity(new Intent(Register.this, Dashboard.class));


                    //getContacts();

//                    AlertDialog alertDialog = new AlertDialog.Builder(Register.this).create();
//                    alertDialog.setTitle("Almost Done");
//                    alertDialog.setMessage("Please wait while your contacts are synchronized");
//                    alertDialog.show();


//                    AlertDialog alertDialog = new AlertDialog.Builder(Register.this).create();
//                    alertDialog.setTitle("Success");
//                    alertDialog.setMessage(httpResponseMsg);
//                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
//                            new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int which) {
//                                    dialog.dismiss();
//                                }
//                            });
//                    alertDialog.setCancelable(false);
//                    alertDialog.setCanceledOnTouchOutside(false);
//                    alertDialog.show();


                } else if (httpResponseMsg.equalsIgnoreCase("Wrong verification code")) {
                    AlertDialog alertDialog = new AlertDialog.Builder(Register.this).create();
                    alertDialog.setTitle("Wrong OTP");
                    alertDialog.setMessage("Wrong Verification Code");
                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });

                    alertDialog.show();
                    alertDialog.setCancelable(false);
                    alertDialog.setCanceledOnTouchOutside(false);
                } else if (httpResponseMsg.equalsIgnoreCase("otp sent")) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(Register.this);
                    builder.setTitle("OTP");
                    builder.setMessage("put OTP below to verify " + Phone_Holder + "");
                    final EditText input = new EditText(Register.this);
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

                            UserRegisterFunction(Name_Holder, Phone_Holder, otp);
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

                }

                progressDialog.dismiss();

                Toast.makeText(Register.this, httpResponseMsg.toString(), Toast.LENGTH_LONG).show();
            }

            @Override
            protected String doInBackground(String... params) {
                hashMap.put("name", params[0]);
                hashMap.put("phone", params[1]);
                hashMap.put("otp", params[2]);
                return httpParse.postRequest(HttpURLr, hashMap);
            }
        }

        UserRegisterFunctionClass userRegisterFunctionClass = new UserRegisterFunctionClass();
        userRegisterFunctionClass.execute(Name, Phone, Otp);
    }

    // Declaring GetDataFromServerIntoTextView method with AsyncTask.
    public class GetDataFromServerIntoTextView extends AsyncTask<Void, Void, Void> {
        // Declaring CONTEXT.
        public Context context;


        public GetDataFromServerIntoTextView(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            HttpClient httpClient = new DefaultHttpClient();

            // Adding HttpURL to my HttpPost oject.
            HttpPost httpPost = new HttpPost(HttpURL);

            try {
                httpResponse = httpClient.execute(httpPost);

                StringHolder = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");

            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                // Passing string holder variable to JSONArray.
                JSONArray jsonArray = new JSONArray(StringHolder);
                jsonObject = jsonArray.getJSONObject(0);


            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Void result) {
            try {

                // Adding JSOn string to textview after done loading.
                countrycode.setText(jsonObject.getString("City"));

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            //Hiding progress bar after done loading TextView.
        }
    }

    public void addNotification() {
        // define sound URI, the sound to be played when there's a notification
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.logo)
                        .setContentTitle("New message")
                        .setContentText("you have a new message")
                        //adding sound to notification
                        .setSound(soundUri);


        Intent notificationIntent = new Intent(this, Launch.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        // Add as notification
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
    }

    @Deprecated
    public void UserRegisterFunctionphone(final String Phone, final String User, final String Namep) {

        class UserRegisterFunctionClassphone extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                pDialogr = new ProgressDialog(Register.this);
                pDialogr.setMessage("Reading contacts...");
                pDialogr.setCancelable(false);
                pDialogr.show();
                pDialogr.cancel();

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

                } else if (httpResponseMsg.equalsIgnoreCase("matched already")) {
                    Toast.makeText(Register.this, httpResponseMsg.toString(), Toast.LENGTH_LONG).show();
                    pDialog.cancel();
                    progressDialog.dismiss();
                } else {

                    Toast.makeText(Register.this, httpResponseMsg.toString(), Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                }

                progressDialog.dismiss();
            }

            @Override
            protected String doInBackground(String... params) {
                hashMap.put("phone", params[0]);
                hashMap.put("user", params[1]);
                hashMap.put("name", params[2]);
                return httpParse.postRequest(HttpURLp, hashMap);
            }
        }

        UserRegisterFunctionClassphone userRegisterFunctionClassphone = new UserRegisterFunctionClassphone();
        userRegisterFunctionClassphone.execute(Phone, User, Namep);
    }

    public void UserRegisterFunctiondelete(final String Phone) {

        class UserRegisterFunctionClassdelete extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                pDialogrd = new ProgressDialog(Register.this);
                pDialogrd.setMessage("Reading contacts...");
                pDialogrd.setCancelable(false);
                pDialogrd.show();
                pDialogrd.cancel();
            }

            @Override
            protected void onPostExecute(String httpResponseMsg) {

                super.onPostExecute(httpResponseMsg);
                if (httpResponseMsg.equalsIgnoreCase("phone book is not registered")) {

                    pDialog.cancel();

                    progressDialog.dismiss();

                } else if (httpResponseMsg.equalsIgnoreCase("matched already")) {
                    Toast.makeText(Register.this, httpResponseMsg.toString(), Toast.LENGTH_LONG).show();
                    pDialog.cancel();
                    progressDialog.dismiss();
                } else {

                    Toast.makeText(Register.this, httpResponseMsg.toString(), Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                }

                progressDialog.dismiss();
            }

            @Override
            protected String doInBackground(String... params) {
                hashMap.put("user", params[0]);
                return httpParse.postRequest(HttpURLd, hashMap);
            }
        }

        UserRegisterFunctionClassdelete userRegisterFunctionClassdelete = new UserRegisterFunctionClassdelete();
        userRegisterFunctionClassdelete.execute(Phone);
    }

    /**
     * The methods below is depreciated and replaced with a IntentService
     * #com.newage.letstalk.services.PhoneContactIntentService
     */
    @Deprecated
    public void getContacts() {
        pDialog.dismiss();

        ArrayList<String> contactList = new ArrayList<String>();
        ArrayList<String> nameList = new ArrayList<String>();
        JSONObject JSONcontacts = new JSONObject();
        JSONObject EverythingJSON = new JSONObject();
        JSONObject JsonName = new JSONObject();
        String phoneNumber = null;
        String email = null;

        Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
        String _ID = ContactsContract.Contacts._ID;
        String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
        String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;

        Uri PhoneCONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String Phone_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
        String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;

        Uri EmailCONTENT_URI = ContactsContract.CommonDataKinds.Email.CONTENT_URI;
        String EmailCONTACT_ID = ContactsContract.CommonDataKinds.Email.CONTACT_ID;
        String DATA = ContactsContract.CommonDataKinds.Email.DATA;

        StringBuffer output;
        StringBuffer nameoutput;

        ContentResolver contentResolver = getContentResolver();

        final Cursor cursor = contentResolver.query(CONTENT_URI, null, null, null, null);

        // Iterate every contact in the phone
        if (cursor.getCount() > 0) {

            counter = 0;
            while (cursor.moveToNext()) {
                output = new StringBuffer();
                nameoutput = new StringBuffer();
                // Update the progress message
                updateBarHandler.post(new Runnable() {
                    public void run() {
                        pDialog.setMessage("Reading contacts : " + counter++ + "/" + cursor.getCount());
                    }
                });

                String contact_id = cursor.getString(cursor.getColumnIndex(_ID));
                String name = cursor.getString(cursor.getColumnIndex(DISPLAY_NAME));

                int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(HAS_PHONE_NUMBER)));

                if (hasPhoneNumber > 0) {
                    nameoutput.append("" + name);
//                    output.append("\n First Name:" + name);

                    //This is to read multiple phone numbers associated with the same contact
                    Cursor phoneCursor = contentResolver.query(PhoneCONTENT_URI, null, Phone_CONTACT_ID + " = ?", new String[]{contact_id}, null);

                    while (phoneCursor.moveToNext()) {
                        int nameFieldColumnIndex = phoneCursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME);
                        namesync = phoneCursor.getString(nameFieldColumnIndex);
                        phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER));
                        phonesync = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER));
                        session.createLoginSession(Phone_Holder, Phone_Holder);

                        HttpURLp = "https://globeexservices.com/letstalk/contacts.php/?user=" + Phone_Holder + "";
                        Toast.makeText(this, "" + namesync + "", Toast.LENGTH_SHORT).show();
                        output.append("" + phonesync);

//                      UserRegisterFunctionphone(phonesync,Phone_Holder,namesync);
                    }

                    phoneCursor.close();
                    session.createLoginSession(Phone_Holder, Phone_Holder);
                    finish();
                    startActivity(new Intent(Register.this, Dashboard.class));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                    // Read every email id associated with the contact
                    Cursor emailCursor = contentResolver.query(EmailCONTENT_URI, null, EmailCONTACT_ID + " = ?", new String[]{contact_id}, null);

                    while (emailCursor.moveToNext()) {

                        email = emailCursor.getString(emailCursor.getColumnIndex(DATA));

//                        output.append("\n Email:" + email);

                    }

                    emailCursor.close();

                    String columns[] = {
                            ContactsContract.CommonDataKinds.Event.START_DATE,
                            ContactsContract.CommonDataKinds.Event.TYPE,
                            ContactsContract.CommonDataKinds.Event.MIMETYPE,
                    };

                    String where = ContactsContract.CommonDataKinds.Event.TYPE + "=" + ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY +
                            " and " + ContactsContract.CommonDataKinds.Event.MIMETYPE + " = '" + ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE + "' and " + ContactsContract.Data.CONTACT_ID + " = " + contact_id;

                    String[] selectionArgs = null;
                    String sortOrder = ContactsContract.Contacts.DISPLAY_NAME;

                    Cursor birthdayCur = contentResolver.query(ContactsContract.Data.CONTENT_URI, columns, where, selectionArgs, sortOrder);
                    Log.d("BDAY", birthdayCur.getCount() + "");
                    if (birthdayCur.getCount() > 0) {
                        while (birthdayCur.moveToNext()) {
                            String birthday = birthdayCur.getString(birthdayCur.getColumnIndex(ContactsContract.CommonDataKinds.Event.START_DATE));
//                            output.append("Birthday :" + birthday);
//                            Log.d("BDAY", birthday);
                        }
                    }
                    birthdayCur.close();
                }

                contactList.add(output.toString());
                nameList.add(nameoutput.toString());
                for (int i = 0; i < contactList.size(); i++) {
                    try {
                        JsonName.put("name" + String.valueOf(i) + "", nameList.get(i));
                        JSONcontacts.put("" + String.valueOf(i) + "", contactList.get(i));

                        EverythingJSON.put("num" + String.valueOf(i) + "", JSONcontacts);
                        EverythingJSON.put("na" + String.valueOf(i) + "", JsonName);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }


                // Add the contact to the ArrayList
//                contactList.add(output.toString());
            }

            // ListView has to be updated using a ui thread
//            runOnUiThread(new Runnable() {
//
//                @Override
//                public void run() {
//                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.single_friend.list_item, R.id.text1, contactList);
//                    mListView.setAdapter(adapter);
//                }
//            });

            // Dismiss the progressbar after 500 millisecondds
            updateBarHandler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    pDialog.cancel();
                }
            }, 500);
        }

        test(EverythingJSON);
    }

    @Deprecated
    public void test(final JSONObject EverythingJSON) {
        new AsyncTask() {
            //String responseBody = "";
            @SuppressWarnings("unused")
            protected void onPostExecute(String msg) {
                //Not Needed
                Toast.makeText(Register.this, msg, Toast.LENGTH_SHORT).show();
            }

            protected Object doInBackground(Object... params) {
                //Create Array of Post Variabels
                ArrayList<NameValuePair> postVars = new ArrayList<NameValuePair>();

                //Add a 1st Post Value called JSON with String value of JSON inside
                //This is first and last post value sent because server side will decode the JSON and get other vars from it.
                postVars.add(new BasicNameValuePair("JSON", EverythingJSON.toString()));

                //Declare and Initialize Http Clients and Http Posts
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(HttpURLp);

                //Format it to be sent
                try {
                    httppost.setEntity(new UrlEncodedFormEntity(postVars));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                /* Send request and Get the Response Back */
                try {
                    HttpResponse response = httpclient.execute(httppost);
                    String responseBody = EntityUtils.toString(response.getEntity());
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                    Log.v("MAD", "Error sending... ");
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.v("MAD", "Error sending... ");
                }
                return null;
            }
        }.execute(null, null, null);
    }

}