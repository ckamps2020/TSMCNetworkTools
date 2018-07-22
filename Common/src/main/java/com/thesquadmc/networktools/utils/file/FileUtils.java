package com.thesquadmc.networktools.utils.file;

import com.thesquadmc.networktools.NetworkTools;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

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
            if (path.exists()) {
                File files[] = path.listFiles();
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteFile(file);
                    } else {
                        file.delete();
                    }
                }
            }
            return (path.delete());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static YamlConfiguration getConfig(String name) {
        File file = new File(NetworkTools.getInstance().getDataFolder(), name + ".yml");
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            NetworkTools.getInstance().saveResource(name + ".yml", false);
        }

        YamlConfiguration config = new YamlConfiguration();
        try {
            config.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

        return config;
    }


    public static boolean saveConfig(FileConfiguration config) {
        return false;
    }

    public static String getRootFolder() {
        return rootFolder;
    }

}
