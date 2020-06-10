package com.twilio.conferencebroadcast.lib;

import com.twilio.conferencebroadcast.exceptions.UndefinedEnvironmentVariableException;

import java.util.Map;

/**
 * Class that holds methods to obtain configuration parameters from the environment.
 */
public class AppSetup {
  private Map<String, String> env;

  public AppSetup() {
    this.env = System.getenv();
  }

  public int getPortNumber() {
    String port = env.get("PORT");

    if (port != null) {
      return Integer.parseInt(port);
    } else {
      return 8080;
    }
  }

  public String getAccountSid() throws UndefinedEnvironmentVariableException {
    String sid = env.get("TWILIO_ACCOUNT_SID");
    if (sid == null) {
      throw new UndefinedEnvironmentVariableException("TWILIO_ACCOUNT_SID is not defined");
    } else {
      return sid;
    }
  }

  public String getAuthToken() throws UndefinedEnvironmentVariableException {
    String token = env.get("TWILIO_AUTH_TOKEN");
    if (token == null) {
      throw new UndefinedEnvironmentVariableException("TWILIO_AUTH_TOKEN is not set");
    } else {
      return token;
    }
  }

  public String getTwilioPhoneNumber() throws UndefinedEnvironmentVariableException {
    String phoneNumber = env.get("TWILIO_PHONE_NUMBER");
    if (phoneNumber == null) {
      throw new UndefinedEnvironmentVariableException("TWILIO_PHONE_NUMBER is not set");
    } else {
      return phoneNumber;
    }
  }

  public String getApplicationSid() throws UndefinedEnvironmentVariableException {
    String sid = env.get("TWILIO_APPLICATION_SID");
    if (sid == null) {
      throw new UndefinedEnvironmentVariableException("TWILIO_APPLICATION_SID is not set");
    } else {
      return sid;
    }
  }

  public String getConferenceNumber() throws UndefinedEnvironmentVariableException {
    String number = env.get("TWILIO_RR_NUMBER");
    if (number == null) {
      throw new UndefinedEnvironmentVariableException("TWILIO_RR_NUMBER is not set");
    } else {
      return number;
    }
  }
}
