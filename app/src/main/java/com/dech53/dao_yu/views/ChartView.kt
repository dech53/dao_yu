package com.dech53.dao_yu.views

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.himanshoe.charty.common.axis.AxisConfig
import com.himanshoe.charty.common.dimens.ChartDimens
import com.himanshoe.charty.line.CurveLineChart
import com.himanshoe.charty.line.LineChart
import com.himanshoe.charty.line.config.LineConfig
import com.himanshoe.charty.line.model.LineData

@Composable
fun ChartView(
    padding: PaddingValues,
) {
    Box(
        modifier = Modifier.padding(padding).fillMaxSize()
    ) {
//        CurveLineChart(
//            lineData = listOf(
//                LineData(xValue = 10f, yValue = 10f),
//                LineData(xValue = 15f, yValue = 15f),
//                LineData(xValue = 18f, yValue = 18f),
//                LineData(xValue = 20f, yValue = 20f),
//                LineData(xValue = 25f, yValue = 25f),
//            ),
//            chartColor = MaterialTheme.colorScheme.primary,
//            lineColor = MaterialTheme.colorScheme.surfaceContainer
//        )
        LineChart(
            modifier = Modifier.fillMaxSize().padding(20.dp),
            lineData = listOf(
                LineData(xValue = 10f, yValue = 10f),
                LineData(xValue = 15f, yValue = 15f),
                LineData(xValue = 18f, yValue = 18f),
                LineData(xValue = 20f, yValue = 20f),
                LineData(xValue = 25f, yValue = 25f),
            ),
            color = MaterialTheme.colorScheme.primary,
        )
    }
}