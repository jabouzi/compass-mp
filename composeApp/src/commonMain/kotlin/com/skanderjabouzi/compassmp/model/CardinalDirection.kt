package com.skanderjabouzi.compassmp.model

import compass_mp.composeapp.generated.resources.Res
import compass_mp.composeapp.generated.resources.cardinal_direction_east
import compass_mp.composeapp.generated.resources.cardinal_direction_north
import compass_mp.composeapp.generated.resources.cardinal_direction_northeast
import compass_mp.composeapp.generated.resources.cardinal_direction_northwest
import compass_mp.composeapp.generated.resources.cardinal_direction_south
import compass_mp.composeapp.generated.resources.cardinal_direction_southeast
import compass_mp.composeapp.generated.resources.cardinal_direction_southwest
import compass_mp.composeapp.generated.resources.cardinal_direction_west
import org.jetbrains.compose.resources.StringResource

enum class CardinalDirection(val labelResourceId: StringResource) {
    NORTH(Res.string.cardinal_direction_north),
    NORTHEAST(Res.string.cardinal_direction_northeast),
    EAST(Res.string.cardinal_direction_east),
    SOUTHEAST(Res.string.cardinal_direction_southeast),
    SOUTH(Res.string.cardinal_direction_south),
    SOUTHWEST(Res.string.cardinal_direction_southwest),
    WEST(Res.string.cardinal_direction_west),
    NORTHWEST(Res.string.cardinal_direction_northwest)
}
