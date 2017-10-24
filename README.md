# distributed-system
Hadoop MapReduce Indexing GCP

1. Go to https://cloud.google.com/free/ redeem the $300 Google Cloud Platform Credits. Make sure you use your “gmail” email.
2. Creating a Hadoop Cluster on Google Cloud Platform
  a. Create a Google Dataproc Cluster.
  b. Go to the VM Instances tab and click on the SSH button next to the instance with the Master Role.
  c. Clicking on the SSH button will take you to a Command line Interface(CLI) like an xTerm or Terminal. All the commands in the following steps are to be entered in the CLI. There is no home directory on HDFS for the current user so set up the user directory on HDFS. So, we’ll have to set this up before proceeding further. (To find out your user name run whoami) hadoop fs -mkdir -p /user/<your username here>
  d. Set up environment variables for JAVA and HADOOP_CLASSPATH. Please note that this step has to be done each time you open a new SSH terminal.
    ○ JAVA_HOME is already set-up. Do not change this.
    ○ export PATH=${JAVA_HOME}/bin:${PATH}
    ○ export HADOOP_CLASSPATH=${JAVA_HOME}/lib/tools.jar
  e. Run hadoop fs -ls
3. Upload Data(Books) to the Storage Bucket
 a. Uploading the input data into the bucket
 b. Get the books from either of the links below http://www-scf.usc.edu/~csci572/2017Fall/hw3/DATA.zip
 c. Go to the storage section in the left navigation bar select your cluster’s default bucket from the list of buckets. At the top you should see menu items UPLOAD FILES, UPLOAD FOLDER, CREATE FOLDER, etc. Click on the UPLOAD FOLDER button and upload the dev_data folder and full_data folder individually. This will take a while, but there will be a progress bar
4. Inverted Index Implementation using Map-Reduce
  Examples of Map-Reduce Jobs
        1. https://developer.yahoo.com/hadoop/tutorial/module4.html#wordcount
        2. https://hadoop.apache.org/docs/stable/hadoop-mapreduce-client/hadoop-mapreduce-clientcore/MapReduceTutorial.html
5. Creating a jar for your code
  a. The Google Cloud console requires us to upload a Map-Reduce job as a jar file. You can upload from Google Cloud console java file.
  b. In Google Cloud console. Say your Java Job file is called InvertedIndex.java. Create a JAR as follows:
        ○ hadoop com.sun.tools.javac.Main InvertedIndexJob.java
        If you get the following Note you can ignore them
        Note: InvertedIndexJob.java uses or overrides a deprecated API.
        Note: Recompile with -Xlint:deprecation for details.
        ○ jar cf invertedindex.jar InvertedIndex*.class
  c. Now you have a jar file for your job. You need to place this jar file in the default cloud bucket of your cluster. Just create a folder called JAR on your bucket and upload it to that folder. If you created your jar file on the cluster’s master node itself use the following commands to copy it to the JAR folder.
            ○ hadoop fs -copyFromLocal ./invertedindex.jar
            ○ hadoop fs -cp ./invertedindex.jar gs://< your dataproc-69070...>/JAR
6. Submitting the Hadoop job to your cluster:
    1. From the console’s UI.
        a. Go to the “Jobs” section in the left navigation bar of the Dataproc page and click on “Submit job”.
        b. Fill the job parameters as follows (see Figure 13 for reference):
            ○ Cluster: Select the cluster you created
            ○ Job Type: Hadoop
            ○ Jar File: Full path to the jar file you uploaded earlier to the Google storage bucket. Don’t forget the gs://
            ○ Main Class or jar: The name of the java class you wrote the mapper and reducer in.
            ○ Arguments: This takes two arguments
                i. Input: Path to the input data you uploaded
                ii. Output: Path to the storage bucket followed by a new folder name. The folder is created during execution. You will get an error if you give the name of an existing folder.
            ○ Leave the rest at their default settings
    2. The output files will be stored in the output folder on the bucket. If you open this folder you’ll notice that the inverted index is in several segments.(Delete the _SUCCESS file in the folder before merging all the output files)
    3. To merge the output files, run the following command in the master nodes command line(SSH) 
              ○ hadoop fs -getmerge gs://dataproc-69070458-bbe2-.../output ./output.txt
              ○ hadoop fs -copyFromLocal ./output.txt ○ hadoop fs -cp ./output.txt gs://dataproc-69070458-bbe2-.../output.txt
        The output.txt file in the bucket contains the full Inverted Index for all the books.
    
    
