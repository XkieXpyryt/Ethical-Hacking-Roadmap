@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package com.bernportal.ehr.ui.screens

import android.content.Context
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bernportal.ehr.data.db.Finding
import com.bernportal.ehr.data.db.Report
import com.bernportal.ehr.ui.LocalNav
import com.bernportal.ehr.ui.LocalVm
import com.bernportal.ehr.ui.components.*
import com.bernportal.ehr.ui.go
import com.bernportal.ehr.ui.theme.Brand
import com.bernportal.ehr.util.PdfBuilder
import com.bernportal.ehr.util.SecTools
import com.bernportal.ehr.util.rememberFileSaver
import kotlinx.coroutines.launch

val SEVERITIES = listOf("Critical", "High", "Medium", "Low", "Informational")
fun sevColor(s: String) = when (s) { "Critical" -> Color(0xFFDC2626); "High" -> Color(0xFFF97316); "Medium" -> Color(0xFFFBBF24); "Low" -> Color(0xFF4ADE80); else -> Color(0xFF94A3B8) }

@Composable
fun ReportsScreen() {
    val vm = LocalVm.current; val nav = LocalNav.current
    val u by vm.user.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    var newTitle by remember { mutableStateOf("") }
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item {
            GlassCard(glow = Brand.Gold) {
                Text("Professional penetration-testing report builder", style = MaterialTheme.typography.titleMedium)
                Text("Create reports for fictional organisations using lab-generated evidence. Drafts save offline automatically; export a professional PDF any time.", style = MaterialTheme.typography.bodySmall)
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 8.dp)) {
                    OutlinedTextField(newTitle, { newTitle = it }, label = { Text("New report title") }, singleLine = true, modifier = Modifier.weight(1f))
                    Spacer(Modifier.width(8.dp))
                    Button(onClick = { scope.launch { val id = vm.newReport(newTitle.ifBlank { "Untitled assessment" }); newTitle = ""; nav.go("report/$id") } }) { Icon(Icons.Filled.Add, null) }
                }
            }
        }
        if (u.reports.isEmpty()) item { EmptyState(Icons.Filled.Description, "No reports yet", "Create one above, or finish the Silverline capstone to get a pre-filled draft.") }
        items(u.reports) { r ->
            GlassCard(Modifier.fillMaxWidth(), onClick = { nav.go("report/${r.id}") }, padding = 12.dp) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Glossy3DIcon(Icons.Filled.Description, paletteFor(r.id.toInt()), 44.dp); Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(r.title, style = MaterialTheme.typography.titleSmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text("${r.client.ifBlank { "No client set" }} · updated ${PdfBuilder.date(r.updatedAt)}", style = MaterialTheme.typography.labelSmall)
                    }
                    IconButton(onClick = { vm.deleteReport(r.id) }) { Icon(Icons.Filled.Delete, "Delete report") }
                }
            }
        }
    }
}

@Composable
fun ReportEditor(id: Long) {
    val vm = LocalVm.current
    val u by vm.user.collectAsStateWithLifecycle()
    val report = u.reports.firstOrNull { it.id == id } ?: run { EmptyState(Icons.Filled.Description, "Report not found", ""); return }
    val findingsFlow = remember(id) { vm.findings(id) }
    val findings by findingsFlow.collectAsStateWithLifecycle(emptyList())
    val ctx = LocalContext.current
    val saver = rememberFileSaver("application/pdf")
    var r by remember(id) { mutableStateOf(report) }
    var editing by remember { mutableStateOf<Finding?>(null) }
    var tab by remember { mutableIntStateOf(0) }
    LaunchedEffect(r) { if (r != report) { kotlinx.coroutines.delay(600); vm.saveReport(r) } }

    Column {
        ScrollableTabRow(tab, edgePadding = 0.dp, containerColor = Color.Transparent) {
            listOf("Client & scope", "Findings (${findings.size})", "Summaries", "Remediation table", "Export").forEachIndexed { i, t -> Tab(tab == i, { tab = i }, text = { Text(t) }) }
        }
        LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            when (tab) {
                0 -> item { Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(r.title, { r = r.copy(title = it) }, label = { Text("Report title") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(r.client, { r = r.copy(client = it) }, label = { Text("Client (fictional organisation)") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(r.dates, { r = r.copy(dates = it) }, label = { Text("Testing dates") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(r.tester, { r = r.copy(tester = it) }, label = { Text("Tester / author") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(r.scope, { r = r.copy(scope = it) }, label = { Text("Scope (in scope / out of scope)") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
                    OutlinedTextField(r.methodology, { r = r.copy(methodology = it) }, label = { Text("Methodology") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
                    OutlinedTextField(r.limitations, { r = r.copy(limitations = it) }, label = { Text("Limitations") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
                    Text("Saved automatically on this device.", style = MaterialTheme.typography.labelSmall, color = Brand.Neon)
                } }
                1 -> {
                    item {
                        Button(onClick = { editing = Finding(reportId = id, title = "") }) { Icon(Icons.Filled.Add, null); Text(" Add finding") }
                    }
                    items(findings.sortedBy { SEVERITIES.indexOf(it.severity) }) { f ->
                        GlassCard(Modifier.fillMaxWidth(), glow = sevColor(f.severity), onClick = { editing = f }, padding = 12.dp) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Pill(f.severity, sevColor(f.severity)); Spacer(Modifier.width(8.dp))
                                Text(f.title, style = MaterialTheme.typography.titleSmall, modifier = Modifier.weight(1f))
                                Pill(f.status, Brand.Cyan)
                            }
                            Text(f.affected, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
                2 -> item { Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { r = r.copy(execSummary = genExec(r, findings), techSummary = genTech(findings)) }) { Icon(Icons.Filled.AutoAwesome, null); Text(" Generate both summaries from findings") }
                    OutlinedTextField(r.execSummary, { r = r.copy(execSummary = it) }, label = { Text("Executive summary") }, modifier = Modifier.fillMaxWidth(), minLines = 6)
                    OutlinedTextField(r.techSummary, { r = r.copy(techSummary = it) }, label = { Text("Technical summary") }, modifier = Modifier.fillMaxWidth(), minLines = 6)
                    Text("Generated text is a starting point - edit it into your own professional words.", style = MaterialTheme.typography.labelSmall)
                } }
                3 -> item { Column {
                    Row(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceVariant).padding(8.dp)) {
                        Text("Finding", Modifier.weight(2f), fontWeight = FontWeight.Bold); Text("Severity", Modifier.weight(1f), fontWeight = FontWeight.Bold)
                        Text("Owner", Modifier.weight(1f), fontWeight = FontWeight.Bold); Text("Due", Modifier.weight(1f), fontWeight = FontWeight.Bold)
                    }
                    findings.sortedBy { SEVERITIES.indexOf(it.severity) }.forEach { f ->
                        Row(Modifier.fillMaxWidth().padding(8.dp)) {
                            Text(f.title, Modifier.weight(2f), style = MaterialTheme.typography.bodySmall); Text(f.severity, Modifier.weight(1f), color = sevColor(f.severity))
                            Text(f.owner.ifBlank { "-" }, Modifier.weight(1f), style = MaterialTheme.typography.bodySmall); Text(f.due.ifBlank { "-" }, Modifier.weight(1f), style = MaterialTheme.typography.bodySmall)
                        }
                        HorizontalDivider()
                    }
                } }
                else -> item { Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    GlassCard(glow = Brand.Gold) {
                        Text("Export a professional PDF", style = MaterialTheme.typography.titleMedium)
                        Text("Includes cover, executive & technical summaries, scope, methodology, limitations, findings with evidence and remediation, a remediation table and appendices. Save it to your device or choose Google Drive in the system picker.",
                            style = MaterialTheme.typography.bodySmall)
                        Button(onClick = {
                            vm.saveReport(r)
                            saver.launch(r.title.replace(Regex("[^A-Za-z0-9 _-]"), "").ifBlank { "report" } + ".pdf") { reportPdf(ctx, r, findings) }
                        }, modifier = Modifier.padding(top = 8.dp)) { Icon(Icons.Filled.PictureAsPdf, null); Text(" Export PDF") }
                    }
                } }
            }
        }
    }

    editing?.let { f -> FindingDialog(f, onDismiss = { editing = null }, onSave = { vm.saveFinding(it); editing = null }, onDelete = { vm.deleteFinding(f.id); editing = null }) }
}

@Composable
private fun FindingDialog(f0: Finding, onDismiss: () -> Unit, onSave: (Finding) -> Unit, onDelete: () -> Unit) {
    val vm = LocalVm.current
    val u by vm.user.collectAsStateWithLifecycle()
    var f by remember { mutableStateOf(f0) }
    var pickEvidence by remember { mutableStateOf(false) }
    var showCvss by remember { mutableStateOf(false) }
    AlertDialog(onDismissRequest = onDismiss, title = { Text(if (f0.id == 0L) "New finding" else "Edit finding") }, text = {
        Column(Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            OutlinedTextField(f.title, { f = f.copy(title = it) }, label = { Text("Title") }, modifier = Modifier.fillMaxWidth())
            Text("Severity", style = MaterialTheme.typography.labelLarge)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) { SEVERITIES.forEach { s -> FilterChip(f.severity == s, { f = f.copy(severity = s) }, { Text(s) }) } }
            OutlinedTextField(f.cvss, { f = f.copy(cvss = it) }, label = { Text("CVSS score / vector") }, modifier = Modifier.fillMaxWidth(),
                trailingIcon = { IconButton(onClick = { showCvss = !showCvss }) { Icon(Icons.Filled.Calculate, "CVSS calculator") } })
            if (showCvss) {
                val parsed = SecTools.parseVector(f.cvss)
                if (parsed.size >= 8) {
                    val c = SecTools.cvss(parsed["AV"] ?: "N", parsed["AC"] ?: "L", parsed["PR"] ?: "N", parsed["UI"] ?: "N", parsed["S"] ?: "U", parsed["C"] ?: "N", parsed["I"] ?: "N", parsed["A"] ?: "N")
                    TextButton(onClick = { f = f.copy(cvss = "%.1f (%s)".format(c.score, c.vector), severity = c.severity.takeIf { it in SEVERITIES } ?: f.severity) }) { Text("Use %.1f %s".format(c.score, c.severity)) }
                } else Text("Paste a vector like CVSS:3.1/AV:N/AC:L/PR:N/UI:N/S:U/C:H/I:H/A:H or use Practice Centre > CVSS calculator.", style = MaterialTheme.typography.labelSmall)
            }
            OutlinedTextField(f.affected, { f = f.copy(affected = it) }, label = { Text("Affected assets") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(f.description, { f = f.copy(description = it) }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth(), minLines = 2)
            OutlinedTextField(f.evidence, { f = f.copy(evidence = it) }, label = { Text("Evidence (redacted, timestamped)") }, modifier = Modifier.fillMaxWidth(), minLines = 2)
            TextButton(onClick = { pickEvidence = !pickEvidence }) { Icon(Icons.Filled.AttachFile, null); Text(" Attach lab evidence") }
            if (pickEvidence) {
                val ev = u.notes.filter { it.refId.startsWith("lab:") }
                if (ev.isEmpty()) Text("Complete a lab first - transcripts are saved as evidence.", style = MaterialTheme.typography.labelSmall)
                ev.forEach { n -> TextButton(onClick = { f = f.copy(evidence = (f.evidence + "\n\n[" + n.refTitle + "]\n" + n.text.take(1500)).trim()); pickEvidence = false }) { Text(n.refTitle) } }
            }
            OutlinedTextField(f.impact, { f = f.copy(impact = it) }, label = { Text("Business impact") }, modifier = Modifier.fillMaxWidth(), minLines = 2)
            OutlinedTextField(f.remediation, { f = f.copy(remediation = it) }, label = { Text("Remediation (specific, verifiable)") }, modifier = Modifier.fillMaxWidth(), minLines = 2)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                OutlinedTextField(f.owner, { f = f.copy(owner = it) }, label = { Text("Owner") }, modifier = Modifier.weight(1f))
                OutlinedTextField(f.due, { f = f.copy(due = it) }, label = { Text("Due") }, modifier = Modifier.weight(1f))
            }
            Text("Status", style = MaterialTheme.typography.labelLarge)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                listOf("Open", "Fixed", "Partially fixed", "Not fixed", "Risk accepted", "Mitigated").forEach { s -> FilterChip(f.status == s, { f = f.copy(status = s) }, { Text(s) }) }
            }
        }
    }, confirmButton = { TextButton(onClick = { onSave(f) }, enabled = f.title.isNotBlank()) { Text("Save") } },
        dismissButton = { Row { if (f0.id != 0L) TextButton(onClick = onDelete) { Text("Delete", color = Brand.Danger) }; TextButton(onClick = onDismiss) { Text("Cancel") } } })
}

private fun counts(f: List<Finding>) = SEVERITIES.associateWith { s -> f.count { it.severity == s } }

fun genExec(r: Report, f: List<Finding>): String {
    val c = counts(f)
    val overall = when { c["Critical"]!! > 0 -> "CRITICAL"; c["High"]!! > 0 -> "HIGH"; c["Medium"]!! > 0 -> "MEDIUM"; else -> "LOW" }
    val top = f.sortedBy { SEVERITIES.indexOf(it.severity) }.take(3)
    return buildString {
        append("${r.client.ifBlank { "The client" }} engaged the assessor to evaluate the security of the in-scope systems${if (r.dates.isNotBlank()) " (${r.dates})" else ""}. ")
        append("Overall risk: $overall. The assessment identified ${f.size} findings: ${c["Critical"]} critical, ${c["High"]} high, ${c["Medium"]} medium, ${c["Low"]} low and ${c["Informational"]} informational.\n\n")
        if (top.isNotEmpty()) { append("Most significant issues:\n"); top.forEach { append("• ${it.title}: ${it.impact.ifBlank { it.description }}\n") } }
        append("\nPriority recommendations:\n")
        top.forEachIndexed { i, it -> append("${i + 1}. ${it.remediation.ifBlank { "Remediate " + it.title }}${if (it.due.isNotBlank()) " (target: ${it.due})" else ""}\n") }
        append("\nRetesting is recommended after remediation to verify fixes.")
    }
}

fun genTech(f: List<Finding>): String = buildString {
    append("Technical summary of ${f.size} findings, ordered by severity.\n")
    f.sortedBy { SEVERITIES.indexOf(it.severity) }.forEach { append("• [${it.severity}${if (it.cvss.isNotBlank()) " / CVSS ${it.cvss}" else ""}] ${it.title} - ${it.affected}\n") }
    val themes = mutableListOf<String>()
    if (f.any { it.title.contains("authori", true) || it.title.contains("IDOR", true) || it.title.contains("access", true) }) themes += "access control"
    if (f.any { it.title.contains("patch", true) || it.title.contains("KEV", true) || it.title.contains("outdated", true) || it.title.contains("version", true) }) themes += "patch management"
    if (f.any { it.title.contains("auth", true) || it.title.contains("password", true) || it.title.contains("MFA", true) }) themes += "authentication"
    if (themes.isNotEmpty()) append("\nRecurring themes: ${themes.joinToString()}.")
}

fun reportPdf(ctx: Context, r: Report, findings: List<Finding>): ByteArray {
    val sorted = findings.sortedBy { SEVERITIES.indexOf(it.severity) }
    val pdf = PdfBuilder(ctx, r.title)
    pdf.title(r.title, "Penetration Test Report · ${r.client.ifBlank { "Fictional client" }}")
        .kv("Testing dates", r.dates).kv("Prepared by", r.tester.ifBlank { "Learner" }).kv("Report date", PdfBuilder.today())
        .text("CONFIDENTIAL - Training report for a fictional organisation produced in Ethical Hacking Roadmap 2026/2027.", 9f, color = android.graphics.Color.GRAY)
        .heading("1. Executive summary").text(r.execSummary.ifBlank { genExec(r, findings) })
        .heading("2. Technical summary").text(r.techSummary.ifBlank { genTech(findings) })
        .heading("3. Scope").text(r.scope.ifBlank { "Not specified." })
        .heading("4. Methodology").text(r.methodology)
        .heading("5. Limitations").text(r.limitations)
        .heading("6. Findings overview")
        .table(listOf("#", "Finding", "Severity", "Status"), sorted.mapIndexed { i, f -> listOf("${i + 1}", f.title, f.severity, f.status) }, listOf(0.5f, 5f, 1.4f, 1.6f))
        .heading("7. Detailed findings")
    sorted.forEachIndexed { i, f ->
        pdf.text("${i + 1}. ${f.title}", 12.5f, true).kv("Severity", f.severity + if (f.cvss.isNotBlank()) "  (CVSS ${f.cvss})" else "")
            .kv("Affected", f.affected).kv("Status", f.status).text("Description", 10.5f, true).text(f.description)
            .text("Evidence", 10.5f, true).text(f.evidence.ifBlank { "See appendix." }).text("Impact", 10.5f, true).text(f.impact)
            .text("Remediation", 10.5f, true).text(f.remediation).space(8f)
    }
    pdf.heading("8. Remediation table")
        .table(listOf("Finding", "Severity", "Recommended action", "Owner", "Target"), sorted.map { listOf(it.title, it.severity, it.remediation.take(160), it.owner, it.due) }, listOf(3f, 1.2f, 4f, 1.3f, 1.2f))
        .heading("Appendix A - Severity ratings")
        .text("Critical: immediate risk of significant compromise. High: serious risk, fix urgently. Medium: fix in the normal cycle. Low: minor risk. Informational: good-practice observation. Ratings combine CVSS v3.1 base scores with exposure, known exploitation and asset criticality.")
        .heading("Appendix B - Retesting").text("Each finding should be retested after remediation using the original conditions and reasonable variants. Statuses: Fixed, Partially fixed, Not fixed, Risk accepted, Mitigated.")
    return pdf.build()
}

@Suppress("unused") private val keepClip = Modifier.clip(RoundedCornerShape(1.dp))
