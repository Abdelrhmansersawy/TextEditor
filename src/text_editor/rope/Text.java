package text_editor.rope;
import text_editor.rope.UndoAction;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.util.ArrayDeque;
import java.awt.*;

import java.util.Deque;
import java.util.Objects;
import java.util.Vector;

public class Text implements UndoAction , KMP{
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
    public void changeAction(char typed_char, int cursor_pos, int start_selection, int end_selection){
        // selected text: range [  start_selection , end_selection ]
        undoAction Action = new undoAction();
        Action.cursorPos = cursor_pos;
        System.out.println(start_selection + " " + end_selection);
        if(start_selection != end_selection){
            Action.text = rope.to_string(start_selection,end_selection);
            System.out.println("Text: " + Action.text);
            Action.isSelectedText = true;
        }else Action.isSelectedText = false;
        if((int) typed_char == 8){
            // remove
            Action.action = "remove"; // remove-operation
            if(!Action.isSelectedText){
                Action.text = rope.to_string(cursor_pos,cursor_pos);
            }
        }else if((int) typed_char >= 32 && (int) typed_char <= 126){
            // add a character
            Action.action = "add"; // add-operation
            Action.text = String.valueOf(typed_char);
        }else{
            // copy, paste, cut,selectedAll
        }
        executeAction(Action);
    }
    public void executeAction(undoAction Action){
        if(Action.isSelectedText){
            int l = Action.cursorPos , r = l + Action.text.length() - 1;
            rope.delete(l , r);
            if(Objects.equals(Action.action, "add")){
                // Add operation
                rope.insert(Action.text, Action.cursorPos);
            }
        }else{
            if(Objects.equals(Action.action, "add")){
                // Add operation
                rope.insert(Action.text, Action.cursorPos);
            }else if(Objects.equals(Action.action, "remove")){
                // remove operation
                rope.delete(Action.cursorPos,Action.cursorPos);
            }
        }
        undoStackSize += Action.text.length(); // update the undoStackSize
        undoStack.addLast(Action);
        System.out.println(to_string());
        fixSizeOfUndoStack();
    }
    public void undo(){
        if(undoStack.isEmpty()) return;
        undoAction Action = undoStack.removeLast();

        if(Action.isSelectedText){
            if(Objects.equals(Action.action, "add")){
                // Add operation
                rope.delete(Action.cursorPos, Action.cursorPos);
            }
            rope.insert(Action.text, Action.cursorPos);
        }else {
            int l = Action.cursorPos , r = l + Action.text.length() - 1;
            if(Objects.equals(Action.action, "add")){
                // Add operation
                rope.delete(l, r);
            }else if(Objects.equals(Action.action, "remove")){
                // remove operation
                rope.insert(Action.text, Action.cursorPos);
            }
        }
        undoStackSize -= Action.text.length(); // update the undoStackSize
    }
    void fixSizeOfUndoStack(){
        while(!undoStack.isEmpty() && undoStackSize > maxUndoStackSize){
            undoStackSize -= undoStack.removeFirst().text.length();
        }
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

        String text; // Special for {"add", "remove", "all"} actions
        int l , r; // Special for {"add"} action
        int cursorPos; // position of the cursor
        public undoAction(){
            isSelectedText = false;
            action = "";
            text = "";
            l = 0;
            r = 0;
            cursorPos = 0;
        }
    }
}

interface KMP{
    static Vector<Integer> KMPSearch(String pat, String txt)
    {
        int M = pat.length();
        int N = txt.length();

        // create lps[] that will hold the longest
        // prefix suffix values for pattern
        int lps[] = new int[M];
        int j = 0; // index for pat[]

        // Preprocess the pattern (calculate lps[]
        // array)
        computeLPSArray(pat, M, lps);
        Vector<Integer> positions = new Vector<>();
        int i = 0; // index for txt[]
        while ((N - i) >= (M - j)) {
            if (pat.charAt(j) == txt.charAt(i)) {
                j++;
                i++;
            }
            if (j == M) {
                // found a pattern
                positions.addLast(i - j);
                j = lps[j - 1];
            }

            // mismatch after j matches
            else if (i < N && pat.charAt(j) != txt.charAt(i)) {
                // Do not match lps[0..lps[j-1]] characters,
                // they will match anyway
                if (j != 0)
                    j = lps[j - 1];
                else
                    i = i + 1;
            }
        }
        return  positions;
    }

    static void computeLPSArray(String pat, int M, int lps[])
    {
        // length of the previous longest prefix suffix
        int len = 0;
        int i = 1;
        lps[0] = 0; // lps[0] is always 0

        // the loop calculates lps[i] for i = 1 to M-1
        while (i < M) {
            if (pat.charAt(i) == pat.charAt(len)) {
                len++;
                lps[i] = len;
                i++;
            }
            else // (pat[i] != pat[len])
            {
                // This is tricky. Consider the example.
                // AAACAAAA and i = 7. The idea is similar
                // to search step.
                if (len != 0) {
                    len = lps[len - 1];

                    // Also, note that we do not increment
                    // i here
                }
                else // if (len == 0)
                {
                    lps[i] = len;
                    i++;
                }
            }
        }
    }
}