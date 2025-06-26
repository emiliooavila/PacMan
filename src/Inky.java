import javax.swing.*;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import java.awt.*;
import java.util.*;
import java.util.List;

public class Inky implements Runnable{
    private ImageIcon imagenInky;
    private ImageIcon imagenAsustado;
    private int[][] mapita;
    private Graph<String, DefaultEdge> camino;
    private int inkyX;
    private int inkyY;
    private Pacman auxPacman;

    // Variables para control de pausa
    private volatile boolean pausado = false;
    private volatile boolean running = true;

    // Variables para el modo vulnerable
    private volatile boolean vulnerable = false;
    private final int CENTRO_X = 14; // Ajustar según posición inicial deseada
    private final int CENTRO_Y = 14; // Ajustar según posición inicial deseada

    public Inky(Pacman pacman, int [][] laberinto){
        try{
            imagenInky=new ImageIcon(getClass().getResource("inky.gif"));
            imagenAsustado=new ImageIcon(getClass().getResource("fantasmaAsustado.gif"));
        }catch(Exception e){
            System.err.println("Algo salio mal " + e);
        }
        this.inkyX = 14;
        this.inkyY = 14;
        this.auxPacman=pacman;

        this.mapita = laberinto;

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
        while (running) {
            try {
                // Verificar si está pausado
                while (pausado && running) {
                    Thread.sleep(100); // Esperar mientras está pausado
                }

                if (!running) break; // Salir si se detuvo el juego

                Thread.sleep(1000);

                String siguiente;

                if (vulnerable) {
                    // Comportamiento cuando es vulnerable: huir del Pacman
                    siguiente = movimientoVulnerable(auxPacman.getXposcion(), auxPacman.getYposcion());
                } else {
                    // Comportamiento normal: perseguir al Pacman
                    siguiente = movimientoInky(auxPacman.getXposcion(), auxPacman.getYposcion());
                }

                if (siguiente != null) {
                    String[] auxCoordenadas = siguiente.split("_");
                    inkyX = Integer.parseInt(auxCoordenadas[0]);
                    inkyY = Integer.parseInt(auxCoordenadas[1]);
                }

            } catch (InterruptedException e) {
                System.out.println("Inky interrumpido: " + e.getMessage());
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public String movimientoInky(int x, int y){
        String origen = nodoId(inkyX, inkyY);
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

    // Método para movimiento cuando es vulnerable (huir del Pacman)
    private String movimientoVulnerable(int pacmanX, int pacmanY) {
        String origen = nodoId(inkyX, inkyY);

        // Obtener todos los vecinos posibles
        List<String> vecinosDisponibles = new ArrayList<>();

        for (DefaultEdge edge : camino.edgesOf(origen)) {
            String vecino = camino.getEdgeSource(edge).equals(origen)
                    ? camino.getEdgeTarget(edge)
                    : camino.getEdgeSource(edge);
            vecinosDisponibles.add(vecino);
        }

        if (vecinosDisponibles.isEmpty()) {
            return origen;
        }

        // Encontrar el vecino más alejado del Pacman
        String mejorVecino = null;
        double maxDistancia = -1;

        for (String vecino : vecinosDisponibles) {
            String[] coords = vecino.split("_");
            int vx = Integer.parseInt(coords[0]);
            int vy = Integer.parseInt(coords[1]);

            double distancia = Math.sqrt(Math.pow(vx - pacmanX, 2) + Math.pow(vy - pacmanY, 2));

            if (distancia > maxDistancia) {
                maxDistancia = distancia;
                mejorVecino = vecino;
            }
        }

        return mejorVecino != null ? mejorVecino : origen;
    }

    public void dibujarInky(Graphics g) {
        if(vulnerable){
            g.drawImage(imagenAsustado.getImage(), inkyX * 20, inkyY * 20, 20, 20, null);
        }
        else{
            g.drawImage(imagenInky.getImage(), inkyX * 20, inkyY * 20, 20, 20, null);
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
            System.out.println("Inky ahora es vulnerable!");
        } else {
            System.out.println("Inky ya no es vulnerable.");
        }
    }


    public boolean isVulnerable() {
        return vulnerable;
    }


    public void reiniciarEnCentro() {
        // Establecer coordenadas del centro (ajustar según cada fantasma)
        inkyX = CENTRO_X;
        inkyY = CENTRO_Y;
        vulnerable = false;
        System.out.println("Inky reiniciado en el centro del mapa");
    }


    public int getInkyX() {
        return inkyX;
    }

    public int getInkyY() {
        return inkyY;
    }
}