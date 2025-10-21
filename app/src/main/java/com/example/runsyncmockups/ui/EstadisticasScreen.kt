package com.example.runsyncmockups.ui

import BottomBarView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color // Color de Compose
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.color
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@Composable
fun EstadisticaScreen(navController: NavController) {
    Scaffold(
        bottomBar = { BottomBarView(navController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .padding(paddingValues)
                .padding(top = 60.dp, bottom = 40.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top
        ) {
            // Título
            Text(
                text = "Actividad",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(16.dp))

            // Subtitulo
            Text(
                text = "Esta semana",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color.DarkGray,
                modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
            )

            // Row de resumen semanal
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Distancia", style = MaterialTheme.typography.labelLarge, color = Color.DarkGray)
                    Text("12 km", fontWeight = FontWeight.Bold, color = Color.Black)
                }
                Column {
                    Text("Tiempo", style = MaterialTheme.typography.labelLarge, color = Color.DarkGray)
                    Text("1h 30m", fontWeight = FontWeight.Bold, color = Color.Black)
                }
                Column {
                    Text("Calorías", style = MaterialTheme.typography.labelLarge, color = Color.DarkGray)
                    Text("180 kcal", fontWeight = FontWeight.Bold, color = Color.Black)
                }
            }

            Spacer(Modifier.height(32.dp))

            // Grafica de barras de km
            Text(
                text = "Distancia recorrida (km)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color.DarkGray,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Barras(
                dataPoints = listOf(
                    Pair("L", 9f),
                    Pair("M", 10f),
                    Pair("X", 11f),
                    Pair("J", 12f),
                    Pair("V", 13f),
                    Pair("S", 14f),
                    Pair("D", 15f)
                )
            )

            Spacer(Modifier.height(48.dp))

            // Grafico curvo de ritmo
            Text(
                text = "Ritmo promedio (min/km)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color.DarkGray,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Curvo(
                dataPoints = listOf(6.2f, 5.8f, 5.5f, 6.0f, 5.7f, 5.9f, 6.1f),
                color = Color(0xFFFFB74D) // Color de Compose
            )
        }
    }
}

@Composable
fun Barras(
    dataPoints: List<Pair<String, Float>>,
    modifier: Modifier = Modifier
) {
    val maxValue = dataPoints.maxOfOrNull { it.second } ?: 0f
    val barColor = Color(0xFFFF5722)

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        val chartHeight = size.height - 40.dp.toPx()
        val chartWidth = size.width - 40.dp.toPx()
        val barWidth = chartWidth / (dataPoints.size * 2)
        val spacing = barWidth * 0.5f

        // Dibujar líneas horizontales de guía
        val gridLines = 5
        for (i in 0..gridLines) {
            val y = chartHeight * (1 - i.toFloat() / gridLines)
            drawLine(
                color = Color.LightGray,
                start = Offset(30.dp.toPx(), y),
                end = Offset(size.width - 10.dp.toPx(), y),
                strokeWidth = 1.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f))
            )
        }

        val textPaint = android.graphics.Paint().apply {
            color = android.graphics.Color.GRAY // Color de Android nativo
            textSize = 10.sp.toPx()
            textAlign = android.graphics.Paint.Align.LEFT
        }

        // Dibujar etiquetas del eje Y (kilómetros)
        val yLabels = listOf("0.0", "2.6", "5.2", "7.8", "10.4", "13.0", "15.6")
        for (i in yLabels.indices) {
            val y = chartHeight * (1 - i.toFloat() / (yLabels.size - 1))
            drawContext.canvas.nativeCanvas.drawText(
                yLabels[i],
                10.dp.toPx(),
                y + 5.dp.toPx(),
                textPaint
            )
        }

        // Dibujar barras
        if (maxValue > 0) { // Evitar división por cero si no hay datos
            dataPoints.forEachIndexed { index, (_, value) ->
                val barHeight = (value / maxValue) * chartHeight
                val x = 30.dp.toPx() + index * (barWidth + spacing) + spacing

                drawRect(
                    color = barColor,
                    topLeft = Offset(x, chartHeight - barHeight),
                    size = androidx.compose.ui.geometry.Size(barWidth, barHeight)
                )
            }
        }

        // Dibujar eje X
        drawLine(
            color = Color.Black,
            start = Offset(30.dp.toPx(), chartHeight),
            end = Offset(size.width - 10.dp.toPx(), chartHeight),
            strokeWidth = 2.dp.toPx()
        )

        val labelPaint = android.graphics.Paint().apply {
            color = android.graphics.Color.BLACK
            textSize = 12.sp.toPx()
            textAlign = android.graphics.Paint.Align.CENTER
        }

        // Dibujar etiquetas de días debajo del eje X
        dataPoints.forEachIndexed { index, (label, _) ->
            val x = 30.dp.toPx() + index * (barWidth + spacing) + spacing + barWidth / 2
            drawContext.canvas.nativeCanvas.drawText(
                label,
                x,
                chartHeight + 25.dp.toPx(),
                labelPaint
            )
        }
    }
}

@Composable
fun Curvo(
    dataPoints: List<Float>,
    color: Color,
    modifier: Modifier = Modifier
) {
    if (dataPoints.isEmpty()) return // No dibujar nada si no hay datos

    val maxValue = dataPoints.maxOrNull() ?: 0f
    val minValue = dataPoints.minOrNull() ?: 0f

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp)
    ) {
        val chartHeight = size.height - 60.dp.toPx()
        val chartWidth = size.width - 60.dp.toPx()
        val stepX = if (dataPoints.size > 1) chartWidth / (dataPoints.size - 1) else 0f

        // Dibujar eje Y
        drawLine(
            color = Color.Black,
            start = Offset(40.dp.toPx(), 10.dp.toPx()),
            end = Offset(40.dp.toPx(), chartHeight + 10.dp.toPx()),
            strokeWidth = 2.dp.toPx()
        )

        // Dibujar eje X
        drawLine(
            color = Color.Black,
            start = Offset(40.dp.toPx(), chartHeight + 10.dp.toPx()),
            end = Offset(size.width - 20.dp.toPx(), chartHeight + 10.dp.toPx()),
            strokeWidth = 2.dp.toPx()
        )

        val yPaint = android.graphics.Paint().apply {
            this.color = android.graphics.Color.GRAY // Color de Android nativo
            textSize = 11.sp.toPx()
            textAlign = android.graphics.Paint.Align.RIGHT
        }

        // Etiquetas del eje Y (minutos por km)
        val yLabels = listOf("7", "6", "5", "4", "3")
        yLabels.forEachIndexed { index, label ->
            val y = 10.dp.toPx() + (chartHeight / (yLabels.size - 1)) * index
            drawContext.canvas.nativeCanvas.drawText(
                label,
                35.dp.toPx(), // Ajustado para que no se pegue al eje
                y + 5.dp.toPx(),
                yPaint
            )
        }

        val xPaint = android.graphics.Paint().apply {
            this.color = android.graphics.Color.BLACK
            textSize = 11.sp.toPx()
            textAlign = android.graphics.Paint.Align.CENTER
        }

        // Etiquetas del eje X (kilómetros)
        dataPoints.forEachIndexed { index, _ ->
            val x = 40.dp.toPx() + index * stepX
            drawContext.canvas.nativeCanvas.drawText(
                "${index + 1}",
                x,
                chartHeight + 30.dp.toPx(),
                xPaint
            )
        }

        // Dibujar línea curva
        val path = Path()
        val range = if (maxValue - minValue == 0f) 1f else maxValue - minValue

        val normalizedPoints = dataPoints.mapIndexed { index, value ->
            val x = 40.dp.toPx() + index * stepX
            val normalizedValue = (value - minValue) / range
            val y = chartHeight + 10.dp.toPx() - (normalizedValue * chartHeight)
            Offset(x, y)
        }

        path.moveTo(normalizedPoints.first().x, normalizedPoints.first().y)

        for (i in 0 until normalizedPoints.size - 1) {
            val current = normalizedPoints[i]
            val next = normalizedPoints[i + 1]
            val controlX = (current.x + next.x) / 2

            path.cubicTo(
                controlX, current.y,
                controlX, next.y,
                next.x, next.y
            )
        }

        drawPath(
            path = path,
            color = color,
            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
        )

        // Dibujar puntos en cada dato
        normalizedPoints.forEach { point ->
            drawCircle(
                color = color,
                radius = 4.dp.toPx(),
                center = point
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewEstadisticaScreen() {
    MaterialTheme {
        EstadisticaScreen(navController = rememberNavController())
    }
}


