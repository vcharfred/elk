package top.vchar.crud;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpHost;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.get.MultiGetItemResponse;
import org.elasticsearch.action.get.MultiGetRequest;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.*;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.script.mustache.SearchTemplateRequest;
import org.elasticsearch.script.mustache.SearchTemplateResponse;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

/**
 * <p> 汽车案例 </p>
 *
 * @author vchar fred
 * @version 1.0
 * @create_date 2020/9/11
 */
public class CardDemo {

    private RestHighLevelClient restHighLevelClient = null;

    @Before
    public void init() {
        HttpHost[] httpHost = {HttpHost.create("192.168.111.71:9200")};
        restHighLevelClient = new RestHighLevelClient(RestClient.builder(httpHost));
    }

    @Before
    public void destroy() throws IOException {
        if (restHighLevelClient != null) {
            restHighLevelClient.close();
        }
    }

    /**
     * 用一个upsert语法实现数据存在则更新，不存在则添加
     */
    @Test
    public void addOrUpdate() throws IOException {
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
    }

    /**
     * 批量查询mget
     */
    @Test
    public void mget() throws IOException {

        MultiGetRequest multiGetRequest = new MultiGetRequest();
        multiGetRequest.add("car_shop", "1");
        multiGetRequest.add("car_shop", "2");

        MultiGetResponse multiGetResponse = restHighLevelClient.mget(multiGetRequest, RequestOptions.DEFAULT);
        MultiGetItemResponse[] responses = multiGetResponse.getResponses();
        for (MultiGetItemResponse response : responses) {
            System.out.println(response.getResponse().getSourceAsMap());
        }
    }

    /**
     * 使用bulk批量推送数据
     */
    @Test
    public void bulk() throws IOException {
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
        System.out.println(bulk.hasFailures() + " " + bulk.buildFailureMessage());
    }

    @Test
    public void scroll() throws IOException {

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
        ClearScrollResponse clearScrollResponse = restHighLevelClient.clearScroll(clearScrollRequest, RequestOptions.DEFAULT);
        System.out.println("succeeded:" + clearScrollResponse.isSucceeded());

    }

    /**
     * 执行模版查询
     */
    @Test
    public void searchTemplate() throws IOException {
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
        if (null != hits && hits.length != 0) {
            for (SearchHit hit : hits) {
                System.out.println(hit.getSourceAsString());
            }
        } else {
            System.out.println("无符合条件的数据");
        }
    }

    /**
     * 全文搜索示例
     */
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
        if (searchHits != null && searchHits.length != 0) {
            for (SearchHit searchHit : searchHits) {
                System.out.println(searchHit.getSourceAsString());
            }
        }
    }


    /**
     * 组合查询
     */
    @Test
    public void boolQuery() throws IOException {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.boolQuery()
                .must(QueryBuilders.matchQuery("brand", "奔驰"))
                .mustNot(QueryBuilders.termQuery("name.raw", "奔驰C203"))
                .should(QueryBuilders.termQuery("produce_date", "2020-01-02"))
                .filter(QueryBuilders.rangeQuery("price").gte("280000").lt("500000"))
        );
        search(searchSourceBuilder);
    }

    /**
     * 坐标查询
     */
    @Test
    public void geoPoint() throws IOException {

        // 搜索两个坐标点组成的一个区域
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.geoBoundingBoxQuery("pin.location")
                .setCorners(40.73, -74.1, 40.01, -71.12));

        search(searchSourceBuilder);

        // 指定一个区域，由三个坐标点，组成，比如上海大厦，东方明珠塔，上海火车站
        searchSourceBuilder = new SearchSourceBuilder();
        List<GeoPoint> points = new ArrayList<>();
        points.add(new GeoPoint(40.73, -74.1));
        points.add(new GeoPoint(40.01, -71.12));
        points.add(new GeoPoint(50.56, -90.58));
        searchSourceBuilder.query(QueryBuilders.geoPolygonQuery("pin.location", points));
        search(searchSourceBuilder);

        // 搜索距离当前位置在200公里内的4s店
        searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.geoDistanceQuery("pin.location")
                .point(40, -70).distance(200, DistanceUnit.KILOMETERS));
        search(searchSourceBuilder);
    }

}
