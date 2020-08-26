package com.georgeampartzidis.greenfridge.utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetworkUtilities {

    private static final String BASE_URL = "https://world.openfoodfacts.org/api/v0/product/.json";
    private static final String JSON_URL = ".json";

    public static String buildQuery (String barcode){
        return BASE_URL + barcode + JSON_URL;
    }

    public static String getJSONResults(String queryString){

        String jSONString = null;
        BufferedReader reader = null;
        HttpURLConnection urlConnection = null;

        try{
            URL requestUrl = new URL(queryString);

            urlConnection = (HttpURLConnection) requestUrl.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            // Get the InputStream
            InputStream inputStream = urlConnection.getInputStream();
            // Create a buffered reader for that InputStream
            reader = new BufferedReader(new InputStreamReader(inputStream));
            // Use a String Builder to hold the incoming response
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null){
                builder.append(line);
                // for easier debugging from the logcat, we can add a newline
                builder.append("\n");

                // Lastly we convert the StringBuilder to String
                jSONString = builder.toString();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Here we close the connection ant the BufferedReader
            if(urlConnection != null){
                urlConnection.disconnect();
            }
            if(reader != null){
                try{
                    reader.close();
                } catch (IOException e){
                    e.printStackTrace();
                }
            }
        }

        return jSONString;
    }
}
