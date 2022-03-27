package geneticFunctions;

import layoutHandler.*;

import java.util.*;

public class GeneticAlgorithm {

    public List<Integer> stepByStepGeneticAlgorithm(Layout layout, ArrayList<List<Integer>> population, Double crossoverProbability,
                                                    Double mutationProbability, String selectionOption, int stopCondition, int tournamentSize) {
        ArrayList<List<Integer>> tempPopulation = new ArrayList<>(population);
        ArrayList<List<Integer>> newPopulation = new ArrayList<>();
        List<Integer> optimalIndividual = tempPopulation.get(0);
        List<Integer> parentOne;
        List<Integer> parentTwo;
        List<Integer> childOne;
        List<Integer> childTwo;
        List<List<Integer>> children;
        List<List<Integer>> objectiveFunctionsValues = new ArrayList<>();
        int potentialOptimum;
        int counter = 1;
        List<Integer> populationOptimums = new ArrayList<>();
        List<Integer> populationAverages = new ArrayList<>();
        List<Integer> populationWorsts = new ArrayList<>();
        List<Integer> standardDeviations = new ArrayList<>();

        int optimum = layout.getOptimum(population);
        objectiveFunctionsValues.add(calculateObjectiveFunctions(population, layout));
        populationOptimums.add(Collections.min(objectiveFunctionsValues.get(0)));
        populationAverages.add((int) (objectiveFunctionsValues.get(0)).stream().mapToInt(i -> i).average().orElse(0.0));
        populationWorsts.add(Collections.max(objectiveFunctionsValues.get(0)));
        List<Integer> populationObjectiveFunctions;


        while (counter != stopCondition) {
            populationObjectiveFunctions = calculateObjectiveFunctions(tempPopulation, layout);
            objectiveFunctionsValues.add(populationObjectiveFunctions);
            while (newPopulation.size() < population.size()) {
                if (selectionOption == "roulette") {
                    parentOne = rouletteSelection(layout, tempPopulation, populationObjectiveFunctions);
                    parentTwo = rouletteSelection(layout, tempPopulation, populationObjectiveFunctions);
                } else {
                    parentOne = tournamentSelection(layout, tournamentSize, tempPopulation, populationObjectiveFunctions);
                    parentTwo = tournamentSelection(layout, tournamentSize, tempPopulation, populationObjectiveFunctions);
                }
                if (Math.random() < crossoverProbability) {
                    children = crossover(parentOne, parentTwo);
                    childOne = children.get(0);
                    childTwo = children.get(1);
                } else {
                    childOne = parentOne;
                    childTwo = parentTwo;
                }
                mutation(childOne, mutationProbability);
                mutation(childTwo, mutationProbability);
                newPopulation.add(childOne);
                newPopulation.add(childTwo);

                potentialOptimum = layout.objectiveFunction(childOne);
                if (potentialOptimum < optimum) {
                    optimum = potentialOptimum;
                    optimalIndividual = new ArrayList<>(childOne);
                }
                potentialOptimum = layout.objectiveFunction(childTwo);
                if (potentialOptimum < optimum) {
                    optimum = potentialOptimum;
                    optimalIndividual = new ArrayList<>(childTwo);
                }
            }
            System.out.print(optimum + " ");
            tempPopulation.clear();
            populationOptimums.add(optimum);
            populationAverages.add((int) (objectiveFunctionsValues.get(counter)).stream().mapToInt(i -> i).average().orElse(0.0));
            populationWorsts.add(Collections.max(objectiveFunctionsValues.get(counter)));
            standardDeviations.add(calculateStandardDeviation(objectiveFunctionsValues.get(counter), populationAverages.get(counter)));

            populationObjectiveFunctions.clear();
            tempPopulation = new ArrayList<>(newPopulation);
            newPopulation.clear();
            counter++;
        }

        GeneticChart GC = new GeneticChart(populationOptimums, populationWorsts, populationAverages);

        System.out.println("\n" + "Optymalny osobnik: " + optimalIndividual);
        System.out.println("\n" + "Znalezione optimum: " + optimum);
        return optimalIndividual;
    }

    public List<Integer> tournamentSelection(Layout layout, int tournamentSize, ArrayList<List<Integer>> population, List<Integer> objectiveFunctionsValues) {
        int optimum = 0;
        int indexOfOptimum = 0;
        HashSet<Integer> randomIndexes = new HashSet<>(0);
        while (randomIndexes.size() != tournamentSize) {
            randomIndexes.add((int) (Math.random() * (population.size())));
        }

        List<Integer> tournament = randomIndexes.stream().toList();
        optimum = objectiveFunctionsValues.get(0);

        for (int i = 0; i < tournamentSize; i++) {
            int potentialOptimum = objectiveFunctionsValues.get(tournament.get(i));
            if (potentialOptimum < optimum) {
                optimum = potentialOptimum;
                indexOfOptimum = tournament.get(i);
            }
        }
        return population.get(indexOfOptimum);
    }

    public List<Integer> rouletteSelection(Layout layout, ArrayList<List<Integer>> population, List<Integer> objectiveFunctionsValues) {
        double invertedObjectiveFunctionsSum = 0;
        int chosenIndex = 0;


        for (int i = 0; i < population.size(); i++) {
            invertedObjectiveFunctionsSum += 1d / objectiveFunctionsValues.get(i);
        }

        Double drawnValue = Math.random() * invertedObjectiveFunctionsSum;
        double previous = 1d / objectiveFunctionsValues.get(0);
        for (int i = 1; i < population.size(); i++) {
            if (previous > drawnValue) {
                chosenIndex = i - 1;
                break;
            }
            previous += 1d / objectiveFunctionsValues.get(i);
        }
        return population.get(chosenIndex);
    }

    public void mutation(List<Integer> individual, double mutationProbability) {
        Random random = new Random();
        if (mutationProbability >= Math.random()) {
            Collections.swap(individual, individual.indexOf(individual.get(random.nextInt(0, individual.size()))),
                    individual.indexOf(individual.get(random.nextInt(0, individual.size()))));
        }
    }

    public List<List<Integer>> crossover(List<Integer> firstParent, List<Integer> secondParent) {
        List<List<Integer>> children = new ArrayList<List<Integer>>();
        int numberOfEmptyPlacesC1 = Collections.frequency(firstParent, -1);
        int numberOfEmptyPlacesC2 = numberOfEmptyPlacesC1;
        children.add(new ArrayList<>());
        children.add(new ArrayList<>());
        int crossPoint = (int) (Math.random() * (firstParent.size() + 1));
        for (int i = 0; i < crossPoint; i++) {
            children.get(0).add(firstParent.get(i));
            children.get(1).add(secondParent.get(i));
        }
        numberOfEmptyPlacesC1 -= Collections.frequency(children.get(0), -1);
        numberOfEmptyPlacesC2 -= Collections.frequency(children.get(1), -1);
        for (int i = 0; i < firstParent.size(); i++) {
            if (secondParent.get(i) == -1) {
                if (numberOfEmptyPlacesC1 > 0) {
                    children.get(0).add(-1);
                    numberOfEmptyPlacesC1--;
                }
            } else if (!children.get(0).contains(secondParent.get(i))) {
                children.get(0).add(secondParent.get(i));
            }
            if (firstParent.get(i) == -1) {
                if (numberOfEmptyPlacesC2 > 0) {
                    children.get(1).add(-1);
                    numberOfEmptyPlacesC2--;
                }
            } else if (!children.get(1).contains(firstParent.get(i))) {
                children.get(1).add(firstParent.get(i));
            }
        }

        return children;
    }

    public List<Integer> calculateObjectiveFunctions(ArrayList<List<Integer>> population, Layout layout) {
        List<Integer> objectiveFunctions = new ArrayList<>();
        for (List<Integer> individual : population) {
            objectiveFunctions.add(layout.objectiveFunction(individual));
        }
        return objectiveFunctions;
    }

    public int calculateStandardDeviation(List<Integer> values, Integer mean) {
        double numerator = 0;
        for (int i = 0; i < values.size(); i++) {
            numerator +=  Math.pow((values.get(i) - mean), 2);
        }
        return (int) Math.sqrt(numerator / values.size());
    }
}
