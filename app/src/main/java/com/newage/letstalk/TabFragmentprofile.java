package com.newage.letstalk;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import java.io.IOException;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.EditText;
import android.widget.ImageView;
import android.net.Uri;
import android.provider.MediaStore;
import java.io.BufferedReader;
import java.net.URLEncoder;
import java.util.Map;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;
import java.io.UnsupportedEncodingException;
import android.util.Base64;
import android.view.View;
import android.widget.Button;

import static android.app.Activity.RESULT_OK;


/**
 * Created by Newage_android on 5/3/2018.
 */
public class TabFragmentprofile extends Fragment {

    HttpResponse httpResponse;
    Button button;
    TextView textView;
    JSONObject jsonObject = null ;
    String StringHolder = "" ;
    ProgressBar progressBar;
    // Adding HTTP Server URL to string variable.
    public static String HttpURL = "";
    public static String imageUrl = "";
    SessionManager session;
    Bitmap bitmap;
    Button update;
   public static String HTTP_JSON_URL = "";
    boolean check = true;

    Button SelectImageGallery, UploadImageServer;

    ImageView imageViewb4;
    CircleImageView imageView;

    EditText imageName;

    ProgressDialog progressDialog ;

    String GetImageNameEditText, Getimgcheck;
    String Image_Name_JSON = "user_status";
    String ImageName = "image_name" ;
    String imgstatus ="imgstatus";
    String ImagePath = "image_path" ;
    TextView myText;
    Boolean imagecheck;
    public static String ServerUploadPath ="" ;

    JsonArrayRequest RequestOfJSonArray ;

    RequestQueue requestQueue ;

    View view ;

    ProgressDialog pd;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_fragment_profile, container, false);
        textView = (TextView)view.findViewById(R.id.status);
        progressBar = (ProgressBar)view.findViewById(R.id.pbprofile);

        session = new SessionManager(getActivity().getApplicationContext());



        /**
         * Call this function whenever you want to check user login
         * This will redirect user to LoginActivity is he is not
         * logged in
         * */


        // get user data from session
        HashMap<String, String> user = session.getUserDetails();

        // name
        String nameofuser = user.get(SessionManager.KEY_NAME);
        //codes go here
        HttpURL = "https://dtodxlogistics.com/Letstalk/userdp.php/?user="+nameofuser+"";
        HTTP_JSON_URL = "https://dtodxlogistics.com/Letstalk/userdp.php/?user="+nameofuser+"";
        ServerUploadPath ="https://dtodxlogistics.com/Letstalk/updateprofile.php/?user="+nameofuser+"" ;
        // Showing progress bar on button click.
        imageUrl = "https://dtodxlogistics.com/Letstalk/images/"+nameofuser+".png";
        //Calling GetDataFromServerIntoTextView method to Set JSon MySQL data into TextView.
//        new GetDataFromServerIntoTextView(getActivity().getApplicationContext()).execute();
        JSON_HTTP_CALL();
        imageView = (CircleImageView) view.findViewById(R.id.dp);
        imageName = (EditText)view.findViewById(R.id.status_input);

        update = (Button)view.findViewById(R.id.update);
        Picasso.with(getActivity().getApplicationContext()).load(imageUrl)
                .placeholder(R.mipmap.defaultuser).error(R.mipmap.defaultuser)
                .into(imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent();

                intent.setType("image/*");

                intent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(intent, "Select Image From Gallery"), 1);



            }
        });
        myText = (TextView) view.findViewById(R.id.wait );
        imagecheck=false;
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(imagecheck) {
                    GetImageNameEditText = imageName.getText().toString();
                    Getimgcheck="image";
                    ImageUploadToServerFunction();

                }else{
                    bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.defaultuser);
                    GetImageNameEditText = imageName.getText().toString();
                    imageView.setImageBitmap(bitmap);
                    Getimgcheck="empty";
                    ImageUploadToServerFunction();
                }


            }
        });


        return view;
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
                textView.setText(jsonObject.getString("user_status"));
                imageUrl=jsonObject.getString("dp");

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            //Hiding progress bar after done loading TextView.
            progressBar.setVisibility(View.GONE);


        }
    }

    @Override
    public void onActivityResult(int RC, int RQC, Intent I) {

        super.onActivityResult(RC, RQC, I);

        if (RC == 1 && RQC == RESULT_OK && I != null && I.getData() != null) {

            Uri uri = I.getData();

            try {

                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);

                imageView.setImageBitmap(bitmap);
                imagecheck=true;

            } catch (IOException e) {

                e.printStackTrace();
            }
        }else{

            //below code changes the bitmap to a drawable image
            bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.defaultuser);
            imageView.setImageBitmap(bitmap);
            imagecheck=false;
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

                myText.setVisibility(View.VISIBLE);
                Animation anim = new AlphaAnimation(0.0f, 1.0f);
                anim.setDuration(50); //You can manage the blinking time with this parameter
                anim.setStartOffset(20);
                anim.setRepeatMode(Animation.REVERSE);
                anim.setRepeatCount(Animation.INFINITE);
                myText.startAnimation(anim);
                update.setVisibility(View.INVISIBLE);
            }

            @Override
            protected void onPostExecute(String string1) {

                super.onPostExecute(string1);

                // Dismiss the progress dialog after done uploading.
                if(string1.equalsIgnoreCase("Profile updated.")){
                    Picasso.with(getActivity().getApplicationContext()).load(imageUrl)
                            .placeholder(R.mipmap.defaultuser).error(R.mipmap.defaultuser)
                            .into(imageView);
                    Intent intent = new Intent(getActivity().getApplicationContext(), Launch.class);

                    Toast.makeText(getActivity().getApplicationContext(),string1,Toast.LENGTH_LONG).show();

                    startActivity(intent);
                }else{
                    Intent intent = new Intent(getActivity().getApplicationContext(), Launch.class);

                    Toast.makeText(getActivity().getApplicationContext(),string1,Toast.LENGTH_LONG).show();

                    startActivity(intent);
                    Toast.makeText(getActivity().getApplicationContext(),"Failed Try again later",Toast.LENGTH_LONG).show();
                }

                // Printing uploading success message coming from server on android app.


                // Setting image as transparent after done uploading.
//                imageView.setImageResource(android.R.color.transparent);


            }

            @Override
            protected String doInBackground(Void... params) {

                ImageProcessClass imageProcessClass = new ImageProcessClass();

                HashMap<String,String> HashMapParams = new HashMap<String,String>();

                HashMapParams.put(ImageName, GetImageNameEditText);
                HashMapParams.put(imgstatus, Getimgcheck);

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




    public void JSON_HTTP_CALL(){

//        Toast.makeText(SignOnActivity.this, "I c it", Toast.LENGTH_SHORT).show();
        RequestOfJSonArray = new JsonArrayRequest(HTTP_JSON_URL,


                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
//                        Toast.makeText(SignOnActivity.this, "I c it too", Toast.LENGTH_SHORT).show();

                        ParseJSonResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());

        requestQueue.add(RequestOfJSonArray);
    }

    public void ParseJSonResponse(JSONArray array){

        for(int i = 0; i<array.length(); i++) {

            DataAdapter GetDataAdapter2 = new DataAdapter();

            JSONObject json = null;
            try {

                json = array.getJSONObject(i);

                GetDataAdapter2.setImageTitle(json.getString(Image_Name_JSON));
                textView.setText(json.getString(Image_Name_JSON));



            } catch (JSONException e) {

                e.printStackTrace();
            }

        }

    }


}