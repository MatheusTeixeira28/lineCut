package com.br.linecut.ui.screens

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.br.linecut.R
import com.br.linecut.ui.components.LineCutBottomNavigationBar
import com.br.linecut.ui.components.LineCutDesignSystem
import com.br.linecut.ui.theme.LineCutRed
import com.br.linecut.ui.theme.LineCutTheme
import com.br.linecut.ui.theme.TextSecondary
import com.br.linecut.ui.viewmodel.AuthViewModel
import java.io.ByteArrayOutputStream

@Composable
fun AccountDataScreen(
    onBackClick: () -> Unit = {},
    onEditClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onOrdersClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel = viewModel()
) {
     //Estados dos dados do usuário do Firebase
    val currentUser by authViewModel.currentUser.collectAsState()
    
    // Estados para controle de edição
    var isEditing by remember { mutableStateOf(false) }
    var editedName by remember { mutableStateOf("") }
    var editedPhone by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var selectedImageUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    var selectedImageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var showImageError by remember { mutableStateOf(false) }
    var imageErrorMessage by remember { mutableStateOf("") }
    
    val context = LocalContext.current
    
    // Launcher para seleção de imagem
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val validationResult = validateImage(context, it)
            if (validationResult.isValid) {
                selectedImageUri = it
                showImageError = false
            } else {
                showImageError = true
                imageErrorMessage = validationResult.errorMessage
            }
        }
    }
    
    // Carregar bitmap da imagem selecionada
    LaunchedEffect(selectedImageUri) {
        val uri = selectedImageUri
        if (uri != null) {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                selectedImageBitmap = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()
            } catch (e: Exception) {
                selectedImageBitmap = null
                showImageError = true
                imageErrorMessage = "Erro ao carregar imagem"
            }
        } else {
            selectedImageBitmap = null
        }
    }
    
     //Carregar dados do usuário quando a tela for aberta
    LaunchedEffect(Unit) {
        authViewModel.loadCurrentUser()
    }
    
    // Atualizar campos editáveis quando os dados do usuário mudarem
    LaunchedEffect(currentUser) {
        currentUser?.let { user ->
            editedName = user.fullName ?: ""
            editedPhone = user.phone ?: ""
        }
    }
    
     //Formatação dos dados para exibição
    val formattedCpf = currentUser?.cpf?.let { cpf ->
        val digits = cpf.filter { it.isDigit() }
        when {
            digits.length <= 3 -> digits
            digits.length <= 6 -> "${digits.substring(0, 3)}.${digits.substring(3)}"
            digits.length <= 9 -> "${digits.substring(0, 3)}.${digits.substring(3, 6)}.${digits.substring(6)}"
            else -> "${digits.substring(0, 3)}.${digits.substring(3, 6)}.${digits.substring(6, 9)}-${digits.substring(9)}"
        }
    } ?: ""

    val formattedPhone = currentUser?.phone?.let { phone ->
        val digits = phone.filter { it.isDigit() }
        when {
            digits.isEmpty() -> ""
            digits.length == 1 -> "(${digits}"
            digits.length <= 2 -> "(${digits})"
            digits.length <= 7 -> "(${digits.substring(0, 2)}) ${digits.substring(2)}"
            else -> "(${digits.substring(0, 2)}) ${digits.substring(2, 7)}-${digits.substring(7)}"
        }
    } ?: ""

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(LineCutDesignSystem.screenBackgroundColor)
    ) {
        // Header com fundo arredondado - mesmas proporções da QRCodePixScreen
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(126.dp)
                .background(
                    LineCutDesignSystem.screenBackgroundColor,
                    shape = RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp)
                )
                .shadow(
                    elevation = 4.dp,
                    shape = RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp),
                    ambientColor = Color.Black.copy(alpha = 0.25f),
                    spotColor = Color.Black.copy(alpha = 0.25f)
                )
        ) {
            // Linha inferior do header com voltar + título
            Row(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 34.dp, end = 34.dp, bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.size(20.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_filter_arrow),
                        contentDescription = "Voltar",
                        tint = Color.Unspecified,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Meus dados",
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = LineCutRed,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                )
            }
        }

        // Espaço para centralizar a foto entre header e cards
        Spacer(modifier = Modifier.height(40.dp))

        // Container centralizado para foto e botão editar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp), // Altura suficiente para a foto e espaçamento
            contentAlignment = Alignment.Center
        ) {
            // Foto do usuário centralizada
            Box(
                modifier = Modifier
                    .size(135.dp)
                    .shadow(
                        elevation = 4.dp,
                        shape = CircleShape
                    )
                    .background(
                        color = Color(0xFFE8E8E8),
                        shape = CircleShape
                    )
                    .clip(CircleShape)
                    .clickable(enabled = isEditing) {
                        if (isEditing) {
                            imagePickerLauncher.launch("image/*")
                        }
                    }
            ) {
                // Exibir foto selecionada, foto do usuário ou placeholder
                when {
                    selectedImageBitmap != null -> {
                        Image(
                            bitmap = selectedImageBitmap!!.asImageBitmap(),
                            contentDescription = "Foto selecionada",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    !currentUser?.profileImageUrl.isNullOrEmpty() -> {
                        // Por enquanto usar imagem padrão para URLs do Firebase
                        // TODO: Implementar carregamento de URL quando necessário
                        Image(
                            painter = painterResource(id = R.drawable.icon_perfil),
                            contentDescription = "Foto do usuário",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    else -> {
                        Image(
                            painter = painterResource(id = R.drawable.icon_perfil),
                            contentDescription = "Foto padrão",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
                
                // Overlay para indicar que é clicável quando editando
                if (isEditing) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Tocar para\nalterar foto",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
            
            // Botão editar posicionado entre o canto da tela e a foto
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .offset(x = 140.dp, y = (-80).dp) // Movido ainda mais para o canto conforme a imagem
                    .shadow(
                        elevation = 4.dp,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .background(
                        color = LineCutDesignSystem.screenBackgroundColor,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .border(
                        BorderStroke(1.dp, LineCutRed),
                        shape = RoundedCornerShape(10.dp)
                    )
                    .clickable { 
                        if (isEditing) {
                            // Cancelar edição
                            isEditing = false
                            selectedImageUri = null
                            showImageError = false
                            // Restaurar valores originais
                            currentUser?.let { user ->
                                editedName = user.fullName ?: ""
                                editedPhone = user.phone ?: ""
                            }
                        } else {
                            // Iniciar edição
                            isEditing = true
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                if (isEditing) {
                    Text(
                        text = "✕",
                        color = LineCutRed,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.editar),
                        contentDescription = "Editar dados",
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
        
        // Espaço antes dos cards
        Spacer(modifier = Modifier.height(20.dp))

        // Seção de dados do usuário
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 45.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (currentUser == null) {
                // Estado de carregamento
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = LineCutRed,
                        modifier = Modifier.size(40.dp)
                    )
                }
            } else {
                // Campos de dados com dados do usuário
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(15.dp)
                ) {
                    if (isEditing) {
                        // Campos editáveis
                        // Nome completo
                        EditableDataField(
                            icon = Icons.Default.Person,
                            value = editedName,
                            onValueChange = { editedName = it },
                            placeholder = "Nome completo"
                        )

                        // CPF (não editável)
                        AccountDataField(
                            icon = Icons.Default.CreditCard,
                            value = formattedCpf,
                            placeholder = "CPF"
                        )

                        // Telefone
                        EditableDataField(
                            icon = Icons.Default.Phone,
                            value = editedPhone,
                            onValueChange = { editedPhone = it },
                            placeholder = "Telefone"
                        )

                        // Email (somente leitura)
                        AccountDataField(
                            icon = Icons.Default.Email,
                            value = currentUser?.email ?: "",
                            placeholder = "Email"
                        )
                        
                        Spacer(modifier = Modifier.height(20.dp))
                        
                        // Botões de salvar e cancelar
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Botão cancelar
                            TextButton(
                                onClick = {
                                    isEditing = false
                                    selectedImageUri = null
                                    showImageError = false
                                    // Restaurar valores originais
                                    currentUser?.let { user ->
                                        editedName = user.fullName ?: ""
                                        editedPhone = user.phone ?: ""
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = "Cancelar",
                                    color = TextSecondary
                                )
                            }
                            
                            // Botão salvar
                            Button(
                                onClick = {
                                    isLoading = true
                                    // Processar imagem se selecionada
                                    val imageByteArray = if (selectedImageUri != null) {
                                        processImage(context, selectedImageUri!!)
                                    } else {
                                        null
                                    }
                                    
                                    // Atualizar dados no Firebase (mantendo email atual)
                                    authViewModel.updateUserProfile(
                                        fullName = editedName,
                                        phone = editedPhone,
                                        email = currentUser?.email ?: "", // Usar email atual do usuário
                                        profileImage = imageByteArray
                                    ) { success ->
                                        isLoading = false
                                        if (success) {
                                            isEditing = false
                                            selectedImageUri = null
                                            showImageError = false
                                        }
                                    }
                                },
                                enabled = !isLoading && editedName.isNotBlank(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = LineCutRed
                                ),
                                modifier = Modifier.weight(1f)
                            ) {
                                if (isLoading) {
                                    CircularProgressIndicator(
                                        color = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                } else {
                                    Text(
                                        text = "Salvar",
                                        color = Color.White
                                    )
                                }
                            }
                        }
                        
                        // Mensagem de erro da imagem
                        if (showImageError) {
                            Text(
                                text = imageErrorMessage,
                                color = LineCutRed,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    } else {
                        // Campos somente leitura
                        // Nome completo
                        AccountDataField(
                            icon = Icons.Default.Person,
                            value = currentUser?.fullName ?: "",
                            placeholder = "Nome completo"
                        )

                        // CPF
                        AccountDataField(
                            icon = Icons.Default.CreditCard,
                            value = formattedCpf,
                            placeholder = "CPF"
                        )

                        // Telefone
                        AccountDataField(
                            icon = Icons.Default.Phone,
                            value = formattedPhone,
                            placeholder = "Telefone"
                        )

                        // Email
                        AccountDataField(
                            icon = Icons.Default.Email,
                            value = currentUser?.email ?: "",
                            placeholder = "Email"
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Bottom Navigation
        LineCutBottomNavigationBar(
            onHomeClick = onHomeClick,
            onSearchClick = onSearchClick,
            onNotificationClick = onNotificationClick,
            onOrdersClick = onOrdersClick,
            onProfileClick = onProfileClick
        )
    }
}

@Composable
private fun EditableDataField(
    icon: ImageVector,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(25.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFD1D1D1))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Ícone
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(20.dp)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Campo de texto editável
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                placeholder = {
                    Text(
                        text = placeholder,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color(0xFFBBBBBB),
                            fontSize = 14.sp
                        )
                    )
                },
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    color = TextSecondary,
                    fontSize = 14.sp
                ),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    cursorColor = LineCutRed,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                ),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun AccountDataField(
    icon: ImageVector,
    value: String,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(43.dp),
        shape = RoundedCornerShape(100.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFD1D1D1))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Ícone
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(20.dp)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Texto do campo
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = TextSecondary,
                    fontSize = 14.sp
                ),
                modifier = Modifier.weight(1f)
            )
        }
    }
}


// Previews
@Preview(
    name = "Account Data Screen",
    showBackground = true,
    showSystemUi = true
)
@Composable
fun AccountDataScreenPreview() {
    LineCutTheme {
        AccountDataScreen()
    }
}

@Preview(
    name = "Account Data Field",
    showBackground = true
)
@Composable
fun AccountDataFieldPreview() {
    LineCutTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AccountDataField(
                icon = Icons.Default.Person,
                value = "Hannah Montana",
                placeholder = "Nome completo"
            )
            
            AccountDataField(
                icon = Icons.Default.Email,
                value = "hannah.montana@gmail.com",
                placeholder = "Email"
            )
        }
    }
}

// Classe para resultado da validação de imagem
data class ImageValidationResult(
    val isValid: Boolean,
    val errorMessage: String = ""
)

// Função para validar a imagem selecionada
private fun validateImage(context: Context, uri: Uri): ImageValidationResult {
    return try {
        val contentResolver = context.contentResolver
        val inputStream = contentResolver.openInputStream(uri)
        
        // Verificar tamanho do arquivo (máximo 2MB)
        val fileSize = inputStream?.available() ?: 0
        if (fileSize > 2 * 1024 * 1024) { // 2MB
            return ImageValidationResult(false, "A imagem deve ter no máximo 2MB")
        }
        
        // Verificar tipo de arquivo
        val mimeType = contentResolver.getType(uri)
        if (mimeType != "image/jpeg" && mimeType != "image/png") {
            return ImageValidationResult(false, "Apenas imagens JPEG e PNG são permitidas")
        }
        
        inputStream?.close()
        ImageValidationResult(true)
    } catch (e: Exception) {
        ImageValidationResult(false, "Erro ao validar imagem: ${e.message}")
    }
}

// Função para processar e redimensionar a imagem
private fun processImage(context: Context, uri: Uri): ByteArray? {
    return try {
        val contentResolver = context.contentResolver
        val inputStream = contentResolver.openInputStream(uri)
        val originalBitmap = BitmapFactory.decodeStream(inputStream)
        inputStream?.close()
        
        // Redimensionar para 200x200px
        val resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, 200, 200, true)
        
        // Converter para ByteArray (JPEG com qualidade 85%)
        val outputStream = ByteArrayOutputStream()
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
        val byteArray = outputStream.toByteArray()
        outputStream.close()
        
        // Limpar bitmaps da memória
        originalBitmap.recycle()
        resizedBitmap.recycle()
        
        byteArray
    } catch (e: Exception) {
        null
    }
}
