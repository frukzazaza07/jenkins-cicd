# ls /var/jenkins_home/plugins check plugins

1. run jenkins from docker container
2. jen rsa key from destination server for deploy or ...
 2.1 cmd run on des server (run on user for deploy or ...) ssh-keygen -t rsa -b 4096 -f ~/.ssh/jenkins_id_rsa -N ""
3. copy private key into jenkins credential and input username using key gen cmd