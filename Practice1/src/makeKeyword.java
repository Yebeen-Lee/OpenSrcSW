import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
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

public class makeKeyword {
	
	File collection;
	
	makeKeyword(){};
	makeKeyword(File file){
		collection = file;
	};
	
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
	
	public void makeKeywrd() throws Exception {
		char temp = 0x00;
		int i = 0, m = 0, count = 0;
		char[] title_value = new char[10];
		char[] body_value = new char[10000];
		String keyWordStr = "";
		
		// Making Document, element instances
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		
		Document docu = docBuilder.newDocument();
		
		Element docs = docu.createElement("docs");
		docu.appendChild(docs);
		
		// Keyword Extractor
		KeywordExtractor ke = new KeywordExtractor();
		
		FileInputStream input = new FileInputStream(collection);
		InputStreamReader reader = new InputStreamReader(input,"UTF-8");
		BufferedReader br = new BufferedReader(reader);
		
		// Reading html data
		for(int id = 0; id<5; id++) {
			temp = '\u0000';
			i = 0; m = 0;
			title_value[0] = '\u0000';
			body_value[0] = '\u0000';
			keyWordStr = "";
			
			while(true) {
				temp = read(br,count);
				// title
				if(temp =='e' && read(br,count)=='>'){
					System.out.println("title"+id);
					while((temp=read(br,count))!='<') {
						title_value[i] = temp;
						i++;
					}
					for(int k = 0; k<7; k++) read(br,count);
					continue;
				}
				// body
				else if(temp=='y' && read(br,count)=='>') {
					System.out.println("body"+id);
					while((temp = read(br,count)) != '<') {
						body_value[m] = temp;
						m++;
					}
					continue;
				}
				// /body> : breakpoint
				else if(temp=='/' && read(br,count)=='d' && read(br,count)=='o' && read(br,count)=='c' && read(br,count)=='>') {
					System.out.println("break! "+id);
					break;
				}
			}
			
			String t_value_str = charArrToStr(i, title_value);
			String b_value_str = charArrToStr(m, body_value);
			
			// Extracting keywords
			KeywordList kl = ke.extractKeyword(b_value_str, true);
			for(int c = 0; c<kl.size(); c++) {
				Keyword kwrd = kl.get(c);
				keyWordStr += kwrd.getString()+":"+kwrd.getCnt()+"#";
			}

			// Inserting data into xml file
			Element doc = docu.createElement("doc");
			docs.appendChild(doc);
			
			doc.setAttribute("id", Integer.toString(id));
			
			Element title = docu.createElement("title");
			title.appendChild(docu.createTextNode(t_value_str));
			doc.appendChild(title);
			
			Element body = docu.createElement("body");
			body.appendChild(docu.createTextNode(keyWordStr));
			doc.appendChild(body);
			
		}
		br.close();
		
		// Making xml file using transformerFactory, DOMSource instances
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
		
		DOMSource source = new DOMSource(docu);
		StreamResult result = new StreamResult(new FileOutputStream(new File("./index.xml")));
		
		transformer.transform(source,  result);
	}
}
