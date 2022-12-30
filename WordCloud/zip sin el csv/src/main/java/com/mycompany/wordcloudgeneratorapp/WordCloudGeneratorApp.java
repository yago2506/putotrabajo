/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Project/Maven2/JavaApp/src/main/java/${packagePath}/${mainClassName}.java to edit this template
 */

/*
ISSUES
- Initializing bufferedReader to null;
- Explain differences between using maxent and perceptron algorithm
- en-sent.bin has to be 1.5.3!!
- sentencedetector model sometimes includes positive and negative as words from the user's input. should we remove them manually?
- around line 156 we have to tokenize depending on what the user has told us

*/


package com.mycompany.wordcloudgeneratorapp;

import com.kennycason.kumo.CollisionMode;
import com.kennycason.kumo.WordCloud;
import com.kennycason.kumo.WordFrequency;
import com.kennycason.kumo.bg.RectangleBackground;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.sentdetect.SentenceDetector;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.Span;

/**
 *
 * @author Jokin
 */
public class WordCloudGeneratorApp {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        
        final String csvPath = "C://Users//Jokin//Documents//NetBeansProjects//WordCloud//Conget.csv/";
        final String sentDetectorBinaryPath = "C://Users//Jokin//Desktop//Libros Teoria//POO//en-sent.bin/";
        final String tokenizerBinaryPath = "C://Users//Jokin//Desktop//Libros Teoria//POO//en-token.bin";
        final String nameFinderBinaryPath = "C://Users//Jokin//Desktop//Libros Teoria//POO//en-ner-person.bin";
        final String outputPngPath = "C://Users//Jokin//Desktop//Libros Teoria//POO//CongetYago.png";
        
        
        //================================================================================//
        //INITIALIZE CSV READER TO READ REVIEWS FROM CSV LINE BY LINE
        try
        {
            
            FileReader fileReader = new FileReader(csvPath);
            CSVReader csvReader = new CSVReader(fileReader);
            String[] nextRecord;
            while((nextRecord = csvReader.readNext()) != null)
            {
//                for(String cell : nextRecord)
//                {
//                    System.out.println(cell + "/t");
//                }
//                System.out.println();
            }
        }        
        catch(CsvValidationException | IOException e)
        {
            System.err.println("Exception caught @ readDataLineByLine: " + e);
            System.err.println("Message: " + e.getMessage());
            System.exit(1);
        }
        //================================================================================//
        
        
        //================================================================================//                
        //MENU. ASK USER FOR TYPE OF WORDS TO USE AND HOW TO NAME THE WORDCLOUD
        System.out.println("Welcome to the word cloud generator app!");
            
        String typeOfWord;
        String fileName;
        while(true)
        {
            System.out.println("What type of words would you like the cloud to have?");
            System.out.println("""
                           1 - Adverbs
                           2 - Adjectives
                           3 - Verbs
                           4 - Substantives
                           ---> """);
            typeOfWord = sc.nextLine();
            if(!typeOfWord.matches("^([1-4])$"))            
            {
                System.out.println("Write numbers from 1 to 4 both inclusive please!");
            }
            else
            {
                break;
            }                        
        }
        while(true)
        {
            System.out.println("How do you want to name the word cloud?");    
            fileName = sc.nextLine();
            if(!fileName.matches("^[a-zA-Z]+$"))
            {
                System.out.println("Please introduce another name!");
            }
            else
            {
                break;
            }
        }
        //================================================================================//                
        
                
        //================================================================================//  
        //OPEN CSV FILE AND INITIALIZE OPENNLP TOOLS. PROCESSING REVIEWS AND TOKENIZING
        BufferedReader br;
        try
        {            
            try 
            {
                Map<String,WordFrequency> freqs = new HashMap<String,WordFrequency>();                
                //================================================================================//                
                //LOAD MODELS FOR SENTENCE DETECTION, TOKENIZATION AND NAME DETECTION                   
                
                //INITIALIZING SENTENCE DETECTOR                
                InputStream inputStreamSent = new FileInputStream(sentDetectorBinaryPath); 
                SentenceModel sentModel = new SentenceModel(inputStreamSent); //model receives input from inputstream which is the model file binary
                SentenceDetector sentDetector = new SentenceDetectorME(sentModel); //SentenceDetector is the interface and SentenceDetectorME is an implementation of it.
                
                //INITIALIZING TOKENIZER
                InputStream inputStreamToken = new FileInputStream(tokenizerBinaryPath);
                TokenizerModel tokenModel = new TokenizerModel(inputStreamToken);
                Tokenizer tokenizer = new TokenizerME(tokenModel);                                
                                
                //INITIALIZING NAME DETECTOR
                InputStream inputStreamName = new FileInputStream(nameFinderBinaryPath);
                TokenNameFinderModel nameModel = new TokenNameFinderModel(inputStreamName);
                NameFinderME nameFinder = new NameFinderME(nameModel);                
                //================================================================================//                
                
                
                //================================================================================//  
                //LINE BY LINE, SENTENCE BY SENTENCE, TOKENIZE THE SENTENCE AND FIND THE WORDS YOU WANT
                //BE IT ADVERBS, SUBSTANTIVES, NAMES OR ANYTHING ELSE. STORE ITEMS THAT FIT OUT SEARCH ON
                //ANOTHER LIST (NAMESPANS BUT COULD BE VERBSPANS TOO). THIS NEEDS MORE WORK
                
                br = new BufferedReader(new FileReader("./Conget.csv"));
                String line = "";
                while((line = br.readLine()) != null) //FOR EVERY LINE IN THE CSV FILE
                {
                    String[] sentences = sentDetector.sentDetect(line);
                    for(String sentence : sentences) //FOR EVERY SENTENCE IN THE CURRENT LINE
                    {                        
                        String[] tokens = tokenizer.tokenize(sentence); //TOKENIZE THE SENTENCE
                        
                        Span nameSpans[] = nameFinder.find(tokens); //FIND WHATEVER WE WANT TO FIND AND STORE IT IN A LIST
                        
                        for(Span nameSpan : nameSpans) //WHAT THE FUCK IS A SPAN THOUGH
                        {
                            String candidateName = "";
                            
                            //loop for cases when we have consecutive names, in order to count it as ONE appearance of one name (Lewis Carroll)
                            for(int index = nameSpan.getStart(); index < nameSpan.getEnd(); index++)
                            {
                                candidateName += tokens[index] +" ";
                            }
                            
                            
                            //name frequencies management

                            if (freqs.containsKey(candidateName))
                            {
                                WordFrequency wf = freqs.get(candidateName);
                                int frequency = wf.getFrequency();
                                freqs.put(candidateName, new WordFrequency(candidateName, frequency+1));
                                
                            }
                            else
                            {
                                freqs.put(candidateName, new WordFrequency(candidateName, 1));
                            }                            
                        }
                    }                                                                                                            
                }
                //================================================================================//                
                
                
                //================================================================================//                
                //we now have the frequencies for each name
                final Dimension dimension = new Dimension(200,200);
                final CollisionMode collisionMode = CollisionMode.PIXEL_PERFECT;
                final WordCloud wordCloud = new WordCloud(dimension, collisionMode);
                
                //use freqs to obtain word frequencies
                wordCloud.build(new ArrayList<WordFrequency>(freqs.values()));
                wordCloud.setPadding(2);
                wordCloud.setBackground(new RectangleBackground(dimension));
                
                final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                wordCloud.writeToStreamAsPNG(byteArrayOutputStream);
                byteArrayOutputStream.writeTo(new FileOutputStream(outputPngPath));                                                                
                
            } 
            catch (FileNotFoundException ex) 
            {
                Logger.getLogger(WordCloud.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        catch(IOException e)
        {
            System.err.println("Exception class: " + e.getClass());
            System.out.println("Exception message: " + e.getMessage());
            System.exit(1);
        }                        
    }                                                        
}

