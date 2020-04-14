package toolgood.words.ehcache;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.apache.log4j.Logger;

/**
 使用API来动态的添加缓存(将缓存的配置信息通过java代码来实现而非写在配置文件)
 @author Administrator
 */
public class CacheContainer {

        static Logger logger;
        static CacheManager singletonManager;
        static String SENSITIVE_WORDS = "SENSITIVE_WORDS";
        static String SENSITIVE_WORDS_KEY = "SENSITIVE_WORDS_KEY";
        static String BASE_LIB_PATH = "sensitive.txt";
        //字符串分隔符(英文逗号)
        public static final String STRING_SPLIT_CHAR_COMMA = ",";

        static {
            logger = Logger.getLogger(CacheContainer.class);
            singletonManager = CacheManager.create();
        }

        /**
         * 创建缓存区间
         */
        public static void createCache() {
            Cache memoryOnlyCache = singletonManager.getCache(SENSITIVE_WORDS);
            //建立一个缓存实例
            if (memoryOnlyCache == null) {
                //项目名称   缓存最大数量 是否缓存成文件 设定缓存是否过期  对象存活时间   无访问多长时间缓存失效
                memoryOnlyCache = new Cache(SENSITIVE_WORDS, 8, false, false, 7200, 3600);
                //在内存管理器中添加缓存实例
                singletonManager.addCache(memoryOnlyCache);
            }
            logger.info("创建Ehcache缓存实例成功，实例名称：SENSITIVE_WORDS");
        }

        /**
         * 查询缓存中的数据
         *
         * @param key
         * @return
         */
        @SuppressWarnings("deprecation")
        public static Object getValue(String key) {
            //在缓存管理器中获取一个缓存实例
            Cache cache = singletonManager.getCache(SENSITIVE_WORDS);
            Element element = cache.get(key);
            if (element == null) {
                return null;
            }
            logger.info("从缓存EhCache中取出：[" + key + "]对应的值[" + element.getValue()+"]");
            return element.getValue();
        }

        /**
         * 添加缓存
         *
         * @param key
         * @param value
         */
        public static void setValue(String key, Object value) {
            logger.info("增加缓存[" + key + "]及对应的value值：[" + value+"]");
            Cache cache = singletonManager.getCache(SENSITIVE_WORDS);
            //使用获取到的缓存实例
            Element element = new Element(key, value);
            cache.put(element);//添加缓存值
        }

        /**
         * 删除缓存
         *
         * @param key
         */
        public static void remove(String key) {
            Cache cache = singletonManager.getCache(SENSITIVE_WORDS);
            Element element = cache.get(key);
            if (element != null) {
                cache.remove(key);
            }
            logger.info("删除" + key + "对应的值");
        }
}
