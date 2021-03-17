import java.io.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

class Main{
	
	static char read(BufferedReader bfr, int cnt) throws Exception {
		char ch = (char)bfr.read();
		bfr.mark(cnt);
		bfr.reset();
		cnt++;
		return ch;
	}
	
	public static void main(String [] args) throws Exception{
		
		char temp = 0x00;
		int i = 0, m = 0, count = 0;
		char[] title_value = new char[10];
		char[] body_value = new char[10000];
		
		File dir = new File("sample_html");
		File htmls[] = dir.listFiles();

		// Making Document, element instants
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		
		Document docu = docBuilder.newDocument();
		
		Element docs = docu.createElement("docs");
		docu.appendChild(docs);
		
		// Reading html data
		for(int j = 0; j<5; j++) {
			temp = '\u0000';
			i = 0; m = 0; count = 0;
			title_value[0] = '\u0000';
			body_value[0] = '\u0000';
			
			FileInputStream input = new FileInputStream(htmls[j]);
			InputStreamReader reader = new InputStreamReader(input,"UTF-8");
			BufferedReader br = new BufferedReader(reader);
			while(true) {
				temp = read(br,count);
				if(temp =='e' && read(br,count)=='>'){
					while((temp=read(br,count))!='<') {
						title_value[i] = temp;
						i++;
					}
					for(int k = 0; k<7; k++) read(br,count);
					continue;
				}
				
				else if(temp=='p' && read(br,count)=='>') {
					while((temp = read(br,count)) != '<') {
						body_value[m] = temp;
						m++;
					}
					continue;
				}
				else if(temp=='/' && read(br,count)=='h' && read(br,count)=='t') {
					break;
				}
			}
			char [] t_value = new char[i];
			for(int k=0; k<i; k++) {
				t_value[k] = title_value[k];
			}
			char [] b_value = new char[m];
			for(int k=0; k<m; k++) {
				b_value[k] = body_value[k];
			}
			String t_value_str = new String(t_value);
			String b_value_str = new String(b_value);
			br.close();
	
			// Inserting data into xml file
			Element doc = docu.createElement("doc");
			docs.appendChild(doc);
			
			doc.setAttribute("id", Integer.toString(j));
			
			Element title = docu.createElement("title");
			title.appendChild(docu.createTextNode(t_value_str));
			doc.appendChild(title);
			
			Element body = docu.createElement("body");
			body.appendChild(docu.createTextNode(b_value_str));
			doc.appendChild(body);
			
		}
		
		// Making xml file using transformerFactory, DOMSource instants
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
		
		DOMSource source = new DOMSource(docu);
		StreamResult result = new StreamResult(new FileOutputStream(new File("collection.xml")));
		
		transformer.transform(source,  result);
	}
}