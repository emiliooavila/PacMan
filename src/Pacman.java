import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Pacman implements Runnable, KeyListener {

    private ImageIcon imagen;
    private int Xposicion, Yposicion;
    private  int Xinicial=14, Yinicial=23;
    enum Direction {UP,DOWN,LEFT,RIGHT,NONE};
    private Mapa.Direction movment= Mapa.Direction.RIGHT;
    Mapa.Direction aux=movment;
    private Timer moveTimer;
    private int [][] laberinto;
    private final int WIDTH = 28;
    private final int HEIGHT = 31;
    boolean pause=false;

    public Pacman (int [][]Lab){
        try{
            imagen=new ImageIcon(getClass().getResource("Pacmangif.gif"));
        }catch(Exception e){
            System.err.println("No se pudo imprimir el gif "+e);
        }
        Xposicion=Xinicial;
        Yposicion=Yinicial;
        laberinto=Lab;
    }
    @Override
    public void run(){
        moveTimer=new Timer(350, e->{


            boolean can=false;
            int x = this.getXposcion();
            int y = this.getYposcion();
            if (aux != movment) {
                switch (aux) {
                    case UP:
                        if (y>0&&laberinto[y-1][x]!=1){
                            movment= Mapa.Direction.UP;
                        }
                        break;
                    case DOWN:
                        if (y<HEIGHT-1&&laberinto[y+1][x]!=1){
                            movment = Mapa.Direction.DOWN;
                        }
                        break;
                    case LEFT:
                        if (x>0&&laberinto[y][x-1]!=1){
                            movment = Mapa.Direction.LEFT;
                        }
                        break;
                    case RIGHT:
                        if(x<WIDTH-1&&laberinto[y][x+1]!=1){
                            movment = Mapa.Direction.RIGHT;
                        }
                        break;
                }
            }
            x = this.getXposcion();
            y = this.getYposcion();
            switch (movment) {
                case UP:
                    if(y>0&&laberinto[y-1][x]!=1){

                        try{
                            ImageIcon imagen=new ImageIcon(getClass().getResource("PacmangifUP.gif"));
                            this.setImagen(imagen);
                        }catch(Exception error){
                            System.err.println("No se pudo imprimir el gif "+error);
                        }
                        this.moverArriba();
                    }
                    break;
                case DOWN:
                    if (y<HEIGHT-1&&laberinto[y+1][x]!=1){
                        try{
                            ImageIcon imagen=new ImageIcon(getClass().getResource("PacmangifDOWN.gif"));
                            this.setImagen(imagen);
                        }catch(Exception error){
                            System.err.println("No se pudo imprimir el gif "+error);
                        }
                        this.moverAbajo();
                    }
                    break;
                case LEFT:
                    if(x>0&&laberinto[y][x-1]!=1){
                        try{
                            ImageIcon imagen=new ImageIcon(getClass().getResource("PacmangifLEFT.gif"));
                            this.setImagen(imagen);
                        }catch(Exception error){
                            System.err.println("No se pudo imprimir el gif "+error);
                        }
                        this.moverIzquierda();
                    }
                    break;
                case RIGHT:
                    if (x<WIDTH-1&&laberinto[y][x+1]!=1){
                        try{
                            ImageIcon imagen=new ImageIcon(getClass().getResource("Pacmangif.gif"));
                            this.setImagen(imagen);
                        }catch(Exception error){
                            System.err.println("No se pudo imprimir el gif "+error);
                        }

                        this.moverDerecha();
                    }
                    break;
                case NONE:
                    break;
            }


        });
        moveTimer.start();
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
    public synchronized int getXposcion() {
        return Xposicion;
    }
    public void setYposcion(int Yposcion) {
        this.Yposicion = Yposcion;
    }
    public synchronized int getYposcion() {
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
    public void setPause(boolean pause) {
        this.pause = pause;
        if(pause){
            moveTimer.stop();
        }
        else{
            moveTimer.start();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        moverPacman(e);
    }
    public void moverPacman(KeyEvent e){
        int x = this.getXposcion();
        int y = this.getYposcion();
        boolean can=false;

        switch (e.getKeyCode()){
            case KeyEvent.VK_UP:
                aux= Mapa.Direction.UP;
                break;
            case KeyEvent.VK_DOWN:
                aux= Mapa.Direction.DOWN;
                break;
            case KeyEvent.VK_LEFT:
                aux= Mapa.Direction.LEFT;
                break;
            case KeyEvent.VK_RIGHT:
                aux= Mapa.Direction.RIGHT;
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
