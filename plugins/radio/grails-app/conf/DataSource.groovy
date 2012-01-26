dataSource {
	pooled = true
	driverClassName = "org.hsqldb.jdbcDriver"
	username = "sa"
	password = ""
}
// environment specific settings
hibernate {
	cache.use_second_level_cache = true
	cache.use_query_cache = true
	cache.provider_class = 'net.sf.ehcache.hibernate.EhCacheProvider'
}


environments {
	development {
		dataSource {
			dbCreate = "create-drop" // one of 'create', 'create-drop','update'
			url = "jdbc:hsqldb:mem:devDB"
		}
	}

	test {
		dataSource {
			dbCreate = "update"
			url = "jdbc:hsqldb:mem:testDb"
		}
		hibernate {
			cache.use_second_level_cache = false
			cache.use_query_cache = false
		}
	}
	production {
		dataSource {
			dbCreate = "update"
			url = "jdbc:hsqldb:file:${System.properties.'user.home'}/.frontlinesms2/prodDb;shutdown=true"
		}

	}
	
	standalone {
		dataSource {
			dbCreate = "update"
			url = "jdbc:hsqldb:file:standalone;shutdown=true"
		}
	}
}