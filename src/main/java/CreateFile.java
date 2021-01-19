import java.io.File; // Import the File class
import java.io.FileWriter;
import java.io.IOException; // Import the IOException class to handle errors

public class CreateFile {

	public static void main(String[] args) {
		try {
			File myObj = new File("id.txt");
			if (myObj.createNewFile()) {
				System.out.println("File created: " + myObj.getName());
			} else {
				System.out.println("File already exists.");
			}

			FileWriter myWriter = new FileWriter("id.txt");
			myWriter.write("[");
			int c = 1;
			for (int i = 0; i < 1000000; i++) {
				myWriter.write(c + ",");
				System.out.println(i);
				c++;
			}
			myWriter.write("]");
			myWriter.close();
		} catch (Exception e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
	}
}