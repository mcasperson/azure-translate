package com.matthewcasperson.azuretranslate;

import com.matthewcasperson.azuretranslate.services.TranscribeService;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

/**
 * Azure Functions with HTTP Trigger.
 */
public class Function {

    private static final TranscribeService TRANSCRIBE_SERVICE = new TranscribeService();

    /**
     * This function listens at endpoint "/api/HttpExample". Two ways to invoke it using "curl" command in bash:
     * 1. curl -d "HTTP Body" {your host}/api/HttpExample
     * 2. curl "{your host}/api/HttpExample?name=HTTP%20Query"
     */
    @FunctionName("transcribe")
    public HttpResponseMessage run(
            @HttpTrigger(
                name = "req",
                methods = {HttpMethod.GET, HttpMethod.POST},
                authLevel = AuthorizationLevel.ANONYMOUS)
                HttpRequestMessage<Optional<byte[]>> request,
            final ExecutionContext context) {

        if (request.getBody().isEmpty()) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                .body("The audio file must be in the body of the post.")
                .build();
        }

        try {
            final String text = TRANSCRIBE_SERVICE.transcribe(
                request.getBody().get(),
                request.getQueryParameters().get("language"));

            return request.createResponseBuilder(HttpStatus.OK).body(text).build();
        } catch (final IOException | ExecutionException | InterruptedException ex) {
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("There was an error transcribing the audio file.")
                .build();
        }
    }
}
