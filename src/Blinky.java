
import javax.swing.*;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import java.awt.*;
import java.util.List;


public class Blinky implements Runnable{
    private ImageIcon imagenBlinky;
    private int[][] mapita;
    private Graph<String, DefaultEdge> camino;
    private int blinkyX;
    private int blinkyY;
    private Pacman auxPacman;

    public Blinky(Pacman pacman, int[][] laberinto){
        try{
            imagenBlinky=new ImageIcon(getClass().getResource("blinky.gif"));
        }catch(Exception e){
            System.err.println("Algo salio mal " + e);
        }
        this.blinkyX = 13;
        this.blinkyY = 14;
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
                Thread.sleep(500);

                String siguiente = movimientoBlinky(auxPacman.getXposcion(), auxPacman.getYposcion());
                //ME FALTA PONER LOS PACMAN EN X Y EN Y

                if (siguiente != null) {
                    String[] auxCoordenadas = siguiente.split("_");
                    blinkyX = Integer.parseInt(auxCoordenadas[0]);
                    blinkyY = Integer.parseInt(auxCoordenadas[1]);
                }

            } catch (InterruptedException e) {
                System.out.println("Algo salio mla" + e.getMessage());
            }
        }

    }

    public String movimientoBlinky(int x, int y){
        String origen = nodoId(blinkyX, blinkyY);
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

    public void dibujarBlinky(Graphics g) {
        if (imagenBlinky != null) {
            g.drawImage(imagenBlinky.getImage(), blinkyX * 20, blinkyY * 20, 20, 20, null);
        }
    }


}
