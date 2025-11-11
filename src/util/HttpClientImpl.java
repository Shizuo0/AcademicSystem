package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;

public class HttpClientImpl implements IHttpClient {
    
    private static final int TIMEOUT_MILLISECONDS = 3000;
    private static final String REQUEST_METHOD = "GET";
    
    @Override
    public String get(String url) throws IOException {
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        
        try {
            URI uri = new URI(url);
            connection = (HttpURLConnection) uri.toURL().openConnection();
            connection.setRequestMethod(REQUEST_METHOD);
            connection.setConnectTimeout(TIMEOUT_MILLISECONDS);
            connection.setReadTimeout(TIMEOUT_MILLISECONDS);
            connection.setRequestProperty("Accept", "application/json");
            
            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw new IOException("HTTP Error: " + responseCode + " - " + connection.getResponseMessage());
            }
            
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            
            while ((line = reader.readLine()) != null) {
                response.append(line);
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
