package layoutHandler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Collections2;
import geneticFunctions.*;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Layout {
    private String option;
    private int height;
    private int width;
    private int numberOfMachines;
    private HashMap<Integer, HashMap<Integer, Integer>>cost;
    private HashMap<Integer, HashMap<Integer, Integer>>flow;

    /**
     *
     * @param option available options(easy, flat, hard)
     * @param width width of layout
     * @param height height of layout
     * @param numberOfMachines number of machines to place
     */
    public Layout(String option, int height, int width, int numberOfMachines) {
        this.option = option;
        this.width = width;
        this.height = height;
        this.numberOfMachines = numberOfMachines;
        this.cost = new HashMap<Integer, HashMap<Integer, Integer>>();
        this.flow = new HashMap<Integer, HashMap<Integer, Integer>>();
        loadCost(option);
        loadFlow(option);
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getNumberOfMachines() {
        return numberOfMachines;
    }

    public void setNumberOfMachines(int numberOfMachines) {
        this.numberOfMachines = numberOfMachines;
    }

    public HashMap<Integer, HashMap<Integer, Integer>> getCost() {
        return cost;
    }

    public void setCost(HashMap<Integer, HashMap<Integer, Integer>> cost) {
        this.cost = cost;
    }

    public HashMap<Integer, HashMap<Integer, Integer>> getFlow() {
        return flow;
    }

    public void setFlow(HashMap<Integer, HashMap<Integer, Integer>> flow) {
        this.flow = flow;
    }

    public void loadFlow(String option){
        for (int i = 0; i < width * height; i++) {
            this.flow.put(i, new HashMap<Integer, Integer>());
        }

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            List<Flow> listFlow = objectMapper.readValue(new File("src/main/resources/"+option+"_flow.json"), new TypeReference<List<Flow>>(){});
            for (Flow flowElement: listFlow) {
                this.flow.get(flowElement.getSource()).put(flowElement.getDest(), flowElement.getAmount());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void loadCost(String option){
        for (int i = 0; i < width * height; i++) {
            this.cost.put(i, new HashMap<Integer, Integer>());
        }

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            List<Cost> listCost = objectMapper.readValue(new File("src/main/resources/"+option+"_cost.json"), new TypeReference<List<Cost>>(){});
            for (Cost costElement: listCost) {
                this.cost.get(costElement.getSource()).put(costElement.getDest(), costElement.getCost());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void printCostAndFlow(){
        System.out.println("\n"+option+":");
        System.out.println("\nFlow:");
        for (int i = 0; i < width * height; i++) {
            System.out.print(this.flow.keySet().toArray()[i]+": ");
            System.out.println(this.flow.get(this.flow.keySet().toArray()[i]));
        }

        System.out.println("\nCost:");
        for (int i = 0; i < width * height; i++) {
            System.out.print(this.cost.keySet().toArray()[i]+": ");
            System.out.println(this.cost.get(this.flow.keySet().toArray()[i]));
        }
    }

    public int objectiveFunction(List<Integer>filledLayout){
        int result = 0;
        int costValue;
        int flowValue;
        int distance;
        for(int i = 0; i < filledLayout.size(); i++){
            for(int j = 0; j < filledLayout.size(); j++){
                distance = Math.abs(i % this.width - j % this.width) + Math.abs((i / this.width) - (j / this.width));
                costValue = (filledLayout.get(i) == -1 || filledLayout.get(j) == -1 ||
                            this.cost.get(filledLayout.get(i)).get(filledLayout.get(j)) == null ||
                            this.cost.get(filledLayout.get(i)).get(filledLayout.get(j)) == -1) ?
                            0 : this.cost.get(filledLayout.get(i)).get(filledLayout.get(j));
                flowValue = (filledLayout.get(i) == -1 || filledLayout.get(j) == -1 ||
                            this.flow.get(filledLayout.get(i)).get(filledLayout.get(j)) == null ||
                            this.cost.get(filledLayout.get(i)).get(filledLayout.get(j)) == -1) ?
                            0 : this.flow.get(filledLayout.get(i)).get(filledLayout.get(j));
                result += distance * costValue * flowValue;
            }
        }

        return result;
    }

    public Collection<List<Integer>> generateRandomPopulation(int numberOfIndividuals){
        Collection<List<Integer>> population = new ArrayList<List<Integer>>();
        List<Integer> permutation;
        List<Integer> machines = new ArrayList<Integer>();
        int numberOfPermutationsToFind = numberOfIndividuals;
        for(int i = 0; i < (this.height * this.width); i++){
            if(i < this.numberOfMachines)
                machines.add(i);
            else
                machines.add(-1);
        }

        while(population.size() != numberOfIndividuals){
            for(int i = 0; i < numberOfPermutationsToFind; i++){
                permutation = machines.stream().collect(Collectors.toList());
                population.add(this.generateRandomPermutation(permutation));
            }
            int permutationsWithReps = population.size();
            population = population.stream().distinct().collect(Collectors.toList());
            int permutationsWithoutReps = population.size();
            numberOfPermutationsToFind = permutationsWithReps - permutationsWithoutReps;
        }

        return population;
    }

    public List<Integer> generateRandomPermutation(List<Integer> permutation){
        for(int i = 0; i < permutation.size()-1; i++){
            int j = (int) Math.floor(Math.random() * (permutation.size() - i)) + i;
            Collections.swap(permutation, i, j);
        }
        return permutation;
    }

    public Collection<List<Integer>> generateAllPermutations(){
        List<Integer> machines = new ArrayList<Integer>();
        for(int i = 0; i < (this.height * this.width); i++){
            if(i < this.numberOfMachines)
                machines.add(i);
            else
                machines.add(-1);
        }

        return Collections2.permutations(machines);
    }

    public int getOptimum(Collection<List<Integer>> population){
        int optimum = this.objectiveFunction(population.iterator().next());
        int cost;
        for (List<Integer> individual: population) {
            cost = this.objectiveFunction(individual);
            if(cost < optimum)
                optimum = cost;
        }
        return optimum;
    }

    public int[] getWorstAvgStd(Collection<List<Integer>> population){
        GeneticAlgorithm GA = new GeneticAlgorithm();
        List<Integer> objValues = GA.calculateObjectiveFunctions((ArrayList<List<Integer>>) population, this);
        int worst = (int) Collections.max(objValues);
        int avg = ((int) (objValues.stream().mapToInt(i -> i).average().orElse(0.0)));
        int std = GA.calculateStandardDeviation(objValues, avg);

        int[] worstAvgStd = new int[3];
        worstAvgStd[0] = worst;
        worstAvgStd[1] = avg;
        worstAvgStd[2] = std;

        return worstAvgStd;

    }
}
