/*
 * Copyright 2016-2018 shardingsphere.io.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * </p>
 */

package io.shardingsphere.example.spring.namespace.jpa.orche;

import io.shardingsphere.example.repository.api.service.TransactionService;
import io.shardingsphere.example.repository.jpa.service.SpringEntityTransactionService;
import io.shardingsphere.example.type.RegistryCenterType;
import io.shardingsphere.example.type.ShardingType;
import io.shardingsphere.transaction.api.TransactionType;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringNamespaceTransactionExample {
    
    private static ShardingType shardingType = ShardingType.SHARDING_DATABASES;
//    private static ShardingType shardingType = ShardingType.SHARDING_TABLES;
//    private static ShardingType shardingType = ShardingType.SHARDING_DATABASES_AND_TABLES;
//    private static ShardingType shardingType = ShardingType.MASTER_SLAVE;
//    private static ShardingType shardingType = ShardingType.SHARDING_MASTER_SLAVE;
    
//    private static boolean isRangeSharding = true;
    private static boolean isRangeSharding = false;
    
    private static RegistryCenterType registryCenterType = RegistryCenterType.ZOOKEEPER;
//    private static RegistryCenterType registryCenterType = RegistryCenterType.ETCD;
    
    private static boolean loadConfigFromRegCenter = false;
//    private static boolean loadConfigFromRegCenter = true;
    
    public static void main(final String[] args) {
        try (ConfigurableApplicationContext applicationContext = new ClassPathXmlApplicationContext(isRangeSharding ? getApplicationFileRange() : getApplicationFilePrecise())) {
            process(applicationContext);
        }
    }
    
    private static String getApplicationFilePrecise() {
        switch (shardingType) {
            case SHARDING_DATABASES:
                return String.format("META-INF/%s/%s/application-sharding-databases-precise.xml", registryCenterType.name().toLowerCase(), loadConfigFromRegCenter ? "cloud" : "local");
            case SHARDING_TABLES:
                return String.format("META-INF/%s/%s/application-sharding-tables-precise.xml", registryCenterType.name().toLowerCase(), loadConfigFromRegCenter ? "cloud" : "local");
            case SHARDING_DATABASES_AND_TABLES:
                return String.format("META-INF/%s/%s/application-sharding-databases-tables-precise.xml", registryCenterType.name().toLowerCase(), loadConfigFromRegCenter ? "cloud" : "local");
            case MASTER_SLAVE:
                return String.format("META-INF/%s/%s/application-master-slave.xml", registryCenterType.name().toLowerCase(), loadConfigFromRegCenter ? "cloud" : "local");
            case SHARDING_MASTER_SLAVE:
                return String.format("META-INF/%s/%s/application-sharding-master-slave-precise.xml", registryCenterType.name().toLowerCase(), loadConfigFromRegCenter ? "cloud" : "local");
            default:
                throw new UnsupportedOperationException(shardingType.name());
        }
    }
    
    private static String getApplicationFileRange() {
        switch (shardingType) {
            case SHARDING_DATABASES:
                return String.format("META-INF/%s/%s/application-sharding-databases-range.xml", registryCenterType.name().toLowerCase(), loadConfigFromRegCenter ? "cloud" : "local");
            case SHARDING_TABLES:
                return String.format("META-INF/%s/%s/application-sharding-tables-range.xml", registryCenterType.name().toLowerCase(), loadConfigFromRegCenter ? "cloud" : "local");
            case SHARDING_DATABASES_AND_TABLES:
                return String.format("META-INF/%s/%s/application-sharding-databases-tables-range.xml", registryCenterType.name().toLowerCase(), loadConfigFromRegCenter ? "cloud" : "local");
            case MASTER_SLAVE:
                return String.format("META-INF/%s/%s/application-master-slave.xml", registryCenterType.name().toLowerCase(), loadConfigFromRegCenter ? "cloud" : "local");
            case SHARDING_MASTER_SLAVE:
                return String.format("META-INF/%s/%s/application-sharding-master-slave-range.xml", registryCenterType.name().toLowerCase(), loadConfigFromRegCenter ? "cloud" : "local");
            default:
                throw new UnsupportedOperationException(shardingType.name());
        }
    }
    
    private static void process(final ConfigurableApplicationContext applicationContext) {
        TransactionService transactionService = getTransactionService(applicationContext);
        transactionService.processSuccess(false);
        processFailureSingleTransaction(transactionService, TransactionType.LOCAL);
        processFailureSingleTransaction(transactionService, TransactionType.XA);
        processFailureSingleTransaction(transactionService, TransactionType.BASE);
        processFailureSingleTransaction(transactionService, TransactionType.LOCAL);
    }
    
    private static void processFailureSingleTransaction(final TransactionService transactionService, final TransactionType type) {
        try {
            switch (type) {
                case LOCAL:
                    transactionService.processFailureWithLocal();
                    break;
                case XA:
                    transactionService.processFailureWithXa();
                    break;
                case BASE:
                    transactionService.processFailureWithBase();
                    break;
                default:
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            transactionService.printData(false);
        }
    }
    
    private static TransactionService getTransactionService(final ConfigurableApplicationContext applicationContext) {
        return applicationContext.getBean("jdbcTransactionService", SpringEntityTransactionService.class);
    }
}
