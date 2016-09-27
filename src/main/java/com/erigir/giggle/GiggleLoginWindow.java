package com.erigir.giggle;

/**
 * Created by cweiss1271 on 9/25/16.
 */
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.net.URI;


public class GiggleLoginWindow extends Application {
    private Scene scene;
    private URI uri;

    public GiggleLoginWindow(URI uri) {
        this.uri = uri;
    }

    @Override public void start(Stage stage) {
        // create the scene
            stage.setTitle("Web View");
            scene = new Scene(new Browser(),750,500, Color.web("#666970"));
            stage.setScene(scene);
            //scene.getStylesheets().add("webviewsample/BrowserToolbar.css");
            stage.show();
    }

    public static void main(String[] args){
        launch(args);
    }
}
