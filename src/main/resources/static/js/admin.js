// static/js/admin.js

// Cada 10 segundos refrescamos la tabla de productos en el dashboard
const REFRESH_INTERVAL_MS = 10_000;

document.addEventListener('DOMContentLoaded', () => {
    // Primera carga inmediata
    fetchYRenderProductos();

    // Cada x segundos, refresca
    setInterval(fetchYRenderProductos, REFRESH_INTERVAL_MS);

    // Si el admin cambia filtros (formulario de búsqueda/categorías), podemos volver a llamar:
    const filtroForm = document.getElementById('filtro-form');
    if (filtroForm) {
        filtroForm.addEventListener('submit', (e) => {
        });
    }
});

/**
 * Construye la URL de la API para el dashboard admin.
 * Asume que tienes:
 *   <input id="input-query" name="query">
 *   <select id="select-category" name="category">
 */
function buildApiUrlAdmin() {
    const base = '/admin/api/products';
    const queryValue = document.getElementById('input-query')?.value.trim() || '';
    const categoryValue = document.getElementById('select-category')?.value.trim() || '';
    const params = [];
    if (queryValue !== '') {
        params.push('query=' + encodeURIComponent(queryValue));
    }
    if (categoryValue !== '') {
        params.push('category=' + encodeURIComponent(categoryValue));
    }
    return params.length ? `${base}?${params.join('&')}` : base;
}

/**
 * Llama al endpoint /admin/api/products y repinta
 * el <tbody id="productos-body"> de admin/dashboard.html
 */
function fetchYRenderProductos() {
    const url = buildApiUrlAdmin();
    fetch(url, {
        method: 'GET',
        headers: { 'Accept': 'application/json' }
    })
        .then(res => {
            if (!res.ok) {
                console.error('Error al obtener productos admin:', res.status, res.statusText);
                return [];
            }
            return res.json();
        })
        .then(productos => {
            actualizarTablaAdmin(productos);
        })
        .catch(err => {
            console.error('Fetch error admin:', err);
        });
}

/**
 * Vacía y rellena el <tbody id="productos-body"> en
 * admin/dashboard.html con los datos que pasemos.
 */
function actualizarTablaAdmin(productos) {
    const tbody = document.getElementById('productos-body');
    if (!tbody) return;

    tbody.innerHTML = '';
    if (!Array.isArray(productos) || productos.length === 0) {
        const tr = document.createElement('tr');
        const td = document.createElement('td');
        td.colSpan = 7;
        td.textContent = 'No se han encontrado productos.';
        td.style.textAlign = 'center';
        tr.appendChild(td);
        tbody.appendChild(tr);
        return;
    }

    productos.forEach(prod => {
        const tr = document.createElement('tr');
        // ID
        const tdId = document.createElement('td');
        tdId.textContent = prod.id || '';
        tr.appendChild(tdId);
        // Categoría
        const tdCat = document.createElement('td');
        tdCat.textContent = prod.categoria || '';
        tr.appendChild(tdCat);
        // Nombre
        const tdNombre = document.createElement('td');
        tdNombre.textContent = prod.nombre || '';
        tr.appendChild(tdNombre);
        // Descripción
        const tdDesc = document.createElement('td');
        tdDesc.textContent = prod.descripcion || '';
        tr.appendChild(tdDesc);
        // Precio
        const tdPrecio = document.createElement('td');
        const precio = parseFloat(prod.precio);
        tdPrecio.textContent = isNaN(precio) ? '$0.00' : ('$' + precio.toFixed(2));
        tr.appendChild(tdPrecio);
        // Stock
        const tdStock = document.createElement('td');
        const stock = parseInt(prod.stock, 10);
        tdStock.textContent = isNaN(stock) ? '0' : stock;
        tr.appendChild(tdStock);
        // Acciones (enlaces a editar)
        const tdAcc = document.createElement('td');
        const aEditar = document.createElement('a');
        aEditar.href = `/admin/edit/${encodeURIComponent(prod.id)}`;
        aEditar.textContent = 'Editar';
        aEditar.className = 'btn btn-edit';
        tdAcc.appendChild(aEditar);
        tr.appendChild(tdAcc);

        tbody.appendChild(tr);
    });
}
