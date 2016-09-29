package com.erigir.giggle;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.concurrent.Worker;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.*;
import java.security.SecureRandom;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * 1) Start a http listener on localhost
 * 2) Pop open a system browser to google with that port as the redirect target
 *
 * Working from : https://developers.google.com/identity/protocols/OAuth2InstalledApp (2016-09-20)
 *
 *
 * Created by cweiss1271 on 9/20/16.
 */
public class Giggle extends Stage{
    private static final Logger LOG = LoggerFactory.getLogger(Giggle.class);

    private GoogleExchanger exchanger;

    private int returnPort = 65100; // Default Giggle port
    private String postLoginUrl="https://www.google.com";
    private Scene scene;
    final WebView browser = new WebView();
    final WebEngine webEngine = browser.getEngine();


    public Giggle(GoogleExchanger exchanger){
        super();
        this.exchanger = Objects.requireNonNull(exchanger);
    }

    public Future<GiggleResponse>  startLogin(){
        LOG.info("Starting listener for response");
        String securityToken = exchanger.buildSecurityToken();
        Future<GiggleResponse> response = Executors.newSingleThreadExecutor().submit(new ListenForGiggleResponse(this,securityToken));
        // create the scene
        setTitle("Web View");

        URI uri = exchanger.buildGoogleUri(securityToken);

        //apply the styles
        //getStyleClass().add("browser");
        // load the web page
        LOG.info("About to open browser to {}",uri);
        webEngine.load(uri.toString());
        //add the web view to the scene
        //getChildren().add(browser);

        // WebEngine updates flag when finished loading web page.
        webEngine.getLoadWorker()
                .stateProperty()
                .addListener( (ChangeListener) (obsValue, oldState, newState) -> {
                    if (newState == Worker.State.SUCCEEDED) {
                        //pageLoadedProperty.set(true);
                        LOG.info("Page loaded : {} {} {}",obsValue, oldState, newState);
                    }
                });



        scene = new Scene(browser,750,500, javafx.scene.paint.Color.web("#666970"));
        setScene(scene);
        //scene.getStylesheets().add("webviewsample/BrowserToolbar.css");
        show();
        return response;
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

    class ListenForGiggleResponse implements Callable<GiggleResponse>
    {
        private Stage parentStage;
        private String securityToken;

        public ListenForGiggleResponse(Stage parentStage,String securityToken) {
            this.parentStage = parentStage;
            this.securityToken = securityToken;
        }

        @Override
        public GiggleResponse call() throws Exception {
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

            // Close the window!
            Platform.runLater(()->parentStage.hide());

            Map<String,String> params = extractParametersFromReturnUrl(allData.toString());
            if (!securityToken.equals(params.get("state")))
            {
                throw new RuntimeException("Couldn't process result");
            }

            String dataString = exchanger.exchangeCodeForTokens(params.get("code"));
            Map<String,String> data = new ObjectMapper().readValue(dataString,Map.class);

            return new GiggleResponse().withAccessToken(data.get("access_token")).withExpiresIn(Integer.parseInt(data.get("expires_in")))
                    .withIdToken(data.get("id_token")).withOauthToken(params.remove("code"))
                    .withOtherData(params).withTokenType(data.get("token_type"));
     }
    }
}
