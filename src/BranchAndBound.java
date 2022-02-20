import java.util.*;

//Branch And Bound implementation
public class BranchAndBound {
    private int numOfItems, knapsackWeight, maxVal, weight;
    private Item[] items;
    private boolean[] bestDecision;
    private PriorityQueue<Node> pq;
    private Node root;

    public class Node implements Comparable<Node> {
        boolean[] decision;
        int totalWeight;
        int totalValue;
        int index;
        float upperBound;

        //compare between two nodes according to upper bound
        public int compareTo(Node node) {
            if (this.upperBound == node.upperBound)
                return 0;
            else if (this.upperBound < node.upperBound)
                return 1;
            else
                return -1;
        }
    }

    public BranchAndBound(ArrayList<Item> items, int numOfItems, int knapsackWeight) {
        Item[] tempItems = new Item[numOfItems];

        this.items = new Item[numOfItems + 1];
        this.pq = new PriorityQueue<Node>();
        this.numOfItems = numOfItems;
        this.knapsackWeight = knapsackWeight;
        this.maxVal = 0;
        this.weight = 0;

        //sort by ratio
        System.arraycopy(items.toArray(), 0, tempItems, 0, numOfItems);
        Arrays.sort(tempItems, new RatioComparator());
        System.arraycopy(tempItems, 0, this.items, 1, numOfItems);
        this.items[0] = null;

        //init root
        root = new Node();
        root.totalWeight = 0;
        root.totalValue = 0;
        float rootRatio = (float)this.items[1].value / (float)this.items[1].weight;
        root.upperBound = knapsackWeight * rootRatio;
        root.index = 0;
        pq.add(root);
    }

    //The function calculates bound for a node
    private float bound(Node node) {
        float upperBound = node.totalValue;
        int i, totalWeight;
        totalWeight = node.totalWeight;

        if (node.totalWeight > knapsackWeight)
            return 0;
        else {
            i = node.index + 1;
            while(totalWeight <= knapsackWeight && i < numOfItems + 1) {
                totalWeight += items[i].weight;
                upperBound += items[i].value;
                i++;
            }
            if (i < numOfItems + 1) {
                float ratio = (float) items[i].value / (float) items[i].weight;
                float diffWeight = knapsackWeight - totalWeight;
                upperBound += diffWeight * ratio;
            }
            return upperBound;
        }
    }

    //The function adds a possible decision
    private boolean[] addDecision(boolean[] src, boolean[] dst, int i) {
        dst = new boolean[i+1];
        dst[i] = true;
        if (src != null)
            System.arraycopy(src, 0, dst, 0, src.length);
        return dst;
    }

    //The function copy the decision and return it
    private boolean[] copyDecision(boolean[] src) {
        boolean[] copyDec = null;
        if (src != null) {
            copyDec = new boolean[src.length];
            System.arraycopy(src, 0, copyDec, 0, src.length);
        }
        return copyDec;
    }

    //Branch and Bound process
    public void branchAndBound() {
        Node curr, left, right;
        while (pq.size() > 0) {
            curr = pq.poll();
            //continue exploring the children of the current node
            if (curr.upperBound > maxVal) {
                //the left child
                left = new Node();
                left.index = curr.index + 1;
                left.totalWeight = curr.totalWeight + items[left.index].weight;
                left.totalValue = curr.totalValue + items[left.index].value;
                left.decision = addDecision(curr.decision, left.decision, left.index);
                left.upperBound = bound(left);

                //check if we get better decision
                if (left.totalWeight <= knapsackWeight && left.totalValue > maxVal) {
                    maxVal = left.totalValue;
                    weight = left.totalWeight;
                    bestDecision = left.decision;
                }
                if (left.upperBound > maxVal)
                    pq.offer(left);

                //the right child
                right = new Node();
                right.index = curr.index + 1;
                right.totalWeight = curr.totalWeight;
                right.totalValue = curr.totalValue;
                right.decision = copyDecision(curr.decision);
                right.upperBound = bound(right);
                //check if we get better decision
                if (right.upperBound > maxVal)
                    pq.offer(right);
            }
        }
    }

    public void printSolution() {
        int totalWeight = 0;
        int totalValue = 0;
        System.out.println(" Item Weight Value");
        for (int i = 1; i < bestDecision.length; i++) {
            if(bestDecision[i]) {
                int index = items[i].index;
                System.out.printf("%4d %5d %5d\n", index+1, items[i].weight,
                        items[i].value);
                totalWeight += items[i].weight;
                totalValue += items[i].value;
            }
        }
        System.out.println("Total value: "+totalValue);
        System.out.println("Total weight: "+totalWeight);
    }

    public static void main(String args[]) {
        long startTime = System.nanoTime();
        BranchAndBound branchAndBound;
        ArrayList<Item> items = new ArrayList<Item>();

        int[] values = {91, 60, 61, 9, 79, 46, 19, 57, 8, 84, 58, 32, 43, 64, 98, 21, 11, 35, 78, 29};
        int[] weights = {29, 65, 71, 60, 45, 71, 22, 97, 6, 91, 1, 23, 43, 54, 11, 76, 22, 5, 2, 13};
        int numOfItems = 20;
        int knapsackWeight = 250;

        for (int i = 0; i < numOfItems; i++) {
            Item item = new Item(i + 1, values[i], weights[i]);
            items.add(item);
        }

        branchAndBound = new BranchAndBound(items, numOfItems, knapsackWeight);
        branchAndBound.branchAndBound();
        branchAndBound.printSolution();
        long endTime   = System.nanoTime();
        long totalTime = endTime - startTime;
        System.out.print("Duration: ");
        System.out.println((float)totalTime/1000000000);
    }
}

class Item implements Comparable<Item> {
    public int index;
    public int value;
    public int weight;

    public Item(int index, int value, int weight) {
        this.index = index;
        this.value = value;
        this.weight = weight;
    }

    public int compareTo(Item item) {
        if (this.index == item.index)
            return 0;
        else if (this.index < item.index)
            return -1;
        else
            return 1;
    }
}

class RatioComparator implements Comparator<Item> {
    public int compare(Item a, Item b) {
        float ratioA, ratioB;
        ratioA = (float)a.value / (float)a.weight;
        ratioB = (float)b.value / (float)b.weight;
        if (ratioA == ratioB)
            return 0;
        else
            return ratioA > ratioB ? -1 : 1;
    }
}