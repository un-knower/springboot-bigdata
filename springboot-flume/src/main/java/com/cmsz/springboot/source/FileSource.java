package com.cmsz.springboot.source;

import com.google.common.base.Preconditions;
import org.apache.commons.lang.StringUtils;
import org.apache.flume.Context;
import org.apache.flume.EventDeliveryException;
import org.apache.flume.PollableSource;
import org.apache.flume.conf.Configurable;
import org.apache.flume.event.EventBuilder;
import org.apache.flume.source.AbstractSource;
import org.mortbay.util.ajax.JSON;

import java.io.*;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by le on 2018/1/29.
 */
public class FileSource extends AbstractSource implements Configurable, PollableSource {

    private static Map<String,String> fileOffset=new HashMap<String,String>();

    private String FILE_PATH="";

    private String OFFSET_FILE="";

    @Override
    public void configure(Context context) {
        FILE_PATH = context.getString("file_path");
        Preconditions.checkNotNull(FILE_PATH, "file_path must be set!!");
        OFFSET_FILE = context.getString("offset_file");
        Preconditions.checkNotNull(OFFSET_FILE, "offset_file must be set!!");
    }

    @Override
    public Status process()throws EventDeliveryException {
        Status status = null;
        String fileLinePoint="";
        RandomAccessFile randomAccessFile = null;
        try {
            File[] files=getAllFiles(FILE_PATH);
            for(File file:files){
                String filePath=file.getPath();
                String fileName=file.getName();
                randomAccessFile = new RandomAccessFile(filePath, "rw");
                /*获取文件对应的偏移量*/
                fileLinePoint = fileOffset.getOrDefault(fileName, "0");
                long fileLength=randomAccessFile.length();
                if(Long.valueOf(fileLinePoint)<fileLength) {
                    randomAccessFile.seek(Long.valueOf(fileLinePoint));
                    //用于保存实际读取的字节数
                    String line;
                    while ((line = randomAccessFile.readLine()) != null) {
                        String content = new String(line.getBytes("ISO-8859-1"), "utf-8");
                        System.out.println("====" + content + "=====");
                        /*将数据源进行分流处理，为后续conf文件中获取header中mapping分流处理*/
                        HashMap<String, String> header = new HashMap<String, String>();
                        header.put("level", "ERROR");
                        this.getChannelProcessor()
                                .processEvent(EventBuilder.withBody(content, Charset.forName("UTF-8"), header));
                        status = Status.READY;
                        /*写偏移量*/
                        fileLinePoint = String.valueOf(randomAccessFile.getFilePointer());
                        fileOffset.put(fileName, fileLinePoint);
                        writeFileOffset(fileOffset);
                    }
                }else{
                    status = Status.READY;
                }
            }
        }catch (Exception e){
            status = Status.BACKOFF;
        }
        try {
            Thread.sleep(5000);
            System.out.println("====线程休眠5s=====");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return status;
    }

    @Override
    public long getBackOffSleepIncrement() {
        return 0;
    }

    @Override
    public long getMaxBackOffSleepInterval() {
        return 0;
    }

    @Override
    public synchronized void start() {
        super.start();
        /*读取历史偏移量*/
        readFileOffset();
    }

    @Override
    public synchronized void stop() {
        super.stop();
    }

    private File[] getAllFiles(String filepath){
        File file = new File(filepath);//File类型可以是文件也可以是文件夹
        return file.listFiles();//将该目录下的所有文件放置在一个File类型的数组中
    }

    /**
     * 以行为单位读取文件，常用于读面向行的格式化文件
     */
    public  void readFileByRandomAccessFile(String fileName) {
        RandomAccessFile randomAccessFile = null;
        String fileLinePoint="";
        try {
            File file=new File(fileName);
            if(file.exists()){
                System.out.println(file.getName());
                randomAccessFile = new RandomAccessFile(fileName, "rw");
                fileLinePoint=fileOffset.getOrDefault(fileName,"0");
                randomAccessFile.seek(Integer.valueOf(fileLinePoint));
                //用于保存实际读取的字节数
                String line;
                while((line = randomAccessFile.readLine()) != null){
                    String content=new String(line.getBytes("ISO-8859-1"), "utf-8");
                    System.out.println("===="+content+"=====");
                    fileLinePoint=String.valueOf(randomAccessFile.getFilePointer());
                    fileOffset.put(fileName,fileLinePoint);
                    writeFileOffset(fileOffset);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /*将偏移量写入目标文件中*/
    private void writeFileOffset(Map<String,String> filePointMap){
        String filePointJson= JSON.toString(filePointMap);
        FileOutputStream fos=null;    //文件输出流
        try {
            byte [] words =filePointJson.getBytes();   //创建一个中转存放字节
            fos=new FileOutputStream(OFFSET_FILE);
            fos.write(words,0,words.length);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(fos!=null){
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void readFileOffset(){
        File filename = new File(OFFSET_FILE);
        try {
            InputStreamReader reader = new InputStreamReader(
                    new FileInputStream(filename)); // 建立一个输入流对象reader
            BufferedReader br = new BufferedReader(reader);
            String line = br.readLine();
            if(!StringUtils.isEmpty(line)){
                fileOffset=(Map<String,String>)JSON.parse(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
