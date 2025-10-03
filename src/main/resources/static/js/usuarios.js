// Call the dataTables jQuery plugin
$(document).ready(function() {
    cargarUsuarios();
    $('#usuarios').DataTable();
});

function getHeaders() {
    const headers = {
        'Accept': 'application/json',
        'Content-Type': 'application/json'
    };

    // Add authorization header if token exists
    const token = localStorage.getItem('token');
    console.log('Retrieved token from localStorage:', token); // Debug log

    if (token) {
        headers.Authorization = 'Bearer ' + token;
        console.log('Authorization header set:', headers.Authorization.substring(0, 30) + '...'); // Debug log
    } else {
        console.log('No token found in localStorage'); // Debug log
    }

    return headers;
}

async function cargarUsuarios() {
    const req = await fetch('api/usuarios', {
        method: 'GET',
        headers: getHeaders()
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
            headers: getHeaders()
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
