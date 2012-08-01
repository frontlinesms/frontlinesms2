// environment specific settings
environments {
    production {
        dataSource {
            url = "jdbc:h2:${frontlinesms2.ResourceUtils.resourcePath}/radioProdDb;MVCC=TRUE"
        }
    }
}
