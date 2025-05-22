# Todo Application with AI Integration

A full-stack Todo application with AI-powered task summarization using Gemini API and Slack notifications.

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
DATABASE_URL=jdbc:postgresql://aws-0-ap-south-1.pooler.supabase.com:5432/postgres
DATABASE_USERNAME=postgres.kvgmoomibfjrsemckshh
DATABASE_PASSWORD=Todoapp@2002
GEMINI_API_KEY=your_gemini_api_key
GEMINI_API_URL=https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent
SLACK_WEBHOOK_URL=your_slack_webhook_url
JWT_SECRET=your_jwt_secret
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
[Add screenshot of login/register pages]

### Todo List
[Add screenshot of todo list view]

### Todo Creation
[Add screenshot of todo creation form]

### Todo Details
[Add screenshot of todo details view]

### AI Summary
[Add screenshot of AI-generated task summary]

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

## Contributing

1. Fork the repository
2. Create your feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is licensed under the MIT License.