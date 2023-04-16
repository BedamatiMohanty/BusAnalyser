
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

import org.json.JSONArray;
import org.json.JSONObject;

public class BusLineAnalyser{

    private static final String API_KEY = "5da196d47f8f4e5facdb68d2e25b9eae";
    public static void main(String[] args) {
        try{
            String url = "http://www.trafiklab.se/api/hallplatser-och-linjer-2/GetLines?key="+ API_KEY;
            String busLineJson = fetchJsonData(url);
            JSONObject busLineData = new JSONObject(busLineJson);
            JSONArray busLineArray = busLineData.getJSONArray("ResponseData");

            HashMap<String,Integer> busLinesMap = new HashMap<>();
            for(int i =0;i<busLineArray.length();i++){
                JSONObject busLine = busLineArray.getJSONObject(i);
                String busLineCode = busLine.getString("LineNumber");
                int stopCount = busLine.getInt("StopNumber");
                busLinesMap.put(busLineCode,stopCount);
            }

            List<Map.Entry<String,Integer>> busLineList = new ArrayList<>(busLinesMap.entrySet());
            busLineList.sort(Collections.reverseOrder(Map.Entry.comparingByValue()));

            System.out.println("Top 10 bus lines with most stops:");
            for(int i=0;i<10 && i< busLineList.size();i++){
                Map.Entry<String,Integer> entry = busLineList.get(i);
                String busLineCode = entry.getKey();
                int stopCount = entry.getValue();

                String busStopUrl = "http://www.trafiklab.se/api/sl-hallplatser-och-linjer-2/GetStops?key="+API_KEY + "&model=Line&lineNumber"+ busLineCode;
                String busStopJSON = fetchJsonData(busStopUrl);
                JSONObject busStopData = new JSONObject(busStopJSON);
                JSONArray busStopArray = busStopData.getJSONArray("ResponseData");

                System.out.println("Bus Line:" + busLineCode);
                System.out.println("Stop Count:" + stopCount);
                System.out.println("Bus Stops:");

                for(int j=0;j< busStopArray.length();j++){
                    JSONObject busStopObject = busStopArray.getJSONObject(j);
                    String busStopName = busStopObject.getString("StopAreaName");
                    System.out.println("-" + busStopName);
                }

            }

        }catch(Exception e){
            e.printStackTrace();
        }

    }

    private static String fetchJsonData(String urlStr)throws IOException{
        StringBuilder sb = new StringBuilder();
        URL url = new URL(urlStr);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept","application/json");

        BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String line;
        while((line= br.readLine()) != null){
            sb.append(line);
        }
        br.close();

        return sb.toString().trim();
    }
}