import javax.swing.*;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import java.util.Random;
import java.awt.*;
import java.util.List;

public class Clyde implements Runnable{
    private ImageIcon imagenClyde;
    private ImageIcon imagenAsustado;
    private int[][] mapita;
    private Graph<String, DefaultEdge> camino;
    private int clydeX;
    private int clydeY;
    private Pacman auxPacman;

    // Variables para control de pausa
    private volatile boolean pausado = false;
    private volatile boolean running = true;

    // Variables para el modo vulnerable
    private volatile boolean vulnerable = false;
    private final int CENTRO_X = 14; // Posición central del laberinto
    private final int CENTRO_Y = 14;

    public Clyde(Pacman pacman, int[][] laberinto){
        try{
            imagenClyde=new ImageIcon(getClass().getResource("clyde.gif"));
            imagenAsustado=new ImageIcon((getClass().getResource("fantasmaAsustado.gif")));

        }catch(Exception e){
            System.err.println("Algo salio mal " + e);
        }

        // Inicializar en el centro del mapa
        this.clydeX = CENTRO_X;
        this.clydeY = CENTRO_Y;
        this.auxPacman=pacman;

        this.mapita = laberinto;

        // Resto del código del constructor...
        camino = new SimpleGraph<>(DefaultEdge.class);

        for (int y = 0; y < this.mapita.length; y++) {
            for (int x = 0; x < this.mapita[0].length; x++) {
                if (this.mapita[y][x] != 1 && this.mapita[y][x] != 5) {
                    String nodo = nodoId(x, y);
                    camino.addVertex(nodo);
                }
            }
        }

        for (int y = 0; y < mapita.length; y++) {
            for (int x = 0; x < mapita[0].length; x++) {
                if (mapita[y][x] == 1) continue;

                String actual = nodoId(x, y);

                if (x + 1 < mapita[0].length && mapita[y][x + 1] != 1 && this.mapita[y][x+1] != 5) {
                    String derecha = nodoId(x + 1, y);
                    camino.addEdge(actual, derecha);
                }
                if (y + 1 < mapita.length && mapita[y + 1][x] != 1 && this.mapita[y+1][x] != 5) {
                    String abajo = nodoId(x, y + 1);
                    camino.addEdge(actual, abajo);
                }
            }
        }
    }

    private String nodoId(int x, int y) {
        return x + "_" + y;
    }

    @Override
    public void run() {
        Random rand = new Random();
        while (running) {
            try {
                // Verificar si está pausado
                if (pausado) {
                    Thread.sleep(50);
                    continue;
                }

                if (!running) break;

                Thread.sleep(400);

                if (vulnerable) {
                    // Comportamiento cuando es vulnerable: huir de Pacman
                    moverAlejandoseDePacman();
                } else {
                    // Comportamiento normal: movimiento aleatorio
                    String actual = nodoId(clydeX, clydeY);
                    List<String> vecinos = Graphs.neighborListOf(camino, actual);

                    if (!vecinos.isEmpty()) {
                        String siguiente = vecinos.get(rand.nextInt(vecinos.size()));
                        String[] auxCoordenadas = siguiente.split("_");
                        clydeX = Integer.parseInt(auxCoordenadas[0]);
                        clydeY = Integer.parseInt(auxCoordenadas[1]);
                    }
                }

            } catch (InterruptedException e) {
                System.out.println("Clyde interrumpido: " + e.getMessage());
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public String movimientoClyde(int x, int y){
        String origen = nodoId(clydeX, clydeY);
        String destino = nodoId(x, y);

        DijkstraShortestPath<String, DefaultEdge> dijkstra = new DijkstraShortestPath<>(camino);

        GraphPath<String, DefaultEdge> ruta = dijkstra.getPath(origen, destino);

        if (ruta == null) return null;

        List<String> moverse = ruta.getVertexList();
        if (moverse.size() < 2){
            return origen;
        }

        return moverse.get(1);
    }

    private void moverAlejandoseDePacman() {
        if (auxPacman == null) return;

        int pacX = auxPacman.getXposcion();
        int pacY = auxPacman.getYposcion();

        String actual = nodoId(clydeX, clydeY);
        List<String> vecinos = Graphs.neighborListOf(camino, actual);

        if (vecinos.isEmpty()) return;

        // Encontrar el vecino más alejado de Pacman
        String mejorMovimiento = actual;
        double maxDistancia = 0;

        for (String vecino : vecinos) {
            String[] coords = vecino.split("_");
            int x = Integer.parseInt(coords[0]);
            int y = Integer.parseInt(coords[1]);

            // Calcular distancia a Pacman
            double distancia = Math.sqrt(Math.pow(x - pacX, 2) + Math.pow(y - pacY, 2));

            if (distancia > maxDistancia) {
                maxDistancia = distancia;
                mejorMovimiento = vecino;
            }
        }

        // Mover al mejor vecino (más alejado de Pacman)
        if (!mejorMovimiento.equals(actual)) {
            String[] auxCoordenadas = mejorMovimiento.split("_");
            clydeX = Integer.parseInt(auxCoordenadas[0]);
            clydeY = Integer.parseInt(auxCoordenadas[1]);
        }
    }

    public void setVulnerable(boolean vulnerable) {
        this.vulnerable = vulnerable;
    }

    public boolean isVulnerable() {
        return vulnerable;
    }

    public void reiniciarEnCentro() {
        clydeX = CENTRO_X;
        clydeY = CENTRO_Y;
        vulnerable = false; // Al reiniciar, ya no es vulnerable
    }

    public int getClydeX() {
        return clydeX;
    }

    public int getClydeY() {
        return clydeY;
    }

    public void dibujarClyde(Graphics g) {
        if(vulnerable){
            g.drawImage(imagenAsustado.getImage(), clydeX * 20, clydeY * 20, 20, 20, null);
        }
        else{
            g.drawImage(imagenClyde.getImage(), clydeX * 20, clydeY * 20, 20, 20, null);
        }


    }

    // Métodos para controlar la pausa
    public void setPausado(boolean pausado) {
        this.pausado = pausado;
    }

    public void detener() {
        this.running = false;
    }

    public boolean isPausado() {
        return pausado;
    }
}