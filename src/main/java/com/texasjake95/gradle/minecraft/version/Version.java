package com.texasjake95.gradle.minecraft.version;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;

import org.gradle.api.Project;

import com.google.common.collect.Maps;
import com.google.common.io.ByteStreams;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.texasjake95.commons.file.FileOutput;

public class Version {

	public static String getForgeVersion(Project project)
	{
		return MinecraftForgeVersion.getVersion(project, "forge", "net.minecraftforge", "forge_version");
	}

	public static final String getVersionFromFile(Project project, File file, String id) throws IOException
	{
		Map<String, Object> json = getJson(file);
		if (json.containsKey(id))
			return (String) json.get(id);
		return null;
	}

	public static void updateVersionFile(Project project, File file, String mod, String version) throws IOException
	{
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String jsonString;
		Map<String, Object> json = getJson(file);
		json.put(mod + "Version", version);
		jsonString = gson.toJson(json);
		FileOutput fo = new FileOutput(file);
		fo.println(jsonString);
		fo.close();
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
			json = Maps.newHashMap();
		return json;
	}
}
