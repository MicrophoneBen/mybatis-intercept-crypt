说明：
本项目提供数据库敏感数据的加解密，采用mybatis的拦截器实现。加密方式为Base64+AES192的方式

**使用方式:**

一、在项目配置文件中配置:

dbcrypt.secretkey，值为密钥，长度24位，请自行定义，例如:

dbcrypt.secretkey=123456789012345678901234

dbcrypt.enable,值为是否开启加解密，true为开启，false关闭，例如：

dbcrypt.enable=true

二、将属性注给配置类,这里采用了DataSource属性注入类似的方式;
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

···<plugins>
        <plugin interceptor="intercept.crypt.CryptInterceptor"/>
</plugins>···

四、在需要加密的字段上注解@CryptField，也支持在mapper入参前注解@CryptField


注：master代码为最新代码，支持以上配置类的方式配置
