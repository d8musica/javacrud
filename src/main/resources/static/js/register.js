// Call the dataTables jQuery plugin
$(document).ready(function() {
    cargarUsuarios();
    $('#usuarios').DataTable();
});

async function cargarUsuarios() {
    const req = await fetch('api/usuarios', {
        method: 'GET',
        headers: { 'Accept': 'application/json', 'Content-Type': 'application/json' }
    });
    const usuarios = await req.json();

    document.querySelector('#usuarios tbody').innerHTML = '';
    for (let usuario of usuarios) { // Recorremos el JSON que nos ha devuelto el servidor
        document.querySelector('#usuarios tbody').innerHTML += `
            <tr>
                <td>${usuario.id}</td>
                <td>${usuario.nombre} ${usuario.apellido}</td>
                <td>${usuario.email}</td>
                <td>${usuario.telefono}</td>
                <td>
                    <button class="btn btn-sm btn-primary">Editar</button>
                    <button class="btn btn-sm btn-danger" onclick="eliminarUsuario(${usuario.id})">Eliminar</button>
                </td>
            </tr>
        `;
    }
}

async function eliminarUsuario(id) {
    if (!confirm('¿Estás seguro de que deseas eliminar este usuario?')) {
        return;
    }

    try {
        const response = await fetch(`api/usuarios/${id}`, {
            method: 'DELETE',
            headers: { 'Accept': 'application/json', 'Content-Type': 'application/json' }
        });

        if (response.ok) {
            alert('Usuario eliminado correctamente');
            cargarUsuarios(); // Recargar la tabla
        } else {
            alert('Error al eliminar el usuario');
        }
    } catch (error) {
        console.error('Error:', error);
        alert('Error al eliminar el usuario');
    }
}

async function registrarUsuario() {
    // Get form data using actual field IDs from register.html
    const nombre = document.getElementById('exampleFirstName').value;
    const apellido = document.getElementById('exampleLastName').value;
    const email = document.getElementById('exampleInputEmail').value;
    const password = document.getElementById('exampleInputPassword').value;
    const repeatPassword = document.getElementById('exampleRepeatPassword').value;

    // Validate required fields
    if (!nombre || !apellido || !email || !password) {
        alert('Por favor, complete todos los campos obligatorios');
        return;
    }

    // Validate password confirmation
    if (password !== repeatPassword) {
        alert('Las contraseñas no coinciden');
        return;
    }

    // Create user object (telefono is optional and not in the form)
    const usuario = {
        nombre: nombre,
        apellido: apellido,
        email: email,
        telefono: null, // Not in the current form
        password: password
    };

    try {
        const response = await fetch('api/usuarios', {
            method: 'POST',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(usuario)
        });

        if (response.ok) {
            alert('Usuario registrado correctamente');
            // Clear form fields
            document.getElementById('exampleFirstName').value = '';
            document.getElementById('exampleLastName').value = '';
            document.getElementById('exampleInputEmail').value = '';
            document.getElementById('exampleInputPassword').value = '';
            document.getElementById('exampleRepeatPassword').value = '';
        } else {
            alert('Error al registrar el usuario');
        }
    } catch (error) {
        console.error('Error:', error);
        alert('Error al registrar el usuario');
    }
}
