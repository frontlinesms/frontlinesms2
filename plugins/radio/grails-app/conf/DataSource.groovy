// environment specific settings
environments {
    test {
        dataSource {
            dbCreate = "update"
            url = "jdbc:h2:mem:testDb${frontlinesms2.StaticApplicationInstance.uniqueId};MVCC=TRUE"
            logSql = true
        }
    }
    production {
        dataSource {
            url = "jdbc:h2:${frontlinesms2.ResourceUtils.resourcePath}/radioProdDb;MVCC=TRUE"
        }
    }
}
