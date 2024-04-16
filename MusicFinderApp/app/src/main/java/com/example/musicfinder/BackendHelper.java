package com.example.musicfinder;

import android.app.AlertDialog;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URI;
import java.net.URISyntaxException;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class BackendHelper {
    public static List<String> requestFilters(String filterType, List<String> previousFilter) {
        try {
            HttpURLConnection connection = getHttpURLConnection(filterType, previousFilter);

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String responseText = null;

                while ((responseText = reader.readLine()) != null) {
                    response.append(responseText.trim());
                }
                JSONObject jsonObject = new JSONObject(response.toString());
                JSONArray jsonArray = jsonObject.getJSONArray("filters");
                List<String> filters = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    System.out.println("hello");
                    filters.add(jsonArray.getString(i));
                }


                connection.disconnect();
                return filters;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @NonNull
    private static HttpURLConnection getHttpURLConnection(String filterType, List<String> previousFilter) throws URISyntaxException, IOException {
        URI uri = new URI("http://192.168.0.69:5000/api/request-filter");
        URL url = uri.toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        String jsonData = "{\"type\" : \""+ filterType + "\",\"previous_filter\" :" + previousFilter + "}";

        try (OutputStream stream = connection.getOutputStream()) {
            byte[] input = jsonData.getBytes(StandardCharsets.UTF_8);
            stream.write(input, 0, input.length);
        }
        return connection;
    }
}
