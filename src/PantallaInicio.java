import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.RoundRectangle2D;

public class PantallaInicio extends JPanel implements KeyListener {

    // Constantes del mapa
    public static final int CELL_SIZE = 20;
    public static final int WIDTH = 28;
    public static final int HEIGHT = 31;
    public boolean mostrarMatriz = false;

    // Variables de pantalla
    private int screenWidth;
    private int screenHeight;
    private double scaleX, scaleY;

    // Variables del menú
    private int opcionSeleccionada = 0;
    private String[] opciones = {"INICIAR JUEGO", "JUGADORES", "CRÉDITOS", "SALIR"};

    // Variables para animación de fantasmas mejorada
    private float[] fantasmasPosicionX = new float[4];
    private float[] fantasmasVelocidad = {0.8f, -0.6f, 0.7f, -0.9f}; // Velocidades diferentes
    private long tiempoInicio;
    private boolean mostrarCursor = true;

    //Variable para la música
    private Audio reproductor;

    // Matriz simplificada que solo rodea el área de texto
    private int[][] laberintoMarco = {
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

    public PantallaInicio() {
        // Validar dimensiones de la matriz
        System.out.println("Dimensiones de la matriz: " + laberintoMarco.length + " filas x " + laberintoMarco[0].length + " columnas");
        System.out.println("Constantes: HEIGHT=" + HEIGHT + ", WIDTH=" + WIDTH);

        // Configurar la pantalla completa
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        screenWidth = gd.getDisplayMode().getWidth();
        screenHeight = gd.getDisplayMode().getHeight();

        // Calcular escala para ajustar el mapa a la pantalla
        int mapPixelWidth = WIDTH * CELL_SIZE;
        int mapPixelHeight = HEIGHT * CELL_SIZE;

        scaleX = (double) screenWidth / mapPixelWidth;
        scaleY = (double) screenHeight / mapPixelHeight;

        // Usar la escala menor para mantener proporciones
        double scale = Math.min(scaleX, scaleY);
        scaleX = scaleY = scale;

        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);

        // Inicializar tiempo para efectos
        tiempoInicio = System.currentTimeMillis();

        // Inicializar posiciones de fantasmas
        inicializarFantasmas();

        // Iniciar animación
        Timer timer = new Timer(16, e -> {
            // Actualizar efectos de parpadeo
            long tiempoActual = System.currentTimeMillis();
            long tiempoTranscurrido = tiempoActual - tiempoInicio;

            // Cambiar estado de parpadeo cada 500ms
            mostrarCursor = (tiempoTranscurrido / 500) % 2 == 0;

            // Cambiar estado de parpadeo de la matriz cada 800ms para un efecto más lento
            mostrarMatriz = (tiempoTranscurrido / 800) % 2 == 0;

            // Actualizar posiciones de fantasmas
            actualizarFantasmas();

            repaint();
        });
        timer.start();

        //Inicializar Audio/Música
        reproductor = new Audio();
        reproductor.reproducir("resources/lobby-theme.wav");
    }

    private void inicializarFantasmas() {
        // Posiciones iniciales aleatorias para cada fantasma
        for (int i = 0; i < 4; i++) {
            fantasmasPosicionX[i] = (float) (Math.random() * screenWidth);
        }
    }

    private void actualizarFantasmas() {
        for (int i = 0; i < 4; i++) {
            // Actualizar posición X
            fantasmasPosicionX[i] += fantasmasVelocidad[i];

            // Verificar límites y rebotar suavemente
            if (fantasmasPosicionX[i] <= 0) {
                fantasmasPosicionX[i] = 0;
                fantasmasVelocidad[i] = Math.abs(fantasmasVelocidad[i]); // Cambiar a positivo
            } else if (fantasmasPosicionX[i] >= screenWidth - 80) {
                fantasmasPosicionX[i] = screenWidth - 80;
                fantasmasVelocidad[i] = -Math.abs(fantasmasVelocidad[i]); // Cambiar a negativo
            }
        }
    }

    // Usar la misma lógica de dibujo de tubos de la clase Mapa
    private void dibujarTubo(Graphics g, int x, int y, int pixelX, int pixelY) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(new Color(0, 0, 255));
        g2d.setStroke(new BasicStroke(2));

        // Verificar bloques adyacentes con las dimensiones reales de la matriz
        boolean arriba = (y > 0) && (laberintoMarco[y-1][x] == 1);
        boolean abajo = (y < laberintoMarco.length-1) && (laberintoMarco[y+1][x] == 1);
        boolean izquierda = (x > 0) && (laberintoMarco[y][x-1] == 1);
        boolean derecha = (x < laberintoMarco[y].length-1) && (laberintoMarco[y][x+1] == 1);

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
                g2d.drawArc(x1, y1, arcSize, arcSize, 90, 90);
                g2d.drawLine(x1 + arcSize/2, y1, x2 - arcSize/2, y1);
                g2d.drawArc(x2 - arcSize, y1, arcSize, arcSize, 0, 90);
            } else if (!izquierda) {
                g2d.drawArc(x1, y1, arcSize, arcSize, 90, 90);
                g2d.drawLine(x1 + arcSize/2, y1, x2, y1);
            } else if (!derecha) {
                g2d.drawLine(x1, y1, x2 - arcSize/2, y1);
                g2d.drawArc(x2 - arcSize, y1, arcSize, arcSize, 0, 90);
            } else {
                g2d.drawLine(x1, y1, x2, y1);
            }
        }

        // Borde inferior
        if (!abajo) {
            if (!izquierda && !derecha) {
                g2d.drawArc(x1, y2 - arcSize, arcSize, arcSize, 180, 90);
                g2d.drawLine(x1 + arcSize/2, y2, x2 - arcSize/2, y2);
                g2d.drawArc(x2 - arcSize, y2 - arcSize, arcSize, arcSize, 270, 90);
            } else if (!izquierda) {
                g2d.drawArc(x1, y2 - arcSize, arcSize, arcSize, 180, 90);
                g2d.drawLine(x1 + arcSize/2, y2, x2, y2);
            } else if (!derecha) {
                g2d.drawLine(x1, y2, x2 - arcSize/2, y2);
                g2d.drawArc(x2 - arcSize, y2 - arcSize, arcSize, arcSize, 270, 90);
            } else {
                g2d.drawLine(x1, y2, x2, y2);
            }
        }

        // Borde izquierdo
        if (!izquierda) {
            if (!arriba && !abajo) {
                // Ya dibujado en las esquinas
            } else if (!arriba) {
                g2d.drawLine(x1, y1 + arcSize/2, x1, y2);
            } else if (!abajo) {
                g2d.drawLine(x1, y1, x1, y2 - arcSize/2);
            } else {
                g2d.drawLine(x1, y1, x1, y2);
            }
        }

        // Borde derecho
        if (!derecha) {
            if (!arriba && !abajo) {
                // Ya dibujado en las esquinas
            } else if (!arriba) {
                g2d.drawLine(x2, y1 + arcSize/2, x2, y2);
            } else if (!abajo) {
                g2d.drawLine(x2, y1, x2, y2 - arcSize/2);
            } else {
                g2d.drawLine(x2, y1, x2, y2);
            }
        }

        // Restaurar stroke normal
        g2d.setStroke(new BasicStroke(1));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Habilitar antialiasing
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Dibujar el fondo del laberinto (solo el marco)
        dibujarMarcoLaberinto(g2d);

        // Dibujar el título PAC-MAN
        dibujarTitulo(g2d);

        // Dibujar el menú
        dibujarMenu(g2d);

        // Dibujar elementos decorativos (fantasmas pequeños)
        dibujarFantasmasDecorativos(g2d);
    }

    private void dibujarMarcoLaberinto(Graphics2D g) {
        // Solo dibujar si la matriz debe estar visible (parpadeo)
        if (!mostrarMatriz) {
            return;
        }

        // Centrar el dibujo del laberinto
        int offsetX = (int) ((screenWidth - WIDTH * CELL_SIZE * scaleX) / 2);
        int offsetY = (int) ((screenHeight - HEIGHT * CELL_SIZE * scaleY) / 2);

        g.translate(offsetX, offsetY);
        g.scale(scaleX, scaleY);

        // Dibujar solo el marco (bordes de la matriz)
        for (int y = 0; y < laberintoMarco.length; y++) {
            for (int x = 0; x < laberintoMarco[0].length; x++) {
                int cellValue = laberintoMarco[y][x];
                int pixelX = x * CELL_SIZE;
                int pixelY = y * CELL_SIZE;

                if (cellValue == 1) {
                    dibujarTubo(g, x, y, pixelX, pixelY);
                }
            }
        }

        // Resetear transformación
        g.setTransform(new java.awt.geom.AffineTransform());
    }

    private void dibujarTitulo(Graphics2D g) {
        // Título PAC-MAN
        Font tituloFont = new Font("Arial", Font.BOLD, (int)(screenWidth * 0.08));
        g.setFont(tituloFont);
        FontMetrics fm = g.getFontMetrics();

        String titulo = "PAC-MAN";
        int tituloWidth = fm.stringWidth(titulo);
        int tituloX = (screenWidth - tituloWidth) / 2;
        int tituloY = screenHeight / 4;

        // Sombra del título
        g.setColor(new Color(139, 69, 19)); // Color marrón oscuro
        g.drawString(titulo, tituloX + 3, tituloY + 3);

        // Título principal
        g.setColor(Color.YELLOW);
        g.drawString(titulo, tituloX, tituloY);

        // Subtítulo con nombres
        Font subtituloFont = new Font("Arial", Font.BOLD, (int)(screenWidth * 0.010));
        g.setFont(subtituloFont);
        fm = g.getFontMetrics();

        String subtitulo = "EMILIO AVILA • DIEGO DELGADO • REY PICAZO • MAXY VELAZQUEZ";
        int subtituloWidth = fm.stringWidth(subtitulo);
        int subtituloX = (screenWidth - subtituloWidth) / 2;
        int subtituloY = tituloY + 60;

        g.setColor(Color.WHITE);
        g.drawString(subtitulo, subtituloX, subtituloY);
    }

    private void dibujarMenu(Graphics2D g) {
        Font menuFont = new Font("Arial", Font.BOLD, (int)(screenWidth * 0.025));
        g.setFont(menuFont);
        FontMetrics fm = g.getFontMetrics();

        int menuStartY = screenHeight / 2 + 50;
        int lineHeight = 60;

        for (int i = 0; i < opciones.length; i++) {
            int y = menuStartY + i * lineHeight;

            // Dibujar cursor (símbolo de Pac-Man)
            if (i == opcionSeleccionada && mostrarCursor) {
                g.setColor(Color.YELLOW);
                int cursorSize = 20;
                int cursorX = screenWidth / 2 - 200;
                int cursorY = y - cursorSize + 5;

                // Dibujar Pac-Man como cursor
                g.fillArc(cursorX, cursorY, cursorSize, cursorSize, 45, 270);
            }

            // Configurar color de la opción
            if (i == opcionSeleccionada) {
                String opcion = opciones[i];
                int textWidth = fm.stringWidth(opcion);
                int textX = (screenWidth - textWidth) / 2;

                // Sombra (color marrón oscuro)
                g.setColor(new Color(139, 69, 19));
                g.drawString(opcion, textX + 3, y + 3);

                // Texto principal (amarillo)
                g.setColor(Color.YELLOW);
                g.drawString(opcion, textX, y);
            } else {
                // Otras opciones (color naranja suave)
                g.setColor(new Color(255, 184, 82));
                String opcion = opciones[i];
                int textWidth = fm.stringWidth(opcion);
                int textX = (screenWidth - textWidth) / 2;

                g.drawString(opcion, textX, y);
            }

        }

        // Instrucciones de control
        Font instruccionFont = new Font("Arial", Font.PLAIN, (int)(screenWidth * 0.012));
        g.setFont(instruccionFont);
        g.setColor(Color.GRAY);

        String instruccion = "USA ↑↓ PARA NAVEGAR, ENTER PARA SELECCIONAR, ESC PARA SALIR";
        FontMetrics fmInst = g.getFontMetrics();
        int instWidth = fmInst.stringWidth(instruccion);
        int instX = (screenWidth - instWidth) / 2;
        int instY = screenHeight - 50;

        g.drawString(instruccion, instX, instY);
    }

    private void dibujarFantasmasDecorativos(Graphics2D g) {
        // Dibujar pequeños fantasmas con animación suave
        int fantasmaSize = 30;
        Color[] coloresFantasmas = {
                new Color(255, 0, 0),   // Rojo
                new Color(255, 184, 255), // Rosa
                new Color(0, 255, 255),   // Cian
                new Color(255, 184, 82)   // Naranja
        };

        // Alturas fijas para cada fantasma
        int[] alturasY = {
                100,                    // Superior
                screenHeight / 3,       // Medio-alto
                2 * screenHeight / 3,   // Medio-bajo
                screenHeight - 130      // Inferior
        };

        for (int i = 0; i < 4; i++) {
            g.setColor(coloresFantasmas[i]);

            // Usar las posiciones X calculadas dinámicamente
            int x = (int) fantasmasPosicionX[i];
            int y = alturasY[i] + (int) (Math.sin((System.currentTimeMillis() + i * 1000) / 800.0) * 5); // Movimiento vertical suave

            // Cuerpo del fantasma (semicírculo + rectángulo)
            g.fillArc(x, y, fantasmaSize, fantasmaSize, 0, 180);
            g.fillRect(x, y + fantasmaSize/2, fantasmaSize, fantasmaSize/2);

            // Ondas inferiores del fantasma
            int waveWidth = fantasmaSize / 6;
            for (int j = 0; j < 6; j++) {
                int waveX = x + j * waveWidth;
                int waveY = y + fantasmaSize;
                int[] xPoints = {waveX, waveX + waveWidth/2, waveX + waveWidth};
                int[] yPoints = {waveY, waveY - waveWidth/2, waveY};
                g.fillPolygon(xPoints, yPoints, 3);
            }

            // Ojos
            g.setColor(Color.WHITE);
            g.fillOval(x + 6, y + 8, 6, 8);
            g.fillOval(x + 18, y + 8, 6, 8);

            g.setColor(Color.BLACK);
            g.fillOval(x + 8, y + 10, 2, 4);
            g.fillOval(x + 20, y + 10, 2, 4);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                opcionSeleccionada = (opcionSeleccionada - 1 + opciones.length) % opciones.length;
                break;

            case KeyEvent.VK_DOWN:
                opcionSeleccionada = (opcionSeleccionada + 1) % opciones.length;
                break;

            case KeyEvent.VK_ENTER:
                manejarSeleccion();
                break;

            case KeyEvent.VK_ESCAPE:
                System.exit(0);
                break;
        }
        repaint();
    }

    private void manejarSeleccion() {
        switch (opcionSeleccionada) {
            case 0: // INICIAR JUEGO
                iniciarJuego();
                break;

            case 1: // JUGADORES
                System.out.println("Seleccionado: JUGADORES");
                // Aquí iría la lógica para cambiar a la pantalla de selección de jugadores
                break;

            case 2: // CREDITOS
                System.out.println("Seleccionado: CREDITOS");
                // Aquí iría la lógica para cambiar a la pantalla de créditos
                break;

            case 3: // SALIR
                System.exit(0);
                break;
        }
    }

    private void iniciarJuego() {
        // Crear nueva ventana para el juego
        SwingUtilities.invokeLater(() -> {
            JFrame gameFrame = new JFrame("Pac-Man");
            // Asumiendo que tienes una clase Mapa
             Mapa mapa = new Mapa();

            gameFrame.add(mapa);
            gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            gameFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            gameFrame.setUndecorated(true);
            gameFrame.setVisible(true);

            mapa.requestFocusInWindow();

            // Cerrar la ventana actual de inicio
            SwingUtilities.getWindowAncestor(this).dispose();
        });
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}

    // Método main para probar la pantalla de inicio
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Pac-Man - Pantalla de Inicio");
            PantallaInicio pantallaInicio = new PantallaInicio();

            frame.add(pantallaInicio);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            frame.setUndecorated(true); // Pantalla completa sin bordes
            frame.setVisible(true);

            pantallaInicio.requestFocusInWindow();
        });
    }
}