<img src="https://rawgit.com/elytra/Concrete/1.11.2/doc/emblem.svg" align="right" width="180px"/>

# Concrete
*A solid foundation for Elytra mods.*

Concrete is a varied set of useful *stuff* that is designed to be shaded into a
mod's jar, meaning it's not a runtime dependency and is rather included in the
dependent mod itself.

## Features

* NBT {de,}serialization utilities (common module)
* Easy networking primitives, based on a Message class (network module)
* Easy reflection primitives that take advantage of MethodHandles when possible (reflect module)
* Functional-style block classes (block module)

More coming soon!

## Depending on Concrete

Concrete *must* be shaded into the jar and will throw an exception at runtime if
it is not shaded.

Merge the following with your build.gradle to use Concrete:

```gradle
plugins {
	id 'com.github.johnrengelman.shadow' version '1.2.3'
}

repositories {
	maven {
		url = 'http://repo.elytradev.com'
	}
}

jar {
	classifier = 'slim'
}

shadowJar {
	classifier = ''
	relocate 'com.elytradev.concrete', '**me.mymod.repackage.**com.elytradev.concrete'
	configurations = [project.configurations.shadow]
}

reobf {
	shadowJar { mappingType = 'SEARGE' }
}

tasks.build.dependsOn reobfShadowJar

artifacts {
	archives shadowJar
}

dependencies {
	compile 'com.elytradev:concrete:0.2.1:common'
	shadow 'com.elytradev:concrete:0.2.1:common'

	compile 'com.elytradev:concrete:0.2.1:<module name>'
	shadow 'com.elytradev:concrete:0.2.1:<module name>'
}
```

Of course, any other method of shading will work too. The Gradle Shadow plugin
is what we recommend, though.

Alternatively, you can use the [Elytra Project Skeleton](https://github.com/elytra/skel),
which is designed for Elytra mods, but should work for any mod project.

Concrete only supports the latest version of Minecraft, but its utilities are generic
enough that they are known to sometimes work on older versions if used with caution.
