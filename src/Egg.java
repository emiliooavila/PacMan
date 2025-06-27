import javax.swing.*;
import java.awt.*;

import static java.lang.System.exit;

public class Egg extends JFrame {

    public Egg() {
        setExtendedState(JFrame.MAXIMIZED_BOTH); // pantalla completa
        setUndecorated(true); // sin bordes
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.BLACK);

        Font font = new Font("Monospaced", Font.PLAIN, 24);

        String[] letras = {
                "Este juego fue desarrollado por:",
                "Emilio Avila",
                "Rey Picazo",
                "Maxy Velazquez",
                "Diego Delgado",
                "Cher no chambeo"
        };

        panel.add(Box.createVerticalGlue());

        for (String linea : letras) {
            JPanel lineaPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            lineaPanel.setBackground(Color.BLACK);
            lineaPanel.setBorder(BorderFactory.createEmptyBorder(0, 150, 0, 0)); // Mueve texto un poco a la izquierda

            JLabel label = new JLabel(linea);
            label.setFont(font);
            label.setForeground(Color.WHITE);

            lineaPanel.add(label);
            panel.add(lineaPanel);
        }

        panel.add(Box.createVerticalGlue());

        add(panel);
        setVisible(true);

        try {
            Thread.sleep(10000); //creo que son 10 segundos
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        dispose();
        exit(0);
    }

}
