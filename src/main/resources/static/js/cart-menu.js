// Al cargar la página, obtenemos todos los botones “Añadir al carrito”
// (supongo que en el listado de productos tienes botones con clase .add-to-cart).
document.addEventListener('DOMContentLoaded', () => {
    // 1) Seleccionamos todos los botones “Añadir al carrito”
    const addButtons = document.querySelectorAll('.add-to-cart, .add-to-cart-btn');

    addButtons.forEach(btn => {
        btn.addEventListener('click', (e) => {
            e.preventDefault();
            // Si no hay sesión, redirigimos a login
            if (!isUserLoggedIn) {
                window.location.href = '/login';
                return;
            }

            // 2) Si hay sesión, obtenemos el ID del producto y lo guardamos en localStorage o hacemos
            //    una llamada AJAX a un endpoint “/cart/add” para persistir en servidor/FireStore
            const productId = btn.getAttribute('data-id');
            addToCart({id: productId, quantity: 1});
            actualizarBadge();
        });
    });

    // 3) También controlamos el propio icono del carrito (en el header) para abrir el panel
    const cartBtn = document.querySelector('.cart-btn');
    const cartPanel = document.querySelector('.cart-panel');
    const closeCartBtn = document.querySelector('.cart-close-btn');

    if (cartBtn && cartPanel) {
        cartBtn.addEventListener('click', () => {
            if (!isUserLoggedIn) {
                window.location.href = '/login';
                return;
            }
            cartPanel.classList.add('open');
            renderCart(); // función que, si hay sesión, lee los ítems y los pinta
        });
    }

    if (closeCartBtn && cartPanel) {
        closeCartBtn.addEventListener('click', () => {
            cartPanel.classList.remove('open');
        });
    }
});
/**
 * Lee el carrito actual de localStorage
 * @returns Array<{ id: string, quantity: number }>
 */
function getCart() {
    try {
        return JSON.parse(localStorage.getItem('cart') || '[]');
    } catch (e) {
        return [];
    }
}

// Función ejemplo para añadir un producto al carrito (usa localStorage para persistir temporalmente)
function addToCart(item) {
    const cart = getCart();
    // Si el item ya existía, aumentamos cantidad; si no, lo agregamos con qty=1
    const existing = cart.find(p => p.id === item.id);
    if (existing) {
        existing.quantity += item.quantity;
    } else {
        cart.push({id: item.id,quantity: item.quantity });
    }
    saveCart(cart);
}
/**
 * Guarda el carrito en localStorage
 * @param {Array<{id:string, quantity:number}>} cartList
 */
function saveCart(cartList) {
    localStorage.setItem('cart', JSON.stringify(cartList));
}

function updateCartBadge() {
    const badge = document.getElementById('cart-badge');
    if (!badge) return;
    const cart = getCart();
    const totalQty = cart.reduce((sum, p) => sum + p.quantity, 0);
    badge.textContent = totalQty;
}

/**
 * Renderiza el carrito dentro de #cart-items-container leyendo localStorage.
 * Para cada ítem, hace fetch a `/products/{id}` para obtener nombre y precio.
 */
function renderCart() {
    const container = document.getElementById('cart-items-container');
    const badge = document.getElementById('cart-badge');
    if (!container || !badge) return;

    container.innerHTML = ''; // limpiamos contenido previo
    const cart = getCart();

    if (cart.length === 0) {
        container.innerHTML = '<p class="empty-cart-msg">Tu carrito está vacío.</p>';
        badge.textContent = '0';
        return;
    }

    let totalQuantity = 0;

    cart.forEach(async item => {
        totalQuantity += item.quantity;
        try {
            // Se asume que existe un endpoint que devuelva JSON para /products/{id}
            const res = await fetch(`/products/${item.id}`, {
                headers: { 'Accept': 'application/json' }
            });
            if (!res.ok) throw new Error('No encontrado');
            const prod = await res.json();

            // Creamos el bloque HTML para este ítem
            const div = document.createElement('div');
            div.className = 'cart-item';
            div.innerHTML = `
              <span class="cart-item-name">${prod.nombre}</span>
              <span class="cart-item-qty">x${item.quantity}</span>
              <span class="cart-item-price">$${(prod.precio * item.quantity).toFixed(2)}</span>
            `;
            container.appendChild(div);
        } catch (err) {
            console.error('Error cargando producto', err);
        }
    });

    badge.textContent = totalQuantity.toString();

    // Añadimos el footer con el botón “Comprar”
    // (Primero eliminamos si existía uno previo.)
    const prevFooter = document.querySelector('.cart-panel-footer');
    if (prevFooter) prevFooter.remove();

    const footerDiv = document.createElement('div');
    footerDiv.className = 'cart-panel-footer';
    footerDiv.innerHTML = `
      <button id="checkout-btn" class="btn btn-primary w-100">Comprar</button>
    `;
    container.parentElement.appendChild(footerDiv);

    // Evento para el botón “Comprar”
    document.getElementById('checkout-btn').addEventListener('click', () => {
        window.location.href = '/checkout';
    });
}

// Cada vez que cargue la página o modifiquemos el carrito, actualizamos la badge:
function actualizarBadge() {
    const badge = document.getElementById('cart-badge');
    let cart = JSON.parse(localStorage.getItem('cart') || '[]');
    const total = cart.reduce((sum, item) => sum + item.quantity, 0);
    badge.textContent = total.toString();
}

// Al cargar la página, invocamos actualizarBadge una sola vez:
document.addEventListener('DOMContentLoaded', actualizarBadge);
