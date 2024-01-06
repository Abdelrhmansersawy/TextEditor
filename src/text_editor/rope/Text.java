package text_editor.rope;

import java.util.Deque;
import java.util.LinkedList;

public class Text {
    String text;
    Deque<String> undo_stack = new LinkedList<>();
    Rope rope = new Rope();
    void findChangeAction(){
        // find the change in the content of the textArea

    }
    void undo(){

    }
}
