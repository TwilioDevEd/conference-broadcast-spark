package com.twilio.conferencebroadcast.controllers;

import com.twilio.conferencebroadcast.exceptions.UndefinedEnvironmentVariableException;
import com.twilio.conferencebroadcast.lib.AppSetup;
import com.twilio.conferencebroadcast.lib.RecordingUriTransformer;
import com.twilio.sdk.TwilioRestClient;
import com.twilio.sdk.resource.instance.Recording;
import com.twilio.sdk.resource.list.RecordingList;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import spark.Route;

import java.text.SimpleDateFormat;

public class RecordingController {
  AppSetup appSetup;
  TwilioRestClient client;
  public Route index = (request, response) -> {
    response.type("application/json");

    return getRecordingsAsJSON();
  };

  public RecordingController() {
    this.appSetup = new AppSetup();
    try {
      this.client = new TwilioRestClient(appSetup.getAccountSid(), appSetup.getAuthToken());
    } catch (UndefinedEnvironmentVariableException e) {
      System.out.println(e.getMessage());
    }
  }

  public RecordingController(AppSetup appSetup, TwilioRestClient client) {
    this.appSetup = appSetup;
    this.client = client;
  }

  public String getRecordingsAsJSON() {
    RecordingList recordings = client.getAccount().getRecordings();
    JSONArray jsonRecordings = new JSONArray();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-M-dd");

    for (Recording recording : recordings) {
      JSONObject obj = new JSONObject();
      obj.put("url", RecordingUriTransformer.transform(recording.getProperty("uri")));
      obj.put("date", dateFormat.format(recording.getDateCreated()));
      jsonRecordings.add(obj);
    }

    return jsonRecordings.toJSONString();
  }
}
