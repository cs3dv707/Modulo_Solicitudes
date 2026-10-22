package com.example.modulo_solicitudes.ui.solicitud

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolicitudFormScreen(
    viewModel: SolicitudViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var showCameraX by remember { mutableStateOf(false) }
    var showSavedConfirmation by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(showSavedConfirmation) {
        if (showSavedConfirmation) {
            snackbarHostState.showSnackbar("Solicitud registrada correctamente")
            delay(700)
            onBack()
        }
    }

    LaunchedEffect(uiState.guardadoError) {
        uiState.guardadoError?.let { snackbarHostState.showSnackbar(it) }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            showCameraX = true
        } else {
            Toast.makeText(context, "Se requiere permiso de cámara para tomar la fotografía", Toast.LENGTH_LONG).show()
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.onEvidenciaUriChange(uri.toString())
        }
    }

    if (showCameraX) {
        CameraXView(
            onPhotoCaptured = { uri ->
                viewModel.onEvidenciaUriChange(uri.toString())
                showCameraX = false
            },
            onClose = {
                showCameraX = false
            }
        )
    } else {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                TopAppBar(
                    title = { Text("Registrar Solicitud", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Volver"
                            )
                        }
                    }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .imePadding()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AnimatedVisibility(
                    visible = showSavedConfirmation,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "¡Solicitud guardada exitosamente!",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }

                // Sección 1: Información Básica
                FormSectionCard(
                    title = "1. Información de la Solicitud",
                    icon = Icons.AutoMirrored.Filled.Assignment
                ) {
                    // Título
                    OutlinedTextField(
                        value = uiState.titulo,
                        onValueChange = { viewModel.onTituloChange(it) },
                        label = { Text("Título de la Solicitud * (mínimo 5 caracteres)") },
                        placeholder = { Text("ej. Problema con conexión de red") },
                        isError = uiState.tituloError != null,
                        supportingText = {
                            AnimatedVisibility(
                                visible = uiState.tituloError != null,
                                enter = fadeIn() + expandVertically(),
                                exit = fadeOut() + shrinkVertically()
                            ) {
                                uiState.tituloError?.let {
                                    Text(text = it, color = MaterialTheme.colorScheme.error)
                                }
                            }
                        },
                        trailingIcon = {
                            if (uiState.tituloError != null || uiState.titulo.isNotBlank()) {
                                Icon(
                                    imageVector = if (uiState.tituloError == null) Icons.Default.CheckCircle
                                    else Icons.Default.ErrorOutline,
                                    contentDescription = null,
                                    tint = if (uiState.tituloError == null) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.error
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Categoría
                    OutlinedTextField(
                        value = uiState.categoria,
                        onValueChange = { viewModel.onCategoriaChange(it) },
                        label = { Text("Categoría * (mínimo 3 caracteres)") },
                        placeholder = { Text("ej. Soporte Técnico, Reclamo, Consulta") },
                        isError = uiState.categoriaError != null,
                        supportingText = {
                            AnimatedVisibility(
                                visible = uiState.categoriaError != null,
                                enter = fadeIn() + expandVertically(),
                                exit = fadeOut() + shrinkVertically()
                            ) {
                                uiState.categoriaError?.let {
                                    Text(text = it, color = MaterialTheme.colorScheme.error)
                                }
                            }
                        },
                        trailingIcon = {
                            if (uiState.categoriaError != null || uiState.categoria.isNotBlank()) {
                                Icon(
                                    imageVector = if (uiState.categoriaError == null) Icons.Default.CheckCircle
                                    else Icons.Default.ErrorOutline,
                                    contentDescription = null,
                                    tint = if (uiState.categoriaError == null) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.error
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                // Sección 2: Servicio y Descripción
                FormSectionCard(
                    title = "2. Detalle del Servicio",
                    icon = Icons.Default.Build
                ) {
                    // Servicio Relacionado
                    OutlinedTextField(
                        value = uiState.servicio,
                        onValueChange = { viewModel.onServicioChange(it) },
                        label = { Text("Servicio Relacionado * (mínimo 3 caracteres)") },
                        placeholder = { Text("ej. Facturación, Red, Equipo") },
                        isError = uiState.servicioError != null,
                        supportingText = {
                            AnimatedVisibility(
                                visible = uiState.servicioError != null,
                                enter = fadeIn() + expandVertically(),
                                exit = fadeOut() + shrinkVertically()
                            ) {
                                uiState.servicioError?.let {
                                    Text(text = it, color = MaterialTheme.colorScheme.error)
                                }
                            }
                        },
                        trailingIcon = {
                            if (uiState.servicioError != null || uiState.servicio.isNotBlank()) {
                                Icon(
                                    imageVector = if (uiState.servicioError == null) Icons.Default.CheckCircle
                                    else Icons.Default.ErrorOutline,
                                    contentDescription = null,
                                    tint = if (uiState.servicioError == null) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.error
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Descripción
                    OutlinedTextField(
                        value = uiState.descripcion,
                        onValueChange = { viewModel.onDescripcionChange(it) },
                        label = { Text("Descripción * (10 a 500 caracteres)") },
                        placeholder = { Text("Describe detalladamente la solicitud (mínimo 10 caracteres)") },
                        isError = uiState.descripcionError != null,
                        supportingText = {
                            AnimatedVisibility(
                                visible = uiState.descripcionError != null,
                                enter = fadeIn() + expandVertically(),
                                exit = fadeOut() + shrinkVertically()
                            ) {
                                uiState.descripcionError?.let {
                                    Text(text = it, color = MaterialTheme.colorScheme.error)
                                }
                            }
                        },
                        trailingIcon = {
                            if (uiState.descripcionError != null || uiState.descripcion.isNotBlank()) {
                                Icon(
                                    imageVector = if (uiState.descripcionError == null) Icons.Default.CheckCircle
                                    else Icons.Default.ErrorOutline,
                                    contentDescription = null,
                                    tint = if (uiState.descripcionError == null) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.error
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp),
                        maxLines = 5,
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                // Sección 3: Evidencia Fotográfica
                FormSectionCard(
                    title = "3. Evidencia Adjunta (Opcional)",
                    icon = Icons.Default.PhotoCamera
                ) {
                    if (!uiState.evidenciaUri.isNullOrEmpty()) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                AsyncImage(
                                    model = uiState.evidenciaUri,
                                    contentDescription = "Evidencia capturada",
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(RoundedCornerShape(12.dp)),
                                    contentScale = ContentScale.Crop
                                )

                                IconButton(
                                    onClick = { viewModel.onEvidenciaUriChange(null) },
                                    modifier = Modifier.align(Alignment.TopEnd)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Quitar foto",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    Row(modifier = Modifier.fillMaxWidth()) {
                        OutlinedButton(
                            onClick = {
                                val hasPermission = ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.CAMERA
                                ) == PackageManager.PERMISSION_GRANTED

                                if (hasPermission) {
                                    showCameraX = true
                                } else {
                                    permissionLauncher.launch(Manifest.permission.CAMERA)
                                }
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (uiState.evidenciaUri.isNullOrEmpty()) "Tomar Foto" else "Cambiar"
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        OutlinedButton(
                            onClick = {
                                galleryLauncher.launch("image/*")
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Image,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Galería")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Botón Guardar
                Button(
                    onClick = {
                        viewModel.guardarSolicitud { showSavedConfirmation = true }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    enabled = !uiState.guardando,
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        text = if (uiState.guardando) "Guardando..." else "Guardar Solicitud",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun FormSectionCard(
    title: String,
    icon: ImageVector,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
            content()
        }
    }
}
