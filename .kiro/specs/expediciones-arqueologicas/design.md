# Diseño Técnico: Sistema de Gestión de Expediciones Arqueológicas

## Overview

El Sistema de Gestión de Expediciones Arqueológicas es una aplicación de escritorio Java Swing que implementa el patrón arquitectónico MVC (Model-View-Controller) para gestionar el ciclo de vida completo de expediciones arqueológicas. La aplicación proporciona operaciones CRUD persistentes contra una base de datos MySQL 8.x alojada en AWS RDS, utilizando JDBC como puente de conectividad.

La arquitectura está diseñada para maximizar la separación de responsabilidades, permitiendo que cada capa (Modelo, Vista, Controlador) evolucione independientemente. La persistencia es garantizada mediante sincronización inmediata con la base de datos, mientras que la interfaz gráfica permanece responsiva incluso durante operaciones de I/O.

---

## Architecture

### Componentes Principales de la Arquitectura MVC

```
┌─────────────────────────────────────────────────────────────┐
│                    CAPA DE PRESENTACIÓN (VISTA)             │
│  ┌──────────────────────────────────────────────────────┐   │
│  │  MainFrame (JFrame principal)                        │   │
│  │  ├─ PanelFormulario (JPanel - entrada de datos)      │   │
│  │  ├─ PanelTabla (JPanel - visualización con JTable)   │   │
│  │  ├─ PanelBotones (JPanel - acciones)                 │   │
│  │  └─ PanelBusqueda (JPanel - búsqueda y filtrado)     │   │
│  └──────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
            ▲                                    │
            │  notificación de eventos          │ actualización UI
            │  (ActionListener)                 │ (SwingUtilities)
            │                                    ▼
┌─────────────────────────────────────────────────────────────┐
│             CAPA DE CONTROL (CONTROLADOR)                   │
│  ┌──────────────────────────────────────────────────────┐   │
│  │  ExpedicionController                               │   │
│  │  ├─ crearExpedicion()                               │   │
│  │  ├─ listarExpediciones()                            │   │
│  │  ├─ modificarExpedicion()                           │   │
│  │  ├─ eliminarExpedicion()                            │   │
│  │  ├─ buscarExpediciones()                            │   │
│  │  ├─ validarExpedicion()                             │   │
│  │  └─ manejarErrores()                                │   │
│  └──────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
            ▲                                    │
            │  lectura/escritura de datos       │ modelos de datos
            │  (DAO)                            │ (Entity)
            │                                    ▼
┌─────────────────────────────────────────────────────────────┐
│             CAPA DE DATOS (MODELO)                          │
│  ┌──────────────────────────────────────────────────────┐   │
│  │  Expedicion (Entity/DTO)                             │   │
│  │  ├─ codigo: String                                   │   │
│  │  ├─ nombre: String                                   │   │
│  │  ├─ fechaInicio: LocalDate                           │   │
│  │  ├─ fechaFin: LocalDate                              │   │
│  │  └─ sitioArqueologico: String                        │   │
│  ├──────────────────────────────────────────────────────┤   │
│  │  ExpedicionDAO (Data Access Object)                  │   │
│  │  ├─ create(Expedicion)                               │   │
│  │  ├─ readAll()                                        │   │
│  │  ├─ readById(codigo)                                 │   │
│  │  ├─ update(Expedicion)                               │   │
│  │  └─ delete(codigo)                                   │   │
│  ├──────────────────────────────────────────────────────┤   │
│  │  DatabaseConnection                                  │   │
│  │  ├─ conexionAWSRDS: Connection                       │   │
│  │  ├─ conectar()                                       │   │
│  │  ├─ desconectar()                                    │   │
│  │  └─ verificarConexion()                              │   │
│  └──────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
            │
            │  JDBC
            ▼
┌─────────────────────────────────────────────────────────────┐
│         MySQL 8.x en AWS RDS                                │
│  ┌──────────────────────────────────────────────────────┐   │
│  │  Tabla: expediciones                                 │   │
│  │  ├─ codigo_expedicion (VARCHAR, PK)                 │   │
│  │  ├─ nombre_expedicion (VARCHAR)                      │   │
│  │  ├─ fecha_inicio (DATE)                              │   │
│  │  ├─ fecha_fin (DATE)                                 │   │
│  │  └─ sitio_arqueologico (VARCHAR)                     │   │
│  └──────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
```

---

## Components and Interfaces

### 1. Capa de Presentación (VISTA)

#### 1.1 MainFrame (JFrame Principal)

```java
public class MainFrame extends JFrame {
    // Propiedades
    private PanelFormulario panelFormulario;
    private PanelTabla panelTabla;
    private PanelBotones panelBotones;
    private PanelBusqueda panelBusqueda;
    private ExpedicionController controlador;
    
    // Constructor
    public MainFrame(ExpedicionController controlador) {
        this.controlador = controlador;
        inicializarComponentes();
        configurarLayout();
        establecerEventosActualizacion();
    }
    
    // Métodos principales
    private void inicializarComponentes()
    private void configurarLayout()
    private void establecerEventosActualizacion()
    public void actualizarTablaExpediciones(List<Expedicion> expediciones)
    public void mostrarMensaje(String mensaje, int tipoMensaje)
    public Expedicion getExpedicionSeleccionada()
    public void limpiarFormulario()
    public void habilitarFormulario(boolean habilitado)
}
```

**Responsabilidades:**
- Contener la ventana principal de la aplicación
- Coordinar todos los paneles secundarios
- Responder a eventos de actualización del controlador
- Mostrar mensajes de error, éxito y confirmación

---

#### 1.2 PanelFormulario (JPanel - Entrada de Datos)

```java
public class PanelFormulario extends JPanel {
    // Componentes de entrada
    private JTextField txtCodigo;
    private JTextField txtNombre;
    private JTextField txtFechaInicio;      // Formato: DD/MM/YYYY
    private JTextField txtFechaFin;         // Formato: DD/MM/YYYY
    private JTextField txtSitioArqueologico;
    
    // Etiquetas
    private JLabel lblCodigo;
    private JLabel lblNombre;
    private JLabel lblFechaInicio;
    private JLabel lblFechaFin;
    private JLabel lblSitioArqueologico;
    
    // Constructor
    public PanelFormulario() {
        inicializarComponentes();
        configurarLayout();
    }
    
    // Métodos de acceso
    public String getCodigo()
    public String getNombre()
    public String getFechaInicio()
    public String getFechaFin()
    public String getSitioArqueologico()
    
    // Métodos de manipulación
    public void setCampos(Expedicion expedicion)
    public void limpiar()
    public void habilitarCampos(boolean habilitados)
    public void deshabilitarCampoCodigoEnEdicion()
}
```

**Responsabilidades:**
- Proporcionar interfaz para entrada de datos de expediciones
- Validación visual (ej: indicación de formato de fecha)
- Permitir precarga de datos para operación de edición
- Facilitar limpieza de formulario

---

#### 1.3 PanelTabla (JPanel - Visualización)

```java
public class PanelTabla extends JPanel {
    // Componentes de tabla
    private JTable tablaExpediciones;
    private DefaultTableModel modeloTabla;
    private JScrollPane scrollPane;
    
    // Columnas
    private static final String[] COLUMNAS = {
        "Código", "Nombre Expedición", "Fecha Inicio", "Fecha Fin", "Sitio Arqueológico"
    };
    
    // Constructor
    public PanelTabla() {
        inicializarComponentes();
        configurarLayout();
        configurarTabla();
    }
    
    // Métodos de actualización
    public void actualizarDatos(List<Expedicion> expediciones)
    public void limpiar()
    public void agregarFila(Expedicion expedicion)
    public void actualizarFila(int filaIndex, Expedicion expedicion)
    public void eliminarFila(int filaIndex)
    
    // Métodos de selección
    public int getFilaSeleccionada()
    public Expedicion getExpedicionSeleccionada()
    
    // Métodos de visualización
    public void mostrarMensajeVacio()
    public void ocultarMensajeVacio()
}
```

**Responsabilidades:**
- Mostrar todas las expediciones en formato tabular
- Permitir selección de filas
- Actualizar automáticamente tras operaciones CRUD
- Mostrar mensaje cuando no hay datos

---

#### 1.4 PanelBotones (JPanel - Acciones)

```java
public class PanelBotones extends JPanel {
    // Botones de acción
    private JButton btnNueva;
    private JButton btnEditar;
    private JButton btnEliminar;
    private JButton btnLimpiar;
    
    // Constructor con listeners
    public PanelBotones(ActionListener escuchador) {
        inicializarComponentes();
        configurarLayout();
        asignarListeners(escuchador);
    }
    
    // Métodos de control
    public void habilitarBotones(boolean habilitar)
    public void habilitarBotonEditar(boolean habilitar)
    public void habilitarBotonEliminar(boolean habilitar)
    
    // Métodos de acceso a eventos
    public void agregarListenerNueva(ActionListener listener)
    public void agregarListenerEditar(ActionListener listener)
    public void agregarListenerEliminar(ActionListener listener)
    public void agregarListenerLimpiar(ActionListener listener)
}
```

**Responsabilidades:**
- Proporcionar interfaz de botones para acciones CRUD
- Gestionar habilitación/deshabilitación de botones según contexto
- Disparar eventos de acción hacia el controlador

---

#### 1.5 PanelBusqueda (JPanel - Búsqueda y Filtrado)

```java
public class PanelBusqueda extends JPanel {
    // Componentes de búsqueda
    private JTextField txtBusqueda;
    private JButton btnBuscar;
    private JButton btnMostrarTodos;
    
    // Etiqueta
    private JLabel lblBusqueda;
    
    // Constructor
    public PanelBusqueda(ActionListener escuchador) {
        inicializarComponentes();
        configurarLayout();
        asignarListeners(escuchador);
    }
    
    // Métodos de acceso
    public String getTerminoBusqueda()
    public void limpiarBusqueda()
    
    // Métodos de evento
    public void agregarListenerBuscar(ActionListener listener)
    public void agregarListenerMostrarTodos(ActionListener listener)
}
```

**Responsabilidades:**
- Proporcionar campo para búsqueda por nombre de expedición
- Disparar eventos de búsqueda
- Permitir mostrar todas las expediciones nuevamente

---

### 2. Capa de Control (CONTROLADOR)

#### 2.1 ExpedicionController

```java
public class ExpedicionController {
    // Dependencias
    private ExpedicionDAO dao;
    private MainFrame vista;
    
    // Estado
    private Expedicion expedicionEnEdicion;
    private List<Expedicion> expedicionesActuales;
    private List<Expedicion> expedicionesFiltradas;
    
    // Constructor
    public ExpedicionController(ExpedicionDAO dao, MainFrame vista) {
        this.dao = dao;
        this.vista = vista;
        this.expedicionesActuales = new ArrayList<>();
        this.expedicionesFiltradas = new ArrayList<>();
        inicializarListeners();
        cargarExpedicionesinicial();
    }
    
    // Operaciones CRUD
    public void crearExpedicion(String codigo, String nombre, 
                               String fechaInicio, String fechaFin, 
                               String sitioArqueologico) {
        try {
            // Validar datos de entrada
            validarExpedicion(codigo, nombre, fechaInicio, fechaFin, sitioArqueologico);
            
            // Verificar duplicado de código
            if (dao.existeCodigo(codigo)) {
                vista.mostrarMensaje("El código de expedición ya existe", 
                                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Crear objeto
            Expedicion expedicion = construirExpedicion(codigo, nombre, 
                                                       fechaInicio, fechaFin, 
                                                       sitioArqueologico);
            
            // Persistir
            dao.create(expedicion);
            
            // Actualizar lista y UI
            expedicionesActuales.add(expedicion);
            vista.actualizarTablaExpediciones(expedicionesActuales);
            vista.limpiarFormulario();
            vista.mostrarMensaje("Expedición creada exitosamente", 
                                JOptionPane.INFORMATION_MESSAGE);
            
        } catch (ValidacionException ex) {
            vista.mostrarMensaje(ex.getMessage(), JOptionPane.ERROR_MESSAGE);
        } catch (DatabaseException ex) {
            manejarErrorBaseDatos(ex);
        }
    }
    
    public void modificarExpedicion(String codigo, String nombre, 
                                   String fechaInicio, String fechaFin, 
                                   String sitioArqueologico) {
        try {
            // Validar que hay expedición seleccionada
            if (expedicionEnEdicion == null) {
                vista.mostrarMensaje("Seleccione una expedición para editar", 
                                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Validar datos
            validarExpedicion(codigo, nombre, fechaInicio, fechaFin, sitioArqueologico);
            
            // Verificar duplicado (excepto código actual)
            if (!codigo.equals(expedicionEnEdicion.getCodigo()) && 
                dao.existeCodigo(codigo)) {
                vista.mostrarMensaje("El código de expedición ya existe", 
                                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Actualizar objeto
            Expedicion expedicion = construirExpedicion(codigo, nombre, 
                                                       fechaInicio, fechaFin, 
                                                       sitioArqueologico);
            expedicion.setId(expedicionEnEdicion.getId());
            
            // Persistir cambios
            dao.update(expedicion);
            
            // Actualizar lista y UI
            int indice = expedicionesActuales.indexOf(expedicionEnEdicion);
            expedicionesActuales.set(indice, expedicion);
            vista.actualizarTablaExpediciones(expedicionesActuales);
            vista.limpiarFormulario();
            expedicionEnEdicion = null;
            vista.mostrarMensaje("Expedición actualizada exitosamente", 
                                JOptionPane.INFORMATION_MESSAGE);
            
        } catch (ValidacionException ex) {
            vista.mostrarMensaje(ex.getMessage(), JOptionPane.ERROR_MESSAGE);
        } catch (DatabaseException ex) {
            manejarErrorBaseDatos(ex);
        }
    }
    
    public void eliminarExpedicion(String codigo) {
        try {
            // Confirmar con usuario
            int resultado = JOptionPane.showConfirmDialog(null, 
                "¿Está seguro de que desea eliminar esta expedición?", 
                "Confirmar eliminación", 
                JOptionPane.YES_NO_OPTION);
            
            if (resultado != JOptionPane.YES_OPTION) {
                return;
            }
            
            // Persistir eliminación
            dao.delete(codigo);
            
            // Actualizar lista y UI
            expedicionesActuales.removeIf(e -> e.getCodigo().equals(codigo));
            vista.actualizarTablaExpediciones(expedicionesActuales);
            vista.limpiarFormulario();
            expedicionEnEdicion = null;
            vista.mostrarMensaje("Expedición eliminada exitosamente", 
                                JOptionPane.INFORMATION_MESSAGE);
            
        } catch (DatabaseException ex) {
            manejarErrorBaseDatos(ex);
        }
    }
    
    public void listarExpediciones() {
        try {
            expedicionesActuales = dao.readAll();
            vista.actualizarTablaExpediciones(expedicionesActuales);
        } catch (DatabaseException ex) {
            manejarErrorBaseDatos(ex);
        }
    }
    
    public void buscarExpediciones(String termino) {
        try {
            if (termino == null || termino.isEmpty()) {
                vista.actualizarTablaExpediciones(expedicionesActuales);
                return;
            }
            
            String terminoMinuscula = termino.toLowerCase();
            expedicionesFiltradas = expedicionesActuales.stream()
                .filter(e -> e.getNombre().toLowerCase().contains(terminoMinuscula))
                .collect(Collectors.toList());
            
            vista.actualizarTablaExpediciones(expedicionesFiltradas);
            
        } catch (Exception ex) {
            vista.mostrarMensaje("Error en búsqueda: " + ex.getMessage(), 
                                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Métodos de validación
    private void validarExpedicion(String codigo, String nombre, 
                                   String fechaInicio, String fechaFin, 
                                   String sitioArqueologico) throws ValidacionException {
        
        // Validar código
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new ValidacionException("El código de expedición es requerido");
        }
        if (codigo.length() > 10) {
            throw new ValidacionException("El código no puede exceder 10 caracteres");
        }
        
        // Validar nombre
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new ValidacionException("El nombre de expedición es requerido");
        }
        if (nombre.length() > 255) {
            throw new ValidacionException("El nombre no puede exceder 255 caracteres");
        }
        
        // Validar fechas
        LocalDate inicio = parsearFecha(fechaInicio);
        LocalDate fin = parsearFecha(fechaFin);
        
        if (inicio.isAfter(fin)) {
            throw new ValidacionException("La fecha de fin debe ser igual o posterior a la fecha de inicio");
        }
        
        // Validar sitio arqueológico
        if (sitioArqueologico == null || sitioArqueologico.trim().isEmpty()) {
            throw new ValidacionException("El sitio arqueológico es requerido");
        }
        if (sitioArqueologico.length() > 255) {
            throw new ValidacionException("El sitio arqueológico no puede exceder 255 caracteres");
        }
    }
    
    private LocalDate parsearFecha(String fecha) throws ValidacionException {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            return LocalDate.parse(fecha, formatter);
        } catch (DateTimeParseException ex) {
            throw new ValidacionException("Ingrese las fechas en formato DD/MM/YYYY");
        }
    }
    
    // Métodos de utilidad
    private Expedicion construirExpedicion(String codigo, String nombre, 
                                          String fechaInicio, String fechaFin, 
                                          String sitioArqueologico) throws ValidacionException {
        Expedicion expedicion = new Expedicion();
        expedicion.setCodigo(codigo);
        expedicion.setNombre(nombre);
        expedicion.setFechaInicio(parsearFecha(fechaInicio));
        expedicion.setFechaFin(parsearFecha(fechaFin));
        expedicion.setSitioArqueologico(sitioArqueologico);
        return expedicion;
    }
    
    private void manejarErrorBaseDatos(DatabaseException ex) {
        if (ex.getCausa() == DatabaseException.Causa.SIN_CONEXION) {
            vista.mostrarMensaje("Error de conexión con la base de datos. " + 
                                "Los datos cargados en memoria aún están disponibles.", 
                                JOptionPane.ERROR_MESSAGE);
        } else {
            vista.mostrarMensaje("Error en la base de datos: " + ex.getMensajeUsuario(), 
                                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void inicializarListeners() {
        // Listeners de botones conectados a la vista
    }
    
    private void cargarExpedicionesinicial() {
        listarExpediciones();
    }
}
```

**Responsabilidades:**
- Orquestar operaciones CRUD
- Validar entrada del usuario
- Sincronizar estado entre modelo y vista
- Manejar excepciones y errores
- Notificar a la vista de cambios

---

### 3. Capa de Datos (MODELO)

#### 3.1 Expedicion (Entity/DTO)

```java
public class Expedicion {
    // Propiedades
    private int id;                          // ID interno BD
    private String codigo;                   // Código alpanumérico (PK)
    private String nombre;                   // Máximo 255 caracteres
    private LocalDate fechaInicio;          // Fecha YYYY-MM-DD
    private LocalDate fechaFin;             // Fecha YYYY-MM-DD
    private String sitioArqueologico;       // Máximo 255 caracteres
    
    // Constructores
    public Expedicion() {}
    
    public Expedicion(String codigo, String nombre, LocalDate fechaInicio, 
                     LocalDate fechaFin, String sitioArqueologico) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.sitioArqueologico = sitioArqueologico;
    }
    
    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }
    
    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }
    
    public String getSitioArqueologico() { return sitioArqueologico; }
    public void setSitioArqueologico(String sitioArqueologico) { 
        this.sitioArqueologico = sitioArqueologico; 
    }
    
    // Métodos de validación
    public boolean esValida() {
        return codigo != null && !codigo.isEmpty() &&
               nombre != null && !nombre.isEmpty() &&
               fechaInicio != null && fechaFin != null &&
               !fechaInicio.isAfter(fechaFin) &&
               sitioArqueologico != null && !sitioArqueologico.isEmpty();
    }
    
    // Override equals y hashCode
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Expedicion)) return false;
        Expedicion otra = (Expedicion) obj;
        return this.codigo.equals(otra.codigo);
    }
    
    @Override
    public int hashCode() {
        return codigo.hashCode();
    }
    
    // Override toString
    @Override
    public String toString() {
        return String.format("Expedicion[código=%s, nombre=%s, inicio=%s, fin=%s, sitio=%s]",
                           codigo, nombre, fechaInicio, fechaFin, sitioArqueologico);
    }
}
```

**Responsabilidades:**
- Representar una expedición arqueológica
- Validar que cumpla invariantes básicos
- Proporcionar acceso a propiedades

---

#### 3.2 ExpedicionDAO (Data Access Object)

```java
public class ExpedicionDAO {
    // Conexión a BD
    private DatabaseConnection conexion;
    
    // SQL Statements (preparadas para inyección)
    private static final String SQL_INSERT = 
        "INSERT INTO expediciones (codigo_expedicion, nombre_expedicion, " +
        "fecha_inicio, fecha_fin, sitio_arqueologico) VALUES (?, ?, ?, ?, ?)";
    
    private static final String SQL_SELECT_ALL = 
        "SELECT id, codigo_expedicion, nombre_expedicion, fecha_inicio, " +
        "fecha_fin, sitio_arqueologico FROM expediciones ORDER BY codigo_expedicion ASC";
    
    private static final String SQL_SELECT_BY_CODIGO = 
        "SELECT id, codigo_expedicion, nombre_expedicion, fecha_inicio, " +
        "fecha_fin, sitio_arqueologico FROM expediciones WHERE codigo_expedicion = ?";
    
    private static final String SQL_UPDATE = 
        "UPDATE expediciones SET nombre_expedicion = ?, fecha_inicio = ?, " +
        "fecha_fin = ?, sitio_arqueologico = ? WHERE codigo_expedicion = ?";
    
    private static final String SQL_DELETE = 
        "DELETE FROM expediciones WHERE codigo_expedicion = ?";
    
    private static final String SQL_EXISTS = 
        "SELECT COUNT(*) FROM expediciones WHERE codigo_expedicion = ?";
    
    // Constructor
    public ExpedicionDAO(DatabaseConnection conexion) {
        this.conexion = conexion;
    }
    
    // CRUD Operations
    public void create(Expedicion expedicion) throws DatabaseException {
        try (Connection conn = conexion.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(SQL_INSERT)) {
            
            pstmt.setString(1, expedicion.getCodigo());
            pstmt.setString(2, expedicion.getNombre());
            pstmt.setDate(3, java.sql.Date.valueOf(expedicion.getFechaInicio()));
            pstmt.setDate(4, java.sql.Date.valueOf(expedicion.getFechaFin()));
            pstmt.setString(5, expedicion.getSitioArqueologico());
            
            int filasAfectadas = pstmt.executeUpdate();
            if (filasAfectadas == 0) {
                throw new DatabaseException("No se pudo insertar la expedición", 
                                           DatabaseException.Causa.FALLO_INSERCION);
            }
            
        } catch (SQLException ex) {
            throw new DatabaseException("Error al crear expedición: " + ex.getMessage(), 
                                       DatabaseException.Causa.ERROR_SQL, ex);
        }
    }
    
    public List<Expedicion> readAll() throws DatabaseException {
        List<Expedicion> expediciones = new ArrayList<>();
        
        try (Connection conn = conexion.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                expediciones.add(mapearResultSet(rs));
            }
            
        } catch (SQLException ex) {
            throw new DatabaseException("Error al leer expediciones: " + ex.getMessage(), 
                                       DatabaseException.Causa.ERROR_SQL, ex);
        }
        
        return expediciones;
    }
    
    public Expedicion readByCodigo(String codigo) throws DatabaseException {
        try (Connection conn = conexion.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(SQL_SELECT_BY_CODIGO)) {
            
            pstmt.setString(1, codigo);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapearResultSet(rs);
                }
            }
            
        } catch (SQLException ex) {
            throw new DatabaseException("Error al leer expedición: " + ex.getMessage(), 
                                       DatabaseException.Causa.ERROR_SQL, ex);
        }
        
        return null;
    }
    
    public void update(Expedicion expedicion) throws DatabaseException {
        try (Connection conn = conexion.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(SQL_UPDATE)) {
            
            pstmt.setString(1, expedicion.getNombre());
            pstmt.setDate(2, java.sql.Date.valueOf(expedicion.getFechaInicio()));
            pstmt.setDate(3, java.sql.Date.valueOf(expedicion.getFechaFin()));
            pstmt.setString(4, expedicion.getSitioArqueologico());
            pstmt.setString(5, expedicion.getCodigo());
            
            int filasAfectadas = pstmt.executeUpdate();
            if (filasAfectadas == 0) {
                throw new DatabaseException("No se encontró la expedición para actualizar", 
                                           DatabaseException.Causa.NO_ENCONTRADO);
            }
            
        } catch (SQLException ex) {
            throw new DatabaseException("Error al actualizar expedición: " + ex.getMessage(), 
                                       DatabaseException.Causa.ERROR_SQL, ex);
        }
    }
    
    public void delete(String codigo) throws DatabaseException {
        try (Connection conn = conexion.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(SQL_DELETE)) {
            
            pstmt.setString(1, codigo);
            int filasAfectadas = pstmt.executeUpdate();
            if (filasAfectadas == 0) {
                throw new DatabaseException("No se encontró la expedición para eliminar", 
                                           DatabaseException.Causa.NO_ENCONTRADO);
            }
            
        } catch (SQLException ex) {
            throw new DatabaseException("Error al eliminar expedición: " + ex.getMessage(), 
                                       DatabaseException.Causa.ERROR_SQL, ex);
        }
    }
    
    public boolean existeCodigo(String codigo) throws DatabaseException {
        try (Connection conn = conexion.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(SQL_EXISTS)) {
            
            pstmt.setString(1, codigo);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
            
        } catch (SQLException ex) {
            throw new DatabaseException("Error al verificar código: " + ex.getMessage(), 
                                       DatabaseException.Causa.ERROR_SQL, ex);
        }
        
        return false;
    }
    
    // Método privado de mapeo
    private Expedicion mapearResultSet(ResultSet rs) throws SQLException {
        Expedicion exp = new Expedicion();
        exp.setId(rs.getInt("id"));
        exp.setCodigo(rs.getString("codigo_expedicion"));
        exp.setNombre(rs.getString("nombre_expedicion"));
        exp.setFechaInicio(rs.getDate("fecha_inicio").toLocalDate());
        exp.setFechaFin(rs.getDate("fecha_fin").toLocalDate());
        exp.setSitioArqueologico(rs.getString("sitio_arqueologico"));
        return exp;
    }
}
```

**Responsabilidades:**
- Encapsular acceso a datos
- Ejecutar operaciones CRUD contra la BD
- Mapear resultados SQL a objetos Java
- Usar sentencias preparadas para prevenir inyección SQL

---

#### 3.3 DatabaseConnection

```java
public class DatabaseConnection {
    // Configuración AWS RDS
    private static final String URL = 
        "jdbc:mysql://[ENDPOINT-AWS-RDS]:3306/expediciones_arqueologicas";
    private static final String USUARIO = "[AWS-RDS-USERNAME]";
    private static final String PASSWORD = "[AWS-RDS-PASSWORD]";
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    
    // Pool de conexiones (opcional pero recomendado)
    private HikariDataSource dataSource;
    private static DatabaseConnection instancia;
    
    // Patrón Singleton
    private DatabaseConnection() {
        inicializarDriver();
        crearPool();
    }
    
    public static synchronized DatabaseConnection getInstance() {
        if (instancia == null) {
            instancia = new DatabaseConnection();
        }
        return instancia;
    }
    
    // Inicialización
    private void inicializarDriver() {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException("Driver MySQL no encontrado", ex);
        }
    }
    
    private void crearPool() {
        try {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(URL);
            config.setUsername(USUARIO);
            config.setPassword(PASSWORD);
            config.setMaximumPoolSize(5);
            config.setMinimumIdle(2);
            config.setConnectionTimeout(10000);
            config.setIdleTimeout(600000);
            
            dataSource = new HikariDataSource(config);
        } catch (Exception ex) {
            throw new RuntimeException("Error configurando pool de conexiones", ex);
        }
    }
    
    // Obtener conexión
    public Connection obtenerConexion() throws DatabaseException {
        try {
            if (dataSource == null) {
                throw new DatabaseException("Pool de conexiones no inicializado", 
                                           DatabaseException.Causa.SIN_CONEXION);
            }
            return dataSource.getConnection();
        } catch (SQLException ex) {
            throw new DatabaseException("Error obteniendo conexión: " + ex.getMessage(), 
                                       DatabaseException.Causa.SIN_CONEXION, ex);
        }
    }
    
    // Cerrar recursos
    public void cerrar() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
    
    // Verificar conexión
    public boolean verificarConexion() {
        try (Connection conn = obtenerConexion()) {
            return conn.isValid(5);
        } catch (Exception ex) {
            return false;
        }
    }
}
```

**Responsabilidades:**
- Gestionar conexiones a AWS RDS
- Implementar pool de conexiones
- Proporcionar acceso a conexiones JDBC
- Manejar ciclo de vida de conexiones

---

#### 3.4 Excepciones Personalizadas

```java
public class DatabaseException extends Exception {
    public enum Causa {
        SIN_CONEXION,
        ERROR_SQL,
        FALLO_INSERCION,
        NO_ENCONTRADO,
        DUPLICADO
    }
    
    private Causa causa;
    private Throwable causaOriginal;
    
    public DatabaseException(String mensaje, Causa causa) {
        super(mensaje);
        this.causa = causa;
    }
    
    public DatabaseException(String mensaje, Causa causa, Throwable causaOriginal) {
        super(mensaje, causaOriginal);
        this.causa = causa;
        this.causaOriginal = causaOriginal;
    }
    
    public Causa getCausa() { return causa; }
    
    public String getMensajeUsuario() {
        switch (causa) {
            case SIN_CONEXION:
                return "No se puede conectar a la base de datos";
            case ERROR_SQL:
                return "Error en la operación de base de datos";
            case FALLO_INSERCION:
                return "No se pudo guardar el registro";
            case NO_ENCONTRADO:
                return "El registro no existe";
            case DUPLICADO:
                return "El código ya existe";
            default:
                return "Error desconocido";
        }
    }
}

public class ValidacionException extends Exception {
    public ValidacionException(String mensaje) {
        super(mensaje);
    }
}
```

---

## Data Models

### Tabla Expediciones (MySQL)

```sql
CREATE TABLE expediciones (
    id INT AUTO_INCREMENT PRIMARY KEY,
    codigo_expedicion VARCHAR(10) NOT NULL UNIQUE,
    nombre_expedicion VARCHAR(255) NOT NULL,
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE NOT NULL,
    sitio_arqueologico VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    CONSTRAINT chk_fechas CHECK (fecha_inicio <= fecha_fin),
    CONSTRAINT chk_codigo_length CHECK (LENGTH(codigo_expedicion) <= 10),
    CONSTRAINT chk_nombre_length CHECK (LENGTH(nombre_expedicion) <= 255),
    CONSTRAINT chk_sitio_length CHECK (LENGTH(sitio_arqueologico) <= 255),
    
    INDEX idx_codigo (codigo_expedicion),
    INDEX idx_nombre (nombre_expedicion)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

**Diagrama E-R Simplificado:**

```
┌─────────────────────────────────────┐
│         EXPEDICIONES               │
├─────────────────────────────────────┤
│ PK  id (INT)                        │
├─────────────────────────────────────┤
│ UQ  codigo_expedicion (VARCHAR 10)  │
│     nombre_expedicion (VARCHAR 255) │
│     fecha_inicio (DATE)             │
│     fecha_fin (DATE)                │
│     sitio_arqueologico (VARCHAR 255)│
│     created_at (TIMESTAMP)          │
│     updated_at (TIMESTAMP)          │
└─────────────────────────────────────┘

Restricciones:
- codigo_expedicion: Único, obligatorio, máx 10 caracteres
- nombre_expedicion: Obligatorio, máx 255 caracteres
- fecha_inicio: Obligatorio, anterior o igual a fecha_fin
- fecha_fin: Obligatorio, posterior o igual a fecha_inicio
- sitio_arqueologico: Obligatorio, máx 255 caracteres
```

---

## Correctness Properties

*Una propiedad es una característica o comportamiento que debe mantenerse verdadera en todas las ejecuciones válidas del sistema; esencialmente, una declaración formal de qué debe hacer el sistema. Las propiedades sirven como puente entre especificaciones legibles por humanos y garantías de corrección verificables por máquinas.*

### Property 1: Round-Trip para Expediciones (Persistencia e Integridad)

*Para cualquier Expedición válida E creada y persistida en la Base de Datos, leerla desde la Base de Datos debe retornar datos idénticos a los que se ingresaron.*

**Valida: Requisitos 2.4 (redondez de lectura), 5.3 (sincronización inmediata)**

**Aplicación en Testing:**
- Crear una expedición con datos X
- Leer la expedición desde la BD
- Verificar que código, nombre, fechas y sitio coinciden exactamente

---

### Property 2: Invariante de Fechas (Consistencia Temporal)

*Para cualquier Expedición E almacenada en la Base de Datos, la Fecha_Inicio siempre debe ser menor o igual a la Fecha_Fin.*

**Valida: Requisito 7.3 (validación de fechas)**

**Aplicación en Testing:**
- Para toda expedición en la BD, verificar que fechaInicio <= fechaFin
- El sistema debe rechazar cualquier intento de violar este invariante

---

### Property 3: Idempotencia de Lectura (Consultas Consistentes)

*Leer la lista de Expediciones múltiples veces sin realizar modificaciones debe retornar el mismo conjunto de datos.*

**Valida: Requisito 2.2 (actualización automática sin recargar aplicación)**

**Aplicación en Testing:**
- Leer lista de expediciones N veces consecutivas
- Verificar que todas las lecturas retornan el mismo resultado

---

### Property 4: Unicidad de Código (Invariante de Identificador)

*No pueden existir dos Expediciones con el mismo Código_Expedición en la Base de Datos.*

**Valida: Requisito 7.2 (validación de duplicado)**

**Aplicación en Testing:**
- Intentar crear una expedición con un código que ya existe
- El sistema debe rechazar la operación y mostrar mensaje de error
- El código único debe ser verificado tanto en operación CREATE como UPDATE

---

### Property 5: Cobertura Completa de CRUD (Metamorfismo de Operaciones)

*El número de Expediciones en la Base de Datos aumenta exactamente en 1 tras CREATE, no cambia tras READ, disminuye exactamente en 1 tras DELETE, y solo cambia en campos específicos tras UPDATE.*

**Valida: Requisitos 1.3, 2.2, 3.5, 4.2**

**Aplicación en Testing:**
- Contar expediciones antes de CREATE: N
- Contar después de CREATE: N+1
- Contar después de READ: N+1 (sin cambio)
- Contar después de DELETE: N (vuelve a estado anterior)

---

### Property 6: Integridad de Actualización (Invariante de Modificación)

*Cuando se actualiza una Expedición, solo los campos modificados cambian; los campos no tocados permanecen idénticos a su valor anterior.*

**Valida: Requisito 8.3 (independencia de capas)**

**Aplicación en Testing:**
- Crear una expedición con datos completos
- Modificar solo el Nombre_Expedición
- Verificar que código, fechas y sitio arqueológico permanecen iguales

---

## Error Handling

### 1. Capas de Manejo de Errores

```
┌─────────────────────────────────────────────────────────────┐
│ NIVEL 1: VISTA (Presentación al usuario)                    │
│ - JOptionPane dialogs (ERROR, WARNING, INFO)                 │
│ - Mensajes legibles para usuario final                       │
│ - Sin detalles técnicos de SQL                               │
└─────────────────────────────────────────────────────────────┘
                            ▲
                            │
┌─────────────────────────────────────────────────────────────┐
│ NIVEL 2: CONTROLADOR (Orquestación de errores)              │
│ - Captura excepciones de DAO y validación                    │
│ - Traduce a mensajes de usuario                              │
│ - Decide cómo proceder (reintentar, abortar, etc.)          │
└─────────────────────────────────────────────────────────────┘
                            ▲
                            │
┌─────────────────────────────────────────────────────────────┐
│ NIVEL 3: MODELO (Detección de errores técnicos)            │
│ - DatabaseException para errores BD                          │
│ - ValidacionException para validaciones fallidas             │
│ - SQLException capturada y envuelta                          │
└─────────────────────────────────────────────────────────────┘
```

### 2. Matriz de Errores y Respuestas

| Situación | Excepción | Controlador | Vista |
|-----------|-----------|-------------|-------|
| Conexión BD perdida | DatabaseException(SIN_CONEXION) | Capturada, operaciones locales permitidas | "Error de conexión. Datos cargados aún disponibles" |
| Código duplicado | ValidacionException | Capturada antes de persistir | "El código de expedición ya existe" |
| Fecha fin anterior a inicio | ValidacionException | Capturada, DAO no ejecuta | "Fecha de fin debe ser posterior a inicio" |
| Campo excede límite caracteres | ValidacionException | Capturada antes de BD | "El campo [X] excede límite de [N] caracteres" |
| Formato fecha inválido | ValidacionException | Capturada, parseDate falla | "Ingrese fechas en formato DD/MM/YYYY" |
| Error SQL genérico | SQLException → DatabaseException | Capturada, error SQL envuelto | "Error en operación de base de datos" |
| No hay expedición seleccionada | Lógica en Controlador | Validación en controlador | "Seleccione una expedición para editar" |

### 3. Flujo de Manejo en Operación CRUD

```
Usuario → Controlador → Validación → DAO → BD
  │           │              │         │      │
  │           ├─ try block   │         │      │
  │           │              ├─ Falsa │      │
  │           │              └─→ ValidacionException
  │           │                       │
  │           ├─ catch(ValidacionException)
  │           │  └─ mostrarError("El código ya existe")
  │           │      ↓
  │           │  Vista: JOptionPane.ERROR_MESSAGE
  │           │      ↓
  │           │  Usuario: Ve mensaje
  │           │
  │           ├─ try/catch(DatabaseException)
  │           │  └─ SI SIN_CONEXION: mostrar aviso pero permitir operaciones
  │           │  └─ SI ERROR_SQL: mostrar error genérico sin detalles técnicos
```

---

## Testing Strategy

### 1. Estrategia de Testing Dual (Unit + Property-Based)

Para este sistema, se implementará un enfoque de testing que combine:

#### a) Unit Tests (Ejemplo-basado)
- Casos específicos de operaciones CRUD
- Validaciones de entrada con ejemplos concretos
- Integración con BD mockada
- Errores de conexión simulados
- Interacciones usuario-botón

**Ejemplos de Unit Tests:**
- `testCrearExpedicionValida()` - Crear con datos válidos
- `testCrearExpedicionCodigoDuplicado()` - Rechazar código existente
- `testFechaFinAnteriorAInicio()` - Rechazar fechas inválidas
- `testBuscarPorNombreInsensibleAMayusculas()` - Búsqueda case-insensitive
- `testMensajeConfirmacionEliminacion()` - Dialog de confirmación

#### b) Property-Based Tests (Cuando aplica)
Las siguientes propiedades de corrección se prueban con PBT:

**Property 1: Round-Trip**
```java
@Property
public void testRoundTripExpedicion(@ForAll Expedicion exp) {
    // Crear expedición
    dao.create(exp);
    
    // Leer desde BD
    Expedicion leida = dao.readByCodigo(exp.getCodigo());
    
    // Verificar que es idéntica
    assertEquals(exp.getCodigo(), leida.getCodigo());
    assertEquals(exp.getNombre(), leida.getNombre());
    assertEquals(exp.getFechaInicio(), leida.getFechaInicio());
    assertEquals(exp.getFechaFin(), leida.getFechaFin());
    assertEquals(exp.getSitioArqueologico(), leida.getSitioArqueologico());
}
// Tag: Feature: expediciones-arqueologicas, Property 1: Round-Trip
```

**Property 2: Invariante de Fechas**
```java
@Property
public void testInvarianteFechas(@ForAll Expedicion exp) {
    assertTrue(exp.getFechaInicio().isEqual(exp.getFechaFin()) || 
              exp.getFechaInicio().isBefore(exp.getFechaFin()),
              "Fecha inicio debe ser <= fecha fin");
}
// Tag: Feature: expediciones-arqueologicas, Property 2: Invariante Fechas
```

**Property 3: Idempotencia de Lectura**
```java
@Property
public void testIdempotenciaLectura() {
    List<Expedicion> lectura1 = dao.readAll();
    List<Expedicion> lectura2 = dao.readAll();
    List<Expedicion> lectura3 = dao.readAll();
    
    assertEquals(lectura1, lectura2);
    assertEquals(lectura2, lectura3);
}
// Tag: Feature: expediciones-arqueologicas, Property 3: Idempotencia Lectura
```

**Property 4: Unicidad de Código**
```java
@Property
public void testUnicidadCodigo(@ForAll String codigo) {
    Expedicion exp1 = new Expedicion(codigo, "Exp1", date1, date2, "Sitio1");
    Expedicion exp2 = new Expedicion(codigo, "Exp2", date3, date4, "Sitio2");
    
    dao.create(exp1);
    
    // Intentar crear con código duplicado debe fallar
    assertThrows(DatabaseException.class, () -> dao.create(exp2));
}
// Tag: Feature: expediciones-arqueologicas, Property 4: Unicidad Código
```

**Property 5: Cobertura CRUD**
```java
@Property
public void testCoberturaCRUD(@ForAll Expedicion exp) {
    int countAntes = dao.readAll().size();
    
    // CREATE
    dao.create(exp);
    assertEquals(countAntes + 1, dao.readAll().size());
    
    // READ
    assertEquals(countAntes + 1, dao.readAll().size()); // No cambia
    
    // UPDATE
    exp.setNombre("Nuevo nombre");
    dao.update(exp);
    assertEquals(countAntes + 1, dao.readAll().size()); // No cambia
    
    // DELETE
    dao.delete(exp.getCodigo());
    assertEquals(countAntes, dao.readAll().size()); // Vuelve a estado anterior
}
// Tag: Feature: expediciones-arqueologicas, Property 5: Cobertura CRUD
```

**Property 6: Integridad de Actualización**
```java
@Property
public void testIntegridadActualizacion(@ForAll Expedicion expOriginal) {
    dao.create(expOriginal);
    
    // Leer original
    Expedicion original = dao.readByCodigo(expOriginal.getCodigo());
    
    // Modificar solo el nombre
    original.setNombre("Nombre modificado");
    dao.update(original);
    
    // Leer actualizada
    Expedicion actualizada = dao.readByCodigo(original.getCodigo());
    
    // Verificar que solo cambió el nombre
    assertEquals("Nombre modificado", actualizada.getNombre());
    assertEquals(original.getFechaInicio(), actualizada.getFechaInicio());
    assertEquals(original.getFechaFin(), actualizada.getFechaFin());
    assertEquals(original.getSitioArqueologico(), actualizada.getSitioArqueologico());
}
// Tag: Feature: expediciones-arqueologicas, Property 6: Integridad Actualización
```

### 2. Generadores para Property-Based Testing

```java
@Provide
public Arbitrary<Expedicion> expediciones() {
    return Combinators.combine(
        Arbitraries.strings()
            .withChars('A', 'Z', '0', '9', '-', '_')
            .ofMinLength(1).ofMaxLength(10)
            .unique(),
        Arbitraries.strings()
            .ofMinLength(1).ofMaxLength(255),
        Arbitraries.dates().between(
            LocalDate.of(1800, 1, 1),
            LocalDate.now()
        ),
        Arbitraries.dates().between(
            LocalDate.of(1800, 1, 1),
            LocalDate.now()
        ),
        Arbitraries.strings()
            .ofMinLength(1).ofMaxLength(255)
    ).filter(tuple -> !tuple.get3().isAfter(tuple.get4()))
     .map(tuple -> new Expedicion(
         tuple.get0(),
         tuple.get1(),
         tuple.get3(),
         tuple.get4(),
         tuple.get5()
     ));
}
```

### 3. Configuración de Ejecución

**Parámetros de Ejecución para PBT:**
- Mínimo 100 iteraciones por propiedad
- Seed fijo para reproducibilidad en CI/CD
- Timeout: 5 segundos por iteración
- Shrinking habilitado para reportar contraejemplos simples

**Herramienta recomendada:** jUnit 5 + jqwik (Java property-based testing)

---

## Interaction Flows

### Flujo 1: Crear Nueva Expedición

```
┌─────────────────────────────────────────────────────────────┐
│ USUARIO INICIA OPERACIÓN "CREAR"                           │
└──────────────────────┬──────────────────────────────────────┘
                       │ Click "Botón Nueva"
                       ▼
┌─────────────────────────────────────────────────────────────┐
│ VISTA: MainFrame.btnNueva.actionPerformed()                │
│ - Habilita campo Código (normal)                            │
│ - Limpia todos los campos del formulario                    │
│ - Establece modo = CREAR                                    │
│ - Habilita botones Guardar y Cancelar                       │
└──────────────────────┬──────────────────────────────────────┘
                       │ User llena formulario
                       ▼
┌─────────────────────────────────────────────────────────────┐
│ USUARIO INGRESA DATOS EN FORMULARIO                        │
│ - Código: "ARCH2024"                                        │
│ - Nombre: "Expedición Machu Picchu 2024"                   │
│ - Fecha Inicio: "15/03/2024"                                │
│ - Fecha Fin: "20/03/2024"                                   │
│ - Sitio Arqueológico: "Machu Picchu, Perú"                 │
│ - Click "Guardar"                                           │
└──────────────────────┬──────────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────────────┐
│ CONTROLADOR: crearExpedicion(...)                           │
│ - Recibe datos del formulario                               │
│ - Inicia try/catch block                                    │
└──────────────────────┬──────────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────────────┐
│ CONTROLADOR: validarExpedicion(...)                         │
│ - Verifica código no vacío y ≤10 caracteres ✓              │
│ - Verifica nombre no vacío y ≤255 caracteres ✓             │
│ - Parsea "15/03/2024" → LocalDate (dd/MM/yyyy) ✓           │
│ - Parsea "20/03/2024" → LocalDate ✓                        │
│ - Verifica fechaInicio ≤ fechaFin ✓                        │
│ - Verifica sitio no vacío y ≤255 caracteres ✓              │
│ - Validación exitosa → continúa                             │
└──────────────────────┬──────────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────────────┐
│ CONTROLADOR: dao.existeCodigo("ARCH2024")                   │
│ - Consulta BD: SELECT COUNT(*) WHERE código = ?             │
│ - Retorna 0 (no existe) ✓                                   │
└──────────────────────┬──────────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────────────┐
│ CONTROLADOR: construirExpedicion(...)                       │
│ - Crea objeto Expedicion con datos validados                │
│ - expedicion.codigo = "ARCH2024"                            │
│ - expedicion.nombre = "Expedición Machu Picchu 2024"        │
│ - expedicion.fechaInicio = LocalDate.of(2024, 3, 15)       │
│ - expedicion.fechaFin = LocalDate.of(2024, 3, 20)          │
│ - expedicion.sitioArqueologico = "Machu Picchu, Perú"       │
└──────────────────────┬──────────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────────────┐
│ MODELO: dao.create(expedicion)                              │
│ - Obtiene conexión desde pool AWS RDS                       │
│ - Prepara sentencia SQL:                                    │
│   INSERT INTO expediciones (...) VALUES (?, ?, ?, ?, ?)     │
│ - Bind parameters (previene inyección SQL)                  │
│ - Ejecuta: executeUpdate() → 1 fila insertada ✓             │
│ - Cierra PreparedStatement                                  │
│ - Cierra Connection                                         │
│ - Retorna exitoso                                           │
└──────────────────────┬──────────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────────────┐
│ CONTROLADOR: catch block                                    │
│ - Sin excepciones, no entra en catch                        │
│ - Continúa con post-operación                               │
└──────────────────────┬──────────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────────────┐
│ CONTROLADOR: Post-operación                                 │
│ - expedicionesActuales.add(expedicion)                      │
│ - vista.actualizarTablaExpediciones(expedicionesActuales)    │
│ - vista.limpiarFormulario()                                 │
│ - expedicionEnEdicion = null                                │
│ - vista.mostrarMensaje("Expedición creada exitosamente",    │
│                        JOptionPane.INFORMATION_MESSAGE)    │
└──────────────────────┬──────────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────────────┐
│ VISTA: actualizarTablaExpediciones(...)                     │
│ - SwingUtilities.invokeLater() → EDT                        │
│ - tablaExpediciones.setModel(nuevoModeloTabla)              │
│ - Tabla se actualiza: nueva fila con ARCH2024 visible       │
│ - modeloTabla.fireTableDataChanged()                        │
└──────────────────────┬──────────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────────────┐
│ VISTA: mostrarMensaje(...)                                  │
│ - JOptionPane.showMessageDialog()                           │
│ - Título: "Éxito"                                           │
│ - Mensaje: "Expedición creada exitosamente"                │
│ - Icono: INFORMATION_MESSAGE (ícono verde ✓)                │
└──────────────────────┬──────────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────────────┐
│ USUARIO VE RESULTADO                                        │
│ - Dialog de éxito                                           │
│ - Tabla actualizada con nueva expedición                    │
│ - Formulario vacío y listo para nueva entrada               │
│ - Puede crear otra o seleccionar de la tabla                │
└─────────────────────────────────────────────────────────────┘
```

---

### Flujo 2: Editar Expedición Existente

```
┌─────────────────────────────────────────────────────────────┐
│ USUARIO SELECCIONA FILA EN TABLA Y CLICK "EDITAR"          │
└──────────────────────┬──────────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────────────┐
│ VISTA: btnEditar.actionPerformed()                          │
│ - Obtiene filaSeleccionada = tabla.getSelectedRow()        │
│ - IF filaSeleccionada == -1:                                │
│     mostrarMensaje("Seleccione una expedición", WARNING)    │
│     RETURN                                                   │
│ - ELSE: continúa                                            │
└──────────────────────┬──────────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────────────┐
│ CONTROLADOR: cargarExpedicionEnFormulario()                 │
│ - expedicionEnEdicion = tabla.getExpedicionSeleccionada()  │
│ - panelFormulario.setCampos(expedicionEnEdicion)            │
│ - PanelFormulario carga campos:                             │
│   txtCodigo.setText("ARCH2024")                             │
│   txtNombre.setText("Expedición original")                  │
│   txtFechaInicio.setText("15/03/2024")                      │
│   txtFechaFin.setText("20/03/2024")                         │
│   txtSitioArqueologico.setText("Machu Picchu")              │
│ - deshabilitarCampoCodigoEnEdicion() - el código NO cambia  │
│ - Habilita botones Guardar y Cancelar                       │
└──────────────────────┬──────────────────────────────────────┘
                       │ User modifica nombre
                       ▼
┌─────────────────────────────────────────────────────────────┐
│ USUARIO MODIFICA DATOS                                      │
│ - Nombre: "Expedición Machu Picchu 2024 - Actualizada"    │
│ - Click "Guardar"                                           │
└──────────────────────┬──────────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────────────┐
│ CONTROLADOR: modificarExpedicion(...)                       │
│ - IF expedicionEnEdicion == null:                           │
│     mostrarMensaje("Seleccione expedición", WARNING)        │
│     RETURN                                                   │
│ - ELSE: continúa                                            │
└──────────────────────┬──────────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────────────┐
│ CONTROLADOR: validarExpedicion(...) [idem a crear]          │
│ - Todas las validaciones pasan ✓                            │
└──────────────────────┬──────────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────────────┐
│ CONTROLADOR: Verificación de código duplicado               │
│ - IF código cambió AND dao.existeCodigo(nuevoCodigo):       │
│     mostrarMensaje("Código ya existe", WARNING)             │
│     RETURN                                                   │
│ - ELSE (código no cambió o es único): continúa             │
└──────────────────────┬──────────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────────────┐
│ CONTROLADOR: Actualización de objeto                        │
│ - expedicion = construirExpedicion(...)                     │
│ - expedicion.setId(expedicionEnEdicion.getId())             │
│   [Importante: preserva ID de BD]                           │
└──────────────────────┬──────────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────────────┐
│ MODELO: dao.update(expedicion)                              │
│ - Obtiene conexión                                          │
│ - Prepara SQL:                                              │
│   UPDATE expediciones SET nombre = ?, fecha_inicio = ?,    │
│   fecha_fin = ?, sitio = ? WHERE codigo = ?                │
│ - Bind parameters con nuevos valores                        │
│ - Ejecuta: executeUpdate() → 1 fila actualizada ✓           │
│ - Cierra recursos                                           │
│ - Retorna exitoso                                           │
└──────────────────────┬──────────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────────────┐
│ CONTROLADOR: Post-operación                                 │
│ - int indice = expedicionesActuales.indexOf(expedicionEnEdicion)
│ - expedicionesActuales.set(indice, expedicion)              │
│ - vista.actualizarTablaExpediciones(expedicionesActuales)   │
│ - vista.limpiarFormulario()                                 │
│ - expedicionEnEdicion = null                                │
│ - vista.mostrarMensaje("Expedición actualizada exitosamente"│
└──────────────────────┬──────────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────────────┐
│ USUARIO VE RESULTADO                                        │
│ - Dialog de éxito                                           │
│ - Tabla actualizada: fila con nombre modificado             │
│ - Formulario vacío                                          │
└─────────────────────────────────────────────────────────────┘
```

---

### Flujo 3: Eliminar Expedición

```
┌─────────────────────────────────────────────────────────────┐
│ USUARIO SELECCIONA FILA Y CLICK "ELIMINAR"                 │
└──────────────────────┬──────────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────────────┐
│ CONTROLADOR: eliminarExpedicion(codigo)                     │
│ - IF expedicionSeleccionada == null:                        │
│     mostrarMensaje("Seleccione una expedición", WARNING)    │
│     RETURN                                                   │
└──────────────────────┬──────────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────────────┐
│ VISTA: Mostrar confirmación                                 │
│ - JOptionPane.showConfirmDialog():                          │
│   ¿Está seguro de que desea eliminar esta expedición?       │
│   [Sí] [No]                                                 │
└──────────────────────┬──────────────────────────────────────┘
                       │
                       ▼
            ┌──────────┴──────────┐
            │                     │
       Usuario       Usuario No
       Confirma      Confirma
            │                │
            ▼                ▼
         [Sí]          Retorna sin hacer nada
            │
            ▼
┌─────────────────────────────────────────────────────────────┐
│ MODELO: dao.delete(codigo)                                  │
│ - Obtiene conexión                                          │
│ - Prepara SQL: DELETE FROM expediciones WHERE código = ?    │
│ - Bind parameter                                            │
│ - Ejecuta: executeUpdate() → 1 fila eliminada ✓             │
│ - Cierra recursos                                           │
└──────────────────────┬──────────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────────────┐
│ CONTROLADOR: Post-operación                                 │
│ - expedicionesActuales.removeIf(e → e.getCodigo() == código)
│ - vista.actualizarTablaExpediciones(expedicionesActuales)   │
│ - vista.mostrarMensaje("Expedición eliminada exitosamente"  │
└──────────────────────┬──────────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────────────┐
│ USUARIO VE RESULTADO                                        │
│ - Dialog de éxito                                           │
│ - Tabla actualizada sin la fila eliminada                   │
│ - Formulario vacío                                          │
└─────────────────────────────────────────────────────────────┘
```

---

### Flujo 4: Manejo de Error - Código Duplicado

```
┌─────────────────────────────────────────────────────────────┐
│ USUARIO INTENTA CREAR EXPEDICIÓN CON CÓDIGO "ARCH2024"     │
│ (que ya existe en la BD)                                    │
└──────────────────────┬──────────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────────────┐
│ CONTROLADOR: crearExpedicion(...)                           │
│ - Validación exitosa ✓                                      │
│ - dao.existeCodigo("ARCH2024")                              │
│   → Consulta BD, retorna true (existe) ✗                    │
└──────────────────────┬──────────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────────────┐
│ CONTROLADOR: Manejo de error                                │
│ - IF dao.existeCodigo(...):                                 │
│     vista.mostrarMensaje(                                   │
│       "El código de expedición ya existe",                  │
│       JOptionPane.WARNING_MESSAGE                           │
│     )                                                       │
│     RETURN (sin ejecutar dao.create())                      │
└──────────────────────┬──────────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────────────┐
│ VISTA: mostrar dialog de advertencia                        │
│ - Icono: ⚠️ WARNING                                         │
│ - Mensaje: "El código de expedición ya existe"              │
│ - Botón: [OK]                                               │
└──────────────────────┬──────────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────────────┐
│ USUARIO VE ADVERTENCIA Y PUEDE REINTENTAR                   │
│ - El formulario permanece lleno con los datos               │
│ - Puede cambiar el código e intentar de nuevo               │
│ - O cancelar la operación                                   │
└─────────────────────────────────────────────────────────────┘
```

---

## CRUD Sequence Diagrams

### Diagrama de Secuencia: CREATE

```
Usuario        Vista        Controlador      Modelo(DAO)      BD(MySQL)
  │              │                │              │               │
  ├─ Click Nueva─>│                │              │               │
  │              ├─ Limpia Form─> │              │               │
  │              │<── Modo CREAR ──┤              │               │
  │              │                │              │               │
  │ [Ingresa datos]              │              │               │
  ├─ Click Guardar->│              │              │               │
  │              ├─ Obtiene datos->│              │               │
  │              │                ├─ Valida─────>│ (local)        │
  │              │<──OK Validación─┤              │               │
  │              │                ├─ ExisteCod?─>│               │
  │              │<──No existe────┤              │               │
  │              │                ├─ Create────>│<─ INSERT ─────│
  │              │                │              │<─ OK ─────────│
  │              │<── Creada OK ──┤              │               │
  │              ├─ Actualiza Tabla             │               │
  │              ├─ Limpia Formulario           │               │
  │<── Muestra Éxito ─────────────┤              │               │
  │              │                │              │               │
```

### Diagrama de Secuencia: UPDATE

```
Usuario        Vista        Controlador      Modelo(DAO)      BD(MySQL)
  │              │                │              │               │
  ├─ Select+Editar->│              │              │               │
  │              ├─ Carga Datos──>│              │               │
  │              │<─ Campos llenos┤              │               │
  │              │                │              │               │
  │ [Modifica datos]             │              │               │
  ├─ Click Guardar->│              │              │               │
  │              ├─ Obtiene datos->│              │               │
  │              │                ├─ Valida─────>│ (local)        │
  │              │<──OK Validación─┤              │               │
  │              │                ├─ Existe otro?>│              │
  │              │<──No duplicado──┤              │               │
  │              │                ├─ Update───-->│<─ UPDATE ────│
  │              │                │              │<─ OK ────────│
  │              │<── Actualizada ┤              │               │
  │              ├─ Actualiza Tabla             │               │
  │              ├─ Limpia Formulario           │               │
  │<── Muestra Éxito ─────────────┤              │               │
  │              │                │              │               │
```

### Diagrama de Secuencia: DELETE

```
Usuario        Vista        Controlador      Modelo(DAO)      BD(MySQL)
  │              │                │              │               │
  ├─ Select+Eliminar->│              │              │               │
  │              ├─ Confirma────>│              │               │
  │              │   [¿Seguro?]   │              │               │
  │ Click Sí     │                │              │               │
  ├─ Confirma──->│                ├─ Delete───-->│<─ DELETE ─────│
  │              │                │              │<─ OK ─────────│
  │              │<── Eliminada ──┤              │               │
  │              ├─ Actualiza Tabla             │               │
  │              ├─ Limpia Formulario           │               │
  │<── Muestra Éxito ─────────────┤              │               │
  │              │                │              │               │
```

### Diagrama de Secuencia: READ (Listar)

```
Usuario        Vista        Controlador      Modelo(DAO)      BD(MySQL)
  │              │                │              │               │
  │ [App inicia o CLIC Mostrar Todos]           │               │
  │              ├─ ListarExped──>│              │               │
  │              │                ├─ ReadAll────>│<─ SELECT * ───│
  │              │                │              │<─ ResultSet ──│
  │              │                ├─ Mapea──────>│               │
  │              │<── Lista OK ───┤              │               │
  │              ├─ Actualiza Tabla             │               │
  │              ├─ Columnas: Código, Nombre, Inicio, Fin, Sitio
  │<── Tabla actualizada ─────────┤              │               │
  │              │                │              │               │
```

---

## Summary

El diseño técnico del Sistema de Gestión de Expediciones Arqueológicas implementa una arquitectura MVC completa con:

- **Separación clara de responsabilidades** entre presentación, lógica de negocio y acceso a datos
- **Persistencia robusta** mediante JDBC contra MySQL 8.x en AWS RDS
- **Interfaz gráfica responsiva** construida con componentes Java Swing
- **Validación multicapa** que previene datos inválidos
- **Manejo comprehensivo de errores** con mensajes amigables al usuario
- **Propiedades de corrección** formalmente especificadas y verificables mediante property-based testing
- **Operaciones CRUD completas** con confirmaciones y transaccionalidad
- **Búsqueda y filtrado** de expediciones con soporte case-insensitive

Todos los componentes están diseñados para maximizar la testabilidad, mantenibilidad y escalabilidad del sistema.
