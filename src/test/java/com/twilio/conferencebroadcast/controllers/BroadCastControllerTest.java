package com.twilio.conferencebroadcast.controllers;

import com.twilio.conferencebroadcast.exceptions.UndefinedEnvironmentVariableException;
import com.twilio.conferencebroadcast.lib.AppSetup;
import com.twilio.sdk.TwilioRestClient;
import com.twilio.sdk.TwilioRestException;
import com.twilio.sdk.resource.factory.CallFactory;
import com.twilio.sdk.resource.instance.Account;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.junit.Test;
import spark.Request;

import java.io.IOException;
import java.io.StringReader;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class BroadCastControllerTest {
  @Test
  public void getXMLRecordResponseTest() {
    AppSetup mockAppSetup = mock(AppSetup.class);
    TwilioRestClient mockClient = mock(TwilioRestClient.class);
    BroadcastController controller = new BroadcastController(mockAppSetup, mockClient);

    String xml = controller.getXMLRecordResponse();

    SAXBuilder builder = new SAXBuilder();
    Document document = null;
    Element sayElement = null;
    Element recordElement = null;

    try {
      document = builder.build(new StringReader(xml));
      sayElement = document.getRootElement().getChild("Say");
      recordElement = document.getRootElement().getChild("Record");
    } catch (JDOMException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

    assertThat(sayElement.getText(), is("Please record your message after the beep. Press star to end your recording."));
    assertThat(recordElement.getAttributeValue("finishOnKey"), is("*"));
    assertThat(recordElement.getAttributeValue("action"), is("/broadcast/hangup"));
  }

  @Test
  public void getXMLHangupResponseTest() {
    AppSetup mockAppSetup = mock(AppSetup.class);
    TwilioRestClient mockClient = mock(TwilioRestClient.class);
    BroadcastController controller = new BroadcastController(mockAppSetup, mockClient);

    String xml = controller.getXMLHangupResponse();

    SAXBuilder builder = new SAXBuilder();
    Document document = null;
    Element sayElement = null;
    Element hangupElement = null;

    try {
      document = builder.build(new StringReader(xml));
      sayElement = document.getRootElement().getChild("Say");
      hangupElement = document.getRootElement().getChild("Hangup");
    } catch (JDOMException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

    assertNotNull(hangupElement);
    assertThat(sayElement.getText(), is("Your recording has been saved. Good bye."));
  }

  @Test
  public void broadcastSendTest() throws UndefinedEnvironmentVariableException,
      TwilioRestException {
    AppSetup mockAppSetup = mock(AppSetup.class);
    TwilioRestClient mockClient = mock(TwilioRestClient.class);
    Account mockAccount = mock(Account.class);
    CallFactory mockCallFactory = mock(CallFactory.class);
    Request mockRequest = mock(Request.class);

    BroadcastController controller = new BroadcastController(mockAppSetup, mockClient);

    when(mockClient.getAccount()).thenReturn(mockAccount);
    when(mockAccount.getCallFactory()).thenReturn(mockCallFactory);
    when(mockAppSetup.getTwilioPhoneNumber()).thenReturn("+twilio_number");
    when(mockRequest.queryParams("numbers")).thenReturn("+12345,+67890");
    when(mockRequest.queryParams("recording_url")).thenReturn("Some_url");
    when(mockRequest.url()).thenReturn("some_url");
    when(mockRequest.uri()).thenReturn("some_uri");

    controller.broadcastSend(mockRequest);

    verify(mockRequest).queryParams("numbers");
    verify(mockRequest).queryParams("recording_url");
    verify(mockCallFactory, times(2)).create(any(Map.class));
  }

  @Test
  public void getXMLPlayResponse() {
    AppSetup mockAppSetup = mock(AppSetup.class);
    TwilioRestClient mockClient = mock(TwilioRestClient.class);
    Request mockRequest = mock(Request.class);

    BroadcastController controller = new BroadcastController(mockAppSetup, mockClient);

    when(mockRequest.queryParams("recording_url")).thenReturn("http://www.example.com/uri");

    String xml = controller.getXMLPlayResponse(mockRequest);

    SAXBuilder builder = new SAXBuilder();
    Document document = null;
    Element playElement = null;

    try {
      document = builder.build(new StringReader(xml));
      playElement = document.getRootElement().getChild("Play");
    } catch (JDOMException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

    assertThat(playElement.getText(), is("http://www.example.com/uri"));
  }
}
