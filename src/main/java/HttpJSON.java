import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

public class HttpJSON {

    public static void main(final String[] args) {
        try {
            final URL url = new URL("http://10.1.37.52:9200/art/_search");
            final HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setRequestProperty("Accept", "application/json");
            con.setDoOutput(true);

            final StringBuilder sb = new StringBuilder();
            // sb.append("1");
            for (int i = 0; i < 1000000; i++) {
                sb.append(i);
                if (i < 999999) {
                    sb.append(",");
                }
                System.out.println(i);
            }

            final VectorMap vectorMap = new VectorMap();
            vectorMap.createDataList();
            final List<Double> list = vectorMap.mapDouble.get("plate1.jpg");
            final String strVector = Arrays.toString(list.toArray());

            // String jsonInputString = "{\"size\":2,\"_source\":[\"id\",\"name\"],\"query\":{\"terms\":{\"name\":[\"coin1.jpg\",\"spoon1.jpg\"]}}}";
            String jsonInputString = "{"
                                     + "  \"size\": 3,"
                                     + "  \"_source\": ["
                                     + "    \"id\","
                                     + "    \"name\""
                                     + "  ],"
                                     + "  \"query\": {"
                                     + "    \"ids\": {"
                                     + "      \"values\": ["
                                     + sb.toString()
                                     + "]"
                                     + "    }"
                                     + "  }"

                                     + "}";

            jsonInputString = "{"
                              + "  \"size\": 3,"
                              + "  \"_source\": ["
                              + "    \"id\","
                              + "    \"name\""
                              + "  ],"
                              + "  \"query\": {"
                              + "    \"script_score\": {"
                              + "      \"query\": {"
                              + "        \"ids\": {"
                              + "          \"values\": ["
                              + sb.toString()
                              + "          ]"
                              + "        }"
                              + "      },"
                              + "      \"script\": {"
                              + "        \"source\": \"cosineSimilarity(params.query_vector, 'vec')\","
                              + "        \"params\": {"
                              + "          \"query_vector\":"
                              + strVector
                              + "        }"
                              + "      }"
                              + "    }"
                              + "  }"
                              + "}";

            try (OutputStream os = con.getOutputStream()) {
                final byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"))) {
                final StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                System.out.println(response.toString());
            }

        } catch (final MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (final IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
