import javax.swing.*;
import java.awt.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.formdev.flatlaf.FlatLightLaf;
import gui.*;
public class Main {
    public static void main(String[] args) {
        FlatLightLaf.setup();

        GUI window = new GUI();
    }
}
