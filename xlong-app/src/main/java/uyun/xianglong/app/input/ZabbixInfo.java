package uyun.xianglong.app.input;

public class ZabbixInfo {
    String id;
    String name;
    String host;
    String user;
    String password;
    String modelName;

    public String getId() {
        return id;
    }

    public ZabbixInfo setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public ZabbixInfo setName(String name) {
        this.name = name;
        return this;
    }

    public String getHost() {
        return host;
    }

    public ZabbixInfo setHost(String host) {
        this.host = host;
        return this;
    }

    public String getUser() {
        return user;
    }

    public ZabbixInfo setUser(String user) {
        this.user = user;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public ZabbixInfo setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getModelName() {
        return modelName;
    }

    public ZabbixInfo setModelName(String modelName) {
        this.modelName = modelName;
        return this;
    }
}
