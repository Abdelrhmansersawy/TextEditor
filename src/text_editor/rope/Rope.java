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
        // push_back
        insert(s , Treap.size(root));
    }
    public  void insert(char ch,int pos){
        insert(String.valueOf(ch) , pos);
    }
    public void delete(int l , int r){
        assert(l >= 0);
        Treap[] halves = Treap.split(root, r);
        root = halves[0];
        // delete halves[1] with destructor
        halves = Treap.split(root,l);
        root = halves[0];
        // delete halves[1] destructor
    }
    public  void  delete(int pos){
        delete(pos,pos + 1);
    }
    public String to_string(int l , int r){
        // l inclusive, r exclusive
        String ans = Treap.to_string(root, 0, l , r);
        // System.out.println("String is " + ans);
        return ans;
    };
    public String to_string(){
        return Treap.to_string(root);
    };
    public int length(){ return Treap.size(root); }

}
