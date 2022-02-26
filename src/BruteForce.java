//Brute Force implementation
public class BruteForce {
    private static Item[] items;
    private int numOfItems;
    private int knapsackWeight;
    private float bestValue;
    private boolean bestDecision[];
    private boolean currentDecision[];

    public BruteForce(int numOfItems, int knapsackWeight) {
        this.numOfItems = numOfItems;
        this.knapsackWeight = knapsackWeight;

        bestValue =Float.MIN_VALUE;
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
            float totalValue = 0;
            for (int i = 0; i < numOfItems; i++) {
                if (currentDecision[i]) {
                    totalWeight += items[i].weight();
                    totalValue += (items[i].moneyValue()+items[i].artisticValue())/2;
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
        float totalMoneyValue = 0;
        float totalArtisticValue = 0;
        float totalValues = 0;
        System.out.println(" Item Weight MoneyValue ArtisticValue");
        for (int i = 0; i < numOfItems; i++) {
            if (bestDecision[i]) {
                System.out.printf("%4d %5d %8d %11d\n", i+1, items[i].weight(),
                        items[i].moneyValue(),  items[i].artisticValue());
                totalWeight +=  items[i].weight();
                totalMoneyValue +=items[i].moneyValue();
                totalArtisticValue+=items[i].artisticValue();
            }
        }
        totalValues+=totalMoneyValue+totalArtisticValue;
        System.out.println("Total Money Value: "+totalMoneyValue);
        System.out.println("Total Artistic Value: "+totalArtisticValue);
        System.out.println("Total Values (combined):" +totalValues);
        System.out.println("Total weight: "+totalWeight);

    }

    //The function updates the best decision if the current is better
    public void updateBestDecision() {
        for (int i = 0; i < numOfItems; i++)
            bestDecision[i] = currentDecision[i];
    }

    public static class Item {
        public int moneyValue;
        public int artisticValue;
        public int weight;

        public Item(int moneyValue,int artisticValue, int weight) {
            this.moneyValue = moneyValue;
            this.artisticValue = artisticValue;
            this.weight = weight;
        }
        public int weight() { return weight; }
        public int moneyValue() { return moneyValue; }
        public int artisticValue() { return artisticValue; }

    }

    public static void main(String[] args) {
        long startTime = System.nanoTime();
        int[] moneyValues = {91, 60, 61, 9, 79, 46, 19, 57, 8, 84, 58, 32, 43, 64, 98, 21, 11, 35, 78, 29};
        int[] artisticValues = {20, 12, 17, 9, 0, 20, 19, 30, 2, 42, 30, 5, 26, 2, 53, 4, 9, 5, 60, 2};
        int[] weights = {29, 65, 71, 60, 45, 71, 22, 97, 6, 91, 1, 23, 43, 54, 11, 76, 22, 5, 2, 13};
        int numOfItems = 20;
        int knapsackWeight = 250;

        items = new Item[numOfItems];

        for (int i = 0; i < numOfItems; i++) {
            Item item = new Item(moneyValues[i],artisticValues[i], weights[i]);
            items[i] = item;
        }
        new BruteForce(numOfItems,knapsackWeight);
        long endTime   = System.nanoTime();
        long totalTime = endTime - startTime;
        System.out.print("Duration: ");
        System.out.println((float)totalTime/1000000000);
    }
}
