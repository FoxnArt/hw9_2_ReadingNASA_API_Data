package ru.netology;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.*;

public class Main {
    public static final String REMOTE_SERVICE_URI = "https://api.nasa.gov/planetary/apod?api_key=DojNceu8ravwmGUd5fyUpYVp6koA8LigSWgpcDjK";
    public static ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) throws IOException {

        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(5000)
                        .setSocketTimeout(30000)
                        .setRedirectsEnabled(false)
                        .build())
                .build();

        HttpGet request = new HttpGet(REMOTE_SERVICE_URI);
        CloseableHttpResponse response = httpClient.execute(request);
        NasaData nasaData = mapper.readValue(
                response.getEntity().getContent(),
                new TypeReference<NasaData>() {});
        String pictureUrl = nasaData.getUrl();
        int lastFileSeparatorIndex = pictureUrl.lastIndexOf('/');
        String fileName = pictureUrl.substring(lastFileSeparatorIndex + 1);

        HttpGet requestPicture = new HttpGet(pictureUrl);
        CloseableHttpResponse responsePicture = httpClient.execute(requestPicture);
        byte[] buffer = responsePicture.getEntity().getContent().readAllBytes();
        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(fileName))) {
            bos.write(buffer, 0, buffer.length);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

        responsePicture.close();
        response.close();
        httpClient.close();
    }
}