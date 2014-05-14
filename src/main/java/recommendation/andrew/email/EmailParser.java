package recommendation.andrew.email;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jgrapht.Graphs;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.WeightedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.graph.SimpleWeightedGraph;

import recommendation.andrew.email.testing.Testing;
import recommendation.groups.seedless.SeedlessGroupRecommender;
import recommendation.groups.seedless.hybrid.HybridCliqueMerger;

import com.google.common.primitives.Ints;

/**
 * Created with IntelliJ IDEA.
 * User: Andrew Ghobrial
 * Date: 9/28/13
 * Time: 6:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class EmailParser {
    public static final long ONE_SECOND = 1000L;
    public static final long ONE_HOUR = 1000L * 3600;
    public static final long HALF_DAY = 1000L * 3600 * 12;
    public static final long ONE_DAY = 1000L * 3600 * 24;
    public static final long ONE_WEEK = 1000L * 3600 * 24 * 7;
    public static final long ONE_MONTH = 1000L * 3600 * 24 * 30;
    static final String RESEARCH_TOOLS_PARENT = System.getProperty("user.home");

    public static void parser() throws IOException{
        File anonymous_data = new File(RESEARCH_TOOLS_PARENT+"/email_threads/anonymous_data/");
        File[] allUsers = anonymous_data.listFiles();
        for (File user: allUsers) {
            String user_id = user.getName();
            if(user.isDirectory()) {
                File[] messageFiles = user.listFiles(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {
                        return name.equals("messages.txt");
                    }
                });
                 //   compareGroups(messageFile,user_id);
                System.out.println("-----------------START OF FILE "+user_id+"-----------------");
                compareGroupsWithTest(messageFiles[0], user_id, ONE_MONTH, "one month");
                System.out.println("-----------------END OF FILE "+user_id+"-----------------");
             /*   compareGroupsWithTest(messageFile, user_id, ONE_WEEK, "one week");
                compareGroupsWithTest(messageFile, user_id, ONE_HOUR, "one hour");
                compareGroupsWithTest(messageFile, user_id, ONE_DAY, "one day");
                compareGroupsWithTest(messageFile, user_id, HALF_DAY, "half day");
                compareGroupsWithTest(messageFile, user_id, ONE_SECOND, "one second");     */
            }
        }
    }
    public static List<Message> parseMessageFile(File messagesFile) throws IOException {
        List<Message> messages = new ArrayList<Message>();
        BufferedReader br = new BufferedReader(new FileReader(messagesFile));
        String currLine;
        while((currLine=br.readLine())!=null) {
            try {
                if(!(currLine.substring(0,8)).equalsIgnoreCase("Message:")) {
                    continue;
                }
            } catch (StringIndexOutOfBoundsException e) {
                continue;
            }
            Message m = parseLine(currLine);
            if(m!=null) messages.add(m);
        }
        return sort(messages);
    }
    static Message parseLine(String currLine) {
        Message m = new Message();
        // parse message id
        Pattern pattern = Pattern.compile("Message:(.*?) ");
        Matcher matcher = pattern.matcher(currLine);
        if(matcher.find()) m.setMessageId(Integer.parseInt(matcher.group().substring(8).trim()));
        // parse thread id
        pattern = Pattern.compile("Thread:(.*?) ");
        matcher = pattern.matcher(currLine);
        if(matcher.find()) m.setThreadId(Integer.parseInt(matcher.group().substring(7).trim()));
        // parse from id
        pattern = Pattern.compile("From:(.*?) ");
        matcher = pattern.matcher(currLine);
        if(matcher.find()){
                String fromId = matcher.group().substring(5).replaceAll("\\[", "").replaceAll("\\]", "").trim();
                if(fromId.equals("")) {System.out.println("No From Id found!"); m.setFromId(-1);}
                else m.setFromId(Integer.parseInt(fromId));
            }
        // parse recipients
        pattern = Pattern.compile("Recipients:(.*?) ");
        matcher = pattern.matcher(currLine);
        if(matcher.find()) {
            String[] sa =matcher.group().substring(11).replaceAll("\\[", "").replaceAll("\\]", "").trim().split(",");
            ArrayList<Integer> ial = new ArrayList<Integer>(sa.length);
            for(int j=0;j<sa.length;j++) if(sa[j].equalsIgnoreCase("")) continue; else {
                ial.add(j,Integer.parseInt(sa[j]));
            }
            m.setRecipients(Ints.toArray(ial));
        }
        // received date
        pattern = Pattern.compile("Received-Date:.*");
        matcher = pattern.matcher(currLine);
        if(matcher.find()) {
            String dateString = matcher.group().substring(14).trim();
            SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
            Date parsedDate = null;
            try {
                parsedDate = formatter.parse(dateString);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            m.setReceivedDate(parsedDate);
        }
        return m;
    }
    static List<Message> sort(List<Message> messages) {
        Collections.sort(messages, new Comparator<Message>() {
            @Override
            public int compare(Message m1, Message m2) {
                return m1.getReceivedDate().compareTo(m2.getReceivedDate());
            }
        });
        return messages;
    }
    static ArrayList<List<Message>> divide(List<Message> messages) {
        ArrayList<List<Message>> splitted = new ArrayList<List<Message>>();
        int sizeTotal = messages.size();
        int size80 = (int) Math.round(messages.size()*.80);
        List<Message> train = messages.subList(0,size80);
        List<Message> test = messages.subList(size80, sizeTotal);
        splitted.add(train);
        splitted.add(test);
        return splitted;
    }
    static String getSenderAddress (String user_id) throws IOException{
        File summary = new File(RESEARCH_TOOLS_PARENT+"/email_threads/private_data/"+user_id+"/summary.txt");
        if(!summary.exists()) {System.out.println("No Summary Exists!"); return null;  }
        BufferedReader br = new BufferedReader(new FileReader(summary));
        return br.readLine().replace("Source email:","").trim();
    }
    public static UndirectedGraph<String, DefaultEdge> createGraph(List<Message> train, long thresholdAge, Date oldestTestMessage, Map<Integer,String> addressMap) {
        UndirectedGraph<String, DefaultEdge> graph = new SimpleGraph<String,DefaultEdge>(DefaultEdge.class) {
        };
        Calendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(oldestTestMessage.getTime()-thresholdAge);
        Date threshold = calendar.getTime();

        for(Message m : train) {
            if(m.getReceivedDate().before(threshold)) continue;
            for(int recipient:m.getRecipients()) {
                graph.addVertex(addressMap.get(m.getFromId()));
                graph.addVertex(addressMap.get(recipient));
                  if(!addressMap.get(m.getFromId()).equals(addressMap.get(recipient)))
                    graph.addEdge(addressMap.get(m.getFromId()),addressMap.get(recipient));
            }
            if(m.getRecipients().length>1) {
                for (int i=0;i<m.getRecipients().length;i++){
                    for (int j=i+1;j<m.getRecipients().length;j++) {
                        graph.addEdge(addressMap.get(m.getRecipients()[i]), addressMap.get(m.getRecipients()[j]));
                    }
                }
            }
        }
        return graph;
    }
    static int allSent = 0;
    static int allTotal = 0;
    public static WeightedGraph<String, DefaultWeightedEdge> createWeightedGraph(List<Message> train, long thresholdAge, Date oldestTestMessage, Map<Integer,String> addressMap, String sourceEmail,String user_id,String time, long time_long) throws IOException{

        WeightedGraph<String, DefaultWeightedEdge> graph = new SimpleWeightedGraph<String,DefaultWeightedEdge>(DefaultWeightedEdge.class) {
        };

        Calendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(oldestTestMessage.getTime()-thresholdAge);
        Date threshold = calendar.getTime();
        long newestTrainTime = train.get(train.size()-1).getReceivedDate().getTime();
   //     System.out.println("LATEST TRAIN MESSAGE DATE: " + train.get(train.size()-1).getReceivedDate());

        File createPath = new File(RESEARCH_TOOLS_PARENT+"/research-tools/email_groups/half_life - 16x/");
        if(!createPath.exists()) createPath.mkdirs();
        BufferedWriter out = new BufferedWriter(new FileWriter(new File(createPath,"COMBINED"+"-"+time+".csv"),true));

        int sentCounter=0;
        int totalSize = train.size();
        for(Message m : train) {
         //   if(m.getReceivedDate().before(threshold)) continue;
            for(int recipient:m.getRecipients()) {
                if(m.getFromId()>=0) {
                    try {
                        graph.addVertex(addressMap.get(m.getFromId()));
                    } catch(NullPointerException e) {
                        graph.addVertex(Integer.toString(m.getFromId()));
                    }
                }
                try {
                    graph.addVertex(addressMap.get(recipient));
                } catch (NullPointerException e) {
                    graph.addVertex(Integer.toString(recipient));
                }
                try {
                    if(m.getFromId()>=0 && !addressMap.get(m.getFromId()).equals(addressMap.get(recipient))) {
                        graph.addEdge(addressMap.get(m.getFromId()),addressMap.get(recipient));

                        double halflife = Math.pow(.5,(newestTrainTime-m.getReceivedDate().getTime())/(time_long));
                        double currWeight = graph.getEdgeWeight(graph.getEdge(addressMap.get(m.getFromId()), addressMap.get(recipient)));
                        if(addressMap.get(m.getFromId()).equals(sourceEmail)) {
                            halflife = halflife* 16;  /*System.out.println(m.getMessageId());*/
                            sentCounter++;
                        }

                        out.write(String.valueOf(halflife));
                        out.newLine();
                        //System.out.println(String.valueOf(halflife));
                        graph.setEdgeWeight(graph.getEdge(addressMap.get(m.getFromId()), addressMap.get(recipient)), currWeight + halflife);
                    }
                } catch (NullPointerException e) {
                    if(m.getFromId()>=0 && !Integer.toString(m.getFromId()).equals(Integer.toString(recipient))) {
                        graph.addEdge(Integer.toString(m.getFromId()),Integer.toString(recipient));

                        double halflife = Math.pow(.5,(newestTrainTime-m.getReceivedDate().getTime())/(time_long));
                        double currWeight = graph.getEdgeWeight(graph.getEdge(Integer.toString(m.getFromId()), Integer.toString(recipient)));
                        if(Integer.toString(m.getFromId()).equals(sourceEmail)) {
                            halflife = halflife* 16;  /*System.out.println(m.getMessageId());*/
                            sentCounter++;
                        }

                        out.write(String.valueOf(halflife));
                        out.newLine();
                        //System.out.println(String.valueOf(halflife));
                        graph.setEdgeWeight(graph.getEdge(Integer.toString(m.getFromId()), Integer.toString(recipient)), currWeight + halflife);
                    }
                }
            }
            try {
                if(m.getRecipients().length>1) {
                    for (int i=0;i<m.getRecipients().length;i++){
                        for (int j=i+1;j<m.getRecipients().length;j++) {
                            if(!addressMap.get(m.getRecipients()[i]).equals(addressMap.get(m.getRecipients()[j])))
                                graph.addEdge(addressMap.get(m.getRecipients()[i]), addressMap.get(m.getRecipients()[j]));
                        }
                    }
                }
            } catch (NullPointerException e) {
                if(m.getRecipients().length>1) {
                    for (int i=0;i<m.getRecipients().length;i++){
                        for (int j=i+1;j<m.getRecipients().length;j++) {
                            if(!Integer.toString(m.getRecipients()[i]).equals(Integer.toString(m.getRecipients()[j])))
                                graph.addEdge(Integer.toString(m.getRecipients()[i]), Integer.toString(m.getRecipients()[j]));
                        }
                    }
                }
            }
        }
        allSent+=sentCounter;
        allTotal+=totalSize;
    //    System.out.println("Sent: "+ sentCounter+" Percent Sent: "+(double)sentCounter / (double)totalSize);
    //    System.out.println("Sent: "+ allSent+" Percent Sent: "+(double)allSent / (double)allTotal);
        out.close();
        return graph;
    }
    public static UndirectedGraph<String, DefaultEdge> createGraph(List<Message> messages, Map<Integer,String> addressMap) {
        UndirectedGraph<String, DefaultEdge> graph = new SimpleGraph<String,DefaultEdge>(DefaultEdge.class) {
        };
        for(Message m : messages) {
            for(int recipient:m.getRecipients()) {
                graph.addVertex(addressMap.get(m.getFromId()));
                    graph.addVertex(addressMap.get(recipient));
                    if(!addressMap.get(m.getFromId()).equals(addressMap.get(recipient)))
                        graph.addEdge(addressMap.get(m.getFromId()),addressMap.get(recipient));
            }
            if(m.getRecipients().length>1) {
                for (int i=0;i<m.getRecipients().length;i++){
                    for (int j=i+1;j<m.getRecipients().length;j++) {
                            graph.addEdge(addressMap.get(m.getRecipients()[i]), addressMap.get(m.getRecipients()[j]));
                    }
                }
            }
        }
        return graph;
    }
    public static Map<Integer,String> addressMap(String user_id) throws IOException{
        Map<Integer,String> map = new HashMap<Integer, String>();
        File addresses = new File(RESEARCH_TOOLS_PARENT+"/email_threads/private_data/"+user_id+"/addresses.txt");
        if(!addresses.exists()) { System.out.println("No address mappings exist!"); return null;}
        BufferedReader br = new BufferedReader(new FileReader(addresses));
        String currLine;
        while((currLine=br.readLine())!=null) {
            String [] array = currLine.split(":");
            map.put(Integer.parseInt(array[0]),array[1]);
        }
        return map;
    }
    public static void compareGroups(File messageFile, String user_id) throws IOException {
        File createPath = new File(RESEARCH_TOOLS_PARENT+"/research-tools/email_groups/group_compare/");
        if(!createPath.exists()) createPath.mkdirs();
        BufferedWriter out = new BufferedWriter(new FileWriter(new File(createPath,user_id+".csv")));
        out.write("first_group,second_group,closeness");
        out.newLine();
        List<Message> messages = parseMessageFile(messageFile);
        Map<Integer,String> map = addressMap(user_id);
        UndirectedGraph<String, DefaultEdge> graph = createGraph(messages,map);
        SeedlessGroupRecommender<String> recommender = new HybridCliqueMerger<String>(graph);
        Collection<Set<String>> results = recommender.getRecommendations ();
        List<Set<String>> orderedResults = new ArrayList<Set<String>>(results);
        Set<String> s,s2;
        for(int i=0;i<orderedResults.size();i++) {
            s=orderedResults.get(i);
            for(int j=0;j<orderedResults.size();j++) {
                s2=orderedResults.get(j);
                if (s.equals(s2)) continue;
                int closeness = Testing.closeness(s,s2);
                out.write(i +","+ j+","+closeness);
                out.newLine();
            }
        }
        out.close();
    }
    public static void compareGroupsWithTest(File messageFile, String user_id, long time, String time_label) throws IOException {
        File createPath = new File(RESEARCH_TOOLS_PARENT+"/research-tools/email_groups/test_compare/"+time_label);
        if(!createPath.exists()) createPath.mkdirs();
        BufferedWriter out = new BufferedWriter(new FileWriter(new File(createPath,user_id+".csv")));
        out.write("group_id,message_id,closeness");
        out.newLine();
        List<Message> messages = parseMessageFile(messageFile);
        if(messages.size()<5) {System.out.println("Less than five messages"); return;}
        Map<Integer,String> map = addressMap(user_id);
        ArrayList<List<Message>> splitted = divide(messages);
        String sourceEmail = getSenderAddress(user_id);
//        if(sourceEmail==null) return;
        WeightedGraph<String, DefaultWeightedEdge> graph = createWeightedGraph(splitted.get(0), time, splitted.get(1).get(0).getReceivedDate(),map,sourceEmail,user_id,"ONE_MONTH",ONE_MONTH);
        createWeightedGraph(splitted.get(0), time, splitted.get(1).get(0).getReceivedDate(),map,sourceEmail,user_id,"TWO_WEEKS",2*ONE_WEEK);
        createWeightedGraph(splitted.get(0), time, splitted.get(1).get(0).getReceivedDate(),map,sourceEmail,user_id,"ONE_WEEK", ONE_WEEK);
        createWeightedGraph(splitted.get(0), time, splitted.get(1).get(0).getReceivedDate(),map,sourceEmail,user_id,"ONE_DAY", ONE_DAY);
        createWeightedGraph(splitted.get(0), time, splitted.get(1).get(0).getReceivedDate(), map, sourceEmail, user_id, "ONE_HOUR", ONE_HOUR);
        SeedlessGroupRecommender<String> recommender = new HybridCliqueMerger<String>(dropEdges(graph));
        Collection<Set<String>> results = recommender.getRecommendations ();
        printGroups(results);
        //Testing.messageToGroupCloseness(results,splitted.get(1),map,out);
        out.close();
    }
    static void printGroups(Collection<Set<String>> results) {
        for(Set group: results) {
            System.out.println(Arrays.toString(group.toArray()));
        }
    }
    public static UndirectedGraph<String, DefaultEdge> dropEdges (WeightedGraph<String, DefaultWeightedEdge> graph) {
        UndirectedGraph<String, DefaultEdge> newGraph = new SimpleGraph<String,DefaultEdge>(DefaultEdge.class) {
        };
        Set<String> allVertices = graph.vertexSet();
        for (String vertix : allVertices) {
            newGraph.addVertex(vertix);
            for (String vertix2: allVertices){
                newGraph.addVertex(vertix2);
                Set<DefaultWeightedEdge> allEdges = graph.getAllEdges(vertix,vertix2);
                for (DefaultWeightedEdge edge : allEdges) {
               //     System.out.println(graph.getEdgeWeight(edge));
                    if(graph.getEdgeWeight(edge)>=50) {
                        String source = graph.getEdgeSource(edge);
                        String target = graph.getEdgeTarget(edge);
                        graph.addEdge(source,target);
                    }

                }
                Graphs.addAllEdges(newGraph,graph,allEdges);
            }
        }

     return newGraph;
    }
    public static void main(String[]args) throws IOException {
        parser();
     /*   WeightedGraph<String, DefaultWeightedEdge> graph = new SimpleWeightedGraph<String,DefaultWeightedEdge>(DefaultWeightedEdge.class) {
        };
        graph.addVertex("andrew");
        graph.addVertex("jacob");
        graph.addEdge("andrew","jacob");
        graph.setEdgeWeight(graph.getEdge("jacob","andrew"),5);
        graph.addEdge("andrew","jacob");
        graph.setEdgeWeight(graph.getEdge("jacob","andrew"),graph.getEdgeWeight(graph.getEdge("andrew","jacob")));
        System.out.println( graph.getEdgeWeight(graph.getEdge("andrew","jacob")));                    */

    }
}