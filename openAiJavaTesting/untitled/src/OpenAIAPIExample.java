
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;

    public class OpenAIAPIExample {
        public static void main(String[] args) {
            String apiKey = "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";
            HttpClient client = HttpClient.newHttpClient();
            String requestBody = """
            {
                "model": "gpt-3.5-turbo",
                "messages": [
                    {"role": "system", "content": "You are a helpful assistant."},
                    {"role": "user", "content": "What is 1+1"}
                ]
            }
            """;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.openai.com/v1/chat/completions"))
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .POST(BodyPublishers.ofString(requestBody))
                    .build();

            try {
                HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
                System.out.println(response.body());
            } catch (Exception e) {
                System.out.println("There was an error processing your request.");
                e.printStackTrace();
            }
        }
    }


