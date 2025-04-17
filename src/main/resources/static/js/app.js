// Datos de productos demo
const products = [
    {
        id: 1,
        name: "Smartphone X1",
        price: 599.99,
        image: "https://via.placeholder.com/300x300?text=Smartphone+X1",
        description: "El último smartphone con cámara de 48MP y pantalla AMOLED.",
        category: "Electrónicos"
    },
    {
        id: 2,
        name: "Laptop Pro",
        price: 899.99,
        image: "https://via.placeholder.com/300x300?text=Laptop+Pro",
        description: "Laptop potente con procesador i7 y 16GB de RAM.",
        category: "Electrónicos"
    },
    {
        id: 3,
        name: "Zapatos Running",
        price: 79.99,
        image: "https://via.placeholder.com/300x300?text=Zapatos+Running",
        description: "Zapatos cómodos para correr con amortiguación de aire.",
        category: "Deportes"
    },
    {
        id: 4,
        name: "Cámara DSLR",
        price: 499.99,
        image: "https://via.placeholder.com/300x300?text=Cámara+DSLR",
        description: "Cámara profesional con lente intercambiable 24-70mm.",
        category: "Electrónicos"
    },
    {
        id: 5,
        name: "Reloj Inteligente",
        price: 199.99,
        image: "https://via.placeholder.com/300x300?text=Reloj+Inteligente",
        description: "Monitoriza tu actividad física y notificaciones.",
        category: "Electrónicos"
    },
    {
        id: 6,
        name: "Mochila Viaje",
        price: 49.99,
        image: "https://via.placeholder.com/300x300?text=Mochila+Viaje",
        description: "Mochila resistente con compartimento para laptop.",
        category: "Accesorios"
    }
];

document.addEventListener('DOMContentLoaded', function() {
    setupSearch();
    setupLoginButton();

    if (document.getElementById('featured-products')) {
        loadFeaturedProducts();
    }
});


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
        // Guardar la búsqueda en localStorage para usarla en la página de productos
        localStorage.setItem('searchQuery', query);
        window.location.href = 'products.html';
    }
}

function setupLoginButton() {
    const loginBtn = document.getElementById('login-btn');

    loginBtn.addEventListener('click', () => {
        // Aquí iría la lógica para mostrar el modal de login
        alert('Funcionalidad de login/registro. Se puede implementar un modal aquí.');
    });
}

function loadFeaturedProducts() {
    const featuredContainer = document.getElementById('featured-products');
    const featuredProducts = products.slice(0, 4); // Tomamos los primeros 4 como destacados

    featuredProducts.forEach(product => {
        const productCard = createProductCard(product);
        featuredContainer.appendChild(productCard);
    });
}

function createProductCard(product) {
    const card = document.createElement('div');
    card.className = 'product-card';

    card.innerHTML = `
        <div class="product-image">
            <img src="${product.image}" alt="${product.name}">
        </div>
        <div class="product-info">
            <h3>${product.name}</h3>
            <p class="product-description">${product.description}</p>
            <p class="product-price">$${product.price.toFixed(2)}</p>
            <button class="add-to-cart" data-id="${product.id}">Añadir al carrito</button>
        </div>
    `;

    return card;
}