# 添加 Java 8 镜像来源
FROM java:17

# 添加 Spring Boot 包
ADD ./target/SchedulingSystem.jar app.jar

EXPOSE 8090

# 执行启动命令
ENTRYPOINT ["java","-jar","/app.jar"]
