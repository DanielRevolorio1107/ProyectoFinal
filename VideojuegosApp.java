package TiendaVideojuegos;

import java.awt.Component;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.FocusManager;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane; 
import javax.swing.SwingUtilities;

public class VideojuegosApp extends JFrame {
    private Conexion conexion;
    public VideojuegosApp() {
        
        
       addWindowListener(new WindowAdapter() {
    @Override
    public void windowClosing(WindowEvent e) {
        if (conexion != null) {
            conexion.cerrarConexion();
        }
        super.windowClosing(e);
       }
        });
    }
    
       public static void main(String[] args) {
           SwingUtilities.invokeLater(() -> {
             VideojuegosApp app = new VideojuegosApp();
           app.conexion = new Conexion();

            JOptionPane.showMessageDialog(null, "Conexión Exitosa ", "Conexión Exitosa", JOptionPane.INFORMATION_MESSAGE);
            
            VideojuegosGUI gui = new VideojuegosGUI(app.conexion);
            gui.setVisible(true);

            KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {
                @Override
                public boolean dispatchKeyEvent(KeyEvent e) {
                    if (e.getID() == KeyEvent.KEY_PRESSED && e.getKeyCode() == KeyEvent.VK_ENTER) {
                        Component focused = FocusManager.getCurrentManager().getFocusOwner();
                        if (focused instanceof JButton) {
                            ((JButton) focused).doClick();
                            return true;
                        }
                    }
                    return false;
                }
            });
        });
    }
}