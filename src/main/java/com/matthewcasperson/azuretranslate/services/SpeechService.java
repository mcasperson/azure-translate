package com.matthewcasperson.azuretranslate.services;

import com.microsoft.cognitiveservices.speech.AudioDataStream;
import com.microsoft.cognitiveservices.speech.SpeechConfig;
import com.microsoft.cognitiveservices.speech.SpeechSynthesisOutputFormat;
import com.microsoft.cognitiveservices.speech.SpeechSynthesisResult;
import com.microsoft.cognitiveservices.speech.SpeechSynthesizer;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.commons.io.FileUtils;

public class SpeechService {

  public byte[] translateText(final String input, final String targetLanguage) throws IOException {
    final Path tempFile = Files.createTempFile("", ".wav");

    try (final SpeechConfig speechConfig = SpeechConfig.fromSubscription(
        System.getenv("SPEECH_KEY"),
        System.getenv("SPEECH_REGION"))) {
      speechConfig.setSpeechSynthesisLanguage(targetLanguage);
      speechConfig.setSpeechSynthesisOutputFormat(SpeechSynthesisOutputFormat.Webm24Khz16Bit24KbpsMonoOpus);

      final SpeechSynthesizer synthesizer = new SpeechSynthesizer(speechConfig, null);
      final SpeechSynthesisResult result = synthesizer.SpeakText(input);
      final AudioDataStream stream = AudioDataStream.fromResult(result);
      stream.saveToWavFile(tempFile.toString());
      return Files.readAllBytes(tempFile);
    } finally {
      FileUtils.deleteQuietly(tempFile.toFile());
    }
  }
}
