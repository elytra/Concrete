<img src="https://rawgit.com/elytra/Concrete/1.11.2/doc/emblem.svg" align="right" width="180px"/>

# Concrete
*A solid foundation for Elytra mods.*

Concrete is a varied set of useful *stuff* that is designed to be shaded into a
mod's jar, meaning it's not a runtime dependency.

## Features

* Easy networking primitives, based on a Message class
* Easy reflection primitives that take advantage of MethodHandles when possible

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
	compile 'com.elytradev:concrete:0.1.0'
	shadow 'com.elytradev:concrete:0.1.0'
}
```

Of course, any other method of shading will work too. The Gradle Shadow plugin
is what we recommend, though.