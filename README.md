# Práctica 3

## Implementación de una Interfaz Gráfica de Usuario (GUI) para SQL.

## Descripción Detallada de la Actividad Práctica: Implementación de un IDE SQL con JavaFX y Maven.

> **Requisitos**
>
> Para ésta práctica es necesario tener implementadas las prácticas 1 versión 2 y práctica 2 versión 2.


**Objetivo:**

El objetivo de esta actividad práctica es desarrollar una aplicación IDE (entorno de desarrollo integrado) específico para la edición y ejecución de códigos SQL utilizando JavaFX y Maven. La aplicación debe cumplir con las siguientes características:

**Funcionalidades:**

1. **Resaltado de código SQL mediante colores:** Implementar un mecanismo para resaltar diferentes elementos del código SQL, como palabras clave, funciones, nombres de tablas, campos, etc., utilizando diferentes colores para mejorar la legibilidad y comprensión del código.
2. **Menús para la carga y guardado de archivos SQL:** Implementar menús y/o botones que permitan al usuario cargar archivos SQL existentes y guardar los cambios realizados en la aplicación.
3. **Botones para ejecución de códigos SQL:** Implementar botones que permitan al usuario ejecutar el código SQL escrito en el editor. Cada comando ejecutado se deberá ver reflejado en los archivos CSV que fungen como tablas, todo ésto mediante la implementación propia del manejador de bases de datos. La aplicación debe poder conectarse a una base de datos (carpeta) y ejecutar los comandos SQL, mostrando los resultados en un panel designado.
4. **Panel izquierdo con control tipo árbol para el despliegue y muestra de las tablas y sus campos:** Implementar un control tipo árbol en el panel izquierdo de la interfaz que permita al usuario visualizar las tablas existentes en la base de datos y sus respectivos campos. El control debe actualizarse dinámicamente al conectarse a una base de datos (carpeta) o al realizar cambios en la estructura de la misma.
5. **Panel inferior de tipo rejilla para mostrar resultados de consultas SELECT:** Implementar un control tipo rejilla en el panel inferior de la interfaz que permita al usuario visualizar los resultados de las consultas SELECT ejecutadas. La rejilla debe mostrar los nombres de las columnas y los datos correspondientes a cada fila. Se deberán respetar los nombre de los campos y/o los alias designados en el comando SELECT.

**Conceptos clave:**

Se deberán utilizar los siguientes temas vistos anteriormente:

1. **Clases, objetos e interfaces:** Utilizar clases para representar las diferentes entidades del sistema, como la ventana principal, el editor de código, el control de árbol y la rejilla de resultados. Utilizar objetos para crear instancias de las clases y utilizar interfaces para definir contratos de comportamiento entre las clases.
2. **Clases abstractas:** Utilizar clases abstractas para definir clases base que comparten características comunes, como la clase abstracta `SQLCommand` que puede representar una consulta SQL o un procedimiento almacenado.
3. **Clases genéricas:** Utilizar clases genéricas para definir clases que pueden trabajar con diferentes tipos de datos, como la clase genérica `TreeItem` que puede representar un nodo en el control de árbol y puede contener datos de cualquier tipo.
4. **Manejo de excepciones:** Implementar el manejo de excepciones para capturar y gestionar errores durante la ejecución de la aplicación, como errores de conexión a la base de datos, errores de sintaxis SQL o errores de tipo de datos.

**Evaluación:**

La actividad práctica será evaluada en base a los siguientes criterios:

* **Funcionalidad:** La aplicación debe cumplir con todas las características descritas anteriormente.
* **Diseño de la interfaz de usuario:** La interfaz de usuario debe ser intuitiva y fácil de usar.
* **Calidad del código:** El código debe ser limpio, organizado y bien documentado.
* **Manejo de excepciones:** La aplicación debe manejar adecuadamente las excepciones que puedan surgir durante la ejecución.
* **Test Unitarios y de integración** Se deberán generar las pruebas unitadas y de integración para el correcto functionamiento del sistema.