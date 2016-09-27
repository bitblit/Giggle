package com.erigir.giggle;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.security.SecureRandom;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.concurrent.*;

/**
 * 1) Start a http listener on localhost
 * 2) Pop open a system browser to google with that port as the redirect target
 *
 * Working from : https://developers.google.com/identity/protocols/OAuth2InstalledApp (2016-09-20)
 *
 *
 * Created by cweiss1271 on 9/20/16.
 */
public class Giggle extends Application{
    private static final Logger LOG = LoggerFactory.getLogger(Giggle.class);

    private String applicationId;
    private InformationReceiver informationRecipient;
    private int returnPort = 65100; // Default Giggle port
    private String postLoginUrl="https://www.google.com";
    private Stage parentPrimaryStage;

    public Giggle(String applicationId, InformationReceiver informationRecipient,Stage parentPrimaryStage) {
        this.applicationId = Objects.requireNonNull(applicationId);
        this.informationRecipient = Objects.requireNonNull(informationRecipient);
        this.parentPrimaryStage = parentPrimaryStage;

    }

    @Override
    public void start(Stage primaryStage) throws Exception {

    }

    public void processGoogleLogin()
    {
        try {
            Future<String> googleResponse = Executors.newSingleThreadExecutor().submit(new ListenForGoogleResponse());
            String securityToken = buildSecurityToken();
            URI uri = buildGoogleUri(securityToken);

            //Desktop.getDesktop().browse(uri);
            GiggleLoginWindow loginWindow = new GiggleLoginWindow(uri);
            Platform.runLater(()->
            {
                loginWindow.start(parentPrimaryStage);
           });

            //WebViewSample wvs = new WebViewSample();
            //wvs.start(primaryStage);


            //Stage stage = new Stage(StageStyle.UTILITY);
            //WebView wv2 = new WebView();
            //stage.setScene(new Scene(wv2));
            //stage.show();
            //wv2.getEngine().load("https://www.google.com");

            //GiggleLoginWindow

            String value = googleResponse.get(5, TimeUnit.MINUTES);
            Map<String,String> params = extractParametersFromReturnUrl(value);
            if (securityToken.equals(params.get("state")))
            {
                informationRecipient.receiveGoogleInformation(params.get("code"), new TreeMap<String, String>());
            }
            else
            {
                throw new RuntimeException("Couldn't process result");
            }
        }
        catch (InterruptedException | ExecutionException | TimeoutException ioe) //| IOException
        {
            throw new RuntimeException("IOE",ioe);
        }
    }

    Map<String,String> extractParametersFromReturnUrl(String returnUrl)
    {
        Map<String,String> rval = new TreeMap<>();
        if (returnUrl!=null)
        {
            String[] lines = returnUrl.split("\n");
            if (lines.length>0)
            {
                String first = lines[0];
                int start = first.indexOf('?');
                if (start!=-1)
                {
                    String[] queryPairs = first.substring(start+1).split("&");
                    for (String s:queryPairs)
                    {
                        String[] piece = s.split("=");
                        rval.put(piece[0],piece[1]);
                    }
                }
            }
        }

        return rval;
    }

    private String exchangeCodeForTokens(String code)
    {
        /*POST /oauth2/v4/token HTTP/1.1
Host: www.googleapis.com
Content-Type: application/x-www-form-urlencoded

code=4/v6xr77ewYqhvHSyW6UJ1w7jKwAzu&
client_id=8819981768.apps.googleusercontent.com&
client_secret=your_client_secret&
redirect_uri=https://oauth2.example.com/code&
grant_type=authorization_code
*/
        return null;


    }

    private URI buildGoogleTokenExchangeUri(String code, String clientId, String clientSecret)
    {
        return null;
    }

    private URI buildGoogleUri(String securityToken)
    {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("https://accounts.google.com/o/oauth2/v2/auth?")
                    .append("client_id=").append(applicationId)
                    .append("&response_type=code&scope=openid%20email&redirect_uri=http://localhost:")
                    .append(returnPort).append("&state=")
                    .append(securityToken);
            return new URI(sb.toString());
        }
        catch (URISyntaxException use)
        {
            throw new RuntimeException("Cant happen",use);
        }
    }

    private String buildSecurityToken()
    {
        // Create a state token to prevent request forgery.
        // Store it in the session for later validation.
        String state = new BigInteger(130, new SecureRandom()).toString(32);
        return state;
        /*
        request.session().attribute("state", state);
        // Read index.html into memory, and set the client ID,
        // token state, and application name in the HTML before serving it.
        return new Scanner(new File("index.html"), "UTF-8")
                .useDelimiter("\\A").next()
                .replaceAll("[{]{2}\\s*CLIENT_ID\\s*[}]{2}", CLIENT_ID)
                .replaceAll("[{]{2}\\s*STATE\\s*[}]{2}", state)
                .replaceAll("[{]{2}\\s*APPLICATION_NAME\\s*[}]{2}",
                        APPLICATION_NAME);

        return */
    }

    class ListenForGoogleResponse implements Callable<String>
    {
        @Override
        public String call() throws Exception {
            LOG.info("About to open port");
            ServerSocket serverSocket = new ServerSocket(returnPort);
            LOG.info("About to open port");
            Socket clientSocket = serverSocket.accept();
            LOG.info("About to open port");
            PrintWriter out =
                    new PrintWriter(clientSocket.getOutputStream(), true);
            LOG.info("About to open port");
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));

            // Block here until we get a connection
            LOG.info("About to open port");
            StringBuilder allData = new StringBuilder();
            String toRead = in.readLine();
            while (toRead!=null && toRead.length()>0)
            {
                LOG.info("Read : {} {}",toRead, toRead.length());
                allData.append(toRead).append("\n");
                toRead = in.readLine();
            }

            LOG.info("Finished reading");

            //out.println("HTTP/1.1 301 Moved Permanently");
            //out.println("Location: "+postLoginUrl);
            out.println("HTTP/1.1 200 OK");
            out.println("Content-Type: text/html");
            out.println("");
            out.println("<html><body><h1>You are logged in, return to the application</h1></body></html>");
            return allData.toString();
     }
    }
}
