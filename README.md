# Spring Boot Backend Server

![Java](https://img.shields.io/badge/Java-JDK%2023-blue)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-green)
![Maven](https://img.shields.io/badge/Maven-3.x-orange)
![JUnit](https://img.shields.io/badge/JUnit-5-brightgreen)
![Checkstyle](https://img.shields.io/badge/Checkstyle-10.12.4-yellow)
![jscpd](https://img.shields.io/badge/jscpd-3.5.1-red)
![Docker](https://img.shields.io/badge/Docker-20.x-blue)
![Docker Compose](https://img.shields.io/badge/Docker%20Compose-2.x-blueviolet)
![CI/CD](https://img.shields.io/badge/CI/CD-GitHub%20Actions-success)

## Overview

This project is a robust and scalable backend server built using **Spring Boot**  
and adhering to **Clean Architecture** principles. It is designed to be easily  
extendable into a microservices architecture.  

The project leverages modern development tools and practices, including:  

- **Maven** for dependency management  
- **JUnit** for testing  
- **Checkstyle** and **jscpd** for code quality  
- **Docker** for containerization  
- **CI/CD** pipelines for automated deployment 

## Features

- **Clean Architecture**: Separation of concerns with clear boundaries between layers (Presentation, Application, Domain, and Infrastructure).
- **Scalability**: Designed to scale horizontally, making it suitable for microservices.
- **Code Quality**: Integrated with **Checkstyle** for code style enforcement and **jscpd** for copy-paste detection.
- **Testing**: Comprehensive unit and integration tests using **JUnit 5**.
- **Containerization**: Ready for deployment using **Docker** and **Docker Compose**.
- **CI/CD**: Automated build, test, and deployment pipelines using **GitHub Actions**.

## Technologies

- **Java JDK 23**: The latest LTS version of Java.
- **Spring Boot 3.x**: For building the backend server.
- **Maven**: For dependency management and build automation.
- **JUnit 5**: For unit and integration testing.
- **Checkstyle**: For enforcing coding standards.
- **jscpd**: For detecting code duplication.
- **Docker**: For containerization and deployment.
- **Docker Compose**: For managing multi-container Docker applications.
- **GitHub Actions**: For CI/CD automation.

## Prerequisites

Before you begin, ensure you have the following installed:

- **Java JDK 23**
- **Maven 3.x**
- **Docker 20.x**
- **Docker Compose 2.x**

## Getting Started

### Clone the Repository

```bash
git clone https://github.com/NourAlPha/8-no_standups_please.git
cd 8-no_standups_please
```  

### Build the Project
```bash
mvn clean install
```  
### Run the App
```bash
mvn spring-boot:run
```  
### You can build the docker image
```bash
docker build -t your-app-name .
```
### To run the unit tests
```bash
mvn test
```
---

## Project Structure
```css
src/
├── main/
│   ├── java/
│   │   ├── com.example.com/
│   │   │   ├── controller/       
│   │   │   ├── service/          
│   │   │   ├── model/    
│   │   │   └── repository/      
│   └── resources/                 
└── test/
    └── java/                      
```
