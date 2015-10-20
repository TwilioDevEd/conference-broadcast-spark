package com.twilio.conferencebroadcast.lib;

public class RecordingUriTransformer {
  /**
   * Creates a full url based on the uri of a recording obtained using Twilio's
   * rest client.
   * @param originalUri
   * @return modified url
   */
  public static String transform(String originalUri) {
    return "https://api.twilio.com" + originalUri.replace(".json", "");
  }
}
