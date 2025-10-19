# Biblioteca Duoc

Aplicación Android (Jetpack Compose) para gestionar libros: listar, crear/editar y ver detalles, con almacenamiento local (Room), navegación Compose y acceso a cámara y dictado por voz.

## Integrantes
- Nicolás Cisternas
- Nicolas Salas
- Sección: 012V / Profesor: VICTOR ANDRES TOLEDO MIRANDA

## Contexto y objetivos
La app resuelve la necesidad de registrar y consultar libros de forma offline con una UI simple y responsiva.

## Tecnologías
- Kotlin + Jetpack Compose (Material 3)
- Navigation Compose
- Room (persistencia local)
- ViewModel + StateFlow (gestión de estado)
- Coil (carga de imágenes)
- Recursos nativos: Cámara (TakePicture + FileProvider), Micrófono (Speech-to-Text)

## Arquitectura
app/
├─ data/local/ # Room (AppDatabase, BookDao, BookEntity, Mappers)
├─ repository/ # BooksRepository, RoomBooksRepository
├─ di/ # RepositoryProvider
├─ model/ # Book
├─ viewmodel/ # BooksViewModel, BookFormViewModel
├─ ui/screens/ # BooksScreen, BookDetailsScreen, BookFormScreen, BookSummaryScreen
├─ navigation/ # AppNav, Routes
└─ res/xml/ # file_paths.xml (FileProvider)

## Funcionalidades implementadas
- **Navegación** entre lista ↔ detalle ↔ formulario (Compose Navigation).
- **Formulario con validación** desacoplada en ViewModel (mensajes por campo).
- **Animaciones** (`Crossfade`) en transición de estados.
- **Persistencia local** (Room): crear, listar libros.
- **Cámara**: captura de portada con `TakePicture()` y `FileProvider`.
- **Dictado por voz** (micrófono) para descripción usando `RecognizerIntent`.

## Requisitos de permisos
En `AndroidManifest.xml`:
- `android.permission.CAMERA`
- `android.permission.RECORD_AUDIO`

## Configuración y ejecución
1. Android Studio Ladybug+ | Gradle 8.x | SDK 34
2. Abrir el proyecto (`settings.gradle.kts`).
3. Sincronizar Gradle.
4. Ejecutar en emulador o dispositivo real (API 24+).

## Pruebas
- Alta/Baja/Modificación en Room.
- Captura de imagen y visualización con Coil.
- Dictado por voz para descripción.
- Validaciones por campo (ver mensajes de error).

## Planificación (Trello)
- Tablero: https://trello.com/invite/b/68f194322685f980227a92ca/ATTIa9c94634523425810075804849fa9bedBF44324B/ev-parcial-2
- Listas: Por hacer, En proceso, Hecho
- Tareas asignadas a cada integrante.

## Repositorio
- GitHub: https://github.com/NicoCisternas1111/ProyectoAppsMoviles.git
--------------------------------------------------------------------

1. Historia del Proyecto

    La Biblioteca Duoc busca modernizar su sistema interno para permitir a estudiantes y docentes gestionar sus libros de manera más simple y accesible desde dispositivos móviles.
    El proyecto tiene como objetivo ofrecer una aplicación intuitiva que funcione sin conexión a internet, permitiendo registrar, consultar y actualizar información de libros de forma ágil, además de aprovechar funciones nativas del teléfono como la cámara y el micrófono.

2. Objetivos

    -Digitalizar la gestión básica de libros desde una aplicación móvil.
    -Reducir el uso de papel y mejorar la trazabilidad del inventario.
    -Permitir que cualquier usuario pueda registrar nuevos libros fácilmente, incluyendo imágenes y descripciones por voz.

3. Requisitos Funcionales

   Código	Requisito	                Descripción
   RF01	    Navegación funcional	    El usuario puede moverse entre la lista de libros, el detalle y el formulario de registro o edición.
   RF02	    Gestión de libros (CRUD)	Permite crear, leer, actualizar y eliminar registros de libros almacenados localmente.
   RF03	    Formulario validado	        Todos los campos (título, autor, descripción, etc.) se validan con mensajes visuales y texto de error.
   RF04	    Persistencia local	        Los datos se almacenan mediante Room, asegurando funcionamiento sin conexión.
   RF05	    Captura de imágenes	        Permite tomar una fotografía del libro o subirla desde la galería usando la cámara del dispositivo.
   RF06	    Dictado por voz	            Usa el micrófono del teléfono para transcribir texto en el campo de descripción.
   RF07	    Animaciones básicas	        Se integran transiciones suaves entre pantallas (Crossfade) para una experiencia fluida.

4. Requisitos de Diseño

   Tipo	                Especificación
   Colores principales	Azul (#1565C0) para encabezados y botones; Blanco (#FFFFFF) para fondo; Gris claro (#F5F5F5) para tarjetas.
   Tipografía	        Roboto (Google Fonts). Moderna, legible y nativa de Android.
   Estilo visual	    Minimalista, limpio y enfocado en la legibilidad. Uso de tarjetas con sombras suaves y botones redondeados.
   Diseño adaptable	    Interfaz optimizada para teléfonos y tablets, con tamaños ajustables según el ancho de pantalla.
   Iconografía	        Uso de íconos Material Design (libro, cámara, micrófono, guardar, eliminar).
   Animaciones	        Crossfade en transiciones y feedback visual en botones.

5. Requisitos No Funcionales

    -Compatibilidad: Android 7.0 (API 24) o superior.
    -Modo offline: La aplicación debe operar sin conexión, sincronizando datos localmente.
    -Usabilidad: Todos los campos deben ser comprensibles y estar acompañados de texto de ayuda.
    -Seguridad: La app no solicita credenciales ni accede a datos personales fuera de las funciones nativas necesarias.

6. Escenario de Uso

    -El usuario abre la aplicación y visualiza la lista de libros almacenados.
    -Desde el botón “+”, accede al formulario de registro.
    -Puede tomar una foto del libro con la cámara o dictar la descripción por voz.
    -Al guardar, el libro se almacena en la base local (Room) y aparece automáticamente en la lista.
    -Desde la lista puede editar, eliminar o consultar los detalles de cualquier registro.


--------------------------------------------------------------------
