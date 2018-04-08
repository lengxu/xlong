package uyun.xianglong.app.input;

public class JdbcInfo {
    String id;
    String name;
    String host;
    String user;
    String password;

    public String getId() {
        return id;
    }

    public JdbcInfo setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public JdbcInfo setName(String name) {
        this.name = name;
        return this;
    }

    public String getHost() {
        return host;
    }

    public JdbcInfo setHost(String host) {
        this.host = host;
        return this;
    }

    public String getUser() {
        return user;
    }

    public JdbcInfo setUser(String user) {
        this.user = user;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public JdbcInfo setPassword(String password) {
        this.password = password;
        return this;
    }
}
