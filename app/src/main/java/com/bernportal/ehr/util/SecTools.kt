package com.bernportal.ehr.util

import java.security.MessageDigest
import kotlin.math.floor
import kotlin.math.ln
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToLong

/** Offline, purely local calculators used by the Practice Centre. */
object SecTools {

    // ---------------- Subnet calculator ----------------
    data class Subnet(
        val ip: String, val prefix: Int, val mask: String, val wildcard: String, val network: String, val broadcast: String,
        val first: String, val last: String, val total: Long, val usable: Long, val isPrivate: Boolean, val binaryMask: String
    )

    private fun toInt(ip: String): Long? {
        val p = ip.trim().split(".")
        if (p.size != 4) return null
        var v = 0L
        for (s in p) { val n = s.toIntOrNull() ?: return null; if (n !in 0..255) return null; v = (v shl 8) or n.toLong() }
        return v
    }

    private fun toIp(v: Long) = "${(v shr 24) and 255}.${(v shr 16) and 255}.${(v shr 8) and 255}.${v and 255}"

    fun subnet(input: String): Subnet? {
        val parts = input.trim().split("/")
        val ip = toInt(parts[0]) ?: return null
        val prefix = when {
            parts.size == 2 && parts[1].contains(".") -> { val m = toInt(parts[1]) ?: return null; java.lang.Long.bitCount(m) }
            parts.size == 2 -> parts[1].toIntOrNull() ?: return null
            else -> 24
        }
        if (prefix !in 0..32) return null
        val mask = if (prefix == 0) 0L else (0xFFFFFFFFL shl (32 - prefix)) and 0xFFFFFFFFL
        val net = ip and mask
        val bc = net or (mask.inv() and 0xFFFFFFFFL)
        val total = 2.0.pow(32 - prefix).toLong()
        val usable = when (prefix) { 32 -> 1L; 31 -> 2L; else -> total - 2 }
        val first = if (prefix >= 31) net else net + 1
        val last = if (prefix >= 31) bc else bc - 1
        val priv = (ip shr 24 == 10L) || (ip shr 20 == 0xAC1L) || (ip shr 16 == 0xC0A8L)
        val bin = (0 until 32).joinToString("") { if ((mask shr (31 - it)) and 1L == 1L) "1" else "0" }.chunked(8).joinToString(".")
        return Subnet(toIp(ip), prefix, toIp(mask), toIp(mask.inv() and 0xFFFFFFFFL), toIp(net), toIp(bc), toIp(first), toIp(last), total, usable, priv, bin)
    }

    // ---------------- Number conversion ----------------
    fun convert(value: String, base: Int): Triple<String, String, String>? {
        val n = value.trim().removePrefix("0x").removePrefix("0b").toLongOrNull(base) ?: return null
        return Triple(n.toString(), java.lang.Long.toBinaryString(n), java.lang.Long.toHexString(n).uppercase())
    }

    fun textToHex(s: String) = s.toByteArray().joinToString(" ") { "%02X".format(it) }

    // ---------------- Hashing demo ----------------
    fun hash(algorithm: String, text: String): String =
        MessageDigest.getInstance(algorithm).digest(text.toByteArray()).joinToString("") { "%02x".format(it) }

    // ---------------- Password strength (educational estimate, never stored) ----------------
    data class Strength(val bits: Double, val label: String, val tips: List<String>)

    fun strength(pw: String): Strength {
        if (pw.isEmpty()) return Strength(0.0, "Empty", listOf("Type a sample passphrase - never use a real password here."))
        var pool = 0
        if (pw.any { it.isLowerCase() }) pool += 26
        if (pw.any { it.isUpperCase() }) pool += 26
        if (pw.any { it.isDigit() }) pool += 10
        if (pw.any { !it.isLetterOrDigit() }) pool += 33
        var bits = pw.length * (ln(pool.toDouble().coerceAtLeast(1.0)) / ln(2.0))
        val tips = ArrayList<String>()
        val lower = pw.lowercase()
        val common = listOf("password", "qwerty", "123456", "letmein", "welcome", "admin", "iloveyou", "monkey", "dragon", "football", "summer", "winter", "spring", "autumn")
        if (common.any { lower.contains(it) }) { bits *= 0.4; tips += "Contains a very common word/pattern found in breached-password lists." }
        if (Regex("(19|20)\\d{2}").containsMatchIn(pw)) { bits *= 0.85; tips += "Years are predictable - attackers add them automatically." }
        if (Regex("(.)\\1{2,}").containsMatchIn(pw)) { bits *= 0.8; tips += "Repeated characters add little strength." }
        if (pw.length < 12) tips += "Use at least 12-16 characters; a passphrase of 4+ random words is easier to remember."
        if (tips.isEmpty()) tips += "Good length and variety. Use a password manager and MFA as well."
        val label = when { bits < 28 -> "Very weak"; bits < 40 -> "Weak"; bits < 60 -> "Reasonable"; bits < 80 -> "Strong"; else -> "Very strong" }
        return Strength(bits, label, tips)
    }

    // ---------------- CVSS v3.1 base score ----------------
    data class Cvss(val score: Double, val severity: String, val vector: String)

    fun cvss(av: String, ac: String, pr: String, ui: String, s: String, c: String, i: String, a: String): Cvss {
        val avV = mapOf("N" to 0.85, "A" to 0.62, "L" to 0.55, "P" to 0.2)[av]!!
        val acV = if (ac == "L") 0.77 else 0.44
        val changed = s == "C"
        val prV = when (pr) { "N" -> 0.85; "L" -> if (changed) 0.68 else 0.62; else -> if (changed) 0.5 else 0.27 }
        val uiV = if (ui == "N") 0.85 else 0.62
        fun cia(x: String) = when (x) { "H" -> 0.56; "L" -> 0.22; else -> 0.0 }
        val iss = 1 - (1 - cia(c)) * (1 - cia(i)) * (1 - cia(a))
        val impact = if (changed) 7.52 * (iss - 0.029) - 3.25 * (iss - 0.02).pow(15) else 6.42 * iss
        val expl = 8.22 * avV * acV * prV * uiV
        val score = if (impact <= 0) 0.0 else if (changed) roundUp(min(1.08 * (impact + expl), 10.0)) else roundUp(min(impact + expl, 10.0))
        return Cvss(score, severity(score), "CVSS:3.1/AV:$av/AC:$ac/PR:$pr/UI:$ui/S:$s/C:$c/I:$i/A:$a")
    }

    private fun roundUp(x: Double): Double {
        val i = (x * 100000).roundToLong()
        return if (i % 10000 == 0L) i / 100000.0 else (floor(i / 10000.0) + 1) / 10.0
    }

    fun severity(score: Double) = when { score == 0.0 -> "None"; score < 4.0 -> "Low"; score < 7.0 -> "Medium"; score < 9.0 -> "High"; else -> "Critical" }

    fun parseVector(v: String): Map<String, String> =
        v.split("/").mapNotNull { p -> p.split(":").takeIf { it.size == 2 }?.let { it[0] to it[1] } }.toMap()

    // ---------------- Port reference ----------------
    data class PortInfo(val port: String, val service: String, val note: String, val secure: Boolean)

    val ports = listOf(
        PortInfo("20/21", "FTP", "Cleartext file transfer - replace with SFTP/FTPS", false),
        PortInfo("22", "SSH / SFTP", "Encrypted remote shell; use keys, disable root login", true),
        PortInfo("23", "Telnet", "Cleartext remote shell - disable", false),
        PortInfo("25", "SMTP", "Mail transfer between servers", false),
        PortInfo("53", "DNS", "UDP and TCP; restrict zone transfers", false),
        PortInfo("67/68", "DHCP", "Address assignment (UDP)", false),
        PortInfo("80", "HTTP", "Cleartext web - redirect to HTTPS", false),
        PortInfo("88", "Kerberos", "Active Directory authentication", true),
        PortInfo("110", "POP3", "Cleartext mail retrieval - use 995", false),
        PortInfo("123", "NTP", "Time sync - accurate logs depend on it", false),
        PortInfo("135/139/445", "RPC / NetBIOS / SMB", "Windows file sharing; require SMB signing, disable SMBv1", false),
        PortInfo("143", "IMAP", "Cleartext mail - use 993", false),
        PortInfo("161/162", "SNMP", "Network management; avoid v1/v2c 'public'", false),
        PortInfo("389/636", "LDAP / LDAPS", "Directory queries; enforce signing / use LDAPS", false),
        PortInfo("443", "HTTPS", "TLS-encrypted web", true),
        PortInfo("465/587", "SMTPS / Submission", "Encrypted mail submission", true),
        PortInfo("993/995", "IMAPS / POP3S", "Encrypted mail retrieval", true),
        PortInfo("1433", "Microsoft SQL Server", "Should not be internet-facing", false),
        PortInfo("3306", "MySQL / MariaDB", "Bind to localhost or app subnet", false),
        PortInfo("3389", "RDP", "Never expose directly to the internet; use VPN + NLA + MFA", false),
        PortInfo("5432", "PostgreSQL", "Restrict access", false),
        PortInfo("5985/5986", "WinRM", "Remote PowerShell; HTTPS on 5986", false),
        PortInfo("6379", "Redis", "Often unauthenticated - never expose", false),
        PortInfo("8080/8443", "Alt HTTP/HTTPS", "Admin consoles, proxies, CI tools", false),
        PortInfo("9200", "Elasticsearch", "Frequently exposed with data - require auth", false),
        PortInfo("27017", "MongoDB", "Require auth; bind privately", false),
    )
}
