package com.br.linecut.ui.navigation

import android.util.Log
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.br.linecut.R
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
import com.br.linecut.ui.screens.profile.FavoritesScreen
import com.br.linecut.ui.screens.OrdersScreen
import com.br.linecut.service.ServiceManager
import com.br.linecut.ui.screens.OrderDetailsScreen
import com.br.linecut.ui.screens.RateOrderScreen
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
import com.br.linecut.data.repository.OrderRepository
import com.br.linecut.data.repository.NotificationRepository
import kotlin.random.Random

/**
 * Gera um código único para pedido com formato: 3 LETRAS + 2 NÚMEROS (ex: ABC12, XYZ45)
 * Verifica no Firebase se o código já existe antes de retornar
 */
suspend fun generateUniqueOrderCode(): String {
    val database = FirebaseDatabase.getInstance()
    val pedidosRef = database.getReference("pedidos")
    
    var attempts = 0
    val maxAttempts = 50 // Limitar tentativas para evitar loop infinito
    
    while (attempts < maxAttempts) {
        // Gerar 3 letras aleatórias (A-Z)
        val letters = (1..3).map { 
            ('A'..'Z').random() 
        }.joinToString("")
        
        // Gerar 2 números aleatórios (0-9)
        val numbers = (1..2).map { 
            Random.nextInt(0, 10) 
        }.joinToString("")
        
        val code = letters + numbers
        
        // Verificar se já existe no Firebase
        val snapshot = pedidosRef.child(code).get().await()
        
        if (!snapshot.exists()) {
            Log.d("ORDER_CODE", "✅ Código único gerado: $code (tentativa ${attempts + 1})")
            return code
        }
        
        attempts++
        Log.d("ORDER_CODE", "⚠️ Código $code já existe, tentando novamente... (tentativa $attempts)")
    }
    
    // Fallback: se não conseguir gerar código único após maxAttempts, usar timestamp
    val fallbackCode = "ORD${System.currentTimeMillis().toString().takeLast(5)}"
    Log.e("ORDER_CODE", "❌ Não foi possível gerar código único após $maxAttempts tentativas. Usando fallback: $fallbackCode")
    return fallbackCode
}

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
    
    // OrderRepository para buscar pedidos
    val orderRepository = remember { OrderRepository() }
    
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
    var selectedOrderForRating by remember { mutableStateOf<com.br.linecut.ui.screens.Order?>(null) }
    var existingRatingForOrder by remember { mutableStateOf<com.br.linecut.ui.screens.ExistingRating?>(null) }
    
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
                rating = 5f,
                imageRes = R.drawable.burger_queen
            )
        }
    }
    
    when (currentScreen) {
        Screen.LOGIN -> {
            val context = LocalContext.current
            
            LoginScreen(
                onLoginSuccess = {
                    // Após login bem-sucedido, iniciar o service de monitoramento
                    ServiceManager.startOrderMonitoringIfLoggedIn(context)
                    
                    // Navegar para a tela principal
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
                        selectedPaymentMethod = method
                    },
                    onPaymentTypeChange = { type ->
                        selectedPaymentType = type
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
                        // Não temos pedido - gerar novo código único
                        pedidoId = generateUniqueOrderCode()
                        pedidoRef = database.getReference("pedidos").child(pedidoId)
                        currentPedidoId = pedidoId
                        
                        // Limpar selectedOrderDetail de pedidos anteriores
                        selectedOrderDetail = null
                        
                        android.util.Log.d("PIX_RESPONSE", "Criando novo pedido com código: $pedidoId")
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
                    
                    // Atualizar cod_transacao_pagamento, pix_copia_cola e qr_code_pedido (base64) no Firebase
                    val txid = pixResponseData?.cobData?.txid ?: ""
                    val pixCopiaCola = pixResponseData?.cobData?.pixCopiaECola ?: ""
                    val qrCodeImageRaw = pixResponseData?.qrCodeImage ?: ""
                    
                    android.util.Log.d("PIX_RESPONSE", "txid: ${if (txid.isEmpty()) "VAZIO ❌" else "OK ✅"}")
                    android.util.Log.d("PIX_RESPONSE", "pixCopiaCola: ${if (pixCopiaCola.isEmpty()) "VAZIO ❌" else "OK ✅ (${pixCopiaCola.length} chars)"}")
                    android.util.Log.d("PIX_RESPONSE", "qrCodeImageRaw: ${if (qrCodeImageRaw.isEmpty()) "VAZIO ❌" else "OK ✅ (${qrCodeImageRaw.length} chars)"}")
                    android.util.Log.d("PIX_RESPONSE", "resultado.isSuccess: ${resultado.isSuccess}")
                    
                    // Remover prefixo "data:image/png;base64," se existir
                    val qrCodeImage = if (qrCodeImageRaw.contains("base64,")) {
                        qrCodeImageRaw.substringAfter("base64,")
                    } else {
                        qrCodeImageRaw
                    }
                    
                    if (txid.isNotEmpty() && pixCopiaCola.isNotEmpty() && resultado.isSuccess) {
                        val updates = mapOf<String, Any>(
                            "cod_transacao_pagamento" to txid,
                            "pix_copia_cola" to pixCopiaCola,
                            "qr_code_pedido" to qrCodeImage
                        )
                        pedidoRef.updateChildren(updates).await()
                        android.util.Log.d("PIX_RESPONSE", "✅ Dados PIX salvos no Firebase com sucesso!")
                        
                        // Criar notificação de pedido realizado
                        val notificationRepository = NotificationRepository()
                        val storeName = selectedStore?.name ?: "Lanchonete"
                        val notificationCreated = notificationRepository.createOrderPlacedNotification(
                            userId = userId,
                            orderId = pedidoId,
                            storeName = storeName
                        )
                        
                        if (notificationCreated) {
                            android.util.Log.d("PIX_RESPONSE", "✅ Notificação de pedido criada com sucesso!")
                        } else {
                            android.util.Log.e("PIX_RESPONSE", "❌ Falha ao criar notificação")
                        }
                    } else {
                        android.util.Log.e("PIX_RESPONSE", "❌ Dados PIX NÃO foram salvos! txid vazio: ${txid.isEmpty()}, pixCopiaCola vazio: ${pixCopiaCola.isEmpty()}, resultado.isSuccess: ${resultado.isSuccess}")
                    }
                    
                    // Salvar dados persistentes do PIX (já limpo, sem prefixo)
                    pixResponse = pixResponseData
                    pixQrCodeBase64 = qrCodeImage
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
            // Se tem selectedOrderDetail (pedido existente), usar o total dele
            // Senão, calcular do carrinho (pedido novo)
            val totalAmount = selectedOrderDetail?.total ?: cartItems.sumOf { it.price * it.quantity }
            
            // Log para debug
            Log.d("LineCutNavigation", "QR_CODE_PIX - totalAmount: $totalAmount")
            Log.d("LineCutNavigation", "QR_CODE_PIX - selectedOrderDetail?.total: ${selectedOrderDetail?.total}")
            Log.d("LineCutNavigation", "QR_CODE_PIX - cartItems total: ${cartItems.sumOf { it.price * it.quantity }}")
            
            // Priorizar dados do pedido do Firebase (selectedOrderDetail) sobre estados persistentes
            val qrCodeToDisplay = selectedOrderDetail?.qrCodeBase64 ?: pixQrCodeBase64
            val pixCopiaCola = selectedOrderDetail?.pixCopiaCola ?: pixResponse?.cobData?.pixCopiaECola
            
            // Log dos dados PIX
            Log.d("LineCutNavigation", "QR_CODE_PIX - qrCodeBase64 (selectedOrderDetail): ${if (selectedOrderDetail?.qrCodeBase64.isNullOrEmpty()) "VAZIO" else "${selectedOrderDetail?.qrCodeBase64?.length} chars"}")
            Log.d("LineCutNavigation", "QR_CODE_PIX - qrCodeBase64 (pixQrCodeBase64): ${if (pixQrCodeBase64.isNullOrEmpty()) "VAZIO" else "${pixQrCodeBase64?.length} chars"}")
            Log.d("LineCutNavigation", "QR_CODE_PIX - qrCodeToDisplay: ${if (qrCodeToDisplay.isNullOrEmpty()) "VAZIO" else "${qrCodeToDisplay.length} chars"}")
            Log.d("LineCutNavigation", "QR_CODE_PIX - pixCopiaCola (selectedOrderDetail): ${if (selectedOrderDetail?.pixCopiaCola.isNullOrEmpty()) "VAZIO" else "OK"}")
            Log.d("LineCutNavigation", "QR_CODE_PIX - pixCopiaCola (pixResponse): ${if (pixResponse?.cobData?.pixCopiaECola.isNullOrEmpty()) "VAZIO" else "OK"}")
            Log.d("LineCutNavigation", "QR_CODE_PIX - pixCopiaCola final: ${if (pixCopiaCola.isNullOrEmpty()) "VAZIO" else "OK"}")
            
            QRCodePixScreen(
                totalAmount = totalAmount,
                qrCodeBase64 = qrCodeToDisplay,
                pixCopiaCola = pixCopiaCola,
                onBackClick = {
                    currentScreen = Screen.PAYMENT_METHOD
                },
                onFinishPaymentClick = {
                    // Buscar pedido do Firebase e navegar para OrderDetailsScreen
                    coroutineScope.launch {
                        Log.d("LineCutNavigation", "==== FINALIZAR PAGAMENTO ====")
                        Log.d("LineCutNavigation", "currentPedidoId: $currentPedidoId")
                        Log.d("LineCutNavigation", "selectedOrderDetail?.orderId: ${selectedOrderDetail?.orderId}")
                        
                        // SEMPRE usar currentPedidoId quando vier de um novo pedido do carrinho
                        // Só usar selectedOrderDetail se NÃO tivermos currentPedidoId (pedido existente sendo reaberto)
                        if (currentPedidoId != null) {
                            // Pedido NOVO do carrinho - usar currentPedidoId
                            val pedidoId = currentPedidoId!!
                            Log.d("LineCutNavigation", "✅ Pedido NOVO do carrinho - buscando ID: $pedidoId")
                            
                            val orderDetail = orderRepository.getOrderById(pedidoId)
                            
                            if (orderDetail != null) {
                                Log.d("LineCutNavigation", "✅ Pedido encontrado, navegando para detalhes")
                                selectedOrderDetail = orderDetail
                                
                                // Clear cart e estados de PIX
                                cartItems = emptyList()
                                shoppingCart = emptyList()
                                pixResponse = null
                                pixQrCodeBase64 = null
                                pixTxid = null
                                currentPedidoId = null
                                
                                currentScreen = Screen.ORDER_DETAILS
                            } else {
                                Log.e("LineCutNavigation", "❌ Erro: Pedido não encontrado no Firebase para ID: $pedidoId")
                                // Fallback: limpar e voltar para home
                                cartItems = emptyList()
                                shoppingCart = emptyList()
                                currentPedidoId = null
                                pixResponse = null
                                pixQrCodeBase64 = null
                                pixTxid = null
                                currentScreen = Screen.STORES
                            }
                        } else if (selectedOrderDetail != null && selectedOrderDetail?.orderId != null) {
                            // Pedido EXISTENTE sendo reaberto (sem currentPedidoId)
                            Log.d("LineCutNavigation", "✅ Pedido EXISTENTE - usando selectedOrderDetail: ${selectedOrderDetail?.orderId}")
                            
                            // Clear cart e estados de PIX
                            cartItems = emptyList()
                            shoppingCart = emptyList()
                            pixResponse = null
                            pixQrCodeBase64 = null
                            pixTxid = null
                            
                            currentScreen = Screen.ORDER_DETAILS
                        } else {
                            // Nenhum pedido disponível
                            Log.w("LineCutNavigation", "⚠️ Nenhum pedido disponível - currentPedidoId e selectedOrderDetail são null")
                            
                            // Clear cart e estados
                            cartItems = emptyList()
                            shoppingCart = emptyList()
                            pixResponse = null
                            pixQrCodeBase64 = null
                            pixTxid = null
                            currentScreen = Screen.STORES
                        }
                    }
                },
                modifier = modifier
            )
        }
        
        Screen.PICKUP_QR -> {
            // Usar dados do pedido atual (selectedOrderDetail)
            selectedOrderDetail?.let { order ->
                PickupQRScreen(
                    orderNumber = order.orderId,
                    storeName = order.storeName,
                    storeType = order.storeType,
                    date = order.date,
                    status = order.status,
                    items = order.items,
                    total = order.total,
                    paymentMethod = order.paymentMethod,
                    imageRes = order.imageRes,
                    onBackClick = {
                        currentScreen = Screen.ORDER_DETAILS
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
                    currentScreen = Screen.FAVORITES
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
                    // Buscar a notificação para obter o orderId
                    coroutineScope.launch {
                        try {
                            val userId = authViewModel.currentUser.value?.uid
                            if (userId != null) {
                                val database = FirebaseDatabase.getInstance()
                                val notificationRef = database.getReference("notificacoes")
                                    .child(userId)
                                    .child(notificationId)
                                
                                val snapshot = notificationRef.get().await()
                                val orderId = snapshot.child("orderId").getValue(String::class.java)
                                
                                if (orderId != null) {
                                    Log.d("LineCutNavigation", "Notificação de avaliação clicada - OrderId: $orderId")
                                    
                                    // Buscar detalhes do pedido
                                    val orderDetail = orderRepository.getOrderById(orderId)
                                    
                                    if (orderDetail != null) {
                                        // Buscar avaliação existente
                                        val existingRating = orderRepository.getOrderRating(
                                            storeId = orderDetail.storeId,
                                            orderId = orderDetail.orderId
                                        )
                                        
                                        existingRatingForOrder = if (existingRating != null) {
                                            com.br.linecut.ui.screens.ExistingRating(
                                                qualityRating = existingRating.qualidade,
                                                speedRating = existingRating.velocidade,
                                                serviceRating = existingRating.atendimento
                                            )
                                        } else null
                                        
                                        selectedOrderForRating = com.br.linecut.ui.screens.Order(
                                            id = orderDetail.orderId,
                                            orderNumber = orderDetail.orderId,
                                            date = orderDetail.date,
                                            storeName = orderDetail.storeName,
                                            storeCategory = orderDetail.storeType,
                                            status = if (orderDetail.statusPedido == "retirado" || orderDetail.statusPedido == "entregue") 
                                                com.br.linecut.ui.screens.OrderStatus.COMPLETED 
                                            else 
                                                com.br.linecut.ui.screens.OrderStatus.IN_PROGRESS,
                                            total = orderDetail.total,
                                            rating = orderDetail.rating?.toFloat(),
                                            storeId = orderDetail.storeId
                                        )
                                        
                                        currentScreen = Screen.RATE_ORDER
                                        Log.d("LineCutNavigation", "Navegando para tela de avaliação")
                                    } else {
                                        Log.e("LineCutNavigation", "Pedido não encontrado: $orderId")
                                    }
                                } else {
                                    Log.e("LineCutNavigation", "OrderId não encontrado na notificação")
                                }
                            }
                        } catch (e: Exception) {
                            Log.e("LineCutNavigation", "Erro ao navegar para avaliação", e)
                        }
                    }
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
        
        Screen.FAVORITES -> {
            FavoritesScreen(
                onBackClick = {
                    currentScreen = Screen.PROFILE
                },
                onStoreClick = { store ->
                    selectedStore = store
                    currentScreen = Screen.STORE_DETAIL
                },
                onHomeClick = {
                    showSearchBarOnStores = false
                    currentScreen = Screen.STORES
                },
                onSearchClick = {
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
                    Log.d("RateOrderScreen", "==== NAVEGAÇÃO: onOrderClick CHAMADO ====")
                    Log.d("RateOrderScreen", "Order recebido - ID: ${order.id}")
                    Log.d("RateOrderScreen", "Order recebido - Number: ${order.orderNumber}")
                    Log.d("RateOrderScreen", "Order recebido - Store: ${order.storeName}")
                    Log.d("RateOrderScreen", "Order recebido - StoreId: ${order.storeId}")
                    
                    // Buscar dados reais do pedido no Firebase
                    coroutineScope.launch {
                        val orderId = order.id.removePrefix("#")
                        Log.d("RateOrderScreen", "Buscando pedido do Firebase - OrderId sem #: $orderId")
                        Log.d("LineCutNavigation", "Buscando pedido do Firebase ao clicar: $orderId")
                        
                        val firebaseOrder = orderRepository.getOrderById(orderId)
                        
                        if (firebaseOrder != null) {
                            Log.d("RateOrderScreen", "✅ PEDIDO ENCONTRADO NO FIREBASE")
                            Log.d("RateOrderScreen", "  - OrderId: ${firebaseOrder.orderId}")
                            Log.d("RateOrderScreen", "  - StoreName: ${firebaseOrder.storeName}")
                            Log.d("RateOrderScreen", "  - StoreId: ${firebaseOrder.storeId}")
                            Log.d("RateOrderScreen", "  - Status Pagamento: ${firebaseOrder.statusPagamento}")
                            Log.d("RateOrderScreen", "  - Status Pedido: ${firebaseOrder.statusPedido}")
                            
                            Log.d("LineCutNavigation", "✅ Pedido encontrado - statusPagamento: ${firebaseOrder.statusPagamento}")
                            Log.d("LineCutNavigation", "QR Code Base64: ${if (firebaseOrder.qrCodeBase64.isNullOrEmpty()) "VAZIO ❌" else "OK ✅ (${firebaseOrder.qrCodeBase64.length} chars)"}")
                            Log.d("LineCutNavigation", "PIX Copia e Cola: ${if (firebaseOrder.pixCopiaCola.isNullOrEmpty()) "VAZIO ❌" else "OK ✅ (${firebaseOrder.pixCopiaCola.length} chars)"}")
                            
                            selectedOrderDetail = firebaseOrder
                            
                            Log.d("RateOrderScreen", "Navegando para Screen.ORDER_DETAILS")
                            currentScreen = Screen.ORDER_DETAILS
                        } else {
                            Log.e("RateOrderScreen", "❌ PEDIDO NÃO ENCONTRADO NO FIREBASE")
                            Log.e("RateOrderScreen", "  - OrderId buscado: $orderId")
                            Log.e("RateOrderScreen", "  - Função getOrderById retornou NULL")
                            
                            Log.e("LineCutNavigation", "❌ Pedido não encontrado no Firebase - ID: $orderId")
                            // TODO: Mostrar mensagem de erro para o usuário
                            // Por enquanto, ficar na tela de pedidos
                        }
                    }
                },
                onRateOrderClick = { order ->
                    coroutineScope.launch {
                        Log.d("LineCutNavigation", "==== BUSCANDO AVALIAÇÃO (Orders Screen) ====")
                        Log.d("LineCutNavigation", "StoreId: ${order.storeId}")
                        Log.d("LineCutNavigation", "OrderId: ${order.id}")
                        
                        // Buscar avaliação existente do Firebase
                        val existingRating = orderRepository.getOrderRating(
                            storeId = order.storeId,
                            orderId = order.id
                        )
                        
                        Log.d("LineCutNavigation", "Avaliação encontrada: ${existingRating != null}")
                        if (existingRating != null) {
                            Log.d("LineCutNavigation", "  - Qualidade: ${existingRating.qualidade}")
                            Log.d("LineCutNavigation", "  - Velocidade: ${existingRating.velocidade}")
                            Log.d("LineCutNavigation", "  - Atendimento: ${existingRating.atendimento}")
                        }
                        
                        existingRatingForOrder = if (existingRating != null) {
                            com.br.linecut.ui.screens.ExistingRating(
                                qualityRating = existingRating.qualidade,
                                speedRating = existingRating.velocidade,
                                serviceRating = existingRating.atendimento
                            )
                        } else null
                        
                        Log.d("LineCutNavigation", "ExistingRatingForOrder: $existingRatingForOrder")
                        
                        selectedOrderForRating = order
                        currentScreen = Screen.RATE_ORDER
                    }
                }
            )
        }
        
        Screen.ORDER_DETAILS -> {
            selectedOrderDetail?.let { initialOrderDetail ->
                // Estado para o pedido atualizado em tempo real
                var currentOrder by remember(initialOrderDetail.orderId) { 
                    mutableStateOf(initialOrderDetail) 
                }
                
                // Estado para rastrear status anterior (para detectar mudanças)
                var previousStatusPedido by remember(initialOrderDetail.orderId) {
                    mutableStateOf(initialOrderDetail.statusPedido)
                }
                
                // Listener para monitorar mudanças no pedido
                DisposableEffect(initialOrderDetail.orderId) {
                    val orderId = initialOrderDetail.orderId.removePrefix("#")
                    val database = FirebaseDatabase.getInstance()
                    val pedidoRef = database.getReference("pedidos").child(orderId)
                    
                    val listener = object : com.google.firebase.database.ValueEventListener {
                        override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {
                            try {
                                val statusPagamento = snapshot.child("status_pagamento").getValue(String::class.java) ?: "pendente"
                                val statusPedido = snapshot.child("status_pedido").getValue(String::class.java) ?: "pendente"
                                val idLanchonete = snapshot.child("id_lanchonete").getValue(String::class.java) ?: ""
                                
                                Log.d("LineCutNavigation", "Firebase atualizado - status_pagamento: $statusPagamento, status_pedido: $statusPedido")
                                
                                // Mapear status_pedido do Firebase para status da UI
                                val statusUI = when (statusPedido.lowercase()) {
                                    "pendente", "em_preparo" -> "Em preparo"
                                    "pronto" -> "Pronto para retirada"
                                    "entregue" -> "Pedido concluído"
                                    "cancelado" -> "Pedido cancelado"
                                    else -> "Em preparo"
                                }
                                
                                // Detectar mudança de status e criar notificação
                                if (statusPedido != previousStatusPedido) {
                                    Log.d("LineCutNavigation", "🔔 Status mudou de $previousStatusPedido para $statusPedido")
                                    
                                    // Buscar nome da lanchonete do Firebase
                                    coroutineScope.launch {
                                        try {
                                            val companiesRef = database.getReference("empresas").child(idLanchonete)
                                            val companySnapshot = companiesRef.get().await()
                                            val storeName = companySnapshot.child("nome_lanchonete").getValue(String::class.java) 
                                                ?: currentOrder.storeName
                                            
                                            Log.d("LineCutNavigation", "Nome da lanchonete: $storeName")
                                            
                                            // Obter userId
                                            val userId = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid
                                            
                                            if (userId != null) {
                                                val notificationRepository = NotificationRepository()
                                                
                                                // Criar notificação baseada no novo status
                                                when (statusPedido.lowercase()) {
                                                    "em_preparo" -> {
                                                        notificationRepository.createOrderPreparingNotification(
                                                            userId = userId,
                                                            orderId = orderId,
                                                            storeName = storeName
                                                        )
                                                        Log.d("LineCutNavigation", "✅ Notificação 'em preparo' criada")
                                                    }
                                                    "pronto" -> {
                                                        notificationRepository.createOrderReadyNotification(
                                                            userId = userId,
                                                            orderId = orderId,
                                                            storeName = storeName
                                                        )
                                                        Log.d("LineCutNavigation", "✅ Notificação 'pronto' criada")
                                                    }
                                                    "retirado", "entregue" -> {
                                                        notificationRepository.createOrderPickedUpNotification(
                                                            userId = userId,
                                                            orderId = orderId,
                                                            storeName = storeName
                                                        )
                                                        // Também criar notificação de avaliação
                                                        notificationRepository.createRatingNotification(
                                                            userId = userId,
                                                            orderId = orderId,
                                                            storeName = storeName
                                                        )
                                                        Log.d("LineCutNavigation", "✅ Notificações 'retirado' e 'avaliação' criadas")
                                                    }
                                                }
                                            }
                                        } catch (e: Exception) {
                                            Log.e("LineCutNavigation", "Erro ao criar notificação", e)
                                        }
                                    }
                                    
                                    // Atualizar status anterior
                                    previousStatusPedido = statusPedido
                                }
                                
                                // Atualizar o estado do pedido para forçar recomposição
                                if (statusPagamento != currentOrder.statusPagamento || statusPedido != currentOrder.statusPedido) {
                                    currentOrder = currentOrder.copy(
                                        statusPagamento = statusPagamento,
                                        paymentStatus = if (statusPagamento == "pago") "aprovado" else "pendente",
                                        status = statusUI,
                                        statusPedido = statusPedido
                                    )
                                    Log.d("LineCutNavigation", "✅ UI atualizada - statusPagamento: $statusPagamento, statusPedido: $statusPedido, statusUI: $statusUI")
                                }
                            } catch (e: Exception) {
                                Log.e("LineCutNavigation", "Erro ao processar atualização: ${e.message}", e)
                            }
                        }
                        
                        override fun onCancelled(error: com.google.firebase.database.DatabaseError) {
                            Log.e("LineCutNavigation", "Erro ao monitorar pedido: ${error.message}")
                        }
                    }
                    
                    pedidoRef.addValueEventListener(listener)
                    Log.d("LineCutNavigation", "Listener registrado para pedido: $orderId")
                    
                    // Cleanup: remover listener quando sair da tela
                    onDispose {
                        pedidoRef.removeEventListener(listener)
                        Log.d("LineCutNavigation", "Listener removido para pedido: $orderId")
                    }
                }
                
                OrderDetailsScreen(
                    order = currentOrder,
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
                        coroutineScope.launch {
                            Log.d("LineCutNavigation", "==== BUSCANDO AVALIAÇÃO ====")
                            Log.d("LineCutNavigation", "StoreId: ${currentOrder.storeId}")
                            Log.d("LineCutNavigation", "OrderId: ${currentOrder.orderId}")
                            
                            // Buscar avaliação existente do Firebase
                            val existingRating = orderRepository.getOrderRating(
                                storeId = currentOrder.storeId,
                                orderId = currentOrder.orderId
                            )
                            
                            Log.d("LineCutNavigation", "Avaliação encontrada: ${existingRating != null}")
                            if (existingRating != null) {
                                Log.d("LineCutNavigation", "  - Qualidade: ${existingRating.qualidade}")
                                Log.d("LineCutNavigation", "  - Velocidade: ${existingRating.velocidade}")
                                Log.d("LineCutNavigation", "  - Atendimento: ${existingRating.atendimento}")
                            }
                            
                            existingRatingForOrder = if (existingRating != null) {
                                com.br.linecut.ui.screens.ExistingRating(
                                    qualityRating = existingRating.qualidade,
                                    speedRating = existingRating.velocidade,
                                    serviceRating = existingRating.atendimento
                                )
                            } else null
                            
                            Log.d("LineCutNavigation", "ExistingRatingForOrder: $existingRatingForOrder")
                            
                            selectedOrderForRating = com.br.linecut.ui.screens.Order(
                                id = currentOrder.orderId,
                                orderNumber = currentOrder.orderId,
                                date = currentOrder.date,
                                storeName = currentOrder.storeName,
                                storeCategory = currentOrder.storeType,
                                status = if (currentOrder.statusPedido == "retirado" || currentOrder.statusPedido == "entregue") 
                                    com.br.linecut.ui.screens.OrderStatus.COMPLETED 
                                else 
                                    com.br.linecut.ui.screens.OrderStatus.IN_PROGRESS,
                                total = currentOrder.total,
                                rating = currentOrder.rating?.toFloat(),
                                storeId = currentOrder.storeId
                            )
                            
                            currentScreen = Screen.RATE_ORDER
                        }
                    },
                    onAddToCartClick = {
                        // TODO: Add items to cart and navigate
                        currentScreen = Screen.CART
                    },
                    onCompletePaymentClick = {
                        // Buscar pedido atualizado do Firebase antes de navegar
                        coroutineScope.launch {
                            val orderId = currentOrder.orderId.removePrefix("#")
                            Log.d("LineCutNavigation", "Buscando pedido do Firebase: $orderId")
                            
                            val updatedOrder = orderRepository.getOrderById(orderId)
                            
                            if (updatedOrder != null) {
                                Log.d("LineCutNavigation", "✅ Pedido encontrado, navegando para QR Code PIX")
                                selectedOrderDetail = updatedOrder
                                
                                // Passar os dados do PIX do pedido atualizado diretamente
                                pixQrCodeBase64 = updatedOrder.qrCodeBase64
                                
                                // Limpar pixResponse pois vamos usar os dados do pedido
                                pixResponse = null
                                
                                Log.d("LineCutNavigation", "QR Code do pedido: ${if (updatedOrder.qrCodeBase64.isNullOrEmpty()) "VAZIO" else "${updatedOrder.qrCodeBase64.length} chars"}")
                                Log.d("LineCutNavigation", "PIX Copia Cola: ${if (updatedOrder.pixCopiaCola.isNullOrEmpty()) "VAZIO" else "OK"}")
                                
                                currentScreen = Screen.QR_CODE_PIX
                            } else {
                                Log.e("LineCutNavigation", "❌ Erro: Pedido não encontrado ou não pertence ao usuário")
                                // Mostrar mensagem de erro ou manter na tela atual
                            }
                        }
                    },
                    onViewPickupCodeClick = {
                        // Navegar para tela de código de retirada
                        currentScreen = Screen.PICKUP_QR
                    },
                    modifier = modifier
                )
            }
        }
        
        Screen.RATE_ORDER -> {
            selectedOrderForRating?.let { order ->
                com.br.linecut.ui.screens.RateOrderScreen(
                    order = com.br.linecut.ui.screens.OrderRatingData(
                        orderId = order.id,
                        storeId = order.storeId,
                        orderNumber = order.orderNumber,
                        storeName = order.storeName,
                        storeCategory = order.storeCategory,
                        date = order.date,
                        storeImageUrl = order.storeImageUrl,
                        totalPrice = order.total,
                        existingRating = existingRatingForOrder
                    ),
                    onBackClick = {
                        currentScreen = Screen.ORDERS
                    },
                    onHomeClick = {
                        showSearchBarOnStores = false
                        currentScreen = Screen.STORES
                    },
                    onSearchClick = {
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
                    onSubmitRating = { qualityRating, speedRating, serviceRating ->
                        coroutineScope.launch {
                            try {
                                Log.d("LineCutNavigation", "Salvando avaliação - StoreId: ${order.storeId}, OrderId: ${order.id}")
                                
                                // Usar o OrderRepository para salvar a avaliação
                                val result = orderRepository.saveOrderRating(
                                    storeId = order.storeId,
                                    orderId = order.id,
                                    qualityRating = qualityRating,
                                    speedRating = speedRating,
                                    serviceRating = serviceRating
                                )
                                
                                result.onSuccess {
                                    Log.d("LineCutNavigation", "✅ Avaliação salva com sucesso!")
                                    // NÃO navegar automaticamente - usuário permanece na tela
                                }.onFailure { error ->
                                    Log.e("LineCutNavigation", "❌ Erro ao salvar avaliação: ${error.message}", error)
                                    // TODO: Mostrar mensagem de erro para o usuário
                                }
                            } catch (e: Exception) {
                                Log.e("LineCutNavigation", "❌ Exceção ao salvar avaliação: ${e.message}", e)
                            }
                        }
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
    rating = 5f,
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
