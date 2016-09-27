package com.erigir.giggle;

import java.util.Map;

/**
 * Created by cweiss1271 on 9/20/16.
 */
public interface InformationReceiver {
    void receiveGoogleInformation(String token, Map<String,String> otherData);
}
