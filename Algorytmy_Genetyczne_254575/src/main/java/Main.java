import com.google.common.collect.Lists;
import layoutHandler.*;
import geneticFunctions.*;

import java.time.Clock;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class Main {

    public static void main(String[] args){
        long before = System.currentTimeMillis();
        runTests();
        long after = System.currentTimeMillis();
        System.out.println("Czas wykonywania testów: " + (double)(after-before)/1000d + "s");

        GeneticAlgorithm GA = new GeneticAlgorithm();

        Layout easyLayout = new Layout("easy", 3, 3, 9);
        Collection<List<Integer>> randomEasyPopulation = easyLayout.generateRandomPopulation(500);

        GA.stepByStepGeneticAlgorithm(easyLayout, (ArrayList<List<Integer>>) randomEasyPopulation, 0.8d, 0.8d,
                "tournament", 10, 40);

        Layout flatLayout = new Layout("flat", 1, 12, 12);
        Collection<List<Integer>> randomFlatPopulation = flatLayout.generateRandomPopulation(500);
        Collection<List<Integer>> randomSmallFlatPopulation = flatLayout.generateRandomPopulation(100);

        GA.stepByStepGeneticAlgorithm(flatLayout, (ArrayList<List<Integer>>) randomFlatPopulation, 0.8d, 0.8d,
                "tournament", 20, 50);
        GA.stepByStepGeneticAlgorithm(flatLayout, (ArrayList<List<Integer>>) randomSmallFlatPopulation, 0.8d, 0.8d,
                "roulette", 50, 50);

        Layout hardLayout = new Layout("hard", 5, 6, 24);
        Collection<List<Integer>> randomHardPopulation = hardLayout.generateRandomPopulation(1000);
        GA.stepByStepGeneticAlgorithm(hardLayout, (ArrayList<List<Integer>>) randomHardPopulation, 1d, 0.3d,
                "tournament", 100, 25);



        Collection<List<Integer>> randomRHardPopulation = hardLayout.generateRandomPopulation(1000);
        int randomRHardOptimum = hardLayout.getOptimum(randomRHardPopulation);
        System.out.println("Random hard optimum C = " + randomRHardOptimum);
        int[] worstAvgStd;
        worstAvgStd = hardLayout.getWorstAvgStd(randomRHardPopulation);
        System.out.println(worstAvgStd[0] +" "+ worstAvgStd[1] +" "+ worstAvgStd[2]);

    }

    private static void runTests() {

        GeneticAlgorithm GA = new GeneticAlgorithm();
        Layout easyLayout = new Layout("easy", 3, 3, 9);
        //optimum C=4818
        Collection<List<Integer>> easyPopulation = easyLayout.generateAllPermutations();
        int easyOptimum = easyLayout.getOptimum(easyPopulation);
        System.out.println("Easy Optimum C = " + easyOptimum);

        Collection<List<Integer>> randomEasyPopulation = easyLayout.generateRandomPopulation(500);
        Collection<List<Integer>> randomEasyPopulationForRoulette = easyLayout.generateRandomPopulation(200);
        int randomEasyOptimum = easyLayout.getOptimum(randomEasyPopulation);
        System.out.println("Random easy optimum C = " + randomEasyOptimum);

        List<Integer> optimalIndividual = GA.stepByStepGeneticAlgorithm(easyLayout, (ArrayList<List<Integer>>) randomEasyPopulation,
                0.8d, 0.8d, "tournament", 15, 20);
        System.out.println("\nGenetically generated easy optimal layout: " + optimalIndividual);
        System.out.println("Genetically generated easy optimum:" + easyLayout.objectiveFunction(optimalIndividual));

        optimalIndividual = GA.stepByStepGeneticAlgorithm(easyLayout, (ArrayList<List<Integer>>) randomEasyPopulationForRoulette,
                0.8d, 0.8d, "roulette", 50, 0);
        System.out.println("\nGenetically generated easy optimal layout: " + optimalIndividual);
        System.out.println("Genetically generated easy optimum: " + easyLayout.objectiveFunction(optimalIndividual));

        Layout flatLayout = new Layout("flat", 1, 12, 12);
        //optimum C=11055 liczy kilkanascie minut
        Collection<List<Integer>> randomFlatPopulation = flatLayout.generateRandomPopulation(1000);
        int randomFlatOptimum = flatLayout.getOptimum(randomFlatPopulation);
        System.out.println("Random flat optimum C = " + randomFlatOptimum);

        optimalIndividual = GA.stepByStepGeneticAlgorithm(flatLayout, (ArrayList<List<Integer>>) randomFlatPopulation,
                0.8d, 0.8d, "tournament", 10, 100);
        System.out.println("\nGenetically generated flat optimal layout: " + optimalIndividual);
        System.out.println("Genetically generated flat optimum: " + flatLayout.objectiveFunction(optimalIndividual));



        Layout hardLayout = new Layout("hard", 5, 6, 24);
        //optimum C=20210(najlepsze jakie znalazlem)
        Collection<List<Integer>> randomHardPopulation = hardLayout.generateRandomPopulation(1000);
        int randomHardOptimum = hardLayout.getOptimum(randomHardPopulation);
        System.out.println("Random hard optimum C = " + randomHardOptimum);

        optimalIndividual = GA.stepByStepGeneticAlgorithm(hardLayout, (ArrayList<List<Integer>>) randomHardPopulation,
                0.8d, 0.8d, "tournament", 10, 50);
        System.out.println("\nGenetically generated hard optimal layout: " + optimalIndividual);
        System.out.println("Genetically generated hard optimum: " + hardLayout.objectiveFunction(optimalIndividual));

        optimalIndividual = GA.stepByStepGeneticAlgorithm(hardLayout, (ArrayList<List<Integer>>) randomHardPopulation,
                0.8d, 0.8d, "tournament", 20, 50);
        System.out.println("\nGenetically generated hard optimal layout: " + optimalIndividual);
        System.out.println("Genetically generated hard optimum: " + hardLayout.objectiveFunction(optimalIndividual));
    }
}