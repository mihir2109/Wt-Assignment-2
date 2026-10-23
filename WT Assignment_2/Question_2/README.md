# Student Portal Assignment Solution

This starter project demonstrates:
1. HTML/CSS/JavaScript registration form validation.
2. PHP + MySQL CRUD and secure-ish file upload with metadata.
3. Java Servlet/JSP + JDBC profile viewing with session check.

## Requirements
- XAMPP (Apache + MySQL/MariaDB + PHP) for PHP section.
- JDK 17+ and Apache Tomcat 10.1+ for the Java section.
- MySQL Connector/J for Java database connectivity.
- VS Code or NetBeans.

## A. Run the PHP project
1. Start Apache and MySQL in XAMPP.
2. Copy the `php` folder into `C:\xampp\htdocs\student-portal`.
3. Open phpMyAdmin at `http://localhost/phpmyadmin`.
4. Import `database/schema.sql` (or create the database and run the SQL manually).
5. Check `php/config.php` database credentials. XAMPP commonly uses user `root` and a blank password on a local development machine.
6. Visit `http://localhost/student-portal/`.
7. Uploads are stored in `php/uploads/`. In this starter, files are kept in a folder under the project for simple local testing. For production, store uploads outside the public web root and serve them through a validated download endpoint.

## B. Run the Java Servlet/JSP section
1. Create a Maven Web Application in NetBeans, or use this folder as the source reference.
2. Use Tomcat 10.1+ (Jakarta Servlet API). Add MySQL Connector/J to the project dependencies.
3. Copy `java/src/main/java/com/example/StudentServlet.java` into the matching package in your web project.
4. Copy `java/src/main/webapp/WEB-INF/student-profile.jsp` into `src/main/webapp/WEB-INF/`.
5. Configure DB credentials in the servlet.
6. Set `UPLOAD_ROOT` to the absolute path of the PHP uploads folder, or to the directory where you keep uploaded files.
7. Configure the login route in your project. The example servlet checks a session attribute named `loggedIn`; your login servlet/page must set `session.setAttribute("loggedIn", true)` after successful authentication.
8. Deploy to Tomcat and visit `/student-profile?id=1`.

## Notes
- This is an educational local-development starter, not a production-ready authentication system.
- Change database credentials and add CSRF protection, secure authentication, authorization, audit logging, and download access controls before real deployment.
- The upload handler uses a strict extension allowlist, size limit, MIME check, random stored filenames, and avoids overwriting existing files.
