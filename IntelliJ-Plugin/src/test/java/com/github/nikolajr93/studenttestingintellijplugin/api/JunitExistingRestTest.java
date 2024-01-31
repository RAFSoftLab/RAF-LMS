package com.github.nikolajr93.studenttestingintellijplugin.api;

import com.github.nikolajr93.studenttestingintellijplugin.Config;
import org.hamcrest.CoreMatchers;
import org.jsmart.zerocode.core.runner.ZeroCodeUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;

import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(ZeroCodeUnitRunner.class)
public class JunitExistingRestTest {
    private static final HttpClient CLIENT = HttpClient.newHttpClient();

    /**
     * This test sometimes failed due to GitHub rate limiting settings.
     * The failures should be captured in the reports(CSV and html).
     * This is knowing kept here to test the rate limiting and show
     * how a failed test can be tracked in the log/reports
     */
    @Test
    public void testRafStudentsGetApi() {

        String result = null;
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(Config.REST_API_BASE_URL))
                    .build();

            // - - - - - - - - - - - - - - - - - - - - - - - - -
            // Add known delay to reflect "responseDelay" value
            // in the CSV report at least more than this number.
            // - - - - - - - - - - - - - - - - - - - - - - - - -

            Thread.sleep(1000);

            java.net.http.HttpResponse<String> response = CLIENT.send(request, java.net.http.HttpResponse.BodyHandlers.ofString());
            result = response.body();
            System.out.println("### response: \n" + result);
            assertThat(response.statusCode(), CoreMatchers.is(200));

        } catch (IOException | InterruptedException e){
            e.printStackTrace();
        }
    }
}