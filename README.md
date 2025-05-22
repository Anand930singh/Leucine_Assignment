# Todo Application with AI Integration

A full-stack Todo application with AI-powered task summarization using Gemini API and Slack notifications.

## ⚠️ Important Note
The backend is hosted on Render's free tier, which means:
- The first request after inactivity (cold start) may take 3-4 minutes to respond
- This is normal behavior for free tier hosting services
- Subsequent requests will be much faster
- Please be patient during the initial load

## Features

- User authentication with JWT
- CRUD operations for todos
- AI-powered task summarization using Google's Gemini API
- Slack notifications for task updates
- Filtering and pagination for todos
- Responsive frontend design

## Tech Stack

### Backend
- Java 21
- Spring Boot 3.2.3
- Spring Security with JWT
- PostgreSQL
- Gradle

### Frontend
- React
- Axios for API calls
- Material-UI for components

## System Architecture & Design Decisions

### Backend Architecture
1. **Layered Architecture**
   - Controller Layer: REST endpoints with request validation
   - Service Layer: Business logic and transaction management
   - Repository Layer: Data access with JPA/Hibernate
   - DTO Layer: Data transfer objects for API contracts

2. **Security Design**
   - JWT-based stateless authentication
   - Password encryption using BCrypt
   - CORS configuration for frontend security

3. **Database Design**
   - PostgreSQL with connection pooling
   - Efficient pagination implementation
   - Transaction management

4. **Error Handling**
   - Global exception handler
   - Structured error responses

### Frontend Architecture
1. **Component Design**
   - Reusable UI components
   - Custom hooks for logic reuse

2. **State Management**
   - React Context for global state
   - Local state for UI components
   - Persistent auth state

3. **API Integration**
   - Axios for HTTP requests
   - Request/response interceptors
   - Error handling middleware
   - API response caching

## Integration Setup Guide

### Slack Integration
1. **Initial Setup**
   ```bash
   # Create Slack App
   1. Go to api.slack.com/apps
   2. Click "Create New App"
   3. Choose "From scratch"
   4. Name your app and select workspace
   ```

2. **Webhook Configuration**
   ```bash
   # Enable Incoming Webhooks
   1. Go to "Incoming Webhooks"
   2. Toggle "Activate Incoming Webhooks"
   3. Click "Add New Webhook to Workspace"
   4. Choose channel and authorize
   ```

3. **Environment Setup**
   ```properties
   # application.properties
   slack.webhook.url=${SLACK_WEBHOOK_URL}
   slack.notification.enabled=true
   slack.channel=#your-channel
   ```

4. **Notification Types**
   - Task Creation: New task alerts
   - Task Updates: Status changes
   - Task Completion: Achievement notifications
   - Error Alerts: System issues

### Gemini LLM Integration
1. **Google Cloud Setup**
   ```bash
   # Enable Gemini API
   1. Go to Google Cloud Console
   2. Create new project
   3. Enable Gemini API
   4. Create API key
   ```

2. **Configuration**
   ```properties
   # application.properties
   gemini.api.key=${GEMINI_API_KEY}
   gemini.api.url=${GEMINI_API_URL}
   gemini.model.name=gemini-pro
   gemini.max.tokens=1000
   ```

3. **Implementation Features**
   - Task Summarization
     ```java
     // Example usage
     @Autowired
     private GeminiService geminiService;
     
     public String summarizeTasks(List<Todo> tasks) {
         return geminiService.generateSummary(tasks);
     }
     ```


## Prerequisites

- Java 21
- Node.js and npm
- PostgreSQL database
- Google Cloud account (for Gemini API)
- Slack workspace (for notifications)

## Local Setup

### Backend Setup

1. Clone the repository:
```bash
git clone <repository-url>
cd todo
```

2. Configure environment variables:
Create a `.env` file in the `todo` directory with:
```
PORT=4040
DATABASE_URL=xxxxxxxxx
DATABASE_USERNAME=xxxxxxxxxx
DATABASE_PASSWORD=xxxxxxxxxxxxx
GEMINI_API_KEY=xxxxxxxxxxxxxxx
GEMINI_API_URL=xxxxxxxxxxxx
SLACK_WEBHOOK_URL=xxxxxxxxxxxxx
JWT_SECRET=xxxxxxxxxxxxxxxxx
```

3. Build and run the backend:
```bash
./gradlew build
./gradlew bootRun
```

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
npm start
```

## API Documentation

### Authentication

#### Register User
```http
POST /api/auth/register
Content-Type: application/json

{
    "username": "string",
    "email": "string",
    "password": "string"
}
```

Response:
```json
{
    "status": 201,
    "message": "User registered successfully!"
}
```

#### Login
```http
POST /api/auth/login
Content-Type: application/json

{
    "userNameOrEmail": "string",
    "password": "string"
}
```

Response:
```json
{
    "status": 200,
    "message": "Login successful",
    "data": {
        "token": "jwt_token"
    }
}
```

### Todo Operations

#### Create Todo
```http
POST /api/todos
Authorization: Bearer <jwt_token>
Content-Type: application/json

{
    "task": "string",
    "status": "PENDING"
}
```

Response:
```json
{
    "status": 201,
    "message": "Todo added successfully"
}
```

#### Get Todos with Filter
```http
POST /api/todos/filter
Authorization: Bearer <jwt_token>
Content-Type: application/json

{
    "task": "string",
    "status": "PENDING",
    "createdAt": "2024-03-20T10:00:00Z",
    "updatedAt": "2024-03-20T10:00:00Z",
    "page": 0,
    "size": 10
}
```

Response:
```json
{
    "status": 200,
    "message": "Todos fetched successfully",
    "data": {
        "content": [
            {
                "id": 1,
                "task": "string",
                "status": "PENDING",
                "createdAt": "2024-03-20T10:00:00Z",
                "updatedAt": "2024-03-20T10:00:00Z"
            }
        ],
        "totalElements": 1,
        "totalPages": 1,
        "size": 10,
        "number": 0
    }
}
```

#### Update Todo
```http
PUT /api/todos/{id}
Authorization: Bearer <jwt_token>
Content-Type: application/json

{
    "task": "string",
    "status": "COMPLETED"
}
```

Response:
```json
{
    "status": 200,
    "message": "Todo updated successfully"
}
```

#### Delete Todo
```http
DELETE /api/todos/{id}
Authorization: Bearer <jwt_token>
```

Response:
```json
{
    "status": 200,
    "message": "Todo deleted successfully"
}
```

### AI Integration

#### Get Task Summary
```http
GET /api/llm/summarize-tasks
Authorization: Bearer <jwt_token>
```

Response:
```json
{
    "status": 200,
    "message": "Summary generated successfully",
    "data": "AI-generated summary of pending tasks"
}
```

## Screenshots

### Authentication
![image](https://github.com/user-attachments/assets/f5306820-cee9-4f13-ae56-29d6ac7d3772)
![image](https://github.com/user-attachments/assets/892992bb-c8e3-4d96-8b0d-7cd3cc979501)

### Todo List
![image](https://github.com/user-attachments/assets/8555f927-a65d-452b-aa1f-a5d4673d9563)

### Todo Creation
![image](https://github.com/user-attachments/assets/fe439790-9ede-4af8-bdfb-5464eac231d0)

### AI Summary
![image](https://github.com/user-attachments/assets/c42e1e0e-e37b-4be6-80ae-3db0de0d9416)


## Deployment

### Backend Deployment on Render
1. Create a new Web Service
2. Connect your repository
3. Set build command: `./gradlew build`
4. Set start command: `java -jar build/libs/todo.jar`
5. Add environment variables from `.env` file
6. Deploy

### Frontend Deployment
1. Build the frontend:
```bash
cd ToDoFrontend
npm run build
```
2. Deploy the `build` folder to your preferred hosting service
