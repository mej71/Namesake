package application;

import java.io.File;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationObserver;

public class FileAlterationListenerImpl implements FileAlterationListener {

	/**
     * {@inheritDoc}
     */
    @Override
    public void onStart(final FileAlterationObserver observer) {
        //System.out.println("The WindowsFileListener has started on " + observer.getDirectory().getAbsolutePath());
    }
 
    /**
     * {@inheritDoc}
     */
    @Override
    public void onDirectoryCreate(final File directory) {
        ///System.out.println(directory.getAbsolutePath() + " was created.");
        StaticValues.setChangeDir(1);
    }
 
    /**
     * {@inheritDoc}
     */
    @Override
    public void onDirectoryChange(final File directory) {
        //System.out.println(directory.getAbsolutePath() + " wa modified");
        StaticValues.setChangeDir(1);
    }
 
    /**
     * {@inheritDoc}
     */
    @Override
    public void onDirectoryDelete(final File directory) {
        //System.out.println(directory.getAbsolutePath() + " was deleted.");
        StaticValues.setChangeDir(1);
    }
 
    /**
     * {@inheritDoc}
     */
    @Override
    public void onFileCreate(final File file) {
        //System.out.println(file.getAbsoluteFile() + " was created.");
        //System.out.println("----------> length: " + file.length());
        //System.out.println("----------> last modified: " + new Date(file.lastModified()));
        //System.out.println("----------> readable: " + file.canRead());
        //System.out.println("----------> writable: " + file.canWrite());
        //System.out.println("----------> executable: " + file.canExecute());
        StaticValues.setChangeDir(1);
    }
 
    /**
     * {@inheritDoc}
     */
    @Override
    public void onFileChange(final File file) {
        //System.out.println(file.getAbsoluteFile() + " was modified.");
        //System.out.println("----------> length: " + file.length());
        //System.out.println("----------> last modified: " + new Date(file.lastModified()));
        //System.out.println("----------> readable: " + file.canRead());
        //System.out.println("----------> writable: " + file.canWrite());
        //System.out.println("----------> executable: " + file.canExecute());
        StaticValues.setChangeDir(1);
    }
 
    /**
     * {@inheritDoc}
     */
    @Override
    public void onFileDelete(final File file) {
        //System.out.println(file.getAbsoluteFile() + " was deleted.");
        StaticValues.setChangeDir(1);
    }
 
    /**
     * {@inheritDoc}
     */
    @Override
    public void onStop(final FileAlterationObserver observer) {
        //System.out.println("The WindowsFileListener has stopped on " + observer.getDirectory().getAbsolutePath());
    }

}
