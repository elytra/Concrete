# Concrete
A compact and clean wrapper for custom payload packets in Forge.

## Depending on Concrete
Concrete must be shaded into the jar, and will throw an exception at runtime if
it is not properly shaded. The best way to do this is with the Gradle Shadow
plugin:
```gradle
plugins {
	id 'com.github.johnrengelman.shadow' version '1.2.3'
}

repositories {
	maven {
		url = 'http://unascribed.com/maven/releases'
	}
}

jar {
	classifier = 'slim'
}

shadowJar {
	classifier = ''
	relocate 'io.github.elytra.concrete', 'me.mymod.repackage.io.github.elytra.concrete'
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
	compile 'io.github.elytra:concrete:0.0.7'
	shadow 'io.github.elytra:concrete:0.0.7'
}
```

## Quick Start
Make a Message class:
```java
@ReceivedOn(Side.SERVER)
public class MyPacket extends Message {
	@MarshalledAs("u8")
	public int someUnsignedByte = 255;
	
	public MyPacket(NetworkContext ctx) {
		super(ctx);
	}
	
	// will be called on the server, as specified in the ReceivedOn annotation
	@Override
	protected void handle(EntityPlayer sender) {
		System.out.println("Hello, Concrete!");
	}
}
```

Define a NetworkContext in your mod class (or elsewhere, doesn't really matter),
and register your Message:
```java
network = NetworkContext.forChannel("MyMod");
network.register(MyPacket.class);
```

Then send it:
```java
new MyPacket(MyMod.inst.network).sendToEveryone();
```

## Differences from SimpleImpl

* Easier to use
* More convenient send methods, such as:
	* sendToAllWatching: sends the packet to everyone that can see a certain
		entity, tile entity, or block. No more giant TargetPoint declarations
		that don't even do what you really mean.
	* sendToAllAround: Of course, if you actually do want to send a packet to
		everyone in a radius, that works too.
	* sendToAllIn: Send a packet to everyone in a world, without having to
		unwrap its dimensionId.
* You don't have to write serialization/deserialization code. Concrete
	handles it all for you, including packing multiple booleans in the same
	packet into bitfields, so that 8 booleans uses 1 byte instead of 8.
* Messages are handled in themselves, so it's easier to grab the fields, and is
	not prone to mistakes like using the wrong field if you implement IMessage
	and IMessageHandler on the same class.
* Handling messages on the main thread is the default; you must annotate the
	message class with @Asynchronous if you want to process it on the network
	thread

## Differences from LambdaNetwork (and why you should use Concrete instead)

* Uses concrete classes, rather than sloppy HashMaps
* Due to the above, many common mistakes on LambdaNetwork are completely
	avoided, such as typo-ing the field name.
* Fully extensible, you can add your own Marshallers or implement Marshallable
	on your objects to easily use them.
