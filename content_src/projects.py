from lib import Q

PROJECTS = []

LEVELS = {"4": "Excellent", "3": "Good", "2": "Developing", "1": "Insufficient"}


def R(criterion, excellent, insufficient):
    return {"criterion": criterion, "excellent": excellent, "insufficient": insufficient}


SAFETY = R("Safety & scope compliance", "All work in isolated lab or supplied data; scope and rules stated and followed.", "Any activity outside the lab, scope or rules (automatic fail).")


def P(id, number, title, module, summary, objectives, tasks, deliverable, rubric, labs):
    PROJECTS.append({"id": id, "number": number, "title": title, "module": module, "summary": summary, "objectives": objectives,
                     "tasks": tasks, "deliverable": deliverable, "rubric": [SAFETY] + rubric, "labs": labs})


P("p01", 1, "Cybersecurity Workstation", "m01", "Build Your Cybersecurity Learning Environment: a safe, isolated virtual lab you will use for the rest of the course.",
  ["Understand your hardware", "Install a hypervisor and Linux VM", "Configure snapshots and isolated networking", "Document the setup"],
  ["Record your computer's CPU, cores, RAM, free disk and virtualization support (VT-x/AMD-V)", "Install a free hypervisor (VirtualBox or VMware Workstation Pro free edition)",
   "Download a Linux ISO/VM image and verify its SHA-256 checksum", "Create a VM (2 vCPU, 4 GB RAM, 40 GB disk where possible) and install Linux",
   "Create an Internal/Host-only network and attach the VM to it", "Take a snapshot named 'clean-install' and practise restoring it",
   "Write a one-page setup document with a simple network diagram", "List five personal lab safety rules"],
  "Setup document (specs, hypervisor, VM settings, network mode, snapshot plan, diagram, safety rules).",
  [R("Accuracy", "Specs and settings recorded correctly with checksum evidence.", "Missing or wrong core details."),
   R("Methodology", "Logical, reproducible steps.", "No clear process."),
   R("Documentation", "Clear diagram and notes others could follow.", "No usable documentation.")], ["lab_linux_nav"])

P("p02", 2, "Network Engineer Challenge", "m02", "Create and analyse a virtual network: plan subnets, assign addresses and verify services.",
  ["Design subnets", "Configure a virtual network", "Verify connectivity and DNS"],
  ["Design three subnets (users, servers, IoT) from 10.50.0.0/22 with justification", "Draw the topology with gateway and DNS", "Assign static IPs for servers and a DHCP range for users",
   "Configure two VMs on an internal network and verify connectivity with ping and traceroute", "Complete the Network Basics and Subnetting labs", "Write firewall rules (in words) allowing only needed traffic between zones"],
  "Network design pack: subnet table, diagram, addressing plan, test results, zone rules.",
  [R("Accuracy", "All subnet maths correct.", "Major calculation errors."),
   R("Technical reasoning", "Sizes and rules justified by requirements.", "No justification."),
   R("Evidence quality", "Test output captured and explained.", "No evidence.")], ["lab_net_basics", "lab_subnet", "lab_pcap"])

P("p03", 3, "Linux Security Audit", "m03", "Audit a Linux system using a safe checklist and produce prioritised recommendations.",
  ["Apply a hardening checklist", "Gather evidence", "Prioritise fixes"],
  ["Complete the Linux Permissions Audit and Log Analysis labs", "Run a read-only audit of your own lab VM: users, sudo, SUID, world-writable files, services, SSH config, firewall, updates",
   "Record evidence for each check", "Rate each issue (Critical-Low) with reasoning", "Write remediation commands and verify them in your VM", "Write a 1-page summary"],
  "Linux audit report with checklist, evidence, ratings and remediation.",
  [R("Accuracy", "Findings correct and evidenced.", "Incorrect findings."),
   R("Risk classification", "Ratings consistent and justified.", "No ratings."),
   R("Remediation quality", "Specific, tested fixes.", "Vague or missing.")], ["lab_linux_perms", "lab_linux_logs"])

P("p04", 4, "Windows Security Audit", "m03", "Review a simulated Windows environment and recommend hardening.",
  ["Audit accounts, firewall, Defender, services and logs", "Map findings to controls"],
  ["Complete the Windows PowerShell Audit lab", "List each finding with evidence from the simulator", "Map findings to controls (LAPS, GPO firewall, Defender updates, least privilege)",
   "Write PowerShell commands that would verify each fix", "Identify three event IDs to monitor and why"],
  "Windows audit report.",
  [R("Accuracy", "All key findings identified.", "Major findings missed."),
   R("Technical reasoning", "Correct control mapping.", "No mapping."),
   R("Report quality", "Clear and professional.", "Unclear.")], ["lab_win_ps"])

P("p05", 5, "Network Discovery Project", "m07", "Build an authorised network inventory from a supplied simulated environment.",
  ["Discover hosts within scope", "Enumerate services", "Produce an inventory and map"],
  ["Read the scope and exclusions", "Complete the Scanning Simulator lab without scope violations", "Build an inventory table (IP, hostname, OS guess, services, versions, owner, notes)",
   "Draw the network map", "Flag unexpected exposed services with recommendations", "Validate one finding with a second method"],
  "Inventory spreadsheet/table, network map and exposure findings.",
  [R("Scope compliance", "No exclusions touched; output saved.", "Scope breached."),
   R("Accuracy", "Inventory complete and correct.", "Hosts/services missing."),
   R("Evidence quality", "Saved output referenced.", "No evidence.")], ["lab_scan_sim"])

P("p06", 6, "Vulnerability Assessment", "m08", "Assess a fictional company lab and prioritise findings into a remediation plan.",
  ["Score findings", "Prioritise with context", "Plan remediation"],
  ["Complete the CVSS and Prioritisation lab", "Score five findings with the CVSS calculator", "Identify and justify at least one false positive", "Prioritise using exposure, KEV and asset criticality",
   "Build a remediation plan with owners, dates and verification", "Write a 150-word executive summary"],
  "Prioritised vulnerability report and remediation plan.",
  [R("Risk classification", "Scores and context correct.", "Wrong scoring."),
   R("Remediation quality", "Owners, dates, verification included.", "No plan."),
   R("Professional communication", "Executive summary clear.", "Jargon-heavy or missing.")], ["lab_cvss"])

P("p07", 7, "Web Security Project", "m09", "Complete a guided assessment of the locally simulated Silverline Portal.",
  ["Test access control, injection, XSS, traversal and SSRF", "Verify fixes", "Report"],
  ["Complete every task in the Web Security lab", "Write findings for IDOR, SQLi, stored XSS, traversal and SSRF in the Report Builder", "For each, document the v2 verification",
   "Add missing security headers and cookie flags as a finding", "Export a PDF report"],
  "Web assessment report PDF (Report Builder).",
  [R("Accuracy", "All five flaws correctly described.", "Flaws misdescribed."),
   R("Evidence quality", "Minimal, redacted evidence.", "Excessive or no evidence."),
   R("Remediation quality", "Developer-ready fixes and verification.", "Vague advice.")], ["lab_web_sec", "lab_report_finding"])

P("p08", 8, "API Security Project", "m09", "Assess a mock API using supplied endpoints and data.",
  ["Build an authorisation matrix", "Find BOLA, excessive data and mass assignment", "Recommend secure API design"],
  ["Review the supplied endpoint list: GET /api/v1/users/{id}, PATCH /api/v1/users/{id}, GET /api/v1/orders/{id}, POST /api/v1/coupons/redeem, GET /api/v0/users (legacy)",
   "Build a roles x endpoints authorisation matrix (customer, support, admin)", "Using the supplied responses, identify: excessive data (password_hash and dob in user responses), mass assignment (role field accepted on PATCH), BOLA on orders, unlimited coupon redemption, legacy v0 without auth",
   "Map each to the OWASP API Top 10 2023", "Write remediation and verification for each"],
  "API assessment report.",
  [R("Accuracy", "All five issues found and mapped.", "Fewer than three."),
   R("Methodology", "Complete authorisation matrix.", "No matrix."),
   R("Remediation quality", "Secure design recommendations.", "Missing.")], ["lab_web_sec"])

P("p09", 9, "Wireless Security Project", "m11", "Assess a fictional wireless network configuration and recommend improvements.",
  ["Review survey data", "Identify rogue/weak networks", "Design improvements"],
  ["Complete the Wireless Configuration Review lab", "Write findings for WPS, open guest network, rogue AP and Enterprise certificate validation", "Design the target architecture (SSIDs, auth, VLANs)",
   "Write a staff awareness note about unknown Wi-Fi networks"],
  "Wireless review and target design.",
  [R("Accuracy", "All issues identified.", "Rogue AP missed."),
   R("Technical reasoning", "WPA3/EAP-TLS/isolation justified.", "Unjustified."),
   R("Professional communication", "Clear awareness note.", "Missing.")], ["lab_wireless"])

P("p10", 10, "Mobile Security Project", "m11", "Review the deliberately insecure sample app SilverNotes.",
  ["Static review", "MASVS mapping", "Hardening plan"],
  ["Complete the Mobile Permissions and Static Review lab", "Map findings to MASVS groups", "Write remediation for each with Android-specific detail (manifest, network security config, Keystore)",
   "Create a mobile release security checklist (10 items)"],
  "Mobile review report and checklist.",
  [R("Accuracy", "All findings and MASVS mapping correct.", "Major errors."),
   R("Remediation quality", "Platform-specific and actionable.", "Generic."),
   R("Documentation", "Useful release checklist.", "Missing.")], ["lab_mobile"])

P("p11", 11, "Active Directory Security Project", "m13", "Review a simulated corporate domain and identify misconfigurations.",
  ["Interpret AD health data", "Explain attack paths", "Prioritise hardening"],
  ["Complete the AD Misconfiguration Review lab", "Draw the Helpdesk -> Domain Admins attack path", "Write findings for privileged group size, Kerberoastable service account, LAPS, krbtgt rotation",
   "Create a 90-day AD hardening roadmap", "List monitoring event IDs for regression"],
  "AD security review and roadmap.",
  [R("Technical reasoning", "Attack path clearly explained.", "Path not understood."),
   R("Risk classification", "Tier 0 prioritised correctly.", "Wrong priority."),
   R("Remediation quality", "Realistic roadmap.", "Missing.")], ["lab_ad_review"])

P("p12", 12, "Cloud Security Project", "m13", "Audit a fictional cloud deployment.",
  ["Review IAM, storage, network and logging", "Map to CIS benchmarks", "Design guardrails"],
  ["Complete the Cloud IAM and Storage Review lab", "Rewrite the wildcard policy with least privilege for a developer role", "Write findings with CIS mapping",
   "Propose preventive guardrails (block public access, SCPs, Config rules)", "Threat model the three-tier app"],
  "Cloud audit report with guardrails and threat model.",
  [R("Accuracy", "All issues identified.", "Public bucket missed."),
   R("Technical reasoning", "Least-privilege policy correct.", "Still wildcard."),
   R("Remediation quality", "Preventive guardrails.", "Only one-off fixes.")], ["lab_cloud_iam"])

P("p13", 13, "Container Security Project", "m13", "Review a sample container environment.",
  ["Review runtime and image", "Fix the Dockerfile", "Design CI controls"],
  ["Complete the Container Security Review lab", "Rewrite the Dockerfile: slim base, non-root USER, no secrets, pinned versions", "Write run-time fixes (no --privileged, no socket mount, read-only fs, limits)",
   "Design a CI pipeline step list (build, Trivy scan, sign, push)"],
  "Container review with fixed Dockerfile and CI design.",
  [R("Accuracy", "All runtime issues identified.", "Socket mount missed."),
   R("Technical reasoning", "Correct Dockerfile.", "Still root/secrets."),
   R("Methodology", "Complete CI controls.", "Missing.")], ["lab_container"])

P("p14", 14, "Capstone Ethical Hacking Project", "m15", "Conduct a complete authorised assessment of the fictional Silverline Enterprise in the contained capstone simulation.",
  ["Run a full engagement from scope to retest", "Produce a professional report", "Present findings"],
  ["Complete the Silverline Enterprise capstone (all 17 stages)", "Define scope and confirm rules of engagement", "Review assets and perform reconnaissance using supplied information", "Map the environment",
   "Identify weaknesses and validate safely", "Assess risks and collect evidence", "Recommend fixes", "Produce a professional report in the Report Builder", "Prepare a 5-slide executive presentation outline", "Verify remediations in the retest stage"],
  "Capstone report PDF, presentation outline and portfolio entry.",
  [R("Methodology", "All phases followed in order.", "Phases skipped."),
   R("Technical reasoning", "Sound decisions at each stage.", "Guesswork."),
   R("Evidence quality", "Minimal, sufficient evidence.", "Missing or excessive."),
   R("Report quality", "Complete, accurate, professional.", "Incomplete."),
   R("Professional communication", "Clear executive presentation.", "Missing.")], ["lab_report_finding", "lab_exploit_safety"])

# ---------------- CHALLENGE ARENA ----------------
CHALLENGES = []


def CH(id, title, kind, difficulty, minutes, prompt, artifact, question, options, answer, explain, hints):
    CHALLENGES.append({"id": id, "title": title, "kind": kind, "difficulty": difficulty, "minutes": minutes, "prompt": prompt,
                       "artifact": artifact.strip("\n"), "question": question, "options": options, "answer": answer, "explain": explain, "hints": hints})


CH("c01", "Spot the Weakness: sshd_config", "Find a configuration error", "Easy", 3, "Review this SSH configuration for an internet-facing server.",
   "Port 22\nPermitRootLogin yes\nPasswordAuthentication yes\nPubkeyAuthentication yes\nX11Forwarding no", "Which line is the most serious issue?", ["Port 22", "PermitRootLogin yes", "PubkeyAuthentication yes", "X11Forwarding no"], 1,
   "Direct root login over the internet maximises impact of a guessed password. Also disable PasswordAuthentication once keys work.", ["Think about the most powerful account.", "Which setting lets the admin account log in directly?"])
CH("c02", "Read the Scan", "Interpret scan output", "Easy", 3, "Nmap result for an office printer:", "9100/tcp open jetdirect\n80/tcp open http (admin page)\n21/tcp open ftp (anonymous allowed)\n161/udp open snmp (community: public)",
   "Which two findings matter most?", ["9100 and 80", "Anonymous FTP and SNMP public", "Port numbers above 1000", "None"], 1, "Anonymous FTP and default SNMP community expose data and configuration.", ["Look for default or anonymous access.", "'public' is a default value."])
CH("c03", "Packet Puzzle", "Analyse a packet capture", "Medium", 4, "Summary of a capture:", "10.0.0.5 -> 10.0.0.9 TCP SYN to 1-1024 within 3 seconds\n10.0.0.9 -> 10.0.0.5 RST for most, SYN-ACK for 22, 80",
   "What happened?", ["Backup job", "Port scan from 10.0.0.5; 22 and 80 are open on 10.0.0.9", "DNS outage", "ARP spoofing"], 1, "SYN sweep with RST/SYN-ACK replies = scan.", ["Count the ports in 3 seconds.", "SYN-ACK means open."])
CH("c04", "Suspicious Logins", "Identify suspicious logs", "Medium", 4, "Windows Security events:", "02:01 4625 x 300 user=administrator src=10.1.1.50\n02:09 4624 type 3 user=administrator src=10.1.1.50\n02:10 4732 member added: helpdesk01 -> Administrators",
   "What is the most likely story?", ["Normal admin work", "Password guessing succeeded, then privilege change", "System reboot", "Printer issue"], 1, "Burst of failures then success then group change.", ["Read the events in time order.", "What does 4732 mean?"])
CH("c05", "Fix the Query", "Review source code", "Easy", 3, "Python login code:", "cur.execute(f\"SELECT id FROM users WHERE name='{u}' AND pw='{p}'\")", "Best fix?",
   ["Escape quotes manually", "cur.execute(\"SELECT id FROM users WHERE name=? AND pw_hash=?\", (u, hash(p)))", "Use GET", "Add a WAF"], 1, "Parameterise and never compare plaintext passwords.", ["f-strings build SQL text.", "Also look at how passwords are compared."])
CH("c06", "Remediation Match", "Match a vulnerability to remediation", "Easy", 3, "Finding: Stored XSS in product reviews.", "", "Best remediation?", ["Rate limiting", "Context-aware output encoding + CSP", "MFA", "Longer session timeout"], 1, "Encoding stops script execution; CSP limits impact.", ["Where does the script run?"])
CH("c07", "Subnet Sprint", "Solve a networking problem", "Medium", 3, "A host is 172.16.9.200/21.", "", "What is the network address?", ["172.16.8.0", "172.16.9.0", "172.16.0.0", "172.16.15.0"], 0, "/21 block size 8 in third octet: 8-15.", ["/21 = 255.255.248.0", "Block size in 3rd octet: 256-248."])
CH("c08", "Cloud Bucket", "Find a configuration error", "Medium", 3, "Bucket policy:", '{"Effect":"Allow","Principal":"*","Action":"s3:GetObject","Resource":"arn:aws:s3:::hr-payroll/*"}', "What is wrong?",
   ["Nothing", "Anyone on the internet can read payroll files", "Only admins can read", "Encryption missing only"], 1, "Principal * = public.", ["Who is the principal?"])
CH("c09", "Incident Triage", "Analyse a fictional incident", "Hard", 5, "A user reports their files renamed to *.locked and a note on the desktop. Network shares are also affected.", "",
   "First action?", ["Pay the ransom", "Isolate the device from the network and escalate per IR plan", "Reboot repeatedly", "Delete the note"], 1, "Contain spread first, preserve evidence, escalate.", ["Think containment.", "Shares are being affected right now."])
CH("c10", "Header Hunt", "Identify the security weakness", "Easy", 3, "Response headers:", "Set-Cookie: sid=abc123; Path=/\nServer: Apache/2.4.29\nX-Powered-By: PHP/7.2.1", "Which improvement is most important?",
   ["Remove Path", "Add HttpOnly; Secure; SameSite to the session cookie", "Rename sid", "Use port 8080"], 1, "Protect the session cookie; also hide versions and patch.", ["Focus on the session."])
CH("c11", "Hash or Encrypt?", "Identify the security weakness", "Easy", 2, "A developer stores passwords using AES with a key in the config file.", "", "What is the issue?",
   ["None", "Passwords should be hashed with a slow salted algorithm, not reversibly encrypted", "AES is too slow", "Keys should be shorter"], 1, "Reversible storage means one key leak exposes all passwords.", ["Should anyone ever decrypt a password?"])
CH("c12", "Kerberos Clue", "Interpret output", "Hard", 4, "BloodHound summary:", "svc_backup -> MemberOf -> Backup Operators\nBackup Operators -> can back up DC files\nsvc_backup password last set: 2017", "Why is this high risk?",
   ["It is not", "Backup Operators can read sensitive DC data; an old service password makes compromise likely", "Backups are slow", "It uses DNS"], 1, "Privileged group + weak/old password = path to domain compromise.", ["What can Backup Operators read?"])
CH("c13", "Bash Bug", "Complete a coding challenge", "Medium", 3, "Script:", "for f in $(ls /data/*.log); do\n  rm $f\ndone", "What is the safest improvement?",
   ["Use rm -rf /", "for f in /data/*.log; do rm -- \"$f\"; done", "Add sudo", "Remove the loop"], 1, "Glob directly, quote variables, use -- to stop option parsing.", ["What happens with spaces in names?"])
CH("c14", "Report Rewrite", "Create a professional report", "Medium", 4, "Draft finding: 'Server is hackable, very bad, fix ASAP.'", "", "Best rewrite?",
   ["Server hackable!!", "Outdated Apache 2.4.29 on www (CVE-listed issues) - High; upgrade to supported version and verify with rescan", "Fix everything", "Remove server"], 1, "Specific, rated, actionable, verifiable.", ["What, where, how bad, how to fix?"])
CH("c15", "DNS Detective", "Analyse a packet capture", "Hard", 4, "DNS queries from one host every 60s:", "a1b2c3d4e5.data.exfil-test.example\nf6g7h8i9j0.data.exfil-test.example\nk1l2m3n4o5.data.exfil-test.example",
   "Most likely explanation?", ["Normal browsing", "Possible DNS tunnelling/beaconing", "NTP sync", "Printer discovery"], 1, "Regular, random-looking subdomains suggest beaconing or tunnelling.", ["Look at timing and names."])
CH("c16", "Least Privilege", "Fix a vulnerable configuration", "Medium", 3, "sudoers:", "deploy ALL=(ALL) NOPASSWD: ALL", "Best fix?",
   ["Leave it", "Restrict to specific commands, e.g. deploy ALL=(root) /usr/bin/systemctl restart app", "Give it to everyone", "Remove the password from root"], 1, "Limit sudo to what is needed.", ["What does deploy actually need?"])
CH("c17", "Python Validator", "Complete a coding challenge", "Medium", 3, "Which function correctly validates an IPv4/IPv6 address?", "", "Choose:",
   ["def ok(s): return '.' in s", "import ipaddress\ndef ok(s):\n  try: ipaddress.ip_address(s); return True\n  except ValueError: return False", "def ok(s): return len(s) < 16", "def ok(s): return s.isdigit()"], 1, "Use the standard library parser.", ["Do not reinvent parsing."])
CH("c18", "Container Clue", "Find a configuration error", "Hard", 3, "docker-compose service:", "privileged: true\nvolumes:\n  - /:/host\nuser: root", "What is the impact?",
   ["Minimal", "Container compromise = host compromise", "Faster builds", "Only logs affected"], 1, "Privileged + host root mount removes isolation.", ["What is mounted at /host?"])
CH("c19", "Wi-Fi Choice", "Solve a networking problem", "Easy", 2, "Which configuration is best for staff Wi-Fi in a hospital?", "", "Choose:",
   ["WPA2-PSK shared password", "WPA3-Enterprise / WPA2-Enterprise with EAP-TLS and certificate validation", "Open network with VPN", "WEP"], 1, "Per-user/device certificate auth.", ["Think individual revocation."])
CH("c20", "Ethics Check", "Identify the security weakness", "Easy", 2, "You discover your gym's member portal exposes other members' details via an ID in the URL. You have no testing agreement.", "",
   "What should you do?", ["Download data to prove it", "Stop, do not access more, report through their security contact or privacy officer", "Post on social media", "Sell it"], 1, "Responsible disclosure, no further access.", ["What does the law say about authorisation?"])

# ---------------- CAPSTONE ----------------
CAPSTONE = {
  "title": "The Silverline Enterprise Security Assessment",
  "intro": "Silverline Enterprise Ltd (fictional), a 400-person logistics company with offices in Rugby and Accra, has engaged you as an ethical security consultant. This fully simulated engagement takes you from client briefing to retest. Every decision is scored on accuracy, methodology, safety, scope compliance, technical reasoning, evidence quality, risk classification, remediation quality, report quality and professional communication.",
  "company": "Silverline Enterprise Ltd - logistics and fleet management. Crown jewels: customer shipment database, payment portal, Active Directory. Cloud: AWS for the customer portal. Staff: 400. Previous incident: phishing in 2025.",
  "scope": "IN SCOPE: 203.0.113.0/28 (external), portal.silverline.test, api.silverline.test, internal 10.10.10.0/24 (via provided VPN), AD domain SILVERLINE.TEST (read-only review + test accounts). OUT OF SCOPE: 10.10.10.200 (medical-courier temperature gateway), shop.silverline.test (third-party SaaS), social engineering, denial of service, physical testing. Window: Mon-Fri 09:00-17:00 UTC. Emergency contact: Priya Shah (SOC lead).",
  "assets": "203.0.113.2 vpn.silverline.test | 203.0.113.5 portal.silverline.test | 203.0.113.6 api.silverline.test | 10.10.10.15 web01 | 10.10.10.20 db01 | 10.10.10.30 dev01 (Jenkins) | 10.10.10.10 DC01 | 10.10.10.200 gateway (EXCLUDED)",
  "stages": [
    {"n": 1, "title": "Client Briefing", "category": "Professional communication", "brief": "Kick-off call with Silverline's CISO.", "artifact": "",
     "question": "Which question is MOST important to ask at kick-off?", "options": ["What is your favourite tool?", "Who is the emergency contact and what are the stop conditions?", "Can we skip the report?", "Can we test competitors too?"], "answer": 1, "explain": "Emergency contacts and stop conditions protect the client during testing."},
    {"n": 2, "title": "Scope Confirmation", "category": "Scope compliance", "brief": "Review the scope statement.", "artifact": "SCOPE",
     "question": "Which target is OUT of scope?", "options": ["portal.silverline.test", "10.10.10.20", "shop.silverline.test", "api.silverline.test"], "answer": 2, "explain": "The shop is third-party SaaS and explicitly excluded."},
    {"n": 3, "title": "Rules of Engagement", "category": "Safety", "brief": "Silverline's IT manager phones: 'Feel free to try a quick DoS on the VPN to check resilience.'", "artifact": "",
     "question": "Your response?", "options": ["Run it now", "Explain DoS is excluded; any change needs a written amendment from an authorised signatory", "Run it at night", "Ask a colleague to do it"], "answer": 1, "explain": "Verbal scope changes are not authorisation."},
    {"n": 4, "title": "Reconnaissance", "category": "Methodology", "brief": "Passive recon dataset: _dmarc TXT 'p=none'; dev.silverline.test CNAME to a deleted cloud app; job advert mentions 'Jenkins and AWS'.", "artifact": "",
     "question": "Which is a confirmed fact (not an assumption)?", "options": ["The company uses Jenkins in production", "DMARC is set to p=none", "dev is vulnerable to takeover", "The VPN is outdated"], "answer": 1, "explain": "The DNS record is directly observed; the others need validation."},
    {"n": 5, "title": "Asset Mapping", "category": "Accuracy", "brief": "Map assets to the scope.", "artifact": "ASSETS",
     "question": "Which asset should be tagged 'do not touch'?", "options": ["DC01", "dev01", "10.10.10.200", "web01"], "answer": 2, "explain": "The gateway is excluded and fragile."},
    {"n": 6, "title": "Network Discovery Simulation", "category": "Scope compliance", "brief": "You prepare the internal discovery command.", "artifact": "",
     "question": "Which command respects scope?", "options": ["nmap -sn 10.10.0.0/16", "nmap -sn 10.10.10.0/24 --exclude 10.10.10.200", "nmap -sn 10.10.10.0/24", "nmap -A 0.0.0.0/0"], "answer": 1, "explain": "Correct range with the exclusion."},
    {"n": 7, "title": "Service Assessment", "category": "Technical reasoning", "brief": "Results: dev01 8080 Jenkins (no login required); db01 3306 MySQL on LAN; web01 21 anonymous FTP; DC01 445 SMB signing not required.", "artifact": "",
     "question": "Which service is the most critical exposure?", "options": ["Anonymous FTP", "Unauthenticated Jenkins", "MySQL on LAN", "SMB signing"], "answer": 1, "explain": "Unauthenticated CI often permits code execution and holds secrets."},
    {"n": 8, "title": "Vulnerability Analysis", "category": "Risk classification", "brief": "The VPN appliance version matches a CVE listed in CISA KEV (CVSS 8.1). dev01 Jenkins is internal only.", "artifact": "",
     "question": "How do you rate the VPN issue?", "options": ["Low", "Medium", "Critical priority: internet-facing + KEV", "Informational"], "answer": 2, "explain": "Exposure and active exploitation raise priority above the base score."},
    {"n": 9, "title": "Web Security Assessment", "category": "Accuracy", "brief": "As test user 'learner', GET /api/shipments/5512 returns another customer's shipment.", "artifact": "",
     "question": "What is this?", "options": ["XSS", "BOLA/IDOR", "CSRF", "SSRF"], "answer": 1, "explain": "Object-level authorisation failure."},
    {"n": 10, "title": "Controlled Validation", "category": "Safety", "brief": "You need to prove SQL injection in the portal search.", "artifact": "",
     "question": "Best evidence?", "options": ["Download the customer table", "Show the DB version string and a row count", "Drop a table", "Change prices"], "answer": 1, "explain": "Minimal proof, no data exposure."},
    {"n": 11, "title": "Risk Classification", "category": "Risk classification", "brief": "Order these: VPN KEV issue, BOLA on shipments, unauthenticated Jenkins (internal), DMARC p=none.", "artifact": "",
     "question": "Which order is best?", "options": ["DMARC, Jenkins, BOLA, VPN", "VPN, BOLA, Jenkins, DMARC", "Jenkins, DMARC, VPN, BOLA", "All equal"], "answer": 1, "explain": "Internet-exposed and exploited first, then customer data exposure, then internal RCE, then email spoofing."},
    {"n": 12, "title": "Evidence Collection", "category": "Evidence quality", "brief": "Preparing evidence for the BOLA finding.", "artifact": "",
     "question": "Best evidence item?", "options": ["Full customer address list", "Redacted screenshot of request/response with timestamp and test accounts named", "Your browser history", "A video of your desk"], "answer": 1, "explain": "Minimal, redacted, timestamped."},
    {"n": 13, "title": "Remediation", "category": "Remediation quality", "brief": "Write the fix for Jenkins.", "artifact": "",
     "question": "Best remediation?", "options": ["Use a stronger firewall somewhere", "Enable authentication with SSO/MFA, restrict network access to the build subnet, rotate stored credentials, update Jenkins", "Delete Jenkins", "Change port to 9090"], "answer": 1, "explain": "Specific, layered, includes credential rotation."},
    {"n": 14, "title": "Report Writing", "category": "Report quality", "brief": "Drafting the executive summary.", "artifact": "",
     "question": "Best opening line?", "options": ["We ran Nmap -A and found CVEs", "Overall risk is HIGH: an internet attacker could exploit the VPN to enter the network, and any customer can view other customers' shipments", "Everything is broken", "See appendix"], "answer": 1, "explain": "Business-level risk in plain language."},
    {"n": 15, "title": "Executive Presentation", "category": "Professional communication", "brief": "The CFO asks: 'What should we fund first?'", "artifact": "",
     "question": "Best answer?", "options": ["Buy a new SIEM", "Patch the VPN within 48 hours and fix shipment authorisation - these protect customers and the network fastest", "Hire more testers", "Nothing"], "answer": 1, "explain": "Prioritised, business-justified actions."},
    {"n": 16, "title": "Retesting", "category": "Accuracy", "brief": "Retest: /api/shipments/5512 now returns 404, but /api/shipments/5512/label still returns the label PDF.", "artifact": "",
     "question": "Status?", "options": ["Fixed", "Partially fixed", "Risk accepted", "Not applicable"], "answer": 1, "explain": "A variant remains vulnerable."},
    {"n": 17, "title": "Final Review", "category": "Methodology", "brief": "Closing the engagement.", "artifact": "",
     "question": "Which is required before closing?", "options": ["Keep all client data forever", "Remove test artefacts, confirm cleanup, deliver report securely and schedule deletion of evidence per contract", "Post results online", "Skip the debrief"], "answer": 1, "explain": "Professional closure and data handling."},
  ]
}
