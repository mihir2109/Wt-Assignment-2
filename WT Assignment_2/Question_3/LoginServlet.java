
package com.portal;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.mindrot.jbcrypt.BCrypt;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        String sql =
            "SELECT user_id, password_hash, role " +
            "FROM users WHERE username=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next() &&
                    BCrypt.checkpw(
                        password,
                        rs.getString("password_hash")
                    )) {

                    // Create a session for the logged-in user
                    HttpSession session =
                        request.getSession(true);

                    session.setAttribute(
                        "userId", rs.getInt("user_id")
                    );

                    session.setAttribute(
                        "role", rs.getString("role")
                    );

                    response.sendRedirect("students");

                } else {
                    request.setAttribute(
                        "error",
                        "Invalid username or password."
                    );

                    request.getRequestDispatcher("login.jsp")
                           .forward(request, response);
                }
            }

        } catch (Exception e) {
            throw new ServletException(
                "Unable to verify login.", e
            );
        }
    }

    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        request.getRequestDispatcher("login.jsp")
               .forward(request, response);
    }
}
