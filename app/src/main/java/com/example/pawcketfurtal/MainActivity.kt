@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.pawcketfurtal

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(
                colorScheme = lightColorScheme(
                    primary = Color(0xFFFF8A3D),
                    secondary = Color(0xFF4A90E2)
                )
            ) {
                PawcketFurtalApp()
            }
        }
    }
}

enum class Tab(val label: String, val filLabel: String, val icon: ImageVector) {
    Home("Home", "Home", Icons.Filled.Home),
    LostFound("Lost & Found", "Nawawala", Icons.Filled.Search),
    Adoption("Adoption", "Pag-ampon", Icons.Filled.Favorite)
}

data class Post(
    val id: Int,
    val title: String,
    val details: String,
    val contact: String,
    val mine: Boolean = false
)

data class Pet(
    val name: String,
    val species: String,
    val size: String,
    val energy: String,
    val about: String
)

data class Prefs(val species: String, val size: String, val energy: String)

@Composable
fun PawcketFurtalApp() {
    val ctx = LocalContext.current
    fun toast(msg: String) = Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show()

    var tab by remember { mutableStateOf(Tab.Home) }
    var user by remember { mutableStateOf<String?>(null) }
    var lang by remember { mutableStateOf("English") }
    var prefs by remember { mutableStateOf<Prefs?>(null) }
    var nextId by remember { mutableStateOf(4) }
    val requested = remember { mutableStateListOf<String>() }

    val posts = remember {
        mutableStateListOf(
            Post(1, "Lost: Brown Aspin", "Last seen near Burnham Park. Wearing a red collar.", "09171234567"),
            Post(2, "Found: Orange cat", "Found along Magsaysay. Very friendly, now with a neighbor.", "09181234567"),
            Post(3, "Lost: White Shih Tzu", "Last seen in Aurora Hill. Answers to Snow.", "09191234567")
        )
    }

    val pets = remember {
        listOf(
            Pet("Bantay", "Dog", "Medium", "Playful", "1 yr. Vaccinated. Good with kids."),
            Pet("Mingming", "Cat", "Small", "Calm", "6 months. Litter trained."),
            Pet("Choco", "Dog", "Large", "Calm", "3 yrs. Quiet and loves walks."),
            Pet("Muning", "Cat", "Medium", "Calm", "2 yrs. Shy at first, very cuddly later."),
            Pet("Pogi", "Dog", "Small", "Playful", "8 months. Loves to fetch.")
        )
    }

    var showAnnounce by remember { mutableStateOf(false) }
    var showLang by remember { mutableStateOf(false) }
    var showLogin by remember { mutableStateOf(false) }
    var showAbout by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }

    val fil = lang == "Filipino"

    if (showAnnounce) AnnouncementDialog(onDismiss = { showAnnounce = false })
    if (showAbout) AboutDialog(onDismiss = { showAbout = false })
    if (showLang) {
        LanguageDialog(
            current = lang,
            onDismiss = { showLang = false },
            onPick = {
                lang = it
                showLang = false
                toast("Language: $it")
            }
        )
    }
    if (showLogin) {
        LoginDialog(
            user = user,
            onDismiss = { showLogin = false },
            onLogin = {
                user = it
                showLogin = false
                toast("Welcome, $it")
            },
            onLogout = {
                user = null
                showLogin = false
                toast("Logged out")
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pawcket Furtal", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { showAnnounce = true }) {
                        Icon(Icons.Filled.Notifications, "Announcement")
                    }
                },
                actions = {
                    IconButton(onClick = { showLang = true }) {
                        Icon(Icons.Filled.Language, "Language")
                    }
                    IconButton(onClick = { showLogin = true }) {
                        Icon(
                            Icons.Filled.Person,
                            "Log in",
                            tint = if (user != null) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(Icons.Filled.Menu, "Menu")
                        }
                        DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                            DropdownMenuItem(
                                text = { Text("About Pawcket Furtal") },
                                onClick = {
                                    showMenu = false
                                    showAbout = true
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(if (user == null) "Log in" else "Log out") },
                                onClick = {
                                    showMenu = false
                                    if (user == null) showLogin = true
                                    else {
                                        user = null
                                        toast("Logged out")
                                    }
                                }
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                Tab.values().forEach {
                    NavigationBarItem(
                        selected = tab == it,
                        onClick = { tab = it },
                        icon = { Icon(it.icon, it.label) },
                        label = {
                            Text(if (fil) it.filLabel else it.label, fontSize = 11.sp, maxLines = 1)
                        }
                    )
                }
            }
        }
    ) { pad ->
        Box(Modifier.padding(pad).fillMaxSize()) {
            when (tab) {
                Tab.Home -> HomeScreen(
                    fil = fil,
                    user = user,
                    reportCount = posts.size,
                    go = { tab = it }
                )
                Tab.LostFound -> LostFoundScreen(
                    posts = posts,
                    onAdd = { title, details, contact ->
                        val id = nextId
                        nextId = id + 1
                        posts.add(0, Post(id, title, details, contact, true))
                        toast("Report posted")
                    },
                    onRemove = {
                        posts.remove(it)
                        toast("Marked as reunited")
                    }
                )
                Tab.Adoption -> AdoptionScreen(
                    pets = pets,
                    prefs = prefs,
                    requested = requested,
                    user = user,
                    onPrefs = { prefs = it },
                    onAdopt = {
                        requested.add(it.name)
                        toast("Adoption request sent for ${it.name}")
                    }
                )
            }
        }
    }
}

@Composable
fun Banner(title: String, subtitle: String, onClick: () -> Unit = {}) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(110.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFE0C7))
    ) {
        Column(
            Modifier.padding(16.dp).fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            Text(title, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(subtitle, fontSize = 13.sp)
        }
    }
}

@Composable
fun FilterRow(options: List<String>, selected: String, onSelect: (String) -> Unit) {
    Row(
        Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.forEach {
            FilterChip(
                selected = selected == it,
                onClick = { onSelect(it) },
                label = { Text(it) }
            )
        }
    }
}

@Composable
fun PostCard(
    title: String,
    details: String,
    extra: String? = null,
    actions: @Composable RowScope.() -> Unit
) {
    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
        Column(Modifier.padding(16.dp)) {
            Box(
                Modifier.fillMaxWidth().height(110.dp),
                contentAlignment = Alignment.Center
            ) { Icon(Icons.Filled.Pets, null, Modifier.size(56.dp), tint = Color.Gray) }
            Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(details, fontSize = 13.sp)
            if (extra != null) {
                Spacer(Modifier.height(4.dp))
                Text(
                    extra,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), content = actions)
        }
    }
}

@Composable
fun HomeScreen(fil: Boolean, user: String?, reportCount: Int, go: (Tab) -> Unit) {
    LazyColumn(
        Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            if (user != null) {
                Text("Hi, $user", fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
            }
            Text(
                if (fil) "Iuwi ang mga alaga nang mas mabilis" else "Bring pets home faster",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                if (fil) "Mag-ulat ng nawawala at nakitang pusa at aso, o mag-ampon, sa iisang lugar."
                else "Report lost and found cats and dogs, or adopt one, all in one place."
            )
            Spacer(Modifier.height(4.dp))
            Text(
                if (fil) "Aktibong ulat: $reportCount" else "Active reports: $reportCount",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
        item {
            Banner("Lost & Found", "Report or search missing pets") { go(Tab.LostFound) }
        }
        item {
            Banner("Adopt a Pet", "Find your new best friend") { go(Tab.Adoption) }
        }
        item {
            Text(
                if (fil) "Mabilis na Access" else "Quick Access",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
        items(listOf(Tab.LostFound, Tab.Adoption)) {
            Card(
                onClick = { go(it) },
                modifier = Modifier.fillMaxWidth().height(70.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    Modifier.fillMaxSize().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(it.icon, null)
                    Spacer(Modifier.width(12.dp))
                    Text(if (fil) it.filLabel else it.label, fontSize = 16.sp)
                }
            }
        }
        item {
            Text(
                if (fil) "Aming mga Layunin" else "Our Goals",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
        item {
            Card(
                Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFDCEBFB))
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("SDG 11: Sustainable Cities and Communities", fontWeight = FontWeight.Bold)
                    Text("Fewer stray pets means safer and more livable neighborhoods.", fontSize = 13.sp)
                    Spacer(Modifier.height(10.dp))
                    Text("SDG 3: Good Health and Well-being", fontWeight = FontWeight.Bold)
                    Text("Reuniting and adopting pets supports rabies control and mental health.", fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
fun LostFoundScreen(
    posts: List<Post>,
    onAdd: (String, String, String) -> Unit,
    onRemove: (Post) -> Unit
) {
    var query by remember { mutableStateOf("") }
    var filter by remember { mutableStateOf("All") }
    var showReport by remember { mutableStateOf(false) }
    var showScan by remember { mutableStateOf(false) }
    var contactPost by remember { mutableStateOf<Post?>(null) }

    val shown = posts.filter { p ->
        val okFilter = when (filter) {
            "All" -> true
            "Mine" -> p.mine
            else -> p.title.startsWith(filter)
        }
        okFilter && (p.title.contains(query, ignoreCase = true) ||
                p.details.contains(query, ignoreCase = true))
    }

    if (showReport) {
        ReportDialog(
            onDismiss = { showReport = false },
            onSubmit = { title, details, contact ->
                onAdd(title, details, contact)
                showReport = false
            }
        )
    }
    if (showScan) FaceScanDialog(posts = posts, onDismiss = { showScan = false })
    contactPost?.let { ContactDialog(post = it, onDismiss = { contactPost = null }) }

    LazyColumn(
        Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Banner("Lost & Found", "Help reunite pets with their owners") }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { showReport = true }) { Text("Report") }
                OutlinedButton(onClick = { showScan = true }) { Text("AI Pet Face Scan") }
            }
        }
        item {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search lost or found pets") },
                leadingIcon = { Icon(Icons.Filled.Search, null) },
                singleLine = true
            )
        }
        item { FilterRow(listOf("All", "Lost", "Found", "Mine"), filter) { filter = it } }
        if (shown.isEmpty()) {
            item { Text("No posts found.", color = Color.Gray) }
        }
        items(shown, key = { it.id }) { p ->
            PostCard(
                title = p.title,
                details = p.details,
                extra = if (p.mine) "Posted by you" else null
            ) {
                Button(onClick = { contactPost = p }) { Text("Contact") }
                if (p.mine) {
                    OutlinedButton(onClick = { onRemove(p) }) { Text("Mark reunited") }
                }
            }
        }
    }
}

@Composable
fun AdoptionScreen(
    pets: List<Pet>,
    prefs: Prefs?,
    requested: List<String>,
    user: String?,
    onPrefs: (Prefs?) -> Unit,
    onAdopt: (Pet) -> Unit
) {
    var filter by remember { mutableStateOf("All") }
    var showPrefs by remember { mutableStateOf(false) }
    var adoptPet by remember { mutableStateOf<Pet?>(null) }

    fun score(p: Pet): Int {
        val pr = prefs ?: return 0
        var s = 0
        if (pr.species == "Any" || pr.species == p.species) s++
        if (pr.size == p.size) s++
        if (pr.energy == p.energy) s++
        return s
    }

    val shown = pets
        .filter { filter == "All" || it.species == filter }
        .sortedByDescending { score(it) }

    if (showPrefs) {
        PrefsDialog(
            initial = prefs,
            onDismiss = { showPrefs = false },
            onApply = {
                onPrefs(it)
                showPrefs = false
            },
            onClear = {
                onPrefs(null)
                showPrefs = false
            }
        )
    }
    adoptPet?.let { pet ->
        AdoptDialog(
            pet = pet,
            defaultName = user ?: "",
            onDismiss = { adoptPet = null },
            onConfirm = {
                onAdopt(pet)
                adoptPet = null
            }
        )
    }

    LazyColumn(
        Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Banner("Adopt a Pet", "Give a pet a loving home") }
        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(onClick = { showPrefs = true }) { Text("AI Matchmaking Preference") }
                if (prefs != null) {
                    TextButton(onClick = { onPrefs(null) }) { Text("Clear") }
                }
            }
        }
        item { FilterRow(listOf("All", "Dog", "Cat"), filter) { filter = it } }
        items(shown, key = { it.name }) { p ->
            val extra = if (prefs == null) null
            else if (score(p) == 3) "Best match (3 of 3)"
            else "Match: ${score(p)} of 3"
            val done = requested.contains(p.name)
            PostCard(
                title = "${p.species}: ${p.name}",
                details = "${p.size}, ${p.energy}. ${p.about}",
                extra = extra
            ) {
                Button(onClick = { adoptPet = p }, enabled = !done) {
                    Text(if (done) "Requested" else "Adopt")
                }
            }
        }
    }
}

@Composable
fun AnnouncementDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Announcements") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Adoption drive this Saturday at Burnham Park.")
                Text("Tip: add a clear description and exact location when you report a pet.")
                Text("New: AI Pet Face Scan demo is now available in Lost & Found.")
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Close") } }
    )
}

@Composable
fun AboutDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("About Pawcket Furtal") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("One app to report lost and found cats and dogs, and to adopt a pet.")
                Text("SDG 11: Sustainable Cities and Communities", fontWeight = FontWeight.Bold)
                Text("SDG 3: Good Health and Well-being", fontWeight = FontWeight.Bold)
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Close") } }
    )
}

@Composable
fun LanguageDialog(current: String, onDismiss: () -> Unit, onPick: (String) -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Language") },
        text = { FilterRow(listOf("English", "Filipino"), current) { onPick(it) } },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Close") } }
    )
}

@Composable
fun LoginDialog(
    user: String?,
    onDismiss: () -> Unit,
    onLogin: (String) -> Unit,
    onLogout: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    if (user != null) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Account") },
            text = { Text("Logged in as $user") },
            confirmButton = { TextButton(onClick = onLogout) { Text("Log out") } },
            dismissButton = { TextButton(onClick = onDismiss) { Text("Close") } }
        )
    } else {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Log in") },
            text = {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Your name") },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(
                    enabled = name.isNotBlank(),
                    onClick = { onLogin(name.trim()) }
                ) { Text("Log in") }
            },
            dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
        )
    }
}

@Composable
fun ReportDialog(onDismiss: () -> Unit, onSubmit: (String, String, String) -> Unit) {
    var type by remember { mutableStateOf("Lost") }
    var pet by remember { mutableStateOf("") }
    var place by remember { mutableStateOf("") }
    var contact by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Report a pet") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterRow(listOf("Lost", "Found"), type) { type = it }
                OutlinedTextField(
                    value = pet,
                    onValueChange = { pet = it },
                    label = { Text("Pet (ex: Brown Aspin)") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = place,
                    onValueChange = { place = it },
                    label = { Text("Location") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = contact,
                    onValueChange = { contact = it },
                    label = { Text("Contact number") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                )
            }
        },
        confirmButton = {
            TextButton(
                enabled = pet.isNotBlank() && place.isNotBlank() && contact.isNotBlank(),
                onClick = { onSubmit("$type: ${pet.trim()}", "Location: ${place.trim()}", contact.trim()) }
            ) { Text("Post") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun FaceScanDialog(posts: List<Post>, onDismiss: () -> Unit) {
    var scanning by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) {
        delay(2000)
        scanning = false
    }
    val match = posts.firstOrNull { it.title.startsWith("Lost") }
    AlertDialog(
        onDismissRequest = { if (!scanning) onDismiss() },
        title = { Text("AI Pet Face Scan") },
        text = {
            if (scanning) {
                Column(
                    Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                    Spacer(Modifier.height(12.dp))
                    Text("Scanning pet face...")
                }
            } else if (match != null) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Possible match (87%)", fontWeight = FontWeight.Bold)
                    Text(match.title)
                    Text(match.details, fontSize = 13.sp)
                    Spacer(Modifier.height(6.dp))
                    Text("Demo result for the prototype.", fontSize = 12.sp, color = Color.Gray)
                }
            } else {
                Text("No match found.")
            }
        },
        confirmButton = {
            if (!scanning) {
                TextButton(onClick = onDismiss) { Text("Close") }
            }
        }
    )
}

@Composable
fun ContactDialog(post: Post, onDismiss: () -> Unit) {
    val ctx = LocalContext.current
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Contact poster") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(post.title, fontWeight = FontWeight.Bold)
                Text(post.details, fontSize = 13.sp)
                Spacer(Modifier.height(6.dp))
                Text("Number: ${post.contact}")
            }
        },
        confirmButton = {
            TextButton(onClick = {
                runCatching {
                    ctx.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:${post.contact}")))
                }
                onDismiss()
            }) { Text("Call") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Close") } }
    )
}

@Composable
fun PrefsDialog(
    initial: Prefs?,
    onDismiss: () -> Unit,
    onApply: (Prefs) -> Unit,
    onClear: () -> Unit
) {
    var species by remember { mutableStateOf(initial?.species ?: "Any") }
    var size by remember { mutableStateOf(initial?.size ?: "Small") }
    var energy by remember { mutableStateOf(initial?.energy ?: "Calm") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("AI Matchmaking") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Pet type")
                FilterRow(listOf("Any", "Dog", "Cat"), species) { species = it }
                Text("Size")
                FilterRow(listOf("Small", "Medium", "Large"), size) { size = it }
                Text("Energy")
                FilterRow(listOf("Calm", "Playful"), energy) { energy = it }
            }
        },
        confirmButton = {
            TextButton(onClick = { onApply(Prefs(species, size, energy)) }) { Text("Find matches") }
        },
        dismissButton = { TextButton(onClick = onClear) { Text("Clear") } }
    )
}

@Composable
fun AdoptDialog(
    pet: Pet,
    defaultName: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    var name by remember { mutableStateOf(defaultName) }
    var contact by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Adopt ${pet.name}") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Your name") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = contact,
                    onValueChange = { contact = it },
                    label = { Text("Contact number") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                )
            }
        },
        confirmButton = {
            TextButton(
                enabled = name.isNotBlank() && contact.isNotBlank(),
                onClick = onConfirm
            ) { Text("Send request") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}