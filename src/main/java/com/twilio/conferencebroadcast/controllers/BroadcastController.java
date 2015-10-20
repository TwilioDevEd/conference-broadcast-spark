package com.twilio.conferencebroadcast.controllers;

import com.twilio.conferencebroadcast.lib.AppSetup;
import com.twilio.sdk.verbs.*;
import spark.ModelAndView;
import spark.Route;
import spark.TemplateViewRoute;

import java.util.HashMap;
import java.util.Map;

public class BroadcastController {
  AppSetup appSetup;

  public BroadcastController() {
    this.appSetup = new AppSetup();
  }

  public BroadcastController(AppSetup appSetup) {
    this.appSetup = appSetup;
  }

  public TemplateViewRoute index = (request, response) -> {
    Map map = new HashMap();
    String number = appSetup.getConferenceNumber();

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
