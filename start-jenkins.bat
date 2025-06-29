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
  --network jenkins ^
  --env DOCKER_HOST=tcp://host.docker.internal:2375 ^ 
  --volume jenkins-data:/var/jenkins_home ^
  --volume jenkins-docker-certs:/certs/client:ro ^
  --publish 8080:8080 --publish 50000:50000 %IMAGE_TAG_NAME%

echo Jenkins Docker-in-Docker started

@REM docker run options ถ้าใส่ env สองอันนี้ แล้วเราจะใช้ plugins docker เพื่อ build app มันจะถามหา TLS
@REM for 2375 not use cert if using docker-desktop config in setting>general>2375 --env DOCKER_HOST=tcp://host.docker.internal:2375 ^ 
@REM for window using tcp for build docker in docker --env DOCKER_HOST=tcp://host.docker.internal:2376 ^
@REM for linux using socket for build docker in docker --env DOCKER_HOST=tcp://host.docker.internal:2376 ^
@REM --env DOCKER_CERT_PATH=/certs/client --env DOCKER_TLS_VERIFY=1 ^

@REM docker cp C:\Users\User\.ssh ~root/.ssh/authorized_keys