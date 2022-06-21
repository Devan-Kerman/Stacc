# Stacc
infinite stacking for fabric

# API
~~As of November 9th 2020, Stacc is now an 'api' mod. However there is no api, just include the mod inside your mod and now you can set `Item.Settings#maxCount` to any number below `int_max/2`!~~

As of June 3rd 2021, Stacc is now **only** an 'api' mod. 
To use it, simply configure your item to have a max stack size of greater than 64. (Item$Settings#maxCount)
It is not recommended setting the stack size greater than int_max/2 (1,073,741,823).

gradle:
```groovy
repositories {
    maven {
        url = uri("https://storage.googleapis.com/devan-maven/")
        // for versions lower than 1.2.0
        // url = uri("https://raw.githubusercontent.com/Devan-Kerman/Devan-Repo/master/")
    }
}

dependencies {
  // there is no api (well except for render handler), just set maxCount in Item$Settings to values over 64
  modRuntime(include("net.devtech:Stacc:1.3.4"))
}
```
