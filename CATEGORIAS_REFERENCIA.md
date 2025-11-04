# Guia Rápido - IDs de Categorias

## Mapeamento de Categorias do Firebase para IDs

Ao cadastrar produtos no Firebase, use os valores **exatamente como aparecem** na tabela `categorias_produto` (com primeira letra maiúscula):

| Nome no Firebase | Valor para usar em `category` | ID Normalizado (gerado automaticamente) |
|-----------------|------------------------------|----------------------------------------|
| Salgados | `Salgados` | `salgados` |
| Lanches | `Lanches` | `lanches` |
| Bebidas | `Bebidas` | `bebidas` |
| Sobremesas | `Sobremesas` | `sobremesas` |
| Doces | `Doces` | `doces` |
| Acompanhamentos | `Acompanhamentos` | `acompanhamentos` |
| Saudáveis | `Saudáveis` | `saudaveis` |
| Laticínios | `Laticínios` | `laticinios` |
| Ingredientes | `Ingredientes` | `ingredientes` |
| Outros | `Outros` | `outros` |

## Exemplo de Produto

```json
{
  "restaurants": {
    "burger_queen": {
      "products": {
        "xburguer_001": {
          "id": "xburguer_001",
          "name": "X-Burger",
          "description": "Hambúrguer com queijo",
          "price": 15.90,
          "category": "Lanches",  // ← Use exatamente como está no Firebase
          "image_url": "https://...",
          "restaurant_id": "burger_queen"
        },
        "coca_001": {
          "id": "coca_001",
          "name": "Coca-Cola Lata 350ml",
          "description": "Refrigerante clássico de cola",
          "price": 1,
          "category": "Bebidas",  // ← Primeira letra maiúscula
          "image_url": "https://...",
          "restaurant_id": "burger_queen"
        }
      }
    }
  }
}
```

## Como o Sistema Funciona

1. **Categorias** são lidas do Firebase como strings (ex: "Salgados", "Lanches", "Bebidas")
2. **Sistema normaliza automaticamente** os IDs para lowercase sem acentos (ex: "Laticínios" → "laticinios")
3. **Produtos** devem ter `category` com o nome exato do Firebase (com primeira letra maiúscula)
4. **Sistema normaliza a categoria do produto** no momento de carregar para fazer a comparação

## Regra de Normalização Automática

O sistema automaticamente converte (você NÃO precisa fazer isso manualmente):
- Para minúsculas: `Lanches` → `lanches`
- Remove acentos: `Laticínios` → `laticinios`, `Saudáveis` → `saudaveis`
- Substitui espaços por underscore (se houver)

**IMPORTANTE:** Use o nome da categoria **exatamente como está** no Firebase `categorias_produto`.
- ✅ Correto: `"category": "Bebidas"`
- ❌ Errado: `"category": "bebidas"` (minúscula)
- ❌ Errado: `"category": "2"` (índice numérico)

## Comportamento do Filtro

- **Inicial:** Todos os produtos da loja são exibidos
- **Clicar em categoria:** Mostra apenas produtos daquela categoria
- **Clicar novamente:** Volta a mostrar todos os produtos
- **Produtos são filtrados localmente** para resposta instantânea
