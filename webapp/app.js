// STATE MANAGEMENT
let products = [];
let cart = {};
let currentCategory = 'all';
let currentType = 'all';

// DOM ELEMENTS
const loginContainer = document.getElementById('login-container');
const loginForm = document.getElementById('login-form');
const usernameInput = document.getElementById('user-name');
const passwordInput = document.getElementById('password');
const errorMessageContainer = document.getElementById('error-message-container');
const errorText = document.getElementById('error-text');

const storeContainer = document.getElementById('store-container');
const productGrid = document.getElementById('product-grid');
const cartBadge = document.getElementById('cart-badge');
const cartDrawer = document.getElementById('cart-drawer');
const cartItemsList = document.getElementById('cart-items-list');
const cartTotalPrice = document.getElementById('cart-total-price');

const sidebar = document.getElementById('sidebar');
const burgerMenuBtn = document.getElementById('react-burger-menu-btn');
const closeSidebarBtn = document.getElementById('close-sidebar-btn');
const logoutLink = document.getElementById('logout_sidebar_link');

const checkoutOverlay = document.getElementById('checkout-overlay');
const checkoutStepOne = document.getElementById('checkout-step-one');
const checkoutStepTwo = document.getElementById('checkout-step-two');
const checkoutComplete = document.getElementById('checkout-complete');
const checkoutForm = document.getElementById('checkout-form');
const checkoutButton = document.getElementById('checkout');

const firstNameInput = document.getElementById('first-name');
const lastNameInput = document.getElementById('last-name');
const postalCodeInput = document.getElementById('postal-code');

const overviewItemsPrice = document.getElementById('overview-items-price');
const overviewGrandTotal = document.getElementById('overview-grand-total');

// INITIALIZE
function init() {
    setupEventListeners();
}

// FETCH PRODUCTS FROM DATABASE API
async function loadProducts() {
    try {
        const res = await fetch('/api/products');
        if (res.ok) {
            products = await res.json();
            renderProducts();
        } else {
            console.error('Failed to load products from server database.');
        }
    } catch (e) {
        console.error('Network error loading products:', e);
    }
}

// AUTHENTICATION VIA DATABASE API
loginForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    const username = usernameInput.value.trim();
    const password = passwordInput.value;

    try {
        const res = await fetch('/api/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, password })
        });
        
        const data = await res.json();
        
        if (res.ok && data.success) {
            errorMessageContainer.classList.add('hidden');
            loginContainer.classList.add('hidden');
            storeContainer.classList.remove('hidden');
            
            // Check for problem_user visual glitch flag
            if (data.user.glitch) {
                document.body.classList.add('glitched-ui');
            } else {
                document.body.classList.remove('glitched-ui');
            }
            
            // Load and render catalog from database
            await loadProducts();
        } else {
            errorText.innerText = data.error || "Login failed.";
            errorMessageContainer.classList.remove('hidden');
        }
    } catch (err) {
        errorText.innerText = "Fatal: Unable to contact the database auth server.";
        errorMessageContainer.classList.remove('hidden');
    }
});

// RENDER PRODUCTS
function renderProducts() {
    productGrid.innerHTML = '';
    
    // Apply Category and Type Filters
    let filteredProducts = products;
    
    if (currentCategory !== 'all') {
        filteredProducts = filteredProducts.filter(p => p.category === currentCategory);
    }
    
    if (currentType !== 'all') {
        filteredProducts = filteredProducts.filter(p => p.type === currentType);
    }

    if (filteredProducts.length === 0) {
        productGrid.innerHTML = `<p class="empty-cart-msg">No products found matching these filters.</p>`;
        return;
    }

    filteredProducts.forEach(product => {
        const isInCart = cart[product.id] !== undefined;
        const buttonText = isInCart ? "Remove" : "Add to Cart";
        const buttonClass = isInCart ? "btn-add-cart added" : "btn-add-cart";
        const cleanId = `add-to-cart-${product.id}`;

        const card = document.createElement('div');
        card.className = 'product-card';
        card.style.setProperty('--theme-color', product.themeColor);
        card.style.setProperty('--theme-shadow', product.themeShadow);

        card.innerHTML = `
            <span class="product-badge ${product.category}">${product.category}</span>
            <div class="product-image-container">
                ${product.svg}
            </div>
            <div class="product-info">
                <h3>${product.name}</h3>
                <p class="product-desc">${product.description}</p>
            </div>
            <div class="product-footer">
                <span class="product-price">$${product.price.toFixed(2)}</span>
                <button class="${buttonClass}" id="${cleanId}">${buttonText}</button>
            </div>
        `;

        productGrid.appendChild(card);

        // Bind Add/Remove click handler
        document.getElementById(cleanId).addEventListener('click', () => {
            toggleCartItem(product);
        });
    });
}

// CART MANAGEMENT
function toggleCartItem(product) {
    if (cart[product.id]) {
        delete cart[product.id];
    } else {
        cart[product.id] = {
            ...product,
            quantity: 1
        };
    }
    updateCartUI();
    renderProducts();
}

function changeQuantity(productId, delta) {
    if (!cart[productId]) return;
    
    cart[productId].quantity += delta;
    if (cart[productId].quantity <= 0) {
        delete cart[productId];
        renderProducts();
    }
    updateCartUI();
}

function updateCartUI() {
    let totalItems = 0;
    let totalPrice = 0;
    
    Object.values(cart).forEach(item => {
        totalItems += item.quantity;
        totalPrice += item.price * item.quantity;
    });

    if (totalItems > 0) {
        cartBadge.innerText = totalItems;
        cartBadge.classList.remove('hidden');
    } else {
        cartBadge.innerText = '0';
        cartBadge.classList.add('hidden');
    }

    cartItemsList.innerHTML = '';
    if (Object.keys(cart).length === 0) {
        cartItemsList.innerHTML = `<p class="empty-cart-msg">Your cart is floating in empty space...</p>`;
    } else {
        Object.values(cart).forEach(item => {
            const itemElement = document.createElement('div');
            itemElement.className = 'cart-item';
            itemElement.innerHTML = `
                <div class="cart-item-img">
                    ${item.svg}
                </div>
                <div class="cart-item-details">
                    <p class="cart-item-title">${item.name}</p>
                    <p class="cart-item-price">$${item.price.toFixed(2)}</p>
                </div>
                <div class="cart-item-actions">
                    <button class="qty-btn" onclick="changeQuantity('${item.id}', -1)">-</button>
                    <span class="qty-val">${item.quantity}</span>
                    <button class="qty-btn" onclick="changeQuantity('${item.id}', 1)">+</button>
                </div>
            `;
            cartItemsList.appendChild(itemElement);
        });
    }

    cartTotalPrice.innerText = `$${totalPrice.toFixed(2)}`;
}

// Global scope bindings for inline onclick attributes
window.changeQuantity = changeQuantity;

// EVENT LISTENERS SETUP
function setupEventListeners() {
    // Cart open/close
    document.getElementById('cart-btn').addEventListener('click', () => {
        cartDrawer.classList.toggle('hidden');
    });
    document.getElementById('close-cart-btn').addEventListener('click', () => {
        cartDrawer.classList.add('hidden');
    });

    // Sidebar navigation
    burgerMenuBtn.addEventListener('click', () => {
        sidebar.classList.remove('hidden');
    });
    closeSidebarBtn.addEventListener('click', () => {
        sidebar.classList.add('hidden');
    });
    
    logoutLink.addEventListener('click', () => {
        cart = {};
        updateCartUI();
        currentCategory = 'all';
        currentType = 'all';
        document.body.classList.remove('glitched-ui');
        
        usernameInput.value = '';
        passwordInput.value = '';
        errorMessageContainer.classList.add('hidden');
        sidebar.classList.add('hidden');
        storeContainer.classList.add('hidden');
        loginContainer.classList.remove('hidden');
    });

    // Category Filter Tabs
    document.querySelectorAll('.filter-tab').forEach(tab => {
        tab.addEventListener('click', (e) => {
            document.querySelectorAll('.filter-tab').forEach(t => t.classList.remove('active'));
            e.target.classList.add('active');
            currentCategory = e.target.getAttribute('data-category');
            renderProducts();
        });
    });

    // Type Filter Tabs
    document.querySelectorAll('.type-tab').forEach(tab => {
        tab.addEventListener('click', (e) => {
            document.querySelectorAll('.type-tab').forEach(t => t.classList.remove('active'));
            e.target.classList.add('active');
            currentType = e.target.getAttribute('data-type');
            renderProducts();
        });
    });

    // Checkout Flow events
    checkoutButton.addEventListener('click', () => {
        if (Object.keys(cart).length === 0) {
            alert("Your cart is empty! Pick some accessories/posters first.");
            return;
        }
        cartDrawer.classList.add('hidden');
        checkoutOverlay.classList.remove('hidden');
        checkoutStepOne.classList.remove('hidden');
        checkoutStepTwo.classList.add('hidden');
        checkoutComplete.classList.add('hidden');
    });

    // Form Step 1 Submit
    checkoutForm.addEventListener('submit', (e) => {
        e.preventDefault();
        
        let subtotal = 0;
        Object.values(cart).forEach(item => {
            subtotal += item.price * item.quantity;
        });
        const grandTotal = subtotal + 4.99;

        overviewItemsPrice.innerText = `$${subtotal.toFixed(2)}`;
        overviewGrandTotal.innerText = `$${grandTotal.toFixed(2)}`;

        // Prefill Cardholder display
        const firstName = firstNameInput.value.trim();
        const lastName = lastNameInput.value.trim();
        document.getElementById('card-holder-display').innerText = `${firstName} ${lastName}`.toUpperCase();

        checkoutStepOne.classList.add('hidden');
        checkoutStepTwo.classList.remove('hidden');
    });

    // Step 2 Back Button
    document.getElementById('back-step-one-btn').addEventListener('click', () => {
        checkoutStepTwo.classList.add('hidden');
        checkoutStepOne.classList.remove('hidden');
    });

    // Card Input Formatters and Brand Detector
    const cardNumInput = document.getElementById('card-number');
    const cardExpInput = document.getElementById('card-expiry');
    const cardCvvInput = document.getElementById('card-cvv');
    const paymentForm = document.getElementById('payment-form');

    cardNumInput.addEventListener('input', (e) => {
        let value = e.target.value.replace(/\s+/g, '').replace(/[^0-9]/gi, '');
        let formatted = '';
        for (let i = 0; i < value.length; i++) {
            if (i > 0 && i % 4 === 0) {
                formatted += ' ';
            }
            formatted += value[i];
        }
        e.target.value = formatted;
        document.getElementById('card-num-display').innerText = formatted || '•••• •••• •••• ••••';
        
        // Brand Detection
        const brandDisplay = document.getElementById('card-brand-display');
        if (value.startsWith('4')) {
            brandDisplay.innerText = 'VISA';
        } else if (value.startsWith('5')) {
            brandDisplay.innerText = 'MASTERCARD';
        } else if (value.startsWith('3')) {
            brandDisplay.innerText = 'AMEX';
        } else {
            brandDisplay.innerText = 'COSMIC';
        }
    });

    cardExpInput.addEventListener('input', (e) => {
        let value = e.target.value.replace(/\s+/g, '').replace(/[^0-9]/gi, '');
        if (value.length > 2) {
            value = value.substring(0, 2) + '/' + value.substring(2, 4);
        }
        e.target.value = value;
        document.getElementById('card-exp-display').innerText = value || 'MM/YY';
    });

    // Handle payment simulation submission
    paymentForm.addEventListener('submit', (e) => {
        e.preventDefault();
        
        const payBtnText = document.getElementById('pay-btn-text');
        payBtnText.innerText = "Processing Payment...";
        
        const inputs = paymentForm.querySelectorAll('input, button');
        inputs.forEach(input => input.disabled = true);
        
        setTimeout(() => {
            payBtnText.innerText = "Pay & Finish Order";
            inputs.forEach(input => input.disabled = false);
            
            // Clear payment inputs
            cardNumInput.value = '';
            cardExpInput.value = '';
            cardCvvInput.value = '';
            document.getElementById('card-num-display').innerText = '•••• •••• •••• ••••';
            document.getElementById('card-exp-display').innerText = 'MM/YY';
            
            checkoutStepTwo.classList.add('hidden');
            checkoutComplete.classList.remove('hidden');
            
            cart = {};
            updateCartUI();
            renderProducts();
        }, 1200);
    });

    // Back to products from success screen
    document.getElementById('back-to-products-btn').addEventListener('click', () => {
        checkoutOverlay.classList.add('hidden');
    });
}

// Start application
init();
