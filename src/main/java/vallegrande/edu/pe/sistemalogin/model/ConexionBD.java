package vallegrande.edu.pe.sistemalogin.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {
    private static ConexionBD instance;
    private Connection conexion;

    private static final String URL = "jdbc:mysql://localhost:3307/expediciones_db";
    private static final String USUARIO = "usuario_app";
    private static final String PASSWORD = "pass123";
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";

    private ConexionBD() {
        try {
            Class.forName(DRIVER);
            this.conexion = DriverManager.getConnection(URL, USUARIO, PASSWORD);
            System.out.println("✓ Conexión a BD exitosa");
        } catch (ClassNotFoundException | SQLException ex) {
            System.err.println("ERROR: " + ex.getMessage());
        }
    }

    public static synchronized ConexionBD getInstance() {
        if (instance == null) {
            instance = new ConexionBD();
        }
        return instance;
    }

    public Connection obtenerConexion() throws SQLException {
        if (conexion == null || conexion.isClosed()) {
            this.conexion = DriverManager.getConnection(URL, USUARIO, PASSWORD);
        }
        return conexion;
    }

    public boolean verificarConexion() {
        try {
            if (conexion != null && !conexion.isClosed()) {
                conexion.createStatement().execute("SELECT 1");
                return true;
            }
        } catch (SQLException ex) {
            System.err.println("ERROR: Conexión no disponible");
        }
        return false;
    }

    public void cerrar() {
        try {
            if (conexion != null && !conexion.isClosed()) {
                conexion.close();
                System.out.println("✓ Conexión cerrada");
            }
        } catch (SQLException ex) {
            System.err.println("ERROR: " + ex.getMessage());
        }
    }
}
