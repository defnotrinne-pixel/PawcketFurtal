package com.pawcket.furtal

import androidx.activity.enableEdgeToEdge
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

val Green = Color(0xFF3F7D58); val Dark = Color(0xFF1F4D3A); val Cream = Color(0xFFFBF9F1)
val Orange = Color(0xFFF4A261); val Teal = Color(0xFF2A7F86); val Pink = Color(0xFFFFEEF2)
val Mint = Color(0xFFE6F4EA); val Lav = Color(0xFFF3E8FF); val Peach = Color(0xFFFFEFE6); val Ink = Color(0xFF1E2A24)

class MainActivity : ComponentActivity() {
    override fun onCreate(s: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(s)
        setContent {
            MaterialTheme(colorScheme = lightColorScheme(primary = Green, background = Cream, surface = Cream)) {
                Surface(Modifier.fillMaxSize(), color = Cream) { App(viewModel()) }
            }
        }
    }
}

fun Context.toast(m: String) = Toast.makeText(this, m, Toast.LENGTH_SHORT).show()
fun Context.go(i: Intent) = try { startActivity(i) } catch (e: Exception) { toast("No app available") }

@Composable fun App(vm: Vm) {
    var screen by remember { mutableStateOf("login") }
    var menu by remember { mutableStateOf(false) }
    var langDlg by remember { mutableStateOf(false) }
    val tabs = listOf("home" to Icons.Filled.Home, "lost" to Icons.Filled.Search, "adopt" to Icons.Filled.Favorite,
        "clinics" to Icons.Filled.LocalHospital, "give" to Icons.Filled.CardGiftcard)
    fun t(k: String) = if (!vm.filipino) mapOf("home" to "Home", "lost" to "Lost", "adopt" to "Adopt", "clinics" to "Clinics", "give" to "Give")[k]!!
        else mapOf("home" to "Tahanan", "lost" to "Nawala", "adopt" to "Ampunin", "clinics" to "Klinika", "give" to "Magbigay")[k]!!
    if (screen == "login") { Login(vm) { screen = "home" }; return }
    if (screen == "account") { BackHandler { screen = "home" }; Account(vm, { screen = "home" }) { screen = "login" }; return }
    Box(Modifier.fillMaxSize()) {
        Scaffold(containerColor = Cream,
            topBar = { TopBar({ screen = "account" }, { langDlg = true }, { menu = true }) },
            bottomBar = {
                NavigationBar(containerColor = Color.White) {
                    tabs.forEach { (k, ic) ->
                        NavigationBarItem(selected = screen == k, onClick = { screen = k },
                            icon = { Icon(ic, null) }, label = { Text(t(k), fontSize = 11.sp) },
                            colors = NavigationBarItemDefaults.colors(selectedIconColor = Green, selectedTextColor = Green, indicatorColor = Mint))
                    }
                }
            }) { pad ->
            Box(Modifier.padding(pad)) {
                when (screen) {
                    "home" -> Home(vm) { screen = it }
                    "adopt" -> Adopt(vm)
                    "lost" -> Lost(vm)
                    "clinics" -> Clinics(vm)
                    "give" -> Give(vm)
                }
            }
        }
        if (menu) Box(Modifier.fillMaxSize().background(Color(0x66000000)).clickable { menu = false }) {
            Column(Modifier.align(Alignment.CenterEnd).fillMaxHeight().width(290.dp).background(Cream).padding(20.dp).clickable(enabled = false) {}) {
                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                    Column { Label("EXPLORE"); Text("Menu", fontSize = 24.sp) }
                    IconButton({ menu = false }) { Icon(Icons.Filled.Close, null) }
                }
                Spacer(Modifier.height(12.dp))
                listOf("home" to "Home", "lost" to "Lost", "adopt" to "Adopt", "clinics" to "Clinics", "give" to "Donations").forEach { (k, n) ->
                    Row(Modifier.fillMaxWidth().clickable { screen = k; menu = false }.padding(vertical = 16.dp), Arrangement.SpaceBetween) {
                        Text(n, color = if (screen == k) Green else Ink, fontWeight = if (screen == k) FontWeight.Bold else FontWeight.Normal)
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, null, Modifier.size(18.dp))
                    }
                    HorizontalDivider(color = Color(0x11000000))
                }
                Spacer(Modifier.height(20.dp))
                Box(Modifier.clip(RoundedCornerShape(12.dp)).background(Mint).padding(16.dp)) {
                    Text("From stray to stay, we've got the way.", color = Green, fontSize = 14.sp)
                }
            }
        }
    }
    if (langDlg) AlertDialog(onDismissRequest = { langDlg = false }, confirmButton = {}, title = { Text("Language") }, text = {
        Column {
            listOf("English" to false, "Filipino" to true).forEach { (n, f) ->
                Row(Modifier.fillMaxWidth().clickable { vm.filipino = f; langDlg = false }, verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(vm.filipino == f, { vm.filipino = f; langDlg = false }); Text(n)
                }
            }
        }
    })
}

@Composable fun Label(s: String) = Text(s, fontSize = 10.sp, letterSpacing = 1.5.sp, color = Color(0xFF6B7A72), fontWeight = FontWeight.Medium)

@Composable fun TopBar(acc: () -> Unit, lang: () -> Unit, menu: () -> Unit) {
    Row(Modifier.fillMaxWidth().background(Color.White).statusBarsPadding().padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically) {
        Image(painterResource(R.drawable.logo), "Logo", Modifier.size(34.dp)); Spacer(Modifier.width(8.dp))
        Text("PawCKet FurTal", fontWeight = FontWeight.SemiBold, color = Dark, modifier = Modifier.weight(1f))
        IconButton(acc) { Icon(Icons.Filled.Person, null, tint = Dark) }
        IconButton(lang) { Icon(Icons.Filled.Language, null, tint = Dark) }
        IconButton(menu) { Icon(Icons.Filled.Menu, null, tint = Dark) }
    }
}

@Composable fun BigBtn(text: String, color: Color = Green, fg: Color = Color.White, icon: ImageVector? = null, mod: Modifier = Modifier, onClick: () -> Unit) =
    Button(onClick, mod.fillMaxWidth().height(50.dp), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(color, fg)) {
        icon?.let { Icon(it, null, Modifier.size(18.dp)); Spacer(Modifier.width(8.dp)) }; Text(text)
    }

@Composable fun F(label: String, v: String, on: (String) -> Unit, lines: Int = 1, kb: KeyboardType = KeyboardType.Text, ph: String = "") {
    Text(label, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 10.dp, bottom = 4.dp))
    OutlinedTextField(v, on, Modifier.fillMaxWidth(), placeholder = { Text(ph, fontSize = 13.sp) }, minLines = lines, maxLines = if (lines == 1) 1 else 6,
        shape = RoundedCornerShape(10.dp), keyboardOptions = KeyboardOptions(keyboardType = kb),
        colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable fun Sheet(title: String, close: () -> Unit, body: @Composable ColumnScope.() -> Unit) =
    ModalBottomSheet(close, sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true), containerColor = Cream) {
        // imePadding must come BEFORE verticalScroll so the scroll area shrinks to fit above the keyboard
        Column(Modifier.fillMaxWidth().imePadding().navigationBarsPadding().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp).padding(bottom = 24.dp)) {
            Label("PAWCKET FURTAL"); Text(title, fontSize = 22.sp); body()
        }
    }

@Composable fun Banner(kicker: String, title: String, sub: String, bg: Color, content: @Composable ColumnScope.() -> Unit = {}) =
    Column(Modifier.fillMaxWidth().background(bg).padding(18.dp)) {
        Label(kicker); Text(title, fontSize = 24.sp, color = Dark); Spacer(Modifier.height(4.dp)); Text(sub, fontSize = 13.sp, color = Color(0xFF55645B)); content()
    }

@Composable fun Pic(emoji: String, mod: Modifier, img: Int? = null, bg: Color = Color(0xFFD9C7A8)) =
    Box(mod.background(bg), contentAlignment = Alignment.Center) {
        if (img != null) Image(painterResource(img), null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        else Icon(Icons.Filled.Pets, null, Modifier.size(72.dp), tint = Color(0xFF8A7556))
    }

@Composable fun Chips(opts: List<String>, sel: Int, on: (Int) -> Unit) =
    Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(50)).background(Color(0xFFEDEBE3)).padding(4.dp)) {
        opts.forEachIndexed { i, o ->
            Box(Modifier.weight(1f).clip(RoundedCornerShape(50)).background(if (i == sel) Color.White else Color.Transparent).clickable { on(i) }.padding(vertical = 8.dp),
                Alignment.Center) { Text(o, fontSize = 13.sp, fontWeight = if (i == sel) FontWeight.Bold else FontWeight.Normal) }
        }
    }

// ---------- LOGIN ----------
@Composable fun Login(vm: Vm, done: () -> Unit) {
    val c = androidx.compose.ui.platform.LocalContext.current
    var email by remember { mutableStateOf("") }; var pw by remember { mutableStateOf("") }; var name by remember { mutableStateOf("") }
    var show by remember { mutableStateOf(false) }; var signup by remember { mutableStateOf(false) }
    fun enter(p: Profile) { vm.user = p; vm.guest = false; done() }
    Column(Modifier.fillMaxSize().imePadding().navigationBarsPadding().verticalScroll(rememberScrollState()).statusBarsPadding().padding(24.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) { Image(painterResource(R.drawable.logo), "Logo", Modifier.size(40.dp)); Spacer(Modifier.width(8.dp)); Text("PawCKet Furtal", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Dark) }
        Image(painterResource(R.drawable.welcome), null, Modifier.fillMaxWidth().padding(vertical = 12.dp), contentScale = ContentScale.FillWidth)
        Text(if (signup) "Join the pack" else "A home for every pet", fontSize = 26.sp, color = Dark)
        Text("Find your new best friend, or help a lost one find their way home.", fontSize = 13.sp, color = Color(0xFF55645B))
        if (signup) F("Full name", name, { name = it }, ph = "Juan Dela Cruz")
        F("Email address", email, { email = it }, kb = KeyboardType.Email, ph = "you@example.com")
        Text("Password", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 10.dp, bottom = 4.dp))
        OutlinedTextField(pw, { pw = it }, Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(10.dp), placeholder = { Text("Enter your password") },
            visualTransformation = if (show) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = { IconButton({ show = !show }) { Icon(if (show) Icons.Filled.VisibilityOff else Icons.Filled.Visibility, null) } },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password))
        Text("Forgot password?", color = Green, fontSize = 12.sp, modifier = Modifier.align(Alignment.End).padding(vertical = 8.dp).clickable {
            if (email.contains("@")) c.toast("Reset link sent to $email") else c.toast("Enter your email first") })
        BigBtn(if (signup) "Create account" else "Log in", Teal, icon = Icons.AutoMirrored.Filled.ArrowForward) {
            when {
                !email.contains("@") -> c.toast("Enter a valid email")
                pw.length < 6 -> c.toast("Password must be at least 6 characters")
                signup && name.isBlank() -> c.toast("Enter your name")
                else -> enter(Profile(if (signup) name else email.substringBefore("@"), email))
            }
        }
        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            listOf("Apple", "Google").forEach { n ->
                OutlinedButton({ enter(Profile("$n User", "demo@${n.lowercase()}.com")) }, Modifier.weight(1f).height(46.dp), shape = RoundedCornerShape(12.dp)) { Text("Login via $n", color = Ink, fontSize = 12.sp) }
            }
        }
        Text("or continue with", fontSize = 12.sp, modifier = Modifier.align(Alignment.CenterHorizontally).padding(10.dp))
        OutlinedButton({ vm.user = Profile(); vm.guest = true; done() }, Modifier.fillMaxWidth().height(48.dp), shape = RoundedCornerShape(12.dp)) { Text("Explore as a guest", color = Ink) }
        Text(if (signup) "Already have an account? Log in" else "New to the pack? Sign up", color = Green, modifier = Modifier.align(Alignment.CenterHorizontally).padding(16.dp).clickable { signup = !signup })
    }
}
// ---------- HOME ----------
@Composable fun Home(vm: Vm, nav: (String) -> Unit) = LazyColumn {
    item {
        Column(Modifier.fillMaxWidth().background(Dark).padding(20.dp)) {
            Spacer(Modifier.height(50.dp)); Label("BAGUIO CITY ANIMAL WELFARE")
            Text("Connecting paws with hearts", fontSize = 28.sp, color = Color.White)
            Text("A caring community for pet adoption, welfare, and support in the City of Pines.", fontSize = 13.sp, color = Color(0xFFD5E5DB), modifier = Modifier.padding(vertical = 8.dp))
            BigBtn("Find a Pet", Orange, Ink, Icons.Filled.Favorite) { nav("adopt") }
            Spacer(Modifier.height(8.dp))
            OutlinedButton({ nav("lost") }, Modifier.fillMaxWidth().height(50.dp), shape = RoundedCornerShape(12.dp)) { Text("Report Lost Pet", color = Color.White) }
        }
    }
    item {
        Column(Modifier.padding(16.dp)) {
            Label("START HERE")
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                Text("Quick access", fontSize = 22.sp)
                Text("View all", color = Green, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { nav("adopt") })
            }
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                listOf(Triple("Lost & Found", "Help a pet find its way home", "lost"), Triple("Adoption", "Meet your new best friend", "adopt")).forEach { (a, b, k) ->
                    val tag = if (k == "lost") "${vm.reports.count { it.status == "Lost" }} LOST · ${vm.reports.count { it.status == "Found" }} FOUND" else "${vm.pets.size} PETS WAITING"
                    Box(Modifier.weight(1f).height(210.dp).clip(RoundedCornerShape(20.dp)).clickable { nav(k) }) {
                        Image(painterResource(if (k == "lost") R.drawable.max else R.drawable.cookie), null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                        Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent, Color(0xE6173B2C)))))
                        Column(Modifier.align(Alignment.BottomStart).padding(14.dp)) {
                            Text(tag, color = Color.White, fontSize = 10.sp, letterSpacing = 1.sp)
                            Text(a, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(b, color = Color(0xFFE3EEE7), fontSize = 12.sp, modifier = Modifier.weight(1f))
                                Icon(Icons.AutoMirrored.Filled.ArrowForward, null, Modifier.size(18.dp), tint = Color.White)
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
            listOf(Triple("Need veterinary care?", "Find trusted clinics near you", "clinics"), Triple("Support our mission", "Every little act of kindness helps", "give")).forEach { (a, b, k) ->
                Row(Modifier.padding(bottom = 10.dp).fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(Color.White).clickable { nav(k) }.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).background(if (k == "give") Peach else Mint), Alignment.Center) { Icon(if (k == "give") Icons.Filled.CardGiftcard else Icons.Filled.LocalHospital, null, tint = if (k == "give") Orange else Green) }
                    Column(Modifier.weight(1f).padding(start = 12.dp)) { Text(a, fontWeight = FontWeight.SemiBold); Text(b, fontSize = 12.sp, color = Color.Gray) }
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, null)
                }
            }
        }
    }
}

// ---------- ADOPT ----------
@Composable fun Adopt(vm: Vm) {
    val c = androidx.compose.ui.platform.LocalContext.current
    var tab by remember { mutableIntStateOf(0) }; var target by remember { mutableStateOf<Pet?>(null) }
    val list = vm.pets.filter { tab == 0 || (tab == 1 && it.type == "Dog") || (tab == 2 && it.type == "Cat") }
    LazyColumn(verticalArrangement = Arrangement.spacedBy(14.dp), contentPadding = PaddingValues(bottom = 16.dp)) {
        item { Banner("MAKE ROOM FOR LOVE", "Adopt a Pet", "Find your perfect companion. Each pet's personality is highlighted to help you connect.", Pink) }
        item { Box(Modifier.padding(horizontal = 16.dp)) { Chips(listOf("All Pets (${vm.pets.size})", "Dogs (${vm.pets.count { it.type == "Dog" }})", "Cats (${vm.pets.count { it.type == "Cat" }})"), tab) { tab = it } } }
        items(list) { p ->
            Card(Modifier.padding(horizontal = 16.dp).fillMaxWidth(), shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(Color.White)) {
                Box { Pic(p.emoji, Modifier.fillMaxWidth().height(190.dp), p.img)
                    if (p.vaccinated) Text("✓ Vaccinated", Modifier.padding(10.dp).clip(RoundedCornerShape(50)).background(Color.White).padding(horizontal = 10.dp, vertical = 4.dp), fontSize = 11.sp) }
                Column(Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) { Text(p.name, fontWeight = FontWeight.SemiBold); Text("${p.breed} · ${p.age}", fontSize = 13.sp, color = Color.Gray) }
                        IconButton({ if (p.id in vm.favorites) vm.favorites.remove(p.id) else vm.favorites.add(p.id) }) {
                            Icon(if (p.id in vm.favorites) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder, null, tint = Color(0xFFE91E63)) }
                    }
                    Text("Gender: ${p.gender}   Size: ${p.size}", fontSize = 12.sp, modifier = Modifier.padding(vertical = 8.dp))
                    BigBtn(if ("${p.name}" in vm.applications) "Application sent ✓" else "Apply to adopt ${p.name}") { target = p }
                }
            }
        }
    }
    target?.let { p ->
        var n by remember { mutableStateOf(vm.user?.name ?: "") }; var e by remember { mutableStateOf(vm.user?.email ?: "") }; var why by remember { mutableStateOf("") }
        Sheet("Adopt ${p.name}", { target = null }) {
            F("Full name", n, { n = it }, ph = "Your name"); F("Email address", e, { e = it }, kb = KeyboardType.Email, ph = "you@example.com")
            F("Why would you like to adopt ${p.name}?", why, { why = it }, 4, ph = "Tell us about your home and experience with pets...")
            Spacer(Modifier.height(16.dp))
            BigBtn("Submit") {
                if (n.isBlank() || !e.contains("@") || why.length < 10) c.toast("Please complete all fields")
                else { vm.applications.add(p.name); c.toast("Application for ${p.name} submitted!"); target = null }
            }
        }
    }
}

// ---------- LOST & FOUND ----------
@Composable fun Lost(vm: Vm) {
    val c = androidx.compose.ui.platform.LocalContext.current
    var q by remember { mutableStateOf("") }; var tab by remember { mutableIntStateOf(0) }
    var post by remember { mutableStateOf(false) }; var ident by remember { mutableStateOf(false) }; var view by remember { mutableStateOf<Report?>(null) }
    val list = vm.reports.filter { (tab == 0 || (tab == 1 && it.status == "Lost") || (tab == 2 && it.status == "Found")) &&
        (q.isBlank() || listOf(it.name, it.breed, it.place, it.desc).any { s -> s.contains(q, true) }) }
    LazyColumn(verticalArrangement = Arrangement.spacedBy(14.dp), contentPadding = PaddingValues(bottom = 16.dp)) {
        item { Banner("COMMUNITY BOARD", "Lost & Found Pets", "Help reunite pets with their families or find the owner of a pet you found.", Peach) {
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedButton({ ident = true }, Modifier.weight(1f).height(46.dp), shape = RoundedCornerShape(12.dp)) { Text("Identify Pet", color = Green) }
                Button({ post = true }, Modifier.weight(1f).height(46.dp), shape = RoundedCornerShape(12.dp)) { Icon(Icons.Filled.Add, null); Text("Post Report") }
            } } }
        item { Column(Modifier.padding(horizontal = 16.dp)) {
            OutlinedTextField(q, { q = it }, Modifier.fillMaxWidth(), leadingIcon = { Icon(Icons.Filled.Search, null) }, placeholder = { Text("Search breed, location, or name...") },
                singleLine = true, shape = RoundedCornerShape(12.dp), colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White))
            Spacer(Modifier.height(10.dp))
            Chips(listOf("All (${vm.reports.size})", "Lost (${vm.reports.count { it.status == "Lost" }})", "Found (${vm.reports.count { it.status == "Found" }})"), tab) { tab = it } } }
        if (list.isEmpty()) item { Text("No reports match your search.", Modifier.padding(24.dp), color = Color.Gray) }
        items(list) { r ->
            Card(Modifier.padding(horizontal = 16.dp).fillMaxWidth(), shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(Color.White)) {
                Box { Pic(r.emoji, Modifier.fillMaxWidth().height(170.dp), r.img)
                    Text(r.status, Modifier.align(Alignment.TopEnd).padding(10.dp).clip(RoundedCornerShape(8.dp)).background(if (r.status == "Lost") Color(0xFFE5665A) else Green).padding(horizontal = 10.dp, vertical = 3.dp), color = Color.White, fontSize = 11.sp) }
                Column(Modifier.padding(14.dp)) {
                    Text("${r.name} — ${r.breed}", fontWeight = FontWeight.SemiBold); Text(r.desc, fontSize = 13.sp, color = Color.Gray)
                    Text("Location: ${r.place}", fontSize = 12.sp, modifier = Modifier.padding(vertical = 6.dp))
                    OutlinedButton({ view = r }, Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp)) { Text("View & contact", color = Ink) }
                }
            }
        }
    }
    if (post) {
        var nm by remember { mutableStateOf("") }; var br by remember { mutableStateOf("") }; var ds by remember { mutableStateOf("") }
        var pl by remember { mutableStateOf("") }; var ct by remember { mutableStateOf(vm.user?.phone ?: "") }; var st by remember { mutableIntStateOf(0) }
        Sheet("Post a pet report", { post = false }) {
            Spacer(Modifier.height(8.dp)); Chips(listOf("Lost", "Found"), st) { st = it }
            F("Pet name", nm, { nm = it }, ph = "Unknown if found"); F("Breed", br, { br = it }, ph = "Aspin, Puspin..."); F("Description", ds, { ds = it }, 2, ph = "Color, collar, behavior")
            F("Last seen / found at", pl, { pl = it }, ph = "Burnham Park"); F("Contact number", ct, { ct = it }, kb = KeyboardType.Phone, ph = "+63...")
            Spacer(Modifier.height(16.dp))
            BigBtn("Submit report") {
                if (br.isBlank() || pl.isBlank() || ct.isBlank()) c.toast("Breed, location and contact are required")
                else { vm.reports.add(0, Report(vm.reports.size + 1, nm.ifBlank { "Unnamed" }, br, ds, pl, if (st == 0) "Lost" else "Found", ct, if (st == 0) "🐕" else "🐈")); c.toast("Report posted"); post = false }
            }
        }
    }
    if (ident) {
        var kw by remember { mutableStateOf("") }
        val m = if (kw.isBlank()) emptyList() else vm.reports.filter { r -> kw.split(" ", ",").filter { it.length > 2 }.any { w -> listOf(r.breed, r.desc, r.name).any { it.contains(w, true) } } }
        Sheet("Identify a pet", { ident = false }) {
            F("Describe the pet", kw, { kw = it }, 2, ph = "e.g. orange tabby, blue collar")
            Spacer(Modifier.height(12.dp))
            if (kw.isNotBlank() && m.isEmpty()) Text("No matches yet. Try posting a report.", color = Color.Gray)
            m.forEach { r -> Row(Modifier.fillMaxWidth().padding(vertical = 6.dp).clickable { ident = false; view = r }, verticalAlignment = Alignment.CenterVertically) {
                Pic(r.emoji, Modifier.size(48.dp).clip(RoundedCornerShape(12.dp)), r.img); Column(Modifier.padding(start = 10.dp)) { Text("${r.name} — ${r.breed} (${r.status})", fontWeight = FontWeight.SemiBold); Text(r.place, fontSize = 12.sp, color = Color.Gray) } } }
        }
    }
    view?.let { r -> Sheet("${r.name} — ${r.status}", { view = null }) {
        Text("${r.breed} · ${r.desc}", Modifier.padding(top = 8.dp)); Text("Location: ${r.place}", Modifier.padding(vertical = 6.dp)); Text("Contact: ${r.contact}")
        Spacer(Modifier.height(14.dp))
        BigBtn("Call", icon = Icons.Filled.Phone) { c.go(Intent(Intent.ACTION_DIAL, Uri.parse("tel:${r.contact.filter { it.isDigit() || it == '+' }}"))) }
        Spacer(Modifier.height(8.dp))
        BigBtn("Send SMS", Teal, icon = Icons.Filled.Email) { c.go(Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:${r.contact.filter { it.isDigit() || it == '+' }}")).putExtra("sms_body", "Hi, I'm contacting you about ${r.name} (${r.status}) via PawCKet FurTal.")) }
    } }
}

// ---------- CLINICS ----------
@Composable fun Clinics(vm: Vm) {
    val c = androidx.compose.ui.platform.LocalContext.current
    var q by remember { mutableStateOf("") }; var book by remember { mutableStateOf<Clinic?>(null) }
    LazyColumn(verticalArrangement = Arrangement.spacedBy(14.dp), contentPadding = PaddingValues(bottom = 16.dp)) {
        item { Banner("CARE NEARBY", "Veterinary Clinics", "Find trusted veterinary care and services in Baguio City.", Mint) }
        item { OutlinedTextField(q, { q = it }, Modifier.padding(horizontal = 16.dp).fillMaxWidth(), leadingIcon = { Icon(Icons.Filled.Search, null) }, placeholder = { Text("Search clinics...") }, singleLine = true,
            shape = RoundedCornerShape(12.dp), colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White)) }
        items(vm.clinics.filter { q.isBlank() || it.name.contains(q, true) || it.services.any { s -> s.contains(q, true) } }) { k ->
            Card(Modifier.padding(horizontal = 16.dp).fillMaxWidth(), shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(Color.White)) {
                Column(Modifier.padding(16.dp)) {
                    Text(k.name, fontWeight = FontWeight.SemiBold, fontSize = 17.sp)
                    Text(k.tag, Modifier.padding(vertical = 6.dp).clip(RoundedCornerShape(50)).background(Green).padding(horizontal = 10.dp, vertical = 3.dp), color = Color.White, fontSize = 11.sp)
                    Text("📍 ${k.address}", fontSize = 13.sp, modifier = Modifier.padding(top = 4.dp))
                    Text("📞 ${k.phone}", fontSize = 13.sp, color = Green, modifier = Modifier.padding(top = 4.dp).clickable { c.go(Intent(Intent.ACTION_DIAL, Uri.parse("tel:${k.phone.filter { it.isDigit() || it == '+' }}"))) })
                    Text("🕘 ${k.hours}", fontSize = 13.sp, modifier = Modifier.padding(top = 4.dp))
                    Text("Services", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, modifier = Modifier.padding(top = 10.dp))
                    k.services.forEach { Text("• $it", fontSize = 13.sp, color = Color.Gray) }
                    Spacer(Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedButton({ c.go(Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=${Uri.encode(k.name + ", " + k.address)}"))) }, Modifier.weight(1f), shape = RoundedCornerShape(10.dp)) { Text("Directions", color = Green) }
                        Button({ book = k }, Modifier.weight(1f), shape = RoundedCornerShape(10.dp)) { Text("Book visit") }
                    }
                }
            }
        }
    }
    book?.let { k ->
        var n by remember { mutableStateOf(vm.user?.name ?: "") }; var pet by remember { mutableStateOf("") }; var d by remember { mutableStateOf("") }
        var slot by remember { mutableIntStateOf(0) }; val slots = listOf("9:00 AM", "1:00 PM", "4:00 PM")
        Sheet("Book a visit", { book = null }) {
            Text(k.name, color = Green, modifier = Modifier.padding(top = 4.dp))
            F("Your name", n, { n = it }, ph = "Your name"); F("Pet's name", pet, { pet = it }, ph = "Cookie"); F("Preferred date", d, { d = it }, ph = "e.g. Oct 20, 2026")
            Text("Time", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 10.dp, bottom = 4.dp)); Chips(slots, slot) { slot = it }
            Spacer(Modifier.height(16.dp))
            BigBtn("Confirm booking") {
                if (n.isBlank() || pet.isBlank() || d.isBlank()) c.toast("Please complete all fields")
                else { vm.appointments.add("${k.name} · $d ${slots[slot]}"); c.toast("Booked at ${k.name}: $d, ${slots[slot]}"); book = null }
            }
        }
    }
}

// ---------- GIVE ----------
@Composable fun Give(vm: Vm) {
    val c = androidx.compose.ui.platform.LocalContext.current
    var mode by remember { mutableStateOf("") }
    LazyColumn(contentPadding = PaddingValues(bottom = 16.dp)) {
        item { Banner("KINDNESS IN ACTION", "Support Our Mission", "Help us provide care, shelter, and a second chance for animals in need.", Lav) }
        item { Column(Modifier.padding(16.dp)) {
            Text("How you can help", fontSize = 20.sp); Spacer(Modifier.height(10.dp))
            listOf(Triple("Monetary Donation", "Help fund medical treatments, food, and shelter for rescued animals.", "money"),
                Triple("Supplies Donation", "Donate pet food, medicine, blankets, and other essential supplies.", "supplies")).forEach { (a, b, k) ->
                Card(Modifier.padding(bottom = 10.dp).fillMaxWidth().clickable { mode = k }, shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(Color.White)) {
                    Column(Modifier.padding(16.dp)) { Text(a, fontWeight = FontWeight.SemiBold); Text(b, fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(vertical = 4.dp)); Text("Get started →", color = Green, fontSize = 13.sp) }
                }
            }
            Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(Dark).padding(18.dp)) {
                Label("YOUR IMPACT"); Text("100+ animals fed", color = Color.White, fontSize = 24.sp)
                Text("Community donations supported the December food drive and emergency care.", color = Color(0xFFD5E5DB), fontSize = 12.sp)
                if (vm.totalDonated > 0) Text("Your contributions: ₱${vm.totalDonated} · ${vm.donations.size} donation(s)", color = Orange, modifier = Modifier.padding(top = 8.dp), fontSize = 13.sp)
            }
        } }
    }
    if (mode == "money") {
        var n by remember { mutableStateOf(vm.user?.name ?: "") }; var e by remember { mutableStateOf(vm.user?.email ?: "") }; var amt by remember { mutableStateOf("") }
        Sheet("Make a Donation", { mode = "" }) {
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { listOf("250", "500", "1000").forEach { a ->
                Box(Modifier.weight(1f).clip(RoundedCornerShape(10.dp)).background(if (amt == a) Green else Mint).clickable { amt = a }.padding(10.dp), Alignment.Center) { Text("₱$a", color = if (amt == a) Color.White else Green) } } }
            F("Full name", n, { n = it }, ph = "Your name"); F("Email address", e, { e = it }, kb = KeyboardType.Email, ph = "you@example.com")
            F("Custom amount", amt, { amt = it.filter(Char::isDigit) }, kb = KeyboardType.Number, ph = "Amount in PHP")
            Spacer(Modifier.height(16.dp))
            BigBtn("Donate securely") {
                val v = amt.toIntOrNull() ?: 0
                if (n.isBlank() || !e.contains("@") || v <= 0) c.toast("Enter your details and a valid amount")
                else { vm.totalDonated += v; vm.donations.add("₱$v"); c.toast("Thank you, $n! ₱$v donation recorded."); mode = "" }
            }
        }
    }
    if (mode == "supplies") {
        var n by remember { mutableStateOf(vm.user?.name ?: "") }; var e by remember { mutableStateOf(vm.user?.email ?: "") }; var items by remember { mutableStateOf("") }
        Sheet("Donate Supplies", { mode = "" }) {
            F("Full name", n, { n = it }, ph = "Your name"); F("Email address", e, { e = it }, kb = KeyboardType.Email, ph = "you@example.com")
            F("Items you wish to donate", items, { items = it }, 3, ph = "Pet food, medicine, blankets...")
            Spacer(Modifier.height(16.dp))
            BigBtn("Submit") {
                if (n.isBlank() || !e.contains("@") || items.isBlank()) c.toast("Please complete all fields")
                else { vm.donations.add(items); c.toast("Thank you! We'll contact you at $e"); mode = "" }
            }
        }
    }
}

// ---------- ACCOUNT ----------
@Composable fun Account(vm: Vm, back: () -> Unit, logout: () -> Unit) {
    val c = androidx.compose.ui.platform.LocalContext.current
    val u = vm.user ?: Profile()
    var edit by remember { mutableStateOf(false) }
    var n by remember(u) { mutableStateOf(u.name) }; var ph by remember(u) { mutableStateOf(u.phone) }; var fb by remember(u) { mutableStateOf(u.facebook) }
    var em by remember(u) { mutableStateOf(u.email) }; var ad by remember(u) { mutableStateOf(u.address) }
    Column(Modifier.fillMaxSize().imePadding().navigationBarsPadding().verticalScroll(rememberScrollState())) {
        Column(Modifier.fillMaxWidth().background(Green).statusBarsPadding().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Row(Modifier.fillMaxWidth()) { IconButton(back) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White) } }
            Label("MY ACCOUNT"); Text(u.name, color = Color.White, fontSize = 22.sp)
            Box(Modifier.padding(20.dp).size(90.dp).clip(CircleShape).background(Mint), Alignment.Center) { Icon(Icons.Filled.Person, null, Modifier.size(48.dp), tint = Green) }
        }
        Column(Modifier.padding(16.dp)) {
            if (vm.guest) Text("You're browsing as a guest. Log out and sign up to save your profile.", color = Color.Gray, fontSize = 12.sp)
            if (edit) { F("Full name", n, { n = it }); F("Phone number", ph, { ph = it }, kb = KeyboardType.Phone); F("Facebook", fb, { fb = it }); F("Email address", em, { em = it }, kb = KeyboardType.Email); F("Home address", ad, { ad = it }) }
            else listOf("Full name" to u.name, "Phone number" to u.phone, "Facebook" to u.facebook, "Email address" to u.email, "Home address" to u.address).forEach { (l, v) ->
                Column(Modifier.fillMaxWidth().padding(vertical = 8.dp)) { Text(l, fontSize = 11.sp, color = Color.Gray); Text(v.ifBlank { "Not set" }, color = if (v.isBlank()) Color.LightGray else Ink) }; HorizontalDivider() }
            Spacer(Modifier.height(16.dp))
            BigBtn(if (edit) "Save profile" else "Edit profile") { if (edit) { vm.user = Profile(n, em, ph, fb, ad); c.toast("Profile saved") }; edit = !edit }
            if (vm.applications.isNotEmpty()) Text("My adoption applications: ${vm.applications.joinToString()}", Modifier.padding(top = 14.dp), fontSize = 13.sp)
            if (vm.appointments.isNotEmpty()) Text("My appointments:\n" + vm.appointments.joinToString("\n"), Modifier.padding(top = 8.dp), fontSize = 13.sp)
            if (vm.favorites.isNotEmpty()) Text("Favorites: ${vm.pets.filter { it.id in vm.favorites }.joinToString { it.name }}", Modifier.padding(top = 8.dp), fontSize = 13.sp)
            TextButton({ vm.user = null; vm.guest = false; logout() }, Modifier.align(Alignment.CenterHorizontally)) { Text("Log out", color = Color(0xFFB3261E)) }
        }
    }
}