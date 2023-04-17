.PHONY: all install test_compile run

all: install clean test_compile run

install:
	cd ../wiremock-graphql-extension && mvn install

clean:
	mvn clean

test_compile:
	mvn test-compile

run:
	mvn gauge:execute -Dtags="$(TAGS)"

