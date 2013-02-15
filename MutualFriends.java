import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;
        
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.Reducer.Context;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
        
public class MutualFriends {
        
 public static class Map extends Mapper<LongWritable, Text, IntWritable, IntWritable> {
    
    private IntWritable userID = new IntWritable();
    private IntWritable userFriendAID = new IntWritable();
    private IntWritable userFriendBID = new IntWritable();
    
    public int makeInt(String s) {
        return Integer.parseInt(s);
    }
    
    public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String line = value.toString();
        String[] tokens = line.split("[    ,\n]");
        userID.set(makeInt(tokens[0]));
        // for every friend of the user, add the key (userID, negative userFriendID)
        for (int j = 1; j < tokens.length; j++) {
            userFriendAID.set(0 - makeInt(tokens[j]));
            context.write(userID, userFriendAID);
        }
        // for every possible pair of friends of the user, add the key (userA, userB)
        for (int i = 1; i < tokens.length; i++) {
            for (int j = 1; j < tokens.length && j != i; j++) {
                userFriendAID.set(makeInt(tokens[i]));
                userFriendBID.set(makeInt(tokens[j]));
                context.write(userFriendAID, userFriendBID);
                context.write(userFriendBID, userFriendAID);
            }
        }
        }
    } 
        
public static class Reduce extends Reducer<IntWritable, IntWritable, IntWritable, Text> {
    
    public void reduce(IntWritable key, Iterable<IntWritable> values, Context context) 
      throws IOException, InterruptedException {
        // key is recommended friend of the user, value is the count of how often (user, recommended user) showed up 
        HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
        // a set of users which are already friends of the user, so should not exist in the hashmap
        Set<Integer> set = new HashSet<Integer>();
        int value;
        int negValue;
        // iterate through list of key value pairs in the map function output
        for (IntWritable val : values) {
            value = val.get();
            // if the value is positive, it is a potential recommended friend
            if (value > 0) {
                // check if this user is already in the set of friends, if it is skip the loop
                if (!set.contains(value)) {
                    // check if the hashmap has this value, if so increment by one if not initialize at one
                    if (map.containsKey(value)) {
                        map.put(value, map.get(value) + 1);
                    }
                    else map.put(value, 1);
                }
            }
            // if value is negative, then add its negative to the set and remove from hashmap
            else {
                negValue = 0 - value;
                set.add(negValue);
                if (map.containsKey(negValue)) {
                    map.remove(negValue);
                }
            }
        }
        Set<Entry<Integer, Integer>> mapSet = map.entrySet();
        
        LinkedList<LinkedList<Integer>> grandList = new LinkedList<LinkedList<Integer>>();
        LinkedList<Integer> list;
        Integer key1;
        Integer value1;
        int index;
        for (Entry<Integer, Integer> entry : mapSet) {
            key1 = entry.getKey();
            value1 = entry.getValue();
            index = 0;
            // go through linked list until finding a list whose second element is smaller than this value and insert this user as the tail of this list, the head of the list is the userCount
            while (index < grandList.size() && (grandList.get(index)).get(0) > value1) {
                index++;
            }
            // if you have reached end of list or next element is strictly smaller, add a new list at this index of the grandList with userCount as head and userID as tail
            if (index == grandList.size() || (grandList.get(index)).get(0) < value1) {
                list = new LinkedList<Integer>();
                list.add(value1);
                list.add(key1);
                grandList.add(index, list);
            }
            // if it is not end of list and the next element has head value equal to the userCount of this user, append this userID to that list and sort the linked list
            else {
                list = grandList.get(index);
                list.add(key1);
            }
        }
        
        String finalList = "";
        
        int count = 0;
        for (LinkedList<Integer> l : grandList) {
            for (int j = 1; count < 10 && j < l.size(); j++) {    
                finalList = finalList + l.get(j) + ",";
                count++;
            }
        }
        if (finalList.length() > 0 && finalList.charAt(finalList.length()-1) == ',') {
            finalList = finalList.substring(0, finalList.length()-1);
        }
        
        Text finalText = new Text();
        finalText.set(finalList);
        
        context.write(key, finalText);
     
    }
 }
        
 public static void main(String[] args) throws Exception {
    Configuration conf = new Configuration();
        
    Job job = new Job(conf, "MutualFriends");
    job.setJarByClass(WordCount.class);
    job.setOutputKeyClass(IntWritable.class);
    job.setOutputValueClass(IntWritable.class);
        
    job.setMapperClass(Map.class);
    job.setReducerClass(Reduce.class);
        
    job.setInputFormatClass(TextInputFormat.class);
    job.setOutputFormatClass(TextOutputFormat.class);
        
    FileInputFormat.addInputPath(job, new Path(args[0]));
    FileOutputFormat.setOutputPath(job, new Path(args[1]));
        
    job.waitForCompletion(true);
    
    
 }
        
}
