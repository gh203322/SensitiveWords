package toolgood.words.ehcache;


import com.alibaba.fastjson.JSONArray;
import net.sf.ehcache.CacheManager;
import org.apache.log4j.Logger;
import org.springframework.core.io.ClassPathResource;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 通过此类来初始化并创建资源缓存
 @author Administrator
 */
public class SensitiveEhcacheManager {

    static Logger logger;
    private static SensitiveEhcacheManager sensitiveEhcacheManager;

    static {
        logger = Logger.getLogger(CacheContainer.class);
        sensitiveEhcacheManager = new SensitiveEhcacheManager();
        CacheContainer.createCache();
    }

    /**
     * 敏感词库是否已经初始化
     */
    private Boolean isInit = false;
    /**
     * 敏感词库文件路径
     */
    private String sensitiveFilePath = null;
    /**
     * 敏感词库集合
     */
    private List<String> sensitiveWords = null;

    public SensitiveEhcacheManager(){

        this.sensitiveFilePath = sensitiveFilePath;
    }

    public SensitiveEhcacheManager(List<String> sensitiveWords){

        this.sensitiveWords = sensitiveWords;
    }

    public SensitiveEhcacheManager getInstance(){

        if (null == this.sensitiveEhcacheManager){
            this.sensitiveEhcacheManager = new SensitiveEhcacheManager();
        }
        return this.sensitiveEhcacheManager;
    }

    public String getSensitiveFilePath() {
        return sensitiveFilePath;
    }

    public void setSensitiveFilePath(String sensitiveFilePath) {
        this.sensitiveFilePath = sensitiveFilePath;
    }

    public List<String> getSensitiveWords() {
        return sensitiveWords;
    }

    public void setSensitiveWords(List<String> sensitiveWords) {
        this.sensitiveWords = sensitiveWords;
    }

    /**
     * 敏感词库初始化
     * @return
     */
    public static Boolean init(){

        logger.info("敏感词词库初始化到缓存开始...");
        if (!sensitiveEhcacheManager.isInit){
            // 从内部敏感词库加载
            FileInputStream fis = null;
            try {
                ClassPathResource classPathResource = new ClassPathResource(CacheContainer.BASE_LIB_PATH);
                fis = new FileInputStream(FileUtil.getFileFromDirByFileName(classPathResource.getFile(),CacheContainer.BASE_LIB_PATH));
            }catch (IOException e){
                e.printStackTrace();
            }
            if (null == fis){
                throw new RuntimeException("未获取到词库文件sensitive.txt，初始化失败!");
            }
            List<String> sensitiveWords = FileUtil.readFileContent(fis);
            if (null == sensitiveWords || sensitiveWords.isEmpty()){
                throw new RuntimeException("敏感词库内容为空，初始化失败!");
            }
            CacheContainer.setValue(CacheContainer.SENSITIVE_WORDS_KEY, JSONArray.toJSONString(sensitiveWords));
        }
        logger.info("敏感词词库初始化到缓存结束...");

        return true;
    }

    /**
     * 敏感词库初始化
     * @param sensitiveFilePath 指定的词库文件路径
     * @return
     */
    public static Boolean init(String sensitiveFilePath){

        logger.info("敏感词词库初始化到缓存开始...");
        if (null == sensitiveFilePath || "".equals(sensitiveFilePath)){
            throw new RuntimeException("指定的敏感词库路径不能为空，初始化失败!");
        }
        if (!sensitiveEhcacheManager.isInit){
            List<String> sensitiveWords = FileUtil.readFileContent(sensitiveFilePath);
            if (null == sensitiveWords || sensitiveWords.isEmpty()){
                throw new RuntimeException("敏感词库内容为空，初始化失败!");
            }
            CacheContainer.setValue(CacheContainer.SENSITIVE_WORDS_KEY, JSONArray.toJSONString(sensitiveWords));
        }
        logger.info("敏感词词库初始化到缓存结束...");

        return true;
    }

    /**
     * 敏感词库初始化
     * @param sensitiveWords 敏感词库集合
     * @return
     */
    public static Boolean init(List<String> sensitiveWords){

        logger.info("敏感词词库初始化到缓存开始...");

        if (null == sensitiveWords || sensitiveWords.isEmpty()){
            throw new RuntimeException("敏感词库集合不能为空，初始化失败!");
        }
        if (!sensitiveEhcacheManager.isInit){
            CacheContainer.setValue(CacheContainer.SENSITIVE_WORDS_KEY, JSONArray.toJSONString(sensitiveWords));
        }
        logger.info("敏感词词库初始化到缓存结束...");

        return true;
    }

    /**
     * 获取内存中所有敏感词
     *
     * @return
     */
    public static JSONArray getSensitiveWors() {

        Object object = CacheContainer.getValue(CacheContainer.SENSITIVE_WORDS_KEY);
        if (null != object){
            return JSONArray.parseArray((String)object);
        }
        return null;
    }

    /**
     * 从内存中增加指定敏感词
     * @param keywords 敏感词，多个用逗号分隔
     * @return
     */
    public static Boolean addSensitiveWors(String keywords) {

        List<String> sensitives = splitStringByTarChar(keywords);
        if (null == sensitives || sensitives.isEmpty()){
            return false;
        }
        JSONArray jsonArray = getSensitiveWors();
        if (null == jsonArray){
            return false;
        }
        for(String string: sensitives){
            jsonArray.add(string);
        }
        // 新增到缓存
        CacheContainer.setValue(CacheContainer.SENSITIVE_WORDS_KEY,jsonArray.toJSONString());

        return true;
    }

    /**
     * 从内存中删除指定敏感词
     * @param keywords 敏感词，多个用逗号分隔
     * @return
     */
    public static Boolean deleteSensitiveWors(String keywords) {

        List<String> sensitives = splitStringByTarChar(keywords);
        if (null == sensitives || sensitives.isEmpty()){
            return false;
        }
        JSONArray jsonArray = getSensitiveWors();
        if (null == jsonArray){
            return false;
        }
        for(String string: sensitives){
            jsonArray.remove(string);
        }
        // 刷新缓存
        CacheContainer.setValue(CacheContainer.SENSITIVE_WORDS_KEY,jsonArray.toJSONString());

        return true;
    }

    /**
     * 获取逗号分隔的字符串集合
     * @param string 逗号分隔的字符串
     * @return
     */
    public static List<String> splitStringByTarChar(String string) {

        if (null == string || "".equals(string)){
            return Collections.emptyList();
        }
        String[] strings = string.split(CacheContainer.STRING_SPLIT_CHAR_COMMA);
        if (null == strings || strings.length == 0){
            return Collections.emptyList();
        }
        return Arrays.asList(strings);
    }
}
