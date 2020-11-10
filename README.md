# Stacc
infinite stacking for fabric

# API
As of November 9th 2020, Stacc is now an 'api' mod. However there is no api, just include the mod inside your mod and now you can set `Item.Settings#maxCount` to any number below `int_max/2`!

gradle:
```
repositories {
	maven {
		url = 'https://raw.githubusercontent.com/Devan-Kerman/Devan-Repo/master/'
	}
}

dependencies {
  include "net.devtech:Stacc:1.0.0"
}
```

## Mod Usage
Stacc also has a mod version, that uses datapack configs and is what is on curseforge

make a datapack with the a file in the data dir under
stacc/stacc.properties

The theoretical maximum value is 2,147,483,647! But it's recommended to not go above 1 billion, 2 billion max.
```properties
# minecraft objects don't need namespaces
stone=5
# but u can do it anyways
minecraft\:iron_block=5
# to change the default max stack value
default=64
```
