package com.github.nikolajr93.studenttestingintellijplugin.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.github.nikolajr93.studenttestingintellijplugin.Config;
import org.apache.hc.client5.http.async.methods.SimpleHttpRequest;
import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;
import org.apache.hc.client5.http.async.methods.SimpleRequestBuilder;
import org.apache.hc.client5.http.fluent.Request;
import org.apache.hc.client5.http.fluent.Response;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.client5.http.impl.async.HttpAsyncClients;
import org.apache.hc.core5.http.ContentType;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class RafApiClient {
    public static String getStudents() {
        String result = null;
        try {
            Response response = Request.get(Config.REST_API_BASE_URL).execute();
            result = response.returnContent().asString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String getStudent(Integer id) {
        String result = null;
        try {
            Response response = Request.get(Config.REST_API_BASE_URL + "/" + id).execute();
            result = response.returnContent().asString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String createStudent(Student student) throws IOException {
        CloseableHttpAsyncClient client = HttpAsyncClients.createDefault();

        try (client) {
            client.start();
            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            String json = ow.writeValueAsString(student);

            SimpleHttpRequest request = SimpleRequestBuilder.post(Config.REST_API_BASE_URL)
                    .setBody(json, ContentType.APPLICATION_JSON)
                    .build();
            Future<SimpleHttpResponse> future = client.execute(request, null);
            SimpleHttpResponse response = future.get();
            return response.getBodyText();
        } catch (JsonProcessingException | ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return "Student was not created";
    }

    public static String deleteStudent(Integer id) {
        String result = null;
        try {
            Response response = Request.delete(Config.REST_API_BASE_URL + "/" + id).execute();
            result = response.returnContent().asString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
