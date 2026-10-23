
<!DOCTYPE html>
<html>
<head>
    <title>Student Portal Login</title>

    <style>
        body {
            font-family: Arial, sans-serif;
            background: #eef2f7;
            padding: 30px;
        }

        .login-box {
            max-width: 350px;
            margin: 50px auto;
            background: white;
            padding: 25px;
            border-radius: 10px;
        }

        input, button {
            width: 100%;
            box-sizing: border-box;
            padding: 10px;
            margin-top: 10px;
        }

        button {
            background: #2457d6;
            color: white;
            border: none;
            cursor: pointer;
        }

        .error {
            color: red;
        }
    </style>
</head>
<body>

<div class="login-box">

    <h2>Student Portal</h2>
    <p>Login to view student profiles.</p>

    <form action="login" method="post">

        <label>Username</label>
        <input type="text" name="username" required>

        <label>Password</label>
        <input type="password" name="password" required>

        <button type="submit">Login</button>
    </form>

    <p class="error">${requestScope.error}</p>

</div>

</body>
</html>
