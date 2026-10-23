
<%@ page import="java.util.*" %>

<%!
    // Escape special HTML characters before displaying database text
    public String esc(Object value) {
        if (value == null) return "";

        return value.toString()
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#39;");
    }
%>

<%
    // Restrict this page to logged-in users
    if (session.getAttribute("userId") == null) {
        response.sendRedirect("login.jsp");
        return;
    }

    List<Map<String, Object>> students =
        (List<Map<String, Object>>) request.getAttribute("students");
%>

<!DOCTYPE html>
<html>
<head>
    <title>Student Profiles</title>

    <style>
        body {
            font-family: Arial, sans-serif;
            background: #eef2f7;
            padding: 20px;
        }

        h2 {
            color: #2457d6;
            text-align: center;
        }

        .profile {
            max-width: 650px;
            margin: 20px auto;
            background: white;
            padding: 20px;
            border-radius: 10px;
        }

        img {
            max-width: 150px;
            height: auto;
        }

        a {
            color: #2457d6;
        }
    </style>
</head>

<body>

<h2>Student Profiles</h2>

<% if (students == null || students.isEmpty()) { %>

    <p>No student records found.</p>

<% } else { %>

    <% for (Map<String, Object> student : students) { %>

        <div class="profile">

            <h3><%= esc(student.get("name")) %></h3>

            <p>Roll Number:
                <%= esc(student.get("roll")) %>
            </p>

            <p>Email:
                <%= esc(student.get("email")) %>
            </p>

            <p>Course:
                <%= esc(student.get("course")) %>
            </p>

            <% if (student.get("fileId") != null) { %>

                <p>Uploaded File:
                    <%= esc(student.get("fileName")) %>
                </p>

                <% String type =
                    (String) student.get("fileType"); %>

                <% if (type != null &&
                       type.startsWith("image/")) { %>

                    <img
                        src="file?id=<%= esc(student.get("fileId")) %>"
                        alt="Student profile image">

                <% } else { %>

                    <a href="file?id=<%= esc(student.get("fileId")) %>">
                        Open Document
                    </a>

                <% } %>

            <% } else { %>

                <p>No uploaded files.</p>

            <% } %>

        </div>

    <% } %>

<% } %>

<form action="logout" method="post">
    <button type="submit">Logout</button>
</form>

</body>
</html>
