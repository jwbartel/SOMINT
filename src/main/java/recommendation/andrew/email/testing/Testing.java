package recommendation.andrew.email.testing;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import recommendation.andrew.email.Message;

public class Testing {
    static public int subsetCounter(Collection<Set<String>> results, ArrayList<Message> messages, Map<Integer, String> addressMap) {
        int i = 0;
        for(Message m : messages) {
            for(Set s : results) {

                if(isSubset(s, getSetOfRecipentsAndFrom(m), addressMap)){
                    i++;
                    break;
                }
            }
        }
        return i;
    }
//    static public int supersetCounter(Collection<Set<String>> results, ArrayList<Message> messages, Map<Integer, String> addressMap) {
//        int i = 0;
//        for(Message m : messages) {
//            for(Set s : results) {
//
//                if(isSuperset(s, getSetOfRecipentsAndFrom(m), addressMap)){
//                    i++;
//                    break;
//                }
//            }
//        }
//        return i;
//    }
    static public int perfectCounter (Collection<Set<String>> results, ArrayList<Message> messages,  Map<Integer,String> addressMap) {
        int i = 0;
        for(Message m : messages) {
            for(Set s : results) {
                if(isPerfectMatch(s, getSetOfRecipentsAndFrom(m), addressMap)){
                    i++;
                    break;
                }
            }
        }
        return i;
    }
    static public boolean isSubset(Set<String> s, Set<Integer> recipientsAndFrom, Map<Integer,String> addressMap) {
       if(recipientsAndFrom.size()> s.size()) return false;
       boolean found = false;
        for (int recipient : recipientsAndFrom) {
            Iterator<String> iterable = s.iterator();
            while (iterable.hasNext()) {
                String next = iterable.next();
                if (addressMap.get(recipient)==null) continue;
                if(addressMap.get(recipient).equalsIgnoreCase(next)) {
                    found = true;
                    break;
                }
                else {
                    found = false;
                }
            }
            if(!found) break;
        }
        return found;
    }
    static public boolean isSuperset(Set<String> s, Set<String> recipientsAndFrom, Map<Integer,String> addressMap) {
        if(recipientsAndFrom.size()< s.size()) return false;
        boolean found = false;
        for (String s_member : s) {
            Iterator<String> iterable = recipientsAndFrom.iterator();
            while (iterable.hasNext()) {
                String next = iterable.next();
                if (addressMap.get(next)==null) continue;
                if(addressMap.get(next).equalsIgnoreCase(s_member)) {
                    found = true;
                    break;
                }
                else {
                    found = false;
                }
            }
            if(!found) break;
        }
        return found;
    }
    static public boolean isPerfectMatch(Set<String> s, Set<Integer> recipientsAndFrom, Map<Integer,String> addressMap) {
        boolean found = false;
        if(recipientsAndFrom.size() == s.size()) {
            for (int recipient : recipientsAndFrom) {
                Iterator<String> iterable = s.iterator();
                while (iterable.hasNext()) {
                    String next = iterable.next();
                    if (addressMap.get(recipient)==null) continue;
                    if(addressMap.get(recipient).equalsIgnoreCase(next)) {
                        found = true;
                        break;
                    }
                    else {
                        found = false;
                    }
                }
                if(!found) break;
            }
        }
        return found;
    }
    static public Set<Integer> getSetOfRecipentsAndFrom (Message message) {
        Set<Integer> recipientsAndFrom = new HashSet<Integer>();
        for (int recipient : message.getRecipients()) {
            recipientsAndFrom.add(recipient);
        }
        recipientsAndFrom.add(message.getFromId());
        return recipientsAndFrom;
    }
    static public Set<String> getSetOfRecipentsAndFrom (Message message,Map<Integer,String> addressMap) {
        Set<String> recipientsAndFrom = new HashSet<String>();
        for (int recipient : message.getRecipients()) {
            recipientsAndFrom.add(addressMap.get(recipient));
        }
        recipientsAndFrom.add(addressMap.get(message.getFromId()));
        return recipientsAndFrom;
    }
    static public int closeness (Set<String> s1, Set<String> s2) {
        Set<String> temp = new HashSet<String>(s1);
        temp.retainAll(s2);
        int deletions = s1.size()-temp.size();
        Set<String> temp2 = new HashSet<String>(s2);
        temp2.retainAll(s1);
        int additions = s2.size()-temp2.size();
        return additions + deletions;
    }
    static public List<Double> messageToGroupCloseness(PrintWriter out, Collection<Set<String>> results, List<Message> messages, Map<Integer, String> addressMap) throws IOException{
        List<Double> returnThis = new ArrayList<Double>();
        List<Double> allMins = new ArrayList<Double>();
        int groupsAccepted =0;
        for(Message m : messages) {
            Set<String> messageSet = getSetOfRecipentsAndFrom(m,addressMap);
            double min = Double.MAX_VALUE;
            for(Set<String> result : results) {
                double closenessPercent = (double) closeness(messageSet, result)/messageSet.size();
                if(closenessPercent<min) {
                    min = closenessPercent;
                }
            }
            if(min<1) {
                allMins.add(min);
                groupsAccepted++;
            }
        }
        out.println("message to group average: "+ average(allMins));
        out.println("percent groups accepted: " + (double) groupsAccepted/results.size());
        returnThis.add(average(allMins));
        returnThis.add((double) groupsAccepted/results.size());
        return returnThis;
    }
    static public List<Double> groupToMessageCloseness(PrintWriter out, Collection<Set<String>> results, List<Message> messages, Map<Integer, String> addressMap) throws IOException{
        List<Double> returnThis = new ArrayList<Double>();
        List<Double> allMins = new ArrayList<Double>();
        int groupsAccepted=0;
        for(Set<String> result : results) {
            double min = Double.MAX_VALUE;
            Message minMessage = null;
            for(Message m : messages) {
                Set<String> messageSet = getSetOfRecipentsAndFrom(m,addressMap);
                double closeness = (double) closeness(messageSet, result)/messageSet.size();
                if(closeness<min) {
                    min = closeness;
                    minMessage = m;
                }
            }

            if(min<1) {
                allMins.add(min);
                groupsAccepted++;
            }
        }
        returnThis.add(average(allMins));
        returnThis.add((double) groupsAccepted/results.size());
        return returnThis;
    }
    public static double average(List<Double> values ) {
        double sum =0;
        for(double n :values) {
            sum= sum + n;
        }
        return (double) sum/ (double)values.size();
    }
    static double averageInt(List<Integer> values ) {
        int sum =0;
        for(int n :values) {
            sum= sum + n;
        }
        return (double) sum/ (double)values.size();
    }
    public static void main(String[]args) {
        Set<String> s = new HashSet<String>();
        s.add("hi");
        s.add("andrew");
        s.add("ghobrial");
        int [] ints = {1,2,3};
        Map<Integer,String> map = new HashMap<Integer, String>();
        map.put(1,"andrew");
        map.put(2,"ghobrial");
        map.put(3,"hi");
     //   boolean found = isPerfectMatch(s, ints, map);
     //   System.out.println(found);

    }
}
