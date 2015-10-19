package com.twilio.conferencebroadcast.controllers;

import com.twilio.conferencebroadcast.exceptions.UndefinedEnvironmentVariableException;
import com.twilio.conferencebroadcast.lib.AppSetup;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.junit.Test;
import spark.Request;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ConferenceControllerTest {
  @Test
  public void getXMLJoinResponseTest() throws UndefinedEnvironmentVariableException {
    AppSetup mockAppSetup = mock(AppSetup.class);
    ConferenceController controller = new ConferenceController(mockAppSetup);

    String xml = controller.getXMLJoinResponse();

    SAXBuilder builder = new SAXBuilder();
    Document document = null;
    List<Element> sayVerbs = null;
    String gatherVerbAction = null;
    try {
      document = builder.build(new StringReader(xml));
      sayVerbs = document.getRootElement().getChild("Gather").getChildren();
      gatherVerbAction = document.getRootElement().getChild("Gather").getAttributeValue("action");
    } catch (JDOMException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

    assertThat(sayVerbs.size(), is(3));
    assertThat(gatherVerbAction, is("/conference/connect"));
  }

  @Test
  public void getXMLConnectListenerTest() {
    AppSetup mockAppSetup = mock(AppSetup.class);
    Request mockRequest = mock(Request.class);
    ConferenceController controller = new ConferenceController(mockAppSetup);

    when(mockRequest.queryParams("Digits")).thenReturn("1");

    String xml = controller.getXMLConnectResponse(mockRequest);

    SAXBuilder builder = new SAXBuilder();
    Document document = null;
    List<Element> mainVerbs = null;
    Element conferenceAttributes = null;

    try {
      document = builder.build(new StringReader(xml));
      mainVerbs = document.getRootElement().getChildren();
      conferenceAttributes = document.getRootElement().getChild("Dial").getChild("Conference");
    } catch (JDOMException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

    assertThat(mainVerbs.size(), is(2));
    assertThat(conferenceAttributes.getAttributeValue("waitUrl"), is("http://twimlets.com/holdmusic?Bucket=com.twilio.music.ambient"));
    assertThat(conferenceAttributes.getAttributeValue("muted"), is("true"));
    assertThat(conferenceAttributes.getAttributeValue("startConferenceOnEnter"), is("false"));
    assertThat(conferenceAttributes.getAttributeValue("endConferenceOnExit"), is("false"));
  }

  @Test
  public void getXMLConnectModeratorTest() {
    AppSetup mockAppSetup = mock(AppSetup.class);
    Request mockRequest = mock(Request.class);
    ConferenceController controller = new ConferenceController(mockAppSetup);

    when(mockRequest.queryParams("Digits")).thenReturn("3");

    String xml = controller.getXMLConnectResponse(mockRequest);

    SAXBuilder builder = new SAXBuilder();
    Document document = null;
    List<Element> mainVerbs = null;
    Element conferenceAttributes = null;

    try {
      document = builder.build(new StringReader(xml));
      mainVerbs = document.getRootElement().getChildren();
      conferenceAttributes = document.getRootElement().getChild("Dial").getChild("Conference");
    } catch (JDOMException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

    assertThat(mainVerbs.size(), is(2));
    assertThat(conferenceAttributes.getAttributeValue("waitUrl"), is("http://twimlets.com/holdmusic?Bucket=com.twilio.music.ambient"));
    assertThat(conferenceAttributes.getAttributeValue("muted"), is("false"));
    assertThat(conferenceAttributes.getAttributeValue("startConferenceOnEnter"), is("true"));
    assertThat(conferenceAttributes.getAttributeValue("endConferenceOnExit"), is("true"));
  }

  @Test
  public void getXMLConnectSpeakerTest() {
    AppSetup mockAppSetup = mock(AppSetup.class);
    Request mockRequest = mock(Request.class);
    ConferenceController controller = new ConferenceController(mockAppSetup);

    when(mockRequest.queryParams("Digits")).thenReturn("2");

    String xml = controller.getXMLConnectResponse(mockRequest);

    SAXBuilder builder = new SAXBuilder();
    Document document = null;
    List<Element> mainVerbs = null;
    Element conferenceAttributes = null;

    try {
      document = builder.build(new StringReader(xml));
      mainVerbs = document.getRootElement().getChildren();
      conferenceAttributes = document.getRootElement().getChild("Dial").getChild("Conference");
    } catch (JDOMException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

    assertThat(mainVerbs.size(), is(2));
    assertThat(conferenceAttributes.getAttributeValue("waitUrl"), is("http://twimlets.com/holdmusic?Bucket=com.twilio.music.ambient"));
    assertThat(conferenceAttributes.getAttributeValue("muted"), is("false"));
    assertThat(conferenceAttributes.getAttributeValue("startConferenceOnEnter"), is("false"));
    assertThat(conferenceAttributes.getAttributeValue("endConferenceOnExit"), is("false"));
  }
}
