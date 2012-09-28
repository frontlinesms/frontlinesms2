dataSource {
    pooled = true
    driverClassName = "org.h2.Driver"
    username = "sa"
    password = ""
}
hibernate {
    cache.use_second_level_cache = true
    cache.use_query_cache = false
    cache.region.factory_class = 'net.sf.ehcache.hibernate.EhCacheRegionFactory'
}
// environment specific settings
environments {
    development {
        dataSource {
            def runMigrations = System.properties.'run.migration'
            if(runMigrations == "false") {
              println "WARNING:: DATABASE MIGRATION DISABLED"
              dbCreate = "create-drop"
            }
            url = "jdbc:h2:mem:devDb;MVCC=TRUE"
        }
    }
    test {
        dataSource {
            dbCreate = "update"
            url = "jdbc:h2:mem:testDb${frontlinesms2.StaticApplicationInstance.uniqueId};MVCC=TRUE"
            logSql = true
        }
    }
    production {
        dataSource {
            def prodDbName = System.properties.'db.name' ?: 'prodDb' // production DB name defaults to prodDb
            url = "jdbc:h2:${frontlinesms2.ResourceUtils.resourcePath}/${prodDbName};MVCC=TRUE"
            pooled = true
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
