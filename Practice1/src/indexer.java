import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.snu.ids.kkma.index.Keyword;
import org.snu.ids.kkma.index.KeywordExtractor;
import org.snu.ids.kkma.index.KeywordList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class indexer{
	
	private File index;
	
	indexer(){};
	indexer(File file){
		index = file;
	}

	// Reading each charactor by BufferedReader & counting size
	public char read(BufferedReader bfr, int cnt) throws Exception {
		char ch = (char)bfr.read();
		bfr.mark(cnt);
		bfr.reset();
		cnt++;
		return ch;
	}
	
	// Changing char array to string
	public String charArrToStr(int size, char [] targetArr) {
		char [] charArr = new char[size];
		for(int k=0; k<size; k++) {
			charArr[k] = targetArr[k];
		}
		String str = new String(charArr);
		return str;
	}
	
	// Changing String to int
	public int strToInt(String str){
		return Integer.parseInt(str);
	}
	
	// make TF HashMap
	public HashMap<String, Integer> mkTF(char [] body_value, int size) throws IOException, ClassNotFoundException{
		HashMap<String, Integer> keyWord = new HashMap<> (); // TF HashMap
		String keyWordStr = "";
		String tfStr = "";
		
		// make keyword hashmap
		for(int j = 0; j<size; j++) { // data format -> "keyWordStr:tfStr# ..."
			if(body_value[j] == ':') { // reading tf data
				tfStr = ""; // initialize
				j++;
				while(body_value[j] != '#') {
					tfStr += body_value[j];
					j++;
				}
				keyWord.put(keyWordStr, strToInt(tfStr));
				keyWordStr = "";
			}
			else { // reading keyword data
				keyWordStr += body_value[j];
				continue;
			}
		}
		return keyWord;
	}
	
	// make DF HashMap
	public HashMap<String, Integer> mkDF(HashMap<String, Integer> TF0,HashMap<String, Integer> TF1,
			HashMap<String, Integer> TF2, HashMap<String, Integer> TF3, HashMap<String, Integer> TF4){
		
		HashMap<String, Integer> DF = new HashMap<>(); // DF HashMap
		Iterator<String> it_tf = TF0.keySet().iterator();
		
		for(int id = 0; id<5; id++) {
			if (id==1) it_tf = TF1.keySet().iterator();
			else if (id==2) it_tf = TF2.keySet().iterator();
			else if (id==3) it_tf = TF3.keySet().iterator();
			else if (id==4) it_tf = TF4.keySet().iterator();
			
			while(it_tf.hasNext()) {
				String key = it_tf.next();
				
				if (DF.containsKey(key)) { // case : already existing key
					DF.put(key, DF.get(key) + 1); // put 'old value + 1' at value
				}
				else { // case : new key
					DF.put(key, 1);
				}
			}
		}
		return DF;
	}
	
	// make TF_IDF HashMap
	public HashMap<String, List<String>> mkTF_IDF(HashMap<String, Integer> TF0,HashMap<String, Integer> TF1,
			HashMap<String, Integer> TF2, HashMap<String, Integer> TF3, HashMap<String, Integer> TF4,
			HashMap<String, Integer> DF){
		// <make tf_idf HashMap>
		HashMap<String, List<String>> tf_idf = new HashMap<>();
		HashMap<String, Integer> TF = TF0;
		Iterator<String> it_tf = TF.keySet().iterator();
		Iterator<String> it_df = DF.keySet().iterator();
		
		for(int id = 0; id<5; id++) {
			if (id==1) TF = TF1;
			else if (id==2) TF = TF2;
			else if (id==3) TF = TF3;
			else if (id==4) TF = TF4;
			it_tf = TF.keySet().iterator();
			
			while(it_tf.hasNext()) {
				List<String> idWeight_list = new ArrayList<String>(); // value of df_idf HashMap ([id weight id weight ... ])
				String key = it_tf.next(); // key of TF(num.id) HashMap
				double tf_value = (double)TF.get(key); // integer value of TF(num.id) HashMap -> type casting to double (for * eval)
				double df_value = (double)DF.get(key); // integer value of DF HashMap -> type casting to double (for \ eval)
				if(!tf_idf.containsKey(key)) {
					tf_idf.put(key,idWeight_list);
				}
				idWeight_list = tf_idf.get(key);
				// weight - 소수점 셋째자리에서 반올림
				double weight = Math.round(tf_value * Math.log(5.0/df_value)*100.0)/100.0;
				// add id and weight to value list
				idWeight_list.add(Integer.toString(id));
				idWeight_list.add(Double.toString(weight));
				// put value list to tf_idf HashMap
				tf_idf.put(key, idWeight_list);
			}
		}
		return tf_idf;
	}
	
	// make Inverted File
	public void mkInvertedFile() throws IOException, ClassNotFoundException, Exception {
		char temp = 0x00;
		int i = 0, m = 0, count = 0;
		char[] body_value = new char[10000];
		
		// make TF HashMap
		HashMap<String, Integer> TF0 = new HashMap<>();
		HashMap<String, Integer> TF1 = new HashMap<>();
		HashMap<String, Integer> TF2 = new HashMap<>();
		HashMap<String, Integer> TF3 = new HashMap<>();
		HashMap<String, Integer> TF4 = new HashMap<>();
		
		// make DF HashMap
		//File file_df = new File("df_test.post");
		HashMap<String, Integer> DF = new HashMap<>();
		
		// make tf_idf HashMap
		HashMap<String, List<String>> tf_idf  = new HashMap<>();
		File file = new File("index.post"); // output inverted file
				
		// input file
		FileInputStream input = new FileInputStream(index);
		InputStreamReader reader = new InputStreamReader(input,"UTF-8");
		BufferedReader br = new BufferedReader(reader);
		
		// output file
		FileOutputStream fileOutputStream = new FileOutputStream(file);
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
		
		// Reading body data -> char [] type
		for(int id = 0; id<5; id++) {
			i = 0; m = 0;
			temp = '\u0000';
			body_value[0] = '\u0000';
			
			while(true) {
				temp = read(br,count);
				// body
				if(temp=='y' && read(br,count)=='>') {
					while((temp = read(br,count)) != '<') {
						body_value[m] = temp;
						m++;
					}
					continue;
				}
				// /body> : breakpoint
				else if(temp=='/' && read(br,count)=='d' && read(br,count)=='o' && read(br,count)=='c' && read(br,count)=='>') {
					break;
				}
			}
			String b_value_str = charArrToStr(m, body_value); // type casting for checking body length
			
			// <make TF HashMap>
			switch(id) {
			case 0: TF0 = mkTF(body_value, b_value_str.length()); break;
			case 1: TF1 = mkTF(body_value, b_value_str.length()); break;
			case 2: TF2 = mkTF(body_value, b_value_str.length()); break;
			case 3: TF3 = mkTF(body_value, b_value_str.length()); break;
			case 4: TF4 = mkTF(body_value, b_value_str.length()); break;
			}
		}
		br.close();
		
		// <make DF HashMap>
		DF = mkDF(TF0, TF1, TF2, TF3, TF4);
		
		// <make tf_idf HashMap>
		tf_idf = mkTF_IDF(TF0, TF1, TF2, TF3, TF4, DF);
		
		// write tf_idf HashMap into file
		objectOutputStream.writeObject(tf_idf);
		objectOutputStream.close();
		
		// print tf_idf HashMap
		printHashMap(file);
	}
	
	// print HashMap<String, List<String>>
	public void printHashMap(File file) throws IOException, ClassNotFoundException {
		FileInputStream fileInputStream = new FileInputStream(file);
		ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
		
		Object object = objectInputStream.readObject();
		objectInputStream.close();
		
		System.out.println("\nFile name : " + file.getName());
		System.out.println("Read object type : " + object.getClass() + "\n");
		
		HashMap hashMap = (HashMap)object;
		Iterator<String> it = hashMap.keySet().iterator();
		
		while(it.hasNext()) {
			String key = it.next();
			List<String> value = (List<String>) hashMap.get(key);
			System.out.println(key + " -> " + value);
		}
	}
}
