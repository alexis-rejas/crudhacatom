package vallegrande.edu.pe.sistemalogin.model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExpedicionDAO {
    private ConexionBD conexion;
    
    public ExpedicionDAO(ConexionBD conexion) {
        this.conexion = conexion;
    }
    
    public void crear(Expedicion exp) throws SQLException {
        String sql = "INSERT INTO expediciones (codigo_expedicion, nombre_expedicion, fecha_inicio, fecha_fin, sitio_arqueologico) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = conexion.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, exp.getCodigo());
            pstmt.setString(2, exp.getNombre());
            pstmt.setDate(3, Date.valueOf(exp.getFechaInicio()));
            pstmt.setDate(4, Date.valueOf(exp.getFechaFin()));
            pstmt.setString(5, exp.getSitioArqueologico());
            pstmt.executeUpdate();
        }
    }
    
    public List<Expedicion> obtenerTodas() throws SQLException {
        List<Expedicion> expediciones = new ArrayList<>();
        String sql = "SELECT id, codigo_expedicion, nombre_expedicion, fecha_inicio, fecha_fin, sitio_arqueologico FROM expediciones ORDER BY codigo_expedicion";
        try (Connection conn = conexion.obtenerConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Expedicion exp = new Expedicion();
                exp.setId(rs.getInt("id"));
                exp.setCodigo(rs.getString("codigo_expedicion"));
                exp.setNombre(rs.getString("nombre_expedicion"));
                exp.setFechaInicio(rs.getDate("fecha_inicio").toLocalDate());
                exp.setFechaFin(rs.getDate("fecha_fin").toLocalDate());
                exp.setSitioArqueologico(rs.getString("sitio_arqueologico"));
                expediciones.add(exp);
            }
        }
        return expediciones;
    }
    
    public Expedicion obtenerPorCodigo(String codigo) throws SQLException {
        String sql = "SELECT id, codigo_expedicion, nombre_expedicion, fecha_inicio, fecha_fin, sitio_arqueologico FROM expediciones WHERE codigo_expedicion = ?";
        try (Connection conn = conexion.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, codigo);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
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
        }
        return null;
    }
    
    public void actualizar(Expedicion exp) throws SQLException {
        String sql = "UPDATE expediciones SET nombre_expedicion = ?, fecha_inicio = ?, fecha_fin = ?, sitio_arqueologico = ? WHERE codigo_expedicion = ?";
        try (Connection conn = conexion.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, exp.getNombre());
            pstmt.setDate(2, Date.valueOf(exp.getFechaInicio()));
            pstmt.setDate(3, Date.valueOf(exp.getFechaFin()));
            pstmt.setString(4, exp.getSitioArqueologico());
            pstmt.setString(5, exp.getCodigo());
            pstmt.executeUpdate();
        }
    }
    
    public void eliminar(String codigo) throws SQLException {
        String sql = "DELETE FROM expediciones WHERE codigo_expedicion = ?";
        try (Connection conn = conexion.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, codigo);
            pstmt.executeUpdate();
        }
    }
    
    public boolean existeCodigo(String codigo) throws SQLException {
        String sql = "SELECT COUNT(*) FROM expediciones WHERE codigo_expedicion = ?";
        try (Connection conn = conexion.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, codigo);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
}
