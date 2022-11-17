package demo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class Filereader {

    public double[][] getdata(String pathName,int col1) {
//        这里声明维度还要修改一下
        double[][] data = new double[10000][col1];
        try {
            /* 读入TXT文件 */
//            String pathname = "/Users/lium/Desktop/test.txt";
            File filename = new File(pathName); // 要读取以上路径的input。txt文件
            InputStreamReader reader = new InputStreamReader(
                    new FileInputStream(filename)); // 建立一个输入流对象reader
            BufferedReader br = new BufferedReader(reader); // 建立一个对象，它把文件内容转成计算机能读懂的语言
            String line = "  ";
            int row = 0;
            while (line != null) {
                String delimeter = ",";
                line = br.readLine();
                if (line != null) {
//                    System.out.println(line);
                    String[] res = line.split(delimeter);
                    for (int i = 0; i < res.length; i++) {
//                        System.out.println(res[i]);
                        data[row][i] = Double.parseDouble(res[i]);
                    }
                }
                row++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        };

        return data;
    }
}
