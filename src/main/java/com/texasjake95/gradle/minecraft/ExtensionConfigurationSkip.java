package com.texasjake95.gradle.minecraft;

import java.util.HashSet;

import com.google.common.collect.Sets;

public class ExtensionConfigurationSkip {
	
	private HashSet<String> dataConfig = Sets.newHashSet();
	private HashSet<String> dataDepend = Sets.newHashSet();
	
	public ExtensionConfigurationSkip()
	{
		dataConfig.add("minecraft");
		dataConfig.add("userDevPackageDepConfig");
		dataConfig.add("minecraftDeps");
		dataConfig.add("minecraftNatives");
		dataConfig.add("archives");
		dataDepend.add("launchwrapper");
		dataDepend.add("asm-debug-all");
		dataDepend.add("scala-library");
		dataDepend.add("scala-compiler");
		dataDepend.add("jopt-simple");
		dataDepend.add("lzma");
		dataDepend.add("realms");
		dataDepend.add("commons-compress");
		dataDepend.add("httpclient");
		dataDepend.add("commons-logging");
		dataDepend.add("httpcore");
		dataDepend.add("vecmath");
		dataDepend.add("trove4j");
		dataDepend.add("codecjorbis");
		dataDepend.add("codecwav");
		dataDepend.add("libraryjavasound");
		dataDepend.add("librarylwjglopenal");
		dataDepend.add("soundsystem");
		dataDepend.add("netty-all");
		dataDepend.add("guava");
		dataDepend.add("commons-lang3");
		dataDepend.add("commons-io");
		dataDepend.add("commons-codec");
		dataDepend.add("jinput");
		dataDepend.add("jutils");
		dataDepend.add("gson");
		dataDepend.add("authlib");
		dataDepend.add("log4j-api");
		dataDepend.add("log4j-core");
		dataDepend.add("lwjgl");
		dataDepend.add("lwjgl_util");
		dataDepend.add("twitch");
		dataDepend.add("forgeSrc");
		dataDepend.add("forgeBin");
	}
	
	public void skipConfig(String config)
	{
		dataConfig.add(config);
	}
	
	public void skipDependency(String config)
	{
		this.dataDepend.add(config);
	}
	
	public boolean containsConfig(String string)
	{
		return this.dataConfig.contains(string);
	}
	
	public boolean containsDepend(String string)
	{
		return this.dataDepend.contains(string);
	}
}
