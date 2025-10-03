async function iniciarSesion() {
    // Get form data from login form
    const email = document.getElementById('exampleInputEmail').value;
    const password = document.getElementById('exampleInputPassword').value;

    // Validate required fields
    if (!email || !password) {
        alert('Por favor, ingrese email y contraseña');
        return;
    }

    // Create login object
    const loginData = {
        email: email,
        password: password
    };

    try {
        const response = await fetch('api/login', {
            method: 'POST',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(loginData)
        });

        if (response.ok) {
            const responseData = await response.json();
            console.log('Login response:', responseData); // Debug log
            alert('Inicio de sesión exitoso');

            // Store token and user info separately in localStorage
            localStorage.setItem('token', responseData.token);
            localStorage.setItem('usuario', JSON.stringify(responseData.usuario));

            // Debug: verify storage
            console.log('Token stored:', localStorage.getItem('token'));
            console.log('Usuario stored:', localStorage.getItem('usuario'));

            // Redirect to main page or dashboard
            window.location.href = 'index.html';
        } else if (response.status === 401) {
            alert('Email o contraseña incorrectos');
        } else {
            alert('Error al iniciar sesión');
        }
    } catch (error) {
        console.error('Error:', error);
        alert('Error al conectar con el servidor');
    }
}
