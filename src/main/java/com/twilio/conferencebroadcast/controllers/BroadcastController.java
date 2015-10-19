package com.twilio.conferencebroadcast.controllers;

import com.twilio.conferencebroadcast.lib.AppSetup;
import spark.ModelAndView;
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
}
