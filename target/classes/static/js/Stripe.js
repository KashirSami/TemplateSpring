document.addEventListener('DOMContentLoaded', () => {
    // 1) Obtenemos la clave pública y el customerId a través de atributos
    //    que Thymeleaf habrá inyectado como data-attributes en el <body> o en un elemento.
    //    Por ejemplo, en el controlador podrías hacer:
    //       model.addAttribute("stripePublicKey", "pk_test_XXXX");
    //       model.addAttribute("stripeCustomerId", "cus_ABCDEF");
    //
    //    Y luego, en tu plantilla, ponemos en <body>:
    //       <body
    //         data-stripe-public-key="${stripePublicKey}"
    //         data-stripe-customer-id="${stripeCustomerId}">
    //
    //    Aquí los leemos directamente:
    const stripePublicKey = document.body.getAttribute('data-stripe-public-key') || '';
    const customerId = document.body.getAttribute('data-stripe-customer-id') || '';

    if (!stripePublicKey || !customerId) {
        console.error('Faltan stripePublicKey o customerId en el template.');
        return;
    }

    // 2) Inicializamos Stripe
    const stripe = Stripe(stripePublicKey);

    // 3) Creamos un SetupIntent en el backend para este customer
    fetch(`/stripe/create-setup-intent?customerId=${encodeURIComponent(customerId)}`)
        .then(res => res.json())
        .then(data => {
            const clientSecret = data.clientSecret;
            const appearance = { theme: 'stripe' };
            const elements = stripe.elements({ appearance, clientSecret });

            const paymentElement = elements.create('payment');
            paymentElement.mount('#payment-element');

            // 4) Al enviar el formulario, confirmamos el SetupIntent
            const form = document.getElementById('payment-form');
            form.addEventListener('submit', async (e) => {
                e.preventDefault();

                const { error } = await stripe.confirmSetup({
                    elements,
                    confirmParams: {
                        // Después de guardar la tarjeta, redirigir a /profile con parámetro paymentSuccess
                        return_url: window.location.origin + '/profile?paymentSuccess=true'
                    },
                });

                if (error) {
                    document.getElementById('payment-message').textContent = error.message;
                }
            });
        })
        .catch(err => {
            console.error('Error al crear el SetupIntent:', err);
            document.getElementById('payment-message').textContent = 'Error al inicializar el pago.';
        });
});