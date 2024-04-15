package com.example.musicfinder;
import android.app.AlertDialog;
import android.util.Log;

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
import java.util.List;

public class BackendHelper {
    public static JSONArray requestFilters(String filterType, List<String> previousFilter) {
        try {
            HttpURLConnection connection = getHttpURLConnection(filterType, previousFilter);

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String responseText = null;

                while ((responseText = reader.readLine()) != null) {
                    response.append(responseText.trim());
                }
                JSONObject object = new JSONObject(response.toString());
                JSONArray items_array = object.getJSONArray("filters");
                connection.disconnect();
                return items_array;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @NonNull
    private static HttpURLConnection getHttpURLConnection(String filterType, List<String> previousFilter) throws URISyntaxException, IOException {
        URI uri = new URI("http://192.168.43.189:5000/api/request-filter");
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
