package com.texasjake95.gradle.minecraft;

import java.io.File;

import org.gradle.api.Project;

import com.texasjake95.gradle.ProjectHelper;

public class AfterThought {

	private String name;
	private String version;
	private String classifer;
	private boolean hasAT = false;
	private String atName;
	private String unPackDir;
	private String soruceClassifer;

	public AfterThought(String name, String version, String classifer, String soruceClassifer)
	{
		this.name = name;
		this.version = version;
		this.classifer = classifer;
		this.soruceClassifer = soruceClassifer;
	}

	public AfterThought(String name, String version, String classifer, String soruceClassifer, String atName, String unPackDir)
	{
		this(name, version, classifer, soruceClassifer);
		this.atName = atName;
		this.unPackDir = unPackDir;
		this.hasAT = true;
	}

	public String getAtName()
	{
		return this.atName;
	}

	public String getClassifer()
	{
		return this.classifer;
	}

	public File getFile(Project project)
	{
		return ProjectHelper.getFile(project, "afterThought", this.name, this.version, this.classifer);
	}

	public String getName()
	{
		return this.name;
	}

	public String getSoruceClassifer()
	{
		return this.soruceClassifer;
	}

	public String getUnPackDir()
	{
		return this.unPackDir;
	}

	public String getVersion()
	{
		return this.version;
	}

	public boolean hasAT()
	{
		return this.hasAT;
	}

	public void setAtName(String atName)
	{
		this.atName = atName;
	}

	public void setClassifer(String classifer)
	{
		this.classifer = classifer;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public void setSoruceClassifer(String soruceClassifer)
	{
		this.soruceClassifer = soruceClassifer;
	}

	public void setUnPackDir(String unPackDir)
	{
		this.unPackDir = unPackDir;
	}

	public void setVersion(String version)
	{
		this.version = version;
	}
}
