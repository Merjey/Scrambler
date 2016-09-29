package scrambler;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import scrambler.gui.Controller;

public class MainClass extends Application {
	private Stage primaryStage;
    private AnchorPane rootLayout;
    private Controller controller;

	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Scrambler");
		try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainClass.class.getResource("gui/Scrambler.fxml"));
            rootLayout = (AnchorPane) loader.load();
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            controller = loader.getController();
            controller.setMainClass(this);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	public Stage getPrimaryStage() {
		return this.primaryStage;
	}

	public static void main(String[] args) {
		launch(args);
	}
}
