# ThunderNet WoW ⚡ - WebView Application

![ThunderNet WoW ](https://img.shields.io/badge/ThunderNet-WoW-0078D7?style=for-the-badge&logo=blizzard&logoColor=white.svg)
![Android 5.0 ](https://img.shields.io/badge/Android-5.0%2B-3DDC84?style=for-the-badge&logo=android&logoColor=white.svg)
![Kotlin 1.9.0 ](https://img.shields.io/badge/Kotlin-1.9.0-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white.svg)
![Licencia Personal](https://img.shields.io/badge/License-Custom-blue?style=for-the-badge.svg)

</div>

## 📖 Descripción General

Aplicación WebView estilo BattleNet para servidores World of Warcraft privados. Interfaz profesional con pantalla de carga animada y funciones avanzadas.


## ✨ Características

🎮 Interfaz de Usuario

- ✅ Pantalla de carga estilo BattleNet con animación circular personalizada
- ✅ Interfaz limpia y profesional sin barras de navegación intrusivas
- ✅ Modo oscuro/claro con inyección CSS automática
- ✅ Pantalla completa en orientación vertical exclusiva
- ✅ Icono personalizado TN estilo BattleNet

🌐 Funciones WebView

- ✅ WebView optimizado para servidores WoW
- ✅ JavaScript habilitado para funcionalidad completa
- ✅ Gestión de cookies persistente para mantener sesiones
- ✅ Zoom con gestos (pinch to zoom) habilitado
- ✅ Descargas de archivos permitidas (.apk, .zip, .pdf, etc.)
- ✅ Notificaciones push compatibles (Firebase configurable)
- ✅ Auto-guardado de contraseñas del navegador

⚙️ Funciones Avanzadas

- ✅ Menú contextual con 5 opciones:
  - 🔄 Actualizar: Recargar la página actual
  - ⚙️ Configurar URL: Cambiar dirección del servidor
  - 🌙 Modo Oscuro/Claro: Alternar tema
  - 🧹 Limpiar Caché: Borrar datos temporales
  - ℹ️ Acerca de: Información de la aplicación
- ✅ Configuración persistente de URL personalizada
- ✅ Detección de conexión a internet
- ✅ Manejo de errores con interfaz de reintento
- ✅ Animaciones fluidas y transiciones suaves

## 📦 Requisitos

- Sistema Operativo: Android 5.0 Lollipop (API 21) o superior
- Memoria RAM: Mínimo 2GB recomendado
- Almacenamiento: 50MB libres
- Conexión: Internet para cargar contenido web
- Permisos: Almacenamiento para descargas

## 🚀 Instalación

Método 1: APK Directo

1. Descarga el archivo ThunderNet.apk desde Releases
2. Habilita "Orígenes desconocidos" en ajustes de seguridad
3. Instala la aplicación
4. Abre y disfruta

Método 2: Desde Código Fuente

```bash
# Clonar el repositorio
git clone https://github.com/tu-usuario/ThunderNet.git
cd ThunderNet

# Abrir en Android Studio
# Esperar a que se sincronicen las dependencias
# Conectar dispositivo o usar emulador
# Ejecutar la aplicación (Shift + F10)
```

## ⚙️ Configuración

URL por Defecto

```kotlin
private val DEFAULT_URL = "http://172.16.1.1"
```

Cambiar URL del Servidor

1. Toca el botón de menú (⏵) en la esquina superior derecha
2. Selecciona "Configurar URL"
3. Ingresa la nueva dirección (ej: http://192.168.1.100)
4. Toca "Guardar"
5. La aplicación se recargará automáticamente

Restaurar URL Predeterminada

1. Ve a Configurar URL
2. Toca "Restaurar predeterminada"
3. Confirmar para volver a http://172.16.1.1

## 🎮 Uso

Navegación Básica

- Deslizar hacia abajo: Actualizar la página
- Pellizcar para alejar/acercar: Control de zoom
- Toque largo: Menú contextual del navegador
- Botón de menú: Acceso a funciones avanzadas

Funciones del Menú

Icono Función Descripción
🔄 Actualizar Recarga la página actual
⚙️ Configurar URL Cambia la dirección del servidor
🌙 Modo Oscuro/Claro Alterna entre temas claro y oscuro
🧹 Limpiar Caché Borra datos temporales y cookies
ℹ️ Acerca de Muestra información de la aplicación

Modo Oscuro

1. Activa desde el menú principal
2. La aplicación inyecta CSS automáticamente
3. Funciona incluso en páginas sin soporte nativo
4. Se mantiene la configuración entre sesiones

## 📁 Estructura del Proyecto

```
ThunderNet/
├── app/
│   ├── src/main/
│   │   ├── java/com/thundernet/web/
│   │   │   └── MainActivity.kt          # Lógica principal
│   │   ├── res/
│   │   │   ├── layout/
│   │   │   │   ├── splash_layout.xml    # Pantalla de carga
│   │   │   │   └── activity_main.xml    # Layout principal
│   │   │   ├── drawable/
│   │   │   │   ├── loading_circle.xml   # Animación de carga
│   │   │   │   ├── ic_tn_logo.xml       # Logo TN
│   │   │   │   └── splash_background.xml# Fondo del splash
│   │   │   ├── menu/
│   │   │   │   └── web_menu.xml         # Menú de opciones
│   │   │   ├── values/
│   │   │   │   ├── colors.xml           # Paleta de colores
│   │   │   │   ├── strings.xml          # Textos de la app
│   │   │   │   └── themes.xml           # Temas claro/oscuro
│   │   │   └── xml/
│   │   │       └── network_security_config.xml
│   │   └── AndroidManifest.xml
│   └── build.gradle.kts
├── gradle/
├── build.gradle.kts
├── settings.gradle.kts
├── proguard-rules.pro
└── README.md
```

## 🛠️ Tecnologías Utilizadas

Tecnología Versión Propósito
Kotlin 1.9.0 Lenguaje principal
Android SDK 34 Desarrollo nativo
Android WebView 1.9.0 Navegador integrado
Material Components 1.10.0 Diseño UI/UX
Firebase Messaging 23.3.1 Notificaciones push
AndroidX Preferences 1.2.1 Configuración persistente

Dependencias principales:

```gradle
dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.webkit:webkit:1.9.0")
    implementation("androidx.preference:preference-ktx:1.2.1")
    implementation("com.google.firebase:firebase-messaging:23.3.1")
}
```

## 🔐 Permisos

Permiso Propósito Obligatorio
INTERNET Conexión a servidores web ✅ Sí
ACCESS_NETWORK_STATE Detectar estado de red ✅ Sí
WRITE_EXTERNAL_STORAGE Guardar archivos descargados ⚠️ Condicional
POST_NOTIFICATIONS Mostrar notificaciones push ⚠️ Android 13+
MANAGE_EXTERNAL_STORAGE Gestión avanzada de archivos ❌ Opcional

## 🔧 Compilación

Requisitos previos:

- Android Studio Electric Eel o superior
- JDK 17 o superior
- Android SDK Platform 34
- Dispositivo físico o emulador Android 5.0+

Pasos de compilación:

```bash
# 1. Clonar repositorio
git clone https://github.com/tu-usuario/ThunderNet.git

# 2. Abrir en Android Studio
# 3. Esperar sincronización de Gradle
# 4. Configurar keystore para release (opcional)

# 5. Generar APK de debug
# Build > Build Bundle(s) / APK(s) > Build APK(s)

# 6. Generar APK firmado para release
# Build > Generate Signed Bundle / APK

# 7. Instalar en dispositivo
adb install app/build/outputs/apk/debug/app-debug.apk
```

Configuración de Firebase (Opcional):

1. Crea proyecto en Firebase Console
2. Añade aplicación Android con tu package name
3. Descarga google-services.json
4. Colócalo en app/
5. Descomenta dependencias en build.gradle.kts

## 🎨 Personalización

Cambiar Logo de la App

1. Reemplaza ic_tn_logo.xml en res/drawable/
2. Actualiza ic_launcher.png en carpetas mipmap-*
3. Modifica dimensiones en splash_layout.xml

Cambiar Colores Principales

Editar res/values/colors.xml:

```xml
<color name="thundernet_blue">#0078D7</color>    <!-- Azul principal -->
<color name="background_dark">#0A0A0A</color>     <!-- Fondo oscuro -->
<color name="text_primary">#FFFFFF</color>        <!-- Texto principal -->
```

Modificar Animación de Carga

Editar res/drawable/loading_circle.xml:

```xml
<animated-rotate
    android:duration="800"                        <!-- Velocidad -->
    android:pivotX="50%"
    android:pivotY="50%">
    <shape android:thickness="6dp">               <!-- Grosor -->
        <stroke android:color="#FF5722" />        <!-- Color -->
    </shape>
</animated-rotate>
```

Agregar Nuevas Opciones al Menú

1. Añadir item en res/menu/web_menu.xml
2. Implementar lógica en MainActivity.kt
3. Agregar icono en res/drawable/

## 🔍 Troubleshooting

Problemas Comunes y Soluciones:

Problema Causa Probable Solución
Página no carga Sin internet / URL incorrecta Verificar conexión y URL
Error de certificado HTTP sin SSL Agregar dominio a network_security_config.xml
Sin zoom Controles ocultos Habilitar displayZoomControls
Cookies no guardan Configuración de WebView Verificar domStorageEnabled
Descargas fallan Permisos de almacenamiento Otorgar permisos manualmente
Modo oscuro no funciona CSS injection falla Verificar JavaScript habilitado

Logs de Depuración:

```bash
# Ver logs de la aplicación
adb logcat -s ThunderNet

# Limpiar caché de la app
adb shell pm clear com.thundernet.web

# Reiniciar aplicación
adb shell am force-stop com.thundernet.web
adb shell am start -n com.thundernet.web/.MainActivity
```

## 🤝 Contribuciones

¡Las contribuciones son bienvenidas! Sigue estos pasos:

1. Fork el repositorio
2. Crea una rama para tu feature:
   ```bash
   git checkout -b feature/NuevaFuncionalidad
   ```
3. Commit tus cambios:
   ```bash
   git commit -m "Añadir: Nueva funcionalidad"
   ```
4. Push a la rama:
   ```bash
   git push origin feature/NuevaFuncionalidad
   ```
5. Abre un Pull Request

Guía de Estilo:

· Usa Kotlin para nueva lógica
· Sigue Material Design 3 para UI
· Documenta funciones complejas
· Añade comentarios en inglés

## 📄 Licencia

```
Copyright 2026+ ThunderNet WoW

Este software es proporcionado "TAL CUAL", sin garantía de ningún tipo,
expresa o implícita. El uso de este software es bajo tu propio riesgo.

RESERVADO PARA USO EN SERVIDORES WORLD OF WARCRAFT PRIVADOS.
NO AFILIADO A BLIZZARD ENTERTAINMENT.
```

Restricciones:

- ✅ Uso personal y comunitario permitido
- ✅ Modificaciones permitidas con atribución
- ❌ Distribución comercial prohibida
- ❌ Uso en servidores oficiales de Blizzard prohibido

## 📞 Contacto y Soporte

Canales de Soporte:

- GitHub Issues: Reportar bug
- Email: devthundercore@gmail.com

Información del Proyecto:

- Versión Actual: 1.0.0
- Última Actualización: Diciembre 2025
- Mantenido por: Equipo ThunderNet
- Estado: Activo y en desarrollo

## 🌟 Reconocimientos

- BattleNet UI - Por la inspiración del diseño
- Android Developer Community - Por el soporte constante
- World of Warcraft Community - Por hacer esto posible

## 🔮 Roadmap Futuro

- Integración con API de personajes WoW
- Sistema de noticias push automáticas
- Chat integrado en la aplicación
- Estadísticas del servidor en tiempo real
- Soporte para múltiples servidores
- Widgets para la pantalla de inicio
- Soporte para tabletas Android

Nota: Esta aplicación está diseñada específicamente para la comunidad de servidores World of Warcraft privados. No está afiliada, respaldada ni autorizada por Blizzard Entertainment.

<div align="center">

"Por la Horda! Por la Alianza! Por ThunderNet!" 🛡️⚔️

```
© 2026+ ThunderNet WoW - Todos los derechos reservados para la comunidad.
```

</div>
