package scrambler.gui;

import java.io.File;
import java.util.Locale;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser;

import scrambler.MainClass;
import static scrambler.core.Core.*;

public class Controller {
	@FXML
	private Button encrypt;
	@FXML
	private Button decrypt;
	private MainClass mainClass;
	private Double startTime, ellapsedTime;
	
	public Controller() {
		
	}
	
	@FXML
	public void encryptPressed() {
		File file = openFile();
		if (file != null) {
			deactivateButtons();
			reactivateButtons("Encryption", encryptFile(file));
		}
	}
	
	@FXML
	public void decryptPressed() {
		File file = openFile();
		if (file != null) {
			deactivateButtons();
			reactivateButtons("Decryption", decryptFile(file));
		}
	}
	
	private File openFile() {
		FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(mainClass.getPrimaryStage());
        return file;
	}
	
	private void deactivateButtons() {
		startTime=System.nanoTime()/1e9;
		encrypt.setDisable(true);
		decrypt.setDisable(true);
	}
	
	private void reactivateButtons(String operation, boolean success) {
		ellapsedTime=System.nanoTime()/1e9-startTime;
		showSummary(operation, success);
		encrypt.setDisable(false);
		decrypt.setDisable(false);
	}
	
	private void showSummary(String operation, boolean success) {
		Alert alert;
		if (success) {
			String s = String.format(Locale.ENGLISH, "%s was successfully completed in %.2f seconds.", operation, ellapsedTime);
			alert = new Alert(AlertType.INFORMATION);
			alert.initOwner(mainClass.getPrimaryStage());
			alert.setTitle("Scrambler");
			alert.setHeaderText("Success");
			alert.setContentText(s);
			alert.showAndWait();
		} else {
			String s = String.format("%s was not completed due to an error.", operation);
			alert = new Alert(AlertType.ERROR);
			alert.initOwner(mainClass.getPrimaryStage());
			alert.setTitle("Scrambler");
			alert.setHeaderText("Error");
			alert.setContentText(s);
			alert.showAndWait();
		}
	}
	
	public void setMainClass(MainClass mainClass) {
		this.mainClass=mainClass;
	}

}
