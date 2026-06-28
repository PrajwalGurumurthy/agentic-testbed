package com.stockservices.couchbase.service;

import com.couchbase.client.java.ReactiveCluster;
import com.couchbase.client.java.ReactiveCollection;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.query.ReactiveQueryResult;
import com.stockservices.couchbase.domain.User;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class UserService {

    private final ReactiveCollection collection;
    private final ReactiveCluster cluster;
    private final String bucketName;

    public UserService(ReactiveCollection collection, ReactiveCluster cluster, org.springframework.core.env.Environment env) {
        this.collection = collection;
        this.cluster = cluster;
        this.bucketName = env.getProperty("couchbase.bucketName", "sample_bucket");
    }

    public Mono<User> saveUser(User user) {
        JsonObject content = JsonObject.create()
                .put("id", user.getId())
                .put("name", user.getName())
                .put("email", user.getEmail())
                .put("type", "user");

        return collection.upsert(user.getId(), content)
                .map(result -> user);
    }

    public Mono<User> getUser(String id) {
        return collection.get(id)
                .map(getResult -> {
                    JsonObject content = getResult.contentAsObject();
                    return new User(
                            content.getString("id"),
                            content.getString("name"),
                            content.getString("email")
                    );
                });
    }

    public Flux<User> getAllUsers() {
        String statement = "SELECT id, name, email FROM `" + bucketName + "` WHERE type = 'user'";

        return cluster.query(statement)
                .flatMapMany(ReactiveQueryResult::rowsAsObject)
                .map(row -> new User(
                        row.getString("id"),
                        row.getString("name"),
                        row.getString("email")
                ));
    }
}
