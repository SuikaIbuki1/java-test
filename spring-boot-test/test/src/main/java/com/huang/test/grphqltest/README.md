## Graphql 项目说明

该项目构建了一个基于 GraphQL 的 API 服务，使用了 Apollo Server 作为服务端框架。该项
目旨在提供一个灵活的查询语言来获取数据， 并通过 GraphQL 查询语言来定义数据需求，进行 graphql 
简单应用与测试。

### springboot + graphql 整合
1. 首先在 /resourcs/graphql 目录下创建了 schema.graphqls 文件，用于定义 GraphQL 的模式（Schema）。
   ```
   type Query {
       bookById(id: ID): Book
   }
   
   type Book {
       id: ID
       name: String
       pageCount: Int
       author: Author
   }
   
   type Author {
       id: ID
       firstName: String
       lastName: String
   }
   ```
   
2. 引入相关依赖，这里引入一下依赖
    ```xml
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-graphql</artifactId>
        </dependency>
   ``` 
3. 创建 book 与 author 实体类，创建 BookController 控制器 

   // book.java
   ```java
   @Data
   @AllArgsConstructor
   @NoArgsConstructor
   public class Book {
       private String id;
       private String name;
       private int pageCount;
       private String authorId;
       private static List<Book> books = Arrays.asList(
               new Book("1", "Java编程思想", 10, "1"),
               new Book("2", "Effective Java", 12, "2")
       );
       public static Book getById(String id) {
           return books.stream().filter(book -> book.getId().equals(id)).findFirst().orElse(null);
       }
   }
   ```
   // author.java
   
   ```java
   @Data
   @AllArgsConstructor
   @NoArgsConstructor
   public class Author {
       private String id;
       private String firstName;
       private String lastName;
       private static List<Author> authors = Arrays.asList(
           new Author("1", "John", "Doe"),
           new Author("2", "Jane", "Doe")
       );
       public static Author getById(String id) {
           return authors.stream().filter(author -> author.getId().equals(id)).findFirst().orElse(null);
       }
   }
   ```
   // BookController.java
   ```java
   @RestController
   @RestController
   public class BookController {
   
       @QueryMapping("bookById")
       public Book bookById(@Argument String id) {
           return Book.getById(id);
       }
   
       @SchemaMapping
       public Author author(Book book){
           return Author.getById(book.getAuthorId());
       }
   }
   ```

5. 访问[本地 graphql 主页](http://localhost:8080/graphiql) 测试，测试参数为：
    ```
    query bookDetails {
      bookById(id: "2") {
        id
        name
        author {
          id
          firstName
        }
      }
    }
    ```

### 分析
测试参数与实际参数不符，如 请求 book 比实际参数少 pageCount 字段，
author 比实际参数少 lastName 字段。可判断前端可以根据所需要的字段
进行查询，与 restful 比较，restful 返回所有 pojo 字段，而 graphql 
可以根据所需返回对应结果，具有约束受限特性。

同时注意到，Book 对象只设置 authorid 字段，而没有显式设置 author 对象。
但依然能查询到 author 对象，这是因为有 @SchemaMapping 注解方法。
graphql 中的 type 将 book 与 author 联系起来，通过 @SchemaMapping 
注解的方法，查询到相关的对象数据。

#### graphql 优点：
1. 更高效的数据获取。
   * GraphQL 允许客户端指派需要的数据结构字段。
2. 一次请求，多资源获取。
   * 客户端可以通过一次查询获取多个资源的数据，而无需多次请求。
3. 强类型和自描述性
   * 允许开发者定义精确的查询和变更接口。客户端可以在查询前了解到有哪些字段与
   相关的字段类型。
4. 版本控制和向后兼容
   * GraphQL 基于 schema。可以更容易演进。不会因为接口调整导致兼容性问题。
5. 易于调试、文档化
   * 有浏览器模式，可以查看相关 scheme 信息，可查看文档与测试Api，方便调试。

#### graphql 缺点：
1. 复杂度高
   * 相比于 REST，GraphQL 实现更加复杂。需要处理更复杂的查询解析、性能优化
   等问题。
2. 有数据泄露风险
   * 在没有适当的权限设置情况下，客户端可以请求任意字段，可能导致敏感数据泄露。
3. 缓存策略复杂
   * GraphQL 的查询结果依赖于客户端请求的字段，因此具有动态查询特性，导致缓存
   策略更加复杂。



### 调用
可以通过 http 请求调用，也可以通过 graphql 自带工具 graphiql 进行调用。
示例文件在 /request.http 中。

也可用网页测试相关数据。

## 安全加密
graphql 支持 headers，可以在 headers 中添加 Authorization 进行安全验证。
首先继承 WebGraphQlInterceptor 接口，然后实现 intercept 方法，在方法中添加
认证处理逻辑。

如实例所示，对 Authorization 进行校验，校验通过后，继续执行后续逻辑。
```java
@Component
public class SecurityGraphQlStrategy implements WebGraphQlInterceptor {
    
    @Override
    public Mono<WebGraphQlResponse> intercept(WebGraphQlRequest request, Chain chain) {
        boolean regine = false;
        for (String s : request.getHeaders().keySet()) {
            List<String> v = request.getHeaders().get(s);
            if (v != null && s.equals("authorization") && v.get(0).equals("123")) {
                regine = true;
            }
        }
        if (!regine) {
            ExecutionResult executionResult = ExecutionResultImpl.newExecutionResult()
                    .data(null)
                    .errors(List.of(new GenericGraphQLError("token is null")))
                    .build();
            return Mono.error(new RuntimeException("token is null"));
        }
        return chain.next(request);
    }
}
```
在请求的 header 中添加 Authorization 为 123
```json
{"authorization":"123"}
```

## 异常处理
graphql 的异常处理与 restful 不同，restful 可以通过 http 状态码判断是否异常，
graphql 中则使用 error 字段显示异常信息。
```json
{
  "errors": [
    {
      "message": "INTERNAL_ERROR for e88b404d-279b-9bbe-1ea7-11b3f8f15481",
      "locations": [
        {
          "line": 2,
          "column": 3
        }
      ],
      "path": [
        "bookById"
      ],
      "extensions": {
        "classification": "INTERNAL_ERROR"
      }
    }
  ],
  "data": {
    "bookById": null
  }
}
```
因此与其他rpc框架的兼容性有待考察。
