package com.example.musicfinder;

import android.app.AlertDialog;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
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
    static final String baseURL = "http://192.168.43.189:5000";

    public static List<String> requestFilters(int filterAmt, String filterType, List<String> previousFilter, List<String> previousResponse) {
        try {
            JSONArray prevFilterArray = new JSONArray(previousFilter);
            JSONArray prevResponseArray = new JSONArray(previousResponse);
            String jsonData = "{\"amount\" : " + filterAmt + ", \"type\" : \"" + filterType + "\", \"previous_filter\" : " + prevFilterArray + ", \"previous_response\" : " + prevResponseArray + " }";
            String requestURL = baseURL + "/api/request-filter";
            HttpURLConnection connection = getHttpURLConnection(jsonData, requestURL, 30000);

            try {
                JSONObject jsonObject = readResponse(connection);
                JSONArray jsonArray = jsonObject.getJSONArray("filters");
                List<String> filters = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    filters.add(jsonArray.getString(i));
                }
                connection.disconnect();
                return filters;
            } catch (Exception e) {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    public static List<Song> requestPlaylist(int amtOfSongs, List<String> filters) {
        try {
            JSONArray filterArray = new JSONArray(filters);
            String jsonData = "{\"amount\" : " + (amtOfSongs + 5) + ", \"filters\" : " + filterArray + "}";
            String requestURL = baseURL + "/api/request-playlist";
            HttpURLConnection connection = getHttpURLConnection(jsonData, requestURL, 10000000);
            try {
                JSONObject jsonObject = readResponse(connection);
                connection.disconnect();
                JSONArray songsArray = jsonObject.getJSONArray("playlist");

                List<Song> playlist = new ArrayList<>();
                int minSongAmt = Math.min(songsArray.length(), amtOfSongs);
                for (int i = 0; i < minSongAmt; i++) {
                    JSONObject songObject = songsArray.getJSONObject(i);
                    String title = songObject.getString("track");
                    String artist = songObject.getString("artist");
                    Song song = new Song(title, artist);
                    playlist.add(song);
                }
                System.out.println(playlist);
                return playlist;
            } catch (Exception e) {
                return null;
            }
        } catch (Exception e) {
            return null;
        }

    }

    public static JSONObject readResponse(HttpURLConnection connection) throws IOException, JSONException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
        StringBuilder response = new StringBuilder();
        String responseText = null;

        while ((responseText = reader.readLine()) != null) {
            response.append(responseText.trim());
        }
        return new JSONObject(response.toString());
    }

    @NonNull
    private static HttpURLConnection getHttpURLConnection(String jsonData, String requestURL, int timeoutMillis) throws URISyntaxException, IOException {
        URI uri = new URI(requestURL);
        URL url = uri.toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setConnectTimeout(timeoutMillis);
        connection.setReadTimeout(timeoutMillis);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        try (OutputStream stream = connection.getOutputStream()) {
            byte[] input = jsonData.getBytes(StandardCharsets.UTF_8);
            stream.write(input, 0, input.length);
        }
        return connection;
    }
}
