package com.stockservices.couchbase.config;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.ClusterOptions;
import com.couchbase.client.java.ReactiveCluster;
import com.couchbase.client.java.ReactiveCollection;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CouchbaseConfig {

    @Value("${couchbase.connectionString:localhost}")
    private String connectionString;

    @Value("${couchbase.username:Administrator}")
    private String username;

    @Value("${couchbase.password:password}")
    private String password;

    @Value("${couchbase.bucketName:sample_bucket}")
    private String bucketName;

    @Bean(destroyMethod = "disconnect")
    public Cluster couchbaseCluster() {
        return Cluster.connect(connectionString, ClusterOptions.clusterOptions(username, password));
    }

    @Bean
    public ReactiveCluster reactiveCluster(Cluster cluster) {
        return cluster.reactive();
    }

    @Bean
    public ReactiveCollection reactiveUserCollection(ReactiveCluster reactiveCluster) {
        return reactiveCluster.bucket(bucketName).defaultCollection();
    }
}
