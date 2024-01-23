package text_editor.text;
import text_editor.rope.Pair;
import text_editor.rope.Rope;
import text_editor.text.KMP;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.util.ArrayDeque;
import java.awt.*;
import java.util.Deque;
import java.util.Objects;
import java.util.Vector;

public class Text implements UndoAction{
    Deque<undoAction> undoStack;
    int undoStackSize = 0;
    final int maxUndoStackSize = (int) 2e7; // maximum size of stack
    Rope rope;
    public Text(String s){
        rope = new Rope();
        undoStack = new ArrayDeque<>();
        rope.insert(s);
    }
    public Text(){
        rope = new Rope();
        undoStack = new ArrayDeque<>();
    }
    // -----------------------------------Undo feature section-----------------------------------------------
    // Use Command Pattern to implement the undo features
    public void changeAction(char typed_char, int cursor_pos, int start_selection, int end_selection){
        // selected text: range [  start_selection , end_selection ]
        undoAction Action = new undoAction();
        Action.cursorPos = cursor_pos;
        if(start_selection != end_selection){
            Action.isSelectedText = true;
        }else Action.isSelectedText = false;
        Action.selectedText = rope.to_string(start_selection,end_selection + 1);
        if((int) typed_char == 8){
            // remove
            Action.action = "remove"; // remove-operation
        }else if((int) typed_char >= 32 && (int) typed_char <= 126){
            // add a character
            Action.action = "add"; // add-operation
            Action.typedtext = String.valueOf(typed_char);
        }else return;
        executeAction(Action);
    }
    public void findAction(int l , int r, String word, String replacedWord){
        undoAction Action = new undoAction();
        Action.cursorPos = l;
        Action.action = "add";
        Action.isSelectedText = true;
        Action.l = l;
        Action.r = r;
        Action.selectedText = word;
        Action.typedtext = replacedWord;
        executeAction(Action);
    }
    public void executeAction(undoAction Action){
        if(Action.isSelectedText){
            int l = Action.cursorPos , r = l + Action.selectedText.length();
            rope.delete(l , r);
            if(Objects.equals(Action.action, "add")){
                // Add operation
                rope.insert(Action.typedtext, Action.cursorPos);
            }
        }else{
            if(Objects.equals(Action.action, "add")){
                // Add operation
                rope.insert(Action.typedtext, Action.cursorPos);
            }else if(Objects.equals(Action.action, "remove")){
                // remove operation
                rope.delete(Action.cursorPos,Action.cursorPos + 1);
            }
        }
        undoStackSize += Action.selectedText.length() + Action.typedtext.length(); // update the undoStackSize
        undoStack.addLast(Action);
        fixSizeOfUndoStack();
    }
    public void undo(){
        if(undoStack.isEmpty()) return;

        undoAction Action = undoStack.removeLast();
        if(Action.isSelectedText){
            if(Objects.equals(Action.action, "add")){
                // Add operation
                int l = Action.cursorPos , r = l + Action.typedtext.length();
                rope.delete(l, r);
            }
            rope.insert(Action.selectedText, Action.cursorPos);
        }else {
            int l = Action.cursorPos , r = l + Action.typedtext.length();
            if(Objects.equals(Action.action, "add")){
                // Add operation
                rope.delete(l, r);
            }else if(Objects.equals(Action.action, "remove")){
                // remove operation
                rope.insert(Action.typedtext, Action.cursorPos);
            }
        }
        undoStackSize -= Action.selectedText.length(); // update the undoStackSize
    }
    void fixSizeOfUndoStack(){
        while(!undoStack.isEmpty() && undoStackSize > maxUndoStackSize){
            undoAction Action = undoStack.removeFirst();
            undoStackSize -= Action.selectedText.length() + Action.typedtext.length();
        }
    }
    // ----------------------------------------------------------------------------------------------------------
    
    // -----------------------------------Find feature section-----------------------------------------------
    public Vector<Pair<Integer,Integer>> find(String pattern){
        Vector<Integer> startPos = KMP.KMPSearch(pattern, rope.to_string());
        Vector<Pair<Integer,Integer>> Interval = new Vector<>();
        for(Integer st : startPos){
            Interval.add(new Pair<>(st, st + pattern.length()));
        }
        return Interval;
    }
    // ----------------------------------------------------------------------------------------------------------

    public String to_string(){
        return rope.to_string();
    }
    private static String getClipboardContent() {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable transferable = clipboard.getContents(null);

        if (transferable != null && transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            try {
                return (String) transferable.getTransferData(DataFlavor.stringFlavor);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }
}


interface UndoAction {
    static class undoAction{
        String action; // {"add" ,"remove", "all"}
        Boolean isSelectedText; // Special for {"add", "remove"} actions

        String selectedText; // Special for {"add", "remove", "all"} actions
        String typedtext;
        int l , r; // Special for {"add"} action
        int cursorPos; // position of the cursor
        public undoAction(){
            isSelectedText = false;
            action = "";
            selectedText = "";
            typedtext = "";
            l = 0;
            r = 0;
            cursorPos = 0;
        }
    }
}

