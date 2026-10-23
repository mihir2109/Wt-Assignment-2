<?php
require __DIR__ . '/config.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST') { header('Location: register.html'); exit; }
$name = trim($_POST['name'] ?? '');
$roll = trim($_POST['roll_number'] ?? '');
$email = trim($_POST['email'] ?? '');
$course = trim($_POST['course'] ?? '');
$password = $_POST['password'] ?? '';

if ($name === '' || $roll === '' || $course === '' || !filter_var($email, FILTER_VALIDATE_EMAIL) || strlen($password) < 8) {
    http_response_code(422); exit('Invalid input. Go back and check all fields.');
}

try {
    $stmt = $pdo->prepare('INSERT INTO students (name, roll_number, email, course, password_hash) VALUES (?, ?, ?, ?, ?)');
    $stmt->execute([$name, $roll, $email, $course, password_hash($password, PASSWORD_DEFAULT)]);
    $studentId = (int)$pdo->lastInsertId();

    $uploadDir = __DIR__ . '/uploads';
    if (!is_dir($uploadDir) && !mkdir($uploadDir, 0750, true)) throw new RuntimeException('Cannot create upload folder.');
    $allowed = [
        'jpg' => ['image/jpeg'], 'jpeg' => ['image/jpeg'], 'png' => ['image/png'],
        'pdf' => ['application/pdf'], 'doc' => ['application/msword'],
        'docx' => ['application/vnd.openxmlformats-officedocument.wordprocessingml.document']
    ];
    $maxBytes = 5 * 1024 * 1024;
    $finfo = new finfo(FILEINFO_MIME_TYPE);

    if (isset($_FILES['files']) && is_array($_FILES['files']['name'])) {
        foreach ($_FILES['files']['name'] as $i => $original) {
            if ($_FILES['files']['error'][$i] === UPLOAD_ERR_NO_FILE) continue;
            if ($_FILES['files']['error'][$i] !== UPLOAD_ERR_OK) throw new RuntimeException('A file upload failed.');
            $tmp = $_FILES['files']['tmp_name'][$i];
            $size = (int)$_FILES['files']['size'][$i];
            $ext = strtolower(pathinfo($original, PATHINFO_EXTENSION));
            $mime = $finfo->file($tmp);
            if ($size > $maxBytes || !isset($allowed[$ext]) || !in_array($mime, $allowed[$ext], true)) {
                throw new RuntimeException('File type or size is not allowed: ' . basename($original));
            }
            $stored = bin2hex(random_bytes(16)) . '.' . $ext; // random name prevents overwrite
            $destination = $uploadDir . '/' . $stored;
            if (!move_uploaded_file($tmp, $destination)) throw new RuntimeException('Could not store uploaded file.');
            $meta = $pdo->prepare('INSERT INTO student_files (student_id, original_name, stored_name, file_path, file_type, file_size) VALUES (?, ?, ?, ?, ?, ?)');
            $meta->execute([$studentId, basename($original), $stored, 'uploads/' . $stored, $mime, $size]);
        }
    }
    header('Location: view.php?id=' . $studentId); exit;
} catch (PDOException $e) {
    http_response_code(400);
    exit('Could not create student. Check that the roll number is unique and the database is running.');
} catch (Throwable $e) {
    http_response_code(400);
    exit('Registration/upload failed: ' . htmlspecialchars($e->getMessage(), ENT_QUOTES, 'UTF-8'));
}
