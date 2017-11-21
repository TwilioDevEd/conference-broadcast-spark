package com.twilio.conferencebroadcast.controllers;

import java.util.HashMap;
import java.util.Map;

import com.twilio.conferencebroadcast.lib.AppSetup;

import com.twilio.http.HttpMethod;
import com.twilio.twiml.TwiMLException;
import com.twilio.twiml.VoiceResponse;
import com.twilio.twiml.voice.Conference;
import com.twilio.twiml.voice.Dial;
import com.twilio.twiml.voice.Gather;
import com.twilio.twiml.voice.Say;
import spark.ModelAndView;
import spark.Request;
import spark.Route;
import spark.TemplateViewRoute;

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
  public Route connect = (request, response) -> {
    response.type("application/xml");

    return getXMLConnectResponse(request);
  };

  public TemplateViewRoute index = (request, response) -> {
    Map<String, String> map = new HashMap();
    String number = appSetup.getConferenceNumber();
    map.put("conference_number", number);

    return new ModelAndView(map, "conference.mustache");
  };

  /**
   * Generates the xml necessary to ask the user what role will be used to join the call
   * 
   * @return XML response
   */
  public String getXMLJoinResponse() {
    String message =
        "You are about to join the Rapid Response conference." + "Press 1 to join as a listener."
            + "Press 2 to join as a speaker." + "Press 3 to join as the moderator.";

    Say sayMessage = new Say.Builder(message).build();
    Gather gather = new Gather.Builder()
        .action("/conference/connect")
        .method(HttpMethod.POST)
        .say(sayMessage)
        .build();

    VoiceResponse voiceResponse = new VoiceResponse.Builder().gather(gather).build();

    try {
      return voiceResponse.toXml();
    } catch (TwiMLException e) {
      System.out.println("Twilio's response building error");
      return "Twilio's response building error";
    }
  }

  /**
   * Returns necessary xml to join the conference call
   * 
   * @param request
   * @return XML string
   */
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

    String defaultMessage = "You have joined the conference.";
    Say sayMessage = new Say.Builder(defaultMessage).build();

    Conference conference = new Conference.Builder("RapidResponseRoom")
        .waitUrl("http://twimlets.com/holdmusic?Bucket=com.twilio.music.ambient")
        .muted(muted)
        .startConferenceOnEnter(moderator)
        .endConferenceOnExit(moderator)
        .build();

    Dial dial = new Dial.Builder().conference(conference).build();

    VoiceResponse voiceResponse = new VoiceResponse.Builder().say(sayMessage).dial(dial).build();
    try {
      return voiceResponse.toXml();
    } catch (TwiMLException e) {
      System.out.println("Twilio's response building error");
      return "Twilio's response building error";
    }
  }
}
