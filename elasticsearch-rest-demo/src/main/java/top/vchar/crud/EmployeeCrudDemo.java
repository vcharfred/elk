package top.vchar.crud;

import org.apache.http.HttpHost;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentFactory;

import java.io.IOException;

/**
 * <p> 员工信息增删查改 </p>
 *
 * @author vchar fred
 * @version 1.0
 * @create_date 2020/8/3
 */
public class EmployeeCrudDemo {

    public static void main(String[] args) throws IOException {
        HttpHost[] httpHost = {HttpHost.create("192.168.111.40:9200")};
        RestHighLevelClient restHighLevelClient = new RestHighLevelClient(RestClient.builder(httpHost));
        addDoc(restHighLevelClient);
//        getDoc(restHighLevelClient);
//        updateDoc(restHighLevelClient);
//        delDoc(restHighLevelClient);

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


}
