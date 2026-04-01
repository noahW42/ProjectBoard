# ProjectBoard

A full stack collaborative project management application inspired by Trello. Multiple users can work on the same board simultaneously with real time using websockets

## Features

- Real time collaboration via WebSockets/STOMP — changes sync instantly across all connected users
- Drag and drop task and column management with persistent ordering
- Session based authentication with BCrypt password hashing
- Project invite system — owners can add collaborators by username
- Task descriptions with inline editing

## Tech Stack

**Frontend**
- React
- @hello-pangea/dnd (drag and drop)
- @stomp/stompjs + SockJS (WebSockets)

**Backend**
- Spring Boot
- Spring Security (BCrypt)
- Spring WebSocket (STOMP)
- JPA / Hibernate
- PostgreSQL

## Prerequisites

- Node.js
- Java 17
- PostgreSQL
- Maven

## Getting Started

### 1. Clone the repository
```bash
git clone https://github.com/yourusername/projectboard.git
cd projectboard
```

### 2. Set up the database

Create a PostgreSQL database called `projectboard`:
```sql
CREATE DATABASE projectboard;
```

### 3. Configure the backend

Copy the example config and fill in your values:
```bash
cp ProjectboardBackend/src/main/resources/application.yaml.example ProjectboardBackend/src/main/resources/application.yaml
```

Open `application.yaml` and update:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/projectboard
    username: your_postgres_username
    password: your_postgres_password
```

### 4. Run the backend
```bash
cd ProjectboardBackend
mvn spring-boot:run
```

Backend runs on `http://localhost:8081`

### 5. Run the frontend
```bash
cd frontend
npm install
npm start
```

Frontend runs on `http://localhost:3000`

## Usage

1. Register an account at `http://localhost:3000`
2. Create a project from the dashboard
3. Invite collaborators by username
4. Open the project board in two browser windows to see real time collaboration in action

## Project Structure
```
projectboard/
├── frontend/                  # React frontend
│   ├── src/
│   │   ├── pages/             # LoginPage, RegisterPage, Home, ProjectBoardPage
│   │   ├── components/        # Column, Task
│   │   ├── hooks/             # useBoardSocket (WebSocket logic)
│   │   └── Base.css           # Global styles
├── ProjectboardBackend/       # Spring Boot backend
│   └── src/main/java/com/noah/projectboard/
│       ├── controller/        # REST + WebSocket controllers
│       ├── model/             # JPA entities
│       ├── repository/        # Spring Data repositories
│       ├── config/            # Security + WebSocket config
│       └── websocket/         # SocketEvent class
```

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /api/register | Register a new user |
| POST | /api/login | Login |
| POST | /api/logout | Logout |
| GET | /api/me | Get current user |
| GET | /api/projects | Get all projects for current user |
| POST | /api/projects | Create a project |
| GET | /api/projects/{id} | Get project with columns and tasks |
| POST | /api/projects/{id}/invite | Invite a user by username |
| POST | /api/projects/{id}/columns | Create a column |
| PUT | /api/projects/{id}/columns/reorder | Reorder columns |
| POST | /api/columns/{id}/tasks | Create a task |
| PUT | /api/tasks/{id} | Update task title/description |
| PUT | /api/tasks/{id}/move | Move task to another column |
| DELETE | /api/columns/{id} | Delete a column |
| DELETE | /api/tasks/{id} | Delete a task |
