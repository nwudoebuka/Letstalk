package com.newage.letstalk;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

public class Sms extends AppCompatActivity {
ImageButton add;
    TextView num;
    public int REQUESTCODE = 1;
    String finalResult, msgs, nums;
    String HttpURLin = "https://globeexservices.com/letstalk/freesms.php";
    Boolean CheckEditText ;
    ProgressDialog progressDialog;
    HashMap<String,String> hashMap = new HashMap<>();
    HttpParse httpParse = new HttpParse();
    Button send;
    EditText msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);
        num = (TextView) findViewById(R.id.smsnumber);
        send = (Button) findViewById(R.id.send);
        msg = (EditText) findViewById(R.id.msg);
        msgs = msg.getText().toString();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_contact);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Dashboard.class));
            }
        });

        add = (ImageButton) findViewById(R.id.imageButton);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO Auto-generated method stub
                // using Intent for fetching contacts from phone-book
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
                startActivityForResult(intent, REQUESTCODE);
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkempty();

            }
        });
    }

public void checkempty(){
    msgs = msg.getText().toString();
    if (nums != null && !nums.isEmpty() && !nums.equals("null") && msgs != null && !msgs.isEmpty() && !msgs.equals("null")){
        UserRegisterFunctionmsg(nums,msgs);

    }else{


        Toast.makeText(Sms.this,"Please fill all fields", Toast.LENGTH_LONG).show();
    }


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {
            Uri uri = data.getData();
            Log.i("data", uri.toString());
            if (uri != null) {
                Cursor c = null;
                try {
                    c = Sms.this.getContentResolver()
                            .query(uri,
                                    new String[] {
                                            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                                            ContactsContract.CommonDataKinds.Phone.NUMBER,
                                            ContactsContract.CommonDataKinds.Phone.TYPE },
                                    null, null, null);

                    if (c != null && c.moveToFirst()) {
                        String name = c.getString(0);
                        String number = c.getString(1);
                        int type = c.getInt(2);

                        showSelectedNumber(name, number, type);
                    }
                } finally {
                    if (c != null) {
                        c.close();
                    }
                }
            }
        }
    }


    public void showSelectedNumber(String name, String number, int type) {
//        TextView userNumber = (TextView) findViewById(R.id.textViewc2);
        String typeNumber = (String) ContactsContract.CommonDataKinds.Phone
                .getTypeLabel(getResources(), type, "");
        num.setText("to "+ number +"");
        nums = number;
//        userNumber.setText(name + ": " + number + " " + typeNumber);


    }



    public void UserRegisterFunctionmsg(final String Phonemsg,final String Message){

        class UserRegisterFunctionClass extends AsyncTask<String,Void,String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                progressDialog = ProgressDialog.show(Sms.this,"Sending...",null,true,true);
                progressDialog.setCancelable(false);
                progressDialog.setCanceledOnTouchOutside(false);
            }

            @Override
            protected void onPostExecute(String httpResponseMsg) {

                super.onPostExecute(httpResponseMsg);

               progressDialog.dismiss();
                msg.setText("");
                Toast.makeText(Sms.this,httpResponseMsg.toString(), Toast.LENGTH_LONG).show();




            }

            @Override
            protected String doInBackground(String... params) {

                hashMap.put("phone",params[0]);
                hashMap.put("message",params[1]);

                finalResult = httpParse.postRequest(hashMap, HttpURLin);

                return finalResult;
            }
        }

        UserRegisterFunctionClass userRegisterFunctionClass = new UserRegisterFunctionClass();

        userRegisterFunctionClass.execute(Phonemsg,Message);
    }

}
