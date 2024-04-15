import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URI;
import java.net.URISyntaxException;
import java.io.IOException;
import java.io.OutputStream;

public class Frontend {
    public static void main(String[] args) {
        try {
            URI uri = new URI("http://localhost:5000/api/request-filter");
            URL url = uri.toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);
            String jsonData = "{\"type\" : \"Artist\",\"previous_filter\" : [\"Relaxing\", \"Energetic\", \"Jazz\", \"Pop\", \"2000s\", \"1960s\"]}";
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