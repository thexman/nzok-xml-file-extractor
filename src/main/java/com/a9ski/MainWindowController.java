package com.a9ski;


import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class MainWindowController {
	
	private Stage stage;
	
	@FXML 
	private TextField xmlFileNameText;
	
	@FXML
	private Button browseButton;
	
	@FXML
	private Button extractButton;
	
	@FXML
	private Button openButton;
	
	@FXML
	private TextField extractedFileNameText;
	
	private File xmlFile;
	private File extractedFile;

	private App application;
	
	@FXML 
	protected void onBrowseClicked(ActionEvent event) {
		final FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open XML File");
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml"));
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("All files (*.*)", "*.*"));
		xmlFile = fileChooser.showOpenDialog(stage);
		xmlFileNameText.setText(getFileName(xmlFile));
		extractButton.setDisable(false);
    }
	

	@FXML 
	protected void onExtractClicked(ActionEvent event) {
		final XmlToFileExtractor extractor = new XmlToFileExtractor();
		try {
			extractedFile = extractor.exportFile(xmlFile);
			extractedFileNameText.setText(getFileName(extractedFile));
			openButton.setDisable(false);
		} catch (final IOException | SAXException | ParserConfigurationException ex) {
			showErrorDialog(ex);

		}
    }


	private void showErrorDialog(final Exception ex) {
		final Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Error Dialog");
		alert.setHeaderText("Error extracting file");
		alert.setContentText(ex.getMessage());
		
		// Create expandable Exception.
		final StringWriter sw = new StringWriter();
		final PrintWriter pw = new PrintWriter(sw);
		ex.printStackTrace(pw);
		final String exceptionText = sw.toString();

		final Label label = new Label("Exceotion stacktrace:");

		final TextArea textArea = new TextArea(exceptionText);
		textArea.setEditable(false);
		textArea.setWrapText(true);

		textArea.setMaxWidth(Double.MAX_VALUE);
		textArea.setMaxHeight(Double.MAX_VALUE);
		GridPane.setVgrow(textArea, Priority.ALWAYS);
		GridPane.setHgrow(textArea, Priority.ALWAYS);

		final GridPane expContent = new GridPane();
		expContent.setMaxWidth(Double.MAX_VALUE);
		expContent.add(label, 0, 0);
		expContent.add(textArea, 0, 1);

		// Set expandable Exception into the dialog pane.
		alert.getDialogPane().setExpandableContent(expContent);

		alert.showAndWait();
	}
	
	@FXML 
	protected void onOpenClicked(ActionEvent event) {
		application.getHostServices().showDocument(extractedFile.toURI().toString());
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}
	
	public void setApplication(App app) {
		this.application = app;
	}
	
	private String getFileName(File f) {
		try {
			return f.getCanonicalPath();
		} catch (final IOException ex) {
			return f.getAbsolutePath();
		}
	}
}
