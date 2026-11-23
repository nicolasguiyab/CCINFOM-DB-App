
document.addEventListener("DOMContentLoaded", () => {

    const loginForm = document.getElementById("loginForm");

    loginForm.addEventListener("submit", async (event) => {
        event.preventDefault();

        const customerID =
            document.getElementById("customerID").value.trim();

        const password =
            document.getElementById("password").value;

        try {
            const response = await fetch(
                "http://localhost:8080/api/login",
                {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/x-www-form-urlencoded"
                    },
                    body: new URLSearchParams({
                        customerID: customerID,
                        password: password
                    })
                }
            );

            const result = await response.text();

            if (!response.ok) {
                alert(result);
                return;
            }

            // Remember the logged-in customer
            localStorage.setItem("customerID", customerID);

            alert("Login successful!");

            window.location.href = "./reservation.html";

        } catch (error) {
            console.error("Login error:", error);

            alert(
                "Unable to connect to the server. " +
                "Make sure RegistrationServer is running."
            );
        }
    });

});
