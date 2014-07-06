package com.texasjake95.gradle.minecraft;

import java.io.File;

import org.gradle.api.Project;

public class AfterThought {

	
    public String getName()
    {
    	return name;
    }

	
    public void setName(String name)
    {
    	this.name = name;
    }

	
    public String getVersion()
    {
    	return version;
    }

	
    public void setVersion(String version)
    {
    	this.version = version;
    }

	
    public String getClassifer()
    {
    	return classifer;
    }

	
    public void setClassifer(String classifer)
    {
    	this.classifer = classifer;
    }

	
    public String getAtName()
    {
    	return atName;
    }

	
    public void setAtName(String atName)
    {
    	this.atName = atName;
    }

	
    public String getUnPackDir()
    {
    	return unPackDir;
    }

	
    public void setUnPackDir(String unPackDir)
    {
    	this.unPackDir = unPackDir;
    }

	private String name;
	private String version;
	private String classifer;
	private boolean hasAT = false;
	private String atName;
	private String unPackDir;

	public AfterThought(String name, String version, String classifer)
	{
		this.name = name;
		this.version = version;
		this.classifer = classifer;
	}

	public AfterThought(String name, String version, String classifer, String atName, String unPackDir)
	{
		this(name, version, classifer);
		this.atName = atName;
		this.unPackDir = unPackDir;
		this.hasAT = true;
	}

	public File getFile(Project project)
	{
		return DependencyManager.getFile(project, name, version, classifer);
	}

	public boolean hasAT()
	{
		return this.hasAT;
	}
}
