!pragma enable debug_dump

loop positioned ~1 ~ ~ if block ~ ~ ~ minecraft:lime_concrete {
    particle minecraft:happy_villager ~ ~1 ~ 0 0 0 1 1 normal @a
    loop positioned ~ ~ ~1 if block ~ ~ ~ minecraft:blue_concrete {
        particle minecraft:heart ~ ~1 ~ 0 0 0 1 1 normal @a
    }
}
