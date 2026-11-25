@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package com.bernportal.ehr.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bernportal.ehr.data.Lesson
import com.bernportal.ehr.ui.LocalNav
import com.bernportal.ehr.ui.components.*
import com.bernportal.ehr.ui.go
import com.bernportal.ehr.ui.theme.Brand
import com.bernportal.ehr.ui.theme.LocalMotion
import com.bernportal.ehr.util.SecTools
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class SimInfo(val id: String, val title: String, val desc: String, val icon: ImageVector)

val SIMS = listOf(
    SimInfo("osi", "Animated OSI model", "Watch data encapsulate down the stack and back up", Icons.Filled.Layers),
    SimInfo("tcp", "TCP/IP stack & handshake", "SYN, SYN-ACK, ACK animated", Icons.Filled.SyncAlt),
    SimInfo("packet", "Packet journey", "Follow a packet hop by hop", Icons.Filled.Route),
    SimInfo("subnet", "Subnet calculator & CIDR visualiser", "Network, broadcast, hosts and bits", Icons.Filled.Calculate),
    SimInfo("dns", "DNS lookup simulator", "Resolver, root, TLD, authoritative", Icons.Filled.Dns),
    SimInfo("dhcp", "DHCP simulation", "Discover, Offer, Request, Acknowledge", Icons.Filled.SettingsEthernet),
    SimInfo("routing", "Routing simulator", "Longest-prefix match in a routing table", Icons.Filled.AltRoute),
    SimInfo("topology", "Network topology builder", "Build zones and get security feedback", Icons.Filled.AccountTree),
    SimInfo("ports", "Port explorer", "Common ports, services and risks", Icons.Filled.Lan),
    SimInfo("convert", "Binary / hex converter", "Number bases and text to hex", Icons.Filled.DataObject),
    SimInfo("hash", "Hashing vs encoding lab", "SHA-256, SHA-1, MD5 and Base64 compared", Icons.Filled.Fingerprint),
    SimInfo("password", "Password strength analyser", "Educational estimate - never stored", Icons.Filled.Password),
    SimInfo("cvss", "CVSS v3.1 calculator", "Score and severity from metrics", Icons.Filled.Speed),
    SimInfo("sqli", "SQL injection visualiser", "Concatenated vs parameterised query", Icons.Filled.Storage)
)

@Composable
fun PracticeScreen() {
    val nav = LocalNav.current
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item {
            GlassCard(glow = Brand.Neon) {
                Text("Interactive simulators", style = MaterialTheme.typography.titleMedium)
                Text("Hands-on, fully offline tools to practise networking and security concepts safely.", style = MaterialTheme.typography.bodySmall)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 8.dp)) {
                    OutlinedButton(onClick = { nav.go("labs?tab=0") }) { Text("Labs") }
                    OutlinedButton(onClick = { nav.go("labs?tab=2") }) { Text("Challenge Arena") }
                    OutlinedButton(onClick = { nav.go("quiz/daily") }) { Text("Daily quiz") }
                }
            }
        }
        itemsIndexed(SIMS) { i, s ->
            GlassCard(Modifier.fillMaxWidth(), glow = paletteFor(i).last(), onClick = { nav.go("sim/${s.id}") }, padding = 12.dp) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Glossy3DIcon(s.icon, paletteFor(i), 50.dp); Spacer(Modifier.width(12.dp))
                    Column { Text(s.title, style = MaterialTheme.typography.titleSmall); Text(s.desc, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                }
            }
        }
    }
}

@Composable
fun SimScreen(id: String) {
    val info = SIMS.firstOrNull { it.id == id }
    LazyColumn(contentPadding = PaddingValues(16.dp)) {
        item { Column {
            Breadcrumb(listOf("Practice Centre", info?.title ?: id))
            Pill("SIMULATION - offline and safe", Brand.Warn)
            Spacer(Modifier.height(10.dp))
            SimContent(id, null)
        } }
    }
}

@Composable
fun SimContent(id: String, lesson: Lesson?) {
    when (id) {
        "osi" -> OsiSim(); "tcp" -> TcpSim(); "packet" -> PacketSim(); "subnet" -> SubnetSim(); "dns" -> DnsSim(); "dhcp" -> DhcpSim()
        "routing" -> RoutingSim(); "topology" -> TopologySim(); "ports" -> PortsSim(); "convert" -> ConvertSim(); "hash" -> HashSim()
        "password" -> PasswordSim(); "cvss" -> CvssSim(); "sqli" -> SqliSim(); else -> StepsSim(lesson?.flow ?: listOf("Start", "Process", "Finish"))
    }
}

// ---------------- OSI ----------------
private val OSI = listOf(
    Triple("7 Application", "HTTP, DNS, SMTP", "Data"), Triple("6 Presentation", "TLS, encoding, compression", "Data"), Triple("5 Session", "Session setup & teardown", "Data"),
    Triple("4 Transport", "TCP/UDP, ports 443 -> 51234", "Segment"), Triple("3 Network", "IP 10.0.0.5 -> 93.184.215.14", "Packet"),
    Triple("2 Data Link", "Ethernet, MAC addresses", "Frame"), Triple("1 Physical", "Bits on copper, fibre or radio", "Bits")
)

@Composable
private fun OsiSim() {
    val motion = LocalMotion.current
    val anim = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()
    var selected by remember { mutableIntStateOf(-1) }
    var tcpip by remember { mutableStateOf(false) }
    val stage = anim.value // 0..14 : down 0-7, wire 7, up 7-14
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Button(onClick = { scope.launch { anim.snapTo(0f); anim.animateTo(14f, tween(if (motion.enabled) 7000 else 10, easing = LinearEasing)) } }) {
                Icon(Icons.Filled.PlayArrow, null); Text(" Send a web request")
            }
            Spacer(Modifier.weight(1f)); Text("TCP/IP view"); Switch(tcpip, { tcpip = it })
        }
        Spacer(Modifier.height(10.dp))
        Row {
            Column(Modifier.weight(1f)) {
                Text("Sender", style = MaterialTheme.typography.labelLarge, color = Brand.Cyan)
                OSI.forEachIndexed { i, (name, desc, unit) ->
                    val active = stage in i.toFloat()..(i + 1f)
                    LayerBox(if (tcpip) tcpName(i) else name, if (stage >= i) unit else "", active, i == selected) { selected = i }
                }
            }
            Spacer(Modifier.width(8.dp))
            Column(Modifier.weight(1f)) {
                Text("Receiver", style = MaterialTheme.typography.labelLarge, color = Brand.Violet)
                OSI.forEachIndexed { i, (name, _, unit) ->
                    val upIndex = 13 - i
                    val active = stage in upIndex.toFloat()..(upIndex + 1f)
                    LayerBox(if (tcpip) tcpName(i) else name, if (stage >= upIndex) unit else "", active, false) { selected = i }
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        val msg = when {
            stage <= 0f -> "Press the button to send data from a browser to a web server."
            stage < 7f -> "Encapsulation at ${OSI[stage.toInt().coerceIn(0, 6)].first}: a header is added (${OSI[stage.toInt().coerceIn(0, 6)].second})."
            stage < 7.5f -> "The bits travel across the network medium."
            stage < 14f -> "De-encapsulation at ${OSI[(13 - stage.toInt()).coerceIn(0, 6)].first}: the receiver strips that layer's header."
            else -> "Delivered! The application on the server receives the original data."
        }
        GlassCard { Text(msg) }
        if (selected >= 0) GlassCard(Modifier.padding(top = 8.dp), glow = Brand.Gold) {
            Text(OSI[selected].first, style = MaterialTheme.typography.titleSmall); Text(OSI[selected].second); Text("Data unit: ${OSI[selected].third}")
            Text("Security examples: " + listOf("XSS, SQLi, phishing", "Weak TLS, encoding tricks", "Session hijacking", "Port scanning, SYN floods", "IP spoofing, routing attacks", "ARP spoofing, MAC flooding", "Cable tapping, jamming")[selected],
                color = Brand.Danger, style = MaterialTheme.typography.bodySmall)
        }
    }
}

private fun tcpName(i: Int) = when (i) { 0, 1, 2 -> "Application"; 3 -> "Transport"; 4 -> "Internet"; else -> "Link" }

@Composable
private fun LayerBox(name: String, unit: String, active: Boolean, selected: Boolean, onClick: () -> Unit) {
    val motion = LocalMotion.current
    Row(
        Modifier.fillMaxWidth().padding(vertical = 2.dp).graphicsLayer { if (active && motion.effects3d) { rotationX = -12f; scaleX = 1.04f; scaleY = 1.04f } }
            .clip(RoundedCornerShape(8.dp))
            .background(if (active) Brush.horizontalGradient(listOf(Brand.Cyan, Brand.Violet)) else Brush.horizontalGradient(listOf(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.surfaceVariant)))
            .border(1.dp, if (selected) Brand.Gold else Color.Transparent, RoundedCornerShape(8.dp))
            .clickable(onClick = onClick).padding(8.dp)
    ) {
        Text(name, style = MaterialTheme.typography.labelMedium, modifier = Modifier.weight(1f), color = if (active) Color.Black else MaterialTheme.colorScheme.onSurface)
        Text(unit, style = MaterialTheme.typography.labelSmall, color = if (active) Color.Black else Brand.Gold)
    }
}

// ---------------- TCP handshake ----------------
@Composable
private fun TcpSim() {
    val steps = listOf("SYN  (seq=100)" to true, "SYN-ACK  (seq=300, ack=101)" to false, "ACK  (ack=301)" to true, "Data: GET / HTTP/1.1" to true, "Data: 200 OK" to false, "FIN / ACK - connection closes" to true)
    MessageSequence("Client 10.0.0.5:51234", "Server 93.184.215.14:443", steps,
        listOf("Client asks to open a connection and proposes a starting sequence number.", "Server agrees and sends its own sequence number, acknowledging the client's.",
            "Client acknowledges - the connection is ESTABLISHED.", "Application data flows reliably with acknowledgements.", "Server responds.", "Either side closes gracefully. A port scanner using SYN scans stops after step 2."))
}

@Composable
private fun MessageSequence(left: String, right: String, steps: List<Pair<String, Boolean>>, notes: List<String>) {
    val motion = LocalMotion.current
    var step by remember { mutableIntStateOf(0) }
    val prog = remember { Animatable(0f) }
    LaunchedEffect(step) { prog.snapTo(0f); prog.animateTo(1f, tween(motion.dur(900).coerceAtLeast(1))) }
    Column {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) { Glossy3DIcon(Icons.Filled.Computer, paletteFor(0), 48.dp); Text(left, style = MaterialTheme.typography.labelSmall) }
            Column(horizontalAlignment = Alignment.CenterHorizontally) { Glossy3DIcon(Icons.Filled.Storage, paletteFor(3), 48.dp); Text(right, style = MaterialTheme.typography.labelSmall) }
        }
        Canvas(Modifier.fillMaxWidth().height((steps.size * 34).dp)) {
            val l = size.width * 0.1f; val r = size.width * 0.9f
            drawLine(Brand.Cyan.copy(alpha = 0.5f), Offset(l, 0f), Offset(l, size.height), 3f)
            drawLine(Brand.Violet.copy(alpha = 0.5f), Offset(r, 0f), Offset(r, size.height), 3f)
            steps.take(step + 1).forEachIndexed { i, (_, toRight) ->
                val y = 17.dp.toPx() + i * 34.dp.toPx()
                val f = if (i == step) prog.value else 1f
                val (x0, x1) = if (toRight) l to r else r to l
                val xe = x0 + (x1 - x0) * f
                drawLine(if (toRight) Brand.Cyan else Brand.Violet, Offset(x0, y), Offset(xe, y), 4f)
                drawCircle(Brand.Gold, 7f, Offset(xe, y))
            }
        }
        steps.take(step + 1).forEachIndexed { i, (label, _) -> Text("${i + 1}. $label", fontFamily = FontFamily.Monospace, fontSize = 12.sp, color = if (i == step) Brand.Gold else MaterialTheme.colorScheme.onSurfaceVariant) }
        GlassCard(Modifier.padding(vertical = 8.dp)) { Text(notes[step]) }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = { step = 0 }) { Text("Restart") }
            Button(onClick = { if (step < steps.lastIndex) step++ }, enabled = step < steps.lastIndex) { Text("Next message") }
        }
    }
}

// ---------------- DNS / DHCP ----------------
@Composable
private fun DnsSim() {
    var domain by remember { mutableStateOf("portal.silverline.test") }
    Column {
        OutlinedTextField(domain, { domain = it.lowercase().trim() }, label = { Text("Domain to resolve (fictional)") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        val tld = domain.substringAfterLast(".", "test")
        val ip = "203.0.113.${(domain.hashCode() and 0x7f) + 10}"
        MessageSequence("Your laptop", "Resolver 10.10.10.53", listOf(
            "Query A $domain ?" to true, "Resolver -> Root (.): who handles .$tld?" to true, "Root: ask the .$tld TLD servers" to false,
            "Resolver -> .$tld TLD: who handles ${domain.substringAfter(".")}?" to true, "TLD: ask ns1.${domain.substringAfter(".")}" to false,
            "Resolver -> authoritative: A $domain?" to true, "Authoritative: $domain A $ip (TTL 300)" to false, "Resolver -> laptop: $ip (cached 300s)" to false
        ), listOf("Your device asks its configured resolver.", "The resolver starts at the root servers.", "Root servers refer to the TLD.", "The resolver asks the TLD server.",
            "TLD refers to the domain's authoritative name server.", "The resolver asks the authoritative server.", "The authoritative server returns the record and TTL.",
            "The answer is cached. Security: DNS spoofing, cache poisoning and tunnelling target this chain; DNSSEC and DoH/DoT help."))
    }
}

@Composable
private fun DhcpSim() {
    MessageSequence("New laptop (no IP)", "DHCP server 10.10.10.1", listOf(
        "DISCOVER (broadcast 255.255.255.255)" to true, "OFFER 10.10.10.57/24, gw .1, dns .53" to false, "REQUEST 10.10.10.57" to true, "ACK lease 24h" to false
    ), listOf("The client broadcasts because it has no address yet.", "A server offers an address and settings. A ROGUE server could offer a malicious gateway/DNS!",
        "The client requests the offered address.", "The server confirms the lease. Defence: DHCP snooping on switches blocks rogue servers."))
}

// ---------------- Packet journey ----------------
@Composable
private fun PacketSim() {
    val hops = listOf(
        Triple("Laptop", Icons.Filled.Laptop, "Builds the packet: src 192.168.1.20, dst 93.184.215.14, TCP 443."),
        Triple("Switch", Icons.Filled.DeviceHub, "Forwards the frame to the router's MAC (layer 2)."),
        Triple("Router/NAT", Icons.Filled.Router, "Rewrites the source to the public IP and routes it to the ISP (layer 3)."),
        Triple("Firewall", Icons.Filled.Shield, "Stateful firewall allows the outbound HTTPS connection and tracks it."),
        Triple("Internet", Icons.Filled.Public, "Many ISP routers forward it using BGP routes - encryption protects confidentiality."),
        Triple("Web server", Icons.Filled.Storage, "Receives the packet, decrypts TLS and processes the HTTP request.")
    )
    val motion = LocalMotion.current
    val t = rememberInfiniteTransition(label = "pj")
    val p by t.animateFloat(0f, hops.size.toFloat(), infiniteRepeatable(tween(hops.size * 1300, easing = LinearEasing)), label = "p")
    val cur = if (motion.enabled) p.toInt().coerceIn(0, hops.lastIndex) else 0
    var pinned by remember { mutableIntStateOf(-1) }
    val show = if (pinned >= 0) pinned else cur
    Column {
        Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), verticalAlignment = Alignment.CenterVertically) {
            hops.forEachIndexed { i, (n, ic, _) ->
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(72.dp).clickable { pinned = if (pinned == i) -1 else i }) {
                    Box(Modifier.graphicsLayer { val s = if (i == show) 1.15f else 1f; scaleX = s; scaleY = s }) { Glossy3DIcon(ic, if (i == show) paletteFor(6) else paletteFor(i), 46.dp) }
                    Text(n, style = MaterialTheme.typography.labelSmall, textAlign = TextAlign.Center)
                }
                if (i < hops.lastIndex) Icon(Icons.Filled.ArrowForward, null, tint = if (i < show) Brand.Gold else Brand.Muted)
            }
        }
        GlassCard(Modifier.padding(top = 10.dp), glow = Brand.Gold) { Text("Hop ${show + 1}: ${hops[show].first}", style = MaterialTheme.typography.titleSmall); Text(hops[show].third) }
        Text("Tap a hop to pin it.", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

// ---------------- Subnet + CIDR ----------------
@Composable
private fun SubnetSim() {
    var input by remember { mutableStateOf("10.10.10.77/27") }
    val r = SecTools.subnet(input)
    Column {
        OutlinedTextField(input, { input = it.trim() }, label = { Text("IP/prefix or IP/mask") }, singleLine = true, modifier = Modifier.fillMaxWidth(),
            isError = r == null, supportingText = { if (r == null) Text("Example: 192.168.1.130/25 or 172.16.4.9/255.255.252.0") })
        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) { listOf("/24", "/25", "/26", "/27", "/28", "/30").forEach { p -> AssistChip({ input = input.substringBefore("/") + p }, { Text(p) }) } }
        if (r != null) {
            GlassCard(Modifier.padding(vertical = 8.dp)) {
                listOf("Network" to "${r.network}/${r.prefix}", "Subnet mask" to r.mask, "Wildcard" to r.wildcard, "Broadcast" to r.broadcast,
                    "First usable" to r.first, "Last usable" to r.last, "Total addresses" to r.total.toString(), "Usable hosts" to r.usable.toString(),
                    "Private (RFC 1918)" to if (r.isPrivate) "Yes" else "No").forEach { (k, v) ->
                    Row { Text(k, Modifier.width(150.dp), color = MaterialTheme.colorScheme.onSurfaceVariant); Text(v, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold) }
                }
            }
            Text("CIDR visualiser - network bits (cyan) vs host bits (gold)", style = MaterialTheme.typography.labelLarge)
            Row(Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                for (b in 0 until 32) {
                    Box(Modifier.weight(1f).height(26.dp).padding(horizontal = 0.5.dp).clip(RoundedCornerShape(2.dp)).background(if (b < r.prefix) Brand.Cyan else Brand.Gold))
                    if (b % 8 == 7 && b < 31) Spacer(Modifier.width(3.dp))
                }
            }
            Text(r.binaryMask, fontFamily = FontFamily.Monospace, fontSize = 11.sp)
            Text("Block size in the interesting octet: ${256 - (r.mask.split(".").firstOrNull { it != "255" }?.toIntOrNull() ?: 0)}", style = MaterialTheme.typography.bodySmall)
        }
    }
}

// ---------------- Routing ----------------
@Composable
private fun RoutingSim() {
    val table = listOf("10.10.10.0/24" to "eth0 (direct)", "10.10.0.0/16" to "via 10.10.10.254", "10.0.0.0/8" to "via 10.10.10.253 (corporate WAN)", "0.0.0.0/0" to "via 10.10.10.1 (default gateway)")
    var dst by remember { mutableStateOf("10.10.20.5") }
    val ip = SecTools.subnet("$dst/32")
    val match = table.filter { (cidr, _) ->
        val n = SecTools.subnet(cidr); val probe = SecTools.subnet("$dst/${cidr.substringAfter("/")}")
        n != null && probe != null && probe.network == n.network
    }.maxByOrNull { it.first.substringAfter("/").toInt() }
    Column {
        OutlinedTextField(dst, { dst = it.trim() }, label = { Text("Destination IP") }, singleLine = true, isError = ip == null, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        table.forEach { (c, via) ->
            val hit = c == match?.first
            Row(Modifier.fillMaxWidth().padding(vertical = 2.dp).clip(RoundedCornerShape(8.dp)).background(if (hit) Brand.Neon.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant).padding(10.dp)) {
                Text(c, Modifier.width(140.dp), fontFamily = FontFamily.Monospace); Text(via, Modifier.weight(1f)); if (hit) Icon(Icons.Filled.CheckCircle, null, tint = Brand.Neon)
            }
        }
        GlassCard(Modifier.padding(top = 8.dp)) {
            Text(if (match != null) "Longest-prefix match: ${match.first} -> ${match.second}" else "Enter a valid IPv4 address.")
            Text("Routers choose the MOST SPECIFIC matching route. 0.0.0.0/0 (default) matches everything but is used only when nothing more specific matches.", style = MaterialTheme.typography.bodySmall)
        }
    }
}

// ---------------- Topology builder ----------------
@Composable
private fun TopologySim() {
    val zones = listOf("Internet", "DMZ", "Internal LAN", "Server VLAN", "Guest Wi-Fi")
    val devices = listOf("Router", "Firewall", "Switch", "Web server", "Database", "Workstation", "Access point", "Card terminal", "IoT camera")
    val placed = remember { mutableStateListOf("Internet:Router", "DMZ:Web server", "Internal LAN:Workstation") }
    var zone by remember { mutableStateOf(zones[1]) }
    Column {
        Text("Add a device to a zone", style = MaterialTheme.typography.labelLarge)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) { zones.forEach { z -> FilterChip(zone == z, { zone = z }, { Text(z) }) } }
        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) { devices.forEach { d -> AssistChip({ placed.add("$zone:$d") }, { Text("+ $d") }) } }
        Spacer(Modifier.height(8.dp))
        zones.forEachIndexed { i, z ->
            val items = placed.filter { it.startsWith("$z:") }
            GlassCard(Modifier.fillMaxWidth().padding(vertical = 4.dp), glow = paletteFor(i).last(), padding = 10.dp) {
                Text(z, style = MaterialTheme.typography.titleSmall, color = paletteFor(i).last())
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items.forEach { it2 ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { placed.remove(it2) }) {
                            Glossy3DIcon(deviceIcon(it2.substringAfter(":")), paletteFor(i), 40.dp); Text(it2.substringAfter(":"), style = MaterialTheme.typography.labelSmall)
                        }
                    }
                    if (items.isEmpty()) Text("(empty)", style = MaterialTheme.typography.labelSmall)
                }
            }
        }
        val warnings = buildList {
            if (placed.none { it.endsWith(":Firewall") }) add("No firewall: add one between the Internet and internal zones.")
            if (placed.any { it == "DMZ:Database" }) add("Database in the DMZ: keep databases in the server VLAN; only the web tier should be public-facing.")
            if (placed.any { it.startsWith("Guest Wi-Fi:") && (it.endsWith("Card terminal") || it.endsWith("Database") || it.endsWith("Workstation")) }) add("Sensitive device on guest Wi-Fi: separate guests completely.")
            if (placed.any { it.endsWith("IoT camera") && !it.startsWith("Guest") && !it.startsWith("Server") && it.startsWith("Internal LAN") }) add("IoT on the staff LAN: put IoT on its own isolated segment.")
            if (placed.any { it.startsWith("Internet:") && (it.endsWith("Workstation") || it.endsWith("Database")) }) add("Device placed directly on the Internet zone - it should sit behind a firewall.")
        }
        GlassCard(glow = if (warnings.isEmpty()) Brand.Neon else Brand.Warn) {
            Text(if (warnings.isEmpty()) "Design looks well segmented." else "Security review", style = MaterialTheme.typography.titleSmall)
            warnings.forEach { Text("• $it", style = MaterialTheme.typography.bodySmall) }
            Text("Tap a device to remove it.", style = MaterialTheme.typography.labelSmall)
        }
    }
}

private fun deviceIcon(d: String) = when (d) {
    "Router" -> Icons.Filled.Router; "Firewall" -> Icons.Filled.Shield; "Switch" -> Icons.Filled.DeviceHub; "Web server" -> Icons.Filled.Language
    "Database" -> Icons.Filled.Storage; "Workstation" -> Icons.Filled.Computer; "Access point" -> Icons.Filled.Wifi; "Card terminal" -> Icons.Filled.CreditCard
    else -> Icons.Filled.Videocam
}

// ---------------- Ports ----------------
@Composable
private fun PortsSim() {
    var q by remember { mutableStateOf("") }
    Column {
        OutlinedTextField(q, { q = it }, label = { Text("Search port or service") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        SecTools.ports.filter { q.isBlank() || it.port.contains(q) || it.service.contains(q, true) || it.note.contains(q, true) }.forEach { p ->
            Row(Modifier.fillMaxWidth().padding(vertical = 4.dp).clip(RoundedCornerShape(10.dp)).background(MaterialTheme.colorScheme.surfaceVariant).padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(p.port, Modifier.width(92.dp), fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                Column(Modifier.weight(1f)) { Text(p.service, style = MaterialTheme.typography.titleSmall); Text(p.note, style = MaterialTheme.typography.bodySmall) }
                Icon(if (p.secure) Icons.Filled.Lock else Icons.Filled.LockOpen, null, tint = if (p.secure) Brand.Neon else Brand.Warn)
            }
        }
    }
}

// ---------------- Converter ----------------
@Composable
private fun ConvertSim() {
    var v by remember { mutableStateOf("200") }
    var base by remember { mutableIntStateOf(10) }
    var text by remember { mutableStateOf("Hack") }
    val r = SecTools.convert(v, base)
    Column {
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) { listOf(10 to "Decimal", 2 to "Binary", 16 to "Hex").forEach { (b, l) -> FilterChip(base == b, { base = b }, { Text(l) }) } }
        OutlinedTextField(v, { v = it.trim() }, label = { Text("Value") }, singleLine = true, isError = r == null, modifier = Modifier.fillMaxWidth())
        if (r != null) GlassCard(Modifier.padding(vertical = 8.dp)) {
            Text("Decimal: ${r.first}", fontFamily = FontFamily.Monospace); Text("Binary: ${r.second.chunked(4).joinToString(" ")}", fontFamily = FontFamily.Monospace); Text("Hex: 0x${r.third}", fontFamily = FontFamily.Monospace)
        }
        Text("Place values: 128 64 32 16 8 4 2 1", style = MaterialTheme.typography.bodySmall)
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(text, { text = it }, label = { Text("Text to hex (ASCII/UTF-8)") }, modifier = Modifier.fillMaxWidth())
        MonoBlock(SecTools.textToHex(text))
    }
}

// ---------------- Hash ----------------
@Composable
private fun HashSim() {
    var text by remember { mutableStateOf("Silverline") }
    Column {
        OutlinedTextField(text, { text = it }, label = { Text("Input text (do not use real secrets)") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        listOf("SHA-256", "SHA-1", "MD5").forEach { a ->
            Text(a + if (a != "SHA-256") " (broken for collision resistance - do not use for security)" else "", style = MaterialTheme.typography.labelLarge)
            MonoBlock(SecTools.hash(a, text)); Spacer(Modifier.height(6.dp))
        }
        Text("Base64 (encoding - reversible, NOT protection)", style = MaterialTheme.typography.labelLarge)
        MonoBlock(android.util.Base64.encodeToString(text.toByteArray(), android.util.Base64.NO_WRAP))
        GlassCard(Modifier.padding(top = 8.dp), glow = Brand.Gold) {
            Text("Try changing a single character: the hash changes completely (avalanche effect). The same input always gives the same hash, so hashes prove integrity - but fast hashes like these must NOT be used alone for password storage. Use bcrypt, scrypt or Argon2 with a salt.",
                style = MaterialTheme.typography.bodySmall)
        }
    }
}

// ---------------- Password ----------------
@Composable
private fun PasswordSim() {
    var pw by remember { mutableStateOf("") }
    var show by remember { mutableStateOf(false) }
    val r = SecTools.strength(pw)
    Column {
        Text("Educational estimate only. Nothing you type is stored or sent. Never enter a real password.", color = Brand.Warn, style = MaterialTheme.typography.bodySmall)
        OutlinedTextField(pw, { pw = it }, label = { Text("Sample password or passphrase") }, singleLine = true, modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (show) androidx.compose.ui.text.input.VisualTransformation.None else androidx.compose.ui.text.input.PasswordVisualTransformation(),
            trailingIcon = { IconButton(onClick = { show = !show }) { Icon(if (show) Icons.Filled.VisibilityOff else Icons.Filled.Visibility, "Show") } })
        val frac = (r.bits / 100.0).toFloat().coerceIn(0f, 1f)
        LinearProgressIndicator({ frac }, Modifier.fillMaxWidth().padding(vertical = 8.dp).height(10.dp).clip(RoundedCornerShape(5.dp)),
            color = when { r.bits < 40 -> Brand.Danger; r.bits < 60 -> Brand.Warn; else -> Brand.Neon })
        Text("${r.label} · about ${r.bits.toInt()} bits of estimated entropy", style = MaterialTheme.typography.titleSmall)
        r.tips.forEach { Text("• $it", style = MaterialTheme.typography.bodySmall) }
    }
}

// ---------------- CVSS ----------------
@Composable
fun CvssSim() {
    val metrics = listOf(
        Triple("AV", "Attack Vector", listOf("N" to "Network", "A" to "Adjacent", "L" to "Local", "P" to "Physical")),
        Triple("AC", "Attack Complexity", listOf("L" to "Low", "H" to "High")),
        Triple("PR", "Privileges Required", listOf("N" to "None", "L" to "Low", "H" to "High")),
        Triple("UI", "User Interaction", listOf("N" to "None", "R" to "Required")),
        Triple("S", "Scope", listOf("U" to "Unchanged", "C" to "Changed")),
        Triple("C", "Confidentiality", listOf("H" to "High", "L" to "Low", "N" to "None")),
        Triple("I", "Integrity", listOf("H" to "High", "L" to "Low", "N" to "None")),
        Triple("A", "Availability", listOf("H" to "High", "L" to "Low", "N" to "None"))
    )
    val sel = remember { mutableStateMapOf("AV" to "N", "AC" to "L", "PR" to "N", "UI" to "N", "S" to "U", "C" to "H", "I" to "H", "A" to "H") }
    val r = SecTools.cvss(sel["AV"]!!, sel["AC"]!!, sel["PR"]!!, sel["UI"]!!, sel["S"]!!, sel["C"]!!, sel["I"]!!, sel["A"]!!)
    val col = when (r.severity) { "Critical" -> Color(0xFFDC2626); "High" -> Color(0xFFF97316); "Medium" -> Brand.Warn; "Low" -> Brand.Neon; else -> Brand.Muted }
    Column {
        GlassCard(glow = col) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("%.1f".format(r.score), style = MaterialTheme.typography.displaySmall, color = col, fontWeight = FontWeight.Black)
                Spacer(Modifier.width(12.dp))
                Column { Text(r.severity, style = MaterialTheme.typography.titleLarge, color = col); Text(r.vector, fontFamily = FontFamily.Monospace, fontSize = 11.sp) }
            }
        }
        metrics.forEach { (k, name, opts) ->
            Text(name, style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(top = 8.dp))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) { opts.forEach { (code, label) -> FilterChip(sel[k] == code, { sel[k] = code }, { Text(label) }) } }
        }
        Text("CVSS v3.1 base score per FIRST specification. Adjust priority further for exposure, CISA KEV listing and asset criticality.", style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 8.dp))
    }
}

// ---------------- SQLi visualiser ----------------
@Composable
private fun SqliSim() {
    var user by remember { mutableStateOf("alice") }
    val tautology = user.contains("'") && Regex("or\\s*'?1'?\\s*=\\s*'?1", RegexOption.IGNORE_CASE).containsMatchIn(user)
    Column {
        OutlinedTextField(user, { user = it }, label = { Text("Username input") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            AssistChip({ user = "alice" }, { Text("normal") }); AssistChip({ user = "' OR '1'='1" }, { Text("classic tautology") }); AssistChip({ user = "O'Brien" }, { Text("legit apostrophe") })
        }
        Text("v1 - string concatenation (vulnerable)", style = MaterialTheme.typography.labelLarge, color = Brand.Danger, modifier = Modifier.padding(top = 8.dp))
        MonoBlock("SELECT * FROM users WHERE name = '$user' AND pw_hash = '...';", color = Color(0xFFFCA5A5))
        Text(when { tautology -> "Result (simulated): WHERE clause is always true -> logged in as the first user (admin)."; user.contains("'") -> "Result (simulated): SQL syntax error - the quote broke the query (a classic detection sign)."; else -> "Result (simulated): normal lookup for '$user'." },
            color = if (user.contains("'")) Brand.Danger else MaterialTheme.colorScheme.onSurface)
        Text("v2 - parameterised query (safe)", style = MaterialTheme.typography.labelLarge, color = Brand.Neon, modifier = Modifier.padding(top = 8.dp))
        MonoBlock("SELECT * FROM users WHERE name = ? AND pw_hash = ?;\nparams = [\"${user.replace("\"", "\\\"")}\", \"...\"]")
        Text("Result (simulated): the database looks for a user literally named \"$user\" - the input is data, never code.", color = Brand.Neon)
    }
}

// ---------------- Generic step-through ----------------
@Composable
private fun StepsSim(steps: List<String>) {
    var i by remember(steps) { mutableIntStateOf(0) }
    Column {
        FlowDiagram(steps.take(i + 1))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 8.dp)) {
            OutlinedButton(onClick = { i = 0 }) { Text("Restart") }
            Button(onClick = { if (i < steps.lastIndex) i++ }, enabled = i < steps.lastIndex) { Text("Next step") }
        }
    }
}

@Suppress("unused") private suspend fun pause() = delay(1)
