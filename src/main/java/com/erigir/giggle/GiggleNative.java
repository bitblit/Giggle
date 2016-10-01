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

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Requests a token from Google using the native browser - This has the disadvantage that
 * it requires opening a port, and leaves a browser window running.  It has the advantage that
 * the browser has cookies and is probably already logged into Google, making the login process
 * much more streamlined for the user.  Also, doesn't need to run its own window.
 *
 * 1) Start a http listener on localhost
 * 2) Pop open a system browser to google with that port as the redirect target
 *
 * Working from : https://developers.google.com/identity/protocols/OAuth2InstalledApp (2016-09-20)
 *
 *
 * Created by cweiss1271 on 9/20/16.
 */
public class GiggleNative {
    private static final Logger LOG = LoggerFactory.getLogger(GiggleNative.class);

    private GoogleExchanger exchanger;

    public GiggleNative(GoogleExchanger exchanger){
        super();
        this.exchanger = Objects.requireNonNull(exchanger);
    }

    public Future<GiggleResponse>  startLogin(){
        try {
            LOG.info("Starting listener for response");
            Future<GiggleResponse> response = Executors.newSingleThreadExecutor().submit(new ListenForGiggleResponse());

            URI uri = exchanger.buildGoogleUri();
            Desktop.getDesktop().browse(uri);

            return response;
        }
        catch (IOException ioe)
        {
            throw new RuntimeException(ioe);
        }
    }

    class ListenForGiggleResponse implements Callable<GiggleResponse>
    {
        @Override
        public GiggleResponse call() throws Exception {
            LOG.info("About to open port");
            ServerSocket serverSocket = new ServerSocket(exchanger.getReturnPort());
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

            return exchanger.buildResponseFromLocation(allData.toString());
     }
    }
}
