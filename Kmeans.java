import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
        
public class Kmeans {
    public static double cost;
    
    public static double[] makeDoubleArray(String s1) {
        String[] s = s1.split("[ ]");
        int n = s.length;
        double[] doubleArray = new double[n];
        for (int i = 0; i < n; i++) {
            doubleArray[i] = Double.parseDouble(s[i]);
        }
        return doubleArray;
    }
    
    public static String makeString(double[] a) {
        StringBuilder s = new StringBuilder();
        int n = a.length;
        for (int i = 0; i < n-1; i++) {
            s.append(a[i]+" ");
        }
        s.append(a[n-1]);
        return s.toString();
    }
    // distance between two points
    public static double distance(double[] v1, double[] v2) {
        double distance = 0.0;
        int n = v1.length;
        for (int i = 0; i < n; i++) {
            distance += (v1[i]-v2[i])*(v1[i]-v2[i]);
        }
        return distance;
    }     
    
 public static class Map extends Mapper<LongWritable, Text, Text, Text> {
    private Text centroidTxt = new Text();
    private Text pointTxt = new Text();
    
    
    public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String centerPath = context.getConfiguration().get("centerPath");
        FileInputStream fstream = new FileInputStream(centerPath);
        // Get the object of DataInputStream
        DataInputStream in = new DataInputStream(fstream);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String strLine;
        double[] point, closestCentroid = null;
        ArrayList<double[]> centroids = new ArrayList<double[]>();

        while ((strLine = br.readLine()) != null) {
            // Print the content on the console
            double[] centroidA = makeDoubleArray(strLine);
            centroids.add(centroidA);
        }
        br.close();
        
        String line = value.toString();
        
        point = makeDoubleArray(line);
        
        double dist = 999999999.9;
        double currDist;
        for (double[] centroid : centroids) {
            currDist = distance(centroid, point);
            if (dist > currDist) {
                dist = currDist;
                closestCentroid = centroid;
            }
        }
        String centroidStr = makeString(closestCentroid);
        centroidTxt.set(centroidStr) ;
        pointTxt.set(line);
        context.write(centroidTxt, pointTxt);
        }
    } 
        
public static class Reduce extends Reducer<Text, Text, Text, Text> {
    private Text centroidTxt = new Text();
    private Text blank = new Text();
    public void reduce(Text key, Iterable<Text> values, Context context) 
      throws IOException, InterruptedException {
        double[] centroid = makeDoubleArray(key.toString());
        int d = centroid.length;
        double[] newCentroid = new double[d];
        String pointStr;
        double[] point;
        
        int numPoints = 0;
        for (Text pointTxt : values) {
            numPoints++;
            pointStr = pointTxt.toString();
            point = makeDoubleArray(pointStr);
            for (int i = 0; i < d; i++) {
                newCentroid[i] += point[i];
            }
            cost += distance(centroid, point);
        }
        
        for (int i = 0; i < d; i++) {
            newCentroid[i] /= (double) numPoints;
        }
        
        centroidTxt.set(makeString(newCentroid));
        context.write(blank, centroidTxt);
     
    }
 }
        
 public static void main(String[] args) throws Exception {
    Configuration conf = new Configuration();
    Path inputDir, centerPath;
    String centerStr;
    // this should be .../data.txt
    String inputStr = args[0];
    // this should be .../Desktop/clusterOut
    String outputStr = args[1];
    // since all output files of hadoop have this filename, this will be used for input filenames as well
    String centerFile = "/part-r-00000";
    // this will initially be .../Desktop/clusterOut00, subsequently /Desktop/clusterOut01, clusterOut02 etc till clusterOut20
    inputDir = new Path(inputStr);
    final int maxIter = 20;
    double[] costs = new double[maxIter];
     
    for (int i = 0; i < maxIter; i++) {
        Kmeans.cost = 0;
        centerStr = outputStr + String.format("%02d", i) + centerFile;
        conf.set("centerPath", centerStr);
        Job job = new Job(conf, "Kmeans");
        job.setJarByClass(Kmeans.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);
            
        job.setMapperClass(Map.class);
        job.setReducerClass(Reduce.class);
            
        job.setInputFormatClass(TextInputFormat.class);
        job.setOutputFormatClass(TextOutputFormat.class);
            
        FileInputFormat.addInputPath(job, inputDir);
        centerStr = outputStr + String.format("%02d", i+1);
        centerPath = new Path(centerStr);
        FileOutputFormat.setOutputPath(job, centerPath);
        
        job.waitForCompletion(true);
        costs[i] = Kmeans.cost; 
    }
    
    for (int i = 0; i < maxIter; i++) {
        System.out.println(costs[i]);
    }
    
 }
        
}
