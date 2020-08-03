FROM openjdk:13.0.1-jdk-oraclelinux7

RUN yum -y install curl
RUN curl https://bintray.com/sbt/rpm/rpm | tee /etc/yum.repos.d/bintray-sbt-rpm.repo
RUN yum -y install sbt
# this is the folder in the container
WORKDIR /opt/app

RUN chmod +x ./script.sh