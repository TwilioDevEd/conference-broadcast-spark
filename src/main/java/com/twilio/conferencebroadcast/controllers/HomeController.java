package com.twilio.conferencebroadcast.controllers;

import spark.ModelAndView;
import spark.TemplateViewRoute;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
public class HomeController {
  public TemplateViewRoute index = (request, response) -> {
    Map<String, String> map = new HashMap();

    return new ModelAndView(map, "home.mustache");
  };
}
