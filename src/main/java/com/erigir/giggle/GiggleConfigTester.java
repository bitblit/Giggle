package com.erigir.giggle;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.Map;

/**
 * Created by cweiss1271 on 9/20/16.
 */
public class GiggleConfigTester extends Application{
    private static final Logger LOG = LoggerFactory.getLogger(GiggleConfigTester.class);

    private Giggle giggle;

    public static void main(String[] args) {
        launch(args);
        /*
        try
        {
            if (args.length!=1)
            {
                System.out.println("GiggleConfigTester {appId}");
            }
            else
            {
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        */
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        VBox pane = new VBox();

        Application.Parameters params = getParameters();
        String appId = params.getRaw().get(0);
        Label label = new Label("Using app id : "+appId);
        pane.getChildren().add(label);
        // Setup giggle
        giggle = new Giggle(appId,new TestInformationReceiver(), primaryStage);


        Button login = new Button("Login with Google");
        login.setOnAction((e)->{
            LOG.info("Starting login");
            giggle.processGoogleLogin();
            //GiggleLoginWindow wsv = new GiggleLoginWindow(new URI("https://www.yahoo.com"))
                    ;
            //wsv.start(primaryStage);
        });

        pane.getChildren().add(login);

        Scene scene = new Scene(pane, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Giggle Config Tester");
        primaryStage.setOnCloseRequest(e -> {LOG.info("Handling close request");
            Platform.exit();System.exit(0);});
        primaryStage.show();

    }

    static class TestInformationReceiver implements InformationReceiver{
        @Override
        public void receiveGoogleInformation(String token, Map<String, String> otherData) {
            System.out.println(String.format("Got token : %s, otherData: %s", token, otherData));
            System.exit(0);
        }
    }
}
