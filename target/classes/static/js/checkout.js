document.addEventListener('DOMContentLoaded', () => {
    const payBtn = document.getElementById('pay-btn');
    if (!payBtn) return;

    const stripePublicKey = document.body.getAttribute('data-stripe-public-key') || '';
    if (!stripePublicKey) {
        console.error('Stripe public key not found on page');
        return;
    }

    const stripe = Stripe(stripePublicKey);
    const csrfToken = document.querySelector('meta[name="_csrf"]')?.getAttribute('content');
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.getAttribute('content');
    payBtn.addEventListener('click', async () => {
        try {
            const headers = {};
            if (csrfToken && csrfHeader) {
                headers[csrfHeader] = csrfToken;
            }
            const res = await fetch('/checkout/create-session', { method: 'POST', headers });
            const data = await res.json();
            if (!data.sessionId) {
                console.error('No se recibió sessionId');
                return;
            }
            stripe.redirectToCheckout({ sessionId: data.sessionId });
        } catch (err) {
            console.error('Error iniciando pago:', err);
        }
    });
});