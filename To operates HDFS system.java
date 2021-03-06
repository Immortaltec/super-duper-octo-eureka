#操作HDFS的三种常用方法
##HDFS Shell
#操作HDFS的三种常用方法
##HDFS Shell
/*首先打开hdfs
kyle@ubuntu:/usr/local/hadoop$ start-all.sh
（1）列举一个目录的所有文件
kyle@ubuntu:/usr/local/hadoop$ hdfs dfs -ls /
Found 1 items
drwxr-xr-x   - hadoop supergroup          0 202203-11 18:40 /user

kyle@ubuntu:/usr/local/hadoop$ hadoop fs -ls /
Found 1 items
drwxr-xr-x   - hadoop supergroup          0 202203-11 18:40 /user

（2）创建文件夹
kyle@ubuntu:/usr/local/hadoop$ hdfs dfs -mkdir /dataset
kyle@ubuntu:/usr/local/hadoop$ hadoop fs -mkdir /dataset
kyle@ubuntu:/usr/local/hadoop$ hdfs dfs -ls /
Found 2 items
drwxr-xr-x   - hadoop supergroup          0 202203-11 22:32 /dataset
drwxr-xr-x   - hadoop supergroup          0 202203-11 18:40 /user

（3）将本地文件上传至HDFS
kyle@ubuntu:/usr/local/hadoop$ hdfs dfs -put /home/kyle/Documents/kyle01/zhaohaosen.txt /dataset
kyle@ubuntu:/usr/local/hadoop$ hadoop fs -put /home/kyle/Documents/kyle01/zhaohaosen.txt /dataset
kyle@ubuntu:/usr/local/hadoop$ hdfs dfs -ls /dataset
Found 1 items
-rw-r--r--   1 kyle supergroup          0 202203-11 22:37 /dataset/zhaohaosen.txt

（4）将文件从HDFS下载到本地文件系统
kyle@ubuntu:/usr/local/hadoop$ hdfs dfs -get /dataset/zhaohaosen.txt /home/kyle/Downloads
kyle@ubuntu:/usr/local/hadoop$ ls /home/kyle/Downloads
kyle_me.txt

（5）查看文件的内容
kyle@ubuntu:/usr/local/hadoop$ hdfs dfs -text /dataset/kyle_me.txt

（6）删除文件或者文件夹
kyle@ubuntu:/usr/local/hadoop$ hdfs dfs -rm /dataset/kyle_me.txt
Deleted /dataset/kyle_me.txt
kyle@ubuntu:/usr/local/hadoop$ hdfs dfs -ls /dataset
kyle@ubuntu:/usr/local/hadoop$
*/

##HDFS Web
主机名/localhost+":"+50070(attention:首先保证hdfs已启动start-dfs.sh;start-yarn.sh)

##HDFS API
//使用JavaApi操作HDFS，使用的是MAVEN，操作的环境是Linux  
//首先要配置好Maven环境，我使用的是已经有的仓库，
//如果你下载的jar包 速度慢，可以改变Maven 下载jar包的镜像站改为 阿里云。
//使用到的jar包
<dependencies>
        <dependency>
            <groupId>org.apache.hadoop</groupId>
            <artifactId>hadoop-common</artifactId>
            <version>2.10.0</version>
        </dependency>
        <dependency>
            <groupId>org.apache.hadoop</groupId>
            <artifactId>hadoop-hdfs</artifactId>S
            <version>2.10.0</version>
        </dependency>
    </dependencies>
//java aip
/**
 * Created by kyle on 22-3-14.
 */

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import java.net.URI;

public class wang_text {
    //指定默认的HDFS的路径
    String hdfsURL = "hdfs://localhost:9000";

    FileSystem fs = null;
    Configuration configuration = null;


    //构造函数
    public wang_text() {
        try {
            configuration = new Configuration();
            fs = FileSystem.get(URI.create(hdfsURL), configuration);
        } catch (Exception e) {
            System.out.println("a exception");
        }
    }

    //main函数
    public static void main(String[] args) {
        wang_text hdfsclient = new wang_text();
        hdfsclient.mkdir();
        hdfsclient.create();
        hdfsclient.put();
        hdfsclient.get();
        hdfsclient.detele();
    }



    //在HDFS根目录下的daStaset目录下创建一个test子目录
    public void mkdir() {
        try {
            boolean maked=fs.mkdirs(new Path("/dataset/test"));
            System.out.println("a dir is created！");
        }catch (Exception e){
            System.out.println("a exception");
        }
    }
    //在HDFS根目录下的dataset目录下test子目录中创建一个文件
    public void create() {
        try {
            FSDataOutputStream output=fs.create(new Path("/dataset/test/example.txt"));
            output.write("nihao".getBytes());
            output.flush();
            output.close();
            System.out.println("a file is created！");
        }catch (Exception e){
            System.out.println("a exception");
        }
    }
    //将本地“/home/hadoop/”路径下的文件example.txt上传到HDFS
//的“/dataset/test”路径下
    public void put() {
        try {
            fs.copyFromLocalFile(new Path("/home/kyle/wangyongkai.txt"),
                    new Path("/dataset/test/"));
            System.out.println("a file is put to HDFS！");
        }catch (Exception e){
            System.out.println("a exception");
        }
    }
    //将HDFS“/dataset/test”路径下的example.txt文件下载到
//本地“/home/hadoop/”路径下
    public void get() {
        try {
            fs.copyToLocalFile(new Path("/dataset/test/example.txt"),
                    new Path("/home/kyle/"));
            System.out.println("a file is got from HDFS！");
        }catch (Exception e){
            System.out.println("a exception");
        }
    }
    //将HDFS“/dataset/test”路径下的example.txt文件删除
    public void detele() {
        try {
            boolean delete = fs.delete(new Path("/dataset/test/example.txt"), true);
            System.out.println("a file is deleted！");
        }catch (Exception e){
            System.out.println("a exception");
        }
    }

}

##others
vim maven/conf/settings.xml
  <localRepository>/home/kyle/repository</localRepository>
   <mirror>
      <id>alimaven</id>
      <mirrorOf>central</mirrorOf>
      <name>aliyun maven</name>
      <url>http://maven.aliyun.com/nexus/content/repositories/central/</url>
   </mirror>

//log4j error solution
//under 'resorce' menu ,creat log4j.propertise file,add thiese demo
hadoop.root.logger=DEBUG, console
log4j.rootLogger = DEBUG, console
log4j.appender.console=org.apache.log4j.ConsoleAppender
log4j.appender.console.target=System.out
log4j.appender.console.layout=org.apache.log4j.PatternLayout
log4j.appender.console.layout.ConversionPattern=%d{yy/MM/dd HH:mm:ss} %p %c{2}: %m%n
