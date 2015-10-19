package com.twilio.conferencebroadcast.controllers;

import com.twilio.conferencebroadcast.lib.AppSetup;
import com.twilio.sdk.TwilioRestClient;
import com.twilio.sdk.resource.instance.Account;
import com.twilio.sdk.resource.instance.Recording;
import com.twilio.sdk.resource.list.RecordingList;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RecordingControllerTest {
  @Test
  public void getRecordingsAsJSONTest() {
    AppSetup mockAppSetup = mock(AppSetup.class);
    TwilioRestClient mockClient = mock(TwilioRestClient.class);
    Account mockAccount = mock(Account.class);
    RecordingList mockRecordingList = mock(RecordingList.class);
    Recording mockRecording1 = mock(Recording.class);
    Recording mockRecording2 = mock(Recording.class);
    Iterator recordingIterator = mock(Iterator.class);

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-M-dd");
    String strDate = "2013-12-21";
    Date newDate = null;
    try {
      newDate = dateFormat.parse(strDate);
    } catch (java.text.ParseException e) {
      System.out.println("Unable to parse date");
    }

    when(mockClient.getAccount()).thenReturn(mockAccount);
    when(mockAccount.getRecordings()).thenReturn(mockRecordingList);
    when(mockRecordingList.iterator()).thenReturn(recordingIterator);
    when(recordingIterator.hasNext()).thenReturn(true, true, false);
    when(recordingIterator.next()).thenReturn(mockRecording1).thenReturn(mockRecording2);
    when(mockRecording1.getProperty("uri")).thenReturn("/some/uri");
    when(mockRecording1.getDateCreated()).thenReturn(newDate);
    when(mockRecording2.getProperty("uri")).thenReturn("/some/uri");
    when(mockRecording2.getDateCreated()).thenReturn(newDate);

    RecordingController controller = new RecordingController(mockAppSetup, mockClient);
    String json = controller.getRecordingsAsJSON();
    JSONArray obj = null;

    try {
      obj = (JSONArray) new JSONParser().parse(json);
    } catch (ParseException e) {
      e.printStackTrace();
    }

    JSONObject firstElement = (JSONObject) obj.get(0);

    assertThat(obj.size(), is(2));
    assertThat(firstElement.get("url"), is("https://api.twilio.com/some/uri"));
    assertThat(firstElement.get("date"), is(dateFormat.format(newDate)));
  }
}
