//Brute Force implementation
public class BruteForce {
    private static Item[] items;
    private int numOfItems;
    private int knapsackWeight;
    private int bestValue;
    private boolean bestDecision[];
    private boolean currentDecision[];

    public BruteForce(int numOfItems, int knapsackWeight) {
        this.numOfItems = numOfItems;
        this.knapsackWeight = knapsackWeight;

        bestValue = Integer.MIN_VALUE;
        bestDecision = new boolean[numOfItems];
        currentDecision = new boolean[numOfItems];

        calculateDecision(numOfItems-1);
        printSolution();
    }

    //The function calculate the decision so far
    public void calculateDecision(int numCurrItems) {
        //if all items were checked
        if (numCurrItems < 0) {
            int totalWeight = 0;
            int totalValue = 0;
            for (int i = 0; i < numOfItems; i++) {
                if (currentDecision[i]) {
                    totalWeight += items[i].weight();
                    totalValue += items[i].value();
                }
            }
            //if we got a better solution
            if (totalWeight <= knapsackWeight && totalValue > bestValue) {
                bestValue = totalValue;
                updateBestDecision();
            }
            return;
        }
        //check all options
        currentDecision[numCurrItems] = true;
        calculateDecision(numCurrItems-1);

        currentDecision[numCurrItems] = false;
        calculateDecision(numCurrItems-1);
    }

    public void printSolution() {
        int totalWeight = 0;
        System.out.println(" Item Weight Value");
        for (int i = 0; i < numOfItems; i++) {
            if (bestDecision[i]) {
                System.out.printf("%4d %5d %5d\n", i+1, items[i].weight(),
                        items[i].value());
                totalWeight += items[i].weight();
            }
        }
        System.out.println("Total value: "+bestValue);
        System.out.println("Total weight: "+totalWeight);
    }

    //The function updates the best decision if the current is better
    public void updateBestDecision() {
        for (int i = 0; i < numOfItems; i++)
            bestDecision[i] = currentDecision[i];
    }

    public static class Item {
        private int value, weight;

        public Item(int value, int weight) {
            this.value = value;
            this.weight = weight;
        }

        public int weight() { return weight; }
        public int value() { return value; }
    }

    public static void main(String[] args) {
        long startTime = System.nanoTime();
        int[] values = {91, 60, 61, 9, 79, 46, 19, 57, 8, 84, 58, 32, 43, 64, 98, 21, 11, 35, 78, 29};
        int[] weights = {29, 65, 71, 60, 45, 71, 22, 97, 6, 91, 1, 23, 43, 54, 11, 76, 22, 5, 2, 13};
        int numOfItems = 20;
        int knapsackWeight = 250;

        items = new Item[numOfItems];

        for (int i = 0; i < numOfItems; i++) {
            Item item = new Item(values[i], weights[i]);
            items[i] = item;
        }
        new BruteForce(numOfItems,knapsackWeight);
        long endTime   = System.nanoTime();
        long totalTime = endTime - startTime;
        System.out.print("Duration: ");
        System.out.println((float)totalTime/1000000000);
    }
}
