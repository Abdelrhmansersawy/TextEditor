package text_editor.rope;

public class Rope extends Treap_impl {
    Treap root = null;
    public void insert(String s, int pos){
        Treap cur = null;
        for(char ch : s.toCharArray()){
            cur = Treap.merge(cur , new Treap(ch));
        }
        Treap[] halves = Treap.split(root, pos);
        root = halves[0];
        root = Treap.merge(root, cur);
        root = Treap.merge(root, halves[1]);
    }
    public void insert(String s){
        // insert back
        insert(s , Treap.size(root));
    }
    public void delete(int l , int r){
        Treap[] halves = Treap.split(root, r);
        root = halves[0];
        // delete halves[1]
        halves = Treap.split(root,l);
        root = halves[0];
        // delete halves[1]
    }
    public String to_string(){
        return Treap.to_string(root);
    };

}
