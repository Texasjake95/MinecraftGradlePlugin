package com.texasjake95.gradle.minecraft;

public class ModFolderData {

	private String mod;

	public ModFolderData(String mod)
	{
		this.mod = mod;
	}

	public String getMod()
	{
		return this.mod;
	}

	public void setMod(String mod)
	{
		this.mod = mod;
	}

	@Override
	public String toString()
	{
		return this.mod;
	}
}
