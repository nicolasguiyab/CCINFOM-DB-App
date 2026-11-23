document.addEventListener("DOMContentLoaded", () => {
    const form = document.getElementById("registerForm");

    const customerType = document.getElementById("customerType");
    const studentFields = document.getElementById("studentFields");
    const governmentFields = document.getElementById("governmentFields");

    const password = document.getElementById("password");
    const confirmPassword = document.getElementById("confirmPassword");

    // Show the appropriate ID fields
    customerType.addEventListener("change", () => {
        const isStudent = customerType.value === "Student";

        studentFields.hidden = !isStudent;
        governmentFields.hidden = isStudent;

        document.getElementById("studentID").disabled = !isStudent;
        document.getElementById("university").disabled = !isStudent;

        document.getElementById("govID").disabled = isStudent;
        document.getElementById("idType").disabled = isStudent;

        document.getElementById("studentID").required = isStudent;
        document.getElementById("govID").required = !isStudent;
        document.getElementById("idType").required = !isStudent;
    });

    // Submit registration
    form.addEventListener("submit", async (event) => {
        event.preventDefault();

        if (!form.checkValidity()) {
            form.reportValidity();
            return;
        }

        if (password.value.length < 8) {
            alert("Password must be at least 8 characters long.");
            return;
        }

        if (password.value !== confirmPassword.value) {
            alert("Passwords do not match.");
            return;
        }

        try {
            const response = await fetch(
                "http://localhost:8080/api/register",
                {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/x-www-form-urlencoded"
                    },
                    body: new URLSearchParams(new FormData(form))
                }
            );

            const result = await response.text();

            if (!response.ok) {
                throw new Error(result);
            }

            alert(result);
            form.reset();

            studentFields.hidden = true;
            governmentFields.hidden = true;

        } catch (error) {
            console.error("Registration error:", error);
            alert("Registration failed. Make sure the Java server is running.\n\n" + error.message);
        }
    });
});