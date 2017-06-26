package us.brianolsen.justgo.osm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.graph.domain.basic.UndirectedGraphImpl;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import us.brianolsen.justgo.graph.model.OSMEdge;
import us.brianolsen.justgo.graph.model.OSMVertex;

public class OSMWrapperAPI {

	private static final String OVERPASS_API = "http://www.overpass-api.de/api/interpreter";
	
	public static void main(String[] args) throws ClientProtocolException, URISyntaxException, IOException {
		String query = buildQueryString(41.70752269548981, -87.75063514709473, 41.72036884468498, -87.72698879241943);
		System.out.println(query);
		String resultJson = getNodesViaOverpass(query);

		System.out.println(toPrettyFormat(resultJson));
		
		UndirectedGraphImpl graph = convertJsonToGraph(resultJson);
		
		for(Object vertex: graph.getVertices()){
			System.out.println(vertex);
		}
		for(Object edge: graph.getEdges()){
			System.out.println(edge);
		}

	}

	public static String getNodesViaOverpass(String query)
			throws URISyntaxException, ClientProtocolException, IOException {
		StringBuilder result = new StringBuilder();

		URI uri = new URIBuilder(OVERPASS_API).addParameter("data", query).build();
		HttpClient client = HttpClientBuilder.create().build();
		HttpGet request = new HttpGet(uri);
		HttpResponse response = client.execute(request);
		System.out.println("Response Code : " + response.getStatusLine().getStatusCode());

		BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		String line;
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}
		rd.close();
		return result.toString();
	}


	public static String buildQueryString(Double south, Double west, Double north, Double east) {
		String bboxString = buildBboxString(south, west, north, east);
		StringBuilder sb = new StringBuilder();
		sb.append("[out:json]");
		sb.append("[bbox:");
		sb.append(bboxString);
		sb.append("];");
		sb.append("way[highway];");
		sb.append("out geom;");
		return sb.toString();
	}

	public static String buildBboxString(Double south, Double west, Double north, Double east) {
		StringBuilder sb = new StringBuilder();
		DecimalFormat format = new DecimalFormat("##0.0000000", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
		sb.append(format.format(south));
		sb.append(",");
		sb.append(format.format(west));
		sb.append(",");
		sb.append(format.format(north));
		sb.append(",");
		sb.append(format.format(east));
		return sb.toString();
	}

	public static String toPrettyFormat(String jsonString) {
		JsonObject json = parse(jsonString).getAsJsonObject();

		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String prettyJson = gson.toJson(json);

		return prettyJson;
	}
	
	
	public static UndirectedGraphImpl convertJsonToGraph(String jsonString){
		UndirectedGraphImpl graph = new UndirectedGraphImpl();
		JsonElement jsonElement = parse(jsonString);
		JsonArray elements = jsonElement.getAsJsonObject().get("elements").getAsJsonArray();
		
		for(JsonElement element: elements){
			JsonObject object = element.getAsJsonObject();
			JsonArray nodeIds = object.get("nodes").getAsJsonArray();
			JsonArray locations = object.get("geometry").getAsJsonArray();
			Map<String, String> tags = new GsonBuilder().create().fromJson(object.get("tags"), Map.class);
			OSMVertex v2 = null;
			
			for(int i = 0; i < nodeIds.size(); i++){
				int id = nodeIds.get(i).getAsInt();
				JsonObject location = locations.get(i).getAsJsonObject();
				Double latitude = location.get("lat").getAsDouble();
				Double longitude = location.get("lon").getAsDouble();
				OSMVertex v1 = new OSMVertex(id, latitude, longitude, tags);
				
				
				//TODO if(graph.getVertices().contains(v1)) { // connect ways }
				
				graph.addVertex(v1);
				if(i > 0){
					graph.addEdge(new OSMEdge(v1, v2));
				}
				v2 = v1;
			}
			
			
		}
		//
		//osmNodes.add(new OSMVertex(id, latitude, longitude, version, tags));
		return graph;
	}

	public static JsonElement parse(String json) {
		JsonParser parser = new JsonParser();
		return parser.parse(json);
	}
	



}
