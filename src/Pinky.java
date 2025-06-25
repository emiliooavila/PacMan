
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
        while (true) {
            try {
                Thread.sleep(600);

                String siguiente = movimientoPinky(auxPacman.getXposcion(), auxPacman.getYposcion());

                if (siguiente != null) {
                    String[] auxCoordenadas = siguiente.split("_");
                    pinkyX = Integer.parseInt(auxCoordenadas[0]);
                    pinkyY = Integer.parseInt(auxCoordenadas[1]);
                }

            } catch (InterruptedException e) {
                System.out.println("Algo salio mla" + e.getMessage());
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

    public void dibujarPinky(Graphics g) {
        if (imagenPinky != null) {
            g.drawImage(imagenPinky.getImage(), pinkyX * 20, pinkyY * 20, 20, 20, null);
        }
    }


}
