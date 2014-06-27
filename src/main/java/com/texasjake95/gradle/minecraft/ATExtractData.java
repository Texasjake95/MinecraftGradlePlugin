package com.texasjake95.gradle.minecraft;

import java.io.File;

public class ATExtractData {
	
	private String modFile;
	private String at;
	private File fileUnpacked;
	private String taskName;
	
	ATExtractData(String modFile, String at, File fileUnpacked)
	{
		this.modFile = modFile;
		this.at = at;
		this.fileUnpacked = fileUnpacked;
		if (at.contains("_"))
		{
			taskName = at.split("_")[0] + "AT";
		}
		else
		{
			taskName = at.split("\\.")[0] + "AT";
		}
	}
	
	public String getModFile()
	{
		return modFile;
	}
	
	public String getAt()
	{
		return at;
	}
	
	public File getFileUnpacked()
	{
		return fileUnpacked;
	}
	
	public String getTaskName()
	{
		return this.taskName;
	}
}
