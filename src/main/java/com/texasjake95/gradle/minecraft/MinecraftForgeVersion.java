package com.texasjake95.gradle.minecraft;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;

import org.gradle.api.Project;

import com.google.common.io.ByteStreams;
import com.google.gson.Gson;

import com.texasjake95.commons.file.FileInput;
import com.texasjake95.commons.file.FileOutput;

public class MinecraftForgeVersion {
	
	@SuppressWarnings("unchecked")
	public static String getVersion(Project project)
	{
		project.getLogger().debug("Attempting to get forge version");
		String version = getUserForge(project);
		if (version != null)
			return version;
		File versionFile = new File(project.getBuildDir().getAbsolutePath() + "/version.info");
		if (!project.getGradle().getStartParameter().isOffline())
			try
			{
				URL url = new URL("http://files.minecraftforge.net/maven/net/minecraftforge/forge/promotions_slim.json");
				InputStream con = url.openStream();
				String data = new String(ByteStreams.toByteArray(con));
				con.close();
				Map<String, Object> json = new Gson().fromJson(data, Map.class);
				// String homepage = (String)json.get("homepage");
				Map<String, String> promos = (Map<String, String>) json.get("promos");
				String rec = promos.get(project.property("minecraft_version") + "-recommended");
				String lat = promos.get(project.property("minecraft_version") + "-latest");
				boolean useLatest = project.hasProperty("useLatest") ? getPropertyAsBoolean(project, "useLatest") : false;
				if (rec != null && !useLatest)
				{
					return getWebForge(project, rec, versionFile, useLatest);
				}
				if (lat != null)
				{
					return getWebForge(project, lat, versionFile, useLatest);
				}
			}
			catch (Exception e)
			{
			}
		if (versionFile.exists())
		{
			FileInput fi = new FileInput(versionFile);
			ArrayList<String> lines = fi.getFileLines();
			if (lines.size() > 0)
			{
				project.getLogger().debug("Using last retrieved version of forge");
				return lines.get(0);
			}
		}
		throw new IllegalArgumentException("Unable to connect to the internet. Please specify a forge version to use (CommandLine => -Pforge_version=X.X.X.X or gradle.properties => forge_version=X.X.X.X)");
	}
	
	private static <T> boolean getPropertyAsBoolean(Project project, String propName)
	{
		Object prop = project.property(propName);
		if (prop instanceof Boolean)
			return (boolean) prop;
		return false;
	}
	
	private static String getUserForge(Project project)
	{
		if (project.hasProperty("forge_version"))
		{
			project.getLogger().debug("Using user defined version of forge");
			return (String) project.property("forge_version");
		}
		return null;
	}
	
	private static String getWebForge(Project project, String version, File file, boolean isLatest) throws IOException
	{
		FileOutput fo = new FileOutput(new File(project.getBuildDir().getAbsolutePath() + "/version.info"));
		fo.println(version);
		fo.close();
		project.getLogger().debug(String.format("Using %s version of forge", isLatest ? "Latest" : "Recommended"));
		return version;
	}
}
