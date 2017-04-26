package com.elytradev.concrete.common;

import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.fml.common.Loader;

public class ShadingValidator {

	private static final String DEFAULT_PACKAGE;
	
	private static boolean validated = false;
	
	static {
		// we have to do this so the shadow plugin doesn't remap the string
		char[] c = {
				'c','o','m','.','e','l','y','t','r','a','d','e','v','.','c','o','n','c','r','e','t','e'
		};
		DEFAULT_PACKAGE = new String(c);
	}
	
	public static void ensureShaded() {
		if (validated) return;
		if (ShadingValidator.class.getName().startsWith(DEFAULT_PACKAGE)) {
			if (!((Boolean)Launch.blackboard.get("fml.deobfuscatedEnvironment"))) {
				throw new RuntimeException("Concrete is designed to be shaded and must not be left in the default package! (Offending mod: "+Loader.instance().activeModContainer().getName()+")");
			} else {
				ConcreteLog.warn("Concrete is in the default package. This is not a fatal error, as you are in a development environment, but remember to repackage it!");
			}
			validated = true;
		}
	}

}
