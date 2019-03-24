package com.newage.letstalk.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.newage.letstalk.R;
import com.newage.letstalk.SessionManager;
import com.newage.letstalk.utils.CacheStore;
import com.newage.letstalk.utils.ImagePicker;

import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;

import android.content.Intent;
import android.graphics.Bitmap;
import android.widget.EditText;

import java.io.BufferedReader;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import java.io.UnsupportedEncodingException;
import java.util.Objects;

import android.util.Base64;


/**
 * Created by Newage_android on 5/3/2018.
 */
public class ProfileTabFragment extends Fragment {

    HttpResponse httpResponse;
    Bitmap bitmap = null;
    JsonArrayRequest RequestOfJSonArray;
    RequestQueue requestQueue;

    Button update;
    CircleImageView profileDp;
    TextView statusTv;
    EditText statusEt;

    SessionManager session;
    ProgressDialog progressDialog;

    String newStatus;
    String withImage;
    boolean isNewImage = false;

    public static String HTTP_JSON_URL = "https://globeexservices.com/letstalk/userdp.php/?user=";
    public static String ServerUploadPath = "https://globeexservices.com/letstalk/updateprofile.php/?user=";
    public static String imageUrl = "https://globeexservices.com/letstalk/images/";
    private String phoneNumber;
    private CacheStore cacheStore;

    private static final int MULTIPLE_PERMISSIONS = 10;
    private String[] permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // width and height will be at least 600px long (optional).
        ImagePicker.setMinQuality(600, 600);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tab_fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        update = (Button) view.findViewById(R.id.update);
        profileDp = (CircleImageView) view.findViewById(R.id.dp);
        statusTv = (TextView) view.findViewById(R.id.status);
        statusEt = (EditText) view.findViewById(R.id.status_input);

        session = new SessionManager(getContext());
        phoneNumber = session.getPhoneNumber();

        HTTP_JSON_URL = HTTP_JSON_URL + phoneNumber;
        ServerUploadPath = ServerUploadPath + phoneNumber;
        imageUrl = imageUrl + phoneNumber + ".png";

        cacheStore = CacheStore.getInstance();
        statusTv.setText(session.getUserStatus());

        profileDp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkPermissions(getContext())) {
                    ImagePicker.pickImage(ProfileTabFragment.this, "Select your image", 1234, false);
                }
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newStatus = statusEt.getText().toString();
                withImage = isNewImage ? "image" : "empty";
                ImageUploadToServerFunction();
            }
        });

        bitmap = cacheStore.getCacheFile(phoneNumber);
        if (bitmap != null) {
            profileDp.setImageBitmap(bitmap);
        } else {
            bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.defaultuser);
            cacheStore.saveCacheFile(phoneNumber, bitmap);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        bitmap = ImagePicker.getImageFromResult(getContext(), requestCode, resultCode, data);
        if (bitmap != null) {
            if (resultCode == Activity.RESULT_OK && requestCode == 1234) {
                profileDp.setImageBitmap(bitmap);
                isNewImage = true;
            }
        }
    }

    public boolean checkPermissions(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return true;

        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissions) {
            if (ContextCompat.checkSelfPermission(context, p) != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(Objects.requireNonNull(getActivity()), listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        if (requestCode == MULTIPLE_PERMISSIONS) {
            for (String p : permissions) {
                if (TextUtils.equals(p, Manifest.permission.CAMERA) &&
                        ContextCompat.checkSelfPermission(Objects.requireNonNull(getContext()), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    ImagePicker.pickImage(this, "Select your image", 1234, false);
                }
            }
        }
    }

    public void getDpStatus() {
        RequestOfJSonArray = new JsonArrayRequest(HTTP_JSON_URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    JSONObject json = null;

                    try {
                        json = response.getJSONObject(i);

                        String status = json.getString("user_status");
                        String dp = json.getString("dp");

                        //statusTv.setText(json.getString("user_status"));
                        //TODO

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        requestQueue = Volley.newRequestQueue(Objects.requireNonNull(getContext()));
        requestQueue.add(RequestOfJSonArray);
    }


    public void ImageUploadToServerFunction() {
        //TODO show progress
        progressDialog = ProgressDialog.show(getContext(), "Uploading files...", null, true, true);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);

        ByteArrayOutputStream byteArrayOutputStreamObject;
        byteArrayOutputStreamObject = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStreamObject);

        byte[] byteArrayVar = byteArrayOutputStreamObject.toByteArray();
        final String ConvertImage = Base64.encodeToString(byteArrayVar, Base64.DEFAULT);

        @SuppressLint("StaticFieldLeak")
        class AsyncTaskUploadClass extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                update.setVisibility(View.INVISIBLE);
            }

            @Override
            protected String doInBackground(Void... params) {
                ImageProcessClass imageProcessClass = new ImageProcessClass();
                HashMap<String, String> HashMapParams = new HashMap<>();

                HashMapParams.put("image_name", newStatus);
                HashMapParams.put("imgstatus", withImage);
                HashMapParams.put("image_path", ConvertImage);

                return imageProcessClass.ImageHttpRequest(ServerUploadPath, HashMapParams);
            }

            @Override
            protected void onPostExecute(String string1) {
                super.onPostExecute(string1);
                if (string1.equalsIgnoreCase("Profile updated.")) {
                    if (isNewImage) {
                        cacheStore.saveCacheFile(phoneNumber, bitmap);
                    }

                    if(!TextUtils.isEmpty(newStatus)){
                        session.setUserSatus(newStatus);
                        statusTv.setText(session.getUserStatus());
                    }

                    Toast.makeText(getActivity(), string1, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getActivity(), "Failed Try again later", Toast.LENGTH_LONG).show();
                }

                progressDialog.dismiss();
                update.setVisibility(View.VISIBLE);
            }
        }

        AsyncTaskUploadClass AsyncTaskUploadClassOBJ = new AsyncTaskUploadClass();
        AsyncTaskUploadClassOBJ.execute();
    }

    public class ImageProcessClass {

        public String ImageHttpRequest(String requestURL, HashMap<String, String> PData) {
            StringBuilder stringBuilder = new StringBuilder();
            try {
                URL url;
                HttpURLConnection httpURLConnectionObject;
                OutputStream OutPutStream;
                BufferedWriter bufferedWriterObject;
                BufferedReader bufferedReaderObject;
                int RC;

                url = new URL(requestURL);
                httpURLConnectionObject = (HttpURLConnection) url.openConnection();
                httpURLConnectionObject.setReadTimeout(19000);
                httpURLConnectionObject.setConnectTimeout(19000);
                httpURLConnectionObject.setRequestMethod("POST");
                httpURLConnectionObject.setDoInput(true);
                httpURLConnectionObject.setDoOutput(true);
                OutPutStream = httpURLConnectionObject.getOutputStream();

                bufferedWriterObject = new BufferedWriter(new OutputStreamWriter(OutPutStream, "UTF-8"));
                bufferedWriterObject.write(bufferedWriterDataFN(PData));
                bufferedWriterObject.flush();
                bufferedWriterObject.close();
                OutPutStream.close();

                RC = httpURLConnectionObject.getResponseCode();

                if (RC == HttpsURLConnection.HTTP_OK) {
                    bufferedReaderObject = new BufferedReader(new InputStreamReader(httpURLConnectionObject.getInputStream()));
                    stringBuilder = new StringBuilder();
                    String RC2;
                    while ((RC2 = bufferedReaderObject.readLine()) != null) {
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
            boolean check = true;

            for (Map.Entry<String, String> KEY : HashMapParams.entrySet()) {

                if (check) check = false;
                else stringBuilderObject.append("&");

                stringBuilderObject.append(URLEncoder.encode(KEY.getKey(), "UTF-8"));
                stringBuilderObject.append("=");
                stringBuilderObject.append(URLEncoder.encode(KEY.getValue(), "UTF-8"));
            }

            return stringBuilderObject.toString();
        }

    }
}