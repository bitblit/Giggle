package com.erigir.giggle;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.concurrent.Worker;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.Objects;

/**
 * Created by cweiss1271 on 9/25/16.
 */
public class GiggleLoginWindowBak extends Application {
    private static final Logger LOG = LoggerFactory.getLogger(GiggleLoginWindow.class);

    private final WebView webPage = new WebView();
    private Scene scene;
    private final WebEngine webEngine = webPage.getEngine();
    private  URI uri;

    @Override
    public void start(Stage stage) throws Exception {

        stage.setTitle("Web View");
        scene = new Scene(new Browser(),750,500, Color.web("#666970"));
        stage.setScene(scene);
        scene.getStylesheets().add("webviewsample/BrowserToolbar.css");
        stage.show();


        /*
        //this.uri = uri;

        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(webPage);
        Scene scene = new Scene(borderPane, 300, 250);

        webEngine.load("https://www.google.com");//uri.toString());
        primaryStage.setTitle("Login...");
        primaryStage.setScene(scene);


        // WebEngine updates flag when finished loading web page.
        webEngine.getLoadWorker()
                .stateProperty()
                .addListener( (ChangeListener) (obsValue, oldState, newState) -> {
                    if (newState == Worker.State.SUCCEEDED) {
                        //pageLoadedProperty.set(true);
                        LOG.info("Page loaded : {} {} {}",obsValue, oldState, newState);
                    }
                });


        primaryStage.show();
        */


        /*
        setTitle("HTML");
        setWidth(1280);
        setHeight(720);
        Scene scene = new Scene(new Group());

        VBox root = new VBox();

        //webPage = new WebView();
        //webEngine = webPage.getEngine();

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(webPage);
        webEngine.loadContent("<b>asdf</b>");

        root.getChildren().addAll(scrollPane);
        scene.setRoot(root);

        setScene(scene);
        show();*/

    }

}
