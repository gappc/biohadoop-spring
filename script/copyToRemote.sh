#!/bin/bash

PROJECT_HOME=/sdb/studium/master-thesis/code/git/biohadoop-spring

~/opt/maven/current/bin/mvn -f $PROJECT_HOME clean install
scp $PROJECT_HOME/target/biohadoop-spring-0.0.1-SNAPSHOT.jar root@master:/tmp/lib
ssh root@172.17.0.100 '/opt/hadoop/current/bin/hdfs dfs -rm /tmp/lib/biohadoop-spring-0.0.1-SNAPSHOT.jar'
ssh root@172.17.0.100 '/opt/hadoop/current/bin/hdfs dfs -copyFromLocal /tmp/lib/biohadoop-spring-0.0.1-SNAPSHOT.jar /tmp/lib/biohadoop-spring-0.0.1-SNAPSHOT.jar'
