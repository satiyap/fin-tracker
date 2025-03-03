# Fin-Tracker

A comprehensive personal finance application for tracking expenses, income, scheduled payments, investments, and more.

## Features

- Track expenses and income
- Manage multiple accounts
- Schedule recurring transactions
- Track investments and their performance
- Categorize transactions
- Generate reports and analytics
- Responsive web interface

## Architecture

This project consists of:
- **Backend**: Spring Boot REST API
- **Frontend**: React-based UI (in separate repository: fin-tracker-ui)

## Technology Stack

### Backend
- Spring Boot 2.7.0
- Spring MVC
- Spring Data JPA
- Spring Security
- JWT Authentication
- H2 Database (for development)
- PostgreSQL (for production)
- Gradle
- Swagger UI for API documentation

### Frontend
- React 18
- React Router 6
- Bootstrap 5
- Chart.js
- Formik & Yup for form validation
- Axios for API requests
- React-Toastify for notifications
- FontAwesome icons

## Prerequisites

- Java 11 or higher
- Gradle 7.0 or higher
- Node.js 14.x or higher
- npm 6.x or higher
- PostgreSQL (for production)

## Building and Running the Application

### Backend

1. Clone the repository:
```bash
git clone https://github.com/sprasath/fin-tracker.git
cd fin-tracker
```
2. Build the application:
```bash
mvn clean install
```
3. Run the application:
```bash
mvn spring-boot:run
```
The API will be available at http://localhost:8080. API documentation will be available at http://localhost:8080/swagger-ui/index.html

### Frontend

1. Clone and navigate to the frontend directory:
```bash
git clone https://github.com/sprasath/fin-tracker-ui.git
cd fin-tracker-ui
```
2. Install dependencies:
```bash
npm install
```

3. Create a .env file with:
```markdown
REACT_APP_API_URL=http://localhost:8080/api/v1
```

4. Start the development server:
```bash
npm start
```
The frontend will be available at http://localhost:3000.

# Project Structure
## Backend
fintracker - Java source files
resources - Configuration files
test - Test files
## Frontend
api - API integration functions
components - React components organized by feature
context - React context providers
hooks - Custom React hooks
pages - Page components
utils - Utility functions
## License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
