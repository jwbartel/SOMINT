package recommendation.andrew.email;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import recommendation.groups.seedless.SeedlessGroupRecommender;
import recommendation.groups.seedless.hybrid.HybridCliqueMerger;


/**
 * Created with IntelliJ IDEA.
 * User: Andrew Ghobrial
 * Date: 9/28/13
 * Time: 6:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class GuiEmailParser {
    public void parser() {
        BufferedReader br=null;
        try {
            br = new BufferedReader(new FileReader("/Users/andrewwg94/Downloads/email_thread_data.txt"));
            String currLine;
            int i=0;
            while((currLine=br.readLine())!=null) {
                try { if(!(currLine.substring(0,8)).equalsIgnoreCase("Message:")) {
                    continue;
                } } catch (StringIndexOutOfBoundsException e) {
                    continue;}
                Message2 m = new Message2();
                // parse message id
                Pattern pattern = Pattern.compile("Message:(.*?) ");
                Matcher matcher = pattern.matcher(currLine);
                if(matcher.find()) m.setMessageId(Integer.parseInt(matcher.group().toString().substring(8).trim()));
                // parse thread id
                pattern = Pattern.compile("Thread:(.*?) ");
                matcher = pattern.matcher(currLine);
                if(matcher.find()) m.setThreadId(Integer.parseInt(matcher.group().toString().substring(7).trim()));
                // parse from id
                pattern = Pattern.compile("(?<=From:\\[)(.*)(?=] Recipients:)");
                matcher = pattern.matcher(currLine);
                if(matcher.find())
                    m.setFrom(matcher.group());
                // parse recipients
                pattern = Pattern.compile("(?<=Recipients:\\[)(.*)(?=] Subject:)");
                matcher = pattern.matcher(currLine);
                if(matcher.find()) {
                    String[] sa =matcher.group().trim().split(",");
                    m.setRecipients(sa);
                }
                // received date
                pattern = Pattern.compile("Received-Date:.*");
                matcher = pattern.matcher(currLine);
                if(matcher.find()) m.setReceivedDate(matcher.group().toString().substring(14).trim());
                messages.add(m);
           //         System.out.println(Arrays.toString(messages.get(i).getRecipients()));
                i++;
            }
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }
    public UndirectedGraph<String, DefaultEdge> createGraph() {
        UndirectedGraph<String, DefaultEdge> graph = new SimpleGraph<String,DefaultEdge>(DefaultEdge.class) {
        };
        for(Message2 m : messages) {
            for(String recipient:m.getRecipients()) {
                graph.addVertex(m.getFrom());
                graph.addVertex(recipient);
                try {
                    graph.addEdge(m.getFrom(),recipient);
                } catch(IllegalArgumentException e) {
                    System.out.println(e.getMessage());
                }

                System.out.println("Message: " + m.getMessageId() +
                        " Edge is From:  "+ m.getFrom() + " To " + recipient);
            }
            if(m.getRecipients().length>1) {
                for (int i=0;i<m.getRecipients().length;i++){
                    for (int j=i+1;j<m.getRecipients().length;j++) {
                        try {
                            graph.addEdge(m.getRecipients()[i], m.getRecipients()[j]);
                        } catch (IllegalArgumentException e) {
                            System.out.println(e.getMessage());
                        }
                        System.out.println("Message: " + m.getMessageId() +
                                " Edge is From: "+ m.getRecipients()[i] + " To " + m.getRecipients()[j]);
                    }
                }
            }
        }
        return graph;
    }
    ArrayList<Message2> messages = new ArrayList<Message2>();
    public ArrayList<Message2> getMessages() {
        return messages;
    }

    public static void main(String[]args) {
        GuiEmailParser p = new GuiEmailParser();
        p.parser();
        UndirectedGraph<String, DefaultEdge> graph = p.createGraph();
        SeedlessGroupRecommender<String> recommender = new HybridCliqueMerger<String>(graph);
        Collection<Set<String>> results = recommender.getRecommendations ();
        for(Set s: results) {
            System.out.println(Arrays.toString(s.toArray()));
        }
    }
}
