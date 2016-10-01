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
 * Class that wraps up all interaction with Google OpenID
 *
 * Created by cweiss1271 on 9/20/16.
 */
public class GoogleExchanger{
    private static final Logger LOG = LoggerFactory.getLogger(GoogleExchanger.class);

    private String clientId;
    private String clientSecret;
    private String redirectUri;
    private GoogleFetchType fetchType;
    private int returnPort = 65100; // Default Giggle port

    // Create a state token to prevent request forgery.
    // Store it in the session for later validation.
    private final String securityState = new BigInteger(130, new SecureRandom()).toString(32);

    private GoogleExchanger() {
        super();
    }

    public int getReturnPort() {
        return returnPort;
    }

    public GiggleResponse buildResponseFromLocation(String location)
    {
        GiggleResponse rval = null;
        Map<String,String> params = GoogleExchanger.extractParametersFromReturnUrl(location);
        if (!securityState.equals(params.get("state")))
        {
            throw new RuntimeException("Couldn't process result");
        }

        rval = new GiggleResponse().withOtherData(params).withOauthToken(params.remove("code"));

        if (fetchType==GoogleFetchType.ACCESS_TOKEN)
        {
            String dataString = exchangeCodeForTokens(params.get("code"));
            try {
                Map<String, String> data = new ObjectMapper().readValue(dataString, Map.class);

                rval = rval.withAccessToken(data.get("access_token")).withExpiresIn(Integer.parseInt(String.valueOf(data.get("expires_in"))))
                        .withIdToken(data.get("id_token")).withTokenType(data.get("token_type"));
            }
            catch (IOException ioe)
            {
                throw new RuntimeException(ioe);
            }
        }

        return rval;
    }

    public static final Map<String,String> extractParametersFromReturnUrl(String returnUrl)
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

    protected final URI buildGoogleUri()
    {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("https://accounts.google.com/o/oauth2/v2/auth?")
                    .append("client_id=").append(clientId)
                    .append("&response_type=code&scope=openid%20email&redirect_uri=http://localhost:")
                    .append(returnPort).append("&state=")
                    .append(securityState);
            return new URI(sb.toString());
        }
        catch (URISyntaxException use)
        {
            throw new RuntimeException("Cant happen",use);
        }
    }

    public static class GoogleExchangerBuilder
    {
        private String clientId;
        private String clientSecret;
        private String redirectUri;
        private GoogleFetchType fetchType;
        private int returnPort = 65100; // Default Giggle port

        public GoogleExchanger build()
        {
            Objects.requireNonNull(clientId, "Client Id is required");
            Objects.requireNonNull(fetchType, "Fetch type is required");
            if (fetchType==GoogleFetchType.ACCESS_TOKEN)
            {
                Objects.requireNonNull(clientSecret, "Client Secret is required if fetch type is ACCESS_TOKEN");
                Objects.requireNonNull(redirectUri, "Redirect URI is required if fetch type is ACCESS_TOKEN");
            }
            GoogleExchanger rval = new GoogleExchanger();
            rval.clientId = clientId;
            rval.clientSecret = clientSecret;
            rval.redirectUri = redirectUri;
            rval.fetchType = fetchType;
            rval.returnPort = returnPort;
            return rval;
        }

        public GoogleExchangerBuilder withClientId(final String clientId) {
            this.clientId = clientId;
            return this;
        }

        public GoogleExchangerBuilder withClientSecret(final String clientSecret) {
            this.clientSecret = clientSecret;
            return this;
        }

        public GoogleExchangerBuilder withRedirectUri(final String redirectUri) {
            this.redirectUri = redirectUri;
            return this;
        }

        public GoogleExchangerBuilder withFetchType(final GoogleFetchType fetchType) {
            this.fetchType = fetchType;
            return this;
        }

        public GoogleExchangerBuilder withReturnPort(final int returnPort) {
            this.returnPort = returnPort;
            return this;
        }


    }

}
