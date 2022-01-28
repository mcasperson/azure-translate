package com.matthewcasperson.azuretranslate.readers;

import com.microsoft.cognitiveservices.speech.audio.PullAudioInputStreamCallback;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * https://github.com/Azure-Samples/cognitive-services-speech-sdk/blob/master/samples/java/android/compressed-input/app/src/main/java/com/microsoft/cognitiveservices/speech/samples/compressedinput/BinaryAudioStreamReader.java
 */
public class BinaryAudioStreamReader extends PullAudioInputStreamCallback {

  InputStream inputStream;

  public BinaryAudioStreamReader(String fileName) throws FileNotFoundException {
    File file = new File(fileName);
    inputStream = new FileInputStream(file);
  }

  @Override
  public int read(byte[] dataBuffer) {
    try {
      return inputStream.read(dataBuffer, 0, dataBuffer.length);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return 0;
  }

  /**
   * Closes the audio input stream.
   */
  @Override
  public void close() {
    try {
      inputStream.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
