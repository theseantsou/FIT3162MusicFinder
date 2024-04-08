import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URISyntaxException;
import java.io.IOException;
import java.io.OutputStream;

public class Frontend {
    public static void main(String[] args) {
        try {
            URL url = new URL("http://localhost:5000/api/request-filter");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            String jsonData = "{\"type\" : \"Mood or Occasion\"}";

            try (OutputStream stream = connection.getOutputStream()) {
                byte[] input = jsonData.getBytes("utf-8");
                stream.write(input, 0, input.length);
            }

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseText = null;

                while ((responseText = reader.readLine()) != null) {
                    response.append(responseText.trim());
                }
                System.out.println(response.toString());
            }
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}