import javax.swing.*;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import java.awt.*;
import java.util.List;


public class Pikzy implements Runnable{
    private ImageIcon imagenPikzyNegro;
    private ImageIcon imagenPikzyBlanco;
    private int[][] mapita;
    private int[][] mapita2;
    private Graph<String, DefaultEdge> camino;
    private Graph<String, DefaultEdge> camino2;
    private int pikzyX;
    private int pikzyY;
    private Pacman auxPacman;
    private int estado = 1;
    private Timer timerCambio;

    public Pikzy(Pacman pacman, int [][] laberinto){
        try{
            imagenPikzyNegro=new ImageIcon(getClass().getResource("pikzyNegro.gif"));
            imagenPikzyBlanco=new ImageIcon(getClass().getResource("pikzyBlanco.gif"));
        }catch(Exception e){
            System.err.println("Algo salio mal " + e);
        }
        this.pikzyX = 12;
        this.pikzyY = 14;
        this.auxPacman=pacman;


        this.mapita2 =new int[][] {
                {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
                {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
                {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
                {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
                {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
                {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
                {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
                {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
                {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
                {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
                {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
                {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
                {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
                {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
                {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
                {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
                {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
                {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
                {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
                {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
                {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
                {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
                {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
                {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
                {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
                {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
                {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
                {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
                {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
                {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
                {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}
        };

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





        camino2 = new SimpleGraph<>(DefaultEdge.class);

        for (int y = 0; y < this.mapita2.length; y++) {
            for (int x = 0; x < this.mapita2[0].length; x++) {
                if (this.mapita2[y][x] != 1) {
                    String nodo = nodoId(x, y);
                    camino2.addVertex(nodo);
                }
            }
        }

        for (int y = 0; y < mapita2.length; y++) {
            for (int x = 0; x < mapita2[0].length; x++) {
                if (mapita2[y][x] == 1) continue;

                String actual = nodoId(x, y);

                if (x + 1 < mapita2[0].length && mapita2[y][x + 1] != 1) {
                    String derecha = nodoId(x + 1, y);
                    camino2.addEdge(actual, derecha);
                }
                if (y + 1 < mapita2.length && mapita2[y + 1][x] != 1) {
                    String abajo = nodoId(x, y + 1);
                    camino2.addEdge(actual, abajo);
                }
            }
        }


        timerCambio = new Timer(8539, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cambiarEstado();
            }
        });

        timerCambio.start();
    }

    private String nodoId(int x, int y) {
        return x + "_" + y;
    }


    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(450);
                String siguiente;

                if(estado==1){
                    siguiente = movimiento1Pikzy(auxPacman.getXposcion(), auxPacman.getYposcion());
                }
                else{
                    siguiente = movimiento2Pikzy(auxPacman.getXposcion(), auxPacman.getYposcion());
                }

                if (siguiente != null) {
                    String[] auxCoordenadas = siguiente.split("_");
                    pikzyX = Integer.parseInt(auxCoordenadas[0]);
                    pikzyY = Integer.parseInt(auxCoordenadas[1]);
                }

            } catch (Exception e) {
                pikzyX=12;
                pikzyY=14;
            }
        }

    }

    public String movimiento1Pikzy(int x, int y){
        String origen = nodoId(pikzyX, pikzyY);
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

    public String movimiento2Pikzy(int x, int y){
        String origen = nodoId(pikzyX, pikzyY);
        String destino = nodoId(x, y);

        DijkstraShortestPath<String, DefaultEdge> dijkstra2 = new DijkstraShortestPath<>(camino2);

        GraphPath<String, DefaultEdge> ruta2 = dijkstra2.getPath(origen, destino);

        if (ruta2 == null) return null;

        List<String> moverse2 = ruta2.getVertexList();
        if (moverse2.size() < 2){
            return origen;
        }

        return moverse2.get(1);

    }

    public void cambiarEstado(){
        if(estado==1){
            estado=2;
        }
        else{
            estado=1;
        }
    }

    public void dibujarPikzy(Graphics g) {
        if (estado==1) {
            g.drawImage(imagenPikzyNegro.getImage(), pikzyX * 20, pikzyY * 20, 20, 20, null);
        }
        else{
            g.drawImage(imagenPikzyBlanco.getImage(), pikzyX * 20, pikzyY * 20, 20, 20, null);
        }
    }


}
