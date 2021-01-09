# Dockerfile
# base image
FROM centos:centos8

COPY artifactory.repo /etc/yum.repos.d/
RUN yum clean all &&\
    yum updateinfo metadata &&\
    yum install -y java-1.8.0-openjdk-devel.x86_64 git atlassian-plugin-sdk &&\
	cd / && git clone https://github.com/AndreyVMarkelov/unique-regex-custom-field.git
   
ENV JAVA_HOME=/usr/lib/jvm/java-1.8.0-openjdk
