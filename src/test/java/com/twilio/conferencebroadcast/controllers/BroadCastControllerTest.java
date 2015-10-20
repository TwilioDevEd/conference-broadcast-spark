package com.twilio.conferencebroadcast.controllers;

import com.twilio.conferencebroadcast.lib.AppSetup;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

public class BroadCastControllerTest {
  @Test
  public void getXMLRecordResponseTest() {
    AppSetup mockAppSetup = mock(AppSetup.class);
    BroadcastController controller = new BroadcastController(mockAppSetup);

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
    BroadcastController controller = new BroadcastController(mockAppSetup);

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
}
