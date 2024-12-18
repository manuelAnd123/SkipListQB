import java.util.*;

class SkipListPQ {

    private double alpha;
    private Random rand;
    private Node head; //posizione iniziale (in alto a sx)
    private Node tail;
    private int height;
    private int size; //numero di entries nella skip list
    

    private static class Node {
        MyEntry entry;
        Node next, prev, above, below; //pointers

        //costruttrore dei nodi sentinella e dei nodi adiacenti
        Node(MyEntry entry, Node next, Node prev, Node above, Node below) {
            this.entry = entry;
            this.next = next;
            this.prev = prev;
            this.above = above;
            this.below = below;
        }
    }


    public SkipListPQ(double alpha) {
        this.alpha = alpha;
        this.rand = new Random();
	    this.height = 0; //altezza default
        this.size = 0;   //grandezza default                                                                            
        this.head = createLevel();
        this.tail = head.next;
        addLevel(); 
    }

    private Node createLevel() {
        MyEntry negInf = new MyEntry(Integer.MIN_VALUE, null);
        MyEntry posInf = new MyEntry(Integer.MAX_VALUE, null); 
        
        Node leftSentinel = new Node(negInf, null, null, null, null);
        Node rightSentinel = new Node(posInf, null, leftSentinel, null, null);

        leftSentinel.next = rightSentinel;
        return leftSentinel;
    }

    private void addLevel() {
        Node newLevel = createLevel();
        newLevel.below = head;
        head.above = newLevel;
        head = newLevel;
        
        head.next.below = tail;
        tail.above = head.next;
        tail = head.next;

        height++;
    }

    public int size() {
	    return size;      
    }

    public MyEntry min() {
        Node min = head;
        while (min.below != null) {
            min = min.below;
        }
        min = min.next;
        System.out.println(min.entry.toString());
        return min.entry;
    }

    public int insert(int key, String value) {
        MyEntry newEntry = new MyEntry(key, value);
        
        SearchResult searchResult = SkipSearch(key);
        Node p = searchResult.node;
        int nodesVisited = searchResult.nodesVisited;
        
        int level = generateEll(alpha, key);
        
        while (height <= level) {
            addLevel();
        }
        Node below = null;
        for (int i = 0; i <= level; i++) {                                                                                                                                 
            Node newNode = insertAfterAbove(p, below, newEntry);
            below = newNode;

            while (p.above == null && p.prev != null) {                            
                p = p.prev;
            }
            p = p.above;
        }
        size++;
        return nodesVisited;
    }

    private int generateEll(double alpha_ , int key) {
        int level = 0;
        if (alpha_ >= 0. && alpha_< 1) {
            while (rand.nextDouble() < alpha_) {
                level += 1;
            }
        }
        else{
            while (key != 0 && key % 2 == 0){
                key = key / 2;
                level += 1;
            }
        }
        return level;
    }   
    
    public MyEntry removeMin() {
        Node min = head;
        while (min.below != null) {
            min = min.below;
        }
        min = min.next;
        MyEntry minEntry = min.entry;

        int counter = 0;
        do { 
            min.prev.next = min.next;
            min.next.prev = min.prev;
            if (min.above != null) {
                min = min.above;
                counter++;
            }
            
        } while (min.above != null);
        while (head.below.next == tail.below) {
            head.below = head.below.below;  //rimuovo il livello e ricollego i nodi             //controlla che head e tail siano corrette
            tail.below = tail.below.below;
            height--;
        }
        size--; 
        return minEntry;
    }

    public void print() {
        // Start from the first node in the bottom-most level
        Node current = head;
        while (current.below != null) {
            current = current.below; // Move to the bottom-most level
        }
        current = current.next; // Skip the left sentinel node

        StringBuilder result = new StringBuilder();

        // Traverse the bottom-most level
        while (current.entry.getKey() != Integer.MAX_VALUE) { // Stop at the right sentinel
            // Count the size of the vertical list for the current entry
            int verticalSize = 0;
            Node temp = current;
            while (temp != null) {
                verticalSize++;
                temp = temp.above;
            }
            // Append the entry and its vertical size to the result
            result.append(current.entry.getKey())     
                .append(" ")
                .append(current.entry.getValue())
                .append(" ")
                .append(verticalSize)
                .append(", ");
            current = current.next;
    }

    // Remove the trailing comma and space, if present
    if (result.length() > 0) {
        result.setLength(result.length() - 2);
    }

    // Print the result
    System.out.println(result.toString());
    }

    //search algorithm used in this skiplist
    private SearchResult SkipSearch(int key) {
        Node p = head;
        int nodesVisited = 0;
        
        while (p.below != null) {
            p = p.below;
            nodesVisited++;
            // Scan forward at the current level until key of next position is greater than the target key
            while (key >= p.next.entry.getKey()) {
                p = p.next;
                nodesVisited++;
            }
        }
        SearchResult result = new SearchResult(p, nodesVisited);
        
        return result;
    }
    //helper function used in insert()                                                                                                                                                                                                                                                                                      
    private Node insertAfterAbove(Node p, Node below, MyEntry entry) {
        // Create the new node
        Node newNode = new Node(entry, p.next, p, null, below);

        // Update pointers for the current level
        if (p.next != null) {
            p.next.prev = newNode;
        }
        p.next = newNode;

        // Link with the node below, if provided
        if (below != null) {
            below.above = newNode;
        }

        return newNode;
    }
    //helper class used to return both the node and the count in the SkipSearch() method
    private static class SearchResult {
        Node node;
        int nodesVisited;

        SearchResult(Node node, int nodesVisited) {
            this.node = node;
            this.nodesVisited = nodesVisited;
        }
    }
    
}

