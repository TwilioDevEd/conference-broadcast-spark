package com.twilio.conferencebroadcast.controllers;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import com.twilio.Twilio;
import com.twilio.conferencebroadcast.exceptions.UndefinedEnvironmentVariableException;
import com.twilio.conferencebroadcast.lib.AppSetup;
import com.twilio.exception.TwilioException;
import com.twilio.rest.api.v2010.account.Call;
import com.twilio.twiml.*;
import com.twilio.type.PhoneNumber;

import spark.ModelAndView;
import spark.Request;
import spark.Route;
import spark.TemplateViewRoute;

public class BroadcastController {
  AppSetup appSetup;

  public BroadcastController() {
    this.appSetup = new AppSetup();
  }

  public BroadcastController(AppSetup appSetup) {
    this.appSetup = appSetup;
  }

  public TemplateViewRoute index = (request, response) -> {
    Map<String, String> map = new HashMap();

    return new ModelAndView(map, "broadcast.mustache");
  };
  public Route record = (request, response) -> {
    response.type("application/xml");

    return getXMLRecordResponse();
  };
  public Route hangup = (request, response) -> {
    response.type("application/xml");

    return getXMLHangupResponse();
  };
  public Route play = (request, response) -> {
    return getXMLPlayResponse(request);
  };

  public TemplateViewRoute send = (request, response) -> {
    Map<String, String> map = new HashMap();
    broadcastSend(request);

    map.put("message", "true");
    map.put("notice", "Broadcast was successfully sent");

    return new ModelAndView(map, "broadcast.mustache");
  };

  /**
   * Returns the xml response that will play the recorded message for the given URL
   * 
   * @param request
   * @return xml response
   */
  public String getXMLPlayResponse(Request request) {
    String recordingUrl = request.queryParams("recording_url");

    Play play = new Play.Builder(recordingUrl).build();

    VoiceResponse voiceResponse = new VoiceResponse.Builder().play(play).build();

    try {
      return voiceResponse.toXml();
    } catch (TwiMLException e) {
      return "Unable tu create valid TwiML";
    }
  }

  /**
   * Method that will create the remote calls using Twilio's rest client for every number especified
   * in the CSV.
   * 
   * @param request
   */
  public void broadcastSend(Request request) {
    initializeTwilioClient();

    String numbers = request.queryParams("numbers");
    String recordingUrl = request.queryParams("recording_url");
    String[] parsedNumbers = numbers.split(",");
    String url =
        request.url().replace(request.uri(), "") + "/broadcast/play?recording_url=" + recordingUrl;
    String twilioNumber = null;
    try {
      twilioNumber = appSetup.getTwilioPhoneNumber();
    } catch (UndefinedEnvironmentVariableException e) {
      e.printStackTrace();
    }

    for (String number : parsedNumbers) {
      try {
        Call.create(new PhoneNumber(number), new PhoneNumber(twilioNumber), new URI(url)).execute();
      } catch (TwilioException e) {
        System.out.println("Twilio rest client error " + e.getLocalizedMessage());
        System.out.println("Remember not to use localhost to access this app, use your ngrok URL");
      } catch (URISyntaxException e) {
        System.out.println(e.getLocalizedMessage());
      }
    }
  }

  /**
   * This XML response is necessary to end the call when a new recording is made
   * 
   * @return
   */
  public String getXMLHangupResponse() {
    Say say = new Say.Builder("Your recording has been saved. Good bye.").build();
    Hangup hangup = new Hangup();

    VoiceResponse voiceResponse = new VoiceResponse.Builder().say(say).hangup(hangup).build();

    try {
      return voiceResponse.toXml();
    } catch (TwiMLException e) {
      System.out.println("Unable to create twiml response");
      return "Unable to create twiml response";
    }
  }

  public String getXMLRecordResponse() {
    Say say = new Say.Builder(
        "Please record your message after the beep. Press star to end your recording.").build();
    Record record = new Record.Builder()
        .action("/broadcast/hangup")
        .method(Method.POST)
        .finishOnKey("*")
        .build();

    VoiceResponse voiceResponse = new VoiceResponse.Builder()
        .say(say)
        .record(record)
        .build();

    try {
      return voiceResponse.toXml();
    } catch (TwiMLException e) {
      System.out.println("Unable to create Twiml Response");
      return "Unable to create Twiml Response";
    }
  }

  private void initializeTwilioClient() {
    String accountSid = null;
    String authToken = null;

    try {
      accountSid = appSetup.getAccountSid();
      authToken = appSetup.getAuthToken();
    } catch (UndefinedEnvironmentVariableException e) {
      System.out.println(e.getLocalizedMessage());
    }

    Twilio.init(accountSid, authToken);
  }
}
