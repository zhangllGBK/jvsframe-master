### JDBC组件

#### 详细介绍

主要包括

* 支持O/R Mapping
* 支持SQL操作

#### 组件配置

- maven依赖配置

```xml
<dependency>
    <groupId>com.jarveis</groupId>
    <artifactId>jvsframe-jdbc</artifactId>
    <version>3.0.0</version>
</dependency>
```

- config.xml配置文件

```xml
<?xml version="1.0" encoding="utf-8"?>
<config>
    
    <module>
　　　　　...
        <parser clazz="com.jarveis.frame.jdbc.JdbcParser"/>
        ...
    </module>

    <jdbcConfig>
        <datasource id="db56" default="true">
            <property name="druid.driverClassName" value="org.gjt.mm.mysql.Driver" />
            <property name="druid.url" value="jdbc:mysql://192.168.1.100:3306/test" />
            <property name="druid.username" value="shfcoc" />
            <property name="druid.password" value="shfcoc" />
            <!-- 初始化时获取的连接数， -->
            <property name="druid.initialSize" value="5" />
            <!-- 连接池中保留的最大连接数。 -->
            <property name="druid.maxActive" value="15" />
            <!-- 连接池中保留的最小连接数。 -->
            <property name="druid.minIdle" value="3" />
            <!-- 配置获取连接等待超时的时间(ms)。 -->
            <property name="druid.maxWait" value="10000" />
            <!-- 配置间隔多久才进行一次检测，检测需要关闭的空闲连接(ms)， -->
            <property name="druid.timeBetweenEvictionRunsMillis" value="60000" />
            <!-- 配置一个连接在池中最小生存的时间(ms)。 -->
            <property name="druid.minEvictableIdleTimeMillis" value="300000" />
            <!-- 申请连接的时候检测，如果空闲时间大于timeBetweenEvictionRunsMillis，执行validationQuery检测连接是否有效。 -->
            <property name="druid.testWhileIdle" value="true" />
            <!-- 申请连接时执行validationQuery检测连接是否有效。 -->
            <property name="druid.testOnBorrow" value="true" />
            <!-- 归还连接时执行validationQuery检测连接是否有效。 -->
            <property name="druid.testOnReturn" value="false" />
            <!-- 验证连接有效与否的SQL。 -->
            <property name="druid.validationQuery" value="SELECT 1 FROM DUAL" />
        </datasource>
    </jdbcConfig>
    
    <sql-files>
        <file path=""/>
    </sql-files>
    
</config>
```



### 测试表

#### 用户表(user)

| 字段名 | 数据类型    | 备注           |
| ------ | ----------- | -------------- |
| suid   | bigint      | 用户主键（PK） |
| uname  | varchar(20) | 用户名         | 
| uphone | varchar(11) | 手机号         |

```sql
create table user(
    suid bigint(20) not null,
    uname varchar(20) not null,
    uphone varchar(11) not null,
    primary key(suid)
)
```



#### 银行卡表(user_bank)

| 字段名   | 数据类型    | 备注           |
| -------- | ----------- | -------------- |
| suid     | bigint      | 用户主键（PK） |
| bankcode | varchar(20) | 银行卡号（PK)  |
| bankname | varchar(11) | 银行名称       |

```sql
create table user_bank(
    suid bigint(20) not null,
    bankcode varchar(20) not null,
    bankname varchar(11) not null,
    primary key(bankcode, bankname)
)
```



### SQL操作

#### 添加数据

```java
public class createUserService implements Service {
    
    public Param callService(Param in) {
        Param out = new Param(Param.RESP);
        String uname = in.getBody().getString("@uname"); // 用户名
        String uphone = in.getBody().getString("@uphone"); // 手机号
        long suid = 10000001L;
        try {
            int result = JdbcUtil.excute("insert into user(suid, uname, uphone) values(?,?,?)", new Object[]{suid, uname, uphone});
            if (result > 0) {
                out.getHead().setProperty("@errcode", "0000");
            } else {
                out.getHead().setProperty("@errcode", "1001");
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
        }
        
        return out;
    }
    
}
```



#### 查询数据

```java
public class LoadUserService implements Service {
    
    public Param callService(Param in) {
        Param out = new Param(Param.RESP);
        String uphone = in.getBody().getString("@uphone"); // 手机号
        
        HashMap<String, String> params = new HashMap<String, String>(1);
        params.put("uphone", uphone);
        
        Map<String, Object> mapResult = new HashMap<String, Object>();
        try {
            mapResult = (Map) JdbcUtil.query("select * from user where uphone = :uphone limit 1", new MapHandler(), params);
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
        }

        Param item = out.getBody().addParam("user");
        item.setProperty("@suid", (Long) mapResult.get("suid"));
        item.setProperty("@uname", (String) mapResult.get("uname"));
        item.setProperty("@uphone", (String) mapResult.get("uphone"));
        
        return out;
    }
    
}
```



#### 更新数据

```java
public class updateUserService implements Service {
    
    public Param callService(Param in) {
        Param out = new Param(Param.RESP);
        String suid = in.getBody().getString("@suid"); // 用户id
        String uphone = in.getBody().getString("@uphone"); // 手机号
        try {
            int result = JdbcUtil.excute("update user set uphone = ? where suid = ?", new Object[]{uphone, suid});
            if (result > 0) {
                out.getHead().setProperty("@errcode", "0000");
            } else {
                out.getHead().setProperty("@errcode", "1001");
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
        }
        
        return out;
    }
    
}
```



#### 删除数据

```java
public class deleteUserService implements Service {
    
    public Param callService(Param in) {
        Param out = new Param(Param.RESP);
        String suid = in.getBody().getString("@suid"); // 用户id
        try {
            int result = JdbcUtil.excute("delete from user where suid = ?", new Object[]{suid});
            if (result > 0) {
                out.getHead().setProperty("@errcode", "0000");
            } else {
                out.getHead().setProperty("@errcode", "1001");
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
        }
        
        return out;
    }
    
}
```






### O/R Mapping

#### Mapping Bean

```java
@Table(name = "user")
public class User {
    
    @Column(primaryKey = true)
    private Long suid;
    @Column
    private String uname;
    @Column
    private String uphone;

    
    public User(){}

    public Long getSuid() {
        return suid;
    }

    public void setSuid(Long suid) {
        this.suid = suid;
    }

    public String getUname() {
        return this.uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getUphone() {
        return this.uphone;
    }

    public void setUphone(String uphone) {
        this.uphone = uphone;
    }
}
```



#### 添加数据

```java
public class CreateUserService implements Service {
    
    public Param callService(Param in) {
        Param out = new Param(Param.RESP);
        
        String uname = in.getBody().getString("@uname"); // 用户名
        String uphone = in.getBody().getString("@uphone"); // 手机号
        long suid = 10000002L;
        
        User user = new User();
        user.setSuid(suid);
        user.setUphone(uphone);
        user.setUname(uname);
        
        int count = JdbcUtil.save(s);
        if (result > 0) {
            out.getHead().setProperty("@errcode", "0000");
        } else {
            out.getHead().setProperty("@errcode", "1001");
        }
    }
    
}
```



#### 查询数据

```java
public class LoadUserService implements Service {
    
    public Param callService(Param in) {
        Param out = new Param(Param.RESP);
        String uphone = in.getBody().getString("@uphone"); // 手机号
        
        User user = new User();
        user.setUphone(uphone);
        try {
            user = (Map) JdbcUtil.query(user, new BeanHandler(User.class));
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
        }

        Param item = out.getBody().addParam("user");
        item.setProperty("@suid", user.getSuid("suid"));
        item.setProperty("@uname", user.getUname("uname"));
        item.setProperty("@uphone", user.getUphone("uphone"));
        
        return out;
    }
    
}
```



#### 更新数据

```java
public class updateUserService implements Service {
    
    public Param callService(Param in) {
        Param out = new Param(Param.RESP);
        long suid = in.getBody().getLong("@suid"); // 用户id
        String uphone = in.getBody().getString("@uphone"); // 手机号
        
        User user = new User();
        user.setSuid(suid);
        user.setUphone(uphone);
            
        int count = JdbcUtil.update(user, user.getSuid());
        if (result > 0) {
            out.getHead().setProperty("@errcode", "0000");
        } else {
            out.getHead().setProperty("@errcode", "1001");
        }
        
        return out;
    }
    
}
```



#### 删除数据

```java
public class deleteUserService implements Service {
    
    public Param callService(Param in) {
        Param out = new Param(Param.RESP);
        long suid = in.getBody().getLong("@suid"); // 用户id
            
        int count = JdbcUtil.delete(User.class, suid);
        if (result > 0) {
            out.getHead().setProperty("@errcode", "0000");
        } else {
            out.getHead().setProperty("@errcode", "1001");
        }
        
        return out;
    }
    
}
```



#### 复合主键更新数据

```java
@Table(name = "user_bank")
public class UserBank implements Serializable {
    
    @Column
    private Long suid;
    @Column
    private String bankcode;
    @Column
    private String bankname;
    
    public void setSuid(long suid){
        this.suid = suid;
    }
    
    public Long getSuid(){
        return this.suid;
    }
    
    public void setBankcode(String bankcode) {
        this.bankcode = bankcode;
    }
    
    public String getBankcode() {
        return this.bankcode;
    }
    
    public void setBankname(String bankname) {
        this.bankname = bankname;
    }
    
    public String getBankname() {
        return this.bankname;
    }
}

public class UserBankCompositeKey implements CompositeKey {
    
    private Long suid;
    private String bankcode;
    
    public void setSuid(long suid){
        this.suid = suid;
    }
    
    public Long getSuid(){
        return this.suid;
    }
    
    public void setBankcode(String bankcode) {
        this.bankcode = bankcode;
    }
    
    public String getBankcode() {
        return this.bankcode;
    }
}

public class updateUserBankService implements Service {
    
    public Param callService(Param in) {
        Param out = new Param(Param.RESP);
        long suid = in.getBody().getLong("@suid"); // 用户id
        String bankcode = in.getBody().getString("@bankcode"); // 银行卡号
        String bankname = in.getBody().getString("@bankname"); // 银行卡名称
        
        UserBank bank = new UserBank();
        bank.setBankname(bankname);
        
        UserBankCompositeKey bankCompositeKey = new UserBankCompositeKey();
        bankCompositeKey.setSuid(suid);
        bankCompositeKey.setBankcode(bankcode);
            
        int count = JdbcUtil.update(bank, bankCompositeKey);
        if (result > 0) {
            out.getHead().setProperty("@errcode", "0000");
        } else {
            out.getHead().setProperty("@errcode", "1001");
        }
        
        return out;
    }
    
}
```



