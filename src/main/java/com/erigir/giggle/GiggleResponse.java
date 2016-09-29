package com.erigir.giggle;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

/**
 * Created by cweiss1271 on 9/27/16.
 */
public class GiggleResponse {
    private String oauthToken;
    private String accessToken;
    private String tokenType;
    private int expiresIn;
    private String idToken;


    private Map<String,String> otherData;

    public Map<String, String> getOtherData() {
        return otherData;
    }

    public void setOtherData(Map<String, String> otherData) {
        this.otherData = otherData;
    }

    public GiggleResponse withOauthToken(final String oauthToken) {
        this.oauthToken = oauthToken;
        return this;
    }

    public GiggleResponse withAccessToken(final String accessToken) {
        this.accessToken = accessToken;
        return this;
    }

    public GiggleResponse withTokenType(final String tokenType) {
        this.tokenType = tokenType;
        return this;
    }

    public GiggleResponse withExpiresIn(final int expiresIn) {
        this.expiresIn = expiresIn;
        return this;
    }

    public GiggleResponse withIdToken(final String idToken) {
        this.idToken = idToken;
        return this;
    }

    public GiggleResponse withOtherData(final Map<String, String> otherData) {
        this.otherData = otherData;
        return this;
    }

    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        String json = "";
        try
        {
            json = new ObjectMapper().writeValueAsString(this);
        }
        catch (Exception e)
        {
            json = e.getMessage();
        }

        sb.append("GiggleResponse[").append(json).append("']");
        return sb.toString();
    }

}
