package com.github.merjey.scrambler;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * This program is designed to encrypt and then decrypt the small (up to 50MB) files.
 * @version 2.0 2016-10-03
 * @author Evgeniy Merjey
 */
public class MainClass extends Application {
	private Stage primaryStage;
    private AnchorPane rootLayout;
   
	@Override
	public void start(Stage pS) {
		primaryStage = pS;
        primaryStage.setTitle("Scrambler");
        primaryStage.setResizable(false);
		try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainClass.class.getResource("gui/Scrambler.fxml"));
            rootLayout = (AnchorPane) loader.load();
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
