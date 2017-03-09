package org.obsidiantoaster.quickstart;

import com.jayway.restassured.response.Response;
import org.junit.*;
import org.junit.runners.MethodSorters;

import java.net.MalformedURLException;
import java.util.concurrent.TimeUnit;

import static com.jayway.awaitility.Awaitility.await;
import static com.jayway.restassured.RestAssured.get;
import static org.hamcrest.core.IsEqual.equalTo;

/**
 * @author <a href="http://escoffier.me">Clement Escoffier</a>
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class OpenShiftIT {

    private static OpenShiftTestAssistant assistant = new OpenShiftTestAssistant();

    @BeforeClass
    public static void prepare() throws Exception {
        assistant.deployApplication();
    }

    @AfterClass
    public static void cleanup() {
        assistant.cleanup();
    }

    @Test
    public void testThatWeAreReady() throws Exception {
        assistant.awaitApplicationReadinessOrFail();
        await().atMost(5, TimeUnit.MINUTES).catchUncaughtExceptions().until(() -> {
            Response response = get();
            return response.getStatusCode() < 500;
        });
    }

    @Test
    public void testThatWeServeAsExpected() throws MalformedURLException {
        get("/greeting").then().body("content", equalTo("Hello, World!"));
        get("/greeting?name=vert.x").then().body("content", equalTo("Hello, vert.x!"));
    }

}
