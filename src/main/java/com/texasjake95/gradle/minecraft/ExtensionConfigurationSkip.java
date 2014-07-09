package com.texasjake95.gradle.minecraft;

import java.util.HashSet;

import com.google.common.collect.Sets;

public class ExtensionConfigurationSkip {

	private HashSet<String> dataConfig = Sets.newHashSet();
	private HashSet<String> dataDepend = Sets.newHashSet();

	public ExtensionConfigurationSkip()
	{
		this.dataConfig.add("minecraft");
		this.dataConfig.add("userDevPackageDepConfig");
		this.dataConfig.add("minecraftDeps");
		this.dataConfig.add("minecraftNatives");
		this.dataConfig.add("archives");
		this.dataDepend.add("launchwrapper");
		this.dataDepend.add("asm-debug-all");
		this.dataDepend.add("scala-library");
		this.dataDepend.add("scala-compiler");
		this.dataDepend.add("jopt-simple");
		this.dataDepend.add("lzma");
		this.dataDepend.add("realms");
		this.dataDepend.add("commons-compress");
		this.dataDepend.add("httpclient");
		this.dataDepend.add("commons-logging");
		this.dataDepend.add("httpcore");
		this.dataDepend.add("vecmath");
		this.dataDepend.add("trove4j");
		this.dataDepend.add("codecjorbis");
		this.dataDepend.add("codecwav");
		this.dataDepend.add("libraryjavasound");
		this.dataDepend.add("librarylwjglopenal");
		this.dataDepend.add("soundsystem");
		this.dataDepend.add("netty-all");
		this.dataDepend.add("guava");
		this.dataDepend.add("commons-lang3");
		this.dataDepend.add("commons-io");
		this.dataDepend.add("commons-codec");
		this.dataDepend.add("jinput");
		this.dataDepend.add("jutils");
		this.dataDepend.add("gson");
		this.dataDepend.add("authlib");
		this.dataDepend.add("log4j-api");
		this.dataDepend.add("log4j-core");
		this.dataDepend.add("lwjgl");
		this.dataDepend.add("lwjgl_util");
		this.dataDepend.add("twitch");
		this.dataDepend.add("forgeSrc");
		this.dataDepend.add("forgeBin");
	}

	public boolean containsConfig(String string)
	{
		return this.dataConfig.contains(string);
	}

	public boolean containsDepend(String string)
	{
		return this.dataDepend.contains(string);
	}

	public void skipConfig(String config)
	{
		this.dataConfig.add(config);
	}

	public void skipDependency(String config)
	{
		this.dataDepend.add(config);
	}
}
