package com.texasjake95.gradle.minecraft;

import java.io.File;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

import com.texasjake95.commons.file.FileHelper;

public class ATExtract extends DefaultTask {
	
	private String modFile;
	private String at;
	private File fileUnpacked;
	
	public String getAt()
	{
		return at;
	}
	
	public void setAt(String at)
	{
		this.at = at;
	}
	
	public File getFileUnpacked()
	{
		return fileUnpacked;
	}
	
	public void setFileUnpacked(File fileUnpacked)
	{
		this.fileUnpacked = fileUnpacked;
	}
	
	private final File atFolder = new File(this.getProject().getBuildDir(), "/ats/");
	
	public String getModFile()
	{
		return modFile;
	}
	
	public void setModFile(String modFile)
	{
		this.modFile = modFile;
	}
	
	@TaskAction
	public void doTask()
	{
		String atLoc = fileUnpacked + "/" + at;
		String dest = atFolder.getAbsolutePath() + "/" + at;
		FileHelper.extractFolder(modFile, fileUnpacked.getAbsolutePath(), false);
		FileHelper.copyFileTo(atLoc, dest);
	}
}
