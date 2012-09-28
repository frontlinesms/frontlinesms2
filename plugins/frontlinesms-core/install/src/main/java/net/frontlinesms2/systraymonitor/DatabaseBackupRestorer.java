package net.frontlinesms2.systraymonitor;

import static net.frontlinesms2.systraymonitor.Utils.*;
import static net.frontlinesms2.systraymonitor.CommandlineUtils.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Arrays;

public class DatabaseBackupRestorer {
	public static final String DB_FILE_NAME = "prodDb.h2.db";
	public static final String BACKUP_FOLDER_IDENTIFIER = ".frontlinesms2-backup.";
	public static final String BROKEN_DB_BACKUP_FOLDER_NAME = ".damaged_db";
	
	public DatabaseBackupRestorer() {

	}

	public boolean restore(String resourcePath) {
		File resourceDir = new File(resourcePath);
		if (!resourceDir.exists()) {
			System.out.println("Can't find existing resource directory at " + resourceDir.getAbsolutePath());
			return false;
		}
		System.out.println("looking for DB at " + resourcePath + "/" + DB_FILE_NAME);
		File brokenDatabase = new File(resourcePath, DB_FILE_NAME);
		if (!brokenDatabase.exists()) {
			System.out.println("Current database not found at " + brokenDatabase.getAbsolutePath());
			return false;
		}
		File latestBackup;
		String [] backupFiles = resourceDir.getParentFile().list(new FilenameFilter() {
			@Override
			public boolean accept(File arg0, String arg1) {
				return arg1.contains(BACKUP_FOLDER_IDENTIFIER);
			}
		});
		if (backupFiles.length == 0) {
			System.out.println("no backup folders found");
			return false;
		}
		Arrays.sort(backupFiles);
		latestBackup = new File(resourceDir.getParentFile().getAbsolutePath() + "/" + backupFiles[0] + "/" + DB_FILE_NAME);
		System.out.println("Trying to find backup db at "+latestBackup.getAbsolutePath());
		if (latestBackup.exists()) {
			File brokenBackupDir = new File(resourceDir.getAbsolutePath() + "/" + BROKEN_DB_BACKUP_FOLDER_NAME + "/");
			brokenBackupDir.mkdir();
			copyFile(brokenDatabase, new File(brokenDatabase.getParentFile().getAbsolutePath() + "/" + BROKEN_DB_BACKUP_FOLDER_NAME + "/" + brokenDatabase.getName()));
			brokenDatabase.delete();
			copyFile(latestBackup, brokenDatabase);
			return true;
		}
		else {
			System.out.println("Latest backup not found");
			return false;
		}
	}
	
	private void copyFile(File sourceFile, File destFile) {
		System.out.println("Copying from "+sourceFile.getAbsolutePath() + " to " + destFile.getAbsolutePath());
		try {
			if(!destFile.exists()) {
				destFile.createNewFile();
			}
			FileChannel source = null;
			FileChannel destination = null;
			try {
				source = new FileInputStream(sourceFile).getChannel();
				destination = new FileOutputStream(destFile).getChannel();
				destination.transferFrom(source, 0, source.size());
			} finally {
				if(source != null) { try { source.close(); } catch(Exception _) { /* ignore */ } }
				if(destination != null) { try { destination.close(); } catch(Exception _) { /* ignore */ }}
			}
		} catch(IOException e) {
			System.err.println("failed to copy file");
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new DatabaseBackupRestorer().restore(System.getProperty("user.home") +"/.frontlinesms2");
	}
}

