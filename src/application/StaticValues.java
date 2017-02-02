package application;

public class StaticValues {

	private static int confirmation = -1;
	private static int changeDir = 0;
	
	public static void setConfirmed(){
		confirmation=1;
	}
	
	public static void setCancel(){
		confirmation=0;
	}
	
	public static int getConfirmation(){
		return confirmation;
	}

	public static int getChangeDir() {
		return changeDir;
	}

	public static void setChangeDir(int changeDir) {
		StaticValues.changeDir = changeDir;
	}
}
