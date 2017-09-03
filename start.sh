docker images|grep 0.2.0-SNAPSHOT|awk '{print $3}'|xargs docker rmi -f

mvn clean package -DskipTests -DskipITs -Pdocker -PHuaweiCloud

docker tag worker:0.2.0-SNAPSHOT  registry.cn-north-1.hwclouds.com/willemjiang/worker-service:0.2.0-SNAPSHOT$1
docker tag beekeeper:0.2.0-SNAPSHOT  registry.cn-north-1.hwclouds.com/willemjiang/beekeeper-service:0.2.0-SNAPSHOT$1
docker tag doorman:0.2.0-SNAPSHOT  registry.cn-north-1.hwclouds.com/willemjiang/doorman-service:0.2.0-SNAPSHOT$1
docker tag manager:0.2.0-SNAPSHOT  registry.cn-north-1.hwclouds.com/willemjiang/manager-service:0.2.0-SNAPSHOT$1

docker push registry.cn-north-1.hwclouds.com/willemjiang/worker-service:0.2.0-SNAPSHOT$1
docker push   registry.cn-north-1.hwclouds.com/willemjiang/beekeeper-service:0.2.0-SNAPSHOT$1
docker push  registry.cn-north-1.hwclouds.com/willemjiang/doorman-service:0.2.0-SNAPSHOT$1
docker push  registry.cn-north-1.hwclouds.com/willemjiang/manager-service:0.2.0-SNAPSHOT$1

