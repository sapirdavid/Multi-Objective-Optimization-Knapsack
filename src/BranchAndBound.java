import java.util.*;

//Branch And Bound implementation
public class BranchAndBound {
    private int numOfItems, knapsackWeight, weight;
    private float maxVal;
    private Item[] items;
    private boolean[] bestDecision;
    private PriorityQueue<Node> pq;
    private Node root;

    public class Node implements Comparable<Node> {
        boolean[] decision;
        int totalWeight;
        float totalValue;
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
        float combinedValue=(this.items[1].moneyValue+this.items[1].artisticValue)/2;
        float rootRatio = combinedValue / (float)this.items[1].weight;
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
                float combinedValue=(this.items[i].moneyValue+this.items[i].artisticValue)/2;
                upperBound +=combinedValue;
                i++;
            }
            if (i < numOfItems + 1) {
                float combinedValue=(this.items[i].moneyValue+this.items[i].artisticValue)/2;
                float ratio = combinedValue / (float) items[i].weight;
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
                left.totalValue = curr.totalValue + ((items[left.index].moneyValue +items[left.index].artisticValue)/2) ;
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
        float totalMoneyValue = 0;
        float totalArtisticValue = 0;
        float totalValues = 0;

        System.out.println(" Item Weight MoneyValue ArtisticValue");
        for (int i = 1; i < bestDecision.length; i++) {
            if(bestDecision[i]) {
                int index = items[i].index;
                System.out.printf("%4d %5d %8d %10d\n", index+1, items[i].weight,
                        items[i].moneyValue, items[i].artisticValue);
                totalWeight += items[i].weight;
                totalMoneyValue +=items[i].moneyValue;
                totalArtisticValue+=items[i].artisticValue;

            }
        }
        totalValues+=totalMoneyValue+totalArtisticValue;
        System.out.println("Total Money Value: "+totalMoneyValue);
        System.out.println("Total Artistic Value: "+totalArtisticValue);
        System.out.println("Total Values (combined):" +totalValues);
        System.out.println("Total weight: "+totalWeight);
    }

    public static void main(String args[]) {
        long startTime = System.nanoTime();
        BranchAndBound branchAndBound;
        ArrayList<Item> items = new ArrayList<Item>();

        int[] moneyValues = {91, 60, 61, 9, 79, 46, 19, 57, 8, 84, 58, 32, 43, 64, 98, 21, 11, 35, 78, 29};
        int[] artisticValues = {20, 12, 17, 9, 0, 20, 19, 30, 2, 42, 30, 5, 26, 2, 53, 4, 9, 5, 60, 2};
        int[] weights = {29, 65, 71, 60, 45, 71, 22, 97, 6, 91, 1, 23, 43, 54, 11, 76, 22, 5, 2, 13};
        int numOfItems = 20;
        int knapsackWeight = 250;

        for (int i = 0; i < numOfItems; i++) {
            Item item = new Item(i + 1, moneyValues[i],artisticValues[i], weights[i]);
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
    public int moneyValue;
    public int artisticValue;
    public int weight;

    public Item(int index, int moneyValue,int artisticValue, int weight) {
        this.index = index;
        this.moneyValue = moneyValue;
        this.artisticValue = artisticValue;
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
        float combinedValueA= (a.moneyValue+a.artisticValue)/2;
        float combinedValueB= (b.moneyValue+b.artisticValue)/2;
        ratioA = combinedValueA / (float)a.weight;
        ratioB = combinedValueB / (float)b.weight;
        if (ratioA == ratioB)
            return 0;
        else
            return ratioA > ratioB ? -1 : 1;
    }
}