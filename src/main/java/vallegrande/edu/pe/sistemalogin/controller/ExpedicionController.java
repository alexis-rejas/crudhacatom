package vallegrande.edu.pe.sistemalogin.controller;

import vallegrande.edu.pe.sistemalogin.model.Expedicion;
import vallegrande.edu.pe.sistemalogin.model.ExpedicionDAO;
import vallegrande.edu.pe.sistemalogin.view.MainFrame;

import javax.swing.JOptionPane;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class ExpedicionController {
    private ExpedicionDAO dao;
    private MainFrame vista;
    private List<Expedicion> expedicionesActuales;
    private Expedicion enEdicion = null;

    public ExpedicionController(ExpedicionDAO dao, MainFrame vista) {
        this.dao = dao;
        this.vista = vista;
        cargarDatos();
    }

    public void cargarDatos() {
        try {
            expedicionesActuales = dao.obtenerTodas();
            System.out.println("✓ Datos cargados: " + expedicionesActuales.size() + " registros");
            vista.actualizarTabla(expedicionesActuales);
        } catch (Exception ex) {
            System.err.println("ERROR: " + ex.getMessage());
            ex.printStackTrace();
            vista.mostrarMensaje("Error al cargar: " + ex.getMessage(), JOptionPane.ERROR_MESSAGE);
        }
    }

    public void guardar(String[] datos) {
        try {
            String codigo = datos[0];
            String nombre = datos[1];
            String fechaIn = datos[2];
            String fechaFin = datos[3];
            String sitio = datos[4];

            if (codigo.isEmpty() || nombre.isEmpty() || fechaIn.isEmpty() || fechaFin.isEmpty() || sitio.isEmpty()) {
                vista.mostrarMensaje("Todos los campos son requeridos", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (enEdicion == null && dao.existeCodigo(codigo)) {
                vista.mostrarMensaje("El código ya existe", JOptionPane.WARNING_MESSAGE);
                return;
            }

            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            Expedicion exp = new Expedicion(codigo, nombre,
                    LocalDate.parse(fechaIn, fmt),
                    LocalDate.parse(fechaFin, fmt),
                    sitio);

            if (enEdicion == null) {
                dao.crear(exp);
                vista.mostrarMensaje("Expedición creada", JOptionPane.INFORMATION_MESSAGE);
            } else {
                exp.setId(enEdicion.getId());
                dao.actualizar(exp);
                vista.mostrarMensaje("Expedición actualizada", JOptionPane.INFORMATION_MESSAGE);
                enEdicion = null;
            }

            vista.limpiar();
            cargarDatos();
        } catch (Exception ex) {
            vista.mostrarMensaje("Error: " + ex.getMessage(), JOptionPane.ERROR_MESSAGE);
        }
    }

    public void editar(int fila, MainFrame vista) {
        if (fila < 0) {
            vista.mostrarMensaje("Selecciona una expedición", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            String codigo = vista.getCodigoSeleccionado();
            enEdicion = dao.obtenerPorCodigo(codigo);
            if (enEdicion != null) {
                vista.cargarEnFormulario(enEdicion);
            }
        } catch (Exception ex) {
            vista.mostrarMensaje("Error: " + ex.getMessage(), JOptionPane.ERROR_MESSAGE);
        }
    }

    public void eliminar(int fila) {
        if (fila < 0) {
            vista.mostrarMensaje("Selecciona una expedición", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            String codigo = vista.getCodigoSeleccionado();
            int confirm = JOptionPane.showConfirmDialog(vista, "¿Confirmar eliminación?", "Confirmación", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                dao.eliminar(codigo);
                vista.mostrarMensaje("Expedición eliminada", JOptionPane.INFORMATION_MESSAGE);
                cargarDatos();
            }
        } catch (Exception ex) {
            vista.mostrarMensaje("Error: " + ex.getMessage(), JOptionPane.ERROR_MESSAGE);
        }
    }

    public void buscar(String termino) {
        try {
            if (termino.isEmpty()) {
                vista.actualizarTabla(expedicionesActuales);
                return;
            }

            List<Expedicion> filtradas = expedicionesActuales.stream()
                    .filter(e -> e.getNombre().toLowerCase().contains(termino.toLowerCase()))
                    .collect(Collectors.toList());

            if (filtradas.isEmpty()) {
                vista.mostrarMensaje("No hay resultados", JOptionPane.INFORMATION_MESSAGE);
            }
            vista.actualizarTabla(filtradas);
        } catch (Exception ex) {
            vista.mostrarMensaje("Error: " + ex.getMessage(), JOptionPane.ERROR_MESSAGE);
        }
    }

    public void mostrarTodos() {
        vista.actualizarTabla(expedicionesActuales);
    }
}
