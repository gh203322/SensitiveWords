package toolgood.words.ehcache;

import org.apache.log4j.Logger;

import java.io.*;
import java.util.*;

/**
 * @author Administrator
 *  文件读写工具类
 */
@SuppressWarnings({"resource","unused"})
public class FileUtil {

    static Logger logger = Logger.getLogger(FileUtil.class);

    /**
     * 通过文件名称获取某个路径的这个文件实例（递归所有子目录）
     * @param dir 指定目录
     * @return
     */
    public static File getFileFromDirByFileName(File dir, String fileName) throws IOException{
        if(!dir.exists()){
            logger.info("目录:"+dir+"不存在!");
        }
        if(!dir.isDirectory()){
            logger.info(dir+"不是目录!");
        }
        if(null == fileName || "".equals(fileName)){
            logger.info("要查找的文件名不能为空!");
        }
        //如果要遍历子目录下的内容就需要构造成File对象做递归操作.File提供了直接返回对象的API
        File resultFile = null;
        File[] files = dir.listFiles();
        List<File> allFiles = new ArrayList(8);
        if (files!=null && files.length > 0){
            for (File file:files){
                if (file.isDirectory()){
                    //递归
                    resultFile = getFileFromDirByFileName(file,fileName);
                }else{
                    if (file.getName().equals(fileName)){
                        return file;
                    }
                }
            }
        }
        return resultFile;
    }

    /**
     * 以行为单位读取文件，读取到最后一行
     * @param filePath
     * @return
     */
    public static List<String> readFileContent(String filePath) {
        BufferedReader reader = null;
        List<String> listContent = new ArrayList<>();
        try {
            reader = new BufferedReader(new FileReader(filePath));
            String tempString = null;
            int line = 1;
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
                listContent.add(tempString);
                line++;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
        return listContent;
    }

    /**
     * 以行为单位读取文件，读取到最后一行
     * @param inputStream
     * @return
     */
    public static List<String> readFileContent(InputStream inputStream) {
        BufferedReader reader = null;
        List<String> listContent = new ArrayList<>();
        try {
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String tempString = null;
            int line = 1;
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
                listContent.add(tempString);
                line++;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
        return listContent;
    }
}
