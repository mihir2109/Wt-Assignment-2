package com.example;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.*;
import java.nio.file.*;
import java.sql.*;
import jakarta.servlet.ServletException;

@WebServlet("/student-file")
public class StudentFileServlet extends HttpServlet {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/student_portal";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";
    private static final Path UPLOAD_ROOT = Paths.get("C:/xampp/htdocs/student-portal/uploads").toAbsolutePath().normalize();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null || !Boolean.TRUE.equals(session.getAttribute("loggedIn"))) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED); return;
        }
        String name = req.getParameter("name");
        if (name == null || !name.matches("[a-f0-9]{32}\\.(jpg|jpeg|png|pdf|doc|docx)")) {
            resp.sendError(400, "Invalid file name."); return;
        }
        // Verify that the filename is actually recorded in the database.
        try (Connection c = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement ps = c.prepareStatement("SELECT file_type, original_name FROM student_files WHERE stored_name=?")) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) { resp.sendError(404); return; }
                Path p = UPLOAD_ROOT.resolve(name).normalize();
                if (!p.getParent().equals(UPLOAD_ROOT) || !Files.isRegularFile(p)) { resp.sendError(404); return; }
                resp.setContentType(rs.getString("file_type"));
                resp.setHeader("X-Content-Type-Options", "nosniff");
                resp.setHeader("Content-Disposition", "inline; filename=\"student-file\"");
                Files.copy(p, resp.getOutputStream());
            }
        } catch (SQLException ex) {
            resp.sendError(500, "Database error.");
        }
    }
}
