<?php
require __DIR__ . '/config.php';
$students = $pdo->query('SELECT id, name, roll_number, email, course, created_at FROM students ORDER BY id DESC')->fetchAll();
?>
<!doctype html>
<html lang="en">
<head>
<meta charset="utf-8"><meta name="viewport" content="width=device-width, initial-scale=1">
<title>Student Portal</title><link rel="stylesheet" href="style.css">
</head>
<body>
<main class="container">
  <header class="top"><div><h1>Student Portal</h1><p>Register and manage student records</p></div>
  <a class="button" href="register.html">Register student</a></header>
  <section class="panel">
    <h2>Student records</h2>
    <?php if (!$students): ?><p>No students yet. Register the first student.</p>
    <?php else: ?>
    <div class="table-wrap"><table><thead><tr><th>Name</th><th>Roll no.</th><th>Email</th><th>Course</th><th>Actions</th></tr></thead><tbody>
    <?php foreach ($students as $s): ?>
      <tr>
        <td><?= e($s['name']) ?></td><td><?= e($s['roll_number']) ?></td><td><?= e($s['email']) ?></td><td><?= e($s['course']) ?></td>
        <td class="actions"><a href="view.php?id=<?= (int)$s['id'] ?>">View</a> <a href="edit.php?id=<?= (int)$s['id'] ?>">Edit</a>
        <form method="post" action="delete.php" onsubmit="return confirm('Delete this student and their file records?')">
          <input type="hidden" name="id" value="<?= (int)$s['id'] ?>"><button class="link danger" type="submit">Delete</button>
        </form></td>
      </tr>
    <?php endforeach; ?>
    </tbody></table></div><?php endif; ?>
  </section>
</main>
</body></html>
