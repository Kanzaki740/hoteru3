const stripe = Stripe('pk_test_51R9I54QcZhzKgcVM8NT0Ad57dSCkYpeQchvJausPz50EhN96ynrtuFmSDF3MrcKdxSwFdDXjgUxAu0rnQjFZXtPE00iQsCGDBZ');
const paymentButton = document.querySelector('#paymentButton');
paymentButton.addEventListener('click', () => {
  stripe.redirectToCheckout({
    sessionId: sessionId
  })
});
