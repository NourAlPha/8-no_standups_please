FROM openjdk:25-ea-4-jdk-oraclelinux9

WORKDIR /app

# Add environment variables
ENV spring.application.name=MiniProject1 \
    spring.application.userDataPath=src/main/java/com/example/data/users.json \
    spring.application.productDataPath=src/main/java/com/example/data/products.json \
    spring.application.orderDataPath=src/main/java/com/example/data/orders.json \
    spring.application.cartDataPath=src/main/java/com/example/data/carts.json

COPY ./ /app

EXPOSE 8080

CMD ["java","-jar","/app/target/mini1.jar"]