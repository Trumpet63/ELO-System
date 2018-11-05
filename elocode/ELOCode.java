package elocode;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ELOCode {
    public static void main(String[] args) {
        File[] files = getFilesFromArgs(args);
        new ELOProcessor(files[0], files[1], files[2]).process();
    }

    private static File[] getFilesFromArgs(String[] args) {
        File[] files = new File[3];
        switch(args.length) {
            case 0:
                tooFewArgumentsError();
                break;
            case 1:
                files[0] = new File(args[0]);
                verifyDataFile(files[0]);
                files[1] = generateRatingsFile();
                files[2] = generateReportFile();
                break;
            case 2:
                files[0] = new File(args[0]);
                verifyDataFile(files[0]);
                files[1] = new File(args[1]);
                files[2] = generateReportFile();
                break;
            case 3:
                files[0] = new File(args[0]);
                verifyDataFile(files[0]);
                files[1] = new File(args[1]);
                files[2] = new File(args[2]);
                break;
            default:
                argumentsError();
                break;
        }
        return files;
    }

    private static void tooFewArgumentsError() {
        System.out.println("This program requires between 1 and 3 arguments.");
        printHelpMessage();
        System.exit(0);
    }

    private static void verifyDataFile(File file) {
        if(!file.exists() || file.isDirectory()) {
            System.out.println("Data file is invalid.");
            printHelpMessage();
            System.exit(0);
        }
    }

    private static File generateRatingsFile() {
        File ratingsFile = new File("ratings.csv");
        if(ratingsFile.exists()) {
            System.out.println("File with default name 'ratings.csv' already exists");
            System.exit(0);
        }
        return ratingsFile;
    }

    private static File generateReportFile() {
        String timeString = LocalDateTime.now().format(DateTimeFormatter.ofPattern("uuuuMMddHHmmss"));
        return new File("report_" + timeString + ".csv");
    }

    private static void argumentsError() {
        System.out.println("This program requires between 1 and 3 arguments.");
        printHelpMessage();
        System.exit(0);
    }

    private static void printHelpMessage() {
        System.out.println("Usage:\n"
                + "java elocode/ELOCode <dataFile> [ratingsFile] [reportFile]\n"
                + "NOTE: arguments other than dataFile will be generated\n"
                + "      for you if not specified.");
    }
    
}