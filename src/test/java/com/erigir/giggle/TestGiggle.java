package com.erigir.giggle;

import org.junit.Ignore;
import org.junit.Test;


import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created by cweiss1271 on 3/9/16.
 */
public class TestGiggle {

    /*private Giggle giggle = new Giggle("asdf","asdf","asdf");
    private String sampleResponse = "GET /?state=nnjikn26o2dg2515ivd82jpv8m&code=4/NhbBsCXwl9zS4vJMXlp-0LHServCpeNDJU7xaSGHz_I&" +
            "authuser=0&session_state=55211f1adadbe2c8be659857f1f196b4ef5851df..8f50&prompt=none HTTP/1.1\n" +
            "Host: localhost:65100\n" +
            "Connection: keep-alive\n" +
            "Upgrade-Insecure-Requests: 1\n" +
            "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/52.0.2743.116 Safari/537.36\n" +
            "X-Client-Data: CIW2yQEIo7bJAQjBtskBCPOcygE=\n" +
            "X-Chrome-Connected: id=118067220992401648123,mode=0,enable_account_consistency=false\n" +*/
            //"Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8\n" +
          /*  "Accept-Encoding: gzip, deflate, sdch\n" +
            "Accept-Language: en-US,en;q=0.8\n" +
            "Cookie: __ngDebug=true; gsScrollPos=";

    @Test
    @Ignore
    public void testParseReturnUrlToParams()
    {
        Map<String,String> params = giggle.extractParametersFromReturnUrl(sampleResponse);
        assertEquals("nnjikn26o2dg2515ivd82jpv8m",params.get("state"));
        assertEquals("4/NhbBsCXwl9zS4vJMXlp-0LHServCpeNDJU7xaSGHz_I",params.get("code"));
        assertEquals("55211f1adadbe2c8be659857f1f196b4ef5851df..8f50",params.get("session_state"));
    }*/

    /*
    @Test
    public void testSimpleQuery() {
        SimpleHttpUtils.HttpTx tx = SimpleHttpUtils.quietFetchUrlDetails("https://www.google.com", 5000, 3);
        assertTrue(tx.getStatus() < 300);
    }

    @Test
    @Ignore
    public void testSimpleQuery2() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        SimpleStreamHttpUtils.StreamHttpTx tx = SimpleStreamHttpUtils.http(
                new SimpleStreamHttpUtils.SimpleStreamHttpRequest()
                .withUrl("https://www.google.com")
                .withConnectTimeout(5000)
                .withTries(3)
                .withDestination(baos)
        );

        String body = new String(baos.toByteArray());

        assertTrue(tx.getStatus() < 300);
    }

    @Test
    @Ignore
    public void testSimplePost2() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        //ByteArrayInputStream input = new ByteArrayInputStream("THIS IS A TEST".getBytes());
        SimpleStreamHttpUtils.StreamHttpTx tx = SimpleStreamHttpUtils.http(
                new SimpleStreamHttpUtils.SimpleStreamHttpRequest()
                        .withUrl("http://httpbin.org/post")
                        .withMethod("POST")
                        .withConnectTimeout(5000)
                        .withTries(3)
                        .withDestination(baos)
                .withSource("TEST2".getBytes())
                //.withSource(input)
        );

        String body = new String(baos.toByteArray());

        assertTrue(tx.getStatus() < 300);
    }

    @Test
    @Ignore
    public void testSimplePost() {
        byte[] postData = "This is a test".getBytes();
        Map<String, String> headers = new TreeMap<>();
        headers.put("Content-Type", "text/plain");


        String url = "https://test.server.com/v1/info/server";

        AllowSelfSignedHttps.allowSelfSignedHttpsCertificates();
        SimpleHttpUtils.HttpTx tx = SimpleHttpUtils.postDataToURL(url, headers, postData);
        assertTrue(tx.getStatus() < 300);
    }

    */

}
