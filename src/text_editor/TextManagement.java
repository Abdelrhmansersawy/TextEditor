package text_editor;
import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.filechooser.*;
import java.util.*;
import javax.swing.text.*;

public class TextManagement  implements KeyListener{
    JFileChooser fileChooser;
    JTextArea textArea;
    DefaultHighlighter highlighter;
    JScrollPane scrollPane;
    String text, path, title;
    boolean is_saved, auto_save;
    Deque<String> undoStack = new LinkedList<>();
    public TextManagement(){
        is_saved = false;
        auto_save = false;
        text = "";
        title = "New file";
        setTextArea();
    }
    void setTextArea(){
        textArea = new JTextArea();
        scrollPane = new JScrollPane(textArea,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        highlighter = (DefaultHighlighter) textArea.getHighlighter();
        highlighter.setDrawsLayeredHighlights(false);
        textArea.addKeyListener(this);
        undoStack.push("");
        setFont();
    }
    void setFont(){
        Font font = new Font("Arial", Font.PLAIN, 16);
        textArea.setFont(font);
    }
    public void save(){
        fileChooser = new JFileChooser(FileSystemView.getFileSystemView());
        int response = fileChooser.showSaveDialog(null);
        // Check if the user clicked the "Open" or "Save" button
        if(response == JFileChooser.APPROVE_OPTION){
            PrintWriter fileOut = null;
            File file = new File(fileChooser.getSelectedFile().getAbsolutePath());
            path = fileChooser.getSelectedFile().getAbsolutePath(); // save the path of the file
            try {
                fileOut = new PrintWriter(file);
                fileOut.println(text);
            }
            catch (FileNotFoundException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            finally {
                fileOut.close();
                is_saved = true;
                auto_save = true;
            }
        }
    }
    public void auto_save() {
        boolean fileExists = Files.exists(Path.of(path));
        if(!fileExists){
             auto_save = false;
             return;
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
            // Write content to the file
            writer.write(textArea.getText());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public boolean open(){
        fileChooser = new JFileChooser(FileSystemView.getFileSystemView());
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Text files", "txt", "cpp", "java");
        fileChooser.setFileFilter(filter);
        int response = fileChooser.showSaveDialog(null);
        // Check if the user clicked the "Open" or "Save" button
        if(response == JFileChooser.APPROVE_OPTION){
            File file = new File(fileChooser.getSelectedFile().getAbsolutePath());
            path = fileChooser.getSelectedFile().getAbsolutePath(); // update the path of the file
            Path filePath = Paths.get(path);
            boolean fileExists = Files.exists(filePath);
            if(!fileExists){ // No exit File with this path
                alert("There doesn't exit a file with this path.","Can't open the file");
                return false;
            }
            Scanner fileIn = null;
            try {
                fileIn = new Scanner(file);
                StringBuilder content = new StringBuilder();
                if(file.isFile()) {
                    while(fileIn.hasNextLine()) {
                        String line = fileIn.nextLine()+"\n";
                        content.append(line);
                    }
                }
                text = content.toString();
                textArea.setText(text);
            } catch (FileNotFoundException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            finally {
                fileIn.close();
                is_saved = true;
                auto_save = true;
                return true;
            }
        }
        return  false;
    }
    public void alert(String message, String title){
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE);
    }
    @Override
    public void keyTyped(KeyEvent e) {
        // This method is called when a key is typed (pressed and released)
        char x = e.getKeyChar();
        System.out.println((int)x);
        if(x == ' ') {
            undoStack.push(textArea.getText());
            if (auto_save) auto_save();
        }
    }
    void undo(){
        if(undoStack.size() == 1) return;
        undoStack.pop();
        textArea.setText(undoStack.peek());
    }
    void find(){
        String word = JOptionPane.showInputDialog(null, "Enter something:");
        if (word != null) {

        }
    }
    public void highlightRange(int start, int end) {
        try {
            highlighter.addHighlight(start, end, DefaultHighlighter.DefaultPainter);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void keyPressed(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}
}
