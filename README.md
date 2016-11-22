# FrontlineSMS v2 core codebase:ivan dummy update remove

This folder contains 
- the codebase for FrontlineSMS 2.x in the standard Grails application layout (grails-app, src, test, web-app, etc)
- Utility scripts, mainly in bash and groovy, in the 'do/' folder
- install4j & maven config for bundling the desktop version of FrontlineSMS in the 'install' folder

Getting started
- Run `grails -Ddb.migrations=false run-app` to start the app in dev mode, with bootstrap data, and with database migrations disabled
- Run `grails test-app` to run our test suite, and `do/test_backup` to open the results in a browser
- Run `grails dependency-report` to view the list of plugin dependencies. If you need access to a FrontlineSMS-developed plugin that is not available in a public repo, please let us know via a github issue.
