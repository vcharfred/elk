package top.vchar.entity;

import java.io.Serializable;

/**
 * <p> 员工信息 </p>
 *
 * @author vchar fred
 * @version 1.0
 * @create_date 2020/8/3
 */
public class Employee implements Serializable {

    private String id;

    private String user;

    private Integer age;

    private String position;

    private String country;

    private String joinData;

    private Integer salary;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getJoinData() {
        return joinData;
    }

    public void setJoinData(String joinData) {
        this.joinData = joinData;
    }

    public Integer getSalary() {
        return salary;
    }

    public void setSalary(Integer salary) {
        this.salary = salary;
    }
}
