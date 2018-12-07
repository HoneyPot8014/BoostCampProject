package com.leeyh.boostcampproject.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;

import static com.leeyh.boostcampproject.constant.StaticString.API_URL;
import static com.leeyh.boostcampproject.constant.StaticString.CLIENT_ID;
import static com.leeyh.boostcampproject.constant.StaticString.CLIENT_ID_VALUE;
import static com.leeyh.boostcampproject.constant.StaticString.GET;
import static com.leeyh.boostcampproject.constant.StaticString.SECRET_ID;
import static com.leeyh.boostcampproject.constant.StaticString.SECRET_ID_VALUE;

public class Network {

    public boolean networkStatus(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo != null) {
            if(networkInfo.isConnected()) {
                return true;
            } else return false;
        } else {
            return false;
        }
    }

    public String request(Map<String, String> queryString) throws IOException {
        StringBuilder params = new StringBuilder();
        Iterator<String> iterator = queryString.keySet().iterator();
        while (iterator.hasNext()) {
            String queryName = iterator.next();
            String queryValue = queryString.get(queryName);
            params.append(queryName).append("=").append(queryValue);
            if (iterator.hasNext()) {
                params.append("&");
            }
        }
        String url = API_URL + params;
        HttpURLConnection urlConnection = (HttpURLConnection) new URL(url).openConnection();
        urlConnection.setRequestMethod(GET);
        urlConnection.setRequestProperty(CLIENT_ID, CLIENT_ID_VALUE);
        urlConnection.setRequestProperty(SECRET_ID, SECRET_ID_VALUE);
        urlConnection.setUseCaches(false);
        BufferedReader reader;
        int responseCode = urlConnection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "utf-8"));
        } else {
            reader = new BufferedReader(new InputStreamReader(urlConnection.getErrorStream()));
            reader.close();
            urlConnection.disconnect();
            throw new Error(getResult(reader));
        }

        String result = getResult(reader);
        Log.d("확인", "request: " + result);
        reader.close();
        urlConnection.disconnect();
        return result;
    }

    private String getResult(BufferedReader reader) throws IOException {
        StringBuilder builder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            builder.append(line).append("\n");
        }
        return builder.toString();
    }

    public Bitmap loadImage(String url) throws IOException {
        Bitmap imageBitmap = null;
        InputStream inputStream = new URL(url).openStream();
        imageBitmap = BitmapFactory.decodeStream(inputStream);
        return imageBitmap;
    }

}
