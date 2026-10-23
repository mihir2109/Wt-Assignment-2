
package com.portal;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/file")
public class FileServlet extends HttpServlet {

    // Change this to the actual upload directory
    private static final Path UPLOAD_DIR =
        Paths.get("C:/xampp/htdocs/student_admin/uploads")
             .toAbsolutePath().normalize();

    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        // Check login session
        HttpSession session = request.getSession(false);

        if (session == null ||
            session.getAttribute("userId") == null) {

            response.sendError(
                HttpServletResponse.SC_UNAUTHORIZED
            );
            return;
        }

        // Read the requested file ID
        int fileId;

        try {
            fileId = Integer.parseInt(
                request.getParameter("id")
            );
        } catch (Exception e) {
            response.sendError(
                HttpServletResponse.SC_BAD_REQUEST
            );
            return;
        }

        String storedName;
        String fileType;

        // Fetch file information from MySQL
        String sql =
            "SELECT stored_name, file_type " +
            "FROM student_files WHERE file_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, fileId);

            try (ResultSet rs = ps.executeQuery()) {

                if (!rs.next()) {
                    response.sendError(
                        HttpServletResponse.SC_NOT_FOUND
                    );
                    return;
                }

                storedName = rs.getString("stored_name");
                fileType = rs.getString("file_type");
            }

        } catch (Exception e) {
            throw new ServletException(
                "Unable to retrieve file details.", e
            );
        }

        // Resolve the stored filename inside the upload directory
        Path filePath = UPLOAD_DIR.resolve(storedName).normalize();

        if (!filePath.startsWith(UPLOAD_DIR) ||
            !Files.isRegularFile(filePath)) {

            response.sendError(
                HttpServletResponse.SC_NOT_FOUND
            );
            return;
        }

        // Allow only the file types supported by this application
        if (!"image/jpeg".equals(fileType) &&
            !"image/png".equals(fileType) &&
            !"application/pdf".equals(fileType)) {

            response.sendError(
                HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE
            );
            return;
        }

        response.setContentType(fileType);
        response.setContentLengthLong(Files.size(filePath));

        // Stream the file to the browser
        try (InputStream in = Files.newInputStream(filePath);
             OutputStream out = response.getOutputStream()) {

            byte[] buffer = new byte[4096];
            int count;

            while ((count = in.read(buffer)) != -1) {
                out.write(buffer, 0, count);
            }
        }
    }
}
