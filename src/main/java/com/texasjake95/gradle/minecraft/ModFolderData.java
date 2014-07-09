package com.texasjake95.gradle.minecraft;

public class ModFolderData {
	
	public ModFolderData(String mod)
	{
		this.mod = mod;
	}
	
	private String mod;
	
	public String getMod()
	{
		return mod;
	}
	
	public void setMod(String mod)
	{
		this.mod = mod;
	}
	
	public String toString()
	{
		return this.mod;
	}
}
