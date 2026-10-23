package com.example;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.nio.file.*;
import java.sql.*;
import java.util.*;

@WebServlet("/student-profile")
public class StudentServlet extends HttpServlet {
    // Update these for your local MySQL setup.
    private static final String DB_URL = "jdbc:mysql://localhost:3306/student_portal";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";
    // Point this to the PHP upload directory, or your chosen shared storage folder.
    private static final Path UPLOAD_ROOT = Paths.get("C:/xampp/htdocs/student-portal/uploads").toAbsolutePath().normalize();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || !Boolean.TRUE.equals(session.getAttribute("loggedIn"))) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String rawId = request.getParameter("id");
        int id;
        try { id = Integer.parseInt(rawId); }
        catch (Exception ex) { response.sendError(400, "A valid student id is required."); return; }

        String studentSql = "SELECT id,name,roll_number,email,course FROM students WHERE id=?";
        String filesSql = "SELECT original_name,stored_name,file_type,file_size,uploaded_at FROM student_files WHERE student_id=? ORDER BY id DESC";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement ps = conn.prepareStatement(studentSql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) { response.sendError(404, "Student not found."); return; }
                Map<String,Object> student = new HashMap<>();
                student.put("id", rs.getInt("id"));
                student.put("name", rs.getString("name"));
                student.put("rollNumber", rs.getString("roll_number"));
                student.put("email", rs.getString("email"));
                student.put("course", rs.getString("course"));
                request.setAttribute("student", student);
            }
            List<Map<String,Object>> files = new ArrayList<>();
            try (PreparedStatement fps = conn.prepareStatement(filesSql)) {
                fps.setInt(1, id);
                try (ResultSet frs = fps.executeQuery()) {
                    while (frs.next()) {
                        String stored = frs.getString("stored_name");
                        Path safePath = UPLOAD_ROOT.resolve(stored).normalize();
                        // Only allow direct children of UPLOAD_ROOT; do not trust a DB path from request input.
                        if (!safePath.getParent().equals(UPLOAD_ROOT) || !Files.isRegularFile(safePath)) continue;
                        Map<String,Object> file = new HashMap<>();
                        file.put("name", frs.getString("original_name"));
                        file.put("storedName", stored);
                        file.put("type", frs.getString("file_type"));
                        file.put("size", frs.getLong("file_size"));
                        file.put("uploadedAt", frs.getTimestamp("uploaded_at"));
                        file.put("image", frs.getString("file_type").startsWith("image/"));
                        files.add(file);
                    }
                }
            }
            request.setAttribute("files", files);
            request.getRequestDispatcher("/WEB-INF/student-profile.jsp").forward(request, response);
        } catch (SQLException ex) {
            throw new ServletException("Unable to retrieve student profile. Check DB settings.", ex);
        }
    }
}
