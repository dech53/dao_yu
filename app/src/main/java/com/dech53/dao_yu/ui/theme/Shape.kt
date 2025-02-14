package com.dech53.dao_yu.ui.theme

import androidx.compose.foundation.shape.GenericShape
import androidx.compose.ui.geometry.Rect


//胶囊形状
val capsuleShape = GenericShape { size, _ ->
    val radius = size.height / 2
    moveTo(radius, 0f)
    lineTo(size.width - radius, 0f)
    arcTo(
        rect = Rect(
            size.width - size.height,
            0f,
            size.width,
            size.height,
        ),
        startAngleDegrees = -90f,
        sweepAngleDegrees = 180f,
        forceMoveTo = false,
    )
    lineTo(radius,size.height)
    arcTo(
        rect = Rect(
            0f, 0f, size.height, size.height
        ),
        startAngleDegrees = 90f,
        sweepAngleDegrees = 180f,
        forceMoveTo = false,
    )
    close()
}