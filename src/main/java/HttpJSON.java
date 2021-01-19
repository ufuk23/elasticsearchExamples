import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpJSON {

	public static void main(String[] args) {
		try {
			URL url = new URL("http://localhost:9200/art/_search");
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/json; utf-8");
			con.setRequestProperty("Accept", "application/json");
			con.setDoOutput(true);

			StringBuilder sb = new StringBuilder();
			//sb.append("1");
			for (int i = 0; i < 1000000; i++) {
				sb.append(i);
				if(i < 999999) {
					sb.append(",");
				}
				System.out.println(i);
			}
			
			//String jsonInputString = "{\"size\":2,\"_source\":[\"id\",\"name\"],\"query\":{\"terms\":{\"name\":[\"coin1.jpg\",\"spoon1.jpg\"]}}}";
			String jsonInputString = "{\r\n" + 
					"  \"size\": 3,\r\n" +
					"  \"_source\": [\r\n" + 
					"    \"id\",\r\n" + 
					"    \"name\"\r\n" + 
					"  ],\r\n" + 
					"  \"query\": {\r\n" + 
					"    \"ids\": {\r\n" + 
					"      \"values\": [" + sb.toString() + "]"
					+ "\r\n" + 
					"    }\r\n" + 
					"  }\r\n" + 
					"}";
			
			try (OutputStream os = con.getOutputStream()) {
				byte[] input = jsonInputString.getBytes("utf-8");
				os.write(input, 0, input.length);
			}
			
			try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"))) {
				StringBuilder response = new StringBuilder();
				String responseLine = null;
				while ((responseLine = br.readLine()) != null) {
					response.append(responseLine.trim());
				}
				System.out.println(response.toString());
			}

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
