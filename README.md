# Todo Application

A full-stack Todo application built with React and Spring Boot, featuring a modern UI and secure backend implementation.

## Project Structure

The project consists of two main components:

### Frontend (`ToDoFrontend/`)
- Built with React and Vite
- Uses Material-UI for modern UI components
- Features a responsive and user-friendly interface

### Backend (`todo/`)
- Spring Boot application
- RESTful API implementation
- JWT-based authentication
- PostgreSQL database integration
- Spring Security for enhanced security

## Tech Stack

### Frontend
- React 19
- Material-UI
- Vite
- ESLint for code quality

### Backend
- Spring Boot 3.4.5
- Spring Security
- Spring Data JPA
- PostgreSQL
- JWT Authentication
- Java 24

## Getting Started

### Prerequisites
- Node.js (for frontend)
- Java 24 (for backend)
- PostgreSQL database

### Frontend Setup
1. Navigate to the frontend directory:
   ```bash
   cd ToDoFrontend
   ```
2. Install dependencies:
   ```bash
   npm install
   ```
3. Start the development server:
   ```bash
   npm run dev
   ```

### Backend Setup
1. Navigate to the backend directory:
   ```bash
   cd todo
   ```
2. Configure your PostgreSQL database in `application.properties`
3. Run the Spring Boot application:
   ```bash
   ./gradlew bootRun
   ```

## Features
- User authentication and authorization
- CRUD operations for todo items
- Secure API endpoints
- Modern and responsive UI
- Real-time updates

## Development
- Frontend runs on `http://localhost:5173` by default
- Backend API runs on `http://localhost:8080` by default

## License
This project is licensed under the MIT License.