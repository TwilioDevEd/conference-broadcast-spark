package com.twilio.conferencebroadcast;

import com.twilio.conferencebroadcast.controllers.BroadcastController;
import com.twilio.conferencebroadcast.controllers.ConferenceController;
import com.twilio.conferencebroadcast.controllers.HomeController;
import com.twilio.conferencebroadcast.controllers.RecordingController;
import com.twilio.conferencebroadcast.lib.AppSetup;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import static spark.Spark.*;

/**
 * Main application class. The environment is set up here, and all necessary services are run.
 */
public class App {
  public static void main(String[] args) {
    AppSetup appSetup = new AppSetup();

    /**
     * Sets the port in which the application will run. Takes the port value from PORT
     * environment variable, if not set, uses Spark default port 4567.
     */
    port(appSetup.getPortNumber());

    /**
     * Gets the entity manager based on environment variable DATABASE_URL and injects it into
     * AppointmentService which handles all DB operations.
     */
    // EntityManagerFactory factory = appSetup.getEntityManagerFactory();

    /**
     * Specifies the directory within resources that will be publicly available when the
     * application is running. Place static web files in this directory (JS, CSS).
     */
    Spark.staticFileLocation("/public");

    ConferenceController conferenceController = new ConferenceController();
    BroadcastController broadcastController = new BroadcastController();
    RecordingController recordingController = new RecordingController();

    /**
     * Home route
     */
    get("/", new HomeController().index, new MustacheTemplateEngine());

    /**
     * Defines routes for everything related to conference calls
     */
    get("/conference", conferenceController.index, new MustacheTemplateEngine());
    post("/conference", conferenceController.join);
    post("/conference/connect", conferenceController.connect);

    /**
     * Defines routes for everything related to broadcast calls
     */
    get("/broadcast", broadcastController.index, new MustacheTemplateEngine());
    post("/broadcast/record", broadcastController.record);
    post("/broadcast/hangup", broadcastController.hangup);
    post("/broadcast/send", broadcastController.send, new MustacheTemplateEngine());
    post("/broadcast/play", broadcastController.play);

    /**
     * Defines routes for everything related to recordings
     */
    get("/recording/index", recordingController.index);
    post("/recording/create", recordingController.create);
  }
}
