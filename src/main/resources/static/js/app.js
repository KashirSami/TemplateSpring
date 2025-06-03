// static/js/app.js

document.addEventListener('DOMContentLoaded', () => {
    setupSearch();
    setupLoginButton();

    // Si estamos en la página “products.html”, cargamos TODOS los productos
    if (window.location.pathname === '/products') {
        loadProductosDesdeBackend();
    }

    // Si hay un contenedor “featured-products” en cualquier página,
    // cargamos solo los primeros 4 productos
    if (document.getElementById('featured-products')) {
        loadFeaturedProducts();
    }

    // Marca como “activo” el enlace del menú
    setActiveLink();
});


/**
 * Carga TODOS los productos desde /products y los pinta en #products-list
 */
function loadProductosDesdeBackend() {
    // Si alguna vez guardaste algo en localStorage para buscar, lo podrías adaptar aquí.
    // Pero si no vas a filtrar, NO necesitas leer ni borrar nada de localStorage.
    fetch('/api/products', {
        method: 'GET',
        headers: { 'Accept': 'application/json' }
    })
        .then(res => {
            if (!res.ok) {
                console.error('Error cargando products:', res.status, res.statusText);
                return [];
            }
            return res.json();
        })
        .then(productos => {
            // OJO: aquí debes pasar "productos", no "products"
            renderListadoDeProductos(productos);
        })
        .catch(err => {
            console.error('Fetch error en /products:', err);
        });
}


function renderListadoDeProductos(products) {
    const contenedor = document.getElementById('products-list');
    if (!contenedor) return;

    contenedor.innerHTML = ''; // vaciamos primero

    if (!Array.isArray(products) || products.length === 0) {
        const mensaje = document.createElement('p');
        mensaje.textContent = 'No hay productos disponibles.';
        contenedor.appendChild(mensaje);
        return;
    }

    products.forEach(prod => {
        const card = createProductCard(prod);
        contenedor.appendChild(card);
    });
}


function createProductCard(product) {
    // Ajusta estos campos según tu JSON real
    const card = document.createElement('div');
    card.className = 'product-card';

    const imageUrl = product.image || 'https://via.placeholder.com/300x300?text=Sin+imagen';

    card.innerHTML = `
      <div class="product-image">
        <img src="${imageUrl}" alt="${product.nombre}">
      </div>
      <div class="product-info">
        <h3>${product.nombre}</h3>
        <p class="product-description">${product.descripcion}</p>
        <p class="product-price">$${(product.precio || 0).toFixed(2)}</p>
        <button class="add-to-cart" data-id="${product.id}">Añadir al carrito</button>
      </div>
    `;
    return card;
}


function loadFeaturedProducts() {
    // Solo cogemos los primeros 4 productos de /products?limit=4
    fetch('/api/products?limit=4', {
        method: 'GET',
        headers: { 'Accept': 'application/json' }
    })
        .then(res => {
            if (!res.ok) {
                console.error('Error cargando destacados:', res.status, res.statusText);
                return [];
            }
            return res.json();
        })
        .then(productos => {
            const featuredContainer = document.getElementById('featured-products');
            if (!featuredContainer) return;
            featuredContainer.innerHTML = '';

            productos.slice(0, 4).forEach(p => {
                const card = createProductCard(p);
                featuredContainer.appendChild(card);
            });
        })
        .catch(err => console.error('Fetch error en /products?limit=4:', err));
}


function setActiveLink() {
    const currentPage = window.location.pathname.split('/').pop() || 'index.html';
    document.querySelectorAll('.nav-links a').forEach(link => {
        const linkPage = link.getAttribute('href').split('/').pop();
        if (currentPage === linkPage) {
            link.classList.add('active');
        }
    });
}


function setupSearch() {
    const searchInput = document.querySelector('.search-bar input');
    const searchIcon = document.querySelector('.search-bar i');
    if (!searchInput || !searchIcon) return;

    searchIcon.addEventListener('click', () => {
        performSearch(searchInput.value);
    });
    searchInput.addEventListener('keypress', e => {
        if (e.key === 'Enter') {
            performSearch(searchInput.value);
        }
    });
}

function performSearch(query) {
    if (query.trim() !== '') {
        localStorage.setItem('searchQuery', query);
        window.location.href = 'products.html';
    }
}

function setupLoginButton() {
    const loginBtn = document.getElementById('login-btn');
    if (!loginBtn) return;
    loginBtn.addEventListener('click', () => {
        alert('Funcionalidad de login/registro. Se puede implementar un modal aquí.');
    });
}

document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('registerForm');
    if (!form) return;

    form.addEventListener('submit', e => {
        let valido = true;
        const email = document.querySelector('input[name="email"]').value.trim();
        const confirmEmail = document.querySelector('input[name="confirmEmail"]').value.trim();
        const pass = document.querySelector('input[name="password"]').value;
        const confirmPass = document.querySelector('input[name="confirmPassword"]').value;

        // Limpiar mensajes anteriores:
        document.getElementById('emailError1').textContent = '';
        document.getElementById('emailError2').textContent = '';
        document.getElementById('passwordError').textContent = '';

        if (email !== confirmEmail) {
            document.getElementById('emailError2').textContent = 'Los correos no coinciden';
            valido = false;
        }
        if (pass !== confirmPass) {
            document.getElementById('passwordError').textContent = 'Las contraseñas no coinciden';
            valido = false;
        }

        if (!valido) {
            e.preventDefault();
        }
    });
});
