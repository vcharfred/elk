package top.vchar.crud;

import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.io.IOException;
import java.net.InetAddress;

/**
 * <p> 员工信息增删查改 </p>
 *
 * @author vchar fred
 * @version 1.0
 * @create_date 2020/8/3
 */
public class EmployeeCrudDemo {

    public static void main(String[] args) throws Exception {

        // 构建client
        Settings settings = Settings.builder()
                .put("cluster.name", "docker-cluster")
                .build();
        TransportClient client = new PreBuiltTransportClient(settings)
                .addTransportAddress(new TransportAddress(InetAddress.getByName("192.168.111.40"), 9300));

//        addDoc(client);
//        getDoc(client);
//        updateDoc(client);
//        delDoc(client);
        search(client);
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

    public static void search(TransportClient client){
        SearchResponse response = client.prepareSearch("employee")
                .setQuery(QueryBuilders.boolQuery().must(QueryBuilders.matchQuery("position", "scientist"))
                        .filter(QueryBuilders.rangeQuery("age").gte(28).lte(40))).setFrom(0).setSize(2).get();
        System.out.println(response);
    }
}
