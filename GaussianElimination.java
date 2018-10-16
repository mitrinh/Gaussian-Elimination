/**
 * Michael Trinh
 * CS 3010
 * Programming Project 1
 * Description: Write a program that asks the user for the number of linear 
 *                  equations to solve using 3 methods. 
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;

public class GaussianElimination {
    // creates a long start, end, and total time variable to time an alg.
    int numOfLinearEq;
    float[][] matrix;
    Scanner input;
    
    // constructor: initializes matrix essentially
    public GaussianElimination() throws FileNotFoundException{
        input = new Scanner(System.in);
        numOfLinearEq = getNumOfLinearEq();
        input.nextLine(); // clear any remaining input in scanner
        matrix = getMatrix();
//        input.nextLine();
    } // end constructor
    
    /** 
     * asks the user to enter the number of linear equations
     * @return the number of linear equations 
     */
    private int getNumOfLinearEq() {
        System.out.print("Enter the number of equations: ");
        return input.nextInt();
    } // end getNumOfLinearEq
    
    /** 
     * asks the user to enter the coefficients for the augmented coefficient 
     * matrix and returns the matrix from that information
     * @return the augmented coefficient matrix 
     * @throws java.io.FileNotFoundException File not found
     */
    private float[][] getMatrix() throws FileNotFoundException {
        matrix = new float[numOfLinearEq][numOfLinearEq+1];
        int inputChoice;
        
        System.out.print("Do you want to enter the coefficients by command"
                + "line or by filename? (1 for command line, 2 for filename):");
        inputChoice = input.nextInt();
        input.nextLine();
        if(inputChoice == 1) commandLineMethod();
        else filenameMethod();        
        return matrix;
    } // end getMatrix
    
    /**
     * uses the command line to get the coefficients of the matrix
     */
    private void commandLineMethod(){
        String[] coefficients;
        // rows
        for (int i = 0; i < numOfLinearEq; i++) {
            System.out.print("Enter the " + (numOfLinearEq+1) + 
                    " coefficients for row " + (i+1) +
                    " (seperate each coefficient by space): ");
            coefficients = input.nextLine().split("\\s+");
            // columns
            for(int j = 0; j < coefficients.length; j++){
                matrix[i][j] = Float.parseFloat(coefficients[j]);
            }
        }
    } // end commandLineMethod
    
    /**
     * uses a file to get the coefficients of the matrix
     * @throws java.io.FileNotFoundException
     */
    private void filenameMethod() throws FileNotFoundException {
        System.out.print("Enter the filename including extension: ");
        File file = new File(input.nextLine());
        try (Scanner fileScanner = new Scanner(file)) {
            // rows
            for (int i = 0; i < numOfLinearEq && fileScanner.hasNextFloat();
                    i++) {
                // columns
                for(int j = 0; j < numOfLinearEq+1 && 
                        fileScanner.hasNextFloat(); j++){
                    matrix[i][j] = fileScanner.nextFloat();
                }
            }
            fileScanner.close();
        }
        catch(FileNotFoundException e){}
    } // end filenameMethod
    
    /**
     * scaled partial pivoting method for Gaussian Elimination
     */
    private void scaledPartialPivoting(){
        float[][] tempMatrix = matrix;
        float[] weights = getWeights();
        // [1][1] = row number, [1][2] = ratio
        float[][] maxRatioPair;
        // record of all pivotal equations and initialize them with -1
        int[] pivotRecord = new int[numOfLinearEq-1];
        Arrays.fill(pivotRecord, -1);
        /* iterate through each equation to get pivotal equation */
        // iterate through each column except for b and last pivotal equation
        for(int i = 0; i < numOfLinearEq-1; i++){
            // get pivotal equation
            maxRatioPair = getMaxRatioPair(i,weights,pivotRecord);
            System.out.println("pivot row = " + (int)maxRatioPair[0][0]);
            System.out.println("pivot ratio = " + maxRatioPair[0][1]);
            // perform gauss elimination, returning a new matrix
            performGaussElimination(i,maxRatioPair,weights,pivotRecord,tempMatrix);
        }
        System.out.println("");
    } // end scaledPartialPivoting
    
    /**
     * gets the weight iteratively
     * @return weights 
     */
    private float[] getWeights() {
        float[] weights = new float[numOfLinearEq];
        float maximum; 
        for(int i = 0; i < numOfLinearEq; i++) {
            maximum = -1;
            for(int j = 0; j < numOfLinearEq+1; j++) {
                maximum = Math.max(maximum, Math.abs(matrix[i][j]));
            }
            weights[i] = maximum;
        }
        return weights;
    } // end getWeights
    
    /**
     * gets the maxRatioPair for the pivotal equation
     * @return maxRatioPair
     */    
    private float[][] getMaxRatioPair(int i, float[] weights, 
            int[] pivotRecord){
        float[][] maxRatioPair = { {-1,-1} };
        float ratio;
        // get the maxRatioPair through each row to get highest ratio
        // this will give the pivotal equation row
        for(int j = 0; j < numOfLinearEq; j++){
            // do not include previous pivots to getting max ratio
            if(!contains(pivotRecord,j)) { 
                // ratio = matrix number / weight of row
                ratio = (float) Math.floor((
                        Math.abs(matrix[j][i]) / weights[j]) * 100) / 100;
                if(ratio > maxRatioPair[0][1]) {
                    maxRatioPair[0][0] = j;
                    maxRatioPair[0][1] = ratio;
                }
            }
        }
        // add pivot to record
        pivotRecord[i] = (int) maxRatioPair[0][0];
        System.out.print("pivot record: ");
        for(int pivot : pivotRecord){
            System.out.print(pivot + " ");
        }
        System.out.println("");
        return maxRatioPair;
    } // end getMaxRatioPair
    
    /**
    * performs gauss elimination of a matrix
    * @param i the pivotal column
    * @param maxRatioPair pivotal equation
    * @param pivotRecord record of current and previous pivots
    */
    private void performGaussElimination(int i, float[][] maxRatioPair, 
            float[] weights, int[] pivotRecord, float[][] tempMatrix){
        float pivotNumber;
        float pivotRate;
        float targetNumber;
        float pivotWeight;
        /* perform gaussian elimination for each row using that pivot */
        // rows
        for(int j = 0; j < numOfLinearEq; j++) {
            // don't perform gauss elimiation if row is currently  pivot or if already
            // has 0 at pivotal column
            if(!contains(pivotRecord,j) || tempMatrix[j][i] == 0) {
                // pivot rate = matrix[pivot row][pivot col] / matrix[row][pivot col]
                pivotRate = tempMatrix[(int) maxRatioPair[0][0]][i] / tempMatrix[j][i];
                // pivot weight = weight of the pivot equation;
                pivotWeight = weights[(int) maxRatioPair[0][0]];
                // columns
                for(int k = 0; k < numOfLinearEq+1; k++) {
                    // pivotNumber = xk , number that is subtracted
                    pivotNumber = tempMatrix[(int) maxRatioPair[0][0]][k];
                    // targetNumber = matrix number that is being changed
                    targetNumber = tempMatrix[j][k];
                    // new matrix number = (pivotNumber - pivotRate*targetnumber) / pivot weight
                    // round to 3 decimal places
                    tempMatrix[j][k] = (float) (Math.floor(
                            ((pivotNumber - (pivotRate * targetNumber)) / pivotWeight)
                                * 100) / 100);
                }
                // print intermidiate matrixes
                printMatrix(tempMatrix);
            }
        }
    } // return performGaussElimination
    
    /**
     * 
     * @param pivotRecord record of pivots
     * @param key number you want to see if it is contained
     * @return whether pivotRecord has key
     */
    public boolean contains(int[] pivotRecord,int key){
        for(int num : pivotRecord){
            if(num == key) return true;
        }
        return false;
    } // end contains
    
    /**
     * Gauss Jacobi iterative method
     */
    public void Jacobi(){
        // results = x at latest iteration
        float[] results = new float[numOfLinearEq];
        // initialize x0 to all zeros
        Arrays.fill(results, 0);
        // converts 
        for(int i = 0; i < 50; i++) {
            for(int j = 0; j < numOfLinearEq; j++){
                for(int k = 0; k < numOfLinearEq+1; k++){
                    results[j] = 
                }
            }
        }
    } // end Jacobi
    
    /** 
     * prints the matrix
     * @param matrix the augmented matrix
     */
    public static void printMatrix(float[][] matrix){
        for (float[] row : matrix) {
            System.out.print("[ ");
            for (int column = 0; column < row.length; column++) {
                System.out.print(row[column] + " ");
            }
            System.out.println("]");
        }
        System.out.println("");
    } // end printMatrix        
            
    /**
     * performs the 3 algorithms that solves a system of equations
     * @param args the command line arguments
     * @throws java.io.FileNotFoundException file not found
     */
    public static void main(String[] args) throws FileNotFoundException {
        GaussianElimination ge = new GaussianElimination();
        GaussianElimination.printMatrix(ge.matrix);
        //ge.scaledPartialPivoting();
    } // end main
    
} // end GaussianElimination