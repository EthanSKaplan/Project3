import java.util.ArrayList;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicMarkableReference;

public class LockFreeList {
    final Node head;

    public LockFreeList() {
        head = new Node(Integer.MIN_VALUE);
        Node sentinal = new Node(Integer.MAX_VALUE);
        Node tail = new Node(Integer.MAX_VALUE);
        head.next = new AtomicMarkableReference<Node>(tail, false);
        tail.next = new AtomicMarkableReference<Node>(sentinal, false);
    }

    private class Node {
        int tag;
        public AtomicMarkableReference<Node> next;
        public Node(int tag) {
            this.tag = tag;
        }
    }

    public boolean add(int tag) {
        while(true) {
            Window window = Window.find(head, tag);
            Node pred = window.pred, curr = window.curr;
            if(curr.tag == tag)
                return false;
            else {
                Node node = new Node(tag);
                node.next = new AtomicMarkableReference<>(curr, false);
                if(pred.next.compareAndSet(curr, node, false, false)) {
                    return true;
                }
            }
        }
    }

    public boolean remove(int tag) {
        boolean snip;
        while(true) {
            Window window = Window.find(head, tag);
            Node pred = window.pred, curr = window.curr;
            if(curr.tag != tag)
                return false;
            else {
                Node succ = curr.next.getReference();
                snip = curr.next.compareAndSet(succ, succ, false, true);
                if(!snip)
                    continue;
                pred.next.compareAndSet(curr, succ, false, false);
                return true;
            }
        }
    }

    public boolean contains(int tag) {
        Node curr = head;
        while(curr.tag < tag) {
            curr = curr.next.getReference();
        }
        return (curr.tag == tag && !curr.next.isMarked());
    }
    
    private static class Window {
        public Node pred, curr;
    
        Window(Node myPred, Node myCurr) {
            pred = myPred; curr = myCurr;
        }
    
        public static Window find(Node head, int tag) {
            Node pred = null, curr = null, succ = null;
            boolean[] marked = {false};
            boolean snip;
            retry: while(true) {
                pred = head;
                curr = pred.next.getReference();
                while(true) {
                    pred = head;
                    curr = pred.next.getReference();
                    while(true) {
                        succ = curr.next.get(marked);
                        while(marked[0]) {
                            snip = pred.next.compareAndSet(curr, succ, false, false);
                            if(!snip) continue retry;
                            curr = succ;
                            succ = curr.next.get(marked);
                        }
                        if(curr.tag >= tag)
                            return new Window(pred, curr);
                        pred = curr;
                        curr = succ;
                    }
                }
            }
        }
    }
}