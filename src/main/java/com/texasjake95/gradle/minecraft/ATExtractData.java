package com.texasjake95.gradle.minecraft;

import java.io.File;

public class ATExtractData {

	private String modFile;
	private String at;
	private File fileUnpacked;
	private String taskName;

	ATExtractData(String modFile, File fileUnpacked)
	{
		this.modFile = modFile;
		this.fileUnpacked = fileUnpacked;
		this.taskName = modFile.substring(0, modFile.indexOf("-")) + "AT";
	}

	ATExtractData(String modFile, String at, File fileUnpacked)
	{
		this.modFile = modFile;
		this.at = at;
		this.fileUnpacked = fileUnpacked;
		if (at.contains("_"))
			this.taskName = at.split("_")[0] + "AT";
		else
			this.taskName = at.split("\\.")[0] + "AT";
	}

	public String getAt()
	{
		return this.at;
	}

	public File getFileUnpacked()
	{
		return this.fileUnpacked;
	}

	public String getModFile()
	{
		return this.modFile;
	}

	public String getTaskName()
	{
		return this.taskName;
	}
}
