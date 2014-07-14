package com.texasjake95.gradle.minecraft.version;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.gradle.api.Project;

public class CCVersion {

	private static final String urlFormat = "http://www.chickenbones.net/Files/notification/version.php?version=%s&file=%s";

	private static String getUserVersion(Project project, String versionProp)
	{
		if (project.hasProperty(versionProp))
			return (String) project.property(versionProp);
		return null;
	}

	public static String getVersion(Project project, String mod, String versionProp)
	{
		String userVersion = getUserVersion(project, versionProp);
		if (userVersion != null)
			return userVersion;
		File versionFile = new File(project.getBuildDir().getAbsolutePath() + "/version.info");
		if (!project.getGradle().getStartParameter().isOffline())
			try
			{
				URL url = new URL(String.format(urlFormat, project.property("minecraft_version"), mod));
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setRequestMethod("GET");
				conn.setConnectTimeout(5000);
				conn.setReadTimeout(5000);
				BufferedReader read = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				String ret = read.readLine();
				read.close();
				if (ret == null)
					ret = "";
				if (!ret.startsWith("Ret: "))
					return null;
				String version = ret.substring(5);
				Version.updateVersionFile(project, versionFile, mod, version);
				return version;
			}
			catch (Exception e)
			{
			}
		if (versionFile.exists())
			try
			{
				String fileVersion = Version.getVersionFromFile(project, versionFile, mod + "Version");
				if (fileVersion != null)
					return fileVersion;
			}
			catch (Exception e)
			{
			}
		throw new IllegalArgumentException("Unable to connect to the internet. Please specify a " + mod + " version to use (CommandLine => -P" + versionProp + "=X.X.X.X or gradle.properties => " + versionProp + "=X.X.X.X)");
	}
}
