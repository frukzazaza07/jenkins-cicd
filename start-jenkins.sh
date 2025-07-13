#!/bin/bash
IMAGE_TAG_NAME=myjenkins-blueocean:2.504.2-1

# Check if Docker network 'jenkins' exists, create if not
if ! docker network inspect jenkins > /dev/null 2>&1; then
    echo "Create network jenkins"
    docker network create jenkins
fi

docker build --no-cache -t $IMAGE_TAG_NAME . && \
docker run \
  --name jenkins-blueocean --restart=on-failure --detach \
  --network jenkins \
  --env DOCKER_HOST=tcp://host.docker.internal:2375 \
  --volume /home/docker/volume/jenkins/jenkins-data:/var/jenkins_home \
  --volume /home/docker/volume/jenkins/jenkins-docker-certs:/certs/client:ro \
  --publish 8080:8080 --publish 50000:50000 $IMAGE_TAG_NAME

echo Jenkins Docker-in-Docker started

# docker run options ถ้าใส่ env สองอันนี้ แล้วเราจะใช้ plugins docker เพื่อ build app มันจะถามหา TLS
# for 2375 not use cert if using docker-desktop config in setting>general>2375 --env DOCKER_HOST=tcp://host.docker.internal:2375 ^ 
# ถ้าเป็น window สำคัญเพราะเป็นการบอกให้ docker ภายใน jenkins container มาใช้ host เดียวกับ local pc 
# ถ้าเป็น linux ให้ volume docker.sock ภายใน jenkins container มาใช้ host เดียวกับ local pc 
# for window using tcp for build docker in docker --env DOCKER_HOST=tcp://host.docker.internal:2376 ^
# for linux using socket for build docker in docker --env DOCKER_HOST=tcp://host.docker.internal:2376 ^
# --env DOCKER_CERT_PATH=/certs/client --env DOCKER_TLS_VERIFY=1 ^

# docker cp C:\Users\User\.ssh ~root/.ssh/authorized_keys