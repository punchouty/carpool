dataSource {
    pooled = true
    driverClassName = "org.h2.Driver"
    username = "sa"
    password = ""
}
hibernate {
    cache.use_second_level_cache = true
    cache.use_query_cache = true
    cache.provider_class='org.hibernate.cache.EhCacheProvider'
}
// environment specific settings
environments {
    development {
        dataSource {
			dbCreate = "create-drop"
            url = "jdbc:mysql://localhost:3306/ebdb"
			driverClassName = "com.mysql.jdbc.Driver"
			pooled = true
			username = "root"
			password = ""
			logSql = true
			properties {
               maxActive = -1
               minEvictableIdleTimeMillis=1800000
               timeBetweenEvictionRunsMillis=1800000
               numTestsPerEvictionRun=3
               testOnBorrow=true
               testWhileIdle=true
               testOnReturn=true
               validationQuery="SELECT 1"
            }
        }
    }
    test {
        dataSource {
            dbCreate = "update"
            url = "jdbc:h2:mem:testDb;MVCC=TRUE;LOCK_TIMEOUT=10000"
        }
    }
    production {
        dataSource {
            dbCreate = "create-drop"
            url = "jdbc:mysql://aa1yp8u4ri0ltz.cgz251990xxz.ap-southeast-1.rds.amazonaws.com:3306/ebdb?user=racloopdb&password=W!shtech0"
			driverClassName = "com.mysql.jdbc.Driver"
            pooled = true
			username = "racloopdb"
			password = "W!shtech0"
			properties {
               maxActive = -1
               minEvictableIdleTimeMillis=1800000
               timeBetweenEvictionRunsMillis=1800000
               numTestsPerEvictionRun=3
               testOnBorrow=true
               testWhileIdle=true
               testOnReturn=true
               validationQuery="SELECT 1"
            }
        }
    }
}
