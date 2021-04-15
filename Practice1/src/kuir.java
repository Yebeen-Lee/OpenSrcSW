import java.io.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

//import org.snu.ids.kkma.index.Keyword;
//import org.snu.ids.kkma.index.KeywordExtractor;
//import org.snu.ids.kkma.index.KeywordList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class kuir {
	
	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		
		// error
		if (args.length <2) {
			System.out.println("You need to enter two elements at least.");
			System.exit(0);
		}
		
		// get option&path data
		String option = args[0];
		System.out.println("option : " + option + " start!");
		
		String filePath = args[1];
		File fileOrDir = new File(filePath);
		System.out.println("filePath : "+filePath + "\n");
		
		String option2 = "";
		option2 = args[2];
		
		// 라면에는 면, 분말 스프가 있다.
		String query = "";
		query = args[3];
		
		// makeCollection
		if(option.equals("-c")) {
			// filepath : ./src/sample_html
			makeCollection mkColl = new makeCollection(fileOrDir);
			mkColl.makeColl();
			System.out.println("\n makeCollection succeeded!");
		}
			
		// makeKeyword
		else if(option.equals("-k")) {
			// filepath : ./collection.xml
			makeKeyword mkKwd = new makeKeyword(fileOrDir);
			mkKwd.makeKeywrd();
			System.out.println("\n makeKeyword succeeded!");
		}
		
		// indexer
		else if(option.equals("-i")) {
			// filepath : ./index.xml
			indexer indxer = new indexer(fileOrDir);
			indxer.mkInvertedFile();
			System.out.println("\n indexer succeeded!");
		}
		
		// searcher
		else if(option.equals("-s")) {
			// filepath : ./index.post
			if (option2.equals("-q")) {
				query = getString(args); // get String query
				searcher srcher = new searcher(fileOrDir, query);
				srcher.search();
				System.out.println("\n searcher succeeded!");
			}
			else {
				System.out.println("You need to input more options to implement searcher!");
			}
		}
	}
	
	public static String getString(String [] args) {
		String query = args[3];
		for(int i = 4; i<args.length; i++) query+=(" "+args[i]); 
		return query;
	}
}
