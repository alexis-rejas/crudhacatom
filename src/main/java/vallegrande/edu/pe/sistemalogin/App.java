package vallegrande.edu.pe.sistemalogin;

import vallegrande.edu.pe.sistemalogin.model.ConexionBD;
import vallegrande.edu.pe.sistemalogin.model.ExpedicionDAO;
import vallegrande.edu.pe.sistemalogin.controller.ExpedicionController;
import vallegrande.edu.pe.sistemalogin.view.MainFrame;

import javax.swing.SwingUtilities;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                ConexionBD conexion = ConexionBD.getInstance();
                
                if (!conexion.verificarConexion()) {
                    System.err.println("ERROR: No se pudo conectar a la base de datos");
                    System.exit(1);
                }
                
                ExpedicionDAO dao = new ExpedicionDAO(conexion);
                MainFrame vista = new MainFrame(null);
                ExpedicionController controller = new ExpedicionController(dao, vista);
                vista.setController(controller);
                vista.setVisible(true);
                
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }
}
