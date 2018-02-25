/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2018:
 * 	Una Thompson (unascribed),
 * 	Isaac Ellingson (Falkreon),
 * 	Jamie Mansfield (jamierocks),
 * 	Alex Ponebshek (capitalthree),
 * 	and contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.elytradev.concrete.common;

import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.fml.common.Loader;

public final class ShadingValidator {

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
			if (!((Boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment"))) {
				throw new RuntimeException("Concrete is designed to be shaded and must not be left in the default package! (Offending mod: " + Loader.instance().activeModContainer().getName() + ")");
			} else {
				ConcreteLog.warn("Concrete is in the default package. This is not a fatal error, as you are in a development environment, but remember to repackage it!");
			}
			validated = true;
		}
	}

	private ShadingValidator() {}
}
