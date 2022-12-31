/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Project/Maven2/JavaApp/src/main/java/${packagePath}/${mainClassName}.java to edit this template
 */

/*
ISSUES

- Explain differences between using maxent and perceptron algorithm (create .txt)
- en-sent.bin has to be 1.5.3!!
- manually remove errors ("Positive", "Negative"...)
- POS tagset doesn't match all words: VB, VBZ... so we just use VB -> USE REGEX (cambie la linea 247 y ya pilla todos)
- Folders inside project
- Fragment
-linea 130 y 247 para cambiar los VB
-linea 171 ponerlo bien
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
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetector;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import java.io.*;

public class WordCloudGeneratorApp {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        
//        final String csvPath = "C://Users//Jokin//Documents//NetBeansProjects//WordCloud//Conget.csv/";
//        final String sentDetectorBinaryPath = "C://Users//Jokin//Desktop//Libros Teoria//POO//en-sent.bin/";
//        final String tokenizerBinaryPath = "C://Users//Jokin//Desktop//Libros Teoria//POO//en-token.bin";
//        final String nameFinderBinaryPath = "C://Users//Jokin//Desktop//Libros Teoria//POO//en-ner-person.bin";        
//        final String maxentBinPath = "C://Users//Jokin//Desktop//Libros Teoria//POO//en-pos-maxent.bin";
//        final String perceptronBinPath = "C://Users//Jokin//Desktop//Libros Teoria//POO//en-pos-perceptron.bin";

//        final String csvPath = "C://Users//yago2//Documents//NetBeansProjects//WordCloudGeneratorApp//WordCloud//Conget.csv/";
//        final String sentDetectorBinaryPath = "C://Users//yago2//Desktop//en-sent.bin/";
//        final String tokenizerBinaryPath = "C://Users//yago2//Desktop//en-token.bin";
//        final String nameFinderBinaryPath = "C://Users//yago2//Desktop//en-ner-person.bin";        
//        final String maxentBinPath = "C://Users//yago2//Desktop//en-pos-maxent.bin";
//        final String perceptronBinPath = "C://Users//yago2//Desktop//en-pos-perceptron.bin";
//        final String outputPngPath; //WE WILL ASK FOR THIS LATER
        
        
        File fcsvPath = new File("Conget.csv");
        File fsentDetectorBinaryPath = new File("en-sent.bin");
        File ftokenizerBinaryPath = new File("en-token.bin");
        File fnameFinderBinaryPath = new File("en-ner-person.bin");        
        File fmaxentBinPath = new File("en-pos-maxent.bin");
        File fperceptronBinPath = new File("en-pos-perceptron.bin");
        
        final String csvPath = fcsvPath.getAbsolutePath();
        final String sentDetectorBinaryPath = fsentDetectorBinaryPath.getAbsolutePath();
        final String tokenizerBinaryPath = ftokenizerBinaryPath.getAbsolutePath();
        final String nameFinderBinaryPath = fnameFinderBinaryPath.getAbsolutePath();  
        final String maxentBinPath = fmaxentBinPath.getAbsolutePath();
        final String perceptronBinPath = fperceptronBinPath.getAbsolutePath();  
        
        final String outputPngPath; //WE WILL ASK FOR THIS LATER
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
        String posAlgo;
        
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
        
        if(typeOfWord.equals("1")) //THIS DEPENDS ON THE TAGSET USED BY POSTAGGERME!!!
        {
            typeOfWord = "RB";
        }
        else if(typeOfWord.equals("2"))
        {
            typeOfWord = "JJ";
        }
        else if(typeOfWord.equals("3"))
        {
            typeOfWord = "VB"; 
        }
        else//if(typeOfWord.equals("4")
        {
            typeOfWord = "NN";
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
  //              File foutputPngPath = new File();       
   //             final String outputPngPath = foutputPngPath.getAbsolutePath()+"//%s.png".formatted(fileName);
               outputPngPath = ".//%s.png".formatted(fileName);
                System.out.println(outputPngPath);
//                 outputPngPath = "C://Users//yago2//Desktop//%s.png".formatted(fileName);
//                outputPngPath = "C://Users//Jokin//Desktop//Libros Teoria//POO//%s.png".formatted(fileName);
                break;
            }
        }
        
        while(true)
        {            
            System.out.println("""
                               What POS algorithm would you like to use?
                               1 - Maximum Entropy (MaxEnt)
                               2 - Perceptron
                               """);
            
            posAlgo = sc.nextLine();
            if(!posAlgo.matches("1|2"))
            {
                System.out.println("Answer 1 or 2 please!");
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
                
                //INITIALIZING POS ALGORITHM TO IDENTIFY ADVERBS, SUBSTANTIVES...
                POSTaggerME posTagger;
                if(posAlgo.equals("1")) //INITIALIZE MAXENT ALGORITHM
                {
                    InputStream inputStreamMaxEnt = new FileInputStream(maxentBinPath);
                    POSModel posModelMaxEnt = new POSModel(inputStreamMaxEnt);
                    posTagger = new POSTaggerME(posModelMaxEnt);
                }
                else //INITIALIZE PERCEPTRON ALGORITHM
                {
                    InputStream inputStreamPeTron = new FileInputStream(perceptronBinPath); 
                    POSModel posModelPeTron = new POSModel(inputStreamPeTron);
                    posTagger = new POSTaggerME(posModelPeTron);
                }
                //================================================================================//                
                
                
                //================================================================================//  
                //LINE BY LINE, SENTENCE BY SENTENCE, TOKENIZE THE SENTENCE AND FIND THE WORDS YOU WANT
                //BE IT ADVERBS, SUBSTANTIVES, NAMES OR ANYTHING ELSE. STORE ITEMS THAT FIT OUT SEARCH ON
                //ANOTHER LIST (NAMESPANS BUT COULD BE VERBSPANS TOO). THIS NEEDS MORE WORK
                
                br = new BufferedReader(new FileReader(csvPath));
                String line = "";
                while((line = br.readLine()) != null) //FOR EVERY LINE IN THE CSV FILE
                {                    
                    String[] sentences = sentDetector.sentDetect(line);
                    for(String sentence : sentences) //FOR EVERY SENTENCE IN THE CURRENT LINE
                    {                                                                        
                        String[] tokens = tokenizer.tokenize(sentence); //TOKENIZE THE SENTENCE: ["i","like","the","movie"]                             
                        
                        for(String token : tokens)
                        {
                            //POSTaggerME.tag(token) returns "laugh/VB". we have to split to get the tag or the word
                            String candidate = posTagger.tag(token).split("/")[0];
                            String tag = posTagger.tag(token).split("/")[1];
//                            System.out.println("\nTAG: "+tag+"WORD: "+candidate);
                            
                            if(tag.startsWith(typeOfWord))
                            {            
  //                              System.out.println("true");
                                if (freqs.containsKey(candidate)) //WORD HAS ALREADY APPEARED. ADD 1 TO FREQUENCY
                                {
                                    WordFrequency wf = freqs.get(candidate);
                                    int frequency = wf.getFrequency();
                                    freqs.put(candidate, new WordFrequency(candidate, frequency+1));

                                }
                                else //FIRST APPEARANCE. CREATE NEW HASH WITH FREQUENCY = 1
                                {
                                    freqs.put(candidate, new WordFrequency(candidate, 1));
                                }                            
                            }                                                        
                        }                                                                        
                    }                                                                                                            
                }
                //================================================================================//                
                
               //WE NEED TO MANUALLY DELETE ERRORS. DEPENDS ON WHAT TYPE OF WORDS YOU WANT; THERE WILL BE DIFFERENT ISSUES LIKE "n't"
                
                if(typeOfWord.equals("RB")) //adverbs
                {
                    
                }
                else if(typeOfWord.equals("VB"))
                {
                    
                }
                else if(typeOfWord.equals("JJ")) //adjectives
                {
                    freqs.remove("negative");
                    freqs.remove("positive");
                }
                else//if(typeOfWord.equals("NN")
                {
                    
                }
                
                //================================================================================//                
                //we now have the frequencies for each candidate
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


