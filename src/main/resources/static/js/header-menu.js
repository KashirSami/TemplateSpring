// src/main/resources/static/js/header-menu.js

document.addEventListener('DOMContentLoaded', () => {
    const btnHamburger = document.querySelector('.hamburger-btn');
    const sideMenu = document.querySelector('.side-menu');

    if (!btnHamburger || !sideMenu) return;

    // Al hacer clic en el botón hamburguesa, alternamos la clase .open
    btnHamburger.addEventListener('click', (e) => {
        e.stopPropagation();
        sideMenu.classList.toggle('open');
    });

    // Si el usuario hace clic fuera del menú, lo cerramos
    document.addEventListener('click', (e) => {
        if (!sideMenu.contains(e.target) && !btnHamburger.contains(e.target)) {
            sideMenu.classList.remove('open');
        }
    });

    // Prevenir el cierre automático si clicamos dentro de la propia lista
    sideMenu.addEventListener('click', (e) => {
        e.stopPropagation();
    });
});
