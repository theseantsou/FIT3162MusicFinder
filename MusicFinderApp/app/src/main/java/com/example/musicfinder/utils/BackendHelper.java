package com.example.musicfinder.utils;

import androidx.annotation.NonNull;

import com.example.musicfinder.Playlist;
import com.example.musicfinder.Song;

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
    public static final String baseURL = "http://192.168.0.69:5000";

    public static List<String> requestFilters(int filterAmt, String filterType, List<String> previousFilter, List<String> previousResponse) {
        try {
            JSONArray prevFilterArray = new JSONArray(previousFilter);
            JSONArray prevResponseArray = new JSONArray(previousResponse);
            String jsonData = "{\"amount\" : " + filterAmt + ", \"type\" : \"" + filterType + "\", \"previous_filter\" : " + prevFilterArray + ", \"previous_response\" : " + prevResponseArray + " }";
            String requestURL = baseURL + "/request-filter";
            HttpURLConnection connection = getPostHttpURLConnection(jsonData, requestURL, 30000);

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
    }

    public static Playlist requestPlaylist(int amtOfSongs, List<String> filters) {
        try {
            JSONArray filterArray = new JSONArray(filters);
            String jsonData = "{\"amount\" : " + (amtOfSongs + 5) + ", \"filters\" : " + filterArray + "}";
            String requestURL = baseURL + "/request-playlist";
            HttpURLConnection connection = getPostHttpURLConnection(jsonData, requestURL, 100000);
            Playlist generatedPlaylist = new Playlist();
            generatedPlaylist.setFilters(new ArrayList<>(ActivityUtil.getFilters()));

            ActivityUtil.emptyFilter();
            JSONObject jsonObject = readResponse(connection);
            connection.disconnect();
            JSONArray songsArray = jsonObject.getJSONArray("playlist");
            String playlistTitle = jsonObject.getString("title");
            List<Song> songsList = new ArrayList<>();
            int minSongAmt = Math.min(songsArray.length(), amtOfSongs);
            for (int i = 0; i < minSongAmt; i++) {
                JSONObject songObject = songsArray.getJSONObject(i);
                String title = songObject.getString("track");
                String artist = songObject.getString("artist");
                Song song = new Song(title, artist);
                songsList.add(song);
            }
            generatedPlaylist.setTitle(playlistTitle);
            generatedPlaylist.setSongs(songsList);
            return generatedPlaylist;

        } catch (Exception e) {
            return null;
        }

    }

    public static String sendSpotifySessionParams(String callbackURL) {
        try {
            URL url = new URL(callbackURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            JSONObject responseObj = readResponse(connection);
            if (responseObj.getString("status").equalsIgnoreCase("success")) {
                return responseObj.getString("email");
            }
            else {
                return "";
            }
        }
        catch (Exception e) {
            return "";
        }
    }

    public static String getSpotifyLoginURL(String email) {
        try {
            String jsonData = "{\"email\" : \"" + email + "\"}";
            String requestURL = baseURL + "/spotify-login";


            HttpURLConnection connection = getPostHttpURLConnection(jsonData, requestURL, 10000);

            JSONObject response = readResponse(connection);

            return response.getString("url");

        }
        catch (Exception e) {
            return null;
        }
    }

    public static boolean savePlaylistToSpotify(String email, List<Song> songsList, String title) {

        try {
            JSONArray songsArray = new JSONArray();
            for (Song song : songsList) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("artist", song.getArtist());
                jsonObject.put("track", song.getTitle());
                songsArray.put(jsonObject);
            }
            String jsonData = "{\"email\" : \"" + email + "\", \"songs\" : "+ songsArray + ", \"title\" : \""+ title + "\"}";
            String requestURL = baseURL + "/save-playlist";

            HttpURLConnection connection = getPostHttpURLConnection(jsonData, requestURL, 100000);

            JSONObject response = readResponse(connection);

            return response.getString("status").equals("success");

        }
        catch (Exception ignored) {
            return false;
        }


    }

    public static JSONObject readResponse(HttpURLConnection connection) throws IOException, JSONException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
        StringBuilder response = new StringBuilder();
        String responseText = null;

        while ((responseText = reader.readLine()) != null) {
            response.append(responseText.trim());
        }
        reader.close();
        return new JSONObject(response.toString());
    }

    @NonNull
    private static HttpURLConnection getPostHttpURLConnection(String jsonData, String requestURL, int timeoutMillis) throws URISyntaxException, IOException {
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
