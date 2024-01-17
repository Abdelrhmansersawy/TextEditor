package text_editor;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GUI implements ActionListener{
    JFrame window;
    JMenuBar menuBar;
    JMenu menuFile, menuEdit;
    JTabbedPane tabbedPane;
    JMenuItem newFileItem, openFileItem, saveFileItem, saveAsFileItem; // File section items
    JMenuItem undoItem, findItem, cutItem, copyItem, pasteItem; // Edit section items
    TextManagement file;
    public GUI(){
        this.setWindow();
        this.setMenuBar();
        this.window.setVisible(true);
        file = new TextManagement();
        setTabbedPane();
    }
    void setWindow(){
        window = new JFrame("Notepad");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setSize(800 , 800);
    }
    void setTabbedPane(){
        tabbedPane = new JTabbedPane();
        tabbedPane.add("Tab 1", file.textArea);
        window.add(tabbedPane);
    }
    void setMenuFile(){
        menuFile = new JMenu("File");
        menuBar.add(menuFile);
        // new file item
        newFileItem = new JMenuItem("new");
        menuFile.add(newFileItem);
        newFileItem.addActionListener(this);
        // open file item
        openFileItem = new JMenuItem("open");
        menuFile.add(openFileItem);
        openFileItem.addActionListener(this);
        // save file item
        saveFileItem = new JMenuItem("save");
        menuFile.add(saveFileItem);
        saveFileItem.addActionListener(this);
        // save as file item
        saveAsFileItem = new JMenuItem("save as");
        menuFile.add(saveAsFileItem);
        saveAsFileItem.addActionListener(this);

    }
    void setMenuEdit(){
        menuEdit = new JMenu("Edit");
        menuBar.add(menuEdit);
        // Undo item
        undoItem = new JMenuItem("undo");
        menuEdit.add(undoItem);
        undoItem.addActionListener(this);
        // Cut
        cutItem = new JMenuItem("cut");
        menuEdit.add(cutItem);
        cutItem.addActionListener(this);
        // Copy
        copyItem = new JMenuItem("copy");
        menuEdit.add(copyItem);
        copyItem.addActionListener(this);
        // Paste
        pasteItem = new JMenuItem("paste");
        menuEdit.add(pasteItem);
        pasteItem.addActionListener(this);
        // Find item
        findItem = new JMenuItem("find");
        menuEdit.add(findItem);
        findItem.addActionListener(this);
    }
    void setMenuBar(){
        menuBar = new JMenuBar();
        window.setJMenuBar(menuBar);
        setMenuFile();
        setMenuEdit();
    }
    @Override
    public void actionPerformed(ActionEvent e){
        if(e.getSource() == saveFileItem){
            file.save();
        }else if(e.getSource() == openFileItem){
            TextManagement tmp = new TextManagement();
            boolean is_opened = tmp.open();
            if(is_opened){
                tabbedPane.remove(0);
                file = tmp;
                tabbedPane.add("Tab 1", file.textArea);
                window.setVisible(true);
            }
            // add in the new tab
        }else if(e.getSource() == undoItem){
            file.undo();
        }else if(e.getSource() == saveAsFileItem){
            file.save();
        }else if(e.getSource() == findItem){
            file.find();
        } else if (e.getSource() == newFileItem) {
            tabbedPane.remove(0);
            file = new TextManagement();
            tabbedPane.add("Tab 1", file.textArea);
            window.setVisible(true);
        }
    }
}
