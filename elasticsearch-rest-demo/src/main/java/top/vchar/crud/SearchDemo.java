package top.vchar.crud;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpHost;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import top.vchar.entity.Employee;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * <p> 搜索demo </p>
 *
 * @author vchar fred
 * @version 1.0
 * @create_date 2020/8/3
 */
public class SearchDemo {

    public static void main(String[] args) {
        try (RestHighLevelClient client = new RestHighLevelClient(RestClient.builder(new HttpHost("192.168.111.40", 9200)))) {
            initDoc(client);
            search(client);
        } catch (Exception e) {
            e.printStackTrace();
        }

//        RestHighLevelClient client = new RestHighLevelClient(RestClient
//                .builder(new HttpHost("192.168.111.40", 9200)
//                        , new HttpHost("192.168.111.41", 9200)));

    }

    /**
     * 初始化数据
     */
    public static void initDoc(RestHighLevelClient client) throws IOException {
        BulkRequest bulkRequest = new BulkRequest();
        List<Employee> employees = data();
        for (Employee employee : employees) {
            IndexRequest indexRequest = new IndexRequest("employee");
            indexRequest.id(employee.getId());
            indexRequest.source(JSONObject.toJSONString(employee), XContentType.JSON);
            bulkRequest.add(indexRequest);
        }
        BulkResponse bulk = client.bulk(bulkRequest, RequestOptions.DEFAULT);
        System.out.println(bulk.buildFailureMessage());
    }

    private static List<Employee> data(){
        String[] names = {"Tom", "Jerry", "Dacey", "Sophie", "Malcolm", "Lucy", "Joan", "Luther", "Bill", "Mark"};
        List<Employee> list = new ArrayList<>();
        for(int i=0; i<10; i++){
            Employee employee = new Employee();
            employee.setId(String.valueOf(i+1));
            employee.setUser(names[i]);
            employee.setAge(25+i);
            if(i==0){
                employee.setPosition("scientist manger");
            }else {
                employee.setPosition("scientist "+i);
            }
            employee.setCountry("China");
            employee.setJoinData("2025-01-0"+i);
            employee.setSalary(10000*(i+1));
            list.add(employee);
        }
        return list;
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


}
