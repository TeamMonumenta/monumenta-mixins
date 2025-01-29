!pragma enable debug_dump

run as @e {
    tellraw @s "meow"
    run as @s[tag=SomeTag] {
        break 2
    }

    tellraw @s "hi"
}

run as @e {
    tellraw @s "meow 2"
}