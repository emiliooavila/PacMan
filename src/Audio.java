
import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;

public class Audio {
    private Clip clip;

    public void reproducir(String nombreArchivo) {
        try {
            URL sonidoURL = getClass().getClassLoader().getResource("lobby-theme.wav");
            if (sonidoURL == null) {
                System.err.println("No se encontró el archivo: " + nombreArchivo);
                return;
            }

            AudioInputStream audioStream = AudioSystem.getAudioInputStream(sonidoURL);
            clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public void detener() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
        }
    }

    public void pausar() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
        }
    }

    public void continuar() {
        if (clip != null && !clip.isRunning()) {
            clip.start();
        }
    }
}
