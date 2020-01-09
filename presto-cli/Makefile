DOCKER_TAG="docker.pkg.github.com/sudhanshuptl/presto/presto-to-hyper"

.PHONY: all run deploy

#all: build

login:
	docker login docker.pkg.github.com --username sudhanshuptl
	# Use private-gpg key with package write access as password

build: Dockerfile
	# Download HyperAPi for linux 
	#  http://downloads.tableau.com/tssoftware/tableauhyperapi-java-linux-x86_64-release-hyperapi_release_2.0.0.8953.r50e2ce3a.zip
	# Unzip download
	# mv ./extract-folder/lib/hyper .
	docker build -t ${DOCKER_TAG} .
	docker tag ${DOCKER_TAG} ${DOCKER_TAG}:latest
pull:
	docker pull ${DOCKER_TAG}

run:
	docker pull ${DOCKER_TAG}
	docker run -it --rm --entrypoint /bin/bash ${DOCKER_TAG}

deploy:
	docker push $(DOCKER_TAG)

