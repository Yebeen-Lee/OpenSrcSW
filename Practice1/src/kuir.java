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
		
		String option = args[0];
		System.out.println("option : " + option + "start!");
		
		String filePath = args[1];
		File fileOrDir = new File(filePath);
		System.out.println("filePath : "+filePath);
		
		// makeCollection
		if(option.equals("-c")) {
			// ./src/sample_html
			makeCollection mkColl = new makeCollection(fileOrDir);
			mkColl.makeColl();
			System.out.println("makeCollection succeeded!");
		}
			
		// makeKeyword
		else if(option.equals("-k")) {
			// ./collection.xml
			//File file = new File(./collection.xml);
			makeKeyword mkKwd = new makeKeyword(fileOrDir);
			mkKwd.makeKeywrd();
			System.out.println("makeKeyword succeeded!");
		}
	}
}
