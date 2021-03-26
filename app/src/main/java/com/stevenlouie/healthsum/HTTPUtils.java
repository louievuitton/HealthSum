package com.stevenlouie.healthsum;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HTTPUtils {
    
    public static String getNutrientsData(String uri, String query) {
        String response = null;

        try {
            URL url = new URL(uri);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("x-app-id", "9787a77e");
            urlConnection.setRequestProperty("x-app-id", "9787a77e");
            urlConnection.setRequestProperty("x-app-key", "40785cdacf0ad9c162ae95eb2f83aa9d");
            urlConnection.setRequestProperty("x-remote-user-id", "0");

            urlConnection.setDoOutput(true);
            urlConnection.setChunkedStreamingMode(0);

            OutputStreamWriter out = new OutputStreamWriter(urlConnection.getOutputStream());
            out.write(query);
            out.close();
            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream stream = urlConnection.getInputStream();
                response = getStringfromStream(stream);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }

    // Get a string from an input stream
    private static String getStringfromStream(InputStream stream) {
        String line, jsonString = null;
        if (stream != null) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            StringBuilder out = new StringBuilder();
            try {
                while ((line = reader.readLine()) != null) {
                    out.append(line);
                }
                reader.close();
                jsonString = out.toString();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return jsonString;
    }
}
