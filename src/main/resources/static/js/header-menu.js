document.addEventListener('DOMContentLoaded', () => {
    const btnHamburger = document.querySelector('.hamburger-btn');
    const sideMenu = document.querySelector('.side-menu');

    if (!btnHamburger || !sideMenu) return;

    btnHamburger.addEventListener('click', (e) => {
        e.stopPropagation();
        sideMenu.classList.toggle('open');
    });

    document.addEventListener('click', (e) => {
        if (!sideMenu.contains(e.target) && !btnHamburger.contains(e.target)) {
            sideMenu.classList.remove('open');
        }
    });

    sideMenu.addEventListener('click', (e) => {
        e.stopPropagation();
    });
});
