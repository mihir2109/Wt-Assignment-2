
package com.portal;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/students")
public class StudentServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        // Check whether the user is logged in
        HttpSession session = request.getSession(false);

        if (session == null ||
            session.getAttribute("userId") == null) {

            response.sendRedirect("login.jsp");
            return;
        }

        List<Map<String, Object>> students =
            new ArrayList<>();

        String sql =
            "SELECT s.id, s.name, s.roll_number, s.email, s.course, " +
            "f.file_id, f.original_name, f.file_type " +
            "FROM students s " +
            "LEFT JOIN student_files f ON s.id=f.student_id " +
            "ORDER BY s.id";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            // Loop through the database results
            while (rs.next()) {

                Map<String, Object> student =
                    new HashMap<>();

                student.put("id", rs.getInt("id"));
                student.put("name", rs.getString("name"));
                student.put("roll", rs.getString("roll_number"));
                student.put("email", rs.getString("email"));
                student.put("course", rs.getString("course"));

                student.put("fileId", rs.getObject("file_id"));
                student.put("fileName",
                    rs.getString("original_name"));
                student.put("fileType",
                    rs.getString("file_type"));

                students.add(student);
            }

            // Send student records to the JSP page
            request.setAttribute("students", students);

            request.getRequestDispatcher("students.jsp")
                   .forward(request, response);

        } catch (Exception e) {
            throw new ServletException(
                "Unable to retrieve student details.", e
            );
        }
    }
}
