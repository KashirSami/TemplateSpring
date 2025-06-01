// static/js/app.js

document.addEventListener('DOMContentLoaded', () => {
    setupSearch();
    setupLoginButton();

    // Si estamos en la página “productos.html”:
    if (window.location.pathname.endsWith('products.html')) {
        loadProductosDesdeBackend();
    }

    // Si tienes un contenedor de “destacados”:
    if (document.getElementById('featured-products')) {
        loadFeaturedProducts();
    }
});


/**
 * 1) EJEMPLO: Cargar todos los productos desde el backend (/productos) y luego filtrarlos.
 */
function loadProductosDesdeBackend() {
    // Podemos leer la query que guardamos en localStorage (desde la búsqueda del index):
    const q = localStorage.getItem('searchQuery') || '';
    // Luego borramos para que no interfiera la siguiente vez:
    localStorage.removeItem('searchQuery');

    // Si hay alguna búsqueda, pedimos al backend /productos/filtro?query=...
    let url = '/products';
    if (q.trim() !== '') {
        url = `/products/filtro?query=${encodeURIComponent(q)}`;
    }

    fetch(url, { method: 'GET', headers: { 'Accept': 'application/json' }})
        .then(res => {
            if (!res.ok) {
                console.error('Error cargando productos:', res.status, res.statusText);
                return [];
            }
            return res.json();
        })
        .then(productos => {
            // Ahora sí, para cada producto, creamos una card
            renderListadoDeProductos(productos);
        })
        .catch(err => {
            console.error('Fetch error en /products:', err);
        });
}


function renderListadoDeProductos(productos) {
    const contenedor = document.getElementById('featured-products') || document.getElementById('products-list');
    if (!contenedor) return;

    contenedor.innerHTML = ''; // vaciamos primero

    if (!Array.isArray(productos) || productos.length === 0) {
        const mensaje = document.createElement('p');
        mensaje.textContent = 'No hay productos disponibles.';
        contenedor.appendChild(mensaje);
        return;
    }

    productos.forEach(prod => {
        const card = createProductCard(prod);
        contenedor.appendChild(card);
    });
}


/**
 * 3) Mantenemos tu createProductCard, adaptándola si cambian los nombres de campos
 */
function createProductCard(product) {
    // Asumo que tu objeto Java “Product” en JSON tiene:
    //   { id, nombre, descripcion, precio, category (o categoria), image (si lo guardas), stock }
    const card = document.createElement('div');
    card.className = 'product-card';

    // Si no tienes “image” en Firestore, quita esa parte o asigna un placeholder
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


/**
 * ------ Resto de tu código de búsqueda dentro del mismo app.js -------
 * (setupSearch, performSearch, setupLoginButton, etc.)
 */
function setActiveLink() {
    const currentPage = window.location.pathname.split('/').pop() || 'index.html';
    const navLinks = document.querySelectorAll('.nav-links a');

    navLinks.forEach(link => {
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
    searchInput.addEventListener('keypress', (e) => {
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

function loadFeaturedProducts() {
    // Podemos reutilizar la misma lógica: siempre que haya un contenedor #featured-products,
    // llamamos a loadProductosDesdeBackend y pintamos solo los primeros 4.
    fetch('/productos?limit=4', { method: 'GET', headers: { 'Accept': 'application/json' } })
        .then(res => res.json())
        .then(productos => {
            const featured = productos.slice(0, 4);
            const featuredContainer = document.getElementById('featured-products');
            featuredContainer.innerHTML = '';
            featured.forEach(p => {
                const card = createProductCard(p);
                featuredContainer.appendChild(card);
            });
        })
        .catch(err => console.error('Error cargando destacados:', err));
}
