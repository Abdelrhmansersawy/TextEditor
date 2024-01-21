package text_editor;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GUI implements ActionListener, KeyListener{
    JFrame window;
    JMenuBar menuBar;
    JMenu menuFile, menuEdit;
    JTabbedPane tabbedPane;
    // find feature
    JPanel findBar;
    JTextField findBarText, replaceBarText;
    JButton replaceButton, replaceAllButton;

    JButton findBarCloseButton;

    JMenuItem newFileItem, openFileItem, saveFileItem, saveAsFileItem; // File section items
    JMenuItem undoItem, findItem, findReplaceItem, cutItem, copyItem, pasteItem; // Edit section items
    TextManagement file;
    public GUI(){
        this.setWindow();
        this.setMenuBar();
        file = new TextManagement();
        setFindBar();
        setTabbedPane();

        this.window.setVisible(true);

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
        undoItem = new JMenuItem("Undo");
        menuEdit.add(undoItem);
        undoItem.addActionListener(this);
        // Find item
        findItem = new JMenuItem("Find");
        menuEdit.add(findItem);
        findItem.addActionListener(this);
        // Find Replace item
        findReplaceItem = new JMenuItem("Find and Replace");
        menuEdit.add(findReplaceItem);
        findReplaceItem.addActionListener(this);
        // Cut
        cutItem = new JMenuItem("Cut");
        menuEdit.add(cutItem);
        cutItem.addActionListener(this);
        // Copy
        copyItem = new JMenuItem("Copy");
        menuEdit.add(copyItem);
        copyItem.addActionListener(this);
        // Paste
        pasteItem = new JMenuItem("Paste");
        menuEdit.add(pasteItem);
        pasteItem.addActionListener(this);
    }
    void setMenuBar(){
        menuBar = new JMenuBar();
        window.setJMenuBar(menuBar);
        setMenuFile();
        setMenuEdit();
    }
    void setFindBar(){
        findBar = new JPanel();
        findBarText = new JTextField(20);
        replaceBarText = new JTextField(20);

        findBarCloseButton = new JButton("Close");
        replaceButton = new JButton("Replace");
        replaceAllButton = new JButton("Replace All");
        findBar.add(findBarText);
        findBar.add(replaceBarText);
        findBar.add(replaceButton);
        findBar.add(replaceAllButton);
        findBar.add(findBarCloseButton);
        findBar.setLayout(new FlowLayout());
        replaceButton.addActionListener(this);
        replaceAllButton.addActionListener(this);
        findBarCloseButton.addActionListener(this);
        findBar.setVisible(false);
        findBarText.addKeyListener(this);
        window.add(findBar,  BorderLayout.SOUTH);
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
            // Hide all component special of replace bar
            replaceBarText.setVisible(false);
            replaceButton.setVisible(false);
            replaceAllButton.setVisible(false);
            findBar.setVisible(true); // show the search panel
        }else if(e.getSource() == findReplaceItem){
            // show all component special of replace bar
            replaceBarText.setVisible(true);
            replaceButton.setVisible(true);
            replaceAllButton.setVisible(true);
            findBar.setVisible(true); // show the search panel

        }else if(e.getSource() == replaceButton){
            file.replace(findBarText.getText(), replaceBarText.getText());
            file.find(findBarText.getText());
        }else if(e.getSource() == replaceAllButton){
            file.replaceAll(findBarText.getText(), replaceBarText.getText());
            file.find(findBarText.getText());
        } else if (e.getSource() == newFileItem) {
            tabbedPane.remove(0);
            file = new TextManagement();
            tabbedPane.add("Tab 1", file.textArea);
            window.setVisible(true);
        }else if(e.getSource() == findBarCloseButton){
            findBarText.setText(""); // Clear the search bar
            findBar.setVisible(false); // Hide the search panel
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {
        if(e.getSource() == findBarText){
            file.find(findBarText.getText());
        }

    }
}
