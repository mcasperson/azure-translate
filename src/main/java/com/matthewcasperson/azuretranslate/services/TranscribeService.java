package com.matthewcasperson.azuretranslate.services;

import com.matthewcasperson.azuretranslate.readers.ByteArrayReader;
import com.microsoft.cognitiveservices.speech.SpeechRecognitionResult;
import com.microsoft.cognitiveservices.speech.SpeechRecognizer;
import com.microsoft.cognitiveservices.speech.audio.AudioConfig;
import com.microsoft.cognitiveservices.speech.audio.AudioStreamContainerFormat;
import com.microsoft.cognitiveservices.speech.audio.AudioStreamFormat;
import com.microsoft.cognitiveservices.speech.audio.PullAudioInputStream;
import com.microsoft.cognitiveservices.speech.translation.SpeechTranslationConfig;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class TranscribeService {

  public String transcribe(final byte[] file, final String language)
      throws IOException, ExecutionException, InterruptedException {
    try {
      try (SpeechTranslationConfig config = SpeechTranslationConfig.fromSubscription(
          System.getenv("SPEECH_KEY"),
          System.getenv("SPEECH_REGION"))) {
        config.setSpeechRecognitionLanguage(language);
        final PullAudioInputStream pullAudio = PullAudioInputStream.create(
            new ByteArrayReader(file),
            AudioStreamFormat.getCompressedFormat(AudioStreamContainerFormat.ANY));
        final AudioConfig audioConfig = AudioConfig.fromStreamInput(pullAudio);
        final SpeechRecognizer reco = new SpeechRecognizer(config, audioConfig);
        final Future<SpeechRecognitionResult> task = reco.recognizeOnceAsync();
        final SpeechRecognitionResult result = task.get();
        return result.getText();
      }
    } catch (final Exception ex) {
      System.out.println(ex);
      throw ex;
    }
  }
}
