# 📱 Módulo de Solicitudes

Aplicación Android nativa desarrollada en **Kotlin** y **Jetpack Compose** diseñada para el registro, consulta, escaneo y gestión integral de solicitudes de soporte, reclamos y requerimientos técnicos.

---

## 🌟 Funcionalidades Principales

### 1. 📋 Gestión de Solicitudes (CRUD Completo)
- **Dashboard Estadístico:** Visualización del total de solicitudes registradas y desglose por filtros.
- **Búsqueda en Tiempo Real:** Filtro rápido por título, código único (`SOL-XXXXX`), categoría o servicio.
- **Filtro por Estado:** Clasificación ágil mediante chips (*Todas*, *Recibida*, *En Proceso*, *Resuelta*).
- **Eliminación Segura:** Confirmación modal mediante `AlertDialog` previo a la eliminación de un registro.
- **Estado Vacío Amigable:** Ilustraciones e instrucciones claras cuando no hay registros disponibles.

### 2. ✍️ Formulario Inteligente y Validación
- **Secciones Estructuradas:** Formulario organizado en bloques visuales (*1. Información de la Solicitud*, *2. Detalle del Servicio*, *3. Evidencia Adjunta*).
- **Validación en Tiempo Real:** Evaluación campo por campo (Título, Categoría, Servicio y Descripción).
- **Feedback Visual Inmediato:** Íconos de validación (`CheckCircle` y `ErrorOutline`) y mensajes de error con animaciones suaves (`AnimatedVisibility`).
- **Control de Longitud:** Restricción obligatoria de mínimo 10 caracteres en la descripción.
- **Confirmación al Guardar:** Notificación emergente animada y Snackbar al guardar exitosamente.

### 3. 📸 Recursos Nativos Integrados
- **Cámara Fotográfica (CameraX):** Captura directa de fotografías como evidencia adjunta al requerimiento.
- **Galería del Dispositivo:** Selección alternativa de imágenes mediante `ActivityResultContracts.GetContent`.
- **Lector de Código QR (CameraX + ML Kit):** Escáner integrado para la búsqueda automática de solicitudes por su código único.
- **Generación de Código QR:** Generación dinámica de QR en el detalle de cada solicitud para verificación rápida.

### 4. 🗄️ Persistencia de Datos con Room
- **Base de Datos Local:** Implementación de `RoomDatabase` y `SolicitudDao` para asegurar persistencia tras reiniciar la aplicación.
- **Estrategia de Migración:** Migración explícita de versión 1 a versión 2 (`MIGRATION_1_2`) para incorporar la columna de evidencia `evidenciaUri` sin pérdida de datos existentes.
- **Patrón Repository:** Desacoplamiento de la fuente de datos mediante `SolicitudRepository`.

### 5. 🎨 Diseño Moderno Material 3 y Animaciones
- **Tema Personalizado:** Paleta corporativa (Azul Índigo, Teal y Ámbar) con soporte para modo claro y modo oscuro.
- **Micro-interacciones y Animaciones:**
  - Animación de entrada para tarjetas de solicitudes (`slideInVertically` y `fadeIn`).
  - Redimensionamiento suave de componentes con `animateContentSize`.
  - Transición fluida de mensajes de validación y banners de confirmación.

---

## 🏗️ Arquitectura del Proyecto

El proyecto sigue el patrón de arquitectura recomendado por Google **MVVM (Model-View-ViewModel)** estructurado en capas limpias:

```text
com.example.modulo_solicitudes/
├── data/
│   ├── local/          # Room Database, Entity y DAO
│   └── repository/     # SolicitudRepository
├── ui/
│   ├── navigation/     # AppNavigation (NavHost y rutas)
│   ├── solicitud/      # Pantallas, ViewModel, UiState y Validaciones
│   │   ├── CameraXView.kt
│   │   ├── ListaSolicitudesScreen.kt
│   │   ├── QrScanner.kt / QrScannerScreen.kt
│   │   ├── SolicitudDetailScreen.kt
│   │   ├── SolicitudFormScreen.kt
│   │   ├── SolicitudUiState.kt
│   │   ├── SolicitudValidation.kt
│   │   └── SolicitudViewModel.kt
│   └── theme/          # Color, Theme y Type (Material 3)
└── MainActivity.kt
```

---

## 🧪 Pruebas Unitarias

El proyecto incluye un conjunto de pruebas unitarias automatizadas para verificar las reglas de negocio y validación de formularios en `SolicitudValidationTest.kt`.

### Ejecutar las Pruebas Localmente

Asegúrate de contar con la variable de entorno `ANDROID_HOME` o el archivo `local.properties` configurado con la ruta de tu SDK de Android.

**En Windows (PowerShell / Command Prompt):**
```powershell
.\gradlew.bat testDebugUnitTest
```

**En macOS / Linux:**
```bash
./gradlew testDebugUnitTest
```

Las pruebas verifican los siguientes escenarios:
- Rechazo de campos vacíos o únicamente con espacios en blanco.
- Rechazo de descripciones con 9 o menos caracteres.
- Aceptación de descripciones válidas (10 o más caracteres con recortes de espacios).
- Confirmación de resultado válido sin errores reportados.

---

## ⚙️ Requisitos del Sistema y Ejecución

- **Android SDK:** Min SDK 24 (Android 7.0) / Target SDK 35 (Android 15)
- **Lenguaje:** Kotlin 2.0+
- **Herramienta de Construcción:** Gradle 8.x
- **Permisos Requeridos:** `<uses-permission android:name="android.permission.CAMERA" />`

> 💡 **Nota:** Para probar las funcionalidades de Cámara y Escáner QR en el Emulador de Android Studio, se recomienda verificar que la cámara trasera esté configurada como `Webcam0` o `VirtualScene` en la configuración del dispositivo virtual (AVD).

---

## 🤝 Evidencia de Colaboración (GitHub y Trello)

Para efectos de evaluación académica:
- La evidencia de commits reales por cada integrante debe consultarse directamente en el repositorio remoto configurado en **GitHub**.
- La planificación y distribución de tareas compartidas se encuentra documentada en el tablero correspondiente de **Trello**.
