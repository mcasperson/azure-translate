package com.matthewcasperson.azuretranslate.services;

import java.io.IOException;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.commons.text.StringEscapeUtils;

public class TranslateService {

  public String translate(final String input, final String sourceLanguage,
      final String targetLanguage) throws IOException {

    final HttpUrl url = new HttpUrl.Builder()
        .scheme("https")
        .host("api.cognitive.microsofttranslator.com")
        .addPathSegment("/translate")
        .addQueryParameter("api-version", "3.0")
        .addQueryParameter("from", sourceLanguage)
        .addQueryParameter("to", targetLanguage)
        .build();

    final OkHttpClient client = new OkHttpClient();

    final MediaType mediaType = MediaType.parse("application/json");
    final RequestBody body = RequestBody.create("[{\"Text\": \""
        + StringEscapeUtils.escapeJson(input) + "\"}]", mediaType);
    final Request request = new Request.Builder().url(url).post(body)
        .addHeader("Ocp-Apim-Subscription-Key", System.getenv("TRANSLATOR_KEY"))
        .addHeader("Ocp-Apim-Subscription-Region", System.getenv("TRANSLATOR_REGION"))
        .addHeader("Content-type", "application/json")
        .build();
    final Response response = client.newCall(request).execute();
    return response.body().string();
  }
}
