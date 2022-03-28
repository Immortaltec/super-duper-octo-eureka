package Kyle;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
/**
 * Created by Kyle on 22-3-23.
 */
        import java.io.*;
        import org.apache.hadoop.io.IntWritable;
        import org.apache.hadoop.io.Writable;

public class WordCountAndLen implements Writable {
    private IntWritable count;
    private IntWritable length;
    //构造方法
    public WordCountAndLen (){
        set(new IntWritable(), new IntWritable());
    }

    //构造方法
    public WordCountAndLen (IntWritable count, IntWritable length){
        set(count, length);
    }

    //接收int型数据进行构造
    public WordCountAndLen (int count, int length){
        set(new IntWritable(count), new IntWritable(length));
    }

    //set方法
    public void set(IntWritable count, IntWritable length){
        this. count = count;
        this. length = length;
    }
    public IntWritable getCount(){
        return count;
    }

    //get方法
    public IntWritable getLength(){
        return length;
    }

    //重载序列化方法

    public void write(DataOutput out) throws IOException {
        count.write(out);
        length.write(out);
    }

    //重载反序列化方法

    public void readFields(DataInput in) throws IOException{
        count.readFields(in);
        length.readFields(in);
    }

}
 
package Kyle;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;


public class Main {
    /*
    1.WordCount的main函数。
    2.main函数主要创建一个job对象，然后对WordCount任务所需的map函数、reduce函数、输入文件路径、输出文件路径等信息进行配置。
    */
    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "new word count");//获取一个任务实例
        job.setJarByClass(WordCount.class);//设置工作类
        job.setMapperClass(MyMapper.class);//设置Mapper类
        job.setReducerClass(MyReducer.class);//设置Reducer类vim
        job.setOutputKeyClass(Text.class);//设置输出键值对中key的类型
        job.setOutputValueClass(IntWritable.class);//设置输出键值对中value的类型
        FileInputFormat.addInputPath(job, new Path(args[0]));//设置输入文件的路径
        FileOutputFormat.setOutputPath(job, new Path(args[1]));//设置输出文件的路径
        FileSystem fs=FileSystem.get(conf);//获取HDFS文件系统
        fs.delete(new Path(args[1]),true);//删除输出路径下可能已经存在的文件
        boolean result=job.waitForCompletion(true);//提交运行任务
        System.exit(result? 0: 1);//如result为 false则等待任务结束
    }
}
 
package Kyle;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

/**
 * Created by kyle on 22-3-23.
 */
//自定义Reduce类，主要是在类中重载reduce函数。
public class MyReducer
        extends Reducer<Text,IntWritable,Text,IntWritable> {
    //从这可以看出reduce处理的输入数据是<key, value-list>类型的键值对
    public void reduce(Text key, Iterable<IntWritable> values,Context context)
            throws IOException, InterruptedException {
        int sum = 0;
        //reduce函数就是对列表values中的数值进行相加
        for (IntWritable val : values) {
            sum += val.get();
        }
        WordCountAndLen result=new WordCountAndLen(sum,key.getLength());
        //将结果写入context
        context.write(key, result.getCount());
        context.write(key, result.getLength());
    }
}
 
package Kyle;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;
import java.util.StringTokenizer;

/**
 * Created by kyle on 22-3-23.
 */
public class MyMapper extends Mapper<Object, Text, Text, IntWritable> {
    private final static IntWritable one = new IntWritable(1);
    //创建一个IntWritable类的对象，初始化为1，用来将键值对的value初始化为1
    private Text word = new Text();//创建Text类的对象，用于处理文本
    public void map(Object key, Text value, Context context)
            throws IOException, InterruptedException {
        StringTokenizer itr = new StringTokenizer(value.toString());
        //构造一个用来解析value的对象，默认用空格分隔
        while (itr.hasMoreTokens()) {
            word.set(itr.nextToken());
            //char c= (char) word.charAt(word.getLength()-1);
            //if(c==',')
            //word.charAt(Array.getLength(word)-1)='1';
            String word1=word.toString();
            word1=word1.replace(",","");
            word1=word1.replace(".","");
            word1=word1.replace(":","");
            word.set(word1);
            //将结果写入context
            context.write(word, one);
        }
    }
}
