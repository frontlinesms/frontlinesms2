package net.frontlinesms.install4j.custom;

import com.install4j.api.actions.*;
import com.install4j.api.context.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;

import static net.frontlinesms.install4j.custom.Futil.*;

public class UserDataBackupAction extends AbstractInstallAction {
	private static final String dateFormat = "yyyy-MM-dd-HH-mm";
	private static final long backupExpiryDuration = 7776000000L; // 90 days in miliseconds
	private static final String backupDirNameFormat = ".frontlinesms2-backup.";
	private File fsms2Home;

	public boolean isRollbackSupported() {
		return false;
	}

	public boolean install(InstallerContext context) {
		fsms2Home = Futil.getResourceDirectory();
		log(".fronlinesms2 folder " + (fsms2Home.exists() ? "found" : "not found"));
		newBackup();
		deleteOldBackups();
		return true;
	}

	private void newBackup() {
		if (fsms2Home.exists()) {
			Date now = new Date();
			SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
			File thisBackupDir = new File(fsms2Home.getAbsolutePath() + "-backup." + formatter.format(now));
			log("Will backup to " + thisBackupDir.getAbsolutePath());
			thisBackupDir.mkdir();
			String [] files = fsms2Home.list();
			for (int i = 0; i < files.length; i++) {
				log("trying to copy " + files[i]);
				copyFile(new File(fsms2Home + File.separator + files[i]), 
					new File(thisBackupDir + File.separator + files[i]));
			}
			log("Successfully backed up to " + thisBackupDir.getAbsolutePath());
		} else {
			// No resource directory found. Nothing to back up?
			// TODO may have to find way to handle Windows pointing at wrong user.home
		}
	}

	private void deleteOldBackups() {
		File[] backups = new File(System.getProperty("user.home")).listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.contains(backupDirNameFormat);
			}
		});
		Date oldestBackupToKeep = new Date(new Date().getTime() - backupExpiryDuration);
		log("oldest backup to keep: " + oldestBackupToKeep);
		for(int i=0; i<backups.length; i++) {
			log("Found backup :" + backups[i].getAbsolutePath());
			Date thisBackupDate;
			try {
				thisBackupDate = new SimpleDateFormat(dateFormat).parse(backups[i].getPath().substring(
						backups[i].getPath().lastIndexOf(backupDirNameFormat) + backupDirNameFormat.length()));
			} catch(ParseException e) {
				log("failed to parse date!");
				break;
			}
			log("Backup is dated " + thisBackupDate);
			if(thisBackupDate.compareTo(oldestBackupToKeep) < 0) {
				log("backup is older than backup expiry duration. Will be deleted");
				File [] currentDirContents = backups[i].listFiles();
				for(int f=0; f < currentDirContents.length; f++) {
					currentDirContents[f].delete();
				}
				log(backups[i].delete() ? " delete successful" : " delete failed");
			}
		}
	}

	private void copyFile(File sourceFile, File destFile) {
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
			log("failed to copy file");
		}
	}

	public static void main(String [] args) {
		// For testing
		UserDataBackupAction action = new UserDataBackupAction();
		action.install(null);
	}
}

