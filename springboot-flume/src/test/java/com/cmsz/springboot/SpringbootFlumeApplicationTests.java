package com.cmsz.springboot;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mortbay.util.ajax.JSON;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringbootFlumeApplicationTests {
	private static Map<String,String> filePoint=new HashMap<String,String>();

	@Test
	public void contextLoads() {
		readFilePoint();
		readFileByRandomAccessFile("F:\\test");

	}

	private File[] getAllFiles(String filepath){
		File file = new File(filepath);//File类型可以是文件也可以是文件夹
		return file.listFiles();//将该目录下的所有文件放置在一个File类型的数组中
	}

	private void readFileByRandomAccessFile(String filePath){
		RandomAccessFile randomAccessFile = null;
		String fileLinePoint="";
		try {
			File[] files=getAllFiles(filePath);
			for(File file:files){
				String fileName=file.getName();
				System.out.println(fileName);
				String sourceFilePath=file.getPath();
				randomAccessFile = new RandomAccessFile(sourceFilePath, "rw");
				long fileLength=randomAccessFile.length();
				System.out.println("文件长度为："+randomAccessFile.length());
				fileLinePoint=filePoint.getOrDefault(fileName,"0");
				if(Long.valueOf(fileLinePoint)<fileLength){
					randomAccessFile.seek(Integer.valueOf(fileLinePoint));
					//用于保存实际读取的字节数
					String line;
					while((line = randomAccessFile.readLine()) != null){
						String content=new String(line.getBytes("ISO-8859-1"), "utf-8");
						System.out.println("===="+content+"=====");
						fileLinePoint=String.valueOf(randomAccessFile.getFilePointer());
						filePoint.put(fileName,fileLinePoint);
						writeFilePoint(filePoint);
					}
				 }
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void writeFilePoint(Map<String,String> filePointMap){
		String filePointJson= JSON.toString(filePointMap);
		FileOutputStream fos=null;    //文件输出流
		try {
			byte [] words =filePointJson.getBytes();   //创建一个中转存放字节
			fos=new FileOutputStream("F:\\filePoint.txt");
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

	private void readFilePoint(){
		String pathname ="F:\\filePoint.txt";
		File filename = new File(pathname);
		try {
			InputStreamReader reader = new InputStreamReader(
					new FileInputStream(filename)); // 建立一个输入流对象reader
			BufferedReader br = new BufferedReader(reader);
			String line = br.readLine();
			if(!StringUtils.isEmpty(line)){
				filePoint=(Map<String,String>)JSON.parse(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
