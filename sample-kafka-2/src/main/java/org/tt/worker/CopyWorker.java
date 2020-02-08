package org.tt.worker;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class CopyWorker implements Runnable {

	String fileName;

	public CopyWorker(String fileName) {
		this.fileName = fileName;
	}

	@Override
	public void run() {
		String source = "/Users/kh2338/Documents/MyFiles/TestFolder/src/" + this.fileName;
		String destination = "/Users/kh2338/Documents/MyFiles/TestFolder/dest/" + this.fileName;

		File srcFile = new File(source);
		File destFile = new File(destination);

		try {
			Files.copy(srcFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
