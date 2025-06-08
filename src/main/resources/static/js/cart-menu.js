// Al cargar la página, obtenemos todos los botones “Añadir al carrito”
// (supongo que en el listado de productos tienes botones con clase .add-to-cart).
document.addEventListener('DOMContentLoaded', () => {
    // 1) Seleccionamos todos los botones “Añadir al carrito”
    const addButtons = document.querySelectorAll('.add-to-cart, .add-to-cart-btn');

    addButtons.forEach(btn => {
        btn.addEventListener('click', async (e) => {
            e.preventDefault();
            // Si no hay sesión, redirigimos a login
            if (!isUserLoggedIn) {
                window.location.href = '/login';
                return;
            }

            // 2) Si hay sesión, obtenemos el ID del producto y lo guardamos en localStorage o hacemos
            //    una llamada AJAX a un endpoint “/cart/add” para persistir en servidor/FireStore
            const productId = btn.getAttribute('data-id');
            await addToCart({id: productId, quantity: 1});
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
async function fetchServerCart() {
    try {
        const res = await fetch('/api/cart');
        if (!res.ok) throw new Error('network');
        return await res.json();
    } catch (e) {
        return [];
    }
}
// Función ejemplo para añadir un producto al carrito (usa localStorage para persistir temporalmente)
const token = document.querySelector('meta[name="_csrf"]').getAttribute('content');
const header = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');
async function addToCart(item) {
    const cart = getCart();
    // Si el item ya existía, aumentamos cantidad; si no, lo agregamos con qty=1
    const existing = cart.find(p => p.id === item.id);
    if (existing) {
        existing.quantity += item.quantity;
    } else {
        cart.push({id: item.id, quantity: item.quantity });
    }
    saveCart(cart);
    try {
        await fetch('/api/cart/add', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json',
            [header]: token},
            body: JSON.stringify({ productId: item.id, cantidad: item.quantity })
        });
    } catch (e) {
        console.error('Error enviando al servidor', e);
    }
}
/**
 * Guarda el carrito en localStorage
 * @param {Array<{id:string, quantity:number}>} cartList
 */
function saveCart(cartList) {
    localStorage.setItem('cart', JSON.stringify(cartList));
}

async function updateCartBadge() {
    const badge = document.getElementById('cart-badge');
    if (!badge) return;
    const cart = await fetchServerCart();
    const totalQty = cart.reduce((sum, p) => sum + p.quantity, 0)
    badge.textContent = totalQty;
}

/**
 * Renderiza el carrito dentro de #cart-items-container leyendo localStorage.
 * Para cada ítem, hace fetch a `/products/{id}` para obtener nombre y precio.
 */
async function renderCart() {
    const container = document.getElementById('cart-items-container');
    const badge = document.getElementById('cart-badge');
    if (!container || !badge) return;

    container.innerHTML = ''; // limpiamos contenido previo
    const cart = await fetchServerCart();

    if (cart.length === 0) {
        container.innerHTML = '<p class="empty-cart-msg">Tu carrito está vacío.</p>';
        badge.textContent = '0';
        return;
    }

    let totalQuantity = 0;

    for (const item of cart) {
        totalQuantity += item.quantity;
        const prod = await fetch(`/api/products/${item.productId}`).then(r => r.ok ? r.json() : {});
        const image = prod.image || `https://via.placeholder.com/80x80?text=${encodeURIComponent(item.itemName)}`;
        const div = document.createElement('div');
        div.className = 'cart-item-card';
        div.innerHTML = `
          <img src="${image}" alt="${item.itemName}" class="cart-item-img">
          <div class="cart-item-info">
            <span class="cart-item-name">${item.itemName}</span>
            <span class="cart-item-price">$${item.unitPrice.toFixed(2)}</span>
            <div class="qty-controls" data-id="${item.productId}">
              <button class="qty-minus">-</button>
              <span class="qty-value">${item.quantity}</span>
              <button class="qty-plus">+</button>
            </div>
          </div>`;
        container.appendChild(div);
    }

    container.querySelectorAll('.qty-plus').forEach(btn => {
        btn.addEventListener('click', async () => {
            const wrapper = btn.closest('.qty-controls');
            const id = wrapper.dataset.id;
            const valueSpan = wrapper.querySelector('.qty-value');
            const newQty = parseInt(valueSpan.textContent) + 1;
            await fetch('/api/cart/update', {
                method: 'POST',
                headers: {'Content-Type': 'application/json',
                [header]: token},
                body: JSON.stringify({productId: id, cantidad: newQty})
            });
            renderCart();
            actualizarBadge();
        });
    });

    container.querySelectorAll('.qty-minus').forEach(btn => {
        btn.addEventListener('click', async () => {
            const wrapper = btn.closest('.qty-controls');
            const id = wrapper.dataset.id;
            const valueSpan = wrapper.querySelector('.qty-value');
            const current = parseInt(valueSpan.textContent);
            const newQty = current - 1;
            await fetch('/api/cart/update', {
                method: 'POST',
                headers: {'Content-Type': 'application/json'},
                body: JSON.stringify({productId: id, cantidad: newQty})
            });
            renderCart();
            actualizarBadge();
        });
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
    updateCartBadge()
}

// Al cargar la página, invocamos actualizarBadge una sola vez:
document.addEventListener('DOMContentLoaded', actualizarBadge);
