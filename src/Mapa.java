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

    //Instancia para regresar a PantallaInicio
    private JFrame ventanaInicio;

    //Variables para la música del juego
    private Audio reproductorJuego;
    private Audio eatSound;
    private Audio deathSound;

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
    enum Direction {UP,DOWN,LEFT,RIGHT,NONE};
    private Direction movment=Direction.RIGHT;

    Direction aux=movment;
    private Timer moveTimer;

    // Variables del sistema de jugadores
    private String jugadorActual;
    private java.util.List<String[]> listaJugadores;
    private int highScoreGlobal = 0;

    // Variables para el sistema de puntos
    private int puntosRestantes;
    private int puntajeNivel = 0;
    private boolean nivelCompletado = false;

    //Fantasmas
    private Clyde clyde;
    private Blinky blinky;
    private Pinky pinky;
    private Inky inky;
    private Pikzy pikzy;

    // Threads de los fantasmas
    private Thread threadClyde;
    private Thread threadBlinky;
    private Thread threadPinky;
    private Thread threadInky;
    private Thread threadPikzy;

    // Variables para el modo Power Pellet
    private boolean modoFantasmasComestibles = false;
    private long tiempoActivacionPowerPellet = 0;
    private final long DURACION_POWER_PELLET = 10000; // 10 segundos en milisegundos

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
            {5,5,5,5,5,1,2,1,1,1,1,1,0,1,1,0,1,1,1,1,1,2,1,5,5,5,5,5},
            {5,5,5,5,5,1,2,1,1,0,0,0,0,0,0,0,0,0,0,1,1,2,1,5,5,5,5,5},
            {5,5,5,5,5,1,2,1,1,0,1,1,1,0,0,1,1,1,0,1,1,2,1,5,5,5,5,5},
            {1,1,1,1,1,1,2,1,1,0,1,0,0,0,0,0,0,1,0,1,1,2,1,1,1,1,1,1},
            {0,0,0,0,0,0,2,0,0,0,1,0,0,0,0,0,0,1,0,0,0,2,0,0,0,0,0,0},
            {1,1,1,1,1,1,2,1,1,0,1,0,0,0,0,0,0,1,0,1,1,2,1,1,1,1,1,1},
            {5,5,5,5,5,1,2,1,1,0,1,1,1,1,1,1,1,1,0,1,1,2,1,5,5,5,5,5},
            {5,5,5,5,5,1,2,1,1,0,0,0,0,0,0,0,0,0,0,1,1,2,1,5,5,5,5,5},
            {5,5,5,5,5,1,2,1,1,0,1,1,1,1,1,1,1,1,0,1,1,2,1,5,5,5,5,5},
            {1,1,1,1,1,1,2,1,1,0,1,1,1,1,1,1,1,1,0,1,1,2,1,1,1,1,1,1},
            {1,2,2,2,2,2,2,2,2,2,2,2,2,1,1,2,2,2,2,2,2,2,2,2,2,2,2,1},
            {1,2,1,1,1,1,2,1,1,1,1,1,2,1,1,2,1,1,1,1,1,2,1,1,1,1,2,1},
            {1,2,1,1,1,1,2,1,1,1,1,1,2,1,1,2,1,1,1,1,1,2,1,1,1,1,2,1},
            {1,3,2,2,1,1,2,2,2,2,2,2,2,0,4,2,2,2,2,2,2,2,1,1,2,2,3,1},
            {1,1,1,2,1,1,2,1,1,2,1,1,1,1,1,1,1,1,2,1,1,2,1,1,2,1,1,1},
            {1,1,1,2,1,1,2,1,1,2,1,1,1,1,1,1,1,1,2,1,1,2,1,1,2,1,1,1},
            {1,2,2,2,2,2,2,1,1,2,2,2,2,1,1,2,2,2,2,1,1,2,2,2,2,2,2,1},
            {1,2,1,1,1,1,1,1,1,1,1,1,2,1,1,2,1,1,1,1,1,1,1,1,1,1,2,1},
            {1,2,1,1,1,1,1,1,1,1,1,1,2,1,1,2,1,1,1,1,1,1,1,1,1,1,2,1},
            {1,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,1},
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}
    };
    private Pacman runPac=new Pacman(laberinto);
    private Thread pacman=new Thread(runPac);

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

    public Mapa(String jugador) {
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

        // Inicializar fantasmas y sus threads
        clyde = new Clyde(runPac, laberinto);
        threadClyde = new Thread(clyde);
        threadClyde.start();

        blinky = new Blinky(runPac, laberinto);
        threadBlinky = new Thread(blinky);
        threadBlinky.start();

        pinky = new Pinky(runPac, laberinto);
        threadPinky = new Thread(pinky);
        threadPinky.start();

        inky = new Inky(runPac, laberinto);
        threadInky = new Thread(inky);
        threadInky.start();

        pikzy = new Pikzy(runPac, laberinto);
        threadPikzy = new Thread(pikzy);
        threadPikzy.start();

        //Inicializar y reproducir música del juego
        reproductorJuego = new Audio();
        reproductorJuego.reproducir("main-theme.wav");
        eatSound=new Audio();
        deathSound=new Audio();

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

        clyde.dibujarClyde(g2d);
        blinky.dibujarBlinky(g2d);
        pinky.dibujarPinky(g2d);
        inky.dibujarInky(g2d);
        pikzy.dibujarPikzy(g2d);

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
                    case 1: // Paredes (bordes azules con efecto de tubo)
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
                        if(this.mostrarPuntos){
                            g.setColor(new Color(255, 184, 255));
                            int pelletSize = CELL_SIZE / 2;
                            int pelletX = pixelX + CELL_SIZE / 2 - pelletSize / 2;
                            int pelletY = pixelY + CELL_SIZE / 2 - pelletSize / 2;
                            g.fillOval(pelletX, pelletY, pelletSize, pelletSize);

                            // Efecto de brillo
                            g.setColor(Color.WHITE);
                            g.fillOval(pelletX + pelletSize/4, pelletY + pelletSize/4, pelletSize/4, pelletSize/4);
                        }
                        break;

                    case 4:
                        ImageIcon imagen = runPac.getImagen();
                        int TamPacman = CELL_SIZE/2;
                        int pacX = (runPac.getXposcion()*CELL_SIZE)+(CELL_SIZE-TamPacman)/2;
                        int pacY = (runPac.getYposcion()*CELL_SIZE)+(CELL_SIZE-TamPacman)/2;

                        if(laberinto[runPac.getYposcion()][runPac.getXposcion()] == 2){
                            // Sumar puntos y reducir contador
                            incrementarScore(5);
                            puntajeNivel += 5;
                            puntosRestantes--;
                            laberinto[runPac.getYposcion()][runPac.getXposcion()] = 0;

                            // Reproducir sonido de comer punto
                            eatSound.reproducirUnaVez("eat-sound.wav");

                            // Verificar si se completó el nivel
                            if (puntosRestantes <= 0) {
                                nivelCompletado = true;
                            }
                        }
                        else if(laberinto[runPac.getYposcion()][runPac.getXposcion()] == 3){
                            // Power pellet - activar modo fantasmas comestibles
                            incrementarScore(50);
                            laberinto[runPac.getYposcion()][runPac.getXposcion()] = 0;

                            // NUEVA FUNCIONALIDAD: Activar pastilla
                            pastillaActivada();
                        }

                        // Lógica del túnel
                        if(runPac.getXposcion() == 0 && runPac.getYposcion() == 14){
                            runPac.setXposcion(26);
                        }
                        else if(runPac.getXposcion() == 27 && runPac.getYposcion() == 14){
                            runPac.setXposcion(1);
                        }

                        if(imagen != null){
                            //imagen.paintIcon(this, g, pacX, pacY);
                            g.drawImage(imagen.getImage(), runPac.getXposcion() * 20, runPac.getYposcion() * 20, 20, 20, null);
                        }
                        break;

                    case 0: // Espacio vacío
                    case 5:
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

    private void reanudarJuego() {
        enPausa = false;
        pausarTodosLosFantasmas(false);
        runPac.setPause(false);
    }

    //metodos Mapa
    private void manejarMenuPausa(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_ESCAPE:
                reanudarJuego();
                break;

            case KeyEvent.VK_UP:
            case KeyEvent.VK_W:
                opcionSeleccionada = (opcionSeleccionada - 1 + opcionesMenu.length) % opcionesMenu.length;
                break;

            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_S:
                opcionSeleccionada = (opcionSeleccionada + 1) % opcionesMenu.length;
                break;

            case KeyEvent.VK_ENTER:
                switch (opcionSeleccionada) {
                    case 0: // Continuar
                        reanudarJuego();
                        break;
                    case 1: // Regresar al Menú
                        regresarAlMenu();
                        break;
                    case 2: // Salir del juego (opcional)
                        System.exit(0);
                        break;
                }
                break;
        }
    }

    private void regresarAlMenu() {
        // Detener todos los hilos
        detenerJuego();

        //Detener la música
        reproductorJuego.detener();
        reproductorJuego=null;

        // Cerrar la ventana actual del juego
        JFrame ventanaActual = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (ventanaActual != null) {
            ventanaActual.dispose();
        }

        // Mostrar la ventana de inicio que ya existía
        SwingUtilities.invokeLater(() -> {
            if (ventanaInicio != null) {
                ventanaInicio.setVisible(true);
                ventanaInicio.toFront(); // Traer al frente
                ventanaInicio.requestFocus(); // Dar foco a la ventana

                // CLAVE: Dar foco específicamente al componente que maneja las teclas
                Component componente = ventanaInicio.getContentPane().getComponent(0);
                if (componente instanceof PantallaInicio) {
                    componente.requestFocusInWindow();
                    ((PantallaInicio) componente).reanudarMusica();
                }
            } else {
                new PantallaInicio().setVisible(true);
            }
        });
    }

    private void detenerJuego() {
        // Detener PacMan
        if (runPac != null) {
            runPac.setPause(true);
            if (runPac.moveTimer != null) {
                runPac.moveTimer.stop();
            }
        }

        // Detener todos los fantasmas
        if (clyde != null) {
            clyde.detener();
        }
        if (blinky != null) {
            blinky.detener();
        }
        if (pinky != null) {
            pinky.detener();
        }
        if (inky != null) {
            inky.detener();
        }
        if (pikzy != null) {
            pikzy.detener();
        }
    }

    private void ejecutarOpcionMenu() {
        switch (opcionSeleccionada) {
            case 0: // Continuar
                enPausa = false;

                // Reanudar todos los fantasmas
                if (clyde != null) clyde.setPausado(false);
                if (blinky != null) blinky.setPausado(false);
                if (pinky != null) pinky.setPausado(false);
                if (inky != null) inky.setPausado(false);
                if (pikzy != null) pikzy.setPausado(false);
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
        g.drawString("NIVEL 1", screenWidth - 250, screenHeight - 100);
    }

    @Override
    public void run() {
        while (running) {
            if (!enPausa) {
                // Actualizar efectos de parpadeo solo si no está en pausa
                long tiempoActual = System.currentTimeMillis();
                long tiempoTranscurrido = tiempoActual - tiempoInicio;
                mostrarPuntos = (tiempoTranscurrido / 500) % 2 == 0;

                // NUEVAS VERIFICACIONES:
                verificarEstadoPowerPellet();
                verificarColisiones();

                // Verificar si el nivel está completado
                if (nivelCompletado) {
                    cambiarANivel2();
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

    // Getter para que los fantasmas puedan consultar el estado
    public boolean isModoFantasmasComestibles() {
        return modoFantasmasComestibles;
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

    public void pastillaActivada() {
        modoFantasmasComestibles = true;
        tiempoActivacionPowerPellet = System.currentTimeMillis();

        // Notificar a todos los fantasmas que ahora son vulnerables
        if (clyde != null) clyde.setVulnerable(true);
        if (blinky != null) blinky.setVulnerable(true);
        if (pinky != null) pinky.setVulnerable(true);
        if (inky != null) inky.setVulnerable(true);
        if (pikzy != null) pikzy.setVulnerable(true);

        System.out.println("¡Power Pellet activado! Los fantasmas son vulnerables por " + (DURACION_POWER_PELLET/1000) + " segundos");
    }

    private void verificarEstadoPowerPellet() {
        if (modoFantasmasComestibles) {
            long tiempoTranscurrido = System.currentTimeMillis() - tiempoActivacionPowerPellet;

            if (tiempoTranscurrido >= DURACION_POWER_PELLET) {
                // Se acabó el efecto del Power Pellet
                modoFantasmasComestibles = false;

                // Notificar a todos los fantasmas que ya no son vulnerables
                if (clyde != null) clyde.setVulnerable(false);
                if (blinky != null) blinky.setVulnerable(false);
                if (pinky != null) pinky.setVulnerable(false);
                if (inky != null) inky.setVulnerable(false);
                if (pikzy != null) pikzy.setVulnerable(false);

                System.out.println("Efecto Power Pellet terminado. Los fantasmas vuelven a ser peligrosos.");
            }
        }
    }

    private void verificarColisiones() {
        int pacX = runPac.getXposcion();
        int pacY = runPac.getYposcion();

        // Verificar colisión con cada fantasma
        verificarColisionFantasma("Clyde", clyde, pacX, pacY);
        verificarColisionFantasma("Blinky", blinky, pacX, pacY);
        verificarColisionFantasma("Pinky", pinky, pacX, pacY);
        verificarColisionFantasma("Inky", inky, pacX, pacY);
        verificarColisionFantasma("Pikzy", pikzy, pacX, pacY);
    }

    private void verificarColisionFantasma(String nombre, Object fantasma, int pacX, int pacY) {
        if (fantasma == null) return;

        int fantasmaX = -1, fantasmaY = -1;

        // Obtener posición del fantasma según su tipo
        if (fantasma instanceof Clyde) {
            Clyde c = (Clyde) fantasma;
            fantasmaX = c.getClydeX();
            fantasmaY = c.getClydeY();
        } else if (fantasma instanceof Blinky) {
            Blinky b = (Blinky) fantasma;
            fantasmaX = b.getBlinkyX(); // Necesitas agregar este método
            fantasmaY = b.getBlinkyY(); // Necesitas agregar este método
        } else if (fantasma instanceof Pinky) {
            Pinky p = (Pinky) fantasma;
            fantasmaX = p.getPinkyX(); // Necesitas agregar este método
            fantasmaY = p.getPinkyY(); // Necesitas agregar este método
        } else if (fantasma instanceof Inky) {
            Inky i = (Inky) fantasma;
            fantasmaX = i.getInkyX(); // Necesitas agregar este método
            fantasmaY = i.getInkyY(); // Necesitas agregar este método
        } else if (fantasma instanceof Pikzy) {
            Pikzy p = (Pikzy) fantasma;
            fantasmaX = p.getPikzyX(); // Necesitas agregar este método
            fantasmaY = p.getPikzyY(); // Necesitas agregar este método
        }

        // Verificar si están en la misma posición
        if (fantasmaX == pacX && fantasmaY == pacY) {
            if (modoFantasmasComestibles) {
                // Pacman se come al fantasma
                System.out.println("¡Pacman se comió a " + nombre + "!");
                incrementarScore(200);

                // Reiniciar fantasma según su tipo
                if (fantasma instanceof Clyde) {
                    ((Clyde) fantasma).reiniciarEnCentro();
                } else if (fantasma instanceof Blinky) {
                    ((Blinky) fantasma).reiniciarEnCentro();
                } else if (fantasma instanceof Pinky) {
                    ((Pinky) fantasma).reiniciarEnCentro();
                } else if (fantasma instanceof Inky) {
                    ((Inky) fantasma).reiniciarEnCentro();
                } else if (fantasma instanceof Pikzy) {
                    ((Pikzy) fantasma).reiniciarEnCentro();
                }
            } else {
                // El fantasma mata a Pacman - AQUÍ SE PIERDE LA VIDA
                System.out.println("¡" + nombre + " atrapó a Pacman!");
                deathSound.reproducirUnaVez("death-sound.wav");
                perderVida(); // Ya tienes este método implementado

                if (getVidas() > 0) {
                    reiniciarPosiciones();
                } else {
                    // Game Over
                    System.out.println("Game Over!");
                    deathSound.reproducirUnaVez("finaldeath-sound.wav");
                    mostrarPantallaGameOver();
                }
            }
        }
    }

    private void mostrarPantallaGameOver() {
        // Detener el juego actual
        detener();

        // Mostrar transición de Game Over
        SwingUtilities.invokeLater(() -> {
            JFrame currentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);

            // Crear un panel de transición para Game Over
            JPanel gameOverPanel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    g.setColor(Color.BLACK);
                    g.fillRect(0, 0, getWidth(), getHeight());

                    g.setColor(Color.RED);
                    g.setFont(new Font("Arial", Font.BOLD, 48));
                    FontMetrics fm = g.getFontMetrics();
                    String texto = "¡PERDISTE TODAS LAS VIDAS!";
                    int x = (getWidth() - fm.stringWidth(texto)) / 2;
                    int y = getHeight() / 2 - 50;
                    g.drawString(texto, x, y);

                    g.setColor(Color.YELLOW);
                    texto = "GAME OVER";
                    x = (getWidth() - fm.stringWidth(texto)) / 2;
                    y = getHeight() / 2 + 50;
                    g.drawString(texto, x, y);
                }
            };

            currentFrame.setContentPane(gameOverPanel);
            currentFrame.revalidate();
            currentFrame.repaint();

            // Después de 3 segundos, regresar al menú principal
            Timer gameOverTimer = new Timer(3000, e -> {
                currentFrame.dispose();

                // Regresar a PantallaInicio
                reproductorJuego.detener();
                reproductorJuego.reproducir("lobby-theme.wav");
                SwingUtilities.invokeLater(() -> {
                    if (ventanaInicio != null) {
                        ventanaInicio.setVisible(true);
                        ventanaInicio.toFront();
                        ventanaInicio.requestFocus();

                        Component componente = ventanaInicio.getContentPane().getComponent(0);
                        if (componente != null) {
                            componente.requestFocusInWindow();
                        }
                    } else {
                        // Si no hay referencia, crear nueva pantalla de inicio
                        new PantallaInicio().setVisible(true);
                    }
                });
            });
            gameOverTimer.setRepeats(false);
            gameOverTimer.start();
        });
    }

    private void reiniciarPosiciones() {
        // Reiniciar Pacman a su posición inicial
        runPac.reiniciarPosicion();

        // Reiniciar fantasmas al centro
        if (clyde != null) clyde.reiniciarEnCentro();
        if (blinky != null) blinky.reiniciarEnCentro();
        if (pinky != null) pinky.reiniciarEnCentro();
        if (inky != null) inky.reiniciarEnCentro();
        if (pikzy != null) pikzy.reiniciarEnCentro();
    }



    private void pausarTodosLosFantasmas(boolean pausado) {
        if (clyde != null) clyde.setPausado(pausado);
        if (blinky != null) blinky.setPausado(pausado);
        if (pinky != null) pinky.setPausado(pausado);
        if (inky != null) inky.setPausado(pausado);
        if (pikzy != null) pikzy.setPausado(pausado);
    }


    // Eventos de teclado
    @Override
    public void keyPressed(KeyEvent e) {
        if (enPausa) {
            // Manejar menú de pausa
            manejarMenuPausa(e);
            return;
        }

        switch (e.getKeyCode()) {
            case KeyEvent.VK_ESCAPE:
                // Entrar en pausa
                enPausa = true;
                opcionSeleccionada = 0;
                pausarTodosLosFantasmas(true);
                runPac.setPause(true);
                break;

            case KeyEvent.VK_SPACE:
                // Pausa rápida sin menú
                enPausa = !enPausa;
                pausarTodosLosFantasmas(enPausa);
                runPac.setPause(enPausa);
                break;

            default:
                runPac.moverPacman(e);
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}

    public void detener() {
        guardarJugador();
        running = false;

        // Detener todos los fantasmas
        if (clyde != null) {
            clyde.detener();
        }
        if (threadClyde != null) {
            threadClyde.interrupt();
        }

        if (blinky != null) {
            blinky.detener();
        }
        if (threadBlinky != null) {
            threadBlinky.interrupt();
        }

        if (pinky != null) {
            pinky.detener();
        }
        if (threadPinky != null) {
            threadPinky.interrupt();
        }

        if (inky != null) {
            inky.detener();
        }
        if (threadInky != null) {
            threadInky.interrupt();
        }

        if (pikzy != null) {
            pikzy.detener();
        }
        if (threadPikzy != null) {
            threadPikzy.interrupt();
        }

        if (gameThread != null) {
            gameThread.interrupt();
        }
        if (moveTimer != null) {
            moveTimer.stop();
        }
    }

    private void cambiarANivel2() {
        // Detener el juego actual
        detener();

        // Detener la música
        reproductorJuego.detener();

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
                    String texto = "¡NIVEL COMPLETADO!";
                    int x = (getWidth() - fm.stringWidth(texto)) / 2;
                    int y = getHeight() / 2 - 50;
                    g.drawString(texto, x, y);

                    texto = "NIVEL 2";
                    x = (getWidth() - fm.stringWidth(texto)) / 2;
                    y = getHeight() / 2 + 50;
                    g.drawString(texto, x, y);
                }
            };

            currentFrame.setContentPane(transicion);
            currentFrame.revalidate();
            currentFrame.repaint();

            // Después de 2 segundos, cambiar al nivel 2
            Timer transicionTimer = new Timer(2000, e -> {
                currentFrame.dispose();

                // Crear nueva ventana con Mapa2
                JFrame frameNivel2 = new JFrame("Pac-Man - Nivel 2");
                Mapa2 mapa2 = new Mapa2(jugadorActual);
                mapa2.setHighScore(highScore); // Mantener puntaje
                mapa2.setVentanaInicio(ventanaInicio);

                frameNivel2.add(mapa2);
                frameNivel2.setUndecorated(true);
                frameNivel2.setExtendedState(JFrame.MAXIMIZED_BOTH);
                frameNivel2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frameNivel2.setVisible(true);

                mapa2.requestFocus();
            });
            transicionTimer.setRepeats(false);
            transicionTimer.start();
        });
    }
}