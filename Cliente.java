package TiendaVideojuegos;
import java.util.HashMap;
import java.util.Map;

public class Cliente{
    private String nombres;
    private String apellidos;
    private String cui;
    private String nit;
    private Map<String, Integer> pedido = new HashMap<>();
 private double totalAPagar;
 public Cliente() {
        pedido = new HashMap<>();
        totalAPagar = 0.0;
    }
     
    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getCui() {
        return cui;
    }

    public void setCui(String cui) {
        this.cui = cui;
    }

    public String getNit() {
        return nit;
    }

    public void setNit(String nit) {
        this.nit = nit;
    }

    public Map<String, Integer> getPedido() {
        return pedido;
    }
    
    
    public void actualizarTotalAPagar(double precioProducto, int cantidad) {
        totalAPagar += precioProducto * cantidad;
    }

    public double getTotalAPagar() {
        return totalAPagar;
    }
}


