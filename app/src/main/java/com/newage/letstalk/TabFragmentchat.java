package com.newage.letstalk;

/**
 * Created by Newage_android on 5/3/2018.
 */

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import java.util.HashMap;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class TabFragmentchat extends Fragment {
FloatingActionButton chat;
    public int REQUESTCODE = 1;
    Button register, log_in;
    EditText First_Name, Last_Name, Email, Password ;
    String F_Name_Holder, L_Name_Holder, EmailHolder, PasswordHolder,phonetoserver;
    String finalResult, nameofuser;
    String HttpURL = "https://globeexservices.com/letstalk/check_contact.php";
    String HttpURLin = "https://globeexservices.com/letstalk/invite.php";
    Boolean CheckEditText ;
    ProgressDialog progressDialog;
    HashMap<String,String> hashMap = new HashMap<>();
    HttpParse httpParse = new HttpParse();
    TextView pdt;
    LinearLayout ntf;
    TextView ntftxt;
    SessionManager session;

    ProgressBar progressBar;
    public static String urladdress = "";
    String[] id;
    String[] name;
    String[] email;
    String[] imagepath;
    ListView listView;
    BufferedInputStream is;
    String line = null;
    String result = null;
    ListView list;
   public static Boolean loadedurl;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_fragment_chat, container, false);
        pdt = (TextView)view.findViewById(R.id.pdt);
        ntftxt = (TextView)view.findViewById(R.id.ntftxt);
        list = (ListView)view.findViewById(R.id.lview);
       ntf = (LinearLayout) view.findViewById(R.id.notify);
        session = new SessionManager(getActivity().getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();

        // name
        nameofuser = user.get(SessionManager.KEY_NAME);
        urladdress = "https://globeexservices.com/letstalk/chat_contacts.php/?user="+nameofuser+"";
//        final TextView textViewToChange = (TextView) findViewById(R.id.welcome);

        progressBar = (ProgressBar) view.findViewById(R.id.ProgressBar1);
        chat = (FloatingActionButton) view.findViewById(R.id.fab);
        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ntf.setVisibility(View.GONE);
                list.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                // TODO Auto-generated method stub
                // using Intent for fetching contacts from phone-book
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
                startActivityForResult(intent, REQUESTCODE);


            }
        });


        listView = (ListView) view.findViewById(R.id.lview);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg1, View v, int position,
                                    long arg3) {
                String nam = name[position].toString();
                String phone = email[position].toString();
                String img = imagepath[position].toString();
                Intent i = new Intent(getActivity().getApplicationContext(), Chat2.class);
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
            CustomListView customListView = new CustomListView(getActivity(), name, email, imagepath);
            listView.setAdapter(customListView);
        }
        return view;


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
                    c = getActivity().getContentResolver()
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

        UserRegisterFunction(number,name,nameofuser);
        Toast.makeText(getActivity().getApplicationContext(), number, Toast.LENGTH_SHORT).show();
    }




    public void UserRegisterFunction(final String Phonec,final String Namec,final String Username){

        class UserRegisterFunctionClass extends AsyncTask<String,Void,String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

               pdt.setText("fetching");
            }

            @Override
            protected void onPostExecute(String httpResponseMsg) {

                super.onPostExecute(httpResponseMsg);

                pdt.setText("done!!!");
                Toast.makeText(getActivity().getApplicationContext(),httpResponseMsg.toString(), Toast.LENGTH_LONG).show();

                if(httpResponseMsg.toString().equalsIgnoreCase("true")){

                    Intent myIntent = new Intent(getActivity(), Dashboard.class);

                    getActivity().startActivity(myIntent);
                    Toast.makeText(getActivity(),"Added", Toast.LENGTH_LONG).show();
                    list.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);

                }else if(httpResponseMsg.toString().equalsIgnoreCase("he is not a user")){

                    AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
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



                    list.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);

                ntf.setVisibility(View.GONE);
                    ntftxt.setText("not yet registered, would you like to invite"+Namec+" to letstalk?");

                }else if(httpResponseMsg.toString().equalsIgnoreCase("Contact is already added")){
                    list.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getActivity().getApplicationContext(),httpResponseMsg.toString(), Toast.LENGTH_LONG).show();

                }

            }

            @Override
            protected String doInBackground(String... params) {

                hashMap.put("phonec",params[0]);
                hashMap.put("nameec",params[1]);
                hashMap.put("username",params[2]);
                finalResult = httpParse.postRequest(hashMap, HttpURL);

                return finalResult;
            }
        }

        UserRegisterFunctionClass userRegisterFunctionClass = new UserRegisterFunctionClass();

        userRegisterFunctionClass.execute(Phonec,Namec,Username);
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
                Toast.makeText(getActivity().getApplicationContext(),httpResponseMsg.toString(), Toast.LENGTH_LONG).show();




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
Toast.makeText(getActivity().getApplicationContext(),"ehn no work",Toast.LENGTH_SHORT).show();



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
