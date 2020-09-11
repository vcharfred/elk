# ELK

-----------------------

[![build status](https://img.shields.io/badge/build-elasticsearch-red)](https://www.elastic.co/cn/elasticsearch/service)
[![jdk](https://img.shields.io/badge/jdk-1.8-green)]()
[![jdk](https://img.shields.io/badge/IK中文分词器-7.8-green)](https://github.com/medcl/elasticsearch-analysis-ik)

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
    docker run -d --name kibana --link es7:elasticsearch -p 5601:5601 kibana:7.8.0

通过`http://192.168.111.44:5601`访问kibana

> 如果需要中文界面在kibana.yml文件中添加 `i18n.locale: "zh-CN"`配置重启即可

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

> 删除使用的逻辑删除，之后会统一进行物理删除
    
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
      "producer":"Huawei 成都",
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

### elasticsearch查询语句示例

#### query string search

`query string search`就是将查询条件放到http的参数上

1、查询全部

    GET /ecommerce/_search

返回字段说明：

    took：耗费了几毫秒
    timed_out：是否超时
    _shards：数据拆成几个分片，所以对于搜索请求，会打到所有的primary shard（或者是它的某个replica shard也可以）
    hits.total：查询结果的数量，即几个document
    hits.max_score：score的含义，就是document对于一个search的相关度的匹配分数，越相关，就越匹配，分数也高
    hits.hits：包含了匹配搜索的document的详细数据

2、查询名称包含`华为`的商品,并且按照售价降序排序

    GET /ecommerce/_search?q=name:华为&sort=price:desc

3、只返回name、price字段

    GET /ecommerce/_search?_source=name,price

#### query DSL

DSL：Domain Specified Language，特定领域的语言

> http request body：请求体，可以用json的格式来构建查询语法，
> 比较方便，可以构建各种复杂的语法，比query string search肯定强大多了

查询所有match_all

    GET /ecommerce/_search
    {
        "query": { "match_all": {} }
    }

查询名称包含`华为`的商品，同时按照价格降序排序

    GET /ecommerce/_search
    {
      "query": {
        "match": {
          "name": "华为"
        }
      }
      , "sort": [
        {
          "price": {
            "order": "desc"
          }
        }
      ]
    }
    
分页查询

    GET /ecommerce/_search
    {
      "query": {
        "match_all": {}
      },
      "from": 2,
      "size": 2
    }
> from 从第几条开始，起始为0
> size 返回多少条记录

指定返回的字段

    GET /ecommerce/_search
    {
      "query": {
        "match_all": {}
      },
      "_source": ["name", "price"]
    }
    
#### query filter

对数据进行过滤

搜索商品名称包含`华为`，而且售价大于8000元的商品

    GET /ecommerce/_search
    {
    "query": {
     "bool": {
       "must": [
         {
           "match": {
             "name": "华为"
           }
         }
       ],
       "filter": [
         {
           "range": {
             "price": {
               "gt": 8000
             }
           }
         }
       ]
     }
    }
    }
    
    或者
    
    GET /ecommerce/_search
    {
      "query": {
        "bool": {
          "must": {
              "match": {
                "name": "华为"
              }
            }
          ,
          "filter": {
              "range": {
                "price": {
                  "gt": 8000
                }
              }
            }
        }
      }
    }
    
> bool 里面可以写多个条件

#### full-text search（全文检索）

全文检索会将输入的搜索串拆解开来，去倒排索引里面去一一匹配，只要能匹配上任意一个拆解后的单词，就可以作为结果返回
    
    GET /ecommerce/_search
    {
      "query":{
        "match": {
          "producer": "Huawei 成都"
        }
      }
    }

#### phrase search（短语搜索）

跟全文检索相对应相反，phrase search，要求输入的搜索串，必须在指定的字段文本中，完全包含一模一样的，才可以算匹配，才能作为结果返回

     GET /ecommerce/_search
     {
       "query": {
         "match_phrase": {
           "producer": "Huawei 成都"
         }
       }
     }
 
 
#### highlight search（高亮搜索结果）

高亮搜索结果就是将匹配的字段做标识，就像百度搜索中那些匹配的内容是红色显示

    GET /ecommerce/_search
    {
      "query": {
        "match": {
          "producer": "Huawei"
        }
      },
     "highlight": {
       "fields": {
         "producer": {}
       }
     }
    }

#### 聚合：计算每个tag下的商品数量

    GET /ecommerce/_search
    {
      "size": 0, 
      "aggs": {
       "group_by_tags":{
         "terms": {
           "field": "tags"
         }
       }
      }
    }
> group_by_tags 是随意取的一个名字，待会的查询统计结果会放到这个字段中
> 加size是不返回原始数据

上面那样操作会报错，需要先执行下面的语句，更新tags字段的fielddata属性设置为true

    PUT /ecommerce/_mapping
    {
      "properties":{
        "tags":{
          "type":"text",
          "fielddata":true
        }
      }
    }
    
#### 聚合：对名称中包含yagao的商品，计算每个tag下的商品数量

    GET /ecommerce/_search
    {
      "size": 0, 
      "query": {
        "match": {
          "name": "华为"
        }
      },
      "aggs": {
        "all_tags": {
          "terms": {
            "field": "tags"
          }
        }
      }
    }

> 先执行query条件查询，然后对结果做aggs聚合处理
    
    
#### 聚合：计算每个tag下的商品的平均价格（先分组再平均）

    GET /ecommerce/_search
    {
      "size": 0, 
      "aggs": {
        "group_by_tags": {
          "terms": {
            "field": "tags"
          },
          "aggs": {
            "avg_price": {
              "avg": {
                "field": "price"
              }
            }
          }
        }
      }
    }

#### 计算每个tag下的商品的平均价格，并且按照平均价格降序排序
	
	GET /ecommerce/_search
    {
      "size": 0
      , "aggs": {
        "all_tags": {
          "terms": {
            "field": "tags", "order": {
              "avg_price": "desc"
            }
          },
          "aggs": {
            "avg_price": {
              "avg": {
                "field": "price"
              }
            }
          }
        }
      }
    }

#### 按照指定的价格范围区间进行分组，然后在每组内再按照tag进行分组，最后再计算每组的平均价格

    GET /ecommerce/_search
    {
      "size": 0, 
      "aggs": {
        "group_by_price": {
          "range": {
            "field": "price",
            "ranges": [
              {
                "from": 0,
                "to": 5000
              },
              {
                "from": 6000
              }
            ]
          },
          "aggs": {
            "group_by_tags": {
              "terms": {
                "field": "tags"
              },
              "aggs": {
                "avg_price": {
                  "avg": {
                    "field": "price"
                  }
                }
              }
            }
          }
        }
      }
    }

### cerebo

这个是个es的监控软件，可以很方便的查询es集群的分片等情况，能集中管理alias和index template；在kibana中需要使用命令才可以实现，可以根据自己需要来安装。

拉取镜像：

    docker pull yannart/cerebro

启动容器：
    
    docker run -d -p 9000:9000 --name cerebro yannart/cerebro:latest

浏览器访问9000

    http://ip:9000

直接输入es的连接地址即可，如：`http://192.168.6.2:9200`

#### 在一台机器上导致连接失败的问题

由于资源有限，上面这些组件全部都放在了一机器上导致无法访问，需要打开防火墙开放端口才可以；

    查看防火墙状态
    systemctl status firewalld
    
    重启防火墙
    systemctl restart firewalld.service
    
    查看已经开放的端口
    firewall-cmd --zone=public --list-ports
 
    开放如下端口（第二个可选）
    firewall-cmd --zone=public --add-port=9200/tcp --permanent && firewall-cmd --reload
    firewall-cmd --zone=public --add-port=9300/tcp --permanent && firewall-cmd --reload
    firewall-cmd --zone=public --add-port=5601/tcp --permanent && firewall-cmd --reload
    firewall-cmd --zone=public --add-port=9000/tcp --permanent && firewall-cmd --reload

这样容器间就可以相互访问了（开启防火墙后可能需要重启docker服务才行）

## 三、Elasticsearch 的分布式集群

![](./image/Elasticsearch集群分布式构架.png)

### shard&replica机制

 （1）index包含多个shard
 
 （2）每个shard都是一个最小工作单元，承载部分数据，lucene实例，完整的建立索引和处理请求的能力
 
 （3）增减节点时，shard会自动在nodes中负载均衡
 
 （4）primary shard和replica shard，每个document肯定只存在于某一个primary shard以及其对应的replica shard中，不可能存在于多个primary shard
 
 （5）replica shard是primary shard的副本，负责容错，以及承担读请求负载
 
 （6）primary shard的数量在创建索引的时候就固定了，replica shard的数量可以随时修改
 
 （7）primary shard的默认数量是5，replica默认是1，默认有10个shard，5个primary shard，5个replica shard
 
 （8）primary shard不能和自己的replica shard放在同一个节点上（否则节点宕机，primary shard和副本都丢失，起不到容错的作用），但是可以和其他primary shard的replica shard放在同一个节点上

 （9）相同primary shard的replica shard不能放在同一个节点上；（节点宕机时，replica shard副本都丢失，起不到容错的作用）

### 单node环境下创建index

（1）单node环境下，创建一个index，有3个primary shard，3个replica shard

（2）集群status是yellow

（3）这个时候，只会将3个primary shard分配到仅有的一个node上去，另外3个replica shard是无法分配的

（4）集群可以正常工作，但是一旦出现节点宕机，数据全部丢失，而且集群不可用，无法承接任何请求

## 四、Elasticsearch内部相关实现

### 并发数据修改控制
 
Elasticsearch内部是多线程异步并发的进行修改（即可能出现后修改的先处理），采用version进行乐观锁；

具体原理：Elasticsearch每次执行更新和删除操作成功时，它的version都会自动加1，
每次执行更新删除时会带上版本号，如果版本号不一致，则会放弃此次操作；
这样就保证了后修改的先执行的情况能够正常处理，不会被先修改的覆盖掉。

#### 示例：在更新的时候带上版本号参数

    POST /ecommerce/_update/2?version=3
    {
      "doc":{
        "tags":["laptop ", "Huawei"]
      }
    }

> 当版本号version不匹配的时候会更新失败

### 使用external version来进行乐观锁并发控制

es提供了一个feature，就是说，你可以不用它提供的内部_version版本号来进行并发控制，可以基于你自己维护的一个版本号来进行并发控制。

举个列子，假如你的数据在mysql里也有一份，然后你的应用系统本身就维护了一个版本号，无论是什么自己生成的，程序控制的。
这个时候，你进行乐观锁并发控制的时候，可能并不是想要用es内部的_version来进行控制，而是用你自己维护的那个version来进行控制。


    PUT /ecommerce/_doc/1?version=2&version_type=external
    {
        "name" : "小米10Pro",
        "desc" : "支持5G、全面屏6.4",
        "price" : 3000,
        "producer" : "小米",
        "tags" : [
          "xiaomi",
          "mobile",
          "5G"
        ]
    }
    
> 在后面多加一个`version_type=external`参数，只有version版本比当前ES维护的版本号大就可以更新成功

### partial update说明

语法（url地址后面可以加版本号?version=1）：

    POST /索引名称/_update/ID值
    {
      "doc":{
            // 更新字段信息
      }
    }


使用partial update进行更新其实际执行过程如下：

1. 内部先获取document；
2. 将传过来的field更新到document的json中去；
3. 将原来的document标记为删除状态；
4. 将修改后的新的document创建出来；

> 实际上和传统的全量替换几乎一样。
> 
> 如果document不存在会报错

同时partial update将自动执行基于version的乐观锁并发控制


设置在发送冲突时进行重试的次数

    POST /ecommerce/_update/1?retry_on_conflict=2
    {
      "doc": {
        "price":3000
      }
    }


#### 优点

1. 所有查询、修改和写回操作都发生在es的一个shard内部，几乎避免了所有的网络数据传输开销，提升性能；
2. 减少了查询和修改的时间间隔，能够有效的减少并发的冲突的情况；（因为其内部操作几乎在毫秒级别）

#### 示例


    POST /ecommerce/_update/1
    {
      "doc":{
            "name" : "小米10"
      }
    }

### es的脚本支持：groovy

#### 使用内置脚本来做累加操作

将price加1

    POST /ecommerce/_update/1
    {
      "script": "ctx._source.price+=1"
    }

#### 外置脚本

这个相当于关系型数据库的存储过程，将需要执行的脚本放到es的`config/scripts`目录下

如在`config/scripts`目录下创建一个名为`add-price.groovy`文件，在里面写入如下脚本：

    ctx._source.price+=add_price

执行这个脚本：

    POST /ecommerce/_update/1
    {
      "script": {
        "lang": "groovy",
        "file": "add-price",
        "params": {
          "add_price":1
        }
      }
    }

##### 示例删除document的脚本
在`config/scripts`目录下创建一个名为`del-doc.groovy`文件，在里面写入如下脚本：

    ctx.op = ctx._source.price>price?'delete':'none'

执行脚本

    POST /ecommerce/_update/1
    {
      "script": {
        "lang": "groovy",
        "file": "del-doc",
        "params": {
          "price":5000
        }
      }
    }

#### upsert的使用

解决当在执行更新时document不存在导致更新失败的问题。

    POST /ecommerce/_update/1
    {
      "script": "ctx._source.price+=1",
      "upsert": {
        "price":0,
        "tags":[]
      }
    }

> upsert就是没有的时候对document进行初始化

### _mget批量查询

普通的查询方式只能一条一条的查询，使用mget可以实现批量查询，减少网络开销

#### 查询ID为1和2的数据

不同的index

    GET /_mget
    {
      "docs":[
        {
          "_index":"ecommerce",
          "_id":1
        },
            {
          "_index":"goods",
          "_id":2
        }
        ]
    }

同一个index

    GET /ecommerce/_mget
    {
      "docs":[
        {
          "_id":1
        },
        {
          "_id":2
        }
        ]
    }
    
同一个index且相同的filed      

    GET /ecommerce/_mget
    {
      "ids":[1,2]
      
    }

对返回的source字段进行过滤

    GET /ecommerce/_mget
    {
      "docs":[
        {
          "_id":1,
          "_source":["price","name"]
        },
        {
          "_id":2,
          "_source":"price"
        },
        {
          "_id":3,
          "_source":false
        }
        ]
    }

> 注意直接用ids来查询时不能进行字段过滤

### _bulk批量增删改

create：创建
delete：删除
update：更新

    POST /_bulk
    {"delete":{"_index":"ecommerce","_id":3}}
    {"create":{"_index":"ecommerce","_id":3}}
    {"price":5000}
    {"update":{"_index":"ecommerce","_id":3}}
    {"doc":{"price":6000}}

> 一条语句不能有换行这些，直接一行
> 在create之后可以添加需要添加的属性
> update的更新属性需要加`doc`
> 如果在一个index中可以不写index，直接跟在url上即可

    POST /ecommerce/_bulk
    {"delete":{"_id":3}}
    {"create":{"_id":3}}
    {"price":5000}
    {"update":{"_id":3}}
    {"doc":{"price":6000}}

_bulk在执行的时候，如果其中有一条语句执行失败，不会影响其他的执行，会在返回结果中将异常提示返回

#### bulk size最佳大小

bulk request会加载到内存里，如果太大的话，性能反而会下降，因此需要反复尝试一个最佳的bulk size。
一般从1000到5000条数据开始，尝试逐渐增加。另外，如果看大小的话，最好是在5~15MB之间。

### 什么是distributed document store？

围绕着document在操作，其实就是把es当成了一个NoSQL存储引擎，一个可以存储文档类型数据的存储系统，操作里面的document。

适合的的应用程序类型

（1）数据量较大，es的分布式本质，可以帮助你快速进行扩容，承载大量数据

（2）数据结构灵活多变，随时可能会变化，而且数据结构之间的关系，非常复杂，如果我们用传统数据库，那是不是很坑，因为要面临大量的表

（3）对数据的相关操作，较为简单，比如就是一些简单的增删改查，用我们之前讲解的那些document操作就可以搞定

（4）NoSQL数据库，适用的也是类似于上面的这种场景

### document数据路由原理
（1）document路由到shard上是什么意思？

一个index的数据会被分为多片，每片都在一个shard中，因此一个document只能存在一个shard中；
当有一个document需要操作时，es就需要知道这个document是放在index的那个shard上的。
这个过程就称之为document的数据路由。

（2）路由算法：shard = hash(routing) % number_of_primary_shards

举个例子来简要说明哈这个算法：
    一个index有3个primary shard（分别为P0，P1，P2），每次增删改查一个document的时候，都会带过来一个routing number，
默认就是这个document的_id（可能是手动指定，也可能是自动生成）routing = _id，假设_id=1；将这个routing值传入一个hash函数中，产出一个routing值的hash值；
然后将hash函数产出的值对这个index的primary shard的数量求余数，根据这个余数的值决定document放在那个shard上

> 决定一个document在哪个shard上，最重要的一个值就是routing值，默认是_id，也可以手动指定，保证相同的routing值，每次过来，从hash函数中，产出的hash值一定是相同的；
> 这也是为什么ES启动后设置好primary_shards数量之后，primary_shards的数量不能再更改了的原因

### document增删改内部原理

（1）客户端选择一个node发送请求过去，这个node就是coordinating node（协调节点）
（2）coordinating node，对document进行路由，将请求转发给对应的node（有primary shard）
（3）实际的node上的primary shard处理请求，然后将数据同步到replica node
（4）coordinating node，如果发现primary node和所有replica node都搞定之后，就返回响应结果给客户端

![](./image/Elasticsearch增删改内部原理.png)

### 写一致性原理以及quorum机制剖析

consistency，one（primary shard），all（all shard），quorum（default）

我们在发送任何一个增删改操作的时候，比如说`put /index/_doc/id`，都可以带上一个`consistency`参数，指明我们想要的写一致性是什么？
`put /index/_doc/id?consistency=quorum`

    one：要求我们这个写操作，只要有一个primary shard是active活跃可用的，就可以执行
    all：要求我们这个写操作，必须所有的primary shard和replica shard都是活跃的，才可以执行这个写操作
    quorum：默认的值，要求所有的shard中，必须是大部分的shard都是活跃的，可用的，才可以执行这个写操作

#### quorum机制，写之前必须确保大多数shard都可用（也就是半数以上）

计算公式：

    quroum = int( (primary + number_of_replicas) / 2 ) + 1，当number_of_replicas>1时才生效；active状态的的shard数>=quroum才可以执行
    
    
举个例子：

    3个primary shard，number_of_replicas=1，总共有3 + 3 * 1 = 6个shard
    quorum = int( (3 + 1) / 2 ) + 1 = 3
    所以，要求6个shard中至少有3个shard是active状态的，才可以执行写操作

#### 如果节点数少于quorum数量，可能导致quorum不齐全，进而导致无法执行任何写操作

如下2个例子：

例子1：3个primary shard，replica=1，要求至少3个shard是active，3个shard按照之前学习的shard&replica机制，必须在不同的节点上，如果说只有1台机器的话，是不是有可能出现说，3个shard都没法分配齐全，此时就可能会出现写操作无法执行的情况

例子2：1个primary shard，replica=3，quorum=((1 + 3) / 2) + 1 = 3，要求1个primary shard + 3个replica shard = 4个shard，其中必须有3个shard是要处于active状态的。如果这个时候只有2台机器的话，会出现什么情况呢？

因此es提供了一种特殊的处理场景，当number_of_replicas>1时才生效，因为假如说，就一个primary shard，replica=1，此时就2个shard
(1 + 1 / 2) + 1 = 2，要求必须有2个shard是活跃的，但是可能就1个node，此时就1个shard是活跃的，如果你不特殊处理的话，导致我们的单节点集群就无法工作

#### quorum不齐全时，wait，默认1分钟，timeout，100，30s

等待期间，期望活跃的shard数量可以增加，最后实在不行，就会timeout

我们其实可以在写操作的时候，加一个timeout参数，比如说`put /index/type/id?timeout=30`，这个就是自己去设定quorum不满足条件的时候，es的timeout时长，可以缩短，也可以增长

### document查询内部原理

1. 客户端发送请求到任意一个node，成为coordinate node
2. coordinate node对document进行路由，将请求转发到对应的node，此时会使用round-robin随机轮询算法，在primary shard以及其所有replica中随机选择一个，让读请求负载均衡
3. 接收请求的node返回document给coordinate node
4. coordinate node返回document给客户端
5. 特殊情况：document如果还在建立索引过程中，可能只有primary shard有，任何一个replica shard都没有，此时可能会导致无法读取到document，
但是document完成索引建立之后，primary shard和replica shard就都有了

![](./image/Elasticsearch查询内部原理.png)

### bulk api的奇特json格式与底层性能优化关系

bulk api奇特的json格式

    {"action": {"meta"}}\n
    {"data"}\n
    {"action": {"meta"}}\n
    {"data"}\n

1、bulk中的每个操作都可能要转发到不同的node的shard去执行

2、如果采用比较良好的json数组格式

允许任意的换行，整个可读性非常棒，读起来很爽，es拿到那种标准格式的json串以后，要按照下述流程去进行处理

* 将json数组解析为JSONArray对象，这个时候，整个数据，就会在内存中出现一份一模一样的拷贝，一份数据是json文本，一份数据是JSONArray对象
* 解析json数组里的每个json，对每个请求中的document进行路由
* 为路由到同一个shard上的多个请求，创建一个请求数组
* 将这个请求数组序列化
* 将序列化后的请求数组发送到对应的节点上去

3、耗费更多内存，更多的jvm gc开销

占用更多的内存可能就会积压其他请求的内存使用量，比如说最重要的搜索请求，分析请求，等等，此时就可能会导致其他请求的性能急速下降
另外的话，占用内存更多，就会导致java虚拟机的垃圾回收次数更多，更频繁，每次要回收的垃圾对象更多，耗费的时间更多，导致es的java虚拟机停止工作线程的时间更多

假如：一个bulk size的请求为10M，共计100个请求就是1GB的内存占用，假设转为json对象后为2GB，如果请求数量更多，那么消耗的内存就就更多了，同时Java虚拟机的垃圾回收也会更加的耗时，导致系统性能下降。

4、使用现在的奇特格式的优点

* 不用将其转换为json对象，不会出现内存中的相同数据的拷贝，直接按照换行符切割json
* 对每两个一组的json，读取meta，进行document路由
* 直接将对应的json发送到node上去

5、最大的优势在于，不需要将json数组解析为一个JSONArray对象形成一份大数据的拷贝，浪费内存空间，这样可以尽可能地保证性能

## 五、搜索引擎

### 5.1 search结果解析（search timeout机制说明）

![](./image/Elasticsearch搜索的timeout机制.png)

    took：整个搜索请求花费了多少毫秒
    hits.total：本次搜索，返回了几条结果
    hits.max_score：本次搜索的所有结果中，最大的相关度分数是多少，每一条document对于search的相关度，_score分数越大，排位越靠前
    hits.hits：默认查询前10条数据，完整数据，_score降序排序
    timeout：默认无timeout，latency平衡completeness，手动指定timeout，timeout查询执行机制
    shards：shards fail的条件（primary和replica全部挂掉），不影响其他shard。默认情况下来说，一个搜索请求，会打到一个index的所有primary shard上去，每个primary shard都可能会有一个或多个replica shard，所以请求也可以到primary shard的其中一个replica shard上去。


带上超时参数：timeout=10ms，timeout=1s，timeout=1m

    GET /_search?timeout=10m

### 5.2 multi-index和multi-type搜索模式解析以及搜索原理

#### multi-index和multi-type搜索模式

如何一次性搜索多个index和多个type下的数据

    GET /_search：               所有索引，所有type下的所有数据都搜索出来
    GET /index1/_search：        指定一个index，搜索其下所有的数据
    GET /index1,index2/_search： 同时搜索两个index下的数据
    GET /*1,*2/_search：         按照通配符去匹配多个索引
    GET /_all/_search            可以代表搜索所有index下的数据

> 也可以加删除type属性，但是es

#### 简单的搜索原理

客户端发送一个请求，会将请求分发到所有的primary shard上执行，因为每一个shard上都包含部分数据，所有每一个shard上都可能包含搜索请求的结果；
如果primary shard有 replica shard，那么请求也会发送到replica shard上去处理

### 5.3 分页搜索以及deep paging性能问题

#### 使用es进行分页搜索的语法

size，from

    GET /_search?size=10
    GET /_search?size=10&from=0
    GET /_search?size=10&from=20

#### deep paging

搜索很深就是deep paging；会很耗费性能，应当尽量避免。比如查询临近最后一页的数据，而数据在各个分片上，最后需要将各个分片返回的数据进行综合处理，每个分片实际返回数据不是每页的条数。

### 5.4 快速掌握query string search语法以及_all metadata

#### query string基础语法

    GET /demo_index/_search?q=test_field:test
    GET /demo_index/_search?q=+test_field:test
    GET /demo_index/_search?q=-test_field:test

使用+号和没有+号是一样，表示包含指定的关键词；-号表示不含

#### _all metadata的原理和作用

    # 匹配包含test的数据
    GET /demo_index/_search?q=test

直接可以搜索所有的field，任意一个field包含指定的关键字就可以搜索出来。我们在进行中搜索的时候，不是对document中的每一个field都进行一次搜索；
    
es中的_all元数据，在建立索引的时候，每插入一条document，它里面包含了多个field，此时，es会自动将多个field的值，全部用字符串的方式串联起来，变成一个长的字符串，作为_all field的值，同时建立索引；
后面如果在搜索的时候，没有对某个field指定搜索，就默认搜索_all field，其中是包含了所有field的值的。
    
举个例子
    
    {
      "name": "tom",
      "age": 25,
      "email": "tom@1qq.com",
      "address": "beijing"
    }
    
`tom 25 tom@qq.com beijing`，作为这一条document的_all field的值，同时进行分词后建立对应的倒排索引；生产环境通常不使用

### 5.5 mapping到底是什么？

自动或手动为index中的type建立的一种数据结构和相关配置，简称为mapping。

当添加数据时会dynamic mapping，自动为我们建立index，创建type，以及type对应的mapping，mapping中包含了每个field对应的数据类型，以及如何分词等设置。

#### 示例

添加一些测试数据

    PUT /website/_doc/1
    {
      "post_date": "2020-01-01",
      "title": "my first article",
      "content": "this is my first article in this website",
      "author_id": 9527
    }
    
    PUT /website/_doc/2
    {
      "post_date": "2020-01-02",
      "title": "my second article",
      "content": "this is my second article in this website",
      "author_id": 9527
    }
    
    PUT /website/_doc/3
    {
      "post_date": "2020-01-03",
      "title": "my third article",
      "content": "this is my third article in this website",
      "author_id": 9527
    }

尝试如下搜索，只会返回1条数据：

    GET /website/_search?q=2020
    GET /website/_search?q=2020-01-02
    GET /website/_search?q=post_date:2020-01-01
    GET /website/_search?q=post_date:2020

查看mapping

    GET /website/_mapping

搜索结果为什么不一致，因为es自动建立mapping的时候，设置了不同的field不同的data type。不同的data type的分词、搜索等行为是不一样的。所以出现了_all field和post_date field的搜索表现不是我们所期望的

### 5.6 精确匹配与全文搜索的对比分析

#### 精确匹配（exact value）

2020-01-01，exact value，搜索的时候，2020-01-01，才能搜索出来；如果你输入一个01，是搜索不出来的

#### 全文搜索（full text）

* 缩写 vs. 全程：cn vs. china；如2020-01-01，2020 01 01，搜索2020，或者01，都可以搜索出来；china，搜索cn，也可以将china搜索出来
* 格式转化：like liked likes；likes，搜索like，也可以将likes搜索出来
* 大小写：Tom vs tom；Tom，搜索tom，也可以将Tom搜索出来
* 同义词：like vs love；like，搜索love，同义词，也可以将like搜索出来

所有全文搜索不只是匹配完整的一个值，而是可以对值进行拆分词语后（分词）进行匹配，也可以通过缩写、时态、大小写、同义词等进行匹配。

### 5.7 倒排索引核心原理

倒排索引（Inverted Index）也叫反向索引，有反向索引必有正向索引。通俗地来讲，正向索引是通过key找value，反向索引则是通过value找key。

doc1：I really liked my small dogs, and I think my mom also liked them.
doc2：He never liked any dogs, so I hope that my mom will not expect me to liked him.

分词，初步的倒排索引的建立

    |word  |doc1|doc2|
    |:     |:   |:   |
    |I     | *	|  * |
    |really| *  |    |
    |liked | *	|    |
    |my	   | *	|  * |
    |small | *	|    |
    |dogs  | *  |    |
    |and   | *  |    |
    ....

搜索`mother like little dog`，不可能有任何结果

这个是不是我们想要的搜索结果，因为在我们看来，mother和mom有区别吗？同义词，都是妈妈的意思。like和liked有区别吗？没有，都是喜欢的意思，只不过一个是现在时，一个是过去时。little和small有区别吗？同义词，都是小小的。dog和dogs有区别吗？狗，只不过一个是单数，一个是复数。

因此正常情况下在建立倒排索引的时候，会执行一个normalization操作，对拆分出的各个单词进行相应的处理，以提升后面搜索的时候能够搜索到相关联的文档的概率

重新建立倒排索引，加入normalization，再次用mother liked little dog搜索，就可以搜索到了

`mother like little dog`会先分词再normalization（时态的转换，单复数的转换，同义词的转换，大小写的转换）

    mother	--> mom
    like	--> like
    little	--> little
    dog	--> dog

doc1和doc2都会搜索出来

### 5.8 分词器的内部组成到底是什么，以及内置分词器的介绍

#### 什么是分词器

一个分词器，将一段文本拆分成一个一个的单个的单词，同时对每个单词进行normalization（时态转换，单复数转换），最后将处理好的结果才会拿去建立倒排索引。

切分词语，normalization（提升召回率【recall】）；具体包含如下：

    recall，召回率：搜索的时候，增加能够搜索到的结果的数量

    character filter：在一段文本进行分词之前，先进行预处理，比如说最常见的就是，过滤html标签（<span>hello<span> --> hello），& --> and（I&you --> I and you）

    tokenizer：分词，hello you and me --> hello, you, and, me

    token filter：lowercase，stop word，synonymom，dogs --> dog，liked --> like，Tom --> tom，a/the/an --> 干掉，mother --> mom，small --> little

### 5.9 query string的分词以及mapping

query string必须以和index建立时相同的analyzer进行分词

query string对exact value和full text的区别对待

> 不同类型的field，可能有的就是full text，有的就是exact value；因此上面进行搜索时查询结果不是我们预期的

#### 测试分词器

    GET /_analyze
    {
      "analyzer": "standard",
      "text": "Text to analyze"
    }

### 5.10 什么是mapping再次回炉透彻理解

* 往es里面直接插入数据，es会自动建立索引，同时建立type以及对应的mapping
* mapping中就自动定义了每个field的数据类型
* 不同的数据类型（比如说text和date），可能有的是exact value，有的是full text
* exact value，在建立倒排索引的时候，分词的时候，是将整个值一起作为一个关键词建立到倒排索引中的；full text，会经历各种各样的处理，分词，normaliztion（时态转换，同义词转换，大小写转换），才会建立到倒排索引中
* 同时呢，exact value和full text类型的field就决定了，在一个搜索过来的时候，对exact value field或者是full text field进行搜索的行为也是不一样的，会跟建立倒排索引的行为保持一致；比如说exact value搜索的时候，就是直接按照整个值进行匹配，full text query string，也会进行分词和normalization再去倒排索引中去搜索
* 可以用es的dynamic mapping，让其自动建立mapping，包括自动设置数据类型；也可以提前手动创建index和type的mapping，自己对各个field进行设置，包括数据类型，包括索引行为，包括分词器，等等

mapping，就是index的type的元数据，每个type都有一个自己的mapping，决定了数据类型，建立倒排索引的行为，还有进行搜索的行为

### 5.11 mapping的核心数据类型以及dynamic mapping    

#### 核心的数据类型

    string
    byte，short，integer，long
    float，double
    boolean
    date
#### dynamic mapping类型推测

    true or false	 -->	boolean
    123		         -->	long
    123.01		     -->	double
    2020-01-01	     -->	date
    "hello world es" -->	string/text

### 5.12 手动建立和修改mapping以及定制string类型数据是否分词

#### 如何建立索引

    analyzed      ------ 分词类型
    not_analyzed  ------ 不分词
    no            ------ 不分词同时不能被搜索

只能创建index时手动建立mapping，或者新增field mapping，但是不能update field mapping

    PUT /website
    {
      "mappings": {
        "properties": {
          "author_id": {
            "type": "long"
          },
          "title": {
            "type": "text",
            "analyzer": "english"
          },
          "content": {
            "type": "text"
          },
          "post_date": {
            "type": "date"
          },
          "publisher_id": {
            "type": "keyword"
          },
          "is_del": {
            "type":"boolean",
            "index":false
          }
        }
      }
    }

> "index":false 表示不加入索引
> "type": "keyword" 表示不分词，在7.x版本后not_analyzed已经被取消掉了

新增filed mapping

    PUT /website/_mapping
    {
      "properties":{
        
        "new_filed":{
          "type":"text",
          "index":false
        }
      }
    }

### 5.13 mapping复杂数据类型以及object类型数据底层结构

#### multivalue field

    { "tags": [ "tag1", "tag2" ]}

建立索引时与string是一样的，数据类型不能混

#### empty field

    null，[]，[null]

#### object field

    PUT /company/employee/1
    {
      "address": {
        "country": "china",
        "province": "guangdong",
        "city": "guangzhou"
      },
      "name": "jack",
      "age": 27,
      "join_date": "2020-01-01"
    }

对应这种object类型的底层数据存储示例

    "authors": [
        { "age": 26, "name": "Jack White"},
        { "age": 55, "name": "Tom Jones"},
        { "age": 39, "name": "Kitty Smith"}
    ]
    
    上面的会转换成下面这种：
    
    {
        "authors.age":    [26, 55, 39],
        "authors.name":   [jack, white, tom, jones, kitty, smith]
    }

### 5.14 search api的基础语法介绍
    
#### search api的基本语法

    GET /search
    {}

    GET /index1,index2/type1,type2/search
    {}

    GET /_search
    {
      "from": 0,
      "size": 10
    }

#### http协议中get是否可以带上request body
    
HTTP协议，一般不允许get请求带上request body，但是因为get更加适合描述查询数据的操作。

碰巧，很多浏览器，或者是服务器，也都支持GET+request body模式；如果遇到不支持的场景，也可以用POST /_search

### 5.15 快速上机动手实战Query DSL搜索语法

#### 示例什么是Query DSL

    GET /_search
    {
        "query": {
            "match_all": {}
        }
    }
#### Query DSL的基本语法
    {
        QUERY_NAME: {
            ARGUMENT: VALUE,
            ARGUMENT: VALUE,...
        }
    }

    {
        QUERY_NAME: {
            FIELD_NAME: {
                ARGUMENT: VALUE,
                ARGUMENT: VALUE,...
            }
        }
    }

示例

    GET /test_index/_search 
    {
      "query": {
        "match": {
          "test_field": "test"
        }
      }
    }
    
#### 如何组合多个搜索条件

初始数据：

    PUT /website/_doc/1
    {
      
      "title": "my elasticsearch article",
      "content": "es is very bad",
      "author_id": 110
    }
    
    PUT /website/_doc/2
    {
      
      "title": "my elasticsearch article",
      "content": "es is very good",
      "author_id": 111
    }
    
    PUT /website/_doc/3
    {
      
      "title": "my elasticsearch article",
      "content": "es is just so so",
      "author_id": 112
    }



1. title必须包含elasticsearch，content可以包含elasticsearch也可以不包含，author_id必须不为111


    GET /website/_search
    {
      "query": {
        "bool": {
          "must": [
            {
              "match": {
                "title": "elasticsearch"
              }
            }
          ],
          "should": [
            {
              "match": {
                "content": "elasticsearch"
              }
            }
          ],
          "must_not": [
            {
              "match": {
                "author_id": 111
              }
            }
          ]
        }
      }
    }    

示例2

    GET /website/_search
    {
      "query": {
        "bool": {
          "must": {
            "match": {
              "name": "tom"
            }
          },
          "should": [
            {
              "match": {
                "hired": true
              }
            },
            {
              "bool": {
                "must": {
                  "match": {
                    "personality": "good"
                  }
                },
                "must_not": {
                  "match": {
                    "rude": true
                  }
                }
              }
            }
          ],
          "minimum_should_match": 1
        }
      }
    }
    
> should 相当于or
> bool 相当于（）
> must 相当于and
> must_not 就是不等于  

### 5.16 filter与query深入对比解密：相关度，性能

#### filter与query示例

    PUT /company/_doc/1
    {
      "join_date": "2016-01-01",
      "age":33,
      "name":"tom cat"
    }
    
    PUT /company/_doc/2
    {
      "join_date": "2016-01-01",
      "age":29,
      "name":"jerry mouse"
    }

搜索请求：年龄必须大于等于30，同时join_date必须是2016-01-01

    GET /company/_search
    {
      "query": {
        "bool": {
          "must": [
            {
              "match": {
                "join_date": "2016-01-01"
              }
            }
          ],
          "filter": {
            "range": {
              "age": {
                "gte": 30
              }
            }
          }
        }
      }
    }

#### filter与query对比大解密

* filter，仅仅只是按照搜索条件过滤出需要的数据而已，不计算任何相关度分数，对相关度没有任何影响
* query，会去计算每个document相对于搜索条件的相关度，并按照相关度进行排序

一般来说，如果你是在进行搜索，需要将最匹配搜索条件的数据先返回，那么用query；如果只是要根据一些条件筛选出一部分数据，不关注其排序，那么用filter
除非是你的这些搜索条件，你希望越符合这些搜索条件的document越排在前面返回，那么这些搜索条件要放在query中；如果你不希望一些搜索条件来影响你的document排序，那么就放在filter中即可

#### filter与query性能

* filter，不需要计算相关度分数，不需要按照相关度分数进行排序，同时还有内置的自动cache最常使用filter的数据
* query，相反，要计算相关度分数，按照分数进行排序，而且无法cache结果


bool，must，must_not，should，filter
每个子查询都会计算一个document针对它的相关度分数，然后bool综合所有分数，合并为一个分数，当然filter是不会计算分数的

### 5.17 常用的各种query搜索语法

#### match all

    GET /_search
    {
        "query": {
            "match_all": {}
        }
    }

#### match

    GET /_search
    {
        "query": { "match": { "title": "my elasticsearch article" }}
    }

#### multi match

    GET /test_index/_search
    {
      "query": {
        "multi_match": {
          "query": "test",
          "fields": ["test_field", "test_field1"]
        }
      }
    }

#### range query

    GET /company/_search 
    {
      "query": {
        "range": {
          "age": {
            "gte": 30
          }
        }
      }
    }

#### term query

    GET /test_index/_search 
    {
      "query": {
        "term": {
          "test_field": "test hello"
        }
      }
    }

#### terms query

对tag指定多个分组词

    GET /_search
    {
        "query": { "terms": { "tag": [ "search", "full_text", "nosql" ] }}
    }

### 5.18 如何定位不合法的搜索以及其原因

    GET /company/_validate/query?explain
    {
      "query": {
        "match": {
          "name": "cat"
        }
      }
    }

一般用在那种特别复杂庞大的搜索下，比如一下子写了上百行的搜索，这个时候可以先用validate api去验证一下，搜索是否合法

### 5.19 如何定制搜索结果的排序规则

#### 默认排序规则

默认情况下，是按照_score降序排序的；然而，某些情况下，可能没有有用的_score，比如说filter

    GET /_search
    {
      "query": {
        "bool": {
          "filter": {
            "term": {
              "name": "cat"
            }
          }
        }
      }
    }

当然，也可以是constant_score

    GET /_search
    {
      "query": {
        "constant_score": {
          "filter": {
            "term": {
              "author_id": 1
            }
          }
        }
      }
    }

#### 定制排序规则

使用sort来定制排序规则

    GET /company/_search 
    {
      "query": {
        "constant_score": {
          "filter": {
            "range": {
              "age": {
                "gte": 25
              }
            }
          }
        }
      },
      "sort": [
        {
          "join_date": {
            "order": "asc"
          }
        }
      ]
    }

### 5.20 如何将一个field索引两次来解决字符串排序问题
如果对一个string field进行排序，结果往往不准确，因为分词后是多个单词，再排序就不是我们想要的结果了

通常解决方案是，将一个string field建立两次索引，一个分词，用来进行搜索；一个不分词，用来进行排序

示例：先创建索引

    PUT /website 
    {
      "mappings": {
        "properties": {
          "title": {
            "type": "text",
            "fields": {
              "raw": {
                "type": "keyword"
              }
            },
            "fielddata": true
          },
          "content": {
            "type": "text"
          },
          "post_date": {
            "type": "date"
          },
          "author_id": {
            "type": "long"
          }
        }
      }
    }

> 设置正排索引 "fielddata": true

添加初始数据

    PUT /website/_doc/1
    {
      "title": "first article",
      "content": "this is my first article",
      "post_date": "2017-01-01",
      "author_id": 110
    }
    
    PUT /website/_doc/2
    {
      "title": "second article",
      "content": "this is my second article",
      "post_date": "2018-01-01",
      "author_id": 111
    }   

执行查询

    GET /website/_search
    {
      "query": {
        "match_all": {}
      },
      "sort": [
        {
          "title.raw": {
            "order": "desc"
          }
        }
      ]
    }    

### 5.21 相关度评分TF&IDF算法    

#### TF&IDF算法介绍

* relevance score算法，简单来说，就是计算出，一个索引中的文本，与搜索文本，他们之间的关联匹配程度；
* Elasticsearch使用的是 term frequency/inverse document frequency算法，简称为TF/IDF算法；
* Term frequency：搜索文本中的各个词条在field文本中出现了多少次，出现次数越多，就越相关；
* Inverse document frequency：搜索文本中的各个词条在整个索引的所有文档中出现了多少次，出现的次数越多，就越不相关；


    举个例子：
       搜索请求：hello world
       
       doc1：hello, today is very good
       doc2：hi world, how are you
       
       比如说，在index中有1万条document，hello这个单词在所有的document中，一共出现了1000次；world这个单词在所有的document中，一共出现了100次；那么doc2的相关度越高

* Field-length norm：field内容长度，越长，相关度越弱       

### 5.22 内核级知识点之doc value初步探秘

搜索的时候，要依靠倒排索引；排序的时候，需要依靠正排索引，看到每个document的每个field，然后进行排序，所谓的正排索引，其实就是doc values；
在建立索引的时候，一方面会建立倒排索引，以供搜索用；一方面会建立正排索引，也就是doc values，以供排序，聚合，过滤等操作使用；
doc values是被保存在磁盘上的，此时如果内存足够，os会自动将其缓存在内存中，性能还是会很高；如果内存不足够，os会将其写入磁盘上；

倒排索引类似如下（对每个字段进行操作）：

    doc1的content字段内容: hello world you and me
    doc2的content字段内容: hi, world, how are you

对上面的内容进行分词，拆分为一个个单词(term)，建立类似如下的字典目录

|term|Posting List（倒排列表）|
|:--:|:--|
|world|[doc1,doc2]|
|hello|doc1|
|hi|doc2|
|...|....|

> Posting List（倒排列表）里面是文档的id

这样在搜索时根据term的索引（类似MySQL的索引）去找到符合的term进而找到对应的文档信息；

可以这样理解倒排索引：通过单词找到对应的倒排列表，根据倒排列表中的倒排项进而可以找到文档记录；过程类型如下图：
![](./image/倒排索引.png)


正排索引类似如下（对整个文档进行操作）：
    
    doc1内容: { "name": "jack", "age": 27 }
    doc2内容: { "name": "tom", "age": 30 }
    
    document	name		age
    doc1		jack		27
    doc2		tom		    30	

### 5.23 分布式搜索引擎内核解密之query phase
#### query phase

1. 搜索请求发送到某一个coordinate node，构构建一个priority queue，长度以paging操作from和size为准，默认为10
2. coordinate node将请求转发到所有shard，每个shard本地搜索，并构建一个本地的priority queue
3. 各个shard将自己的priority queue返回给coordinate node，并构建一个全局的priority queue

![](./image/Elasticsearch的query_phase原理.png)

#### replica shard如何提升搜索吞吐量

一次请求要打到所有shard的一个replica/primary上去，如果每个shard都有多个replica，那么同时并发过来的搜索请求可以同时打到其他的replica上去

### 5.24 分布式搜索引擎内核解密之fetch phase

fetch phbase工作流程

1. coordinate node构建完priority queue之后，就发送mget请求去所有shard上获取对应的document
2. 各个shard将document返回给coordinate node
3. coordinate node将合并后的document结果返回给client客户端

一般搜索，如果不加from和size，就默认搜索前10条，按照_score排序

![](./image/Elasticsearch的fetch_phase原理.png)

### 5.25 搜索相关参数梳理以及bouncing results问题解决方案       

#### preference

决定了哪些shard会被用来执行搜索操作

_primary, _primary_first, _local, _only_node:xyz, _prefer_node:xyz, _shards:2,3

bouncing results问题，两个document排序，field值相同；不同的shard上，可能排序不同；每次请求轮询打到不同的replica shard上；每次页面上看到的搜索结果的排序都不一样。这就是bouncing result，也就是跳跃的结果。

搜索的时候，是轮询将搜索请求发送到每一个replica shard（primary shard），但是在不同的shard上，可能document的排序不同

解决方案就是将preference设置为一个字符串，比如说user_id，让每个user每次搜索的时候，都使用同一个replica shard去执行，就不会看到bouncing results了

#### timeout

主要就是限定在一定时间内，将部分获取到的数据直接返回，避免查询耗时过长

#### routing

document文档路由，_id路由，routing=user_id，这样的话可以让同一个user对应的数据到一个shard上去

#### search_type

default：query_then_fetch

dfs_query_then_fetch，可以提升revelance sort精准度

### 5.26 基于scoll技术滚动搜索大量数据

如果一次性要查出来10万条数据，那么性能会很差，此时一般会采取用scoll滚动查询，一批一批的查，直到所有数据都查询完处理完。

使用scoll滚动搜索，可以先搜索一批数据，然后下次再搜索一批数据，以此类推，直到搜索出全部的数据来

scoll搜索会在第一次搜索的时候，保存一个当时的视图快照，之后只会基于该旧的视图快照提供数据搜索，如果这个期间数据变更，是不会让用户看到的

采用基于_doc进行排序的方式，性能较高

每次发送scroll请求，我们还需要指定一个scoll参数，指定一个时间窗口，每次搜索请求只要在这个时间窗口内能完成就可以了；


    GET /website/_search?scroll=1m
    {
      "query": {
        "match_all": {}
      },
      "sort": [ "_doc" ],
      "size": 3
    }

获得的结果会有一个scoll_id，下一次再发送scoll请求的时候，必须带上这个scoll_id

    GET /_search/scroll
    {
        "scroll": "1m", 
        "scroll_id" : "FGluY2x1ZGVfY29udGV4dF91dWlkDXF1ZXJ5QW5kRmV0Y2gBFHg3bnJvM01CYXBadGRjZ1FELWNqAAAAAAAADY8WdXVKQzR3TzVSMEtialVYM1gxbWkzZw=="
    }

## 六、索引管理

### 6.1 索引的创建、修改、删除

#### 创建索引

指定分片信息、mapping信息

    PUT /index_demo
    {
      "settings": {
        "number_of_shards": 1,
        "number_of_replicas": 0
      },
      "mappings": {
        "properties": {
          "name":{
            "type": "text"
          }
        }
      }
    }

使用默认的配置
    
    PUT /index_pretty?pretty

#### 修改索引

    PUT /index_demo/_settings
    {
      "number_of_replicas": 1
    }

#### 删除索引

    DELETE /index_demo
    DELETE /index_1,index_2
    DELETE /index_demo*
    DELETE /_all
     
     
     
### 6.2 修改分词器以及定制自己的分词器

#### 默认的分词器standard

* standard tokenizer：以单词边界进行切分
* standard token filter：什么都不做
* lowercase token filter：将所有字母转换为小写
* stop token filer（默认被禁用）：移除停用词，比如a the it等等

#### 修改分词器的设置

* 启用english停用词token filter（创建索引的时候才可以）


    PUT /index_demo
    {
      "settings": {
        "number_of_shards": 1,
        "number_of_replicas": 0,
        "analysis": {
          "analyzer": {
            "es_std":{
              "type": "standard",
              "stopwords": "_english_"
            }
          }
        }
      },
      "mappings": {
        "properties": {
          "name":{
            "type": "text"
          }
        }
      }
    }

测试定制的分词器的效果：

    # 使用定制的
    GET /index_demo/_analyze
    {
      "analyzer": "es_std",
      "text": "a dog is in the house"
    }
    
    # 使用默认的
    GET /index_demo/_analyze
    {
      "analyzer": "standard",
      "text":"a dog is in the house"
    }

#### 定制分词器

将`&`转换为and，`a 、the`不做处理，将html标签过滤掉，将字符转为小写的

    PUT /index_demo
    {
      "settings": {
        "analysis": {
          "char_filter": {
            "&_to_and": {
              "type": "mapping",
              "mappings": ["&=> and"]
            }
          },
          "filter": {
            "my_stopwords":{
              "type": "stop",
              "stopwords": ["the", "a"]
            }
          },
          "analyzer": {
            "my_analyzer":{
              "type":"custom",
              "char_filter": ["html_strip", "&_to_and"],
              "tokenizer":"standard",
              "filter":["lowercase","my_stopwords"]
            }
          }
        }
      }
    }    

测试定制的分词器

    GET /index_demo/_analyze
    {
      "text": "tom&jerry are a friend in the house, <a>, HAHA!!",
      "analyzer": "my_analyzer"
    }
    
    
    
### 6.3 深入探秘type底层数据结构

type，是一个index中用来区分类似的数据的，类似的数据，但是可能有不同的fields，而且有不同的属性来控制索引建立、分词器；
field的value，在底层的lucene中建立索引的时候，全部是opaque bytes类型，不区分类型的；
lucene是没有type的概念的，在document中，实际上将type作为一个document的field来存储，即_type，es通过_type来进行type的过滤和筛选；
一个index中的多个type，实际上是放在一起存储的，因此一个index下，不能有多个type重名，因为那样是无法处理的；
在es7中一个index只能有一个type，默认为_doc，不推荐去自定义了。

#### 举例说明

设置的mappings如下：

    {
       "ecommerce": {
          "mappings": {
            "_type": {
              "type": "string",
              "index": "not_analyzed"
            },
            "name": {
              "type": "string"
            }
            "price": {
              "type": "double"
            }
            "service_period": {
              "type": "string"
            }
            "eat_period": {
              "type": "string"
            }
          }
       }
    }

假设有如下2条数据存入

    {
      "name": "geli kongtiao",
      "price": 1999.0,
      "service_period": "one year"
    }

    {
      "name": "aozhou dalongxia",
      "price": 199.0,
      "eat_period": "one week"
    }

在底层的存储是这样子的

    {
      "_type": "elactronic_goods",
      "name": "geli kongtiao",
      "price": 1999.0,
      "service_period": "one year",
      "eat_period": ""
    }

    {
      "_type": "fresh_goods",
      "name": "aozhou dalongxia",
      "price": 199.0,
      "service_period": "",
      "eat_period": "one week"
    }

如果存入数据没有某个filed时，将会存入一个空值；假如说，将两个type的field完全不同，放在一个index下，那么就每条数据都至少有一半的field在底层的lucene中是空值，会有严重的性能问题；
因此在es7中一个index只能有一个type，默认为_doc，不推荐去自定义了。
    

### 6.4 mapping root object剖析 

#### root object

就是某个type对应的mapping json，包括了properties，metadata（_id，_source，_type），settings（analyzer），其他settings（比如include_in_all）

    PUT /index_demo
    {
      "mappings": {
        "properties": {
          
        }
      }
    }

#### properties

type，index，analyzer

    PUT /index_demo/_mapping
    {
      "properties": {
        "title": {
          "type": "text"
        }
      }
    }

#### _source

优点：

1. 查询的时候，直接可以拿到完整的document，不需要先拿document id，再发送一次请求拿document
2. partial update基于_source实现
3. reindex时，直接基于_source实现，不需要从数据库（或者其他外部存储）查询数据再修改
4. 可以基于_source定制返回field
5. debug query更容易，因为可以直接看到_source

如果不需要上述好处，可以禁用_source；但是不建议这么做[官方说明](https://www.elastic.co/guide/en/elasticsearch/reference/current/mapping-source-field.html#disable-source-field)

    PUT /index_demo
    {
      "mappings": {
        "_source": {"enabled": false}
      }
    }

#### 标识性metadata

_index，_type，_id

### 6.5 定制化自己的dynamic mapping

#### 定制dynamic策略

    true：遇到陌生字段，就进行dynamic mapping
    false：遇到陌生字段，就忽略
    strict：遇到陌生字段，就报错

示例：

    PUT /index_demo
    {
      "mappings": {
        "dynamic":"strict",
        "properties": {
          "title":{
            "type": "text"
          },
          "address":{
            "type": "object",
            "dynamic": true
          }
        }
      }
    }

测试数据添加是否可以成功    
    
    PUT /index_demo/_doc/1
    {
      "title":"this is firestone",
      "content":"this is content",
      "address":{
        "province":"北京",
        "city":"北京"
      }
    }

由于做了现在，因此上面这个会添加失败
    
    PUT /index_demo/_doc/1
    {
      "title":"this is firestone",
      "address":{
        "province":"北京",
        "city":"北京"
      }
    }


   
   
#### 定制dynamic mapping策略

默认会按照一定格式识别date，比如yyyy-MM-dd。但是如果某个field先过来一个2017-01-01的值，就会被自动dynamic mapping成date，
后面如果再来一个"hello world"之类的值，就会报错。可以手动关闭某个type的date_detection，如果有需要，自己手动指定某个field为date类型。

    PUT /index_demo/_mapping
    {
        "date_detection": false
    }
    
    
#### 定制自己的dynamic mapping template

    PUT /index_demo
    {
      "mappings": {
        "dynamic_templates": [
          {
            "en": {
              "match": "*_en",
              "match_mapping_type": "string",
              "mapping": {
                "type": "text",
                "analyzer": "english"
              }
            }
          }
        ]
      }
    }

测试   
   
    PUT index_demo/_doc/1
    {
     "title":"this is my first article"
    }
    
    PUT index_demo/_doc/2
    {
     "title_en":"this is my first article"
    }
    
    GET /index_demo/_search
    {
      "query":{
        "match": {
          "title": "is"
        }
      }
    }

title没有匹配到任何的dynamic模板，默认就是standard分词器，不会过滤停用词，is会进入倒排索引，用is来搜索是可以搜索到的；
title_en匹配到了dynamic模板，就是english分词器，会过滤停用词，is这种停用词就会被过滤掉，用is来搜索就搜索不到了；
    
    
### 6.6 基于scoll+bulk+索引别名实现零停机重建索引

#### 重建索引

一个field的设置是不能被修改的，如果要修改一个Field，那么应该重新按照新的mapping，建立一个index，然后将数据批量查询出来，重新用bulk api写入index中；
批量查询的时候，建议采用scroll api，并且采用多线程并发的方式来reindex数据，每次scoll就查询指定日期的一段数据，交给一个线程即可；

举个例子：

（1）一开始，依靠dynamic mapping，插入数据，但是不小心有些数据是2017-01-01这种日期格式的，所以title这种field被自动映射为了date类型，实际上它应该是string类型的。

    PUT /index_demo/_doc/1
    {
      "title":"2020-01-01"
    }
    
    PUT /index_demo/_doc/2
    {
      "title":"2020-01-02"
    }

（2）当后期向索引中加入string类型的title值的时候，就会报错。

    PUT /index_demo/_doc/3
    {
      "title":"es 入门"
    }

（3）如果此时想修改title的类型，是不可能的

    PUT /index_demo/_mapping
    {
      "properties":{
        "title":{
          "type":"text"
        }
      }
    }

（4）此时，唯一的办法，就是进行reindex，也就是说，重新建立一个索引，将旧索引的数据查询出来，再导入新索引

（5）如果旧索引的名字是old_index，新索引的名字是new_index，终端java应用，已经在使用old_index在操作了，难道还要去停止java应用，修改使用的index为new_index，才重新启动java应用吗？这个过程中，就会导致java应用停机，可用性降低

（6）所以说，给java应用一个别名，这个别名是指向旧索引的，java应用先用着，java应用先用goods_index alias来操作，此时实际指向的是旧的my_index

    PUT /index_demo/_alias/goods_index

（7）新建一个index，调整其title的类型为string

    PUT /index_demo_new
    {
      "mappings": {
        "properties": {
          "title":{
            "type":"text"
          }
        }
      }
    }

（8）使用scroll api将数据批量查询出来

    GET /index_demo/_search?scroll=1m
    {
      "query": {
        "match_all": {}
      },
      "sort": ["_doc"],
      "size": 1
    }

（9）采用bulk api将scoll查出来的一批数据，批量写入新索引

    POST /_bulk
    {"index":{"_index":"index_demo_new", "_id":"1"}}
    {"title":"2020-01-01"}

（10）反复循环8~9，查询一批又一批的数据出来，采取bulk api将每一批数据批量写入新索引

（11）将goods_index alias切换到my_index_new上去，java应用会直接通过index别名使用新的索引中的数据，java应用程序不需要停机，零提交，高可用

    POST /_aliases
    {
      "actions": [
        {
          "remove": {
            "index": "index_demo",
            "alias": "goods_index"
          }
        },
        {
          "add": {
            "index": "index_demo_new",
            "alias": "goods_index"
          }
        }
      ]
    }

（12）直接通过goods_index别名来查询，是否ok
    

## 七、内核原理

### 7.1 倒排索引组成结构以及其索引不可变原因

倒排索引，是适合用于进行搜索的

倒排索引的结构：

1. 包含这个关键词的document list
2. 包含这个关键词的所有document的数量：IDF（inverse document frequency）
3. 这个关键词在每个document中出现的次数：TF（term frequency）
4. 这个关键词在这个document中的次序
5. 每个document的长度：length norm
6. 包含这个关键词的所有document的平均长度


    word		doc1		doc2
    
    dog		     *		     *
    hello		 *
    you				         *

倒排索引不可变的好处

1. 不需要锁，提升并发能力，避免锁的问题
2. 数据不变，一直保存在os cache中，只要cache内存足够
3. filter cache一直驻留在内存，因为数据不变
4. 可以压缩，节省cpu和io开销

倒排索引不可变的坏处：每次都要重新构建整个索引

### 7.2 图解剖析document写入原理（buffer，segment，commit）

#### 基本流程

![](./image/document写入原理.png)

1. 数据写入buffer
2. commit point
3. buffer中的数据写入新的index segment
4. 等待在os cache中的index segment被fsync强制刷到磁盘上
5. 新的index sgement被打开，供search使用
6. buffer被清空

每次commit point时，会有一个.del文件，标记了哪些segment中的哪些document被标记为deleted;
搜索的时候，会依次查询所有的segment，从旧的到新的，比如被修改过的document，在旧的segment中，会标记为deleted，在新的segment中会有其新的数据

#### 优化后的流程
在基础流程中通常写入磁盘是比较耗时，因此无法实现NTR近实时的查询。主要瓶颈在于fsync实际发生磁盘IO写数据进磁盘，是很耗时的。

写入流程别改进如下：

（1）数据写入buffer
（2）每隔一定时间，buffer中的数据被写入segment文件，但是先写入os cache
（3）只要segment写入os cache，那就直接打开供search使用，不立即执行commit

数据写入os cache，并被打开供搜索的过程，叫做refresh，默认是每隔1秒refresh一次。
也就是说，每隔一秒就会将buffer中的数据写入一个新的index segment file，先写入os cache中。
所以，es是近实时的，数据写入到可以被搜索，默认是1秒。

`POST /index_demo/_refresh`，可以手动refresh，一般不需要手动执行，没必要，让es自己搞就可以了

比如现在的时效性要求，比较低，只要求一条数据写入es，一分钟以后才让我们搜索到就可以了，那么就可以调整refresh interval

    PUT /index_demo
    {
      "settings": {
        "refresh_interval": "30s" 
      }
    }

#### 最终优化流程

![](./image/document写入原理最终版.png)

1. 数据写入buffer缓冲和translog日志文件
2. 每隔一秒钟，buffer中的数据被写入新的segment file，并进入os cache，此时segment被打开并供search使用
3. buffer被清空
4. 重复1~3，新的segment不断添加，buffer不断被清空，而translog中的数据不断累加
5. 当translog长度达到一定程度的时候，commit操作发生

5-1. buffer中的所有数据写入一个新的segment，并写入os cache，打开供使用
5-2. buffer被清空
5-3. 一个commit ponit被写入磁盘，标明了所有的index segment
5-4. filesystem cache中的所有index segment file缓存数据，被fsync强行刷到磁盘上
5-5. 现有的translog被清空，创建一个新的translog

#### 基于translog和commit point，如何进行数据恢复

fsync+清空translog，就是flush，默认每隔30分钟flush一次，或者当translog过大的时候，也会flush

`POST /index_demo/_flush`，一般来说别手动flush，让它自动执行就可以了

translog，每隔5秒被fsync一次到磁盘上。在一次增删改操作之后，当fsync在primary shard和replica shard都成功之后，那次增删改操作才会成功

但是这种在一次增删改时强行fsync translog可能会导致部分操作比较耗时，也可以允许部分数据丢失，设置异步fsync translog

    PUT /index_demo/_settings
    {
        "index.translog.durability": "async",
        "index.translog.sync_interval": "5s"
    }

#### 最后优化写入流程实现海量磁盘文件合并（segment merge，optimize）
每秒一个segment file，文件过多，而且每次search都要搜索所有的segment，很耗时

默认会在后台执行segment merge操作，在merge的时候，被标记为deleted的document也会被彻底物理删除

每次merge操作的执行流程

1. 选择一些有相似大小的segment，merge成一个大的segment
2. 将新的segment flush到磁盘上去
3. 写一个新的commit point，包括了新的segment，并且排除旧的那些segment
4. 将新的segment打开供搜索
5. 将旧的segment删除

`POST /index_demo/_optimize?max_num_segments=1`，尽量不要手动执行，让它自动默认执行就可以了

## 八、Java API初步使用

### CRUD

#### 老版本（下面的方法都是过期的，在es8开始将会被移除）

引入maven依赖：

    <dependency>
        <groupId>org.elasticsearch.client</groupId>
        <artifactId>transport</artifactId>
        <version>7.8.1</version>
    </dependency>

添加日志依赖（可选）：

    <dependency>
        <groupId>org.apache.logging.log4j</groupId>
        <artifactId>log4j-api</artifactId>
        <version>2.13.3</version>
    </dependency>
    <dependency>
        <groupId>org.apache.logging.log4j</groupId>
        <artifactId>log4j-core</artifactId>
        <version>2.13.3</version>
    </dependency>

代码测试

    public static void main(String[] args) throws Exception {

        // 构建client
        Settings settings = Settings.builder()
                .put("cluster.name", "docker-cluster")
                .build();
        TransportClient client = new PreBuiltTransportClient(settings)
                .addTransportAddress(new TransportAddress(InetAddress.getByName("192.168.111.40"), 9300));

        //addDoc(client);
        //getDoc(client);
        //updateDoc(client);
        delDoc(client);
        
        client.close();
    }

    /**
     * 添加
     */
    public static void addDoc(TransportClient client) throws IOException {
        IndexResponse response = client.prepareIndex("employee", "_doc", "1")
                .setSource(XContentFactory.jsonBuilder()
                        .startObject()
                        .field("user", "tom")
                        .field("age", 18)
                        .field("position", "scientist")
                        .field("country", "China")
                        .field("join_data", "2020-01-01")
                        .field("salary", 10000)
                        .endObject())
                .get();
        System.out.println(response.getResult());
    }

    /**
     * 查询
     */
    public static void getDoc(TransportClient client){
        GetResponse documentFields = client.prepareGet("employee", "_doc", "1").get();
        System.out.println(documentFields.getSourceAsString());
    }

    /**
     * 更新
     */
    public static void updateDoc(TransportClient client) throws IOException {
        UpdateResponse response = client.prepareUpdate("employee", "_doc", "1")
                .setDoc(XContentFactory.jsonBuilder()
                        .startObject()
                        .field("salary", 1000000)
                        .endObject())
                .get();
        System.out.println(response.getResult());
    }

    /**
     * 删除
     */
    public static void delDoc(TransportClient client){
        DeleteResponse response = client.prepareDelete("employee", "_doc", "1").get();
        System.out.println(response);
    }
    
    /***
     * 查询职位中包含scientist，并且年龄在28到40岁之间
     */
    public static void search(TransportClient client){
        SearchResponse response = client.prepareSearch("employee")
                .setQuery(QueryBuilders.boolQuery().must(QueryBuilders.matchQuery("position", "scientist"))
                        .filter(QueryBuilders.rangeQuery("age").gte(28).lte(40))).setFrom(0).setSize(2).get();
        System.out.println(response);
    }
    
    /***
     * 聚合查询(需要重建mapping)
     */
    public static void search2(TransportClient client){
        SearchResponse response = client.prepareSearch("employee")
                .addAggregation(AggregationBuilders.terms("group_by_country")
                        .field("country")
                        .subAggregation(AggregationBuilders.dateHistogram("group_by_join_date")
                                .field("joinDate")
                                .dateHistogramInterval(DateHistogramInterval.YEAR)
                                .subAggregation(AggregationBuilders.avg("avg_salary").field("salary")))
                ).execute().actionGet();

        System.out.println(response);
    }

> 重建mapping语句：

    PUT /employee
    {
      "mappings": {
        "properties": {
          "age": {
            "type": "long"
          },
          "country": {
            "type": "text",
            "fields": {
              "keyword": {
                "type": "keyword",
                "ignore_above": 256
              }
            },
            "fielddata": true
          },
          "joinData": {
            "type": "date"
          },
          "name": {
            "type": "text",
            "fields": {
              "keyword": {
                "type": "keyword",
                "ignore_above": 256
              }
            }
          },
          "position": {
            "type": "text",
            "fields": {
              "keyword": {
                "type": "keyword",
                "ignore_above": 256
              }
            }
          },
          "salary": {
            "type": "long"
          }
        }
      }
    }


#### 新版本

添加maven依赖：

    <dependency>
        <groupId>org.elasticsearch.client</groupId>
        <artifactId>elasticsearch-rest-high-level-client</artifactId>
        <version>7.8.1</version>
    </dependency>

代码测试

    public static void main(String[] args) throws IOException {
        HttpHost[] httpHost = {HttpHost.create("192.168.111.40:9200")};
        RestHighLevelClient restHighLevelClient = new RestHighLevelClient(RestClient.builder(httpHost));
        // addDoc(restHighLevelClient);
        // getDoc(restHighLevelClient);
        // updateDoc(restHighLevelClient);
        delDoc(restHighLevelClient);

        restHighLevelClient.close();
    }

    /**
     * 添加
     */
    public static void addDoc(RestHighLevelClient client) throws IOException {
        IndexRequest request = new IndexRequest("employee");
        request.id("1");
        request.source(XContentFactory.jsonBuilder()
                .startObject()
                .field("user", "tom")
                .field("age", 18)
                .field("position", "scientist")
                .field("country", "China")
                .field("join_data", "2020-01-01")
                .field("salary", 10000)
                .endObject());
        IndexResponse response = client.index(request, RequestOptions.DEFAULT);
        System.out.println(response.getResult());
    }

    /**
     * 查询
     */
    public static void getDoc(RestHighLevelClient client) throws IOException {
        // 通过ID来查询
        GetRequest request = new GetRequest("employee","1");
        GetResponse response = client.get(request, RequestOptions.DEFAULT);
        // 更丰富的查询条件
        /// SearchRequest searchRequest = new SearchRequest();
        /// client.search(searchRequest, RequestOptions.DEFAULT);

        System.out.println(response.getSourceAsString());
    }

    /**
     * 更新
     */
    public static void updateDoc(RestHighLevelClient client) throws IOException {
        UpdateRequest request = new UpdateRequest("employee", "1");
        request.doc(XContentFactory.jsonBuilder()
                .startObject()
                .field("salary", 1000000)
                .endObject());
        UpdateResponse response = client.update(request, RequestOptions.DEFAULT);
        System.out.println(response.getResult());
    }

    /**
     * 删除
     */
    public static void delDoc(RestHighLevelClient client) throws IOException {
        DeleteRequest request = new DeleteRequest("employee", "1");
        DeleteResponse response = client.delete(request, RequestOptions.DEFAULT);
        System.out.println(response);
    }  
    
     /**
     * 查询职位中包含scientist，并且年龄在28到40岁之间
     */
     public static void search(RestHighLevelClient client) throws IOException {
        SearchRequest request = new SearchRequest("employee");
        request.source(SearchSourceBuilder.searchSource()
                .query(QueryBuilders.boolQuery()
                        .must(QueryBuilders.matchQuery("position", "scientist"))
                        .filter(QueryBuilders.rangeQuery("age").gte("28").lte("28"))
                ).from(0).size(2)
        );
        SearchResponse search = client.search(request, RequestOptions.DEFAULT);
        System.out.println(JSONObject.toJSONString(search.getHits()));
     }

## 九、深度探索搜索技术

### 9.1 使用term filter来搜索数据

#### 准备测试数据

    POST /forum/_bulk
    { "index": { "_id": 1 }}
    { "articleID" : "XHDK-A-1293-#fJ3", "userID" : 1, "hidden": false, "postDate": "2020-09-09" }
    { "index": { "_id": 2 }}
    { "articleID" : "KDKE-B-9947-#kL5", "userID" : 1, "hidden": false, "postDate": "2020-09-10" }
    { "index": { "_id": 3 }}
    { "articleID" : "JODL-X-1937-#pV7", "userID" : 2, "hidden": false, "postDate": "2020-09-09" }
    { "index": { "_id": 4 }}
    { "articleID" : "QQPX-R-3956-#aD8", "userID" : 2, "hidden": true, "postDate": "2020-09-10" }

#### 查看mapping

    GET /forum/_mapping

查询结果：
    
    {
      "forum": {
        "mappings": {
          "article": {
            "properties": {
              "articleID": {
                "type": "text",
                "fields": {
                  "keyword": {
                    "type": "keyword",
                    "ignore_above": 256
                  }
                }
              },
              "hidden": {
                "type": "boolean"
              },
              "postDate": {
                "type": "date"
              },
              "userID": {
                "type": "long"
              }
            }
          }
        }
      }
    }
    
type=text，默认会设置两个field，一个是field本身，比如articleID，就是分词的；还有一个的就是field.keyword，articleID.keyword，默认不分词，会最多保留256个字符

#### 根据用户ID搜索帖子

    GET /forum/_search
    {
        "query" : {
            "constant_score" : { 
                "filter" : {
                    "term" : { 
                        "userID" : 1
                    }
                }
            }
        }
    }
    
term filter/query：对搜索文本不分词，直接拿去倒排索引中匹配，你输入的是什么，就去匹配什么；
比如如果对搜索文本进行分词的话，“helle world” --> 直接去倒排索引中匹配“hello world”；而不会去分词后再匹配。

#### 搜索没有隐藏的帖子

    GET /forum/_search
    {
        "query" : {
            "constant_score" : { 
                "filter" : {
                    "term" : { 
                        "hidden" : false
                    }
                }
            }
        }
    }

#### 根据发帖日期搜索帖子

    GET /forum/_search
    {
        "query" : {
            "constant_score" : { 
                "filter" : {
                    "term" : { 
                        "postDate" : "2020-09-09"
                    }
                }
            }
        }
    }

#### 根据帖子ID搜索帖子

    GET /forum/_search
    {
        "query" : {
            "constant_score" : { 
                "filter" : {
                    "term" : { 
                        "articleID" : "XHDK-A-1293-#fJ3"
                    }
                }
            }
        }
    }

上面那个查询不得任何结果

    GET /forum/_search
    {
        "query" : {
            "constant_score" : { 
                "filter" : {
                    "term" : { 
                        "articleID.keyword" : "XHDK-A-1293-#fJ3"
                    }
                }
            }
        }
    }

第一个为什么查询不到结果？前面讲了对应类型是text的es会建立2次索引，一个是分词一个不分词（在keyword中）；
使用term 进行查询时不会对其进行分词就开始查询，此时直接通过字段查询是取匹配分词的倒排索引自然也就匹配不到了；因此需要使用articleID.keyword去匹配。

> articleID.keyword，是es最新版本内置建立的field，就是不分词的。所以一个articleID过来的时候，会建立两次索引，一次是自己本身，是要分词的，分词后放入倒排索引；
> 另外一次是基于articleID.keyword，不分词，最多保留256个字符，直接一个字符串放入倒排索引中。
> term filter，对text过滤，可以考虑使用内置的field.keyword来进行匹配。但是有个问题，默认就保留256个字符，如果超过了就GG了。
> 所以尽可能还是自己去手动建立索引，指定not_analyzed。在最新版本的es中，不需要指定not_analyzed也可以，将type设为keyword即可。

#### 查看分词

    GET /forum/_analyze
    {
      "field": "articleID",
      "text": "XHDK-A-1293-#fJ3"
    }

默认是analyzed的text类型的field，建立倒排索引的时候，会对所有的articleID分词，分词以后，原本的articleID就没有了，只有分词后的各个word存在于倒排索引中。
term，是不对搜索文本分词的，但是articleID建立索引为 xhdk，a，1293，fj3，自然直接搜索也就没得结果了。

#### 重建索引

    DELETE /forum
    
    PUT /forum
    {
      "mappings": {
          "properties": {
            "articleID": {
              "type": "keyword"
            }
          }
        }
    }
执行上面的初始化数据语句，再次直接查询即可查询到结果

> term filter：根据exact value进行搜索，数字、boolean、date天然支持
> 相当于SQL中的单个where条件

### 9.2 filter执行原理深度剖析（bitset机制与caching机制）

* （1）在倒排索引中查找搜索串，获取document list；
* （2）为每个在倒排索引中搜索到的结果（doc list），构建一个bitset，就是一个二进制的数组，数组每个元素都是0或1，用来标识一个doc对一个filter条件是否匹配，如果匹配就是1，不匹配就是0，类似这样：[0, 0, 0, 1, 0, 1]；
这样尽可能用简单数据结构去实现复杂的功能，可以节省内存空间，提升性能；
* （3）遍历每个过滤条件对应的bitset，优先从最稀疏的开始搜索，查找满足所有条件的document

> 一次性其实可以在一个search请求中，发出多个filter条件，每个filter条件都会对应一个bitset；
遍历每个filter条件对应的bitset，先从最稀疏的开始遍历

    [0, 0, 0, 1, 0, 0]：比较稀疏
    [0, 1, 0, 1, 0, 1]

先遍历比较稀疏的bitset，就可以先过滤掉尽可能多的数据；

遍历所有的bitset，找到匹配所有filter条件的doc；就可以将document作为结果返回给client了

* （4）caching bitset，跟踪query，在最近256个query中超过一定次数的过滤条件，缓存其bitset。对于小segment（<1000，或<3%），不缓存bitset。

比如条件为postDate=2017-01-01，生成的bitset为[0, 0, 1, 1, 0, 0]，可以缓存在内存中，这样下次如果再有这个条件过来的时候，就不用重新扫描倒排索引，反复生成bitset，可以大幅度提升性能。

在最近的256个filter中，有某个filter超过了一定的次数，这个次数不固定，就会自动缓存这个filter对应的bitset。

segment（分片），filter针对小segment获取到的结果，可以不缓存，segment记录数<1000，或者segment大小<index总大小的3%。

segment数据量很小时，哪怕是扫描也很快；同时segment会在后台自动合并，小segment很快就会跟其他小segment合并成大segment，此时缓存也没有什么意义，因为这些小segment合并后很快就消失了。

filter比query的好处就在于会caching，实际上并不是一个filter返回的完整的doc list数据结果。而是filter bitset缓存完整的doc list数据结果。下次不用扫描倒排索引了。

* （5）filter大部分情况下来说，在query之前执行，先尽量过滤掉尽可能多的数据

query：是会计算doc对搜索条件的relevance score，还会根据这个score去排序

filter：只是简单过滤出想要的数据，不计算relevance score，也不排序

* （6）如果document有新增或修改，那么cached bitset会被自动更新；
即当document有新增或修改时，会自动更新到相关filter的bitset中缓存中。

* （7）以后只要是有相同的filter条件的，会直接来使用这个过滤条件对应的cached bitset即可快速将数据过滤出来返回。

### 9.3 基于bool组合多个filter条件来搜索数据

bool中可以通过must，must_not，should来组合多个过滤条件；bool可以嵌套,类似SQL中的and

#### 搜索发帖日期为2020-09-09，或者帖子ID为XHDK-A-1293-#fJ3的帖子，同时要求帖子的发帖日期绝对不为2020-09-09

类似SQL如下：

    SELECT
        * 
    FROM
        forum.article 
    WHERE
        ( post_date = '2020-09-09' OR article_id = 'XHDK-A-1293-#fJ3' ) 
        AND post_date != '2020-09-10'

es查询语句

    GET /forum/_search
    {
      "query": {
        "constant_score": {
          "filter": {
            "bool": {
              "should": [
                {"term": { "postDate": "2020-09-09" }},
                {"term": {"articleID": "XHDK-A-1293-#fJ3"}}
              ],
              "must_not": {
                "term": {
                  "postDate": "2020-09-10"
                }
              }
            }
          }
        }
      }
    }

> must 必须匹配 ，should 可以匹配其中任意一个即可，must_not 必须不匹配

#### 搜索帖子ID为XHDK-A-1293-#fJ3，或者是帖子ID为JODL-X-1937-#pV7而且发帖日期为2020-09-09的帖子

    GET /forum/_search 
    {
      "query": {
        "constant_score": {
          "filter": {
            "bool": {
              "should": [
                {
                  "term": {
                    "articleID": "XHDK-A-1293-#fJ3"
                  }
                },
                {
                  "bool": {
                    "must": [
                      {
                        "term":{
                          "articleID": "JODL-X-1937-#pV7"
                        }
                      },
                      {
                        "term": {
                          "postDate": "2020-09-09"
                        }
                      }
                    ]
                  }
                }
              ]
            }
          }
        }
      }
    }

### 9.4 使用terms搜索多个值以及多值搜索结果优化

    term: {"field": "value"}
    terms: {"field": ["value1", "value2"]}

sql中的in

    select * from tbl where col in ("value1", "value2")

#### 为帖子数据增加tag字段

    POST /forum/_bulk
    { "update": { "_id": "1"} }
    { "doc" : {"tag" : ["java", "hadoop"]} }
    { "update": { "_id": "2"} }
    { "doc" : {"tag" : ["java"]} }
    { "update": { "_id": "3"} }
    { "doc" : {"tag" : ["hadoop"]} }
    { "update": { "_id": "4"} }
    { "doc" : {"tag" : ["java", "elasticsearch"]} }

#### 搜索articleID为KDKE-B-9947-#kL5或QQPX-R-3956-#aD8的帖子，

    GET /forum/_search 
    {
      "query": {
        "constant_score": {
          "filter": {
            "terms": {
              "articleID": [
                "KDKE-B-9947-#kL5",
                "QQPX-R-3956-#aD8"
              ]
            }
          }
        }
      }
    }
    
#### 搜索tag中包含java的帖子

    GET /forum/_search
    {
        "query" : {
            "constant_score" : {
                "filter" : {
                    "terms" : { 
                        "tag" : ["java"]
                    }
                }
            }
        }
    }

#### 优化搜索结果，仅仅搜索tag只包含java的帖子

现有的数据结构无法完成要求，因此我们添加一个标识字段

    POST /forum/_bulk
    { "update": { "_id": "1"} }
    { "doc" : {"tag_cnt" : 2} }
    { "update": { "_id": "2"} }
    { "doc" : {"tag_cnt" : 1} }
    { "update": { "_id": "3"} }
    { "doc" : {"tag_cnt" : 1} }
    { "update": { "_id": "4"} }
    { "doc" : {"tag_cnt" : 2} }

执行查询语句
    
    GET /forum/_search
    {
      "query": {
        "constant_score": {
          "filter": {
            "bool": {
              "must": [
                {
                  "term": {
                    "tag_cnt": 1
                  }
                },
                {
                  "terms": {
                    "tag": ["java"]
                  }
                }
              ]
            }
          }
        }
      }
    }    

### 9.5 基于range filter来进行范围过滤

#### 为帖子数据增加浏览量的字段

    POST /forum/_bulk
    { "update": { "_id": "1"} }
    { "doc" : {"view_cnt" : 30} }
    { "update": { "_id": "2"} }
    { "doc" : {"view_cnt" : 50} }
    { "update": { "_id": "3"} }
    { "doc" : {"view_cnt" : 100} }
    { "update": { "_id": "4"} }
    { "doc" : {"view_cnt" : 80} }

#### 搜索浏览量在30~60之间的帖子

    GET /forum/_search
    {
      "query": {
        "constant_score": {
          "filter": {
            "range": {
              "view_cnt": {
                "gt": 30,
                "lt": 60
              }
            }
          }
        }
      }
    }

#### 搜索发帖日期在最近1个月的帖子

准备示例数据

    POST /forum/_bulk
    { "index": { "_id": 5 }}
    { "articleID" : "DHJK-B-1395-#Ky5", "userID" : 3, "hidden": false, "postDate": "2020-10-01", "tag": ["elasticsearch"], "tag_cnt": 1, "view_cnt": 10 }

执行查询语句

    GET /forum/_search 
    {
      "query": {
        "constant_score": {
          "filter": {
            "range": {
              "postDate": {
                "lt": "2020-10-10||-30d"
              }
            }
          }
        }
      }
    }

    GET /forum/_search 
    {
      "query": {
        "constant_score": {
          "filter": {
            "range": {
              "postDate": {
                "gt": "now-30d"
              }
            }
          }
        }
      }
    }

range相当于sql中的between，做范围过滤

### 9.6 手动控制全文检索结果的精准度

全文检索的时候，进行多个值的检索，有两种做法，match query；should；

控制搜索结果精准度：and operator，minimum_should_match

#### 为帖子数据增加标题字段

    POST /forum/_bulk
    { "update": { "_id": "1"} }
    { "doc" : {"title" : "this is java and elasticsearch blog"} }
    { "update": { "_id": "2"} }
    { "doc" : {"title" : "this is java blog"} }
    { "update": { "_id": "3"} }
    { "doc" : {"title" : "this is elasticsearch blog"} }
    { "update": { "_id": "4"} }
    { "doc" : {"title" : "this is java, elasticsearch, hadoop blog"} }
    { "update": { "_id": "5"} }
    { "doc" : {"title" : "this is spark blog"} }

#### 搜索标题中包含java或elasticsearch的blog

这个和之前的那个term query不一样。不是搜索exact value，是进行全文检索（full text）。
负责进行全文检索的是match query。当然，如果要检索的field，是not_analyzed类型的，那么match query也相当于term query。

    GET /forum/_search
    {
        "query": {
            "match": {
                "title": "java elasticsearch"
            }
        }
    }

#### 搜索标题中包含java和elasticsearch的

搜索结果精准控制的第一步：灵活使用and关键字，如果你是希望所有的搜索关键字都要匹配的，那么就用and，可以实现单纯match query无法实现的效果。

    GET /forum/_search
    {
      "query": {
        "match": {
          "title": {
            "query": "java elasticsearch",
            "operator": "and"
          }
        }
      }
    }
    

#### 搜索包含java，elasticsearch，spark，hadoop，4个关键字中，至少3个

控制搜索结果的精准度的第二步：指定一些关键字中，必须至少匹配其中的多少个关键字，才能作为结果返回

    GET /forum/_search
    {
      "query": {
        "match": {
          "title": {
            "query": "java elasticsearch spark hadoop",
            "minimum_should_match": "75%"
          }
        }
      }
    }

#### 用bool组合多个搜索条件，来搜索title

    GET /forum/_search
    {
      "query": {
        "bool": {
          "must": {
            "match": {
              "title": "java"
            }
          },
          "must_not": {
            "match": {
              "title": "spark"
            }
          },
          "should": [
            {
              "match": {
                "title": "hadoop"
              }
            },
            {
              "match": {
                "title": "elasticsearch"
              }
            }
          ]
        }
      }
    }    

#### bool组合多个搜索条件，如何计算relevance score？

must和should搜索对应的分数，加起来，除以must和should的总数

排名第一：java，同时包含should中所有的关键字，hadoop，elasticsearch
排名第二：java，同时包含should中的elasticsearch
排名第三：java，不包含should中的任何关键字

should是可以影响相关度分数的

must是确保谁必须有这个关键字，同时会根据这个must的条件去计算出document对这个搜索条件的relevance score
在满足must的基础之上，should中的条件，不匹配也可以，但是如果匹配的更多，那么document的relevance score就会更高

#### 搜索java，hadoop，spark，elasticsearch，至少包含其中3个关键字

默认情况下，should是可以不匹配任何一个的，比如上面的搜索中，this is java blog，就不匹配任何一个should条件
但是有个例外的情况，如果没有must的话，那么should中必须至少匹配一个才可以
比如下面的搜索，should中有4个条件，默认情况下，只要满足其中一个条件，就可以匹配作为结果返回

但是可以精准控制，should的4个条件中，至少匹配几个才能作为结果返回

    GET /forum/_search
    {
      "query": {
        "bool": {
          "should": [
            {
              "match": {
                "title": "java"
              }
            },
            {
              "match": {
                "title": "elasticsearch"
              }
            },
            {
              "match": {
                "title": "hadoop"
              }
            },
            {
              "match": {
                "title": "spark"
              }
            }
          ],
          "minimum_should_match": 3
        }
      }
    }


    

### 9.7 基于term+bool实现的multiword搜索底层原理剖析

#### 普通match如何转换为term+should

    {
        "match": { "title": "java elasticsearch"}
    }

使用诸如上面的match query进行多值搜索的时候，es会在底层自动将这个match query转换为bool的语法。
bool should，指定多个搜索词，同时使用term query

    {
      "bool": {
        "should": [
          { "term": { "title": "java" }},
          { "term": { "title": "elasticsearch"   }}
        ]
      }
    }

#### and match如何转换为term+must

    {
        "match": {
            "title": {
                "query":    "java elasticsearch",
                "operator": "and"
            }
        }
    }

转化为：

    {
      "bool": {
        "must": [
          { "term": { "title": "java" }},
          { "term": { "title": "elasticsearch"   }}
        ]
      }
    }

#### minimum_should_match如何转换
    
    {
        "match": {
            "title": {
                "query": "java elasticsearch hadoop spark",
                "minimum_should_match": "75%"
            }
        }
    }

转化为

    {
      "bool": {
        "should": [
          { "term": { "title": "java" }},
          { "term": { "title": "elasticsearch"   }},
          { "term": { "title": "hadoop" }},
          { "term": { "title": "spark" }}
        ],
        "minimum_should_match": 3 
      }
    }

### 9.8 基于boost的细粒度搜索条件权重控制

##### 需求：

搜索标题中包含java的帖子，同时呢，如果标题中包含hadoop或elasticsearch就优先搜索出来，
同时呢，如果一个帖子包含java hadoop，一个帖子包含java elasticsearch，包含hadoop的帖子要比elasticsearch优先搜索出来

##### 知识点：

搜索条件的权重，boost，可以将某个搜索条件的权重加大，此时当匹配这个搜索条件和匹配另一个搜索条件的document，
计算relevance score时，匹配权重更大的搜索条件的document，relevance score会更高，当然也就会优先被返回回来。
默认情况下，搜索条件的权重是相同的，都是1

    GET /forum/_search 
    {
      "query": {
        "bool": {
          "must": [
            {
              "match": {
                "title": "java"
              }
            }
          ],
          "should": [
            {
              "match": {
                "title": {
                  "query": "elasticsearch"
                }
              }
            },
            {
              "match": {
                "title": {
                  "query": "hadoop",
                  "boost": 5
                }
              }
            }
          ]
        }
      }
    }

### 9.9 多shard场景下relevance score不准确问题

#### 多shard场景下relevance score不准确问题

如果你的一个index有多个shard的话，可能搜索结果会不准确
![](./image/多shard场景下relevance%20score不准确问题.png)

#### 如何解决该问题？
* （1）生产环境下，数据量大，尽可能实现均匀分配

数据量很大的话，其实一般情况下，在概率学的背景下，es都是在多个shard中均匀路由数据的，路由的时候根据_id，负载均衡
比如说有10个document，title都包含java，一共有5个shard，那么在概率学的背景下，如果负载均衡的话，其实每个shard都应该有2个doc，title包含java
如果数据分布均匀的话，其实就没有刚才说的那个问题了

* （2）测试环境下，将索引的primary shard设置为1个，number_of_shards=1，index settings

如果只有一个shard，所有的document都在这个shard里面，也就没有这个问题了

* （3）测试环境下，搜索附带search_type=dfs_query_then_fetch参数，会将local IDF取出来计算global IDF

计算一个doc的相关度分数的时候，就会将所有shard对local IDF计算一下获取出来，然后在本地进行global IDF分数的计算，之后将所有shard的doc作为上下文来进行计算，也能确保准确性。
但是production生产环境下，不推荐这个参数，因为性能很差。

### 9.10 基于dis_max实现best fields策略进行多字段搜索

#### 为帖子数据增加content字段

    POST /forum/_bulk
    { "update": { "_id": "1"} }
    { "doc" : {"content" : "i like to write best elasticsearch article"} }
    { "update": { "_id": "2"} }
    { "doc" : {"content" : "i think java is the best programming language"} }
    { "update": { "_id": "3"} }
    { "doc" : {"content" : "i am only an elasticsearch beginner"} }
    { "update": { "_id": "4"} }
    { "doc" : {"content" : "elasticsearch and hadoop are all very good solution, i am a beginner"} }
    { "update": { "_id": "5"} }
    { "doc" : {"content" : "spark is best big data solution based on scala ,an programming language similar to java"} }

#### 搜索title或content中包含java或solution的帖子

    GET /forum/_search
    {
        "query": {
            "bool": {
                "should": [
                    { "match": { "title": "java solution" }},
                    { "match": { "content":  "java solution" }}
                ]
            }
        }
    }

#### 搜索结果分析

期望的是doc5，结果是doc2,doc4排在了前面

计算每个document的relevance score：每个query的分数，乘以matched query数量，除以总query数量

* 算一下doc4的分数


    { "match": { "title": "java solution" }}，针对doc4，是有一个分数的
    { "match": { "content":  "java solution" }}，针对doc4，也是有一个分数的

所以是两个分数加起来，比如说，1.1 + 1.2 = 2.3；matched query数量 = 2；总query数量 = 2；即：2.3 * 2 / 2 = 2.3

* 算一下doc5的分数


    { "match": { "title": "java solution" }}，针对doc5，是没有分数的
    { "match": { "content":  "java solution" }}，针对doc5，是有一个分数的

只有一个query是有分数的，比如2.3；matched query数量 = 1；总query数量 = 2；即：2.3 * 1 / 2 = 1.15

doc5的分数 = 1.15 < doc4的分数 = 2.3

#### best fields策略，dis_max

best fields策略，搜索到的结果，应该是某一个field中匹配到了尽可能多的关键词，被排在前面；而不是尽可能多的field匹配到了少数的关键词，排在了前面

dis_max语法，直接取多个query中，分数最高的那一个query的分数即可

    { "match": { "title": "java solution" }}，针对doc4，是有一个分数的，1.1
    { "match": { "content":  "java solution" }}，针对doc4，也是有一个分数的，1.2

取最大分数，1.2

    { "match": { "title": "java solution" }}，针对doc5，是没有分数的
    { "match": { "content":  "java solution" }}，针对doc5，是有一个分数的，2.3

取最大分数，2.3

然后doc4的分数 = 1.2 < doc5的分数 = 2.3，所以doc5就可以排在更前面的地方，符合我们的需要

    GET /forum/_search
    {
        "query": {
            "dis_max": {
                "queries": [
                    { "match": { "title": "java solution" }},
                    { "match": { "content":  "java solution" }}
                ]
            }
        }
    }

### 9.11 基于tie_breaker参数优化dis_max搜索效果

#### 搜索title或content中包含java beginner的帖子

    GET /forum/_search
    {
        "query": {
            "dis_max": {
                "queries": [
                    { "match": { "title": "java beginner" }},
                    { "match": { "body":  "java beginner" }}
                ]
            }
        }
    }

可能在实际场景中出现的一个情况是这样的：

* （1）某个帖子，doc1，title中包含java，content不包含java beginner任何一个关键词
* （2）某个帖子，doc2，content中包含beginner，title中不包含任何一个关键词
* （3）某个帖子，doc3，title中包含java，content中包含beginner
* （4）最终搜索，可能出来的结果是，doc1和doc2排在doc3的前面，而不是我们期望的doc3排在最前面

dis_max，只是取分数最高的那个query的分数而已

#### dis_max只取某一个query最大的分数，完全不考虑其他query的分数

#### 使用tie_breaker将其他query的分数也考虑进去

tie_breaker参数的意义，在于将其他query的分数，乘以tie_breaker，然后综合与最高分数的那个query的分数，综合在一起进行计算；
除了取最高分以外，还会考虑其他的query的分数；tie_breaker的值，在0~1之间，是个小数，就ok

    GET /forum/_search
    {
        "query": {
            "dis_max": {
                "queries": [
                    { "match": { "title": "java beginner" }},
                    { "match": { "body":  "java beginner" }}
                ],
                "tie_breaker": 0.3
            }
        }
    }

### 9.12 基于multi_match语法实现dis_max+tie_breaker

    GET /forum/_search
    {
      "query": {
        "multi_match": {
            "query":                "java solution",
            "type":                 "best_fields", 
            "fields":               [ "title^2", "content" ],
            "tie_breaker":          0.3,
            "minimum_should_match": "50%" 
        }
      } 
    }

    GET /forum/_search
    {
      "query": {
        "dis_max": {
          "queries":  [
            {
              "match": {
                "title": {
                  "query": "java beginner",
                  "minimum_should_match": "50%",
              "boost": 2
                }
              }
            },
            {
              "match": {
                "body": {
                  "query": "java beginner",
                  "minimum_should_match": "30%"
                }
              }
            }
          ],
          "tie_breaker": 0.3
        }
      } 
    }

minimum_should_match，主要是用来干嘛的？

去长尾 long tail，什么是长尾，比如你搜索5个关键词，但是很多结果只匹配1个关键词，其实跟你想要的结果相差甚远，这些结果就是长尾；
minimum_should_match，控制搜索结果的精准度，只有匹配一定数量的关键词的数据，才能返回

### 9.13 基于multi_match+most fiels策略进行multi-field搜索

从best-fields换成most-fields策略

best-fields策略，主要是将某一个field匹配尽可能多的关键词的doc优先返回回来

most-fields策略，主要是尽可能返回更多field匹配到某个关键词的doc，优先返回回来

    POST /forum/_mapping
    {
      "properties": {
          "sub_title": { 
              "type":     "text",
              "analyzer": "english",
              "fields": {
                  "std":   { 
                      "type":     "text",
                      "analyzer": "standard"
                  }
              }
          }
      }
    }

    POST /forum/_bulk
    { "update": { "_id": "1"} }
    { "doc" : {"sub_title" : "learning more courses"} }
    { "update": { "_id": "2"} }
    { "doc" : {"sub_title" : "learned a lot of course"} }
    { "update": { "_id": "3"} }
    { "doc" : {"sub_title" : "we have a lot of fun"} }
    { "update": { "_id": "4"} }
    { "doc" : {"sub_title" : "both of them are good"} }
    { "update": { "_id": "5"} }
    { "doc" : {"sub_title" : "haha, hello world"} }

    GET /forum/_search
    {
      "query": {
        "match": {
          "sub_title": "learning courses"
        }
      }
    }

sub_title用的是enligsh analyzer，所以还原了单词

为什么，因为如果我们用的是类似于english analyzer这种分词器的话，就会将单词还原为其最基本的形态，stemmer

    learning --> learn
    learned --> learn
    courses --> course


    GET /forum/_search
    {
       "query": {
            "multi_match": {
                "query":  "learning courses",
                "type":   "most_fields", 
                "fields": [ "sub_title", "sub_title.std" ]
            }
        }
    }

#### 与best_fields的区别

* （1）best_fields，是对多个field进行搜索，挑选某个field匹配度最高的那个分数，同时在多个query最高分相同的情况下，在一定程度上考虑其他query的分数。
简单来说，你对多个field进行搜索，就想搜索到某一个field尽可能包含更多关键字的数据。

优点：通过best_fields策略，以及综合考虑其他field，还有minimum_should_match支持，可以尽可能精准地将匹配的结果推送到最前面。

缺点：除了那些精准匹配的结果，其他差不多大的结果，排序结果不是太均匀，没有什么区分度了。

实际的例子：百度之类的搜索引擎，最匹配的到最前面，但是其他的就没什么区分度了

* （2）most_fields，综合多个field一起进行搜索，尽可能多地让所有field的query参与到总分数的计算中来，此时就会是个大杂烩，出现类似best_fields案例最开始的那个结果，结果不一定精准，
某一个document的一个field包含更多的关键字，但是因为其他document有更多field匹配到了，所以排在了前面；
因此需要建立类似sub_title.std这样的field，尽可能让某一个field精准匹配query string，贡献更高的分数，将更精准匹配的数据排到前面

优点：将尽可能匹配更多field的结果推送到最前面，整个排序结果是比较均匀的；

缺点：可能那些精准匹配的结果，无法推送到最前面

实际的例子：wiki，明显的most_fields策略，搜索结果比较均匀，但是的确要翻好几页才能找到最匹配的结果

### 9.14 使用most_fields策略进行cross-fields search弊端

cross-fields搜索，一个唯一标识，跨了多个field。
比如一个人，标识，是姓名；一个建筑，它的标识是地址。姓名可以散落在多个field中，比如first_name和last_name中，地址可以散落在country，province，city中。

跨多个field搜索一个标识，比如搜索一个人名，或者一个地址，就是cross-fields搜索

初步来说，如果要实现，可能用most_fields比较合适。因为best_fields是优先搜索单个field最匹配的结果，cross-fields本身就不是一个field的问题了。

    POST /forum/_bulk
    { "update": { "_id": "1"} }
    { "doc" : {"author_first_name" : "Peter", "author_last_name" : "Smith"} }
    { "update": { "_id": "2"} }
    { "doc" : {"author_first_name" : "Smith", "author_last_name" : "Williams"} }
    { "update": { "_id": "3"} }
    { "doc" : {"author_first_name" : "Jack", "author_last_name" : "Ma"} }
    { "update": { "_id": "4"} }
    { "doc" : {"author_first_name" : "Robbin", "author_last_name" : "Li"} }
    { "update": { "_id": "5"} }
    { "doc" : {"author_first_name" : "Tonny", "author_last_name" : "Peter Smith"} }

    GET /forum/_search
    {
      "query": {
        "multi_match": {
          "query":       "Peter Smith",
          "type":        "most_fields",
          "fields":      [ "author_first_name", "author_last_name" ]
        }
      }
    }

Peter Smith，匹配author_first_name，匹配到了Smith，这时候它的分数很高，为什么啊？？？

因为IDF分数高，IDF分数要高，那么这个匹配到的term（Smith），在所有doc中的出现频率要低，author_first_name field中，Smith就出现过1次

Peter Smith这个人，doc 1，Smith在author_last_name中，但是author_last_name出现了两次Smith，所以导致doc 1的IDF分数较低


问题1：只是找到尽可能多的field匹配的doc，而不是某个field完全匹配的doc

问题2：most_fields，没办法用minimum_should_match去掉长尾数据，就是匹配的特别少的结果

问题3：TF/IDF算法，比如Peter Smith和Smith Williams，搜索Peter Smith的时候，由于first_name中很少有Smith的，所以query在所有document中的频率很低，得到的分数很高，可能Smith Williams反而会排在Peter Smith前面

### 9.15 使用copy_to定制组合field解决cross-fields搜索弊端

上一讲，我们说了用most_fields策略，去实现cross-fields搜索，有3大弊端，而且搜索结果也显示出了这3大弊端

第一个办法：用copy_to，将多个field组合成一个field

问题其实就出在有多个field，有多个field以后，就很尴尬，我们要想办法将一个标识跨在多个field的情况，合并成一个field。
比如说，一个人名，本来是first_name，last_name，现在合并成一个full_name，这样就直接查full_name 就ok了。

    PUT /forum/_mapping
    {
      "properties": {
          "new_author_first_name": {
              "type":     "text",
              "copy_to":  "new_author_full_name" 
          },
          "new_author_last_name": {
              "type":     "text",
              "copy_to":  "new_author_full_name" 
          },
          "new_author_full_name": {
              "type":     "text"
          }
      }
    }

用了这个copy_to语法之后，就可以将多个字段的值拷贝到一个字段中，并建立倒排索引

    POST /forum/_bulk
    { "update": { "_id": "1"} }
    { "doc" : {"new_author_first_name" : "Peter", "new_author_last_name" : "Smith"} }
    { "update": { "_id": "2"} }	
    { "doc" : {"new_author_first_name" : "Smith", "new_author_last_name" : "Williams"} }
    { "update": { "_id": "3"} }
    { "doc" : {"new_author_first_name" : "Jack", "new_author_last_name" : "Ma"} }
    { "update": { "_id": "4"} }
    { "doc" : {"new_author_first_name" : "Robbin", "new_author_last_name" : "Li"} }
    { "update": { "_id": "5"} }
    { "doc" : {"new_author_first_name" : "Tonny", "new_author_last_name" : "Peter Smith"} }

    GET /forum/_search
    {
      "query": {
        "match": {
          "new_author_full_name": "Peter Smith"
        }
      }
    }
  
问题1：只是找到尽可能多的field匹配的doc，而不是某个field完全匹配的doc --> 解决，最匹配的document被最先返回

问题2：most_fields，没办法用minimum_should_match去掉长尾数据，就是匹配的特别少的结果 --> 解决，可以使用minimum_should_match去掉长尾数据

问题3：TF/IDF算法，比如Peter Smith和Smith Williams，搜索Peter Smith的时候，由于first_name中很少有Smith的，所以query在所有document中的频率很低，得到的分数很高，可能Smith Williams反而会排在Peter Smith前面 --> 解决，Smith和Peter在一个field了，所以在所有document中出现的次数是均匀的，不会有极端的偏差

### 9.16 使用原生cross-fiels技术解决搜索弊端

    GET /forum/_search
    {
      "query": {
        "multi_match": {
          "query": "Peter Smith",
          "type": "cross_fields", 
          "operator": "and",
          "fields": ["author_first_name", "author_last_name"]
        }
      }
    } 
    
问题1：只是找到尽可能多的field匹配的doc，而不是某个field完全匹配的doc --> 解决，要求每个term都必须在任何一个field中出现

问题2：most_fields，没办法用minimum_should_match去掉长尾数据，就是匹配的特别少的结果 --> 解决，既然每个term都要求出现，长尾肯定被去除掉了

问题3：TF/IDF算法，比如Peter Smith和Smith Williams，搜索Peter Smith的时候，由于first_name中很少有Smith的，
所以query在所有document中的频率很低，得到的分数很高，可能Smith Williams反而会排在Peter Smith前面 --> 计算IDF的时候，
将每个query在每个field中的IDF都取出来，取最小值，就不会出现极端情况下的极大值了    

### 9.17 掌握phrase matching搜索技术

如果我们要尽量让java和spark离的很近的document优先返回，要给它一个更高的relevance score，这就涉及到了proximity match，近似匹配

需求：

1. java spark，就靠在一起，中间不能插入任何其他字符，就要搜索出来这种doc
2. java spark，但是要求，java和spark两个单词靠的越近，doc的分数越高，排名越靠前

要实现上述两个需求，用match做全文检索，是搞不定的，必须得用proximity match，近似匹配

phrase match，proximity match：短语匹配，近似匹配

#### 使用match_phrase来查询包含`java and elasticsearch`的数据

    POST /forum/_bulk
    { "update": { "_id": "1"} }
    { "doc" : {"content" : "java elasticsearch is friend"} }
    { "update": { "_id": "2"} }
    { "doc" : {"content" : "java and elasticsearch very good"} }
    { "update": { "_id": "3"} }
    { "doc" : {"content" : "this is elasticsearch blog"} }
    { "update": { "_id": "4"} }
    { "doc" : {"content" : "this is java, elasticsearch, hadoop blog"} }
    { "update": { "_id": "5"} }
    { "doc" : {"content" : "this is spark blog"} }

使用match_phrase来查询包含`java and elasticsearch`的数据

    GET /forum/_search
    {
        "query": {
            "match_phrase": {
                "content": "java and elasticsearch"
            }
        }
    }

#### match_phrase的基本原理

这里举个简单例子来说明；有如下2个文档内容，我们需要 match_phrase匹配的是`java elasticsearch`

    doc1 : hello, java elasticsearch
    doc2 : hello, elasticsearch java

首先对文档内容建立类似如下的倒排索引，在value中保存了term（单词）的position（位置）

    hello -------------- [doc1(1), doc2(1)]  
    java --------------- [doc1(2), doc2(3)]
    elasticsearch ------ [doc1(3), doc2(2)]

这样在查询时先对查询的内容`java elasticsearch`进行分词得到`java`、`elasticsearch`然后根据倒排索引进行查询得到匹配的文档doc1,doc2;

现在对数据进一步做匹配处理：

doc1-->> java-doc1(2)，elasticsearch-doc1(3)；elasticsearch的position刚好比java的大1，符合实际的顺序，doc1 符合条件；

doc2-->> java-doc2(3)，elasticsearch-doc2(2)；elasticsearch的position比java的小1，不符合实际的顺序，doc2 不符合条件；

最终只有doc1符合条件。

### 9.18 基于slop参数实现近似匹配以及原理剖析和相关实验

    GET /forum/_search
    {
        "query": {
            "match_phrase": {
                "content": {
                    "query": "java elasticsearch",
                    "slop":  1
                }
            }
        }
    }
    
    
slop的含义：query string，搜索文本中的几个term，要经过几次移动才能与一个document匹配，这个移动的次数，就是slop；
这里设置slop的意思就是在匹配的过程中最多可以移动多少次；

> 其实，加了slop的phrase match，就是proximity match，近似匹配

### 9.19 混合使用match和近似匹配实现召回率与精准度的平衡

召回率：搜索一个java elasticsearch，总共有100个doc，能返回多少个doc作为结果，就是召回率（recall）

精准度：搜索一个java elasticsearch，能不能尽可能让包含java elasticsearch，或者是java和elasticsearch离的很近的doc，排在最前面，就是精准度（precision）


直接用match_phrase短语搜索，会导致必须所有term都在doc field中出现，而且距离在slop限定范围内，才能匹配上

match phrase和proximity match要求doc必须包含所有的term，才能作为结果返回；如果某一个doc可能就是有某个term没有包含，那么就无法作为结果返回

近似匹配的时候，召回率比较低，精准度太高了

但是有时我们希望的是匹配到几个term中的部分，就可以作为结果出来，这样可以提高召回率。
同时我们也希望用上match_phrase根据距离提升分数的功能，让几个term距离越近分数就越高，优先返回。
就是优先满足召回率意思，比如搜索java elasticsearch，包含java的也返回，包含elasticsearch的也返回，包含java和elasticsearch的也返回；
同时兼顾精准度，就是包含java和elasticsearch，同时java和elasticsearch离的越近的doc排在最前面

此时可以用bool组合match query和match_phrase query一起，来实现上述效果

    GET /forum/_search
    {
      "query": {
        "bool": {
          "must": {
            "match": { 
              "content": {
                "query": "java elasticsearch" 
              }
            }
          },
          "should": {
            "match_phrase": { 
              "content": {
                "query": "java elasticsearch",
                "slop":  50
              }
            }
          }
        }
      }
    }

> 在match query中java或elasticsearch或java elasticsearch，java和elasticsearch靠前，但是没法区分java和elasticsearch的距离，也许java和elasticsearch靠的很近，但是没法排在最前面
> match_phrase在slop以内，如果java elasticsearch能匹配上一个doc，那么就会对doc贡献自己的relevance score，如果java和elasticsearch靠的越近，那么就分数越高

### 9.20 使用rescoring机制优化近似匹配搜索的性能

#### match和phrase match(proximity match)区别

match： 只要简单的匹配到了一个term，就可以理解将term对应的doc作为结果返回，扫描倒排索引，扫描到了就ok

phrase match ：首先扫描到所有term的doc list; 找到包含所有term的doc list; 然后对每个doc都计算每个term的position，是否符合指定的范围; slop，需要进行复杂的运算，来判断能否通过slop移动，匹配一个doc

match query的性能比phrase match和proximity match（有slop）要高很多。因为后两者都要计算position的距离。
match query比phrase match的性能要高10倍，比proximity match的性能要高20倍。

但是别太担心，因为es的性能一般都在毫秒级别，match query一般就在几毫秒，或者几十毫秒，而phrase match和proximity match的性能在几十毫秒到几百毫秒之间，所以也是可以接受的。

优化proximity match的性能，一般就是减少要进行proximity match搜索的document数量。
主要思路就是，用match query先过滤出需要的数据，然后再用proximity match来根据term距离提高doc的分数，
同时proximity match只针对每个shard的分数排名前n个doc起作用，来重新调整它们的分数，这个过程称之为重计分(rescoring)。
因为一般用户会分页查询，只会看到前几页的数据，所以不需要对所有结果进行proximity match操作。

用我们刚才的说法，match + proximity match同时实现召回率和精准度

默认情况下，match也许匹配了1000个doc，proximity match全都需要对每个doc进行一遍运算，判断能否slop移动匹配上，然后去贡献自己的分数
但是很多情况下，match出来也许1000个doc，其实用户大部分情况下是分页查询的，所以可能最多只会看前几页，比如一页是10条，最多也许就看5页，就是50条
proximity match只要对前50个doc进行slop移动去匹配，去贡献自己的分数即可，不需要对全部1000个doc都去进行计算和贡献分数

    GET /forum/_search 
    {
      "query": {
        "match": {
          "content": "java elasticsearch"
        }
      },
      "rescore": {
        "window_size": 50,
        "query": {
          "rescore_query": {
            "match_phrase": {
              "content": {
                "query": "java elasticsearch",
                "slop": 50
              }
            }
          }
        }
      }
    }

### 9.21 实战前缀搜索、通配符搜索、正则搜索等技术

#### 前缀搜索

    GET /forum/_search
    {
      "query": {
        "prefix": {
          "articleID.keyword": {
            "value": "X"
          }
        }
      }
    }

前缀搜索的原理：prefix query不计算relevance score，与prefix filter唯一的区别就是，filter会cache bitset；扫描整个倒排索引。前缀越短，要处理的doc越多，性能越差，尽可能用长前缀搜索
        
前缀搜索，它是怎么执行的？性能为什么差呢？

根据前缀扫描完整个的倒排索引，一个个匹配将结果返回，这就是为什么性能差

#### 通配符搜索

跟前缀搜索类似，使用通配符去表达更加复杂的模糊搜索的语义，功能更加强大
        
5?-*5：5个字符 D 任意个字符5

    GET /forum/_search
    {
      "query": {
        "wildcard": {
          "articleID": {
            "value": "X?K*5"
          }
        }
      }
    }

> ?：任意字符; *：0个或任意多个字符

性能一样差，必须扫描整个倒排索引

#### 正则搜索

    GET /forum/_search
    {
      "query": {
        "regexp": {
          "articleID": "X[0-9].+"
        }
      }
    }
    
wildcard和regexp，与prefix原理一致，都会扫描整个索引，性能很差

### 9.22 实战match_phrase_prefix实现search-time搜索推荐

    GET /forum/_search
    {
      "query": {
        "match_phrase_prefix": {
          "content": "java e"
        }
      }
    }

原理跟match_phrase类似，唯一的区别，就是把最后一个term作为前缀去搜索

大致流程：

1. 搜索`java e`会先分词为java、e；
2. 然后java会进行match搜索对应的doc; 
3. e会作为前缀，去扫描整个倒排索引，找到所有w开头的doc;
4. 然后找到所有doc中，即包含java，又包含e开头的字符的doc; 根据你的slop去计算，看在slop范围内，能不能让java e，
正好跟doc中的java和e开头的单词的position相匹配；也可以指定slop，但是只有最后一个term会作为前缀。

max_expansions：指定prefix最多匹配多少个term，超过这个数量就不继续匹配了，限定性能

默认情况下，前缀要扫描所有的倒排索引中的term，去查找e打头的单词，但是这样性能太差。可以用max_expansions限定，e前缀最多匹配多少个term，就不再继续搜索倒排索引了。

尽量不要用，因为，最后一个前缀始终要去扫描大量的索引，性能可能会很差

### 9.23 实战通过ngram分词机制实现index-time搜索推荐

#### ngram和index-time搜索推荐原理

什么是ngram？按词语可以拆分的长度进行处理，下面举例说明：

quick，5种长度下的ngram

    ngram length=1，q u i c k
    ngram length=2，qu ui ic ck
    ngram length=3，qui uic ick
    ngram length=4，quic uick
    ngram length=5，quick

什么是edge ngram？固定首字母，然后依次叠加词；下面举例说明：

quick，根据首字母后进行ngram

    q
    qu
    qui
    quic
    quick

使用edge ngram将每个单词都进行进一步的分词切分，用切分后的ngram来实现前缀搜索推荐功能

搜索的时候，不用再根据一个前缀，然后扫描整个倒排索引了; 直接拿前缀去倒排索引中匹配即可，如果匹配上了，那么就好了。

#### ngram示例

设置ngram

    PUT /ngram-demo
    {
        "settings": {
            "analysis": {
                "filter": {
                    "autocomplete_filter": { 
                        "type":     "edge_ngram",
                        "min_gram": 1,
                        "max_gram": 20
                    }
                },
                "analyzer": {
                    "autocomplete": {
                        "type":      "custom",
                        "tokenizer": "standard",
                        "filter": [
                            "lowercase",
                            "autocomplete_filter" 
                        ]
                    }
                }
            }
        }
    }

查看分词
    
    GET /ngram-demo/_analyze
    {
      "analyzer": "autocomplete",
      "text": "quick brown"
    }

_mapping设置
    
    PUT /ngram-demo/_mapping
    {
      "properties": {
          "title": {
              "type":     "text",
              "analyzer": "autocomplete",
              "search_analyzer": "standard"
          }
      }
    }

添加测试数据    
    
    POST /ngram-demo/_bulk
    { "index": { "_id": 1 }}
    { "title" : "hello wiki", "userID" : 1, "hidden": false }

数据查询      
      
    GET /ngram-demo/_search
    {
      "query": {
        "match_phrase": {
          "title": "hello w"
        }
      }
    }

如果用match，只有hello的也会出来，全文检索，只是分数比较低；
推荐使用match_phrase，要求每个term都有，而且position刚好靠着1位，符合我们的期望的

### 9.24 深入揭秘TF&IDF算法以及向量空间模型算法

#### boolean model：
类似and这种逻辑操作符，先过滤出包含指定term的doc；比如：

    query "hello world" --> 过滤 --> hello / world / hello & world
    bool --> must/must not/should --> 过滤 --> 包含 / 不包含 / 可能包含
    doc --> 不打分数 --> 正或反 true or false --> 为了减少后续要计算的doc的数量，提升性能

#### 单个term在doc中的分数

* TF/IDF：

一个term在一个doc中，根据出现的次数给个分数，出现的次数越多，那么最后给的相关度评分就会越高

* IDF：inversed document frequency

一个term在所有的doc中，出现的次数越多，那么最后给的相关度评分就会越低

* length norm：搜索的那个field内容的长度，field长度越长，给的相关度评分越低; field长度越短，给的相关度评分越高

最后，会将这个term，对doc1的分数，综合TF，IDF，length norm，计算出来一个综合性的分数

* vector space model：多个term对一个doc的总分数

es会根据搜索词语在所有doc中的评分情况，计算出一个query vector(query向量)；
会给每一个doc，拿每个term计算出一个分数来，再拿所有term的分数组成一个doc vector；

画在一个图中，取每个doc vector对query vector的弧度，给出每个doc对多个term的总分数

每个doc vector计算出对query vector的弧度，最后基于这个弧度给出一个doc相对于query中多个term的总分数
弧度越大，分数月底; 弧度越小，分数越高

如果是多个term，那么就是线性代数来计算，无法用图表示

### 9.25 实战掌握四种常见的相关度分数优化方法

#### query-time boost

    GET /forum/_search
    {
      "query": {
        "bool": {
          "should": [
            {
              "match": {
                "title": {
                  "query": "java spark",
                  "boost": 2
                }
              }
            },
            {
              "match": {
                "content": "java spark"
              }
            }
          ]
        }
      }
    }

#### 重构查询结构
     
重构查询结果，在es新版本中，影响越来越小了。一般情况下，没什么必要的话，大家不用也行。
     
     GET /forum/article/_search 
     {
       "query": {
         "bool": {
           "should": [
             {
               "match": {
                 "content": "java"
               }
             },
             {
               "match": {
                 "content": "spark"
               }
             },
             {
               "bool": {
                 "should": [
                   {
                     "match": {
                       "content": "solution"
                     }
                   },
                   {
                     "match": {
                       "content": "beginner"
                     }
                   }
                 ]
               }
             }
           ]
         }
       }
     }

#### negative boost降低相关度

    GET /forum/_search 
    {
      "query": {
        "boosting": {
          "positive": {
            "match": {
              "content": "java"
            }
          },
          "negative": {
            "match": {
              "content": "spark"
            }
          },
          "negative_boost": 0.2
        }
      }
    }

> negative的doc，会乘以negative_boost，降低分数

#### constant_score

如果你压根儿不需要相关度评分，直接走constant_score加filter，所有的doc分数都是1，没有评分的概念了

    GET /forum/_search 
    {
      "query": {
        "bool": {
          "should": [
            {
              "constant_score": {
                "query": {
                  "match": {
                    "title": "java"
                  }
                }
              }
            },
            {
              "constant_score": {
                "query": {
                  "match": {
                    "title": "spark"
                  }
                }
              }
            }
          ]
        }
      }
    }

### 9.26 实战用function_score自定义相关度分数算法

我们可以做到自定义一个function_score函数，自己将某个field的值，跟es内置算出来的分数进行运算，然后由自己指定的field来进行分数的增强

数据准备

    POST /forum/_bulk
    { "update": { "_id": "1"} }
    { "doc" : {"follower_num" : 5} }
    { "update": { "_id": "2"} }
    { "doc" : {"follower_num" : 10} }
    { "update": { "_id": "3"} }
    { "doc" : {"follower_num" : 25} }
    { "update": { "_id": "4"} }
    { "doc" : {"follower_num" : 3} }
    { "update": { "_id": "5"} }
    { "doc" : {"follower_num" : 60} }


将搜索得到的分数，跟follower_num进行运算，由follower_num在一定程度上增强其分数；follower_num越大，那么分数就越高

    GET /forum/_search
    {
      "query": {
        "function_score": {
          "query": {
            "multi_match": {
              "query": "java spark",
              "fields": ["tile", "content"]
            }
          },
          "field_value_factor": {
            "field": "follower_num",
            "modifier": "log1p",
            "factor": 0.5
          },
          "boost_mode": "sum",
          "max_boost": 2
        }
      }
    }

如果只有field，那么会将每个doc的分数都乘以follower_num，如果有的doc follower是0，那么分数就会变为0，效果很不好。
因此一般会加个log1p函数，公式会变为，new_score = old_score * log(1 + number_of_votes)，这样出来的分数会比较合理；
再加个factor，可以进一步影响分数，new_score = old_score * log(1 + factor * number_of_votes)；
boost_mode，可以决定分数与指定字段的值如何计算，multiply，sum，min，max，replace；
max_boost，限制计算出来的分数不要超过max_boost指定的值

### 9.27 实战掌握误拼写时的fuzzy模糊搜索技术

搜索的时候，可能输入的搜索文本会出现误拼写的情况

fuzzy搜索技术 --> 自动将拼写错误的搜索文本，进行纠正，纠正以后去尝试匹配索引中的数据


实际想要hello，但是少写个o

    GET /forum/_search
    {
      "query": {
        "fuzzy": {
          "title": {
            "value": "hell",
            "fuzziness": 2
          }
        }
      }
    }

> fuzziness 指定的修订最大次数，默认为2


    GET /forum/_search
    {
      "query": {
        "match": {
          "title": {
            "query": "helio",
            "fuzziness": "AUTO",
            "operator": "and"
          }
        }
      }
    }

## 十、IK中文分词器

### 安装

从github上下载安装包（或者自己编译）：

    https://github.com/medcl/elasticsearch-analysis-ik

将解压后的文件放到es的docker容器中（也可以做个文件目录的映射）：

    docker cp /home/ik es7:/usr/share/elasticsearch/plugins/

> ik目录就是解压后的文件目录
> 如果不确定plugins目录在哪儿，可以通过`docker exec -it es7 /bin/bash`命令进入容器内查看

然后重启es

    docker restart es7

### ik分词器基础知识

两种analyzer，根据自己的需要选择，但是一般是选用ik_max_word

ik_max_word: 会将文本做最细粒度的拆分，比如会将“中华人民共和国国歌”拆分为“中华人民共和国,中华人民,中华,华人,人民共和国,人民,人,民,共和国,共和,和,国国,国歌”，会穷尽各种可能的组合；

ik_smart: 会做最粗粒度的拆分，比如会将“中华人民共和国国歌”拆分为“中华人民共和国,国歌”。

### ik分词器的使用

配置mapping：

    PUT /news
    {
      "mappings": {
        "properties": {
          "content":{
            "type": "text",
            "analyzer": "ik_max_word"
          }
        }
      }
    }

### IK分词器配置文件讲解以及自定义词库

    IKAnalyzer.cfg.xml：用来配置自定义词库
    main.dic：ik原生内置的中文词库，总共有27万多条，只要是这些单词，都会被分在一起
    quantifier.dic：放了一些单位相关的词
    suffix.dic：放了一些后缀
    surname.dic：中国的姓氏
    stopword.dic：英文停用词

ik原生最重要的两个配置文件

main.dic：包含了原生的中文词语，会按照这个里面的词语去分词

stopword.dic：包含了英文的停用词，停用词，stopword

一般，像停用词，会在分词的时候，直接被干掉，不会建立在倒排索引中

### 自定义词库    

（1）自己建立词库：每年都会涌现一些特殊的流行词，网红，蓝瘦香菇，喊麦，鬼畜，一般不会在ik的原生词典里

自己补充自己的最新的词语，到ik的词库里面去

在IKAnalyzer.cfg.xml中配置自定义的词，ext_dict，custom/mydict.dic

补充自己的词语，然后需要重启es，才能生效

（2）自己建立停用词库：比如了，的，啥，么，我们可能并不想去建立索引，让人家搜索

custom/ext_stopword.dic，已经有了常用的中文停用词，可以补充自己的停用词，然后重启es

### 修改IK分词器源码来基于mysql热更新词库

热更新

每次都是在es的扩展词典中，手动添加新词语，很坑
（1）每次添加完，都要重启es才能生效，非常麻烦
（2）es是分布式的，可能有数百个节点，你不能每次都一个一个节点上面去修改

es不停机，直接我们在外部某个地方添加新的词语，es中立即热加载到这些新词语

热更新的方案

（1）修改ik分词器源码，然后手动支持从mysql中每隔一定时间，自动加载新的词库
（2）基于ik分词器原生支持的热更新方案，部署一个web服务器，提供一个http接口，通过modified和tag两个http响应头，来提供词语的热更新

用第一种方案，第二种，ik git社区官方都不建议采用，觉得不太稳定

## 十一、ICU分词器

ICU Analysis插件是一组将Lucene ICU模块集成到Elasticsearch中的库。 
本质上，ICU的目的是增加对Unicode和全球化的支持，以提供对亚洲语言更好的文本分割分析，还有大量对除英语外其他语言进行正确匹配和排序所必须的分词过滤器。

## 十二、深入聚合数据分析

### 12.1 bucket与metric两个核心概念

bucket：就是对数据进行分组，类似MySQL中的group

metric：对一个数据分组执行的统计；metric就是对一个bucket执行的某种聚合分析的操作，比如说求平均值，求最大值，求最小值

### 12.2 家电卖场案例以及统计哪种颜色电视销量最高

以一个家电卖场中的电视销售数据为背景，来对各种品牌，各种颜色的电视的销量和销售额，进行各种各样角度的分析

#### 初始化数据

    PUT /tvs
    {
        "mappings": {
            "properties": {
                "price": {
                    "type": "long"
                },
                "color": {
                    "type": "keyword"
                },
                "brand": {
                    "type": "keyword"
                },
                "sold_date": {
                    "type": "date"
                }
            }
        }
    }
 
添加数据    
    
    POST /tvs/_bulk
    { "index": {}}
    { "price" : 1000, "color" : "红色", "brand" : "长虹", "sold_date" : "2019-10-28" }
    { "index": {}}
    { "price" : 2000, "color" : "红色", "brand" : "长虹", "sold_date" : "2019-11-05" }
    { "index": {}}
    { "price" : 3000, "color" : "绿色", "brand" : "小米", "sold_date" : "2019-05-18" }
    { "index": {}}
    { "price" : 1500, "color" : "蓝色", "brand" : "TCL", "sold_date" : "2019-07-02" }
    { "index": {}}
    { "price" : 1200, "color" : "绿色", "brand" : "TCL", "sold_date" : "2019-08-19" }
    { "index": {}}
    { "price" : 2000, "color" : "红色", "brand" : "长虹", "sold_date" : "2019-11-05" }
    { "index": {}}
    { "price" : 8000, "color" : "红色", "brand" : "三星", "sold_date" : "2020-01-01" }
    { "index": {}}
    { "price" : 2500, "color" : "蓝色", "brand" : "小米", "sold_date" : "2020-02-12" }

#### 统计哪种颜色的电视销量最高

    GET /tvs/_search
    {
        "size" : 0,
        "aggs" : { 
            "popular_colors" : { 
                "terms" : { 
                  "field" : "color"
                }
            }
        }
    }
> size：只获取聚合结果，而不要执行聚合的原始数据
> aggs：固定语法，要对一份数据执行分组聚合操作
> popular_colors：就是对每个aggs，都要起一个名字，这个名字是随机的，你随便取什么都ok
> terms：根据字段的值进行分组
> field：根据指定的字段的值进行分组

返回结果说明：

* hits.hits：我们指定了size是0，所以hits.hits就是空的，否则会把执行聚合的那些原始数据给你返回回来
* aggregations：聚合结果
* popular_color：我们指定的某个聚合的名称
* buckets：根据我们指定的field划分出的buckets
* key：每个bucket对应的那个值
* doc_count：这个bucket分组内，有多少个数据
* 数量，其实就是这种颜色的销量

每种颜色对应的bucket中的数据的默认的排序规则：按照doc_count降序排序

### 12.3 实战bucket+metric：统计每种颜色电视平均价格

    GET /tvs/_search
    {
       "size" : 0,
       "aggs": {
          "colors": {
             "terms": {
                "field": "color"
             },
             "aggs": { 
                "avg_price": { 
                   "avg": {
                      "field": "price" 
                   }
                }
             }
          }
       }
    }    

### 12.4 bucket嵌套实现颜色+品牌的多层下钻分析

统计每个颜色的平均价格，同时统计每个颜色下每个品牌的平均价格

    GET /tvs/_search 
    {
      "size": 0,
      "aggs": {
        "group_by_color": {
          "terms": {
            "field": "color"
          },
          "aggs": {
            "color_avg_price": {
              "avg": {
                "field": "price"
              }
            },
            "group_by_brand": {
              "terms": {
                "field": "brand"
              },
              "aggs": {
                "brand_avg_price": {
                  "avg": {
                    "field": "price"
                  }
                }
              }
            }
          }
        }
      }
    }

这里需要知道的是es是根据语句顺序执行的，就像人去读取执行一样。

### 12.5 掌握更多metrics：统计每种颜色电视最大最小价格

更多的metric

    count：bucket，terms，自动就会有一个doc_count，就相当于是count
    avg：avg aggs，求平均值
    max：求一个bucket内，指定field值最大的那个数据
    min：求一个bucket内，指定field值最小的那个数据
    sum：求一个bucket内，指定field值的总和



    GET /tvs/_search
    {
       "size" : 0,
       "aggs": {
          "colors": {
             "terms": {
                "field": "color"
             },
             "aggs": {
                "avg_price": { "avg": { "field": "price" } },
                "min_price" : { "min": { "field": "price"} }, 
                "max_price" : { "max": { "field": "price"} },
                "sum_price" : { "sum": { "field": "price" } } 
             }
          }
       }
    }

### 12.6 实战histogram按价格区间统计电视销量和销售额

histogram：类似于terms，也是进行bucket分组操作，接收一个field，按照这个field的值的各个范围区间，进行bucket分组操作；比如：

    "histogram":{ 
      "field": "price",
      "interval": 2000
    },

interval：2000，划分范围，0~2000，2000~4000，4000~6000，6000~8000，8000~10000分组

根据price的值，比如2500，看落在哪个区间内，比如2000~4000，此时就会将这条数据放入2000~4000对应的那个bucket中

bucket划分的方法，terms，将field值相同的数据划分到一个bucket中；bucket有了之后，去对每个bucket执行avg，count，sum，max，min，等各种metric聚合分析操作

    GET /tvs/_search
    {
       "size" : 0,
       "aggs":{
          "price":{
             "histogram":{ 
                "field": "price",
                "interval": 2000
             },
             "aggs":{
                "revenue": {
                   "sum": { 
                     "field" : "price"
                   }
                 }
             }
          }
       }
    }

### 12.7 实战date histogram之统计每月电视销量

* histogram，按照某个值指定的interval划分；
* date histogram，按照我们指定的某个date类型的日期field，以及日期interval，按照一定的日期间隔，去划分；


    GET /tvs/_search
    {
       "size" : 0,
       "aggs": {
          "sales": {
             "date_histogram": {
                "field": "sold_date",
                "interval": "month", 
                "format": "yyyy-MM-dd",
                "min_doc_count" : 0, 
                "extended_bounds" : { 
                    "min" : "2019-01-01",
                    "max" : "2020-12-31"
                }
             }
          }
       }
    }
    
min_doc_count：即使某个日期interval，2019-01-01~2019-01-31中，一条数据都没有，那么这个区间也是要返回的，不然默认是会过滤掉这个区间的

extended_bounds：min，max：划分bucket的时候，会限定在这个起始日期，和截止日期内

### 12.8 下钻分析之统计每季度每个品牌的销售额

    GET /tvs/_search
    {
      "size": 0,
      "aggs": {
        "group_by_sold_date": {
          "date_histogram": {
            "field": "sold_date",
            "interval": "quarter",
            "format": "yyyy-MM-dd",
            "min_doc_count": 0,
            "extended_bounds": {
              "min": "2016-01-01",
              "max": "2017-12-31"
            }
          },
          "aggs": {
            "group_by_brand": {
              "terms": {
                "field": "brand"
              },
              "aggs": {
                "sum_price": {
                  "sum": {
                    "field": "price"
                  }
                }
              }
            },
            "total_sum_price": {
              "sum": {
                "field": "price"
              }
            }
          }
        }
      }
    }

### 12.9 搜索+聚合：统计指定品牌下每个颜色的销量

    GET /tvs/_search 
    {
      "size": 0,
      "query": {
        "term": {
          "brand": {
            "value": "小米"
          }
        }
      },
      "aggs": {
        "group_by_color": {
          "terms": {
            "field": "color"
          }
        }
      }
    }
    
es的任何的聚合，都必须在搜索出来的结果数据中进行聚合分析操作。

### 12.10 global bucket：单个品牌与所有品牌销量对比

一个聚合操作，必须在query的搜索结果范围内执行

上面的需求需要出来两个结果，一个结果，是基于query搜索结果来聚合的; 一个结果，是对所有数据执行聚合的

    GET /tvs/_search 
    {
      "size": 0, 
      "query": {
        "term": {
          "brand": {
            "value": "长虹"
          }
        }
      },
      "aggs": {
        "single_brand_avg_price": {
          "avg": {
            "field": "price"
          }
        },
        "all": {
          "global": {},
          "aggs": {
            "all_brand_avg_price": {
              "avg": {
                "field": "price"
              }
            }
          }
        }
      }
    }
    
global：就是global bucket，就是将所有数据纳入聚合的scope，而不管之前的query

### 12.11 过滤+聚合：统计价格大于1200的电视平均价格

搜索+聚合
过滤+聚合

    GET /tvs/_search 
    {
      "size": 0,
      "query": {
        "constant_score": {
          "filter": {
            "range": {
              "price": {
                "gte": 1200
              }
            }
          }
        }
      },
      "aggs": {
        "avg_price": {
          "avg": {
            "field": "price"
          }
        }
      }
    }

### 12.12 bucket filter：统计牌品最近一个月的平均价格

    GET /tvs/_search 
    {
      "size": 0,
      "query": {
        "term": {
          "brand": {
            "value": "长虹"
          }
        }
      },
      "aggs": {
        "recent_150d": {
          "filter": {
            "range": {
              "sold_date": {
                "gte": "now-150d"
              }
            }
          },
          "aggs": {
            "recent_150d_avg_price": {
              "avg": {
                "field": "price"
              }
            }
          }
        },
        "recent_140d": {
          "filter": {
            "range": {
              "sold_date": {
                "gte": "now-140d"
              }
            }
          },
          "aggs": {
            "recent_140d_avg_price": {
              "avg": {
                "field": "price"
              }
            }
          }
        },
        "recent_130d": {
          "filter": {
            "range": {
              "sold_date": {
                "gte": "now-130d"
              }
            }
          },
          "aggs": {
            "recent_130d_avg_price": {
              "avg": {
                "field": "price"
              }
            }
          }
        }
      }
    }

bucket filter：对不同的bucket下的aggs，进行filter

### 12.13 排序：按每种颜色的平均销售额降序排序

默认排序，是按照每个bucket的doc_count降序来排的

    GET /tvs/_search 
    {
      "size": 0,
      "aggs": {
        "group_by_color": {
          "terms": {
            "field": "color"
          },
          "aggs": {
            "avg_price": {
              "avg": {
                "field": "price"
              }
            }
          }
        }
      }
    }

指定排序规则

    GET /tvs/_search 
    {
      "size": 0,
      "aggs": {
        "group_by_color": {
          "terms": {
            "field": "color",
            "order": {
              "avg_price": "asc"
            }
          },
          "aggs": {
            "avg_price": {
              "avg": {
                "field": "price"
              }
            }
          }
        }
      }
    }

### 12.14 颜色+品牌下钻分析时按最深层metric进行排序

    GET /tvs/_search 
    {
      "size": 0,
      "aggs": {
        "group_by_color": {
          "terms": {
            "field": "color"},
          "aggs": {
            "group_by_brand": {
              "terms": {
                "field": "brand",
                "order": {
                  "avg_price": "desc"
                }
              },
              "aggs": {
                "avg_price": {
                  "avg": {
                    "field": "price"
                  }
                }
              }
            }
          }
        }
      }
    }

### 12.15 易并行聚合算法，三角选择原则，近似聚合算法

#### 易并行聚合算法：max

有些聚合分析的算法，是很容易就可以并行的，比如说max，只需要各个节点单独求最大，然后将结果返回再求最大值即可。

有些聚合分析的算法，是不好并行的，比如count(distinct)，并不是在每个node上，直接就去重求和就可以的，因为数据可能会很多，同时各个节点之间也有重复数据的情况；

因此为提高性能es会采取近似聚合的方式，就是采用在每个node上进行近估计的方式，得到最终的结论；
近似估计后的结果，不完全准确，但是速度会很快，一般会达到完全精准的算法的性能的数十倍

#### 三角选择原则

精准+实时+大数据 --> 选择2个

* （1）精准+实时: 没有大数据，数据量很小，那么一般就是单机跑，随便你则么玩儿就可以
* （2）精准+大数据：hadoop，批处理，非实时，可以处理海量数据，保证精准，可能会跑几个小时
* （3）大数据+实时：es，不精准，近似估计，可能会有百分之几的错误率

#### 近似聚合算法

如果采取近似估计的算法：延时在100ms左右，0.5%错误

如果采取100%精准的算法：延时一般在5s~几十s，甚至几十分钟、几个小时， 0%错误

### 12.16 cardinality去重算法以及每月销售品牌数量统计

cartinality metric：对每个bucket中的指定的field进行去重，取去重后的count，类似于count(distcint)

    GET /tvs/_search
    {
      "size" : 0,
      "aggs" : {
          "months" : {
            "date_histogram": {
              "field": "sold_date",
              "interval": "month"
            },
            "aggs": {
              "distinct_colors" : {
                  "cardinality" : {
                    "field" : "brand"
                  }
              }
            }
          }
      }
    }

### 12.17 cardinality算法之优化内存开销以及HLL算法

cardinality，count(distinct)，5%的错误率，性能在100ms左右

#### precision_threshold优化准确率和内存开销

    GET /tvs/_search
    {
        "size" : 0,
        "aggs" : {
            "distinct_brand" : {
                "cardinality" : {
                  "field" : "brand",
                  "precision_threshold" : 100 
                }
            }
        }
    }

brand去重，如果brand的unique value在precision_threshold个以内，cardinality，几乎保证100%准确

cardinality算法，会占用precision_threshold * 8 byte 内存消耗，100 * 8 = 800个字节；
占用内存很小,而且unique value如果的确在值以内，那么可以确保100%准确；数百万的unique value，错误率在5%以内

precision_threshold，值设置的越大，占用内存越大，1000 * 8 = 8000 / 1000 = 8KB，可以确保更多unique value的场景下，100%的准确

#### HyperLogLog++ (HLL)算法性能优化

cardinality底层算法：HLL算法，HLL算法的性能

对所有的uqniue value取hash值，通过hash值近似去求distcint count，存在误差

默认情况下，发送一个cardinality请求的时候，会动态地对所有的field value，取hash值; 将取hash值的操作，前移到建立索引的时候

构建hash

    PUT /tvs
    {
      "mappings": {
          "properties": {
            "brand": {
              "type": "text",
              "fields": {
                "hash": {
                  "type": "murmur3" 
                }
              }
            }
          }
        }
    }
    
基于hash进行去重查询   
    
    GET /tvs/_search
    {
        "size" : 0,
        "aggs" : {
            "distinct_brand" : {
                "cardinality" : {
                  "field" : "brand.hash",
                  "precision_threshold" : 100 
                }
            }
        }
    }

### 12.18 percentiles百分比算法以及网站访问时延统计

需求：比如有一个网站，记录下了每次请求的访问的耗时，需要统计tp50，tp90，tp99

* tp50：50%的请求的耗时最长在多长时间
* tp90：90%的请求的耗时最长在多长时间
* tp99：99%的请求的耗时最长在多长时间

设置mapping
    
    PUT /website
    {
        "mappings": {
          "properties": {
              "latency": {
                  "type": "long"
              },
              "province": {
                  "type": "keyword"
              },
              "timestamp": {
                  "type": "date"
              }
          }
        }
    }

添加数据
    
    POST /website/_bulk
    { "index": {}}
    { "latency" : 105, "province" : "江苏", "timestamp" : "2016-10-28" }
    { "index": {}}
    { "latency" : 83, "province" : "江苏", "timestamp" : "2016-10-29" }
    { "index": {}}
    { "latency" : 92, "province" : "江苏", "timestamp" : "2016-10-29" }
    { "index": {}}
    { "latency" : 112, "province" : "江苏", "timestamp" : "2016-10-28" }
    { "index": {}}
    { "latency" : 68, "province" : "江苏", "timestamp" : "2016-10-28" }
    { "index": {}}
    { "latency" : 76, "province" : "江苏", "timestamp" : "2016-10-29" }
    { "index": {}}
    { "latency" : 101, "province" : "新疆", "timestamp" : "2016-10-28" }
    { "index": {}}
    { "latency" : 275, "province" : "新疆", "timestamp" : "2016-10-29" }
    { "index": {}}
    { "latency" : 166, "province" : "新疆", "timestamp" : "2016-10-29" }
    { "index": {}}
    { "latency" : 654, "province" : "新疆", "timestamp" : "2016-10-28" }
    { "index": {}}
    { "latency" : 389, "province" : "新疆", "timestamp" : "2016-10-28" }
    { "index": {}}
    { "latency" : 302, "province" : "新疆", "timestamp" : "2016-10-29" }

统计数据

    GET /website/_search 
    {
      "size": 0,
      "aggs": {
        "latency_percentiles": {
          "percentiles": {
            "field": "latency",
            "percents": [
              50,
              95,
              99
            ]
          }
        },
        "latency_avg": {
          "avg": {
            "field": "latency"
          }
        }
      }
    }

50%的请求，数值的最大的值是多少，不是完全准确的

    GET /website/_search 
    {
      "size": 0,
      "aggs": {
        "group_by_province": {
          "terms": {
            "field": "province"
          },
          "aggs": {
            "latency_percentiles": {
              "percentiles": {
                "field": "latency",
                "percents": [
                  50,
                  95,
                  99
                ]
              }
            },
            "latency_avg": {
              "avg": {
                "field": "latency"
              }
            }
          }
        }
    }

### 12.19 percentiles rank以及网站访问时延SLA统计    

SLA：就是你提供的服务的标准

我们的网站的提供的访问延时的SLA，确保所有的请求100%，都必须在200ms以内，大公司内，一般都是要求100%在200ms以内

如果超过1s，则需要升级到A级故障，代表网站的访问性能和用户体验急剧下降

需求：在200ms以内的，有百分之多少，在1000毫秒以内的有百分之多少，percentile ranks metric

这个percentile ranks，其实比pencentile还要常用

按照品牌分组，计算，电视机，售价在1000占比，2000占比，3000占比

    GET /website/_search 
    {
      "size": 0,
      "aggs": {
        "group_by_province": {
          "terms": {
            "field": "province"
          },
          "aggs": {
            "latency_percentile_ranks": {
              "percentile_ranks": {
                "field": "latency",
                "values": [
                  200,
                  1000
                ]
              }
            }
          }
        }
      }
    }
    
percentile的优化：TDigest算法，用很多的节点来执行百分比的计算，近似估计，有误差，节点越多，越精准

compression：限制节点数量最多 compression * 20 = 2000个node去计算；默认100；越大，占用内存越多，越精准，性能越差

一个节点占用32字节，100 * 20 * 32 = 64KB

如果你想要percentile算法越精准，compression可以设置的越大

### 12.20 基于doc value正排索引的聚合内部原理

* 聚合分析的内部原理是什么？
* aggs，term，metric avg max这些执行一个聚合操作的时候，内部原理是怎样的呢？
* 用了什么样的数据结构去执行聚合？是不是用的倒排索引？

### 12.21 doc value机制内核级原理深入探秘

#### doc value原理

* （1）index-time生成

PUT/POST的时候，就会生成doc value数据，也就是正排索引

* （2）核心原理与倒排索引类似

正排索引，也会写入磁盘文件中，然后呢，os cache先进行缓存，以提升访问doc value正排索引的性能
如果os cache内存大小不足够放得下整个正排索引，doc value，就会将doc value的数据写入磁盘文件中

* （3）性能问题：给jvm更少内存，64g服务器，给jvm最多16g

es官方是建议，es大量是基于os cache来进行缓存和提升性能的，不建议用jvm内存来进行缓存，那样会导致一定的gc开销和oom问题；
给jvm更少的内存，给os cache更大的内存；64g服务器，给jvm最多16g，几十个g的内存给os cache；
os cache可以提升doc value和倒排索引的缓存和查询效率

#### column压缩

    doc1: 550
    doc2: 550
    doc3: 500

合并相同值，550，doc1和doc2都保留一个550的标识即可

    （1）所有值相同，直接保留单值
    （2）少于256个值，使用table encoding模式：一种压缩方式
    （3）大于256个值，看有没有最大公约数，有就除以最大公约数，然后保留这个最大公约数

    doc1: 36
    doc2: 24

6 --> doc1: 6, doc2: 4 --> 保留一个最大公约数6的标识，6也保存起来

如果没有最大公约数，采取offset结合压缩的方式：

#### disable doc value

如果的确不需要doc value，比如聚合等操作，那么可以禁用，减少磁盘空间占用

    PUT /my_index
    {
      "mappings": {
          "properties": {
            "my_field": {
              "type":       "keyword"
              "doc_values": false 
            }
          }
        }
    }

### 12.22 string field聚合实验以及fielddata原理初探

#### 对于分词的field执行aggregation，发现报错

    GET /test_index/_search 
    {
      "aggs": {
        "group_by_test_field": {
          "terms": {
            "field": "test_field"
          }
        }
      }
    }

对分词的field，直接执行聚合操作，会报错，大概意思是说，你必须要打开fielddata，然后将正排索引数据加载到内存中，才可以对分词的field执行聚合操作，而且会消耗很大的内存

#### 给分词的field，设置fielddata=true，发现可以执行，但是结果似乎不是我们需要的

如果要对分词的field执行聚合操作，必须将fielddata设置为true

    POST /test_index/_mapping
    {
      "properties": {
        "test_field": {
          "type": "text",
          "fielddata": true
        }
      }
    }

    GET /test_index/_search 
    {
      "size": 0, 
      "aggs": {
        "group_by_test_field": {
          "terms": {
            "field": "test_field"
          }
        }
      }
    }
#### 使用内置field不分词，对string field进行聚合

    GET /test_index/_search 
    {
      "size": 0,
      "aggs": {
        "group_by_test_field": {
          "terms": {
            "field": "test_field.keyword"
          }
        }
      }
    }
    
如果对不分词的field执行聚合操作，直接就可以执行，不需要设置fieldata=true

#### 分词field+fielddata的工作原理

doc value --> 不分词的所有field，可以执行聚合操作 --> 如果某个field不分词，那么在创建索引时（index-time）就会自动生成doc value --> 针对这些不分词的field执行聚合操作的时候，自动就会用doc value来执行

分词field，是没有doc value的，在index-time，如果某个field是分词的，那么是不会给它建立doc value正排索引的，因为分词后，占用的空间过于大，所以默认是不支持分词field进行聚合的

分词field默认没有doc value，所以直接对分词field执行聚合操作，是会报错的

对于分词field，必须打开和使用fielddata，完全存在于纯内存中，结构和doc value类似；如果是ngram或者是大量term，那么必将占用大量的内存。

如果一定要对分词的field执行聚合，那么必须将fielddata=true，然后es就会在执行聚合操作的时候，现场将field对应的数据，建立一份fielddata正排索引，fielddata正排索引的结构跟doc value是类似的，
但是只会将fielddata正排索引加载到内存中来，然后基于内存中的fielddata正排索引执行分词field的聚合操作

如果直接对分词field执行聚合，报错，才会让我们开启fielddata=true，告诉我们，会将fielddata uninverted index，正排索引，加载到内存，会耗费内存空间

为什么fielddata必须在内存？因为大家自己思考一下，分词的字符串，需要按照term进行聚合，需要执行更加复杂的算法和操作，如果基于磁盘和os cache，那么性能会很差

### 12.23 fielddata内存控制以及circuit breaker断路器

#### fielddata核心原理

fielddata加载到内存的过程是懒加载的，对一个分词 field执行聚合时，才会加载，而且是field-level加载的；

一个index的一个field，所有doc都会被加载，而不是少数doc；不是index-time创建，是query-time创建

#### fielddata内存限制

* indices.fielddata.cache.size: 20%，超出限制，清除内存已有fielddata数据
* fielddata占用的内存超出了这个比例的限制，那么就清除掉内存中已有的fielddata数据
* 默认无限制，限制内存使用，但是会导致频繁evict和reload，大量IO性能损耗，以及内存碎片和gc

#### 监控fielddata内存使用

    GET /_stats/fielddata?fields=*
    GET /_nodes/stats/indices/fielddata?fields=*
    GET /_nodes/stats/indices/fielddata?level=indices&fields=*

#### circuit breaker

如果一次query load的feilddata超过总内存，就会发生内存溢出（OOM）

circuit breaker会估算query要加载的fielddata大小，如果超出总内存，就短路，query直接失败

    indices.breaker.fielddata.limit：fielddata的内存限制，默认60%
    indices.breaker.request.limit：执行聚合的内存限制，默认40%
    indices.breaker.total.limit：综合上面两个，限制在70%以内

#### fielddata filter的细粒度内存加载控制

    POST /test_index/_mapping
    {
      "properties": {
        "my_field": {
          "type": "text",
          "fielddata": { 
            "filter": {
              "frequency": { 
                "min": 0.01, 
                "min_segment_size": 500  
              }
            }
          }
        }
      }
    }
 
min：仅仅加载至少在1%的doc中出现过的term对应的fielddata

比如说某个值，hello，总共有1000个doc，hello必须在10个doc中出现，那么这个hello对应的fielddata才会加载到内存中来

min_segment_size：少于500 doc的segment不加载fielddata

加载fielddata的时候，也是按照segment去进行加载的，某个segment里面的doc数量少于500个，那么这个segment的fielddata就不加载

一般不会去设置它，知道就好

#### fielddata预加载机制以及序号标记预加载

如果真的要对分词的field执行聚合，那么每次都在query-time现场生产fielddata并加载到内存中来，速度可能会比较慢

我们是不是可以预先生成加载fielddata到内存中来？

#### fielddata预加载

    POST /test_index/_mapping
    {
      "properties": {
        "test_field": {
          "type": "string",
          "fielddata": {
            "loading" : "eager" 
          }
        }
      }
    }

query-time的fielddata生成和加载到内存，变为index-time，建立倒排索引的时候，会同步生成fielddata并且加载到内存中来，
这样的话，对分词field的聚合性能当然会大幅度增强

#### 序号标记预加载

global ordinal原理解释

    doc1: status1
    doc2: status2
    doc3: status2
    doc4: status1

有很多重复值的情况，会进行global ordinal标记，类似下面

    status1 --> 0
    status2 --> 1

这样doc中可以这样存储

    doc1: 0
    doc2: 1
    doc3: 1
    doc4: 0

建立的fielddata也会是这个样子的，这样的好处就是减少重复字符串的出现的次数，减少内存的消耗

    POST /test_index/_mapping
    {
      "properties": {
        "test_field": {
          "type": "string",
          "fielddata": {
            "loading" : "eager_global_ordinals" 
          }
        }
      }
    }

### 12.24 海量bucket优化机制：从深度优先到广度优先

当buckets数量特别多的时候，深度优先和广度优先的原理

## 十三、数据建模实战

### 13.1 关系型与document类型数据模型对比

关系型数据库的数据模型：三范式 --> 将每个数据实体拆分为一个独立的数据表，同时使用主外键关联关系将多个数据表关联起来 --> 确保没有任何冗余的数据；

es的数据模型：类似于面向对象的数据模型，将所有由关联关系的数据，放在一个doc json类型数据中，整个数据的关系，还有完整的数据，都放在了一起。

### 13.2 通过应用层join实现用户与博客的关联

在构造数据模型的时候，还是将有关联关系的数据，然后分割为不同的实体，类似于关系型数据库中的模型   
   
用户信息：
    
    PUT /website-users/1 
    {
      "name":     "小鱼儿",
      "email":    "xiaoyuer@sina.com",
      "birthday":      "1980-01-01"
    }

用户发布的博客

    PUT /website-blogs/1
    {
      "title":    "我的第一篇博客",
      "content":     "这是我的第一篇博客，开通啦！！！"
      "userId":     1 
    }


在进行查询时就属于应用层的join，在应用层先查出一份数据（查用户信息），然后再查出一份数据（查询博客信息），进行关联

优点和缺点

* 优点：数据不冗余，维护方便
* 缺点：应用层join，如果关联数据过多，导致查询过大，性能很差

### 13.3 通过数据冗余实现用户与博客的关联
    
    PUT /website-users/1
    {
      "name":     "小鱼儿",
      "email":    "xiaoyuer@sina.com",
      "birthday":      "1980-01-01"
    }

这里面冗余用户名字段

    PUT /website-blogs/_doc/1
    {
      "title": "小鱼儿的第一篇博客",
      "content": "大家好，我是小鱼儿。。。",
      "userInfo": {
        "userId": 1,
        "username": "小鱼儿"
      }
    }

冗余数据，就是将可能会进行搜索的条件和要搜索的数据，放在一个doc中

优点和缺点

* 优点：性能高，不需要执行两次搜索
* 缺点：数据冗余，维护成本高；比如某个字段更新后，需要更新相关的doc

### 13.4 对每个用户发表的博客进行分组

添加测试数据：

    POST /website_users/_doc/3
    {
      "name": "黄药师",
      "email": "huangyaoshi@sina.com",
      "birthday": "1970-10-24"
    }
    
    PUT /website_blogs/_doc/3
    {
      "title": "我是黄药师",
      "content": "我是黄药师啊，各位同学们！！！",
      "userInfo": {
        "userId": 1,
        "userName": "黄药师"
      }
    }
    
    PUT /website_users/_doc/2
    {
      "name": "花无缺",
      "email": "huawuque@sina.com",
      "birthday": "1980-02-02"
    }
    
    PUT /website_blogs/_doc/4
    {
      "title": "花无缺的身世揭秘",
      "content": "大家好，我是花无缺，所以我的身世是。。。",
      "userInfo": {
        "userId": 2,
        "userName": "花无缺"
      }
    }

对每个用户发表的博客进行分组

    GET /website_blogs/_search 
    {
      "size": 0, 
      "aggs": {
        "group_by_username": {
          "terms": {
            "field": "userInfo.userName.keyword"
          },
          "aggs": {
            "top_blogs": {
              "top_hits": {
                "_source": {
                  "includes": "title"
                }, 
                "size": 5
              }
            }
          }
        }
      }
    }

### 13.5 对文件系统进行数据建模以及文件搜索实战

数据建模，对类似文件系统这种的有多层级关系的数据进行建模

#### 文件系统数据构造

    PUT /fs
    {
      "settings": {
        "analysis": {
          "analyzer": {
            "paths": { 
              "tokenizer": "path_hierarchy"
            }
          }
        }
      }
    }

path_hierarchy示例说明：当文件路径为`/a/b/c/d` 执行path_hierarchy建立如下的分词 `/a/b/c/d`, `/a/b/c`, `/a/b`, `/a`

    PUT /fs/_mapping
    {
      "properties": {
        "name": { 
          "type":  "keyword"
        },
        "path": { 
          "type":  "keyword",
          "fields": {
            "tree": { 
              "type":     "text",
              "analyzer": "paths"
            }
          }
        }
      }
    }

添加数据

    PUT /fs/_doc/1
    {
      "name":     "README.txt", 
      "path":     "/workspace/projects/helloworld", 
      "contents": "这是我的第一个elasticsearch程序"
    }

#### 对文件系统执行搜索

文件搜索需求：查找一份，内容包括elasticsearch，在/workspace/projects/hellworld这个目录下的文件

    GET /fs/_search 
    {
      "query": {
        "bool": {
          "must": [
            {
              "match": {
                "contents": "elasticsearch"
              }
            },
            {
              "constant_score": {
                "filter": {
                  "term": {
                    "path": "/workspace/projects/helloworld"
                  }
                }
              }
            }
          ]
        }
      }
    }

搜索需求2：搜索/workspace目录下，内容包含elasticsearch的所有的文件

    GET /fs/_search 
    {
      "query": {
        "bool": {
          "must": [
            {
              "match": {
                "contents": "elasticsearch"
              }
            },
            {
              "constant_score": {
                "filter": {
                  "term": {
                    "path.tree": "/workspace"
                  }
                }
              }
            }
          ]
        }
      }
    }

### 13.6 基于全局锁实现悲观锁并发控制

如果多个线程，都过来要给/workspace/projects/helloworld下的README.txt修改文件名，需要处理出现多线程的并发安全问题；

#### 全局锁的上锁

    PUT /fs/_doc/global/_create
    {}

* fs: 你要上锁的那个index
* _doc: 就是你指定的一个对这个index上全局锁的一个type
* global: 就是你上的全局锁对应的这个doc的id
* _create：强制必须是创建，如果/fs/lock/global这个doc已经存在，那么创建失败，报错


删除锁

    DELETE /fs/_doc/global

这个其实就是插入了一条带ID的数据，操作完了再删除，这样其他的就可以继续操作（如果程序挂掉了,没有来得及删除锁咋整??）

### 13.7 基于document锁实现悲观锁并发控制

通过脚本来加锁，锁具体某个ID的文档

    POST /fs/_doc/1/_update
    {
      "upsert": { "process_id": 123 },
      "script": "if ( ctx._source.process_id != process_id ) { assert false }; ctx.op = 'noop';"
      "params": {
        "process_id": 123
      }
    }

### 13.8 基于共享锁和排他锁实现悲观锁并发控制

共享锁和排他锁的说明（相当于读写分离）：

* 共享锁：这份数据是共享的，然后多个线程过来，都可以获取同一个数据的共享锁，然后对这个数据执行读操作
* 排他锁：是排他的操作，只能一个线程获取排他锁，然后执行增删改操作

### 13.9 基于nested object实现博客与评论嵌套关系

#### 做一个实验，引出来为什么需要nested object

    PUT /website/_doc/6
    {
      "title": "花无缺发表的一篇帖子",
      "content":  "我是花无缺，大家要不要考虑一下投资房产和买股票的事情啊。。。",
      "tags":  [ "投资", "理财" ],
      "comments": [ 
        {
          "name":    "小鱼儿",
          "comment": "什么股票啊？推荐一下呗",
          "age":     28,
          "stars":   4,
          "date":    "2016-09-01"
        },
        {
          "name":    "黄药师",
          "comment": "我喜欢投资房产，风，险大收益也大",
          "age":     31,
          "stars":   5,
          "date":    "2016-10-22"
        }
      ]
    }

被年龄是28岁的黄药师评论过的博客，搜索

    GET /website/_search
    {
      "query": {
        "bool": {
          "must": [
            { "match": { "comments.name": "黄药师" }},
            { "match": { "comments.age":  28      }} 
          ]
        }
      }
    }

这样的查询结果不是我们期望的


object类型底层数据结构，会将一个json数组中的数据，进行扁平化；类似：

    {
      "title":            [ "花无缺", "发表", "一篇", "帖子" ],
      "content":             [ "我", "是", "花无缺", "大家", "要不要", "考虑", "一下", "投资", "房产", "买", "股票", "事情" ],
      "tags":             [ "投资", "理财" ],
      "comments.name":    [ "小鱼儿", "黄药师" ],
      "comments.comment": [ "什么", "股票", "推荐", "我", "喜欢", "投资", "房产", "风险", "收益", "大" ],
      "comments.age":     [ 28, 31 ],
      "comments.stars":   [ 4, 5 ],
      "comments.date":    [ 2016-09-01, 2016-10-22 ]
    }

#### 引入nested object类型，来解决object类型底层数据结构导致的问题

修改mapping，将comments的类型从object设置为nested

    PUT /website
    {
      "mappings": {
          "properties": {
            "comments": {
              "type": "nested", 
              "properties": {
                "name":    { "type": "text"  },
                "comment": { "type": "text"  },
                "age":     { "type": "short"   },
                "stars":   { "type": "short"   },
                "date":    { "type": "date"    }
              }
            }
          }
        }
    }

执行查询

    GET /website/_search 
    {
      "query": {
        "bool": {
          "must": [
            {
              "match": {
                "title": "花无缺"
              }
            },
            {
              "nested": {
                "path": "comments",
                "query": {
                  "bool": {
                    "must": [
                      {
                        "match": {
                          "comments.name": "黄药师"
                        }
                      },
                      {
                        "match": {
                          "comments.age": 28
                        }
                      }
                    ]
                  }
                }
              }
            }
          ]
        }
      }
    }

### 13.10 对嵌套的博客评论数据进行聚合分析

聚合数据分析的需求1：按照评论日期进行bucket划分，然后拿到每个月的评论的评分的平均值

    GET /website/_search 
    {
      "size": 0, 
      "aggs": {
        "comments_path": {
          "nested": {
            "path": "comments"
          }, 
          "aggs": {
            "group_by_comments_date": {
              "date_histogram": {
                "field": "comments.date",
                "calendar_interval": "month",
                "format": "yyyy-MM"
              },
              "aggs": {
                "avg_stars": {
                  "avg": {
                    "field": "comments.stars"
                  }
                }
              }
            }
          }
        }
      }
    }

查询示例2

    GET /website/_search 
    {
      "size": 0,
      "aggs": {
        "comments_path": {
          "nested": {
            "path": "comments"
          },
          "aggs": {
            "group_by_comments_age": {
              "histogram": {
                "field": "comments.age",
                "interval": 10
              },
              "aggs": {
                "reverse_path": {
                  "reverse_nested": {}, 
                  "aggs": {
                    "group_by_tags": {
                      "terms": {
                        "field": "tags.keyword"
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }

## 十四、高级操作（使用较少）

### 14.1 基于term vector深入探查数据的情况

    GET /twitter/tweet/1/_termvectors
    GET /twitter/tweet/1/_termvectors?fields=text

    GET /my_index/my_type/1/_termvectors
    {
      "fields" : ["fullname"],
      "offsets" : true,
      "positions" : true,
      "term_statistics" : true,
      "field_statistics" : true
    }

### 14.2 深入剖析搜索结果的highlight高亮显示

#### 简单示例

    GET /blog_website/_search 
    {
      "query": {
        "match": {
          "title": "博客"
        }
      },
      "highlight": {
        "fields": {
          "title": {}
        }
      }
    }

<em></em>表现，会变成红色，所以说你的指定的field中，如果包含了那个搜索词的话，就会在那个field的文本中，对搜索词进行红色的高亮显示

    GET /blog_website/blogs/_search 
    {
      "query": {
        "bool": {
          "should": [
            {
              "match": {
                "title": "博客"
              }
            },
            {
              "match": {
                "content": "博客"
              }
            }
          ]
        }
      },
      "highlight": {
        "fields": {
          "title": {},
          "content": {}
        }
      }
    }

> highlight中的field，必须跟query中的field一一对齐的

#### 三种highlight介绍
     
plain highlight，lucene highlight，默认
     
posting highlight，index_options=offsets
     
* （1）性能比plain highlight要高，因为不需要重新对高亮文本进行分词
* （2）对磁盘的消耗更少
* （3）将文本切割为句子，并且对句子进行高亮，效果更好


    GET /blog_website/_search 
    {
      "query": {
        "match": {
          "content": "博客"
        }
      },
      "highlight": {
        "fields": {
          "content": {}
        }
      }
    }

其实可以根据你的实际情况去考虑，一般情况下，用plain highlight也就足够了，不需要做其他额外的设置；
如果对高亮的性能要求很高，可以尝试启用posting highlight；
如果field的值特别大，超过了1M，那么可以用fast vector highlight

#### 设置高亮html标签，默认是<em>标签

    GET /blog_website/_search 
    {
      "query": {
        "match": {
          "content": "博客"
        }
      },
      "highlight": {
        "pre_tags": ["<tag1>"],
        "post_tags": ["</tag2>"], 
        "fields": {
          "content": {
            "type": "plain"
          }
        }
      }
    }

#### 高亮片段fragment的设置

    GET /blog_website/_search
    {
        "query" : {
            "match": { "user": "kimchy" }
        },
        "highlight" : {
            "fields" : {
                "content" : {"fragment_size" : 150, "number_of_fragments" : 3, "no_match_size": 150 }
            }
        }
    }

fragment_size: 你一个Field的值，比如有长度是1万，但是你不可能在页面上显示这么长；设置要显示出来的fragment文本判断的长度，默认是100；

number_of_fragments：你可能你的高亮的fragment文本片段有多个片段，你可以指定就显示几个片段

### 14.3 使用search template将搜索模板化

搜索模板，search template，高级功能，就可以将我们的一些搜索进行模板化，然后的话，每次执行这个搜索，就直接调用模板，给传入一些参数就可以了

#### 基础示例

    GET /website_blogs/_search/template
    {
      "source": {
        "query": {
          "match": {
            "{{field}}": "{{value}}"
          }
        }
      },
      "params": {
        "field": "title",
        "value": "黄药师"
      }
    }

这个部分可以改为脚本文件，替换为"file":"search_by_title"

        "query": {
          "match": {
            "{{field}}": "{{value}}"
          }
        }
        
#### 使用josn串

    GET /website_blogs/_search/template
    {
      "source": "{\"query\": {\"match\": {{#toJson}}matchCondition{{/toJson}}}}",
      "params": {
        "matchCondition": {
          "title": "黄药师"
        }
      }
    }

#### 使用join

    GET /website_blogs/_search/template
    {
      "source": {
        "query": {
          "match": {
            "title": "{{#join delimiter=' '}}titles{{/join delimiter=' '}}"
          }
        }
      },
      "params": {
        "titles": ["黄药师", "花无缺"]
      }
    }

类比：

    GET /website_blogs/_search/
    {
      "query": { 
        "match" : { 
          "title" : "黄药师 花无缺" 
        } 
      }
    }

#### conditional

es的config/scripts目录下，预先保存这个复杂的模板，后缀名是.mustache，文件名是conditonal

内容如下：

    {
      "query": {
        "bool": {
          "must": {
            "match": {
              "line": "{{text}}" 
            }
          },
          "filter": {
            {{#line_no}} 
              "range": {
                "line_no": {
                  {{#start}} 
                    "gte": "{{start}}" 
                    {{#end}},{{/end}} 
                  {{/start}} 
                  {{#end}} 
                    "lte": "{{end}}" 
                  {{/end}} 
                }
              }
            {{/line_no}} 
          }
        }
      }
    }
    
查询语句
    
    GET /website_blogs/_search/template
    {
      "file": "conditional",
      "params": {
        "text": "博客",
        "line_no": true,
        "start": 1,
        "end": 10
      }
    }

#### 保存search template

config/scripts，.mustache 

提供一个思路

比如一般在大型的团队中，可能不同的人，都会想要执行一些类似的搜索操作；
这个时候，有一些负责底层运维的一些同学，就可以基于search template，封装一些模板出来，然后是放在各个es进程的scripts目录下的；
其他的团队，其实就不用各个团队自己反复手写复杂的通用的查询语句了，直接调用某个搜索模板，传入一些参数就好了

### 14.4 基于completion suggest实现搜索提示

suggest，completion suggest，自动完成，搜索推荐，搜索提示 --> 自动完成，auto completion

比如我们在百度，搜索，你现在搜索“大话西游” --> 百度，自动给你提示，“大话西游电影”，“大话西游小说”， “大话西游手游”

不需要所有想要的输入文本都输入完，搜索引擎会自动提示你可能是你想要搜索的那个文本

#### 初始化数据

    PUT /news_website
    {
      "mappings": {
          "properties" : {
            "title" : {
              "type": "text",
              "analyzer": "ik_max_word",
              "fields": {
                "suggest" : {
                  "type" : "completion",
                  "analyzer": "ik_max_word"
                }
              }
            },
            "content": {
              "type": "text",
              "analyzer": "ik_max_word"
            }
          }
        }
    }
    
completion，es实现的时候，是非常高性能的，其构建不是倒排索引，也不是正拍索引，就是单独用于进行前缀搜索的一种特殊的数据结构，
而且会全部放在内存中，所以auto completion进行的前缀搜索提示，性能是非常高的。

    PUT /news_website/_doc/1
    {
      "title": "大话西游电影",
      "content": "大话西游的电影时隔20年即将在2017年4月重映"
    }
    PUT /news_website/_doc/2
    {
      "title": "大话西游小说",
      "content": "某知名网络小说作家已经完成了大话西游同名小说的出版"
    }
    PUT /news_website/_doc/3
    {
      "title": "大话西游手游",
      "content": "网易游戏近日出品了大话西游经典IP的手游，正在火爆内测中"
    }

#### 执行查询

    GET /news_website/_search
    {
      "suggest": {
        "my-suggest" : {
          "prefix" : "大话西游",
          "completion" : {
            "field" : "title.suggest"
          }
        }
      }
    }

直接查询

    GET /news_website/_search
    {
      "query": {
        "match": {
          "content": "大话西游电影"
        }
      }
    }

### 14.5 使用动态映射模板定制自己的映射策略

比如我们本来没有某个type，或者没有某个field，但是希望在插入数据的时候，es自动为我们做一个识别，动态映射出这个type的mapping，包括每个field的数据类型，一般用的动态映射，dynamic mapping

这里有个问题，如果我们对dynamic mapping有一些自己独特的需求，比如es默认的，如经过识别到一个数字，field: 10，默认是搞成这个field的数据类型是long，再比如说，如果我们弄了一个field : "10"，默认就是text，还会带一个keyword的内置field。我们没法改变。

但是我们现在就是希望动态映射的时候，根据我们的需求去映射，而不是让es自己按照默认的规则去玩儿

dyanmic mapping template，动态映射模板

我们自己预先定义一个模板，然后插入数据的时候，相关的field，如果能够根据我们预先定义的规则，匹配上某个我们预定义的模板，那么就会根据我们的模板来进行mapping，决定这个Field的数据类型

#### 根据类型匹配映射模板

动态映射模板，有两种方式，第一种，是根据新加入的field的默认的数据类型，来进行匹配，匹配上某个预定义的模板；
第二种，是根据新加入的field的名字，去匹配预定义的名字，或者去匹配一个预定义的通配符，然后匹配上某个预定义的模板


##### 根据默认类型来

    PUT my_index
    {
      "mappings": {
        "dynamic_templates": [
          {
            "integers": {
              "match_mapping_type": "long",
              "mapping": {
                "type": "integer"
              }
            }
          },
          {
            "strings": {
              "match_mapping_type": "string",
              "mapping": {
                "type": "text",
                "fields": {
                  "raw": {
                    "type": "keyword",
                    "ignore_above": 500
                  }
                }
              }
            }
          }
        ]
      }
    }

##### 根据字段名配映射模板

    PUT /my_index 
    {
      "mappings": {
        "dynamic_templates": [
          {
            "string_as_integer": {
              "match_mapping_type": "string",
              "match": "long_*",
              "unmatch": "*_text",
              "mapping": {
                "type": "integer"
              }
            }
          }
        ]
      }
    }

### 14.6 学习使用geo point地理位置数据类型

设置类型

    PUT /hotel
    {
      "mappings": {
        "properties": {
          "location":{
            "type": "geo_point"
          }
        }
      }
    }

添加数据
    
    PUT /hotel/_doc/1
    {
      "name":"四季酒店",
      "location":{
        "lat":30.558456,
        "lon":104.073273
      }
    }

> lat: 纬度，lon：经度

    PUT /hotel/_doc/2
    {
      "name":"成都威斯凯尔凯特酒店",
      "location":"30.5841,104.061939"
    }

    PUT /hotel/_doc/3
    {
      "name":"北京天安门广场",
      "location":{
        "lat":39.909187,
        "lon":116.397451
      }
    }

> 纬度在前，经度在后    
    
#### 查询范围内的数据（左上角和右下角的点组成的矩形内的坐标）

    GET /hotel/_search
    {
      "query": {
        "geo_bounding_box": {
          "location": {
            "top_left": {
              "lat": 40,
              "lon": 100
            },
            "bottom_right":{
               "lat": 30,
              "lon": 106
            }
          }
        }
      }
    }       

#### 查询包含成都，且在指定区域的数据

    GET /hotel/_search
    {
      "query": {
        "bool": {
          "must": [
            {
              "match": {
                "name": "成都"
              }
            }
          ],
          "filter": {
            "geo_bounding_box": {
              "location": {
                "top_left": {
                  "lat": 40,
                  "lon": 100
                },
                "bottom_right": {
                  "lat": 30,
                  "lon": 106
                }
              }
            }
          }
        }
      }
    } 

#### 搜索多个点组成的多边型内

    GET /hotel/_search
    {
      "query": {
        "bool": {
          "must": [
            {"match_all": {}}
          ],
          "filter": [
            {
            "geo_polygon": {
              "location": {
                "points": [
                  {
                    "lat": 40,
                  "lon": 100
                  },
                  {
                   "lat": 30,
                  "lon": 106
                  },
                  {
                   "lat": 35,
                  "lon": 120
                  }
                ]
              }
            }
            }
          ]
        }
      }
    }    

#### 搜索指定坐标100km范围内的 

    GET /hotel/_search
    {
      "query": {
        "bool": {
          "must": [
            {
              "match_all": {}
            }
          ],
          "filter": [
            {
              "geo_distance": {
                "distance": "100km",
           
                "location": {
                  "lat": 30,
                  "lon": 116
                }
              }
            }
          ]
        }
      }
    } 

#### 统计距离100~300米内的酒店数

    GET /hotel/_search
    {
    "size": 0, 
      "aggs": {
        "agg_by_distance_range": {
          "geo_distance": {
            "field": "location",
            "origin": {
              "lat": 30,
              "lon": 106
            },
            "unit": "mi", 
            "ranges": [
              {
                "from": 100,
                "to": 300
              }
            ]
          }
        }
      }
    }

## 十五、熟练掌握ES Java API

### 15.1 集群自动探查以及汽车零售店案例背景

#### client集群自动探查

默认情况下，是根据我们手动指定的所有节点，依次轮询这些节点，来发送各种请求的，如下面的代码，我们可以手动为client指定多个节点

    RestHighLevelClient client = new RestHighLevelClient(RestClient.builder(new HttpHost("192.168.6.1", 9200)
        , new HttpHost("192.168.6.2", 9200)));
        
但是问题是，如果我们有成百上千个节点呢？难道也要这样手动添加吗？

因此es client提供了一种集群节点自动探查的功能，打开这个自动探查机制以后，es client会根据我们手动指定的几个节点连接过去，
然后通过集群状态自动获取当前集群中的所有data node，然后用这份完整的列表更新自己内部要发送请求的node list。
默认每隔5秒钟，就会更新一次node list。

        // 老版本的写法
        Settings settings = Settings.builder()
                .put("cluster.name", "docker-cluster")
                // 设置集群节点自动发现
                .put("client.transport.sniff", true)
                .build();

注意，es client是不会将Master node纳入node list的，因为要避免给master node发送搜索等请求。

这样的话，我们其实直接就指定几个master node，或者1个node就好了，client会自动去探查集群的所有节点，而且每隔5秒还会自动刷新。

### 15.2 基于upsert实现汽车最新价格的调整

建立mapper

    PUT /car_shop
    {
      "mappings": {
          "properties": {
            "brand": {
              "type": "text",
              "analyzer": "ik_max_word",
              "fields": {
                "raw": {
                  "type": "keyword"
                }
              }
            },
            "name": {
              "type": "text",
              "analyzer": "ik_max_word",
              "fields": {
                "raw": {
                  "type": "keyword"
                }
              }
            }
          }
        }
    }

Java代码实现存在则更新否则添加

    IndexRequest indexRequest = new IndexRequest("car_shop");
            indexRequest.id("1");
            indexRequest.source(XContentFactory.jsonBuilder()
                    .startObject()
                    .field("brand", "宝马")
                    .field("name", "宝马320")
                    .field("price", 320000)
                    .field("produce_date", "2020-01-01")
                    .endObject());
    
    UpdateRequest updateRequest = new UpdateRequest("car_shop", "1");
    updateRequest.doc(XContentFactory.jsonBuilder()
            .startObject()
            .field("price", 320000)
            .endObject()).upsert(indexRequest);

    UpdateResponse response = restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);
    System.out.println(response.getResult());

### 15.3 基于mget实现多辆汽车的配置与价格对比

场景：一般我们都可以在一些汽车网站上，或者在混合销售多个品牌的汽车4S店的内部，都可以在系统里调出来多个汽车的信息，放在网页上，进行对比

mget：一次性将多个document的数据查询出来，放在一起显示。

    PUT /car_shop/_doc/2
    {
        "brand": "奔驰",
        "name": "奔驰C200",
        "price": 350000,
        "produce_date": "2020-01-05"
    }

Java代码：

    MultiGetRequest multiGetRequest = new MultiGetRequest();
    multiGetRequest.add("car_shop", "1");
    multiGetRequest.add("car_shop", "2");

    MultiGetResponse multiGetResponse = restHighLevelClient.mget(multiGetRequest, RequestOptions.DEFAULT);
    MultiGetItemResponse[] responses = multiGetResponse.getResponses();
    for(MultiGetItemResponse response:responses){
        System.out.println(response.getResponse().getSourceAsMap());
    }

### 15.4 基于bulk实现多4S店销售数据批量上传

业务场景：有一个汽车销售公司，拥有很多家4S店，这些4S店的数据，都会在一段时间内陆续传递过来，汽车的销售数据，
现在希望能够在内存中缓存比如1000条销售数据，然后一次性批量上传到es中去。

Java代码：

    BulkRequest bulkRequest = new BulkRequest();

    // 添加数据
    JSONObject car = new JSONObject();
    car.put("brand", "奔驰");
    car.put("name", "奔驰C200");
    car.put("price", 350000);
    car.put("produce_date", "2020-01-05");
    car.put("sale_price", 360000);
    car.put("sale_date", "2020-02-03");
    bulkRequest.add(new IndexRequest("car_sales").id("3").source(car.toJSONString(), XContentType.JSON));

    // 更新数据
    bulkRequest.add(new UpdateRequest("car_shop", "2").doc(jsonBuilder()
            .startObject()
            .field("sale_price", "290000")
            .endObject()));

    // 删除数据
    bulkRequest.add(new DeleteRequest("car_shop").id("1"));

    BulkResponse bulk = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
    System.out.println(bulk.hasFailures() +" " +bulk.buildFailureMessage());

### 15.5 基于scroll实现月度销售数据批量下载

当需要从es中下载大批量的数据时，比如说做业务报表时需要将数据导出到Excel中，如果数据有几十万甚至是上百万条数据，此时可以使用scroll对大量的数据批量的获取和处理


    // 创建查询请求，设置index
    SearchRequest searchRequest = new SearchRequest("car_shop");
    // 设定滚动时间间隔,60秒,不是处理查询结果的所有文档的所需时间
    // 游标查询的过期时间会在每次做查询的时候刷新，所以这个时间只需要足够处理当前批的结果就可以了
    searchRequest.scroll(TimeValue.timeValueMillis(60000));

    // 构建查询条件
    SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
    searchSourceBuilder.query(QueryBuilders.matchQuery("brand", "奔驰"));
    // 每个批次实际返回的数量
    searchSourceBuilder.size(2);
    searchRequest.source(searchSourceBuilder);

    SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

    // 获取第一页的
    String scrollId = searchResponse.getScrollId();
    SearchHit[] searchHits = searchResponse.getHits().getHits();

    int page = 1;
    //遍历搜索命中的数据，直到没有数据
    while (searchHits != null && searchHits.length > 0) {
        System.out.println(String.format("--------第%s页-------", page++));
        for (SearchHit searchHit : searchHits) {
            System.out.println(searchHit.getSourceAsString());
        }
        System.out.println("=========================");

        SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId);
        scrollRequest.scroll(TimeValue.timeValueMillis(60000));
        try {
            searchResponse = restHighLevelClient.scroll(scrollRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }

        scrollId = searchResponse.getScrollId();
        searchHits = searchResponse.getHits().getHits();
    }

    // 清除滚屏任务
    ClearScrollRequest clearScrollRequest = new ClearScrollRequest();
    // 也可以选择setScrollIds()将多个scrollId一起使用
    clearScrollRequest.addScrollId(scrollId);
    ClearScrollResponse clearScrollResponse = restHighLevelClient.clearScroll(clearScrollRequest,RequestOptions.DEFAULT);
    System.out.println("succeeded:" + clearScrollResponse.isSucceeded());

> 所有数据获取完毕之后，需要手动清理掉 scroll_id 。
> 虽然es 会有自动清理机制，但是 scroll_id 的存在会耗费大量的资源来保存一份当前查询结果集映像，并且会占用文件描述符。所以用完之后要及时清理

### 15.6 基于search template实现按品牌分页查询模板

    Map<String, Object> params = new HashMap<>(1);
    params.put("brand", "奔驰");

    SearchTemplateRequest templateRequest = new SearchTemplateRequest();
    templateRequest.setScript("{\n" +
            "  \"query\": {\n" +
            "    \"match\": {\n" +
            "      \"brand\": \"{{brand}}\" \n" +
            "    }\n" +
            "  }\n" +
            "}\n");
    templateRequest.setScriptParams(params);
    templateRequest.setScriptType(ScriptType.INLINE);
    templateRequest.setRequest(new SearchRequest("car_shop"));

    SearchTemplateResponse templateResponse = restHighLevelClient.searchTemplate(templateRequest, RequestOptions.DEFAULT);
    SearchHit[] hits = templateResponse.getResponse().getHits().getHits();
    if(null!=hits && hits.length!=0){
        for (SearchHit hit : hits) {
            System.out.println(hit.getSourceAsString());
        }
    }else {
        System.out.println("无符合条件的数据");
    }

### 15.7 对汽车品牌进行全文检索、精准查询和前缀搜索

    @Test
    public void fullSearch() throws IOException {
        
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchQuery("brand", "奔驰"));
        search(searchSourceBuilder);
        System.out.println("-----------------------------");

        searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.multiMatchQuery("宝马", "brand", "name"));
        search(searchSourceBuilder);
        System.out.println("-----------------------------");

        searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.prefixQuery("name", "奔"));
        search(searchSourceBuilder);
        System.out.println("-----------------------------");

    }

    private void search(SearchSourceBuilder searchSourceBuilder) throws IOException {
        SearchRequest searchRequest = new SearchRequest("car_shop");
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        SearchHit[] searchHits = searchResponse.getHits().getHits();
        if(searchHits!=null && searchHits.length!=0){
            for (SearchHit searchHit : searchHits) {
                System.out.println(searchHit.getSourceAsString());
            }
        }
    }

### 15.8 对汽车品牌进行多种的条件组合搜索

    SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.boolQuery()
                .must(QueryBuilders.matchQuery("brand", "奔驰"))
                .mustNot(QueryBuilders.termQuery("name.raw", "奔驰C203"))
                .should(QueryBuilders.termQuery("produce_date", "2020-01-02"))
                .filter(QueryBuilders.rangeQuery("price").gte("280000").lt("500000"))
        );
        
    SearchRequest searchRequest = new SearchRequest("car_shop");
    searchRequest.source(searchSourceBuilder);
    SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
    SearchHit[] searchHits = searchResponse.getHits().getHits();
    if(searchHits!=null && searchHits.length!=0){
     for (SearchHit searchHit : searchHits) {
         System.out.println(searchHit.getSourceAsString());
     }
    }

### 基于地理位置对周围汽车4S店进行搜索

需要将字段类型设置坐标类型

    POST /car_shop/_mapping
    {
      "properties": {
          "pin": {
              "properties": {
                  "location": {
                      "type": "geo_point"
                  }
              }
          }
      }
    }

添加数据
    
    PUT /car_shop/_doc/5
    {
        "name": "上海至全宝马4S店",
        "pin" : {
            "location" : {
                "lat" : 40.12,
                "lon" : -71.34
            }
        }
    }

#### 搜索两个坐标点组成的一个区域

    SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
    searchSourceBuilder.query(QueryBuilders.geoBoundingBoxQuery("pin.location")
            .setCorners(40.73, -74.1, 40.01, -71.12));

#### 指定一个区域，由三个坐标点，组成，比如上海大厦，东方明珠塔，上海火车站

    searchSourceBuilder = new SearchSourceBuilder();
    List<GeoPoint> points = new ArrayList<>();
    points.add(new GeoPoint(40.73, -74.1));
    points.add(new GeoPoint(40.01, -71.12));
    points.add(new GeoPoint(50.56, -90.58));
    searchSourceBuilder.query(QueryBuilders.geoPolygonQuery("pin.location", points));

#### 搜索距离当前位置在200公里内的4s店

    searchSourceBuilder = new SearchSourceBuilder();
    searchSourceBuilder.query(QueryBuilders.geoDistanceQuery("pin.location")
            .point(40, -70).distance(200, DistanceUnit.KILOMETERS));    


     