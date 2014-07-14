package com.texasjake95.gradle.minecraft.extension.data;

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
	private final File atFolder = new File(this.getProject().getBuildDir(), "/ats/");

	@TaskAction
	public void doTask()
	{
		FileHelper.extractFolder(this.modFile, this.fileUnpacked.getAbsolutePath(), false);
		if (this.modFile.endsWith("\\.jar"))
		{
			JarFile jar = null;
			try
			{
				jar = new JarFile(this.modFile);
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
						String maniFestAT = this.fileUnpacked + "/META-INF/" + at;
						String maniFestDest = this.atFolder.getAbsolutePath() + "/" + at;
						FileHelper.copyFileTo(maniFestAT, maniFestDest);
					}
				}
			}
		}
		if (this.at != null)
		{
			String atLoc = this.fileUnpacked + "/" + this.at;
			String dest = this.atFolder.getAbsolutePath() + "/" + this.at;
			FileHelper.copyFileTo(atLoc, dest);
		}
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

	public void setAt(String at)
	{
		this.at = at;
	}

	public void setFileUnpacked(File fileUnpacked)
	{
		this.fileUnpacked = fileUnpacked;
	}

	public void setModFile(String modFile)
	{
		this.modFile = modFile;
	}
}
