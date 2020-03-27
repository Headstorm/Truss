# FROM openjdk:13.0.2
ARG OPENJDK_TAG=8u232
FROM openjdk:${OPENJDK_TAG}

ARG SBT_VERSION=1.3.8

# Install sbt
RUN \
  curl -L -o sbt-$SBT_VERSION.deb https://dl.bintray.com/sbt/debian/sbt-$SBT_VERSION.deb && \
  dpkg -i sbt-$SBT_VERSION.deb && \
  rm sbt-$SBT_VERSION.deb && \
  apt-get update && \
  apt-get install sbt && \
  sbt sbtVersion

WORKDIR /Truss
ADD . /Truss

EXPOSE 8888

CMD sbt run