package text_editor.rope;
import java.util.Random;
public class Treap_impl {
    static final Random rand = new Random(5);
    static class Treap {
        int priority;
        char data;
        Treap[] kids=new Treap[2];

        int subtreeSize, sum, toProp;

        public Treap(char data) {
            this.data=data;
            priority=rand.nextInt();
            recalc(this);
        }
        static Treap[] split(Treap me, int nInLeft) {
            if (me==null) return new Treap[] {null, null};
            if (size(me.kids[0])>=nInLeft) {
                Treap[] leftRes=split(me.kids[0], nInLeft);
                me.kids[0]=leftRes[1];
                recalc(me);
                return new Treap[] {leftRes[0], me};
            }
            else {
                nInLeft=nInLeft-size(me.kids[0])-1;
                Treap[] rightRes=split(me.kids[1], nInLeft);
                me.kids[1]=rightRes[0];
                recalc(me);
                return new Treap[] {me, rightRes[1]};
            }
        }

        static Treap merge(Treap l, Treap r) {
            if (l==null) return r;
            if (r==null) return l;
            if (l.priority<r.priority) {
                l.kids[1]=merge(l.kids[1], r);
                recalc(l);
                return l;
            }
            else {
                r.kids[0]=merge(l, r.kids[0]);
                recalc(r);
                return r;
            }
        }

        static void recalc(Treap me) {
            if (me==null) return;
            me.subtreeSize=1;
            me.sum=me.data+me.toProp*size(me);
            for (Treap t:me.kids) if (t!=null) me.subtreeSize+=t.subtreeSize;
        }
        static int size(Treap t) {
            return t==null?0:t.subtreeSize;
        }
        static  String to_string(Treap me){
            if(me == null) return "";
            return to_string(me.kids[0]) + me.data + to_string(me.kids[1]);
        }
    }
}
