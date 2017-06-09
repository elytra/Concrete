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
	 * @return
	 */
	public abstract InputStream provide(String name);

}
