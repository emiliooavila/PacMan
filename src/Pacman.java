import javax.swing.*;


public class Pacman {

    private ImageIcon imagen;
    private int Xposicion, Yposicion;
    private  int Xinicial=14, Yinicial=23;

    public Pacman (){
        try{
            imagen=new ImageIcon(getClass().getResource("Pacmangif.gif"));
        }catch(Exception e){
            System.err.println("No se pudo imprimir el gif "+e);
        }
        Xposicion=Xinicial;
        Yposicion=Yinicial;
    }

    public void setImagen(ImageIcon imagen) {
        this.imagen = imagen;
    }
    public ImageIcon getImagen() {
        return this.imagen;
    }
    public void setXposcion(int Xposcion) {
        this.Xposicion = Xposcion;
    }
    public int getXposcion() {
        return Xposicion;
    }
    public void setYposcion(int Yposcion) {
        this.Yposicion = Yposcion;
    }
    public int getYposcion() {
        return Yposicion;
    }
    public void moverDerecha(){
        this.Xposicion++;
    }
    public void moverIzquierda(){
        this.Xposicion--;
    }
    public void moverAbajo(){
        this.Yposicion++;
    }
    public void moverArriba(){
        this.Yposicion--;
    }

}
