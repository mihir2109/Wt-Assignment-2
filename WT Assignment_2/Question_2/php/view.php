<?php
require __DIR__ . '/config.php';
$id = filter_input(INPUT_GET, 'id', FILTER_VALIDATE_INT);
if (!$id) { http_response_code(400); exit('Invalid student ID.'); }
$stmt = $pdo->prepare('SELECT id, name, roll_number, email, course, created_at FROM students WHERE id = ?');
$stmt->execute([$id]); $student = $stmt->fetch();
if (!$student) { http_response_code(404); exit('Student not found.'); }
$stmt = $pdo->prepare('SELECT original_name, file_path, file_type, file_size, uploaded_at FROM student_files WHERE student_id = ? ORDER BY id DESC');
$stmt->execute([$id]); $files = $stmt->fetchAll();
?>
<!doctype html><html lang="en"><head><meta charset="utf-8"><meta name="viewport" content="width=device-width, initial-scale=1"><title>Student profile</title><link rel="stylesheet" href="style.css"></head>
<body><main class="container narrow"><a href="index.php">← All students</a><section class="panel"><h1><?= e($student['name']) ?></h1>
<p><b>Roll number:</b> <?= e($student['roll_number']) ?></p><p><b>Email:</b> <?= e($student['email']) ?></p><p><b>Course:</b> <?= e($student['course']) ?></p><p><b>Created:</b> <?= e($student['created_at']) ?></p>
<h2>Uploaded files</h2><?php if (!$files): ?><p>No files uploaded.</p><?php else: ?><ul class="file-list">
<?php foreach ($files as $f): ?><li><a href="<?= e($f['file_path']) ?>" target="_blank" rel="noopener"><?= e($f['original_name']) ?></a>
— <?= e($f['file_type']) ?>, <?= number_format(((int)$f['file_size'])/1024, 1) ?> KB, uploaded <?= e($f['uploaded_at']) ?></li><?php endforeach; ?>
</ul><?php endif; ?><a class="button" href="edit.php?id=<?= (int)$student['id'] ?>">Edit details</a></section></main></body></html>
