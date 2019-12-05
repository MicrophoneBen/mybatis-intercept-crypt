![](https://ftp.bmp.ovh/imgs/2019/12/9fdfb3fbd3e6225d.jpeg)


# 说明：

简单快捷、热插拔式的数据加解密插件，支持全局数据库加密 -> 入库加密<=>出库解密

优点：

+ 采用jar包的形式依赖，侵入极小

+ 只需在需加密的数据模型类字段上、或mapperInterface的方法参数前，加@CryptField 注解，便可对目标加解密

+ 配置方式简单，适用任何采用了mybatis作为DAO层的java项目

局限性：

+ 不支持mybatis逆向生成的数据模型example类（一般项目很少会使用逆向生成工具拓展的example类，与数据模型bean的耦合性太强）

## 配置方式:

1. spring配置文件形式：

   *PS：关于参数的key，可以取任意参数，只要确保能被spring解析到即可。
   
    下面是例子：

        dbcrypt.enable=true #加密插件是否启用
        dbcrypt.secretkey=123456789012345678901234 #加密密钥（注意值长度和AES加密方式要匹配才可以）

2. 新建配置类:

        @Configuration
        @Data
        public class MybatisConfig {

            @Value("${dbcrypt.secretkey}")
            private String secretkey;

            @Value("${dbcrypt.enable}")
            private boolean enable;

            @Bean
            public CryptInterceptor cryptInterceptor() {
                return new CryptInterceptor();
            }

            @Bean
            public Dbcrypt dbcrypt() {
                return new Dbcrypt(AesEnum.AES192, getSecretkey(), isEnable());
            }
        }
 

配置完毕，注意配置的AES方式，上例中为192标准，请改为自己需要的标准，并且secretKey长度要匹配


### 注意:

假如是比较老的项目，插件的注册也可以在mybatis.xml中完成，如下：
      
        <plugins>
            <plugin interceptor="intercept.crypt.CryptInterceptor"/>
        </plugins>
        
其他bean（Dbcrypt类），请在applicationContext.xml中定义，并配置AES加密标准、密钥和是否启用即可


## 使用方式:

在需要加密的数据模型字段上注解@CryptField，也支持在mapper入参前注解@CryptField；
需要注意的是，暂不支持在数据模型的父类注解，入参是子类的形式

*附上快速开始的demo地址：

# 最后：

工作需求衍生的项目，对这个插件有相关疑问，或者希望共同探讨的朋友，请联系作者qq1569558447，163邮箱：kamjin1996@163.com

