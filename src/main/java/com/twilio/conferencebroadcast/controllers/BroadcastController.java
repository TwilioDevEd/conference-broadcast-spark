package com.twilio.conferencebroadcast.controllers;

import com.twilio.conferencebroadcast.exceptions.UndefinedEnvironmentVariableException;
import com.twilio.conferencebroadcast.lib.AppSetup;
import com.twilio.sdk.TwilioRestClient;
import com.twilio.sdk.TwilioRestException;
import com.twilio.sdk.resource.factory.CallFactory;
import com.twilio.sdk.verbs.*;
import spark.ModelAndView;
import spark.Request;
import spark.Route;
import spark.TemplateViewRoute;

import java.util.HashMap;
import java.util.Map;

public class BroadcastController {
  AppSetup appSetup;
  TwilioRestClient client;

  public BroadcastController() {
    this.appSetup = new AppSetup();
    try {
      this.client = new TwilioRestClient(appSetup.getAccountSid(), appSetup.getAuthToken());
    } catch (UndefinedEnvironmentVariableException e) {
      System.out.println("Required environment variable undefined");
    }
  }

  public BroadcastController(AppSetup appSetup, TwilioRestClient client) {
    this.appSetup = appSetup;
    this.client = client;
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

  public TemplateViewRoute send = (request, response) -> {
    Map<String, String> map = new HashMap();
    broadcastSend(request);

    map.put("message", "true");
    map.put("notice", "Broadcast was successfully sent");

    return new ModelAndView(map, "broadcast.mustache");
  };

  public Route play = (request, response) -> {
    return getXMLPlayResponse(request);
  };

  /**
   * Returns the xml response that will play the recorded message for the given URL
   * @param request
   * @return xml response
   */
  public String getXMLPlayResponse(Request request) {
    TwiMLResponse twiMLResponse = new TwiMLResponse();
    String recordingUrl = request.queryParams("recording_url");

    Play play = new Play(recordingUrl);

    try {
      twiMLResponse.append(play);
    } catch (TwiMLException e) {
      System.out.println("Unable to create twiml response");
    }

    return twiMLResponse.toEscapedXML();
  }

  /**
   * Method that will create the remote calls using Twilio's rest client for every number
   * especified in the CSV.
   * @param request
   */
  public void broadcastSend(Request request) {
    String numbers = request.queryParams("numbers");
    String recordingUrl = request.queryParams("recording_url");
    String[] parsedNumbers = numbers.split(",");
    String url = request.url().replace(request.uri(), "") + "/broadcast/play?recording_url=" + recordingUrl;
    String twilioNumber = null;
    try {
      twilioNumber = appSetup.getTwilioPhoneNumber();
    } catch (UndefinedEnvironmentVariableException e) {
      e.printStackTrace();
    }

    CallFactory callFactory = client.getAccount().getCallFactory();

    for (String number : parsedNumbers) {
      Map<String, String> params = new HashMap<>();
      params.put("From", twilioNumber);
      params.put("To", number);
      params.put("Url", url);

      try {
        callFactory.create(params);
      } catch (TwilioRestException e) {
        System.out.println("Twilio rest client error " + e.getErrorMessage());
        System.out.println("Remember not to use localhost to access this app, use your ngrok URL");
      }
    }
  }

  /**
   * This XML response is necessary to end the call when a new recording is made
   * @return
   */
  public String getXMLHangupResponse() {
    TwiMLResponse twiMLResponse = new TwiMLResponse();

    Say say = new Say("Your recording has been saved. Good bye.");
    Hangup hangup = new Hangup();

    try {
      twiMLResponse.append(say);
      twiMLResponse.append(hangup);
    } catch (TwiMLException e) {
      System.out.println("Unable to create twiml response");
    }

    return twiMLResponse.toXML();
  }

  public String getXMLRecordResponse() {
    TwiMLResponse twiMLResponse = new TwiMLResponse();

    Say say = new Say("Please record your message after the beep. Press star to end your recording.");
    Record record = new Record();
    record.setAction("/broadcast/hangup");
    record.setMethod("POST");
    record.setFinishOnKey("*");

    try {
      twiMLResponse.append(say);
      twiMLResponse.append(record);
    } catch (TwiMLException e) {
      System.out.println("Unable to create Twiml Response");
    }

    return twiMLResponse.toXML();
  }
}
