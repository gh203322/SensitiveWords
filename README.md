#SensitiveWords<br>
#基于开源敏感词识别项目的优化版敏感词识别工具<br>

##原项目地址：https://github.com/toolgood/ToolGood.Words  如果觉得不错请给原作者和我一个star,thanks！<br>
##此版本目前只包含移植优化了JAVA语言版本，后续完善之后会考虑上传到maven中央仓库。<br>

##在原版本的基础上优化了一下内容：<br>
>1、内置了敏感词库。
>2、引入了Ehcache缓存，一次性加载之后以后每次识别都从缓存加载，不需要每次进行IO读取。
>3、封装了一部分静态方法，使用更加方便。

##使用方法：
>1、作为工具类使用<br>
（1）初始化，先调用一次初始化方法,提供了3种初始化方法。
```
     SensitiveEhcacheManager.init() 通过加载内置敏感词资源初始化
     SensitiveEhcacheManager.init(String String sensitiveFilePath) 通过指定的文件路径加载敏感词并初始化
     SensitiveEhcacheManager.init(List<String> sensitiveWords) 通过传入敏感词集合初始化
``` 

（2）实例化WordsSearch，调用敏感词查找方法。
```
     WordsSearch wordsSearch = WordsSearch.build()
     List<WordsSearchResult> = wordsSearch.sensitiveWorsFilter(String text) text为待识别的文本
```

>2、在springboot中使用<br>
（1）可以在springboot项目启动时初始化敏感词库，创建一个启动类。

```
@Component
//指定启动器启动优先级顺序为1
@Order(value = 1)
public class EhcacheInitRunner implements ApplicationRunner {

    @Autowired
    private EhcacheUtil ehcacheUtil;

    @Override
    public void run(ApplicationArguments var1) throws Exception{
        // 读取敏感词词库并加载到缓存
        SensitiveEhcacheManager.init();
    }
}

（2）在接口需要调用的地方调用代码。
    WordsSearch wordsSearch = WordsSearch.build();
    List<WordsSearchResult> = wordsSearch.sensitiveWorsFilter(String text) text为待识别的文本
 ```
<br> 
##更多用法后续完善，to be continue...
