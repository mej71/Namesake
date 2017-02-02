package application;

import java.io.Serializable;
import java.util.ArrayList;

public class ChangedLists implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<ArrayList<FileChanged>> filesList;
	
	public ChangedLists() {
		setFilesList(new ArrayList<>());
	}

	public ArrayList<ArrayList<FileChanged>> getFilesList() {
		return filesList;
	}

	public void setFilesList(ArrayList<ArrayList<FileChanged>> filesList) {
		this.filesList = filesList;
	}
	
}
