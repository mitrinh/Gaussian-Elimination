/**
 * Michael Trinh
 * CS 3010
 * Programming Project 1
 * Description: Write a program that asks the user for the number of linear 
 *                  equations to solve using 3 methods. 
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Stack;
import java.util.Scanner;

public class GaussianElimination {
    // creates a long start, end, and total time variable to time an alg.
    int numOfLinearEq;
    float[][] matrix;
    float[][] originalMatrix;
    Scanner input;
    
    // constructor: initializes matrix essentially
    public GaussianElimination() throws FileNotFoundException{
        input = new Scanner(System.in);
        numOfLinearEq = getNumOfLinearEq();
        input.nextLine(); // clear any remaining input in scanner
        matrix = getMatrix();
        originalMatrix = Arrays.copyOf(matrix, matrix.length);
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
    private void scaledPartialPivoting(float[][] matrix){
        // get weights
        float[] weights = getWeights(matrix);
        // initialize variables
        int pivotRow;
        float[] results = new float[numOfLinearEq];
        // initialize each cell in results to zero
        Arrays.fill(results, 0);        
        // stack of all pivotal equations
        Stack<Integer> pivotRecord = new Stack<>();
        // iterate through each column except for b 
        // to get gauss eliminated augmented matrix
        for(int i = 0; i < numOfLinearEq; i++){
            // get pivotal equation
            pivotRow = getPivotRow(i,weights,pivotRecord,matrix);
            // perform gauss elimination, transforming old matrix to new matrix
            gaussElimination(i,pivotRow,weights,pivotRecord,matrix);
        }
        // perform backwards substitution
        backwardSubstitution(matrix,pivotRecord,results);
        printResults(results);
    } // end scaledPartialPivoting
    
    /**
     * gets the weight iteratively using maximize function
     * @param matrix augmented matrix
     * @return weights 
     */
    private float[] getWeights(float[][] matrix) {
        // initialize variables
        float[] weights = new float[numOfLinearEq];
        float maximum; 
        // iterate through each equation to get the highest number of each one
        for(int i = 0; i < numOfLinearEq; i++) {
            maximum = -1;
            for(int j = 0; j < numOfLinearEq+1; j++) {
                maximum = Math.max(maximum, Math.abs(matrix[i][j]));
            }
            // set the weight to max number in that equation
            weights[i] = maximum;
        }
        return weights;
    } // end getWeights
    
    /**
     * gets the maxRatioPair for the pivotal equation
     * @param i column
     * @param weights array of weights
     * @param pivotRecord record of current and previous pivots
     * @param matrix augmented matrix
     * @return maxRatioPair
     */    
    private int getPivotRow(int i, float[] weights, Stack<Integer> pivotRecord, 
            float[][] matrix){
        // initialize variables
        int pivotRow;
        float[][] maxRatioPair = { {-1, -1} }; // [1][1] = row number, [1][2] = ratio
        float ratio;
        // iterate through every row to get max num/weight ratio for pivot
        for(int j = 0; j < numOfLinearEq; j++){
            // do not include previous pivots to getting a new pivot
            if(!pivotRecord.contains(j)) { 
                // pushes last pivot to stack if last iteration
                if(i+1 == numOfLinearEq) {
                    pivotRecord.push(j);
                    break;
                }
                else {
                    // ratio = abs(matrix number / weight of that row)
                    ratio = Math.abs(matrix[j][i] / weights[j]);
                    // replace max if current ratio is higher
                    if(ratio > maxRatioPair[0][1]) {
                        maxRatioPair[0][0] = j;
                        maxRatioPair[0][1] = ratio;
                    }
                }
            }
        }
        // if last iteration return top of stack, else add to stack
        if(i+1 != numOfLinearEq){
            // set pivot row to paired row of max ratio
            pivotRow = (int) maxRatioPair[0][0];
            // push pivot to record stack
            pivotRecord.push(pivotRow);    
        }
        else pivotRow = pivotRecord.peek();
        return pivotRow;
    } // end getMaxRatioPair
    
    /**
     * performs gauss elimination of a matrix
     * @param i the pivotal column
     * @param maxRatioPair pivotal equation
     * @param pivotRecord record of current and previous pivots
     */
    private void gaussElimination(int i, int pivotRow,float[] weights, 
            Stack<Integer> pivotRecord, float[][] matrix){
        float pivotNumber;
        float pivotRate;
        float targetNumber;
        float pivotWeight;
        // do this if not last iteration
        if(i+1 != numOfLinearEq) {
            /* perform gaussian elimination for each row using that pivot */
            // rows
            for(int j = 0; j < numOfLinearEq; j++) {
                // don't perform gauss elimiation if row is currently  pivot or if already
                // has 0 at pivotal column
                if((!pivotRecord.contains(j)) || matrix[j][i] == 0) {
                    // pivot rate = matrix[pivot row][pivot col] / matrix[row][pivot col]
                    pivotRate = matrix[pivotRow][i] / matrix[j][i];
                    // pivot weight = weight of the pivot equation;
                    pivotWeight = weights[pivotRow];
                    // columns
                    for(int k = 0; k < numOfLinearEq+1; k++) {
                        // pivotNumber = xk , number that is subtracted
                        pivotNumber = matrix[pivotRow][k];
                        // targetNumber = matrix number that is being changed
                        targetNumber = matrix[j][k];
                        // new matrix number = (pivotNumber - pivotRate*targetnumber) / pivot weight
                        matrix[j][k] = (pivotNumber - pivotRate * targetNumber) / pivotWeight;
                    }
                    // print intermidiate matrixes
                    printMatrix(matrix);
                }
            }
        }
    } // return performGaussElimination
    
    /**
     * performs backward substitution on an gauss eliminated augmented matrix
     * @param matrix
     * @param pivotRecord 
     */
    private void backwardSubstitution(float[][] matrix, Stack<Integer> pivotRecord,
            float[] results){        
        int pivotRow;
        float tempWeight;
        // iterates through each pivot from the pivotRecord, latest first
        for(int i = numOfLinearEq-1; !pivotRecord.empty();i--) {
            // set the latest pivot in record to pivotRow then delete it from stack
            pivotRow = pivotRecord.pop();
            tempWeight = matrix[pivotRow][i];
            // set result to b value/xj coefficient
            results[i] = matrix[pivotRow][numOfLinearEq] / tempWeight;
            // column
            for(int j = numOfLinearEq-1; j >= 0 ; j--) {
                if(i != j){
                    // xi = xi - (c[j]/c[i])*x[j]
                    results[i] -= (matrix[pivotRow][j] / tempWeight) * results[j];
                }
            }
        }
    } // end backwardSubstitution
    
    /**
     * Gauss Jacobi iterative method
     * @param matrix augmented matrix
     */
    private void Jacobi(float[][] matrix){
        // asks user for desired error
        System.out.print("Enter the desired error in decimal form: ");
        float error = input.nextFloat();
        float totalError = 0;
        // results = x at latest iteration
        float[] results = new float[numOfLinearEq];
        float[] previousResults;
        float[] tempResults = new float[numOfLinearEq];
        // initialize x^0 to all zeros
        Arrays.fill(results, 0);
        // print intemediate x coefficient matrix
        System.out.println("x^" + 0 + ":");
        printResults(results);
        // 50 iterations if desired error not achieved
        for(int i = 0; i < 50; i++) {
            previousResults = results.clone();
            // row
            for(int j = 0; j < numOfLinearEq; j++){
                // set result to b value/xj coefficient
                tempResults[j] = matrix[j][numOfLinearEq] / matrix[j][j];
                // column
                for(int k = numOfLinearEq-1; k >= 0; k--){
                    if(k != j) {
                        // subtract all values that are used for the sum
                        tempResults[j] -= results[k] * (matrix[j][k] / matrix[j][j]);
                    }
                }
            }
            // update current results
            results = tempResults.clone();
            tempResults = new float[numOfLinearEq];
            // will only calculate error when there's a previous iteration
            if(i > 0){
                // get the sum of error from all numbers
                for (int k = 0; k < results.length; k++){
                    totalError += Math.abs(results[k] - previousResults[k]) / 2; 
                }
                // if error achieved print results and break out of loop
                if(totalError < error){
                    System.out.println("x^" + (i+1) + ":");
                    printResults(results);
                    break;
                }
                totalError = 0;
            }
            // print intemediate x coefficient matrix
            System.out.println("x^" + (i+1) + ":");
            printResults(results);
        }
    } // end Jacobi
    
    /**
     * Gauss Siedel iterative method
     * @param matrix augmented matrix
     */
    private void Siedel(float [][] matrix){
        // asks user for desired error
        System.out.print("Enter the desired error in decimal form: ");
        float error = input.nextFloat();
        float totalError = 0;
        // results = x at latest iteration
        float[] results = new float[numOfLinearEq];
        float[] previousResults;
        // initialize x^0 to all zeros
        Arrays.fill(results, 0);
        // print intemediate x coefficient matrix
        System.out.println("x^" + 0 + ":");
        printResults(results);
        // 50 iterations if desired error not achieved
        for(int i = 0; i < 50; i++) { 
            // store old results
            previousResults = Arrays.copyOf(results, results.length);
            // row
            for(int j = 0; j < numOfLinearEq; j++){
                // set result to b value/xj coefficient
                results[j] = matrix[j][numOfLinearEq] / matrix[j][j];
                // column
                for(int k = numOfLinearEq-1; k >= 0; k--){
                    if(k != j) {
                        // subtract all values that are used for the sum
                        results[j] -= results[k] * (matrix[j][k] / matrix[j][j]);
                    }
                }
            }
            // will only calculate error when there's a previous iteration
            if(i > 0){
                // get the sum of error from all numbers
                for (int k = 0; k < results.length; k++){
                    totalError += Math.abs(results[k] - previousResults[k]) / 2; 
                }
                // if error achieved print results and break out of loop
                if(totalError < error){
                    System.out.println("x^" + (i+1) + ":");
                    printResults(results);
                    break;
                }
                totalError = 0;
            }
            // print intemediate x coefficient matrix
            System.out.println("x^" + (i+1) + ":");
            printResults(results);
        }
    } // end Siedel
    
    /**
     * prints the results from a method
     * @param results results from a method 
     */
    public void printResults(float[] results){
        // truncate to 3 decimal places to look better
        DecimalFormat df = new DecimalFormat("#.###");
        // iterate through each result
        for(int i = 0; i < results.length; i++){
            System.out.println("x" + (i+1) + " = " + df.format(results[i]));
        }
        System.out.println("");
    } // end print results;
    
    /** 
     * prints the matrix
     * @param matrix the augmented matrix
     */
    public static void printMatrix(float[][] matrix){
        // truncate to 3 decimal places to look better
        DecimalFormat df = new DecimalFormat("#.###");
        // iterate through each cell of matrix to print
        for (float[] row : matrix) {
            System.out.print("[ ");
            for (int column = 0; column < row.length; column++) {
                System.out.print(df.format(row[column]) + " ");
            }
            System.out.println("]");
        }
        System.out.println("");
    } // end printMatrix        
            
    public static void cloneMatrix(float[][] target, float[][] source){
        for(int i = 0; i < source.length; i++)
            target[i] = source[i].clone();
    }
    
    /**
     * performs the 3 algorithms that solves a system of equations
     * @param args the command line arguments
     * @throws java.io.FileNotFoundException file not found
     */
    public static void main(String[] args) throws FileNotFoundException {
        GaussianElimination ge = new GaussianElimination();
        float[][] myMatrix = ge.matrix;
        float[][] originalMatrix = new float[myMatrix.length][myMatrix[0].length];
        cloneMatrix(originalMatrix, myMatrix);
        GaussianElimination.printMatrix(originalMatrix);
        
        System.out.println("Scaled Partial Pivoting Gaussian Elimination:");
        ge.scaledPartialPivoting(originalMatrix);
        cloneMatrix(originalMatrix, myMatrix);
        
        System.out.println("Gauss Jacobi:");
        ge.Jacobi(originalMatrix);
        cloneMatrix(originalMatrix, myMatrix);
        
        System.out.println("Gauss Siedel:");
        ge.Siedel(originalMatrix);
    } // end main
    
} // end GaussianElimination