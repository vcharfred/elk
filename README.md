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









