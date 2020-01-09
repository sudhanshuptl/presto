FROM ubuntu:latest
MAINTAINER Varun M S <sudhanshuptl13@gmail.com>

RUN apt-get update && \
  apt-get install -yf software-properties-common \
  && add-apt-repository -y ppa:openjdk-r/ppa\
  && apt-get update \
  && apt-get install -yf openjdk-8-jre

RUN apt-get update

copy ./target/presto-cli-0.231-SNAPSHOT-executable.jar /usr/local/bin/presto-cli
copy ./hyper/*  /usr/local/bin/hyper/

RUN chmod +rx /usr/local/bin/presto-cli
RUN chmod +rx -R /usr/local/bin/hyper

ENTRYPOINT ["bash"]


