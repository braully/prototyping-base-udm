/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.braully.iaml;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.evaluation.NominalPrediction;
import weka.classifiers.functions.LinearRegression;
import weka.core.FastVector;
import weka.core.Instances;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

/**
 * Created by Sarika on 3/25/2015.
 */
//https://www.programcreek.com/java-api-examples/?code=sarikamm%2Fcodemitts%2Fcodemitts-master%2FIdeaProjects%2FTicTacToeTraining%2Fsrc%2Fcom%2Fcodemitts%2FWekaTest.java#
public class WekaTest {

    public static BufferedReader readDataFile(String filename) {
        BufferedReader inputReader = null;

        try {
            inputReader = new BufferedReader(new FileReader(filename));
        } catch (FileNotFoundException ex) {
            System.err.println("File not found: " + filename);
        }

        return inputReader;
    }

    public static Evaluation classify(Classifier model,
            Instances trainingSet, Instances testingSet) throws Exception {
        Evaluation evaluation = new Evaluation(trainingSet);

        model.buildClassifier(trainingSet);
        evaluation.evaluateModel(model, testingSet);

        return evaluation;
    }

    public static double calculateAccuracy(FastVector predictions) {
        double correct = 0;

        for (int i = 0; i < predictions.size(); i++) {
            NominalPrediction np = (NominalPrediction) predictions.elementAt(i);
            if (np.predicted() == np.actual()) {
                correct++;
            }
        }

        return 100 * correct / predictions.size();
    }

    public static Instances[][] crossValidationSplit(Instances data, int numberOfFolds) {
        Instances[][] split = new Instances[2][numberOfFolds];

        for (int i = 0; i < numberOfFolds; i++) {
            split[0][i] = data.trainCV(numberOfFolds, i);
            split[1][i] = data.testCV(numberOfFolds, i);
        }

        return split;
    }

    public void train(String filename) throws Exception {
        BufferedReader datafile = readDataFile(filename);

        Instances data = new Instances(datafile);
        data.setClassIndex(data.numAttributes() - 1);

        // Do 10-split cross validation
        Instances[][] split = crossValidationSplit(data, 10);

        // Separate split into training and testing arrays
        Instances[] trainingSplits = split[0];
        Instances[] testingSplits = split[1];

        // Use a set of classifiers
        Classifier[] models = {
            new LinearRegression(), //new SMOreg(),
        //new MultilayerPerceptron()
        //new J48(),
        //new PART(),
        //new DecisionTable(),//decision table majority classifier
        //new DecisionStump() //one-level decision tree
        };

        // Run for each model
        for (int j = 0; j < models.length; j++) {

            // Collect every group of predictions for current model in a FastVector
            FastVector predictions = new FastVector();

            // Print model
            System.out.println("Model:");

            // For each training-testing split pair, train and test the classifier
            for (int i = 0; i < trainingSplits.length; i++) {
                Evaluation validation = classify(models[j], trainingSplits[i], testingSplits[i]);
                System.out.println(validation.rootMeanSquaredError());

                try {
                    //predictions.appendElements(validation.predictions());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // Uncomment to see the summary for each training-testing pair.
                //System.out.println(models[j].toString());
            }

            System.out.println(models[j].toString());
            System.out.println("\n\n\n");

            // Calculate overall accuracy of current classifier on all splits
            //double accuracy = calculateAccuracy(predictions);
            // Print current classifier's name and accuracy in a complicated,
            // but nice-looking way.
            //System.out.println("Accuracy of " + models[j].getClass().getSimpleName() + ": "
            //      + String.format("%.2f%%", accuracy)
            //    + "\n---------------------------------");
        }

    }

}
