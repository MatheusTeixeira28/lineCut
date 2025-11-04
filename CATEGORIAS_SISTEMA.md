# Sistema de Categorias de Produtos - LineCut

## Visão Geral
Sistema implementado para gerenciar categorias de produtos dinamicamente a partir do Firebase, permitindo que as categorias sejam atualizadas sem necessidade de alterar código.

## Estrutura do Firebase

### Tabela: `categorias_produto`
Localização: Raiz do Firebase Realtime Database

Estrutura real do Firebase (array de strings):
```json
{
  "categorias_produto": {
    "0": "Salgados",
    "1": "Lanches",
    "2": "Bebidas",
    "3": "Sobremesas",
    "4": "Doces",
    "5": "Acompanhamentos",
    "6": "Saudáveis",
    "7": "Laticínios",
    "8": "Ingredientes",
    "9": "Outros"
  }
}
```

**Nota:** O sistema converte automaticamente o nome da categoria para um ID normalizado:
- "Salgados" → `salgados`
- "Lanches" → `lanches`
- "Bebidas" → `bebidas`
- "Acompanhamentos" → `acompanhamentos`
- "Saudáveis" → `saudaveis` (remove acentos)
- "Laticínios" → `laticinios` (remove acentos)

### Tabela: `restaurants/{restaurant_id}/products`
Cada produto deve ter o campo `category` que corresponde ao **ID normalizado** da categoria:

```json
{
  "restaurants": {
    "restaurant_id_1": {
      "products": {
        "produto_id_1": {
          "id": "produto_id_1",
          "name": "X-Burger",
          "description": "Hambúrguer clássico",
          "price": 15.90,
          "category": "lanches",
          "image_url": "https://...",
          "restaurant_id": "restaurant_id_1"
        },
        "produto_id_2": {
          "id": "produto_id_2",
          "name": "Coca-Cola",
          "description": "Refrigerante 350ml",
          "price": 5.00,
          "category": "bebidas",
          "image_url": "https://...",
          "restaurant_id": "restaurant_id_1"
        },
        "produto_id_3": {
          "id": "produto_id_3",
          "name": "Coxinha",
          "description": "Salgado frito recheado",
          "price": 4.50,
          "category": "salgados",
          "image_url": "https://...",
          "restaurant_id": "restaurant_id_1"
        }
      }
    }
  }
}
```

## Arquivos Criados/Modificados

### Novos Arquivos

1. **ProductCategory.kt** (`data/models/`)
   - Modelo de dados para categorias de produtos
   - Campos: `id`, `nome`, `descricao`

2. **ProductCategoryRepository.kt** (`data/repository/`)
   - Gerencia acesso aos dados de categorias no Firebase
   - Método: `getProductCategories()` - retorna Flow com lista de categorias
   - **Lógica de normalização:**
     - Lê strings simples do Firebase (ex: "Salgados", "Lanches")
     - Converte para ID normalizado (lowercase, remove acentos, substitui espaços por underscore)
     - Cria objetos ProductCategory com id normalizado e nome original

3. **ProductCategoryViewModel.kt** (`ui/viewmodel/`)
   - ViewModel para gerenciar estado das categorias
   - Funções principais:
     - `loadCategories()` - carrega categorias do Firebase
     - `selectCategory(categoryId)` - seleciona uma categoria
     - `refresh()` - recarrega categorias
   - Estados:
     - `categories` - lista de ProductCategory do Firebase
     - `menuCategories` - lista de MenuCategory para UI
     - `selectedCategoryId` - ID da categoria selecionada
     - `isLoading` - status de carregamento
     - `error` - mensagens de erro

### Arquivos Modificados

1. **ProductViewModel.kt**
   - Adicionado estado `filteredMenuItems` - produtos filtrados por categoria
   - Adicionado estado `selectedCategory` - categoria atualmente selecionada
   - Nova função `filterByCategory(categoryId)` - filtra produtos por categoria
   - Modificado `loadProductsByRestaurant()` - aplica filtro após carregar produtos

2. **StoreDetailScreen.kt**
   - Adicionado parâmetro `categoryViewModel: ProductCategoryViewModel`
   - Agora observa `filteredMenuItems` em vez de `menuItems`
   - Observa `firebaseCategories` do categoryViewModel
   - CategoryFilters agora usa categorias do Firebase quando disponíveis
   - Ao clicar em categoria:
     - Atualiza seleção no `categoryViewModel`
     - Filtra produtos no `productViewModel`
     - Chama callback original `onCategoryClick`

## Fluxo de Funcionamento

1. **Inicialização:**
   - `ProductCategoryViewModel` carrega categorias do Firebase automaticamente
   - `ProductViewModel` carrega produtos quando `store.id` muda
   - Primeira categoria é selecionada por padrão

2. **Quando usuário clica em uma categoria:**
   - `categoryViewModel.selectCategory(categoryId)` atualiza categoria selecionada
   - `productViewModel.filterByCategory(categoryId)` filtra produtos
   - UI exibe apenas produtos da categoria selecionada

3. **Sincronização:**
   - Categorias são atualizadas em tempo real via Firebase ValueEventListener
   - Produtos são filtrados localmente para performance
   - Estados são gerenciados via StateFlow (Kotlin Flow)

## Benefícios

1. **Categorias Dinâmicas:** 
   - Adicionar/editar categorias direto no Firebase
   - Sem necessidade de atualizar o app

2. **Filtro Eficiente:**
   - Produtos filtrados localmente
   - Resposta instantânea ao trocar categoria

3. **Estado Consistente:**
   - ViewModels gerenciam estado de forma centralizada
   - UI sempre reflete dados atuais

4. **Separação de Responsabilidades:**
   - Repository: acesso aos dados
   - ViewModel: lógica de negócio
   - Screen: apenas apresentação

## Próximos Passos Sugeridos

1. Adicionar categorias no Firebase seguindo a estrutura documentada
2. Garantir que todos os produtos tenham campo `category` preenchido
3. Testar filtro de categorias com dados reais
4. Considerar adicionar:
   - Opção "Todos" para mostrar produtos sem filtro
   - Indicador visual de categoria selecionada
   - Contagem de produtos por categoria
   - Cache local de categorias para offline
