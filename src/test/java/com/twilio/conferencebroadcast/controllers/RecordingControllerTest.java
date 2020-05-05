package com.twilio.conferencebroadcast.controllers;

import com.twilio.base.ResourceSet;
import com.twilio.conferencebroadcast.exceptions.UndefinedEnvironmentVariableException;
import com.twilio.conferencebroadcast.lib.AppSetup;
import com.twilio.rest.api.v2010.account.Call;
import com.twilio.rest.api.v2010.account.CallCreator;
import com.twilio.rest.api.v2010.account.Recording;
import com.twilio.rest.api.v2010.account.RecordingReader;
import com.twilio.type.PhoneNumber;
import org.joda.time.DateTime;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import spark.Request;

import java.net.URI;
import java.util.Iterator;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@PowerMockIgnore({ "javax.management.*", "com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "org.w3c.*",
    "com.sun.org.apache.xalan.*" })
@RunWith(PowerMockRunner.class)
public class RecordingControllerTest {
  @Test
  @PrepareForTest(Recording.class)
  public void getRecordingsAsJSONTest() throws UndefinedEnvironmentVariableException {
    AppSetup mockAppSetup = mock(AppSetup.class);

    Recording mockRecording1 = mock(Recording.class);
    Recording mockRecording2 = mock(Recording.class);
    Iterator recordingIterator = mock(Iterator.class);

    RecordingReader readerMock = mock(RecordingReader.class);
    mockStatic(Recording.class);

    ResourceSet<Recording> mockRecordingList = mock(ResourceSet.class);

    DateTime newDate = DateTime.parse("2013-12-21T14:12:21");

    when(Recording.reader()).thenReturn(readerMock);
    when(readerMock.read()).thenReturn(mockRecordingList);
    when(mockRecordingList.iterator()).thenReturn(recordingIterator);
    when(recordingIterator.hasNext()).thenReturn(true, true, false);
    when(recordingIterator.next()).thenReturn(mockRecording1).thenReturn(mockRecording2);
    when(mockRecording1.getUri()).thenReturn("/some/uri");
    when(mockRecording1.getDateCreated()).thenReturn(newDate);
    when(mockRecording2.getUri()).thenReturn("/some/uri");
    when(mockRecording2.getDateCreated()).thenReturn(newDate);
    when(Recording.reader()).thenReturn(readerMock);
    when(mockAppSetup.getAccountSid()).thenReturn("accountSid");
    when(mockAppSetup.getAuthToken()).thenReturn("authToken");

    RecordingController controller = new RecordingController(mockAppSetup);
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
    assertThat(firstElement.get("date"), is(newDate.toString("yyyy-M-dd HH:mm:ss")));
  }

  @Test
  @PrepareForTest(Call.class)
  public void createRecordingTest() throws UndefinedEnvironmentVariableException {
    AppSetup mockAppSetup = mock(AppSetup.class);
    Request mockRequest = mock(Request.class);
    CallCreator mockCallCreator = mock(CallCreator.class);
    Call mockCall = mock(Call.class);

    RecordingController controller = new RecordingController(mockAppSetup);

    mockStatic(Call.class);

    when(Call.creator(any(PhoneNumber.class), any(PhoneNumber.class), any(URI.class))).thenReturn(mockCallCreator);
    when(mockCallCreator.create()).thenReturn(mockCall);
    when(mockRequest.queryParams("phone_number")).thenReturn("+number");
    when(mockRequest.url()).thenReturn("some_url");
    when(mockRequest.uri()).thenReturn("some_uri");
    when(mockAppSetup.getTwilioPhoneNumber()).thenReturn("+twilio_number");
    when(mockAppSetup.getAccountSid()).thenReturn("accountSid");
    when(mockAppSetup.getAuthToken()).thenReturn("authToken");

    int status = controller.createRecording(mockRequest);

    verify(mockRequest).queryParams("phone_number");
    verify(mockCallCreator).create();

    assertThat(status, is(200));
  }
}
