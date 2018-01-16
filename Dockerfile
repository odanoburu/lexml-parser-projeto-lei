FROM bigtruedata/scala

COPY ./src/ ./pom.xml ./

RUN apt-get update \
    && apt-get install -y --no-install-recommends maven \
    && rm -rf /var/lib/apt/lists/* \
    && mvn package

COPY ./input .
