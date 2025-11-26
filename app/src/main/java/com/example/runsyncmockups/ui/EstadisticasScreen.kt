package com.example.runsyncmockups.ui

import BottomBarView
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.runsyncmockups.ui.components.ButtonModel
import com.example.runsyncmockups.ui.model.UserViewModel
import kotlin.math.roundToInt

@Composable
fun EstadisticaScreen(
    navController: NavController,
    userVm: UserViewModel = viewModel()
) {
    val stats by userVm.userStats.collectAsState()

    // Cargar stats desde Firebase al entrar a la pantalla (por si ya hay datos guardados)
    LaunchedEffect(Unit) {
        userVm.loadUserStats()
    }

    // Formateo de tiempo desde minutos guardados en stats
    val totalMinutes = stats.timeFromStepsMin.roundToInt()
    val hours = totalMinutes / 60
    val minutes = totalMinutes % 60

    // Datos para grárica de barras (solo distancia actual por ahora)
    val sessionDistanceKm = stats.distanceFromStepsKm.toFloat().coerceAtLeast(0f)
    val barrasData: List<Pair<String, Float>> = listOf("Hoy" to sessionDistanceKm)

    // Datos para gráfica de ritmo (min/km)
    val paceList: List<Float> =
        if (stats.distanceFromStepsKm > 0.0) {
            val pace = (stats.timeFromStepsMin / stats.distanceFromStepsKm).toFloat()
            listOf(pace)
        } else {
            emptyList()
        }

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

            // Contador de pasos + envío de datos al usuario
            StepCounterView(userVm = userVm)

            Spacer(Modifier.height(16.dp))

            // Subtitulo
            Text(
                text = "Esta sesión",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color.DarkGray,
                modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
            )

            // Row de resumen con datos de stats
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Distancia", style = MaterialTheme.typography.labelLarge, color = Color.DarkGray)
                    Text(
                        String.format("%.2f km", stats.distanceFromStepsKm),
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
                Column {
                    Text("Tiempo", style = MaterialTheme.typography.labelLarge, color = Color.DarkGray)
                    Text(
                        "${hours}h ${minutes}m",
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
                Column {
                    Text("Calorías", style = MaterialTheme.typography.labelLarge, color = Color.DarkGray)
                    Text(
                        "${stats.caloriesFromSteps} kcal",
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            // Gráfica de barras de km (usando distancia actual)
            Text(
                text = "Distancia recorrida (km)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color.DarkGray,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Barras(
                dataPoints = barrasData
            )

            Spacer(Modifier.height(48.dp))

            // Gráfico curvo de ritmo
            Text(
                text = "Ritmo promedio (min/km)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color.DarkGray,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Curvo(
                dataPoints = paceList,
                color = Color(0xFFFFB74D)
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
        val barWidth = if (dataPoints.isNotEmpty()) chartWidth / (dataPoints.size * 2) else 0f
        val spacing = barWidth * 0.5f

        // Líneas horizontales de guía
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
            color = android.graphics.Color.GRAY
            textSize = 10.sp.toPx()
            textAlign = android.graphics.Paint.Align.LEFT
        }

        // Etiquetas eje Y (puedes ajustarlo si quieres)
        val yLabels = listOf("0.0", "2.5", "5.0", "7.5", "10.0", "12.5", "15.0")
        for (i in yLabels.indices) {
            val y = chartHeight * (1 - i.toFloat() / (yLabels.size - 1))
            drawContext.canvas.nativeCanvas.drawText(
                yLabels[i],
                10.dp.toPx(),
                y + 5.dp.toPx(),
                textPaint
            )
        }

        // Barras
        if (maxValue > 0 && barWidth > 0f) {
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

        // Eje X
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

        // Etiquetas eje X (días / "Hoy")
        if (barWidth > 0f) {
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

        // Eje Y
        drawLine(
            color = Color.Black,
            start = Offset(40.dp.toPx(), 10.dp.toPx()),
            end = Offset(40.dp.toPx(), chartHeight + 10.dp.toPx()),
            strokeWidth = 2.dp.toPx()
        )

        // Eje X
        drawLine(
            color = Color.Black,
            start = Offset(40.dp.toPx(), chartHeight + 10.dp.toPx()),
            end = Offset(size.width - 20.dp.toPx(), chartHeight + 10.dp.toPx()),
            strokeWidth = 2.dp.toPx()
        )

        val yPaint = android.graphics.Paint().apply {
            this.color = android.graphics.Color.GRAY
            textSize = 11.sp.toPx()
            textAlign = android.graphics.Paint.Align.RIGHT
        }

        // Etiquetas del eje Y (minutos por km, puedes ajustar)
        val yLabels = listOf("8", "7", "6", "5", "4")
        yLabels.forEachIndexed { index, label ->
            val y = 10.dp.toPx() + (chartHeight / (yLabels.size - 1)) * index
            drawContext.canvas.nativeCanvas.drawText(
                label,
                35.dp.toPx(),
                y + 5.dp.toPx(),
                yPaint
            )
        }

        val xPaint = android.graphics.Paint().apply {
            this.color = android.graphics.Color.BLACK
            textSize = 11.sp.toPx()
            textAlign = android.graphics.Paint.Align.CENTER
        }

        // Etiquetas del eje X (p.ej. "1" = sesión actual)
        dataPoints.forEachIndexed { index, _ ->
            val x = 40.dp.toPx() + index * stepX
            drawContext.canvas.nativeCanvas.drawText(
                "${index + 1}",
                x,
                chartHeight + 30.dp.toPx(),
                xPaint
            )
        }

        // Línea curva
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

        // Puntos en cada dato
        normalizedPoints.forEach { point ->
            drawCircle(
                color = color,
                radius = 4.dp.toPx(),
                center = point
            )
        }
    }
}

@Composable
fun StepCounterView(userVm: UserViewModel = viewModel()) {
    val context = LocalContext.current
    val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    val stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)
    var stepCount by remember { mutableStateOf(0) }

    // Parámetros recomendados
    val strideLengthMeters = 0.75f   // longitud media de paso
    val avgSpeedKmH = 5f            // velocidad media caminando
    val kcalPerKm = 35f             // kcal por km aprox

    val distanceKm = remember(stepCount) {
        (stepCount * strideLengthMeters) / 1000f
    }
    val totalMinutes = remember(distanceKm) {
        (if (avgSpeedKmH > 0f) distanceKm / avgSpeedKmH * 60f else 0f).toInt()
    }
    val hours = totalMinutes / 60
    val minutes = totalMinutes % 60
    val kcal = remember(distanceKm) {
        distanceKm * kcalPerKm
    }

    // Cada vez que cambian los pasos, actualizamos stats del usuario (Firebase)
    LaunchedEffect(stepCount) {
        userVm.updateStatsFromSteps(stepCount)
    }

    // Launcher para solicitar el permiso
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted && stepSensor != null) {
            registerStepListener(sensorManager, stepSensor) { stepCount++ }
        } else {
            println("Permiso de reconocimiento de actividad denegado o sensor nulo")
        }
    }

    // Verifica el permiso al iniciar el componente
    LaunchedEffect(Unit) {
        val permission = android.Manifest.permission.ACTIVITY_RECOGNITION
        if (ContextCompat.checkSelfPermission(context, permission)
            == PackageManager.PERMISSION_GRANTED && stepSensor != null
        ) {
            registerStepListener(sensorManager, stepSensor) { stepCount++ }
        } else if (stepSensor != null) {
            permissionLauncher.launch(permission)
        } else {
            println("Sensor de pasos no disponible (emulador probablemente)")
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Pasos: $stepCount",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )


        if (stepSensor == null) {
            Spacer(Modifier.height(8.dp))
            ButtonModel({stepCount++}, Modifier.padding(1.dp), { Text("Simular Paso") })
        }
    }
}

fun registerStepListener(
    sensorManager: SensorManager,
    stepSensor: Sensor?,
    onStepDetected: () -> Unit
) {
    if (stepSensor == null) {
        println("Sensor de pasos no disponible")
        return
    }

    val listener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent?) {
            if (event?.sensor?.type == Sensor.TYPE_STEP_DETECTOR) {
                onStepDetected()
            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    }

    sensorManager.registerListener(listener, stepSensor, SensorManager.SENSOR_DELAY_UI)
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewEstadisticaScreen() {
    MaterialTheme {
        EstadisticaScreen(navController = rememberNavController())
    }
}
