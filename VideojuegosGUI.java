
package TiendaVideojuegos;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import java.sql.Connection;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.table.DefaultTableModel;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map.Entry;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


public class VideojuegosGUI extends JFrame {
    private Map<String, JFrame> windows = new HashMap<>();
    private Conexion conexion;
    private DefaultTableModel tableModel;
    private JTable table;
    private Cliente cliente;
    private List<String> productosAgregados = new ArrayList<>(); 
    
     public VideojuegosGUI(Conexion conexion) {
             this.conexion = conexion;
     
        setTitle("VENTA DE VIDEOJUEGOS");
         setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 400);
       setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        
    JTabbedPane tabbedPane = new JTabbedPane();

        // Panel "Ventas"
         JPanel ventasPanel = new JPanel(new BorderLayout());
        tabbedPane.addTab("Ventas", ventasPanel);

        DefaultListModel<String> ventasListModel = new DefaultListModel<>();
        ventasListModel.addElement("Interacción con el Cliente");

        JList<String> ventasList = new JList<>(ventasListModel);
        ventasList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        ventasList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selectedItem = ventasList.getSelectedValue();
                if (selectedItem != null) {
                    abrirVentana(selectedItem);
                }
                
            }
            
        });

        JScrollPane ventasScrollPane = new JScrollPane(ventasList);
        ventasScrollPane.setBorder(BorderFactory.createEmptyBorder());
        ventasPanel.add(ventasScrollPane, BorderLayout.WEST);

       // Panel "Compras"
        JPanel comprasPanel = new JPanel(new BorderLayout());
        tabbedPane.addTab("Compras", comprasPanel);

        DefaultListModel<String> comprasListModel = new DefaultListModel<>();   
  // Agregar el botón "Gestionar Pedido con Proveedores" al panel de Compras
        JButton gestionarPedidoProveedoresButton = new JButton("Gestionar Pedido con Proveedores");
        gestionarPedidoProveedoresButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                GestionarPedidoConProveedores();
            }
        });

        comprasListModel.addElement("Gestionar Pedido con Proveedores");

        JList<String> comprasList = new JList<>(comprasListModel);
        comprasList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        comprasList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selectedItem = comprasList.getSelectedValue();
                if (selectedItem != null) {
                    if (selectedItem.equals("Gestionar Pedido con Proveedores")) {
                        GestionarPedidoConProveedores(); // Llama al método para gestionar pedidos con proveedores
                    }
                }
            }
        });

        JScrollPane comprasScrollPane = new JScrollPane(comprasList);
        comprasScrollPane.setBorder(BorderFactory.createEmptyBorder());
        comprasPanel.add(comprasScrollPane, BorderLayout.WEST);

            
        
        //PANEL INVENTARIO
        JPanel inventarioPanel = new JPanel(new BorderLayout());
        tabbedPane.addTab("Inventario", inventarioPanel);

        DefaultListModel<String> inventarioListModel = new DefaultListModel<>();
        inventarioListModel.addElement("Juegos en Existencia");

        JList<String> inventarioList = new JList<>(inventarioListModel);
        inventarioList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        inventarioList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selectedItem = inventarioList.getSelectedValue();
                if (selectedItem != null) {
                    abrirVentana(selectedItem);
                }
            }
        });

        JScrollPane inventarioScrollPane = new JScrollPane(inventarioList);
        inventarioScrollPane.setBorder(BorderFactory.createEmptyBorder());
        inventarioPanel.add(inventarioScrollPane, BorderLayout.WEST);
        
        //PANEL REPORTES
        JPanel reportesPanel = new JPanel(new BorderLayout());
        tabbedPane.addTab("Reportes", reportesPanel);

        DefaultListModel<String> reportesListModel = new DefaultListModel<>();
        reportesListModel.addElement(" Facturas");

        JList<String> reportesList = new JList<>(reportesListModel);
        reportesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        reportesList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selectedItem = reportesList.getSelectedValue();
                if (selectedItem != null) {
                    mostrarPanelFactura();
                }
            }
        });

        JScrollPane reportesScrollPane = new JScrollPane(reportesList);
        reportesScrollPane.setBorder(BorderFactory.createEmptyBorder());
        reportesPanel.add(reportesScrollPane, BorderLayout.WEST);

        add(tabbedPane, BorderLayout.CENTER);
    }

  private void abrirVentana(String titulo) {
    if (titulo.equals("Interacción con el Cliente")) {
        mostrarVentanaDatosCliente();
    } else if (titulo.equals("Juegos en Existencia")) {
        if (windows.containsKey(titulo)) {
            windows.get(titulo).setVisible(true);
        } else {
            JFrame ventana = new JFrame("Productos en Existencia");
            ventana.setSize(500, 300);
            ventana.setLocationRelativeTo(this);
            ventana.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

       
            
            
            
            DefaultTableModel tableModel = new DefaultTableModel();
            tableModel.addColumn("Cantidad en Existencia");  // Agrega la columna para mostrar la cantidad
            tableModel.addColumn("Nombre");
            tableModel.addColumn("Precio");
           

            Conexion conexion = new Conexion();
            List<Producto> productos = conexion.getProductosFromDatabase();

            for (Producto producto : productos) {
                tableModel.addRow(new Object[]{producto.getCantidad(), producto.getNombre(), producto.getPrecio()});
            }

            JTable table = new JTable(tableModel);
            JScrollPane scrollPane = new JScrollPane(table);
            ventana.add(scrollPane);
            
            windows.put(titulo, ventana);
            ventana.setVisible(true);
        }
    } else {
        if (windows.containsKey(titulo)) {
            windows.get(titulo).setVisible(true);
        } else {
            JFrame ventana = new JFrame(titulo);
            ventana.setSize(400, 300);
            ventana.setLocationRelativeTo(this);

            ventana.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                    ventana.setVisible(false);
                }
            });

            windows.put(titulo, ventana);
            ventana.setVisible(true);
        }
    }
}

    
    private void mostrarPanelFactura() {
    JFrame ventanaFactura = new JFrame();
    ventanaFactura.setSize(500, 550);
    ventanaFactura.setLocationRelativeTo(null);

    JPanel panelFactura = new JPanel(new GridBagLayout());
    GridBagConstraints constraints = new GridBagConstraints();
    constraints.insets = new Insets(5, 5, 5, 5);

    JLabel labelNombreCliente = new JLabel("NOMBRES O APELLIDOS:");
    constraints.gridx = 0;
    constraints.gridy = 0;
    constraints.anchor = GridBagConstraints.WEST;
    panelFactura.add(labelNombreCliente, constraints);

    JTextField nombresApellidosField = new JTextField(15);
    int textFieldWidth = (int) (0.7 * 25.4);
    int textFieldHeight = (int) (3 * 10);
    nombresApellidosField.setPreferredSize(new Dimension(textFieldWidth, textFieldHeight));
    constraints.gridx = 1;
    constraints.gridy = 0;
    constraints.fill = GridBagConstraints.HORIZONTAL;
    panelFactura.add(nombresApellidosField, constraints);

    JButton buscarButton = new JButton("BUSCAR FACTURA");
    constraints.gridx = 0;
    constraints.gridy = 1;
    constraints.anchor = GridBagConstraints.SOUTHEAST;
    panelFactura.add(buscarButton, constraints);
    
        // Crear el botón "Generar Factura"
    JButton generarFacturaButton = new JButton("GENERAR FACTURA");
    generarFacturaButton.setPreferredSize(buscarButton.getPreferredSize()); // Hacer que sea del mismo tamaño que el botón "Buscar Factura"

    constraints.gridx = 1;
    constraints.gridy = 1;
    constraints.anchor = GridBagConstraints.SOUTHWEST; // Alinearlo al suroeste
    panelFactura.add(generarFacturaButton, constraints);
    
     // Agregar un ActionListener al botón "Generar Factura"
    generarFacturaButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            // Obtener el nombre o apellidos ingresados por el usuario
            String nombresApellidos = nombresApellidosField.getText().trim();

            if (!nombresApellidos.isEmpty()) {
                // Llamar a un método para generar la factura y mostrarla en una nueva ventana
                generarFactura(nombresApellidos);
            }
        }
    });
    
    // Crear una tabla para mostrar los resultados
    DefaultTableModel facturaTableModel = new DefaultTableModel();
    facturaTableModel.addColumn("ID Factura");
    facturaTableModel.addColumn("Nombres");
    facturaTableModel.addColumn("Apellidos");
    facturaTableModel.addColumn("Total a Pagar");

    JTable facturaTable = new JTable(facturaTableModel);
    JScrollPane facturaScrollPane = new JScrollPane(facturaTable);
    constraints.gridx = 0;
    constraints.gridy = 2;
    constraints.gridwidth = 2;
    constraints.fill = GridBagConstraints.BOTH;
    panelFactura.add(facturaScrollPane, constraints);
    
    facturaTable.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == 2) {
                int selectedRow = facturaTable.getSelectedRow();
                if (selectedRow >= 0) {
                    int facturaId = (int) facturaTableModel.getValueAt(selectedRow, 0); // ID de la factura

                 //   mostrarFacturaDetallada(facturaId); // Método para mostrar la factura detallada
                }
            }
        }
    });

    buscarButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            String nombresApellidos = nombresApellidosField.getText().trim();
            if (!nombresApellidos.isEmpty()) {
                buscarFacturasEnBaseDeDatos(nombresApellidos, facturaTableModel);
            }
        }
    });

    ventanaFactura.add(panelFactura);
    ventanaFactura.setVisible(true);
    
    }

private void generarFactura(String nombresApellidos) {
    // Realizar una consulta SQL para obtener los datos de la factura desde la base de datos
    String query = "SELECT f.ID, f.nombres, f.apellidos, f.cui, f.nit, f.total_pagar, fd.cantidad, fd.producto_nombre, fd.precio_total " +
            "FROM factura f " +
            "LEFT JOIN factura_detalle fd ON f.ID = fd.factura_id " +
            "WHERE f.nombres LIKE ? OR f.apellidos LIKE ?";
    
    try {
        PreparedStatement preparedStatement = conexion.conni.prepareStatement(query);
        preparedStatement.setString(1, "%" + nombresApellidos + "%");
        preparedStatement.setString(2, "%" + nombresApellidos + "%");
        ResultSet resultSet = preparedStatement.executeQuery();

        // Crear una ventana para mostrar los datos de la factura
        JFrame ventanaFactura = new JFrame("Factura Generada");
        ventanaFactura.setSize(800, 600);
        ventanaFactura.setLocationRelativeTo(null);

        // Crear un JPanel para mostrar los datos de la factura
        JPanel panelFactura = new JPanel(new BorderLayout());

        // Crear una tabla para mostrar los resultados de la consulta
        DefaultTableModel facturaTableModel = new DefaultTableModel();
   
        facturaTableModel.addColumn("Cantidad");
        facturaTableModel.addColumn("videojuegos");
        facturaTableModel.addColumn("Precio Total");

        // Llenar la tabla con los datos de la factura
        while (resultSet.next()) {
 
            int cantidad = resultSet.getInt("cantidad");
            String productoNombre = resultSet.getString("producto_nombre");
            double precioTotal = resultSet.getDouble("precio_total");

            facturaTableModel.addRow(new Object[]{/*idFactura, nombresFactura, apellidosFactura, cuiFactura, nitFactura,*/ cantidad, productoNombre, precioTotal});
        }

        JTable facturaTable = new JTable(facturaTableModel);
        JScrollPane facturaScrollPane = new JScrollPane(facturaTable);
        panelFactura.add(facturaScrollPane, BorderLayout.CENTER);

        // Ahora, utiliza el mismo código que proporcioné anteriormente para mostrar la ventana de factura generada
        mostrarVentanaFacturaGenerada(ventanaFactura, panelFactura, facturaTableModel);

    } catch (SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error al buscar facturas en la base de datos: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}

private void mostrarVentanaFacturaGenerada(JFrame ventanaFactura, JPanel panelFactura, DefaultTableModel facturaTableModel) {
    // Crear una ventana para mostrar los detalles de la factura generada
    JFrame facturaGeneradaFrame = new JFrame("Factura Generada");
    facturaGeneradaFrame.setSize(800, 600);
    facturaGeneradaFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Para cerrar solo esta ventana
    facturaGeneradaFrame.setLocationRelativeTo(null);

    JPanel facturaGeneradaPanel = new JPanel(new BorderLayout());

    // Crear un JPanel para mostrar los detalles del cliente
    JPanel datosClientePanel = new JPanel(new GridLayout(4, 2));
    datosClientePanel.setBorder(BorderFactory.createTitledBorder("Datos del Cliente"));

    // Agregar etiquetas y campos de texto para mostrar los datos del cliente
    JTextField nombresField = new JTextField(15);
    JTextField apellidosField = new JTextField(15);
    JTextField cuiField = new JTextField(15);
    JTextField nitField = new JTextField(15);

    nombresField.setEditable(false);
    apellidosField.setEditable(false);
    cuiField.setEditable(false);
    nitField.setEditable(false);

    // Obtener los datos del cliente de la base de datos
    Cliente cliente = obtenerDatosClienteDesdeBD(); // Debes implementar esta función para obtener los datos del cliente

    // Establecer los campos de texto con los datos reales del cliente
    nombresField.setText(cliente.getNombres());
    apellidosField.setText(cliente.getApellidos());
    cuiField.setText(cliente.getCui());
    nitField.setText(cliente.getNit());

    // Agregar los campos al panel de datos del cliente
    datosClientePanel.add(new JLabel("Nombres:"));
    datosClientePanel.add(nombresField);
    datosClientePanel.add(new JLabel("Apellidos:"));
    datosClientePanel.add(apellidosField);
    datosClientePanel.add(new JLabel("CUI:"));
    datosClientePanel.add(cuiField);
    datosClientePanel.add(new JLabel("NIT:"));
    datosClientePanel.add(nitField);

    // Crear una tabla para mostrar los detalles de la factura
    JTable facturaDetalleTable = new JTable(facturaTableModel);
    JScrollPane facturaDetalleScrollPane = new JScrollPane(facturaDetalleTable);
    facturaGeneradaPanel.add(facturaDetalleScrollPane, BorderLayout.CENTER);

    // Crear un botón para generar un PDF (como se muestra en la respuesta anterior)
    JButton generarPDFButton = new JButton("Generar PDF");
    generarPDFButton.setForeground(Color.RED);

    generarPDFButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            // Crear un documento PDF
            Document document = new Document();
            try {
                // Especifica la ubicación y el nombre del archivo PDF
                String pdfFilePath = "factura_generada.pdf";
                PdfWriter.getInstance(document, new FileOutputStream(pdfFilePath));
                document.open();

                // Configuración de la página
                document.setPageSize(PageSize.A4); // Tamaño de página A4
                document.setMargins(40, 40, 40, 40); // Márgenes

                // Fuente personalizada para el título y el contenido
                Font titleFont = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD, BaseColor.RED);
                Font contentFont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL, BaseColor.BLACK);

                // Título
                Paragraph title = new Paragraph("Factura", titleFont);
                title.setAlignment(Element.ALIGN_CENTER);
                document.add(title);

                // Datos del Cliente
                Paragraph customerData = new Paragraph("Datos del Cliente:", contentFont);
                document.add(customerData);

                // Agregar datos del cliente
                document.add(new Paragraph("Nombres: " + cliente.getNombres(), contentFont));
                document.add(new Paragraph("Apellidos: " + cliente.getApellidos(), contentFont));
                document.add(new Paragraph("CUI: " + cliente.getCui(), contentFont));
                document.add(new Paragraph("NIT: " + cliente.getNit(), contentFont));

                // Espacio en blanco
                document.add(Chunk.NEWLINE);
                

                                // Pedido
                  Paragraph orderData = new Paragraph("Pedido:", contentFont);
                  orderData.setAlignment(Element.ALIGN_CENTER); // Centrar el texto
                  document.add(orderData);
                     // Espacio en blanco
                document.add(Chunk.NEWLINE);
                // Detalles de la Factura
                PdfPTable table = new PdfPTable(3);
                table.setWidthPercentage(100); // Ancho de tabla al 100%
                table.setWidths(new float[] { 1, 3, 2 });

                // Encabezados de la tabla
                PdfPCell cell1 = new PdfPCell(new Phrase("Cantidad", contentFont));
                PdfPCell cell2 = new PdfPCell(new Phrase("videojuegos", contentFont));
                PdfPCell cell3 = new PdfPCell(new Phrase("Precio Total", contentFont));
                table.addCell(cell1);
                table.addCell(cell2);
                table.addCell(cell3);

                // Agregar detalles de la factura desde la tabla facturaTableModel
                for (int row = 0; row < facturaTableModel.getRowCount(); row++) {
                    String cantidad = facturaTableModel.getValueAt(row, 0).toString();
                    String producto = facturaTableModel.getValueAt(row, 1).toString();
                    String precioTotal = facturaTableModel.getValueAt(row, 2).toString();

                    table.addCell(cantidad);
                    table.addCell(producto);
                    table.addCell(precioTotal);
                }
                
                document.add(table);

                // Cerrar el documento
                document.close();

                // Abrir el PDF en el visor predeterminado
                Desktop.getDesktop().open(new File(pdfFilePath));
            } catch (DocumentException | IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(facturaGeneradaFrame, "Error al generar y abrir el PDF", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    });

    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    buttonPanel.add(generarPDFButton);

    // Agregar el panel de datos del cliente y el botón de generación de PDF al panel principal
    facturaGeneradaPanel.add(datosClientePanel, BorderLayout.NORTH);
    facturaGeneradaPanel.add(buttonPanel, BorderLayout.SOUTH);

    facturaGeneradaFrame.add(facturaGeneradaPanel);
    facturaGeneradaFrame.setVisible(true);
}


private Cliente obtenerDatosClienteDesdeBD() {
    Conexion conexion = new Conexion();
    Cliente cliente = new Cliente();

    String query = "SELECT NOMBRES, APELLIDOS, CUI, NIT FROM clientes";

    try {
        PreparedStatement preparedStatement = conexion.conni.prepareStatement(query);

        ResultSet resultSet = preparedStatement.executeQuery();

        if (resultSet.next()) {
            cliente.setNombres(resultSet.getString("NOMBRES"));
            cliente.setApellidos(resultSet.getString("APELLIDOS"));
            cliente.setCui(resultSet.getString("CUI"));
            cliente.setNit(resultSet.getString("NIT"));
        }
    } catch (SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error al obtener datos del cliente: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    } finally {
        conexion.cerrarConexion();
    }

    return cliente;
}

private double obtenerTotalAPagarDesdeBD(String nombresApellidos) {
    double totalAPagar = 0.0;
    String query = "SELECT SUM(total_pagar) AS total FROM factura WHERE nombres LIKE ? OR apellidos LIKE ?";

    try {
        PreparedStatement preparedStatement = conexion.conni.prepareStatement(query);
        preparedStatement.setString(1, "%" + nombresApellidos + "%");
        preparedStatement.setString(2, "%" + nombresApellidos + "%");
        ResultSet resultSet = preparedStatement.executeQuery();

        if (resultSet.next()) {
            totalAPagar = resultSet.getDouble("total");
        }
    } catch (SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error al obtener el total a pagar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
    return totalAPagar;
}
    
    

private void buscarFacturasEnBaseDeDatos(String nombresApellidos, DefaultTableModel facturaTableModel) {
    try {
        String query = "SELECT ID, nombres, apellidos, total_pagar FROM factura " +
                "WHERE nombres LIKE ? OR apellidos LIKE ?";
        PreparedStatement preparedStatement = conexion.conni.prepareStatement(query);
        preparedStatement.setString(1, "%" + nombresApellidos + "%"); // Búsqueda por nombres
        preparedStatement.setString(2, "%" + nombresApellidos + "%"); // Búsqueda por apellidos

        ResultSet resultSet = preparedStatement.executeQuery();

        // Limpiar la tabla antes de mostrar los resultados
        while (facturaTableModel.getRowCount() > 0) {
            facturaTableModel.removeRow(0);
        }

        while (resultSet.next()) {
            int idFactura = resultSet.getInt("ID"); // Cambiado el nombre de la columna
            String nombres = resultSet.getString("nombres"); // Cambiado el nombre de la columna
            String apellidos = resultSet.getString("apellidos"); // Cambiado el nombre de la columna
            double totalPagar = resultSet.getDouble("total_pagar"); // Cambiado el nombre de la columna

            facturaTableModel.addRow(new Object[]{idFactura, nombres, apellidos, totalPagar});
        }
    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error al buscar facturas en la base de datos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}


     private void actualizarInventario() {
        List<Producto> productos = conexion.getProductosFromDatabase();

        int rowCount = tableModel.getRowCount();
        for (int i = rowCount - 1; i >= 0; i--) {
            tableModel.removeRow(i);
        }

        for (Producto producto : productos) {
            tableModel.addRow(new Object[]{producto.getNombre(), producto.getPrecio()});
        }
    }
         private void mostrarVentanaDatosCliente() {
    cliente = new Cliente();
    JFrame ventanaCliente = new JFrame();
    ventanaCliente.setTitle("Datos del Cliente");
    ventanaCliente.setSize(400, 300);
    ventanaCliente.setLayout(new GridLayout(5, 1));
    ventanaCliente.setLocationRelativeTo(this);

    JTextField nombresField = new JTextField();
    JTextField apellidosField = new JTextField();
    JTextField cuiField = new JTextField();
    JTextField nitField = new JTextField();

    ventanaCliente.add(new JLabel("NOMBRES:"));
    ventanaCliente.add(nombresField);
    ventanaCliente.add(new JLabel("APELLIDOS:"));
    ventanaCliente.add(apellidosField);
    ventanaCliente.add(new JLabel("CUI:"));
    ventanaCliente.add(cuiField);
    ventanaCliente.add(new JLabel("NIT:"));
    ventanaCliente.add(nitField);

    JButton continuarButton = new JButton("Continuar");
    continuarButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            cliente.setNombres(nombresField.getText());
            cliente.setApellidos(apellidosField.getText());
            cliente.setCui(cuiField.getText());
            cliente.setNit(nitField.getText());

            guardarClienteEnBaseDeDatos(cliente);

            mostrarVentanaPedido();
            ventanaCliente.dispose();
        }
    });
    ventanaCliente.add(continuarButton);

    ventanaCliente.setVisible(true);
}
         private void guardarClienteEnBaseDeDatos(Cliente cliente) {
    try {
   
        String query = "INSERT INTO clientes (NOMBRES, APELLIDOS, CUI, NIT) VALUES (?, ?, ?, ?)";
        PreparedStatement preparedStatement = conexion.conni.prepareStatement(query);
        preparedStatement.setString(1, cliente.getNombres());
        preparedStatement.setString(2, cliente.getApellidos());
        preparedStatement.setString(3, cliente.getCui());
        preparedStatement.setString(4, cliente.getNit());

        int rowsAffected = preparedStatement.executeUpdate();

        if (rowsAffected > 0) {
            JOptionPane.showMessageDialog(null, "Cliente agregado correctamente.");
        } else {
            JOptionPane.showMessageDialog(null, "No se pudo guardar el cliente en la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error al guardar cliente en la base de datos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}

private void mostrarVentanaPedido() {
    if (conexion == null) {
        conexion = new Conexion();
    }

    JFrame ventanaPedido = new JFrame();
    ventanaPedido.setTitle("Realizar Pedido");
    ventanaPedido.setSize(550, 300);
    ventanaPedido.setLayout(new BorderLayout());
    ventanaPedido.setLocationRelativeTo(this);


    JPanel inputPanel = new JPanel();
    inputPanel.setLayout(new GridLayout(2, 2));

     JPanel cantidadPanel = new JPanel();
    cantidadPanel.setLayout(new BorderLayout());
    
   JLabel cantidadLabel = new JLabel("Cantidad:");
    JTextField cantidadField = new JTextField();

    cantidadPanel.add(cantidadLabel, BorderLayout.WEST);
    cantidadPanel.add(cantidadField, BorderLayout.CENTER);

    JPanel productoPanel = new JPanel();
    productoPanel.setLayout(new BorderLayout());

    JLabel productoLabel = new JLabel("videojuegos:");
    JTextField productoField = new JTextField();

    productoPanel.add(productoLabel, BorderLayout.WEST);
    productoPanel.add(productoField, BorderLayout.CENTER);

     JButton agregarButton = new JButton("Agregar al Pedido");
    Dimension buttonDimension = new Dimension(200, 25);
    agregarButton.setPreferredSize(buttonDimension);
    inputPanel.add(cantidadPanel);
    inputPanel.add(productoPanel);
    inputPanel.add(agregarButton);
    
     agregarButton.addActionListener(new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
        String productoIngresado = productoField.getText().trim().toLowerCase(); // Convertir a minúsculas
        // Verificar si el producto existe en la base de datos
        double precioProducto = conexion.obtenerPrecioProducto(productoIngresado);
        if (precioProducto >= 0) {
            int cantidad = Integer.parseInt(cantidadField.getText());
            // Verificar si hay suficiente cantidad en la base de datos
            int cantidadDisponible = obtenerCantidadDisponible(productoIngresado);
            if (cantidadDisponible >= cantidad) {
                // Producto válido, agregar al pedido
                cliente.getPedido().put(productoIngresado, cantidad);
                // Disminuir la cantidad en la base de datos
                disminuirCantidadEnBaseDeDatos(productoIngresado, cantidad);
                JOptionPane.showMessageDialog(ventanaPedido, "videojuegos agregado al pedido.");
            } else {
                JOptionPane.showMessageDialog(ventanaPedido, "Cantidad insuficiente en inventario.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            // Producto no existe en la base de datos
            JOptionPane.showMessageDialog(ventanaPedido, "videojuego no existe. Por favor, ingrese un videojuego válido.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
});
        JButton finalizarButton = new JButton("Finalizar Pedido");
     finalizarButton.addActionListener(new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
        Map<String, Integer> pedidoCliente = cliente.getPedido();
        double precioTotal = 0.0;
        for (Map.Entry<String, Integer> entry : pedidoCliente.entrySet()) {
            String producto = entry.getKey();
            int cantidad = entry.getValue();
            double precioProducto = conexion.obtenerPrecioProducto(producto);
            if (precioProducto >= 0) {
                precioTotal += precioProducto * cantidad;
            } else {
                // Producto no encontrado en la base de datos
            }
        }

        guardarFacturaEnBaseDeDatos(cliente, pedidoCliente, precioTotal);

        mostrarVentanaFactura(cliente, precioTotal);
    }
});

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(inputPanel, BorderLayout.NORTH);
        topPanel.add(finalizarButton, BorderLayout.SOUTH);
        ventanaPedido.add(topPanel, BorderLayout.NORTH);

DefaultTableModel productosTableModel = new DefaultTableModel();
productosTableModel.addColumn("videojuegos");
productosTableModel.addColumn("Precio");
//productosTableModel.addColumn("Cantidad");

    List<Producto> productos = conexion.getProductosFromDatabase();

    for (Producto producto : productos) {
    productosTableModel.addRow(new Object[]{producto.getNombre(), producto.getPrecio(), /*producto.getCantidad()*/});
}
    
    JTable productosTable = new JTable(productosTableModel);
  
    JScrollPane productosScrollPane = new JScrollPane(productosTable);
    ventanaPedido.add(productosScrollPane, BorderLayout.CENTER);

    ventanaPedido.setVisible(true);

}

private int obtenerCantidadDisponible(String productoNombre) {
    String sql = "SELECT CANTIDAD FROM videojuegos WHERE NOMBRE_PRODUCTO = ?";
    try {
        PreparedStatement statement = conexion.conni.prepareStatement(sql);
        statement.setString(1, productoNombre);

        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            return resultSet.getInt("CANTIDAD");
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }

    return 0; 
}

private void disminuirCantidadEnBaseDeDatos(String productoNombre, int cantidad) {
    String sql = "UPDATE videojuegos SET CANTIDAD = CANTIDAD - ? WHERE NOMBRE_PRODUCTO = ?";
    try {
        PreparedStatement statement = conexion.conni.prepareStatement(sql);
        statement.setInt(1, cantidad);
        statement.setString(2, productoNombre);

        int rowsAffected = statement.executeUpdate();

        if (rowsAffected > 0) {
            // La cantidad se actualizó correctamente en la base de datos
            JOptionPane.showMessageDialog(null, "La cantidad se actualizó correctamente en la base de datos.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } else {
            // Ocurrió un error al actualizar la cantidad en la base de datos
            JOptionPane.showMessageDialog(null, "Ocurrió un error al actualizar la cantidad en la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
}



 private void mostrarVentanaFactura(Cliente cliente, double totalAPagar) {
    JFrame ventanaFactura = new JFrame("Factura");
    ventanaFactura.setTitle("Factura");
    ventanaFactura.setSize(700, 400);
    ventanaFactura.setLayout(new BorderLayout());
    ventanaFactura.setLocationRelativeTo(this);

    JPanel tituloPanel = new JPanel(new BorderLayout());
    JLabel tituloLabel = new JLabel("Factura");
    tituloLabel.setForeground(Color.RED);
  //  tituloLabel.setFont(new Font("Arial", Font.BOLD, 24));
    tituloLabel.setHorizontalAlignment(JLabel.CENTER);
    tituloPanel.add(tituloLabel, BorderLayout.CENTER);

    JPanel datosClientePanel = new JPanel(new GridLayout(4, 2));
    datosClientePanel.setBorder(BorderFactory.createTitledBorder("Datos del Cliente"));

    JTextField nombresField = new JTextField();
    JTextField apellidosField = new JTextField();
    JTextField cuiField = new JTextField();
    JTextField nitField = new JTextField();

    nombresField.setEditable(false);
    apellidosField.setEditable(false);
    cuiField.setEditable(false);
    nitField.setEditable(false);

    nombresField.setText(cliente.getNombres());
    apellidosField.setText(cliente.getApellidos());
    cuiField.setText(cliente.getCui());
    nitField.setText(cliente.getNit());

    datosClientePanel.add(new JLabel("Nombres:"));
    datosClientePanel.add(nombresField);
    datosClientePanel.add(new JLabel("Apellidos:"));
    datosClientePanel.add(apellidosField);
    datosClientePanel.add(new JLabel("CUI:"));
    datosClientePanel.add(cuiField);
    datosClientePanel.add(new JLabel("NIT:"));
    datosClientePanel.add(nitField);

    JTable pedidoTable = new JTable();
    DefaultTableModel model = (DefaultTableModel) pedidoTable.getModel();
    model.addColumn("Cant.");
    model.addColumn("videojuegos");
    model.addColumn("Precio Total");

    double precioTotal = totalAPagar; 

    for (Map.Entry<String, Integer> entry : cliente.getPedido().entrySet()) {
        String productoNombre = entry.getKey();
        int cantidad = entry.getValue();
        double precioProducto = conexion.obtenerPrecioProducto(productoNombre);

        if (precioProducto >= 0) {
            double precioTotalProducto = precioProducto * cantidad;
            model.addRow(new Object[]{cantidad, productoNombre, precioTotalProducto});
        } else {
    
            model.addRow(new Object[]{cantidad, productoNombre, "No Encontrado en inventario"});
        }
    }

    JScrollPane pedidoScrollPane = new JScrollPane(pedidoTable);
    pedidoScrollPane.setBorder(BorderFactory.createTitledBorder("Pedido"));

    JLabel totalLabel = new JLabel("Total a Pagar: " + precioTotal);

    JTextArea facturaText = new JTextArea();
    facturaText.setEditable(false);

    StringBuilder factura = new StringBuilder();
    factura.append("Factura\n\n");
    factura.append("Datos del Cliente:\n");
    factura.append("Nombres: ").append(cliente.getNombres()).append("\n");
    factura.append("Apellidos: ").append(cliente.getApellidos()).append("\n");
    factura.append("CUI: ").append(cliente.getCui()).append("\n");
    factura.append("NIT: ").append(cliente.getNit()).append("\n\n");

    factura.append("Pedido:\n\n");
    for (Map.Entry<String, Integer> entry : cliente.getPedido().entrySet()) {
        factura.append("videojuegos: ").append(entry.getKey()).append(", Cantidad: ").append(entry.getValue()).append("\n");
    }

    factura.append("Total a Pagar: ").append(precioTotal).append("\n");

    facturaText.setText(factura.toString());

    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

    JButton generarPDFButton = new JButton("Generar PDF");
    generarPDFButton.setForeground(Color.RED);

    generarPDFButton.addActionListener(new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
      
         // Crear un documento PDF
        Document document = new Document();
        try {
            // Especifica la ubicación y el nombre del archivo PDF
            String pdfFilePath = "factura.pdf";
            PdfWriter.getInstance(document, new FileOutputStream(pdfFilePath));
            document.open();

            // Configuración de la página
            document.setPageSize(PageSize.A4); // Tamaño de página A4
            document.setMargins(40, 40, 40, 40); // Márgenes

            // Fuente personalizada para el título y el contenido
            Font titleFont = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD, BaseColor.BLUE);
            Font contentFont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL, BaseColor.BLACK);

            // Título
            Paragraph title = new Paragraph("Factura", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            // Datos del Cliente
            Paragraph customerData = new Paragraph("Datos del Cliente:", contentFont);
            document.add(customerData);

            // Agregar datos del cliente
            document.add(new Paragraph("Nombres: " + cliente.getNombres(), contentFont));
            document.add(new Paragraph("Apellidos: " + cliente.getApellidos(), contentFont));
            document.add(new Paragraph("CUI: " + cliente.getCui(), contentFont));
            document.add(new Paragraph("NIT: " + cliente.getNit(), contentFont));

            // Espacio en blanco
            document.add(Chunk.NEWLINE);

              // Pedido
                  Paragraph orderData = new Paragraph("Pedido:", contentFont);
                  orderData.setAlignment(Element.ALIGN_CENTER); // Centrar el texto
                  document.add(orderData);
            document.add(Chunk.NEWLINE);
            // Agregar detalles del pedido
            PdfPTable table = new PdfPTable(3);
            table.setWidthPercentage(100); // Ancho de tabla al 100%
            table.setWidths(new float[] { 1, 3, 2 });

            // Encabezados de la tabla
            PdfPCell cell1 = new PdfPCell(new Phrase("Cantidad", contentFont));
            PdfPCell cell2 = new PdfPCell(new Phrase("videojuegos", contentFont));
            PdfPCell cell3 = new PdfPCell(new Phrase("Precio Total", contentFont));
            table.addCell(cell1);
            table.addCell(cell2);
            table.addCell(cell3);

            for (Entry<String, Integer> entry : cliente.getPedido().entrySet()) {
                String producto = entry.getKey();
                int cantidad = entry.getValue();
                double precioProducto = conexion.obtenerPrecioProducto(producto);
                double precioTotalProducto = precioProducto * cantidad;

                table.addCell(Integer.toString(cantidad));
                table.addCell(producto);
                table.addCell(Double.toString(precioTotalProducto));
            }

            document.add(table);
            document.add(Chunk.NEWLINE);
            // Total a Pagar
            document.add(new Paragraph("Total a Pagar: " + totalAPagar, contentFont));
            
            // Cerrar el documento
            document.close();

            // Abrir el PDF en el visor predeterminado
            Desktop.getDesktop().open(new File(pdfFilePath));
        } catch (DocumentException | IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(ventanaFactura, "Error al generar y abrir el PDF", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

});

    buttonPanel.add(generarPDFButton);

    JPanel bottomPanel = new JPanel(new BorderLayout());
    bottomPanel.add(totalLabel, BorderLayout.WEST);
    bottomPanel.add(buttonPanel, BorderLayout.EAST);

    ventanaFactura.add(datosClientePanel, BorderLayout.NORTH);
    ventanaFactura.add(pedidoScrollPane, BorderLayout.CENTER);
    ventanaFactura.add(bottomPanel, BorderLayout.SOUTH);

    ventanaFactura.setVisible(true);
}
 
 
  private void GestionarPedidoConProveedores() {
        if (conexion == null) {
            conexion = new Conexion();
        }

        JFrame ventanaPedidoProveedores = new JFrame();
        ventanaPedidoProveedores.setTitle("Gestionar Pedido con Proveedores");
        ventanaPedidoProveedores.setSize(550, 300);
        ventanaPedidoProveedores.setLayout(new BorderLayout());
        ventanaPedidoProveedores.setLocationRelativeTo(this);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(2, 2));

        JPanel cantidadPanel = new JPanel();
        cantidadPanel.setLayout(new BorderLayout());
        JLabel cantidadLabel = new JLabel("Cantidad:");
        JTextField cantidadField = new JTextField();
        cantidadPanel.add(cantidadLabel, BorderLayout.WEST);
        cantidadPanel.add(cantidadField, BorderLayout.CENTER);

        JPanel productoPanel = new JPanel();
        productoPanel.setLayout(new BorderLayout());
        JLabel productoLabel = new JLabel("videojuegos:");
        JTextField productoField = new JTextField();
        productoPanel.add(productoLabel, BorderLayout.WEST);
        productoPanel.add(productoField, BorderLayout.CENTER);

        JButton agregarButton = new JButton("Agregar Pedido a Proveedores");
        agregarButton.setPreferredSize(new Dimension(200, 25));

        inputPanel.add(cantidadPanel);
        inputPanel.add(productoPanel);
        inputPanel.add(agregarButton);

        agregarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String productoIngresado = productoField.getText().trim();
                double precioProducto = conexion.obtenerPrecioProducto(productoIngresado);

                if (precioProducto >= 0) {
                    int cantidad = Integer.parseInt(cantidadField.getText());

                    productosAgregados.add(productoIngresado);

                    // Aumentar la cantidad en la base de datos según lo que se ingresó
                    conexion.aumentarCantidadEnBaseDeDatos(productoIngresado, cantidad);

                    JOptionPane.showMessageDialog(ventanaPedidoProveedores, "videojuego agregado al pedido.");
                } else {
                    int opcion = JOptionPane.showConfirmDialog(ventanaPedidoProveedores, "El videojuego no existe. ¿Desea agregarlo a la base de datos?", "Confirmar Agregar videojuegos", JOptionPane.YES_NO_OPTION);

                    if (opcion == JOptionPane.YES_OPTION) {
                        // Agregar lógica para añadir el producto a la base de datos
                        conexion.agregarProductoALaBaseDeDatos(productoIngresado, 0); // Puedes cambiar el valor inicial
                        productosAgregados.add(productoIngresado);
                        JOptionPane.showMessageDialog(ventanaPedidoProveedores, "videojuego agregado al pedido.");
                    }
                }
            }
        });

        JButton finalizarButton = new JButton("Finalizar Pedido a Proveedores");
        finalizarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Agregar lógica para finalizar el pedido a proveedores
                // No se necesita aumentar la cantidad nuevamente aquí, ya se hizo en el botón "Agregar Pedido a Proveedores"

                JOptionPane.showMessageDialog(ventanaPedidoProveedores, "Pedido a proveedores finalizado.");
                productosAgregados.clear(); // Limpiar la lista de productos agregados
                ventanaPedidoProveedores.dispose();
            }
        });

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(inputPanel, BorderLayout.NORTH);
        topPanel.add(finalizarButton, BorderLayout.SOUTH);

        ventanaPedidoProveedores.add(topPanel, BorderLayout.NORTH);

        DefaultTableModel productosTableModel = new DefaultTableModel();
        productosTableModel.addColumn("Cantidad");
        productosTableModel.addColumn("videojuegos");
        productosTableModel.addColumn("Precio");

        List<Producto> productos = conexion.getProductosFromDatabase();

        for (Producto producto : productos) {
            productosTableModel.addRow(new Object[]{producto.getCantidad(), producto.getNombre(), producto.getPrecio()});
        }

        JTable productosTable = new JTable(productosTableModel);
        JScrollPane productosScrollPane = new JScrollPane(productosTable);

        ventanaPedidoProveedores.add(productosScrollPane, BorderLayout.CENTER);
        ventanaPedidoProveedores.setVisible(true);
    }

  
  
 private void guardarFacturaEnBaseDeDatos(Cliente cliente, Map<String, Integer> pedidoCliente, double totalAPagar) {
    try {
        Connection conn = conexion.conni; // Obtener la conexión a la base de datos

        // Insertar la factura en la tabla 'factura'
        String facturaQuery = "INSERT INTO factura (nombres, apellidos, cui, nit, total_pagar) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement facturaStatement = conn.prepareStatement(facturaQuery);
        facturaStatement.setString(1, cliente.getNombres());
        facturaStatement.setString(2, cliente.getApellidos());
        facturaStatement.setString(3, cliente.getCui());
        facturaStatement.setString(4, cliente.getNit());
        facturaStatement.setDouble(5, totalAPagar);
        facturaStatement.executeUpdate();

        // Obtener el ID de la factura recién insertada
        String obtenerIdFacturaQuery = "SELECT LAST_INSERT_ID()";
        PreparedStatement obtenerIdFacturaStatement = conn.prepareStatement(obtenerIdFacturaQuery);
        ResultSet idResultSet = obtenerIdFacturaStatement.executeQuery();
        int facturaId = 0;
        if (idResultSet.next()) {
            facturaId = idResultSet.getInt(1);
        }

        // Insertar los detalles de la factura en 'factura_detalle'
        String detalleQuery = "INSERT INTO factura_detalle (factura_id, producto_nombre, cantidad, precio_total) VALUES (?, ?, ?, ?)";
        PreparedStatement detalleStatement = conn.prepareStatement(detalleQuery);

        for (Map.Entry<String, Integer> entry : pedidoCliente.entrySet()) {
            String producto = entry.getKey();
            int cantidad = entry.getValue();
            double precioProducto = conexion.obtenerPrecioProducto(producto);
            double precioTotalProducto = precioProducto * cantidad;

            detalleStatement.setInt(1, facturaId);
            detalleStatement.setString(2, producto);
            detalleStatement.setInt(3, cantidad);
            detalleStatement.setDouble(4, precioTotalProducto);
            detalleStatement.executeUpdate();
        }

        JOptionPane.showMessageDialog(null, "Factura guardada en la base de datos correctamente.");
    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error al guardar la factura en la base de datos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}
}
