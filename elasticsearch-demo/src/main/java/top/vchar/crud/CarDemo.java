package top.vchar.crud;

import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.get.MultiGetItemResponse;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.ClearScrollRequestBuilder;
import org.elasticsearch.action.search.ClearScrollResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.script.mustache.SearchTemplateRequestBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;
import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * <p> 汽车demo </p>
 *
 * @author vchar fred
 * @version 1.0
 * @create_date 2020/9/11
 */
public class CarDemo {

    private TransportClient client = null;

    /**
     * 构建client
     */
    @Before
    public void init() throws UnknownHostException {
        Settings settings = Settings.builder()
                .put("cluster.name", "docker-cluster")
                .build();
        client = new PreBuiltTransportClient(settings)
                .addTransportAddress(new TransportAddress(InetAddress.getByName("192.168.111.71"), 9300));
    }

    @After
    public void destroy() {
        if (client != null) {
            client.close();
        }
    }

    /**
     * 用一个upsert语法实现数据存在则更新，不存在则添加
     */
    @Test
    public void addOrUpdate() throws IOException, ExecutionException, InterruptedException {
        IndexRequest indexRequest = new IndexRequest("car_shop", "_doc", "1")
                .source(jsonBuilder()
                        .startObject()
                        .field("brand", "宝马")
                        .field("name", "宝马320")
                        .field("price", 310000)
                        .field("produce_date", "2020-01-01")
                        .endObject());

        UpdateRequest updateRequest = new UpdateRequest("car_shop", "_doc", "1")
                .doc(jsonBuilder()
                        .startObject()
                        .field("price", 310000)
                        .endObject())
                .upsert(indexRequest);

        client.update(updateRequest).get();
    }

    /**
     * 批量查询mget
     */
    @Test
    public void mget() {
        MultiGetResponse multiGetItemResponses = client.prepareMultiGet()
                .add("car_shop", "_doc", "1")
                .add("car_shop", "_doc", "2")
                .get();

        for (MultiGetItemResponse itemResponse : multiGetItemResponses) {
            GetResponse response = itemResponse.getResponse();
            if (response.isExists()) {
                String json = response.getSourceAsString();
                System.out.println(json);
            } else {
                System.out.println("no data");
            }
        }
    }

    /**
     * bulk批量执行
     */
    @Test
    public void bulk() throws IOException {

        BulkRequestBuilder bulkRequest = client.prepareBulk();

        bulkRequest.add(client.prepareIndex("car_sales", "_doc", "3")
                .setSource(jsonBuilder()
                        .startObject()
                        .field("brand", "奔驰")
                        .field("name", "奔驰C200")
                        .field("price", 350000)
                        .field("produce_date", "2017-01-05")
                        .field("sale_price", 340000)
                        .field("sale_date", "2017-02-03")
                        .endObject()
                )
        );

        bulkRequest.add(client.prepareUpdate("car_sales", "_doc", "1")
                .setDoc(jsonBuilder()
                        .startObject()
                        .field("sale_price", "290000")
                        .endObject()
                )
        );

        bulkRequest.add(client.prepareDelete("car_sales", "_doc", "2"));

        BulkResponse bulkResponse = bulkRequest.get();
        System.out.println(bulkResponse.hasFailures());
    }

    /**
     * 使用scroll滚动查询
     */
    @Test
    public void scroll() {
        SearchResponse scrollResp = client.prepareSearch("car_shop").setTypes("_doc")
                .setScroll(new TimeValue(60000))
                .setQuery(termQuery("brand.raw", "宝马"))
                .setSize(2)
                .get();

        SearchHit[] hits = scrollResp.getHits().getHits();
        while (hits != null && hits.length != 0) {
            for (SearchHit hit : hits) {
                System.out.println(hit.getSourceAsString());
            }
            scrollResp = client.prepareSearchScroll(scrollResp.getScrollId())
                    .setScroll(new TimeValue(60000))
                    .execute()
                    .actionGet();
            hits = scrollResp.getHits().getHits();
        }

        ClearScrollRequestBuilder clearScroll = client.prepareClearScroll();
        ClearScrollResponse scrollResponse = clearScroll.addScrollId(scrollResp.getScrollId()).get();
        System.out.println(scrollResponse.isSucceeded());
    }

    /**
     * 模版搜索
     */
    @Test
    public void searchTemplate() {
        Map<String, Object> params = new HashMap<>(1);
        params.put("brand", "奔驰");

        SearchResponse searchResponse = new SearchTemplateRequestBuilder(client)
                .setScript("{\n" +
                        "  \"query\": {\n" +
                        "    \"match\": {\n" +
                        "      \"brand\": \"{{brand}}\" \n" +
                        "    }\n" +
                        "  }\n" +
                        "}\n")
                .setScriptType(ScriptType.INLINE)
                .setScriptParams(params)
                .setRequest(new SearchRequest("car_shop"))
                .get()
                .getResponse();
        SearchHit[] hits = searchResponse.getHits().getHits();
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
    public void fullSearch() {

        SearchResponse response = client.prepareSearch("car_shop")
                .setTypes("_doc")
                .setQuery(matchQuery("brand", "奔驰"))
                .get();

        SearchHit[] hits = response.getHits().getHits();
        if (null != hits && hits.length != 0) {
            for (SearchHit hit : hits) {
                System.out.println(hit.getSourceAsString());
            }
        }


        response = client.prepareSearch("car_shop")
                .setTypes("_doc")
                .setQuery(QueryBuilders.multiMatchQuery("奔驰", "brand", "name"))
                .get();

        hits = response.getHits().getHits();
        if (null != hits && hits.length != 0) {
            for (SearchHit hit : hits) {
                System.out.println(hit.getSourceAsString());
            }
        }

        response = client.prepareSearch("car_shop")
                .setTypes("_doc")
                .setQuery(QueryBuilders.commonTermsQuery("name", "奔驰C201"))
                .get();

        hits = response.getHits().getHits();
        if (null != hits && hits.length != 0) {
            for (SearchHit hit : hits) {
                System.out.println(hit.getSourceAsString());
            }
        }

        response = client.prepareSearch("car_shop")
                .setTypes("_doc")
                .setQuery(QueryBuilders.prefixQuery("name", "奔"))
                .get();

        hits = response.getHits().getHits();
        if (null != hits && hits.length != 0) {
            for (SearchHit hit : hits) {
                System.out.println(hit.getSourceAsString());
            }
        }

    }

    /**
     * 组合查询
     */
    @Test
    public void boolQuery() {

        QueryBuilder qb = QueryBuilders.boolQuery()
                .must(matchQuery("brand", "奔驰"))
                .mustNot(termQuery("name.raw", "奔驰C203"))
                .should(termQuery("produce_date", "2020-01-02"))
                .filter(rangeQuery("price").gte("280000").lt("500000"));

        SearchResponse response = client.prepareSearch("car_shop")
                .setTypes("_doc")
                .setQuery(qb)
                .get();

        SearchHit[] hits = response.getHits().getHits();
        if (null != hits && hits.length != 0) {
            for (SearchHit hit : hits) {
                System.out.println(hit.getSourceAsString());
            }
        }

    }

    /**
     * 坐标查询
     */
    @Test
    public void geoPoint() {
        QueryBuilder queryBuilder = geoBoundingBoxQuery("pin.location")
                .setCorners(40.73, -74.1, 40.01, -71.12);
        SearchResponse response = client.prepareSearch("car_shop")
                .setTypes("_doc")
                .setQuery(queryBuilder)
                .get();

        SearchHit[] hits = response.getHits().getHits();
        if (null != hits && hits.length != 0) {
            for (SearchHit hit : hits) {
                System.out.println(hit.getSourceAsString());
            }
        }

    }

}
