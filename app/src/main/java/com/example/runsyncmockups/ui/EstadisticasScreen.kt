package com.example.runsyncmockups.ui

import BottomBarView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.runsyncmockups.ui.Datos
import androidx.navigation.compose.rememberNavController
import com.github.jaikeerthick.composable_graphs.bar.BarGraph
import com.github.jaikeerthick.composable_graphs.bar.data.BarData
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.sp

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
                modifier = Modifier.padding(bottom = 8.dp)
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
            Barras()

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
                color = Color (0xFFFFB74D)
            )
        }
    }
}


@Composable
fun Barras() {
    val datos = listOf(
        Datos("L", 9f),
        Datos("M", 10f),
        Datos("X", 11f),
        Datos("J", 12f),
        Datos("V", 13f),
        Datos("S", 14f),
        Datos("D", 15f)
    )


    val barras = datos.map { dato ->
        BarData(
            x = dato.dia,
            y = dato.km.toDouble(),
        )
    }

    BarGraph(
        data = barras,
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp),
        barColor = Color(0xFFFF5722),
        showXLabel = true, // Muestra los dias
        showYLabel = true, // Muestra los km
        yAxisTextSize = 10.sp,
        xAxisTextSize = 10.sp,
        yValueTextSize = 10.sp,
    )
}


@Composable
fun Curvo(
    dataPoints : List<Float>,
    color: Color = Color(0xFFFFB74D),
    height: Dp = 200.dp
){
    val textMeasurer = rememberTextMeasurer()

    val xKmLabels = listOf(3f, 6f, 9f, 12f, 15f, 18f, 21f)

    val numPoints = dataPoints.size
    val totalKm = xKmLabels.lastOrNull() ?: 1f // Último valor de la lista de Km

    Canvas(modifier = Modifier
        .fillMaxWidth()
        .height(height)
        .padding(start = 35.dp, end = 10.dp, bottom = 20.dp)
    ){
        val usableWidth = size.width
        val usableHeight = size.height

        // CÁLCULO DE ESCALA X: Basado en el total de Km
        val xStep = usableWidth / (numPoints - 1)

        // CÁLCULO DE ESCALA Y: Basado en los valores de Ritmo
        val yMax = dataPoints.maxOrNull() ?: 1f
        val yMin = dataPoints.minOrNull() ?: 0f
        val yRange = yMax - yMin
        val yScale = usableHeight / if (yRange == 0f) yMax else yRange

        // Dibujar eje x

        drawLine( // Línea del Eje X
            color = Color.DarkGray,
            start = Offset(0f, usableHeight),
            end = Offset(usableWidth, usableHeight),
            strokeWidth = 2.dp.toPx()
        )


        xKmLabels.forEachIndexed { index, km ->
            val xPos = index * xStep
            drawText(
                textMeasurer = textMeasurer,
                text = km.toInt().toString(),
                topLeft = Offset(xPos - 10.dp.toPx(), usableHeight + 5.dp.toPx()),
                style = TextStyle(
                    color = Color.DarkGray,
                    fontSize = 12.sp
                )
            )
        }

        // Dibujar eje  y
        val numLabelsY = 4
        for (i in 0 until numLabelsY) {
            val valueY = yMin + (yRange / (numLabelsY - 1)) * i
            val yPos = usableHeight - ((valueY - yMin) * yScale)

            // Dibuja la línea de referencia horizontal (Grid)
            drawLine(
                color = Color.LightGray.copy(alpha = 0.5f),
                start = Offset(0f, yPos),
                end = Offset(usableWidth, yPos),
                strokeWidth = 1.dp.toPx()
            )

            // Dibuja el texto del valor
            drawText(
                textMeasurer = textMeasurer,
                text = String.format("%.1f", valueY),
                topLeft = Offset(-30.dp.toPx(), yPos - 15f),
                style = TextStyle(
                    color = Color.DarkGray,
                    fontSize = 12.sp
                )
            )

        }

        // Dibujar la curva
        val path = Path()

        dataPoints.forEachIndexed { index, value ->
            val x = index * xStep // Posición X basada en el índice (equivalente a un Km)
            val y = usableHeight - ((value - yMin) * yScale)

            if (index == 0) path.moveTo(x, y)
            else {
                val prevX = (index - 1) * xStep
                val prevY = usableHeight - ((dataPoints[index - 1] - yMin) * yScale)
                val controlX1 = prevX + (x - prevX) / 2

                path.cubicTo(controlX1, prevY, controlX1, y, x, y)

                // Dibujar el punto actual
                drawCircle(
                    color = color,
                    radius = 4.dp.toPx(),
                    center = Offset(x, y)
                )
            }
        }

        drawPath(
            path = path,
            color = color,
            style = Stroke(
                width = 4.dp.toPx()
            )
        )
    }
}

@Preview
@Composable
fun EstadisticaScreenPreview(){
    val navController = rememberNavController()

    EstadisticaScreen(navController)
}

