# Unique Regex Custom Field

Custom field that based on text field and allows to match "regular expression" and/or check uniqueness of custom field from JQL.

## Build application

Build application from source code.
**Requirements**
- JAVA 1.8
- Git
- Atlassian SDK

1. Install [Atlassian Plugin SDK](https://developer.atlassian.com/server/framework/atlassian-sdk/set-up-the-atlassian-plugin-sdk-and-build-a-project/) 
2. Download the current source code
3. Open cmd.exe 'Windows'|bash 'Linux', go to the direcory with pom.xml and run atlas-package
4. Compiled jar file will be placed in direcory unique-regex-custom-field\target\uniqueregexfield-'version'.jar

## Test application

Build and deploy application. JIRA server will be with development license, bind port 2990 by default.
**Requirements**
- The same as in build plus 2GB memory

1. Install [Atlassian Plugin SDK](https://developer.atlassian.com/server/framework/atlassian-sdk/set-up-the-atlassian-plugin-sdk-and-build-a-project/) 
2. Download the current source code
3. Open cmd.exe 'Windows'|bash 'Linux', go to the direcory with pom.xml and run atlas-run -u 6.3.21
4. Open JIRA server by [link](http://localhost:2990/jira/). Login name - admin, password - admin

## Start Jira server with plugin in docker

Start Jira server with plugin in docker container. The container will automatically deploy the JIRA server, this may take 10 minutes.

1. Download image: 'docker pull andrewdvizhok/demo-jira:1.0'
2. Run container: 'docker run -t -p 2990:80 -i andrewdvizhok/demo-jira:1.0'
