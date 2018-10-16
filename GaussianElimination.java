/**
 * Michael Trinh
 * CS 3010
 * Programming Project 1
 *  Description: Write a program that asks the user for the number of linear 
 *   equations to solve (let’s say n <=10) using the following three methods.
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class GaussianElimination {
    // creates a long start, end, and total time variable to time an alg.
    int numOfLinearEq;
    float[][] matrix;
    long startTime;
    long endTime;
    long totalTime;
    Scanner input;
    
    // constructor: initializes matrix essentially
    public GaussianElimination() throws FileNotFoundException{
        input = new Scanner(System.in);
        numOfLinearEq = getNumOfLinearEq();
        input.nextLine();
        matrix = getMatrix();
    } // end constructor
    
    /** 
     * asks the user to enter the number of linear equations
     * @return the number of linear equations 
     */
    public final int getNumOfLinearEq() {
        System.out.print("Enter the number of equations: ");
        return input.nextInt();
    } // end getNumOfLinearEq
    
    /** 
     * asks the user to enter the coefficients for the augmented coefficient 
     * matrix and returns the matrix from that information
     * @return the augmented coefficient matrix 
     * @throws java.io.FileNotFoundException File not found
     */
    public final float[][] getMatrix() throws FileNotFoundException {
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
    public void commandLineMethod(){
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
    public void filenameMethod() throws FileNotFoundException {
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
     * prints the matrix
     * @param matrix the augmented matrix
     */
    public static void printMatrix(float[][] matrix){
        for (float[] row : matrix) {
            System.out.print("[ ");
            for (int column = 0; column < row.length; column++) {
                System.out.print(row[column] + " ");
            }
            System.out.println("] ");
        }
    } // end printMatrix
    
    /**
     * performs the 3 algorithms that solves a system of equations
     * @param args the command line arguments
     * @throws java.io.FileNotFoundException file not found
     */
    public static void main(String[] args) throws FileNotFoundException {
        GaussianElimination ge = new GaussianElimination();
        printMatrix(ge.matrix);
    } // end main
    
} // end GaussianElimination