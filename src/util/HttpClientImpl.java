package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

public class HttpClientImpl implements IHttpClient {

    private static final int CONNECT_TIMEOUT_MILLISECONDS = 5000;
    private static final int READ_TIMEOUT_MILLISECONDS = 5000;
    private static final String REQUEST_METHOD = "GET";
    private static final int BUFFER_SIZE = 8192;

    @Override
    public String get(String url) throws IOException {
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            URI uri = new URI(url);
            connection = (HttpURLConnection) uri.toURL().openConnection();
            connection.setRequestMethod(REQUEST_METHOD);
            connection.setConnectTimeout(CONNECT_TIMEOUT_MILLISECONDS);
            connection.setReadTimeout(READ_TIMEOUT_MILLISECONDS);

            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Accept-Encoding", "gzip, deflate");
            connection.setRequestProperty("Connection", "keep-alive");
            connection.setRequestProperty("User-Agent", "AcademicSystem/1.0");

            connection.setUseCaches(false);

            connection.setInstanceFollowRedirects(true);

            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw new IOException("HTTP Error: " + responseCode + " - " + connection.getResponseMessage());
            }

            reader = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8),
                BUFFER_SIZE
            );

            StringBuilder response = new StringBuilder(BUFFER_SIZE);
            char[] buffer = new char[BUFFER_SIZE];
            int charsRead;

            while ((charsRead = reader.read(buffer)) != -1) {
                response.append(buffer, 0, charsRead);
            }

            return response.toString();

        } catch (SocketTimeoutException e) {
            throw new IOException("Timeout ao conectar com o serviço: " + url, e);
        } catch (URISyntaxException e) {
            throw new IOException("URL inválida: " + url, e);
        } catch (IOException e) {
            throw new IOException("Erro ao realizar requisição HTTP: " + e.getMessage(), e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                }
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}
