import java.io.*;
public class midterm {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		
		// get option&path data
		String option = args[0];
		System.out.println("option : " + option + " start!");
		
		String filePath = args[1];
		File fileOrDir = new File(filePath);
		System.out.println("filePath : "+filePath + "\n");
		
		String option2 = "";
		option2 = args[2];
		
		String query = "";
		query = args[3];
		
		if(option.equals("-f")) {
			// filepath : ./index.txt
			if (option2.equals("-q")) {
				query = getString(args); // get String query
				genSnippet g = new genSnippet(fileOrDir, query);
				g.mainMethod();
				System.out.println("\n succeeded!");
			}
			else {
				System.out.println("You need to input more options to implement!");
			}
		}
	}
	
	public static String getString(String [] args) {
		String query = args[3];
		for(int i = 4; i<args.length; i++) query+=(" "+args[i]); 
		return query;
	}

}
