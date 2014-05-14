package recommendation.andrew.email;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleGraph;

import recommendation.groups.seedless.SeedlessGroupRecommender;
import recommendation.groups.seedless.hybrid.HybridCliqueMerger;

/**
 * Created by andrewwg94 on 4/4/14.
 */
public class BurstyModel {
    public static Set<Set<String>> createGraph(List<Message> messages, Map<Integer,String> addressMap) throws IOException {
        UndirectedGraph<String, DefaultEdge> graph = new SimpleGraph<String,DefaultEdge>(DefaultWeightedEdge.class) {
        };
        Set<Set<String>> addedGroups = new HashSet<Set<String>>();
        Set<Set<String>> prevCollaborators = new HashSet<Set<String>>();
        for (Message m : messages) {
            Set<String> collaborators = collaborators(m, addressMap);
            if(isNew(prevCollaborators, collaborators)) {
                addMessageToGraph(m,graph,addressMap);
                prevCollaborators.add(collaborators);
                SeedlessGroupRecommender<String> recommender = new HybridCliqueMerger<String>(graph);
                Collection<Set<String>> results = recommender.getRecommendations ();
                Set<Set<String>> burstyGroups = getBurstyPredictionsFromAllPredictions(results, addedGroups, collaborators);
                addedGroups.addAll(burstyGroups);
            }
        }
    return addedGroups;
    }
    static Set<Set<String>> getBurstyPredictionsFromAllPredictions(Collection<Set<String>> results, Set<Set<String>> pastRecommendations, Set<String> collaborators) {
        Set<Set<String>> burstyGroups = new HashSet<Set<String>>();
        for (Set<String> group: results) {
            if(!pastRecommendations.contains(group)&&group.containsAll(collaborators))
                burstyGroups.add(group);
        }
        return burstyGroups;
    }
    public static Set<String> collaborators(Message m, Map<Integer,String> addressMap) {
        Set<String> collaborators = new HashSet<String>();
        collaborators.add(addressMap.get(m.getFromId()));
        for (int recipient: m.getRecipients()) {
            collaborators.add(addressMap.get(recipient));
        }
        return collaborators;
    }
    public static boolean isNew (Set<Set<String>> prevCollaborators, Set<String> currCollaborators) {
        return !prevCollaborators.contains(currCollaborators);
    }
    static void addMessageToGraph (Message m, UndirectedGraph<String, DefaultEdge> graph, Map<Integer,String> addressMap) {
        for(int recipient:m.getRecipients()) {
            if(m.getFromId()>=0) {
                graph.addVertex(addressMap.get(m.getFromId()));
                graph.addVertex(addressMap.get(recipient));
                if(m.getFromId()>=0 && !addressMap.get(m.getFromId()).equals(addressMap.get(recipient))) {
                    graph.addEdge(addressMap.get(m.getFromId()),addressMap.get(recipient));
                }
            }
        }
        if(m.getRecipients().length>1) {
            for (int i=0;i<m.getRecipients().length;i++){
                for (int j=i+1;j<m.getRecipients().length;j++) {
                    if(!addressMap.get(m.getRecipients()[i]).equals(addressMap.get(m.getRecipients()[j])))
                        graph.addEdge(addressMap.get(m.getRecipients()[i]), addressMap.get(m.getRecipients()[j]));
                }
            }
        }
    }
    public static void main(String[] args) throws IOException {
        File path = new File("/Users/andrewwg94/email_threads/anonymous_data/1389815561_1/messages.txt");
        List<Message> messages = EmailParser.parseMessageFile(path);
        Map<Integer,String> map = new HashMap<Integer, String>();
        File addresses = new File("/Users/andrewwg94/email_threads/private_data/1389815561_1/addresses.txt");
        BufferedReader br = new BufferedReader(new FileReader(addresses));
        String currLine;
        while((currLine=br.readLine())!=null) {
            String [] array = currLine.split(":");
            map.put(Integer.parseInt(array[0]),array[1]);
        }
        Set<Set<String>> groups = createGraph(messages, map);
        EmailParser.printGroups(groups);
        System.out.println(groups.size());
    }
}
