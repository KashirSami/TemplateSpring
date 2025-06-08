
document.addEventListener('DOMContentLoaded', () => {
    // 1) Leemos desde los atributos data- de <body>
    const stripePublicKey = document.body.getAttribute('data-stripe-public-key') || '';
    const customerId = document.body.getAttribute('data-stripe-customer-id') || '';

    if (!stripePublicKey || !customerId) {
        console.error('Faltan stripePublicKey o customerId en el template.');
        return;
    }

    const stripe = Stripe(stripePublicKey);

    fetch(`/stripe/create-setup-intent?customerId=${encodeURIComponent(customerId)}`)
        .then(res => res.json())
        .then(data => {
            if (data.error || !data.clientSecret) {
                throw new Error(data.error || 'No se recibió clientSecret');
            }

            const clientSecret = data.clientSecret;
            const appearance = { theme: 'stripe' };
            const elements = stripe.elements({ appearance, clientSecret });

            const paymentElement = elements.create('payment');
            paymentElement.mount('#payment-element');

            const form = document.getElementById('payment-form');
            form.addEventListener('submit', async (e) => {
                e.preventDefault();

                const { error } = await stripe.confirmSetup({
                    elements,
                    confirmParams: {
                        return_url: window.location.origin + '/profile?paymentSuccess=true'
                    },
                });

                if (error) {
                    document.getElementById('payment-message').textContent = error.message;
                }
            });
        })
        .catch(err => {
            console.error('Error al crear SetupIntent:', err);
            document.getElementById('payment-message').textContent = 'Error al inicializar el pago.';
        });
});
