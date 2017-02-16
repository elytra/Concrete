package io.github.elytra.concrete.invoker;

public interface Invoker {
	Object invoke(Object owner, Object... args);
}