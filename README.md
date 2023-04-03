# Stacc
Infinite* item stacking for fabric. \
https://www.curseforge.com/minecraft/mc-mods/stacc-api \
https://modrinth.com/mod/stacc-api

~~As of November 9th 2020, Stacc is now an 'api' mod. However there is no api, just include the mod inside your mod and now you can set `Item.Settings#maxCount` to any number below `int_max/2`!~~
~~As of June 3rd 2021, Stacc is now **only** an 'api' mod.~~

As of January 30th, 2023 (version 1.5.0), Stacc now includes what was previously the mod in the API. To use it, create a `data/stacc/config.json` of the following format:
```json5
{
  "item:id": 300,
  // if a lower priority resource pack changed the item count, you can change it back with 'default'
  "minecraft:item": "default",
  "minecraft:other_item": 1,
  // max supported stack size is 1 billion
  "minecraft:really_smol_item": 1000000000
}
```

# API
To use it, simply configure your item to have a max stack size of greater than 64. (`Item$Settings#maxCount`)
It is not recommended setting the stack size greater than int_max/2 (1,073,741,823)
 - 1 billion is recommended to be safe.
 - 536,870,912 is the safest power of two (to stick by Minecraft's conventions)

gradle:
```groovy
repositories {
    maven {
        url = uri("https://maven.ueaj.dev")
        // for versions lower than 1.2.0
        // url = uri("https://raw.githubusercontent.com/Devan-Kerman/Devan-Repo/master/")
    }
}

dependencies {
  // there is no api (well except for render handler), just set maxCount in Item$Settings to values over 64
  modRuntime("net.devtech:stacc:1.5.2") // you should probably include the api though
}
```
