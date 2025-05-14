FROM openjdk:17-alpine

MAINTAINER CopyRia

RUN apk add bash

RUN mkdir /CopyRiaApp
WORKDIR /CopyRiaApp
