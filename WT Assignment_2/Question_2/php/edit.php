<?php
require __DIR__ . '/config.php';
$id = filter_input(INPUT_GET, 'id', FILTER_VALIDATE_INT);
if (!$id) { http_response_code(400); exit('Invalid ID.'); }
$stmt = $pdo->prepare('SELECT id, name, roll_number, email, course FROM students WHERE id=?');
$stmt->execute([$id]); $s = $stmt->fetch();
if (!$s) { http_response_code(404); exit('Student not found.'); }
$error = '';
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $name=trim($_POST['name']??''); $roll=trim($_POST['roll_number']??'');
    $email=trim($_POST['email']??''); $course=trim($_POST['course']??'');
    if (!$name || !$roll || !$course || !filter_var($email, FILTER_VALIDATE_EMAIL)) $error='Enter valid values in all fields.';
    else {
        try {
            $up=$pdo->prepare('UPDATE students SET name=?, roll_number=?, email=?, course=? WHERE id=?');
            $up->execute([$name,$roll,$email,$course,$id]); header('Location: view.php?id='.$id); exit;
        } catch (PDOException $e) { $error='Update failed. The roll number may already be in use.'; }
    }
}
?>
<!doctype html><html lang="en"><head><meta charset="utf-8"><meta name="viewport" content="width=device-width, initial-scale=1"><title>Edit student</title><link rel="stylesheet" href="style.css"></head>
<body><main class="container narrow"><section class="panel"><h1>Edit student</h1><?php if($error): ?><p class="error"><?= e($error) ?></p><?php endif; ?>
<form method="post"><label>Name</label><input name="name" required value="<?= e($s['name']) ?>"><label>Roll number</label><input name="roll_number" required value="<?= e($s['roll_number']) ?>">
<label>Email</label><input type="email" name="email" required value="<?= e($s['email']) ?>"><label>Course</label><input name="course" required value="<?= e($s['course']) ?>"><button type="submit">Save changes</button></form></section></main></body></html>
