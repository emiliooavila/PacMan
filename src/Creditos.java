import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Creditos extends JFrame implements KeyListener {
    private int screenWidth;
    private int screenHeight;
    private List<String> creditosTexto;
    private String[] opciones = {"REGRESAR AL MENÚ"};
    private int opcionSeleccionada = 0;
    private boolean mostrarCursor = true;
    private boolean mostrarMarco = true;
    private Timer cursorTimer;
    private Timer marcoTimer;
    private JFrame ventanaPadre;

    public Creditos(JFrame ventanaPadre) {
        // Configurar pantalla completa
        this.ventanaPadre = ventanaPadre;

        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        // Obtener dimensiones de la pantalla
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        screenWidth = screenSize.width;
        screenHeight = screenSize.height;

        // Configurar el JFrame
        setBackground(Color.BLACK);
        addKeyListener(this);
        setFocusable(true);

        // Cargar el archivo de créditos
        cargarCreditos();

        // Configurar timer para el cursor parpadeante
        cursorTimer = new Timer(500, e -> {
            mostrarCursor = !mostrarCursor;
            repaint();
        });
        cursorTimer.start();

        // Configurar timer para el marco parpadeante
        marcoTimer = new Timer(300, e -> {
            mostrarMarco = !mostrarMarco;
            repaint();
        });
        marcoTimer.start();

        // Crear panel personalizado para dibujar
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;

                // Habilitar antialiasing
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Fondo negro
                g2d.setColor(Color.BLACK);
                g2d.fillRect(0, 0, screenWidth, screenHeight);

                // Dibujar título
                dibujarTitulo(g2d);

                // Dibujar créditos
                dibujarCreditos(g2d);

                // Dibujar menú
                dibujarMenu(g2d);
            }
        };

        panel.setBackground(Color.BLACK);
        add(panel);

        setVisible(true);
    }

    private void cargarCreditos() {
        creditosTexto = new ArrayList<>();
        try {
            InputStream inputStream = getClass().getResourceAsStream("/creditos.txt");
            if (inputStream == null) {
                // Si no encuentra el archivo, mostrar mensaje de error
                creditosTexto.add("Error: No se pudo cargar el archivo creditos.txt");
                creditosTexto.add("Asegúrate de que el archivo esté en la carpeta resources");
                return;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String linea;
            while ((linea = reader.readLine()) != null) {
                creditosTexto.add(linea);
            }
            reader.close();

        } catch (IOException e) {
            creditosTexto.add("Error al leer el archivo: " + e.getMessage());
        }
    }


    private void dibujarTitulo(Graphics2D g) {
        // Título CRÉDITOS
        Font tituloFont = new Font("Arial", Font.BOLD, (int)(screenWidth * 0.06));
        g.setFont(tituloFont);
        FontMetrics fm = g.getFontMetrics();

        String titulo = "CRÉDITOS";
        int tituloWidth = fm.stringWidth(titulo);
        int tituloX = (screenWidth - tituloWidth) / 2;
        int tituloY = screenHeight / 8;

        // Sombra del título
        g.setColor(new Color(139, 69, 19)); // Color marrón oscuro
        g.drawString(titulo, tituloX + 3, tituloY + 3);

        // Título principal
        g.setColor(Color.YELLOW);
        g.drawString(titulo, tituloX, tituloY);
    }

    private void dibujarCreditos(Graphics2D g) {
        if (creditosTexto.isEmpty()) {
            return;
        }

        // Calcular área disponible para créditos
        int areaInicioY = screenHeight / 6;
        int areaFinY = screenHeight - 180; // Dejar espacio para el menú
        int areaAltura = areaFinY - areaInicioY;

        // Calcular tamaño de fuente adaptativo
        int baseFontSize = Math.max(12, screenWidth / 80);

        int currentY = areaInicioY;
        int lineSpacing = baseFontSize / 2;

        for (int i = 0; i < creditosTexto.size(); i++) {
            String linea = creditosTexto.get(i).trim();

            if (linea.isEmpty()) {
                // Línea vacía - agregar espacio extra
                currentY += baseFontSize;
                continue;
            }

            Font font;
            Color color;
            int fontSize;

            // Determinar estilo según el contenido de la línea
            if (linea.contains("DESARROLLO") || linea.contains("EQUIPO") || linea.contains("AGRADECIMIENTOS")) {
                // Títulos de sección
                fontSize = (int)(baseFontSize * 1.4);
                font = new Font("Arial", Font.BOLD, fontSize);
                color = Color.YELLOW;
            } else if (esNombreDesarrollador(linea)) {
                // Nombres de desarrolladores
                fontSize = (int)(baseFontSize * 1.2);
                font = new Font("Arial", Font.BOLD, fontSize);
                color = Color.CYAN;
            } else if (linea.startsWith("©") || linea.contains("Gracias")) {
                // Líneas especiales
                fontSize = baseFontSize;
                font = new Font("Arial", Font.ITALIC, fontSize);
                color = Color.LIGHT_GRAY;
            } else {
                // Descripciones normales
                fontSize = baseFontSize;
                font = new Font("Arial", Font.PLAIN, fontSize);
                color = Color.WHITE;
            }
            g.setFont(font);
            g.setColor(color);

            FontMetrics fm = g.getFontMetrics();

            // Verificar si la línea es muy larga y dividirla si es necesario
            if (fm.stringWidth(linea) > screenWidth * 0.8) {
                String[] palabras = linea.split(" ");
                StringBuilder lineaActual = new StringBuilder();

                for (String palabra : palabras) {
                    String lineaConPalabra = lineaActual.toString() + (lineaActual.length() > 0 ? " " : "") + palabra;
                    if (fm.stringWidth(lineaConPalabra) > screenWidth * 0.8) {
                        if (lineaActual.length() > 0) {
                            // Dibujar la línea actual
                            int lineWidth = fm.stringWidth(lineaActual.toString());
                            int x = (screenWidth - lineWidth) / 2;
                            currentY += fm.getHeight() + lineSpacing;

                            if (currentY < areaFinY) {
                                g.drawString(lineaActual.toString(), x, currentY);
                            }

                            lineaActual = new StringBuilder(palabra);
                        }
                    } else {
                        lineaActual = new StringBuilder(lineaConPalabra);
                    }
                }

                // Dibujar la última línea
                if (lineaActual.length() > 0) {
                    int lineWidth = fm.stringWidth(lineaActual.toString());
                    int x = (screenWidth - lineWidth) / 2;
                    currentY += fm.getHeight() + lineSpacing;

                    if (currentY < areaFinY) {
                        g.drawString(lineaActual.toString(), x, currentY);
                    }
                }
            } else {
                // Línea normal
                int lineWidth = fm.stringWidth(linea);
                int x = (screenWidth - lineWidth) / 2;
                currentY += fm.getHeight() + lineSpacing;

                if (currentY < areaFinY) {
                    g.drawString(linea, x, currentY);
                }
            }

            // Espaciado adicional para diferentes tipos de líneas
            if (linea.contains("DESARROLLO") || linea.contains("EQUIPO") || linea.contains("AGRADECIMIENTOS")) {
                currentY += lineSpacing * 2;
            } else if (esNombreDesarrollador(linea)) {
                currentY += lineSpacing;
            }

            // Si nos quedamos sin espacio, parar de dibujar
            if (currentY >= areaFinY) {
                break;
            }
        }
    }

    private void dibujarMenu(Graphics2D g) {
        Font menuFont = new Font("Arial", Font.BOLD, (int)(screenWidth * 0.025));
        g.setFont(menuFont);
        FontMetrics fm = g.getFontMetrics();

        int menuStartY = screenHeight - 100; // Más arriba para que se vea mejor
        int lineHeight = 60;

        for (int i = 0; i < opciones.length; i++) {
            int y = menuStartY + i * lineHeight;

            // Dibujar cursor (símbolo de Pac-Man)
            if (i == opcionSeleccionada && mostrarCursor) {
                g.setColor(Color.YELLOW);
                int cursorSize = 20;
                int cursorX = screenWidth / 2 - 300;
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
    }

    // Método auxiliar para identificar nombres de desarrolladores
    private boolean esNombreDesarrollador(String linea) {
        String[] nombres = {"Emilio Avila", "Diego Delgado", "Rey Picazo", "Maxy Velazquez"};
        for (String nombre : nombres) {
            if (linea.equals(nombre)) {
                return true;
            }
        }
        return false;
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
            case KeyEvent.VK_SPACE:
                ejecutarOpcion();
                break;

            case KeyEvent.VK_ESCAPE:
                regresarAlMenu();
                break;
        }
        repaint();
    }

    private void ejecutarOpcion() {
        switch (opcionSeleccionada) {
            case 0: // REGRESAR AL MENÚ
                regresarAlMenu();
                break;
        }
    }

    private void regresarAlMenu() {
        // Detener los timers
        if (cursorTimer != null) {
            cursorTimer.stop();
        }
        if (marcoTimer != null) {
            marcoTimer.stop();
        }

        // Mostrar nuevamente la ventana padre y cerrar esta
        SwingUtilities.invokeLater(() -> {
            ventanaPadre.setVisible(true);
            dispose();
        });
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // No se necesita implementación
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // No se necesita implementación
    }
}