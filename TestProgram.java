import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class TestProgram {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java TestProgram <file_path>");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(args[0]))) {
            String[] firstLine = br.readLine().split(" ");
            int N = Integer.parseInt(firstLine[0]);
            double alpha = Double.parseDouble(firstLine[1]);
            System.out.println(N + " " + alpha);

            SkipListPQ skipList = new SkipListPQ(alpha);
            
            int executedInsertCounter = 0;
            int entriesNumber = 0;
            int totalTraversedNodes = 0;

            for (int i = 0; i < N; i++) {
                String[] line = br.readLine().split(" ");
                int operation = Integer.parseInt(line[0]);

                switch (operation) {
                    case 0: //PRINT MINIMUM
                        if (skipList.size() > 0) {
                            skipList.min();
                        } else {
                            System.out.println("Skip list is empty. No minimum to print.");
                        }
                        break;
                        
                                    
                    case 1: //REMOVE MINIMUM
                        if (skipList.size() > 0) {
                            MyEntry removed = skipList.removeMin();
                            System.out.println("Removed minimum: " + removed.getKey() + " " + removed.getValue());
                        } else {
                            System.out.println("Skip list is empty. Cannot remove minimum.");
                        }
                        entriesNumber--;
                        break;
                    
                    case 2: //INSERT
                        int key = Integer.parseInt(line[1]);
                        String value = line[2];
                        int nodesVisited = skipList.insert(key, value);
                        System.out.println("Inserted (" + key + ", " + value + "), nodes visited: " + nodesVisited);
                        executedInsertCounter++;
                        entriesNumber++;
                        totalTraversedNodes = totalTraversedNodes + nodesVisited; 
                        break;
                        
                    case 3: //PRINT SKIPLIST
                         skipList.print();
                        break;
                    default:
                        System.out.println("Invalid operation code");
                        return;
                }
            }

        //prints alpha, # of entries, # of executed insert, avarage number of traversed nodes
        System.out.println(alpha + " " + entriesNumber + " " + executedInsertCounter + " " + totalTraversedNodes/executedInsertCounter); 
        
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }
}