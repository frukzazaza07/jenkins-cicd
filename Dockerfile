FROM jenkins/jenkins:2.504.2-jdk21
USER root

# only linux server
# run grep docker /etc/group เพื่อหาเลข GID เพื่อ add user jenkins ใน container ศามารถใช้ docker.sock แบบ gorup เดียวกับนอก container
ARG DOCKER_GID=988

RUN apt-get update && apt-get install -y lsb-release iputils-ping jq
RUN curl -fsSLo /usr/share/keyrings/docker-archive-keyring.asc \
    https://download.docker.com/linux/debian/gpg
RUN echo "deb [arch=$(dpkg --print-architecture) \
  signed-by=/usr/share/keyrings/docker-archive-keyring.asc] \
  https://download.docker.com/linux/debian \
  $(lsb_release -cs) stable" >/etc/apt/sources.list.d/docker.list
RUN apt-get update && apt-get install -y docker-ce-cli
RUN RUN groupadd -g $DOCKER_GID docker || true && \
    usermod -aG docker jenkins
USER jenkins
RUN jenkins-plugin-cli --plugins "blueocean docker-workflow json-path-api"
# jenkins-plugin-cli --list

