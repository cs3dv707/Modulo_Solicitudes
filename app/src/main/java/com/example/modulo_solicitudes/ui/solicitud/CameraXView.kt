package com.example.modulo_solicitudes.ui.solicitud

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.core.resolutionselector.AspectRatioStrategy
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import kotlinx.coroutines.delay
import java.io.File

@Composable
fun CameraXView(
    onPhotoCaptured: (Uri) -> Unit,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var lensFacing by remember { mutableIntStateOf(CameraSelector.LENS_FACING_BACK) }
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    var isCapturing by remember { mutableStateOf(false) }
    var cameraInitError by remember { mutableStateOf<String?>(null) }
    var retryTrigger by remember { mutableIntStateOf(0) }

    val previewView = remember { PreviewView(context) }

    LaunchedEffect(lensFacing, retryTrigger) {
        cameraInitError = null

        // Reintento de inicialización por si el emulador tarda en responder
        var attempt = 0
        var boundSuccess = false

        while (attempt < 3 && !boundSuccess) {
            attempt++
            val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
            try {
                val cameraProvider = cameraProviderFuture.get()
                val availableCameras = cameraProvider.availableCameraInfos

                if (availableCameras.isNotEmpty()) {
                    val selector = when {
                        lensFacing == CameraSelector.LENS_FACING_BACK && cameraProvider.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA) -> {
                            CameraSelector.DEFAULT_BACK_CAMERA
                        }
                        lensFacing == CameraSelector.LENS_FACING_FRONT && cameraProvider.hasCamera(CameraSelector.DEFAULT_FRONT_CAMERA) -> {
                            CameraSelector.DEFAULT_FRONT_CAMERA
                        }
                        cameraProvider.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA) -> {
                            CameraSelector.DEFAULT_BACK_CAMERA
                        }
                        cameraProvider.hasCamera(CameraSelector.DEFAULT_FRONT_CAMERA) -> {
                            CameraSelector.DEFAULT_FRONT_CAMERA
                        }
                        else -> {
                            CameraSelector.Builder().addCameraFilter { infos ->
                                if (infos.isNotEmpty()) listOf(infos.first()) else emptyList()
                            }.build()
                        }
                    }

                    val resolutionSelector = ResolutionSelector.Builder()
                        .setAspectRatioStrategy(
                            AspectRatioStrategy(
                                AspectRatio.RATIO_4_3,
                                AspectRatioStrategy.FALLBACK_RULE_AUTO
                            )
                        )
                        .build()

                    val preview = Preview.Builder()
                        .setResolutionSelector(resolutionSelector)
                        .build().also {
                            it.setSurfaceProvider(previewView.surfaceProvider)
                        }

                    val capture = ImageCapture.Builder()
                        .setResolutionSelector(resolutionSelector)
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .build()

                    imageCapture = capture

                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        selector,
                        preview,
                        capture
                    )
                    boundSuccess = true
                } else {
                    if (attempt < 3) delay(500)
                }
            } catch (e: Exception) {
                Log.e("CameraXView", "Intento $attempt fallido al vincular CameraX", e)
                if (attempt < 3) delay(500) else {
                    cameraInitError = e.localizedMessage ?: "No se detectó ningún dispositivo de cámara (Available cameras: 0)"
                }
            }
        }

        if (!boundSuccess && cameraInitError == null) {
            cameraInitError = "El sistema Android no reporta cámaras habilitadas (Available cameras: 0)."
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        if (cameraInitError != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Cámara No Habilitada en Emulador",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.DarkGray)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Error detectado:",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                        Text(
                            text = cameraInitError!!,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.LightGray
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "💡 Para habilitar la cámara en Android Studio:",
                            style = MaterialTheme.typography.titleSmall,
                            color = Color.Yellow
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "1. En Android Studio abre 'Device Manager'.\n" +
                                    "2. Edita tu emulador (ícono lápiz).\n" +
                                    "3. Clic en 'Show Advanced Settings'.\n" +
                                    "4. En 'Camera Back' o 'Front' selecciona 'Webcam0' o 'VirtualScene'.\n" +
                                    "5. Guarda y reinicia el emulador.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    OutlinedButton(onClick = { retryTrigger++ }) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Reintentar")
                    }

                    Button(onClick = onClose) {
                        Text("Regresar al Formulario")
                    }
                }
            }
        } else {
            // Vista previa de la cámara en vivo
            AndroidView(
                factory = { previewView },
                modifier = Modifier.fillMaxSize()
            )

            // Botón de Cerrar (superior izquierda)
            IconButton(
                onClick = onClose,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp)
                    .background(Color.Black.copy(alpha = 0.5f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Cerrar cámara",
                    tint = Color.White
                )
            }

            // Barra inferior con Controles (Cambiar Cámara y Disparador)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp, start = 24.dp, end = 24.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                // Botón para alternar cámara frontal / trasera
                IconButton(
                    onClick = {
                        lensFacing = if (lensFacing == CameraSelector.LENS_FACING_BACK) {
                            CameraSelector.LENS_FACING_FRONT
                        } else {
                            CameraSelector.LENS_FACING_BACK
                        }
                    },
                    modifier = Modifier
                        .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                        .size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Cameraswitch,
                        contentDescription = "Cambiar cámara",
                        tint = Color.White
                    )
                }

                // Botón de Disparo (Tomar Foto)
                IconButton(
                    onClick = {
                        val capture = imageCapture
                        if (capture != null && !isCapturing) {
                            isCapturing = true
                            tomarFotografia(
                                context = context,
                                imageCapture = capture,
                                onPhotoCaptured = { uri ->
                                    isCapturing = false
                                    onPhotoCaptured(uri)
                                },
                                onError = { errorMsg ->
                                    isCapturing = false
                                    Toast.makeText(context, "Error al capturar: $errorMsg", Toast.LENGTH_LONG).show()
                                }
                            )
                        }
                    },
                    modifier = Modifier
                        .size(72.dp)
                        .background(if (isCapturing) Color.Gray else Color.White, CircleShape)
                        .border(4.dp, MaterialTheme.colorScheme.primary, CircleShape)
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(if (isCapturing) Color.LightGray else Color.White, CircleShape)
                    )
                }
            }
        }
    }
}

private fun tomarFotografia(
    context: Context,
    imageCapture: ImageCapture,
    onPhotoCaptured: (Uri) -> Unit,
    onError: (String) -> Unit
) {
    val folder = File(context.filesDir, "evidencias")
    if (!folder.exists()) folder.mkdirs()

    val file = File(folder, "evidencia_${System.currentTimeMillis()}.jpg")
    val outputOptions = ImageCapture.OutputFileOptions.Builder(file).build()

    imageCapture.takePicture(
        outputOptions,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                val savedUri = Uri.fromFile(file)
                onPhotoCaptured(savedUri)
            }

            override fun onError(exception: ImageCaptureException) {
                exception.printStackTrace()
                onError(exception.message ?: "Error desconocido al capturar imagen")
            }
        }
    )
}
