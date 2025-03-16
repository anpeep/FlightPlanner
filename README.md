# FlightPlanner

## Overview
This project is a **Vue.js and Spring Boot** web application designed to help air travelers plan flights and receive seat recommendations based on their preferences.

## Table of Contents

- [How to Set Up the Project](#how-to-set-up-the-project)
- [How to Build the Project](#how-to-build-the-project)
- [Starting the Application with PostgreSQL](#Starting-the-Application-with-PostgreSQL)
- [Stopping the Services](#Stopping-the-Services)


---
## How to Set Up the Project

Clone the repository to your local machine:
```bash
    git clone https://github.com/anpeep/FlightPlanner.git
    cd FlightPlanner
```

Prerequisites:
JDK 21
Docker and Docker Compose installed for containerization
Node.js and npm (for the frontend)
Maven (for building the backend)

## Starting the Application with PostgreSQL
To start both the backend application and PostgreSQL, run:
```bash
docker-compose up --build postgres frontend

```

Then, if you are in the root directory, navigate to backend side:
```bash
  cd backend/flightplanner
```

Start Maven:
```bash
  mvn spring-boot:run
```

Then open new window in terminal and navigate to front:
   ```bash
    cd frontend
   ```
Then run:
````bash
    npm run dev
````

The backend will be accessible at http://localhost:8080, frontend on http://localhost:5173 and the PostgreSQL database will be running on port 5432.
## Stopping the Services
To stop and remove the containers, run:
```bash
docker-compose down
```
