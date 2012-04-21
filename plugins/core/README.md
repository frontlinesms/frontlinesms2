Grails 2 Upgrade
================

# Testing

## Functional tests
Problems currently appear to be:
* override of `@href` in BootStrap not working - 'http://localhost:8080' is not stripped
* `$(...).jquery.*` not working - throws Exception
* database returns results in different orders now
