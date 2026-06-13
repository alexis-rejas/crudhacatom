package vallegrande.edu.pe.sistemalogin.model;

import java.time.LocalDate;

public class Expedicion {
    private int id;
    private String codigo;
    private String nombre;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String sitioArqueologico;

    public Expedicion() {}

    public Expedicion(String codigo, String nombre, LocalDate fechaInicio, 
                     LocalDate fechaFin, String sitioArqueologico) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.sitioArqueologico = sitioArqueologico;
    }

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
    public void setSitioArqueologico(String sitioArqueologico) { this.sitioArqueologico = sitioArqueologico; }
}
