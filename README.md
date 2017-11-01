<img src="https://rawgit.com/elytra/Concrete/1.11.2/doc/emblem.svg" align="right" width="180px"/>
 
# Concrete
*A solid foundation for Elytra mods.*

Concrete is a varied set of useful *stuff* that is designed to be shaded into a
mod's jar, meaning it's not a runtime dependency and is rather included in the
dependent mod itself.

## Modules

### Common
Contains small utility classes that most Concrete mods will want to use, as well
as features shared between all modules.

### Block
Contains a functional builder for Block classes.

### Inventory
Contains a Predicate-based validated IItemHandler, and associated IInventory.
Also contains extremely useful replacements for Container and GuiContainer.

### Network
**Depends on Reflect**

Contains an easy-to-use network message framework built on top of plugin
message packets, similar to SimpleImpl. Replacement for the now-deprecated
LambdaNetwork.

### ResGen
**Depends on Reflect**

Contains a fake resource pack that automatically generates the most common
kinds of block and item models. Done without ASM and without any nasty hacks.

It's strongly recommended to put a README.md in your models/block and models/item
assets directory explaining your use of ResGen. An example is provided in
`doc/resgen-README.md`.

### Reflect
Contains a set of method invokers, field accessors, and class instanciators that
take advantage of MethodHandles when they are available.

### Config
Adds a dead-simple annotation-based configuration system, similar to the Network
module.

### UtilPackets
**Depends on Network**

Contains a set of pre-baked Message implementations, for painless use
with a NetworkContext from the Network package.

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
	relocate 'com.elytradev.concrete', '<me.mymod>.repackage.com.elytradev.concrete'
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
	deobfCompile 'com.elytradev.concrete:concrete-common:0.3.4-SNAPSHOT'
	shadow 'com.elytradev.concrete:concrete-common:0.3.4-SNAPSHOT'

	deobfCompile 'com.elytradev.concrete:concrete-<module name>:0.3.4-SNAPSHOT'
	shadow 'com.elytradev.concrete:concrete-<module name>:0.3.4-SNAPSHOT'
}
```

Of course, any other method of shading will work too. The Gradle Shadow plugin
is what we recommend, though.

Alternatively, you can use the [Elytra Project Skeleton](https://github.com/elytra/skel),
which is designed for Elytra mods, but should work for any mod project.

Concrete only supports the latest version of Minecraft, but its utilities are generic
enough that they are known to sometimes work on older versions if used with caution.
