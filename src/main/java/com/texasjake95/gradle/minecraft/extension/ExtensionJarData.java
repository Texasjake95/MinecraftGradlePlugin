package com.texasjake95.gradle.minecraft.extension;

import java.util.ArrayList;

import com.google.common.collect.Lists;

public class ExtensionJarData {

	private final ArrayList<Object> devFrom = Lists.newArrayList();
	private final ArrayList<Object> srcFrom = Lists.newArrayList();
	private final ArrayList<Object> apiFrom = Lists.newArrayList();

	public ArrayList<Object> getDevFrom()
	{
		return devFrom;
	}

	public ArrayList<Object> getSrcFrom()
	{
		return srcFrom;
	}

	public ArrayList<Object> getApiFrom()
	{
		return apiFrom;
	}

	public ExtensionJarData()
	{
	}

	public void addDevFrom(Object object)
	{
		devFrom.add(object);
	}

	public void addSrcFrom(Object object)
	{
		srcFrom.add(object);
	}

	public void addApiFrom(Object object)
	{
		apiFrom.add(object);
	}
}
