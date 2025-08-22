package io.coffeedia.infrastructure.persistence.datasource;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public class RoutingDataSource extends AbstractRoutingDataSource {

    /**
     * 현재 트랜잭션의 읽기 전용 여부에 따라 데이터소스 키를 설정
     */
    @Override
    protected Object determineCurrentLookupKey() {
        return TransactionSynchronizationManager.isCurrentTransactionReadOnly()
            ? DataSourceType.REPLICA
            : DataSourceType.MAIN;
    }
}
