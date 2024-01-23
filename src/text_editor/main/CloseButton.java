/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package text_editor;

/**
 *
 * @author sersawy
 */
import javax.swing.*;
import java.awt.*;

public class CloseButton extends JButton {

    private Integer width = 20;
    private Integer height = 20;
    public CloseButton(String text) {
        super(text);

        this.setPreferredSize(new Dimension(width, height));
        Font font = new Font("Arial", Font.PLAIN, 13);
        this.setFont(font);
        this.setMargin(new Insets(0, 0, 0, 0));
        this.setBorder(BorderFactory.createEmptyBorder());
        setFocusPainted(false); // Remove the default focus border
        setContentAreaFilled(false); // Make the content area transparent
    }
}
