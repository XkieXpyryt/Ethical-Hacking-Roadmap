from lib import *

M("m06", 6, "Reconnaissance & Information Gathering", "radar",
  "Reconnaissance is about understanding a target organisation's exposure before testing it. Done ethically it stays inside scope, respects privacy and separates facts from assumptions. All practice here uses fictional organisations and supplied datasets.",
  ["Distinguish passive and active reconnaissance", "Analyse DNS, WHOIS and subdomain data", "Identify technologies and metadata exposure",
   "Use OSINT tools responsibly", "Document reconnaissance with confirmed facts and clear assumptions"],
  ["Module 5 (ethics, law, rules of engagement)", "Module 2 (DNS)"],
  [
   L("m06l1", "Reconnaissance Overview: Passive, Active, OSINT and Scope", "Beginner", 30,
     "Good recon finds the forgotten server; bad recon breaks the law or invades privacy.",
     ["Define passive and active reconnaissance", "Explain OSINT and its ethical limits", "Validate scope before collecting data"],
     """Reconnaissance is gathering information about a target. Passive recon uses public sources without touching the target's systems (search engines, public DNS, certificate logs). Active recon interacts with the target directly (resolving its DNS servers, visiting its website, scanning) and must be authorised. OSINT (open-source intelligence) is intelligence built from publicly available information. 'Public' does not mean 'anything goes': collect only what the engagement needs.""",
     """Recon goals: identify in-scope assets (domains, IP ranges, cloud services, apps), technologies, exposed services, email formats and potential social engineering exposure - only if social engineering is in scope.

Scope validation: confirm every discovered asset is owned by the client and listed (or approved) in scope before active testing. A subdomain that resolves to a third-party SaaS provider is usually NOT the client's to test.

Privacy and overcollection: avoid personal social media profiling, family details, home addresses or anything unrelated to the organisation's security posture. Apply data minimisation (GDPR) and store recon data securely; delete it at the end of the engagement per the contract.

Passive sources: search engines, company website, job adverts (reveal technologies), certificate transparency logs (crt.sh), public DNS, WHOIS/RDAP, code repositories, archived pages.
Active techniques (authorised only): DNS queries to the target's name servers, zone transfer attempts, web crawling, port scanning.""",
     "Passive recon is reading the estate agent's listing and street map; active recon is walking up and trying the door handles - which needs the owner's permission.",
     "Job adverts asking for 'experience with Citrix NetScaler and SAP' told testers which external technologies to look for - without touching any system.",
     "Recon shapes the whole engagement. Out-of-scope or privacy-invasive collection can end an engagement and a career.",
     ["Confirm scope", "Passive sources", "Build asset list", "Validate ownership", "Approved active checks", "Document"],
     ["Read the fictional 'Northwind Health' scope and list what is in and out of scope", "Classify ten techniques as passive or active", "Write three privacy rules for your recon notes"],
     "A subdomain shop.client.example points to a third-party e-commerce platform. Is it in scope? What do you do?",
     ["Treating 'publicly available' as 'permitted'", "Collecting staff personal lives", "Assuming every discovered subdomain belongs to the client"],
     "Passive first, active only with authorisation, always validate ownership and collect the minimum.",
     ["Reconnaissance", "OSINT", "Passive reconnaissance", "Active reconnaissance", "Scope"],
     "Visiting the target's website is often classed as active (it touches their server) - read the question carefully.",
     [Q("Which is passive reconnaissance?", ["Port scanning the target", "Reading certificate transparency logs", "Attempting a DNS zone transfer on their server", "Brute-forcing a login"], 1, "CT logs are third-party public records."),
      Q("A discovered subdomain is hosted by a SaaS vendor. You should...", ["Test it anyway", "Confirm with the client and vendor rules before any testing", "Ignore scope", "Scan it quietly"], 1, "Ownership and permission must be validated."),
      TF("Data minimisation applies to reconnaissance notes.", True, "Collect only what is needed and protect it.")]),

   L("m06l2", "Domains, DNS Records, WHOIS, Subdomains and Internet Exposure", "Intermediate", 35,
     "DNS is the map of an organisation's internet presence. Forgotten subdomains are a classic entry point.",
     ["Interpret DNS records for recon", "Read WHOIS/RDAP data", "Find subdomains and assess internet exposure"],
     """A domain like northwind.example is managed in DNS. Its records reveal mail servers (MX), name servers (NS), web hosts (A/AAAA), aliases (CNAME) and security settings (TXT records with SPF and DMARC). WHOIS or the newer RDAP shows registration details, though personal data is usually redacted. Subdomains (vpn.northwind.example, dev.northwind.example) often reveal services; dev and test hosts are frequently less protected.""",
     """Interpreting records:
• MX -> mail provider (e.g. Microsoft 365 or Google), which shapes phishing defences.
• TXT v=spf1 ... -> authorised mail senders; '-all' strict, '~all' soft fail; no SPF or DMARC p=none means spoofing may be easier.
• CNAME pointing to a deleted cloud resource -> possible subdomain takeover risk.
• NS -> who hosts DNS; zone transfers (AXFR) should be refused to the public.

Subdomain discovery (passive): certificate transparency logs, search engines, passive DNS datasets, Amass passive mode. Active: brute-forcing names against the target's DNS - authorised only.

Internet exposure assessment: map each discovered host to IP, owner (cloud/on-prem/third party), purpose and exposure (login page, VPN, remote desktop, admin panel). Shodan and Censys index exposed services from their own scans - viewing their records is passive for you, but treat findings as leads to validate within scope.

Output: an asset inventory table with columns: asset, IP, source, owner verified (Y/N), exposure, notes.""",
     "DNS records are the building directory in the lobby: they tell you which floor holds mail, which holds the website and where the side entrances are.",
     "A company deleted a cloud app but left dev.company.example as a CNAME to it. Anyone who claimed that cloud name could host content on the company's subdomain - a subdomain takeover.",
     "Exposure mapping drives which services you test and which risks you report (weak email security, forgotten dev hosts).",
     ["Root domain", "NS/MX/TXT records", "Subdomain sources", "Resolve to IPs", "Verify ownership", "Inventory"],
     ["Open the Recon lab dataset for Silverline and list MX, SPF and DMARC findings", "Identify any CNAME that points to an unclaimed resource", "Build an asset inventory table"],
     "From a supplied list of 12 subdomains, choose the three most interesting for testing and justify each.",
     ["Running active brute-force without permission", "Ignoring email security records", "Reporting unverified assets as facts"],
     "Read MX, NS, TXT and CNAME records; discover subdomains passively first; verify ownership; inventory exposure.",
     ["DNS record", "WHOIS", "RDAP", "Subdomain", "SPF", "DMARC", "Subdomain takeover"],
     "DMARC p=none means 'monitor only' - it does not block spoofed mail.",
     [Q("Which DNS record reveals a domain's mail servers?", ["A", "MX", "NS", "SOA"], 1, "MX records."),
      Q("A CNAME pointing to a deleted cloud resource may allow...", ["Faster DNS", "Subdomain takeover", "Zone transfer", "DHCP spoofing"], 1, "Dangling CNAMEs enable takeover."),
      Q("DMARC policy p=none means...", ["Spoofed mail is rejected", "Spoofed mail is quarantined", "Monitoring only - no enforcement", "SPF is disabled"], 2, "p=none only reports.")]),

   L("m06l3", "Metadata, Technology Identification, Search Engines and Social Engineering Awareness", "Intermediate", 30,
     "Documents, headers and search results leak usernames, software versions and internal paths.",
     ["Analyse document metadata", "Identify web technologies", "Use search operators responsibly and recognise social engineering risk"],
     """Metadata is data about data. Office documents and PDFs can contain author names, usernames, software versions and internal file paths. Photos can contain GPS coordinates. Web servers reveal technologies through headers (Server: nginx), cookies (PHPSESSID suggests PHP) and page source. Search engine operators like site: and filetype: narrow results. Social engineering manipulates people; understanding what an organisation exposes about its staff helps defend against it.""",
     """Metadata examples (from a fictional supplied PDF): Author: j.smith; Creator: Microsoft Word 2016; Producer: macOS Quartz; Path: \\\\fs01\\finance\\Q3. Insights: username format (first initial.surname), an internal file server name, older Office version. Tools: exiftool (read metadata of files you are authorised to analyse).

Technology identification: HTTP response headers, favicon hashes, error pages, JavaScript libraries, Wappalyzer-style fingerprints, TLS certificate details.

Search operators: site:northwind.example filetype:pdf; inurl:login site:northwind.example; intitle:"index of". Use them only to find information about in-scope organisations and stop if you encounter sensitive personal data - report exposure, do not collect it.

Social engineering awareness: phishing, vishing (phone), smishing (SMS), pretexting, tailgating. Exposure factors: staff lists, email formats, org charts, help-desk processes. Defences: training, verification procedures, reporting channels, MFA, least privilege.""",
     "Metadata is the fingerprints left on a letter - the letter's words are harmless, but the prints show who handled it.",
     "A council's published PDFs revealed 40 usernames and the internal server name; the remediation was metadata scrubbing before publication.",
     "Metadata exposure, verbose headers and outdated technologies are common low-to-medium findings; they also feed later testing.",
     ["Collect in-scope documents", "Extract metadata", "Identify users/software/paths", "Correlate with tech fingerprint", "Report exposure"],
     ["Analyse the sample metadata in the Recon lab", "Identify three technologies from the sample HTTP headers", "Write two search operators for in-scope PDFs"],
     "Write a short recommendation to stop metadata leaks in published documents.",
     ["Collecting personal data you stumble on", "Assuming headers are always accurate (they can be faked)", "Running social engineering without explicit scope"],
     "Metadata, headers and search results leak useful facts. Use them in scope, minimise personal data and recommend scrubbing and awareness.",
     ["Metadata", "exiftool", "Search operator", "Social engineering", "Phishing"],
     "Expect questions on which operator restricts results to a domain (site:) or file type (filetype:).",
     [Q("Which operator limits search results to one domain?", ["filetype:", "site:", "intitle:", "cache:"], 1, "site: restricts to a domain."),
      Q("A document's metadata shows Author: a.jones. What might this reveal?", ["The firewall model", "The username format", "The VPN key", "Nothing useful"], 1, "It suggests the username convention."),
      Q("Pretending to be IT support on a phone call to obtain a password is...", ["Smishing", "Vishing / pretexting", "Tailgating", "Spear phishing"], 1, "Voice-based pretexting (vishing).")]),

   L("m06l4", "Recon Tools and Documentation: Maltego, theHarvester, Amass and Reporting", "Intermediate", 35,
     "Tools speed up recon; documentation turns raw data into evidence you can defend.",
     ["Describe what Maltego, theHarvester and Amass do", "Use WHOIS and DNS utilities", "Write a recon report that separates facts from assumptions"],
     """Maltego visualises relationships between domains, IPs, people and organisations as graphs. theHarvester collects emails, names, subdomains and hosts from public sources. OWASP Amass performs in-depth subdomain enumeration and attack surface mapping. Simple utilities like whois, dig and nslookup query registration and DNS data. Whatever tools you use, your recon report must state sources, timestamps and confidence, and clearly separate confirmed facts from assumptions.""",
     """Tool overview (use against in-scope or fictional targets only):
• whois / RDAP: registrar, dates, name servers.
• dig northwind.example MX +short; dig -x 203.0.113.10 (reverse); nslookup.
• theHarvester -d northwind.example -b <public source> - gathers emails/hosts from search engines and public APIs (some sources need free API keys).
• Amass enum -passive -d northwind.example - passive subdomain enumeration.
• Maltego Community Edition (free with registration) - graph-based link analysis using 'transforms'.

Documentation template:
Finding: dev.northwind.example resolves to 198.51.100.24
Source: crt.sh certificate log, 2026-03-02 10:14 UTC
Status: CONFIRMED (resolves) / Ownership: UNVERIFIED - awaiting client confirmation
Assumption: likely a development server (based on name only)

Confidence levels: confirmed (directly verified), probable (multiple sources), possible (single unverified source). Never state an assumption as a fact in a report.

Preventing overcollection: define collection goals before starting, set time limits, exclude personal social media, store data encrypted, delete on schedule.""",
     "Recon notes are like a detective's case file: each clue records where it came from, when, and how sure you are.",
     "A report claimed a server ran an outdated CMS based only on a banner; the banner was stale. Marking it 'possible' would have avoided a false finding.",
     "Clear, sourced recon is what makes later findings credible in front of clients and auditors.",
     ["Define goals", "Run passive tools", "Normalise results", "Verify", "Tag confidence", "Report"],
     ["Complete the Silverline Recon decision lab", "Write three findings using the documentation template", "Mark each as confirmed, probable or possible"],
     "Convert a messy list of 25 recon results into a clean asset inventory with confidence ratings.",
     ["Pasting raw tool output into reports", "Missing timestamps and sources", "Stating guesses as facts"],
     "Tools gather; analysts verify. Every recon item needs a source, time, status and confidence.",
     ["Maltego", "theHarvester", "Amass", "dig", "Confidence level"],
     "Know which tool is graph-based (Maltego) and which focuses on subdomain enumeration (Amass).",
     [Q("Which tool is known for graph-based link analysis?", ["Amass", "Maltego", "Nmap", "Hydra"], 1, "Maltego visualises relationships."),
      Q("A finding seen in one unverified source should be marked...", ["Confirmed", "Possible", "Critical", "Out of scope"], 1, "Single unverified sources are 'possible'."),
      Q("Which command queries MX records?", ["dig example.com MX", "ping example.com", "whois -mx", "traceroute MX"], 0, "dig <domain> MX.")]),
  ],
  ["Passive recon on a fictional organisation", "Analyse a mock domain profile", "Identify public DNS records from a supplied dataset", "Analyse sample metadata",
   "Build an asset inventory", "Create a reconnaissance report", "Distinguish confirmed facts from assumptions"],
  "p05",
  "Complete all 4 lessons, pass the module assessment and complete the Silverline Recon lab.",
  ["List 6 passive sources", "Explain SPF/DMARC findings", "Write one recon finding with source and confidence"],
  ["Recon overview", "Passive", "Active", "OSINT", "Information sources", "Asset discovery", "Domains", "DNS records", "WHOIS", "Metadata", "Subdomains", "Internet exposure",
   "Technology identification", "Search engines", "Social engineering awareness", "Privacy", "Scope validation", "Documentation", "Maltego", "theHarvester", "Amass"])


M("m07", 7, "Scanning & Enumeration", "scan",
  "Scanning turns an asset list into a map of hosts, ports and services. You will learn how scans work, how to read results, why they are sometimes wrong and how to scan safely inside an authorised lab.",
  ["Discover hosts and map networks", "Explain TCP/UDP port states and scan types", "Enumerate services and fingerprint operating systems",
   "Interpret Nmap output and recognise false positives", "Follow a safe scanning methodology"],
  ["Module 2 (TCP/IP and ports)", "Module 6"],
  [
   L("m07l1", "Network Discovery, Host Discovery and Network Mapping", "Intermediate", 30,
     "You cannot assess what you have not found - but finding hosts must stay within scope.",
     ["Explain host discovery techniques", "Map live hosts to a network diagram", "Respect scope during discovery"],
     """Host discovery finds which IP addresses in a range have live devices. Tools send probes - ICMP echo (ping), TCP packets to common ports, or ARP requests on local networks - and see which addresses answer. Nmap's ping scan (nmap -sn 10.10.10.0/24) lists live hosts without scanning their ports. The results become a network map showing hosts, subnets and gateways.""",
     """Discovery methods:
• ARP scan (local subnet only) - very reliable because devices must answer ARP.
• ICMP echo/timestamp - often blocked by firewalls.
• TCP SYN to 443 / TCP ACK to 80 - finds hosts that block ping.
• Reverse DNS - names can reveal roles (dc01, sql-prod).

Nmap: nmap -sn <range> (discovery only); -Pn skips discovery and treats every host as up (useful when ICMP is blocked, but slower); --exclude <ip> to protect out-of-scope hosts; -iL targets.txt to read an approved target list.

Network mapping: group hosts by subnet and role (servers, workstations, network devices, printers, IoT); note gateways and unexpected devices (a personal router plugged into the LAN).

Scope safety: always scan from an approved target file; double-check CIDR; exclude fragile systems listed in the RoE (e.g. medical devices, legacy OT).""",
     "Host discovery is knocking on every door on a street to see which houses are occupied, before deciding which to inspect.",
     "During an internal test, discovery revealed a forgotten Windows Server 2008 machine nobody had on the asset list - itself a significant finding.",
     "Accurate discovery creates the target list for every later phase and reveals unmanaged assets.",
     ["Approved targets file", "Host discovery (-sn)", "Live host list", "Reverse DNS", "Group by role", "Network map"],
     ["Run the Scanning Simulator lab and discover hosts in 10.10.10.0/24", "Draw the network map from the results", "Identify which host might be a domain controller"],
     "Explain why '-Pn' can be necessary and why it makes scans slower.",
     ["Scanning a typed range instead of the approved file", "Assuming no ping reply = no host", "Ignoring exclusions in the rules of engagement"],
     "Find live hosts with suitable probes, map them by role and never stray outside the approved targets.",
     ["Host discovery", "Ping sweep", "ARP scan", "Network map"],
     "Remember ARP discovery only works on the local subnet.",
     [Q("Which Nmap option performs host discovery without port scanning?", ["-sV", "-sn", "-p-", "-O"], 1, "-sn = ping scan only."),
      Q("Why might a live host not reply to ping?", ["It is always down", "ICMP is blocked by a firewall", "It uses IPv4", "It has no MAC address"], 1, "ICMP filtering is common."),
      Q("Which option prevents scanning a specific out-of-scope IP?", ["--exclude", "--skip-host", "-x", "--ignore"], 0, "--exclude removes hosts from the target list.")]),

   L("m07l2", "Ports, Port States, TCP/UDP Scanning and Firewall Behaviour", "Intermediate", 35,
     "Reading port states correctly is the difference between a real finding and a false alarm.",
     ["Explain open, closed and filtered states", "Compare SYN, connect and UDP scans", "Recognise how firewalls change results"],
     """A port is open when a service is listening and accepts connections, closed when the host answers but nothing is listening, and filtered when no answer comes back because a firewall drops the probe. A TCP SYN scan sends the first handshake packet and watches the reply: SYN-ACK means open, RST means closed, nothing means filtered. UDP scanning is slower and less certain because many UDP services do not reply.""",
     """Nmap scan types:
• -sS SYN ('half-open') scan - default when run with privileges; does not complete the handshake.
• -sT connect scan - completes the handshake; used without raw-socket privileges; more likely to be logged.
• -sU UDP scan - open|filtered ambiguity is common; slow because of retransmissions.
• -p 22,80,443 specific ports; -p- all 65,535 TCP ports; --top-ports 1000 (default is the top 1,000).

States: open, closed, filtered, unfiltered (ACK scan), open|filtered, closed|filtered.

Firewall behaviour: stateful firewalls drop (filtered) or reject (closed-looking RST/ICMP unreachable). Rate limiting and IPS can block your scanner part-way through - results may then show everything filtered. Timing: -T3 default; -T4 faster on reliable networks; avoid aggressive timing on fragile networks.

Scan limitations: a closed or filtered port today may open tomorrow; results describe one moment from one network position; scanning from inside vs outside gives very different views.""",
     "Open = someone answers the door; closed = someone shouts 'nobody here'; filtered = a guard at the gate silently ignores you.",
     "An external scan showed 3389 filtered; the same scan from the guest Wi-Fi showed it open - revealing a segmentation gap.",
     "Port states drive findings like 'RDP exposed to the internet' and help verify firewall rules.",
     ["Send SYN", "SYN-ACK -> open", "RST -> closed", "No reply -> filtered", "Record + context"],
     ["In the simulator, run a SYN scan and classify each port state", "Explain one open|filtered UDP result", "Compare internal vs external scan results"],
     "A full TCP scan shows every port filtered after port 2,000. What might have happened and how would you confirm it?",
     ["Treating filtered as closed", "Assuming the default 1,000 ports are 'all ports'", "Using aggressive timing on fragile networks"],
     "Open accepts, closed rejects, filtered is silent. Scan type, firewall and position all shape what you see.",
     ["Port state", "SYN scan", "UDP scan", "Filtered", "Stateful firewall"],
     "Know which reply maps to which state in a SYN scan.",
     [Q("In a SYN scan, a SYN-ACK reply means the port is...", ["Closed", "Filtered", "Open", "Unfiltered"], 2, "SYN-ACK = listening service."),
      Q("No response to a SYN probe usually means...", ["Open", "Closed", "Filtered", "Host down"], 2, "A firewall likely dropped it."),
      Q("How many TCP ports does Nmap scan by default?", ["100", "1,000 most common", "65,535", "All UDP"], 1, "The top 1,000 ports.")]),

   L("m07l3", "Service Enumeration, OS Fingerprinting and Nmap in Practice", "Intermediate", 40,
     "Knowing that port 445 is open is not enough - you need the service, version and configuration.",
     ["Enumerate service versions", "Explain OS fingerprinting concepts", "Read Nmap output confidently"],
     """Service enumeration identifies what software runs on each open port and which version. Nmap's -sV sends probes and matches responses against a database. OS fingerprinting (-O) guesses the operating system from subtle differences in how network stacks respond. The Nmap Scripting Engine (NSE) runs scripts for deeper, safe enumeration such as reading SMB security settings or listing supported TLS ciphers.""",
     """Example (simulated lab output):
PORT     STATE SERVICE  VERSION
22/tcp   open  ssh      OpenSSH 8.2p1 Ubuntu 4ubuntu0.5
80/tcp   open  http     Apache httpd 2.4.41 ((Ubuntu))
445/tcp  open  microsoft-ds  Samba smbd 4.x
3306/tcp open  mysql    MySQL 5.7.33
Service Info: OS: Linux

Reading it: OpenSSH 8.2p1 + Apache 2.4.41 suggests Ubuntu 20.04; MySQL exposed on the network is a finding to review (should it listen only on localhost?); Samba needs SMB signing and share checks.

Useful options: -sV (versions), -sC (default safe scripts), -O (OS guess), -A (aggressive: -sV -sC -O --traceroute), -oA name (save all formats - always save evidence), --script ssl-enum-ciphers -p 443, --script smb2-security-mode.

Enumeration by service: HTTP -> headers, titles, robots.txt; SMB -> shares, signing; SNMP -> community strings (public/private defaults); FTP -> anonymous login; DNS -> zone transfer; SMTP -> open relay checks.

OS fingerprinting is a probability, not proof - confirm through other evidence.""",
     "Port scanning finds the doors; enumeration reads the brand and model of each lock.",
     "Version enumeration showed an internal web server running a CMS version with a publicly known critical vulnerability - which became the top-priority finding.",
     "Versions map to CVEs in Module 8; configuration details become findings on their own.",
     ["Open ports", "-sV versions", "-sC safe scripts", "-O OS guess", "Save -oA", "Analyse"],
     ["Run -sV and -sC against the lab web server in the simulator", "Identify which services should not be network-exposed", "Save results conceptually with -oA"],
     "From the sample output above, list three follow-up checks and why.",
     ["Trusting version banners blindly", "Not saving scan output as evidence", "Running intrusive scripts without approval"],
     "Enumerate services and versions, use safe scripts, treat OS guesses as hypotheses and always save output.",
     ["Service enumeration", "Banner", "OS fingerprinting", "NSE", "Version detection"],
     "-A combines -sV, -sC, -O and traceroute.",
     [Q("Which Nmap flag detects service versions?", ["-sn", "-sV", "-Pn", "-sU"], 1, "-sV enables version detection."),
      Q("What does -oA do?", ["Scans all ports", "Saves output in all major formats", "Aggressive timing", "OS detection"], 1, "Normal, XML and grepable outputs."),
      TF("OS fingerprinting results are always certain.", False, "They are best guesses based on stack behaviour.")]),

   L("m07l4", "Interpreting Results, False Positives, Scanner Limitations and Safe Methodology", "Intermediate", 35,
     "Clients pay for accurate findings. Unvalidated scanner output erodes trust and wastes remediation effort.",
     ["Validate scan results", "Explain vulnerability scanner limitations", "Apply a safe scanning methodology with Nmap, Netcat, Wireshark, OpenVAS and Nessus"],
     """Scanners are fast but imperfect. A false positive is a reported issue that does not exist; a false negative is a real issue the scanner missed. Results must be validated: confirm the service manually, check versions from more than one source and consider whether a backported patch has fixed a flaw even though the version number looks old. Vulnerability scanners such as OpenVAS (free, open source) and Nessus (commercial, with a limited free Essentials edition) automate checks but still need expert interpretation.""",
     """Validation techniques:
• Netcat banner check: nc -nv 10.10.10.15 21 (read the banner yourself).
• Wireshark: confirm what the scanner actually sent and received.
• Check package versions on the host (with access) - Linux distributions backport fixes, so 'Apache 2.4.41' may be patched.
• Credentialed scans give far more accurate results than unauthenticated ones.

Scanner limitations: cannot understand business logic; may crash fragile services; miss issues behind authentication; depend on plugin updates; produce noise.

Safe scanning methodology:
1. Confirm written scope, exclusions and testing window.
2. Notify the client's contact/SOC if agreed.
3. Start with discovery, then light port scans, then service enumeration.
4. Use moderate timing; avoid DoS-prone checks unless approved.
5. Monitor for impact; stop and call the emergency contact if a system degrades.
6. Save all output with timestamps.
7. Validate before reporting.""",
     "A scanner is a smoke detector: very useful, but sometimes it goes off for burnt toast - someone has to check if there is really a fire.",
     "A scanner flagged 300 'critical' OpenSSL issues on a Red Hat estate; most were false positives because Red Hat had backported fixes. Credentialed validation reduced it to 4 real issues.",
     "Accurate validation is a core professional skill and a scoring criterion in your capstone.",
     ["Scope + window", "Discovery", "Port scan", "Enumeration", "Vuln scan", "Validate", "Report"],
     ["Complete the Scanning Simulator lab end-to-end", "Validate one 'finding' with a Netcat banner grab in the simulator", "Mark a sample scanner result as true or false positive"],
     "Write the stop conditions you would agree with a hospital client before scanning.",
     ["Copying scanner output straight into a report", "Running intrusive plugins on production without approval", "Ignoring backporting"],
     "Validate everything, understand scanner limits and follow a scoped, monitored, documented methodology.",
     ["False positive", "False negative", "Credentialed scan", "Netcat", "OpenVAS", "Nessus"],
     "A credentialed scan reduces false positives - a frequent exam answer.",
     [Q("A scanner reports a vulnerability that does not actually exist. This is a...", ["False negative", "True positive", "False positive", "True negative"], 2, "Reported but not real = false positive."),
      Q("Why might an 'old' version number not be vulnerable?", ["Versions never matter", "The vendor backported a security fix", "Scanners are always wrong", "Ports were filtered"], 1, "Distributions backport patches without changing the upstream version."),
      Q("What improves vulnerability scan accuracy most?", ["Faster timing", "Credentialed (authenticated) scanning", "Scanning only UDP", "Skipping discovery"], 1, "Credentials let the scanner check installed packages directly.")]),
  ],
  ["Discover hosts in a simulated network", "Identify open, closed and filtered ports", "Identify services from sample outputs", "Interpret scan results", "Create a network map",
   "Identify unexpected exposed services", "Analyse sample scan reports", "Recommend improvements"],
  "p05",
  "Complete all 4 lessons, pass the module assessment and complete the Scanning Simulator lab.",
  ["Map SYN-scan replies to states", "List 8 Nmap options from memory", "Explain three causes of false positives"],
  ["Network discovery", "Host discovery", "Ports", "TCP/UDP", "Port states", "Service discovery", "Enumeration", "Network mapping", "OS fingerprinting", "Scan interpretation",
   "Firewall behaviour", "Scan limitations", "False positives", "Scope", "Scanner limitations", "Safe methodology", "Nmap", "Wireshark", "Netcat", "OpenVAS", "Nessus"])


M("m08", 8, "Vulnerability Assessment", "bug",
  "Finding vulnerabilities is only half the job. This module teaches how to describe, score, prioritise and drive remediation of vulnerabilities across their lifecycle.",
  ["Run a vulnerability management lifecycle", "Use CVE, CVSS, NVD, KEV and EPSS", "Prioritise using risk and asset criticality",
   "Handle false positives and negatives through validation", "Plan remediation, patching and risk acceptance"],
  ["Module 7"],
  [
   L("m08l1", "Vulnerability Management and the Vulnerability Lifecycle", "Intermediate", 30,
     "Organisations do not fail because they lack scanners; they fail because findings never get fixed.",
     ["Describe the vulnerability management cycle", "Explain discovery sources", "Track vulnerabilities to closure"],
     """Vulnerability management is a continuous cycle: discover assets and vulnerabilities, assess and prioritise them, remediate or mitigate, verify the fix and report. Vulnerabilities are discovered by scanners, penetration tests, bug bounty reports, vendor advisories and threat intelligence. Each one moves through a lifecycle from discovery to disclosure, patch availability, deployment and verification.""",
     """Cycle: Asset inventory -> Discovery -> Assessment/validation -> Prioritisation -> Remediation/mitigation -> Verification (rescan/retest) -> Reporting & metrics -> repeat.

Vulnerability lifecycle (from the industry view): introduced in code -> discovered (by vendor, researcher or attacker) -> disclosed (coordinated) -> CVE assigned -> patch released -> exploit published -> patch deployed by customers. The gap between disclosure and deployment is where most attacks happen. A zero-day is exploited before a fix exists.

Metrics: mean time to remediate (MTTR) by severity, SLA compliance (e.g. critical within 14 days), percentage of assets scanned, recurring findings, risk accepted count.

Ownership: every finding needs an owner (system/asset owner) and a due date. Ticketing systems (Jira, ServiceNow, or free tools such as GitHub Issues for small teams) track status: Open -> In progress -> Fixed -> Verified -> Closed (or Risk accepted).""",
     "Vulnerability management is like a hospital ward round: find problems, decide who is most urgent, treat them, then check the treatment worked.",
     "Many major breaches exploited vulnerabilities whose patches had been available for months - the problem was the remediation process, not detection.",
     "Assessors who understand the full lifecycle write findings that are easier for clients to act on.",
     ["Inventory", "Discover", "Assess", "Prioritise", "Remediate", "Verify", "Report"],
     ["Map the lifecycle stages to a fictional finding", "Define SLAs for critical/high/medium/low", "Design ticket statuses"],
     "Explain why verification (rescanning) is a separate step from remediation.",
     ["No asset inventory - unknown assets are never scanned", "Closing tickets without verification", "Findings with no owner"],
     "Discover, prioritise, fix, verify, report - continuously, with owners and SLAs.",
     ["Vulnerability management", "Zero-day", "MTTR", "SLA", "Remediation"],
     "A zero-day has no available patch at the time it is exploited.",
     [Q("What should happen after a vulnerability is remediated?", ["Delete the ticket", "Verify the fix by rescanning or retesting", "Ignore it", "Accept the risk"], 1, "Verification confirms the fix."),
      Q("A vulnerability exploited before a patch exists is a...", ["Legacy bug", "Zero-day", "False positive", "Misconfiguration"], 1, "Zero-day."),
      Q("MTTR measures...", ["Mean time to remediate", "Maximum threat to resources", "Malware type tracking record", "Minimum test runtime"], 0, "Mean time to remediate.")]),

   L("m08l2", "CVE, CVSS and Vulnerability Databases (NVD, KEV, EPSS)", "Intermediate", 40,
     "These identifiers and scores are the common language between scanners, vendors and clients.",
     ["Read a CVE identifier and record", "Calculate and interpret CVSS base scores", "Use KEV and EPSS to judge real-world exploitation"],
     """A CVE (Common Vulnerabilities and Exposures) ID such as CVE-2021-44228 uniquely names a public vulnerability. CVSS (Common Vulnerability Scoring System) gives a 0-10 severity score from metrics such as attack vector, complexity, privileges required, user interaction and impact on confidentiality, integrity and availability. The NVD (US National Vulnerability Database) enriches CVEs with scores and details. CISA's KEV catalogue lists vulnerabilities known to be exploited in the wild, and EPSS estimates the probability of exploitation.""",
     """CVSS v3.1 base metrics: Attack Vector (Network/Adjacent/Local/Physical), Attack Complexity (Low/High), Privileges Required (None/Low/High), User Interaction (None/Required), Scope (Unchanged/Changed), Confidentiality/Integrity/Availability impact (None/Low/High).
Example vector: CVSS:3.1/AV:N/AC:L/PR:N/UI:N/S:U/C:H/I:H/A:H = 9.8 Critical.

Severity bands: 0.1-3.9 Low, 4.0-6.9 Medium, 7.0-8.9 High, 9.0-10.0 Critical.

CVSS v4.0 (released 2023) adds supplemental metrics and renames some groups; both versions appear in tools today.

Base score = intrinsic severity. Temporal/threat metrics (exploit maturity) and environmental metrics (your asset's importance and controls) adjust it for your context.

Other sources: vendor advisories, Exploit-DB, GitHub Security Advisories, CWE (weakness categories, e.g. CWE-89 SQL injection). Use the in-app CVSS calculator to practise.""",
     "A CVE number is a passport number for a vulnerability; CVSS is its danger rating; KEV is the 'wanted - actively seen in attacks' list.",
     "Log4Shell (CVE-2021-44228) scored CVSS 10.0, appeared in KEV and was exploited at massive scale within days of disclosure.",
     "Findings cite CVEs and CVSS; prioritisation increasingly uses KEV and EPSS so effort goes to what attackers actually use.",
     ["CVE ID", "NVD record", "CVSS vector", "Severity band", "KEV listed?", "EPSS probability"],
     ["Use the in-app CVSS calculator to score AV:N/AC:L/PR:N/UI:R/S:U/C:H/I:N/A:N", "Explain what each metric in a vector means", "Classify scores 3.1, 6.5, 7.2 and 9.1 into bands"],
     "Two findings: one CVSS 9.8 not in KEV, one CVSS 7.5 listed in KEV and internet-facing. Which do you fix first and why?",
     ["Treating CVSS as the full risk picture", "Ignoring KEV", "Confusing CVE (specific flaw) with CWE (weakness type)"],
     "CVE names it, CVSS rates severity, KEV and EPSS show real-world exploitation, environment and asset value complete the picture.",
     ["CVE", "CVSS", "NVD", "KEV", "EPSS", "CWE"],
     "Memorise the CVSS severity bands and the meaning of AV:N and PR:N.",
     [Q("A CVSS score of 7.4 is rated...", ["Medium", "High", "Critical", "Low"], 1, "7.0-8.9 is High."),
      Q("What does CISA's KEV catalogue list?", ["All CVEs", "Vulnerabilities known to be exploited in the wild", "Malware samples", "Firewall rules"], 1, "Known Exploited Vulnerabilities."),
      Q("In a CVSS vector, AV:N means...", ["No attack vector", "Network attack vector", "Needs physical access", "No privileges"], 1, "Exploitable remotely over the network.")]),

   L("m08l3", "Risk, Likelihood, Impact, Asset Criticality and Prioritisation", "Intermediate", 35,
     "Clients have limited time and money. Your job is to tell them what to fix first and why.",
     ["Combine severity with context", "Rate asset criticality", "Produce a prioritised remediation list"],
     """Severity describes how bad a vulnerability is in general; risk describes how bad it is for this organisation. Risk combines likelihood (exposure, exploit availability, attacker interest) and impact (what the affected asset does and holds). Asset criticality ranks systems by business importance - a payment database matters more than a test printer. Prioritise findings that are severe, exploitable, exposed and on critical assets.""",
     """Prioritisation factors:
• Exposure: internet-facing > internal > isolated.
• Exploitability: KEV-listed / public exploit > proof-of-concept > theoretical.
• Asset criticality: crown jewels (customer data, payments, identity systems like AD) > business systems > low-value.
• Compensating controls: WAF, segmentation, MFA may reduce likelihood.
• Data sensitivity and regulatory impact (GDPR, PCI DSS).

Simple model: Priority = CVSS band adjusted up for KEV/internet exposure/critical asset, down for strong compensating controls.

Example prioritised list:
1. VPN appliance CVE in KEV, internet-facing - Critical, fix in 48h.
2. Domain controller missing patch with public exploit - High, 7 days.
3. Internal intranet XSS, low-sensitivity data - Medium, 30 days.
4. TLS 1.0 enabled on internal printer - Low, next cycle.

Communicate impact in business language: 'An attacker on the internet could take over the VPN and access the internal network without credentials.'""",
     "Triage in an emergency department: the most serious injuries on the most vulnerable patients are treated first, not simply in arrival order.",
     "A team spent weeks fixing hundreds of low findings while one internet-facing critical issue stayed open - and was exploited. Prioritisation would have reversed that order.",
     "Prioritised, business-aware recommendations are what separate a professional report from a scanner dump.",
     ["Finding", "Severity (CVSS)", "+ Exposure", "+ Exploitability (KEV/EPSS)", "+ Asset criticality", "= Priority"],
     ["Complete the CVSS Prioritisation lab", "Rate five assets by criticality", "Rewrite one technical impact in business language"],
     "Prioritise a list of 10 fictional findings and justify the top three in writing.",
     ["Sorting by CVSS alone", "Ignoring compensating controls", "Writing impact only in technical jargon"],
     "Priority = severity in context: exposure, exploitability, asset criticality and controls. Explain it in business terms.",
     ["Risk", "Likelihood", "Impact", "Asset criticality", "Prioritisation"],
     "Expect scenario questions where the lower CVSS score should be fixed first because of exposure or KEV status.",
     [Q("Which finding should usually be fixed first?", ["CVSS 9.0 on an isolated lab VM", "CVSS 8.1 KEV-listed on an internet-facing VPN", "CVSS 5.0 on a printer", "CVSS 3.0 on a test server"], 1, "Exposure plus active exploitation drives priority."),
      Q("Asset criticality measures...", ["Patch size", "Business importance of the system", "Scanner speed", "CVE age"], 1, "How important the asset is to the business."),
      TF("A compensating control can lower the likelihood of exploitation.", True, "e.g. segmentation or WAF can reduce likelihood.")]),

   L("m08l4", "False Positives, False Negatives, Validation and Scanner Limitations", "Intermediate", 30,
     "Validation protects your credibility and the client's resources.",
     ["Classify scanner results correctly", "Validate findings safely", "Explain what scanners cannot find"],
     """Every scanner result must be treated as a hypothesis. A true positive is a real issue correctly found. A false positive is reported but not real. A false negative is a real issue that was missed - often more dangerous because nobody knows it exists. Validation means confirming a finding with additional evidence, using safe, non-destructive methods within scope.""",
     """Causes of false positives: version-only detection with backported patches, generic signatures, unusual services on standard ports, outdated plugins.
Causes of false negatives: unauthenticated scans, services behind login, WAF/IPS blocking probes, non-standard ports not scanned, business logic flaws, timing issues, scope gaps.

Safe validation methods: check installed package versions with credentials; confirm configuration directly (e.g. read sshd_config); manual banner checks; vendor-provided detection scripts; safe proof that demonstrates existence without causing impact (e.g. reading a harmless version endpoint, not dumping data).

Documenting validation: record what you did, when, the evidence (screenshot or output), and why you concluded true/false positive.

Scanner limitations summary: no business context, limited auth coverage, cannot chain issues, cannot judge impact, can disrupt fragile systems.""",
     "A metal detector beeps (scanner alert). Digging carefully (validation) tells you whether it is treasure, a bottle cap or nothing at all.",
     "A validated false positive saved a hospital from an emergency weekend patch of 200 servers.",
     "Your findings list must contain validated issues only - and your report should state the limits of what was tested.",
     ["Scanner result", "Gather evidence", "Safe validation", "True or false positive?", "Document reasoning"],
     ["Classify ten sample scanner results as TP/FP with reasons", "List four causes of false negatives", "Write a validation note"],
     "Design a validation plan for a 'critical' OpenSSH finding on an Ubuntu server.",
     ["Exploiting to 'prove' a finding without approval", "Assuming no findings means secure", "Not documenting validation steps"],
     "Treat results as hypotheses, validate safely, document reasoning and state limitations.",
     ["True positive", "False positive", "False negative", "Validation"],
     "False negatives are usually considered more dangerous than false positives.",
     [Q("A real vulnerability that a scanner did not report is a...", ["False positive", "False negative", "True positive", "True negative"], 1, "Missed real issue = false negative."),
      Q("Which is a common cause of false negatives?", ["Backported patches", "Unauthenticated scanning of services behind login", "Too many plugins", "Credentialed scans"], 1, "The scanner cannot see behind authentication."),
      Q("Safe validation should...", ["Dump the database to prove impact", "Demonstrate existence without causing harm", "Disable logging", "Skip documentation"], 1, "Prove existence with minimal impact.")]),

   L("m08l5", "Misconfiguration, Patch Management, Remediation and Risk Acceptance", "Intermediate", 35,
     "Most real-world weaknesses are misconfigurations and missing patches. Good remediation advice is specific, achievable and verifiable.",
     ["Recognise common misconfigurations", "Describe a patch management process", "Write remediation plans and handle risk acceptance"],
     """Misconfigurations are insecure settings: default passwords, unnecessary services, open shares, overly permissive cloud storage, weak TLS. Patch management keeps software updated through a controlled process: inventory, test, deploy, verify. Remediation fixes the root cause; mitigation reduces risk when a fix is not yet possible. When an organisation decides not to fix something, an accountable owner must formally accept the risk with a review date.""",
     """Common misconfigurations: default credentials; directory listing; verbose errors; SMBv1; anonymous FTP; SNMP 'public'; open S3/Blob storage; admin interfaces exposed to the internet; missing security headers; excessive permissions.

Patch management process: maintain inventory -> monitor advisories -> assess applicability and priority -> test in staging -> schedule (maintenance windows) -> deploy -> verify (rescan) -> report. Emergency patching path for KEV/critical issues.

Remediation plan entries: finding, owner, action (specific: 'Disable TLS 1.0/1.1 in nginx ssl_protocols; allow TLSv1.2 TLSv1.3'), priority, due date, verification method, status.

Mitigation examples when patching is delayed: restrict access with firewall rules, disable the vulnerable feature, add WAF rules, increase monitoring.

Risk acceptance: written, by someone with authority, with reason, compensating controls and an expiry/review date. Not acceptable as a default for critical internet-facing issues.""",
     "Remediation is fixing the leaking pipe; mitigation is putting a bucket under it until the plumber comes; risk acceptance is the owner signing that they know about the leak.",
     "A company could not patch a legacy system for six months, so it isolated it on a separate VLAN with strict firewall rules and monitoring - a documented mitigation with an expiry date.",
     "Your recommendations will be judged on whether they are specific, practical and verifiable.",
     ["Finding", "Root cause", "Fix or mitigate", "Owner + due date", "Verify", "Close or accept (documented)"],
     ["Write remediation steps for three misconfigurations", "Build a mini patch schedule", "Draft a risk acceptance record"],
     "Build a vulnerability remediation plan for the fictional Silverline findings in Project 6.",
     ["Vague advice like 'improve security'", "No verification method", "Risk acceptance without an expiry date"],
     "Fix root causes, patch through a tested process, mitigate when you must, and document any accepted risk with an owner and review date.",
     ["Misconfiguration", "Patch management", "Mitigation", "Risk acceptance", "Compensating control"],
     "Mitigation reduces risk; remediation removes the vulnerability. Know the difference.",
     [Q("Restricting access to a vulnerable service with a firewall while waiting for a patch is...", ["Remediation", "Mitigation", "Risk transfer", "Ignoring"], 1, "It reduces risk without removing the flaw."),
      Q("A good risk acceptance includes...", ["No owner", "An accountable owner and a review date", "Only verbal approval", "Deleting the finding"], 1, "Documented, owned and time-limited."),
      Q("Which is a misconfiguration?", ["A zero-day in the kernel", "Default admin password left unchanged", "A strong passphrase", "TLS 1.3 only"], 1, "Default credentials are a classic misconfiguration.")]),
  ],
  ["Analyse fictional vulnerability reports", "Match findings to CVEs", "Calculate and interpret risk", "Compare CVSS scores", "Prioritise a remediation list",
   "Identify false positives", "Review configuration weaknesses", "Build a vulnerability remediation plan"],
  "p06",
  "Complete all 5 lessons, pass the module assessment and complete the CVSS Prioritisation lab.",
  ["Recite CVSS bands", "Explain KEV and EPSS", "Write a specific remediation step for a TLS finding"],
  ["Vulnerability management", "Discovery", "CVE", "CVSS", "Databases", "Severity", "Risk", "Likelihood", "Impact", "Asset criticality", "Misconfiguration", "Patch management",
   "Remediation", "Risk acceptance", "False positives", "False negatives", "Scanner limitations", "Validation", "Prioritisation", "Lifecycle"])
