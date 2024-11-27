import javax.swing.*;
import java.awt.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import gui.*;
public class Main {
    public static void main(String[] args) {
        com.formdev.flatlaf.FlatLightLaf.install();
        GUI window = new GUI();
    }
}
