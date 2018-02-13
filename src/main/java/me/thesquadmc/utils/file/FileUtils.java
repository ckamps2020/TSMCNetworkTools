package me.thesquadmc.utils.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public final class FileUtils {

	private static String rootFolder = new File(".").getAbsolutePath();

	public static void unzip(String zipName) {
		try {
			ZipFile zipFile = new ZipFile(zipName + ".zip");
			Enumeration<?> enu = zipFile.entries();
			while (enu.hasMoreElements()) {
				ZipEntry zipEntry = (ZipEntry) enu.nextElement();

				String name = zipEntry.getName();
				File file = new File(name);
				if (name.endsWith("/")) {
					file.mkdirs();
					continue;
				}
				File parent = file.getParentFile();
				if (parent != null) {
					parent.mkdirs();
				}
				InputStream is = zipFile.getInputStream(zipEntry);
				FileOutputStream fos = new FileOutputStream(file);
				byte[] bytes = new byte[1024];
				int length;
				while ((length = is.read(bytes)) >= 0) {
					fos.write(bytes, 0, length);
				}
				is.close();
				fos.close();

			}
			zipFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static boolean deleteFile(File path) {
		try {
			if(path.exists()) {
				File files[] = path.listFiles();
				for(int i=0; i<files.length; i++) {
					if(files[i].isDirectory()) {
						deleteFile(files[i]);
					} else {
						files[i].delete();
					}
				}
			}
			return(path.delete());
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public static String getRootFolder() {
		return rootFolder;
	}

}
