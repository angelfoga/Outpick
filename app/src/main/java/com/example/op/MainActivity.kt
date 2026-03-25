package com.example.op

import android.content.Context
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import android.widget.VideoView
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// --- COLORES ---
val FondoPrincipal = Color.White
val FondoTarjetas = Color(0xFFF5F5F5)
val Fucsia = Color(0xFFFF00FF)
val ColorTexto = Color(0xFF121212)

// --- DATOS DEL CATÁLOGO ---
data class Prenda(val nombre: String, val zona: String, val tienda: String, val precio: Double, val imagenId: Int?)

val catalogoPrueba = listOf(
    Prenda("Camiseta Básica Blanca", "Torso", "Pull&Bear", 4.99, null),
    Prenda("Top Fucsia", "Torso", "Bershka", 7.50, null),
    Prenda("Sudadera Negra", "Torso", "Primark", 12.00, null),
    Prenda("Chaqueta Vaquera", "Torso", "Lefties", 15.99, null),
    Prenda("Pantalón Vaquero Ancho", "Piernas", "Zara", 19.99, null),
    Prenda("Falda Plisada", "Piernas", "H&M", 14.99, null),
    Prenda("Pantalón Cargo", "Piernas", "Pull&Bear", 17.99, null),
    Prenda("Zapatillas Lona", "Pies", "Lefties", 9.99, null),
    Prenda("Botas Negras", "Pies", "Marypaz", 24.99, null),
    Prenda("Zapatillas Deportivas", "Pies", "Decathlon", 14.99, null)
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { AppPrincipal() }
    }
}

// --- CONTROL DE ANIMACIÓN DE INICIO ---
@Composable
fun AppPrincipal() {
    var mostrarSplash by rememberSaveable { mutableStateOf(true) }

    if (mostrarSplash) {
        LaunchedEffect(Unit) {
            delay(4500)
            mostrarSplash = false
        }
        PantallaSplash()
    } else {
        AppOutpick()
    }
}

@Composable
fun PantallaSplash() {
    Box(modifier = Modifier.fillMaxSize().background(FondoPrincipal), contentAlignment = Alignment.Center) {
        ReproductorVideo(videoResId = R.raw.video_splash)
    }
}

@Composable
fun ReproductorVideo(videoResId: Int) {
    val context = LocalContext.current
    AndroidView(
        factory = { ctx ->
            VideoView(ctx).apply {
                setVideoURI(Uri.parse("android.resource://${context.packageName}/$videoResId"))
                start()
                setOnPreparedListener { mp -> mp.isLooping = true }
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}

// --- NAVEGACIÓN PRINCIPAL ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppOutpick() {
    var pantallaActual by rememberSaveable { mutableStateOf("Inicio") }
    var outfitSeleccionado by remember { mutableStateOf("") }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    BackHandler(enabled = drawerState.isOpen) {
        scope.launch { drawerState.close() }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(drawerContainerColor = FondoPrincipal) {
                Spacer(Modifier.height(55.dp))
                Text("Menú Outpick", modifier = Modifier.padding(16.dp), color = Fucsia, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                HorizontalDivider(color = Color.LightGray)

                NavigationDrawerItem(
                    label = { Text("Mi Perfil", color = ColorTexto, fontWeight = FontWeight.Bold) },
                    selected = pantallaActual == "Perfil",
                    onClick = { pantallaActual = "Perfil"; scope.launch { drawerState.close() } },
                    icon = { Icon(Icons.Default.Person, "Perfil", tint = Fucsia) },
                    colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = FondoPrincipal)
                )
                NavigationDrawerItem(
                    label = { Text("Medidas Modelo 3D", color = ColorTexto, fontWeight = FontWeight.Bold) },
                    selected = pantallaActual == "Medidas",
                    onClick = { pantallaActual = "Medidas"; scope.launch { drawerState.close() } },
                    icon = { Icon(Icons.Default.Accessibility, "Medidas", tint = Fucsia) },
                    colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = FondoPrincipal)
                )
                NavigationDrawerItem(
                    label = { Text("Premium", color = ColorTexto, fontWeight = FontWeight.Bold) },
                    selected = pantallaActual == "Premium",
                    onClick = { pantallaActual = "Premium"; scope.launch { drawerState.close() } },
                    icon = { Icon(Icons.Default.Star, "Premium", tint = Fucsia) },
                    colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = FondoPrincipal)
                )
                NavigationDrawerItem(
                    label = { Text("Información", color = ColorTexto, fontWeight = FontWeight.Bold) },
                    selected = pantallaActual == "Info",
                    onClick = { pantallaActual = "Info"; scope.launch { drawerState.close() } },
                    icon = { Icon(Icons.Default.Info, "Info", tint = Fucsia) },
                    colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = FondoPrincipal)
                )
            }
        }
    ) {
        Scaffold(
            containerColor = FondoPrincipal,
            bottomBar = {
                NavigationBar(containerColor = FondoPrincipal.copy(alpha = 0.95f), tonalElevation = 0.dp) {
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Home, "Inicio") },
                        selected = pantallaActual == "Inicio",
                        onClick = { pantallaActual = "Inicio" },
                        colors = NavigationBarItemDefaults.colors(selectedIconColor = Fucsia, unselectedIconColor = Color.Gray, indicatorColor = Color.Transparent)
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Star, "Armario") },
                        selected = pantallaActual == "MiArmario" || pantallaActual == "DetalleOutfit" || pantallaActual == "ComoMeQueda",
                        onClick = { pantallaActual = "MiArmario" },
                        colors = NavigationBarItemDefaults.colors(selectedIconColor = Fucsia, unselectedIconColor = Color.Gray, indicatorColor = Color.Transparent)
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.AutoMirrored.Filled.List, "Catálogo") },
                        selected = pantallaActual == "Entrar",
                        onClick = { pantallaActual = "Entrar" },
                        colors = NavigationBarItemDefaults.colors(selectedIconColor = Fucsia, unselectedIconColor = Color.Gray, indicatorColor = Color.Transparent)
                    )
                }
            }
        ) { paddingValues ->
            AnimatedContent(
                targetState = pantallaActual,
                transitionSpec = { fadeIn(tween(400)) togetherWith fadeOut(tween(400)) },
                modifier = Modifier.fillMaxSize(),
                label = ""
            ) { pantalla ->
                when (pantalla) {
                    "Inicio" -> PantallaInicio(onAbrirMenu = { scope.launch { drawerState.open() } })
                    "Entrar" -> PantallaTienda(paddingValues)
                    "MiArmario" -> PantallaMiArmario(paddingValues, onAbrirOutfit = { nombre -> outfitSeleccionado = nombre; pantallaActual = "DetalleOutfit" })
                    "DetalleOutfit" -> PantallaDetalleOutfit(paddingValues, outfitSeleccionado, onAbrirAvatar = { pantallaActual = "ComoMeQueda" }, onVolver = { pantallaActual = "MiArmario" })
                    "ComoMeQueda" -> PantallaAvatar3D(paddingValues, onVolver = { pantallaActual = "DetalleOutfit" })
                    "Perfil" -> PantallaPerfil(paddingValues, onVolver = { pantallaActual = "Inicio" })
                    "Medidas" -> PantallaMedidas(paddingValues, onVolver = { pantallaActual = "Inicio" })
                    "Premium" -> PantallaPremium(paddingValues, onVolver = { pantallaActual = "Inicio" })
                    "Info" -> PantallaInfo(paddingValues, onVolver = { pantallaActual = "Inicio" })
                }
            }
        }
    }
}

// --- PANTALLAS ---

@Composable
fun PantallaInicio(onAbrirMenu: () -> Unit) {
    val estadoColumna1 = rememberLazyListState()
    val estadoColumna2 = rememberLazyListState(initialFirstVisibleItemIndex = 500)

    LaunchedEffect(Unit) { while (true) { estadoColumna1.animateScrollBy(3000f, tween(10000, easing = LinearEasing)) } }
    LaunchedEffect(Unit) { while (true) { estadoColumna2.animateScrollBy(-3000f, tween(10000, easing = LinearEasing)) } }

    val fotosColumna1 = listOf(R.drawable.i1, R.drawable.i2, R.drawable.i3, R.drawable.i4, R.drawable.i5)
    val fotosColumna2 = listOf(R.drawable.i6, R.drawable.i7, R.drawable.i8, R.drawable.i9, R.drawable.i10)

    Box(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.fillMaxSize().background(FondoPrincipal).padding(horizontal = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            LazyColumn(state = estadoColumna1, modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp), userScrollEnabled = false) {
                items(1000) { index ->
                    val foto = fotosColumna1[index % 5]
                    Box(modifier = Modifier.fillMaxWidth().aspectRatio(0.7f).background(Color.LightGray, RoundedCornerShape(12.dp))) {
                        Image(painter = painterResource(id = foto), contentDescription = "", modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                    }
                }
            }
            LazyColumn(state = estadoColumna2, modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp), userScrollEnabled = false) {
                items(1000) { index ->
                    val foto = fotosColumna2[index % 5]
                    Box(modifier = Modifier.fillMaxWidth().aspectRatio(0.7f).background(Color.DarkGray, RoundedCornerShape(12.dp))) {
                        Image(painter = painterResource(id = foto), contentDescription = "", modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                    }
                }
            }
        }

        IconButton(
            onClick = onAbrirMenu,
            modifier = Modifier
                .padding(top = 65.dp, start = 16.dp)
                .background(Color.White.copy(alpha = 0.8f), CircleShape)
        ) {
            Icon(Icons.Default.Menu, contentDescription = "Abrir Menú", tint = Fucsia)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaPerfil(paddingValues: PaddingValues, onVolver: () -> Unit) {
    val context = LocalContext.current
    val sharedPref = remember { context.getSharedPreferences("OutpickPrefs", Context.MODE_PRIVATE) }

    var nombre by remember { mutableStateOf(sharedPref.getString("nombre", "") ?: "") }
    var email by remember { mutableStateOf(sharedPref.getString("email", "") ?: "") }
    var fotoUriString by remember { mutableStateOf(sharedPref.getString("fotoUri", "") ?: "") }

    var imagenBitmap by remember(fotoUriString) { mutableStateOf<android.graphics.Bitmap?>(null) }

    val launcherFoto = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            fotoUriString = uri.toString()
            sharedPref.edit().putString("fotoUri", fotoUriString).apply()
        }
    }

    // El bloque try-catch que te daba error está solucionado y movido aquí
    LaunchedEffect(fotoUriString) {
        if (fotoUriString.isNotEmpty()) {
            try {
                val uri = Uri.parse(fotoUriString)
                imagenBitmap = if (Build.VERSION.SDK_INT >= 28) {
                    ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, uri))
                } else {
                    @Suppress("DEPRECATION")
                    MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                }
            } catch (e: Exception) {
                imagenBitmap = null
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
            IconButton(onClick = onVolver) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = ColorTexto) }
        }
        Text("Mi Perfil", color = Fucsia, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(30.dp))

        Box(
            modifier = Modifier.size(120.dp).background(FondoTarjetas, CircleShape).clip(CircleShape).clickable { launcherFoto.launch("image/*") },
            contentAlignment = Alignment.Center
        ) {
            if (imagenBitmap != null) {
                Image(bitmap = imagenBitmap!!.asImageBitmap(), contentDescription = "Foto de perfil", modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
            } else {
                Icon(Icons.Default.Person, contentDescription = "Foto", tint = Color.Gray, modifier = Modifier.size(60.dp))
            }
        }
        Text("Toca para cambiar foto", color = Fucsia, modifier = Modifier.padding(top = 8.dp).clickable { launcherFoto.launch("image/*") })

        Spacer(modifier = Modifier.height(40.dp))
        OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre Completo") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Correo Electrónico") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))

        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = {
                sharedPref.edit().putString("nombre", nombre).putString("email", email).apply()
                Toast.makeText(context, "Perfil guardado correctamente", Toast.LENGTH_SHORT).show()
            },
            colors = ButtonDefaults.buttonColors(containerColor = Fucsia),
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text("Guardar Cambios", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaMedidas(paddingValues: PaddingValues, onVolver: () -> Unit) {
    val context = LocalContext.current
    val sharedPref = remember { context.getSharedPreferences("OutpickPrefs", Context.MODE_PRIVATE) }

    var altura by remember { mutableStateOf(sharedPref.getString("altura", "") ?: "") }
    var peso by remember { mutableStateOf(sharedPref.getString("peso", "") ?: "") }
    var pecho by remember { mutableStateOf(sharedPref.getString("pecho", "") ?: "") }
    var cintura by remember { mutableStateOf(sharedPref.getString("cintura", "") ?: "") }
    var cadera by remember { mutableStateOf(sharedPref.getString("cadera", "") ?: "") }

    Column(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
            IconButton(onClick = onVolver) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = ColorTexto) }
        }
        Text("Medidas Modelo 3D", color = Fucsia, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(10.dp))
        Text("Configura tu maniquí virtual para que la ropa se ajuste a tu cuerpo.", color = Color.Gray, textAlign = TextAlign.Center, modifier = Modifier.padding(horizontal = 20.dp))
        Spacer(modifier = Modifier.height(30.dp))

        OutlinedTextField(value = altura, onValueChange = { altura = it }, label = { Text("Altura (cm)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(value = peso, onValueChange = { peso = it }, label = { Text("Peso (kg)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(value = pecho, onValueChange = { pecho = it }, label = { Text("Contorno Pecho (cm)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(value = cintura, onValueChange = { cintura = it }, label = { Text("Contorno Cintura (cm)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(value = cadera, onValueChange = { cadera = it }, label = { Text("Contorno Cadera (cm)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))

        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = {
                sharedPref.edit()
                    .putString("altura", altura)
                    .putString("peso", peso)
                    .putString("pecho", pecho)
                    .putString("cintura", cintura)
                    .putString("cadera", cadera)
                    .apply()
                Toast.makeText(context, "Medidas guardadas para tu Avatar 3D", Toast.LENGTH_SHORT).show()
            },
            colors = ButtonDefaults.buttonColors(containerColor = Fucsia),
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text("Guardar Medidas", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun PantallaPremium(paddingValues: PaddingValues, onVolver: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
            IconButton(onClick = onVolver) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = ColorTexto) }
        }
        Spacer(modifier = Modifier.height(20.dp))
        Icon(Icons.Default.Star, contentDescription = "Premium", tint = Fucsia, modifier = Modifier.size(100.dp))
        Text("Outpick Premium", color = Fucsia, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(top = 16.dp))
        Spacer(modifier = Modifier.height(30.dp))
        Text("Desbloquea todo el potencial de tu armario virtual:", color = ColorTexto, fontSize = 18.sp, textAlign = TextAlign.Center, modifier = Modifier.padding(horizontal = 20.dp))
        Spacer(modifier = Modifier.height(20.dp))
        Column(horizontalAlignment = Alignment.Start, modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)) {
            Text("✔️ Slots de outfits ilimitados")
            Text("✔️ Filtros de ropa exclusivos")
            Text("✔️ Experiencia sin anuncios")
        }
        Spacer(modifier = Modifier.weight(1f))
        Button(onClick = { }, colors = ButtonDefaults.buttonColors(containerColor = Fucsia), modifier = Modifier.fillMaxWidth().height(60.dp), shape = RoundedCornerShape(12.dp)) {
            Text("Actualizar Ahora - 4.99€/mes", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun PantallaInfo(paddingValues: PaddingValues, onVolver: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
            IconButton(onClick = onVolver) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = ColorTexto) }
        }
        Spacer(modifier = Modifier.weight(1f))
        Text("Outpick", color = Fucsia, fontSize = 36.sp, fontWeight = FontWeight.ExtraBold)
        Spacer(modifier = Modifier.height(20.dp))
        Text("App desarrollada por:", color = Color.Gray, fontSize = 14.sp)
        Text("IAGO FONTENLA GAGO", color = ColorTexto, fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 20.dp))
        Text("Creada por:", color = Color.Gray, fontSize = 14.sp)
        Text("ARMONY ®", color = Fucsia, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(bottom = 40.dp))
        Text("El Equipo:", color = Color.Gray, fontSize = 14.sp)
        Text("IAGO FONTENLA GAGO", color = ColorTexto, fontSize = 16.sp, fontWeight = FontWeight.Medium)
        Text("JOSE PIÑÁN ROMERO", color = ColorTexto, fontSize = 16.sp, fontWeight = FontWeight.Medium)
        Text("PALMIRA GOMEZ", color = ColorTexto, fontSize = 16.sp, fontWeight = FontWeight.Medium)
        Text("FÁTIMA ELGAD", color = ColorTexto, fontSize = 16.sp, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.weight(1f))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaTienda(paddingValues: PaddingValues) {
    var filtroSeleccionado by remember { mutableStateOf("Todo") }
    val categorias = listOf("Todo", "Torso", "Piernas", "Pies")
    val catalogoFiltrado = if (filtroSeleccionado == "Todo") catalogoPrueba else catalogoPrueba.filter { it.zona == filtroSeleccionado }

    Column(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp)) {
        LazyRow(modifier = Modifier.padding(vertical = 12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(categorias) { categoria ->
                FilterChip(
                    selected = filtroSeleccionado == categoria,
                    onClick = { filtroSeleccionado = categoria },
                    label = { Text(categoria, color = if (filtroSeleccionado == categoria) Color.White else ColorTexto) },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Fucsia, containerColor = FondoTarjetas, disabledContainerColor = Color.Transparent)
                )
            }
        }
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(catalogoFiltrado) { prenda ->
                Card(colors = CardDefaults.cardColors(containerColor = FondoTarjetas), modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.padding(12.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(60.dp).background(Color.LightGray, RoundedCornerShape(8.dp)))
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(prenda.nombre, color = ColorTexto, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("${prenda.zona} • ${prenda.tienda}", color = Color.Gray, fontSize = 12.sp)
                        }
                        Text("${prenda.precio} €", color = Fucsia, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun PantallaMiArmario(paddingValues: PaddingValues, onAbrirOutfit: (String) -> Unit) {
    var mostrarDialogoPremium by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 16.dp)) {
        Spacer(modifier = Modifier.height(16.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(16) { index ->
                val numeroOutfit = index + 1
                val esAccesible = numeroOutfit <= 6

                Card(
                    colors = CardDefaults.cardColors(containerColor = FondoTarjetas),
                    modifier = Modifier.aspectRatio(1f).clickable {
                        if (esAccesible) { onAbrirOutfit("Outfit $numeroOutfit") } else { mostrarDialogoPremium = true }
                    }
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(8.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (esAccesible) {
                            Icon(Icons.Default.Face, contentDescription = "Abierto", tint = Fucsia, modifier = Modifier.size(40.dp))
                        } else {
                            Icon(Icons.Default.Lock, contentDescription = "Bloqueado", tint = Color.Gray, modifier = Modifier.size(40.dp))
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Outfit $numeroOutfit", color = ColorTexto, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    if (mostrarDialogoPremium) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoPremium = false },
            containerColor = FondoPrincipal,
            title = { Text("Función Bloqueada", color = ColorTexto, fontWeight = FontWeight.Bold) },
            text = { Text("Para guardar más de 6 outfits necesitas una cuenta superior.", color = Color.DarkGray) },
            confirmButton = { Button(onClick = { mostrarDialogoPremium = false }, colors = ButtonDefaults.buttonColors(containerColor = Fucsia)) { Text("Activa Outpick Premium", color = Color.White) } },
            dismissButton = { TextButton(onClick = { mostrarDialogoPremium = false }) { Text("Cancelar", color = Color.Gray) } }
        )
    }
}

@Composable
fun PantallaDetalleOutfit(paddingValues: PaddingValues, nombreOutfit: String, onAbrirAvatar: () -> Unit, onVolver: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
            IconButton(onClick = onVolver) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = ColorTexto) }
        }
        Text(nombreOutfit, color = ColorTexto, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(20.dp))

        SlotPrenda(zona = "TORSO", estado = "Vacío - Toca para añadir")
        Spacer(modifier = Modifier.height(12.dp))
        SlotPrenda(zona = "PIERNAS", estado = "Pantalón Vaquero Ancho")
        Spacer(modifier = Modifier.height(12.dp))
        SlotPrenda(zona = "PIES", estado = "Zapatillas Lona")

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onAbrirAvatar,
            colors = ButtonDefaults.buttonColors(containerColor = Fucsia),
            modifier = Modifier.fillMaxWidth().height(55.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Accessibility, contentDescription = "3D", tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Ver Modelo 3D", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun SlotPrenda(zona: String, estado: String) {
    Card(colors = CardDefaults.cardColors(containerColor = FondoTarjetas), modifier = Modifier.fillMaxWidth().height(80.dp)) {
        Row(modifier = Modifier.padding(16.dp).fillMaxSize(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text(zona, color = Fucsia, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text(estado, color = ColorTexto, fontSize = 16.sp)
            }
            Icon(Icons.Default.Add, contentDescription = "Añadir", tint = Fucsia)
        }
    }
}

// --- PANTALLA VISOR 3D (CONECTADA A LA CARPETA ASSETS) ---
@Composable
fun PantallaAvatar3D(paddingValues: PaddingValues, onVolver: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
            IconButton(onClick = onVolver) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = ColorTexto) }
        }
        Text("Probador Virtual 3D", color = ColorTexto, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(10.dp))
        Text("Desliza para girar el modelo o pellizca para hacer zoom", color = Color.Gray, fontSize = 12.sp, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(20.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(FondoTarjetas, shape = RoundedCornerShape(16.dp))
                .clip(RoundedCornerShape(16.dp))
        ) {
            AndroidView(
                factory = { ctx ->
                    WebView(ctx).apply {
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true

                        // Estas son las 3 líneas mágicas que apagan el bloqueo CORS
                        settings.allowFileAccess = true
                        settings.allowFileAccessFromFileURLs = true
                        settings.allowUniversalAccessFromFileURLs = true

                        webChromeClient = WebChromeClient()
                        webViewClient = WebViewClient()

                        // Cargamos tu archivo local de la carpeta assets
                        loadUrl("file:///android_asset/visor.html")
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}