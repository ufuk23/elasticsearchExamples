
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.UUID;

import org.apache.http.HttpHost;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Application {

    // The config parameters for the connection
    public static final Random         rand         = new Random();
    private static final String        HOST         = "localhost";
    private static final int           PORT_ONE     = 9200;
    private static final int           PORT_TWO     = 9201;
    private static final String        SCHEME       = "http";

    private static RestHighLevelClient restHighLevelClient;
    private static ObjectMapper        objectMapper = new ObjectMapper();

    private static final String        INDEX        = "art8";
    
    /**
     * Implemented Singleton pattern here so that there is just one connection at a time.
     * 
     * @return RestHighLevelClient
     */
    private static synchronized RestHighLevelClient makeConnection() {

        if (restHighLevelClient == null) {
            restHighLevelClient = new RestHighLevelClient(RestClient.builder(new HttpHost(HOST, PORT_ONE, SCHEME), new HttpHost(HOST, PORT_TWO, SCHEME)));
        }

        return restHighLevelClient;
    }

    private static synchronized void closeConnection() throws IOException {
        restHighLevelClient.close();
        restHighLevelClient = null;
    }

    private static Artifact insertartifact(final Artifact artifact) {
        artifact.setId(UUID.randomUUID().toString());
        final Map<String, Object> dataMap = new HashMap<String, Object>();
        dataMap.put("id", artifact.getId());
        dataMap.put("name", artifact.getName());
        final Double[] array = {11.3, 10.6, 23.0, 11.5, 10.4 };
        dataMap.put("feature", array);
        
        IndexRequest indexRequest = new IndexRequest(INDEX).id(artifact.getId()).source(dataMap);
        
        try {
            final IndexResponse response = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
        } catch (final ElasticsearchException e) {
            e.getDetailedMessage();
        } catch (final java.io.IOException ex) {
            ex.getLocalizedMessage();
        }
        return artifact;
    }

    private static synchronized Artifact insertartifact(final Artifact artifact, final List<Double> list) {
        final Map<String, Object> dataMap = new HashMap<String, Object>();
        dataMap.put("id", artifact.getId());
        dataMap.put("name", artifact.getName());
        dataMap.put("vec", list);
        IndexRequest indexRequest = new IndexRequest(INDEX).id(artifact.getId()).source(dataMap);
        try {
            final IndexResponse response = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
        } catch (final ElasticsearchException e) {
            e.getDetailedMessage();
        } catch (final java.io.IOException ex) {
            ex.getLocalizedMessage();
        }
        return artifact;
    }

    private static Artifact getartifactById(final String id) {
        final GetRequest getartifactRequest = new GetRequest(INDEX, id);
        GetResponse getResponse = null;
        try {
            getResponse = restHighLevelClient.get(getartifactRequest, RequestOptions.DEFAULT);
        } catch (final java.io.IOException e) {
            e.getLocalizedMessage();
        }
        return getResponse != null ? objectMapper.convertValue(getResponse.getSourceAsMap(), Artifact.class) : null;
    }

    private static Artifact updateartifactById(final String id, final Artifact artifact) {
        final UpdateRequest updateRequest = new UpdateRequest(INDEX, id).fetchSource(true); // Fetch Object after its update
        try {
            final String artifactJson = objectMapper.writeValueAsString(artifact);
            updateRequest.doc(artifactJson, XContentType.JSON);
            final UpdateResponse updateResponse = restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);
            return objectMapper.convertValue(updateResponse.getGetResult().sourceAsMap(), Artifact.class);
        } catch (final JsonProcessingException e) {
            e.getMessage();
        } catch (final java.io.IOException e) {
            e.getLocalizedMessage();
        }
        System.out.println("Unable to update artifact");
        return null;
    }

    private static void deleteartifactById(final String id) {
        final DeleteRequest deleteRequest = new DeleteRequest(INDEX, id);
        try {
            restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);
        } catch (final java.io.IOException e) {
            e.getLocalizedMessage();
        }
    }

    public static void main(final String[] args) throws IOException {
        makeConnection();

        final VectorMap vectorMap = new VectorMap();
        vectorMap.createDataList();

//        int c = 0;
//        for (final Entry<String, List<Double>> entry : vectorMap.mapDouble.entrySet()) {
//            final artifact artifact = new artifact();
//            artifact.setId("" + c);
//            artifact.setName(entry.getKey());
//            insertartifact(artifact, entry.getValue());
//            c += 1;
//        }
        
//        for (int i = 0; i < 9220; i++) {
//            deleteartifactById("" + i);
//        }

        final int index = 1;
        int k = 0;
        for (int i = index; i < 100000; i++) {
        	
            for (final Entry<String, List<Double>> entry : vectorMap.mapDouble.entrySet()) {
                final Artifact artifact = new Artifact();
                artifact.setId("" + k);
                artifact.setName(entry.getKey());
                
                int randIndex = rand.nextInt(entry.getValue().size());
                List<Double> list = entry.getValue();
                Double newVal = list.get(randIndex) + Double.valueOf(0.01);
                list.set(randIndex, newVal);
                Application.insertartifact(artifact, list);
                System.out.println(entry.getKey() + " - " + k);
                k += 1;
            }
        	
        }

        // artifact artifact = new artifact();
        // artifact.setName("Shubham");
        // artifact = insertartifact(artifact);
        // System.out.println("artifact inserted --> " + artifact);

        // System.out.println("Changing name to 'ShubhamAggarwalCoin'...");
        // artifact.setName("Shubham Aggarwal");
        // updateartifactById(artifact.getId(), artifact);
        // System.out.println("artifact updated --> " + artifact);
        //
        // System.out.println("Getting Shubham...");
        // final artifact artifactFromDB = getartifactById(artifact.getId());
        // System.out.println("artifact from DB --> " + artifactFromDB);
        //
        // System.out.println("Deleting Shubham...");
        // deleteartifactById(artifactFromDB.getId());
        // System.out.println("artifact Deleted");

        closeConnection();
    }

    public static void changeValues(final Double[] d) {
        final int randomIndex = rand.nextInt(1024);
        d[randomIndex] = d[randomIndex] + 0.01;
    }
}
