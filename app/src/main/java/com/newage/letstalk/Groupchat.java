package com.newage.letstalk;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.newage.letstalk.model.Group;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class Groupchat extends AppCompatActivity {
    String getname;
    ProgressBar progressBar;
    SessionManager session;
    String HttpURL = "https://globeexservices.com/letstalk/groups.php";
    HashMap<String,String> hashMap = new HashMap<>();
    HttpParse httpParse = new HttpParse();
    String HttpURLin = "https://globeexservices.com/letstalk/invite.php";
    TextView pdt;
    BufferedInputStream is;
    public static Boolean loadedurl;
    String line = null;
    String result = null;
    String[] id;
    String[] name;
    String[] email;
    String[] imagepath;


    String phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groupchat);

        TextView groupname = (TextView) findViewById(R.id.user);
        pdt = (TextView) findViewById(R.id.pdt);
        progressBar = (ProgressBar) findViewById(R.id.ProgressBar1);
        FloatingActionButton add = (FloatingActionButton) findViewById(R.id.fab);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_contact);
        setSupportActionBar(toolbar);

        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }

        Intent intent = getIntent();
        if(intent.getExtras() != null){
            Group group = (Group) intent.getSerializableExtra("group");
            getname = group.getName();
        }

        String replacenamespace = getname.replace(" ","_");
        Toast.makeText(Groupchat.this,replacenamespace,Toast.LENGTH_SHORT).show();

        groupname.setText(getname);

        session = new SessionManager(this);
        phoneNumber = session.getPhoneNumber();

        String urladdress = "https://globeexservices.com/letstalk/group_contact.php/?user="+phoneNumber+"&group="+replacenamespace+"";
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                // using Intent for fetching contacts from phone-book
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
                startActivityForResult(intent, 1);
            }
        });

        setTitle("");

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        ListView listView = (ListView)findViewById(R.id.lview);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg1, View v, int position,
                                    long arg3) {
                String nam = name[position].toString();
                String phone = email[position].toString();
                String img = imagepath[position].toString();
                Intent i = new Intent(Groupchat.this, Chat2.class);
                i.putExtra("user", nam);
                i.putExtra("img", img);
                i.putExtra("phone", phone);
                startActivity(i);
            }
        });

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitNetwork().build());
        this.loadedurl = false;
        collectData(urladdress);

        if(this.loadedurl == true) {
            CustomListView customListView = new CustomListView(Groupchat.this, name, email, imagepath);
            listView.setAdapter(customListView);
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
                    c = this.getContentResolver()
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
        String typeNumber = (String) ContactsContract.CommonDataKinds.Phone.getTypeLabel(getResources(), type, "");
        UserRegisterFunction(number,name,phoneNumber, getname);
        Toast.makeText(this, number, Toast.LENGTH_SHORT).show();
    }


    //sending phone to php webservice
    public void UserRegisterFunction(final String Phonec,final String Namec,final String Username, final String Groupname){

        class UserRegisterFunctionClass extends AsyncTask<String,Void,String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                Toast.makeText(Groupchat.this,"please wait", Toast.LENGTH_LONG).show();
            }

            @Override
            protected void onPostExecute(String httpResponseMsg) {

                super.onPostExecute(httpResponseMsg);

                Toast.makeText(Groupchat.this,httpResponseMsg.toString(), Toast.LENGTH_LONG).show();

                if(httpResponseMsg.toString().equalsIgnoreCase("true")){

                    Intent myIntent = new Intent(Groupchat.this, Groupchat.class);
                    myIntent.putExtra("group", getname);
                  startActivity(myIntent);
                    Toast.makeText(Groupchat.this,"Added", Toast.LENGTH_LONG).show();

                    progressBar.setVisibility(View.GONE);

                }else if(httpResponseMsg.toString().equalsIgnoreCase("he is not a user")){

                    AlertDialog.Builder alert = new AlertDialog.Builder(Groupchat.this);
                    alert.setMessage("Not a user: would you invite "+Namec+" to letstalk?");
                    alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                                @Override
                                public void onClick(DialogInterface dialog,int which){


                                }
                            }
                    );
                    alert.setPositiveButton("Invite", new DialogInterface.OnClickListener(){
                                @Override
                                public void onClick(DialogInterface dialog,int which){
                                    UserRegisterFunctioninvite(Phonec,phoneNumber,Namec);

                                }
                            }
                    );
                    alert.show();
                    progressBar.setVisibility(View.GONE);



                }else if(httpResponseMsg.toString().equalsIgnoreCase("Contact is already added")){
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(Groupchat.this,httpResponseMsg.toString(), Toast.LENGTH_LONG).show();

                }

            }

            @Override
            protected String doInBackground(String... params) {

                hashMap.put("number",params[0]);
                hashMap.put("name",params[1]);
                hashMap.put("userid",params[2]);
                hashMap.put("groupname",params[3]);
                return httpParse.postRequest(hashMap, HttpURL);
            }
        }

        UserRegisterFunctionClass userRegisterFunctionClass = new UserRegisterFunctionClass();

        userRegisterFunctionClass.execute(Phonec,Namec,Username,Groupname);
    }


    public void UserRegisterFunctioninvite(final String Phonein,final String Nameofsender,final String Namef){

        class UserRegisterFunctionClass extends AsyncTask<String,Void,String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                pdt.setText("fetching");
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected void onPostExecute(String httpResponseMsg) {

                super.onPostExecute(httpResponseMsg);

                pdt.setText("done!!!");
                progressBar.setVisibility(View.GONE);
                Toast.makeText(Groupchat.this,httpResponseMsg.toString(), Toast.LENGTH_LONG).show();




            }

            @Override
            protected String doInBackground(String... params) {

                hashMap.put("phonein",params[0]);
                hashMap.put("nameofsender",params[1]);
                hashMap.put("nameoff",params[2]);
                return  httpParse.postRequest(hashMap, HttpURLin);
            }
        }

        UserRegisterFunctionClass userRegisterFunctionClass = new UserRegisterFunctionClass();

        userRegisterFunctionClass.execute(Phonein,Nameofsender,Namef);
    }


    public void collectData(String urladdress) {
        try {
            URL url = new URL(urladdress);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            is = new BufferedInputStream(con.getInputStream());
            this.loadedurl = true;
        } catch (Exception ex) {
            ex.printStackTrace();
            this.loadedurl = false;
            Toast.makeText(Groupchat.this,"ehn no work",Toast.LENGTH_SHORT).show();
        }

        try{
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine())!= null){
                sb.append(line+"\n");
            }
            is.close();
            result = sb.toString();
            this.loadedurl = true;

        }catch (Exception ex){
            ex.printStackTrace();
        }
//JSON
        try {
            JSONArray ja = new JSONArray(result);
            JSONObject jo = null;
            id = new String[ja.length()];
            name = new String[ja.length()];
            email = new String[ja.length()];
            imagepath = new String[ja.length()];

//   Toast.makeText(getActivity().getApplicationContext(),String.valueOf(ja.length()),Toast.LENGTH_SHORT).show();
            for(int i = 0; i<=ja.length(); i++){
                jo = ja.getJSONObject(i);
                id[i] = jo.getString("id");
                name[i] = jo.getString("name");
                email[i] = jo.getString("phone");
                imagepath[i] = jo.getString("dp");
            }
        }catch (Exception ex){

            ex.printStackTrace();

        }
    }
}