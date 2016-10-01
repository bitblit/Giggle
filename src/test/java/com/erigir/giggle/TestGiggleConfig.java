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

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created by cweiss1271 on 9/20/16.
 */
public class TestGiggleConfig extends Application{
    private static final Logger LOG = LoggerFactory.getLogger(TestGiggleConfig.class);

    private Giggle giggle;
    private GiggleNative giggleNative;
    private Label resultLabel = new Label("GIGGLE RESPONSE HERE");
    private Label nativeResultLabel = new Label("NATIVE GIGGLE RESPONSE HERE");

    public static void main(String[] args) {
        try
        {
            if (args.length!=3)
            {
                System.out.println("GiggleConfigTester {appId} {appSecret} {appReturnUrl}");
            }
            else
            {
                launch(args);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        VBox pane = new VBox();

        Application.Parameters params = getParameters();
        String appId = params.getRaw().get(0);
        String appSecret = params.getRaw().get(1);
        String redirUrl = params.getRaw().get(2);
        Label label = new Label("Using app id : "+appId);
        pane.getChildren().add(label);

        GoogleExchanger exchanger = new GoogleExchanger.GoogleExchangerBuilder().withClientId(appId).withClientSecret(appSecret)
                .withRedirectUri(redirUrl).withFetchType(GoogleFetchType.PROFILE).build();
        // Setup giggle
        giggle = new Giggle(exchanger);//,appSecret,redirUrl);

        giggleNative = new GiggleNative(exchanger);

        Button login = new Button("Login with Google");
        login.setOnAction((e)->{
            LOG.info("Starting login");
            Future<GiggleResponse> futureResponse = giggle.startLogin();
            //giggle.start(primaryStage);

            new Thread(()->{
                try
                {
                    GiggleResponse response = futureResponse.get(5, TimeUnit.MINUTES);

                    LOG.info("Got response: {}",response);
                    Platform.runLater(()->
                    {
                        resultLabel.setText(response.toString());
                    });
                }
                catch (Exception e2)
                {
                    LOG.info("Failed to get response",e2);
                }
            }).start();

        });



        Button loginNative = new Button("Login with Google Native Browser");
        loginNative.setOnAction((e)->{
            Future<GiggleResponse> futureNativeResponse = giggleNative.startLogin();
            //giggle.start(primaryStage);

            new Thread(()->{
                try
                {
                    GiggleResponse response = futureNativeResponse.get(5, TimeUnit.MINUTES);

                    LOG.info("Got response: {}",response);
                    Platform.runLater(()->
                    {
                        nativeResultLabel.setText(response.toString());
                    });
                }
                catch (Exception e2)
                {
                    LOG.info("Failed to get response",e2);
                }
            }).start();

        });

        pane.getChildren().add(login);
        pane.getChildren().add(resultLabel);
        pane.getChildren().add(loginNative);
        pane.getChildren().add(nativeResultLabel);


        Scene scene = new Scene(pane, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Giggle Config Tester");
        primaryStage.setOnCloseRequest(e -> {LOG.info("Handling close request");
            Platform.exit();System.exit(0);});
        primaryStage.show();

    }

}
