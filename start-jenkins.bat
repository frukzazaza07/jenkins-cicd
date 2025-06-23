@echo off
REM Start Jenkins Docker-in-Docker container

set IMAGE_TAG_NAME=myjenkins-blueocean:2.504.2-1

REM Check if Docker network 'jenkins' exists, create if not
docker network inspect jenkins >nul 2>&1
IF %ERRORLEVEL% NEQ 0 (
    echo Creating Docker network: jenkins
    docker network create jenkins
) 

docker build --no-cache -t %IMAGE_TAG_NAME% . && ^
docker run ^
  --name jenkins-blueocean --restart=on-failure --detach ^
  --network jenkins --env DOCKER_HOST=tcp://docker:2376 ^
  --env DOCKER_CERT_PATH=/certs/client --env DOCKER_TLS_VERIFY=1 ^
  --volume jenkins-data:/var/jenkins_home ^
  --volume jenkins-docker-certs:/certs/client:ro ^
  --publish 8080:8080 --publish 50000:50000 %IMAGE_TAG_NAME%

echo Jenkins Docker-in-Docker started

@REM docker cp C:\Users\User\.ssh ~root/.ssh/authorized_keys