# Stacc
infinite stacking for fabric

## Usage
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
