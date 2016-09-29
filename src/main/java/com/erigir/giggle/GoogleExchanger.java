package com.erigir.giggle;

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

/**
 * 1) Start a http listener on localhost
 * 2) Pop open a system browser to google with that port as the redirect target
 *
 * Working from : https://developers.google.com/identity/protocols/OAuth2InstalledApp (2016-09-20)
 *
 *
 * Created by cweiss1271 on 9/20/16.
 */
public class GoogleExchanger{
    private static final Logger LOG = LoggerFactory.getLogger(GoogleExchanger.class);

    private String clientId;
    private String clientSecret;
    private String redirectUri;
    private int returnPort = 65100; // Default Giggle port


    public GoogleExchanger() {
        super();
        // This version is used by other JavaFX clients
        //this.clientId = Objects.requireNonNull(clientId);
        //this.clientSecret = Objects.requireNonNull(clientSecret);
        //this.redirectUri = Objects.requireNonNull(redirectUri);
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

    final String exchangeCodeForTokens(String code)
    {
        try {
            String url = "https://www.googleapis.com/oauth2/v4/token";
            URL obj = new URL(url);
            HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

            //add request header
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            // Send post request
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(buildGoogleTokenExchangeBody(code));
            wr.flush();
            wr.close();

            int responseCode = con.getResponseCode();

            InputStream src = (responseCode==200)?con.getInputStream():con.getErrorStream();
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(src));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();


            //print result
            return response.toString();
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    private String buildGoogleTokenExchangeBody(String code)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("code=").append(Objects.requireNonNull(code))
                .append("&client_id=").append(Objects.requireNonNull(clientId))
                .append("&client_secret=").append(Objects.requireNonNull(clientSecret))
                .append("&redirect_uri=").append(Objects.requireNonNull(redirectUri))
                .append("&grant_type=authorization_code");
        return sb.toString();
    }

    protected final URI buildGoogleUri(String securityToken)
    {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("https://accounts.google.com/o/oauth2/v2/auth?")
                    .append("client_id=").append(clientId)
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

    protected final String buildSecurityToken()
    {
        // Create a state token to prevent request forgery.
        // Store it in the session for later validation.
        String state = new BigInteger(130, new SecureRandom()).toString(32);
        return state;
    }

    public GoogleExchanger withClientId(final String clientId) {
        this.clientId = Objects.requireNonNull(clientId);
        return this;
    }

    public GoogleExchanger withClientSecret(final String clientSecret) {
        this.clientSecret = Objects.requireNonNull(clientSecret);
        return this;
    }

    public GoogleExchanger withRedirectUri(final String redirectUri) {
        this.redirectUri = Objects.requireNonNull(redirectUri);
        return this;
    }

    public GoogleExchanger withReturnPort(final int returnPort) {
        this.returnPort = returnPort;
        return this;
    }


}
