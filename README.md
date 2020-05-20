<a  href="https://www.twilio.com">
<img  src="https://static0.twilio.com/marketing/bundles/marketing/img/logos/wordmark-red.svg"  alt="Twilio"  width="250"  />
</a>

# Rapid Response Kit: Building Conferencing and Broadcasting with Twilio. Level: Intermediate. Powered by Twilio - Spark

![](https://github.com/TwilioDevEd/conference-broadcast-spark/workflows/Gradle/badge.svg)

> We are currently in the process of updating this sample template. If you are encountering any issues with the sample, please open an issue at [github.com/twilio-labs/code-exchange/issues](https://github.com/twilio-labs/code-exchange/issues) and we'll try to help you.

## About

An example application implementing an disaster response kit that allows an organizer to instantly communicate with volunteers.

[Read the full tutorial here](https://www.twilio.com/docs/tutorials/walkthrough/conference-broadcast/java/spark)!

Implementations in other languages:

| PHP | .NET | Python | Ruby | Node |
| :--- | :--- | :----- | :-- | :--- |
| [Done](https://github.com/TwilioDevEd/conference-broadcast-laravel)  | [Done](https://github.com/TwilioDevEd/conference-broadcast-csharp)  | TBD  | [Done](https://github.com/TwilioDevEd/conference-broadcast-rails) | TDB |

<!--
### How it works

**TODO: Describe how it works**
-->

## Set up

### Requirements

- [Java Development Kit](https://adoptopenjdk.net/) version 11 or later.
- [ngrok](https://ngrok.com)
- A Twilio account - [sign up](https://www.twilio.com/try-twilio)

### Twilio Account Settings

This application should give you a ready-made starting point for writing your
own appointment reminder application. Before we begin, we need to collect
all the config values we need to run the application:

| Config&nbsp;Value | Description                                                                                                                                                  |
| :---------------- | :----------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Account&nbsp;Sid  | Your primary Twilio account identifier - find this [in the Console](https://www.twilio.com/console).                                                         |
| Auth&nbsp;Token   | Used to authenticate - [just like the above, you'll find this here](https://www.twilio.com/console).                                                         |
| Phone&nbsp;number | A Twilio phone number in [E.164 format](https://en.wikipedia.org/wiki/E.164) - you can [get one here](https://www.twilio.com/console/phone-numbers/incoming) |

### Local development

After the above requirements have been met:

1. Clone this repository and `cd` into it

    ```bash
    git clone git@github.com:TwilioDevEd/conference-broadcast-spark.git
    cd conference-broadcast-spark
    ```

2. Set your environment variables

    ```bash
    cp .env.example .env
    ```
    See [Twilio Account Settings](#twilio-account-settings) to locate the necessary environment variables.

    If you are using a UNIX operating system, when the application starts the environment variables will be loaded. _If you are using a different operating system, make sure that all the
    variables from the `.env` file are loaded into your environment._

3. Run the application

    ```bash
    make serve
    ```
    **NOTE:** If you are using a dedicated Java IDE like Eclipse or IntelliJ, you can start the application within the IDE and it will start in development mode, which means any changes on a source file will be automatically reloaded.

7. Navigate to [http://localhost:8080](http://localhost:8080)

That's it!

### Configure Twilio

Configure Twilio to call your webhooks. You will also need to configure Twilio to call your application when calls are received.

You will need to provision at least one Twilio number with voice capabilities so the application's users can join conference calls. You can buy a number [right here](https://www.twilio.com/user/account/phone-numbers/search). Once you have a number you need to configure your number to work with your application. Open [the number management page](https://www.twilio.com/user/account/phone-numbers/incoming) and open a number's configuration by clicking on it.

Remember that the number where you change the voice webhooks must be the same one you set on the `TWILIO_RR_NUMBER` environment variable.

Expose the application to the wider Internet. [We recommend using ngrok to solve this problem](https://www.twilio.com/blog/2015/09/6-awesome-reasons-to-use-ngrok-when-testing-webhooks.html).

```bash
ngrok http 4567 -host-header="localhost:4567"
```

Once you have started ngrok, update your Twilio's number voice URL setting to use your ngrok hostname, so it will look something like this:

```
http://<your-ngrok-subdomain>.ngrok.io/conference
```

   ![Configure Voice](number.png)

### Tests

You can run the tests locally by typing:

```bash
./gradlew check
```

### Configure Twilio

You will need to configure Twilio to send requests to your application when SMS are received.

You will need to provision at least one Twilio number with sms capabilities so the application's users can make property reservations. You can buy a number [here](https://www.twilio.com/user/account/phone-numbers/search). Once you have a number,  you need to configure it to work with your application. Open [the number management page](https://www.twilio.com/user/account/phone-numbers/incoming) and open a number's configuration by clicking on it.

To let our Twilio Phone number use the callback endpoint we exposed our development server will need to be publicly accessible. [We recommend using ngrok to solve this problem](https://www.twilio.com/blog/2015/09/6-awesome-reasons-to-use-ngrok-when-testing-webhooks.html).

Remember that the number you change the _SMS webhook_ for must be the same one you set on the `TwilioPhoneNumber` setting.

   ![Configure Messaging](webhook.png)

To start using `ngrok` in our project you'll have to execute the following line in the _command prompt_.

```
ngrok http 8080 -host-header="localhost:8080"
```

Keep in mind that our endpoint is:

```
http://<your-ngrok-subdomain>.ngrok.io/reservation-confirmation
```

### Cloud deployment

Additionally to trying out this application locally, you can deploy it to a variety of host services. Here is a small selection of them.

Please be aware that some of these might charge you for the usage or might make the source code for this application visible to the public. When in doubt research the respective hosting service first.

| Service                           |                                                                                                                                                                                                                           |
| :-------------------------------- | :------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| [Heroku](https://www.heroku.com/) | [![Deploy](https://www.herokucdn.com/deploy/button.svg)](https://heroku.com/deploy?template=https://github.com/TwilioDevEd/conference-broadcast-spark/tree/master)                                                                                                                                       |

**Some notes:** 
- For Heroku, please [check this](https://devcenter.heroku.com/articles/deploying-gradle-apps-on-heroku) to properly configure the project for deployment.
- You can also follow [this guide](https://vaadin.com/blog/how-to-deploy-your-java-app-to-the-cloud) to deploy the application to several other cloud services including Google Cloud, Oracle Cloud, etc.

## Resources

- The CodeExchange repository can be found [here](https://github.com/twilio-labs/code-exchange/).

## Contributing

This template is open source and welcomes contributions. All contributions are subject to our [Code of Conduct](https://github.com/twilio-labs/.github/blob/master/CODE_OF_CONDUCT.md).

## License

[MIT](http://www.opensource.org/licenses/mit-license.html)

## Disclaimer

No warranty expressed or implied. Software is as is.

[twilio]: https://www.twilio.com
