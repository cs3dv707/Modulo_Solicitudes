package com.example.modulo_solicitudes.ui.navigation

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.modulo_solicitudes.data.local.AppDatabase
import com.example.modulo_solicitudes.data.repository.SolicitudRepository
import com.example.modulo_solicitudes.ui.login.LoginScreen
import com.example.modulo_solicitudes.ui.login.LoginViewModel
import com.example.modulo_solicitudes.ui.menu.MenuScreen
import com.example.modulo_solicitudes.ui.solicitud.ListaSolicitudesScreen
import com.example.modulo_solicitudes.ui.solicitud.QrScannerScreen
import com.example.modulo_solicitudes.ui.solicitud.SolicitudDetailScreen
import com.example.modulo_solicitudes.ui.solicitud.SolicitudFormScreen
import com.example.modulo_solicitudes.ui.solicitud.SolicitudViewModel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

@Composable
fun AppNavigation(database: AppDatabase) {
    val navController = rememberNavController()
    val repository = SolicitudRepository(database.solicitudDao())
    val solicitudViewModel: SolicitudViewModel = viewModel(
        factory = SolicitudViewModel.provideFactory(repository)
    )
    val loginViewModel: LoginViewModel = viewModel()
    val loginUiState by loginViewModel.uiState.collectAsState()
    val userRole = loginUiState.loggedRole

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            LoginScreen(
                viewModel = loginViewModel,
                onLoginSuccess = {
                    navController.navigate("menu") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        composable("menu") {
            MenuScreen(
                userRole = userRole,
                onNavigateToLista = {
                    navController.navigate("lista_solicitudes")
                },
                onNavigateToQrScanner = {
                    navController.navigate("escanear_qr")
                },
                onNavigateToForm = {
                    solicitudViewModel.resetFormulario()
                    navController.navigate("formulario_solicitud")
                },
                onLogout = {
                    loginViewModel.resetState()
                    navController.navigate("login") {
                        popUpTo("menu") { inclusive = true }
                    }
                }
            )
        }

        composable("lista_solicitudes") {
            ListaSolicitudesScreen(
                userRole = userRole,
                viewModel = solicitudViewModel,
                onNavigateToForm = {
                    solicitudViewModel.resetFormulario()
                    navController.navigate("formulario_solicitud")
                },
                onSolicitudClick = { solicitudId ->
                    navController.navigate("detalle_solicitud/$solicitudId")
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable("formulario_solicitud") {
            SolicitudFormScreen(
                viewModel = solicitudViewModel,
                onBack = {
                    solicitudViewModel.resetFormulario()
                    navController.popBackStack()
                }
            )
        }

        composable("escanear_qr") {
            val context = LocalContext.current
            val coroutineScope = rememberCoroutineScope()

            QrScannerScreen(
                onQrDetected = { qrContent ->
                    coroutineScope.launch {
                        val codigoBuscado = qrContent.trim()
                        val solicitudEncontrada = repository.getSolicitudByCodigo(codigoBuscado).firstOrNull()

                        if (solicitudEncontrada != null) {
                            navController.popBackStack()
                            navController.navigate("detalle_solicitud/${solicitudEncontrada.id}")
                        } else {
                            Toast.makeText(
                                context,
                                "Código QR detectado ($codigoBuscado), pero no corresponde a ninguna solicitud registrada.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = "detalle_solicitud/{solicitudId}",
            arguments = listOf(
                navArgument("solicitudId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getLong("solicitudId") ?: 0L
            SolicitudDetailScreen(
                userRole = userRole,
                solicitudId = id,
                viewModel = solicitudViewModel,
                onNavigateToEdit = {
                    navController.navigate("formulario_solicitud")
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
