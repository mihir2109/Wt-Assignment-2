<?php
require __DIR__ . '/config.php';
if ($_SERVER['REQUEST_METHOD'] !== 'POST') { http_response_code(405); exit('POST required.'); }
$id = filter_input(INPUT_POST, 'id', FILTER_VALIDATE_INT);
if (!$id) { http_response_code(400); exit('Invalid ID.'); }
// Remove stored files first; metadata is removed by ON DELETE CASCADE.
$stmt = $pdo->prepare('SELECT stored_name FROM student_files WHERE student_id=?'); $stmt->execute([$id]);
foreach ($stmt->fetchAll() as $file) {
    $path = __DIR__ . '/uploads/' . basename($file['stored_name']);
    if (is_file($path)) unlink($path);
}
$del = $pdo->prepare('DELETE FROM students WHERE id=?'); $del->execute([$id]);
header('Location: index.php'); exit;
