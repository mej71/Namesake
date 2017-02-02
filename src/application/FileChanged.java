package application;

import java.io.Serializable;


public class FileChanged implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String old_filename;
	private String new_filename;
	private String dir;
	
	public FileChanged(String p_old_filename, String p_new_filename, String p_dir){
		setOld_filename(p_old_filename);
		setNew_filename(p_new_filename);
		setDir(p_dir);
	}

	public String getOld_filename() {
		return old_filename;
	}

	public void setOld_filename(String old_filename) {
		this.old_filename = old_filename;
	}

	public String getNew_filename() {
		return new_filename;
	}

	public void setNew_filename(String new_filename) {
		this.new_filename = new_filename;
	}

	public String getDir() {
		return dir;
	}

	public void setDir(String dir) {
		this.dir = dir;
	}
}
