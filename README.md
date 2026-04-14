# Outpick – Android App

Aplicación Android de probador virtual de ropa desarrollada con **Kotlin** y **Jetpack Compose**.

## Descripción

Outpick permite a los usuarios explorar un catálogo de prendas, guardar outfits en un armario virtual y configurar su perfil con medidas personales. La app es la base de referencia del proyecto web homónimo.

## Tecnologías

| Capa | Tecnología |
|------|------------|
| UI | Jetpack Compose |
| Lenguaje | Kotlin |
| Almacenamiento local | SharedPreferences, MediaStore |
| Visor web integrado | WebView (`visor.html`) |
| Mínimo SDK | Android 8.0 (API 26) |

## Pantallas

| Pantalla | Descripción |
|----------|-------------|
| **Inicio** | Feed de inspiración con tarjetas de outfits |
| **Probador** | Flujo de 3 pasos: foto → catálogo → resultado IA |
| **Armario** | Cuadrícula de outfits guardados (6 slots gratuitos) |
| **Perfil** | Nombre, email, foto y medidas corporales |

## Catálogo de prendas

10 prendas de ejemplo distribuidas por zonas:
- **Torso**: Camiseta básica, Camisa Oxford, Sudadera, Blazer, Camiseta estampada, Polo
- **Piernas**: Vaqueros slim, Pantalón chino
- **Pies**: Zapatillas blancas, Botas Chelsea

## Estructura del proyecto

```
app/src/main/
├── java/com/example/op/
│   ├── MainActivity.kt          # Toda la lógica de UI y navegación
│   └── ui/theme/
│       ├── Color.kt             # Paleta: Fucsia #FF00FF, FondoTarjetas #F5F5F5
│       ├── Theme.kt
│       └── Type.kt
├── assets/
│   └── visor.html               # Visor web embebido
└── res/
    └── values/
        ├── colors.xml
        ├── strings.xml
        └── themes.xml
```

## Cómo ejecutar

1. Abre el proyecto en **Android Studio Hedgehog** o superior.
2. Selecciona un emulador o dispositivo físico (API 26+).
3. Pulsa **Run ▶**.

> No requiere ninguna clave de API ni configuración adicional. El probador virtual con IA está implementado en el proyecto web (`outpick-web`).

## Paleta de colores

| Token | Hex | Uso |
|-------|-----|-----|
| `Fucsia` | `#FF00FF` | Color de marca, botones, navegación activa |
| `FondoTarjetas` | `#F5F5F5` | Fondo de tarjetas y slots |
| `TextoPrincipal` | `#121212` | Texto principal |

## Proyecto relacionado

La versión web con probador virtual IA real se encuentra en [`outpick-web`](../outpick-web/README.md).

---

© IAGO FONTENLA GAGO / ARMONY®
