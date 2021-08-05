import com.alexmerz.graphviz.*;
import com.alexmerz.graphviz.objects.*;

import java.io.*;
import java.util.*;

public class EntityParser {

    public EntityParser(String entityFilename, StagMap map) {

        try {
            Parser parser = new Parser();
            FileReader reader = new FileReader(entityFilename);
            parser.parse(reader);
            ArrayList<Graph> mainEntity = parser.getGraphs();
            ArrayList<Graph> locations = mainEntity.get(0).getSubgraphs();
            //goes through all location subgraphs
            for(Graph loc : locations){
                ArrayList<Graph> specificLocation = loc.getSubgraphs();
                //for each specific location
                for (Graph specific : specificLocation){
                    //hashmap for each location
                    LinkedHashMap<String, ArrayList<String>> innerMap = new LinkedHashMap<>();
                    ArrayList<Node> nodesLoc = specific.getNodes(false);
                    Node locationDetails = nodesLoc.get(0);
                    ArrayList<Graph> entities = specific.getSubgraphs();
                    //populates the game map
                    populateMap(innerMap, entities);
                    ArrayList<String> descriptionList = new ArrayList<>();
                    //gets description for map location
                    descriptionList.add(locationDetails.getAttribute("description"));
                    //adds the description for the location
                    innerMap.put("description", descriptionList);
                    map.map.put(locationDetails.getId().getId(), innerMap);
                }
                addRemainingKeys(map);
                addPaths(map, loc);
            }

        } catch (FileNotFoundException fnfe) {
            System.out.println(fnfe);
        } catch (com.alexmerz.graphviz.ParseException pe) {
            System.out.println(pe);
        }

    }

    private void populateMap(LinkedHashMap<String, ArrayList<String>> innermap,   ArrayList<Graph> entities) {

        for (Graph ent : entities) {
            ArrayList<String> entitityList = new ArrayList<>();
            ArrayList<Node> innerDetails = ent.getNodes(false);
            for (Node innerEnt : innerDetails) {
                //adds the entities and description seperated by tab
                String idAndDescrip = innerEnt.getId().getId() + "\t" + innerEnt.getAttribute("description");
                entitityList.add(idAndDescrip);
            }
            innermap.put(ent.getId().getId(), entitityList);
        }
    }

    private void addPaths(StagMap map, Graph loc){
        //adds the paths
        ArrayList<Edge> paths = loc.getEdges();
        for (Edge path : paths){
            map.map.get(path.getSource().getNode().getId().getId()).get("paths").add(path.getTarget().getNode().getId().getId());
        }
    }

    private void addRemainingKeys(StagMap map) {
        //adds the remaining entity types
        String[] entityTypes = {"characters", "artefacts", "furniture", "players", "paths", "players"};

        for (Map.Entry<String, LinkedHashMap<String, ArrayList<String>>> entry : map.map.entrySet()) {
            String key = entry.getKey();
            for (String entityType : entityTypes) {
                //checks if entity exists and if not then adds blank arraylist
                if (!map.map.get(key).containsKey(entityType)) {
                    ArrayList<String> emptyArrayList = new ArrayList<>();
                    map.map.get(key).put(entityType, emptyArrayList);
                }
            }
        }
    }

}

