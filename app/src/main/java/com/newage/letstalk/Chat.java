package com.newage.letstalk;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.List;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.*;
import de.hdodenhof.circleimageview.CircleImageView;
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
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.net.ssl.HttpsURLConnection;

public class Chat extends AppCompatActivity {
    ProgressBar progressBar;
    List<DataAdapter> ListOfdataAdapter;
    String Message_Holder;
    EditText message;
    RecyclerView recyclerView;
    Bitmap bitmap;
    private String m_Text = "";
    public static String HTTP_JSON_URL = "";
    String ImageName = "image_name" ;
    String imgstatus ="imgstatus";
    String ImagePath = "image_path" ;
    String HttpURLauto = "https://dtodxlogistics.com/Letstalk/autoresponder.php";
    String HttpURLdisauto = "https://dtodxlogistics.com/Letstalk/disableautoresponder.php";
  String ServerUploadPath ="https://dtodxlogistics.com/Letstalk/sendimage.php" ;
    boolean check = true;
    String Image_Name_JSON = "messages";
    String Image_Sender_JSON = "sender";

    String Image_URL_JSON = "dp";
    private ProgressDialog pDialog;
    JsonArrayRequest RequestOfJSonArray ;

    RequestQueue requestQueue ;
    ImageButton image;
    View view ;
    // Save state
    private Parcelable recyclerViewState;

    int RecyclerViewItemPosition ;

    RecyclerView.LayoutManager layoutManagerOfrecyclerView;

    RecyclerView.Adapter recyclerViewadapter;
    LinearLayout lin;

    ArrayList<String> ImageTitleNameArrayListForClick;

    String user,img,phone,nameofuser;
    TextView username,userphone,prog;
    CircleImageView imageView;
    SessionManager session;
    ImageButton imageButtonsend;
public int alenght,blenght, newalenght;

    Button register, log_in;
    EditText First_Name, Last_Name, Email, Password ;
    String F_Name_Holder, L_Name_Holder, EmailHolder, PasswordHolder;
    String finalResult ;
   public static  String HttpURL = "";
    Boolean CheckEditText ;
    ProgressDialog progressDialog;
    HashMap<String,String> hashMap = new HashMap<>();
    HttpParse httpParse = new HttpParse();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Intent i = getIntent();
        username = (TextView) findViewById(R.id.user);
        prog = (TextView) findViewById(R.id.prog);
        userphone = (TextView) findViewById(R.id.user_num);
        image = (ImageButton) findViewById(R.id.imageButtoncamera);
        progressBar = (ProgressBar)findViewById(R.id.pb);
//        progressBar.setVisibility(View.VISIBLE);
        user= i.getStringExtra("user");
        img= i.getStringExtra("img");
        phone= i.getStringExtra("phone");
        lin = (LinearLayout) findViewById(R.id.lin);
        if(phone.equalsIgnoreCase("bot")){
            lin.setVisibility(View.GONE);
        }
        message = (EditText)findViewById(R.id.editTextmessage);
        username.setText(user);
        userphone.setText(phone);
        imageView = (de.hdodenhof.circleimageview.CircleImageView)findViewById(R.id.user_dp);
        Picasso.with(Chat.this).load(img)
                .placeholder(R.mipmap.defaultuser).error(R.mipmap.defaultuser)
                .into(imageView);
        setTitle("");
        imageButtonsend = (ImageButton)findViewById(R.id.imageButtonsend);
        imageButtonsend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckEditTextIsEmptyOrNot();
               if(CheckEditText){
                  //update arraylist with the message
//                   recyclerViewadapter.notifyDataSetChanged();

                   updatearray(Message_Holder);
                   message.setText("");
               }else{
                   Toast.makeText(Chat.this, "Empty message", Toast.LENGTH_LONG).show();

               }
            }

        });

        image.setVisibility(View.GONE);

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent();

                intent.setType("image/*");

                intent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(intent, "Select Image From Gallery"), 1);
            }

        });
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_contact);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        session = new SessionManager(getApplicationContext());


        /**
         * Call this function whenever you want to check user login
         * This will redirect user to LoginActivity is he is not
         * logged in
         * */
//        session.checkLogin();
        HashMap<String, String> user = session.getUserDetails();
       nameofuser = user.get(SessionManager.KEY_NAME);


        HTTP_JSON_URL = "https://dtodxlogistics.com/Letstalk/messages.php/?frnd="+phone+"&user="+nameofuser+"";
        HttpURL = "https://dtodxlogistics.com/Letstalk/sendmessage.php/?frnd="+phone+"&user="+nameofuser+"";

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        this.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        ImageTitleNameArrayListForClick = new ArrayList<>();

        ListOfdataAdapter = new ArrayList<>();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview1);

        recyclerView.setHasFixedSize(true);

        layoutManagerOfrecyclerView = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(layoutManagerOfrecyclerView);

        JSON_HTTP_CALL();








        // Implementing Click Listener on RecyclerView.
        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {

            GestureDetector gestureDetector = new GestureDetector(Chat.this, new GestureDetector.SimpleOnGestureListener() {

                @Override public boolean onSingleTapUp(MotionEvent motionEvent) {

                    return true;
                }

            });
            @Override
            public boolean onInterceptTouchEvent(RecyclerView Recyclerview, MotionEvent motionEvent) {

                view = Recyclerview.findChildViewUnder(motionEvent.getX(), motionEvent.getY());

                if(view != null && gestureDetector.onTouchEvent(motionEvent)) {

                    //Getting RecyclerView Clicked Item value.
                    RecyclerViewItemPosition = Recyclerview.getChildAdapterPosition(view);

                    AlertDialog.Builder builder = new AlertDialog.Builder(Chat.this);
                    builder.setTitle("Edit");
                    builder.setMessage("edit message below");
// Set up the input
                    final EditText input = new EditText(Chat.this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                    input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
//                    input.setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)});
                    input.setText(ImageTitleNameArrayListForClick.get(RecyclerViewItemPosition));
                    builder.setView(input);

// Set up the buttons
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
//                            m_Text = input.getText().toString();
//                        Otp_Holder = m_Text;
//                        UserRegisterFunction(Name_Holder, Phone_Holder, Otp_Holder);
//
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
                    // Showing RecyclerView Clicked Item value using Toast.
                    Toast.makeText(Chat.this, ImageTitleNameArrayListForClick.get(RecyclerViewItemPosition), Toast.LENGTH_LONG).show();
                }

                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView Recyclerview, MotionEvent motionEvent) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });


    }

    @Override
    public void onActivityResult(int RC, int RQC, Intent I) {

        super.onActivityResult(RC, RQC, I);

        if (RC == 1 && RQC == RESULT_OK && I != null && I.getData() != null) {

            Uri uri = I.getData();

            try {

                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);




            } catch (IOException e) {

                e.printStackTrace();
            }

            ImageUploadToServerFunction();
        }
    }


    public void ImageUploadToServerFunction(){

        ByteArrayOutputStream byteArrayOutputStreamObject ;

        byteArrayOutputStreamObject = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStreamObject);

        byte[] byteArrayVar = byteArrayOutputStreamObject.toByteArray();

        final String ConvertImage = Base64.encodeToString(byteArrayVar, Base64.DEFAULT);

        class AsyncTaskUploadClass extends AsyncTask<Void,Void,String> {

            @Override
            protected void onPreExecute() {

                super.onPreExecute();
                prog.setText("sending");
            }

            @Override
            protected void onPostExecute(String string1) {

                super.onPostExecute(string1);
                prog.setText("sending");
                Toast.makeText(Chat.this,string1,Toast.LENGTH_LONG).show();



            }



            @Override
            protected String doInBackground(Void... params) {

                Chat.ImageProcessClass imageProcessClass = new Chat.ImageProcessClass();

                HashMap<String,String> HashMapParams = new HashMap<String,String>();

                HashMapParams.put(ImageName, nameofuser);
                HashMapParams.put(imgstatus, phone);

                HashMapParams.put(ImagePath, ConvertImage);

                String FinalData = imageProcessClass.ImageHttpRequest(ServerUploadPath, HashMapParams);

                return FinalData;
            }
        }
        AsyncTaskUploadClass AsyncTaskUploadClassOBJ = new AsyncTaskUploadClass();

        AsyncTaskUploadClassOBJ.execute();
    }


    public class ImageProcessClass{

        public String ImageHttpRequest(String requestURL,HashMap<String, String> PData) {

            StringBuilder stringBuilder = new StringBuilder();

            try {

                URL url;
                HttpURLConnection httpURLConnectionObject ;
                OutputStream OutPutStream;
                BufferedWriter bufferedWriterObject ;
                BufferedReader bufferedReaderObject ;
                int RC ;

                url = new URL(requestURL);

                httpURLConnectionObject = (HttpURLConnection) url.openConnection();

                httpURLConnectionObject.setReadTimeout(19000);

                httpURLConnectionObject.setConnectTimeout(19000);

                httpURLConnectionObject.setRequestMethod("POST");

                httpURLConnectionObject.setDoInput(true);

                httpURLConnectionObject.setDoOutput(true);

                OutPutStream = httpURLConnectionObject.getOutputStream();

                bufferedWriterObject = new BufferedWriter(

                        new OutputStreamWriter(OutPutStream, "UTF-8"));

                bufferedWriterObject.write(bufferedWriterDataFN(PData));

                bufferedWriterObject.flush();

                bufferedWriterObject.close();

                OutPutStream.close();

                RC = httpURLConnectionObject.getResponseCode();

                if (RC == HttpsURLConnection.HTTP_OK) {

                    bufferedReaderObject = new BufferedReader(new InputStreamReader(httpURLConnectionObject.getInputStream()));

                    stringBuilder = new StringBuilder();

                    String RC2;

                    while ((RC2 = bufferedReaderObject.readLine()) != null){

                        stringBuilder.append(RC2);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return stringBuilder.toString();
        }

        private String bufferedWriterDataFN(HashMap<String, String> HashMapParams) throws UnsupportedEncodingException {

            StringBuilder stringBuilderObject;

            stringBuilderObject = new StringBuilder();

            for (Map.Entry<String, String> KEY : HashMapParams.entrySet()) {

                if (check)

                    check = false;
                else
                    stringBuilderObject.append("&");

                stringBuilderObject.append(URLEncoder.encode(KEY.getKey(), "UTF-8"));

                stringBuilderObject.append("=");

                stringBuilderObject.append(URLEncoder.encode(KEY.getValue(), "UTF-8"));
            }

            return stringBuilderObject.toString();
        }

    }
//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        recyclerViewState = recyclerView.getLayoutManager().onSaveInstanceState();
//
//// Restore state
//        recyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);
//
//
//    }
//    @Override
//    protected void onRestoreInstanceState(Bundle savedInstanceState) {
//        super.onRestoreInstanceState(savedInstanceState);
//        recyclerViewState = recyclerView.getLayoutManager().onSaveInstanceState();
//
//// Restore state
//        recyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);
//
//    }

    public void CheckEditTextIsEmptyOrNot(){

        Message_Holder = message.getText().toString();





        if(TextUtils.isEmpty(Message_Holder))
        {

            CheckEditText = false;

        }
        else {

            CheckEditText = true ;
        }

    }

    public void JSON_HTTP_CALL(){

        RequestOfJSonArray = new JsonArrayRequest(HTTP_JSON_URL,

                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        ParseJSonResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.setVisibility(View.GONE);
                    }
                });

        requestQueue = Volley.newRequestQueue(Chat.this);

        requestQueue.add(RequestOfJSonArray);
    }

    public void ParseJSonResponse(JSONArray array){

        for(int i = 0; i<array.length(); i++) {

            DataAdapter GetDataAdapter2 = new DataAdapter();

            JSONObject json = null;
            try {

                json = array.getJSONObject(i);

                GetDataAdapter2.setImageTitle(json.getString(Image_Name_JSON));
                GetDataAdapter2.setImageSender(json.getString(Image_Sender_JSON));
                GetDataAdapter2.setsession(nameofuser);

                // Adding image title name in array to display on RecyclerView click event.
                ImageTitleNameArrayListForClick.add(json.getString(Image_Name_JSON));



                GetDataAdapter2.setImageUrl(json.getString(Image_URL_JSON));

            } catch (JSONException e) {

                e.printStackTrace();
            }
            ListOfdataAdapter.add(GetDataAdapter2);
        }
        progressBar.setVisibility(View.GONE);
        recyclerViewadapter = new RecyclerViewAdapter(ListOfdataAdapter, this);

        recyclerView.setAdapter(recyclerViewadapter);
       Updatearray firstarray = new Updatearray();
        firstarray.setfirst(array.length());
        alenght = firstarray.first();

//        int last = recyclerView.getAdapter().getItemCount()-1;
//        recyclerView.smoothScrollToPosition(last);
//        recyclerViewadapter.notifyDataSetChanged();

        //Declare the timer
        Timer t = new Timer();
//Set the schedule function and rate
        t.scheduleAtFixedRate(new TimerTask() {
                                  @Override
                                  public void run() {
                                      //Called each time when 1000 milliseconds (1 second) (the period parameter)
                                      JSON_HTTP_CALL3();
                                  }

                              },
//Set how long before to start calling the TimerTask (in milliseconds)
                0,
//Set the amount of time between each execution (in milliseconds)
                5000);
    }


    public void updatearray(final String message){
if(message.equalsIgnoreCase("image")){

}else {
    DataAdapter GetDataAdapter2 = new DataAdapter();


    GetDataAdapter2.setImageTitle(message);
    GetDataAdapter2.setImageSender("none");
    GetDataAdapter2.setsession(nameofuser);

    // Adding image title name in array to display on RecyclerView click event.
    ImageTitleNameArrayListForClick.add("https://dtodxlogistics.com/Letstalk/images/07038436255.png");

    GetDataAdapter2.setImageUrl("https://dtodxlogistics.com/Letstalk/images/07038436255.png");


    ListOfdataAdapter.add(GetDataAdapter2);

    progressBar.setVisibility(View.GONE);
    recyclerViewadapter = new RecyclerViewAdapter(ListOfdataAdapter, this);

    recyclerView.setAdapter(recyclerViewadapter);


    int last = recyclerView.getAdapter().getItemCount() - 1;
    recyclerView.smoothScrollToPosition(last);
    recyclerViewadapter.notifyDataSetChanged();
    UserRegisterFunction(message, nameofuser, phone);
}

    }


    public void UserRegisterFunction(final String Message, final String Sender, final String Receiver){

        class UserRegisterFunctionClass extends AsyncTask<String,Void,String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

//                progressDialog = ProgressDialog.show(Chat.this,"Loading Data",null,true,true);
                prog.setText("sending");
            }

            @Override
            protected void onPostExecute(String httpResponseMsg) {

                super.onPostExecute(httpResponseMsg);
                prog.setText("sent");
//                progressDialog.dismiss();
                JSON_HTTP_CALL2();
                Toast.makeText(Chat.this,httpResponseMsg.toString(), Toast.LENGTH_LONG).show();

            }

            @Override
            protected String doInBackground(String... params) {

                hashMap.put("messages",params[0]);
                hashMap.put("sender",params[1]);
                hashMap.put("reciever",params[2]);
                finalResult = httpParse.postRequest(hashMap, HttpURL);
                return finalResult;
            }
        }

        UserRegisterFunctionClass userRegisterFunctionClass = new UserRegisterFunctionClass();

        userRegisterFunctionClass.execute(Message,Sender,Receiver);
    }

    public void JSON_HTTP_CALL2(){

        RequestOfJSonArray = new JsonArrayRequest(HTTP_JSON_URL,

                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        ParseJSonResponse2(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.setVisibility(View.GONE);
                    }
                });

        requestQueue = Volley.newRequestQueue(Chat.this);

        requestQueue.add(RequestOfJSonArray);
    }

    public void ParseJSonResponse2(JSONArray array2){
        ListOfdataAdapter.clear();
        for(int i = 0; i<array2.length(); i++) {

            DataAdapter GetDataAdapter2 = new DataAdapter();

            JSONObject json = null;
            try {

                json = array2.getJSONObject(i);

                GetDataAdapter2.setImageTitle(json.getString(Image_Name_JSON));
                GetDataAdapter2.setImageSender(json.getString(Image_Sender_JSON));
                GetDataAdapter2.setsession(nameofuser);

                // Adding image title name in array to display on RecyclerView click event.
                ImageTitleNameArrayListForClick.add(json.getString(Image_Name_JSON));



                GetDataAdapter2.setImageUrl(json.getString(Image_URL_JSON));

            } catch (JSONException e) {

                e.printStackTrace();
            }

            ListOfdataAdapter.add(GetDataAdapter2);
        }
        progressBar.setVisibility(View.GONE);
        recyclerViewadapter = new RecyclerViewAdapter(ListOfdataAdapter, this);

        recyclerView.setAdapter(recyclerViewadapter);
        int last = recyclerView.getAdapter().getItemCount()-1;
        recyclerView.smoothScrollToPosition(last);
        recyclerViewadapter.notifyDataSetChanged();
        Updatearray latestarray = new Updatearray();
        latestarray.setlatest(array2.length());
        blenght = latestarray.latest();
        newalenght = blenght - alenght;
        Updatearray firstarray = new Updatearray();
        firstarray.setfirst(array2.length());
        alenght = firstarray.first();
        Toast.makeText(Chat.this, String.valueOf(newalenght), Toast.LENGTH_LONG).show();
    }






    public void JSON_HTTP_CALL3(){

        RequestOfJSonArray = new JsonArrayRequest(HTTP_JSON_URL,

                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        ParseJSonResponse3(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.setVisibility(View.GONE);
                    }
                });

        requestQueue = Volley.newRequestQueue(Chat.this);

        requestQueue.add(RequestOfJSonArray);
    }

    public void ParseJSonResponse3(JSONArray array3){
//        ListOfdataAdapter.clear();

        Updatearray latestarray = new Updatearray();
        latestarray.setlatest(array3.length());
        blenght = latestarray.latest();
        newalenght = blenght - alenght;
        Toast.makeText(Chat.this, ""+String.valueOf(alenght)+" & "+ String.valueOf(newalenght)+"", Toast.LENGTH_LONG).show();
        for(int i = 0; i<array3.length(); i++) {

            DataAdapter GetDataAdapter2 = new DataAdapter();

            JSONObject json = null;
            try {

                json = array3.getJSONObject(i);

                GetDataAdapter2.setImageTitle(json.getString(Image_Name_JSON));
                GetDataAdapter2.setImageSender(json.getString(Image_Sender_JSON));
                GetDataAdapter2.setsession(nameofuser);

                // Adding image title name in array to display on RecyclerView click event.
                ImageTitleNameArrayListForClick.add(json.getString(Image_Name_JSON));



                GetDataAdapter2.setImageUrl(json.getString(Image_URL_JSON));

            } catch (JSONException e) {

                e.printStackTrace();
            }
if(i >=alenght) {





//    GetDataAdapter2.setImageTitle("Added");
//    GetDataAdapter2.setImageSender("none");
//    GetDataAdapter2.setsession(nameofuser);
//
//    // Adding image title name in array to display on RecyclerView click event.
//    ImageTitleNameArrayListForClick.add("https://dtodxlogistics.com/Letstalk/images/07038436255.png");
//
//    GetDataAdapter2.setImageUrl("https://dtodxlogistics.com/Letstalk/images/07038436255.png");


    ListOfdataAdapter.add(GetDataAdapter2);



    int last = recyclerView.getAdapter().getItemCount()-1;
    recyclerView.smoothScrollToPosition(last);
    recyclerViewadapter.notifyDataSetChanged();
}
        }


        Updatearray firstarray = new Updatearray();
        firstarray.setfirst(array3.length());
        alenght = firstarray.first();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.popup_menu_chat, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.setautoresponse) {
            AlertDialog.Builder builder = new AlertDialog.Builder(Chat.this);
            builder.setTitle("Auto-response");
            builder.setMessage("set a message as auto-responder");
// Set up the input
            final EditText input = new EditText(Chat.this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_TEXT);
            input.setFilters(new InputFilter[]{new InputFilter.LengthFilter(35)});
            builder.setView(input);

// Set up the buttons
            builder.setPositiveButton("Set", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    m_Text = input.getText().toString();
                    UserRegisterFunctionauto(nameofuser, m_Text);
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
            return true;
        }else  if (id == R.id.unsetautoresponse) {

            UserRegisterFunctiondisauto(nameofuser);
        }

        return super.onOptionsItemSelected(item);
    }




    public void UserRegisterFunctionauto(final String Phone, final String Msg){

        class UserRegisterFunctionClassauto extends AsyncTask<String,Void,String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                pDialog = new ProgressDialog(Chat.this);
                pDialog.setMessage("please wait...");
                pDialog.setCancelable(false);
                pDialog.show();


//                progressDialog = ProgressDialog.show(Register.this,"Connecting...",null,true,true);
//                progressDialog.setCanceledOnTouchOutside(false);
//                progressDialog.setCancelable(false);
            }

            @Override
            protected void onPostExecute(String httpResponseMsg) {

                super.onPostExecute(httpResponseMsg);
                Toast.makeText(Chat.this, httpResponseMsg.toString(), Toast.LENGTH_LONG).show();

                pDialog.cancel();
            }

            @Override
            protected String doInBackground(String... params) {

                hashMap.put("user",params[0]);
                hashMap.put("message",params[1]);
                finalResult = httpParse.postRequest(hashMap, HttpURLauto);

                return finalResult;
            }
        }

        UserRegisterFunctionClassauto userRegisterFunctionClassauto = new UserRegisterFunctionClassauto();

        userRegisterFunctionClassauto.execute(Phone,Msg);
    }





    public void UserRegisterFunctiondisauto(final String Phone){

        class UserRegisterFunctionClassdisauto extends AsyncTask<String,Void,String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                pDialog = new ProgressDialog(Chat.this);
                pDialog.setMessage("please wait...");
                pDialog.setCancelable(false);
                pDialog.show();


//                progressDialog = ProgressDialog.show(Register.this,"Connecting...",null,true,true);
//                progressDialog.setCanceledOnTouchOutside(false);
//                progressDialog.setCancelable(false);
            }

            @Override
            protected void onPostExecute(String httpResponseMsg) {

                super.onPostExecute(httpResponseMsg);
                Toast.makeText(Chat.this, httpResponseMsg.toString(), Toast.LENGTH_LONG).show();

                pDialog.cancel();
            }

            @Override
            protected String doInBackground(String... params) {

                hashMap.put("user",params[0]);

                finalResult = httpParse.postRequest(hashMap, HttpURLdisauto);

                return finalResult;
            }
        }

        UserRegisterFunctionClassdisauto userRegisterFunctionClassdisauto = new UserRegisterFunctionClassdisauto();

        userRegisterFunctionClassdisauto.execute(Phone);
    }

}
