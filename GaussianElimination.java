/**
 * Michael Trinh
 * CS 3010
 * Programming Project 1
 *  Description: Write a program that asks the user for the number of linear 
 *   equations to solve (let’s say n <=10) using the following three methods.
 */

import java.util.Scanner;

public class GaussianElimination {
    // creates a long start, end, and total time variable to time an alg.
    long startTime;
    long endTime;
    long totalTime;
    Scanner input;
    // constructor: creates scanner object
    public GaussianElimination(){
        input = new Scanner(System.in);
    }
    
    /** 
     * asks the user to enter the number of linear equations
     * @return the number of linear equations 
     */
    public int getNumOfLinearEq() {
        System.out.print("Enter the number of equations: ");
        return input.nextInt();
    } // end getNumOfLinearEq
    
    /** 
     * asks the user to enter the coefficients for the augmented coefficient 
     * matrix and returns the matrix from that information
     * @param numOfLinearEq the number of linear equations
     * @return the augmented coefficient matrix 
     */
    public float[][] getMatrix(int numOfLinearEq) {
        float[][] matrix = new float[numOfLinearEq][numOfLinearEq+1];
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
        return matrix;
    } // end getMatrix
    
    /** 
     * prints the matrix
     * @param matrix the augmented matrix
     */
    public void printMatrix(float[][] matrix){
        for (float[] row : matrix) {
            for (int column = 0; column < row.length; column++) {
                System.out.print(row[column] + " ");
            }
            System.out.println("");
        }
    } // end printMatrix
    
    /**
     * performs the 3 algorithms that solves a system of equations
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        GaussianElimination ge = new GaussianElimination();
        int numOfLinearEq = ge.getNumOfLinearEq();
        ge.input.nextLine();
        System.out.println(numOfLinearEq + " equations.");
        float[][] matrix = ge.getMatrix(numOfLinearEq);
        ge.printMatrix(matrix);
    } // end main
    
} // end GaussianElimination