package com.texasjake95.gradle.minecraft;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;

import org.gradle.api.Project;

import com.google.common.collect.Maps;
import com.google.common.io.ByteStreams;
import com.google.gson.Gson;

import com.texasjake95.commons.file.FileOutput;

public class MinecraftForgeVersion {

	private static final String urlFormat = "http://files.minecraftforge.net/maven/%s/%s/promotions_slim.json";

	@SuppressWarnings("unchecked")
	public static String getVersion(Project project, String mod, String group, String versionProp)
	{
		project.getLogger().debug("Attempting to get " + mod + " version");
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
				{
					return getWebForge(project, mod, rec, versionFile, useLatest);
				}
				if (lat != null)
				{
					return getWebForge(project, mod, lat, versionFile, useLatest);
				}
			}
			catch (Exception e)
			{
			}
		if (versionFile.exists())
		{
			try
			{
				String fileVersion = getVersionFromFile(project, versionFile, mod + "Version");
				if (fileVersion != null)
					return fileVersion;
			}
			catch (Exception e)
			{
			}
		}
		throw new IllegalArgumentException("Unable to connect to the internet. Please specify a " + mod + " version to use (CommandLine => -P" + versionProp + "=X.X.X.X or gradle.properties => " + versionProp + "=X.X.X.X)");
	}

	public static String getForgeVersion(Project project)
	{
		return getVersion(project, "forge", "net.minecraftforge", "forge_version");
	}

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
			project.getLogger().debug("Using user defined version of " + mod);
			return (String) project.property(versionProp);
		}
		return null;
	}

	private static String getWebForge(Project project, String mod, String version, File file, boolean isLatest) throws IOException
	{
		updateVersionFile(project, file, mod, version);
		project.getLogger().debug(String.format("Using %s version of " + mod, isLatest ? "Latest" : "Recommended"));
		return version;
	}

	public static final String getVersionFromFile(Project project, File file, String id) throws IOException
	{
		Map<String, Object> json = getJson(file);
		if (json.containsKey(id))
			return (String) json.get(id);
		return null;
	}

	@SuppressWarnings("unchecked")
	private static Map<String, Object> getJson(File file) throws IOException
	{
		Gson gson = new Gson();
		String jsonString;
		Map<String, Object> json;
		if (file.exists())
		{
			FileInputStream fs = new FileInputStream(file);
			jsonString = new String(ByteStreams.toByteArray(fs));
			try
			{
				json = gson.fromJson(jsonString, Map.class);
			}
			catch (Exception e)
			{
				json = Maps.newHashMap();
			}
		}
		else
		{
			json = Maps.newHashMap();
		}
		return json;
	}

	public static void updateVersionFile(Project project, File file, String mod, String version) throws IOException
	{
		Gson gson = new Gson();
		String jsonString;
		Map<String, Object> json = getJson(file);
		json.put(mod + "Version", version);
		jsonString = gson.toJson(json);
		FileOutput fo = new FileOutput(file);
		fo.println(jsonString);
		fo.close();
	}
}
