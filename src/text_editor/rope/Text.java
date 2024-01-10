package text_editor.rope;
import text_editor.Tuple;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

public class Text {
    Deque<Tuple<String,Integer,Boolean>> undoStack;
    // string , pos , type of action {false: remove, true: add}
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
    public void action(char x, int pos){
        if((int) x == 8){
            // remove character
            rope.delete(pos);
            undoStack.add(new Tuple<>(String.valueOf(x),pos,true));
            undoStackSize++;
        }else if((int) x >= 32 && (int) x < 126){
            // insert character
            rope.insert(x, pos);
            undoStack.add(new Tuple<>(String.valueOf(x), pos,false));
            System.out.println(undoStack.size());
            undoStackSize++;
        }else{
            // copy, paste , select all, tab
        }
        fixSizeOfUndoStack();
    }
    public void undo(){
        if(undoStack.isEmpty()) return;
        Tuple<String,Integer,Boolean> Action = undoStack.removeLast();
        if(Action.getThird()){
            // add
            rope.insert(Action.getFirst(),Action.getSecond());
        }else{
            // remove
            int l = Action.getSecond(), r = l + Action.getFirst().length() - 1;
            rope.delete(l,r);
        }
    }
    void fixSizeOfUndoStack(){
        while(!undoStack.isEmpty() && undoStackSize > maxUndoStackSize){
            undoStackSize -= undoStack.removeFirst().getFirst().length();
        }
    }
    public String to_string(){
        return rope.to_string();
    }
}
