import java.io.*;
import java.util.ArrayList;
import java.util.List;
public class genSnippet {
	private File file;
	private String query;
	
	genSnippet(){};
	genSnippet(File f, String str){
		file = f;
		this.query = str;
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
		
		public void mainMethod() throws Exception {
			FileInputStream input = new FileInputStream(file);
			InputStreamReader reader = new InputStreamReader(input,"UTF-8");
			BufferedReader br = new BufferedReader(reader);
			
			List<String> key1 = new ArrayList<String>();
			List<String> key2 = new ArrayList<String>();
			List<String> key3 = new ArrayList<String>();
			List<String> key4 = new ArrayList<String>();
			List<String> key5 = new ArrayList<String>();
			List<String> q = new ArrayList<String>();
			
			char temp = '\u0000';
			int count = 0;
			String word = "";
			
			for (int i = 0; i<5; i++){
				while(true) {
					temp = read(br,count);
					if (temp != ' ' && temp != '\n') {
						word += temp;
					}
					else if (temp== ' ') {
						switch(i){
							case 0:
								key1.add(word); break;
							case 1:
								key2.add(word); break;
							case 2:
								key3.add(word); break;
							case 3:
								key4.add(word); break;
							case 4:
								key5.add(word); break;
							default:
									break;
						}
						word = "";
					}
					else if (temp == '\n') {
						break;
					}
				}
			}
			
			for (int i = 0; i<query.length(); i++) {
				char c = query.charAt(i);
			}
			
			
		}
	

}
