package com.erigir.giggle;

/**
 * Created by cweiss1271 on 10/1/16.
 */
public enum GoogleFetchType {
    CODE_ONLY("Fetch only the code"),
    ACCESS_TOKEN("Fetch access token");

    String label;

    GoogleFetchType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
