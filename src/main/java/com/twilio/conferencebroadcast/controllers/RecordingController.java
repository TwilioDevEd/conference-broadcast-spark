package com.twilio.conferencebroadcast.controllers;

import com.twilio.Twilio;
import com.twilio.base.ResourceSet;
import com.twilio.conferencebroadcast.exceptions.UndefinedEnvironmentVariableException;
import com.twilio.conferencebroadcast.lib.AppSetup;
import com.twilio.conferencebroadcast.lib.RecordingUriTransformer;
import com.twilio.rest.api.v2010.account.Call;
import com.twilio.rest.api.v2010.account.Recording;
import com.twilio.type.PhoneNumber;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import spark.Request;
import spark.Route;

import java.net.URI;
import java.net.URISyntaxException;

public class RecordingController {
  AppSetup appSetup;

  public RecordingController(AppSetup appSetup) {
    this.appSetup = appSetup;
  }

  public RecordingController() {
    this.appSetup = new AppSetup();
  }

  public Route index = (request, response) -> {
    response.type("application/json");

    return getRecordingsAsJSON();
  };

  public Route create = (request, response) -> {
    int status = createRecording(request);
    response.status(status);
    return "";
  };

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

  /**
   * Function that creates a recording remotely using Twilio's rest client
   * 
   * @param request Request holds the phone_number parameter
   * @return Returns the status of the request
   */
  public int createRecording(Request request) {
    initializeTwilioClient();

    String phoneNumber = request.queryParams("phone_number");
    String twilioNumber = null;
    try {
      twilioNumber = appSetup.getTwilioPhoneNumber();
    } catch (UndefinedEnvironmentVariableException e) {
      e.printStackTrace();
    }
    String path = request.url().replace(request.uri(), "") + "/broadcast/record";

    Call call;
    try {
      call = Call.creator(new PhoneNumber(phoneNumber), new PhoneNumber(twilioNumber), new URI(path))
          .create();
    } catch (URISyntaxException e) {
      System.out.println("Invalid URL used in call creator");
    }

    return 200;
  }

  /**
   * Creates a JSON string that contains the url and date of all the user's recordings
   * 
   * @return Returns a JSON string
   */
  public String getRecordingsAsJSON() {
    initializeTwilioClient();

    ResourceSet<Recording> recordings = Recording.reader().read();

    JSONArray jsonRecordings = new JSONArray();

    for (Recording recording : recordings) {
      JSONObject obj = new JSONObject();
      obj.put("url", RecordingUriTransformer.transform(recording.getUri()));
      obj.put("date", recording.getDateCreated().toString("yyyy-M-dd HH:mm:ss"));
      jsonRecordings.add(obj);
    }

    return jsonRecordings.toJSONString();
  }
}
