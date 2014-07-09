package com.texasjake95.gradle.minecraft;

import java.util.ArrayList;
import java.util.List;

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
