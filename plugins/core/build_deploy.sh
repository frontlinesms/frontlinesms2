#!/bin/bash
rm -rf target && rm -rf install/target && rm -rf install/webapp && grails clean && grails prod build-installers | tee build-installers.log && mv install/target/install4j/frontlinesms2-alpha_* ~/Dropbox/FrontlineSMS/1\ Software\ Development/frontlinesms\ builds/

