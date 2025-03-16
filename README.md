# FlightPlanner

## Overview
This project is a **Vue.js and Spring Boot** web application designed to help air travelers plan flights and receive seat recommendations based on their preferences.

## Table of Contents

- [How to Set Up the Project](#how-to-set-up-the-project)
- [How to Build the Project](#how-to-build-the-project)
- [Starting the Application with PostgreSQL](#Starting-the-Application-with-PostgreSQL)


---
## How to Set Up the Project

Clone the repository to your local machine:
   ```bash
   git clone https://github.com/anpeep/FlightPlanner.git
   cd nameOfYourChoice

   ```
Prerequisites:
JDK 21
Docker and Docker Compose installed for containerization
Node

## How to Build the Project

First, backend
   ```bash
   
cd backend
docker build -t backend-app .
docker run -d -p 8080:8080 backend-app
   ```
Then frontend
   ```bash
cd frontend
docker build -t frontend-app .
docker run -d -p 3000:3000 frontend-app
   ```
## Starting the Application with PostgreSQL
To start both the backend application and PostgreSQL, run:
   ```bash
   docker-compose up --build
   ```
The backend will be accessible at http://localhost:8080 and the PostgreSQL database will be running on port 5432.
Stopping the Services
To stop and remove the containers, run:

   ```bash
   docker-compose down
   ```
