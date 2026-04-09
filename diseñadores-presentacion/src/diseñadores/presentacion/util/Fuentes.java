package diseñadores.presentacion.util;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.IOException;

public class Fuentes {

  private static Font baseRegular = null;
  private static Font baseBold = null;
  private static Font baseMedium = null;
  private static boolean loaded = false;

  private Fuentes() {
  }

  public static void cargar() {
    if (loaded) {
      return;
    }
    loaded = true;
    try {
      GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();

      File fReg = new File("fonts/Poppins-Regular.ttf");
      File fBold = new File("fonts/Poppins-Bold.ttf");
      File fMed = new File("fonts/Poppins-Medium.ttf");

      if (fReg.exists()) {
        baseRegular = Font.createFont(Font.TRUETYPE_FONT, fReg);
        ge.registerFont(baseRegular);
      }
      if (fBold.exists()) {
        baseBold = Font.createFont(Font.TRUETYPE_FONT, fBold);
        ge.registerFont(baseBold);
      }
      if (fMed.exists()) {
        baseMedium = Font.createFont(Font.TRUETYPE_FONT, fMed);
        ge.registerFont(baseMedium);
      }

      if (baseRegular != null) {
        System.out.println("[Fuentes] Poppins cargada correctamente.");
      } else {
        System.out.println("[Fuentes] Poppins no encontrada en fonts/ — usando SansSerif.");
      }
    } catch (FontFormatException | IOException ex) {
      System.err.println("[Fuentes] Error al cargar Poppins: " + ex.getMessage());
    }
  }

  public static Font regular(float size) {
    if (baseRegular != null) {
      return baseRegular.deriveFont(Font.PLAIN, size);
    }
    return new Font("SansSerif", Font.PLAIN, (int) size);
  }

  public static Font bold(float size) {
    if (baseBold != null) {
      return baseBold.deriveFont(Font.BOLD, size);
    }
    if (baseRegular != null) {
      return baseRegular.deriveFont(Font.BOLD, size);
    }
    return new Font("SansSerif", Font.BOLD, (int) size);
  }

  public static Font medium(float size) {
    if (baseMedium != null) {
      return baseMedium.deriveFont(Font.PLAIN, size);
    }
    return bold(size);
  }

  public static Font r(int size) {
    return regular(size);
  }

  public static Font b(int size) {
    return bold(size);
  }

  public static Font m(int size) {
    return medium(size);
  }

}
