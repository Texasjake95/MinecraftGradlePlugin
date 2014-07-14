package com.texasjake95.gradle.minecraft.extension;

import java.util.ArrayList;
import java.util.List;

import com.texasjake95.gradle.minecraft.extension.data.ModFolderData;

public class ExtensionModSetup {

	private List<ModFolderData> data = new ArrayList<ModFolderData>();

	public void addMod(Object obj)
	{
		if (obj != null)
			this.data.add(new ModFolderData(obj.toString()));
	}

	public List<ModFolderData> getData()
	{
		return this.data;
	}
}
