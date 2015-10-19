package com.twilio.conferencebroadcast.lib;

public class RecordingUriTransformer {
  public static String transform(String originalUri) {
    return "https://api.twilio.com" + originalUri.replace(".json", "");
  }
}
