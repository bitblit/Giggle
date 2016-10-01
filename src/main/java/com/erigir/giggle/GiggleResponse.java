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
    private Map<String,Object> userProfile;

    public Map<String, String> getOtherData() {
        return otherData;
    }

    public String getOauthToken() {
        return oauthToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public int getExpiresIn() {
        return expiresIn;
    }

    public String getIdToken() {
        return idToken;
    }

    public Map<String, Object> getUserProfile() {
        return userProfile;
    }

    /** Package level to make this immutable outside the package **/
    GiggleResponse withOauthToken(final String oauthToken) {
        this.oauthToken = oauthToken;
        return this;
    }

    GiggleResponse withAccessToken(final String accessToken) {
        this.accessToken = accessToken;
        return this;
    }

    GiggleResponse withTokenType(final String tokenType) {
        this.tokenType = tokenType;
        return this;
    }

    GiggleResponse withExpiresIn(final int expiresIn) {
        this.expiresIn = expiresIn;
        return this;
    }

    GiggleResponse withIdToken(final String idToken) {
        this.idToken = idToken;
        return this;
    }

    GiggleResponse withOtherData(final Map<String, String> otherData) {
        this.otherData = otherData;
        return this;
    }

    GiggleResponse withUserProfile(final Map<String, Object> userProfile) {
        this.userProfile = userProfile;
        return this;
    }

    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        String json = "";
        try
        {
            // TODO: Improve this - this aint very smart
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
