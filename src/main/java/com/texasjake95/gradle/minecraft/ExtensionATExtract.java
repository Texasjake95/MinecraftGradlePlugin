package com.texasjake95.gradle.minecraft;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.texasjake95.commons.helpers.Checker;

public class ExtensionATExtract {
	
	private List<ATExtractData> data = new ArrayList<ATExtractData>();
	
	public void addAT(Object modName, String at, File fileUnpacked)
	{
		if (Checker.areAllNotNull(modName, at, fileUnpacked))
			data.add(new ATExtractData(modName.toString(), at, fileUnpacked));
	}
	
	public List<ATExtractData> getData()
	{
		return data;
	}
	
	public void setData(List<ATExtractData> data)
	{
		this.data = data;
	}
}