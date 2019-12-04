![Image text](https://raw.githubusercontent.com/kamjin1996/picFloder/master/png/toumingsuo.png?token=AKH3PY35MMLGIYHITDNAXAC546DVK)


**说明：**
简单快捷、热插拔式的数据加解密插件，支持全局数据库加密，支持字段 和基本类型参数前加注解，入库加密<=>出库解密

**使用方式:**

一、在项目配置文件中配置:

    dbcrypt.secretkey，值为密钥，长度24位，请自行定义，例如:

    dbcrypt.secretkey=123456789012345678901234

    dbcrypt.enable,值为是否开启加解密，true为开启，false关闭，例如：

    dbcrypt.enable=true

二、将属性注给配置类,这里采用了类似DataSource注入的方式;
在项目中新建配置类:

@Configuration
public class CryptInterceptConfig {

    @Bean
    @ConfigurationProperties(prefix = "dbcrypt")
    public Dbcrypt dbcrypt(){
        return new Dbcrypt();
    }

}

三、在项目的mybatis.xml中配置当前插件：

    <plugins>
       <plugin interceptor="intercept.crypt.CryptInterceptor"/>
    </plugins>

四、在需要加密的字段上注解@CryptField，也支持在mapper入参前注解@CryptField


注：master代码为最新代码，支持以上配置类的方式配置

代码如有需要改进及不妥之处，希望共同探讨，联系kamjin1996@163.com
