package com.twilio.conferencebroadcast.controllers;

import com.twilio.conferencebroadcast.lib.AppSetup;
import com.twilio.sdk.verbs.*;
import spark.ModelAndView;
import spark.Request;
import spark.Route;
import spark.TemplateViewRoute;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
public class ConferenceController {
  AppSetup appSetup;

  public ConferenceController() {
    this.appSetup = new AppSetup();
  }

  public ConferenceController(AppSetup appSetup) {
    this.appSetup = appSetup;
  }

  public Route join = (request, response) -> {
    response.type("application/xml");

    return getXMLJoinResponse();
  };

  public TemplateViewRoute index = (request, response) -> {
    Map map = new HashMap();
    String number = appSetup.getConferenceNumber();
    map.put("conference_number", number);

    return new ModelAndView(map, "conference.mustache");
  };

  public Route connect = (request, response) -> {
    response.type("application/xml");

    return getXMLConnectResponse(request);
  };

  public String getXMLJoinResponse() {
    TwiMLResponse twimlResponse = new TwiMLResponse();

    String defaultMessage =
        "You are about to join the Rapid Response conference.";
    Say sayMessage = new Say(defaultMessage);
    Say sayOption1 = new Say("Press 1 to join as a listener.");
    Say sayOption2 = new Say("Press 2 to join as a speaker.");
    Say sayOption3 = new Say("Press 3 to join as the moderator.");
    Gather gather = new Gather();
    gather.setAction("/conference/connect");
    gather.setMethod("POST");

    try {
      twimlResponse.append(sayMessage);
      gather.append(sayOption1);
      gather.append(sayOption2);
      gather.append(sayOption3);
      twimlResponse.append(gather);
    } catch (TwiMLException e) {
      System.out.println("Twilio's response building error");
    }

    return twimlResponse.toXML();
  }

  public String getXMLConnectResponse(Request request) {
    Boolean muted = false;
    Boolean moderator = false;
    String digits = request.queryParams("Digits");

    if (digits.equals("1")) {
      muted = true;
    }
    if (digits.equals("3")) {
      moderator = true;
    }
    TwiMLResponse twimlResponse = new TwiMLResponse();

    String defaultMessage = "You have joined the conference.";
    Say sayMessage = new Say(defaultMessage);

    Dial dial = new Dial();
    Conference conference = new Conference("RapidResponseRoom");
    conference.setWaitUrl("http://twimlets.com/holdmusic?Bucket=com.twilio.music.ambient");
    conference.setMuted(muted);
    conference.setStartConferenceOnEnter(moderator);
    conference.setEndConferenceOnExit(moderator);
    try {
      dial.append(conference);
      twimlResponse.append(sayMessage);
      twimlResponse.append(dial);
    } catch (TwiMLException e) {
      System.out.println("Twilio's response building error");
    }
    return twimlResponse.toXML();
  }
}
