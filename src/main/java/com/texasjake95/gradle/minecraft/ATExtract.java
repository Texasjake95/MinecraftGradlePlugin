package com.texasjake95.gradle.minecraft;

import java.io.File;
import java.io.IOException;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

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
		FileHelper.extractFolder(modFile, fileUnpacked.getAbsolutePath(), false);
		if (modFile.endsWith("\\.jar"))
		{
			JarFile jar = null;
			try
			{
				jar = new JarFile(modFile);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			if (jar != null)
			{
				Manifest manifest = null;
				try
				{
					manifest = jar.getManifest();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
				if (manifest != null)
				{
					String atList = manifest.getMainAttributes().getValue("FMLAT");
					for (String at : atList.split(" "))
					{
						String maniFestAT = fileUnpacked + "/META-INF/" + at;
						String maniFestDest = atFolder.getAbsolutePath() + "/" + at;
						FileHelper.copyFileTo(maniFestAT, maniFestDest);
					}
				}
			}
		}
		if (at != null)
		{
			String atLoc = fileUnpacked + "/" + at;
			String dest = atFolder.getAbsolutePath() + "/" + at;
			FileHelper.copyFileTo(atLoc, dest);
		}
	}
}
