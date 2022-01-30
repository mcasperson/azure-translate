package com.matthewcasperson.azuretranslate.services;

import com.matthewcasperson.azuretranslate.readers.BinaryAudioStreamReader;
import com.microsoft.cognitiveservices.speech.SpeechRecognitionResult;
import com.microsoft.cognitiveservices.speech.SpeechRecognizer;
import com.microsoft.cognitiveservices.speech.audio.AudioConfig;
import com.microsoft.cognitiveservices.speech.audio.AudioStreamContainerFormat;
import com.microsoft.cognitiveservices.speech.audio.AudioStreamFormat;
import com.microsoft.cognitiveservices.speech.audio.PullAudioInputStream;
import com.microsoft.cognitiveservices.speech.translation.SpeechTranslationConfig;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.apache.commons.io.FileUtils;

public class TranscribeService {

  public String transcribe(final byte[] file, final String language)
      throws IOException, ExecutionException, InterruptedException {
    Path audioFile = null;
    try {
      audioFile = saveFileToDisk(file);

      try (SpeechTranslationConfig config = SpeechTranslationConfig.fromSubscription(
          System.getenv("SPEECH_KEY"),
          System.getenv("SPEECH_REGION"))) {
        config.setSpeechRecognitionLanguage(language);
        final PullAudioInputStream pullAudio = PullAudioInputStream.create(
            new BinaryAudioStreamReader(audioFile.toString()),
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
    } finally {
      if (audioFile != null) {
        FileUtils.deleteQuietly(audioFile.toFile());
      }
    }
  }

  private Path saveFileToDisk(final byte[] file) throws IOException {
    final Path tempFile = Files.createTempFile("", ".webm");
    Files.write(tempFile, file);
    return tempFile;
  }
}
