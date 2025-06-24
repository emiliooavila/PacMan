import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.RoundRectangle2D;

public class Mapa3 extends JPanel implements Runnable, KeyListener {

    // Constantes del mapa
    public static final int CELL_SIZE = 20;
    public static final int WIDTH = 28;
    public static final int HEIGHT = 31;

    // Variables de pantalla
    private int screenWidth;
    private int screenHeight;
    private double scaleX, scaleY;

    //Instancia para regresar a PantallaInicio
    private JFrame ventanaInicio;

    // Variables del juego
    private int highScore = 0;
    private int vidas = 3;
    private boolean running = true;
    private Thread gameThread;

    // Variables para el menú de pausa
    private boolean enPausa = false;
    private int opcionSeleccionada = 0; // 0 = Continuar, 1 = Salir
    private final String[] opcionesMenu = {"CONTINUAR", "SALIR AL MENÚ"};

    // Variables para efectos
    private long tiempoInicio;
    private boolean mostrarPuntos = true;

    enum Direction {UP, DOWN, LEFT, RIGHT, NONE}

    ;
    private Direction movment = Direction.RIGHT;

    Direction aux = movment;
    private Timer moveTimer;

    // Variables del sistema de jugadores
    private String jugadorActual;
    private java.util.List<String[]> listaJugadores;
    private int highScoreGlobal = 0;

    // Variables para el sistema de puntos
    private int puntosRestantes;
    private int puntajeNivel = 0;
    private boolean nivelCompletado = false;

    // Matriz del laberinto
    // 0 = espacio vacío, 1 = pared, 2 = punto, 3 = power pellet
    private int[][] laberinto = {
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
            {1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1},
            {1, 2, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 2, 1, 1, 2, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 2, 1},
            {1, 3, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 2, 1, 1, 2, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 3, 1},
            {1, 2, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 2, 1, 1, 2, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 2, 1},
            {1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1},
            {1, 2, 1, 1, 1, 1, 2, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1, 1, 2, 1, 1, 1, 1, 2, 1},
            {1, 2, 1, 1, 1, 1, 2, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1, 1, 2, 1, 1, 1, 1, 2, 1},
            {1, 2, 2, 2, 2, 2, 2, 1, 1, 2, 2, 2, 2, 1, 1, 2, 2, 2, 2, 1, 1, 2, 2, 2, 2, 2, 2, 1},
            {1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 0, 1, 1, 0, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1},
            {0, 0, 0, 0, 0, 1, 2, 1, 1, 1, 1, 1, 0, 1, 1, 0, 1, 1, 1, 1, 1, 2, 1, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 1, 2, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 2, 1, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 1, 2, 1, 1, 0, 1, 1, 1, 0, 0, 1, 1, 1, 0, 1, 1, 2, 1, 0, 0, 0, 0, 0},
            {1, 1, 1, 1, 1, 1, 2, 1, 1, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 1, 1, 2, 1, 1, 1, 1, 1, 1},
            {0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0},
            {1, 1, 1, 1, 1, 1, 2, 1, 1, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 1, 1, 2, 1, 1, 1, 1, 1, 1},
            {0, 0, 0, 0, 0, 1, 2, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 2, 1, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 1, 2, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 2, 1, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 1, 2, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 2, 1, 0, 0, 0, 0, 0},
            {1, 1, 1, 1, 1, 1, 2, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 2, 1, 1, 1, 1, 1, 1},
            {1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1},
            {1, 2, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 2, 1, 1, 2, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 2, 1},
            {1, 2, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 2, 1, 1, 2, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 2, 1},
            {1, 3, 2, 2, 1, 1, 2, 2, 2, 2, 2, 2, 2, 0, 4, 2, 2, 2, 2, 2, 2, 2, 1, 1, 2, 2, 3, 1},
            {1, 1, 1, 2, 1, 1, 2, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1, 1, 2, 1, 1, 2, 1, 1, 1},
            {1, 1, 1, 2, 1, 1, 2, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1, 1, 2, 1, 1, 2, 1, 1, 1},
            {1, 2, 2, 2, 2, 2, 2, 1, 1, 2, 2, 2, 2, 1, 1, 2, 2, 2, 2, 1, 1, 2, 2, 2, 2, 2, 2, 1},
            {1, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1},
            {1, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1},
            {1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1},
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}
    };
    private Pacman runPac = new Pacman(laberinto);
    private Thread pacman = new Thread(runPac);

    private void dibujarTubo(Graphics g, int x, int y, int pixelX, int pixelY) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(new Color(0, 255, 0)); // CAMBIO: Color verde en lugar de azul o rojo
        g2d.setStroke(new BasicStroke(2));

        // Verificar bloques adyacentes
        boolean arriba = (y > 0) && (laberinto[y - 1][x] == 1);
        boolean abajo = (y < HEIGHT - 1) && (laberinto[y + 1][x] == 1);
        boolean izquierda = (x > 0) && (laberinto[y][x - 1] == 1);
        boolean derecha = (x < WIDTH - 1) && (laberinto[y][x + 1] == 1);

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
                g2d.drawLine(x1 + arcSize / 2, y1, x2 - arcSize / 2, y1); // línea superior
                g2d.drawArc(x2 - arcSize, y1, arcSize, arcSize, 0, 90); // esquina sup-der
            } else if (!izquierda) {
                // Redondeado solo en esquina superior izquierda
                g2d.drawArc(x1, y1, arcSize, arcSize, 90, 90);
                g2d.drawLine(x1 + arcSize / 2, y1, x2, y1);
            } else if (!derecha) {
                // Redondeado solo en esquina superior derecha
                g2d.drawLine(x1, y1, x2 - arcSize / 2, y1);
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
                g2d.drawLine(x1 + arcSize / 2, y2, x2 - arcSize / 2, y2); // línea inferior
                g2d.drawArc(x2 - arcSize, y2 - arcSize, arcSize, arcSize, 270, 90); // esquina inf-der
            } else if (!izquierda) {
                // Redondeado solo en esquina inferior izquierda
                g2d.drawArc(x1, y2 - arcSize, arcSize, arcSize, 180, 90);
                g2d.drawLine(x1 + arcSize / 2, y2, x2, y2);
            } else if (!derecha) {
                // Redondeado solo en esquina inferior derecha
                g2d.drawLine(x1, y2, x2 - arcSize / 2, y2);
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
                g2d.drawLine(x1, y1 + arcSize / 2, x1, y2);
            } else if (!abajo) {
                // Desde arriba hacia esquina redondeada
                g2d.drawLine(x1, y1, x1, y2 - arcSize / 2);
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
                g2d.drawLine(x2, y1 + arcSize / 2, x2, y2);
            } else if (!abajo) {
                // Desde arriba hacia esquina redondeada
                g2d.drawLine(x2, y1, x2, y2 - arcSize / 2);
            } else {
                // Línea completa si hay conexiones arriba y abajo
                g2d.drawLine(x2, y1, x2, y2);
            }
        }

        // Restaurar stroke normal
        g2d.setStroke(new BasicStroke(1));
    }

    ;

    // Método para contar puntos iniciales
    private void contarPuntosIniciales() {
        puntosRestantes = 0;
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                if (laberinto[y][x] == 2) {
                    puntosRestantes++;
                }
            }
        }
    }

    public Mapa3(String jugador) {
        this.jugadorActual = jugador;
        cargarJugadores();

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

        // Contar puntos iniciales en el laberinto
        contarPuntosIniciales();

        // Iniciar el hilo del juego
        gameThread = new Thread(this);
        gameThread.start();
        pacman.start();
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

        // Dibujar menú de pausa si está activo
        if (enPausa) {
            dibujarMenuPausa(g2d);
        }
    }

    private void dibujarMapa(Graphics2D g) {
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                int cellValue = laberinto[y][x];
                int pixelX = x * CELL_SIZE;
                int pixelY = y * CELL_SIZE;

                switch (cellValue) {
                    case 1: // Paredes (bordes verdes con efecto de tubo)
                        dibujarTubo(g, x, y, pixelX, pixelY);
                        break;

                    case 2: // Puntos (amarillo pequeño con parpadeo)
                        g.setColor(Color.YELLOW);
                        int dotSize = CELL_SIZE / 6;
                        int dotX = pixelX + CELL_SIZE / 2 - dotSize / 2;
                        int dotY = pixelY + CELL_SIZE / 2 - dotSize / 2;
                        g.fillOval(dotX, dotY, dotSize, dotSize);
                        break;

                    case 3: // Power Pellets (rosa grande)
                        if (this.mostrarPuntos) {
                            g.setColor(new Color(255, 184, 255));
                            int pelletSize = CELL_SIZE / 2;
                            int pelletX = pixelX + CELL_SIZE / 2 - pelletSize / 2;
                            int pelletY = pixelY + CELL_SIZE / 2 - pelletSize / 2;
                            g.fillOval(pelletX, pelletY, pelletSize, pelletSize);

                            // Efecto de brillo
                            g.setColor(Color.WHITE);
                            g.fillOval(pelletX + pelletSize / 4, pelletY + pelletSize / 4, pelletSize / 4, pelletSize / 4);
                        }
                        break;
                    case 4:
                        ImageIcon imagen = runPac.getImagen();
                        int TamPacman = CELL_SIZE / 2;
                        int pacX = (runPac.getXposcion() * CELL_SIZE) + (CELL_SIZE - TamPacman) / 2;
                        int pacY = (runPac.getYposcion() * CELL_SIZE) + (CELL_SIZE - TamPacman) / 2;

                        if (laberinto[runPac.getYposcion()][runPac.getXposcion()] == 2) {
                            // Sumar puntos y reducir contador
                            incrementarScore(5);
                            puntajeNivel += 5;
                            puntosRestantes--;
                            laberinto[runPac.getYposcion()][runPac.getXposcion()] = 0;

                            // Verificar si se completó el nivel
                            if (puntosRestantes <= 0) {
                                nivelCompletado = true;
                            }
                        } else if (laberinto[runPac.getYposcion()][runPac.getXposcion()] == 3) {
                            // Power pellet - puntos extra
                            incrementarScore(50);
                            laberinto[runPac.getYposcion()][runPac.getXposcion()] = 0;
                        }

                        // Lógica del túnel
                        if (runPac.getXposcion() == 0 && runPac.getYposcion() == 14) {
                            runPac.setXposcion(26);
                        } else if (runPac.getXposcion() == 27 && runPac.getYposcion() == 14) {
                            runPac.setXposcion(1);
                        }

                        if (imagen != null) {
                            imagen.paintIcon(this, g, pacX, pacY);
                        }
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

        // High Score Global
        g.setColor(Color.YELLOW);
        String highScoreText = "HIGH SCORE";
        int textWidth = fm.stringWidth(highScoreText);
        g.drawString(highScoreText, (screenWidth - textWidth) / 2, 30);

        // Puntuación global máxima
        String scoreText = String.format("%02d", highScoreGlobal);
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

        // Mostrar puntos restantes
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 18));
        g.drawString("Puntos restantes: " + puntosRestantes, 50, screenHeight - 100);

        dibujarListaJugadores(g);
        dibujarNivel(g);
    }

    private void dibujarMenuPausa(Graphics2D g) {
        // Difuminar el fondo
        g.setColor(new Color(0, 0, 0, 150)); // Negro semi-transparente
        g.fillRect(0, 0, screenWidth, screenHeight);

        // Configurar fuente para el menú
        Font tituloFont = new Font("Arial", Font.BOLD, 48);
        Font opcionesFont = new Font("Arial", Font.BOLD, 32);

        // Título "PAUSA"
        g.setFont(tituloFont);
        g.setColor(Color.YELLOW);
        FontMetrics titleFm = g.getFontMetrics();
        String pausaText = "PAUSA";
        int titleWidth = titleFm.stringWidth(pausaText);
        int titleX = (screenWidth - titleWidth) / 2;
        int titleY = screenHeight / 2 - 100;
        g.drawString(pausaText, titleX, titleY);

        // Opciones del menú
        g.setFont(opcionesFont);
        FontMetrics optionsFm = g.getFontMetrics();

        for (int i = 0; i < opcionesMenu.length; i++) {
            String opcion = opcionesMenu[i];
            int optionWidth = optionsFm.stringWidth(opcion);
            int currentX = (screenWidth - optionWidth) / 2;
            int menuY = titleY + 80 + (i * 60);

            // Dibujar Pac-Man como cursor para la opción seleccionada
            if (i == opcionSeleccionada) {
                int cursorSize = 20;
                int cursorX = currentX - cursorSize - 15;
                int cursorY = menuY - cursorSize + 5;

                g.setColor(Color.YELLOW);
                g.fillArc(cursorX, cursorY, cursorSize, cursorSize, 45, 270);
            }

            // Configurar color de la opción
            if (i == opcionSeleccionada) {
                // Sombra (color marrón oscuro)
                g.setColor(new Color(139, 69, 19));
                g.drawString(opcion, currentX + 3, menuY + 3);

                // Texto principal (amarillo)
                g.setColor(Color.YELLOW);
                g.drawString(opcion, currentX, menuY);
            } else {
                // Otras opciones (color naranja suave)
                g.setColor(new Color(255, 184, 82));
                g.drawString(opcion, currentX, menuY);
            }
        }
    }

    //metodos Mapa3
    private void manejarMenuPausa(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                opcionSeleccionada = (opcionSeleccionada - 1 + opcionesMenu.length) % opcionesMenu.length;
                break;
            case KeyEvent.VK_DOWN:
                opcionSeleccionada = (opcionSeleccionada + 1) % opcionesMenu.length;
                break;
            case KeyEvent.VK_ENTER:
                ejecutarOpcionMenu();
                break;
            case KeyEvent.VK_ESCAPE:
                enPausa = false; // Salir del menú de pausa
                break;
        }
    }

    private void ejecutarOpcionMenu() {
        switch (opcionSeleccionada) {
            case 0: // Continuar
                enPausa = false;
                break;
            case 1: // Salir
                // Detener el juego
                detener();

                // Cerrar la ventana actual del mapa
                JFrame currentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);

                SwingUtilities.invokeLater(() -> {
                    currentFrame.dispose();

                    // Mostrar la ventana de inicio guardada
                    if (ventanaInicio != null) {
                        ventanaInicio.setVisible(true);
                        ventanaInicio.toFront();
                    }
                });
                break;
        }
    }

    public void setVentanaInicio(JFrame ventanaInicio) {
        this.ventanaInicio = ventanaInicio;
    }

    private void cargarJugadores() {
        listaJugadores = new java.util.ArrayList<>();
        highScoreGlobal = 0;

        try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader("jugadores.txt"))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] partes = linea.split(",");
                if (partes.length == 2) {
                    String nombre = partes[0].trim();
                    try {
                        int puntaje = Integer.parseInt(partes[1].trim());
                        listaJugadores.add(new String[]{nombre, String.valueOf(puntaje)});
                        if (puntaje > highScoreGlobal) {
                            highScoreGlobal = puntaje;
                        }
                    } catch (NumberFormatException e) {
                        // Ignorar líneas con formato incorrecto
                    }
                }
            }
        } catch (java.io.IOException e) {
            System.out.println("Archivo jugadores.txt no encontrado, creando uno nuevo.");
        }
    }

    private void guardarJugador() {
        boolean jugadorExistente = false;

        // Actualizar puntaje si el jugador ya existe
        for (String[] jugador : listaJugadores) {
            if (jugador[0].equals(jugadorActual)) {
                int puntajeActual = Integer.parseInt(jugador[1]);
                if (highScore > puntajeActual) {
                    jugador[1] = String.valueOf(highScore);
                }
                jugadorExistente = true;
                break;
            }
        }

        // Agregar nuevo jugador si no existe
        if (!jugadorExistente) {
            listaJugadores.add(new String[]{jugadorActual, String.valueOf(highScore)});
        }

        // Guardar en archivo
        try (java.io.PrintWriter pw = new java.io.PrintWriter(new java.io.FileWriter("jugadores.txt"))) {
            for (String[] jugador : listaJugadores) {
                pw.println(jugador[0] + ", " + jugador[1]);
            }
        } catch (java.io.IOException e) {
            System.out.println("Error al guardar jugadores: " + e.getMessage());
        }
    }

    private void dibujarListaJugadores(Graphics2D g) {
        Font tituloFont = new Font("Arial", Font.BOLD, 20);
        Font jugadorFont = new Font("Arial", Font.PLAIN, 16);

        // Título "JUGADORES"
        g.setFont(tituloFont);
        g.setColor(Color.YELLOW);
        g.drawString("JUGADORES", screenWidth - 250, 100);

        // Lista de jugadores
        g.setFont(jugadorFont);
        int y = 130;
        int maxJugadores = Math.min(8, listaJugadores.size());

        for (int i = 0; i < maxJugadores; i++) {
            String[] jugador = listaJugadores.get(i);
            String nombre = jugador[0];
            String puntaje = jugador[1];

            // Destacar jugador actual
            if (nombre.equals(jugadorActual)) {
                g.setColor(Color.YELLOW);
            } else {
                g.setColor(Color.WHITE);
            }

            // Limitar longitud del nombre
            if (nombre.length() > 10) {
                nombre = nombre.substring(0, 10) + "...";
            }

            g.drawString(nombre + ": " + puntaje, screenWidth - 250, y);
            y += 25;
        }

        // Mostrar "..." si hay más jugadores
        if (listaJugadores.size() > maxJugadores) {
            g.setColor(Color.GRAY);
            g.drawString("...", screenWidth - 250, y);
        }
    }

    private void dibujarNivel(Graphics2D g) {
        Font nivelFont = new Font("Arial", Font.BOLD, 18);
        g.setFont(nivelFont);
        g.setColor(Color.CYAN);
        g.drawString("NIVEL 3", screenWidth - 250, screenHeight - 100);
    }

    @Override
    public void run() {
        while (running) {
            if (!enPausa) {
                // Actualizar efectos de parpadeo solo si no está en pausa
                long tiempoActual = System.currentTimeMillis();
                long tiempoTranscurrido = tiempoActual - tiempoInicio;
                mostrarPuntos = (tiempoTranscurrido / 500) % 2 == 0;

                // Verificar si el nivel está completado
                if (nivelCompletado) {
                    regresarInicio();
                }
            }

            repaint();

            try {
                Thread.sleep(16);
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


    // Eventos de teclado
    @Override
    public void keyPressed(KeyEvent e) {
        if (enPausa) {
            manejarMenuPausa(e);
            return;
        }
        switch (e.getKeyCode()) {
            case KeyEvent.VK_ESCAPE:
                enPausa = true;
                opcionSeleccionada = 0; // Resetear selección
                break;
            case KeyEvent.VK_SPACE:
                // Pausar/reanudar juego
            default:

                runPac.moverPacman(e); // <<--- Esta línea es clave

                break;
        }
        runPac.setPause(enPausa);
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    public void detener() {
        guardarJugador(); // Guardar puntaje antes de salir
        running = false;

        if (gameThread != null) {
            gameThread.interrupt();
        }
        if (moveTimer != null) {
            moveTimer.stop();
        }
    }

    private void regresarInicio() {
        // Detener el juego actual
        detener();

        // Mostrar transición
        SwingUtilities.invokeLater(() -> {
            JFrame currentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);

            // Crear un panel de transición
            JPanel transicion = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    g.setColor(Color.BLACK);
                    g.fillRect(0, 0, getWidth(), getHeight());

                    g.setColor(Color.YELLOW);
                    g.setFont(new Font("Arial", Font.BOLD, 48));
                    FontMetrics fm = g.getFontMetrics();
                    String texto = "¡JUEGO COMPLETADO!";
                    int x = (getWidth() - fm.stringWidth(texto)) / 2;
                    int y = getHeight() / 2 - 50;
                    g.drawString(texto, x, y);

                    texto = "REGRESANDO AL MENÚ";
                    x = (getWidth() - fm.stringWidth(texto)) / 2;
                    y = getHeight() / 2 + 50;
                    g.drawString(texto, x, y);
                }
            };

            currentFrame.setContentPane(transicion);
            currentFrame.revalidate();
            currentFrame.repaint();

            // Después de 2 segundos, regresar a PantallaInicio
            Timer transicionTimer = new Timer(2000, e -> {
                currentFrame.dispose();

                // Mostrar la ventana de inicio guardada
                if (ventanaInicio != null) {
                    ventanaInicio.setVisible(true);
                    ventanaInicio.toFront();
                }
            });
            transicionTimer.setRepeats(false);
            transicionTimer.start();
        });
    }
}

