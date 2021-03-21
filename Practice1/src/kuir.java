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
		makeCollection mkColl = new makeCollection();
		mkColl.makeColl();
		//makeKeyword mkKwd = new makeKeyword();
			/*
			KeywordExtractor ke = new KeywordExtractor();
			KeywordList kl = ke.extractKeyword(b_value_str, true);
			for(int c = 0; c<kl.size(); c++) {
				Keyword kwrd = kl.get(c);
				System.out.println(kwrd.getString()+"\t"+kwrd.getCnt());
			}*/
			
		
		
		/*
		BufferedInputStream in = null;
		OutputStream out = null;
		
		in = new BufferedInputStream(new FileInputStream(new File("collection.xml")));
		
		out = new FileOutputStream("index.xml");
		
		int s1 = -1;
		while((s1=in.read())!=-1) out.write(s1);
		*/
	}

}
