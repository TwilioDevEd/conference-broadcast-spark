package com.twilio.conferencebroadcast;

import com.twilio.conferencebroadcast.lib.AppSetup;
import spark.Spark;

import static spark.Spark.port;

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
  }
}
