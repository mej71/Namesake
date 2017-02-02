package application;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class ConfirmChangesController {

	@FXML
	private Button confirmButton;
	
	@FXML
	private Button cancelButton;
	
	
	public void initialize(){
		
	}
	
	public void setConfirmed(){
		StaticValues.setConfirmed();
		Stage stage = (Stage) confirmButton.getScene().getWindow();
	    stage.close();
	}
	
	public void setCancel(){
		StaticValues.setCancel();
		Stage stage = (Stage) cancelButton.getScene().getWindow();
	    stage.close();
	}
	
	
}
