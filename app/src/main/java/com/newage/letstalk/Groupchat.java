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

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class Groupchat extends AppCompatActivity {
    public int REQUESTCODE = 1;
    TextView groupname;
    DataAdapter dataadapter;
    CustomAdapter customadapter;
    String getname,replacenamespace, nameofuser;
    FloatingActionButton add;
    ProgressBar progressBar;
    SessionManager session;
    String HttpURL = "https://globeexservices.com/letstalk/groups.php";
    HashMap<String,String> hashMap = new HashMap<>();
    HttpParse httpParse = new HttpParse();
    String finalResult;
    String HttpURLin = "https://globeexservices.com/letstalk/invite.php";
    public static String urladdress = "";
    TextView pdt;
    BufferedInputStream is;
    public static Boolean loadedurl;
    String line = null;
    String result = null;
    String[] id;
    String[] name;
    String[] email;
    String[] imagepath;
    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groupchat);
        Intent i = getIntent();
        groupname = (TextView)findViewById(R.id.user);
        pdt = (TextView)findViewById(R.id.pdt);
        getname = i.getStringExtra("group");
        replacenamespace = getname.replace(" ","_");
        Toast.makeText(Groupchat.this,replacenamespace,Toast.LENGTH_SHORT).show();

        groupname.setText(getname);
        session = new SessionManager(this);
        HashMap<String, String> user = session.getUserDetails();

        // name
        nameofuser = user.get(SessionManager.KEY_NAME);
        urladdress = "https://globeexservices.com/letstalk/group_contact.php/?user="+nameofuser+"&group="+replacenamespace+"";
        progressBar = (ProgressBar)findViewById(R.id.ProgressBar1);
        add = (FloatingActionButton)findViewById(R.id.fab);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                ntf.setVisibility(View.GONE);
//                list.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                // TODO Auto-generated method stub

                // using Intent for fetching contacts from phone-book
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
                startActivityForResult(intent, REQUESTCODE);


            }
        });
        setTitle("");
//        groupname.setText(dataadapter.getgroupname());
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_contact);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);



        listView = (ListView)findViewById(R.id.lview);

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
//       Toast.makeText(getActivity().getApplicationContext(),img ,Toast.LENGTH_SHORT).show();
//                startActivity(new Intent(getActivity().getApplicationContext(), Chat.class));

            }
        });

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitNetwork().build());
        this.loadedurl = false;
        collectData();

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
//        TextView userNumber = (TextView) findViewById(R.id.textViewc2);
        String typeNumber = (String) ContactsContract.CommonDataKinds.Phone
                .getTypeLabel(getResources(), type, "");
//        userNumber.setText(name + ": " + number + " " + typeNumber);

        UserRegisterFunction(number,name,nameofuser,getname);
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
                                    UserRegisterFunctioninvite(Phonec,nameofuser,Namec);

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
                finalResult = httpParse.postRequest(hashMap, HttpURL);

                return finalResult;
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
                finalResult = httpParse.postRequest(hashMap, HttpURLin);

                return finalResult;
            }
        }

        UserRegisterFunctionClass userRegisterFunctionClass = new UserRegisterFunctionClass();

        userRegisterFunctionClass.execute(Phonein,Nameofsender,Namef);
    }


    public void collectData() {
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

        //Content

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
