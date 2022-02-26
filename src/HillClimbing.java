import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

//Hill Climbing implementation
public class HillClimbing {
    Neighbours neighboursOptions;
    Evaluator evaluator;
    State initState;

    public HillClimbing(Neighbours neighbours, Evaluator eval, State initState) {
        this.neighboursOptions = neighbours;
        this.evaluator = eval;
        this.initState = initState;
    }

    //The function performs the climb process to improve at each iteration
    public State climbProcess() {
        State currState = initState;
        while(true) {
            List<State> neighbours = neighboursOptions.getNeighbours(currState);
            float nextEval = Integer.MIN_VALUE;
            State nextState = null;
            //find the best neighbor
            for(State neighbour : neighbours) {
                float eval = evaluator.eval(neighbour);
                if (eval > nextEval) {
                    nextEval = eval;
                    nextState = neighbour;
                }
            }
            if (nextEval <= evaluator.eval(currState)) {
                //if no better neighbour was found return the current state
                System.out.println("no better neighbour was found");
                return currState;
            }
            //if better neighbour was found return him
            System.out.println("better neighbour was found: (Eval: " + nextEval + ")");
            currState = nextState;
        }
    }

    public static void printSolution(State optimumState, int[] moneyValues,int [] artisticValues, int[] weights) {
        int totalWeight = 0;
        float totalMoneyValue = 0;
        float totalArtisticValue = 0;
        float totalValues = 0;
        System.out.println(" Item Weight MoneyValue ArtisticValue");
        for (int i = 0; i < optimumState.getDecisions().length; i++) {
            if(optimumState.getDecisions()[i]) {
                System.out.printf("%4d %5d %8d %10d\n", i+1, weights[i],
                        moneyValues[i],artisticValues[i]);
                totalWeight += weights[i];
                totalMoneyValue +=moneyValues[i];
                totalArtisticValue+=artisticValues[i];
            }
        }
        totalValues+=totalMoneyValue+totalArtisticValue;
        System.out.println("Total Money Value: "+totalMoneyValue);
        System.out.println("Total Artistic Value: "+totalArtisticValue);
        System.out.println("Total Values (combined):" +totalValues);
        System.out.println("Total weight: "+totalWeight);

    }


    public static void main(String[] args) {
        long startTime = System.nanoTime();
        int[] moneyValues = {91, 60, 61, 9, 79, 46, 19, 57, 8, 84, 58, 32, 43, 64, 98, 21, 11, 35, 78, 29};
        int[] artisticValues = {20, 12, 17, 9, 0, 20, 19, 30, 2, 42, 30, 5, 26, 2, 53, 4, 9, 5, 60, 2};
        int[] weights = {29, 65, 71, 60, 45, 71, 22, 97, 6, 91, 1, 23, 43, 54, 11, 76, 22, 5, 2, 13};
        int numOfItems = 20;
        int knapsackWeight = 250;
        Random rnd = new Random();
        Evaluator evaluator = new Evaluator(weights,moneyValues,artisticValues,knapsackWeight);
        Neighbours neighbours = new Neighbours();

        // initialize random starting state
        boolean[] initialDecisions = new boolean[numOfItems];
        for (int i = 0; i < numOfItems; i++) {
            initialDecisions[i] = rnd.nextBoolean();
        }
        State initialState = new State(initialDecisions);

        HillClimbing hillClimbing = new HillClimbing(neighbours,evaluator,initialState);
        State optimumState = hillClimbing.climbProcess();
        printSolution(optimumState, moneyValues,artisticValues, weights);
        long endTime   = System.nanoTime();
        long totalTime = endTime - startTime;
        System.out.print("Duration: ");
        System.out.println((float)totalTime/1000000000);


    }
}

class Neighbours {
    /*
    The function calculates the possible neighbors:
    Thus, if there are 10 possible objects, the string 0010010000 means to include the 3rd and 6th objects only.
    For this scenario, there are always 10 possible neighbors (1010010000, 0110010000, 0000010000, etc.).
     */
    public List<State> getNeighbours(State currState) {
        ArrayList<State> statesList = new ArrayList<>();
        boolean[] decisions = currState.getDecisions();

        for (int i = 0; i < decisions.length; i++) {
            State copyCurrState = currState.copyState();
            if (copyCurrState.getDecisions()[i]) {
                copyCurrState.getDecisions()[i] = false;
            } else {
                copyCurrState.getDecisions()[i] = true;
            }
            statesList.add(copyCurrState);
        }

        for (int i = 0; i < decisions.length; i++) {
            for (int j = i+1; j < decisions.length; j++) {
                if(decisions[i] != decisions[j]) {
                    State copyCurrState = currState.copyState();
                    if (copyCurrState.getDecisions()[i]) {
                        copyCurrState.getDecisions()[i] = false;
                    } else {
                        copyCurrState.getDecisions()[i] = true;
                    }
                    if (copyCurrState.getDecisions()[j]) {
                        copyCurrState.getDecisions()[j] = false;
                    } else {
                        copyCurrState.getDecisions()[j] = true;
                    }
                    statesList.add(copyCurrState);
                }
            }
        }
        return statesList;
    }
}

class Evaluator {
    int[] weights;
    int[] moneyValues;
    int[] artisticValues;
    int knapsackWeight;

    public Evaluator(int[] weights, int[] moneyValues,int[] artisticValues,  int knapsackWeight) {
        this.weights = weights;
        this.moneyValues = moneyValues;
        this.artisticValues = artisticValues;
        this.knapsackWeight = knapsackWeight;
    }
    //The function evaluates the current state and later decides if it is a better solution
    public float eval(State currentState) {
        int totalWeight = 0;
        float totalValue = 0;
        boolean[] decisions = currentState.getDecisions();
        for (int i = 0; i < decisions.length; i++) {
            if(decisions[i]) {
                totalWeight += weights[i];
                totalValue += (moneyValues[i]+artisticValues[i])/2;
            }
        }
        int diffWeights = knapsackWeight - totalWeight;
        if (diffWeights > 0) {
            return totalValue;
        } else {
            return diffWeights;
        }
    }
}

class State {
    private boolean[] decisions;
    public State(boolean[] decisions) {
        this.decisions = decisions;
    }
    public boolean[] getDecisions() {return decisions;}
    //The function create a copy of the current state
    public State copyState() {
        return new State(Arrays.copyOf(decisions,decisions.length));
    }
}



