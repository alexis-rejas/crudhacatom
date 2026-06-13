# Plan de Implementación: Sistema de Gestión de Expediciones Arqueológicas

## Overview

Plan de implementación completo para el Sistema de Gestión de Expediciones Arqueológicas siguiendo arquitectura MVC. La implementación se estructura en seis fases progresivas: (1) Configuración inicial y base de datos, (2) Implementación de la capa Modelo (Entity, DAO, Connection), (3) Implementación de la capa Vista (Panels y MainFrame), (4) Implementación de la capa Controlador, (5) Integración y testing con propiedades de corrección, y (6) Ajustes finales y despliegue. 

Cada tarea construye incrementalmente sobre las anteriores, validando funcionalidad en checkpoints estratégicos. El stack es Java 17, Java Swing, MySQL 8.x en AWS RDS, JDBC y HikariCP.

---

## Tasks

### Fase 1: Configuración Inicial y Base de Datos

- [ ] 1. Configuración del Proyecto Java y Dependencias
  - Crear estructura de directorios del proyecto Maven (src/main/java, src/main/resources, src/test)
  - Configurar pom.xml con dependencias: JDK 17, MySQL Connector/J 8.x, HikariCP 5.x, JUnit 5, jqwik
  - Descargar y validar todas las dependencias
  - Crear archivo de configuración properties para credenciales AWS RDS (sin incluir en git)
  - _Requisitos: 5.1, 8.1_
  - _Complejidad: Baja_

- [ ] 2. Creación y Configuración de Base de Datos AWS RDS
  - Crear instancia MySQL 8.x en AWS RDS (si no existe)
  - Crear esquema (database) `expediciones_arqueologicas`
  - Ejecutar script SQL para crear tabla expediciones con restricciones CHECK y UNIQUE
  - Verificar índices en código_expedicion (único) y nombre_expedicion (búsqueda)
  - Crear timestamps created_at y updated_at
  - Documentar endpoint, puerto 3306, credenciales en archivo local config
  - _Requisitos: 5.1, 5.3, 7.1_
  - _Complejidad: Media_

- [ ] 3. Implementar Clase DatabaseConnection (Singleton con Pool HikariCP)
  - Crear clase DatabaseConnection con patrón Singleton
  - Inicializar HikariDataSource con configuración AWS RDS
  - Implementar método getInstance() sincronizado
  - Implementar método obtenerConexion() que retorna Connection del pool
  - Implementar método verificarConexion() para validar conectividad
  - Implementar método cerrar() para shutdown graceful del pool
  - Manejar ConfigProperty para URL, usuario, password desde archivo config
  - _Requisitos: 5.1, 5.2, 5.4_
  - _Complejidad: Media_

- [ ] 4. Crear Excepciones Personalizadas
  - Crear clase DatabaseException con enum Causa (SIN_CONEXION, ERROR_SQL, FALLO_INSERCION, NO_ENCONTRADO, DUPLICADO)
  - Implementar método getMensajeUsuario() que traduce causa a mensaje legible
  - Crear clase ValidacionException para errores de validación de entrada
  - Documentar stack traces internos sin revelar detalles SQL al usuario
  - _Requisitos: 9.1, 9.3_
  - _Complejidad: Baja_

- [ ] 5. Checkpoint - Base de Datos Operacional
  - Verificar que conexión a AWS RDS es exitosa
  - Confirmar que tabla expediciones existe con estructura correcta
  - Probar inserción manual de registro de prueba y lectura desde Java
  - Documentar cualquier problema de conectividad o credenciales
  - Preguntar al usuario si todo está correcto antes de continuar

---

### Fase 2: Implementación de la Capa Modelo (Entity, DAO)

- [ ] 6. Implementar Clase Entity Expedicion
  - Crear clase Expedicion con propiedades: id (int), codigo (String), nombre (String), fechaInicio (LocalDate), fechaFin (LocalDate), sitioArqueologico (String)
  - Implementar constructores: vacío y con todos los parámetros excepto id
  - Implementar getters y setters para todas las propiedades
  - Implementar método esValida() que verifica invariantes básicos (no nulos, fechas consistentes)
  - Implementar equals() basado en codigo (identificador único)
  - Implementar hashCode() basado en codigo
  - Implementar toString() con formato readable
  - _Requisitos: 1.1, 3.1, 5.5_
  - _Complejidad: Baja_

- [ ] 7. Implementar Clase ExpedicionDAO - Operaciones CREATE y READ
  - Crear clase ExpedicionDAO con referencia a DatabaseConnection
  - Implementar método create(Expedicion) con PreparedStatement para INSERT
  - Implementar validación de integridad referencial en SQL (constraints)
  - Implementar método readAll() que retorna List<Expedicion> ordenada por código
  - Implementar método readByCodigo(String) que retorna Expedicion o null
  - Implementar método mapearResultSet() privado para convertir ResultSet a Expedicion
  - Manejar excepciones SQLException y envolverlas en DatabaseException
  - _Requisitos: 2.1, 2.2, 2.4, 5.3_
  - _Complejidad: Media_

- [ ] 8. Implementar Clase ExpedicionDAO - Operaciones UPDATE y DELETE
  - Implementar método update(Expedicion) con PreparedStatement para UPDATE WHERE codigo
  - Validar que expedición existe antes de actualizar (NO_ENCONTRADO si no)
  - Implementar método delete(String codigo) con PreparedStatement para DELETE WHERE codigo
  - Validar que expedición existe antes de eliminar (NO_ENCONTRADO si no)
  - Implementar método existeCodigo(String) que retorna boolean
  - Utilizar prepared statements en todas las operaciones para prevenir inyección SQL
  - _Requisitos: 3.3, 4.2, 7.2_
  - _Complejidad: Media_

- [ ] 9. Implementar Validaciones en ExpedicionDAO (Integridad Referencial)
  - Agregar validación en create: verificar que codigo no existe previo (existeCodigo)
  - Agregar validación en create: verificar que expedición es válida (esValida)
  - Agregar validación en update: permitir cambio de código solo si nuevo código no existe
  - Agregar validación en update: rechazar si datos no son válidos
  - Lanzar DatabaseException(DUPLICADO) si código ya existe
  - Documentar todas las restricciones SQL implementadas
  - _Requisitos: 7.2, 7.3, 7.4_
  - _Complejidad: Baja_

- [ ] 10. Checkpoint - Modelo y Persistencia Funcionales
  - Crear expedición de prueba vía DAO: ARCH2024, "Test Expedición", 15/03/2024, 20/03/2024, "Sitio Test"
  - Leer expedición desde BD y verificar roundtrip (Property 1)
  - Modificar nombre y verificar que otros campos no cambian (Property 6)
  - Eliminar expedición y verificar que cuenta disminuye en 1 (Property 5)
  - Verificar que lectura múltiple sin cambios retorna lo mismo (Property 3)
  - Intentar crear código duplicado y verificar rechazo (Property 4)
  - Preguntar al usuario si todas las operaciones funcionan correctamente

---

### Fase 3: Implementación de la Capa Vista (Panels y MainFrame)

- [ ] 11. Implementar PanelFormulario para Entrada de Datos
  - Crear clase PanelFormulario extends JPanel
  - Añadir JLabel y JTextField para: Código, Nombre, FechaInicio, FechaFin, SitioArqueologico
  - Configurar layout GridLayout o GroupLayout para alineación clara
  - Implementar getters para acceder a valores: getCodigo(), getNombre(), getFechaInicio(), getFechaFin(), getSitioArqueologico()
  - Implementar método setCampos(Expedicion) para precarga en edición
  - Implementar método limpiar() para vaciar todos los campos
  - Implementar método deshabilitarCampoCodigoEnEdicion() para bloquear cambio de código
  - Implementar método habilitarCampos(boolean) para activar/desactivar entrada
  - _Requisitos: 1.1, 6.1, 6.2_
  - _Complejidad: Baja_

- [ ] 12. Implementar PanelTabla para Visualización de Expediciones
  - Crear clase PanelTabla extends JPanel
  - Crear JTable con DefaultTableModel y columnas: Código, Nombre Expedición, Fecha Inicio, Fecha Fin, Sitio Arqueológico
  - Configurar JScrollPane para tabla con scroll vertical/horizontal
  - Implementar método actualizarDatos(List<Expedicion>) para cargar desde lista
  - Implementar método getExpedicionSeleccionada() retorna Expedicion o null
  - Implementar método getFilaSeleccionada() retorna índice o -1
  - Implementar método limpiar() para vaciar tabla
  - Implementar visualización de mensaje "No hay expediciones registradas" cuando está vacía (Property 2.5 en requisitos)
  - _Requisitos: 2.1, 2.2, 2.3, 2.4_
  - _Complejidad: Media_

- [ ] 13. Implementar PanelBotones para Acciones CRUD
  - Crear clase PanelBotones extends JPanel
  - Crear botones: btnNueva, btnEditar, btnEliminar, btnLimpiar
  - Configurar layout GridLayout para botones en fila o columna
  - Implementar método habilitarBotones(boolean) para activar/desactivar todos
  - Implementar método habilitarBotonEditar(boolean) para solo Editar
  - Implementar método habilitarBotonEliminar(boolean) para solo Eliminar
  - Implementar métodos agregarListener* para asignar ActionListeners externos
  - Conectar acciones de botones a listeners provistos por controlador
  - _Requisitos: 6.1, 6.2_
  - _Complejidad: Baja_

- [ ] 14. Implementar PanelBusqueda para Búsqueda y Filtrado
  - Crear clase PanelBusqueda extends JPanel
  - Crear JTextField txtBusqueda con etiqueta "Buscar por Nombre de Expedición"
  - Crear JButton btnBuscar y btnMostrarTodos
  - Configurar layout para alineación horizontal o vertical
  - Implementar método getTerminoBusqueda() que retorna String (puede ser null/vacío)
  - Implementar método limpiarBusqueda() para vaciar campo
  - Implementar métodos agregarListener* para asignar ActionListeners
  - Permitir ENTER en txtBusqueda para activar búsqueda (KeyListener)
  - _Requisitos: 11.1, 11.2, 11.3_
  - _Complejidad: Baja_

- [ ] 15. Implementar MainFrame (Ventana Principal)
  - Crear clase MainFrame extends JFrame
  - Instanciar y agregar: PanelFormulario, PanelTabla, PanelBotones, PanelBusqueda
  - Configurar BorderLayout: formulario NORTH, tabla CENTER, botones SOUTH, búsqueda EAST/WEST
  - Configurar ventana: título "Gestión de Expediciones Arqueológicas", tamaño 1000x700px, resizable
  - Implementar método actualizarTablaExpediciones(List<Expedicion>) que delega a panelTabla
  - Implementar método mostrarMensaje(String, int tipoMensaje) que usa JOptionPane
  - Implementar método limpiarFormulario() que delega a panelFormulario
  - Implementar método getExpedicionSeleccionada() que obtiene de panelTabla
  - Implementar método habilitarFormulario(boolean) para controlar entrada
  - Configurar operación de cierre: cerrar conexión DB, guardar estado si aplica
  - _Requisitos: 6.1, 6.2_
  - _Complejidad: Media_

- [ ] 16. Checkpoint - Interfaz Gráfica Operacional
  - Verificar que MainFrame se abre sin errores
  - Verificar que todos los paneles se visualizan correctamente
  - Probar que botones responden a clicks (aunque no hacen nada aún)
  - Probar que campos de entrada aceptan texto
  - Verificar que tabla se actualiza manualmente agregando datos (sin persistencia aún)
  - Preguntar al usuario si UI luce bien y está lista para conexión al controlador

---

### Fase 4: Implementación de la Capa Controlador

- [ ] 17. Implementar ExpedicionController - Inicialización y Cargas Iniciales
  - Crear clase ExpedicionController con referencias a ExpedicionDAO y MainFrame
  - Crear propiedades: expedicionEnEdicion, expedicionesActuales (List), expedicionesFiltradas (List)
  - Implementar constructor que inicializa listas, cargas datos iniciales, conecta listeners
  - Implementar método cargarExpedicionesInicial() que llama a listarExpediciones()
  - Implementar método listarExpediciones() que obtiene todas de DAO y actualiza UI
  - Implementar método inicializarListeners() que conecta botones Vista a métodos Controlador
  - Manejar excepción DatabaseException en carga inicial
  - _Requisitos: 2.2, 5.2, 8.2_
  - _Complejidad: Media_

- [ ] 18. Implementar ExpedicionController - Operación CREATE
  - Implementar método crearExpedicion(código, nombre, fechaInicio, fechaFin, sitio)
  - Llamar validarExpedicion() para verificar datos
  - Llamar dao.existeCodigo() para verificar duplicado
  - Mostrar warning "El código de expedición ya existe" si duplicado
  - Construir objeto Expedicion con datos validados
  - Llamar dao.create() para persistir
  - Agregar a expedicionesActuales
  - Actualizar tabla vía vista.actualizarTablaExpediciones()
  - Limpiar formulario
  - Mostrar mensaje "Expedición creada exitosamente"
  - Manejar catch ValidacionException y DatabaseException
  - _Requisitos: 1.1, 1.2, 1.3, 1.4_
  - _Complejidad: Media_

- [ ] 19. Implementar ExpedicionController - Operación READ y LISTAR
  - Ya implementado en task 17, pero verificar que:
  - Lectura carga datos correctamente desde BD
  - Lista se actualiza automáticamente sin recargar aplicación
  - Mensaje "No hay expediciones registradas" aparece cuando está vacía
  - _Requisitos: 2.1, 2.2, 2.3_
  - _Complejidad: Baja_

- [ ] 20. Implementar ExpedicionController - Operación UPDATE
  - Implementar método modificarExpedicion(código, nombre, fechaInicio, fechaFin, sitio)
  - Verificar que expedicionEnEdicion != null, mostrar warning si no
  - Llamar validarExpedicion() para verificar datos
  - Verificar que código no sea duplicado (excepto si es el mismo código anterior)
  - Construir objeto Expedicion con datos validados
  - Copiar ID de expedicionEnEdicion original
  - Llamar dao.update() para persistir
  - Actualizar elemento en expedicionesActuales
  - Actualizar tabla vía vista
  - Limpiar formulario y expedicionEnEdicion = null
  - Mostrar mensaje "Expedición actualizada exitosamente"
  - Manejar excepciones apropiadamente
  - _Requisitos: 3.1, 3.2, 3.3, 3.4, 3.5_
  - _Complejidad: Media_

- [ ] 21. Implementar ExpedicionController - Operación DELETE
  - Implementar método eliminarExpedicion(código)
  - Mostrar JOptionPane.showConfirmDialog() "¿Está seguro de que desea eliminar esta expedición?"
  - IF usuario selecciona NO, retornar sin hacer nada
  - Llamar dao.delete() para persistir eliminación
  - Remover de expedicionesActuales vía removeIf(e -> e.getCodigo().equals(codigo))
  - Actualizar tabla vía vista
  - Limpiar formulario
  - Mostrar mensaje "Expedición eliminada exitosamente"
  - Manejar excepciones DatabaseException
  - _Requisitos: 4.1, 4.2, 4.3, 4.4_
  - _Complejidad: Media_

- [ ] 22. Implementar ExpedicionController - Métodos de Validación
  - Implementar método validarExpedicion(código, nombre, fechaInicio, fechaFin, sitio) throws ValidacionException
  - Validar código: no vacío, máximo 10 caracteres
  - Validar nombre: no vacío, máximo 255 caracteres
  - Validar fechaInicio y fechaFin: parsear con formato dd/MM/yyyy, lanzar error si inválido
  - Validar fechaFin >= fechaInicio, lanzar error si no
  - Validar sitio: no vacío, máximo 255 caracteres
  - Mensajes de error específicos para cada validación fallida
  - Implementar método parsearFecha(String) throws ValidacionException con DateTimeFormatter
  - _Requisitos: 7.1, 7.2, 7.3, 7.4, 7.5_
  - _Complejidad: Media_

- [ ] 23. Implementar ExpedicionController - Búsqueda y Filtrado
  - Implementar método buscarExpediciones(String termino)
  - IF termino null o vacío: mostrar expedicionesActuales (todas)
  - ELSE: filtrar expedicionesActuales usando Stream filter()
  - Filtro case-insensitive: termino.toLowerCase().contains(nombre.toLowerCase())
  - Actualizar vista con expedicionesFiltradas
  - Mostrar mensaje "No se encontraron expediciones con ese criterio" si lista vacía
  - Conectar este método a listeners de PanelBusqueda
  - _Requisitos: 11.2, 11.3, 11.4, 11.5_
  - _Complejidad: Media_

- [ ] 24. Implementar ExpedicionController - Manejo de Errores Centralizado
  - Implementar método privado manejarErrorBaseDatos(DatabaseException ex)
  - IF causa == SIN_CONEXION: mostrar "Error de conexión. Datos cargados aún disponibles"
  - ELSE: mostrar mensaje usuario vía ex.getMensajeUsuario()
  - Asegurar que mensajes no revelen detalles técnicos SQL
  - Implementar manejo de excepción en todos los métodos CRUD
  - _Requisitos: 9.1, 9.2, 9.3, 9.4_
  - _Complejidad: Baja_

- [ ] 25. Checkpoint - Controlador Funcional con Operaciones CRUD
  - Crear expedición vía formulario → aparece en tabla
  - Seleccionar expedición, editar nombre → tabla actualiza
  - Seleccionar expedición, eliminar → desaparece de tabla con confirmación
  - Búscar por nombre parcial → filtra correctamente
  - Limpiar búsqueda → muestra todas
  - Verificar que mensajes de error aparecen para datos inválidos
  - Preguntar al usuario si todas las operaciones funcionan correctamente

---

### Fase 5: Integración y Testing

- [ ] 26. Implementar Suite de Unit Tests para Modelo (Expedicion)
  - Crear clase ExpedicionTest en src/test/java
  - Test testConstructorVacio(): verificar que propiedades son null/default
  - Test testConstructorConParametros(): verificar que propiedades se asignan correctamente
  - Test testEqualsBasadoEnCodigo(): dos expediciones con mismo código son iguales
  - Test testHashCodeConsistente(): hashCode de dos expediciones con mismo código es igual
  - Test testEsValidaConDatosCompletos(): expedición válida retorna true
  - Test testEsValidaFechasInconsistentes(): fechaFin < fechaInicio retorna false
  - Test testToString(): retorna formato readable
  - _Requisitos: 7.1, 7.2, 7.3_
  - _Complejidad: Baja_

- [ ] 27. Implementar Suite de Unit Tests para DAO (ExpedicionDAO)
  - Crear clase ExpedicionDAOTest en src/test/java
  - Configurar @Before para limpiar BD de prueba antes de cada test
  - Test testCrearExpedicionValida(): insert exitoso y lectura retorna lo insertado
  - Test testCrearExpedicionCodigoDuplicado(): segundo insert con mismo código falla
  - Test testLeerTodasLasExpediciones(): readAll() retorna todas en orden
  - Test testLeerExpedicionPorCodigo(): readByCodigo retorna expedición correcta
  - Test testActualizarExpedicion(): update() modifica nombre, otros campos no cambian
  - Test testEliminarExpedicion(): delete() disminuye count en 1
  - Test testVerificarExistenciaCodigo(): existeCodigo retorna true/false correctamente
  - Test testManejoExcepcionConexionPerdida(): simular error BD, verificar DatabaseException
  - _Requisitos: 1.3, 2.4, 3.3, 4.2, 5.3, 7.2_
  - _Complejidad: Media_

- [ ] 28. Implementar Suite de Unit Tests para Controlador (ExpedicionController)
  - Crear clase ExpedicionControllerTest en src/test/java
  - Mockear MainFrame y ExpedicionDAO con Mockito
  - Test testCrearExpedicionConDatosValidos(): create llamado, UI actualizada
  - Test testCrearExpedicionConCodigoDuplicado(): mostrar warning, no crear
  - Test testModificarExpedicionSeleccionada(): update llamado, tabla actualizada
  - Test testEliminarExpedicionConConfirmacion(): delete llamado si usuario confirma
  - Test testBuscarExpedicionesCaseInsensitive(): filtrado correcto
  - Test testValidacionFechasInconsistentes(): validación lanza ValidacionException
  - Test testManejoErrorBaseDatos(): error capturado, mensaje mostrado a usuario
  - _Requisitos: 1.1, 3.1, 4.1, 7.1, 9.1_
  - _Complejidad: Media_

- [ ] 29. Implementar Property-Based Test para Property 1 (Round-Trip)
  - Crear clase ExpedicionPropertyTests en src/test/java
  - Usar jqwik con @Property
  - Generar Expedicion arbitraria con generador personalizado
  - CREATE expedición en BD
  - READ desde BD
  - ASSERT que todos los campos coinciden exactamente
  - Mínimo 100 iteraciones
  - Verificar que Property 1 pasa
  - **Property 1: Round-Trip para Expediciones**
  - **Valida: Requisitos 2.4, 5.3**
  - _Complejidad: Alta_

- [ ] 30. Implementar Property-Based Test para Property 2 (Invariante de Fechas)
  - Extender ExpedicionPropertyTests
  - Usar @Property con generador que asegura fechaInicio <= fechaFin
  - Para toda expedición creada en BD, verificar que fechaInicio <= fechaFin
  - Intentar crear expedición con fechaFin < fechaInicio, verificar rechazo
  - **Property 2: Invariante de Fechas**
  - **Valida: Requisito 7.3**
  - _Complejidad: Media_

- [ ] 31. Implementar Property-Based Test para Property 3 (Idempotencia de Lectura)
  - Extender ExpedicionPropertyTests
  - @Property: leer lista de expediciones N veces sin cambios
  - Verificar que todas las N lecturas retornan la misma lista
  - **Property 3: Idempotencia de Lectura**
  - **Valida: Requisito 2.2**
  - _Complejidad: Media_

- [ ] 32. Implementar Property-Based Test para Property 4 (Unicidad de Código)
  - Extender ExpedicionPropertyTests
  - @Property: crear expedición, intentar crear otra con mismo código
  - Verificar que segundo CREATE es rechazado con DatabaseException(DUPLICADO)
  - Verificar que no aparecen dos expediciones con mismo código en BD
  - **Property 4: Unicidad de Código**
  - **Valida: Requisito 7.2**
  - _Complejidad: Media_

- [ ] 33. Implementar Property-Based Test para Property 5 (Cobertura CRUD)
  - Extender ExpedicionPropertyTests
  - @Property: contar expediciones antes de operación, aplicar operación, contar después
  - CREATE: count aumenta en 1
  - READ: count no cambia
  - UPDATE: count no cambia
  - DELETE: count disminuye en 1
  - **Property 5: Cobertura Completa de CRUD**
  - **Valida: Requisitos 1.3, 2.2, 3.5, 4.2**
  - _Complejidad: Alta_

- [ ] 34. Implementar Property-Based Test para Property 6 (Integridad de Actualización)
  - Extender ExpedicionPropertyTests
  - @Property: crear expedición, modificar solo 1 campo
  - Verificar que campo modificado cambió, otros 4 campos no cambiaron
  - Realizar operación múltiples veces con diferentes campos
  - **Property 6: Integridad de Actualización**
  - **Valida: Requisito 8.3**
  - _Complejidad: Alta_

- [ ] 35. Implementar Generador jqwik Personalizado para Expedicion
  - Crear generador de Expedicion con restricciones realistas
  - Códigos: string 1-10 chars alfanuméricos con guiones
  - Nombres: string 1-255 chars, caracteres válidos
  - Fechas: LocalDate válidas, filtro para asegurar fechaInicio <= fechaFin
  - Sitios: string 1-255 chars
  - Usar Combinators.combine() para coordinar generadores
  - Usar filter() para aplicar restricciones de negocio
  - _Complejidad: Media_

- [ ] 36. Checkpoint - Todos los Tests Pasan
  - Ejecutar: mvn clean test
  - Verificar que 100% de unit tests pasan
  - Verificar que 6 propiedades de corrección pasan con jqwik
  - Verificar cobertura de código > 80% (incluir en maven-surefire)
  - Revisar logs de tests, no hay warnings
  - Preguntar al usuario si tests están listos para integración

---

### Fase 6: Ajustes Finales, Integración e Integración con Login (Requisito 10)

- [ ] 37. Implementar Pantalla de Login (Requisito 10)
  - Crear clase LoginFrame extends JFrame
  - Crear JPanel con JLabel y JTextField para Usuario y Contraseña
  - Crear JButton "Ingresar" y "Salir"
  - Implementar SimpleAuthenticator con credenciales hardcoded (ARCH_USER, ARCH_PASSWORD) para pruebas
  - Validar credenciales contra SimpleAuthenticator
  - IF válidas: crear MainFrame y mostrar
  - IF inválidas: mostrar "Usuario o contraseña incorrectos"
  - Implementar "Cerrar Sesión" en MainFrame que retorna a LoginFrame
  - Limpiar datos en memoria al cerrar sesión
  - _Requisitos: 10.1, 10.2, 10.3, 10.4_
  - _Complejidad: Media_

- [ ] 38. Crear Aplicación Principal (Main)
  - Crear clase App con método main(String[] args)
  - Inicializar DatabaseConnection.getInstance() y verificar conexión
  - IF conexión exitosa: crear y mostrar LoginFrame
  - IF conexión falla: mostrar error y permitir reintentar o salir
  - Configurar look & feel Nimbus o sistema nativo
  - Implementar shutdown hook para cerrar BD apropiadamente
  - _Requisitos: 5.1, 5.2_
  - _Complejidad: Baja_

- [ ] 39. Crear Script SQL de Inicialización
  - Crear script init.sql que contenga CREATE TABLE expediciones
  - Incluir todas las restricciones CHECK, UNIQUE, NOT NULL
  - Incluir índices en codigo_expedicion (UNIQUE) y nombre_expedicion (búsqueda)
  - Incluir timestamps created_at y updated_at
  - Documentar cómo ejecutar script en BD AWS RDS
  - _Requisitos: 5.1_
  - _Complejidad: Baja_

- [ ] 40. Crear Archivo de Configuración de Aplicación
  - Crear archivo application.properties en src/main/resources
  - Configurar: db.url, db.user, db.password, db.pool.size, db.timeout
  - Crear ApplicationConfig que carga propiedades
  - Implementar validación de propiedades requeridas en startup
  - Documentar valores de ejemplo para AWS RDS
  - NO incluir credenciales reales en repositorio (uso de .gitignore)
  - _Requisitos: 5.1_
  - _Complejidad: Baja_

- [ ] 41. Crear Documentación de Instalación y Despliegue
  - Documentar prerrequisitos: Java 17 JDK, MySQL 8.x, cuenta AWS
  - Pasos de instalación: clonar repo, configurar application.properties, mvn clean package
  - Pasos de configuración BD: crear esquema, ejecutar init.sql, verificar conexión
  - Instrucciones de ejecución: java -jar app.jar
  - Troubleshooting común: errores de conexión, credenciales, versiones
  - _Complejidad: Baja_

- [ ] 42. Compilación y Empaquetamiento
  - Ejecutar: mvn clean compile
  - Verificar que no hay errores de compilación
  - Ejecutar: mvn package
  - Verificar que jar ejecutable se genera en target/
  - Probar ejecución local: java -jar target/app.jar
  - Verificar que aplicación inicia, login funciona, conexión BD OK
  - _Complejidad: Baja_

- [ ] 43. Checkpoint - Aplicación Lista para Despliegue
  - Verificar que aplicación compila sin errores
  - Verificar que todos los tests pasan
  - Verificar que empaquetamiento jar funciona
  - Probar inicio de aplicación completo: login → operaciones CRUD → cierre
  - Revisar documentación de despliegue
  - Preguntar al usuario si aplicación está lista

---

### Fase 7: Despliegue y Optimización

- [ ] 44. Despliegue en Ambiente de Producción
  - Transferir jar ejecutable a servidor de producción o máquina usuario
  - Configurar application.properties con credenciales reales AWS RDS
  - Inicializar base de datos de producción ejecutando init.sql
  - Probar operaciones CRUD en ambiente real
  - Verificar logs, manejo de errores
  - Documentar punto de contacto de soporte
  - _Complejidad: Media_

- [ ] 45. Optimización de Consultas y Performance
  - Revisar logs de ejecución para queries lentas
  - Verificar que índices en código y nombre están siendo usados
  - Considerar pagination si tabla expediciones crece > 10000 registros
  - Optimizar pool de conexiones HikariCP (ajustar maximumPoolSize si necesario)
  - Probar aplicación con múltiples usuarios concurrentes
  - _Complejidad: Baja_

- [ ] 46. Final Checkpoint - Producción Operacional
  - Verificar que todas las operaciones funcionan en producción
  - Verificar que BD está sincronizada
  - Revisar logs de aplicación
  - Crear backup de BD
  - Documentar plan de mantenimiento y backup
  - Preguntar al usuario si todo está en producción y funcionando

---

## Notes

- **Tareas marcadas con `*`**: Son opcionales para MVP rápido; pueden omitirse sin afectar funcionalidad core
- **Cada tarea referencias requisitos específicos**: Para trazabilidad directa de requirements a implementación
- **Checkpoints estratégicos**: Validan componentes antes de avanzar a fase siguiente
- **Property-Based Testing**: Validan universales correctness properties del sistema
- **Arquitectura MVC estricta**: Separación de responsabilidades permite evolución independiente de cada capa
- **Seguridad**: Prepared statements previenen inyección SQL, credenciales en archivo config separado
- **Manejo de errores**: Excepciones personalizadas y mensajes user-friendly sin detalles técnicos
- **Persistencia**: Sincronización inmediata con AWS RDS, Pool HikariCP para conexiones eficientes
- **Validación**: Dos niveles (controlador y DAO) garantizan integridad referencial
- **Testing Dual**: Unit tests validan casos específicos, property tests validan invariantes universales

---

## Task Dependency Graph

```json
{
  "waves": [
    {
      "id": 0,
      "tasks": [
        "1",
        "2",
        "3",
        "4"
      ],
      "description": "Configuración inicial: proyecto Maven, BD AWS RDS, conexión JDBC, excepciones"
    },
    {
      "id": 1,
      "tasks": [
        "5"
      ],
      "description": "Checkpoint Base de Datos - antes de pasar a modelo"
    },
    {
      "id": 2,
      "tasks": [
        "6",
        "7",
        "8",
        "9"
      ],
      "description": "Modelo Layer: Entity Expedicion, DAO CRUD, validaciones, excepciones BD"
    },
    {
      "id": 3,
      "tasks": [
        "10"
      ],
      "description": "Checkpoint Modelo - verificar operaciones CRUD, propiedades round-trip"
    },
    {
      "id": 4,
      "tasks": [
        "11",
        "12",
        "13",
        "14"
      ],
      "description": "Vista Layer Panels: Formulario, Tabla, Botones, Búsqueda (independientes entre sí)"
    },
    {
      "id": 5,
      "tasks": [
        "15"
      ],
      "description": "Vista Layer MainFrame - integra todos los panels"
    },
    {
      "id": 6,
      "tasks": [
        "16"
      ],
      "description": "Checkpoint Vista - UI operacional sin lógica aún"
    },
    {
      "id": 7,
      "tasks": [
        "17",
        "18",
        "19",
        "20",
        "21",
        "22",
        "23",
        "24"
      ],
      "description": "Controlador Layer: inicialización, CRUD operations, validaciones, búsqueda, manejo errores"
    },
    {
      "id": 8,
      "tasks": [
        "25"
      ],
      "description": "Checkpoint Controlador - CRUD funcional con Vista y Modelo"
    },
    {
      "id": 9,
      "tasks": [
        "26",
        "27",
        "28"
      ],
      "description": "Unit Tests: Modelo, DAO, Controlador (independientes)"
    },
    {
      "id": 10,
      "tasks": [
        "29",
        "30",
        "31",
        "32",
        "33",
        "34"
      ],
      "description": "Property-Based Tests: 6 propiedades de corrección con jqwik"
    },
    {
      "id": 11,
      "tasks": [
        "35"
      ],
      "description": "Generador jqwik personalizado para Expedicion (soporte de PBT)"
    },
    {
      "id": 12,
      "tasks": [
        "36"
      ],
      "description": "Checkpoint Testing - todos los tests (unit + property) pasan"
    },
    {
      "id": 13,
      "tasks": [
        "37",
        "38",
        "39",
        "40"
      ],
      "description": "Integración final: Login, Main App, scripts SQL, configuración"
    },
    {
      "id": 14,
      "tasks": [
        "41",
        "42"
      ],
      "description": "Documentación y empaquetamiento: guías, compilación, jar"
    },
    {
      "id": 15,
      "tasks": [
        "43"
      ],
      "description": "Checkpoint Pre-Despliegue - aplicación compilada, tested, empaquetada"
    },
    {
      "id": 16,
      "tasks": [
        "44",
        "45",
        "46"
      ],
      "description": "Despliegue producción y optimización final"
    }
  ]
}
```

---

## Estimación de Complejidad y Esfuerzo

| Fase | Tareas | Complejidad Promedio | Esfuerzo Estimado | Duración |
|------|--------|----------------------|-------------------|----------|
| 1: Configuración Inicial | 1-5 | Media | 3-4 horas | 1 día |
| 2: Modelo | 6-10 | Media | 6-8 horas | 1.5 días |
| 3: Vista | 11-16 | Baja-Media | 8-10 horas | 1.5 días |
| 4: Controlador | 17-25 | Media | 10-12 horas | 2 días |
| 5: Testing | 26-36 | Media-Alta | 12-16 horas | 2 días |
| 6: Integración Final | 37-43 | Baja-Media | 5-6 horas | 1 día |
| 7: Despliegue | 44-46 | Baja | 2-3 horas | 0.5 días |
| **TOTAL** | **46** | **Media** | **46-59 horas** | **~9 días** |

---

## Criterios de Completitud por Tarea

Cada tarea se considera completada cuando:

1. **Compilación**: Código compila sin errores ni warnings
2. **Tests**: Tests específicos de la tarea pasan (si aplica)
3. **Funcionalidad**: Cumple con requisitos listados en sección _Requisitos_
4. **Integración**: Se integra sin romper tareas anteriores
5. **Documentación**: Métodos tienen Javadoc, clases documentadas
6. **Code Review**: Código sigue estándares Java 17 y convenciones del proyecto

---

