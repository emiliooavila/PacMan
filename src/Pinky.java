import javax.swing.*;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import java.awt.*;
import java.util.*;
import java.util.List;

public class Pinky implements Runnable{
    private ImageIcon imagenPinky;
    private int[][] mapita;
    private Graph<String, DefaultEdge> camino;
    private int pinkyX;
    private int pinkyY;
    private Pacman auxPacman;

    // Variables para control de pausa
    private volatile boolean pausado = false;
    private volatile boolean running = true;

    // Variables para el modo vulnerable
    private volatile boolean vulnerable = false;
    private final int CENTRO_X = 15; // Posición inicial de Pinky
    private final int CENTRO_Y = 14; // Posición inicial de Pinky

    public Pinky(Pacman pacman, int[][] laberinto){
        try{
            imagenPinky=new ImageIcon(getClass().getResource("pinky.gif"));
        }catch(Exception e){
            System.err.println("Algo salio mal " + e);
        }
        this.pinkyX = 15;
        this.pinkyY = 14;
        this.auxPacman=pacman;

        this.mapita = laberinto;

        camino = new SimpleGraph<>(DefaultEdge.class);

        for (int y = 0; y < this.mapita.length; y++) {
            for (int x = 0; x < this.mapita[0].length; x++) {
                if (this.mapita[y][x] != 1) {
                    String nodo = nodoId(x, y);
                    camino.addVertex(nodo);
                }
            }
        }

        for (int y = 0; y < mapita.length; y++) {
            for (int x = 0; x < mapita[0].length; x++) {
                if (mapita[y][x] == 1) continue;

                String actual = nodoId(x, y);

                if (x + 1 < mapita[0].length && mapita[y][x + 1] != 1) {
                    String derecha = nodoId(x + 1, y);
                    camino.addEdge(actual, derecha);
                }
                if (y + 1 < mapita.length && mapita[y + 1][x] != 1) {
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
        while (running) {
            try {
                // Verificar si está pausado
                while (pausado && running) {
                    Thread.sleep(100); // Esperar mientras está pausado
                }

                if (!running) break; // Salir si se detuvo el juego

                // Comportamiento diferente si es vulnerable
                if (vulnerable) {
                    Thread.sleep(800); // Más lento cuando es vulnerable

                    // Comportamiento de huida (moverse alejándose del Pacman)
                    String siguiente = movimientoHuida(auxPacman.getXposcion(), auxPacman.getYposcion());

                    if (siguiente != null) {
                        String[] auxCoordenadas = siguiente.split("_");
                        pinkyX = Integer.parseInt(auxCoordenadas[0]);
                        pinkyY = Integer.parseInt(auxCoordenadas[1]);
                    }
                } else {
                    Thread.sleep(600);

                    String siguiente = movimientoPinky(auxPacman.getXposcion(), auxPacman.getYposcion());

                    if (siguiente != null) {
                        String[] auxCoordenadas = siguiente.split("_");
                        pinkyX = Integer.parseInt(auxCoordenadas[0]);
                        pinkyY = Integer.parseInt(auxCoordenadas[1]);
                    }
                }

            } catch (InterruptedException e) {
                System.out.println("Pinky interrumpido: " + e.getMessage());
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public String movimientoPinky(int x, int y){
        String origen = nodoId(pinkyX, pinkyY);
        String destino = nodoId(x, y);

        if (!camino.containsVertex(origen) || !camino.containsVertex(destino)) {
            return null;
        }

        Queue<String> cola = new LinkedList<>();
        Map<String, String> visitadoDesde = new HashMap<>();

        cola.add(origen);
        visitadoDesde.put(origen, null);

        while (!cola.isEmpty()) {
            String actual = cola.poll();

            if (actual.equals(destino)) {
                break;
            }

            for (DefaultEdge edge : camino.edgesOf(actual)) {
                String vecino = camino.getEdgeSource(edge).equals(actual)
                        ? camino.getEdgeTarget(edge)
                        : camino.getEdgeSource(edge);

                if (!visitadoDesde.containsKey(vecino)) {
                    cola.add(vecino);
                    visitadoDesde.put(vecino, actual);
                }
            }
        }

        List<String> caminoReverso = new ArrayList<>();
        String paso = destino;
        while (paso != null) {
            caminoReverso.add(paso);
            paso = visitadoDesde.get(paso);
        }

        Collections.reverse(caminoReverso);

        if (caminoReverso.size() < 2){
            return origen;
        }

        return caminoReverso.get(1);
    }

    /**
     * Movimiento de huida cuando el fantasma es vulnerable
     */
    private String movimientoHuida(int pacmanX, int pacmanY) {
        String origen = nodoId(pinkyX, pinkyY);

        // Buscar el movimiento que más aleje del Pacman
        String mejorMovimiento = origen;
        double mayorDistancia = 0;

        for (DefaultEdge edge : camino.edgesOf(origen)) {
            String vecino = camino.getEdgeSource(edge).equals(origen)
                    ? camino.getEdgeTarget(edge)
                    : camino.getEdgeSource(edge);

            String[] coords = vecino.split("_");
            int vecinoX = Integer.parseInt(coords[0]);
            int vecinoY = Integer.parseInt(coords[1]);

            // Calcular distancia euclidiana al Pacman
            double distancia = Math.sqrt(Math.pow(vecinoX - pacmanX, 2) + Math.pow(vecinoY - pacmanY, 2));

            if (distancia > mayorDistancia) {
                mayorDistancia = distancia;
                mejorMovimiento = vecino;
            }
        }

        return mejorMovimiento;
    }

    public void dibujarPinky(Graphics g) {
        if (imagenPinky != null) {
            g.drawImage(imagenPinky.getImage(), pinkyX * 20, pinkyY * 20, 20, 20, null);
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

    /**
     * Establece si el fantasma es vulnerable (puede ser comido)
     */
    public void setVulnerable(boolean vulnerable) {
        this.vulnerable = vulnerable;
        if (vulnerable) {
            System.out.println("PINKY ahora es vulnerable!");
        } else {
            System.out.println("PINKY ya no es vulnerable.");
        }
    }

    /**
     * Verifica si el fantasma es vulnerable
     */
    public boolean isVulnerable() {
        return vulnerable;
    }

    /**
     * Reinicia el fantasma en el centro del mapa
     */
    public void reiniciarEnCentro() {
        pinkyX = CENTRO_X;
        pinkyY = CENTRO_Y;
        vulnerable = false;
        System.out.println("PINKY reiniciado en el centro del mapa");
    }

    /**
     * Obtiene la posición X actual
     */
    public int getPinkyX() {
        return pinkyX;
    }

    /**
     * Obtiene la posición Y actual
     */
    public int getPinkyY() {
        return pinkyY;
    }
}