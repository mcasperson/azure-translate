package com.matthewcasperson.azuretranslate.readers;

import com.microsoft.cognitiveservices.speech.audio.PullAudioInputStreamCallback;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ByteArrayReader extends PullAudioInputStreamCallback {

  private InputStream inputStream;

  public ByteArrayReader(final byte[] data) {
    inputStream = new ByteArrayInputStream(data);
  }

  @Override
  public int read(final byte[] bytes) {
    try {
      return inputStream.read(bytes, 0, bytes.length);
    } catch (final IOException e) {
      e.printStackTrace();
    }
    return 0;
  }

  @Override
  public void close() {
    try {
      inputStream.close();
    } catch (final IOException e) {
      e.printStackTrace();
    }
  }
}
