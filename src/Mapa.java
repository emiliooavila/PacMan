import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.RoundRectangle2D;

public class Mapa extends JPanel implements Runnable, KeyListener {

    // Constantes del mapa
    public static final int CELL_SIZE = 20;
    public static final int WIDTH = 28;
    public static final int HEIGHT = 31;

    // Variables de pantalla
    private int screenWidth;
    private int screenHeight;
    private double scaleX, scaleY;

    // Variables del juego
    private int highScore = 0;
    private int vidas = 3;
    private boolean running = true;
    private Thread gameThread;

    // Variables para efectos
    private long tiempoInicio;
    private boolean mostrarPuntos = true;

    // Matriz del laberinto
    // 0 = espacio vacío, 1 = pared, 2 = punto, 3 = power pellet
    private int[][] laberinto = {
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
            {1,2,2,2,2,2,2,2,2,2,2,2,2,1,1,2,2,2,2,2,2,2,2,2,2,2,2,1},
            {1,2,1,1,1,1,2,1,1,1,1,1,2,1,1,2,1,1,1,1,1,2,1,1,1,1,2,1},
            {1,3,1,1,1,1,2,1,1,1,1,1,2,1,1,2,1,1,1,1,1,2,1,1,1,1,3,1},
            {1,2,1,1,1,1,2,1,1,1,1,1,2,1,1,2,1,1,1,1,1,2,1,1,1,1,2,1},
            {1,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,1},
            {1,2,1,1,1,1,2,1,1,2,1,1,1,1,1,1,1,1,2,1,1,2,1,1,1,1,2,1},
            {1,2,1,1,1,1,2,1,1,2,1,1,1,1,1,1,1,1,2,1,1,2,1,1,1,1,2,1},
            {1,2,2,2,2,2,2,1,1,2,2,2,2,1,1,2,2,2,2,1,1,2,2,2,2,2,2,1},
            {1,1,1,1,1,1,2,1,1,1,1,1,0,1,1,0,1,1,1,1,1,2,1,1,1,1,1,1},
            {0,0,0,0,0,1,2,1,1,1,1,1,0,1,1,0,1,1,1,1,1,2,1,0,0,0,0,0},
            {0,0,0,0,0,1,2,1,1,0,0,0,0,0,0,0,0,0,0,1,1,2,1,0,0,0,0,0},
            {0,0,0,0,0,1,2,1,1,0,1,1,1,0,0,1,1,1,0,1,1,2,1,0,0,0,0,0},
            {1,1,1,1,1,1,2,1,1,0,1,0,0,0,0,0,0,1,0,1,1,2,1,1,1,1,1,1},
            {0,0,0,0,0,0,2,0,0,0,1,0,0,0,0,0,0,1,0,0,0,2,0,0,0,0,0,0},
            {1,1,1,1,1,1,2,1,1,0,1,0,0,0,0,0,0,1,0,1,1,2,1,1,1,1,1,1},
            {0,0,0,0,0,1,2,1,1,0,1,1,1,1,1,1,1,1,0,1,1,2,1,0,0,0,0,0},
            {0,0,0,0,0,1,2,1,1,0,0,0,0,0,0,0,0,0,0,1,1,2,1,0,0,0,0,0},
            {0,0,0,0,0,1,2,1,1,0,1,1,1,1,1,1,1,1,0,1,1,2,1,0,0,0,0,0},
            {1,1,1,1,1,1,2,1,1,0,1,1,1,1,1,1,1,1,0,1,1,2,1,1,1,1,1,1},
            {1,2,2,2,2,2,2,2,2,2,2,2,2,1,1,2,2,2,2,2,2,2,2,2,2,2,2,1},
            {1,2,1,1,1,1,2,1,1,1,1,1,2,1,1,2,1,1,1,1,1,2,1,1,1,1,2,1},
            {1,2,1,1,1,1,2,1,1,1,1,1,2,1,1,2,1,1,1,1,1,2,1,1,1,1,2,1},
            {1,3,2,2,1,1,2,2,2,2,2,2,2,0,0,2,2,2,2,2,2,2,1,1,2,2,3,1},
            {1,1,1,2,1,1,2,1,1,2,1,1,1,1,1,1,1,1,2,1,1,2,1,1,2,1,1,1},
            {1,1,1,2,1,1,2,1,1,2,1,1,1,1,1,1,1,1,2,1,1,2,1,1,2,1,1,1},
            {1,2,2,2,2,2,2,1,1,2,2,2,2,1,1,2,2,2,2,1,1,2,2,2,2,2,2,1},
            {1,2,1,1,1,1,1,1,1,1,1,1,2,1,1,2,1,1,1,1,1,1,1,1,1,1,2,1},
            {1,2,1,1,1,1,1,1,1,1,1,1,2,1,1,2,1,1,1,1,1,1,1,1,1,1,2,1},
            {1,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,1},
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}
    };

    private void dibujarTubo(Graphics g, int x, int y, int pixelX, int pixelY) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(new Color(0, 0, 255));
        g2d.setStroke(new BasicStroke(2));

        // Verificar bloques adyacentes
        boolean arriba = (y > 0) && (laberinto[y-1][x] == 1);
        boolean abajo = (y < HEIGHT-1) && (laberinto[y+1][x] == 1);
        boolean izquierda = (x > 0) && (laberinto[y][x-1] == 1);
        boolean derecha = (x < WIDTH-1) && (laberinto[y][x+1] == 1);

        int margin = 1;
        int arcSize = CELL_SIZE / 4;

        // Coordenadas del rectángulo
        int x1 = pixelX + margin;
        int y1 = pixelY + margin;
        int x2 = pixelX + CELL_SIZE - margin;
        int y2 = pixelY + CELL_SIZE - margin;
        int w = CELL_SIZE - 2 * margin;
        int h = CELL_SIZE - 2 * margin;

        // Si es un bloque aislado, dibujar rectángulo completo redondeado
        if (!arriba && !abajo && !izquierda && !derecha) {
            RoundRectangle2D roundRect = new RoundRectangle2D.Double(x1, y1, w, h, arcSize, arcSize);
            g2d.draw(roundRect);
            return;
        }

        // Dibujar bordes selectivamente para crear efecto de tubo

        // Borde superior
        if (!arriba) {
            if (!izquierda && !derecha) {
                // Borde superior completamente redondeado
                g2d.drawArc(x1, y1, arcSize, arcSize, 90, 90); // esquina sup-izq
                g2d.drawLine(x1 + arcSize/2, y1, x2 - arcSize/2, y1); // línea superior
                g2d.drawArc(x2 - arcSize, y1, arcSize, arcSize, 0, 90); // esquina sup-der
            } else if (!izquierda) {
                // Redondeado solo en esquina superior izquierda
                g2d.drawArc(x1, y1, arcSize, arcSize, 90, 90);
                g2d.drawLine(x1 + arcSize/2, y1, x2, y1);
            } else if (!derecha) {
                // Redondeado solo en esquina superior derecha
                g2d.drawLine(x1, y1, x2 - arcSize/2, y1);
                g2d.drawArc(x2 - arcSize, y1, arcSize, arcSize, 0, 90);
            } else {
                // Sin redondeo si hay conexiones a ambos lados
                g2d.drawLine(x1, y1, x2, y1);
            }
        }

        // Borde inferior
        if (!abajo) {
            if (!izquierda && !derecha) {
                // Borde inferior completamente redondeado
                g2d.drawArc(x1, y2 - arcSize, arcSize, arcSize, 180, 90); // esquina inf-izq
                g2d.drawLine(x1 + arcSize/2, y2, x2 - arcSize/2, y2); // línea inferior
                g2d.drawArc(x2 - arcSize, y2 - arcSize, arcSize, arcSize, 270, 90); // esquina inf-der
            } else if (!izquierda) {
                // Redondeado solo en esquina inferior izquierda
                g2d.drawArc(x1, y2 - arcSize, arcSize, arcSize, 180, 90);
                g2d.drawLine(x1 + arcSize/2, y2, x2, y2);
            } else if (!derecha) {
                // Redondeado solo en esquina inferior derecha
                g2d.drawLine(x1, y2, x2 - arcSize/2, y2);
                g2d.drawArc(x2 - arcSize, y2 - arcSize, arcSize, arcSize, 270, 90);
            } else {
                // Sin redondeo si hay conexiones a ambos lados
                g2d.drawLine(x1, y2, x2, y2);
            }
        }

        // Borde izquierdo
        if (!izquierda) {
            if (!arriba && !abajo) {
                // Ya dibujado en las esquinas superiores/inferiores
            } else if (!arriba) {
                // Desde esquina redondeada hacia abajo
                g2d.drawLine(x1, y1 + arcSize/2, x1, y2);
            } else if (!abajo) {
                // Desde arriba hacia esquina redondeada
                g2d.drawLine(x1, y1, x1, y2 - arcSize/2);
            } else {
                // Línea completa si hay conexiones arriba y abajo
                g2d.drawLine(x1, y1, x1, y2);
            }
        }

        // Borde derecho
        if (!derecha) {
            if (!arriba && !abajo) {
                // Ya dibujado en las esquinas superiores/inferiores
            } else if (!arriba) {
                // Desde esquina redondeada hacia abajo
                g2d.drawLine(x2, y1 + arcSize/2, x2, y2);
            } else if (!abajo) {
                // Desde arriba hacia esquina redondeada
                g2d.drawLine(x2, y1, x2, y2 - arcSize/2);
            } else {
                // Línea completa si hay conexiones arriba y abajo
                g2d.drawLine(x2, y1, x2, y2);
            }
        }

        // Restaurar stroke normal
        g2d.setStroke(new BasicStroke(1));
    };

    public Mapa() {
        // Configurar la pantalla completa
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        screenWidth = gd.getDisplayMode().getWidth();
        screenHeight = gd.getDisplayMode().getHeight();

        // Calcular escala para ajustar el mapa a la pantalla
        int mapPixelWidth = WIDTH * CELL_SIZE;
        int mapPixelHeight = HEIGHT * CELL_SIZE + 100; // +100 para UI

        scaleX = (double) screenWidth / mapPixelWidth;
        scaleY = (double) (screenHeight - 100) / mapPixelHeight;

        // Usar la escala menor para mantener proporciones
        double scale = Math.min(scaleX, scaleY);
        scaleX = scaleY = scale;

        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);

        // Inicializar tiempo para efectos
        tiempoInicio = System.currentTimeMillis();

        // Iniciar el hilo del juego
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Habilitar antialiasing
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Centrar el dibujo
        int offsetX = (int) ((screenWidth - WIDTH * CELL_SIZE * scaleX) / 2);
        int offsetY = 80;

        g2d.translate(offsetX, offsetY);
        g2d.scale(scaleX, scaleY);

        // Dibujar el mapa
        dibujarMapa(g2d);

        // Resetear transformación para UI
        g2d.setTransform(new java.awt.geom.AffineTransform());

        // Dibujar UI
        dibujarUI(g2d);
    }

    private void dibujarMapa(Graphics2D g) {
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                int cellValue = laberinto[y][x];
                int pixelX = x * CELL_SIZE;
                int pixelY = y * CELL_SIZE;

                switch (cellValue) {
                    case 1: // Paredes (bordes azules con efecto de tubo)
                        dibujarTubo(g, x, y, pixelX, pixelY);
                        break;

                    case 2: // Puntos (amarillo pequeño con parpadeo)
                        if (mostrarPuntos) {
                            g.setColor(Color.YELLOW);
                            int dotSize = CELL_SIZE / 6;
                            int dotX = pixelX + CELL_SIZE / 2 - dotSize / 2;
                            int dotY = pixelY + CELL_SIZE / 2 - dotSize / 2;
                            g.fillOval(dotX, dotY, dotSize, dotSize);
                        }
                        break;

                    case 3: // Power Pellets (rosa grande)
                        g.setColor(new Color(255, 184, 255));
                        int pelletSize = CELL_SIZE / 2;
                        int pelletX = pixelX + CELL_SIZE / 2 - pelletSize / 2;
                        int pelletY = pixelY + CELL_SIZE / 2 - pelletSize / 2;
                        g.fillOval(pelletX, pelletY, pelletSize, pelletSize);

                        // Efecto de brillo
                        g.setColor(Color.WHITE);
                        g.fillOval(pelletX + pelletSize/4, pelletY + pelletSize/4, pelletSize/4, pelletSize/4);
                        break;

                    case 0: // Espacio vacío
                        // No dibujar nada
                        break;
                }
            }
        }
    }

    private void dibujarUI(Graphics2D g) {
        // Configurar fuente
        Font font = new Font("Arial", Font.BOLD, 24);
        g.setFont(font);
        FontMetrics fm = g.getFontMetrics();

        // High Score
        g.setColor(Color.YELLOW);
        String highScoreText = "HIGH SCORE";
        int textWidth = fm.stringWidth(highScoreText);
        g.drawString(highScoreText, (screenWidth - textWidth) / 2, 30);

        // Puntuación
        String scoreText = String.format("%02d", highScore);
        textWidth = fm.stringWidth(scoreText);
        g.drawString(scoreText, (screenWidth - textWidth) / 2, 55);

        // Vidas (símbolos de Pac-Man en la parte inferior)
        int vidaSize = 30;
        int startX = 50;
        int vidaY = screenHeight - 50;

        g.setColor(Color.YELLOW);
        for (int i = 0; i < vidas; i++) {
            int x = startX + i * (vidaSize + 10);

            // Dibujar Pac-Man (círculo con boca)
            g.fillArc(x, vidaY, vidaSize, vidaSize, 45, 270);
        }

    }

    @Override
    public void run() {
        while (running) {
            // Actualizar efectos de parpadeo
            long tiempoActual = System.currentTimeMillis();
            long tiempoTranscurrido = tiempoActual - tiempoInicio;

            // Cambiar estado de parpadeo cada 500ms (medio segundo)
            mostrarPuntos = (tiempoTranscurrido / 500) % 2 == 0;

            // Actualizar lógica del juego aquí

            // Repintar
            repaint();

            try {
                Thread.sleep(16); // ~60 FPS
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    // Métodos para controlar el juego
    public void incrementarScore(int puntos) {
        highScore += puntos;
    }

    public void perderVida() {
        if (vidas > 0) {
            vidas--;
        }
    }

    public void agregarVida() {
        vidas++;
    }

    public int getVidas() {
        return vidas;
    }

    public int getHighScore() {
        return highScore;
    }

    public int[][] getLaberinto() {
        return laberinto;
    }

    public void setHighScore(int score) {
        this.highScore = score;
    }

    // Eventos de teclado (para futura expansión)
    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_ESCAPE:
                System.exit(0);
                break;
            case KeyEvent.VK_SPACE:
                // Pausar/reanudar juego
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}

    public void detener() {
        running = false;
        if (gameThread != null) {
            gameThread.interrupt();
        }
    }

    // Método main para probar la clase
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Pac-Man");
            Mapa mapa = new Mapa();

            frame.add(mapa);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            frame.setUndecorated(true); // Pantalla completa sin bordes
            frame.setVisible(true);

            mapa.requestFocusInWindow();
        });
    }
}