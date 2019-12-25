package com.dsj.mock;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

/**
 * 模拟产生数据
 *
 * @author leelovejava
 */
public class AnalogData {

    /**
     * 读取日志数据
     *
     * @param inputFile
     * @param outputFile
     * @throws IOException
     * @throws InterruptedException
     */
    public static void readData(String inputFile, String outputFile) throws IOException, InterruptedException {
        FileInputStream fis = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        String tmp = null;
        try {
            fis = new FileInputStream(inputFile);
            isr = new InputStreamReader(fis, "UTF-8");
            br = new BufferedReader(isr);
            //计数器
            int counter = 1;
            //按行读取数据
            while ((tmp = br.readLine()) != null) {
                System.out.println("第" + counter + "行：" + tmp);
                //数据写入文件
                writeData(outputFile, tmp);
                counter++;
                //方便观察效果，控制数据产生速度
                Thread.sleep(300);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } finally {
            if (isr != null) {
                isr.close();
            }
        }
    }

    /**
     * 数据逐行写入文件
     *
     * @param outputFile
     * @param line
     * @throws FileNotFoundException
     */
    public static void writeData(String outputFile, String line) throws FileNotFoundException {
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile, true)));
            out.write("\n");
            out.write(line);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 程序入口
     *
     * @param args
     */
    public static void main(String[] args) {
        String inputFile = args[0];
        String outputFile = args[1];
        try {
            readData(inputFile, outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
