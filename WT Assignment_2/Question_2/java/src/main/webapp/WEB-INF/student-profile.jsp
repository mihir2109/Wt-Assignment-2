<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%!
  // Escape user-provided values before writing them into HTML.
  private String esc(Object value) {
    if (value == null) return "";
    return value.toString().replace("&","&amp;").replace("<","&lt;")
      .replace(">","&gt;").replace("\"","&quot;").replace("'","&#x27;");
  }
%>
<!doctype html><html lang="en"><head><meta charset="utf-8"><meta name="viewport" content="width=device-width, initial-scale=1">
<title>Student profile</title><style>
body{font:16px/1.5 system-ui;background:#f3f6fb;color:#172033;margin:0}.wrap{max-width:900px;margin:36px auto;padding:20px}
.card{background:white;border-radius:14px;padding:24px;margin-bottom:18px;box-shadow:0 8px 30px #17203312}
.file{padding:12px 0;border-bottom:1px solid #e6eaf0}.preview{max-width:220px;max-height:220px;display:block;margin:8px 0}
</style></head><body><main class="wrap">
<% Map student = (Map)request.getAttribute("student"); List<Map> files = (List<Map>)request.getAttribute("files"); %>
<section class="card"><h1><%= esc(student.get("name")) %></h1>
<p><b>Roll number:</b> <%= esc(student.get("rollNumber")) %></p>
<p><b>Email:</b> <%= esc(student.get("email")) %></p><p><b>Course:</b> <%= esc(student.get("course")) %></p></section>
<section class="card"><h2>Uploaded files</h2>
<% if (files == null || files.isEmpty()) { %><p>No uploaded files were found.</p><% } else { for (Map f : files) { %>
<div class="file"><strong><%= esc(f.get("name")) %></strong>
<p>Type: <%= esc(f.get("type")) %> · Size: <%= esc(f.get("size")) %> bytes · Uploaded: <%= esc(f.get("uploadedAt")) %></p>
<% if (Boolean.TRUE.equals(f.get("image"))) { %>
  <img class="preview" alt="Uploaded student file" src="<%= request.getContextPath() %>/student-file?name=<%= java.net.URLEncoder.encode((String)f.get("storedName"), "UTF-8") %>">
<% } %>
<a href="<%= request.getContextPath() %>/student-file?name=<%= java.net.URLEncoder.encode((String)f.get("storedName"), "UTF-8") %>">Open/download file</a></div>
<% } } %></section></main></body></html>
