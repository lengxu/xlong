package uyun.xianglong.app.input;

public class KafkaInfo {
    String id;
    String server; // localhost:9092
    String topic;

    public String getId() {
        return id;
    }

    public KafkaInfo setId(String id) {
        this.id = id;
        return this;
    }

    public String getServer() {
        return server;
    }

    public KafkaInfo setServer(String server) {
        this.server = server;
        return this;
    }

    public String getTopic() {
        return topic;
    }

    public KafkaInfo setTopic(String topic) {
        this.topic = topic;
        return this;
    }
}
