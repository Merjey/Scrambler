package com.github.merjey.scrambler.gui;

import static com.github.merjey.scrambler.core.Core.*;

import java.io.File;
import java.util.Locale;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;

public class Controller {
	@FXML
	private Button encrypt;
	@FXML
	private Button decrypt;
	@FXML
	private AnchorPane rootLayout;
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
        File file = fileChooser.showOpenDialog(rootLayout.getScene().getWindow());
        if (file.length()>54000000){
        	Alert alert;
        	String s = "Are supported only files up to 50 MB";
			alert = new Alert(AlertType.WARNING);
			alert.initOwner(rootLayout.getScene().getWindow());
			alert.setTitle("Scrambler");
			alert.setHeaderText("Too big file");
			alert.setContentText(s);
			alert.showAndWait();
        	return null;
		}
        return file;
	}
	
	private void deactivateButtons() {
		startTime=System.nanoTime()/1e9;
		encrypt.setDisable(true);
		decrypt.setDisable(true);
	}
	
	/**
	  * @param operation Name of operation: "Encryption" or "Decryption".
	  * @param success True if the operation completed successfully. False otherwise.
	  */
	private void reactivateButtons(String operation, boolean success) {
		ellapsedTime=System.nanoTime()/1e9-startTime;
		showSummary(operation, success);
		encrypt.setDisable(false);
		decrypt.setDisable(false);
	}
	
	/**
	  * @param operation Name of operation: "Encryption" or "Decryption".
	  * @param success True if the operation completed successfully. False otherwise.
	  */
	private void showSummary(String operation, boolean success) {
		Alert alert;
		if (success) {
			String s = String.format(Locale.ENGLISH, "%s was successfully completed in %.2f seconds.", operation, ellapsedTime);
			alert = new Alert(AlertType.INFORMATION);
			alert.initOwner(rootLayout.getScene().getWindow());
			alert.setTitle("Scrambler");
			alert.setHeaderText("Success");
			alert.setContentText(s);
			alert.showAndWait();
		} else {
			String s = String.format("%s was not completed due to an error. Perhaps you try to "+
					"decipher an unencrypted file or do not have access to the file.", operation);
			alert = new Alert(AlertType.ERROR);
			alert.initOwner(rootLayout.getScene().getWindow());
			alert.setTitle("Scrambler");
			alert.setHeaderText("Error");
			alert.setContentText(s);
			alert.showAndWait();
		}
	}
	
}
