package text_editor.main;
import com.formdev.flatlaf.FlatLightLaf;
import text_editor.CloseButton;
import text_editor.text.TextManagement;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Vector;

public class GUI extends javax.swing.JFrame{

    // find feature


    public Color Transparent = new Color(0,0,0,0);
    public GUI(){
        this.setWindow();
        this.setMenuBar();
        tabs = new Vector<>();
        setFindBar();
        setTabbedPane();

        this.setVisible(true);

    }

    void setWindow(){
        this.setTitle("Notepad");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(1200 , 800);
    }
    void setTabbedPane(){
        tabbedPane = new JTabbedPane();
        addTab(new TextManagement());
        this.add(tabbedPane);
    }
    void setMenuBar(){
        menuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        newFileItem = new javax.swing.JMenuItem();
        openFileItem = new javax.swing.JMenuItem();
        saveFileItem = new javax.swing.JMenuItem();
        saveAsFileItem = new javax.swing.JMenuItem();
        editMenu = new javax.swing.JMenu();
        undoItem = new javax.swing.JMenuItem();
        findItem = new javax.swing.JMenuItem();
        findAndReplaceItem = new javax.swing.JMenuItem();
        cutItem = new javax.swing.JMenuItem();
        copyItem = new javax.swing.JMenuItem();
        pasteItem = new javax.swing.JMenuItem();
        aboutMenu = new javax.swing.JMenu();

        fileMenu.setText("File");

        newFileItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        newFileItem.setText("New File");
        newFileItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newFileItemActionPerformed(evt);
            }
        });
        fileMenu.add(newFileItem);

        openFileItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        openFileItem.setText("Open File");
        openFileItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openFileItemActionPerformed(evt);
            }
        });
        fileMenu.add(openFileItem);

        saveFileItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        saveFileItem.setText("Save File");
        saveFileItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveFileItemActionPerformed(evt);
            }
        });
        fileMenu.add(saveFileItem);

        saveAsFileItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.SHIFT_DOWN_MASK | java.awt.event.InputEvent.CTRL_DOWN_MASK));
        saveAsFileItem.setText("Save as File");
        saveAsFileItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveAsFileItemActionPerformed(evt);
            }
        });
        fileMenu.add(saveAsFileItem);

        menuBar.add(fileMenu);

        editMenu.setText("Edit");

        undoItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Z, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        undoItem.setText("Undo");
        undoItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                undoItemActionPerformed(evt);
            }
        });
        editMenu.add(undoItem);

        findItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        findItem.setText("Find");
        findItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                findItemActionPerformed(evt);
            }
        });
        editMenu.add(findItem);

        findAndReplaceItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        findAndReplaceItem.setText("Find and Replace");
        findAndReplaceItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                findAndReplaceItemActionPerformed(evt);
            }
        });
        editMenu.add(findAndReplaceItem);

        cutItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        cutItem.setText("Cut");
        cutItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cutItemActionPerformed(evt);
            }
        });
        editMenu.add(cutItem);

        copyItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        copyItem.setText("Copy");
        editMenu.add(copyItem);

        pasteItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        pasteItem.setText("Paste");
        pasteItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pasteItemActionPerformed(evt);
            }
        });
        editMenu.add(pasteItem);

        menuBar.add(editMenu);



        aboutMenu.setText("About");
        menuBar.add(aboutMenu);

        setJMenuBar(menuBar);

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
        findBar.setVisible(false);
        this.add(findBar,  BorderLayout.SOUTH);
        findBarCloseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                findBarCloseButtonActionPerformed(evt);
            }
        });
        replaceButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                replaceButtonActionPerformed(evt);
            }
        });
        replaceAllButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                replaceAllButtonActionPerformed(evt);
            }
        });
        findBarText.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                findBarTextKeyReleased(evt);
            }
        });
    }
    void addTab(TextManagement e) {
        JPanel panel = new JPanel(new BorderLayout());
        tabs.add(e);
        // Your tab content goes here
        panel.add(tabs.lastElement().scrollPane, BorderLayout.CENTER);
        // Create a close button
        String title = tabs.lastElement().title;
        CloseButton closeButton = new CloseButton("X");
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int tabIndex = tabbedPane.indexOfTab(title);
                assert(tabIndex != -1);
                if(!tabs.elementAt(tabIndex).getStatusOfSave()){
                    if(tabs.elementAt(tabIndex).areYouSureToClose()){
                        tabbedPane.removeTabAt(tabIndex);
                        tabs.removeElementAt(tabIndex);
                    }
                }

                if(tabs.isEmpty()) dispose();
            }
        });

        // Create a panel to hold the tab title and close button
        JPanel tabTitlePanel = new JPanel();

        tabTitlePanel.add(new JLabel(title));
        tabTitlePanel.add(closeButton);
        tabTitlePanel.setBackground(Transparent);
        // Add the panel to the tab
        tabbedPane.addTab(title, panel);
        tabbedPane.setTabComponentAt(tabs.size() - 1, tabTitlePanel);
    }
    private int getIndex(){
        return tabbedPane.getSelectedIndex();
    }
    private void newFileItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newFileItemActionPerformed
        addTab(new TextManagement());
    }//GEN-LAST:event_newFileItemActionPerformed

    private void openFileItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openFileItemActionPerformed
        // TODO add your handling code here:
        TextManagement tmp = new TextManagement();
        if(tmp.open()){
            addTab(tmp);
        }
    }//GEN-LAST:event_openFileItemActionPerformed

    private void undoItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_undoItemActionPerformed
        if(tabs.isEmpty()) return;
        tabs.elementAt(tabbedPane.getSelectedIndex()).undo();
    }//GEN-LAST:event_undoItemActionPerformed

    private void cutItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cutItemActionPerformed
        tabs.elementAt(getIndex()).cut();
    }//GEN-LAST:event_cutItemActionPerformed

    private void pasteItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pasteItemActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_pasteItemActionPerformed

    private void saveFileItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveFileItemActionPerformed
        if(tabs.isEmpty()) return;
        tabs.elementAt(getIndex()).save();
    }//GEN-LAST:event_saveFileItemActionPerformed

    private void saveAsFileItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveAsFileItemActionPerformed
        if(tabs.isEmpty()) return;
        tabs.elementAt(getIndex()).save();
    }//GEN-LAST:event_saveAsFileItemActionPerformed

    private void findItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_findItemActionPerformed
        // Hide all component special of replace bar
        // Hide all component special of replace bar
        replaceBarText.setVisible(false);
        replaceButton.setVisible(false);
        replaceAllButton.setVisible(false);
        findBar.setVisible(true); // show the search panel
    }//GEN-LAST:event_findItemActionPerformed

    private void findAndReplaceItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_findAndReplaceItemActionPerformed
        // show all component special of replace bar
        replaceBarText.setVisible(true);
        replaceButton.setVisible(true);
        replaceAllButton.setVisible(true);
        findBar.setVisible(true); // show the search panel
    }
    private void findBarCloseButtonActionPerformed(java.awt.event.ActionEvent evt){
        findBarText.setText(""); // Clear the search bar
        findBar.setVisible(false); // Hide the search panel
    }
    private void findBarTextKeyReleased(java.awt.event.KeyEvent evt) {
        tabs.elementAt(getIndex()).find(findBarText.getText());
    }
    private void replaceButtonActionPerformed(java.awt.event.ActionEvent evt){
        tabs.elementAt(getIndex()).replace(findBarText.getText(), replaceBarText.getText());
        tabs.elementAt(getIndex()).find(findBarText.getText());
    }
    private void replaceAllButtonActionPerformed(java.awt.event.ActionEvent evt){
        tabs.elementAt(getIndex()).replaceAll(findBarText.getText(), replaceBarText.getText());
        tabs.elementAt(getIndex()).find(findBarText.getText());
    }
    private javax.swing.JMenu aboutMenu;
    private javax.swing.JMenuItem copyItem;
    private javax.swing.JMenuItem cutItem;
    private javax.swing.JMenu editMenu;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JMenuItem findAndReplaceItem;
    private javax.swing.JMenuItem findItem;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenuItem newFileItem;
    private javax.swing.JMenuItem openFileItem;
    private javax.swing.JMenuItem pasteItem;
    private javax.swing.JMenuItem saveAsFileItem;
    private javax.swing.JMenuItem saveFileItem;
    private javax.swing.JTabbedPane tabbedPane;
    private javax.swing.JMenuItem undoItem;
    private JPanel findBar;
    private JTextField findBarText, replaceBarText;
    private JButton replaceButton, replaceAllButton;

    private JButton findBarCloseButton;
    Vector<TextManagement> tabs;

}
