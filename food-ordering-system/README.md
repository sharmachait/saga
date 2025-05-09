### to visualize the dependency graph
```shell
mvn com.github.ferstl:depgraph-maven-plugin:aggregate -DcreateImage=true -DreduceEdges=false -Dscope=compile "-Dincludes=com.food.ordering*:*"
```

![img.png](img.png)