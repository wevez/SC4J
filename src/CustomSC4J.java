import tech.tenamen.sc4j.SC4J;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class CustomSC4J extends SC4J {

    @Override
    protected String getHTTP(String URL, String USER_AGENT) {
        try {
            final URL requestUrl = new URL(URL);

            final HttpURLConnection connection = (HttpURLConnection) requestUrl.openConnection();
            connection.setRequestProperty("User-Agent", USER_AGENT);
            connection.setRequestMethod("GET");

            final BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            final StringBuilder response = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            connection.disconnect();
            return response.toString();
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }
}
