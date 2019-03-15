package com.newage.letstalk;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

import static android.Manifest.permission.READ_CONTACTS;


public class Login extends AppCompatActivity {


    EditText Phone ;
    String Phone_Holder,otp;
    TextView  Not_registered, register_here;
    Button Login;

    SessionManager session;
    String HttpURLd = "https://globeexservices.com/letstalk/clear_contacts.php";
   public static  String HttpURLp = "";

    private static final int REQUEST_READ_CONTACTS = 444;
    private ListView mListView;
    private ProgressDialog pDialog;
    private Handler updateBarHandler;

    ArrayList<String> contactList;
    ArrayList<String> nameList;
    ArrayList<String> contact = new ArrayList<String>();
    JSONObject JSONcontacts = new JSONObject();
    JSONObject EverythingJSON = new JSONObject();
    JSONObject JsonName = new JSONObject();

    Cursor cursor;
    int counter;
//    boolean doubleBackToExitPressedOnce = false;
//
//    @Override
//    public void onBackPressed() {
//        if (doubleBackToExitPressedOnce) {
//            moveTaskToBack(true);
//        }
//
//        this.doubleBackToExitPressedOnce = true;
//        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
//
//        new Handler().postDelayed(new Runnable() {
//
//            @Override
//            public void run() {
//                doubleBackToExitPressedOnce=false;
//            }
//        }, 2000);
//    }

    Button register, log_in;
    EditText First_Name, Last_Name, Email, Password ;
    String F_Name_Holder, L_Name_Holder, EmailHolder, PasswordHolder,phonesync,namesync;
    String finalResult ;
    String HttpURL = "https://globeexservices.com/letstalk/login.php";
    Boolean CheckEditText ;
    ProgressDialog progressDialog;
    HashMap<String,String> hashMap = new HashMap<>();
    HttpParse httpParse = new HttpParse();
    private String m_Text = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //seting logo to my action bar
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.logowhite);

        Phone = (EditText)findViewById(R.id.phone_login);
        Login = (Button)findViewById(R.id.login);
        Not_registered = (TextView) findViewById(R.id.already_registered);
        register_here = (TextView) findViewById(R.id.login_here);

        session = new SessionManager(getApplicationContext());
        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                pDialog = new ProgressDialog(Login.this);
                updateBarHandler = new Handler();
                // Checking whether EditText is Empty or Not
                CheckEditTextIsEmptyOrNot();

                if(CheckEditText){

                    // If EditText is not empty and CheckEditText = True then this block will execute.

//                finish();
//                    startActivity(new Intent(Login.this, Dashboard.class));
//                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
//                    Toast.makeText(Login.this, "goes to dashboard.", Toast.LENGTH_LONG).show();
                    otp="Null";
                    UserRegisterFunction(Phone_Holder,otp);


                }
                else {

                    // If EditText is empty then this block will execute .
                    Toast.makeText(Login.this, "Please fill all form fields.", Toast.LENGTH_LONG).show();

                }


            }

        });

        Not_registered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                finish();
                startActivity(new Intent(Login.this, Register.class));
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

            }

        });


        register_here.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                finish();
                startActivity(new Intent(Login.this, Register.class));
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

            }

        });
    }

    public void CheckEditTextIsEmptyOrNot(){

        Phone_Holder = Phone.getText().toString();





        if(TextUtils.isEmpty(Phone_Holder))
        {

            CheckEditText = false;

        }
        else {

            CheckEditText = true ;
        }

    }



    public void UserRegisterFunction(final String Phone,final String OTP){

        class UserRegisterFunctionClass extends AsyncTask<String,Void,String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                progressDialog = ProgressDialog.show(Login.this,"Loading Data",null,true,true);
            }

            @Override
            protected void onPostExecute(String httpResponseMsg) {

                super.onPostExecute(httpResponseMsg);

                progressDialog.dismiss();

                if(httpResponseMsg.equalsIgnoreCase("Exists")){

                    AlertDialog alertDialog = new AlertDialog.Builder(Login.this).create();
                    alertDialog.setTitle("Notice");
                    alertDialog.setMessage("Letstalk will send an sms to " + Phone_Holder + " to verify its yours");
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    otp= "Check";
                                    UserRegisterFunction(Phone_Holder, otp);




                                }


                            });
                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int id) {

                            //...
                            dialog.dismiss();

                        }
                    });

                    alertDialog.show();

//to prevent a dialog box from being cancled when clicked on the return button

                    alertDialog.setCancelable(false);

                    //And to prevent dialog box from getting dismissed on outside touch use this

                    alertDialog.setCanceledOnTouchOutside(false);

                }else  if(httpResponseMsg.equalsIgnoreCase("check otp")){


                    AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
                    builder.setTitle("OTP");
                    builder.setMessage("put OTP below to verify "+Phone_Holder+"");
// Set up the input
                    final EditText input = new EditText(Login.this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                    input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    input.setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)});
                    builder.setView(input);

// Set up the buttons
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            m_Text = input.getText().toString();
                            otp = m_Text;
                            UserRegisterFunction(Phone_Holder, otp);
//                                            Toast.makeText(Register.this, ""+Otp_Holder+"", Toast.LENGTH_SHORT).show();
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


                }else  if(httpResponseMsg.equalsIgnoreCase("Matched")){


//                    session.createLoginSession(Phone_Holder, Phone_Holder);
//                    startActivity(new Intent(Login.this, Dashboard.class));
//                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
//                    Toast.makeText(Login.this, "goes to dashboard.", Toast.LENGTH_LONG).show();

                    UserRegisterFunctiondelete(Phone_Holder);
                    getContacts();

                }else  if(httpResponseMsg.equalsIgnoreCase("Wrong verification code")){


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

//to prevent a dialog box from being cancled when clicked on the return button

                    alertDialog.setCancelable(false);

                    //And to prevent dialog box from getting dismissed on outside touch use this

                    alertDialog.setCanceledOnTouchOutside(false);

                }


                Toast.makeText(Login.this,httpResponseMsg.toString(), Toast.LENGTH_LONG).show();

            }

            @Override
            protected String doInBackground(String... params) {

                hashMap.put("phone",params[0]);
                hashMap.put("otp",params[1]);
                finalResult = httpParse.postRequest(hashMap, HttpURL);

                return finalResult;
            }
        }

        UserRegisterFunctionClass userRegisterFunctionClass = new UserRegisterFunctionClass();

        userRegisterFunctionClass.execute(Phone,OTP);
    }




    public void UserRegisterFunctiondelete(final String Phone){

        class UserRegisterFunctionClassdelete extends AsyncTask<String,Void,String> {

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
                if(httpResponseMsg.equalsIgnoreCase("phone book is not registered")){

                    pDialog.cancel();

                    progressDialog.dismiss();
                    session.createLoginSession(Phone_Holder, Phone_Holder);
                    finish();
                    startActivity(new Intent(Login.this, Dashboard.class));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                }else if(httpResponseMsg.equalsIgnoreCase("matched already")){
                    Toast.makeText(Login.this, httpResponseMsg.toString(), Toast.LENGTH_LONG).show();
                    pDialog.cancel();
                    progressDialog.dismiss();
                    session.createLoginSession(Phone_Holder, Phone_Holder);
                    finish();
                    startActivity(new Intent(Login.this, Dashboard.class));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }else{

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

                hashMap.put("user",params[0]);
                finalResult = httpParse.postRequest(hashMap, HttpURLd);

                return finalResult;
            }
        }

        UserRegisterFunctionClassdelete userRegisterFunctionClassdelete = new UserRegisterFunctionClassdelete();

        userRegisterFunctionClassdelete.execute(Phone);
    }




    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                getContacts();

            }
        }
    }

    public void getContacts() {

        if (!mayRequestContacts()) {
            return;
        }

           Cursor c = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
               String namea, number = "";
               String id;
               c.moveToFirst();
        for (int i = 0; i < c.getCount(); i++) {
            namea = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            id = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));

            if (Integer.parseInt(c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                Cursor pCur = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id},
                        null);
                while (pCur.moveToNext()) {
                    number = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//                     Toast.makeText(this, "first "+namea+"", Toast.LENGTH_SHORT).show();
                }
            }
            Log.i("name ", namea + " ");
            Log.i("number ", number + " ");
            c.moveToNext();
        }



        contactList = new ArrayList<String>();
        nameList = new ArrayList<String>();
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

        cursor = contentResolver.query(CONTENT_URI, null, null, null, null);

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
                           Toast.makeText(this, contact_id, Toast.LENGTH_SHORT).show();
                int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(HAS_PHONE_NUMBER)));

                if (hasPhoneNumber > 0) {

                    nameoutput.append(""+ name);

                    //This is to read multiple phone numbers associated with the same contact
                    Cursor phoneCursor = contentResolver.query(PhoneCONTENT_URI, null, Phone_CONTACT_ID + " = ?", new String[]{contact_id}, null);

                    while (phoneCursor.moveToNext()) {
                        int nameFieldColumnIndex = phoneCursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME);
                        namesync = phoneCursor.getString(nameFieldColumnIndex);
                        phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER));
                        phonesync = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER));
                        session.createLoginSession(Phone_Holder, Phone_Holder);
                        HttpURLp = "https://globeexservices.com/letstalk/contacts.php/?user="+Phone_Holder+"";
//                        Toast.makeText(this, ""+namesync+"", Toast.LENGTH_SHORT).show();
                        output.append(""+ phonesync);
//                        UserRegisterFunctionphone(phonesync,Phone_Holder,namesync);

                    }

                    phoneCursor.close();


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
                    Log.d("BDAY", birthdayCur.getCount()+"");
                    if (birthdayCur.getCount() > 0) {
                        while (birthdayCur.moveToNext()) {
                            String birthday = birthdayCur.getString(birthdayCur.getColumnIndex(ContactsContract.CommonDataKinds.Event.START_DATE));
//                            output.append("Birthday :" + birthday);
//                            Log.d("BDAY", birthday);
                        }
                    }
                    birthdayCur.close();
                }

                // Add the contact to the ArrayList
                contactList.add(output.toString());
                nameList.add(nameoutput.toString());
                for (int i = 0; i < contactList.size(); i++) {
                    try {
                        JsonName.put("name"+ String.valueOf(i)+"", nameList.get(i));
                        JSONcontacts.put(""+ String.valueOf(i)+"", contactList.get(i));
                        EverythingJSON.put("num"+ String.valueOf(i)+"", JSONcontacts);
                        EverythingJSON.put("na"+ String.valueOf(i)+"", JsonName);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }


            // ListView has to be updated using a ui thread
//            runOnUiThread(new Runnable() {
//
//                @Override
//                public void run() {
//                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.list_item, R.id.text1, contactList);
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
        test();
    }



public void test(){


    new AsyncTask() {
        //String responseBody = "";
        @SuppressWarnings("unused")
        protected void onPostExecute(String msg) {
            //Not Needed
            Toast.makeText(Login.this, msg, Toast.LENGTH_SHORT).show();
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

    session.createLoginSession(Phone_Holder, Phone_Holder);
    finish();
    startActivity(new Intent(Login.this, Dashboard.class));
    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
}


    public void UserRegisterFunctionphone(final String Phone,final String User,final String Namep){

        class UserRegisterFunctionClassphone extends AsyncTask<String,Void,String> {

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
                if(httpResponseMsg.equalsIgnoreCase("phone book is not registered")){

                    pDialog.cancel();

                    progressDialog.dismiss();

                }else if(httpResponseMsg.equalsIgnoreCase("matched already")){
                    Toast.makeText(Login.this, httpResponseMsg.toString(), Toast.LENGTH_LONG).show();
                    pDialog.cancel();
                    progressDialog.dismiss();
                }else{

                    Toast.makeText(Login.this, httpResponseMsg.toString(), Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                }

                progressDialog.dismiss();
            }

            @Override
            protected String doInBackground(String... params) {

                hashMap.put("phone",params[0]);
                hashMap.put("user",params[1]);
                hashMap.put("name",params[2]);
                finalResult = httpParse.postRequest(hashMap, HttpURLp);

                return finalResult;
            }
        }

        UserRegisterFunctionClassphone userRegisterFunctionClassphone = new UserRegisterFunctionClassphone();

        userRegisterFunctionClassphone.execute(Phone,User,Namep);
    }






}
