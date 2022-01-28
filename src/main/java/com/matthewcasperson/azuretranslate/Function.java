package com.matthewcasperson.azuretranslate;

import com.matthewcasperson.azuretranslate.services.SpeechService;
import com.matthewcasperson.azuretranslate.services.TranscribeService;
import com.matthewcasperson.azuretranslate.services.TranslateService;
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
    private static final SpeechService SPEECH_SERVICE = new SpeechService();
    private static final TranslateService TRANSLATE_SERVICE = new TranslateService();

    /**
     * Must use Content-Type: application/octet-stream
     * https://github.com.cnpmjs.org/microsoft/azure-maven-plugins/issues/1351
     */
    @FunctionName("transcribe")
    public HttpResponseMessage transcribe(
            @HttpTrigger(
                name = "req",
                methods = {HttpMethod.POST},
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

    @FunctionName("translate")
    public HttpResponseMessage translate(
        @HttpTrigger(
            name = "req",
            methods = {HttpMethod.POST},
            authLevel = AuthorizationLevel.ANONYMOUS)
        HttpRequestMessage<Optional<String>> request,
        final ExecutionContext context) {

        if (request.getBody().isEmpty()) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                .body("The text to translate file must be in the body of the post.")
                .build();
        }

        try {
            final String text = TRANSLATE_SERVICE.translate(
                request.getBody().get(),
                request.getQueryParameters().get("sourceLanguage"),
                request.getQueryParameters().get("targetLanguage"));

            return request.createResponseBuilder(HttpStatus.OK).body(text).build();
        } catch (final IOException ex) {
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("There was an error translating the text.")
                .build();
        }
    }

    @FunctionName("speak")
    public HttpResponseMessage speak(
        @HttpTrigger(
            name = "req",
            methods = {HttpMethod.POST},
            authLevel = AuthorizationLevel.ANONYMOUS)
        HttpRequestMessage<Optional<String>> request,
        final ExecutionContext context) {

        if (request.getBody().isEmpty()) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                .body("The text to translate file must be in the body of the post.")
                .build();
        }

        try {
            final byte[] audio = SPEECH_SERVICE.translateText(
                request.getBody().get(),
                request.getQueryParameters().get("targetLanguage"));

            return request.createResponseBuilder(HttpStatus.OK).body(audio).build();
        } catch (final IOException ex) {
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("There was an error converting the text to speech.")
                .build();
        }
    }
}
