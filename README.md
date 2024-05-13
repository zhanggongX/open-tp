## 项目介绍

### 项目架构图

![项目架构图](https://opentp.cn/assets/architecture-D4huX2s8.png)

### 代码目录

![代码目录](https://opentp.cn/assets/code-u7hA9CnT.png)


### 详细介绍
#### 整体
opentp 分为服务端和客户端。
#### 客户端
1. 客户端提供了 opentp-client-spring-boot-starter 供 Springboot 项目引用，如果是其他 Java 项目可以直接引用 opentp-client 项目
2. 客户端代码非常简单，只是启动了一个线程监控线程池，然后收集线程池的状态，进行上报到服务端，如果服务端异常或者停机等，客户端将自动停报，并隔一段时间尝试重连服务端，对整个业务 0 影响。
3. 客户端需配置 appId 和 appKey， 可以通过 opentp 服务端申请。
#### 服务端
1. 服务端可集群部署可单机部署
2. 服务端提供了基于 netty 实现的 rest 接口，供查询线程池状态和进行线程池参数修改。
3. Opentp server 是基于 AP 架构的分布式服务器集群，部分节点故障，不会影响整个集群的服务，客户端会自动转移到其他可用服务节点。
4. 基于原生的 Gossip 协议来保证整个集群最终一致性，无需依赖任何第三方中间件。
5. 服务器提供报文上报权限控制，首次链接需要携带 appId 和 appKey 进行权限验证，通过后会返回 licenseKey，licenseKey 有一定的有效时间，客户端会定期申请 licenseKey，业务使用方无感知。
6. 后续客户端上报线程池信息需要携带 licenseKey。
7. 服务端提供用户权限控制，用户分为管理员和普通用户，管理员只是用来管理用户，普通用户仅可看到自己负责的 appId 的线程池信息。
8. 服务端用户可以同时拥有管理员和普通用户权限（即管理员也是普通用户）。

### 运行环境
目前服务端和客户端都是基于 JDK17 进行开发。  
后续会支持 JDK8 JDK11 JDK21。

opentp-client-spring-boot-starter 项目是基于 Springboot3 进行开发。  
后续支持 Spingboot2。

### 项目部署
#### 服务端
```
java -jar opentp-server.jar 
```

#### 客户端
```java
// 增加依赖
<dependency>
    <groupId>cn.opentp</groupId>
    <artifactId>opentp-client-spring-boot-starter</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>

// 添加 @EnableOpentp 注解。
@EnableOpentp
@SpringBootApplication
public class ClientSpringExampleApp {
    public static void main(String[] args) {

        SpringApplication.run(ClientSpringExampleApp.class, args);
    }
}

// 线程池增加 @Opentp 注解。
@Opentp("demoExecutor")
@Bean
public ThreadPoolExecutor threadPoolExecutor() {
    return new ThreadPoolExecutor(10, 200, 100, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1024));
}

// 就会自动收集线程池信息进行上报
```