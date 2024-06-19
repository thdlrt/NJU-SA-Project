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
      atOnceUsers(1000),           // 立即注入1000个用户
    ).protocols(httpProtocol)
  )
}
