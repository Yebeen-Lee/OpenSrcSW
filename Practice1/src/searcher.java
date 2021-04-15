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
		System.out.println("query : " + query + "\n");
	}
	
	public void search() throws Exception{
		sort(CalcSim(query, postFile),5); // query�� �� �������� ���絵 ��� ��, top3�� title ���
	}
	
	public double[] CalcSim(String q, File file) throws Exception {
		// Reading object from post file
		Object object = readFile(file);
		
		// TypeCasting an object to HashMap & make HashMap<String, List<String>> to HashMap<String, List<Double>>
		HashMap hashMap = mkDoubleHash((HashMap)object); // hashMap<String, List<Double>>
		
		// Extract keywords and assign weights(value:1)
		int kwrdCnt = extractKwrds(q).size(); // keywordList.size()
		int docCnt = 5;
		double [] cosSim = new double [docCnt]; // int[keywords count]
		for(int i = 0; i<cosSim.length; i++) cosSim[i] = 0; // initialize cosSim array
		
		double [] innerProductSim = InnerProduct(query, postFile); // innerproduct similarites
		
		// Calculate document cosine similarities
		// (file HashMap : hashMap, query keywords HashMap : queryWeight)
		Iterator<String> it = queryWeight.keySet().iterator();
		String qKey; // queryWeight's current key
		List<Double> newValue; // size:docCnt, currently typecasted value List<Double>
		// loop for docCnt
		for(int id = 0; id<docCnt; id++) {
			it = queryWeight.keySet().iterator(); //queryWeight's keySet iterator
			double currQSize = 0; // |Q|
			double currIdSize = 0; // |current id|
			while(it.hasNext()) { // while queryWeight has a next key
				qKey = it.next();
				newValue = (List<Double>)hashMap.get(qKey); // get current key's value List<String> & typecast to List<Double>
				currQSize += Math.pow(queryWeight.get(qKey),2);
				currIdSize += Math.pow(newValue.get(id),2);
			}
			cosSim[id] = innerProductSim[id]/(Math.sqrt(currQSize)*Math.sqrt(currIdSize));
			cosSim[id] = Math.round(cosSim[id]*100)/100.0; // �Ҽ��� ��°�ڸ����� �ݿø�
			//System.out.println("cosSim["+id+"]: "+cosSim[id]+" (rounded)"); // cosSim[] �� ���
		}
		return cosSim;
	}


	// Calculate document similarity
	public double[] InnerProduct(String q, File file) throws Exception {
		// Reading object from post file
		Object object = readFile(file);
		
		// TypeCasting an object to HashMap & make HashMap<String, List<String>> to HashMap<String, List<Double>>
		HashMap hashMap = mkDoubleHash((HashMap)object); // hashMap<String, List<Double>>
		
		// Extract keywords and assign weights(value:1)
		int kwrdCnt = extractKwrds(q).size(); // keywordList.size()
		int docCnt = 5;
		double [] sim = new double [docCnt]; // int[keywords count]
		for(int i = 0; i<sim.length; i++) sim[i] = 0; // initialize sim array
		
		// Calculate document similarities
		// (file HashMap : hashMap, query keywords HashMap : queryWeight)
		Iterator<String> it = queryWeight.keySet().iterator();
		String qKey; // queryWeight's current key
		List<Double> newValue; // size:docCnt, currently typecasted value List<Double>
		// loop for docCnt
		for(int id = 0; id<docCnt; id++) {
			it = queryWeight.keySet().iterator(); //queryWeight's keySet iterator
			while(it.hasNext()) { // while queryWeigth has a next key
				qKey = it.next();
				newValue = (List<Double>)hashMap.get(qKey); // get current key's value List<String> & typecast to List<Double>
				sim[id] += queryWeight.get(qKey)*newValue.get(id); // similarities evaluated by inner product
			}
			sim[id] = Math.round(sim[id]*100)/100.0; // �Ҽ��� ��°�ڸ����� �ݿø�
			//System.out.println("sim["+id+"]: "+sim[id]+" (rounded)"); // sim[] �� ���
		}
		return sim;
	}
	
	// Sort document similarities and print top3 doc titles
	public void sort(double [] sim, int docCnt) throws Exception {
		// <Sorting>
		int temp_i = 0, k = 0;
		double temp = 0.0;
		int [] top5_id = {0, 1, 2, 3, 4}; // documents' id ������ ������ �迭
		// �������� ����(ū ��->���� ��, ���� ������ ��� id ���� ���� �켱)
		for(int id = 0; id<docCnt; id++) {
			for(int j = 0; j<(docCnt-1); j++) {
				if(sim[j]<sim[j+1]) {
					// sim �迭�� �ε����� �������� ����
					temp_i =  top5_id[j];
					top5_id[j] = top5_id[j+1];
					top5_id[j+1] = temp_i;
					// sim �迭�� �������� ����
					temp = sim[j];
					sim[j] = sim[j+1];
					sim[j+1] = temp;
				}
			}
		}
		
		// <Printing>
		// ������ top5_id[] ���
		//for(int i = 0; i<top5_id.length; i++) System.out.println("top index["+i +"]: "+top5_id[i]);
		
		// Get title value
		File title_file = new File("./collection.xml");
		String [] title = getTitle(title_file, docCnt);
		// printing top3 doc titles	
		System.out.println("< \""+query+"\" document similarity >");
		for(int i = 0; i<3; i++) System.out.println("Top"+(i+1)+"'s title : " + title[top5_id[i]]);
	}
	
	// change HashMap<String, List<String>> to HashMap<String, List<Double>>
	public HashMap<String, List<Double>> mkDoubleHash(HashMap hash) {
		HashMap<String, List<Double>> doubleHash = new HashMap<>(); 
		Iterator<String> it = hash.keySet().iterator();
		
		while(it.hasNext()) { // if s HashMap's key exists
			String key = it.next(); // get key
			List<String> originValue = (List<String>) hash.get(key); // get value, originValue's size:10(id, weight, id, weight, ...)
			List<Double> newValue = new ArrayList<>(); // size:5(index==id:0~4)(weight, weight, weight, weight, weight)
			int i = 0;
			for(double id = 0.0; id < 5.0; id += 1.0) {
				if (i==originValue.size()) { // originValue List�� �� �̻� id, value ���� ������ newValue List ������ 0.0 weight�� ä���
					while(newValue.size()<5) newValue.add(0.0);
					break;
				}
				if(Double.parseDouble(originValue.get(i))==id) { // originValue List�� id�� ������ �ش� weight�� newValue.add()
					newValue.add(Double.parseDouble(originValue.get(++i)));
					i++;
				}
				else newValue.add(0.0); // originValue List�� id ������ 0.0���� weight ä���
			}
			doubleHash.put(key,newValue); // �ش� Ű������ double weight list�� newValue�� doubleHash.put()
		}
		return doubleHash;
	}
	
	// Read collection.xml and get titles of documents
	public String [] getTitle(File file, int docCnt) throws Exception {
		String [] title = new String [docCnt];
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
	
	// Read a postfile and return an object
	public Object readFile(File file) throws ClassNotFoundException, IOException {
		// Reading object
		FileInputStream fileInputStream = new FileInputStream(postFile);
		ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
		
		Object object = objectInputStream.readObject();
		objectInputStream.close();
		
		System.out.println("Read object type : " + object.getClass());
		return object;
	}
	
	// return a KeywordList of an input string
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
