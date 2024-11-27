package text;


import collection.Pair;

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

public class TextManagement extends Component implements KeyListener{
    JFileChooser fileChooser;
    public JTextArea textArea;
    DefaultHighlighter highlighter;
    public JScrollPane scrollPane;
    public String path, title;
    Text text;
    //Text
    boolean is_saved, auto_save;
    private static int cnt = 0;
    public TextManagement(){
        is_saved = false;
        auto_save = false;
        text = new Text();
        title = "New file " + String.valueOf(cnt++);
        setTextArea();

    }
    void setTextArea(){
        textArea = new JTextArea();
        scrollPane = new JScrollPane(textArea,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        // scrollPane.setBorder(BorderFactory.createEmptyBorder());
        highlighter = (DefaultHighlighter) textArea.getHighlighter();
        highlighter.setDrawsLayeredHighlights(false);
        textArea.addKeyListener(this);
        setFont();
    }
    void setFont(){
        Font font = new Font("Arial", Font.PLAIN, 16);
        textArea.setFont(font);
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
        }
    }
    public void undo(){
        text.undo();
        textArea.setText(text.to_string());
    }
    public void save(){
        fileChooser = new JFileChooser(FileSystemView.getFileSystemView());
        int response = fileChooser.showSaveDialog(null);
        // Check if the user clicked the "Open" or "Save" button
        if(response == JFileChooser.APPROVE_OPTION){
            PrintWriter fileOut = null;
            File file = new File(fileChooser.getSelectedFile().getAbsolutePath());
            title = file.getName();
            path = fileChooser.getSelectedFile().getAbsolutePath(); // save the path of the file
            try {
                fileOut = new PrintWriter(file);
                fileOut.println(text.to_string());
            }
            catch (FileNotFoundException e1) {
                // TODO Auto-generated catch block

            }
            finally {
                fileOut.close();
                is_saved = true;
                auto_save = true;
            }
        }
    }
    public boolean getStatusOfSave(){ return auto_save; }
    public void saveAs(){
        fileChooser = new JFileChooser(FileSystemView.getFileSystemView());
        int response = fileChooser.showSaveDialog(null);
        // Check if the user clicked the "Open" or "Save" button
        if(response == JFileChooser.APPROVE_OPTION){
            PrintWriter fileOut = null;
            File file = new File(fileChooser.getSelectedFile().getAbsolutePath());
            title = file.getName();
            path = fileChooser.getSelectedFile().getAbsolutePath(); // save the path of the file
            try {
                fileOut = new PrintWriter(file);
                fileOut.println(text.to_string());
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
    public boolean open(){
        fileChooser = new JFileChooser(FileSystemView.getFileSystemView());
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Text files", "txt", "cpp", "java","log");
        fileChooser.setFileFilter(filter);
        int response = fileChooser.showSaveDialog(null);
        // Check if the user clicked the "Open" or "Save" button
        if(response == JFileChooser.APPROVE_OPTION){
            File file = new File(fileChooser.getSelectedFile().getAbsolutePath());
            title = file.getName();
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
                textArea.setText(content.toString());
                text = new Text(content.toString());
            } catch (FileNotFoundException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            finally {
                fileIn.close();
                is_saved = true;
                auto_save = true;
                --cnt;
                return true;
            }
        }
        return  false;
    }
    public void alert(String message, String title){
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE);
    }
    public Boolean areYouSureToClose(){
        int result = JOptionPane.showConfirmDialog(
                this.textArea,
                "Do you want to save the file?",
                "Confirmation",
                JOptionPane.YES_NO_OPTION
        );

        // Check the result
        if (result == JOptionPane.YES_OPTION) {
            saveAs();
            return false;
        } else if (result == JOptionPane.NO_OPTION) {
            return true;
        }
        return true;
    }
    public void find(String word){
        if (word != null) {
            removeHighlight();
            Vector<Pair<Integer,Integer>> Interval = text.find(word);
            for(int i = 0; i < Interval.size(); ++i){
                highlightRange(Interval.get(i).getKey(), Interval.get(i).getValue());
            }
        }
        Vector<Pair<Integer,Integer>> Interval = text.find(word);
        for(Pair<Integer,Integer> p : Interval){
            highlightRange(p.getKey(),p.getValue());
        }
    }
    public void replace(String pattern, String replacedWord){
        Vector<Pair<Integer,Integer>> Interval = text.find(pattern);
        if(Interval.isEmpty()) return;
        int l = Interval.get(0).getKey() , r = Interval.get(0).getValue();
        text.findAction(l,r, pattern, replacedWord);
        textArea.setText(text.to_string());
    }
    public void replaceAll(String pattern, String replacedWord){
        Vector<Pair<Integer,Integer>> Interval = text.find(pattern);
        String curText = text.to_string();
        StringBuilder newText = new StringBuilder();
        int i = 0 , j = 0;
        int n = curText.length(), m = Interval.size();
        while(i < n && j < m){
            while (i < n && Interval.get(j).getKey() > i){
                newText.append(curText.charAt(i));
                ++i;
            }
            newText.append(replacedWord);
            while(i < n && Interval.get(j).getValue() > i){
                ++i;
            }
            while (j < m && Interval.get(j).getKey() < i) j++;
        }
        while (i < n){
            newText.append(curText.charAt(i));
            ++i;
        }
        text.findAction(0,n, curText, String.valueOf(newText));
        textArea.setText(text.to_string());

    }
    public void highlightRange(int start, int end) {
        try {
            highlighter.addHighlight(start, end, DefaultHighlighter.DefaultPainter);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }
    void removeHighlight() {
        Highlighter highlighter = textArea.getHighlighter();
        Highlighter.Highlight[] highlights = highlighter.getHighlights();

        for (Highlighter.Highlight highlight : highlights) {
            if (highlight.getPainter() instanceof DefaultHighlighter.DefaultHighlightPainter) {
                // Only remove highlights applied by DefaultHighlightPainter
                highlighter.removeHighlight(highlight);
            }
        }
    }
    public void cut(){
        System.out.println("lol");
        int start_selection = textArea.getSelectionStart();
        int end_selection = textArea.getSelectionEnd();
        int cursor_pos = textArea.getCaretPosition();
        char typed_char = (char) 8;
        text.changeAction(typed_char, cursor_pos, start_selection,end_selection);
        //if(auto_save) auto_save(); // auto save the file
    }
    @Override
    public void keyTyped(KeyEvent e) {
        int start_selection = textArea.getSelectionStart();
        int end_selection = textArea.getSelectionEnd();
        int cursor_pos = textArea.getCaretPosition();
        char typed_char = e.getKeyChar();
        text.changeAction(typed_char, cursor_pos, start_selection,end_selection);
        if(auto_save) auto_save(); // auto save the file
    }
    @Override
    public void keyPressed(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {

    }
}