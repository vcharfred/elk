# ELK

-----------------------

[![build status](https://img.shields.io/badge/build-elasticsearch-red)](https://www.elastic.co/cn/elasticsearch/service)
[![jdk](https://img.shields.io/badge/jdk-1.8-green)]()

## 概述

ELK是Elasticsearch、Logstash、Kibana的简称，这三者是核心套件实现日志采集、分析、展示，但并非全部。

Elasticsearch是实时全文搜索和分析引擎，提供搜集、分析、存储数据三大功能；是一套开放REST和JAVA API等结构提供高效搜索功能，可扩展的分布式系统。它构建于Apache Lucene搜索引擎库之上。

Logstash是一个用来搜集、分析、过滤日志的工具。它支持几乎任何类型的日志，包括系统日志、错误日志和自定义应用程序日志。它可以从许多来源接收日志，这些来源包括 syslog、消息传递（例如 RabbitMQ）和JMX，它能够以多种方式输出数据，包括电子邮件、websockets和Elasticsearch。

Kibana是一个基于Web的图形界面，用于搜索、分析和可视化存储在 Elasticsearch指标中的日志数据。它利用Elasticsearch的REST接口来检索数据，不仅允许用户创建他们自己的数据的定制仪表板视图，还允许他们以特殊的方式查询和过滤数据。

## 一、Elasticsearch基础

现在主流的搜索引擎大概就是：Lucene，Solr，ElasticSearch。这里是对ElasticSearch的学习。

### 1.1 Elasticsearch的功能
    
1. 分布式的搜索引擎和数据分析引擎
    
    搜索：百度，网站的站内搜索，IT系统的检索
    数据分析：电商网站，最近7天牙膏这种商品销量排名前10的商家有哪些；新闻网站，最近1个月访问量排名前3的新闻版块是哪些
    分布式，搜索，数据分析
    
2. 全文检索，结构化检索，数据分析
    
    全文检索：我想搜索商品名称包含牙膏的商品，select * from products where product_name like "%牙膏%"
    结构化检索：我想搜索商品分类为日化用品的商品都有哪些，select * from products where category_id='日化用品'
    部分匹配、自动完成、搜索纠错、搜索推荐
    数据分析：我们分析每一个商品分类下有多少个商品，select category_id,count(*) from products group by category_id
    
3. 对海量数据进行近实时的处理
    
    分布式：ES自动可以将海量数据分散到多台服务器上去存储和检索
    海联数据的处理：分布式以后，就可以采用大量的服务器去存储和检索数据，自然而然就可以实现海量数据的处理了
    近实时：检索个数据要花费1小时（这就不要近实时，离线批处理，batch-processing）；在秒级别对数据进行搜索和分析
    
    跟分布式/海量数据相反的：lucene，单机应用，只能在单台服务器上使用，最多只能处理单台服务器可以处理的数据量

### 1.2 Elasticsearch的适用场景

国外

 （1）维基百科，类似百度百科，牙膏，牙膏的维基百科，全文检索，高亮，搜索推荐
 
 （2）The Guardian（国外新闻网站），类似搜狐新闻，用户行为日志（点击，浏览，收藏，评论）+社交网络数据（对某某新闻的相关看法），数据分析，给到每篇新闻文章的作者，让他知道他的文章的公众反馈（好，坏，热门，垃圾，鄙视，崇拜）
 
 （3）Stack Overflow（国外的程序异常讨论论坛），IT问题，程序的报错，提交上去，有人会跟你讨论和回答，全文检索，搜索相关问题和答案，程序报错了，就会将报错信息粘贴到里面去，搜索有没有对应的答案
 
 （4）GitHub（开源代码管理），搜索上千亿行代码
 
 （5）电商网站，检索商品
 
 （6）日志数据分析，logstash采集日志，ES进行复杂的数据分析（ELK技术，elasticsearch+logstash+kibana）
 
 （7）商品价格监控网站，用户设定某商品的价格阈值，当低于该阈值的时候，发送通知消息给用户，比如说订阅牙膏的监控，如果高露洁牙膏的家庭套装低于50块钱，就通知我，我就去买
 
 （8）BI系统，商业智能，Business Intelligence。比如说有个大型商场集团，BI，分析一下某某区域最近3年的用户消费金额的趋势以及用户群体的组成构成，产出相关的数张报表，**区，最近3年，每年消费金额呈现100%的增长，而且用户群体85%是高级白领，开一个新商场。ES执行数据分析和挖掘，Kibana进行数据可视化

国内

    （9）国内：站内搜索（电商，招聘，门户，等等），IT系统搜索（OA，CRM，ERP，等等），数据分析（ES热门的一个使用场景）

### 1.3 elasticsearch的核心概念

    Elasticsearch			数据库
    
    -----------------------------------------
    
    Document			行
    Type				表（在7.x以后已经移除了，默认为_doc; 在6.x以后一个索引只能有一个type了，在5.x以前一个索引可以有多个type）
    Index				库

 （1）Near Realtime（NRT）：近实时，两个意思，从写入数据到数据可以被搜索到有一个小延迟（大概1秒）；基于es执行搜索和分析可以达到秒级

 （2）Cluster：集群，包含多个节点，每个节点属于哪个集群是通过一个配置（集群名称，默认是elasticsearch）来决定的，对于中小型应用来说，刚开始一个集群就一个节点很正常
 
 （3）Node：节点，集群中的一个节点，节点也有一个名称（默认是随机分配的），节点名称很重要（在执行运维管理操作的时候），默认节点会去加入一个名称为“elasticsearch”的集群，如果直接启动一堆节点，那么它们会自动组成一个elasticsearch集群，当然一个节点也可以组成一个elasticsearch集群

 （4）Document&field：文档，es中的最小数据单元，一个document可以是一条客户数据，一条商品分类数据，一条订单数据，通常用JSON数据结构表示，每个index下的type中，都可以去存储多个document。一个document里面有多个field，每个field就是一个数据字段。

 （5）Index：索引，包含一堆有相似结构的文档数据，比如可以有一个客户索引，商品分类索引，订单索引，索引有一个名称。一个index包含很多document，一个index就代表了一类类似的或者相同的document。比如说建立一个product index，商品索引，里面可能就存放了所有的商品数据，所有的商品document。
 
 （6）shard：单台机器无法存储大量数据，es可以将一个索引中的数据切分为多个shard，分布在多台服务器上存储。有了shard就可以横向扩展，存储更多数据，让搜索和分析等操作分布到多台服务器上去执行，提升吞吐量和性能。每个shard都是一个lucene index。
 
 （7）replica：任何一个服务器随时可能故障或宕机，此时shard可能就会丢失，因此可以为每个shard创建多个replica副本。replica可以在shard故障时提供备用服务，保证数据不丢失，多个replica还可以提升搜索操作的吞吐量和性能。primary shard（建立索引时一次设置，不能修改，默认5个），replica shard（随时修改数量，默认1个），默认每个索引10个shard，5个primary shard，5个replica shard，最小的高可用配置，是2台服务器。

### 1.4 使用docker安装Elasticsearch

1. 拉取docker镜像，由于国内网络原因，速度可能会比较慢或者无法下载；可以直接安装对应系统的安装包进行安装即可，基本都是解压运行即可。


	docker pull elasticsearch:7.8.0

> 镜像下载慢可以配置国内的加速

编辑编辑`/etc/docker/daemon.json`文件

    vi /etc/docker/daemon.json

添加镜像加速地址(下面这个是网易的加速地址)：

    {
      "registry-mirrors": ["http://hub-mirror.c.163.com"]
    }
> 也可以使用申请阿里云容器镜像服务ACR[https://www.aliyun.com/product/acr]；申请成功后点击管理控制台，选择镜像中心->镜像加速获取地址。    
    
重启docker

    systemctl daemon-reload
    systemctl restart docker
   
2. 创建elasticsearch容器，并启动(这里使用单机版)


    docker run -d --name es7  -p 9200:9200 -p 9300:9300 -e "discovery.type=single-node" elasticsearch:7.8.0

3. 访问 `http://192.168.1.6:9200/`

如果正常返回则说明成功，类似：

    {
      "name" : "074c8527cecd",
      "cluster_name" : "docker-cluster",
      "cluster_uuid" : "YBNpiQm8Qxmd0ma7j-1uGw",
      "version" : {
        "number" : "7.8.0",
        "build_flavor" : "default",
        "build_type" : "docker",
        "build_hash" : "757314695644ea9a1dc2fecd26d1a43856725e65",
        "build_date" : "2020-06-14T19:35:50.234439Z",
        "build_snapshot" : false,
        "lucene_version" : "8.5.1",
        "minimum_wire_compatibility_version" : "6.8.0",
        "minimum_index_compatibility_version" : "6.0.0-beta1"
      },
      "tagline" : "You Know, for Search"
    }

## 二、Kibana

kibana的界面可以很方便的查看elasticsearch的信息，也可以做图表、指标等。同时提供控制台命令操作elasticsearch。

### 使用docker安装kibana

    # 拉取kibana的镜像
    docker pull kibana:7.8.0
    # 启动kibana
    docker run -d --name kibana --link 已经启动的elasticsearch的容器ID或者是名字:elasticsearch -p 5601:5601 kibana:7.8.0  
    # 例如
    docker run -d --name kibana --link 074c8527cecd:elasticsearch -p 5601:5601 kibana:7.8.0

通过`http://192.168.111.44:5601`访问kibana

![](./image/kibana命令行.jpg)

### 通过kibana的Console来做elasticsearch的crud和相关配置

#### elasticsearch集群状态

    GET _cat/health?v

green：每个索引的primary shard和replica shard都是active状态的
yellow：每个索引的primary shard都是active状态的，但是部分replica shard不是active状态，处于不可用的状态
red：不是所有索引的primary shard都是active状态的，部分索引有数据丢失了

> 后面加v是为了打印出更多的信息

#### 索引相关操作

    # 查询所有索引
    GET _cat/indices?v
    # 创建索引
    PUT /索引名称?pretty
    # 删除索引
    DELETE /索引名称


#### 向elasticsearch中添加和修改数据;

语法, 使用POST或者PUT都可以，存在则更新否则创建；
> 区别在于没有加ID值时（没有ID会自动生成），只能用POST表示创建；
> 需要注意的是使用PUT做更新时，其实是直接覆盖，因此需要带上所有的数据；
  
    
    POST /索引名称/_doc
    POST /索引名称/_create
    
    POST /索引名称/_doc/数据的id值
    POST /索引名称/_create/数据的id值
    
    PUT /索引名称/_doc/数据的id值
    PUT /索引名称/_create/数据的id值

只更新指定字段的值：

    POST/索引名称/_update/数据的ID值 {
        "doc":{
            // 更新内容
        }
    }

#### 查询数据

    # 查询所有
    GET /索引名称/_search
    # 根据ID查询
    GET /索引名称/_doc/数据的id值

#### 删除数据

    DELETE /索引名称/_doc/数据的id值
    
#### 示例

    # 添加或更新替换
    POST /ecommerce/_doc/1
    {
      "name":"小米手机",
      "desc":"支持5G、全面屏6.4",
      "price":3000,
      "producer":"小米",
      "tags":["mobile","5G"]
    }
    
    # 添加或更新替换
    PUT /ecommerce/_doc/2
    {
      "name":"华为MacBook",
      "desc":"支持5G、全面屏15.2寸",
      "price":8000,
      "producer":"Huawei",
      "tags":["笔记本电脑","huawei"]
    }
    
    # 添加或更新替换
    POST /ecommerce/_create/3
    {
      "name":"华为P40 pro",
      "desc":"支持5G、超清摄像",
      "price":12000,
      "producer":"Huawei",
      "tags":["mobile","huawei","5G"]
    }
    
    # 添加
    POST /ecommerce/_doc
    {
      "name":"Ipad mini 5",
      "desc":"7.9英寸",
      "price":4000,
      "producer":"apple",
      "tags":["笔记本电脑","apple"]
    }
    
    # 更新
    POST /ecommerce/_update/1
    {
      "doc": {
        "price":2000
      }
    }
    
    # 查询
    GET /ecommerce/_search
    GET /ecommerce/_doc/1

    # 删除
    DELETE /ecommerce/_doc/4
    

 

	
	





