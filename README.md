# Aplicación de Registro de Partes Policiales

## Descripción

Aplicación Android que permite registrar partes policiales desde un dispositivo móvil. Los datos se almacenan localmente en una base de datos SQLite y pueden ser consultados, editados o eliminados desde distintas vistas.

## Características

- Registro de partes con datos del infractor, vehículo, causal, funcionario y foto.
- Almacenamiento local con SQLite.
- Filtro dinámico de causales según gravedad.
- Consulta de historial de partes.
- Edición y eliminación de registros.
- Diálogo de confirmación para acciones sensibles.

## Estructura del Proyecto

### Actividades

- `ParteActivity`: formulario principal para registrar partes.
- `EditarParteActivity`: permite modificar un parte existente.
- `EliminarParteActivity`: permite eliminar un parte.
- `HistorialActivity`: muestra todos los partes registrados.
- `VistaParteActivity`: muestra los detalles completos de un parte.
- `ListaEditableActivity`: lista partes que pueden editarse.
- `ListaEliminableActivity`: lista partes que pueden eliminarse.

### Clases de soporte

- `DBHelper`: gestiona la base de datos SQLite.
- `Parte`: modelo de datos que representa un parte policial.
- `AutorizacionDialog`: diálogo de confirmación para eliminar registros.

### Adaptadores

- `ParteAdapter`: muestra partes editables.
- `ParteEliminableAdapter`: muestra partes eliminables.
- `ParteHistorialAdapter`: muestra partes en historial.

## Layouts XML

### Actividades

- `activity_parte.xml`
- `activity_editar_parte.xml`
- `activity_eliminar_parte.xml`
- `activity_historial.xml`
- `activity_lista_editable.xml`
- `activity_lista_eliminable.xml`
- `activity_ver_parte.xml`

### Ítems de lista

- `item_parte.xml`
- `item_parte_editable.xml`
- `item_parte_eliminable.xml`
- `item_parte_historial.xml`

### Diálogo

- `dialog_autorizacion.xml`

## Base de Datos

- Tabla: `partes`
- Campos: `id`, `nombres`, `rut`, `causal`, `fotoPath`, etc.
- Clase: `DBHelper`
- Métodos principales:
  - `insertarParte(Parte parte)`
  - `obtenerPartePorId(int id)`
  - `obtenerTodosLosPartes()`
  - `updateParte(Parte parte)`
  - `borrarPartePorId(int id)`

### Uso de ContentValues

Para insertar o actualizar datos se utiliza `ContentValues`:

## Java
ContentValues values = new ContentValues();
values.put("nombres", parte.getNombres());
values.put("rut", parte.getRut());
values.put("causal", parte.getCausal());
values.put("fotoPath", parte.getFotoPath());
db.insert("partes", null, values);

## Tecnologías Utilizadas
- Android Studio
- Java
- SQLite
- App Inspection
- GitHub
- FileProvider

## Estado del Proyecto
- Funcionalidad principal completa.
- Pruebas realizadas en inserción, consulta, edición y eliminación.
- Validación de persistencia con App Inspection.
- Pendientes: mejoras visuales, validación de campos, modularización.

## Próximas Etapas
- Optimizar diseño visual.
- Mejorar mensajes de validación.
- Documentar flujo completo en el repositorio.
- Preparar presentación final con capturas.
- Subir proyecto completo a GitHub.

## Autor
Diego Rojo Peralta  
Estudiante de Analista Programador  
IP Santo Tomas Sede Ovalle
