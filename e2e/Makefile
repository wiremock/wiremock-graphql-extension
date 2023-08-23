.PHONY: all install test_compile run

all: install clean test_compile run

install:
	cd ../wiremock-graphql-extension && mvn install -Dmaven.wagon.http.progress=false

clean:
	mvn clean

test_compile:
	mvn test-compile -Dmaven.wagon.http.progress=false

run:
	mvn gauge:execute -Dtags="$(TAGS)" -Dmaven.wagon.http.progress=false

docker: docker/build docker/run

docker/build:
	cd ../wiremock-graphql-extension && docker build -t wiremock-graphql-extension:latest .

docker/run:
	docker run -p 8888:8080 -d wiremock-graphql-extension:latest
