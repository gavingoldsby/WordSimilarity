package edu.uiowa.cs.similarity;

import java.io.*;
import java.util.*;
import java.lang.Object;
import java.math.*;
import java.text.DecimalFormat;
import java.util.stream.Collectors;


import opennlp.tools.stemmer.PorterStemmer;

import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

class mapOrderer implements Comparator<String> {

    Map<String, Double> base;
    public mapOrderer(Map<String, Double> base) {
        this.base = base;
    }
  
    @Override
    public int compare(String a, String b) {
        if (base.get(a) >= base.get(b)) {
            return -1;
        } else {
            return 1;
        }
    }
}
class wordVector {
    List<String> words;
    Map<String,Map<String,Integer>> wordMap = new HashMap();
    ArrayList wordList;
    List scatter = new ArrayList();
    List scatter2 = new ArrayList();
    

   
    wordVector(List<ArrayList<String>> startList ){
        
        //Method for converting a list of lists to one list
        words = startList.stream().flatMap(x -> x.stream()).collect(Collectors.toList()); 
        //Iterates through the sentences, split off into lists
        Map<String,Map<String,Integer>> workMap = new HashMap(words.size());
        Map<String,Integer> newWords = new HashMap(); 
                for(String each: words){
                   if(!newWords.containsKey(each)){
                        newWords.put(each,0);    //Enters through the one list, adding each word in the text to the submap
                   }
                     
                }
        
       Map<String, Integer> newerWords = new HashMap(words.size());
        
        for(List<String> lists : startList){ 
            //Iterates each word in a sentence
           
            for(String word:lists){
                newerWords.putAll(newWords);
                //Creates the sub-map that will hold the number of words in the sentence the key is in
                //if(newerWords.isEmpty()){
                for(String word2: words){
                    if(lists.contains(word2)){
                        newerWords.put(word2, 1);
                    }
                }
                
                //Puts our word and its submap into the final wordMap, which is out Semantic Descriptor Vector
                newerWords.put(word,0);
                workMap.put(word, newerWords);
              
                newerWords = new HashMap();
                
                
                
                
            }
            
            
                
        }
 ;
        wordMap.putAll(workMap);
    }
    void update(wordVector combining){
        HashMap<String,HashMap<String,Integer>> newMap = new HashMap();
        words.addAll(combining.words);
        
        for(String x :wordMap.keySet()){ //Iterates through each key in our wordMap
            if(combining.wordMap.containsKey(x)){ //Checks if our target vector contains that word as well
                for(String y : wordMap.get(x).keySet()){ //For each word in the key word's vector, the map containing words in the file and their values
                    if(combining.wordMap.get(x).containsKey(y)){ //if the selected key from our target also has the word in its vector
                        if(combining.wordMap.get(x).get(y)>0){ //If the word has a value, meaning our selected key word is placed in a sentence with it
                            wordMap.get(x).put(y, wordMap.get(x).get(y)+combining.wordMap.get(x).get(y)); //Updates our vector so that it's new values are merged with the target's
                        }
                        
                    }
             
                }
            }
            
            
        }
         for(String x :combining.wordMap.keySet()){ //Iterates through each key in our wordMap
            if(!wordMap.containsKey(x)){
                 wordMap.put(x,combining.wordMap.get(x));
            }
            
    }
         for(String x: wordMap.keySet()){
             for(String word:words){
                if(!wordMap.get(x).containsKey(word)){
                    
                    wordMap.get(x).put(word, 0);
                }
            }
         }
    }
    
    public Map getSimilar(String target,String type){
        Map similars = new HashMap();
        double[] vector1 = new double[wordMap.get(target).values().size()]; //new array with only the values from the targets map
        int count = 0;
       
        for(int i : wordMap.get(target).values()){
            vector1[count] = i; //this collects all the values and inserts them into the array
            count++;
        }
        
        for(String x : wordMap.keySet()){
            int count2 = 0;
            double[] vector2 = new double[wordMap.get(x).values().size()];
            for(int i : wordMap.get(x).values()){
            
            vector2[count2] = i; //this also collects the values for our second array, creating a new one for each words
            count2++;
        }
            
            if(type.equals("cosine")||type.equals("")){
            similars.put(x, cosineSimilarity(vector1, vector2)); //inserts the two arrays into the helper function to determine their similarity
            }                                                     //then puts them into a map with the word that was being compared
            else if(type.equals("euc")){
               similars.put(x,EUdistance(vector1,vector2));  
            }
            else if(type.equals("eucnorm")){
               similars.put(x,EUdistancenorm(vector1,vector2));  
            }
         }
        return similars;
    }
    public double EUdistance (double[] vector1, double[] vector2) {
    
    if(vector1.length != vector2.length){
            throw new java.lang.Error("Vectors do not have similar lengths");
        }
        else{
        
        
        
        double Sum = 0.0;
        for(int i=0;i<vector1.length;i++) {
           Sum = Sum + Math.pow((vector1[i]-vector2[i]),2.0);
        }
        return -(Math.sqrt(Sum));
    
        }
    
}
      public double EUdistancenorm (double[] vector1, double[] vector2) {
    
    if(vector1.length != vector2.length){
            throw new java.lang.Error("Vectors do not have similar lengths");
        }
        else{
        
         
        double v1Sum = 0.0;
        double v2Sum = 0.0;
        double Sum = 0.0;
        for(int i=0; i<vector1.length;i++){
            v1Sum += Math.pow(vector1[i],2);
            v2Sum += Math.pow(vector2[i],2);
        }
        double v1Mag = Math.sqrt(v1Sum);
        double v2Mag = Math.sqrt(v2Sum);
        for(int i=0;i<vector1.length;i++) {
         
           double v1 = vector1[i];
           double v2 = vector2[i];
           Sum += Math.pow(((v1/v1Mag)-(v2/v2Mag)),2.0);
           
        }
        return -(Math.sqrt(Sum));
    
        }
    
}
    public double cosineSimilarity(double[] vector1, double[] vector2){
        double dotProduct = 0;
        double mag1 = 0;
        double mag2 = 0;
        if(vector1.length != vector2.length){
            throw new java.lang.Error("Vectors do not have similar lengths");
        }
        else{
        
        
        for(int i = 0; i<vector1.length; i++){
            dotProduct += vector1[i] * vector2[i]; //These three lines do the summation for figuring out the cosine similarity
            mag1 += Math.pow(vector1[i], 2);
            mag2 += Math.pow(vector2[i],2);
            
            
        }
        }
        
        
        
        return Double.valueOf(dotProduct/(Math.sqrt(mag1 * mag2))); //returns a rounded decimal of the final division for the cosine similarity
    }
    
    //Part 3 & 4
    public Map Top(Map<String,Double> Q, int j, String type){
        //rounds the decimal to two places
        DecimalFormat twoD = new DecimalFormat("#.##"); 
        //Returns the top j similar words
        Map<String, Double> similars = new HashMap();
        //Contains the word that is the most similar in Q
        Map<String, Double> max = new HashMap();
        String x = "count";
        max.put(x, -50.0);
        int count =0;
        if(type.equals("")){
        while(count<j){
            for(String i: Q.keySet()){
                //If the word is the word you are topjing then it will skip it
                if(Q.get(i)==1.0){
                    continue;
                }
                else if(max.get(x) <= Q.get(i)){
                   //max is changing to string i
                   max.remove(x);
                   x = i;
                   max.put(x, Q.get(i));
                   
                }
               
               }
            //removes the max from Q so it will not be the same
            Q.remove(x);
            //Adds the max to similars
            similars.put(x, Double.valueOf(twoD.format(max.get(x))));
            //Resets max
            max.remove(x);
            x = "count";
            max.put(x, 0.0);
          
            count++;
            }
        }
        else if(type.equals("euc")||type.equals("eucnorm")){
        while(count<j){
            for(String i: Q.keySet()){
                //If the word is the word you are topjing then it will skip it
                if(Q.get(i)==0.0 || Q.get(i)==-0.0){
                    continue;
                }
                if(max.get(x) <= Q.get(i)){
                   //max is changing to string i
                  
                   max.remove(x);
                   x = i;
                   max.put(x, Q.get(i));
                   
                }
               
               }
            //removes the max from Q so it will not be the same
            Q.remove(x);
            //Adds the max to similars
            similars.put(x, Double.valueOf(twoD.format(max.get(x))));
            //Resets max
            max.remove(x);
            x = "count";
            max.put(x, -50.0);
          
            count++;
            }
        
        }
        return similars;
        }
    
    public List<List<String>> kMeansMain(int listNum, int iterNum){
        List<String> keys = new ArrayList(wordMap.keySet());
        List<double[]> centers = new ArrayList();
        List<List<String>> finale = new ArrayList();
        
        for(int i = 0; i<listNum; i++){

        List test = new ArrayList();
        int count = 0;
        Random random = new Random();
        
        String randomKey = keys.get( random.nextInt(keys.size()) );
        Map<String,Integer> value= wordMap.get(randomKey);
        double[] vector1 = new double[value.values().size()];
        for(int j : value.values()){
            
            vector1[count] = j; //this collects all the values and inserts them into the array
            count++;
        }
        centers.add(vector1);
        
        test.add(randomKey);
        finale.add(test);
        keys.remove(randomKey);
        }
        
      
        for(int i = 0; i<iterNum; i++){
            
            finale = kMeans(centers, listNum);
            centers = kmeansHelper(finale);
        }
        return finale;
    }
    public List<List<String>> kMeans(List<double[]> centers, int listNum){
        
        Map<String,Map<String,Integer>> H = this.wordMap;
        List<List<String>> clusters = new ArrayList();
        List<String> keys = new ArrayList(H.keySet());
        List<String> newKeys = new ArrayList();
        newKeys.addAll(keys);
        for(int x = 0; x<listNum; x++){
            List<String> myList = new ArrayList();
            clusters.add(myList);
        }
       
        
      /*  */
     
        for(String word : keys){
            
            String word2 = "";
          
           
            double[] vector2 = new double[H.get(word).values().size()];
            int count2 = 0;
            int count3 = 0;
            double max = -20.0;
            for(int j: H.get(word).values()){
                vector2[count2]=j;
                count2++;
            }
           
            for(double[] center : centers){
                double holder = this.EUdistance(vector2, center);
              
                if(holder > max){
                    max = holder;
                    
                }
             
               
                
            }
           
            
            
            for(double[] center : centers){
               
                double holder = this.EUdistance(vector2, center);
                if(holder == max){
                        if(newKeys.contains(word)){
                        clusters.get(count3).add(word);
                        scatter.add(max);
                        scatter2.add(count3);
                        newKeys.remove(word);
                       
                        }
                }
               
                count3++;
                
                }
                      
            }
            
            
           
        
        return clusters;
    }
   
        
    public List<double[]> kmeansHelper(List<List<String>> words ){
        Map<String,Map<String,Integer>> H = this.wordMap;
        List<double[]> finale = new ArrayList();
        for(List list : words){
                double mean = 0.0;
                double[] newV = new double[H.size()];
                //System.out.println(list);
                for(int i = 0; i<H.size(); i++){
                    double sum=0.0;
                    for(Object word : list){
                        
                        sum += (int) H.get(word).values().toArray()[i];
                        sum = sum/newV.length;
                    }
                    newV[i]=sum;
                    
                }
                finale.add(newV);
            }
        return finale;
    }
    
    
    //Part 2 returns vectors
    public Map getMap(){
        //The map that will hold all the vectors and its values 
        Map<String,Map<String,Integer>> done = new HashMap();
        
        for(String x: wordMap.keySet()){
            //Will hold the words that have a value >0 for the main vector
            //Will reset for each vector so values don't carry over
          
            Map<String,Integer> value = new HashMap();
            for(String y: wordMap.get(x).keySet()){
                //If the words value is > 0 then
                if(wordMap.get(x).get(y) > 0){
                    
                    value.put(y, wordMap.get(x).get(y));
                }
            }
            done.put(x, value);
            
        }
        return done;
    }
            
           
}
class ScatterPlot extends JFrame{
        private static final long serialVersionUID = 6294689542092367723L;

        public ScatterPlot(List count, List e, int num){
            super("Kmeans Scatter Plot");
            
            //Create dataset
            XYDataset dataset = createDataset(count,e,num);
            
            //Create chart 
            JFreeChart chart = ChartFactory.createScatterPlot("kmeans comparison","Position in list", "Euclidean Distance", dataset);
            
            //Changes background color
            XYPlot plot = (XYPlot)chart.getPlot();
            plot.setBackgroundPaint(new Color(255,228,196));
            
            //Create Panel
            ChartPanel panel = new ChartPanel(chart);
            setContentPane(panel);
        }
        
        private XYDataset createDataset(List<Integer> count, List<Double> e, int num){
            XYSeriesCollection dataset = new XYSeriesCollection();
            List<List<Double>> data = new ArrayList();
            for(int i =0; i< num;i++){
                data.add(new ArrayList());
            }
            for (int i =0;i<count.size();i++){
                data.get(count.get(i)).add(e.get(i));
            }
            for(int i =0;i<data.size();i++){
                XYSeries series = new XYSeries("Cluster "+i);
                
                for(int j=0;j<data.get(i).size();j++){
                    series.add(j, data.get(i).get(j));
                    
                }
                dataset.addSeries(series);
                }
            
            return dataset;
        }
        
        
            
}
public class Main {
        
        private static List<ArrayList<String>> finalsentences = new ArrayList();
        private static wordVector SDVector;
        public static List index(String FILE){
             
             
             List sentences=new ArrayList();
            
              
             
              try{
             BufferedReader fileIn = new BufferedReader(new FileReader(FILE));
             Scanner sc=new Scanner(fileIn);
             sc.useDelimiter("[?!.]");
           
             while (sc.hasNext()){
                  String next=sc.next();
                  
                  sentences.add(next);
                 
             }
             }
              catch (FileNotFoundException e) {
                System.out.println("FILE NOT FOUND");
            }
             
             return sentences;
        }
	
        
        
        //Will be used in the cleaning up of the words
        //Will compare all the words in input and will not return the duplicates
        public static List capital(List<String> input){
            
            
           List<String> finale = new ArrayList();
            for(String x : input){
               /* List<String> lower = new ArrayList();
                String[] words = x.split(" ");
            
                for(String z : words){
                 */   
                    
                    
                    finale.add(x.toLowerCase());
                    
                    
                    
                }
               
                
            
            
            return finale;
        }
        
        //Will be used in the cleaning up of the words
        //Will find the roots of all the words by using "stemming"
        public static List rootWords(List<ArrayList<String>> input){
            PorterStemmer s = new PorterStemmer();
            List<List> f = new ArrayList();
            
            for(List<String> l: input){
                List n = new ArrayList();
                for(String i: l){
                    String[] words = i.split("\\s+ ");
                    for(String x: words){
                        //System.out.println(x);
                        n.add(s.stem(x));
                    }
                }
                f.add(n);
            }
            return f;
        }

        
        

       
         
         
        
        private static void printMenu() {
		System.out.println("Supported commands:");
		System.out.println("help - Print the supported commands");
		System.out.println("quit - Quit this program");
                System.out.println("index FILE - Read in and index the file given by FILE");
                System.out.println("sentences - Prints all sentences");
                System.out.println("num sentences - Prints the total number of sentences");
                System.out.println("vectors - Prints all words and the words that are used in a sentence with said word");
                System.out.println("topj word i - Prints i words that are closest to word (default measurer is cosine similarity)");
                System.out.println("measure cosine - TopJ will measure words using cosine similarity");
                System.out.println("measure euc - TopJ will measure words using Euclidian Distance");
                System.out.println("measure eucnorm - TopJ will measure words using Euclidian Distance Normalized");
                System.out.println("kmeans i j - Prints i clusters of similar words, iterating over the clusters j times for maximum accuracy");
	}
       public static List stopWords(List<String> input){
            File file=new File("stopwords.txt");
            System.out.println("File is being processed.....");
            String[] punct={";",",","--",":","'"};
            Character[] punct2 = {'"'};
             List<String> stopwords = new ArrayList();
             List<List<String>> finale = new ArrayList<>();
              try{
             Scanner sc=new Scanner(file);
             
           
             while (sc.hasNext()){
                 
                 
                  stopwords.add(sc.next());
                 }
                   
             }
             
              catch (FileNotFoundException e) {
                System.out.println("StopWords file not found, you may have to specify it for your machine.");
            }
            
            for (String x: input){
                List<String> lower = new ArrayList();
                String[] words = x.split("\\s+");
             
                for(String z : words){
                
                if(!stopwords.contains(z)){
                       
                       
                      for(String k : punct){
                           if(z.contains(k)){
                                z=z.replace(k,"");
                           }
                           if(z.contains("\"")){
                                z=z.replace("\"","");
                           }
                           
                      }
                       if(!z.isEmpty()){
                            lower.add(z);
                       }
                       }
                    }
                if(!lower.isEmpty()){
                finale.add(lower);
                }
            }
            return finale;
       }
       
    public static void main(String[] args) throws IOException {
		BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
                 String measurer = "";
		while (true) {
                       
    			System.out.print("> ");
			String command = input.readLine();
                        String[] split = command.split(" ");        
			if (command.equals("help") || command.equals("h")) {
				printMenu();
			} else if (command.equals("quit")) {
				System.exit(0);
			}
                        else if(split[0].equals("index")) {
                         if(command.equals("index " + split[1]) || command.equals("index" + split[1])){
                            System.out.println("Indexing "+split[1]);
                            List<String> test=index(split[1]); 
                            List<String> lower = capital(test);
                           
                            List<ArrayList<String>> stoppers = stopWords(lower);
                            List<ArrayList<String>> cut = rootWords(stoppers);
                            finalsentences.addAll(cut); 
                            if(SDVector == null){
                            SDVector = new wordVector(cut);
                            
                            }
                            else{
                                wordVector Holder = new wordVector(cut);
                                SDVector.update(Holder);
                                
                            }
                            
                            
                                }
                        }
                        else if(command.equals("sentences")){
                            System.out.println(SDVector.wordMap);
                            System.out.println(finalsentences);
                        }
                        else if(command.equals("num sentences") || command.equals("Num sentences")){
                            System.out.println(finalsentences.size());
                        }
                        else if(command.equals("vectors")){
                             System.out.println(SDVector.getMap());
                             
                        }
                        else if(split[0].equals("measure")){
                             if(split[1].equals("cosine")){
                                 System.out.println("Similarity measure is cosine similarity");
                                 measurer = split[1];
                             }
                             if(split[1].equals("euc")){
                                  System.out.println("Similarity measure is negative euclidean distance");
                                  measurer = split[1];
                             }
                             else if(split[1].equals("eucnorm")){
                                 System.out.println("Similarity measure is negative euclidean distance between norms");
                                  measurer = split[1];
                             } 
                        }                             
                        else if(split[0].equals("topj")){
                             if(SDVector.wordMap.get(split[1])==null){
                                  System.err.println("Cannot compute top-J similarity to "+split[1]);
                             }
                             
                             
                             else{      
                            Map<String,Double> v = SDVector.Top(SDVector.getSimilar(split[1],measurer), Integer.valueOf(split[2]), measurer);
                            System.out.println(v);
                             }
                             }
                        else if(split[0].equals("kmeans")){
                            List<List<String>> k = new ArrayList();
                            k = SDVector.kMeansMain(Integer.valueOf(split[1]), Integer.valueOf(split[2]));
                            for(int i=0;i < Integer.valueOf(split[1]);i++){
                                System.out.println("Cluster "+i);
                                for (int j = 0; j< k.get(i).size();j++){
                                    System.out.print(k.get(i).get(j) + ", ");
                                }
                                System.out.println("");
                            }
                            
                            ScatterPlot plot = new ScatterPlot(SDVector.scatter2, SDVector.scatter, Integer.valueOf(split[1]));
                            plot.setSize(800,400);
                            plot.setLocationRelativeTo(null);
                            plot.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                            plot.setVisible(true);
                        }
                        else {
				System.err.println("Unrecognized command");
			}
		}
    }
}

