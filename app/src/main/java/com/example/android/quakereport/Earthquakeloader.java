package com.example.android.quakereport;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.LinkedList;


public class Earthquakeloader extends AsyncTaskLoader<LinkedList<Custom_data>> {

    private String mUrl;
    private  String LOG_TAG=Earthquakeloader.class.getName() ;
    public Earthquakeloader(Context context, String url) {
        super(context);
        this.mUrl = url;
    }


    @Override
    public LinkedList<Custom_data> loadInBackground() {
        Log.e(LOG_TAG,"This is loading data") ;


        if (mUrl == null) {
            return null;
        }

        URL url = createUrl(mUrl);
        // creating a url object needs a method because it requires a try catch block to handle the exception

        String JsonResponse = "";
        try {
            JsonResponse = makeHTTPrequest(url);


        } catch (IOException e) {
            Log.e("DO in bc ", "Error while making connection " + e);
        }

        if( JsonResponse=="")
        {return null ;}
        LinkedList<Custom_data> earthquake = jsonresponseextracter(JsonResponse);
        return earthquake;


    }


    @Override
    protected void onStartLoading() {
        forceLoad();
        Log.e(LOG_TAG,"This is when the kickstart is called ") ;
    }

    // method to handle the url link to make a object
    private URL createUrl(String Link) {
        URL url = null;

        try {
            url = new URL(Link);
        } catch (MalformedURLException error) {

            Log.e("LOG_TAG", "Url can't be passed due to " + error);
            // if there is error in creating url then return the unassigned null value as a result
            return null;
        }
        return url;
    }

    private String makeHTTPrequest(URL url) throws IOException {

        String JsonResponse = "";
        InputStream inputStream = null;
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setReadTimeout(15000);

            urlConnection.connect();

            inputStream = urlConnection.getInputStream();
            JsonResponse = readFromStream(inputStream);

        } catch (IOException e) {
            Log.e("HTTP connection ", " Error whule making connection " + e);
        } finally {

            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }

        return JsonResponse;
        //TODO:return the value after the whole setup
    }


    private String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();

        if (inputStream != null) {
            InputStreamReader reader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader bufferedReader = new BufferedReader(reader);

            String line = bufferedReader.readLine();

            while (line != null) {

                output.append(line);
                line = bufferedReader.readLine();

            }


        }
        return output.toString();

    }

    private LinkedList<Custom_data> jsonresponseextracter(String response) {
        LinkedList<Custom_data> earthquakes = new LinkedList<>();

        // Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            JSONObject root = new JSONObject(response);
            JSONArray features = root.getJSONArray("features");

            for (int i = 0; i < features.length(); ++i) {

                JSONObject arrayindex = features.getJSONObject(i);
                JSONObject properties = arrayindex.getJSONObject("properties");
                earthquakes.add(new Custom_data(properties.getDouble("mag"), properties.getString("place"), properties.getLong("time")));

            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("Jsonparsing", "Problem parsing the earthquake JSON results", e);
        }

        // Return the list of earthquakes
        return earthquakes;
    }


}



