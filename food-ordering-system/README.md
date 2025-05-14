### to visualize the dependency graph
```shell
mvn com.github.ferstl:depgraph-maven-plugin:aggregate -DcreateImage=true -DreduceEdges=false -Dscope=compile "-Dincludes=com.food.ordering*:*"
```

![img.png](img.png)

### setup kafka
```shell
cd infrastructure/docker-compose

docker compose -f common.yml -f zookeeper.yml up

echo ruok | nc localhost 2181
# should return imok if zookeeper is healthy

docker compose -f common.yml -f kafka_cluster.yml up

docker compose -f common.yml -f init_kafka.yml up

```