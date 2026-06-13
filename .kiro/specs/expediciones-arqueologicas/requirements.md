# Requisitos: Sistema de Gestión de Expediciones Arqueológicas

## Introducción

El Sistema de Gestión de Expediciones Arqueológicas es una aplicación de escritorio desarrollada en Java Swing que permite a los investigadores y arqueólogos gestionar la información completa del ciclo de vida de expediciones arqueológicas. El sistema proporciona operaciones CRUD (Crear, Leer, Actualizar, Eliminar) para registros de expediciones, permitiendo almacenar detalles críticos como fechas, ubicaciones y sitios arqueológicos asociados. La aplicación utiliza MySQL 8.x en AWS RDS como base de datos persistente y sigue el patrón arquitectónico MVC para garantizar separación de responsabilidades y mantenibilidad.

## Glosario

- **Sistema**: Aplicación de Gestión de Expediciones Arqueológicas desarrollada en Java Swing
- **Expedición**: Registro de una actividad arqueológica que incluye código, nombre, fechas de inicio/fin y sitio arqueológico asociado
- **Código_Expedición**: Identificador único alphanumérico de hasta 10 caracteres para cada expedición
- **Nombre_Expedición**: Descripción textual del nombre o título de la expedición (máximo 255 caracteres)
- **Fecha_Inicio**: Fecha de calendario que marca el comienzo de la expedición
- **Fecha_Fin**: Fecha de calendario que marca el término de la expedición
- **Sitio_Arqueológico**: Nombre o identificación del sitio arqueológico donde se realiza la expedición (máximo 255 caracteres)
- **Usuario**: Investigador o arqueólogo que opera el sistema
- **Base_Datos**: Almacenamiento persistente MySQL 8.x en AWS RDS
- **GUI**: Interfaz gráfica de usuario construida con componentes Java Swing
- **Modelo**: Capa de datos que gestiona la entidad Expedición
- **Vista**: Componentes Swing que presentan la interfaz al usuario
- **Controlador**: Lógica que coordina las interacciones entre Modelo y Vista
- **Validación**: Proceso de verificar que los datos cumplan con reglas de negocio antes de ser almacenados

## Requisitos

### Requisito 1: Crear Nueva Expedición

**Historia de Usuario:** Como investigador arqueológico, quiero crear nuevos registros de expediciones, para que pueda documentar y registrar mis actividades de investigación en la base de datos.

#### Criterios de Aceptación

1. WHEN el Usuario hace clic en el botón "Nueva Expedición", THE Sistema SHALL mostrar un formulario con campos vacíos para: Código_Expedición, Nombre_Expedición, Fecha_Inicio, Fecha_Fin y Sitio_Arqueológico

2. WHEN el Usuario completa todos los campos requeridos con valores válidos, THE Controlador SHALL validar que:
   - El Código_Expedición no esté vacío y no exista previamente en la Base_Datos
   - El Nombre_Expedición no esté vacío y tenga máximo 255 caracteres
   - La Fecha_Inicio sea anterior o igual a la Fecha_Fin
   - El Sitio_Arqueológico no esté vacío y tenga máximo 255 caracteres

3. IF la validación es exitosa, THE Modelo SHALL insertar la Expedición en la Base_Datos y THE Sistema SHALL mostrar un mensaje de confirmación "Expedición creada exitosamente"

4. IF la validación falla, THE Sistema SHALL mostrar un mensaje de error específico indicando qué campo(s) tienen problemas

5. WHEN la operación se completa, THE Vista SHALL limpiar el formulario y actualizar la lista de expediciones

### Requisito 2: Listar Expediciones

**Historia de Usuario:** Como investigador arqueológico, quiero ver un listado de todas las expediciones registradas, para que pueda consultar y revisar rápidamente mis actividades.

#### Criterios de Aceptación

1. THE Sistema SHALL mostrar al Usuario una JTable con todas las Expediciones almacenadas en la Base_Datos

2. WHEN la aplicación se inicia o WHEN se crea/modifica/elimina una Expedición, THE Vista SHALL actualizar automáticamente la JTable con los datos actuales

3. THE JTable SHALL incluir las columnas: Código, Nombre de Expedición, Fecha de Inicio, Fecha de Fin y Sitio Arqueológico, en ese orden

4. FOR ALL Expediciones en la JTable, los datos mostrados SHALL corresponder exactamente a los almacenados en la Base_Datos (propiedad de redondez: lectura desde BD debe ser idéntica a lo que se muestra)

5. WHEN la JTable está vacía, THE Sistema SHALL mostrar un mensaje al Usuario indicando "No hay expediciones registradas"

### Requisito 3: Modificar Expedición Existente

**Historia de Usuario:** Como investigador arqueológico, quiero editar la información de expediciones existentes, para que pueda corregir datos o actualizar detalles de mis actividades arqueológicas.

#### Criterios de Aceptación

1. WHEN el Usuario selecciona una fila en la JTable y hace clic en el botón "Editar", THE Sistema SHALL cargar los datos de esa Expedición en un formulario editable

2. WHEN el Usuario modifica uno o más campos del formulario, THE Controlador SHALL validar que:
   - El Código_Expedición no se haya duplicado con otra expedición
   - El Nombre_Expedición no esté vacío y tenga máximo 255 caracteres
   - La Fecha_Inicio sea anterior o igual a la Fecha_Fin
   - El Sitio_Arqueológico no esté vacío y tenga máximo 255 caracteres

3. IF la validación es exitosa, THE Modelo SHALL actualizar el registro en la Base_Datos y THE Sistema SHALL mostrar un mensaje de confirmación "Expedición actualizada exitosamente"

4. IF la validación falla, THE Sistema SHALL mostrar un mensaje de error sin modificar la Base_Datos

5. WHEN la operación se completa, THE Vista SHALL actualizar la JTable con los datos modificados y limpiar el formulario

6. IF no hay una Expedición seleccionada en la JTable, THE Sistema SHALL mostrar un mensaje de advertencia "Seleccione una expedición para editar"

### Requisito 4: Eliminar Expedición

**Historia de Usuario:** Como investigador arqueológico, quiero eliminar registros de expediciones que ya no sean necesarios, para que pueda mantener la base de datos limpia y organizada.

#### Criterios de Aceptación

1. WHEN el Usuario selecciona una fila en la JTable y hace clic en el botón "Eliminar", THE Sistema SHALL mostrar un cuadro de diálogo de confirmación con el mensaje "¿Está seguro de que desea eliminar esta expedición?"

2. IF el Usuario confirma la eliminación, THE Modelo SHALL eliminar la Expedición de la Base_Datos y THE Sistema SHALL mostrar un mensaje de confirmación "Expedición eliminada exitosamente"

3. IF el Usuario cancela la operación, THE Sistema SHALL no modificar la Base_Datos

4. WHEN la operación se completa, THE Vista SHALL actualizar la JTable removiendo la fila eliminada

5. IF no hay una Expedición seleccionada en la JTable, THE Sistema SHALL mostrar un mensaje de advertencia "Seleccione una expedición para eliminar"

### Requisito 5: Persistencia en Base de Datos

**Historia de Usuario:** Como administrador del sistema, quiero que todos los datos de expediciones se almacenen de forma persistente, para que no se pierdan al cerrar la aplicación.

#### Criterios de Aceptación

1. THE Sistema SHALL conectarse a una Base_Datos MySQL 8.x en AWS RDS utilizando JDBC

2. WHEN la aplicación se inicia, THE Modelo SHALL cargar todas las Expediciones de la Base_Datos sin requerir acción del Usuario

3. FOR ALL operaciones CRUD (crear, leer, actualizar, eliminar), THE Modelo SHALL sincronizar inmediatamente los cambios con la Base_Datos

4. IF ocurre un error de conexión con la Base_Datos, THE Sistema SHALL mostrar un mensaje de error al Usuario y permitir solo ver los datos cargados en memoria

5. THE Modelo SHALL mantener la integridad de datos: si la Base_Datos contiene valores correctos, la Vista SHALL mostrar esos mismos valores sin corrupción

### Requisito 6: Interfaz Gráfica de Usuario

**Historia de Usuario:** Como investigador arqueológico, quiero una interfaz intuitiva y fácil de usar, para que pueda ejecutar las operaciones CRUD sin curva de aprendizaje pronunciada.

#### Criterios de Aceptación

1. THE Sistema SHALL utilizar componentes Java Swing: JFrame como ventana principal, JPanel para organizar secciones, JLabel para etiquetas, JTextField para entrada de datos, JButton para acciones, y JTable para listados

2. THE JFrame SHALL contener:
   - Una sección superior con el título "Gestión de Expediciones Arqueológicas"
   - Un panel de formulario con campos para ingresar datos de Expedición
   - Un panel de botones con acciones: "Nueva", "Editar", "Eliminar", "Limpiar"
   - Un panel central con la JTable mostrando todas las Expediciones

3. WHEN el Usuario interactúa con un componente (botón, campo de texto), THE Vista SHALL responder inmediatamente sin retrasos perceptibles

4. THE Sistema SHALL mostrar todos los mensajes de error, advertencia y confirmación en ventanas de diálogo (JOptionPane) con el tipo de mensaje apropiado

5. WHERE se requiera seleccionar fechas, THE Vista SHALL proporcionar campos JTextField con indicación del formato esperado (DD/MM/YYYY)

### Requisito 7: Validación de Integridad Referencial

**Historia de Usuario:** Como administrador del sistema, quiero que el sistema valide toda la entrada de datos, para que se garantice la consistencia y calidad de los datos en la base de datos.

#### Criterios de Aceptación

1. WHEN se intenta crear o modificar una Expedición, THE Controlador SHALL validar que todos los campos requeridos tengan valores

2. IF el Código_Expedición existe previamente (para operación de creación), THE Sistema SHALL rechazar la operación y mostrar "El código de expedición ya existe"

3. IF la Fecha_Fin es anterior a la Fecha_Inicio, THE Sistema SHALL rechazar la operación y mostrar "La fecha de fin debe ser igual o posterior a la fecha de inicio"

4. IF algún campo de texto excede su límite de caracteres, THE Sistema SHALL rechazar la operación y mostrar "El campo [nombre] excede el límite de [N] caracteres"

5. FOR ALL fechas ingresadas, si el formato no es válido, THE Sistema SHALL mostrar "Ingrese las fechas en formato DD/MM/YYYY"

### Requisito 8: Patrón Arquitectónico MVC

**Historia de Usuario:** Como desarrollador, quiero que el sistema siga el patrón MVC, para que sea mantenible, escalable y fácil de probar.

#### Criterios de Aceptación

1. THE Sistema SHALL implementar tres capas claramente separadas:
   - **Modelo**: Clases que representan la entidad Expedición y gestión de datos con la Base_Datos
   - **Vista**: Componentes Swing que construyen la interfaz gráfica
   - **Controlador**: Lógica que coordina las interacciones y validaciones

2. WHILE el Controlador procesa una operación, THE Vista SHALL permanecer responsiva y cualquier acción del Usuario SHALL ser procesada correctamente

3. THE Modelo SHALL ser independiente de la Vista: cambios en la implementación de la GUI no requerirán modificación de la lógica de datos

4. WHEN el Modelo se actualiza con nuevos datos, THE Vista SHALL reflejarse automáticamente sin recargar la aplicación completa

### Requisito 9: Manejo de Errores y Excepciones

**Historia de Usuario:** Como Usuario del sistema, quiero que los errores sean manejados de forma elegante, para que no se interrumpa la experiencia y el sistema permanezca funcional.

#### Criterios de Aceptación

1. IF ocurre una excepción durante cualquier operación CRUD, THE Sistema SHALL capturar la excepción y mostrar un mensaje de error descriptivo al Usuario

2. IF la conexión con la Base_Datos se pierde, THE Sistema SHALL mostrar un mensaje de error y permanecer en estado funcional para operaciones locales

3. IF ocurre un error de SQL durante la operación, THE Sistema SHALL mostrar un mensaje de error sin revelar detalles técnicos de SQL que confundan al Usuario

4. WHEN ocurre un error, THE Vista SHALL limpiar cualquier estado parcial y permitir al Usuario reintentar la operación

### Requisito 10: Iniciación de Sesión y Acceso

**Historia de Usuario:** Como administrador del sistema, quiero registrar el acceso de usuarios, para que solo arqueólogos autorizados puedan acceder al sistema.

#### Criterios de Aceptación

1. WHEN la aplicación se inicia, THE Sistema SHALL mostrar una ventana de login con campos para Usuario y Contraseña

2. WHEN el Usuario ingresa credenciales válidas, THE Sistema SHALL validar contra una fuente de autenticación y mostrar la interfaz principal

3. IF las credenciales son inválidas, THE Sistema SHALL mostrar un mensaje de error "Usuario o contraseña incorrectos" sin revelar cuál de los dos es incorrecto

4. WHEN el Usuario hace clic en "Cerrar Sesión", THE Sistema SHALL limpiar datos en memoria y retornar a la pantalla de login

### Requisito 11: Búsqueda y Filtrado de Expediciones

**Historia de Usuario:** Como investigador arqueológico, quiero poder buscar expediciones por criterios específicos, para que pueda encontrar rápidamente la información que necesito.

#### Criterios de Aceptación

1. THE Vista SHALL proporcionar un campo JTextField con la etiqueta "Buscar por Nombre de Expedición"

2. WHEN el Usuario ingresa un término de búsqueda y presiona ENTER o hace clic en "Buscar", THE Controlador SHALL filtrar las Expediciones en la JTable que contengan el término ingresado

3. WHEN el Usuario borra el término de búsqueda, THE Vista SHALL mostrar nuevamente todas las Expediciones

4. FOR ALL búsquedas realizadas, the filtering SHALL ser case-insensitive (insensible a mayúsculas/minúsculas)

5. WHERE no hay resultados que coincidan con la búsqueda, THE Sistema SHALL mostrar la JTable vacía y un mensaje "No se encontraron expediciones con ese criterio"

## Propiedades de Corrección (Acceptance Criteria Properties)

### Property 1: Round-Trip para Expediciones (Persistencia e Integridad)

**Descripción:** Toda Expedición creada, modificada y almacenada en la Base_Datos debe ser recuperable exactamente como se ingresó.

**Patrón:** Round-Trip Property

FOR ALL Expedición válida E creada:
```
ReadFromDatabase(Create(E)) == E
```

**Aplicación:** Después de crear una Expedición, leerla desde la Base_Datos debe retornar datos idénticos (código, nombre, fechas, sitio).

---

### Property 2: Invariante de Fechas (Consistencia Temporal)

**Descripción:** Para cualquier Expedición, la Fecha_Inicio siempre debe ser menor o igual a la Fecha_Fin.

**Patrón:** Invariant

FOR ALL Expedición E en la Base_Datos:
```
E.Fecha_Inicio <= E.Fecha_Fin
```

**Aplicación:** El sistema debe validar este invariante en toda operación CRUD y rechazar cualquier estado que lo viole.

---

### Property 3: Idempotencia de Lectura (Consultas Consistentes)

**Descripción:** Leer la misma Expedición múltiples veces sin modificarla debe retornar los mismos datos.

**Patrón:** Idempotence

FOR ALL Expedición E y N > 1:
```
Read(E) == Read(E) == ... (N veces)
```

**Aplicación:** Consultar la lista de expediciones dos veces consecutivas sin cambios debe retornar el mismo resultado.

---

### Property 4: Unicidad de Código (Invariante de Identificador)

**Descripción:** No pueden existir dos Expediciones con el mismo Código_Expedición en la Base_Datos.

**Patrón:** Invariant

FOR ALL Expedición E1, E2 en la Base_Datos:
```
E1.Código_Expedición == E2.Código_Expedición → E1 == E2 (misma expedición)
```

**Aplicación:** El sistema debe rechazar la creación o modificación de una Expedición si su código duplica uno existente.

---

### Property 5: Cobertura Completa de CRUD (Metamorfismo de Operaciones)

**Descripción:** El número de Expediciones en la Base_Datos aumenta exactamente en 1 tras Create, no cambia tras Read, disminuye exactamente en 1 tras Delete.

**Patrón:** Metamorphic Property

```
count(ListExpediciones()) == count(ListExpediciones()) + 1  [después de Create]
count(ListExpediciones()) == count(ListExpediciones())      [después de Read]
count(ListExpediciones()) == count(ListExpediciones()) - 1  [después de Delete]
```

**Aplicación:** Cada operación debe modificar el estado de la Base_Datos de forma predecible y verificable.

---

### Property 6: Integridad de Actualización (Invariante de Modificación)

**Descripción:** Cuando se actualiza una Expedición, solo los campos modificados cambian; los no modificados permanecen idénticos.

**Patrón:** Invariant

FOR ALL Expedición E, con campos F1, F2, ..., Fn:
```
IF F1 es modificado a V1, THEN otros campos Fi (i != 1) permanecen sin cambios
```

**Aplicación:** Editar solo el Nombre_Expedición no debe alterar las fechas, código, ni sitio arqueológico.

