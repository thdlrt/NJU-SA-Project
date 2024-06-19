> 本文为第9次实验报告，课程论文位于report文件夹内，为pdf格式

## 运行与API

- 项目直接通过idea依次启动全部6个application即可
  - <img src="https://thdlrt.oss-cn-beijing.aliyuncs.com/image-20240605154821633.png" alt="image-20240605154821633" style="zoom:33%;" />
- 测试为一个Gatling的scala文件，需要放到`user-files/simulations`下运行
- 其他测试使用postman直接对接口功能进行测试

### API接口

#### 产品接口

- **获取所有产品**
  **URL:** `/Product`
  **方法:** `GET`
  返回所有产品列表。
- **根据ID获取产品**
  **URL:** `/Product/{productId}`
  **方法:** `GET`
  根据产品ID返回产品详细信息。
- **根据名称搜索产品**
  **URL:** `/Product/search/{name}`
  **方法:** `GET`
  根据产品名称搜索产品列表。

#### 订单接口

- **添加产品到订单**
  **URL:** `/Order/add/{productId}`
  **方法:** `POST`
  将指定产品添加到订单中。
- **从订单中删除产品**
  **URL:** `/Order/delete/{productId}`
  **方法:** `DELETE`
  从订单中删除指定产品。
- **结算订单**
  **URL:** `/Order/checkout`
  **方法:** `POST`
  结算当前订单。
- **根据ID获取订单**
  **URL:** `/Order/order/{orderId}`
  **方法:** `GET`
  根据订单ID返回订单详细信息。
- **获取当前购物车**
  **URL:** `/Order/cart`
  **方法:** `GET`
  返回当前购物车中的所有商品。

#### 配送接口

- **获取所有订单**
  **URL:** `/Delivery`
  **方法:** `GET`
  返回所有需要配送的订单列表。

## 架构设计

- 整体架构设计：
  - ![image-20240605161136782](https://thdlrt.oss-cn-beijing.aliyuncs.com/image-20240605161136782.png)

- **API Gateway** API Gateway模块作为系统的入口，负责路由客户端请求到相应的服务。
- **Eureka Server（discover）** Eureka Server模块提供服务发现功能。所有微服务在启动时都会向Eureka Server注册，客户端通过Eureka Server获取服务的实例信息，从而实现服务间的动态负载均衡和故障转移。
- **Model Service** Model Service模块主要负责数据库管理，其他微服务模块通过HTTP调用该模块获取数据。它提供基础的数据存储和查询功能(通过h2进行数据存储)，是其他业务逻辑模块的数据支撑。
- **Order Service** Order Service模块处理与订单相关的业务逻辑。它包括订单的创建、修改、删除以及查询功能。同时，该模块通过消息队列(RabbitMQ)向Delivery Service发送订单创建通知。
- **Product Service** Product Service模块处理与产品相关的业务逻辑。它包括产品的查询、添加、更新以及搜索功能。该模块通过与Model Service交互来管理产品数据。
- **Delivery Service** Delivery Service模块处理与订单配送相关的业务逻辑。它接收Order Service发送的订单创建通知，并更新配送状态。

### webflux迁移

- 本次实验要求从原先的`springMVC`迁移到新的`springWebFlux`响应式框架，主要对`product-server`和`order-server`两个模块进行了修改

#### mvc与webflux

- **Spring MVC**: 基于线程阻塞的Servlet API，传统的同步处理模型。

- **Spring WebFlux**: 基于响应式流和非阻塞I/O，采用Reactor库，支持更高的并发处理能力。
- webflux的核心特点
  - **Flux**: 表示0到N个元素的异步序列，支持背压。
  - **Mono**: 表示0到1个元素的异步序列。
  - 非阻塞、异步处理，资源利用率更高。
  - 支持函数式编程风格，提供丰富的操作符进行流的转换和处理。
  - 更好的处理高并发和I/O密集型任务。

#### 对模块进行改造

- 以product模块部分功能作为示例说明改造过程

**依赖修改**

- 添加`spring-boot-starter-webflux`依赖，替换原有的`spring-boot-starter-web`依赖。

**控制器修改**

- 将控制器方法返回类型从`ResponseEntity`修改为`Mono`或`Flux`，以实现非阻塞式响应。

```java

@GetMapping
@CrossOrigin(value = "*", maxAge = 1800, allowedHeaders = "*")
public Flux<Product> listProducts() {
    return productService.getAllProducts();
}

@GetMapping("/{productId}")
@CrossOrigin(value = "*", maxAge = 1800, allowedHeaders = "*")
public Mono<ResponseEntity<Product>> showProductById(@PathVariable Long productId) {
    return productService.getProductById(productId)
        .map(ResponseEntity::ok)
        .defaultIfEmpty(ResponseEntity.notFound().build());
}

@GetMapping("/search/{name}")
@CrossOrigin(value = "*", maxAge = 1800, allowedHeaders = "*")
public Flux<Product> searchProductByName(@PathVariable String name) {
    return productService.searchProductByName(name);
}
```

**服务层修改**

- 修改服务层方法返回类型为`Mono`或`Flux`，以支持响应式编程模型。

```java
@Override
public Flux<Product> getAllProducts() {
    return productDB.getProducts();
}

@Override
public Mono<Product> getProductById(Long productId) {
    return productDB.getProduct(productId)
        .switchIfEmpty(Mono.error(new HttpClientErrorException(HttpStatusCode.valueOf(404))));
}

@Override
public Flux<Product> searchProductByName(String name) {
    return productDB.searchProductByName(name);
}
```

**数据层修改**

- 使用`WebClient`进行非阻塞式的HTTP调用，并返回`Mono`或`Flux`对象。

```java
@Override
public Flux<Product> getProducts() {
    return productClient.getProducts();
}

@Override
public Mono<Product> getProduct(Long productId) {
    return productClient.getProductById(productId);
}

@Override
public Flux<Product> searchProductByName(String name) {
    return productClient.searchProductByName(name);
}
```

### 商品数据集

- 使用亚马逊数据集，存储在h2数据库
  - <img src="https://thdlrt.oss-cn-beijing.aliyuncs.com/image-20240605162458464.png" alt="image-20240605162458464" style="zoom:50%;" />

## 功能演示

- 使用postman对功能进行验证

### Product

- 获取产品列表
  - <img src="https://thdlrt.oss-cn-beijing.aliyuncs.com/image-20240605162559437.png" alt="image-20240605162559437" style="zoom:33%;" />
- 查看单个产品的具体信息
  - <img src="https://thdlrt.oss-cn-beijing.aliyuncs.com/image-20240605162612506.png" alt="image-20240605162612506" style="zoom:33%;" />

- 搜索产品
  - <img src="https://thdlrt.oss-cn-beijing.aliyuncs.com/image-20240605162648525.png" alt="image-20240605162648525" style="zoom: 33%;" />

### Order/Cart

- 想购物车内添加商品&增加数目
  - <img src="https://thdlrt.oss-cn-beijing.aliyuncs.com/image-20240605162728191.png" alt="image-20240605162728191" style="zoom:33%;" />
- 获取购物车内的商品状况
  - <img src="https://thdlrt.oss-cn-beijing.aliyuncs.com/image-20240605162807793.png" alt="image-20240605162807793" style="zoom:33%;" />
- 结账checkout
  - <img src="https://thdlrt.oss-cn-beijing.aliyuncs.com/image-20240605162842224.png" alt="image-20240605162842224" style="zoom: 33%;" />
- 查看订单信息
  - <img src="https://thdlrt.oss-cn-beijing.aliyuncs.com/image-20240605162920540.png" alt="image-20240605162920540" style="zoom:33%;" />

### Delivery

- 查看运单信息（通过RabbitMQ传输到delivery模块）

  - （前面的数据是之前跑压力测试生成的）最后一条问本次的运单信息，已将通过消息队列实现了模块之间的传输

  - <img src="https://thdlrt.oss-cn-beijing.aliyuncs.com/image-20240605163023903.png" alt="image-20240605163023903" style="zoom:33%;" />

## 测试&验证

- 验证目标
  - **响应性**：系统应能及时响应用户请求。
  - **弹性**：系统应能快速从故障中恢复。
  - **可扩展性**：系统应能够轻松扩展以应对不同的负载。
  - **消息驱动**：系统应使用异步消息传递进行松耦合组件之间的通信。

### 响应性

- 我使用Spring WebFlux进行非阻塞、反应式编程，通过高效利用资源来增强系统的响应性。
- 下面通过Gatling进行测试
  - 压力测试全部三个模块

```scala
package gatlingtest

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class GatlingTestSimulation extends Simulation {

    val httpProtocol = http
    .baseUrl("http://localhost:8080")
    .acceptHeader("application/json")
    .acceptLanguageHeader("en-US,en;q=0.5")
    .acceptEncodingHeader("gzip, deflate")
    .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:16.0) Gecko/20100101 Firefox/16.0")

    val scn = scenario("Testing Product and Order Endpoints")
    // 测试获取所有产品
    .exec(
        http("Get all products")
        .get("/Product")
        .check(status.is(200))
    )
    .pause(2)
    // 测试获取ID为125的产品
    .exec(
        http("Get product by ID 125")
        .get("/Product/125")
        .check(status.is(200))
    )
    .pause(2)
    // 测试获取ID为128的产品
    .exec(
        http("Get product by ID 128")
        .get("/Product/128")
        .check(status.is(200))
    )
    .pause(2)
    // 测试添加产品到订单
    .exec(
        http("Add product to order")
        .get("/Order/add/125")
        .check(status.is(200))
    )
    .pause(2)
    // 测试结账
    .exec(
        http("Checkout order")
        .get("/Order/checkout")
        .check(status.is(200))
    )
    .pause(2)
    // 测试获取购物车
    .exec(
        http("Get cart")
        .get("/Order/cart")
        .check(status.is(200))
    )
    .pause(2)
    // 测试获取所有订单
    .exec(
        http("Get all deliveries")
        .get("/Delivery")
        .check(status.is(200))
    )

    setUp(
        scn.inject(
            atOnceUsers(7000),
        ).protocols(httpProtocol)
    )
}

```

- 下图可知在较大压力下，系统仍然保持了较好的响应性
  - <img src="https://thdlrt.oss-cn-beijing.aliyuncs.com/image-20240605153841791.png" alt="image-20240605153841791" style="zoom:33%;" />

### 弹性

- 本系统应具有弹性，能够快速从故障中恢复并保持可用性。
- 使用Resilience4j实现断路器，防止故障蔓延并允许系统快速从错误中恢复。
- 如product-server中

```java
@Bean
public Customizer<ReactiveResilience4JCircuitBreakerFactory> defaultCustomizer() {
    return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
                                               .circuitBreakerConfig(CircuitBreakerConfig.ofDefaults())
                                               .timeLimiterConfig(TimeLimiterConfig.custom().timeoutDuration(Duration.ofSeconds(4)).build())
                                               .build());
}
```

### 可扩展性

- 系统设计为一组微服务，每个微服务在其自身的进程中运行，并通过轻量级机制（如HTTP和RabbitMQ消息队列）进行通信。
- 使用Eureka进行服务发现，允许服务实例动态注册和发现，便于水平扩展。
- 本系统具有良好的可扩展性，order、product、delivery都可以分别增加进行水平扩展，获得更好的相应

- 分别增加为2个，进行同样的测试
- 由测试结果可见有一定提升
  - <img src="https://thdlrt.oss-cn-beijing.aliyuncs.com/image-20240605165037479.png" alt="image-20240605165037479" style="zoom:33%;" />

### 消息驱动

- **RabbitMQ**：不同模块的系统（order和delivery之间）使用RabbitMQ进行服务之间的消息驱动通信。这允许服务异步通信，并保持松耦合。

- 在producthe order的不同层级之间使用WebFlux提供的反应式编程模型自然支持异步消息处理。
- 不同模块之间的消息传递等在上面功能演示部分已经完成了测试
