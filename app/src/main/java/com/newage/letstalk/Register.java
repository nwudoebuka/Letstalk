package com.newage.letstalk;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
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
import java.util.HashMap;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import java.util.ArrayList;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import static android.Manifest.permission.READ_CONTACTS;

public class Register extends AppCompatActivity {



    private static final int REQUEST_READ_CONTACTS = 444;
    private ListView mListView;
    private ProgressDialog pDialog, pDialogr,pDialogrd;
    private Handler updateBarHandler;

    ArrayList<String> contactList;
    ArrayList<String> nameList;
    ArrayList<String> contact = new ArrayList<String>();
    JSONObject JSONcontacts = new JSONObject();
    JSONObject EverythingJSON = new JSONObject();
    JSONObject JsonName = new JSONObject();
    Cursor cursor;
    int counter;

    EditText Name, Phone ;
    String Name_Holder, Phone_Holder,Otp_Holder,phonesync,namesync,Contact_Holder ;
    String finalResult ;
    String HttpURLr = "https://dtodxlogistics.com/Letstalk/register.php";
    public static String HttpURLp = "";
    String HttpURLd = "https://dtodxlogistics.com/Letstalk/clear_contacts.php";
    Boolean CheckEditText,Status ;
    ProgressDialog progressDialog;
    HashMap<String,String> hashMap = new HashMap<>();
    HttpParse httpParse = new HttpParse();


    HttpResponse httpResponse;
    TextView countrycode;
    JSONObject jsonObject = null ;
    String StringHolder = "" ;
    // Adding HTTP Server URL to string variable.
    String HttpURL = "https://dtodxlogistics.com/Letstalk/countrycode.php", PhoneHolder;

    Button Signup;
    TextView Terms, Already_registered, Login_here;
    private String m_Text = "";
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
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

//    //checking if phone is connected to ineternet
//    private boolean isNetworkAvailable() {
//        ConnectivityManager connectivityManager
//                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
//        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        pDialog = new ProgressDialog(Register.this);
//        pDialog.setMessage("Reading contacts...");
//        pDialog.setCancelable(false);
//        pDialog.show();

//        mListView = (ListView) findViewById(R.id.list);
        updateBarHandler = new Handler();


        session = new SessionManager(getApplicationContext());
        Toast.makeText(getApplicationContext(), "User Login Status: " + session.isLoggedIn(), Toast.LENGTH_LONG).show();
//        new GetDataFromServerIntoTextView(Register.this).execute();
        //seting logo to my action bar


        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.logowhite);

        addNotification();

        Name = (EditText)findViewById(R.id.name_register);
        Phone = (EditText)findViewById(R.id.phone_register);

        Signup = (Button)findViewById(R.id.signup);
        Terms = (TextView) findViewById(R.id.termsandcondition);
        Already_registered = (TextView) findViewById(R.id.already_registered);
        Login_here = (TextView) findViewById(R.id.login_here);
        countrycode = (TextView)findViewById(R.id.countrycode);
        TextView countrycode = (TextView)findViewById(R.id.countrycode);
        countrycode.setPaintFlags(countrycode.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        countrycode.setText("+00");



        Signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                // Checking whether EditText is Empty or Not
                CheckEditTextIsEmptyOrNot();

                if(CheckEditText){

                    // If EditText is not empty and CheckEditText = True then this block will execute.

                    Otp_Holder = "Check";
                    UserRegisterFunction(Name_Holder, Phone_Holder, Otp_Holder);



//code here




                }
                else {

                    // If EditText is empty then this block will execute .
                    Toast.makeText(Register.this, "Please fill all form fields.", Toast.LENGTH_LONG).show();

                }


            }

        });

        Terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                Intent intent = new Intent(Register.this, Terms.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
//                finish();
            }

        });

        Already_registered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                //                finish();
                startActivity(new Intent(Register.this, Login.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

            }

        });

        Login_here.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



//                finish();
                startActivity(new Intent(Register.this, Login.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

            }

        });




    }


    // Declaring GetDataFromServerIntoTextView method with AsyncTask.
    public class GetDataFromServerIntoTextView extends AsyncTask<Void, Void, Void>
    {
        // Declaring CONTEXT.
        public Context context;


        public GetDataFromServerIntoTextView(Context context)
        {
            this.context = context;
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0)
        {

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

            try{
                // Passing string holder variable to JSONArray.
                JSONArray jsonArray = new JSONArray(StringHolder);
                jsonObject = jsonArray.getJSONObject(0);


            } catch ( JSONException e) {
                e.printStackTrace();
            }

            catch (Exception e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }
        protected void onPostExecute(Void result)
        {
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


    public void CheckEditTextIsEmptyOrNot(){

        Name_Holder = Name.getText().toString();
        Phone_Holder = Phone.getText().toString();




        if(TextUtils.isEmpty(Name_Holder) || TextUtils.isEmpty(Phone_Holder))
        {

            CheckEditText = false;

        }
        else {

            CheckEditText = true ;
        }

    }

    public void UserRegisterFunction(final String Name, final String Phone, final String Otp){

        class UserRegisterFunctionClass extends AsyncTask<String,Void,String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = ProgressDialog.show(Register.this,"Connecting...",null,true,true);
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setCancelable(false);
            }

            @Override
            protected void onPostExecute(String httpResponseMsg) {

                super.onPostExecute(httpResponseMsg);
                if(httpResponseMsg.equalsIgnoreCase("That phone number is already registered")){
                    Status = false;
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
                }else if(httpResponseMsg.equalsIgnoreCase("Not yet registered")){
                    Status = true;
                    if(Status) {
                        AlertDialog alertDialog = new AlertDialog.Builder(Register.this).create();
                        alertDialog.setTitle("Notice");
                        alertDialog.setMessage("Letstalk will send an sms to " + Phone_Holder + " to verify its yours");
                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        Otp_Holder = "Null";
                                        UserRegisterFunction(Name_Holder, Phone_Holder, Otp_Holder);




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

                    }
                }else if(httpResponseMsg.equalsIgnoreCase("Registration successful")){

                    // Since reading contacts takes more time, let's run it on a separate thread.
                    session.createLoginSession(Phone_Holder, Phone_Holder);
                    pDialogr = new ProgressDialog(Register.this);
                    pDialogr.setMessage("Synchronizing please wait...");
                    pDialogr.setCancelable(false);
                    pDialogr.show();
                    UserRegisterFunctiondelete(Phone_Holder);
                    getContacts();

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


                } else if(httpResponseMsg.equalsIgnoreCase("Wrong verification code")){

                    AlertDialog alertDialog = new AlertDialog.Builder(Register.this).create();
                    alertDialog.setTitle("Wrong OTP");
                    alertDialog.setMessage("Wrong Verification Code");
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

                }
                else if(httpResponseMsg.equalsIgnoreCase("otp sent")){


                    AlertDialog.Builder builder = new AlertDialog.Builder(Register.this);
                    builder.setTitle("OTP");
                    builder.setMessage("put OTP below to verify "+Phone_Holder+"");
// Set up the input
                    final EditText input = new EditText(Register.this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                    input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    input.setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)});
                    builder.setView(input);

// Set up the buttons
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            m_Text = input.getText().toString();
                            Otp_Holder = m_Text;
                            UserRegisterFunction(Name_Holder, Phone_Holder, Otp_Holder);
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

                }

                progressDialog.dismiss();

                Toast.makeText(Register.this,httpResponseMsg.toString(), Toast.LENGTH_LONG).show();

            }

            @Override
            protected String doInBackground(String... params) {

                hashMap.put("name",params[0]);

                hashMap.put("phone",params[1]);

                hashMap.put("otp",params[2]);

                finalResult = httpParse.postRequest(hashMap, HttpURLr);

                return finalResult;
            }
        }

        UserRegisterFunctionClass userRegisterFunctionClass = new UserRegisterFunctionClass();

        userRegisterFunctionClass.execute(Name,Phone,Otp);
    }

    public void addNotification() {
        // define sound URI, the sound to be played when there's a notification
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.logo)
                        .setContentTitle("New message")
                        .setContentText("Hustle")
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




    public void UserRegisterFunctionphone(final String Phone,final String User,final String Namep){

        class UserRegisterFunctionClassphone extends AsyncTask<String,Void,String> {

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
                if(httpResponseMsg.equalsIgnoreCase("phone book is not registered")){

                    pDialog.cancel();

                    progressDialog.dismiss();

                }else if(httpResponseMsg.equalsIgnoreCase("matched already")){
                    Toast.makeText(Register.this, httpResponseMsg.toString(), Toast.LENGTH_LONG).show();
                    pDialog.cancel();
                    progressDialog.dismiss();
                }else{

                    Toast.makeText(Register.this, httpResponseMsg.toString(), Toast.LENGTH_LONG).show();
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






    public void UserRegisterFunctiondelete(final String Phone){

        class UserRegisterFunctionClassdelete extends AsyncTask<String,Void,String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                pDialogrd = new ProgressDialog(Register.this);
                pDialogrd.setMessage("Reading contacts...");
                pDialogrd.setCancelable(false);
                pDialogrd.show();
                pDialogrd.cancel();

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
                    Toast.makeText(Register.this, httpResponseMsg.toString(), Toast.LENGTH_LONG).show();
                    pDialog.cancel();
                    progressDialog.dismiss();
                }else{

                    Toast.makeText(Register.this, httpResponseMsg.toString(), Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
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
pDialog.dismiss();
        if (!mayRequestContacts()) {
            return;
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

                int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(HAS_PHONE_NUMBER)));

                if (hasPhoneNumber > 0) {
                    nameoutput.append(""+ name);
//                    output.append("\n First Name:" + name);

                    //This is to read multiple phone numbers associated with the same contact
                    Cursor phoneCursor = contentResolver.query(PhoneCONTENT_URI, null, Phone_CONTACT_ID + " = ?", new String[]{contact_id}, null);

                    while (phoneCursor.moveToNext()) {
                        int nameFieldColumnIndex = phoneCursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME);
                        namesync = phoneCursor.getString(nameFieldColumnIndex);
                        phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER));
                        phonesync = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER));
                        session.createLoginSession(Phone_Holder, Phone_Holder);
                        HttpURLp = "https://dtodxlogistics.com/Letstalk/contacts.php/?user="+Phone_Holder+"";
                        Toast.makeText(this, ""+namesync+"", Toast.LENGTH_SHORT).show();
                        output.append(""+ phonesync);

//                        UserRegisterFunctionphone(phonesync,Phone_Holder,namesync);

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


                // Add the contact to the ArrayList
//                contactList.add(output.toString());
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
