package com.github.nikolajr93.studenttestingintellijplugin.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.github.nikolajr93.studenttestingintellijplugin.Config;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class RafApiClient {

    private static final HttpClient CLIENT = HttpClient.newHttpClient();

    public static String getStudents() {
        String result = null;
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(Config.REST_API_BASE_URL))
                    .build();
            HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            result = response.body();
        } catch (IOException | InterruptedException e){
            e.printStackTrace();
        }
        return result;
    }

//    public static String getStudents() {
//        String result = null;
//        try {
//            Response response = Request.get(Config.REST_API_BASE_URL).execute();
//            result = response.returnContent().asString();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return result;
//    }

    public static String getStudent(Integer id) {
        String result = null;
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(Config.REST_API_BASE_URL + "/" + id))
                    .build();
            HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            result = response.body();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }

//    public static String getStudent(Integer id) {
//        String result = null;
//        try {
//            Response response = Request.get(Config.REST_API_BASE_URL + "/" + id).execute();
//            result = response.returnContent().asString();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return result;
//    }

    public static String createStudent(Student student) {
        try {
            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            String json = ow.writeValueAsString(student);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(Config.REST_API_BASE_URL))
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .header("Content-Type", "application/json")
                    .build();

            HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return "Student was not created";
        }
    }

//    public static String createStudent(Student student) throws IOException {
//        CloseableHttpAsyncClient client = HttpAsyncClients.createDefault();
//
//        try (client) {
//            client.start();
//            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
//            String json = ow.writeValueAsString(student);
//
//            SimpleHttpRequest request = SimpleRequestBuilder.post(Config.REST_API_BASE_URL)
//                    .setBody(json, ContentType.APPLICATION_JSON)
//                    .build();
//            Future<SimpleHttpResponse> future = client.execute(request, null);
//            SimpleHttpResponse response = future.get();
//            return response.getBodyText();
//        } catch (JsonProcessingException | ExecutionException | InterruptedException e) {
//            e.printStackTrace();
//        }
//        return "Student was not created";
//    }

    public static String deleteStudent(Integer id) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(Config.REST_API_BASE_URL + "/" + id))
                    .DELETE()
                    .build();
            HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

//    public static String deleteStudent(Integer id) {
//        String result = null;
//        try {
//            Response response = Request.delete(Config.REST_API_BASE_URL + "/" + id).execute();
//            result = response.returnContent().asString();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return result;
//    }
}
