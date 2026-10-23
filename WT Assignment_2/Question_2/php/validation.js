const form = document.getElementById('registrationForm');
if (form) {
  form.addEventListener('submit', (event) => {
    const name = document.getElementById('name').value.trim();
    const roll = document.getElementById('roll_number').value.trim();
    const email = document.getElementById('email').value.trim();
    const course = document.getElementById('course').value.trim();
    const password = document.getElementById('password').value;
    const error = document.getElementById('formError');
    let message = '';
    if (!name || !roll || !email || !course || !password) message = 'Please complete every required field.';
    else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) message = 'Enter a valid email address.';
    else if (password.length < 8) message = 'Password must contain at least 8 characters.';
    if (message) { event.preventDefault(); error.textContent = message; }
    else { error.textContent = ''; }
  });
}
