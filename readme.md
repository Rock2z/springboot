我做的是一个用Springboot框架搭建的符合RESTful风格的web应用项目, 主要是实现了CRUD功能和分页功能;

持久层使用的是springboot jpa 基于hibernate来操作mysql数据库
控制层使用的是Springboot的Controller, 本质上还是基于SpringMVC的
视图层使用的Thymeleaf 这也是Springboot官方推荐的前端框架, 同时为了美观我使用bootstrap套了一下

最终的界面大概是这样的:
![result](https://img-blog.csdnimg.cn/20190405045853732.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzMzOTgyMjMy,size_16,color_FFFFFF,t_70)
然后说一下我写这个应用的顺序, 首先从持久层写起, 把实体类创建好,   因为我使用的是jpa(hibernate), 所以要写好DAO类, 然后定义好Service类接口并且写好Impl的实现类, 然后想好前后端交互的时候用什么的样的url, 然后写好前端界面之后实现对应的后端控制器;

我并没有写一个类的时候就完全写完整, 比如我的DAO类是继承自JPARepository, 然后Service层中应该要包装有需要用到的方法, 但是我也不知道我以后会需要什么方法, 所以我在Service接口中并没有预先写好要用的方法, 而是先空着, 等到Controller需要用到对应的Service的时候再去写好所需要的Service类;


从头开始吧, 在idea中使用Spring Initializr新建一个Springboot项目:
![maven](https://img-blog.csdnimg.cn/20190405050728251.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzMzOTgyMjMy,size_16,color_FFFFFF,t_70)
然后可以得到一个idea自动生成的文件结构, 可以按照我这里的文件结构新建好每一个层的包;
![在这里插入图片描述](https://img-blog.csdnimg.cn/20190405050446542.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzMzOTgyMjMy,size_16,color_FFFFFF,t_70)
## 一.Springboot相应配置
应该也不用多说, 大家看一看名字就知道这些配置是什么意思了吧...
自己写的时候要把这里的数据库的数据库名和用户名密码改成自己的
```xml
spring.thymeleaf.mode=HTML5
spring.thymeleaf.encoding=UTF-8
spring.thymeleaf.servlet.content-type=text/html
spring.thymeleaf.cache=false
server.servlet.context-path=/thymeleaf

spring.datasource.url=jdbc:mysql://127.0.0.1:3306/demo?characterEncoding=UTF-8&serverTimezone=UTC
spring.datasource.username=yourusername
spring.datasource.password=yourpassword
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.properties.hibernate.dialect = org.hibernate.dialect.MySQL57Dialect
spring.jpa.show-sql=true
```
然后这里最好导入一下所需要的包, 在pom层中做相应的导入即可, 我把我的pom代码也附在博客最后, 需要的可以直接复制过去, 然后这里记得要在idea中enable auto import功能;

大概就是需要导入Springboot-web的包, thymeleaf的包, springboot-jpa的包, mysql-connector的包;

## 二.实体类
首先写好实体类的Category:
这里用到了一些JPA的注解, 这里稍作记录:
```@Entity```表示这是一个实体类
```@Table(name = "category_")```表示这个实体类对应的数据库中的表格, 表示对应的表名, 如果表名和实体类相同, 这个注解不写也没关系
```@Id```表示这个是主键ID
```@GeneratedValue(strategy = GenerationType.IDENTITY)```表示这个主键由数据库的```autoincrement```自动生成, 这里还有一些其他的strategy, 可以自己查一下
```@Column(name = "id")```这里的两个Column是表示在表中对应的列名
```java
package com.recluse.spider.demo.pojo;

import javax.persistence.*;

@Entity
@Table(name = "category_")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "name")
    private String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

```

在这里最好能先把数据库所需要的相应配置完成, 配置好相应的表格和一些测试用的数据, 我把需要的sql文件附在博客最后面, 需要的可以下载然后导入mysql;

## 三.DAO层
要实现CategoryDAO层非常简单, 因为我这里不需要使用比较复杂的sql操作, 所以用JPA自带的简单方法就够了, 这里我使用了```@Repository```注解来标记这是一个DAO层类, 代码如下:
```java
package com.recluse.spider.demo.dao;

import com.recluse.spider.demo.pojo.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryDAO extends JpaRepository<Category, Integer> {

}

```
如果有需要什么复杂的查询, 这里可以使用JPA的命名方法会自动生成对应的sql语句, 然后就可以使用啦~

### 四.Service层
其实既然我们在DAO层没做什么复杂操作, 那么在Service层也不太需要做什么封装, 但是出于规范考虑我还是分了一下Service层, 接口代码如下:
```java
package com.recluse.spider.demo.Service;

import com.recluse.spider.demo.pojo.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface CategoryService {
    Page<Category> list(Pageable pageable);
    void add(Category category);
    void delete(int id);
    Category get(int id);
    void update(Category category);
}

```
ServiceImpl实现类代码如下:
```java
package com.recluse.spider.demo.Service.Impl;

import com.recluse.spider.demo.Service.CategoryService;
import com.recluse.spider.demo.dao.CategoryDAO;
import com.recluse.spider.demo.pojo.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    CategoryDAO categoryDAO;

    @Override
    public Page<Category> list(Pageable pageable) {
        return categoryDAO.findAll(pageable);
    }

    @Override
    public void add(Category category) {
        categoryDAO.save(category);
    }

    @Override
    public void delete(int id) {
        categoryDAO.deleteById(id);
    }

    @Override
    public Category get(int id) {
        return categoryDAO.getOne(id);
    }

    @Override
    public void update(Category category) {
        categoryDAO.save(category);
    }
}

```

这里有一点, 首先我使用了```@Service```来标记这个类是Service层的内容, 这里我使用```@Autowired```来自动绑定CategoryDAO类用于实现Service层的对应功能;
可以看到这里的Service层其实本质上就是和DAO层差不多;

### 五.View层(Thymeleaf)
这里主要是```listCategory.html```的文件, 这个文件可以直接放在Templates文件夹下;

这里要在开头的时候使用```<html xmlns:th="http://www.thymeleaf.org">```来用th标记使用thymeleaf的标记;

然后我导入了jquery和bootstrap的需要的库;

这里我设计时想的是, 对于一个listCategory页面, 我会传过来一个page对象, 然后依此做对应的分页, 查询操作;

首先用```th:each```来实现对传过来的```page.content```这个列表的遍历;
然后对应地取出id和name显示出来, 在edit和delete的url我们使用的restful风格, 也就是下面这样的:

比如我这里要对Category做增删改查, 就应该符合:
>##### Add -> /Category -> POST
>##### Delete -> /Category/id -> DELETE
>##### Update -> /Category -> PUT
>##### Get -> /Category/id -> GET
>##### List -> /Category -> GET
>在这里的PUT中, 我看到过两种设计, 有的是直接对/Category进行PUT, 也有对/Category/id进行PUT的操作, 我在这里选了前者, 因为我觉得似乎后者多传一个id到服务器并没有什么意义...

然后注意这里的thymeleaf中的标准url的写法:
```html
<a class="delete" th:href="@{/Category/{id}(id=${c.id})}">Delete</a>
```
这里使用了占位符, 而不能直接
```html
<a th:href="@{/Category/${c.id}}">Delete</a>
```
因为这样thymeleaf会误以为${}这些字符也是url的一部分;

然后这里我实现分页也是用了类似的标准url的写法:
```html
<a class="page-link" th:href="@{/Category(start=${page.number}-1)}">Previous</a>
```

这里要注意, 实现DELETE方法, 要用的METHOD是DELETE, 而不是直接POST;
这里使用了一个隐藏的form来实现这个功能:
```html
    <form id="deleteForm" method="post">
        <input type="hidden" name="_method" value="DELETE" />
    </form>
```
同时要配合js来完成:
```javascript
            $(".delete").click(function(){
                var href=$(this).attr("href");
                $("#deleteForm").attr("action",href).submit();
                return false;
            });
```
这里会自动识别到form中有一个name为_method的内容, 其值为DELETE, 那么就会自动使用DELETE请求发往服务器了;

这里还有另外一段JQuery代码
```javascript
            var isFirst = [[${page.isFirst()}]];
            var isLast = [[${page.isLast()}]];

            if(isFirst) $("#previous").addClass("disabled");
            else $("#previous").removeClass("disabled");

            if(isLast) $("#next").addClass("disabled");
            else $("#next").removeClass("disabled");
```
是为了实现, 当page为第一页的时候, previous按钮为不可用状态, 当page为最后一页时, next按钮为不可用状态,以避免一些未处理的错误

代码如下:
```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<html lang="en">
<head>
    <title>hello</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <script src="https://cdn.bootcss.com/jquery/3.3.1/jquery.min.js"></script>
    <script src="https://cdn.bootcss.com/twitter-bootstrap/4.2.1/js/bootstrap.min.js"></script>
    <link href="https://cdn.bootcss.com/twitter-bootstrap/4.2.1/css/bootstrap.min.css" rel="stylesheet">

    <script th:inline="javascript">
        $(function(){
            $(".delete").click(function(){
                var href=$(this).attr("href");
                $("#deleteForm").attr("action",href).submit();
                return false;
            });

            var isFirst = [[${page.isFirst()}]];
            var isLast = [[${page.isLast()}]];

            if(isFirst) $("#previous").addClass("disabled");
            else $("#previous").removeClass("disabled");

            if(isLast) $("#next").addClass("disabled");
            else $("#next").removeClass("disabled");
        })
    </script>

</head>
<body>

    <br/><br/>
    <h3 style="text-align:center;">Category List</h3>
    <br/>
    <table class="table table-striped table-bordered table-hover">
        <thead>
        <th>id</th>
        <th>name</th>
        <th>Edit</th>
        <th>Delete</th>
        </thead>
        <tbody>
        <tr th:each="c:${page.content}">
            <td th:text="${c.id}"></td>
            <td th:text="${c.name}"></td>
            <td><a th:href="@{/Category/{id}(id=${c.id})}">Edit</a></td>
            <td><a class="delete" th:href="@{/Category/{id}(id=${c.id})}">Delete</a></td>
        </tr>

        </tbody>
    </table>

    <div class="container col-2">
        <ul class="pagination">
            <li class="page-item"><a class="page-link" th:href="@{/Category(start=0)}">First</a></li>
            <li class="page-item" id="previous"><a class="page-link" th:href="@{/Category(start=${page.number}-1)}">Previous</a></li>
            <li class="page-item" id="next"><a class="page-link" th:href="@{/Category(start=${page.number}+1)}">Next</a></li>
            <li class="page-item"><a class="page-link" th:href="@{/Category(start=${page.totalPages}-1)}">Last</a></li>
        </ul>
    </div>

    <form action="Category" method="post" style="text-align:center;">
        <div class="input-group mb-3 col-4 mx-auto">
            <div class="input-group-prepend">
                <span class="input-group-text">NAME</span>
            </div>
            <input type="text" class="form-control" name="name">
        <button type="submit" class="btn btn-primary">Submit</button>
        </div>
    </form>

    <form id="deleteForm" method="post">
        <input type="hidden" name="_method" value="DELETE" />
    </form>

</body>
</html>
```

同时这里还需要一个```editCategory.html```的页面来使用对更新操作的支持, 代码如下:

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<html lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <script src="https://cdn.bootcss.com/jquery/3.3.1/jquery.min.js"></script>
    <script src="https://cdn.bootcss.com/twitter-bootstrap/4.2.1/js/bootstrap.min.js"></script>
    <link href="https://cdn.bootcss.com/twitter-bootstrap/4.2.1/css/bootstrap.min.css" rel="stylesheet">
    <title>Title</title>
</head>
<body>


<form action="../Category" method="post" class="mx-auto">
    <input type="hidden" name="_method" value="PUT"/>
    <div class="input-group mb-3 col-4 mx-auto">
        <div class="input-group-prepend">
            <span class="input-group-text">NAME</span>
        </div>
        <input type="text" class="form-control" name="name" th:value="${c.name}">
        <input th:value="${c.id}" type="hidden" name="id">
        <button type="submit" class="btn btn-primary">Submit</button>
    </div>
</form>

</body>
</html>
```

### 六.Controller层
这里我使用了一个CategoryController类
```@Controller```表示这是一个Controller
```@Autowired```自动绑定对应的CategoryService
```@GetMapping```表示对url的绑定, 而且限定必须是get操作, 同样还有post, delete, put也是类似的意思;
```@RequestParam(value = "start", defaultValue = "0")```这个是在listCategory中的方法对参数的注解, 表示接受的值是start, 如果没有传来start那么默认的value是0, 那么对于size也是类似的意思;
```@PathVariable("id")```在DELETE操作中传来了一个参数为id, 传入用于表示删除的id;

代码如下:
```java
package com.recluse.spider.demo.controller;

import com.recluse.spider.demo.Service.CategoryService;
import com.recluse.spider.demo.pojo.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class CategoryController {
    @Autowired
    CategoryService categoryService;

    @GetMapping("/Category")
    public String listCategory(Model model, @RequestParam(value = "start", defaultValue = "0")int start,
                               @RequestParam(value = "size", defaultValue = "5")int size){
        start = start<0?0:start;
        Pageable pageable = PageRequest.of(start, size, new Sort(Sort.Direction.ASC, "id"));
        Page<Category> page = categoryService.list(pageable);
        model.addAttribute("page", page);
        return "listCategory";
    }

    @PostMapping("/Category")
    public String addCategory(Category category){
        categoryService.add(category);
        return "redirect:/Category";
    }

    @DeleteMapping("/Category/{id}")
    public String deleteCategory(@PathVariable("id")int id){
        categoryService.delete(id);
        return "redirect:/Category";
    }

    @GetMapping("/Category/{id}")
    public String editCategory(Model model,@PathVariable("id")int id){
        model.addAttribute("c", categoryService.get(id));
        return "editCategory";
    }

    @PutMapping("/Category")
    public String updateCategory(Category category){
        categoryService.update(category);
        return "redirect:/Category";
    }

}

```

### 七.附录 sql&pom.xml
到这里代码就结束了, 这里附上所需导入的sql文件和项目依赖的pom.xml

sql文件太长了, 这里就放简略版的结构+少量数据了:
```sql
/*
 Navicat Premium Data Transfer

 Source Server         : Mysql Local DB
 Source Server Type    : MySQL
 Source Server Version : 50724
 Source Host           : localhost:3306
 Source Schema         : demo

 Target Server Type    : MySQL
 Target Server Version : 50724
 File Encoding         : 65001

 Date: 04/04/2019 23:59:09
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for category_
-- ----------------------------
DROP TABLE IF EXISTS `category_`;
CREATE TABLE `category_` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=982 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of category_
-- ----------------------------
BEGIN;
INSERT INTO `category_` VALUES (22, 'category66');
INSERT INTO `category_` VALUES (23, 'category67');
INSERT INTO `category_` VALUES (24, 'category68');
INSERT INTO `category_` VALUES (25, 'category69');
INSERT INTO `category_` VALUES (26, 'category70');
INSERT INTO `category_` VALUES (27, 'category71');
INSERT INTO `category_` VALUES (28, 'category72');
INSERT INTO `category_` VALUES (29, 'category73');
INSERT INTO `category_` VALUES (30, 'category74');
INSERT INTO `category_` VALUES (31, 'category75');
INSERT INTO `category_` VALUES (32, 'category76');
INSERT INTO `category_` VALUES (33, 'category77');
INSERT INTO `category_` VALUES (34, 'category78');
INSERT INTO `category_` VALUES (35, 'category79');
INSERT INTO `category_` VALUES (36, 'category80');
INSERT INTO `category_` VALUES (37, 'category81');
INSERT INTO `category_` VALUES (38, 'category82');
INSERT INTO `category_` VALUES (39, 'category83');
INSERT INTO `category_` VALUES (40, 'category84');
INSERT INTO `category_` VALUES (41, 'category85');
INSERT INTO `category_` VALUES (42, 'category86');
INSERT INTO `category_` VALUES (43, 'category87');
INSERT INTO `category_` VALUES (44, 'category88');
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;

```

然后附上pom.xml
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.1.3.RELEASE</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>
    <groupId>com.recluse.spider</groupId>
    <artifactId>demo</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>demo</name>
    <description>Demo project for Spring Boot</description>

    <properties>
        <java.version>1.8</java.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <!-- https://mvnrepository.com/artifact/mysql/mysql-connector-java -->
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>8.0.15</version>
        </dependency>

        <!-- https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-data-jpa -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
            <version>2.1.3.RELEASE</version>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-thymeleaf</artifactId>
        </dependency>


        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>

</project>

```
