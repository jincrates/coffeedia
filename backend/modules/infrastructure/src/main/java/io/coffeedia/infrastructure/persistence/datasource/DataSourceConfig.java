package io.coffeedia.infrastructure.persistence.datasource;

import com.zaxxer.hikari.HikariDataSource;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class})
public class DataSourceConfig {

    /**
     * 쓰기 전용 데이터소스 (Main Database)
     */
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.main.hikari")
    public DataSource mainDataSource() {
        return DataSourceBuilder
            .create()
            .type(HikariDataSource.class)
            .build();
    }

    /**
     * 읽기 전용 데이터소스 (Replica Database)
     */
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.replica.hikari")
    public DataSource replicaDataSource() {
        return DataSourceBuilder
            .create()
            .type(HikariDataSource.class)
            .build();
    }

    /**
     * 읽기/쓰기 요청을 적절한 데이터소스로 라우팅하는 DataSource
     */
    @Bean
    public DataSource routingDataSource(
        @Qualifier("mainDataSource") DataSource mainDataSource,
        @Qualifier("replicaDataSource") DataSource replicaDataSource
    ) {
        RoutingDataSource routingDataSource = new RoutingDataSource();

        Map<Object, Object> dataSourceMap = new HashMap<>();
        dataSourceMap.put(
            DataSourceType.MAIN,
            mainDataSource
        );
        dataSourceMap.put(
            DataSourceType.REPLICA,
            replicaDataSource
        );

        routingDataSource.setTargetDataSources(dataSourceMap);
        routingDataSource.setDefaultTargetDataSource(mainDataSource);

        return routingDataSource;
    }

    /**
     * 애플리케이션에서 사용할 메인 DataSource LazyConnectionDataSourceProxy로 감싸서 실제 커넥션이 필요할 때까지 지연
     */
    @Primary
    @Bean
    public DataSource dataSource(@Qualifier("routingDataSource") DataSource routingDataSource) {
        return new LazyConnectionDataSourceProxy(routingDataSource);
    }
}
