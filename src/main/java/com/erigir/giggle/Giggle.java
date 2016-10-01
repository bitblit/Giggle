package com.erigir.giggle;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.RuntimeJsonMappingException;
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
import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.security.SecureRandom;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 1) Starts a JavaFX Webview to run the goole login
 * 2) Detect when Google redirects the local browser back to the endpoint and intercept
 *
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
    private Scene scene;
    final WebView browser = new WebView();
    final WebEngine webEngine = browser.getEngine();

    private GiggleResponse gr = null;
    private final AtomicBoolean signal = new AtomicBoolean(false);


    public Giggle(GoogleExchanger exchanger){
        super();
        this.exchanger = Objects.requireNonNull(exchanger);
    }

    public Future<GiggleResponse>  startLogin(){
        LOG.info("Starting listener for response");
        //String securityToken = exchanger.buildSecurityToken();
        Future<GiggleResponse> response = Executors.newSingleThreadExecutor().submit(new ListenForGiggleResponse());
        // create the scene
        setTitle("Web View");

        URI uri = exchanger.buildGoogleUri();

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
                    String location = webEngine.getLocation();
                    LOG.debug("Change {}  to {} : {}",oldState,newState,webEngine.getLocation());
                    if (location.startsWith("http://localhost:65100"))
                    {
                        gr = exchanger.buildResponseFromLocation(location);
                        LOG.info("Response is {}",gr);
                        synchronized (signal)
                        {
                            // Close the window!
                            //Platform.runLater(()->hide());
                            hide();

                            signal.set(true);
                            signal.notify();
                        }
                   }
                });

        scene = new Scene(browser,750,500, javafx.scene.paint.Color.web("#666970"));
        setScene(scene);
        //scene.getStylesheets().add("webviewsample/BrowserToolbar.css");
        show();
        return response;
    }

    class ListenForGiggleResponse implements Callable<GiggleResponse> {

        @Override
        public GiggleResponse call() throws Exception {

            while (!signal.get())
            {
                synchronized (signal)
                {
                    signal.wait();
                }
            }

            return gr;
        }
    }

}
