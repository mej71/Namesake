package application;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.apache.commons.lang3.StringUtils;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;


public class MassFileEditorController {

	//FXML variables
	@FXML
	private ListView<String> filesList;
	
	@FXML
	private Button chooseDirectory; 
	
	@FXML 
	private AnchorPane mainAnchorPane;
	
	@FXML
	private TextField directoryText;
	
	@FXML
	private TextField extensionFilterField;
	
	@FXML
	private ChoiceBox<String> selectionType;
	
	@FXML
	private Label selectionLabel1;
	
	@FXML
	private Label selectionLabel2;
	
	@FXML
	private TextField replaceTextField;
	
	@FXML
	private TextField replaceWithTextField;
	
	@FXML
	private CheckBox replaceCaseCheck;
	
	@FXML
	private ChoiceBox<String> instanceReplaceSelection;
	
	@FXML
	private CheckBox addCaseCheck;
	
	@FXML
	private TextField removeTextField;
	
	@FXML
	private TextField addTextField;
	
	@FXML
	private CheckBox removeCaseCheck;
	
	@FXML
	private ChoiceBox<String> instanceRemoveSelection;
	
	@FXML
	private TextField addAfterTextField;
	
	@FXML
	private ChoiceBox<String> instanceAddSelection;
	
	@FXML
	private ChoiceBox<String> paddingType;	
	
	@FXML
	private TextField paddingZeroes;
	
	@FXML
	private ChoiceBox<String> instanceNumberSelection;
	
	@FXML
	private TextField ignoreNumbers;
	
	@FXML
	private Label ignoreLabel;
	
	@FXML
	private ChoiceBox<String> ignoreSelection;
	
	@FXML
	private Label errorLabel;
	
	@FXML
	private TextField instanceReplaceCounter;
	
	@FXML
	private TextField instanceRemoveCounter;
	
	@FXML
	private TextField instanceAddCounter;
	
	@FXML
	private TextField instanceNumCounter;
	
	@FXML
	private Label numInstancesLabel;
	
	@FXML
	private Label instancesLabel;
	
	
	private FileWriter errorFile;
	
	
	//other variables
	private String dirName = "";
	private ArrayList<String> filter = new ArrayList<String>();
	private int selectionValue = 0;
	
	private String replaceText = "";
	private String replaceWithText = "";
	private String removeString = "";
	private String addStringAfter = "";
	private String addString = ""; 
	
	private int ignoreReplaceCase = 1;
	private int ignoreRemoveCase = 1;
	private int ignoreAddAfterCase = 1;
	
	private int paddingZeroesNum = 0;
	private ArrayList<String> paddingIgnoreNums = new ArrayList<String>();
	
	private ChangedLists changedLists = new ChangedLists();
	private ArrayList<FileChanged> tempFileList = new ArrayList<FileChanged>();
	
	
	private int advancedEditing = 1;
	
	private ArrayList<String> myFiles =  new ArrayList<String>(); 
	
	public void initialize(){
		updateFileList();	
		
		//fill selection types
		selectionType.setItems(FXCollections.observableArrayList("Number Padding","Replace substring","Remove substring","Add substring"));
		if (advancedEditing==1){
			//selectionType.getItems().add("Regex");
		}
		selectionType.setTooltip(new Tooltip("Select editing command"));
		selectionType.getSelectionModel().select(0);
		updateSelectionType();
		selectionType.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
	      @Override
	      public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2) {
	    	 selectionValue = selectionType.getSelectionModel().getSelectedIndex();
	    	 updateSelectionType();
	      }
	    });
		
		instanceReplaceSelection.setItems(FXCollections.observableArrayList("First instance","Last instance","All instances","First n instances","Last n instances"));
		instanceReplaceSelection.setTooltip(new Tooltip("Which instances in the file to be replaced"));
		instanceReplaceSelection.getSelectionModel().select(0);
		instanceReplaceSelection.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
	      @Override
	      public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2) {
	    	  updateSelectionType();
	      }
	    });
		
		instanceRemoveSelection.setItems(FXCollections.observableArrayList("First instance","Last instance","All instances","First n instances","Last n instances"));
		instanceRemoveSelection.setTooltip(new Tooltip("Which instances in the file to be removed"));
		instanceRemoveSelection.getSelectionModel().select(0);
		instanceRemoveSelection.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
	      @Override
	      public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2) {
	    	  updateSelectionType();
	      }
	    });
		
		instanceAddSelection.setItems(FXCollections.observableArrayList("First instance","Last instance","All instances","First n instances","Last n instances"));
		instanceAddSelection.setTooltip(new Tooltip("Which instances in the file to be added after"));
		instanceAddSelection.getSelectionModel().select(0);
		instanceAddSelection.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
	      @Override
	      public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2) {
	    	  updateSelectionType();
	      }
	    });
		
		paddingType.setItems(FXCollections.observableArrayList("Pad left","Pad right"));
		paddingType.setTooltip(new Tooltip("Pad numbers left or right"));
		paddingType.getSelectionModel().select(0);
		
		instanceNumberSelection.setItems(FXCollections.observableArrayList("First instance","Last instance","All instances","First n instances","Last n instances"));
		instanceNumberSelection.setTooltip(new Tooltip("Which instances of the numbers to be padded?"));
		instanceNumberSelection.getSelectionModel().select(0);
		instanceNumberSelection.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
	      @Override
	      public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2) {
	    	  updateSelectionType();
	      }
	    });
		
		ignoreSelection.setItems(FXCollections.observableArrayList("Ignore strict","Ignore loose"));
		ignoreSelection.setTooltip(new Tooltip("Ignore numbers strict or loose.\nFor instance, 3 with ignore strict would not affect 303, but ignore loose would."));
		ignoreSelection.getSelectionModel().select(0);
		
		ignoreSelection.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
	      @Override
	      public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2) {
	    	  updateFileList();
	      }
	    });
		
		//force padding zeroes to only allows integers to be typed in, and less than 100
		paddingZeroes.textProperty().addListener(new ChangeListener<String>() {
		    @Override public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
		    	try {
		    		if (newValue.equals("")){
		    			return;
		    		}
		    		if (newValue.matches("\\d*")) {
		    			if (!(Integer.parseInt(newValue)<100)){
			            	paddingZeroes.setText(oldValue);
			            	paddingZeroesNum = Integer.parseInt(newValue);
						}
			        } 
			        else {
			            paddingZeroes.setText(oldValue);
			        }
				} catch (Exception e) {
					paddingZeroes.setText(oldValue);
				}
		    }
		});
		
		//force padding zeroes to only allows integers and ,
		ignoreNumbers.textProperty().addListener(new ChangeListener<String>() {
		    @Override public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
		    	try {
		    		if (newValue.equals("")){
		    			return;
		    		}
		    		if (newValue.matches("[0-9,]*")) {
		    			ignoreNumbers.setText(newValue);
			        } 
			        else {
			        	ignoreNumbers.setText(oldValue);
			        }
				} catch (Exception e) {
					ignoreNumbers.setText(oldValue);
				}
		    }
		});
		
		instanceNumCounter.textProperty().addListener(new ChangeListener<String>() {
		    @Override public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
		    	try {
		    		if (newValue.equals("")){
		    			return;
		    		}
		    		if (newValue.matches("\\d*")) {
		    			instanceNumCounter.setText(newValue);
			        } 
			        else {
			        	instanceNumCounter.setText(oldValue);
			        }
				} catch (Exception e) {
					instanceNumCounter.setText(oldValue);
				}
		    }
		});
		
		instanceAddCounter.textProperty().addListener(new ChangeListener<String>() {
		    @Override public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
		    	try {
		    		if (newValue.equals("")){
		    			return;
		    		}
		    		if (newValue.matches("\\d*")) {
		    			instanceAddCounter.setText(newValue);
			        } 
			        else {
			        	instanceAddCounter.setText(oldValue);
			        }
				} catch (Exception e) {
					instanceAddCounter.setText(oldValue);
				}
		    }
		});
		
		instanceReplaceCounter.textProperty().addListener(new ChangeListener<String>() {
		    @Override public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
		    	try {
		    		if (newValue.equals("")){
		    			return;
		    		}
		    		if (newValue.matches("\\d*")) {
		    			instanceReplaceCounter.setText(newValue);
			        } 
			        else {
			        	instanceReplaceCounter.setText(oldValue);
			        }
				} catch (Exception e) {
					instanceReplaceCounter.setText(oldValue);
				}
		    }
		});
		
		instanceRemoveCounter.textProperty().addListener(new ChangeListener<String>() {
		    @Override public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
		    	try {
		    		if (newValue.equals("")){
		    			return;
		    		}
		    		if (newValue.matches("\\d*")) {
		    			instanceRemoveCounter.setText(newValue);
			        } 
			        else {
			        	instanceRemoveCounter.setText(oldValue);
			        }
				} catch (Exception e) {
					instanceRemoveCounter.setText(oldValue);
				}
		    }
		});
		
		extensionFilterField.setTooltip(new Tooltip("Type desired extensions here. Separate multiple extensions with a ' , '\n  ' . 's are not needed."));
		
		//file watcher
		//don't accept anything in sub-folders
		fileFilter = new FileFilter() {
			
			@Override
			public boolean accept(File pathname) {
				if (pathname.getPath().equals(dirName+"\\"+pathname.getName())){
					//System.out.println(pathname.toString());
					return true;
				}
				return false;
			}
		};
		fao = new FileAlterationObserver(new File(dirName), fileFilter);
		fileListener = new FileAlterationListenerImpl();
        fao.addListener(fileListener);
        
        final FileAlterationMonitor monitor = new FileAlterationMonitor(5000);
        monitor.addObserver(fao);
        try {
			monitor.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
 
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
        	 
            @Override
            public void run() {
                try {
                    System.out.println("Stopping monitor.");
                    //monitor.stop();
                } catch (Exception ignored) {
                }
            }
        }));
        
        
        Timeline checkDirectory = new Timeline(new KeyFrame(Duration.seconds(1), (ActionEvent event) ->
        {//creates a timer to check if it needs to refresh the file list, and changes the file listener when the directory is changed
            try
            {
            	if (StaticValues.getChangeDir()==1){
            		StaticValues.setChangeDir(0);
            		updateFileList();
            	}
            	if (!oldDir.equals(dirName)){
            		oldDir=dirName;
            		System.out.println("dir changed to " + oldDir);
            		monitor.removeObserver(fao);
            		fao.removeListener(fileListener);
            		fao = new FileAlterationObserver(new File(dirName), fileFilter);
            		fileListener = new FileAlterationListenerImpl();
                    fao.addListener(fileListener);
                    monitor.addObserver(fao);
            	}
            }
            catch (Exception ex)
            {
                Logger.getLogger(MassFileEditorController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }));
		checkDirectory.setCycleCount(Timeline.INDEFINITE);
		checkDirectory.play();

		//read  from undo log to get all sessions
		/*serialize and save changedLists
		URL logURL = getClass().getProtectionDomain().getCodeSource().getLocation();
		File f;
		try {
			f = new File(logURL.toURI());
		} catch (URISyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return;
		}
		String logPath = f.getPath();
		logPath = logPath.replace("file:/", ""); //adds this for some reason?
		*/
		if (new File("./undolog.dat").exists()){
			changedLists = (ChangedLists) FileSerialization.serializeRead("./undolog.dat");
		}
		
	}
	
	String oldDir = dirName;
	FileAlterationObserver fao;
	FileAlterationListenerImpl fileListener;
	FileFilter fileFilter;
	


	
	
	public void chooseFiles(){
		DirectoryChooser chooser = new DirectoryChooser();
		Stage stage = (Stage) mainAnchorPane.getScene().getWindow();
		chooser.setTitle("Choose a directory of files you want to edit");
		File tempPath = new File(dirName);
		if (!dirName.equals("") && tempPath.exists()){
			chooser.setInitialDirectory(new File(dirName));
		}
		File file = chooser.showDialog(stage);
		if (file!=null){
			dirName = file.toString();
		}
		else {
			//leave dirName as is
		}
		directoryText.setText(dirName);
		updateFileList();
	}
	
	public void updateFileList(){
		filesList.getItems().clear();
		dirName = directoryText.getText();
		File dir = new File(dirName);
		myFiles.clear();
		String ext = "";
		String filename;
		Pattern numPattern = Pattern.compile("\\d+");
		Matcher numMatcher;
		String tempNum = "";
		boolean numFlag = false;
		boolean passFlag = true;
		//ensure valid directory with files in it
		if (dir.exists() && dir.listFiles().length>0){
			//if not filter
			if (filter.size()==0){
				File[] files = new File(dirName).listFiles();
				for(File file: files){
					if (file.isFile() && !myFiles.contains(file.getName())) {
						ext = getFileExtension(file);
						//ignore system and core windows files
						if (ext.equals("bootmgr") || ext.equals("BOOTNXT") || ext.equals("sys")){
							continue;
						}
						filename = file.getName();
						filename = filename.replace(ext, "");
						switch (selectionValue) {
						case 0:
							numFlag = false;
							passFlag = true;
							if (filename.matches(".*[0-9].*")){ //if it has numbers
								if (instanceNumberSelection.getSelectionModel().getSelectedIndex()==1){ //last
									numMatcher = numPattern.matcher(new StringBuilder(filename).reverse());
								}
								else {
									numMatcher = numPattern.matcher(filename);
								}
								while (numMatcher.find()) {
									passFlag = true;
									if (instanceNumberSelection.getSelectionModel().getSelectedIndex()==1){
										tempNum = (new StringBuilder(numMatcher.group()).reverse()).toString();
									}
									else {
										tempNum = numMatcher.group();
									}
									
									for (int i = 0; i < paddingIgnoreNums.size(); i++) {
										if (ignoreSelection.getSelectionModel().getSelectedIndex()==0){ //strict
											if (tempNum.equals(paddingIgnoreNums.get(i))){
												passFlag = false;
												break;
											}
										}
										else { //loose
											if (tempNum.contains(paddingIgnoreNums.get(i))){
												passFlag = false;
												break;
											}
										}
									}
									if (passFlag  && !myFiles.contains(file.getName())){
										myFiles.add(file.getName());
										if (instanceNumberSelection.getSelectionModel().getSelectedIndex()<2){ //only add the first thing found
											numFlag = true;
										}
									}
									if (numFlag){
										numFlag = false;
										break;
									}
								}
							}
							break;
						case 1:
							if (ignoreReplaceCase==1){
								filename = filename.toUpperCase();
							}
							if (replaceText.equals("\\()")){
								if (filename.contains("(") && filename.contains(")")){
									myFiles.add(file.getName());
								}
							}
							else if (replaceText.equals("\\[]")){
								if (filename.contains("[") && filename.contains("]")){
									myFiles.add(file.getName());
								}
							}
							else if (replaceText.equals("") || ((ignoreReplaceCase==1 && filename.contains(replaceText.toUpperCase())) || filename.contains(replaceText))){
								myFiles.add(file.getName());
							}
							break;
						case 2:
							if (ignoreRemoveCase==1){
								filename = filename.toUpperCase();
							}
							if (removeString.equals("\\()")){
								if (filename.contains("(") && filename.contains(")")){
									myFiles.add(file.getName());
								}
							}
							else if (removeString.equals("\\[]")){
								if (filename.contains("[") && filename.contains("]")){
									myFiles.add(file.getName());
								}
							}
							else if (removeString.equals("") || ((ignoreRemoveCase==1 && filename.contains(removeString.toUpperCase())) || filename.contains(removeString))){
								myFiles.add(file.getName());
							}
							break;
						case 3:
							if (ignoreAddAfterCase==1){
								filename = filename.toUpperCase();
							}
							if (addStringAfter.equals("\\()")){
								if (filename.contains("(") && filename.contains(")")){
									myFiles.add(file.getName());
								}
							}
							else if (addStringAfter.equals("\\[]")){
								if (filename.contains("[") && filename.contains("]")){
									myFiles.add(file.getName());
								}
							}
							else if (addStringAfter.equals("") || ((ignoreAddAfterCase==1 && filename.contains(addStringAfter.toUpperCase())) || filename.contains(addStringAfter))){
								myFiles.add(file.getName());
							}
							break;
						}
						
				    }
				}
				
				if (myFiles.size()==0){
					filesList.getItems().add("No files of this type were found");
				}
				else {
					ObservableList<String> dirFiles = FXCollections.observableList(myFiles);
					filesList.getItems().addAll(dirFiles);
				}
			}
			//filter files types
			else {
				File[] files = new File(dirName).listFiles();
				for(File file: files){
					if (file.isFile())
						for (int i = 0; i < filter.size(); i++) {
							ext = getFileExtension(file);
							filename = file.getName();
							filename = filename.replace(ext, "");
							if (ext.equals(filter.get(i))){
								switch (selectionValue) {
								case 0:
									numFlag = false;
									passFlag = true;
									if (filename.matches(".*[0-9].*")){ //if it has numbers
										if (instanceNumberSelection.getSelectionModel().getSelectedIndex()==1){ //last
											numMatcher = numPattern.matcher(new StringBuilder(filename).reverse());
										}
										else {
											numMatcher = numPattern.matcher(filename);
										}
										while (numMatcher.find()) {
											passFlag = true;
											if (instanceNumberSelection.getSelectionModel().getSelectedIndex()==1){
												tempNum = (new StringBuilder(numMatcher.group()).reverse()).toString();
											}
											else {
												tempNum = numMatcher.group();
											}
											
											for (int j = 0; j < paddingIgnoreNums.size(); j++) {
												if (ignoreSelection.getSelectionModel().getSelectedIndex()==0){ //strict
													if (tempNum.equals(paddingIgnoreNums.get(j))){
														passFlag = false;
														break;
													}
												}
												else { //loose
													if (tempNum.contains(paddingIgnoreNums.get(j))){
														passFlag = false;
														break;
													}
												}
											}
											if (passFlag && !myFiles.contains(file.getName())){
												myFiles.add(file.getName());
												if (instanceNumberSelection.getSelectionModel().getSelectedIndex()<2){ //only add the first thing found
													numFlag = true;
												}
											}
											if (numFlag){
												numFlag = false;
												break;
											}
										}
									}
									break;
								case 1:
									if (ignoreReplaceCase==1){
										filename = filename.toUpperCase();
									}
									if (replaceText.equals("\\()")){
										if (filename.contains("(") && filename.contains(")")){
											myFiles.add(file.getName());
										}
									}
									else if (replaceText.equals("\\[]")){
										if (filename.contains("[") && filename.contains("]")){
											myFiles.add(file.getName());
										}
									}
									else if (replaceText.equals("") || ((ignoreReplaceCase==1 && filename.contains(replaceText.toUpperCase())) || filename.contains(replaceText))){
										myFiles.add(file.getName());	
									}
									break;
								case 2:
									if (ignoreRemoveCase==1){
										filename = filename.toUpperCase();
									}
									if (removeString.equals("\\()")){
										if (filename.contains("(") && filename.contains(")")){
											myFiles.add(file.getName());
										}
									}
									else if (removeString.equals("\\[]")){
										if (filename.contains("[") && filename.contains("]")){
											myFiles.add(file.getName());
										}
									}
									else if (removeString.equals("") || ((ignoreRemoveCase==1 && filename.contains(removeString.toUpperCase())) || filename.contains(removeString))){
										myFiles.add(file.getName());
									}
									break;
								case 3:
									if (ignoreAddAfterCase==1){
										filename = filename.toUpperCase();
									}
									if (addStringAfter.equals("\\()")){
										if (filename.contains("(") && filename.contains(")")){
											myFiles.add(file.getName());
										}
									}
									else if (addStringAfter.equals("\\[]")){
										if (filename.contains("[") && filename.contains("]")){
											myFiles.add(file.getName());
										}
									}
									else if (addStringAfter.equals("") || ((ignoreAddAfterCase==1 && filename.contains(addStringAfter.toUpperCase())) || filename.contains(addStringAfter))){
										myFiles.add(file.getName());
									}
									break;
								}
							}	
						}
				    }
				if (myFiles.size()==0){
					filesList.getItems().add("No files of this type were found");
				}
				else {
					ObservableList<String> dirFiles = FXCollections.observableList(myFiles);
					filesList.getItems().addAll(dirFiles);
				}
			}			
		}
		else {
			filesList.getItems().add("Invalid or empty directory");
		}
	}
	
	
	public void updateFilter(){
		String tempFilter = extensionFilterField.getText();
		tempFilter = tempFilter.trim();
		filter.clear();
		if (tempFilter.equals("") || tempFilter.equals(".")){
			updateFileList();
			return;
		}
		String tempExt = "";
		for (int i = 0; i < tempFilter.length(); i++) {
			if (tempFilter.charAt(i)!='.'){
				if (tempFilter.charAt(i)==','){
					tempExt="";
				}
				else {
					filter.remove(tempExt);
					tempExt+=tempFilter.charAt(i);
					filter.add(tempExt);
				}
			}
			
		}
		removeDuplicates(filter);
		updateFileList();
	}
	
	public void updateIgnoreNums(){
		String tempNums = ignoreNumbers.getText();
		tempNums = tempNums.trim();
		paddingIgnoreNums.clear();
		if (tempNums.equals("")){
			updateFileList();
			return;
		}
		String tempStr = "";
		for (int i = 0; i < tempNums.length(); i++) {
			if (tempNums.charAt(i)==','){
				tempStr="";
			}
			else {
				paddingIgnoreNums.remove(tempStr);
				tempStr+=tempNums.charAt(i);
				paddingIgnoreNums.add(tempStr);
			}			
		}
		removeDuplicates(paddingIgnoreNums);
		updateFileList();
	}
	
	public static <T> void removeDuplicates(ArrayList<T> list) {
	    int size = list.size();
	    int out = 0;
	    {
	        final Set<T> encountered = new HashSet<T>();
	        for (int in = 0; in < size; in++) {
	            final T t = list.get(in);
	            final boolean first = encountered.add(t);
	            if (first) {
	                list.set(out++, t);
	            }
	        }
	    }
	    while (out < size) {
	        list.remove(--size);
	    }
	}
	
	private String getFileExtension(File file) {
	    String name = file.getName();
	    try {
	        return name.substring(name.lastIndexOf(".") + 1);
	    } catch (Exception e) {
	        return "";
	    }
	}
	
	public void hideFields(){
		selectionLabel1.setVisible(false);
		selectionLabel2.setVisible(false);
		replaceCaseCheck.setVisible(false);
		instanceReplaceSelection.setVisible(false);
		addCaseCheck.setVisible(false);
		replaceTextField.setVisible(false);
		replaceWithTextField.setVisible(false);
		removeCaseCheck.setVisible(false);
		addTextField.setVisible(false);
		removeTextField.setVisible(false);
		instanceRemoveSelection.setVisible(false);
		addTextField.setVisible(false);
		addAfterTextField.setVisible(false);
		instanceAddSelection.setVisible(false);
		paddingType.setVisible(false);
		paddingZeroes.setVisible(false);
		instanceNumberSelection.setVisible(false);
		ignoreNumbers.setVisible(false);
		ignoreLabel.setVisible(false);
		ignoreSelection.setVisible(false);
		errorLabel.setVisible(false);
		instanceReplaceCounter.setVisible(false);
		instanceRemoveCounter.setVisible(false);
		instanceAddCounter.setVisible(false);
		instanceNumCounter.setVisible(false);
		numInstancesLabel.setVisible(false);
		instancesLabel.setVisible(false);
	}
	
	public void updateSelectionType(){
		hideFields();
		switch (selectionValue) {
		case 0:
			selectionLabel1.setText("Padding Style");
			selectionLabel1.setVisible(true);
			selectionLabel1.alignmentProperty().set(Pos.CENTER);
			selectionLabel2.setText("Pad how many digits?");
			selectionLabel2.setVisible(true);
			selectionLabel2.alignmentProperty().set(Pos.CENTER);
			paddingType.setVisible(true);
			paddingZeroes.setVisible(true);
			instanceNumberSelection.setVisible(true);
			ignoreNumbers.setVisible(true);
			ignoreLabel.setVisible(true);
			ignoreLabel.alignmentProperty().set(Pos.CENTER);
			ignoreSelection.setVisible(true);
			if (instanceNumberSelection.getSelectionModel().getSelectedIndex()>2){
				instanceNumCounter.setVisible(true);
				numInstancesLabel.setVisible(true);
			}
			break;
		case 1:
			selectionLabel1.setVisible(true);
			selectionLabel1.setText("Replace substring");
			selectionLabel1.alignmentProperty().set(Pos.CENTER);
			selectionLabel2.setVisible(true);
			selectionLabel2.setText("With");
			selectionLabel2.alignmentProperty().set(Pos.CENTER);
			replaceTextField.setVisible(true);
			replaceWithTextField.setVisible(true);
			replaceCaseCheck.setVisible(true);
			instanceReplaceSelection.setVisible(true);
			if (instanceReplaceSelection.getSelectionModel().getSelectedIndex()>2){
				instanceReplaceCounter.setVisible(true);
				instancesLabel.setVisible(true);
			}
			break;
		case 2:
			removeCaseCheck.setVisible(true);
			removeTextField.setVisible(true);
			selectionLabel1.setVisible(true);
			selectionLabel1.setText("Remove string");
			selectionLabel1.alignmentProperty().set(Pos.CENTER);
			instanceRemoveSelection.setVisible(true);
			if (instanceRemoveSelection.getSelectionModel().getSelectedIndex()>2){
				instanceRemoveCounter.setVisible(true);
				instancesLabel.setVisible(true);
			}
			break;
		case 3:			
			addCaseCheck.setVisible(true);
			selectionLabel1.setVisible(true);
			selectionLabel1.setText("Add string");
			selectionLabel1.alignmentProperty().set(Pos.CENTER);
			selectionLabel2.setVisible(true);
			selectionLabel2.setText("After");
			selectionLabel2.alignmentProperty().set(Pos.CENTER);
			addTextField.setVisible(true);
			addAfterTextField.setVisible(true);
			instanceAddSelection.setVisible(true);
			if (instanceAddSelection.getSelectionModel().getSelectedIndex()>2){
				instanceAddCounter.setVisible(true);
				instancesLabel.setVisible(true);
			}
			break;
		default:
			break;
		}
		updateFileList();
	}
	
	public void updateReplaceText(){
		replaceText=replaceTextField.getText();
		if (replaceText.equals("\\space")){
			replaceText = " ";
		}
		else if (replaceText.equals("\\parenthesis") || replaceText.equals("\\parentheses") || replaceText.equals("\\()")) {
			replaceText = "\\()";
		}
		else if (replaceText.equals("\\brackets") || replaceText.equals("\\bracket") || replaceText.equals("\\[]")) {
			replaceText = "\\[]";
		}
		updateFileList();
	}
	
	public void updateRemoveText(){
		removeString = removeTextField.getText();
		if (removeString.equals("\\space")){
			removeString = " ";
		}
		else if (removeString.equals("\\parenthesis") || removeString.equals("\\parentheses") || replaceText.equals("\\()")) {
			removeString = "\\()";
		}
		else if (removeString.equals("\\brackets") || removeString.equals("\\bracket") || removeString.equals("\\[]")) {
			removeString = "\\[]";
		}
		updateFileList();
	}
	
	public void updateAddText(){
		addString = addTextField.getText();
		if (addString.equals("\\space")){
			addString = " ";
		}
	}
	
	public void updateAddTextAfter(){
		addStringAfter =  addAfterTextField.getText();
		if (addStringAfter.equals("\\space")){
			addStringAfter = " ";
		}
		/*else if (addStringAfter.equals("\\parenthesis") || addStringAfter.equals("\\parentheses") || addStringAfter.equals("\\()")) {
			addStringAfter = "\\()";
		}
		else if (addStringAfter.equals("\\brackets") || addStringAfter.equals("\\bracket") || addStringAfter.equals("\\[]")) {
			addStringAfter = "\\[]";
		}
		*/
		updateFileList();
	}
	
	public void updateReplaceWithText(){
		replaceWithText=replaceWithTextField.getText();
		if (replaceWithText.equals("\\space")){
			replaceWithText = " ";
		}
	}
	
	public void updateIgnoreReplaceCase(){
		if (replaceCaseCheck.isSelected()){
			ignoreReplaceCase=1;
		}
		else {
			ignoreReplaceCase=0;
		}
		updateFileList();
	}
	
	public void updateIgnoreRemoveCase(){
		if (removeCaseCheck.isSelected()){
			ignoreRemoveCase=1;
		}
		else {
			ignoreRemoveCase=0;
		}
		updateFileList();
	}
	
	public void updateIgnoreAddAfterCase(){
		if (addCaseCheck.isSelected()){
			ignoreAddAfterCase=1;
		}
		else {
			ignoreAddAfterCase=0;
		}
		updateFileList();
	}
	
	
	public void modifyFiles() throws IOException{
		//validate required fields for errors
		switch (selectionValue) {
			case 0: 
				if (paddingZeroes.getText().equals("") || Integer.parseInt(paddingZeroes.getText())<=0){
					errorLabel.setVisible(true);
					errorLabel.setText("Digits to pad must be greater than 0");
					return;
				}
				if (instanceNumberSelection.getSelectionModel().getSelectedIndex()>2){
					if (instanceNumCounter.getText().equals("") || Integer.parseInt(instanceNumCounter.getText())<=0){
						errorLabel.setVisible(true);
						errorLabel.setText("'n Instances' must be a number greater than 0");
						return;
					}
				}
				break;
			case 1:
				if (replaceTextField.getText().equals("")){
					errorLabel.setVisible(true);
					errorLabel.setText("Text to replace cannot be blank");
					return;
				}
				if (replaceWithTextField.getText().equals("")){
					errorLabel.setVisible(true);
					errorLabel.setText("Text to replace with cannot be blank");
					return;
				}
				if (replaceTextField.getText().equals(replaceWithTextField.getText())){
					errorLabel.setVisible(true);
					errorLabel.setText("Replace texts cannot be the same");
					return;
				}	
				if (instanceReplaceSelection.getSelectionModel().getSelectedIndex()>2){
					if (instanceReplaceCounter.getText().equals("") || Integer.parseInt(instanceReplaceCounter.getText())<=0){
						errorLabel.setVisible(true);
						errorLabel.setText("'n Instances' must be a number greater than 0");
						return;
					}
				}
				break;
			case 2:
				if (removeTextField.getText().equals("")){
					errorLabel.setVisible(true);
					errorLabel.setText("Text to remove cannot be blank");
					return;
				}
				if (instanceRemoveSelection.getSelectionModel().getSelectedIndex()>2){
					if (instanceRemoveCounter.getText().equals("") || Integer.parseInt(instanceRemoveCounter.getText())<=0){
						errorLabel.setVisible(true);
						errorLabel.setText("'n Instances' must be a number greater than 0");
						return;
					}
				}
				break;
			case 3:
				if (addTextField.getText().equals("")){
					errorLabel.setVisible(true);
					errorLabel.setText("Text to add cannot be blank");
					return;
				}
				if (addAfterTextField.getText().equals("")){
					errorLabel.setVisible(true);
					errorLabel.setText("Text to add after cannot be blank");
					return;
				}
				if (instanceAddSelection.getSelectionModel().getSelectedIndex()>2){
					if (instanceAddCounter.getText().equals("") || Integer.parseInt(instanceAddCounter.getText())<=0){
						errorLabel.setVisible(true);
						errorLabel.setText("'n Instances' must be a number greater than 0");
						return;
					}
				}
				break;
			default: //never reached
				break;
		}
		if (dirName.equals("")){
			errorLabel.setVisible(true);
			errorLabel.setText("Directory cannot be empty");
			return;
		}
		if (myFiles.size()<=0){
			errorLabel.setVisible(true);
			errorLabel.setText("No files to edit that meet the criteria");
			return;
		}
		errorLabel.setVisible(false);
		
		
		//require okay'ed response from pop-up
		Stage dialog = new Stage();
		dialog.initStyle(StageStyle.UTILITY);
		Parent parent = FXMLLoader.load(getClass().getResource("ConfirmChanges.fxml"));
		Scene scene = new Scene(parent);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		dialog.setScene(scene);
		dialog.setWidth(300);
		dialog.setHeight(300);
		dialog.initModality(Modality.APPLICATION_MODAL);
		dialog.showAndWait();
		
		errorFound = false;
		String tempRemoveString = removeString;
		String tempAddStringAfter = addStringAfter;
		String tempReplaceString = replaceText;
		//modify to add literal characters instead of regex characters
		if (!tempRemoveString.equals("\\()") && !tempRemoveString.equals("\\[]")){
			tempRemoveString = tempRemoveString.replaceAll("\\\\", "");
			tempRemoveString = tempRemoveString.replaceAll("\\[", "\\\\\\[");
			tempRemoveString = tempRemoveString.replaceAll("\\]", "\\\\\\]");
			tempRemoveString = tempRemoveString.replaceAll("\\(", "\\\\\\(");
			tempRemoveString = tempRemoveString.replaceAll("\\)", "\\\\\\)");
			tempRemoveString = tempRemoveString.replaceAll("\\&", "\\\\\\&&");
			tempRemoveString = tempRemoveString.replaceAll("\\-", "\\\\\\-");
			tempRemoveString = tempRemoveString.replaceAll("\\.", "\\\\\\.");
			tempRemoveString = tempRemoveString.replaceAll("\\$", "\\\\\\$");
		}
		else {
			System.out.println("asdf");
		}
		
		if (!tempAddStringAfter.equals("\\()") && !tempAddStringAfter.equals("\\[]")){
			tempAddStringAfter = tempAddStringAfter.replaceAll("\\\\", "");
			tempAddStringAfter = tempAddStringAfter.replaceAll("\\[", "\\\\\\[");
			tempAddStringAfter = tempAddStringAfter.replaceAll("\\]", "\\\\\\]");
			tempAddStringAfter = tempAddStringAfter.replaceAll("\\(", "\\\\\\(");
			tempAddStringAfter = tempAddStringAfter.replaceAll("\\)", "\\\\\\)");
			tempAddStringAfter = tempAddStringAfter.replaceAll("\\&", "\\\\\\&&");
			tempAddStringAfter = tempAddStringAfter.replaceAll("\\-", "\\\\\\-");
			tempAddStringAfter = tempAddStringAfter.replaceAll("\\.", "\\\\\\.");
			tempAddStringAfter = tempAddStringAfter.replaceAll("\\$", "\\\\\\$");
		}
		
		if (!tempReplaceString.equals("\\()") && !tempReplaceString.equals("\\[]")){
			tempReplaceString = tempReplaceString.replaceAll("\\\\", "");
			tempReplaceString = tempReplaceString.replaceAll("\\[", "\\\\\\[");
			tempReplaceString = tempReplaceString.replaceAll("\\]", "\\\\\\]");
			tempReplaceString = tempReplaceString.replaceAll("\\(", "\\\\\\(");
			tempReplaceString = tempReplaceString.replaceAll("\\)", "\\\\\\)");
			tempReplaceString = tempReplaceString.replaceAll("\\&", "\\\\\\&&");
			tempReplaceString = tempReplaceString.replaceAll("\\-", "\\\\\\-");
			tempReplaceString = tempReplaceString.replaceAll("\\.", "\\\\\\.");
			tempReplaceString = tempReplaceString.replaceAll("\\$", "\\\\\\$");
		}
		
		if (StaticValues.getConfirmation()==1){
			String old_filename = "";
			String new_filename = "";
			String ext = "";
			String newNum = "";
			switch (selectionValue) {
			case 0: {//number padding
				boolean passFlag;
				Pattern numPattern = Pattern.compile("\\d+");
				Matcher numMatcher;
				String tempNum = "";
				newNum = "";
				for (int i = 0; i < myFiles.size(); i++) {
					passFlag = true;
					old_filename=myFiles.get(i);
					ext = getFileExtension(new File(dirName+old_filename));
					new_filename = old_filename.replace("."+ext, "");
					numMatcher = numPattern.matcher(new_filename);
					ArrayList<SubstrFound> numList = new ArrayList<SubstrFound>();
					int counter = 0;
					while (numMatcher.find()){
						passFlag = true;
						tempNum = numMatcher.group().toString();
						for (int j = 0; j < paddingIgnoreNums.size(); j++) {
							if (ignoreSelection.getSelectionModel().getSelectedIndex()==0){ //strict
								if (tempNum.equals(paddingIgnoreNums.get(j))){
									passFlag = false;
									break;
								}
							}
							else { //loose
								if (tempNum.contains(paddingIgnoreNums.get(j))){
									passFlag = false;
									break;
								}
							}
						}
						if (passFlag){
							numList.add(new SubstrFound(tempNum, numMatcher.start(), numMatcher.end()));
							counter++;
							if (instanceNumberSelection.getSelectionModel().getSelectedIndex()==0){ //first instance
								break;
							}
						}
					}
					//ensure the numList is in order by start position
					Collections.sort(numList, new Comparator<SubstrFound>() {
				        @Override
				        public int compare(SubstrFound start1, SubstrFound start2)
				        {

				            return  Integer.compare(start1.startPos, start2.startPos);
				        }
				    });
					
					if (instanceNumberSelection.getSelectionModel().getSelectedIndex()>0){ //first instance
						Collections.reverse(numList);
					}
					
					int startNum;
				    for (int j = 0; j < numList.size(); j++) {
				    	if (instanceNumberSelection.getSelectionModel().getSelectedIndex()==1 && j>0){//last
				    		break;
				    	}
				    	if (instanceNumberSelection.getSelectionModel().getSelectedIndex()==3) {
				    		startNum = numList.size()-Integer.parseInt(instanceNumCounter.getText());
							if (j<startNum){
								continue;
							}
							
						}
				    	if (instanceNumberSelection.getSelectionModel().getSelectedIndex()==4) {
							if (Integer.parseInt(instanceNumCounter.getText())<=j){
								break;
							}
						}
				    	if (paddingType.getSelectionModel().getSelectedIndex()==0){ //left
				    		newNum = String.format(("%0"+paddingZeroes.getText()+"d"), Integer.parseInt(numList.get(j).numStr));
				    	}
				    	else {
				    		newNum = numList.get(j).numStr;
				    		for (int k = 0; k < Integer.parseInt(paddingZeroes.getText()); k++) {
				    			if (newNum.length()>=Integer.parseInt(paddingZeroes.getText())){
				    				break;
				    			}
								newNum+="0";
							}
						}
				    	new_filename = new_filename.substring(0, numList.get(j).startPos)+newNum+ new_filename.substring(numList.get(j).endPos, new_filename.length());
					}
				    changeFilename(old_filename, new_filename+"."+ext);
				}
				break;
			}
			case 1: {//replace
				Pattern stringPattern ;
				Matcher stringMatcher;
				newNum = "";
				for (int i = 0; i < myFiles.size(); i++) {					
					if (tempReplaceString.equals("\\()")){
						stringPattern = Pattern.compile("\\s*\\([^\\)]*\\)\\s*");
					}
					else if (tempReplaceString.equals("\\[]")) {
						stringPattern = Pattern.compile("\\s*\\[[^\\]]*\\]\\s*");
					}
					else {
						if (ignoreReplaceCase==1){
							stringPattern = Pattern.compile("(?i)"+tempReplaceString);
						}
						else {
							stringPattern = Pattern.compile(tempReplaceString);
						}
					}					
					old_filename=myFiles.get(i);
					ext = getFileExtension(new File(dirName+old_filename));
					new_filename = old_filename.replace("."+ext, "");
					stringMatcher = stringPattern.matcher(new_filename);
					ArrayList<SubstrFound> strList = new ArrayList<SubstrFound>();
					while (stringMatcher.find()){
						
						strList.add(new SubstrFound(stringMatcher.group().toString(), stringMatcher.start(), stringMatcher.end()));
						if (instanceReplaceSelection.getSelectionModel().getSelectedIndex()==0){ //first instance
							break;
						}
					}
					//ensure the numList is in order by start position
					Collections.sort(strList, new Comparator<SubstrFound>() {
				        @Override
				        public int compare(SubstrFound start1, SubstrFound start2)
				        {

				            return  Integer.compare(start1.startPos, start2.startPos);
				        }
				    });
					
					if (instanceReplaceSelection.getSelectionModel().getSelectedIndex()>0){ //not first instance
						Collections.reverse(strList);
					}
					int startNum;
					for (int j = 0; j < strList.size(); j++) {
						if (instanceReplaceSelection.getSelectionModel().getSelectedIndex()==1 && j>0){//last
				    		break;
				    	}
				    	if (instanceReplaceSelection.getSelectionModel().getSelectedIndex()==3) {
				    		startNum = strList.size()-Integer.parseInt(instanceReplaceCounter.getText());
							if (startNum>0 && j<startNum){
								continue;
							}
							
						}
				    	if (instanceReplaceSelection.getSelectionModel().getSelectedIndex()==4) {
							if (Integer.parseInt(instanceReplaceCounter.getText())<=j){
								break;
							}
						}
						new_filename = new_filename.substring(0, strList.get(j).startPos)+replaceWithText+ new_filename.substring(strList.get(j).endPos, new_filename.length());
					}
					changeFilename(old_filename, new_filename+"."+ext);
				}
				break;
			}
			case 2: {//remove
				Pattern stringPattern ;
				Matcher stringMatcher;
				newNum = "";
				for (int i = 0; i < myFiles.size(); i++) {					
					if (tempRemoveString.equals("\\()")){
						stringPattern = Pattern.compile("\\s*\\([^\\)]*\\)\\s*");
					}
					else if (tempRemoveString.equals("\\[]")) {
						stringPattern = Pattern.compile("\\s*\\[[^\\]]*\\]\\s*");
					}
					else {
						if (ignoreReplaceCase==1){
							stringPattern = Pattern.compile("(?i)"+tempRemoveString);
						}
						else {
							stringPattern = Pattern.compile(tempRemoveString);
						}
					}					
					old_filename=myFiles.get(i);
					ext = getFileExtension(new File(dirName+old_filename));
					new_filename = old_filename.replace("."+ext, "");
					stringMatcher = stringPattern.matcher(new_filename);
					ArrayList<SubstrFound> strList = new ArrayList<SubstrFound>();
					while (stringMatcher.find()){
						
						strList.add(new SubstrFound(stringMatcher.group().toString(), stringMatcher.start(), stringMatcher.end()));
						if (instanceRemoveSelection.getSelectionModel().getSelectedIndex()==0){ //first instance
							break;
						}
					}
					//ensure the numList is in order by start position
					Collections.sort(strList, new Comparator<SubstrFound>() {
				        @Override
				        public int compare(SubstrFound start1, SubstrFound start2)
				        {

				            return  Integer.compare(start1.startPos, start2.startPos);
				        }
				    });
					
					if (instanceRemoveSelection.getSelectionModel().getSelectedIndex()>0){ //not first instance
						Collections.reverse(strList);
					}
					int startNum;
					for (int j = 0; j < strList.size(); j++) {
						if (instanceRemoveSelection.getSelectionModel().getSelectedIndex()==1 && j>0){//last
				    		break;
				    	}
				    	if (instanceRemoveSelection.getSelectionModel().getSelectedIndex()==3) {
				    		startNum = strList.size()-Integer.parseInt(instanceRemoveCounter.getText());
							if (startNum>0 && j<startNum){
								continue;
							}
							
						}
				    	if (instanceRemoveSelection.getSelectionModel().getSelectedIndex()==4) {
							if (Integer.parseInt(instanceRemoveCounter.getText())<=j){
								break;
							}
						}
						new_filename = new_filename.substring(0, strList.get(j).startPos) + new_filename.substring(strList.get(j).endPos, new_filename.length());
					}
					//System.out.println(old_filename+" "+new_filename);
					changeFilename(old_filename, new_filename+"."+ext);
				}
				break;
			}
			case 3: {//add
				Pattern stringPattern ;
				Matcher stringMatcher;
				newNum = "";
				for (int i = 0; i < myFiles.size(); i++) {					
					if (tempAddStringAfter.equals("\\()")){
						stringPattern = Pattern.compile("\\s*\\([^\\)]*\\)\\s*");
					}
					else if (tempAddStringAfter.equals("\\[]")) {
						stringPattern = Pattern.compile("\\s*\\[[^\\]]*\\]\\s*");
					}
					else {
						if (ignoreReplaceCase==1){
							stringPattern = Pattern.compile("(?i)"+tempAddStringAfter);
						}
						else {
							stringPattern = Pattern.compile(tempAddStringAfter);
						}
					}					
					old_filename=myFiles.get(i);
					ext = getFileExtension(new File(dirName+old_filename));
					new_filename = old_filename.replace("."+ext, "");
					stringMatcher = stringPattern.matcher(new_filename);
					ArrayList<SubstrFound> strList = new ArrayList<SubstrFound>();
					while (stringMatcher.find()){
						
						strList.add(new SubstrFound(stringMatcher.group().toString(), stringMatcher.start(), stringMatcher.end()));
						if (instanceAddSelection.getSelectionModel().getSelectedIndex()==0){ //first instance
							break;
						}
					}
					//ensure the numList is in order by start position
					Collections.sort(strList, new Comparator<SubstrFound>() {
				        @Override
				        public int compare(SubstrFound start1, SubstrFound start2)
				        {

				            return  Integer.compare(start1.startPos, start2.startPos);
				        }
				    });
					
					if (instanceAddSelection.getSelectionModel().getSelectedIndex()>0){ //not first instance
						Collections.reverse(strList);
					}
					int startNum;
					for (int j = 0; j < strList.size(); j++) {
						if (instanceAddSelection.getSelectionModel().getSelectedIndex()==1 && j>0){//last
				    		break;
				    	}
				    	if (instanceAddSelection.getSelectionModel().getSelectedIndex()==3) {
				    		startNum = strList.size()-Integer.parseInt(instanceAddCounter.getText());
							if (startNum>0 && j<startNum){
								continue;
							}
							
						}
				    	if (instanceAddSelection.getSelectionModel().getSelectedIndex()==4) {
							if (Integer.parseInt(instanceAddCounter.getText())<=j){
								break;
							}
						}
						new_filename = new_filename.substring(0, strList.get(j).startPos) + strList.get(j).numStr + addString + new_filename.substring(strList.get(j).endPos, new_filename.length());
					}
					//System.out.println(old_filename+" "+new_filename);
					changeFilename(old_filename, new_filename+"."+ext);
				}
				break;
			}
			case 4: //regex
				break;
			default:
				break;
			}
		}
		else {
			return;
		}
		if (errorFound){
			writeToErrorLog(newLineChar);
		}
		//if any files were changed
		if (tempFileList.size()>0){
			changedLists.getFilesList().add(new ArrayList<>(tempFileList));
			tempFileList.clear();
			/*serialize and save changedLists
			URL errURL = getClass().getProtectionDomain().getCodeSource().getLocation();
			File f;
			try {
				f = new File(errURL.toURI());
			} catch (URISyntaxException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				return;
			}
			String errPath = f.getPath();
			errPath = errPath.replace("file:/", ""); //adds this for some reason?
			*/
			FileSerialization.serializeWrite("./undolog.dat", changedLists);
			
		}		
		updateFileList();
	}
	
	public void undoLastChange(){
		//make sure there are changes to undo
		int last = changedLists.getFilesList().size()-1;
		if (last<0){
			errorLabel.setVisible(true);
			errorLabel.setText("No changes to undo");
			return;
		}
		else {
			errorLabel.setVisible(false);
		}
		errorFound = false;
		//require okay'ed response from pop-up
		Stage dialog = new Stage();
		dialog.initStyle(StageStyle.UTILITY);
		Parent parent;
		try {
			parent = FXMLLoader.load(getClass().getResource("ConfirmChanges.fxml"));
			Scene scene = new Scene(parent);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			dialog.setScene(scene);
			dialog.setWidth(300);
			dialog.setHeight(300);
			dialog.initModality(Modality.APPLICATION_MODAL);
			dialog.showAndWait();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		if (StaticValues.getConfirmation()==1){
			String new_name;
			String old_name;
			String  tempDir;
			for (int r = 0; r<changedLists.getFilesList().get(last).size(); r++){
				old_name = changedLists.getFilesList().get(last).get(r).getNew_filename();
				new_name = changedLists.getFilesList().get(last).get(r).getOld_filename();
				tempDir = dirName;
				dirName = changedLists.getFilesList().get(last).get(r).getDir();
				changeFilename(old_name,new_name);
				dirName = tempDir;
			}
			if (errorFound){
				writeToErrorLog(newLineChar);
			}
			//remove last
			changedLists.getFilesList().remove(last);

			/*serialize and save changedLists
			URL errURL = getClass().getProtectionDomain().getCodeSource().getLocation();
			File f;
			try {
				f = new File(errURL.toURI());
			} catch (URISyntaxException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				return;
			}
			String errPath = f.getPath();
			*/
			//errPath = errPath.replace("file:/", ""); //adds this for some reason?
			if (new File("./undolog.dat").exists()){
				new File("./undolog.dat").delete();
			}
			if (changedLists.getFilesList().size()>0){
				FileSerialization.serializeWrite("./undolog.dat", changedLists);
			}
		}
		else {
			return;
		}
	}
	
	public String replaceLast(String text, String regex, String replacement) {
        return text.replaceFirst("(?s)(.*)" + regex, "$1" + replacement);
    }
	
	private boolean errorFound = false;
	private String newLineChar = System.getProperty("line.separator");
	
	//assume names inserted have extensions
	public void changeFilename(String old_name, String new_name){		
	    try	
	    {
	    	// The File (or directory) with the old name
	    	File oldFile = new File(dirName+"\\"+old_name);

	    	// The File (or directory) with the new name
	    	File newFile = new File(dirName+"\\"+new_name);
	    	if (newFile.exists()){
	    		System.out.println("The file "+new_name+" already exists, "+old_name+" was not changed.");
	    		writeToErrorLog("The file "+new_name+" already exists, "+old_name+" was not changed.");
	    		return;
	    	}	
	    	if (new_name.length()>255){
	    		writeToErrorLog("File name "+new_name+" is too long.  Filenames must be shorter than 256 characters");
	    		return;
	    	}
	    	// Rename file (or directory)
	    	boolean success = oldFile.renameTo(newFile);
	    	if (!success) {
	    	    // File was not successfully renamed
	    		//if (oldFile.exists()){
	    			System.out.println(dirName+"\\"+old_name);
	    		//}
	    		System.out.println(old_name+" was not renamed to "+ new_name+" .  The file may be read only, or you may not have access to write in this directory");
	    		writeToErrorLog(old_name+" was not renamed to "+ new_name+" .  The file may be read only, or you may not have access to write in this directory");
	    	}
	    	else {
	    		//add to tempFileList
	    		tempFileList.add(new FileChanged(old_name, new_name, dirName));
	    	}

		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Unknown error occured while changing the name of "+old_name+":"+e.toString());
			writeToErrorLog("Unknown error occured while changing the name of "+old_name+":"+e.toString());
		}
	}
	
	//when in jar form should write outside of jar
	public void writeToErrorLog(String errorStr){
		/*
		URL errURL = getClass().getProtectionDomain().getCodeSource().getLocation();
		File f;
		try {
			f = new File(errURL.toURI());
		} catch (URISyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return;
		}
		String errPath = f.getPath();
		errPath = errPath.replace("file:/", ""); //adds this for some reason?
		*/
		try {

			errorFile = new FileWriter("./errorlog.txt",true);
			BufferedWriter errorFileWriter = new BufferedWriter(errorFile);
			if (errorFound==false){
				long yourmilliseconds = System.currentTimeMillis();
			    SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");

			    Date resultdate = new Date(yourmilliseconds);
			    errorFileWriter.write("Date: "+sdf.format(resultdate)+"  Directory: "+dirName);
			    errorFileWriter.newLine();
				errorFound = true;
			}
			errorFileWriter.write(errorStr);
			errorFileWriter.newLine();
			errorFileWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
}
