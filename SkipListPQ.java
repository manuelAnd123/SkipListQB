import java.util.*;

class SkipListPQ {

    private double alpha;
    private Random rand;
    private Node head; //posizione iniziale (in alto a sx)
    private Node tail; //posizione finale del livello più alto (in alto a dx)
    private int height; //altezza della skip list
    private int size; //numero di entries nella skip list
    

    private static class Node {
        MyEntry entry;
        Node next, prev, above, below; //puntatori ai nodi adiacenti e ai nodi sopra e sotto

        //costruttrore dei nodi sentinella e dei nodi adiacenti
        Node(MyEntry entry, Node next, Node prev, Node above, Node below) {
            this.entry = entry;
            this.next = next;
            this.prev = prev;
            this.above = above;
            this.below = below;
        }
    }

    //costruttore della skip list con valori default
    public SkipListPQ(double alpha) {
        this.alpha = alpha;
        this.rand = new Random();
	    this.height = 0; 
        this.size = 0;                                                                               
        this.head = createLevel(); 
        this.tail = head.next;
        addLevel(); 
    }
    
    //funzione che crea un nuovo livello della skip list e ritorna il nodo sentinella sinistro
    private Node createLevel() {
        MyEntry negInf = new MyEntry(Integer.MIN_VALUE, null);
        MyEntry posInf = new MyEntry(Integer.MAX_VALUE, null); 
        
        Node leftSentinel = new Node(negInf, null, null, null, null);
        Node rightSentinel = new Node(posInf, null, leftSentinel, null, null);

        leftSentinel.next = rightSentinel;
        return leftSentinel;
    }

    //funzione che aggiunge un nuovo livello alla skip list
    private void addLevel() {
        Node newLevel = createLevel();
        newLevel.below = head;              //collego il nuovo livello con il livello precedente, aggiornando head e tail
        head.above = newLevel;              
        head = newLevel;                    
        
        head.next.below = tail;             
        tail.above = head.next;
        tail = head.next;

        height++;
    }

    //funzione che restituisce la dimensione della skip list
    public int size() {
	    return size;      
    }

    //funzione che restituisce il minimo valore della skip list e lo stampa
    public MyEntry min() {
        Node min = head;
        while (min.below != null) {         //scendo al livello più basso
            min = min.below;
        }
        min = min.next;                     //trovo il minimo nodo
        System.out.println(min.entry.toString());
        return min.entry;
    }

    //funzione che inserisce un nuovo valore nella skip list e ritorna il numero di nodi visitati
    public int insert(int key, String value) {
        MyEntry newEntry = new MyEntry(key, value);
        
        SearchResult searchResult = skipSearch(key);
        Node p = searchResult.node;
        int nodesVisited = searchResult.nodesVisited;
        
        int level = generateEll(alpha, key);  
        
        //aggiungo nuovi livelli finchè non supero il livello del nuovo nodo
        while (height <= level) {                       
            addLevel();
        }
        
        //inserisco il nuovo nodo in tutti i livelli, in base al risultato di generateEll
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
    
    //funzione che rimuove il minimo valore della skip list e restituisce la entry rimossa
    public MyEntry removeMin() {
        Node min = head;                    
        while (min.below != null) {
            min = min.below;
        }
        min = min.next;                     //trovo il minimo nodo
        MyEntry minEntry = min.entry;       //salvo la entry del minimo nodo

        int counter = 0;
        do {                                
            min.prev.next = min.next;       //rimuovo il nodo minimo e tutti i nodi al di sopra 
            min.next.prev = min.prev;       //e correggo i puntatori 
            if (min.above != null) {        
                min = min.above;
                counter++;
            }
            
        } while (min.above != null);
        
        //controllo se il livello è vuoto e lo rimuovo in modo da mantenere la struttura della skip list
        while (head.below.next == tail.below) {     
            head.below = head.below.below;          //ricollego i livelli
            tail.below = tail.below.below;          
            height--;
        }
        size--; 
        return minEntry;
    }

    //funzione che stampa la skip list
    public void print() {
        Node current = head;
        while (current.below != null) {
            current = current.below;        //scendo al livello più basso
        }
        current = current.next;             //mi posiziono sul primo nodo

        StringBuilder result = new StringBuilder();

        //Itero lungo la lista orizzontale finchè non arrivo al nodo sentinella di destra
        while (current.entry.getKey() != Integer.MAX_VALUE) {
            
            //Conto la grandezza del livello verticale
            int verticalSize = 0;
            Node temp = current;
            while (temp != null) {
                verticalSize++;             
                temp = temp.above;
            }
            //Aggiungo la chiave, il valore e la grandezza del livello verticale alla stringa di output
            result.append(current.entry.getKey())     
                .append(" ")
                .append(current.entry.getValue())
                .append(" ")
                .append(verticalSize)
                .append(", ");
            current = current.next;
        }

        //Rimuovo l'ultima virgola e lo spazio
        if (result.length() > 0) {
            result.setLength(result.length() - 2);
        }

        //Stampo la stringa di output
        System.out.println(result.toString());
    }

    //funzione che cerca un valore nella skip list e restitusce il numero di nodi visitati e il nodo con chiave maggiore a quella cercata
    private SearchResult skipSearch(int key) {
        Node p = head;
        int nodesVisited = 0;
        
        while (p.below != null) {
            p = p.below;
            nodesVisited++;
            
            //scannerizzo la lista orizzontale finchè non trovo una chiave maggiore a quella cercata
            while (p.next != null && key >= p.next.entry.getKey()) {
                p = p.next;
                nodesVisited++;
            }
        }
        SearchResult result = new SearchResult(p, nodesVisited);
        
        return result;
    }
    //helper function utilizzata in insert()                                                                                                                                                                                                                                                                                      
    private Node insertAfterAbove(Node p, Node below, MyEntry entry) {
        // Create the new node
        Node newNode = new Node(entry, p.next, p, null, below);

        //Aggiorno i puntatori dei nodi adiacenti
        if (p.next != null) {
            p.next.prev = newNode;
        }
        p.next = newNode;

        //Aggiorno i puntatori dei nodi sopra e sotto
        if (below != null) {
            below.above = newNode;
        }

        return newNode;
    }
    //helper class usata per restituire sia il nodo che il contatore dei nodi visitati nel metodo skipSearch()
    private static class SearchResult {
        Node node;
        int nodesVisited;

        SearchResult(Node node, int nodesVisited) {
            this.node = node;
            this.nodesVisited = nodesVisited;
        }
    }
    
}

