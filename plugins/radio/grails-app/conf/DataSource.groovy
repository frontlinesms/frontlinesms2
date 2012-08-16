// environment specific settings
environments {
    test {
        dataSource {
            dbCreate = "update"
            url = "jdbc:h2:mem:testDb;MVCC=TRUE"
            logSql = true
        }
    }
    production {
        dataSource {
            url = "jdbc:h2:${frontlinesms2.ResourceUtils.resourcePath}/radioProdDb;MVCC=TRUE"
        }
    }
}
