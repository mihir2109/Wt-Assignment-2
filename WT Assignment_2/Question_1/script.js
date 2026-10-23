
const form = document.getElementById("studentForm");

form.addEventListener("submit", function(event) {
    event.preventDefault();

    const name = document.getElementById("name").value.trim();
    const roll = document.getElementById("roll").value.trim();
    const email = document.getElementById("email").value.trim();
    const password = document.getElementById("password").value;
    const message = document.getElementById("message");

    message.textContent = "";

    if (!name || !roll || !email || !password) {
        message.textContent = "All required fields must be filled.";
        return;
    }

    const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

    if (!emailPattern.test(email)) {
        message.textContent = "Enter a valid email address.";
        return;
    }

    if (password.length < 8) {
        message.textContent =
            "Password must contain at least 8 characters.";
        return;
    }

    message.style.color = "green";
    message.textContent =
        "Validation successful! Registration form is valid.";
});
