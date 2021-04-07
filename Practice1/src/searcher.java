import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.snu.ids.kkma.index.Keyword;
import org.snu.ids.kkma.index.KeywordExtractor;
import org.snu.ids.kkma.index.KeywordList;

public class searcher {
	private File postFile;
	private String query;
	private HashMap<String, Integer> queryWeight = new HashMap<>();
	
	// constructor
	searcher(File file, String str){
		this.postFile = file;
		this.query = str;
		System.out.println("postFile : " + postFile);
		System.out.println("query : " + query);
	}
	
	public void search() throws Exception{
		CalcSim(query, postFile);
		
		
	}
	
	public int[] CalcSim(String q, File file) throws Exception {
		// Reading object
		Object object = readFile(file);
		
		// Reading HashMap data
		HashMap hashMap = mkDoubleHash((HashMap)object);
		
		// Extract keywords and assign weight value 1
		int kwrdCnt = extractKwrds(q).size(); // keywordList.size()
		int [] similarity = new int [kwrdCnt]; // make int[keywords count]
		int docCnt = 5;
		printHashMap_int(queryWeight);
		
		// Get title value
		File title_file = new File("./collection.xml");
		String [] title = getTitle(title_file, docCnt);
		
		/*
		HashMap<String, List<Double>> temp = new HashMap<>();
		List<Double> t = Arrays.asList(0.25, 4.36, 2.0, 6.66, 0.0);
		
		temp.put("라면", t) ;
		printHashMap_listD(temp);
		System.out.println((temp.get("라면")).get(2));
		*/
		
		
		// Calculate document similarity
		// file HashMap : hashMap, query keywords HashMap : queryWeight
		// loop for docCnt
		for(int i = 0; i<docCnt; i++) {
			//similarity[i] = keywor dList[i]
		}
		
		
		
		
		return similarity;
	}
	

	public void sort() {
		
	}
	
	public HashMap<String, List<Double>> mkDoubleHash(HashMap hash) {
		HashMap<String, List<Double>> doubleHash = new HashMap<>(); // double HashMap has 10 elements(id:0~4)
		Iterator<String> it = hash.keySet().iterator();
		double [] weight = new double[5];
		
		while(it.hasNext()) { // if s HashMap's key exists
			String key = it.next(); // get key
			List<String> originValue = (List<String>) hash.get(key); // get value
			List<Double> newValue = new ArrayList<>();
			int i = 0;
			// here
			while(i<originValue.size()) {
				if(newValue.size() == 10) break;
				for(double id = 0.0; id < 5.0; id += 1.0) {
					if (Double.parseDouble(originValue.get(i))==id) { // if this id has a weight
						System.out.println("exist id!");
						newValue.add(id);
						newValue.add(Double.parseDouble(originValue.get(++i)));
					}
					else { // if this id doesn't have a weight
						System.out.println("there's no such a id!");
						newValue.add(id);
						newValue.add(0.0);
					}
					i++;
				}
			}
			doubleHash.put(key,newValue);
			System.out.println(key + " -> " + originValue);
			System.out.println(key + " -> " + newValue);
			// to hear again
		}
		
		return doubleHash;
	}
	
	public String [] getTitle(File file, int docCnt) throws Exception {
		String [] title = new String [5];
		char temp;
		int count = 0;
		FileInputStream input = new FileInputStream(file);
		InputStreamReader reader = new InputStreamReader(input,"UTF-8");
		BufferedReader br = new BufferedReader(reader);
		
		// Reading html data
		for(int id = 0; id<docCnt; id++) {
			temp = '\u0000';
			while(true) {
				temp = read(br,count);
				// Find title
				if(temp =='e' && read(br,count)=='>'){
					temp = read(br,count);
					title[id] = String.valueOf(temp);
					while((temp = read(br,count))!='<') {
						title[id] += String.valueOf(temp);
					}
					for(int k = 0; k<7; k++) read(br,count);
					break;
				}
			}
		}
		return title;
	}
	
	// Reading each charactor by BufferedReader
	public char read(BufferedReader bfr, int cnt) throws Exception {
		char ch = (char)bfr.read();
		bfr.mark(cnt);
		bfr.reset();
		cnt++;
		return ch;
	}
	
	public Object readFile(File file) throws ClassNotFoundException, IOException {
		// Reading object
		FileInputStream fileInputStream = new FileInputStream(postFile);
		ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
		
		Object object = objectInputStream.readObject();
		objectInputStream.close();
		
		System.out.println("Read object type : " + object.getClass());
		return object;
	}
	
	public KeywordList extractKwrds(String str) {
		// Extracting keywords
		KeywordExtractor ke = new KeywordExtractor();
		KeywordList kl = ke.extractKeyword(str, true);
		
		for(int c = 0; c<kl.size(); c++) {
			Keyword kwrd = kl.get(c);
			this.queryWeight.put(kwrd.getString(), 1);
			// kwrd.getCnt()
		}
		System.out.println();
		return kl;
	}
	
	// print HashMap<String, List<String>>
	public void printHashMap_list(HashMap<String, List<String>> hash){
		Iterator<String> it = hash.keySet().iterator();
		
		while(it.hasNext()) { // if s HashMap's key exists
			String key = it.next(); // get key
			List<String> value = (List<String>) hash.get(key); // get value
			System.out.println(key + " -> " + value);
		}
	}
	
	// print HashMap<String, List<Double>>
	public void printHashMap_listD(HashMap<String, List<Double>> hash){
		Iterator<String> it = hash.keySet().iterator();
		
		while(it.hasNext()) { // if s HashMap's key exists
			String key = it.next(); // get key
			List<Double> value = (List<Double>) hash.get(key); // get value
			System.out.println(key + " -> " + value);
		}
	}
	
	// print HashMap<String, Integer>
	public void printHashMap_int(HashMap<String, Integer> hash){
		Iterator<String> it = hash.keySet().iterator();
		
		while(it.hasNext()) { // if s HashMap's key exists
			String key = it.next(); // get key
			int value = (Integer)hash.get(key); // get value
			System.out.println(key + " -> " + value);
		}
	}
	
}
