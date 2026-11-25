GLOSSARY = []


def G(term, category, definition, example, related, lesson, lab=""):
    GLOSSARY.append({"term": term, "category": category, "definition": definition, "example": example, "related": related, "lesson": lesson, "lab": lab})


G("Access point", "Networking", "A device that connects wireless clients to a wired network.", "The ceiling-mounted Wi-Fi units in an office.", ["SSID", "WPA3"], "m02l1")
G("Active Directory", "Identity", "Microsoft's directory service that stores users, computers and groups and manages authentication for a Windows domain.", "Staff log in to any company PC with one domain account.", ["Domain controller", "Kerberos", "Group Policy"], "m03l8", "lab_ad_review")
G("AES", "Cryptography", "Advanced Encryption Standard - the most widely used symmetric encryption algorithm.", "BitLocker and TLS commonly use AES.", ["Symmetric encryption", "TLS"], "m05l2")
G("Allow-list", "Defence", "A list of explicitly permitted items; anything not on it is rejected.", "Only accept file types PNG and JPG.", ["Input validation"], "m04l7")
G("API", "Web", "Application Programming Interface - a defined way for programs to exchange data and requests.", "A mobile app calling /api/v1/orders.", ["REST", "BOLA"], "m09l6")
G("APIPA", "Networking", "Automatic Private IP Addressing (169.254.x.x) used when DHCP fails.", "A PC showing 169.254.20.5 cannot reach DHCP.", ["DHCP"], "m02l2")
G("ARP", "Networking", "Address Resolution Protocol - maps IP addresses to MAC addresses on a local network.", "'Who has 192.168.1.1?'", ["MAC address", "ARP spoofing"], "m02l5")
G("ASLR", "Systems", "Address Space Layout Randomisation - randomises memory locations to make exploits harder.", "Enabled by default in modern Windows, Linux, Android.", ["DEP", "Exploit"], "m01l4")
G("Asset criticality", "Risk", "How important an asset is to the business, used to prioritise protection and remediation.", "Payment database = critical; test printer = low.", ["Risk", "Prioritisation"], "m08l3")
G("Attack surface", "Risk", "All points where an attacker could try to interact with or extract data from a system.", "Open ports, web forms, staff email.", ["Exposure"], "m05l3")
G("Authentication", "Identity", "Verifying who a user or system is.", "Password plus authenticator app.", ["Authorisation", "MFA"], "m05l1")
G("Authorisation", "Identity", "Deciding what an authenticated identity is allowed to do.", "Only managers can approve refunds.", ["Authentication", "Access control"], "m05l1")
G("Base64", "Data", "An encoding that turns binary data into printable text. Not encryption.", "aGVsbG8= decodes to 'hello'.", ["Encoding"], "m01l3")
G("Binary", "Data", "Base-2 number system using 0 and 1.", "200 = 11001000.", ["Hexadecimal", "Bit"], "m01l3")
G("BOLA", "Web", "Broken Object Level Authorisation - an API returns objects the user does not own.", "/api/orders/1002 returns someone else's order.", ["IDOR", "API"], "m09l6")
G("Broken access control", "Web", "Failures that let users act outside their permissions.", "Normal user reaches /admin.", ["IDOR", "Privilege escalation"], "m09l2", "lab_web_sec")
G("Burp Suite", "Tools", "Intercepting proxy and web testing platform by PortSwigger.", "Repeater used to modify requests.", ["OWASP ZAP", "Proxy"], "m09l7")
G("CIA triad", "Fundamentals", "Confidentiality, Integrity and Availability - the core goals of security.", "Ransomware attacks availability.", ["Confidentiality", "Integrity", "Availability"], "m05l1")
G("CIDR", "Networking", "Classless Inter-Domain Routing notation, e.g. /24, giving the number of network bits.", "10.0.0.0/8.", ["Subnet mask"], "m02l3", "lab_subnet")
G("Client-server", "Fundamentals", "Model where clients request services and servers provide them.", "Browser and web server.", ["Server"], "m01l6")
G("Cloud computing", "Cloud", "Renting computing resources and services from a provider over the internet.", "Running VMs in AWS.", ["IaaS", "SaaS", "Shared responsibility model"], "m01l6")
G("Container", "Systems", "A packaged application isolated using kernel features while sharing the host kernel.", "A Docker container running a web app.", ["Docker", "Kubernetes"], "m01l5", "lab_container")
G("Cookie", "Web", "Small data stored by the browser and sent with requests to the same site.", "Session ID cookie.", ["Session", "HttpOnly"], "m09l1")
G("Credential Guard", "Defence", "Windows feature using virtualisation to protect stored credentials.", "Blocks LSASS secret theft.", ["LSASS"], "m10l3")
G("CSP", "Web", "Content Security Policy - header restricting where scripts and resources can load from.", "script-src 'self'.", ["XSS"], "m04l5")
G("CSRF", "Web", "Cross-Site Request Forgery - tricking a logged-in user's browser into sending unwanted requests.", "Hidden form changes a victim's email.", ["SameSite", "Anti-CSRF token"], "m09l4")
G("CVE", "Vulnerability", "Common Vulnerabilities and Exposures - unique ID for a public vulnerability.", "CVE-2021-44228 (Log4Shell).", ["CVSS", "NVD"], "m08l2")
G("CVSS", "Vulnerability", "Common Vulnerability Scoring System - 0-10 severity score.", "9.8 Critical.", ["CVE", "Severity"], "m08l2", "lab_cvss")
G("CWE", "Vulnerability", "Common Weakness Enumeration - catalogue of weakness types.", "CWE-89 SQL injection.", ["CVE"], "m13l5")
G("Defence in depth", "Defence", "Layering multiple controls so one failure does not cause a breach.", "Filtering + MFA + EDR + backups.", ["Zero trust"], "m05l4")
G("DEP", "Systems", "Data Execution Prevention / NX - marks memory as non-executable.", "Stops code running from the stack.", ["ASLR"], "m01l4")
G("DHCP", "Networking", "Dynamic Host Configuration Protocol - automatically assigns IP settings.", "DORA process.", ["APIPA"], "m02l5")
G("Digital signature", "Cryptography", "Cryptographic proof of origin and integrity created with a private key.", "Signed software updates.", ["PKI", "Hash"], "m05l2")
G("Directory traversal", "Web", "Using ../ sequences to access files outside the intended folder.", "file=../../etc/passwd.", ["Input validation"], "m09l5", "lab_web_sec")
G("DMARC", "Email", "Email authentication policy telling receivers how to handle mail failing SPF/DKIM.", "p=reject blocks spoofing.", ["SPF"], "m06l2", "lab_recon_silverline")
G("DMZ", "Networking", "Network zone for public-facing servers, separated from the internal network.", "Web server in DMZ.", ["Segmentation", "Firewall"], "m02l6")
G("DNS", "Networking", "Domain Name System - translates names to IP addresses.", "example.com -> 93.184.215.14.", ["MX record", "Resolver"], "m02l5", "lab_net_basics")
G("Docker", "Systems", "Popular container platform for building and running containers.", "docker run nginx.", ["Container", "Image"], "m13l3", "lab_container")
G("Domain controller", "Identity", "Server running Active Directory services and authentication.", "DC01.", ["Active Directory", "Kerberos"], "m03l8")
G("EDR", "Defence", "Endpoint Detection and Response - monitors endpoints for malicious behaviour and enables response.", "Isolating a laptop remotely.", ["SIEM"], "m12l4")
G("Encryption", "Cryptography", "Transforming data so only holders of the key can read it.", "HTTPS encrypts web traffic.", ["AES", "RSA"], "m05l2")
G("EPSS", "Vulnerability", "Exploit Prediction Scoring System - probability a vulnerability will be exploited.", "EPSS 0.92 = very likely.", ["KEV", "CVSS"], "m08l2")
G("Evil twin", "Wireless", "A fake access point imitating a legitimate SSID.", "'CoffeeShop-WiFi' clone.", ["Rogue access point"], "m11l2", "lab_wireless")
G("Exploit", "Vulnerability", "A technique or code that takes advantage of a vulnerability.", "PoC for a CVE.", ["Payload", "Proof of concept"], "m12l1")
G("False positive", "Vulnerability", "A reported issue that does not actually exist.", "Scanner flags a backported package.", ["False negative", "Validation"], "m08l4")
G("False negative", "Vulnerability", "A real issue that a tool failed to detect.", "Issue behind login missed.", ["False positive"], "m08l4")
G("File integrity monitoring", "Defence", "Detecting changes to important files by comparing hashes to a baseline.", "Alert when sshd_config changes.", ["Hash"], "m04l7")
G("Firewall", "Networking", "A control that allows or blocks traffic according to rules.", "Default deny inbound.", ["Segmentation"], "m02l6")
G("GDPR", "Law", "EU/UK regulation governing processing of personal data.", "72-hour breach notification.", ["Data minimisation"], "m05l5")
G("Ghidra", "Tools", "Free reverse engineering suite with disassembler and decompiler from the NSA.", "Analysing a suspicious binary.", ["Reverse engineering"], "m13l6")
G("Group Policy", "Identity", "Mechanism to push settings to Windows computers and users in AD.", "Enforce firewall via GPO.", ["Active Directory"], "m03l8")
G("Hardening", "Defence", "Reducing attack surface and strengthening configuration.", "Disable unused services.", ["CIS Benchmark"], "m03l5")
G("Hash", "Cryptography", "A one-way function producing a fixed-length fingerprint of data.", "SHA-256.", ["Salt", "Integrity"], "m05l2")
G("Hexadecimal", "Data", "Base-16 number system using 0-9 and A-F.", "0xFF = 255.", ["Binary"], "m01l3")
G("HSTS", "Web", "HTTP Strict Transport Security - forces browsers to use HTTPS for a site.", "Strict-Transport-Security: max-age=31536000.", ["TLS"], "m09l1")
G("HttpOnly", "Web", "Cookie flag that prevents JavaScript from reading the cookie.", "Protects session IDs from XSS theft.", ["Cookie", "Secure flag"], "m04l5")
G("Hypervisor", "Systems", "Software that creates and runs virtual machines.", "VirtualBox, Hyper-V.", ["Virtual machine"], "m01l5")
G("IaaS", "Cloud", "Infrastructure as a Service - renting virtual machines, storage and networks.", "AWS EC2.", ["PaaS", "SaaS"], "m01l6")
G("IAM", "Cloud", "Identity and Access Management - who can do what to which resources.", "AWS IAM policies.", ["Least privilege"], "m13l2", "lab_cloud_iam")
G("ICMP", "Networking", "Internet Control Message Protocol - used for diagnostics like ping.", "Echo request/reply.", ["Ping"], "m02l4")
G("IDOR", "Web", "Insecure Direct Object Reference - accessing objects by changing identifiers without authorisation checks.", "invoice?id=1002.", ["BOLA"], "m09l2", "lab_web_sec")
G("Incident response", "Defence", "Organised approach to handling security incidents.", "NIST lifecycle.", ["Containment"], "m05l5", "lab_incident_logs")
G("Indicator of compromise", "Defence", "Evidence that a system may have been breached.", "Malicious hash or domain.", ["IOC"], "m12l3")
G("Input validation", "Development", "Checking that input matches expected format before use.", "Reject non-numeric IDs.", ["Allow-list"], "m04l7")
G("IPv4", "Networking", "32-bit IP addressing.", "192.168.1.10.", ["IPv6", "CIDR"], "m02l2")
G("IPv6", "Networking", "128-bit IP addressing written in hex.", "2001:db8::1.", ["IPv4"], "m02l2")
G("JWT", "Web", "JSON Web Token - signed token carrying claims.", "Bearer eyJhbGci...", ["OAuth 2.0"], "m13l4")
G("Kali Linux", "Tools", "Debian-based Linux distribution with security testing tools.", "Lab attacker VM.", ["Parrot Security OS"], "m03l1")
G("Kerberos", "Identity", "Ticket-based authentication protocol used by Active Directory.", "TGT and service tickets.", ["Kerberoasting", "KDC"], "m10l3")
G("Kerberoasting", "Identity", "Requesting service tickets for accounts with SPNs and testing them offline for weak passwords.", "svc_sql with old password.", ["Kerberos", "gMSA"], "m10l3")
G("Kernel", "Systems", "The core of an operating system with full hardware control.", "Linux kernel.", ["Operating system"], "m01l4")
G("KEV", "Vulnerability", "CISA Known Exploited Vulnerabilities catalogue.", "VPN CVE listed in KEV.", ["EPSS", "CVE"], "m08l2")
G("Kubernetes", "Systems", "Platform that orchestrates containers at scale.", "Pods, services, RBAC.", ["Container"], "m13l3")
G("LAPS", "Identity", "Local Administrator Password Solution - unique rotated local admin passwords per machine.", "Stops lateral movement via shared passwords.", ["Lateral movement"], "m03l8")
G("Lateral movement", "Attack concepts", "Moving from one compromised system to others in a network.", "Reusing admin credentials across PCs.", ["Segmentation", "LAPS"], "m10l5")
G("Least privilege", "Defence", "Giving identities only the access they need.", "Read-only audit role.", ["Zero trust"], "m05l4")
G("MAC address", "Networking", "48-bit hardware address of a network interface.", "3C:22:FB:1A:9E:04.", ["ARP"], "m02l2")
G("Malware", "Threats", "Malicious software such as ransomware, trojans or worms.", "Ransomware encrypting files.", ["IOC"], "m13l6")
G("Maltego", "Tools", "Graph-based link analysis and OSINT tool.", "Mapping domains to IPs.", ["OSINT"], "m06l4")
G("MASVS", "Mobile", "OWASP Mobile Application Security Verification Standard.", "STORAGE, NETWORK groups.", ["MobSF"], "m11l4", "lab_mobile")
G("Metadata", "Recon", "Data about data, e.g. document author or GPS coordinates.", "PDF author field.", ["OSINT"], "m06l3")
G("Metasploit", "Tools", "Open-source exploitation framework used for controlled validation.", "Using a check module in a lab.", ["Exploit"], "m10l5")
G("MFA", "Identity", "Multi-factor authentication - two or more different factor types.", "Password + security key.", ["FIDO2"], "m05l1")
G("MITRE ATT&CK", "Frameworks", "Knowledge base of adversary tactics and techniques.", "TA0008 Lateral Movement.", ["TTP"], "m05l3")
G("MobSF", "Tools", "Mobile Security Framework - free automated mobile app analysis.", "Static analysis of an APK.", ["MASVS"], "m11l4")
G("NAT", "Networking", "Network Address Translation - maps private addresses to a public address.", "Home router NAT.", ["Private IP"], "m02l6")
G("Nmap", "Tools", "Network scanner for host discovery, port scanning and service detection.", "nmap -sV 10.10.10.15.", ["Port scan"], "m07l3", "lab_scan_sim")
G("NTLM", "Identity", "Legacy Windows challenge-response authentication protocol.", "Fallback when Kerberos unavailable.", ["Kerberos", "SMB signing"], "m10l3")
G("OAuth 2.0", "Web", "Authorisation framework for delegated access using tokens.", "Authorization Code + PKCE.", ["JWT", "OpenID Connect"], "m13l4")
G("OSI model", "Networking", "Seven-layer model describing network communication.", "Layer 3 = Network.", ["TCP/IP model"], "m02l4")
G("OSINT", "Recon", "Open-source intelligence gathered from public information.", "Certificate transparency logs.", ["Passive reconnaissance"], "m06l1", "lab_recon_silverline")
G("OWASP", "Frameworks", "Open Worldwide Application Security Project - community producing free security resources.", "OWASP Top 10.", ["WSTG"], "m09l7")
G("OWASP ZAP", "Tools", "Free open-source web proxy and scanner.", "Passive scan of a lab app.", ["Burp Suite"], "m09l7")
G("Parameterised query", "Development", "Database query where input is passed separately from SQL code.", "SELECT ... WHERE id = ?", ["SQL injection"], "m04l6")
G("Patch management", "Defence", "Process of identifying, testing and deploying software updates.", "Monthly Patch Tuesday cycle.", ["Vulnerability management"], "m08l5")
G("Payload", "Attack concepts", "Code or action executed after successful exploitation.", "In validation: a benign whoami.", ["Exploit"], "m12l1")
G("PCAP", "Networking", "Packet capture file format.", "clinic.pcap.", ["Wireshark"], "m02l7", "lab_pcap")
G("Penetration test", "Methodology", "Authorised, scoped simulated attack to find and demonstrate weaknesses.", "Internal network test.", ["Rules of engagement"], "m10l1")
G("Phishing", "Threats", "Deceptive messages tricking people into revealing data or running malware.", "Fake invoice email.", ["Social engineering"], "m06l3")
G("PKI", "Cryptography", "Public Key Infrastructure - CAs and certificates establishing trust.", "Website certificates.", ["Certificate"], "m05l2")
G("Port", "Networking", "Number identifying a service on a host (0-65535).", "443 = HTTPS.", ["TCP", "UDP"], "m02l4")
G("PowerShell", "Systems", "Windows shell and scripting language based on objects.", "Get-Service.", ["Cmdlet"], "m03l7", "lab_win_ps")
G("Privilege escalation", "Attack concepts", "Gaining higher permissions than originally granted.", "User to root via SUID binary.", ["Least privilege"], "m10l4")
G("Proof of concept", "Vulnerability", "Demonstration that a vulnerability can be exploited.", "Harmless marker file in a lab.", ["Exploit"], "m12l1")
G("RDP", "Networking", "Remote Desktop Protocol, TCP 3389.", "Exposed RDP is high risk.", ["Port"], "m02l4")
G("Reconnaissance", "Recon", "Gathering information about a target.", "DNS record review.", ["OSINT"], "m06l1")
G("Registry", "Systems", "Windows hierarchical configuration database.", "HKLM\\SOFTWARE.", ["Persistence"], "m03l7")
G("Responsible disclosure", "Ethics", "Privately reporting vulnerabilities to the owner and allowing time to fix.", "90-day window.", ["security.txt"], "m12l4")
G("Risk", "Risk", "Likelihood and impact of harm.", "Likelihood 5 x impact 5 = 25.", ["Threat", "Vulnerability"], "m05l3")
G("Rogue access point", "Wireless", "An unauthorised access point connected to or imitating a network.", "Consumer router under a desk.", ["Evil twin"], "m11l2", "lab_wireless")
G("Router", "Networking", "Device forwarding packets between networks.", "Home router.", ["Routing table"], "m02l1")
G("RSA", "Cryptography", "Widely used asymmetric algorithm for encryption and signatures.", "RSA-2048 certificates.", ["Asymmetric encryption"], "m05l2")
G("Rules of engagement", "Ethics", "Agreed rules for a test: scope, timing, techniques, contacts and limits.", "No DoS; exclude 10.10.10.200.", ["Scope"], "m05l5")
G("SaaS", "Cloud", "Software as a Service - finished software delivered online.", "Microsoft 365.", ["IaaS"], "m01l6")
G("SAST", "Development", "Static Application Security Testing - scanning source code for flaws.", "Semgrep.", ["SCA"], "m13l5")
G("SBOM", "Development", "Software Bill of Materials - list of components in software.", "CycloneDX file.", ["Supply chain attack"], "m13l5")
G("SCA", "Development", "Software Composition Analysis - finding vulnerable dependencies.", "Dependabot alerts.", ["SBOM"], "m13l5")
G("Scope", "Ethics", "The defined systems and activities a test is authorised to cover.", "10.10.10.0/24 excluding .200.", ["Rules of engagement"], "m05l5")
G("Secure flag", "Web", "Cookie flag restricting the cookie to HTTPS connections.", "Set-Cookie: sid=..; Secure.", ["HttpOnly"], "m09l1")
G("Segmentation", "Networking", "Dividing a network into zones to limit access and spread.", "Separate payment VLAN.", ["VLAN", "Firewall"], "m02l6")
G("Session", "Web", "Server-side state linking requests from one user, usually via a cookie.", "SESSIONID cookie.", ["Cookie"], "m09l1")
G("Shared responsibility model", "Cloud", "Division of security duties between cloud provider and customer.", "Customer manages IAM.", ["Cloud computing"], "m13l2")
G("SIEM", "Defence", "Security Information and Event Management - collects and correlates logs.", "Alert on 4625 bursts.", ["Logging"], "m04l7")
G("SMB signing", "Networking", "Cryptographic signing of SMB traffic to prevent tampering and relay.", "Required on domain controllers.", ["NTLM"], "m10l2")
G("Snapshot", "Systems", "Saved state of a virtual machine for rollback.", "'clean-install' snapshot.", ["Virtual machine"], "m01l5")
G("Social engineering", "Threats", "Manipulating people into actions or disclosures.", "Vishing the help desk.", ["Phishing"], "m06l3")
G("SPF", "Email", "Sender Policy Framework - DNS record listing authorised mail senders.", "v=spf1 include:... -all.", ["DMARC"], "m06l2")
G("SQL injection", "Web", "Injecting SQL through input to alter database queries.", "' OR '1'='1.", ["Parameterised query"], "m09l3", "lab_web_sec")
G("SSH", "Networking", "Secure Shell - encrypted remote login and command execution, TCP 22.", "ssh user@host.", ["Telnet"], "m02l5")
G("SSID", "Wireless", "Service Set Identifier - the name of a Wi-Fi network.", "SLV-Staff.", ["Access point"], "m11l1")
G("SSRF", "Web", "Server-Side Request Forgery - making a server request attacker-chosen URLs.", "url=http://127.0.0.1/admin.", ["Allow-list"], "m09l5", "lab_web_sec")
G("Subnet mask", "Networking", "Defines the network and host portions of an IP address.", "255.255.255.0 = /24.", ["CIDR"], "m02l3")
G("SUID", "Systems", "Linux permission bit making a program run with its owner's privileges.", "/usr/bin/passwd.", ["Privilege escalation"], "m03l3", "lab_linux_perms")
G("Supply chain attack", "Threats", "Compromising software or services that victims depend on.", "Malicious package update.", ["SBOM"], "m13l5")
G("Switch", "Networking", "Layer 2 device forwarding frames by MAC address.", "Office access switch.", ["VLAN"], "m02l1")
G("TCP", "Networking", "Reliable, connection-oriented transport protocol.", "Three-way handshake.", ["UDP"], "m02l4")
G("Threat", "Risk", "Anything that could exploit a vulnerability to cause harm.", "Ransomware group.", ["Risk"], "m05l3")
G("TLS", "Cryptography", "Transport Layer Security - encrypts data in transit.", "HTTPS uses TLS 1.2/1.3.", ["PKI"], "m05l2")
G("UDP", "Networking", "Connectionless transport protocol without delivery guarantees.", "DNS queries.", ["TCP"], "m02l4")
G("Virtual machine", "Systems", "Software-emulated computer running on a hypervisor.", "Kali VM.", ["Hypervisor", "Snapshot"], "m01l5")
G("VLAN", "Networking", "Virtual LAN - logical separation of a switched network.", "Guest VLAN 30.", ["Segmentation"], "m02l6")
G("VPN", "Networking", "Encrypted tunnel across an untrusted network.", "Remote-access VPN.", ["IPsec", "WireGuard"], "m02l6")
G("Vulnerability", "Risk", "A weakness that could be exploited.", "Unpatched server.", ["Exploit", "Risk"], "m05l3")
G("Vulnerability management", "Defence", "Continuous process of finding, prioritising, fixing and verifying vulnerabilities.", "Monthly scan and patch cycle.", ["Patch management"], "m08l1")
G("WAF", "Defence", "Web Application Firewall - filters HTTP traffic for attacks.", "Blocks common injection patterns.", ["Defence in depth"], "m09l3")
G("Wireshark", "Tools", "Free packet capture and analysis tool.", "Display filter: dns.", ["PCAP"], "m02l7", "lab_pcap")
G("WPA3", "Wireless", "Current Wi-Fi security standard using SAE and mandatory PMF.", "WPA3-Personal.", ["SAE", "WPA2"], "m11l1")
G("XSS", "Web", "Cross-Site Scripting - untrusted script running in a victim's browser in a site's context.", "Stored comment XSS.", ["CSP", "Output encoding"], "m09l4", "lab_web_sec")
G("YARA", "Tools", "Pattern-matching rules language for identifying malware.", "rule demo { strings: $a = \"evil\" condition: $a }", ["IOC"], "m13l6")
G("Zero trust", "Defence", "Security model that never trusts by network location and verifies every request.", "Per-request device and identity checks.", ["Least privilege"], "m05l4")
G("Zero-day", "Vulnerability", "A vulnerability exploited before a fix is available.", "Exploited before vendor patch.", ["Exploit"], "m08l1")

# ---------------- TOOLKIT ----------------
TOOLS = []


def TL(id, name, category, description, purpose, install, os, beginner, interface, uses, practice, limitations, mistakes, detection, ethics, lessons, labs, alternatives, licence, commands):
    TOOLS.append({"id": id, "name": name, "category": category, "description": description, "purpose": purpose, "install": install, "os": os,
                  "beginner": beginner, "interface": interface, "uses": uses, "practice": practice, "limitations": limitations, "mistakes": mistakes,
                  "detection": detection, "ethics": ethics, "lessons": lessons, "labs": labs, "alternatives": alternatives, "licence": licence, "commands": commands})


ETH = "Use only on systems you own or are explicitly authorised in writing to test. Unauthorised use can be a criminal offence (e.g. UK Computer Misuse Act 1990)."

TL("kali", "Kali Linux", "Operating systems", "Debian-based security distribution maintained by OffSec with hundreds of preinstalled tools.", "Provides a ready-made testing platform for authorised work.", "Download official ISO or prebuilt VM image from kali.org; verify SHA-256.", "Runs in VMs on Windows/macOS/Linux; ARM builds available.",
   "Start with the VM image, update with sudo apt update && sudo apt full-upgrade, take a snapshot.", "XFCE desktop; tools grouped in the applications menu; terminal is primary.", ["Lab attacker VM", "Practice platforms via VPN", "Authorised engagements"],
   ["Project 1 workstation set-up", "Linux Navigation lab"], ["Not intended as a daily-driver OS", "Tools need understanding, not just installation"], ["Running everything as root", "Using it for personal browsing/banking"],
   "Defenders see Kali tools by their traffic patterns and user agents, not the OS itself.", ETH, ["m03l1"], ["lab_linux_nav"], ["Parrot Security OS", "Ubuntu + selected tools"], "Free / open source", ["sudo apt update && sudo apt full-upgrade", "kali-tweaks"])
TL("parrot", "Parrot Security OS", "Operating systems", "Debian-based distro with security tools plus privacy tooling; lighter 'Home' edition available.", "Alternative testing platform.", "Download from parrotsec.org; verify checksums.", "VMs on all major OSes.",
   "Use the Security edition in your lab VM.", "MATE/KDE desktop with tool menus.", ["Lab attacker VM", "Privacy-focused research"], ["Use as alternative lab VM"], ["Smaller community than Kali"], ["Same as Kali: running as root"], "As Kali.", ETH, ["m03l1"], [], ["Kali Linux"], "Free / open source", ["sudo parrot-upgrade"])
TL("ubuntu", "Ubuntu / Debian", "Operating systems", "General-purpose Linux distributions widely used on servers and desktops.", "Target and server OS; stable base for tooling.", "ubuntu.com / debian.org images.", "VMs, cloud, bare metal.",
   "Install Ubuntu Server LTS as a practice target; harden it in Project 3.", "Terminal (server); GNOME desktop.", ["Servers", "Hardening practice", "Containers base images"], ["Project 3 Linux audit"], ["Desktop not focused on security tooling"], ["Skipping updates"], "N/A", "Normal admin use.", ["m03l1", "m03l5"], ["lab_linux_perms"], ["Rocky Linux", "Fedora"], "Free / open source", ["sudo apt install unattended-upgrades", "sudo ufw enable"])
TL("windows", "Windows (target and admin)", "Operating systems", "Microsoft's OS dominant on corporate desktops and AD servers.", "Learn to administer, audit and harden Windows targets.", "Microsoft offers free evaluation VMs/ISOs for Windows and Windows Server.", "x86-64, ARM64.",
   "Build a small evaluation lab: one Windows Server DC and one client.", "GUI tools (MMC snap-ins), PowerShell, Event Viewer.", ["AD lab", "PowerShell auditing"], ["Windows PowerShell Audit lab"], ["Evaluation licences expire"], ["Using Domain Admin for daily tasks"], "Windows Event Logs, Sysmon, Defender.", "Use evaluation images legally.", ["m03l6", "m03l7", "m03l8"], ["lab_win_ps"], ["Samba AD (limited)"], "Commercial (free evaluations)", ["Get-LocalGroupMember Administrators", "gpresult /r"])
TL("maltego", "Maltego", "Reconnaissance", "Graph-based OSINT and link analysis platform.", "Visualise relationships between domains, IPs, people and organisations.", "Download from maltego.com; Community Edition requires free registration with limits.", "Windows, macOS, Linux.",
   "Start with a domain entity and run DNS transforms on a fictional or in-scope domain.", "Graph canvas, entity palette, transforms menu.", ["Infrastructure mapping", "Investigations"], ["Map silverline.test-style data in a lab"], ["Community edition limits results", "Some transforms need paid data sources"], ["Collecting personal data beyond scope"], "Passive transforms usually do not touch the target.", ETH, ["m06l4"], ["lab_recon_silverline"], ["SpiderFoot (open source)", "Amass"], "Freemium", ["(GUI) New Graph -> Domain -> Run Transforms"])
TL("theharvester", "theHarvester", "Reconnaissance", "Gathers emails, names, subdomains and hosts from public sources.", "Passive OSINT collection for authorised engagements.", "Preinstalled on Kali; pip/git from GitHub.", "Linux, macOS.",
   "Run against an in-scope domain with one public source and review results manually.", "Command line.", ["Email format discovery", "Subdomain discovery"], ["Compare results against the supplied recon dataset"], ["Results vary by source and API keys", "May include stale data"], ["Treating results as verified facts"], "Mostly passive.", ETH, ["m06l4"], ["lab_recon_silverline"], ["Amass", "SpiderFoot"], "Free / open source", ["theHarvester -d example.test -b <source>"])
TL("amass", "OWASP Amass", "Reconnaissance", "In-depth attack surface mapping and subdomain enumeration.", "Discover subdomains passively and (with authorisation) actively.", "Kali package; GitHub releases.", "Linux, macOS, Windows.",
   "Use passive mode first: amass enum -passive -d example.test.", "Command line with config file for data sources.", ["Subdomain enumeration", "Asset discovery"], ["Passive enumeration of a lab domain"], ["Passive data can be stale", "Active mode sends DNS traffic"], ["Running active brute force without approval"], "Active brute force visible in DNS logs.", ETH, ["m06l2", "m06l4"], ["lab_recon_silverline"], ["subfinder", "theHarvester"], "Free / open source", ["amass enum -passive -d example.test"])
TL("nmap", "Nmap", "Network analysis", "Network scanner for discovery, port scanning, service/OS detection and scripted checks.", "Map hosts and services in authorised scope.", "nmap.org; preinstalled on Kali; Zenmap GUI available.", "Windows, macOS, Linux.",
   "Start with -sn discovery, then -sV on one lab host, and always save with -oA.", "CLI; Zenmap GUI.", ["Host discovery", "Service enumeration", "Firewall rule verification"], ["Scanning Simulator lab"], ["Point-in-time view", "OS detection is a guess", "Can disrupt fragile devices"], ["Scanning outside scope", "Aggressive timing on fragile networks"], "IDS/IPS signatures, firewall logs showing many connection attempts.", ETH, ["m07l1", "m07l2", "m07l3"], ["lab_scan_sim"], ["Masscan", "RustScan"], "Free / open source", ["nmap -sn 10.10.10.0/24 --exclude 10.10.10.200", "nmap -sV -sC -oA web01 10.10.10.15", "nmap -p- 10.10.10.30"])
TL("wireshark", "Wireshark", "Network analysis", "Packet capture and protocol analysis tool; tshark is its CLI.", "Inspect traffic for troubleshooting, investigation and evidence.", "wireshark.org; Kali package.", "Windows, macOS, Linux.",
   "Open a supplied PCAP, apply display filters like dns or http, use Statistics > Conversations.", "Packet list, details, bytes panes; filter bar.", ["Troubleshooting", "Incident analysis", "Proving cleartext protocols"], ["PCAP Analysis lab"], ["Encrypted traffic is opaque without keys", "Large captures are heavy"], ["Capturing without authorisation", "Confusing capture and display filters"], "Promiscuous mode NICs can sometimes be detected; mostly passive.", ETH + " Captures may contain personal data - handle under data protection rules.", ["m02l7"], ["lab_pcap"], ["tcpdump", "Zeek"], "Free / open source", ["tshark -r file.pcap -Y dns", "tshark -r file.pcap -q -z conv,ip"])
TL("netcat", "Netcat", "Network analysis", "Utility for reading and writing raw TCP/UDP connections.", "Banner grabbing, simple port checks, lab file transfer.", "Preinstalled on most Linux; ncat from Nmap project for Windows.", "All major OSes.",
   "Grab a banner: nc -nv <lab-ip> 21.", "Command line.", ["Banner validation", "Connectivity tests"], ["Validate FTP banner in Scanning lab"], ["No encryption (use ncat --ssl if needed)"], ["Leaving listeners open"], "Connections appear in firewall/IDS logs.", ETH, ["m07l4", "m10l5"], ["lab_scan_sim"], ["ncat", "socat"], "Free / open source", ["nc -nv 10.10.10.15 21", "nc -zv 10.10.10.15 20-25"])
TL("burp", "Burp Suite", "Web security", "Intercepting proxy and web testing platform by PortSwigger.", "Inspect, modify and replay HTTP requests during web tests.", "portswigger.net; Community Edition free; Professional paid.", "Windows, macOS, Linux.",
   "Use the built-in Burp browser, set scope to the lab host, use Proxy history and Repeater.", "Tabs: Proxy, Target, Repeater, Intruder, Decoder, Comparer.", ["Manual web testing", "API testing"], ["Web Security lab tasks", "PortSwigger Academy"], ["Community: throttled Intruder, no scanner"], ["Installing CA cert in daily browser", "No scope set"], "Requests visible in web/WAF logs; active scanning is noisy.", ETH, ["m09l7"], ["lab_web_sec"], ["OWASP ZAP", "Caido"], "Freemium", ["Proxy -> Intercept on", "Send to Repeater (Ctrl+R)"])
TL("zap", "OWASP ZAP", "Web security", "Free, open-source web application security scanner and proxy.", "Proxy, spider, passive and active scanning of authorised web apps.", "zaproxy.org; Docker images for automation.", "Windows, macOS, Linux.",
   "Run a passive scan while browsing Juice Shop locally; review alerts and validate.", "Sites tree, alerts panel, HUD.", ["Web testing", "CI DAST scans"], ["Scan local OWASP Juice Shop"], ["Automated alerts need validation"], ["Active scanning production without approval"], "Active scan traffic is noisy in logs.", ETH, ["m09l7"], ["lab_web_sec"], ["Burp Suite"], "Free / open source", ["zap-baseline.py -t http://localhost:3000"])
TL("nikto", "Nikto", "Web security", "Web server scanner checking for dangerous files, outdated software and misconfigurations.", "Quick web server configuration checks.", "Kali package; GitHub.", "Linux, macOS.",
   "Run against a local lab web server and validate each finding.", "Command line.", ["Web server misconfiguration checks"], ["Scan your own lab Apache server"], ["Very noisy; many false positives"], ["Treating output as findings without validation"], "Easily detected by WAF/IDS.", ETH, ["m09l5"], [], ["Nuclei", "ZAP"], "Free / open source", ["nikto -h http://lab-web.local"])
TL("devtools", "Browser Developer Tools", "Web security", "Built-in browser tools for DOM, network, storage and console.", "Understand client-side behaviour and requests.", "Built into Chrome, Edge, Firefox, Safari.", "All desktop OSes.",
   "Open with F12 on a lab site; inspect Network and Application/Storage tabs.", "Elements, Console, Network, Application tabs.", ["Inspect cookies and requests", "Debug JavaScript"], ["Inspect cookie flags on a lab app"], ["Only the client side"], ["Assuming client-side checks are security"], "N/A", ETH, ["m04l5", "m09l7"], [], ["Burp Suite"], "Free", ["F12 -> Network -> Preserve log"])
TL("postman", "Postman / Bruno / Insomnia", "Web security", "API clients for crafting and organising HTTP requests.", "Test APIs methodically by role and endpoint.", "Postman (freemium); Bruno and Insomnia (open source).", "Windows, macOS, Linux.",
   "Import an OpenAPI spec and create collections per role.", "Request builder, environments, collections.", ["API testing", "Authorisation matrices"], ["Project 8 API matrix"], ["Not a security scanner"], ["Storing real tokens in shared workspaces"], "Requests appear in API logs.", ETH, ["m09l6", "m13l4"], [], ["curl", "Burp"], "Freemium / open source", ["(GUI) Import -> OpenAPI"])
TL("nessus", "Nessus", "Vulnerability assessment", "Commercial vulnerability scanner by Tenable; Nessus Essentials is free for limited home use.", "Automated vulnerability discovery with plugins.", "tenable.com; register for Essentials activation.", "Windows, macOS, Linux.",
   "Scan your own lab range with a basic network scan; then try a credentialed scan.", "Web UI with policies, scans and reports.", ["Vulnerability scanning", "Compliance checks"], ["Scan your lab VMs"], ["Essentials host limit", "False positives need validation"], ["Running intrusive plugins on production"], "Very visible in logs; IDS signatures.", ETH, ["m07l4", "m08l4"], ["lab_cvss"], ["OpenVAS", "Nuclei"], "Commercial (free Essentials)", ["(Web UI) New Scan -> Basic Network Scan"])
TL("openvas", "OpenVAS / Greenbone", "Vulnerability assessment", "Open-source vulnerability scanner (Greenbone Community Edition).", "Free alternative for vulnerability scanning.", "Greenbone Community containers; Kali package gvm.", "Linux.",
   "Install via Docker/gvm-setup in a lab VM; scan your lab subnet.", "Web UI (Greenbone Security Assistant).", ["Vulnerability scanning"], ["Scan lab targets"], ["Heavy resource use; feed syncs take time"], ["Not validating results"], "Visible in logs.", ETH, ["m07l4"], [], ["Nessus Essentials"], "Free / open source", ["sudo gvm-setup", "sudo gvm-start"])
TL("nuclei", "Nuclei", "Vulnerability assessment", "Fast template-based vulnerability scanner by ProjectDiscovery.", "Run community templates for known issues and misconfigurations.", "GitHub releases; Kali package.", "Linux, macOS, Windows.",
   "Run only safe/informational templates against lab hosts first.", "Command line with YAML templates.", ["Known CVE checks", "Misconfiguration checks"], ["Scan local Juice Shop with tags"], ["Template quality varies"], ["Running intrusive templates without approval"], "Signatures in WAF/IDS.", ETH, ["m08l4"], [], ["Nessus", "ZAP"], "Free / open source", ["nuclei -u http://localhost:3000 -severity info,low"])
TL("john", "John the Ripper", "Password auditing", "Password hash auditing tool supporting many formats.", "Test password strength offline during authorised audits.", "Kali package; openwall.com (Jumbo version on GitHub).", "All major OSes.",
   "Practise on hashes you create yourself in your lab.", "Command line.", ["Authorised password audits"], ["Password Audit Analysis lab (simulated)"], ["Slow for strong hashes (by design)"], ["Auditing without explicit approval", "Reporting plaintext passwords"], "Offline; detection focuses on how hashes were obtained.", ETH + " Treat any recovered passwords as highly sensitive and never include them in reports.", ["m10l4"], ["lab_hash_audit"], ["Hashcat"], "Free / open source", ["john --wordlist=lab-words.txt --format=nt lab_hashes.txt", "john --show lab_hashes.txt"])
TL("hashcat", "Hashcat", "Password auditing", "GPU-accelerated password recovery/auditing tool.", "Fast offline password strength testing in authorised audits.", "hashcat.net; Kali package.", "Windows, Linux, macOS.",
   "Learn hash modes (-m) with hashes you generate in your lab.", "Command line.", ["Authorised offline audits"], ["Compare hash speeds conceptually"], ["Needs GPU for speed"], ["Unauthorised use", "Plaintext in reports"], "Offline.", ETH, ["m10l4"], ["lab_hash_audit"], ["John the Ripper"], "Free / open source", ["hashcat -m 1000 lab_hashes.txt lab-words.txt"])
TL("hydra", "Hydra", "Password auditing", "Online login guessing tool for many protocols.", "Test online password policies and lockouts in authorised, controlled tests.", "Kali package; GitHub.", "Linux, macOS.",
   "Only against your own lab service with a tiny list - observe lockout and logs.", "Command line.", ["Testing lockout/rate limiting in labs"], ["Observe how fail2ban reacts in your lab"], ["Causes lockouts; slow; noisy"], ["Locking out real users", "No explicit approval"], "Very visible: failed login bursts (4625, auth.log).", ETH, ["m10l4"], [], ["Medusa", "ncrack"], "Free / open source", ["hydra -L users.txt -P small.txt ssh://lab-host (lab only)"])
TL("metasploit", "Metasploit Framework", "Exploitation framework education", "Open-source exploitation framework with exploit, auxiliary and post modules.", "Controlled validation of vulnerabilities in authorised labs.", "Preinstalled on Kali; rapid7 installers.", "Linux, macOS, Windows.",
   "Use auxiliary scanners and 'check' commands against Metasploitable in your isolated lab.", "msfconsole CLI.", ["Controlled validation", "Teaching exploit concepts"], ["Read the Exploit Validation Safety lab first"], ["Can crash services; detected by EDR"], ["Running exploits outside scope", "Using default payloads on production"], "EDR/AV signatures, network IDS, unusual services.", ETH, ["m10l5", "m12l1"], ["lab_exploit_safety"], ["Manual validation"], "Free / open source (Pro commercial)", ["msfconsole", "search type:auxiliary smb", "check"])
TL("searchsploit", "Searchsploit", "Exploitation framework education", "Offline search tool for the Exploit-DB archive.", "Research public exploits for known vulnerabilities.", "Kali package exploitdb.", "Linux.",
   "Search a software version, then READ the code before considering use in a lab.", "Command line.", ["Exploit research"], ["Research a lab service version"], ["Public code may be broken or malicious"], ["Running code without review"], "N/A (offline search).", ETH, ["m10l5"], [], ["Exploit-DB website", "GitHub advisories"], "Free / open source", ["searchsploit vsftpd 3.0", "searchsploit -x <id>"])
TL("aircrack", "Aircrack-ng", "Wireless", "Suite for Wi-Fi monitoring, capture and WPA/WPA2 passphrase strength testing.", "Authorised wireless audits of your own networks.", "Kali package; aircrack-ng.org.", "Linux (best), some Windows support.",
   "Survey only your own AP with airodump-ng; test your own passphrase strength.", "Command line suite.", ["Wireless surveys", "Passphrase strength audits"], ["Wireless Config Review lab"], ["Requires compatible adapters", "WPA3-SAE resists offline guessing"], ["Capturing neighbours' traffic", "Deauthentication without approval"], "WIDS detect deauth floods and monitor-mode activity patterns.", ETH + " Radio interference may also breach telecoms law.", ["m11l2"], ["lab_wireless"], ["Kismet", "Wireshark"], "Free / open source", ["airmon-ng start wlan0", "airodump-ng wlan0mon"])
TL("mobsf", "MobSF", "Mobile security", "Mobile Security Framework for automated static/dynamic analysis.", "Analyse Android/iOS apps you are authorised to test.", "GitHub; Docker image.", "Linux, macOS, Windows (Docker).",
   "Run in Docker and upload a deliberately insecure training APK.", "Web UI reports.", ["Mobile static analysis", "Dynamic analysis with emulator"], ["Mobile Static Review lab"], ["Static results need validation"], ["Uploading third-party apps without permission"], "N/A", ETH, ["m11l4"], ["lab_mobile"], ["jadx", "apktool"], "Free / open source", ["docker run -it -p 8000:8000 opensecurity/mobile-security-framework-mobsf"])
TL("adb", "Android Debug Bridge (adb)", "Mobile security", "Command-line tool to communicate with Android devices and emulators.", "Install test builds, read logs and inspect test devices.", "Android SDK Platform Tools (free from Google).", "Windows, macOS, Linux.",
   "Use with an emulator: adb devices, adb logcat.", "Command line.", ["Debugging", "Log review", "Test app installs"], ["Check logcat of your own test app"], ["Needs USB debugging enabled"], ["Leaving USB debugging on in daily use"], "N/A", ETH, ["m11l4"], [], ["Android Studio"], "Free", ["adb devices", "adb logcat", "adb install app.apk"])
TL("practice", "Practice Platforms", "Practice platforms", "Legal training platforms: TryHackMe, Hack The Box, PortSwigger Web Security Academy, OWASP Juice Shop.", "Authorised targets to practise skills.", "Create free accounts; run Juice Shop locally with Docker.", "Browser-based or VPN to platform labs; Juice Shop local.",
   "Start with TryHackMe beginner paths, PortSwigger Academy for web, Juice Shop for local practice.", "Web dashboards, VPN configs, score boards.", ["Skill building", "Certification preparation"], ["Follow Module 15 practice plan"], ["Some content paid; platform rules apply"], ["Sharing active solutions", "Attacking platform infrastructure"], "N/A", "Follow each platform's terms of service.", ["m15l1"], [], ["VulnHub", "OverTheWire", "PicoCTF"], "Free tiers + paid", ["docker run -d -p 3000:3000 bkimminich/juice-shop"])

# ---------------- CAREER ----------------
CAREER = {
  "paths": [
    {"title": "Ethical Hacker", "summary": "Finds and demonstrates weaknesses under authorisation.", "skills": ["Networking", "Linux/Windows", "Web", "Reporting"], "modules": ["m06", "m07", "m09", "m10", "m14"], "entry": "Mid"},
    {"title": "Penetration Tester", "summary": "Performs scoped assessments of networks, web apps and more, and writes reports.", "skills": ["Methodology", "Web", "AD", "Reporting"], "modules": ["m06", "m07", "m08", "m09", "m10", "m14"], "entry": "Junior-Mid"},
    {"title": "Red Team Operator", "summary": "Emulates specific adversaries over longer engagements to test detection and response.", "skills": ["AD", "Cloud", "OPSEC", "Detection awareness"], "modules": ["m10", "m12", "m13"], "entry": "Senior"},
    {"title": "Security Analyst", "summary": "Monitors, investigates and improves security controls.", "skills": ["Logs", "Networking", "Risk"], "modules": ["m02", "m05", "m08"], "entry": "Entry"},
    {"title": "SOC Analyst", "summary": "Triages alerts in a Security Operations Centre.", "skills": ["SIEM", "Event IDs", "PCAP", "IR"], "modules": ["m02", "m03", "m05", "m12"], "entry": "Entry"},
    {"title": "Blue Team Analyst", "summary": "Defends systems: detection engineering, hardening, threat hunting.", "skills": ["Detection", "Hardening", "Scripting"], "modules": ["m03", "m04", "m05", "m12"], "entry": "Entry-Mid"},
    {"title": "Vulnerability Analyst", "summary": "Runs scanning, validation and remediation tracking.", "skills": ["Scanners", "CVSS", "Prioritisation"], "modules": ["m07", "m08"], "entry": "Entry"},
    {"title": "Security Consultant", "summary": "Assesses and advises clients across technical and governance topics.", "skills": ["Communication", "Assessment", "Reporting"], "modules": ["m05", "m08", "m14"], "entry": "Mid"},
    {"title": "Application Security Engineer", "summary": "Secures software through design reviews, code review and testing.", "skills": ["Secure code", "SAST/DAST", "Threat modelling"], "modules": ["m04", "m09", "m13"], "entry": "Mid"},
    {"title": "Cloud Security Engineer", "summary": "Secures cloud identity, configuration and workloads.", "skills": ["IAM", "IaC", "Containers"], "modules": ["m13"], "entry": "Mid"},
    {"title": "Security Engineer", "summary": "Designs and runs security tooling and controls.", "skills": ["Automation", "Networking", "Identity"], "modules": ["m02", "m04", "m05", "m13"], "entry": "Mid"},
    {"title": "Digital Forensics Analyst", "summary": "Collects and analyses digital evidence.", "skills": ["Forensics", "Chain of custody", "Timelines"], "modules": ["m01", "m03", "m12", "m13"], "entry": "Mid"},
    {"title": "Incident Responder", "summary": "Leads containment, eradication and recovery during incidents.", "skills": ["IR lifecycle", "Logs", "Communication"], "modules": ["m05", "m12"], "entry": "Mid"},
    {"title": "Security Architect", "summary": "Designs secure systems and standards across the organisation.", "skills": ["Zero trust", "Cloud", "Governance"], "modules": ["m05", "m13"], "entry": "Senior"},
    {"title": "DevSecOps Engineer", "summary": "Builds security into CI/CD and the software supply chain.", "skills": ["CI/CD", "SCA/SAST", "Containers"], "modules": ["m04", "m13"], "entry": "Mid"},
  ],
  "certs": [
    {"name": "CompTIA Security+", "provider": "CompTIA", "level": "Foundation", "format": "Multiple-choice and performance-based questions", "modules": ["m01", "m02", "m05", "m08", "m14"], "url": "https://www.comptia.org/certifications/security"},
    {"name": "eJPT", "provider": "INE", "level": "Entry practical", "format": "Hands-on practical exam", "modules": ["m02", "m03", "m06", "m07", "m09", "m10"], "url": "https://ine.com/security/certifications/ejpt-certification"},
    {"name": "PNPT", "provider": "TCM Security", "level": "Intermediate practical", "format": "Practical external/internal test with report and debrief", "modules": ["m06", "m07", "m10", "m13", "m14"], "url": "https://certifications.tcm-sec.com/pnpt/"},
    {"name": "OSCP", "provider": "OffSec", "level": "Intermediate-advanced practical", "format": "Hands-on exam plus professional report", "modules": ["m06", "m07", "m09", "m10", "m12", "m14"], "url": "https://www.offsec.com/courses/pen-200/"},
    {"name": "CEH", "provider": "EC-Council", "level": "Intermediate knowledge (practical option)", "format": "Multiple-choice; separate practical exam", "modules": ["m05", "m06", "m07", "m09", "m10", "m11"], "url": "https://www.eccouncil.org/train-certify/certified-ethical-hacker-ceh/"},
    {"name": "Burp Suite Certified Practitioner", "provider": "PortSwigger", "level": "Intermediate web practical", "format": "Practical web exploitation exam", "modules": ["m09"], "url": "https://portswigger.net/web-security/certification"},
    {"name": "ISC2 Certified in Cybersecurity (CC)", "provider": "ISC2", "level": "Entry knowledge", "format": "Multiple-choice", "modules": ["m05"], "url": "https://www.isc2.org/certifications/cc"},
  ],
  "interview": [
    {"q": "Explain the TCP three-way handshake.", "a": "Client sends SYN, server replies SYN-ACK, client sends ACK; then data flows. It establishes sequence numbers and a reliable connection. Scanners use SYN replies to infer port states."},
    {"q": "What is the difference between hashing and encryption?", "a": "Encryption is reversible with a key and protects confidentiality; hashing is one-way and proves integrity. Passwords should be stored with slow salted hashes like Argon2 or bcrypt."},
    {"q": "How would you explain SQL injection to a developer?", "a": "User input is being treated as SQL code because queries are built with string concatenation. Use parameterised queries so input stays data; add least-privilege DB accounts and generic errors."},
    {"q": "What is the difference between XSS and CSRF?", "a": "XSS runs attacker script in the victim's browser within the site; CSRF makes the victim's browser send an unwanted request using their cookies. Fix XSS with output encoding/CSP; CSRF with tokens/SameSite."},
    {"q": "You see 500 failed logins from one IP in five minutes. What do you do?", "a": "Confirm scope of attempts, check for any successful logins from that IP, check targeted accounts, block or rate-limit the source per playbook, verify MFA, document and escalate if success occurred."},
    {"q": "What is the principle of least privilege?", "a": "Grant only the access needed for a task, for only as long as needed - reducing impact if an account is compromised."},
    {"q": "Walk me through how you would test a login page.", "a": "Confirm scope, map the flow, check HTTPS and cookie flags, username enumeration, rate limiting/lockout, MFA, password reset logic, SQLi/XSS on inputs, session regeneration and logout - all with test accounts."},
    {"q": "What is CVSS and its limitations?", "a": "A 0-10 standard severity score from exploitability and impact metrics. It lacks business context, so combine with exposure, KEV/EPSS and asset criticality."},
    {"q": "Explain Kerberos at a high level.", "a": "Users get a TGT from the KDC, then request service tickets for specific services, which they present to the service. Weak service account passwords risk Kerberoasting."},
    {"q": "How do you stay current in cybersecurity?", "a": "Vendor advisories, CISA KEV, community blogs, practice platforms, conferences such as BSides, and hands-on labs with notes."},
    {"q": "What would you do if you found evidence of a real breach during a pen test?", "a": "Stop testing, preserve evidence of what I observed, notify the emergency contact immediately, follow the client's instructions and document everything."},
    {"q": "What is the difference between a vulnerability assessment and a penetration test?", "a": "An assessment identifies and rates weaknesses; a pen test validates exploitability and impact, often chaining issues, within agreed rules."},
    {"q": "Describe defence in depth with an example.", "a": "Multiple layers so one failure isn't fatal: email filtering, user training, MFA, EDR, least privilege, segmentation, monitoring and backups against phishing-led ransomware."},
    {"q": "What are common Windows event IDs you would monitor?", "a": "4624/4625 logons, 4672 special privileges, 4720 user created, 4728/4732 group changes, 1102 log cleared, 7045 service installed, 4698 scheduled task created."},
    {"q": "How would you explain a critical finding to a non-technical manager?", "a": "State the business impact, likelihood and what to do: 'Anyone on the internet could access customer data through the VPN flaw; patching within 48 hours removes the risk.'"},
    {"q": "Tell me about a time you solved a difficult technical problem (STAR).", "a": "Use Situation, Task, Action, Result - e.g. a system outage, how you troubleshot methodically, what you changed and the measurable result."},
    {"q": "What is zero trust?", "a": "Never trust based on network location; verify identity, device health and context for every request; apply least privilege and assume breach."},
    {"q": "Why is SMB signing important?", "a": "It protects SMB traffic integrity and prevents NTLM relay attacks; it should be required, especially on domain controllers."},
    {"q": "What is the shared responsibility model?", "a": "The cloud provider secures the underlying infrastructure; the customer secures identities, configuration, data and workloads, varying by IaaS/PaaS/SaaS."},
    {"q": "How do you handle sensitive evidence?", "a": "Collect the minimum, redact secrets, encrypt at rest, transfer securely, restrict access and delete per contract with a record."},
  ]
}
