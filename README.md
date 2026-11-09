# Student Complaint Box

A beginner-friendly college complaint management website.

## Technology
- Frontend: HTML, CSS, JavaScript
- Backend: Java 17, Spring Boot, REST API
- Database: H2 file database (data is saved locally)

## Requirements
1. JDK 17 or later
2. Maven 3.6+ (or use Maven support in VS Code)
3. Visual Studio Code
4. VS Code extensions: Extension Pack for Java and Spring Boot Extension Pack

## Run in VS Code
1. Extract this ZIP file.
2. Open the `Student-Complaint-Box` folder in VS Code (the folder containing `pom.xml`).
3. Make sure Java 17 and Maven are installed.
4. Open Terminal > New Terminal.
5. Run:

   `mvn spring-boot:run`

6. Wait until the terminal says the application has started.
7. Open `http://localhost:8080` in your browser.

Do not open `index.html` directly or use Live Server. Spring Boot serves the pages and API from the same address.

## First login
Administrator demo account:
- Email: `admin@college.com`
- Password: `Admin@123`

Students should select **Create account** and register with their own email and password.

## Pages
- `/` or `/index.html` — Login and registration
- `/student.html` — Student dashboard
- `/admin.html` — Administrator dashboard
- `/h2-console` — H2 database console for local development

## H2 console (optional)
- JDBC URL: `jdbc:h2:file:./data/complaintdb`
- User Name: `sa`
- Password: leave blank

## Important notes
- The demo administrator password is for local testing. Change it before any real deployment.
- This starter project uses simple session-based login and BCrypt password hashing. Before public deployment, add CSRF protection, HTTPS, stronger account controls, audit logs and proper administrator account provisioning.
- Complaint priority defaults to Medium if an older record has no priority.
- Do not deploy the H2 console publicly.
