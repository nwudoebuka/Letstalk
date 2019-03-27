package com.newage.letstalk;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Newage_android on 5/2/2018.
 */

public class HttpParse {

//    public String postRequest(HashMap<String, String> Data, String HttpUrlHolder) {
//        String FinalHttpData = "";
//        BufferedWriter bufferedWriter = null;
//        OutputStream outputStream = null;
//        BufferedReader bufferedReader = null;
//        HttpURLConnection httpURLConnection = null;
//
//        try {
//            URL url = new URL(HttpUrlHolder);
//            httpURLConnection = (HttpURLConnection) url.openConnection();
//            httpURLConnection.setReadTimeout(14000);
//            httpURLConnection.setConnectTimeout(14000);
//            httpURLConnection.setRequestMethod("POST");
//            httpURLConnection.setDoInput(true);
//            httpURLConnection.setDoOutput(true);
//
//            outputStream = httpURLConnection.getOutputStream();
//            bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
//            bufferedWriter.write(FinalDataParse(Data));
//            bufferedWriter.flush();
//            bufferedWriter.close();
//            outputStream.close();
//
//            if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
//                bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
//                FinalHttpData = bufferedReader.readLine();
//            } else {
//                FinalHttpData = "Something Went Wrong";
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }finally {
//            try {
//                if(bufferedReader != null){
//                    bufferedReader.close();
//                }
//
//                if(bufferedWriter != null) {
//                    bufferedWriter.close();
//                }
//
//                if(httpURLConnection != null){
//                    httpURLConnection.disconnect();
//                }
//            }catch (Exception e){
//                e.printStackTrace();
//            }
//        }
//
//        return FinalHttpData;
//    }
//
//    private String FinalDataParse(HashMap<String,String> hashMap2) throws UnsupportedEncodingException {
//        StringBuilder stringBuilder = new StringBuilder();
//        boolean isFirst = true;
//
//        for(Map.Entry<String,String> map_entry : hashMap2.entrySet()){
//            if (isFirst) isFirst = false;
//            else stringBuilder.append("&");
//            stringBuilder.append(URLEncoder.encode(map_entry.getKey(), "UTF-8")).append("=");
//            stringBuilder.append(URLEncoder.encode(map_entry.getValue(), "UTF-8"));
//        }
//
//        return stringBuilder.toString();
//    }


    public String postRequest(String url, HashMap<String, String> data) {
        return httpRequest(url, "POST", data);
    }

    public String getRequest(String url) {
        return httpRequest(url, "GET", null);
    }

    private String httpRequest(String requestURL, String requestMethod, HashMap<String, String> data) {
        HttpURLConnection httpURLConnection = null;
        OutputStreamWriter outputStreamWriter = null;
        InputStreamReader inputStreamReader = null;
        BufferedWriter bufferedWriter = null;
        BufferedReader bufferedReader = null;
        StringBuilder stringBuilder = new StringBuilder();

        try {
            URL url = new URL(requestURL);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setReadTimeout(19000);
            httpURLConnection.setConnectTimeout(19000);
            httpURLConnection.setRequestMethod(requestMethod.toUpperCase());
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);

            //For Post request
            if(httpURLConnection.getRequestMethod().equalsIgnoreCase("POST")){
                OutputStream outPutStream = httpURLConnection.getOutputStream();
                outputStreamWriter = new OutputStreamWriter(outPutStream, StandardCharsets.UTF_8);
                bufferedWriter = new BufferedWriter(outputStreamWriter);
                bufferedWriter.write(dataWriter(data));
                bufferedWriter.flush();
            }

            //For GET request and reading value of result for POST request
            InputStream inputStream = httpURLConnection.getInputStream();
            inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            bufferedReader = new BufferedReader(inputStreamReader);

            if(httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                }
            }else {
                stringBuilder.append("Something Went Wrong");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                if(bufferedReader != null){
                    bufferedReader.close();
                }

                if(inputStreamReader != null){
                    inputStreamReader.close();
                }

                if(bufferedWriter != null) {
                    bufferedWriter.close();
                }

                if(outputStreamWriter != null){
                    outputStreamWriter.close();
                }

                if(httpURLConnection != null){
                    httpURLConnection.disconnect();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        return stringBuilder.toString();
    }

    private String dataWriter(HashMap<String, String> HashMapParams) throws UnsupportedEncodingException {
        StringBuilder stringBuilderObject = new StringBuilder();
        boolean isFirst = true;

        for (Map.Entry<String, String> KEY : HashMapParams.entrySet()) {
            if (isFirst) isFirst = false;
            else stringBuilderObject.append("&");

            stringBuilderObject.append(URLEncoder.encode(KEY.getKey(), "UTF-8"));
            stringBuilderObject.append("=");
            stringBuilderObject.append(URLEncoder.encode(KEY.getValue(), "UTF-8"));
        }

        return stringBuilderObject.toString();
    }

}