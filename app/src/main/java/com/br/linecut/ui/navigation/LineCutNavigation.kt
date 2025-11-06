package com.br.linecut.ui.navigation

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.br.linecut.R
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import com.google.firebase.database.FirebaseDatabase
import com.br.linecut.ui.screens.auth.EmailSentScreen
import com.br.linecut.ui.screens.auth.ForgotPasswordScreen
import com.br.linecut.ui.screens.auth.LoginScreen
import com.br.linecut.ui.screens.auth.SignUpScreen
import com.br.linecut.ui.screens.StoresScreen
import com.br.linecut.ui.screens.StoreDetailScreen
import com.br.linecut.ui.screens.CartScreen
import com.br.linecut.ui.screens.OrderSummaryScreen
import com.br.linecut.ui.screens.PaymentMethodScreen
import com.br.linecut.ui.screens.QRCodePixScreen
import com.br.linecut.ui.screens.PickupQRScreen
import com.br.linecut.ui.screens.LoadingScreen
import com.br.linecut.ui.screens.profile.ProfileScreen
import com.br.linecut.ui.screens.profile.AccountDataScreen
import com.br.linecut.ui.screens.profile.NotificationsScreen
import com.br.linecut.ui.screens.profile.PaymentsScreen
import com.br.linecut.ui.screens.OrdersScreen
import com.br.linecut.ui.screens.OrderDetailsScreen
import com.br.linecut.ui.screens.profile.HelpScreen
import com.br.linecut.ui.screens.profile.help.HowToOrderScreen
import com.br.linecut.ui.screens.profile.help.TrackOrderScreen
import com.br.linecut.ui.screens.profile.help.CancelOrderScreen
import com.br.linecut.ui.screens.profile.help.NotPickedUpScreen
import com.br.linecut.ui.screens.profile.help.ContactSupportScreen
import com.br.linecut.ui.screens.profile.help.FAQScreen
import com.br.linecut.ui.screens.profile.SettingsScreen
import com.br.linecut.ui.screens.profile.settings.TermsAndConditionsScreen
import com.br.linecut.ui.screens.profile.settings.PrivacyPolicyScreen
import com.br.linecut.ui.screens.profile.settings.CloseAccountScreen
import com.br.linecut.ui.screens.profile.settings.AccountClosedScreen
import com.br.linecut.ui.screens.OrderDetail
import com.br.linecut.ui.screens.OrderDetailItem
import com.br.linecut.ui.screens.OrderStatus
import com.br.linecut.ui.screens.PaymentMethod
import com.br.linecut.ui.screens.PaymentType
import com.br.linecut.ui.screens.Store
import com.br.linecut.ui.screens.getSampleOrderDetail
import com.br.linecut.ui.theme.LineCutTheme
import com.br.linecut.ui.utils.ImageCache
import com.br.linecut.ui.utils.ImageLoader
import com.br.linecut.ui.viewmodel.AuthViewModel

@Composable
fun LineCutNavigation(
    modifier: Modifier = Modifier,
    startDestination: Screen = Screen.LOGIN,
    authViewModel: AuthViewModel = viewModel()
) {
    var currentScreen by rememberSaveable { mutableStateOf(startDestination) }
    var userEmail by rememberSaveable { mutableStateOf("") }
    var selectedStore by remember { mutableStateOf<Store?>(null) }
    var cartItems by remember { mutableStateOf(getSampleCartItemsForNavigation()) }
    var shoppingCart by remember { mutableStateOf<List<com.br.linecut.ui.screens.MenuItem>>(emptyList()) }
    var selectedPaymentMethod by remember { mutableStateOf(PaymentMethod.PAY_BY_APP) }
    var selectedPaymentType by remember { mutableStateOf(PaymentType.PIX) }
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var availableStores by rememberSaveable { mutableStateOf(getSampleStoresForSearch()) }
    var showSearchBarOnStores by rememberSaveable { mutableStateOf(false) }
    var currentPedidoId by rememberSaveable { mutableStateOf<String?>(null) }
    var pixResponse by remember { mutableStateOf<com.br.linecut.data.models.PixResponse?>(null) }
    
    // Estados para preservar dados do PIX durante rotação de tela
    var pixQrCodeBase64 by rememberSaveable { mutableStateOf<String?>(null) }
    var pixTxid by rememberSaveable { mutableStateOf<String?>(null) }
    
    // CoroutineScope para operações assíncronas
    val coroutineScope = rememberCoroutineScope()
    
    // Observar o usuário atual
    val currentUser by authViewModel.currentUser.collectAsState()
    
    // Carregar dados do usuário quando navegar para o profile
    LaunchedEffect(currentScreen) {
        if (currentScreen == Screen.PROFILE && currentUser == null) {
            authViewModel.loadCurrentUser()
        }
    }
    
    // Pré-carregar imagem do perfil quando o usuário for carregado
    LaunchedEffect(currentUser) {
        currentUser?.profileImageUrl?.let { url ->
            if (url.isNotBlank() && !ImageCache.contains(url)) {
                // Pré-carregar a imagem em background
                ImageLoader.loadImage(url)
            }
        }
    }
    var selectedOrderDetail by remember { mutableStateOf<OrderDetail?>(null) }
    
    // Initialize order detail if starting directly on ORDER_DETAILS screen
    LaunchedEffect(startDestination) {
        if (startDestination == Screen.ORDER_DETAILS && selectedOrderDetail == null) {
            selectedOrderDetail = OrderDetail(
                orderId = "#1020",
                storeName = "Museoh",
                storeType = "Lanches e Salgados",
                date = "24/04/2025",
                status = "Pedido concluído",
                items = listOf(
                    OrderDetailItem("Açaí", 1, 11.90),
                    OrderDetailItem("Pizza", 2, 20.00),
                    OrderDetailItem("Coca-cola", 1, 5.00),
                    OrderDetailItem("Suco", 1, 6.00)
                ),
                total = 39.90,
                paymentMethod = "PIX",
                pickupLocation = "Praça 3 - Senac",
                rating = 5,
                imageRes = R.drawable.burger_queen
            )
        }
    }
    
    when (currentScreen) {
        Screen.LOGIN -> {
            LoginScreen(
                onLoginSuccess = {
                    // Após login bem-sucedido, navegar para a tela principal
                    currentScreen = Screen.STORES
                },
                onForgotPasswordClick = {
                    currentScreen = Screen.FORGOT_PASSWORD
                },
                onSignUpClick = {
                    currentScreen = Screen.SIGNUP
                },
                authViewModel = authViewModel,
                modifier = modifier
            )
        }
        
        Screen.SIGNUP -> {
            SignUpScreen(
                onSignUpSuccess = {
                    // Após cadastro bem-sucedido, navegar para a tela de login
                    currentScreen = Screen.LOGIN
                },
                onLoginClick = {
                    currentScreen = Screen.LOGIN
                },
                onTermsClick = {
                    // TODO: Show terms and conditions
                    println("Terms and Conditions clicked")
                },
                onPrivacyClick = {
                    // TODO: Show privacy policy
                    println("Privacy Policy clicked")
                },
                authViewModel = authViewModel,
                modifier = modifier
            )
        }
        
        Screen.FORGOT_PASSWORD -> {
            ForgotPasswordScreen(
                onSendEmailClick = { email ->
                    // TODO: Implement forgot password logic
                    println("Forgot Password - Email: $email")
                    // Store email and navigate to success screen
                    userEmail = email
                    currentScreen = Screen.EMAIL_SENT
                },
                onCancelClick = {
                    currentScreen = Screen.LOGIN
                },
                modifier = modifier
            )
        }
        
        Screen.EMAIL_SENT -> {
            EmailSentScreen(
                email = userEmail,
                onResendEmailClick = {
                    // TODO: Implement resend email logic
                    println("Resend email to: $userEmail")
                    // Stay on the same screen or show a toast message
                },
                onBackClick = {
                    currentScreen = Screen.FORGOT_PASSWORD
                },
                modifier = modifier
            )
        }
        
        Screen.STORES -> {
            StoresScreen(
                showSearchBar = showSearchBarOnStores,
                onStoreClick = { store ->
                    selectedStore = store
                    currentScreen = Screen.STORE_DETAIL
                },
                onHomeClick = {
                    // Stay on stores screen since this is the home
                    showSearchBarOnStores = false
                },
                onSearchClick = {
                    // Search now integrated in StoresScreen
                },
                onNotificationClick = {
                    currentScreen = Screen.NOTIFICATIONS
                },
                onOrdersClick = {
                    currentScreen = Screen.ORDERS
                },
                onProfileClick = {
                    currentScreen = Screen.PROFILE
                },
                modifier = modifier
            )
        }
        
        Screen.STORE_DETAIL -> {
            val store = selectedStore ?: getSampleStoresForNavigation().firstOrNull()
            if (store != null) {
                shoppingCart.forEach { item ->
                    println("    - ${item.name}: ${item.quantity}x @ R$ ${item.price}")
                }
                
                StoreDetailScreen(
                    store = store,
                    menuItems = getSampleMenuItemsForNavigation(),
                    categories = getSampleCategoriesForNavigation(),
                    initialCartItems = shoppingCart, // Passar itens do carrinho para restaurar estado
                    onBackClick = {
                        // Limpar carrinho ao voltar
                        shoppingCart = emptyList()
                        currentScreen = Screen.STORES
                    },
                    onCategoryClick = { category ->
                        // TODO: Filter menu items by category
                        println("Category clicked: ${category.name}")
                    },
                    onAddItem = { item ->
                        // Atualizar shoppingCart no nível da navegação
                        val existingItem = shoppingCart.find { it.id == item.id }
                        if (existingItem != null) {
                            shoppingCart = shoppingCart.map {
                                if (it.id == item.id) it.copy(quantity = it.quantity + 1) else it
                            }
                        } else {
                            shoppingCart = shoppingCart + item.copy(quantity = 1)
                        }
                        println("Add item: ${item.name}, total items: ${shoppingCart.size}")
                    },
                    onRemoveItem = { item ->
                        // Remover ou decrementar do shoppingCart
                        shoppingCart = shoppingCart.mapNotNull {
                            if (it.id == item.id) {
                                val newQuantity = it.quantity - 1
                                if (newQuantity > 0) it.copy(quantity = newQuantity) else null
                            } else {
                                it
                            }
                        }
                        println("Remove item: ${item.name}, total items: ${shoppingCart.size}")
                    },
                    onViewCartClick = {
                        // Converter MenuItem para CartItem e navegar
                        cartItems = shoppingCart
                            .filter { it.quantity > 0 }
                            .map { convertMenuItemToCartItem(it) }
                        
                        println("Navigating to CART with ${cartItems.size} items")
                        currentScreen = Screen.CART
                    },
                    onHomeClick = {
                        showSearchBarOnStores = false
                        currentScreen = Screen.STORES
                    },
                    onSearchClick = {
                        // Search integrated in StoresScreen - navigate to stores
                        showSearchBarOnStores = true
                        currentScreen = Screen.STORES
                    },
                    onNotificationClick = {
                        currentScreen = Screen.NOTIFICATIONS
                    },
                    onOrdersClick = {
                        currentScreen = Screen.ORDERS

                    },
                    onProfileClick = {
                        currentScreen = Screen.PROFILE
                    },
                    modifier = modifier
                )
            } else {
                // Fallback: return to stores if no store is available
                LaunchedEffect(Unit) {
                    currentScreen = Screen.STORES
                }
            }
        }
        
        Screen.CART -> {
            val store = selectedStore ?: getSampleStoresForNavigation().firstOrNull()
            if (store != null) {
                println("Rendering CartScreen for: ${store.name} with ${cartItems.size} items")
                println("  Store data: id=${store.id}, name=${store.name}, imageUrl=${store.imageUrl}, category=${store.category}")
                cartItems.forEach { item ->
                    println("  - ${item.name}: ${item.quantity}x @ R$ ${item.price} (imageUrl: ${item.imageUrl})")
                }
                
                CartScreen(
                    store = store,
                    cartItems = cartItems,
                    onBackClick = {
                        currentScreen = Screen.STORE_DETAIL
                    },
                    onClearCartClick = {
                        cartItems = emptyList()
                        shoppingCart = emptyList()
                        currentPedidoId = null // Limpar pedido quando limpar carrinho
                    },
                    onAddMoreItemsClick = {
                        currentScreen = Screen.STORE_DETAIL
                    },
                    onAddItem = { item ->
                        cartItems = cartItems.map { 
                            if (it.id == item.id) it.copy(quantity = it.quantity + 1) else it
                        }
                        // Sincronizar com shoppingCart
                        shoppingCart = shoppingCart.map { menuItem ->
                            if (menuItem.id == item.id) menuItem.copy(quantity = menuItem.quantity + 1) else menuItem
                        }
                    },
                    onRemoveItem = { item ->
                        cartItems = cartItems.mapNotNull { 
                            when {
                                it.id == item.id && it.quantity > 1 -> it.copy(quantity = it.quantity - 1)
                                it.id == item.id && it.quantity == 1 -> null
                                else -> it
                            }
                        }
                        // Sincronizar com shoppingCart
                        shoppingCart = shoppingCart.mapNotNull { menuItem ->
                            if (menuItem.id == item.id) {
                                val newQuantity = menuItem.quantity - 1
                                if (newQuantity > 0) menuItem.copy(quantity = newQuantity) else null
                            } else {
                                menuItem
                            }
                        }
                    },
                    onContinueClick = {
                        currentScreen = Screen.ORDER_SUMMARY
                    },
                    onHomeClick = {
                        showSearchBarOnStores = false
                        currentScreen = Screen.STORES
                    },
                    onSearchClick = {
                        // Search integrated in StoresScreen - navigate to stores
                        showSearchBarOnStores = true
                        currentScreen = Screen.STORES
                    },
                    onNotificationClick = {
                        currentScreen = Screen.NOTIFICATIONS
                    },
                    onOrdersClick = {
                        currentScreen = Screen.ORDERS

                    },
                    onProfileClick = {
                        currentScreen = Screen.PROFILE
                    },
                    modifier = modifier
                )
            } else {
                // Fallback: return to stores if no store is available
                LaunchedEffect(Unit) {
                    currentScreen = Screen.STORES
                }
            }
        }
        
        Screen.ORDER_SUMMARY -> {
            val store = selectedStore ?: getSampleStoresForNavigation().firstOrNull()
            if (store != null) {
                OrderSummaryScreen(
                    store = store,
                    cartItems = cartItems,
                    onBackClick = {
                        currentScreen = Screen.CART
                    },
                    onClearCartClick = {
                        cartItems = emptyList()
                        shoppingCart = emptyList() // Limpar também o shoppingCart
                        currentPedidoId = null // Limpar pedido quando limpar carrinho
                    },
                    onAddMoreItemsClick = {
                        currentScreen = Screen.STORE_DETAIL
                    },
                    onPaymentMethodChange = { method ->
                        // TODO: Handle payment method change
                        println("Payment method changed: $method")
                    },
                    onPaymentTypeChange = { type ->
                        // TODO: Handle payment type change
                        println("Payment type changed: $type")
                    },
                    onFinishOrderClick = {
                        currentScreen = Screen.PAYMENT_METHOD
                    },
                    onHomeClick = {
                        showSearchBarOnStores = false
                        currentScreen = Screen.STORES
                    },
                    onSearchClick = {
                        currentScreen = Screen.STORES
                    },
                    onNotificationClick = {
                        currentScreen = Screen.NOTIFICATIONS
                    },
                    onOrdersClick = {
                        currentScreen = Screen.ORDERS

                    },
                    onProfileClick = {
                        currentScreen = Screen.PROFILE
                    },
                    modifier = modifier
                )
            } else {
                // Fallback: return to stores if no store is available
                LaunchedEffect(Unit) {
                    currentScreen = Screen.STORES
                }
            }
        }
        
        Screen.PAYMENT_METHOD -> {
            PaymentMethodScreen(
                selectedPaymentMethod = selectedPaymentMethod,
                selectedPaymentType = selectedPaymentType,
                hasItemsInCart = cartItems.isNotEmpty(),
                onBackClick = {
                    currentScreen = Screen.ORDER_SUMMARY
                },
                onPaymentMethodChange = { method ->
                    selectedPaymentMethod = method
                },
                onPaymentTypeChange = { type ->
                    selectedPaymentType = type
                },
                onConfirmClick = {
                    if (selectedPaymentMethod == PaymentMethod.PAY_BY_APP && cartItems.isNotEmpty()) {
                        // Navegar para tela de loading para pagamento via PIX
                        currentScreen = Screen.LOADING
                    } else if (cartItems.isNotEmpty()) {
                        // Para pagamento na retirada (local)
                        coroutineScope.launch(kotlinx.coroutines.Dispatchers.IO) {
                            val userId = currentUser?.uid ?: ""
                            val storeId = selectedStore?.id ?: ""
                            
                            if (userId.isNotEmpty() && storeId.isNotEmpty()) {
                                // Criar pedido com pagamento local
                                val pedido = com.br.linecut.data.models.Pedido.fromCarrinho(
                                    carrinho = cartItems,
                                    idUsuario = userId,
                                    idLanchonete = storeId,
                                    metodoPagamento = "local"
                                )
                                pedido.criar_pedido()
                            }
                            
                            kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                                // Limpar carrinho após criar pedido local
                                cartItems = emptyList()
                                shoppingCart = emptyList()
                                currentPedidoId = null
                                currentScreen = Screen.STORES
                            }
                        }
                    }
                },
                modifier = modifier
            )
        }
        
        Screen.LOADING -> {
            LoadingScreen(modifier = modifier)
            
            // Disparar requisição PIX e criar/atualizar pedido
            LaunchedEffect(Unit) {
                try {
                    // Verificar se temos usuário e loja selecionados
                    val userId = currentUser?.uid ?: ""
                    val storeId = selectedStore?.id ?: ""
                    
                    if (userId.isEmpty() || storeId.isEmpty()) {
                        currentScreen = Screen.PAYMENT_METHOD
                        return@LaunchedEffect
                    }
                    
                    // Disparar requisição PIX em paralelo
                    val pixJob = async(kotlinx.coroutines.Dispatchers.IO) {
                        val pix = com.br.linecut.data.api.Pix()
                        val valorTotal = cartItems.sumOf { it.price * it.quantity }
                        val chavePix = selectedStore?.chavePix
                        pix.gerarQRCodePix(valorTotal, chavePix)
                    }
                    
                    // Verificar se já temos um pedido em andamento
                    val database = FirebaseDatabase.getInstance()
                    val pedidoId: String
                    val pedidoRef: com.google.firebase.database.DatabaseReference
                    
                    if (currentPedidoId != null) {
                        // Já temos um pedido - usar o ID existente
                        pedidoId = currentPedidoId!!
                        pedidoRef = database.getReference("pedidos").child(pedidoId)
                        android.util.Log.d("PIX_RESPONSE", "Usando pedido existente: $pedidoId")
                    } else {
                        // Não temos pedido - gerar novo ID do Firebase
                        pedidoRef = database.getReference("pedidos").push()
                        pedidoId = pedidoRef.key ?: java.util.UUID.randomUUID().toString()
                        currentPedidoId = pedidoId
                        android.util.Log.d("PIX_RESPONSE", "Criando novo pedido: $pedidoId")
                    }
                    
                    // Criar pedido a partir do carrinho usando o ID correto
                    val pedido = com.br.linecut.data.models.Pedido.fromCarrinho(
                        carrinho = cartItems,
                        idUsuario = userId,
                        idLanchonete = storeId,
                        metodoPagamento = "pix"
                    )
                    // Atualizar com o ID correto
                    val pedidoComIdCorreto = pedido.copy(id_pedido = pedidoId)
                    
                    // Verificar se já existe no Firebase
                    val snapshot = pedidoRef.get().await()
                    
                    val resultado: Result<String>
                    if (snapshot.exists()) {
                        // Pedido já existe - atualizar
                        android.util.Log.d("PIX_RESPONSE", "Pedido $pedidoId já existe - atualizando")
                        resultado = pedidoComIdCorreto.atualizar_pedido()
                    } else {
                        // Pedido não existe - criar novo
                        android.util.Log.d("PIX_RESPONSE", "Salvando pedido $pedidoId pela primeira vez")
                        resultado = pedidoComIdCorreto.criar_pedido()
                    }
                    
                    // Aguardar retorno da API PIX
                    val pixResponseData = pixJob.await()
                    android.util.Log.d("PIX_RESPONSE", "Resposta da API PIX: $pixResponseData")
                    
                    // Atualizar cod_transacao_pagamento no Firebase
                    val txid = pixResponseData?.cobData?.txid ?: ""
                    if (txid.isNotEmpty() && resultado.isSuccess) {
                        val updates = mapOf<String, Any>("cod_transacao_pagamento" to txid)
                        pedidoRef.updateChildren(updates).await()
                        android.util.Log.d("PIX_RESPONSE", "Código de transação atualizado: $txid")
                    }
                    
                    // Salvar dados persistentes do PIX
                    pixResponse = pixResponseData
                    pixQrCodeBase64 = pixResponseData?.qrCodeImage
                    pixTxid = txid
                    
                    // Navegar para QR Code PIX
                    currentScreen = Screen.QR_CODE_PIX
                } catch (e: Exception) {
                    android.util.Log.e("PIX_ERROR", "Erro ao processar PIX", e)
                    currentScreen = Screen.PAYMENT_METHOD
                }
            }
        }
        
        Screen.QR_CODE_PIX -> {
            val totalAmount = cartItems.sumOf { it.price * it.quantity }
            QRCodePixScreen(
                totalAmount = totalAmount,
                qrCodeBase64 = pixQrCodeBase64, // Usar estado persistente
                pixCopiaCola = pixResponse?.cobData?.pixCopiaECola,
                onBackClick = {
                    currentScreen = Screen.PAYMENT_METHOD
                },
                onFinishPaymentClick = {
                    // Clear cart after payment completion
                    cartItems = emptyList()
                    shoppingCart = emptyList() // Limpar também o shoppingCart
                    // Clear pedido ID as the payment is completed
                    currentPedidoId = null
                    // Clear PIX response e estados persistentes
                    pixResponse = null
                    pixQrCodeBase64 = null
                    pixTxid = null
                    // Navigate to pickup QR screen
                    currentScreen = Screen.PICKUP_QR
                },
                modifier = modifier
            )
        }
        
        Screen.PICKUP_QR -> {
            PickupQRScreen(
                orderNumber = "#1024", // TODO: Get actual order number
                onBackClick = {
                    currentScreen = Screen.QR_CODE_PIX
                },
                onHomeClick = {
                    showSearchBarOnStores = false
                    currentScreen = Screen.STORES
                },
                onSearchClick = {
                    currentScreen = Screen.STORES
                },
                onNotificationClick = {
                    currentScreen = Screen.NOTIFICATIONS
                },
                onOrdersClick = {
                    currentScreen = Screen.ORDERS
                },
                onProfileClick = {
                    currentScreen = Screen.PROFILE
                },
                modifier = modifier
            )
        }
        
        Screen.PROFILE -> {
            ProfileScreen(
                onAccountDataClick = {
                    currentScreen = Screen.ACCOUNT_DATA
                },
                onNotificationsClick = {
                    currentScreen = Screen.NOTIFICATIONS
                },
                onPaymentsClick = {
                    currentScreen = Screen.PAYMENTS
                },
                onFavoritesClick = {
                    // TODO: Navigate to favorites screen
                    println("Favorites clicked")
                },
                onHelpClick = {
                    currentScreen = Screen.HELP
                },
                onSettingsClick = {
                    currentScreen = Screen.SETTINGS
                },
                onHomeClick = {
                    showSearchBarOnStores = false
                    currentScreen = Screen.STORES
                },
                onSearchClick = {
                    // Search integrated in StoresScreen - navigate to stores
                    showSearchBarOnStores = true
                    currentScreen = Screen.STORES
                },
                onNotificationClick = {
                    currentScreen = Screen.NOTIFICATIONS
                },
                onOrdersClick = {
                    currentScreen = Screen.ORDERS

                },
                onProfileClick = {
                    // Already on profile screen
                },
                modifier = modifier
            )
        }
        
        Screen.ACCOUNT_DATA -> {
            AccountDataScreen(
                onBackClick = {
                    currentScreen = Screen.PROFILE
                },
                onEditClick = {
                    // TODO: Navigate to edit account data screen
                    println("Edit account data clicked")
                },
                onHomeClick = {
                    showSearchBarOnStores = false
                    currentScreen = Screen.STORES
                },
                onSearchClick = {
                    // Search integrated in StoresScreen - navigate to stores
                    currentScreen = Screen.STORES
                },
                onNotificationClick = {
                    currentScreen = Screen.NOTIFICATIONS
                },
                onOrdersClick = {
                    currentScreen = Screen.ORDERS

                },
                onProfileClick = {
                    currentScreen = Screen.PROFILE
                },
                modifier = modifier
            )
        }
        
        Screen.NOTIFICATIONS -> {
            NotificationsScreen(
                onBackClick = {
                    currentScreen = Screen.PROFILE
                },
                onRatingClick = { notificationId ->
                    // TODO: Navigate to rating screen or show rating dialog
                    println("Rating clicked for notification: $notificationId")
                },
                onHomeClick = {
                    showSearchBarOnStores = false
                    currentScreen = Screen.STORES
                },
                onSearchClick = {
                    // Search integrated in StoresScreen - navigate to stores
                    showSearchBarOnStores = true
                    currentScreen = Screen.STORES
                },
                onNotificationClick = {
                    // Already on notifications screen
                },
                onOrdersClick = {
                    currentScreen = Screen.ORDERS

                },
                onProfileClick = {
                    currentScreen = Screen.PROFILE
                },
                modifier = modifier
            )
        }
        
        Screen.PAYMENTS -> {
            PaymentsScreen(
                onBackClick = {
                    currentScreen = Screen.PROFILE
                },
                onHomeClick = {
                    showSearchBarOnStores = false
                    currentScreen = Screen.STORES
                },
                onSearchClick = {
                    // Search integrated in StoresScreen - navigate to stores
                    showSearchBarOnStores = true
                    currentScreen = Screen.STORES
                },
                onNotificationClick = {
                    currentScreen = Screen.NOTIFICATIONS
                },
                onOrdersClick = {
                    currentScreen = Screen.ORDERS
                },
                onProfileClick = {
                    currentScreen = Screen.PROFILE
                }
            )
        }
        
        Screen.ORDERS -> {
            OrdersScreen(
                onBackClick = {
                    showSearchBarOnStores = false
                    currentScreen = Screen.STORES
                },
                onHomeClick = {
                    showSearchBarOnStores = false
                    currentScreen = Screen.STORES
                },
                onSearchClick = {
                    // Search integrated in StoresScreen - navigate to stores
                    showSearchBarOnStores = true
                    currentScreen = Screen.STORES
                },
                onNotificationClick = {
                    currentScreen = Screen.NOTIFICATIONS
                },
                onOrdersClick = {
                    // Already on orders screen
                },
                onProfileClick = {
                    currentScreen = Screen.PROFILE
                },
                onOrderClick = { order ->
                    // Convert Order to OrderDetail and navigate
                    selectedOrderDetail = OrderDetail(
                        orderId = order.id,
                        storeName = order.storeName,
                        storeType = "Lanches e Salgados", // Default type
                        date = order.date,
                        status = when(order.status) {
                            OrderStatus.COMPLETED -> "Pedido concluído"
                            OrderStatus.IN_PROGRESS -> "Em preparo"
                            OrderStatus.CANCELLED -> "Cancelado"
                        },
                        paymentStatus = "pendente", // Simular pagamento pendente
                        remainingTime = "10:00 min", // Tempo restante para pagamento
                        items = listOf(
                            OrderDetailItem("Item exemplo", 1, order.total)
                        ),
                        total = order.total,
                        paymentMethod = "PIX",
                        pickupLocation = "Praça 3 - Senac",
                        rating = if (order.status == OrderStatus.COMPLETED) 5 else null,
                        imageRes = R.drawable.burger_queen,
                        createdAtMillis = System.currentTimeMillis() // Simula pedido criado agora - em produção virá do Firebase
                    )
                    currentScreen = Screen.ORDER_DETAILS
                }
            )
        }
        
        Screen.ORDER_DETAILS -> {
            selectedOrderDetail?.let { orderDetail ->
                OrderDetailsScreen(
                    order = orderDetail,
                    onBackClick = {
                        currentScreen = Screen.ORDERS
                    },
                    onHomeClick = {
                        showSearchBarOnStores = false
                        currentScreen = Screen.STORES
                    },
                    onSearchClick = {
                        currentScreen = Screen.STORES
                    },
                    onNotificationClick = {
                        currentScreen = Screen.NOTIFICATIONS
                    },
                    onOrdersClick = {
                        currentScreen = Screen.ORDERS
                    },
                    onProfileClick = {
                        currentScreen = Screen.PROFILE
                    },
                    onRateOrderClick = {
                        // TODO: Implement rating functionality
                    },
                    onAddToCartClick = {
                        // TODO: Add items to cart and navigate
                        currentScreen = Screen.CART
                    }
                )
            }
        }
        
        Screen.HELP -> {
            HelpScreen(
                onBackClick = {
                    currentScreen = Screen.PROFILE
                },
                onHowToOrderClick = {
                    currentScreen = Screen.HOW_TO_ORDER
                },
                onTrackOrderClick = {
                    currentScreen = Screen.TRACK_ORDER
                },
                onCancelOrderClick = {
                    currentScreen = Screen.CANCEL_ORDER
                },
                onNotPickedUpClick = {
                    currentScreen = Screen.NOT_PICKED_UP
                },
                onContactSupportClick = {
                    currentScreen = Screen.CONTACT_SUPPORT
                },
                onFAQClick = {
                    currentScreen = Screen.FAQ
                },
                onHomeClick = {
                    showSearchBarOnStores = false
                    currentScreen = Screen.STORES
                },
                onSearchClick = {
                    currentScreen = Screen.STORES
                },
                onNotificationClick = {
                    currentScreen = Screen.NOTIFICATIONS
                },
                onOrdersClick = {
                    currentScreen = Screen.ORDERS
                },
                onProfileClick = {
                    currentScreen = Screen.PROFILE
                }
            )
        }
        
        Screen.HOW_TO_ORDER -> {
            HowToOrderScreen(
                onBackClick = {
                    currentScreen = Screen.HELP
                },
                onHomeClick = {
                    showSearchBarOnStores = false
                    currentScreen = Screen.STORES
                },
                onSearchClick = {
                    currentScreen = Screen.STORES
                },
                onNotificationClick = {
                    currentScreen = Screen.NOTIFICATIONS
                },
                onOrdersClick = {
                    currentScreen = Screen.ORDERS
                },
                onProfileClick = {
                    currentScreen = Screen.PROFILE
                }
            )
        }
        
        Screen.TRACK_ORDER -> {
            TrackOrderScreen(
                onBackClick = {
                    currentScreen = Screen.HELP
                },
                onHomeClick = {
                    showSearchBarOnStores = false
                    currentScreen = Screen.STORES
                },
                onSearchClick = {
                    currentScreen = Screen.STORES
                },
                onNotificationClick = {
                    currentScreen = Screen.NOTIFICATIONS
                },
                onOrdersClick = {
                    currentScreen = Screen.ORDERS
                },
                onProfileClick = {
                    currentScreen = Screen.PROFILE
                }
            )
        }
        
        Screen.CANCEL_ORDER -> {
            CancelOrderScreen(
                onBackClick = {
                    currentScreen = Screen.HELP
                },
                onHomeClick = {
                    showSearchBarOnStores = false
                    currentScreen = Screen.STORES
                },
                onSearchClick = {
                    currentScreen = Screen.STORES
                },
                onNotificationClick = {
                    currentScreen = Screen.NOTIFICATIONS
                },
                onOrdersClick = {
                    currentScreen = Screen.ORDERS
                },
                onProfileClick = {
                    currentScreen = Screen.PROFILE
                }
            )
        }
        
        Screen.NOT_PICKED_UP -> {
            NotPickedUpScreen(
                onBackClick = {
                    currentScreen = Screen.HELP
                },
                onHomeClick = {
                    showSearchBarOnStores = false
                    currentScreen = Screen.STORES
                },
                onSearchClick = {
                    currentScreen = Screen.STORES
                },
                onNotificationClick = {
                    currentScreen = Screen.NOTIFICATIONS
                },
                onOrdersClick = {
                    currentScreen = Screen.ORDERS
                },
                onProfileClick = {
                    currentScreen = Screen.PROFILE
                }
            )
        }
        
        Screen.CONTACT_SUPPORT -> {
            ContactSupportScreen(
                onBackClick = {
                    currentScreen = Screen.HELP
                },
                onHomeClick = {
                    showSearchBarOnStores = false
                    currentScreen = Screen.STORES
                },
                onSearchClick = {
                    currentScreen = Screen.STORES
                },
                onNotificationClick = {
                    currentScreen = Screen.NOTIFICATIONS
                },
                onOrdersClick = {
                    currentScreen = Screen.ORDERS
                },
                onProfileClick = {
                    currentScreen = Screen.PROFILE
                }
            )
        }
        
        Screen.FAQ -> {
            FAQScreen(
                onBackClick = {
                    currentScreen = Screen.HELP
                },
                onHomeClick = {
                    showSearchBarOnStores = false
                    currentScreen = Screen.STORES
                },
                onSearchClick = {
                    currentScreen = Screen.STORES
                },
                onNotificationClick = {
                    currentScreen = Screen.NOTIFICATIONS
                },
                onOrdersClick = {
                    currentScreen = Screen.ORDERS
                },
                onProfileClick = {
                    currentScreen = Screen.PROFILE
                }
            )
        }
        
        Screen.SETTINGS -> {
            SettingsScreen(
                onBackClick = {
                    currentScreen = Screen.PROFILE
                },
                onTermsClick = {
                    currentScreen = Screen.TERMS_AND_CONDITIONS
                },
                onPrivacyClick = {
                    currentScreen = Screen.PRIVACY_POLICY
                },
                onCloseAccountClick = {
                    currentScreen = Screen.CLOSE_ACCOUNT
                },
                onLogoutClick = {
                    // TODO: Show logout confirmation and navigate to login
                    currentScreen = Screen.LOGIN
                },
                onHomeClick = {
                    showSearchBarOnStores = false
                    currentScreen = Screen.STORES
                },
                onSearchClick = {
                    currentScreen = Screen.STORES
                },
                onNotificationClick = {
                    currentScreen = Screen.NOTIFICATIONS
                },
                onOrdersClick = {
                    currentScreen = Screen.ORDERS
                },
                onProfileClick = {
                    currentScreen = Screen.PROFILE
                }
            )
        }

        Screen.TERMS_AND_CONDITIONS -> {
            TermsAndConditionsScreen(
                onBackClick = {
                    currentScreen = Screen.SETTINGS
                },
                onHomeClick = {
                    showSearchBarOnStores = false
                    currentScreen = Screen.STORES
                },
                onSearchClick = {
                    currentScreen = Screen.STORES
                },
                onNotificationClick = {
                    currentScreen = Screen.NOTIFICATIONS
                },
                onOrdersClick = {
                    currentScreen = Screen.ORDERS
                },
                onProfileClick = {
                    currentScreen = Screen.PROFILE
                }
            )
        }
        
        Screen.PRIVACY_POLICY -> {
            PrivacyPolicyScreen(
                onBackClick = {
                    currentScreen = Screen.SETTINGS
                },
                onHomeClick = {
                    showSearchBarOnStores = false
                    currentScreen = Screen.STORES
                },
                onSearchClick = {
                    currentScreen = Screen.STORES
                },
                onNotificationClick = {
                    currentScreen = Screen.NOTIFICATIONS
                },
                onOrdersClick = {
                    currentScreen = Screen.ORDERS
                },
                onProfileClick = {
                    currentScreen = Screen.PROFILE
                }
            )
        }
        
        Screen.CLOSE_ACCOUNT -> {
            CloseAccountScreen(
                onBackClick = {
                    currentScreen = Screen.SETTINGS
                },
                onCancelClick = {
                    currentScreen = Screen.SETTINGS
                },
                onConfirmCloseClick = {
                    // Navigate to account closed confirmation screen
                    currentScreen = Screen.ACCOUNT_CLOSED
                },
                onHomeClick = {
                    showSearchBarOnStores = false
                    currentScreen = Screen.STORES
                },
                onSearchClick = {
                    currentScreen = Screen.STORES
                },
                onNotificationClick = {
                    currentScreen = Screen.NOTIFICATIONS
                },
                onOrdersClick = {
                    currentScreen = Screen.ORDERS
                },
                onProfileClick = {
                    currentScreen = Screen.PROFILE
                }
            )
        }
        
        Screen.ACCOUNT_CLOSED -> {
            AccountClosedScreen(
                onLoginClick = {
                    currentScreen = Screen.LOGIN
                }
            )
        }
    }
}

// Previews para testar diferentes telas da navegação
@Preview(
    name = "Login Screen",
    showBackground = true,
    group = "Navigation"
)
@Composable
fun NavigationLoginPreview() {
    LineCutTheme {
        LineCutNavigation(startDestination = Screen.LOGIN)
    }
}

@Preview(
    name = "SignUp Screen", 
    showBackground = true,
    group = "Navigation"
)
@Composable
fun NavigationSignUpPreview() {
    LineCutTheme {
        LineCutNavigation(startDestination = Screen.SIGNUP)
    }
}

@Preview(
    name = "Forgot Password Screen",
    showBackground = true,
    group = "Navigation"
)
@Composable
fun NavigationForgotPasswordPreview() {
    LineCutTheme {
        LineCutNavigation(startDestination = Screen.FORGOT_PASSWORD)
    }
}

@Preview(
    name = "Email Sent Screen",
    showBackground = true,
    group = "Navigation"
)
@Composable
fun NavigationEmailSentPreview() {
    LineCutTheme {
        // Simulando que veio da tela anterior com um email
        EmailSentScreen(
            email = "usuario@exemplo.com",
            onResendEmailClick = {},
            onBackClick = {}
        )
    }
}

@Preview(
    name = "Store Detail Screen",
    showBackground = true,
    group = "Navigation"
)
@Composable
fun NavigationStoreDetailPreview() {
    LineCutTheme {
        // Preview com store pré-selecionada
        var currentScreen by remember { mutableStateOf(Screen.STORE_DETAIL) }
        var selectedStore by remember { 
            mutableStateOf<com.br.linecut.ui.screens.Store?>(
                getSampleStoresForNavigation().firstOrNull()
            ) 
        }
        
        when (currentScreen) {
            Screen.STORE_DETAIL -> {
                val store = selectedStore ?: getSampleStoresForNavigation().firstOrNull()
                if (store != null) {
                    StoreDetailScreen(
                        store = store,
                        menuItems = getSampleMenuItemsForNavigation(),
                        categories = getSampleCategoriesForNavigation(),
                        onBackClick = { currentScreen = Screen.STORES },
                        onCategoryClick = { },
                        onAddItem = { },
                        onRemoveItem = { },
                        onViewCartClick = { },
                        onHomeClick = { currentScreen = Screen.STORES },
                        onSearchClick = { },
                        onNotificationClick = { },
                        onOrdersClick = { },
                        onProfileClick = { }
                    )
                }
            }
            else -> {
                // Fallback para outras telas se necessário
            }
        }
    }
}

@Preview(
    name = "Stores Screen",
    showBackground = true,
    group = "Navigation"
)
@Composable
fun NavigationStoresPreview() {
    LineCutTheme {
        LineCutNavigation(startDestination = Screen.STORES)
    }
}

@Preview(
    name = "Cart Screen",
    showBackground = true,
    group = "Navigation"
)
@Composable
fun NavigationCartPreview() {
    LineCutTheme {
        LineCutNavigation(startDestination = Screen.CART)
    }
}

// Função auxiliar para dados de exemplo na navegação
private fun getSampleStoresForNavigation() = listOf(
    com.br.linecut.ui.screens.Store(
        id = "1",
    name = "Burger Queen",
        category = "Lanches e Salgados",
        location = "Praça 3 - Senac",
    distance = "150m",
    imageRes = R.drawable.burger_queen
    ),
    com.br.linecut.ui.screens.Store(
        id = "2",
    name = "Sabor & Cia",
        category = "Refeições variadas",
        location = "Praça 3 - Senac",
    distance = "200m",
    imageRes = R.drawable.sabor_e_cia
    ),
    com.br.linecut.ui.screens.Store(
        id = "3",
        name = "Cafezin",
        category = "Café gourmet",
        location = "Praça 2 - Senac",
    distance = "240m",
    imageRes = R.drawable.cafezin
    ),
    com.br.linecut.ui.screens.Store(
        id = "4",
    name = "Sanduba Burger",
        category = "Lanches variados",
        location = "Praça 2 - Senac",
        distance = "260m",
    isFavorite = true,
    imageRes = R.drawable.sanduba_burger
    ),
    com.br.linecut.ui.screens.Store(
        id = "5",
        name = "Vila Sabor",
        category = "Refeições variadas",
        location = "Praça 1 - Senac",
    distance = "300m",
    isFavorite = true,
    imageRes = R.drawable.vila_sabor
    )
)

private fun getSampleMenuItemsForNavigation() = listOf(
    com.br.linecut.ui.screens.MenuItem(
        id = "1",
        name = "Açaí",
        description = "Creme congelado feito da polpa do fruto açaí, geralmente servido com frutas, granola e outros acompanhamentos.",
        price = 11.90,
        category = "acompanhamentos",
        quantity = 1,
        imageRes = R.drawable.acai
    ),
    com.br.linecut.ui.screens.MenuItem(
        id = "2",
        name = "Croissant",
        description = "Massa folhada em formato de meia-lua, geralmente amanteigada e podendo ter recheios doces ou salgados.",
        price = 6.00,
        category = "acompanhamentos",
        quantity = 1,
        imageRes = R.drawable.croissant
    ),
    com.br.linecut.ui.screens.MenuItem(
        id = "3",
        name = "Lanche Natural",
        description = "Sanduíche preparado com pão integral, recheios leves como queijo branco, peito de peru, salada e molhos leves.",
        price = 8.00,
        category = "lanches",
        quantity = 1,
        imageRes = R.drawable.lanche_natural
    ),
    com.br.linecut.ui.screens.MenuItem(
        id = "4",
        name = "Pão de Queijo",
        description = "Pequeno pão assado, feito com polvilho, queijo e outros ingredientes, resultando em uma textura macia e elástica.",
        price = 4.00,
        category = "acompanhamentos",
        quantity = 1,
        imageRes = R.drawable.pao_de_queijo
    )
)

private fun getSampleCategoriesForNavigation() = listOf(
    com.br.linecut.ui.screens.MenuCategory("acompanhamentos", "Acompanhamentos", true),
    com.br.linecut.ui.screens.MenuCategory("bebidas", "Bebidas"),
    com.br.linecut.ui.screens.MenuCategory("combos", "Combos"),
    com.br.linecut.ui.screens.MenuCategory("doces", "Doces"),
    com.br.linecut.ui.screens.MenuCategory("lanches", "Lanches"),
    com.br.linecut.ui.screens.MenuCategory("pratos", "Pratos Principais"),
    com.br.linecut.ui.screens.MenuCategory("salgados", "Salgados"),
    com.br.linecut.ui.screens.MenuCategory("sobremesas", "Sobremesas")
)

// Função auxiliar para dados de exemplo do carrinho
private fun getSampleCartItemsForNavigation() = listOf(
    com.br.linecut.ui.screens.CartItem(
        id = "1",
        name = "Açaí",
        price = 11.90,
        quantity = 1,
        imageRes = R.drawable.acai
    ),
    com.br.linecut.ui.screens.CartItem(
        id = "2",
        name = "Pizza",
        price = 20.00,
        imageRes = R.drawable.pizza,
        quantity = 2
    ),
    com.br.linecut.ui.screens.CartItem(
        id = "3",
        name = "Coca-cola",
        price = 5.00,
        quantity = 1,
        imageRes = R.drawable.coca_cola
    ),
    com.br.linecut.ui.screens.CartItem(
        id = "4",
        name = "Suco",
        price = 5.00,
        imageRes = R.drawable.suco,
        quantity = 1
    )
)

// Função auxiliar para converter MenuItem em CartItem
private fun convertMenuItemToCartItem(menuItem: com.br.linecut.ui.screens.MenuItem): com.br.linecut.ui.screens.CartItem {
    return com.br.linecut.ui.screens.CartItem(
        id = menuItem.id,
        name = menuItem.name,
        price = menuItem.price,
        quantity = menuItem.quantity,
        imageRes = menuItem.imageRes,
        imageUrl = menuItem.imageUrl // Preserva a URL da imagem do Firebase
    )
}

@Preview(
    name = "Order Summary Screen",
    showBackground = true,
    group = "Navigation"
)
@Composable
fun NavigationOrderSummaryPreview() {
    LineCutTheme {
        LineCutNavigation(startDestination = Screen.ORDER_SUMMARY)
    }
}

@Preview(
    name = "Payment Method Screen",
    showBackground = true,
    group = "Navigation"
)
@Composable
fun NavigationPaymentMethodPreview() {
    LineCutTheme {
        LineCutNavigation(startDestination = Screen.PAYMENT_METHOD)
    }
}

@Preview(
    name = "QR Code PIX Screen",
    showBackground = true,
    group = "Navigation"
)
@Composable
fun NavigationQRCodePixPreview() {
    LineCutTheme {
        LineCutNavigation(startDestination = Screen.QR_CODE_PIX)
    }
}

@Preview(
    name = "Pickup QR Screen",
    showBackground = true,
    group = "Navigation"
)
@Composable
fun NavigationPickupQRPreview() {
    LineCutTheme {
        LineCutNavigation(startDestination = Screen.PICKUP_QR)
    }
}

@Preview(
    name = "Profile Screen",
    showBackground = true,
    group = "Navigation"
)
@Composable
fun NavigationProfilePreview() {
    LineCutTheme {
        LineCutNavigation(startDestination = Screen.PROFILE)
    }
}

@Preview(
    name = "Notifications Screen",
    showBackground = true,
    group = "Navigation"
)
@Composable
fun NavigationNotificationsPreview() {
    LineCutTheme {
        LineCutNavigation(startDestination = Screen.NOTIFICATIONS)
    }
}

@Preview(
    name = "Payments Screen",
    showBackground = true,
    group = "Navigation"
)
@Composable
fun NavigationPaymentsPreview() {
    LineCutTheme {
        LineCutNavigation(startDestination = Screen.PAYMENTS)
    }
}

@Preview(
    name = "Account Data Screen",
    showBackground = true,
    group = "Navigation"
)
@Composable
fun NavigationAccountDataPreview() {
    LineCutTheme {
        LineCutNavigation(startDestination = Screen.ACCOUNT_DATA)
    }
}

@Preview(
    name = "Stores Screen with Search",
    showBackground = true,
    group = "Navigation"
)
@Composable
fun NavigationStoresWithSearchPreview() {
    LineCutTheme {
        LineCutNavigation(startDestination = Screen.STORES)
    }
}

@Preview(
    name = "Orders Screen",
    showBackground = true,
    group = "Navigation"
)
@Composable
fun NavigationOrdersScreenPreview() {
    LineCutTheme {
        LineCutNavigation(startDestination = Screen.ORDERS)
    }
}

// Função para gerar dados de exemplo das lojas para busca
private fun getSampleStoresForSearch(): List<Store> {
    return listOf(
        Store(
            id = "1",
            name = "Museoh",
            category = "Lanches e Salgados",
            location = "Praça 3 - Senac",
            distance = "150m",
            isFavorite = true
        ),
        Store(
            id = "2",
            name = "Sabor & Companhia",
            category = "Refeições variadas",
            location = "Praça 3 - Senac",
            distance = "200m",
            isFavorite = false
        ),
        Store(
            id = "3",
            name = "Cafezin",
            category = "Café gourmet",
            location = "Praça 2 - Senac",
            distance = "240m",
            isFavorite = false
        ),
        Store(
            id = "4",
            name = "Sanduba Burguer",
            category = "Lanches variados",
            location = "Praça 2 - Senac",
            distance = "260m",
            isFavorite = true
        ),
        Store(
            id = "5",
            name = "Vila Sabor",
            category = "Refeições variadas",
            location = "Praça 1 - Senac",
            distance = "300m",
            isFavorite = true
        ),
        Store(
            id = "6",
            name = "Varanda",
            category = "Snacks",
            location = "Praça 1 - Senac",
            distance = "320m",
            isFavorite = false
        ),
        Store(
            id = "7",
            name = "Urban Food",
            category = "Refeição rápida",
            location = "Praça 1 - Senac",
            distance = "400m",
            isFavorite = false
        )
    )
}

// Sample data for preview
private fun getSampleOrderDetail() = OrderDetail(
    orderId = "#1020",
    storeName = "Museoh",
    storeType = "Lanches e Salgados",
    date = "24/04/2025",
    status = "Pedido concluído",
    paymentStatus = "aprovado",
    remainingTime = null,
    items = listOf(
        OrderDetailItem("Açaí", 1, 11.90),
        OrderDetailItem("Pizza", 2, 20.00),
        OrderDetailItem("Coca-cola", 1, 5.00),
        OrderDetailItem("Suco", 1, 6.00)
    ),
    total = 39.90,
    paymentMethod = "PIX",
    pickupLocation = "Praça 3 - Senac",
    rating = 5,
    imageRes = R.drawable.burger_queen,
    createdAtMillis = null
)

@Preview(
    name = "Order Details Screen",
    showBackground = true,
    group = "Navigation"
)
@Composable
fun OrderDetailsScreenWithoutRatingPreview() {
    LineCutTheme {
        OrderDetailsScreen(
            order = getSampleOrderDetail().copy(
                status = "Em preparo",
                rating = null
            ),
            onBackClick = { },
            onHomeClick = { },
            onSearchClick = { },
            onNotificationClick = { },
            onOrdersClick = { },
            onProfileClick = { },
            onRateOrderClick = { },
            onAddToCartClick = { }
        )
    }
}

@Preview(
    name = "Help Screen",
    showBackground = true,
    group = "Navigation"
)
@Composable
fun NavigationHelpPreview() {
    LineCutTheme {
        LineCutNavigation(startDestination = Screen.HELP)
    }
}

@Preview(
    name = "Settings Screen",
    showBackground = true,
    group = "Navigation"
)
@Composable
fun NavigationSettingsPreview() {
    LineCutTheme {
        LineCutNavigation(startDestination = Screen.SETTINGS)
    }
}

@Preview(
    name = "Terms and Conditions Screen",
    showBackground = true,
    group = "Navigation"
)
@Composable
fun NavigationTermsAndConditionsPreview() {
    LineCutTheme {
        LineCutNavigation(startDestination = Screen.TERMS_AND_CONDITIONS)
    }
}

@Preview(
    name = "Privacy Policy Screen",
    showBackground = true,
    group = "Navigation"
)
@Composable
fun NavigationPrivacyPolicyPreview() {
    LineCutTheme {
        LineCutNavigation(startDestination = Screen.PRIVACY_POLICY)
    }
}
