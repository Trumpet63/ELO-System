package elocode;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ELOProcessor {
    File dataFile;
    File ratingsFile;
    File reportFile;
    Random rng;
    static double K = 32;
    static final String DATA_HEADER = "id,name,score";
    static final String RATINGS_HEADER = "id,name,rating,start_date,last_updated";
    static final String REPORT_HEADER = "id,name,score,rating_before,rating_after,rating_change";
    Map<String, String> nameLookup;
    Map<String, Double> ratings;
    Map<String, Double> scores;
        
    public ELOProcessor(File dataFile, File ratingsFile, File reportFile) {
        this.dataFile = dataFile;
        this.ratingsFile = ratingsFile;
        this.reportFile = reportFile;
        rng = new Random();
        nameLookup = new HashMap();
        ratings = new HashMap();
        scores = new HashMap();
    }

    void process() {
        Map<String, Double[]> ratingsAndScores = extractRatingsAndScores();
        Map<String, Double> ratingChanges = calculateRatingChanges(ratingsAndScores);
        generateReport(ratingsAndScores, ratingChanges);
        applyChanges(ratingChanges);
    }

    private Map<String, Double[]> extractRatingsAndScores() {
        if(ratingsFile.exists() && !ratingsFile.isDirectory()) 
            parseRatingsFile();
        parseDataFile();
        Map<String, Double[]> ratingsAndScores = new HashMap();
        scores.forEach((id, score) -> {
            ratingsAndScores.put(id, new Double[] {ratings.get(id), score});
        });
        return ratingsAndScores;
    }
    
    private void parseRatingsFile() {
        try {
            Scanner ratingsReader = new Scanner(ratingsFile);
            if(!ratingsReader.hasNextLine()) {
                ratingsReader.close();
                return;
            }
            ratingsReader.nextLine(); // skip header
            while(ratingsReader.hasNextLine()) {
                String[] attributes = parseLine(ratingsReader.nextLine());
                nameLookup.put(attributes[0], attributes[1]);
                ratings.put(attributes[0], Double.parseDouble(attributes[2]));
            }
            ratingsReader.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ELOProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void parseDataFile() {
        try {
            Scanner dataReader = new Scanner(dataFile);
            int lineCount = 1;
            dataReader.nextLine(); // skip header;
            while(dataReader.hasNextLine()) {
                String[] attributes = parseLine(dataReader.nextLine());
                if(!attributes[0].isEmpty()) {
                    if(!nameLookup.containsKey(attributes[0])) {
                        throw new Exception("problem with id on line " + lineCount);
                    }
                    scores.put(attributes[0], Double.parseDouble(attributes[2]));
                }
                else {
                    handleNewPerson(attributes);
                }
                lineCount ++;
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ELOProcessor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            System.out.println(ex);
            Logger.getLogger(ELOProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void handleNewPerson(String[] attributes) {
        String newID = generateUniqueID();
        nameLookup.put(newID, attributes[1]);
        ratings.put(newID, 1000.0);
        scores.put(newID, Double.parseDouble(attributes[2]));
    }
        
    private String[] parseLine(String line) {
        return line.split(",");
    }
    
    private String generateUniqueID() {
        String newID = null;
        do {
            newID = String.format("%05d",randomInteger(0, 99999));
        } while(nameLookup.keySet().contains(newID));
        return newID;
    }
    
    private int randomInteger(int min, int max) {
        return rng.nextInt((max - min) + 1) + min;
    }

    private Map<String, Double> calculateRatingChanges(Map<String, Double[]> ratingsAndScores) {
        int size = ratingsAndScores.size();
        String[] ids = new String[size];
        ids = ratingsAndScores.keySet().toArray(ids);
        double[] changes = new double[size];
        double maxScore = findMaxScore(ratingsAndScores);
        for(int p1 = 0; p1 < size; p1++) {
            for(int p2 = p1 + 1; p2 < size; p2++) {
                double change = calculateRatingChange(ratingsAndScores, ids[p1], ids[p2], maxScore);
                changes[p1] += change;
                changes[p2] -= change;
            }
        }
        Map<String, Double> changeMap = new HashMap();
        for(int i = 0; i < size; i++) {
            changeMap.put(ids[i], changes[i]);
        }
        return changeMap;
    }
    
    private double findMaxScore(Map<String, Double[]> ratingsAndScores) {
        double max = 0.0;
        Iterator<Double[]> iter = ratingsAndScores.values().iterator();
        while(iter.hasNext()) {
            double nextScore = iter.next()[1];
            if(nextScore > max)
                max = nextScore;
        }
        return max;
    }
    
    private double calculateRatingChange(Map<String, Double[]> ratingsAndScores, String id1, String id2, double maxScore) {
        double rating1 = ratingsAndScores.get(id1)[0];
        double rating2 = ratingsAndScores.get(id2)[0];
        double score1 = ratingsAndScores.get(id1)[1];
        double score2 = ratingsAndScores.get(id2)[1];
        double normalizedScore = calculateNormalizedScore(score1, score2, maxScore);
        return pointsExchanged(rating1, rating2, normalizedScore);
    }
    
    private double calculateNormalizedScore(double a, double b, double max) {
	return (a - b + max) / max / 2;
    }
    
    private double pointsExchanged(double rating1, double rating2, double score) {
	double expectedScore = 1 / (1 + Math.pow(10, (rating2 - rating1) / 400));
	return K * (score - expectedScore);
    }

    private void generateReport(Map<String, Double[]> ratingsAndScores, Map<String, Double> ratingChanges) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(reportFile));
            writer.write(REPORT_HEADER + System.getProperty("line.separator"));
            ratingsAndScores.forEach((id, rating_score) -> {
                try {
                    writeToCSV(writer, new String[] {
                        id,
                        nameLookup.get(id),
                        rating_score[1].toString(),
                        roundDouble(rating_score[0], 0),
                        roundDouble(rating_score[0] + ratingChanges.get(id), 0),
                        (ratingChanges.get(id) > 0 ? "+" : "") + roundDouble(ratingChanges.get(id), 1)
                    });
                } catch (IOException ex) {
                    Logger.getLogger(ELOProcessor.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(ELOProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void writeToCSV(BufferedWriter writer, String[] attributes) throws IOException {
        writer.write(String.join(",", attributes) + System.getProperty("line.separator"));
    }
    
    private String roundDouble(double score, int decimalPlaces) {
        String formatString = "%." + decimalPlaces + "f";
        return String.format(formatString, Math.round(score * Math.pow(10, decimalPlaces)) / Math.pow(10, decimalPlaces));
    }
    
    private void applyChanges(Map<String, Double> ratingChanges) {
        File tempFile = new File("eloTempFile.csv");
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
            String currentLine;
            writer.write(RATINGS_HEADER + System.getProperty("line.separator")); // write new header
            if(ratingsFile.exists() && !ratingsFile.isDirectory()) {
                BufferedReader reader = new BufferedReader(new FileReader(ratingsFile));
                reader.readLine(); // skip old header

                // update ratings and copy unchanged ones
                while((currentLine = reader.readLine()) != null) {
                    String[] attributes = parseLine(currentLine);
                    if(ratingChanges.containsKey(attributes[0])) {
                        writeToCSV(writer, new String[] {
                            attributes[0],
                            attributes[1],
                            String.valueOf(ratings.get(attributes[0]) + ratingChanges.get(attributes[0])),
                            attributes[3],
                            LocalDateTime.now().format(DateTimeFormatter.ofPattern("uuuu/MM/dd HH:mm:ss"))
                        });
                        ratingChanges.remove(attributes[0]);
                    }
                    else {
                        writeToCSV(writer, attributes);
                    }
                }
                reader.close();
            }
            
            // add ratings for new people
            ratingChanges.forEach((id, change) -> {
                try {
                    writeToCSV(writer, new String[] {
                        id,
                        nameLookup.get(id),
                        String.valueOf(ratings.get(id) + change),
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("uuuu/MM/dd HH:mm:ss")),
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("uuuu/MM/dd HH:mm:ss"))
                    });
                } catch (IOException ex) {
                    Logger.getLogger(ELOProcessor.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
            writer.close(); 
        } catch(IOException ex) {
            Logger.getLogger(ELOProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
        ratingsFile.delete();
        tempFile.renameTo(ratingsFile);
    }
}