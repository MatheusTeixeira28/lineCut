# LineCut Design System

## 📱 Visão Geral

Este documento descreve o design system criado para o aplicativo LineCut, baseado no design do Figma. O sistema garante consistência visual em todas as telas e facilita a manutenção e expansão do aplicativo.

## 🎨 Paleta de Cores

### Cores Principais
- **LineCutRed**: `#9C0202` - Cor principal da marca
- **LineCutRedLight**: `#B01F1F` - Variação mais clara
- **LineCutRedDark**: `#7A0101` - Variação mais escura

### Cores de Texto
- **TextPrimary**: `#515050` - Texto principal
- **TextSecondary**: `#7D7D7D` - Texto secundário/subtítulos
- **TextPlaceholder**: `#7D7D7D` - Placeholders em campos de entrada

### Cores de Fundo
- **BackgroundPrimary**: `#FFFFFF` - Fundo principal
- **BackgroundSecondary**: `#F5F5F5` - Fundo secundário

### Cores de Borda
- **BorderLight**: `#D1D1D1` - Bordas sutis
- **BorderMedium**: `#B0B0B0` - Bordas de destaque

## 🔤 Tipografia

O sistema utiliza uma hierarquia tipográfica baseada no Material Design 3, adaptada para o LineCut:

- **Display Large**: 32sp, SemiBold - Títulos principais
- **Display Medium**: 24sp, SemiBold - Títulos de seção
- **Headline Large**: 20sp, SemiBold - Subtítulos importantes
- **Body Medium**: 14sp, Regular - Texto principal
- **Label Medium**: 12sp, Medium - Labels e links

## 🧩 Componentes

### LineCutTextField
Campo de entrada customizado com:
- Bordas arredondadas (100dp)
- Ícones de entrada e saída
- Suporte a senhas com toggle de visibilidade
- Estados de placeholder e foco

```kotlin
LineCutTextField(
    value = email,
    onValueChange = { email = it },
    placeholder = "Email",
    leadingIcon = Icons.Outlined.Email,
    keyboardType = KeyboardType.Email
)
```

### LineCutPrimaryButton
Botão principal com:
- Fundo vermelho da marca
- Texto branco
- Bordas arredondadas (100dp)
- Estados enabled/disabled

```kotlin
LineCutPrimaryButton(
    text = "Entrar",
    onClick = { /* ação */ },
    enabled = true
)
```

### LineCutSecondaryButton
Botão secundário com:
- Borda vermelha da marca
- Texto vermelho
- Fundo transparente

### Componentes de Texto
- **LineCutTitle**: Títulos de tela
- **LineCutSubtitle**: Subtítulos
- **LineCutBodyText**: Texto do corpo
- **LineCutLinkText**: Links clicáveis
- **LineCutLogo**: Logo da aplicação

### Espaçamento
Sistema de espaçamento consistente:
- **XSmall**: 4dp
- **Small**: 8dp
- **Medium**: 16dp
- **Large**: 24dp
- **XLarge**: 32dp
- **XXLarge**: 48dp

```kotlin
LineCutSpacer(LineCutSpacing.Large)
```

## 📁 Estrutura dos Arquivos

```
ui/
├── theme/
│   ├── Color.kt          # Definições de cores
│   ├── Type.kt           # Tipografia
│   └── Theme.kt          # Tema principal
├── components/
│   ├── LineCutTextField.kt      # Campo de entrada
│   ├── LineCutButton.kt         # Botões
│   └── LineCutDesignSystem.kt   # Componentes gerais
└── screens/
    └── LoginScreen.kt    # Tela de login
```

## 🚀 Uso

### 1. Aplicar o Tema
```kotlin
LineCutTheme {
    // Conteúdo da aplicação
}
```

### 2. Usar Componentes
```kotlin
@Composable
fun ExampleScreen() {
    Column {
        LineCutTitle("Título da Tela")
        LineCutSpacer(LineCutSpacing.Medium)
        
        LineCutTextField(
            value = value,
            onValueChange = { value = it },
            placeholder = "Digite aqui"
        )
        
        LineCutSpacer(LineCutSpacing.Large)
        
        LineCutPrimaryButton(
            text = "Confirmar",
            onClick = { /* ação */ }
        )
    }
}
```

## 📝 Telas Implementadas

### 1. Login Screen
- Logo centralizado
- Campos de email e senha
- Botão de entrada
- Links para "Esqueceu senha" e "Cadastre-se"
- Layout responsivo com scroll

### 2. Sign Up Screen (Cadastro)
- Logo centralizado
- Campos: Nome completo, CPF, Telefone, Email, Senha, Confirmar senha
- Checkboxes para Termos e Condições e Política de Privacidade
- Botão de cadastro com validação completa
- Link para "Já possuo conta"
- Layout responsivo com scroll

### 3. Forgot Password Screen (Esqueci minha senha)
- Logo centralizado
- Card elevado com sombra
- Campo de email para recuperação
- Botão "Enviar" com validação
- Link "Cancelar" para voltar ao login
- Design minimalista e focado

### 4. Email Sent Screen (Confirmação de envio)
- Logo centralizado
- Card elevado com sombra
- Botão "Voltar" no canto superior esquerdo
- Mensagem de confirmação com email destacado em vermelho
- Botão "Reenviar e-mail" funcional
- Design consistente com a tela anterior

### 🔗 Navegação Implementada
- **Login** ↔ **Cadastro**: Links bidirecionais
- **Login** → **Esqueci minha senha**: Funcional
- **Esqueci minha senha** → **Confirmação de envio**: Após enviar email
- **Confirmação de envio** → **Esqueci minha senha**: Botão voltar
- **Confirmação de envio**: Reenviar email (permanece na tela)
- **Cadastro** → **Login**: Após cadastro bem-sucedido

## 💡 Próximos Passos

Para implementar novas telas:

1. **Use os componentes existentes** sempre que possível
2. **Mantenha a consistência** de cores e espaçamento
3. **Documente novos componentes** se necessário criar
4. **Teste em diferentes tamanhos** de tela

## 🔧 Manutenção

- Todas as cores estão centralizadas em `Color.kt`
- Tipografia está definida em `Type.kt`
- Componentes reutilizáveis em `components/`
- Use o preview para testar mudanças rapidamente

---

**Nota**: Este design system foi criado baseado na tela de login do Figma e deve ser expandido conforme novas telas forem adicionadas, mantendo sempre a consistência visual estabelecida.
