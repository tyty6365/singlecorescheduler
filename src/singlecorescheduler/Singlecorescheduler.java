//* Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
//* Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
package singlecorescheduler;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

/**
 * A application to simulate a non-preemptive scheduler for a single-core CPU
 * using a heap-based implementation of a priority queue
 * @author William Duncan, Tyler Scott
 * @see PQueue.java, PCB.java
 * <pre>
 * DATE: 09/21/2022
 * File:SingleCoreScheduler.java
 * Course: csc 3102
 * Programming Project # 1
 * Instructor: Dr. Duncan
 * Usage: SingleCoreScheduler <number of cylces> <-R or -r> <probability of a  process being created per cycle>  or,
 *        SingleCoreScheduler <number of cylces> <-F or -f> <file name of file containing processes>,
 *        The simulator runs in either random (-R or -r) or file (-F or -f) mode 
 * </pre>
 */
public class Singlecorescheduler {

    /**
     * Single-core processor with non-preemptive scheduling simulator
     *
     * @param args an array of strings containing command line arguments args[0]
     * - number of cyles to run the simulation args[1] - the mode: -r or -R for
     * random mode and -f or -F for file mode args[2] - if the mode is random,
     * this entry contains the probability that a process is created per cycle
     * and if the simulator is running in file mode, this entry contains the
     * name of the file containing the the simulated jobs. In file mode, each
     * line of the input file is in this format:
     * <process ID> <priority value> <cycle of process creation>
     * <time required to execute>
     */
    public static void main(String[] args) throws PQueueException, IOException {
        boolean isFileMode = false;
        Scanner jobsFile = null;
        int process_count = 0;
        int cycles = Integer.parseInt(args[0]);

        if (args.length != 3) {
            System.out.println("Usage: SingleCoreScheduler <number of cylces> <-R or -r> <probability of a  process being created per cycle>  or ");
            System.out.println("       SingleCoreScheduler <number of cylces> <-F or -f> <file name of file containing processes>");
            System.out.println("The simulator runs in either random (-R or -r) or file (-F or -f) mode.");
            System.exit(1);
        }
        
        //reads in file mode
        if (args[1].equalsIgnoreCase("-f")) {
            isFileMode = true;
        //reads in random mode
        } else if (args[1].equalsIgnoreCase("-r")) {
            isFileMode = false;
        } else {
            System.out.println("Invalid mode: " + args[1]);
            System.exit(1);
        }

        Random randomGenerator = null;
        double randomProbability = 0;
        if (isFileMode) {
            try {
                jobsFile = new Scanner(new FileReader(args[2]));
            } catch (FileNotFoundException ee) {
                System.out.println("Invalid path for file: " + args[2]);
                System.exit(1);
            }

        } else {
            randomGenerator = new Random(System.currentTimeMillis());
            try {
                randomProbability = Double.parseDouble(args[2]);
            } catch (NumberFormatException ee) {
                System.out.println("Invalid for random probability: " + args[2]);
            }
        }
        
        //initializes priority queue with comparator
        PQueue<PCB> queue = new PQueue<>((PCB pcb1, PCB pcb2) -> { 

            int pri = pcb2.compareTo(pcb1);

            if (pri == 0) {
                return pcb1.getArrival() - pcb2.getArrival();
            }
            return pri;
        });
        int total_Wait_Time = 0;
        int total_Turn_Around = 0;

        PCB activeProcess = null;

        PCB nextProcess = null;

        if (isFileMode && jobsFile.hasNextLine()) {
            int PID = jobsFile.nextInt();
            int Priority = jobsFile.nextInt();
            int Start = jobsFile.nextInt();
            int Burst = jobsFile.nextInt();

            nextProcess = new PCB(PID, Priority, 0, Start, Burst);

        }
        //checking to print cycle number 
        for (int i = 0; i < cycles; i++) {
            System.out.println("*** Cycle #: " + i);

            if (queue.isEmpty() && activeProcess == null) {
                System.out.println("The CPU is idle."); //prints output of CPU idle
            } else {
                if (activeProcess == null) { //check from here to line 115
                    activeProcess = queue.remove();
                    activeProcess.setWait(i - activeProcess.getArrival());
                    total_Wait_Time += activeProcess.getWait();

                    activeProcess.execute();
                    activeProcess.setStart(i);

                }
                if (activeProcess.getStart() + activeProcess.getBurst() == i) {
                    System.out.println("Process #" + activeProcess.getPid() + "has just terminated.");

                    total_Turn_Around += activeProcess.getBurst();

                    activeProcess = null;
                } else {
                    System.out.println("Process #" + activeProcess.getPid() + "is executing.");
                }
            }

            if (isFileMode && nextProcess != null && nextProcess.getArrival() == i) {
                process_count++;

                queue.insert(nextProcess);

                System.out.println(" Adding job with pid# " + nextProcess.getPid() + " and priority " + nextProcess.getPriority() + " and burst " + nextProcess.getBurst() + ".");

                nextProcess = null;
                
                //applies first job to file 
                if (jobsFile.hasNextLine()) {
                    int PID = jobsFile.nextInt();
                    int Priority = jobsFile.nextInt();
                    int Start = jobsFile.nextInt();
                    int Burst = jobsFile.nextInt();

                    nextProcess = new PCB(PID, Priority, 0, Start, Burst);
                }

            } else if (!isFileMode && randomGenerator.nextDouble() <= randomProbability) {
                PCB process = new PCB(++process_count, randomGenerator.nextInt(21 + 19) - 19, 0, i, randomGenerator.nextInt(101 - 1) + 1);

                System.out.println("Adding job with pid# " + process.getPid() + " and priority " + process.getPriority() + " and burst " + process.getBurst() + ".");

                queue.insert(process);
            } else {
                System.out.println("No new job this cycle.");
            }
        }

        if (jobsFile != null) {
            jobsFile.close();
        }
        
        //prints output to console
        System.out.println("The average number of process created per cycle is " + ((double) process_count / cycles) + ".");  
        System.out.println("The throughput is " + ((double) total_Turn_Around / process_count) + " per cycle.");
        System.out.println("The average wait time per process is " + ((double) total_Wait_Time / process_count) + ".");
    }
}