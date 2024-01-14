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

    public static String getStudent(String id) {
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

    public static String createStudent(Student newStudent) {
        try {
            newStudent.setId(
                    newStudent.getStudyProgram()
                            +newStudent.getMajor()
                            +newStudent.getIndexNumber()
                            +newStudent.getStartYear());
            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            String json = ow.writeValueAsString(newStudent);

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

    public static String taskIsCloned(String id, ExamInfo examInfo) {
        String result = null;
        try {
            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            String json = ow.writeValueAsString(examInfo);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(Config.REST_API_BASE_URL + "/" + id + "/task_cloned"))
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .header("Content-Type", "application/json")
                    .build();

            HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            result = response.body();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String taskIsSubmitted(String id, TaskSubmissionInfo taskSubmissionInfo) {
        String result = null;
        try {
            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            String json = ow.writeValueAsString(taskSubmissionInfo);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(Config.REST_API_BASE_URL + "/" + id + "/task_submitted"))
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .header("Content-Type", "application/json")
                    .build();

            HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            result = response.body();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String deleteStudent(String id) {
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
}
