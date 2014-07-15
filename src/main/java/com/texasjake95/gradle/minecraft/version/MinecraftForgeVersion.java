package com.texasjake95.gradle.minecraft.version;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;

import org.gradle.api.Project;

import com.google.common.io.ByteStreams;
import com.google.gson.Gson;

public class MinecraftForgeVersion {

	private static final String urlFormat = "http://files.minecraftforge.net/maven/%s/%s/promotions_slim.json";

	private static boolean getPropertyAsBoolean(Project project, String propName)
	{
		Object prop = project.property(propName);
		if (prop instanceof Boolean)
			return (boolean) prop;
		return false;
	}

	private static String getUserForge(Project project, String mod, String versionProp)
	{
		if (project.hasProperty(versionProp))
		{
			project.getLogger().info("Using user defined version of " + mod);
			return (String) project.property(versionProp);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static String getVersion(Project project, String mod, String group, String versionProp)
	{
		project.getLogger().info("Attempting to get " + mod + " version");
		String version = getUserForge(project, mod, versionProp);
		if (version != null)
			return version;
		File versionFile = new File(project.getBuildDir().getAbsolutePath() + "/version.info");
		if (!project.getGradle().getStartParameter().isOffline())
			try
			{
				URL url = new URL(String.format(urlFormat, group.replace(".", "/"), mod));
				InputStream con = url.openStream();
				String data = new String(ByteStreams.toByteArray(con));
				con.close();
				Map<String, Object> json = new Gson().fromJson(data, Map.class);
				// String homepage = (String)json.get("homepage");
				Map<String, String> promos = (Map<String, String>) json.get("promos");
				String rec = promos.get(project.property("minecraft_version") + "-recommended");
				String lat = promos.get(project.property("minecraft_version") + "-latest");
				boolean useLatest = project.hasProperty("useLatest" + mod) ? getPropertyAsBoolean(project, "useLatest" + mod) : false;
				if (rec != null && !useLatest)
					return getWebForge(project, mod, rec, versionFile, useLatest);
				if (lat != null)
					return getWebForge(project, mod, lat, versionFile, true);
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

	private static String getWebForge(Project project, String mod, String version, File file, boolean isLatest) throws IOException
	{
		Version.updateVersionFile(project, file, mod, version);
		project.getLogger().info(String.format("Using %s version of " + mod, isLatest ? "Latest" : "Recommended"));
		return version;
	}
}
