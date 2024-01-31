package com.github.nikolajr93.studenttestingintellijplugin.api;

import okhttp3.*;
import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.hamcrest.MatcherAssert.assertThat;

public class RestApiLoadTester {
    private static final Logger LOGGER = LoggerFactory.getLogger(RestApiLoadTester.class);

    @Test
    public void runLoadTest() {
        String url = "http://192.168.124.28:8091/api/v1/students";
        String requestBody = "";
        String headers = "content-type: application/json\n" +
                "user-agent: Mozilla/5.0";
        int threadCount = 10;
        int callCountPerThread = 30;

        OkHttpClient httpClient = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json");

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        AtomicInteger counter = new AtomicInteger();
        for (int i = 0; i < threadCount; i++) {
            int finalI = i;
            executor.submit(() -> {
                for (int j = 0; j < callCountPerThread; j++) {
                    Request.Builder requestBuilder = new Request.Builder()
                            .url(url);

                    // add headers
                    for (String header : headers.split("\n")) {
                        String[] headerParts = header.split(": ");
                        requestBuilder.addHeader(headerParts[0], headerParts[1]);
                    }

                    Request request = requestBuilder
                            .get()
                            .build();

                    LOGGER.info(() -> String.format("counter =" + counter));

                    try {
                        Response response = httpClient.newCall(request).execute();
                        response.body().close();
                        System.out.println(String.format("i= %03d and j = %03d", finalI, j));
                        System.out.println(String.format("counter = %03d", counter));
                        System.out.flush();
                        if(response.code() != 200) {
                            throw new Exception("Response is not 200!");
                        }
                        assertThat(response.code(), CoreMatchers.is(200));
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }

        executor.shutdown();
        System.out.printf("Server has handled load of %03d treads successfully%n", threadCount*callCountPerThread);
        System.out.flush();
    }
}