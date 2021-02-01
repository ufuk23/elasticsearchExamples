import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

public class MilvusHttpJSON {

	public static void main(String[] args) {
		try {
			final VectorMap vectorMap = new VectorMap();
			vectorMap.createDataList();
			Random rand = new Random();
			URL url = new URL("http://localhost:19121/collections/art/vectors");

			List<String> idList = new ArrayList<String>();
			List<List<Double>> vectorList = new ArrayList<List<Double>>();

			try {
				File myObj = new File("idMap.txt");
				if (myObj.createNewFile()) {
					System.out.println("File created: " + myObj.getName());
				} else {
					System.out.println("File already exists.");
				}
				FileWriter myWriter = new FileWriter("idMap.txt");

				final int index = 0;
				int k = 1;
				for (int i = index; i < 40000; i++) {

					for (final Entry<String, List<Double>> entry : vectorMap.mapDouble.entrySet()) {
						final Artifact artifact = new Artifact();
						artifact.setId("" + k);
						artifact.setName(entry.getKey());

						int randIndex = rand.nextInt(entry.getValue().size());
						List<Double> list2 = entry.getValue();
						List<Double> list = new ArrayList<Double>(list2);
						Double newVal = list.get(randIndex) + Double.valueOf(0.01);
						list.set(randIndex, newVal);

						idList.add("" + k);
						vectorList.add(list);

						myWriter.write(k + ":" + entry.getKey() + "\n");
						k += 1;
					}

					System.out.println("k = " + k + " ---- i = " + i);

					if (i % 1000 == 0) {

						HttpURLConnection con = (HttpURLConnection) url.openConnection();
						con.setRequestMethod("POST");
						con.setRequestProperty("Content-Type", "application/json; utf-8");
						con.setRequestProperty("Accept", "application/json");
						con.setDoOutput(true);

						String ids = Arrays.toString(idList.toArray()).replace(" ", "").replace("[", "[\"")
								.replace("]", "\"]").replaceAll(",", "\",\"");
						String jsonInputString = "{" + "  \"vectors\":" + Arrays.toString(vectorList.toArray()) + ","
								+ " \"ids\":" + ids + "}";

						try (OutputStream os = con.getOutputStream()) {
							byte[] input = jsonInputString.getBytes("utf-8");
							os.write(input, 0, input.length);
						}

						try (BufferedReader br = new BufferedReader(
								new InputStreamReader(con.getInputStream(), "utf-8"))) {
							StringBuilder response = new StringBuilder();
							String responseLine = null;
							while ((responseLine = br.readLine()) != null) {
								response.append(responseLine.trim());
							}

							idList = new ArrayList<String>();
							vectorList = new ArrayList<List<Double>>();
						}
					}

				}

				myWriter.close();
			} catch (Exception e) {
				System.out.println("An error occurred.");
				e.printStackTrace();
			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

}
