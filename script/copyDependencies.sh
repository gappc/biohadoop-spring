#!/bin/bash

PROJECT_HOME=/sdb/studium/master-thesis/code/git/biohadoop-spring

ssh root@172.17.0.100 'mkdir -p /tmp/lib'

~/opt/maven/current/bin/mvn -f $PROJECT_HOME dependency:copy-dependencies
scp $PROJECT_HOME/target/dependency/* root@master:/tmp/lib

ssh root@172.17.0.100 '/opt/hadoop/current/bin/hdfs dfs -mkdir -p /tmp/lib'
ssh root@172.17.0.100 '/opt/hadoop/current/bin/hdfs dfs -copyFromLocal /tmp/lib/* /tmp/lib'
