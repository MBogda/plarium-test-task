package com.plarium.client;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

public class PlariumHttpClient {

    private static Logger logger = Logger.getLogger(PlariumHttpClient.class.getName());

    public static void sendBatch(List<String> batch) throws IOException, InterruptedException {
        // todo: должно быть устойчиво к сетевым сбоям;
        if (batch.isEmpty()) {
            logger.info("Empty list is skipped.");
            return;
        }

//        URL url = new URL("http://localhost:8080/" + "upload_json");
//        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//        connection.setRequestMethod("POST");
//        connection.setRequestProperty("Content-Type", "application/json");
//        connection.setConnectTimeout(1000);     // todo: move to constant
//        connection.setReadTimeout(1000);
//        connection.

        URI uri = URI.create("http://localhost:8080/" + "upload_json");     // todo: not hardcode

        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))      // todo: not hardcode
                .build();
        HttpRequest request = HttpRequest
                .newBuilder(uri)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(buildRequestBody(batch)))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        logger.info("Request body: " + buildRequestBody(batch));
        if (response.statusCode() == 200) {     // todo: not hardcode
            logger.info("Request OK");
        } else {
            logger.info("Request failed");
        }
    }

    private static String buildRequestBody(List<String> batch) {
        StringBuilder stringBuilder = new StringBuilder();
        Iterator<String> batchIterator = batch.iterator();
        stringBuilder.append('[');
        stringBuilder.append(batchIterator.next());
        while (batchIterator.hasNext()) {
            stringBuilder.append(',');
            stringBuilder.append(batchIterator.next());
        }
        stringBuilder.append(']');
        return stringBuilder.toString();
    }
}
