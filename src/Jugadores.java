import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Jugadores extends JFrame implements KeyListener {
    private int screenWidth;
    private int screenHeight;
    private Map<String, Integer> jugadoresPuntajes;
    private List<String> nombresJugadores;
    private String[] opciones = {"NUEVA PARTIDA", "REINICIAR PUNTAJES", "REGRESAR AL MENÚ"};
    private int opcionSeleccionada = 0;
    private boolean mostrarCursor = true;
    private boolean mostrarMarco = true;
    private Timer cursorTimer;
    private Timer marcoTimer;
    private JFrame ventanaPadre;
    private String jugadorSeleccionado = "";
    private int jugadorActualIndex = 0;

    public Jugadores(JFrame ventanaPadre) {
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

        // Inicializar estructuras de datos
        jugadoresPuntajes = new HashMap<>();
        nombresJugadores = new ArrayList<>();

        // Cargar jugadores y puntajes
        cargarJugadores();

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

                // Dibujar tabla de jugadores
                dibujarTablaJugadores(g2d);

                // Dibujar menú
                dibujarMenu(g2d);
            }
        };

        panel.setBackground(Color.BLACK);
        add(panel);

        setVisible(true);
    }

    private void cargarJugadores() {
        try {
            // Intentar cargar desde múltiples ubicaciones
            String[] rutas = {
                    "/jugadores.txt",           // Desde resources (classpath)
                    "jugadores.txt",            // Directorio actual
                    "src/main/resources/jugadores.txt",
                    "src/resources/jugadores.txt",
                    "resources/jugadores.txt"
            };

            boolean archivoEncontrado = false;

            // Primero intentar desde el classpath (resources)
            InputStream inputStream = getClass().getResourceAsStream("/jugadores.txt");
            if (inputStream != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String linea;
                while ((linea = reader.readLine()) != null) {
                    if (!linea.trim().isEmpty()) {
                        String[] partes = linea.split(",");
                        if (partes.length == 2) {
                            String nombre = partes[0].trim();
                            int puntaje = Integer.parseInt(partes[1].trim());
                            jugadoresPuntajes.put(nombre, puntaje);
                            nombresJugadores.add(nombre);
                        }
                    }
                }
                reader.close();
                archivoEncontrado = true;
                System.out.println("Archivo cargado desde resources");
            }

            // Si no se encontró en resources, intentar desde archivos locales
            if (!archivoEncontrado) {
                for (int i = 1; i < rutas.length; i++) { // Empezar desde índice 1 para saltar el classpath
                    try {
                        File archivo = new File(rutas[i]);
                        if (archivo.exists()) {
                            BufferedReader reader = new BufferedReader(new FileReader(archivo));
                            String linea;
                            while ((linea = reader.readLine()) != null) {
                                if (!linea.trim().isEmpty()) {
                                    String[] partes = linea.split(",");
                                    if (partes.length == 2) {
                                        String nombre = partes[0].trim();
                                        int puntaje = Integer.parseInt(partes[1].trim());
                                        jugadoresPuntajes.put(nombre, puntaje);
                                        nombresJugadores.add(nombre);
                                    }
                                }
                            }
                            reader.close();
                            archivoEncontrado = true;
                            System.out.println("Archivo cargado desde: " + archivo.getAbsolutePath());
                            break;
                        }
                    } catch (IOException e) {
                        continue; // Probar siguiente ruta
                    }
                }
            }

            // Si no se encontró ningún archivo, crear jugadores por defecto
            if (!archivoEncontrado) {
                crearJugadoresPorDefecto();
                guardarJugadores();
                System.out.println("Creado archivo con jugadores por defecto");
            }

        } catch (IOException | NumberFormatException e) {
            // Si hay error, crear jugadores por defecto
            crearJugadoresPorDefecto();
            guardarJugadores();
            System.err.println("Error al cargar jugadores, usando valores por defecto: " + e.getMessage());
        }

        // Si no se cargaron jugadores, crear los por defecto
        if (jugadoresPuntajes.isEmpty()) {
            crearJugadoresPorDefecto();
            guardarJugadores();
        }
    }

    private void crearJugadoresPorDefecto() {
        String[] nombres = {"Emilio", "Diego", "Rey", "Maxy"};
        jugadoresPuntajes.clear();
        nombresJugadores.clear();

        for (String nombre : nombres) {
            jugadoresPuntajes.put(nombre, 0);
            nombresJugadores.add(nombre);
        }
    }

    private void guardarJugadores() {
        try {
            // Intentar guardar en múltiples ubicaciones para asegurar la persistencia
            String[] rutas = {
                    "src/main/resources/jugadores.txt",
                    "src/resources/jugadores.txt",
                    "jugadores.txt",
                    "resources/jugadores.txt"
            };

            boolean guardadoExitoso = false;

            for (String ruta : rutas) {
                try {
                    File archivo = new File(ruta);

                    // Crear directorios padre si no existen
                    if (archivo.getParentFile() != null) {
                        archivo.getParentFile().mkdirs();
                    }

                    FileWriter writer = new FileWriter(archivo);
                    for (String nombre : nombresJugadores) {
                        writer.write(nombre + "," + jugadoresPuntajes.get(nombre) + "\n");
                    }
                    writer.close();

                    guardadoExitoso = true;
                    System.out.println("Archivo guardado exitosamente en: " + archivo.getAbsolutePath());

                } catch (IOException e) {
                    // Continuar con la siguiente ruta si esta falla
                    continue;
                }
            }

            if (!guardadoExitoso) {
                System.err.println("No se pudo guardar el archivo en ninguna ubicación");
            }

        } catch (Exception e) {
            System.err.println("Error general al guardar jugadores: " + e.getMessage());
        }
    }

    private void dibujarTitulo(Graphics2D g) {
        // Título JUGADORES
        Font tituloFont = new Font("Arial", Font.BOLD, (int)(screenWidth * 0.06));
        g.setFont(tituloFont);
        FontMetrics fm = g.getFontMetrics();

        String titulo = "JUGADORES";
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

    private void dibujarTablaJugadores(Graphics2D g) {
        // Calcular área para la tabla
        int tablaInicioY = screenHeight / 5;
        int baseFontSize = Math.max(16, screenWidth / 60);

        // Título de la tabla
        Font subtituloFont = new Font("Arial", Font.BOLD, (int)(baseFontSize * 1.3));
        g.setFont(subtituloFont);
        FontMetrics fmSubtitulo = g.getFontMetrics();

        String subtitulo = "PUNTUACIONES";
        int subtituloWidth = fmSubtitulo.stringWidth(subtitulo);
        int subtituloX = (screenWidth - subtituloWidth) / 2;

        // Sombra del subtítulo
        g.setColor(new Color(139, 69, 19));
        g.drawString(subtitulo, subtituloX + 2, tablaInicioY + 2);

        // Subtítulo principal
        g.setColor(Color.CYAN);
        g.drawString(subtitulo, subtituloX, tablaInicioY);

        // Dibujar marco de la tabla si debe mostrarse
        int marcoX = screenWidth / 4;
        int marcoY = tablaInicioY + 40;
        int marcoWidth = screenWidth / 2;
        int marcoHeight = nombresJugadores.size() * 60 + 60;

        if (mostrarMarco) {
            g.setColor(Color.YELLOW);
            g.setStroke(new BasicStroke(3));
            g.drawRect(marcoX, marcoY, marcoWidth, marcoHeight);
        }

        // Encabezados de la tabla
        Font headerFont = new Font("Arial", Font.BOLD, baseFontSize);
        g.setFont(headerFont);
        g.setColor(Color.YELLOW);

        int headerY = marcoY + 35;
        int nombreX = marcoX + 50;
        int puntajeX = marcoX + marcoWidth - 150;

        g.drawString("JUGADOR", nombreX, headerY);
        g.drawString("PUNTAJE", puntajeX, headerY);

        // Línea separadora
        g.setColor(Color.WHITE);
        g.setStroke(new BasicStroke(1));
        g.drawLine(marcoX + 20, headerY + 10, marcoX + marcoWidth - 20, headerY + 10);

        // Datos de los jugadores
        Font dataFont = new Font("Arial", Font.PLAIN, baseFontSize);
        g.setFont(dataFont);

        int currentY = headerY + 45;
        for (int i = 0; i < nombresJugadores.size(); i++) {
            String nombre = nombresJugadores.get(i);
            int puntaje = jugadoresPuntajes.get(nombre);

            // Resaltar jugador seleccionado
            if (i == jugadorActualIndex) {
                // Fondo para el jugador seleccionado
                g.setColor(new Color(255, 255, 0, 50)); // Amarillo transparente
                g.fillRect(marcoX + 10, currentY - 25, marcoWidth - 20, 35);

                // Texto del jugador seleccionado
                g.setColor(Color.YELLOW);
                jugadorSeleccionado = nombre;
            } else {
                g.setColor(Color.WHITE);
            }

            // Dibujar nombre del jugador
            g.drawString(nombre, nombreX, currentY);

            // Dibujar puntaje
            g.drawString(String.valueOf(puntaje), puntajeX, currentY);

            currentY += 50;
        }

        // Mostrar jugador seleccionado actual
        if (!jugadorSeleccionado.isEmpty()) {
            Font selectedFont = new Font("Arial", Font.BOLD, (int)(baseFontSize * 1.1));
            g.setFont(selectedFont);
            FontMetrics fmSelected = g.getFontMetrics();

            String textoSeleccionado = "JUGADOR SELECCIONADO: " + jugadorSeleccionado;
            int selectedWidth = fmSelected.stringWidth(textoSeleccionado);
            int selectedX = (screenWidth - selectedWidth) / 2;
            int selectedY = marcoY + marcoHeight + 50;

            // Sombra
            g.setColor(new Color(139, 69, 19));
            g.drawString(textoSeleccionado, selectedX + 2, selectedY + 2);

            // Texto principal
            g.setColor(Color.CYAN);
            g.drawString(textoSeleccionado, selectedX, selectedY);
        }
    }

    private void dibujarMenu(Graphics2D g) {
        Font menuFont = new Font("Arial", Font.BOLD, (int)(screenWidth * 0.025));
        g.setFont(menuFont);
        FontMetrics fm = g.getFontMetrics();

        int menuY = screenHeight - 150;

        // Calcular el ancho total necesario para distribuir las opciones horizontalmente
        int totalWidth = 0;
        int spacing = 80; // Espacio entre opciones

        for (String opcion : opciones) {
            totalWidth += fm.stringWidth(opcion);
        }
        totalWidth += spacing * (opciones.length - 1);

        // Calcular posición inicial para centrar el menú
        int startX = (screenWidth - totalWidth) / 2;
        int currentX = startX;

        for (int i = 0; i < opciones.length; i++) {
            String opcion = opciones[i];
            int textWidth = fm.stringWidth(opcion);

            // Dibujar cursor (símbolo de Pac-Man)
            if (i == opcionSeleccionada && mostrarCursor) {
                g.setColor(Color.YELLOW);
                int cursorSize = 16;
                int cursorX = currentX - 30;
                int cursorY = menuY - cursorSize + 3;

                // Dibujar Pac-Man como cursor
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

            // Mover a la siguiente posición
            currentX += textWidth + spacing;
        }

        // Instrucciones de control
        Font instruccionFont = new Font("Arial", Font.PLAIN, (int)(screenWidth * 0.012));
        g.setFont(instruccionFont);
        g.setColor(Color.GRAY);

        String instruccion = "←/→: Navegar por el menú principal  ↑/↓: Seleccionar jugador";
        FontMetrics fmInst = g.getFontMetrics();
        int instWidth = fmInst.stringWidth(instruccion);
        int instX = (screenWidth - instWidth) / 2;
        int instY = screenHeight - 50;

        g.drawString(instruccion, instX, instY);
    }

    // Método para actualizar el puntaje de un jugador
    public void actualizarPuntaje(String nombreJugador, int nuevoPuntaje) {
        if (jugadoresPuntajes.containsKey(nombreJugador)) {
            if (nuevoPuntaje > jugadoresPuntajes.get(nombreJugador)) {
                jugadoresPuntajes.put(nombreJugador, nuevoPuntaje);
                guardarJugadores();
            }
        }
    }

    // Método para obtener el jugador seleccionado
    public String getJugadorSeleccionado() {
        return jugadorSeleccionado;
    }

    // Método para reiniciar todos los puntajes
    private void reiniciarPuntajes() {
        for (String nombre : nombresJugadores) {
            jugadoresPuntajes.put(nombre, 0);
        }
        guardarJugadores();
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                // Left: navegar menú hacia la izquierda
                opcionSeleccionada = (opcionSeleccionada - 1 + opciones.length) % opciones.length;
                break;

            case KeyEvent.VK_RIGHT:
                // Right: navegar menú hacia la derecha
                opcionSeleccionada = (opcionSeleccionada + 1) % opciones.length;
                break;

            case KeyEvent.VK_UP:
                // Up: navegar entre jugadores hacia arriba
                jugadorActualIndex = (jugadorActualIndex - 1 + nombresJugadores.size()) % nombresJugadores.size();
                break;

            case KeyEvent.VK_DOWN:
                // Down: navegar entre jugadores hacia abajo
                jugadorActualIndex = (jugadorActualIndex + 1) % nombresJugadores.size();
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
            case 0: // NUEVA PARTIDA
                // Aquí puedes iniciar el juego con el jugador seleccionado
                iniciarNuevaPartida();
                break;
            case 1: // REINICIAR PUNTAJES
                reiniciarPuntajes();
                break;
            case 2: // REGRESAR AL MENÚ
                regresarAlMenu();
                break;
        }
    }

    private void iniciarNuevaPartida() {
        // Método para iniciar una nueva partida con el jugador seleccionado
        // Aquí deberías llamar a tu clase principal del juego PacMan
        System.out.println("Iniciando partida para: " + jugadorSeleccionado);

        // Ejemplo de cómo podríamos integrar esto:
        //Mapa game = new PacManGame(jugadorSeleccionado, this);
        this.setVisible(false);

        // Por ahora solo mostramos un mensaje
        JOptionPane.showMessageDialog(this,
                "¡Iniciando partida para " + jugadorSeleccionado + "!",
                "Nueva Partida",
                JOptionPane.INFORMATION_MESSAGE);
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