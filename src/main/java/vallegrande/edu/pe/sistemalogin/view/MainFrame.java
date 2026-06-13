package vallegrande.edu.pe.sistemalogin.view;

import vallegrande.edu.pe.sistemalogin.model.Expedicion;
import vallegrande.edu.pe.sistemalogin.controller.ExpedicionController;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MainFrame extends JFrame {
    private JTextField txtCodigo, txtNombre, txtFechaInicio, txtFechaFin, txtSitio;
    private JTextField txtBusqueda;
    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private ExpedicionController controller;

    public MainFrame(ExpedicionController controller) {
        this.controller = controller;
        inicializar();
    }

    public void setController(ExpedicionController controller) {
        this.controller = controller;
        if (controller != null) {
            controller.cargarDatos();
        }
    }

    private void inicializar() {
        setTitle("Gestión de Expediciones Arqueológicas");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 1000);
        setLocationRelativeTo(null);
        setResizable(true);

        // Panel principal
        JPanel principal = new JPanel(new GridLayout(4, 1, 5, 5));
        principal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 1. TÍTULO
        JLabel titulo = new JLabel("Gestión de Expediciones Arqueológicas", JLabel.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        principal.add(titulo);

        // 2. ENTRADA
        JPanel panelEntrada = crearPanelEntrada();
        principal.add(panelEntrada);

        // 3. BÚSQUEDA + TABLA
        JPanel panelCentral = new JPanel(new BorderLayout());
        panelCentral.add(crearPanelBusqueda(), BorderLayout.NORTH);
        panelCentral.add(crearPanelTabla(), BorderLayout.CENTER);
        principal.add(panelCentral);

        // 4. BOTONES
        JPanel panelBotones = crearPanelBotones();
        principal.add(panelBotones);

        setContentPane(principal);
    }

    private JPanel crearPanelEntrada() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Datos de Expedición"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtCodigo = new JTextField(15);
        txtNombre = new JTextField(20);
        txtFechaInicio = new JTextField(15);
        txtFechaFin = new JTextField(15);
        txtSitio = new JTextField(30);

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.1;
        panel.add(new JLabel("Código:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.2;
        panel.add(txtCodigo, gbc);
        gbc.gridx = 2; gbc.weightx = 0.1;
        panel.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 3; gbc.weightx = 0.3;
        panel.add(txtNombre, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.1;
        panel.add(new JLabel("Fecha Inicio:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.2;
        panel.add(txtFechaInicio, gbc);
        gbc.gridx = 2; gbc.weightx = 0.1;
        panel.add(new JLabel("Fecha Fin:"), gbc);
        gbc.gridx = 3; gbc.weightx = 0.2;
        panel.add(txtFechaFin, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.1;
        panel.add(new JLabel("Sitio:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.weightx = 0.9;
        panel.add(txtSitio, gbc);

        return panel;
    }

    private JPanel crearPanelBusqueda() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Búsqueda"));
        
        txtBusqueda = new JTextField(25);
        JButton btnBuscar = new JButton("Buscar");
        JButton btnMostrarTodos = new JButton("Mostrar Todos");

        panel.add(new JLabel("Buscar:"));
        panel.add(txtBusqueda);
        panel.add(btnBuscar);
        panel.add(btnMostrarTodos);

        btnBuscar.addActionListener(e -> {
            if (controller != null) controller.buscar(txtBusqueda.getText());
        });
        btnMostrarTodos.addActionListener(e -> {
            if (controller != null) controller.mostrarTodos();
        });

        return panel;
    }

    private JPanel crearPanelTabla() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Expediciones"));

        modeloTabla = new DefaultTableModel(
            new String[]{"Código", "Nombre", "Fecha Inicio", "Fecha Fin", "Sitio"}, 
            0
        ) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        tabla = new JTable(modeloTabla);
        tabla.setRowHeight(25);
        tabla.setFont(new Font("Arial", Font.PLAIN, 12));
        
        tabla.getColumnModel().getColumn(0).setPreferredWidth(80);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(200);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(120);
        tabla.getColumnModel().getColumn(3).setPreferredWidth(120);
        tabla.getColumnModel().getColumn(4).setPreferredWidth(250);

        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Acciones"));

        JButton btnGuardar = new JButton("Guardar");
        JButton btnEditar = new JButton("Editar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnLimpiar = new JButton("Limpiar");

        btnGuardar.setPreferredSize(new Dimension(100, 30));
        btnEditar.setPreferredSize(new Dimension(100, 30));
        btnEliminar.setPreferredSize(new Dimension(100, 30));
        btnLimpiar.setPreferredSize(new Dimension(100, 30));

        btnGuardar.addActionListener(e -> {
            if (controller != null) controller.guardar(getFormData());
        });
        btnEditar.addActionListener(e -> {
            if (controller != null) controller.editar(tabla.getSelectedRow(), this);
        });
        btnEliminar.addActionListener(e -> {
            if (controller != null) controller.eliminar(tabla.getSelectedRow());
        });
        btnLimpiar.addActionListener(e -> limpiar());

        panel.add(btnGuardar);
        panel.add(btnEditar);
        panel.add(btnEliminar);
        panel.add(btnLimpiar);

        return panel;
    }

    public void actualizarTabla(List<Expedicion> expediciones) {
        modeloTabla.setRowCount(0);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        for (Expedicion exp : expediciones) {
            modeloTabla.addRow(new Object[]{
                    exp.getCodigo(),
                    exp.getNombre(),
                    exp.getFechaInicio().format(fmt),
                    exp.getFechaFin().format(fmt),
                    exp.getSitioArqueologico()
            });
        }
        System.out.println("✓ Tabla: " + expediciones.size() + " filas");
    }

    public String[] getFormData() {
        return new String[]{
                txtCodigo.getText().trim(),
                txtNombre.getText().trim(),
                txtFechaInicio.getText().trim(),
                txtFechaFin.getText().trim(),
                txtSitio.getText().trim()
        };
    }

    public void cargarEnFormulario(Expedicion exp) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        txtCodigo.setText(exp.getCodigo());
        txtCodigo.setEditable(false);
        txtNombre.setText(exp.getNombre());
        txtFechaInicio.setText(exp.getFechaInicio().format(fmt));
        txtFechaFin.setText(exp.getFechaFin().format(fmt));
        txtSitio.setText(exp.getSitioArqueologico());
    }

    public void limpiar() {
        txtCodigo.setText("");
        txtCodigo.setEditable(true);
        txtNombre.setText("");
        txtFechaInicio.setText("");
        txtFechaFin.setText("");
        txtSitio.setText("");
        tabla.clearSelection();
        txtBusqueda.setText("");
    }

    public void mostrarMensaje(String msg, int tipo) {
        JOptionPane.showMessageDialog(this, msg, "Información", tipo);
    }

    public int getFilaSeleccionada() {
        return tabla.getSelectedRow();
    }

    public String getCodigoSeleccionado() {
        int fila = tabla.getSelectedRow();
        if (fila >= 0) {
            return (String) modeloTabla.getValueAt(fila, 0);
        }
        return null;
    }
}
