import java.io.*;
import java.util.*;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class InvertedIndexJob {
	
	public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
		
		if (args.length != 2) {
			System.err.println("Usage: InvertedIndexJob <input path> <output path>");
			System.exit(-1);
		}
		
		Configuration configuration = new Configuration();
		Job job = Job.getInstance(configuration, "Hadoop MapReduce Inverted Index");
		
		job.setJarByClass(InvertedIndexJob.class);
		
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		
		job.setMapperClass(InvertedIndexMapper.class);
		job.setReducerClass(InvertedIndexReducer.class);
		
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		
		
		
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}

	public static class InvertedIndexMapper extends Mapper<Object, Text, Text, Text> {

		private Text word = new Text();
		private Text docID = new Text();

		public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
			String line = value.toString();
			String[] cont = line.split("\t");
			StringTokenizer token = new StringTokenizer(cont[1]);
			docID.set(cont[0]);
			
			while (token.hasMoreTokens()) {
				word.set(token.nextToken());
				context.write(word, docID);
			}
		}
	}

	public static class InvertedIndexReducer extends Reducer<Text, Text, Text, Text> {
		private Text result = new Text();

		public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
			HashMap<String, Integer> invertedInMap = new HashMap<String, Integer>();
			
			for (Text value : values) {
				
				String docID = value.toString();
				
				if (invertedInMap.containsKey(docID)) {
					invertedInMap.put(docID, invertedInMap.get(docID) + 1);
				} else {
					invertedInMap.put(docID, 1);
				}
			}
			
			StringBuilder sb = new StringBuilder();
			for (Map.Entry<String, Integer> map : invertedInMap.entrySet()) {
				sb.append(map.getKey());
				sb.append(":");
				sb.append(map.getValue());
				sb.append("\t");
			}
			result.set(sb.toString());
			context.write(key, result);
		}
	}

	

}
