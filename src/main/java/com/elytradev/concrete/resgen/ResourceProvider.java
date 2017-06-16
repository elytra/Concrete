/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2017:
 * 	William Thompson (unascribed),
 * 	Isaac Ellingson (Falkreon),
 * 	Jamie Mansfield (jamierocks),
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

package com.elytradev.concrete.resgen;

import java.io.InputStream;

/**
 * Base for all resource providers, manages results for a type of resource.
 */
public abstract class ResourceProvider {

	protected final String modID;
	protected ConcreteResourcePack resourcePack;

	public ResourceProvider(ConcreteResourcePack resourcePack) {
		this.modID = resourcePack.modID;
		this.resourcePack = resourcePack;
	}

	/**
	 * Can this provider provide a resource for the given name?
	 *
	 * @param name the resource name.
	 * @return true if the required resource can be provided, false otherwise.
	 */
	public abstract boolean canProvide(String name);

	/**
	 * Provides an input stream for the given resource name.
	 *
	 * @param name the resource name
	 * @return the input stream for the specified resource.
	 */
	public abstract InputStream provide(String name);

}
