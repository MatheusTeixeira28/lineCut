# Sistema de Monitoramento de Pedidos em Background

## 📋 Visão Geral

O **OrderStatusService** é um Foreground Service que monitora mudanças de status dos pedidos do usuário em tempo real, mesmo quando o app está em segundo plano ou minimizado.

## 🎯 Funcionalidades

- ✅ Monitora todos os pedidos ativos do usuário no Firebase
- ✅ Cria notificações automáticas quando o status do pedido muda
- ✅ Continua funcionando em background (mesmo com app fechado)
- ✅ Reinicia automaticamente se o sistema matar o processo
- ✅ Gerenciamento inteligente de recursos

## 🏗️ Arquitetura

### Componentes

1. **OrderStatusService.kt** - Service principal
   - Monitora mudanças nos pedidos via Firebase Realtime Database
   - Rastreia status anterior de cada pedido
   - Cria notificações quando status muda

2. **ServiceManager.kt** - Gerenciador do service
   - `startOrderMonitoringIfLoggedIn()` - Inicia service se usuário autenticado
   - `stopOrderMonitoring()` - Para o service
   - `isServiceRunning()` - Verifica se está rodando
   - `restartServiceIfNeeded()` - Reinicia se necessário

3. **AndroidManifest.xml** - Configuração
   - Permissões: `FOREGROUND_SERVICE`, `POST_NOTIFICATIONS`
   - Registro do service

## 🔄 Fluxo de Funcionamento

### 1. Inicialização

```
App Inicia (MainActivity.onCreate)
    ↓
ServiceManager.startOrderMonitoringIfLoggedIn()
    ↓
Verifica se usuário está logado (FirebaseAuth)
    ↓
Se SIM → Inicia OrderStatusService
Se NÃO → Não faz nada
```

### 2. Login do Usuário

```
Usuário faz login (LoginScreen)
    ↓
onLoginSuccess callback
    ↓
ServiceManager.startOrderMonitoringIfLoggedIn()
    ↓
OrderStatusService iniciado
```

### 3. Monitoramento em Background

```
OrderStatusService rodando
    ↓
Firebase Listener ativo em pedidos/{userId}
    ↓
Status do pedido muda no Firebase
    ↓
Listener detecta mudança
    ↓
Compara com status anterior armazenado
    ↓
Se diferente → Busca nome da lanchonete
    ↓
Cria notificação apropriada
```

## 📱 Tipos de Notificação

| Status do Pedido | Notificação Criada |
|-----------------|-------------------|
| `em_preparo` | "Pedido em preparo" |
| `pronto` | "Pronto para retirada" |
| `retirado` / `entregue` | "Pedido retirado" + "Avalie seu pedido" |

## 🔧 Como Usar

### Iniciar o Service

```kotlin
// Automático ao fazer login
ServiceManager.startOrderMonitoringIfLoggedIn(context)

// Manual (se necessário)
OrderStatusService.start(context)
```

### Parar o Service

```kotlin
// Ao fazer logout
ServiceManager.stopOrderMonitoring(context)

// Manual
OrderStatusService.stop(context)
```

### Verificar Status

```kotlin
if (ServiceManager.isServiceRunning(context)) {
    Log.d("Service", "Service está rodando")
}
```

## 📝 Logs Importantes

O service gera logs com tag `OrderStatusService`:

```
D/OrderStatusService: Service criado
D/OrderStatusService: Service iniciado
D/OrderStatusService: Iniciando monitoramento de pedidos para userId: xxx
D/OrderStatusService: Pedidos atualizados: 2 pedidos encontrados
D/OrderStatusService: Status do pedido abc123 mudou de pendente para em_preparo
D/OrderStatusService: Criando notificação para status: em_preparo, loja: Burger Queen
D/OrderStatusService: Notificação criada com sucesso
```

## ⚙️ Configurações

### AndroidManifest.xml

```xml
<!-- Permissões -->
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />

<!-- Service -->
<service
    android:name=".service.OrderStatusService"
    android:enabled="true"
    android:exported="false"
    android:foregroundServiceType="dataSync" />
```

### Notificação de Foreground

- **Canal**: `order_status_channel`
- **Título**: "LineCut"
- **Texto**: "Monitorando seus pedidos"
- **Prioridade**: LOW (não interrompe usuário)
- **Ongoing**: true (não pode ser descartada por swipe)

## 🔒 Segurança

- ✅ Service não é exportado (`android:exported="false"`)
- ✅ Verifica autenticação antes de iniciar
- ✅ Usa regras de segurança do Firebase
- ✅ Limpa listeners ao destruir service

## 🚀 Performance

- **Uso de memória**: Mínimo (apenas listeners Firebase)
- **Uso de CPU**: Baixo (apenas quando há mudanças)
- **Uso de bateria**: Otimizado (listeners Firebase eficientes)
- **Rede**: Apenas para sincronização Firebase

## ⚠️ Limitações Android

### Android 8+ (Oreo)
- Requer notificação de foreground obrigatória
- Service pode ser morto após alguns minutos em background
- Solução: `START_STICKY` reinicia automaticamente

### Android 12+ (S)
- Restrições mais rígidas para services
- Pode exigir permissão de "Apps que podem rodar em background"

### Android 13+ (Tiramisu)
- Requer permissão `POST_NOTIFICATIONS` em runtime
- Usuário deve conceder permissão explicitamente

## 🐛 Troubleshooting

### Service não inicia
1. Verificar se usuário está autenticado
2. Verificar logs do ServiceManager
3. Verificar permissões no manifest

### Notificações não aparecem
1. Verificar permissão `POST_NOTIFICATIONS`
2. Verificar canal de notificação criado
3. Verificar configurações de notificação do app

### Service é morto pelo sistema
1. Normal em background prolongado
2. Service reinicia automaticamente (`START_STICKY`)
3. Usuário pode adicionar app à lista branca de economia de bateria

## 📊 Monitoramento

### Verificar se service está ativo

```bash
adb shell dumpsys activity services | grep OrderStatusService
```

### Ver logs em tempo real

```bash
adb logcat -s OrderStatusService ServiceManager
```

## 🔮 Futuras Melhorias

- [ ] WorkManager para garantir execução mais confiável
- [ ] Sincronização local para offline
- [ ] Notificações push via Firebase Cloud Messaging
- [ ] Histórico de notificações perdidas
- [ ] Analytics de performance do service
