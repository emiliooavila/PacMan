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
    private int[][] mapita;
    private Graph<String, DefaultEdge> camino;
    private int clydeX;
    private int clydeY;
    private Pacman auxPacman;

    public Clyde(Pacman pacman, int[][] laberinto){
        try{
            imagenClyde=new ImageIcon(getClass().getResource("clyde.gif"));
        }catch(Exception e){
            System.err.println("Algo salio mal " + e);
        }
        this.clydeX = 16;
        this.clydeY = 14;
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
        Random rand = new Random();
        while (true) {
            try {
                Thread.sleep(400);
                String actual = nodoId(clydeX, clydeY);
                List<String> vecinos = Graphs.neighborListOf(camino, actual);

                if (!vecinos.isEmpty()) {
                    String siguiente = vecinos.get(rand.nextInt(vecinos.size()));
                    String[] auxCoordenadas = siguiente.split("_");
                    clydeX = Integer.parseInt(auxCoordenadas[0]);
                    clydeY = Integer.parseInt(auxCoordenadas[1]);
                }

            } catch (Exception e) {
                System.out.println("Algo salio mla" + e.getMessage());
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

    public void dibujarClyde(Graphics g) {
        if (imagenClyde != null) {
            g.drawImage(imagenClyde.getImage(), clydeX * 20, clydeY * 20, 20, 20, null);
        }
    }


}
