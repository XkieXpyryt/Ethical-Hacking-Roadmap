from lib import *

M("m11", 11, "Wireless & Mobile Security", "wifi",
  "Wi-Fi, Bluetooth and mobile apps extend the attack surface beyond the office walls. Learn how they work, how they fail and how to review them safely using simulated configurations and sample apps.",
  ["Explain Wi-Fi standards, bands and authentication", "Assess wireless configurations and threats", "Describe Android and iOS security models", "Review mobile apps with OWASP MASVS/MASTG"],
  ["Module 2", "Module 5"],
  [
   L("m11l1", "Wireless Fundamentals: Standards, Bands, Access Points and WPA2/WPA3", "Intermediate", 35,
     "Wi-Fi is often the easiest way into a building's network - and a common audit area.",
     ["Describe 802.11 standards and frequency bands", "Explain WPA2/WPA3 Personal and Enterprise", "Identify secure wireless settings"],
     """Wi-Fi follows IEEE 802.11 standards. Wi-Fi 4 (802.11n), 5 (802.11ac), 6/6E (802.11ax) and 7 (802.11be) offer increasing speeds. They use the 2.4 GHz band (longer range, more interference), 5 GHz (faster, shorter range) and 6 GHz (Wi-Fi 6E/7). An access point broadcasts a network name (SSID). Security protocols protect the connection: WEP and WPA are obsolete and broken; WPA2 is widely used; WPA3 is the current standard with stronger protection against password guessing.""",
     """Authentication modes:
• Personal (PSK/SAE): one shared passphrase. WPA2-PSK handshakes can be captured and guessed offline if the passphrase is weak. WPA3-SAE resists offline guessing and gives forward secrecy.
• Enterprise (802.1X/EAP with RADIUS): each user/device authenticates individually (EAP-TLS with certificates is strongest). Misconfigured clients that do not validate the server certificate can be tricked by fake access points.

Encryption: WPA2 uses AES-CCMP; avoid TKIP. WPA3 adds SAE, mandatory PMF (protected management frames - mitigates deauthentication attacks), and 192-bit mode for Enterprise.

Other settings: WPS (PIN-based setup) should be disabled - its PIN design is weak. Hiding the SSID is not security. MAC filtering is easily bypassed. Separate guest networks with client isolation. Strong, long passphrases (20+ characters) for PSK networks.""",
     "WPA2-Personal is one shared house key everyone copies; WPA2/3-Enterprise gives every resident their own key card that can be cancelled individually.",
     "An office used an 8-character WPA2 passphrase printed on the reception wall; a passphrase change and a move to Enterprise authentication for staff fixed both exposure and guessing risk.",
     "Wireless reviews check protocol, authentication mode, passphrase strength, WPS, guest isolation and rogue devices.",
     ["Client", "Association", "Authentication (SAE or 802.1X/EAP)", "Key exchange", "Encrypted AES traffic"],
     ["Open the Wireless Config Review lab", "Rank WEP, WPA, WPA2-PSK, WPA3-SAE, WPA2-Enterprise EAP-TLS by strength", "List five secure AP settings"],
     "Write recommendations for a café that uses one WPA2 password for both staff tills and customers.",
     ["Thinking a hidden SSID is secure", "Leaving WPS enabled", "Using TKIP for compatibility"],
     "Prefer WPA3 (or WPA2-AES with long passphrases), Enterprise with certificate validation for staff, no WPS and separate isolated guest networks.",
     ["SSID", "WPA2", "WPA3", "SAE", "802.1X", "WPS"],
     "WPA3 uses SAE (Simultaneous Authentication of Equals) instead of the PSK four-way handshake vulnerable to offline guessing.",
     [Q("Which protocol is current best practice for Wi-Fi security?", ["WEP", "WPA", "WPA3", "Open"], 2, "WPA3."),
      Q("Why disable WPS?", ["It slows Wi-Fi", "Its PIN method is weak and can be guessed", "It disables WPA3", "It hides the SSID"], 1, "WPS PIN design weaknesses."),
      TF("Hiding the SSID prevents attackers from finding the network.", False, "Hidden SSIDs are revealed by client probes; it is not a control.")]),

   L("m11l2", "Wireless Threats, Rogue APs, Bluetooth, IoT and Wireless Defence", "Intermediate", 35,
     "Wireless attacks need only proximity, not network access.",
     ["Describe rogue and evil-twin access points", "Explain Bluetooth and IoT wireless risks", "Apply wireless auditing principles and defences"],
     """Wireless threats include rogue access points (unauthorised APs plugged into the network), evil twins (fake APs copying a legitimate SSID to lure users), deauthentication floods that knock clients offline, weak passphrase guessing and misconfigured Enterprise clients. Bluetooth and IoT devices bring their own short-range wireless risks, often with weak authentication and rare updates. Defences include WPA3, PMF, certificate validation, wireless intrusion detection and regular surveys.""",
     """Wireless auditing principles: written authorisation including physical locations and frequencies; never interfere with neighbouring networks; avoid deauthentication unless explicitly agreed (it disrupts service); record channel, BSSID, SSID, encryption and signal for each network; compare against the client's approved AP list to find rogues.

Aircrack-ng suite (concept): airmon-ng (monitor mode), airodump-ng (survey networks and capture handshakes for your own lab AP), aircrack-ng (test passphrase strength against a wordlist). In this app you analyse simulated survey output only.

Bluetooth/BLE: risks include unauthenticated pairing ('Just Works'), outdated stacks, tracking via static addresses. IoT: default credentials, unencrypted protocols, no updates, cloud dependency. Put IoT on isolated VLANs.

Defences: WPA3/PMF, 802.1X with EAP-TLS, certificate validation on clients, WIDS/WIPS, AP inventory, guest isolation, disable unused radios, staff awareness (do not connect to unknown networks), VPN on untrusted Wi-Fi.""",
     "An evil twin is a fake shop with the same sign as the real one next door - customers walk in and hand over their details without realising.",
     "A wireless survey found an unapproved consumer router under a desk bridging the secure LAN to an open SSID - a rogue AP that bypassed every perimeter control.",
     "Rogue APs and weak client configuration are frequent wireless findings.",
     ["Authorised survey", "List SSID/BSSID/encryption", "Compare with approved AP list", "Flag rogues/weak config", "Recommend + verify"],
     ["Use the Wireless lab survey to identify the rogue AP", "Recommend defences for a warehouse full of IoT scanners", "Explain why deauthentication testing needs explicit approval"],
     "Write a short wireless security checklist for a small clinic.",
     ["Capturing traffic of neighbouring networks", "Deauthenticating clients without approval", "Ignoring Bluetooth and IoT"],
     "Survey with permission, compare against the approved list, and defend with WPA3/PMF, EAP-TLS, WIDS and isolation.",
     ["Rogue access point", "Evil twin", "Deauthentication", "Aircrack-ng", "WIDS", "IoT"],
     "PMF (802.11w) protects against deauthentication attacks; it is mandatory in WPA3.",
     [Q("A fake AP using a company's SSID to lure users is a...", ["Rogue AP", "Evil twin", "Repeater", "Mesh node"], 1, "Evil twin."),
      Q("Which feature mitigates deauthentication attacks?", ["WPS", "PMF (802.11w)", "Hidden SSID", "MAC filtering"], 1, "Protected Management Frames."),
      Q("Where should IoT devices be placed?", ["On the main staff VLAN", "On an isolated network segment", "On the guest network with admin access", "Directly on the internet"], 1, "Isolate IoT.")]),

   L("m11l3", "Android and iOS Architecture, Permissions and Secure Storage", "Intermediate", 40,
     "Phones hold email, banking, MFA and corporate data - and their security model differs from desktops.",
     ["Describe Android and iOS security architecture", "Explain app sandboxing and permissions", "Identify secure and insecure storage"],
     """Android is based on the Linux kernel; each app runs as its own user in a sandbox and must declare permissions. iOS also sandboxes apps, requires code signing and reviews apps in the App Store. Both use hardware-backed key storage (Android Keystore, iOS Secure Enclave/Keychain) and full-device encryption. Apps should store secrets in these secure stores, not in plain files or shared preferences.""",
     """Android: Linux kernel + SELinux, app sandbox (unique UID), permissions (normal, dangerous/runtime, special), Play Protect, verified boot, file-based encryption, Keystore (often TEE/StrongBox-backed). Components: activities, services, broadcast receivers, content providers - exported components can be reached by other apps and must be protected.

iOS: Secure Boot chain, code signing, sandbox, entitlements, Data Protection classes, Keychain, Secure Enclave, App Transport Security (enforces HTTPS by default).

Permissions best practice: request the minimum, at the moment needed, explain why; Android 13+ needs runtime notification permission; location 'precise' vs 'approximate'.

Storage review: insecure = plaintext SharedPreferences/NSUserDefaults with tokens, world-readable files, logs containing personal data, unencrypted SQLite with sensitive data, backups including secrets. Secure = Keystore/Keychain for keys, encrypted storage for sensitive data, exclude secrets from backups.

Rooting/jailbreaking removes protections; high-risk apps may detect it (defence in depth, not a guarantee).""",
     "Each app is a hotel guest in its own locked room (sandbox); permissions are the extra keys reception hands out only when asked and approved; the Keystore is the hotel safe.",
     "A banking app stored a long-lived session token in plaintext app storage; on a rooted test device it could be copied and reused - fixed by moving it to the Keystore and shortening its lifetime.",
     "Mobile assessments check permissions, storage, components and network security against OWASP MASVS.",
     ["App installed (signed)", "Sandbox (own UID)", "Permission request", "User grants", "Secrets in Keystore/Keychain"],
     ["Complete the Mobile Permissions Review lab", "Classify five storage locations as secure or insecure", "Check the permissions of an app on your own phone"],
     "A torch app requests contacts and SMS permissions. Write the review finding.",
     ["Storing tokens in plaintext preferences", "Exporting components without permission checks", "Requesting all permissions at install time"],
     "Both platforms sandbox apps; request minimal permissions; keep secrets in hardware-backed stores; protect exported components.",
     ["Sandbox", "Runtime permission", "Android Keystore", "Keychain", "Exported component"],
     "On Android, each app gets its own Linux user ID - the basis of its sandbox.",
     [Q("Where should an Android app store cryptographic keys?", ["SharedPreferences", "Android Keystore", "External storage", "Logcat"], 1, "The Keystore protects keys."),
      Q("What isolates apps from each other on Android?", ["Separate Linux user IDs (sandbox)", "Bluetooth", "Wi-Fi", "App size limits"], 0, "Per-app UIDs plus SELinux."),
      Q("Which iOS feature enforces HTTPS connections by default?", ["Keychain", "App Transport Security", "Siri", "AirDrop"], 1, "ATS.")]),

   L("m11l4", "Mobile Application Testing, APIs, Privacy, MobSF and Hardening", "Advanced", 40,
     "Mobile apps are front ends to APIs; most serious mobile findings are really back-end findings.",
     ["Apply OWASP MASVS and MASTG", "Use MobSF for static analysis of sample apps", "Recommend mobile app hardening and privacy controls"],
     """Mobile application testing combines static analysis (inspecting the app package without running it), dynamic analysis (watching it run on a test device or emulator) and API testing (the back end it talks to). The OWASP Mobile Application Security Verification Standard (MASVS) defines what to check and the Testing Guide (MASTG) explains how. MobSF (Mobile Security Framework) is a free, open-source tool that automates static and dynamic analysis of Android and iOS apps.""",
     """MASVS control groups: STORAGE, CRYPTO, AUTH, NETWORK, PLATFORM, CODE, RESILIENCE, PRIVACY.

Static checks (sample/authorised APK only): manifest (debuggable=true? allowBackup? exported components?), hardcoded secrets and API keys, insecure crypto, cleartext traffic allowed (network security config), third-party SDKs and trackers. Tools: MobSF, jadx (decompiler), apktool.

Dynamic checks (test device/emulator): traffic through a proxy (Burp/ZAP) with a test CA; certificate pinning behaviour; data written to storage and logs; authentication flows; deep links. Android debugging tools: adb (logcat, install, shell), emulator.

API communication: everything from Module 9 applies - BOLA, auth, rate limits. Never trust the app to enforce security.

Privacy: data minimisation, consent, clear privacy policy, no excessive tracking SDKs, GDPR rights.

Hardening: remove debug flags, obfuscate (R8/ProGuard) as defence in depth, certificate pinning where appropriate, root/jailbreak detection for high-risk apps, secure storage, no secrets in the app, Play Integrity / App Attest for API abuse protection.""",
     "Testing a mobile app is checking both the car (the app) and the roads it drives on (the APIs) - most crashes happen on the road.",
     "MobSF static analysis of a sample app revealed a hardcoded cloud API key that granted write access to a storage bucket - the key was rotated and moved server-side.",
     "Mobile testing roles expect MASVS knowledge and familiarity with MobSF, jadx and proxies.",
     ["Obtain authorised build", "Static analysis (MobSF)", "Dynamic analysis (proxy)", "API testing", "Map to MASVS", "Report"],
     ["Complete Project 10 (mobile security review) using the sample app report", "Map five findings to MASVS groups", "Create a mobile security checklist"],
     "Write remediation for 'android:debuggable=true in release build' and 'cleartext traffic permitted'.",
     ["Testing apps you do not have permission to analyse", "Only testing the app and not its API", "Treating obfuscation as a security fix"],
     "Use MASVS/MASTG, static + dynamic + API testing, and recommend secure storage, no embedded secrets and server-side enforcement.",
     ["MASVS", "MASTG", "MobSF", "adb", "Certificate pinning", "Obfuscation"],
     "Hardcoded secrets in an app are always extractable - the fix is to move them server-side.",
     [Q("Which free tool automates static analysis of Android apps?", ["Burp Suite Pro", "MobSF", "Nessus", "Hydra"], 1, "MobSF."),
      Q("Which OWASP standard defines mobile app security requirements?", ["ASVS", "MASVS", "WSTG", "SAMM"], 1, "Mobile Application Security Verification Standard."),
      Q("An API key embedded in an APK is...", ["Safe if obfuscated", "Extractable by anyone with the app", "Encrypted by Android automatically", "Invisible"], 1, "Treat it as public.")]),
  ],
  ["Analyse a simulated Wi-Fi security configuration", "Identify weak wireless configurations", "Review mobile application permissions", "Analyse a sample Android app",
   "Conduct a safe mobile application security review", "Create a mobile security checklist"],
  "p09",
  "Complete all 4 lessons, pass the module assessment and complete the Wireless and Mobile labs.",
  ["Rank Wi-Fi protocols", "List MASVS groups", "Explain sandboxing on both platforms"],
  ["Wireless fundamentals", "Wi-Fi standards", "Frequency bands", "Access points", "Authentication", "WPA2", "WPA3", "Encryption", "Wireless threats", "Rogue AP", "Misconfiguration",
   "Bluetooth", "IoT", "Wireless defence", "Auditing principles", "Android architecture", "iOS architecture", "Permissions", "Secure storage", "Mobile networking", "API communication",
   "Privacy", "Threat models", "App testing", "Secure development", "Hardening", "Aircrack-ng", "MobSF", "adb"])


M("m12", 12, "Exploitation & Post-Exploitation Concepts", "bolt",
  "Exploitation is taught here from a security-understanding and controlled-validation perspective: what makes something exploitable, how professionals prove impact safely, what post-exploitation risk looks like, and how defenders detect, contain and recover. No destructive payloads, real credential theft, stealth or evasion are taught.",
  ["Distinguish vulnerability, exploit and proof of concept", "Assess exploitability and impact", "Plan controlled validation with safety checks",
   "Explain post-exploitation risks and their indicators", "Support detection, containment, cleanup, recovery and responsible disclosure"],
  ["Modules 8, 9 and 10"],
  [
   L("m12l1", "Exploit Fundamentals: Vulnerability vs Exploit, Proof of Concept and Lifecycle", "Advanced", 35,
     "Understanding exploitability is how you rate risk accurately and explain it convincingly.",
     ["Define exploit, payload and proof of concept", "Describe the exploit lifecycle", "Assess exploitability factors"],
     """A vulnerability is a weakness; an exploit is a technique or code that uses that weakness to achieve something the system should not allow. A proof of concept (PoC) demonstrates that exploitation is possible, ideally with minimal impact. Exploitability depends on factors such as network reachability, required privileges, user interaction, reliability and available public code. Impact is what an attacker gains: data access, code execution, privilege or disruption.""",
     """Exploit classes (conceptual): memory corruption (buffer overflows, use-after-free), injection (SQL, command), logic flaws, authentication bypass, deserialization, misconfiguration abuse.

Components: the exploit (triggers the flaw) and the payload (what runs afterwards). In professional validation, payloads are minimal and benign (e.g. print a version or create a harmless marker file in a lab).

Exploit lifecycle: discovery -> PoC -> weaponisation by criminals (sometimes) -> public release -> inclusion in frameworks -> mass exploitation -> patch adoption. Exploit maturity is reflected in CVSS threat metrics and KEV.

Exploitability assessment questions: Is it reachable from where an attacker would be? Does it need authentication? Is user interaction required? Is the public exploit reliable or likely to crash the service? Are mitigations (ASLR, DEP, WAF, EDR) present?

Mitigations against memory corruption: ASLR, DEP/NX, stack canaries, CFG/CET, memory-safe languages (Rust, Go, managed languages).""",
     "The vulnerability is a crack in a dam; the exploit is the method that widens it; the proof of concept is showing the engineer a trickle - not flooding the valley.",
     "A public PoC for a critical vulnerability was unreliable and crashed servers; testers who validated with vendor check scripts instead avoided causing an outage.",
     "Exploitability judgements change severity, priority and how you advise clients.",
     ["Vulnerability", "Reachability", "Preconditions", "Mitigations present?", "Exploitability rating", "Impact if exploited"],
     ["Answer the five exploitability questions for two sample findings", "Explain three memory corruption mitigations", "Read the Exploit Validation safety lab briefing"],
     "Explain to a manager the difference between 'a PoC exists' and 'attackers are exploiting this in the wild'.",
     ["Equating 'exploit exists' with 'we must exploit it to prove it'", "Ignoring service stability", "Using unreviewed public code"],
     "Exploitability = reachability + preconditions + reliability - mitigations. Prove existence minimally; describe impact clearly.",
     ["Exploit", "Payload", "Proof of concept", "Exploitability", "ASLR", "DEP"],
     "A payload is what executes after successful exploitation - in professional validation it should be benign.",
     [Q("What is a proof of concept?", ["A patch", "A demonstration that a vulnerability can be exploited", "A firewall rule", "A scan report"], 1, "It shows feasibility."),
      Q("Which mitigation randomises memory locations?", ["DEP", "ASLR", "MFA", "HSTS"], 1, "ASLR."),
      Q("Which factor LOWERS exploitability?", ["No authentication required", "Requires local physical access", "Network reachable", "Reliable public exploit"], 1, "Physical access requirements reduce likelihood.")]),

   L("m12l2", "Controlled Validation and Safety Checks", "Advanced", 35,
     "The professional standard is: prove it safely, or do not do it.",
     ["Plan controlled validation", "Apply pre-validation safety checks", "Choose the least impactful evidence"],
     """Controlled validation confirms a vulnerability is real and shows its impact while protecting the client's systems and data. Before validating, check scope and authorisation for that specific technique, the system's fragility, backups or snapshots, timing, and that the client contact is reachable. Prefer non-destructive checks (vendor check scripts, version endpoints, read-only proof) over full exploitation. Stop immediately if anything behaves unexpectedly.""",
     """Pre-validation checklist:
1. Is this host and technique explicitly in scope?
2. Is exploitation (not just identification) permitted for this engagement?
3. Is the system production, fragile or safety-critical? (medical, OT, payment)
4. Is there a snapshot/backup and a rollback plan? (always in labs)
5. Is the client contact informed and reachable now?
6. What is the minimal evidence that proves impact?
7. What are the stop conditions?

Evidence ladder (least to most impact): version/configuration evidence -> vendor-provided safe check -> harmless behaviour proof (e.g. an error difference, reading a non-sensitive file such as a hostname in a lab) -> controlled code execution of a benign command (whoami) in authorised scope -> anything beyond requires explicit client agreement.

Data handling: never exfiltrate real personal or sensitive data as proof; screenshot minimal redacted evidence; log every action with timestamps.

Lab practice: restore snapshots after validation, verify integrity, document.""",
     "Controlled validation is a surgeon's biopsy: take the smallest sample needed to confirm the diagnosis, not remove the organ.",
     "A tester proved database access by showing the database version and the count of rows in a table, rather than downloading customer data. The client accepted the finding as critical.",
     "Safety and scope compliance are scored in the capstone; in real life they protect clients and your career.",
     ["Scope + permission check", "Fragility check", "Snapshot/rollback", "Minimal evidence", "Validate", "Stop conditions monitored", "Restore + document"],
     ["Complete the Exploit Validation Safety decision lab", "Order five evidence types from least to most impactful", "Write your personal stop conditions"],
     "A critical RCE is found on a hospital's patient-monitoring gateway. Write what you do next, step by step.",
     ["Exploiting fragile production systems to 'be sure'", "Downloading real data as proof", "Continuing after unexpected behaviour"],
     "Check scope, fragility and rollback; use the least impactful evidence; stop on surprises; restore and document.",
     ["Controlled validation", "Stop condition", "Evidence", "Snapshot", "Safety check"],
     "When unsure whether a technique is in scope, the professional answer is to stop and ask the client.",
     [Q("Which is the least impactful way to validate a vulnerability?", ["Full exploitation with data download", "Vendor-provided safe check or configuration evidence", "Denial of service", "Installing persistence"], 1, "Use the lowest-impact evidence that proves the issue."),
      Q("During validation the target service slows down dramatically. You should...", ["Continue quickly", "Stop and contact the client per stop conditions", "Restart it yourself without telling anyone", "Ignore it"], 1, "Stop conditions exist for this."),
      TF("Downloading real customer records is acceptable proof of SQL injection.", False, "Minimal, redacted evidence only.")]),

   L("m12l3", "Post-Exploitation Risk: Privilege, Lateral Movement, Persistence and Evidence", "Advanced", 40,
     "Clients need to understand what an attacker could do next - and defenders need to know what to look for.",
     ["Explain post-exploitation risk categories", "Identify post-exploitation indicators", "Collect evidence professionally"],
     """Post-exploitation is what happens after an attacker gains a foothold: escalating privileges, moving to other systems, establishing persistence and accessing data. In professional testing, this phase is about analysing and demonstrating risk within agreed limits - for example showing that a compromised web server could reach the database network - not about hiding or staying in the environment. Defenders study the same stages to detect intrusions early.""",
     """Risk categories (MITRE ATT&CK tactics): Privilege Escalation (TA0004), Credential Access (TA0006), Discovery (TA0007), Lateral Movement (TA0008), Collection (TA0009), Persistence (TA0003), Exfiltration (TA0010), Impact (TA0040).

Persistence risks (awareness): new services, scheduled tasks/cron, Run keys, new accounts, web shells, SSH authorized_keys entries, startup items. Professional testers do NOT install persistence unless explicitly agreed, and remove all artefacts.

Indicators of post-exploitation activity: new local admins (4732), new services (7045), unusual scheduled tasks (4698), logons at odd hours from unusual hosts, Office apps spawning shells, encoded PowerShell, new files in web roots, outbound connections to unknown hosts, discovery commands in bulk (whoami, net group, ipconfig).

Evidence collection: timestamps (UTC), host, user context, command/action, output (redacted), screenshot, hash of any file obtained, chain-of-custody notes for anything sensitive. Keep a cleanup list of every artefact you create.""",
     "Post-exploitation analysis is a fire investigator explaining how far the fire could have spread through open doors - not starting more fires to prove it.",
     "A test demonstrated that the compromised web server's service account could read the HR file share - one screenshot of the directory listing (no file contents) was enough to show impact.",
     "Your report's attack narrative and the client's detection improvements both come from this phase.",
     ["Foothold", "Discovery (what can I reach?)", "Privilege context", "Paths to sensitive assets", "Evidence (minimal)", "Cleanup list"],
     ["Map ten indicators to ATT&CK tactics", "Complete the Incident Log Analysis lab and find post-exploitation indicators", "Write an evidence log entry"],
     "From a simulated host log, build a timeline of the intruder's post-exploitation steps.",
     ["Creating persistence without permission", "Forgetting to track artefacts for cleanup", "Collecting more data than needed"],
     "Analyse and demonstrate post-exploitation risk minimally, know the indicators, and keep evidence and cleanup logs.",
     ["Post-exploitation", "Persistence", "Lateral movement", "Indicator of compromise", "Chain of custody"],
     "Know at least five ATT&CK tactic names and their purpose.",
     [Q("Which Windows event ID indicates a member was added to a security-enabled local group?", ["4625", "4732", "4634", "1102"], 1, "4732."),
      Q("A new file 'img.php' appears in a web root after an alert. It may be...", ["A web shell (persistence)", "A CSS file", "A DNS record", "A certificate"], 0, "Unexpected scripts in web roots suggest web shells."),
      Q("Professional testers should create persistence...", ["Always", "Only if explicitly agreed, and then remove it", "Secretly", "On every host"], 1, "Only with agreement, with cleanup.")]),

   L("m12l4", "Detection, Containment, Cleanup, Recovery and Responsible Disclosure", "Advanced", 40,
     "The end goal of offensive knowledge is better defence and safe, honest communication.",
     ["Observe how detection tools respond", "Plan containment, cleanup and recovery", "Follow responsible disclosure"],
     """Detection tools such as EDR, SIEM and IDS should alert on exploitation and post-exploitation behaviour; good tests record what was detected. Containment isolates affected systems to stop spread. Cleanup removes every artefact a tester (or attacker) created. Recovery restores systems to a known good state, verified by integrity checks. Responsible (coordinated) disclosure is how researchers report vulnerabilities to vendors privately and give them time to fix before publishing.""",
     """Detection feedback: for each test action record time, technique, expected detection, actual alert (yes/no), time to alert. This turns a pen test into purple-team value.

Containment (incident view): network isolation via EDR, disable compromised accounts, block IOCs, preserve evidence (memory/disk images) before wiping.

Tester cleanup: remove uploaded files, test accounts, scheduled tasks, tools; restore changed configuration; confirm with the client; list any artefacts that could not be removed in the report.

Recovery: rebuild from trusted images or restore lab snapshots; reset credentials; patch; verify integrity with hashes/baselines; monitor closely afterwards.

Responsible disclosure: report privately to the vendor/security.txt contact with clear details; agree a timeline (commonly 90 days); do not access more data than needed; do not threaten or demand payment; follow bug bounty rules (scope, safe harbour); publish after a fix or agreed deadline. CVE IDs are assigned through CNAs. Many countries are updating laws to protect good-faith research - check yours.""",
     "Cleanup is leaving the client's house exactly as you found it - and listing every drawer you opened.",
     "A researcher who found a flaw in a medical device reported it via the manufacturer's disclosure policy; a patch was released and the researcher was credited in the advisory.",
     "Detection feedback, cleanup records and disclosure ethics demonstrate professionalism and are part of capstone scoring.",
     ["Action performed", "Detected?", "Contain", "Cleanup artefacts", "Recover + verify integrity", "Disclose responsibly"],
     ["Restore a lab snapshot and verify integrity with hashes", "Fill a detection feedback table for five lab actions", "Find a real organisation's security.txt and read its disclosure policy"],
     "Write a responsible disclosure email to a fictional vendor about an IDOR in their API.",
     ["Leaving test accounts or files behind", "Publishing a zero-day without notifying the vendor", "Wiping systems before preserving evidence in an incident"],
     "Measure detection, contain, clean up completely, recover to a verified state and disclose responsibly.",
     ["EDR", "Containment", "Cleanup", "Recovery", "Responsible disclosure", "security.txt"],
     "A common disclosure window is 90 days - a widely used industry norm, not a law.",
     [Q("What should happen before wiping a compromised system during incident response?", ["Nothing", "Preserve evidence such as memory and disk images", "Post on social media", "Reinstall antivirus"], 1, "Evidence first."),
      Q("Responsible disclosure means...", ["Publishing immediately", "Privately reporting to the vendor and allowing time to fix", "Selling the bug", "Ignoring it"], 1, "Coordinated, private reporting."),
      Q("Which file can tell researchers how to report vulnerabilities to a website owner?", ["robots.txt", "security.txt", "sitemap.xml", "humans.txt"], 1, "/.well-known/security.txt (RFC 9116).")]),
  ],
  ["Analyse safe exploit demonstrations", "Review vulnerability PoC code", "Validate a simulated vulnerability in an isolated lab", "Observe how detection tools respond",
   "Analyse security logs", "Identify post-exploitation indicators", "Restore a lab snapshot", "Verify system integrity", "Document findings"],
  "p14",
  "Complete all 4 lessons, pass the module assessment and complete the Exploit Validation Safety and Incident Log labs.",
  ["Recite the pre-validation checklist", "Name 6 post-exploitation indicators", "Explain responsible disclosure steps"],
  ["Exploit fundamentals", "Vulnerability vs exploit", "Proof of concept", "Exploit lifecycle", "Exploitability", "Impact", "Controlled validation", "Safety checks",
   "Privilege escalation", "Authentication weaknesses", "Post-exploitation risk", "Lateral movement", "Persistence risks", "Evidence collection", "Detection", "Containment",
   "Cleanup", "Recovery", "Responsible disclosure"])


M("m13", 13, "Advanced Security Testing", "layers",
  "Modern environments mean identity platforms, cloud, containers, APIs, source code and compiled binaries. This module builds advanced, defence-aware assessment skills in each area.",
  ["Audit Active Directory with defensive tooling", "Assess AWS, Azure and Google Cloud configurations", "Review Docker and Kubernetes security",
   "Assess API authentication, tokens and rate limits", "Perform secure code review and supply chain checks", "Explain binary analysis, reverse engineering and malware analysis foundations"],
  ["Modules 9, 10 and 12"],
  [
   L("m13l1", "Active Directory Security: Architecture, Misconfigurations, Auditing and Hardening", "Advanced", 45,
     "AD compromise usually means total organisational compromise.",
     ["Describe AD architecture in depth", "Identify common AD misconfigurations", "Audit and harden AD with free tools"],
     """Active Directory consists of domains, forests and trusts, with domain controllers hosting the directory, Kerberos and DNS. Security depends on who has privileged rights (directly or via nested groups and object permissions), how authentication protocols are configured and how Group Policy is managed. Free tools like PingCastle and BloodHound Community Edition reveal risky configurations and attack paths so they can be fixed.""",
     """Architecture: forest (security boundary), domains, OUs, sites, trusts (direction, transitivity, SID filtering), FSMO roles, LDAP (389/636), Kerberos (88), global catalog (3268).

Common misconfigurations: excessive Domain/Enterprise Admins; nested groups granting hidden privilege; dangerous ACEs (GenericAll, WriteDACL, WriteOwner on users/groups/GPOs); unconstrained delegation; old OS domain controllers; LDAP signing/channel binding not enforced; NTLMv1 allowed; passwords in GPP/SYSVOL scripts; stale accounts; krbtgt password never rotated; AdminSDHolder misuse; weak service account passwords.

Defensive auditing: PingCastle health check (score + recommendations), BloodHound CE (shortest paths to Domain Admins), Purple Knight (free), PowerShell AD module queries (Get-ADUser -Filter {PasswordNeverExpires -eq $true}).

Hardening: tiered admin model (Tier 0 = DCs and identity), Privileged Access Workstations, Protected Users group, LAPS, disable NTLM where possible, enforce LDAP signing, rotate krbtgt twice per schedule, gMSAs, audit policy + monitoring (4728/4732/4756 group changes, 5136 directory changes), Microsoft Defender for Identity or free alternatives.""",
     "AD privileges are like building access badges that can be copied through relationships: if the cleaner's badge can open the manager's office where spare master keys are kept, the cleaner effectively has a master key.",
     "BloodHound showed that a help-desk group had WriteDACL over a group nested in Domain Admins - a three-step path to full domain control that nobody knew about.",
     "Internal tests and red teams focus on AD; defenders use the same graphs to cut attack paths.",
     ["Collect AD data (authorised)", "Graph privileges (BloodHound)", "Health check (PingCastle)", "Prioritise Tier 0 paths", "Harden", "Re-audit"],
     ["Complete the AD Review lab and Project 11", "Explain a three-hop attack path in plain language", "List ten AD hardening controls by impact"],
     "Write an executive summary for an AD health check that scored poorly.",
     ["Running collection tools without explicit scope", "Ignoring nested group membership", "Never rotating krbtgt"],
     "Find privilege through groups and ACLs, check protocol settings, and harden with tiering, LAPS, signing and monitoring.",
     ["Forest", "Trust", "ACE", "Delegation", "PingCastle", "Tiered administration"],
     "The forest, not the domain, is the security boundary in AD.",
     [Q("What is the security boundary in Active Directory?", ["OU", "Domain", "Forest", "Site"], 2, "The forest."),
      Q("Which free tool graphs attack paths to Domain Admins?", ["Wireshark", "BloodHound Community Edition", "Nikto", "Aircrack-ng"], 1, "BloodHound CE."),
      Q("Tier 0 in the tiered admin model includes...", ["User laptops", "Domain controllers and identity systems", "Printers", "Guest Wi-Fi"], 1, "Tier 0 = identity control plane.")]),

   L("m13l2", "Cloud Security: AWS, Azure, Google Cloud, IAM, Storage, Logging and Threat Modelling", "Advanced", 50,
     "Most new infrastructure is in the cloud, and most cloud breaches are misconfigurations.",
     ["Apply the shared responsibility model", "Assess IAM, storage and network configuration", "Use cloud logging and threat modelling"],
     """In the cloud, the provider secures the physical infrastructure and core services; the customer secures identities, configurations, data and workloads. Identity and Access Management (IAM) is the cloud's main security control - overly broad permissions are the most common serious issue. Other frequent problems are public storage buckets, open security groups, missing logging and leaked access keys. Cloud testing must follow each provider's penetration testing policy.""",
     """Provider concepts:
• AWS: IAM users/roles/policies, S3 buckets, EC2 + security groups, VPC, CloudTrail (API logs), GuardDuty, Config, IMDSv2.
• Azure: Entra ID (identities), RBAC roles, subscriptions/resource groups, Storage accounts/Blob, NSGs, Activity Log, Defender for Cloud.
• Google Cloud: IAM with roles on projects/folders/organisation, Cloud Storage, VPC firewall rules, Cloud Audit Logs, Security Command Center.

Common misconfigurations: wildcard policies ("Action": "*", "Resource": "*"); long-lived access keys for humans; no MFA on root/global admins; public buckets/blobs; security groups allowing 0.0.0.0/0 to SSH/RDP/databases; disabled or short-retention logging; unencrypted snapshots shared publicly; IMDSv1 enabled; secrets in environment variables or code.

Assessment approach: review with read-only credentials (e.g. SecurityAudit/Reader roles); free tools - Prowler (AWS/Azure/GCP), ScoutSuite, CloudSploit, Steampipe; map to CIS Foundations Benchmarks.

Cloud threat modelling: identify assets, trust boundaries (internet -> load balancer -> compute -> data), identities and their permissions, data flows; ask STRIDE questions; prioritise identity and data exposure.

Pen testing policy: AWS and Azure and GCP allow testing of your own resources for most services without pre-approval but prohibit DoS and testing the provider's infrastructure - always check current policy.""",
     "The cloud provider builds and guards the apartment block; you choose who gets keys to your flat and whether you leave the windows open.",
     "A public storage bucket containing database backups was found by automated scanning bots within hours of being made public.",
     "Cloud security engineers and testers review IAM and configuration far more than they exploit software bugs.",
     ["Read-only audit role", "Enumerate IAM", "Storage exposure", "Network rules", "Logging", "Benchmark (CIS) mapping"],
     ["Complete the Cloud IAM Review lab and Project 12", "Rewrite a wildcard IAM policy with least privilege", "List the logging service for each provider"],
     "Threat model a three-tier web app in AWS and list the top five risks.",
     ["Testing provider infrastructure", "Using admin credentials for an audit when read-only will do", "Ignoring identity in favour of network findings"],
     "Customers own identity and configuration. Audit IAM, storage, network and logging read-only against CIS benchmarks, within provider policy.",
     ["Shared responsibility model", "IAM", "S3 bucket", "Security group", "CloudTrail", "Prowler"],
     "AWS CloudTrail, Azure Activity Log and Google Cloud Audit Logs record control-plane API calls.",
     [Q("Which is the customer's responsibility in IaaS?", ["Data centre physical security", "IAM configuration and data", "Hypervisor patching", "Power supply"], 1, "Identity, config and data are customer-owned."),
      Q("A security group allowing 0.0.0.0/0 on port 22 means...", ["SSH is open to the whole internet", "SSH is disabled", "Only local access", "Encrypted DNS"], 0, "Any source can reach SSH."),
      Q("Which free tool audits cloud accounts against best practices?", ["Prowler", "Hydra", "Maltego", "John"], 0, "Prowler (also ScoutSuite).")]),

   L("m13l3", "Container Security: Docker, Images, Registries, Secrets and Kubernetes", "Advanced", 45,
     "Containers run much of today's software; misconfiguration can expose the host.",
     ["Explain container architecture and isolation", "Review images, registries and secrets handling", "Identify Kubernetes security basics and hardening"],
     """A container packages an application with its dependencies and runs it isolated using Linux kernel features (namespaces and cgroups). Images are built from Dockerfiles and stored in registries. Because containers share the host kernel, dangerous settings - running as root, --privileged mode, mounting the Docker socket - can let a compromised container affect the host. Kubernetes orchestrates many containers and adds its own security controls such as RBAC, network policies and pod security standards.""",
     """Docker review checklist:
• Image: minimal base (distroless/alpine/slim), pinned versions, no secrets baked in (check layers with 'docker history'), scanned with Trivy or Grype (free).
• Runtime: run as non-root (USER in Dockerfile), no --privileged, drop capabilities, read-only filesystem, no /var/run/docker.sock mount, resource limits, seccomp/AppArmor profiles.
• Secrets: use Docker/Kubernetes secrets or an external vault, not ENV variables in images.
• Registries: private, authenticated, image signing (cosign/Notary), vulnerability scanning on push.
• Networking: only publish needed ports; separate networks per app.

Kubernetes basics: API server (protect it - not public), etcd (encrypt, restrict), kubelet (authN/authZ on), RBAC least privilege (avoid cluster-admin for apps), service accounts (disable automount when unused), NetworkPolicies (default deny), Pod Security Standards (restricted), audit logging, namespaces for separation. Free tools: kube-bench (CIS benchmark), kubescape, Trivy.

Image vulnerability assessment: scan, prioritise by fixable/critical/reachable, rebuild regularly, avoid 'latest' tags.""",
     "Containers are tents on the same campsite sharing one ground (the kernel); a privileged container is a tent with a trapdoor into the campsite office.",
     "A cryptomining campaign targeted Kubernetes dashboards exposed to the internet without authentication.",
     "Container and Kubernetes reviews are growing parts of cloud and DevSecOps roles.",
     ["Dockerfile", "Build image", "Scan (Trivy)", "Sign + push to registry", "Deploy (non-root, no privileged)", "Runtime monitoring"],
     ["Complete the Container Review lab and Project 13", "Fix a sample Dockerfile that runs as root with a secret in ENV", "List five Kubernetes hardening controls"],
     "Explain why mounting /var/run/docker.sock into a container is equivalent to giving it root on the host.",
     ["Using 'latest' tags in production", "Storing secrets in image layers", "Running everything as root with --privileged"],
     "Minimal, scanned, signed images; non-root, unprivileged runtime; real secrets management; Kubernetes RBAC, network policies and pod security.",
     ["Container", "Docker", "Image", "Registry", "Kubernetes", "RBAC", "Trivy"],
     "Namespaces isolate what a container can see; cgroups limit what resources it can use.",
     [Q("Which Linux feature limits container resource usage?", ["namespaces", "cgroups", "SUID", "cron"], 1, "cgroups control resources."),
      Q("Which setting is most dangerous for container isolation?", ["USER appuser", "--privileged", "read-only filesystem", "Dropping capabilities"], 1, "--privileged removes most isolation."),
      Q("Which free tool scans container images for vulnerabilities?", ["Trivy", "Burp", "Hashcat", "Maltego"], 0, "Trivy (or Grype).")]),

   L("m13l4", "API Security: Architecture, Tokens, Authorisation, Discovery and Hardening", "Advanced", 40,
     "APIs carry most modern data flows, and token mistakes are subtle and severe.",
     ["Assess API authentication and token handling", "Discover undocumented and shadow APIs", "Harden APIs end to end"],
     """API security builds on Module 9. Most APIs use tokens: API keys for applications, OAuth 2.0 access tokens and OpenID Connect ID tokens for users, often as JWTs. Security depends on correctly validating tokens, scoping them, expiring them and authorising every object and function. Discovery finds every API an organisation exposes, including forgotten versions and shadow APIs nobody documented.""",
     """Token security: JWT signature verified with the right algorithm (reject 'none', avoid algorithm confusion between RS256/HS256), check exp/iss/aud, short lifetimes with refresh tokens, store securely (HttpOnly cookies or secure platform storage), revoke on logout/compromise. OAuth: use Authorization Code + PKCE for public clients; avoid the Implicit flow; validate redirect URIs exactly; least-privilege scopes.

Authorisation: object-level (BOLA), property-level (excessive data, mass assignment), function-level (admin endpoints) - test every role on every endpoint.

Rate limiting and resource consumption: per user/IP/token limits, pagination caps, payload size limits, timeouts; GraphQL depth and complexity limits; disable introspection in production if not needed.

Data validation: JSON schema validation, type and range checks, reject unknown fields.

API discovery: OpenAPI files, JavaScript bundles, mobile apps, gateway inventories, certificate logs, traffic analysis - all within scope. Old versions (/v1 still live after /v3) are classic findings (OWASP API9).

Hardening: API gateway (authN, rate limits, schema validation, logging), mTLS for service-to-service, central authorisation, inventory and lifecycle management, monitoring for anomalous usage.""",
     "A token is a festival wristband: it must be genuine (signature), for the right festival (audience), not expired, and only valid for the areas printed on it (scopes).",
     "An API accepted JWTs with 'alg: none', letting anyone forge an admin token - fixed by enforcing the expected algorithm and key.",
     "API security testing is one of the fastest-growing specialties and a core AppSec engineer skill.",
     ["Discover APIs", "Inventory + versions", "Token validation tests", "Authorisation matrix tests", "Rate limit + schema tests", "Harden at gateway + code"],
     ["Complete Project 8 using the mock API", "Write a JWT validation checklist", "Build an authorisation test matrix (roles x endpoints)"],
     "Write a finding for 'deprecated /v1 API still live and missing authorisation checks'.",
     ["Trusting token claims without verifying the signature", "Long-lived tokens with no revocation", "Forgetting old API versions"],
     "Validate tokens strictly, authorise every object/property/function, limit resources, validate schemas and inventory every API.",
     ["OAuth 2.0", "OpenID Connect", "JWT", "PKCE", "API gateway", "Shadow API"],
     "For mobile and single-page apps, the recommended OAuth flow is Authorization Code with PKCE.",
     [Q("Which JWT 'alg' value must always be rejected?", ["RS256", "ES256", "none", "HS256 (when expected)"], 2, "'none' means unsigned."),
      Q("Which OAuth flow is recommended for public clients?", ["Implicit", "Authorization Code with PKCE", "Password grant", "None"], 1, "Code + PKCE."),
      Q("A forgotten old API version still in production relates to...", ["Improper inventory management", "XSS", "SQL injection", "Clickjacking"], 0, "OWASP API9:2023.")]),

   L("m13l5", "Source Code Security, Static Analysis and Software Supply Chain", "Advanced", 40,
     "Finding flaws in code is cheaper than finding them in production - and supply chain attacks bypass perimeter defences.",
     ["Perform focused secure code review", "Use SAST, dependency and secrets scanning", "Explain software supply chain risks and controls"],
     """Secure code review reads source code to find vulnerabilities before release. Static analysis (SAST) tools scan code automatically for risky patterns. Dependency scanning (SCA) finds known-vulnerable libraries, and secrets scanning finds passwords and keys committed to repositories. The software supply chain includes every library, build tool and pipeline step; attackers increasingly compromise these to reach many victims at once.""",
     """Manual review focus: sources (user input) -> sinks (SQL queries, OS commands, file paths, HTML output, deserialization, redirects); authentication and authorisation checks; crypto usage; error handling and logging; secrets.

Common weaknesses (CWE Top 25 examples): CWE-79 XSS, CWE-89 SQLi, CWE-78 OS command injection, CWE-22 path traversal, CWE-787 out-of-bounds write, CWE-862 missing authorisation, CWE-798 hardcoded credentials, CWE-502 deserialization.

Free tools: Semgrep (SAST rules), Bandit (Python), SpotBugs/FindSecBugs (Java), ESLint security plugins; SCA - OWASP Dependency-Check, GitHub Dependabot, npm audit, pip-audit, Trivy; secrets - gitleaks, trufflehog; GitHub secret scanning and code scanning (CodeQL) on public repos.

Supply chain: typosquatted packages, compromised maintainer accounts, malicious updates, poisoned build pipelines. Controls: pin and verify dependencies (lockfiles, hashes), SBOMs (CycloneDX/SPDX), signed artefacts (Sigstore/cosign), SLSA build levels, least-privilege CI tokens, branch protection, code review requirements, reproducible builds.

Process: shift-left (scan in IDE and pull requests), triage findings (many SAST results are false positives), fix and add tests.""",
     "SAST is a spell-checker for security mistakes; SCA checks the ingredients list for recalled products; supply chain security makes sure nobody swapped ingredients at the factory.",
     "A popular open-source compression library had a backdoor inserted by a maintainer over a long period; it was caught shortly before reaching major stable Linux releases - a wake-up call for supply chain verification.",
     "Code review and supply chain security are central to AppSec and DevSecOps careers.",
     ["Developer commit", "Secrets scan", "SAST", "SCA + SBOM", "Signed build", "Review + deploy"],
     ["Review the sample vulnerable code in the Code Review lab", "Run through the gitleaks/Semgrep entries in the Toolkit", "Write a CI pipeline security checklist"],
     "Find three vulnerabilities in a 40-line sample login handler and propose fixes.",
     ["Committing secrets then 'deleting' them (they stay in Git history - rotate them)", "Ignoring transitive dependencies", "Treating every SAST result as true"],
     "Review sources to sinks, automate SAST/SCA/secrets scanning, and protect the supply chain with pinning, SBOMs and signed builds.",
     ["SAST", "SCA", "SBOM", "Secrets scanning", "Supply chain attack", "CWE"],
     "A secret committed to Git must be rotated even after removal, because history preserves it.",
     [Q("Which tool type finds known-vulnerable libraries?", ["SAST", "SCA", "DAST", "WAF"], 1, "Software Composition Analysis."),
      Q("A password was committed to Git and then deleted in the next commit. What must you do?", ["Nothing", "Rotate the secret", "Rename the repo", "Add a comment"], 1, "Git history still contains it."),
      Q("What is an SBOM?", ["A firewall", "A list of components in software", "A malware family", "A browser"], 1, "Software Bill of Materials.")]),

   L("m13l6", "Binary Analysis, Reverse Engineering and Malware Analysis Foundations", "Advanced", 50,
     "Understanding compiled code lets you verify software integrity, analyse suspicious files and understand memory-safety flaws.",
     ["Describe binaries, assembly and CPU basics", "Compare static and dynamic analysis", "Apply safe malware analysis foundations"],
     """A binary is a compiled program the CPU runs directly. Reverse engineering works out what a binary does without its source code, using disassemblers and decompilers to turn machine code back into readable form, and debuggers to watch it run. Malware analysis applies these techniques to suspicious files to understand their behaviour and produce detections. It must be done in an isolated lab with no connection to real networks.""",
     """Binary fundamentals: formats PE (Windows), ELF (Linux), Mach-O (macOS); sections (.text code, .data, .rdata), imports/exports, entry point; strings; packers.

Assembly introduction (x86-64): registers (RAX, RBX, RCX, RDX, RSP stack pointer, RIP instruction pointer), instructions (mov, add, sub, cmp, jmp, je/jne, call, ret, push, pop), the stack and calling conventions. ARM64 for mobile/Apple.

Static analysis: file type and hashes (sha256sum), strings, imports (what APIs it uses), entropy (packing), disassembly/decompilation. Free tools: Ghidra (NSA, open source), Cutter/radare2, Detect It Easy, PE-bear, YARA for pattern rules.

Dynamic analysis: run in an isolated VM snapshot with no network (or a simulated network such as INetSim/FakeNet-NG), monitor processes (Procmon, Process Explorer), file and registry changes (Regshot), network (Wireshark), debuggers (x64dbg, GDB). Revert the snapshot afterwards.

Malware analysis foundations: triage (hash lookup in threat intel), static, behavioural, code-level; output IOCs (hashes, domains, mutexes, registry keys) and YARA/Sigma detections. Safety: use dedicated analysis VMs, never on your host, disable shared folders/clipboard, handle samples in password-protected archives ('infected' is the convention), comply with law and policy.

Software integrity: verify signatures (Authenticode, GPG), compare hashes, check build provenance.""",
     "Reverse engineering is taking apart a cake to work out the recipe; dynamic analysis is watching the baker in a sealed kitchen.",
     "Analysts reverse-engineered a ransomware sample in an isolated lab, found a flaw in its key handling and helped victims recover files without paying.",
     "These foundations lead to malware analyst, threat researcher and exploit-development careers and improve understanding of memory-safety vulnerabilities.",
     ["Sample in isolated VM", "Hash + triage", "Static (strings, imports, Ghidra)", "Dynamic (snapshot, monitors)", "IOCs + YARA", "Revert snapshot"],
     ["Read the Ghidra and YARA entries in the Toolkit", "Identify file types from sample magic bytes", "Write a simple YARA rule for a fictional string"],
     "Describe the lab set-up you would need to safely analyse a suspicious invoice attachment.",
     ["Analysing malware on your everyday computer", "Allowing samples network access", "Forgetting to revert snapshots"],
     "Know binary formats and assembly basics; combine static and dynamic analysis in an isolated lab; produce IOCs and detections; verify software integrity.",
     ["Binary", "Assembly", "Disassembler", "Ghidra", "Static analysis", "Dynamic analysis", "YARA", "IOC"],
     "RIP holds the address of the next instruction on x86-64; controlling it is the goal of many memory-corruption exploits.",
     [Q("Which free tool from the NSA is a disassembler/decompiler?", ["Wireshark", "Ghidra", "Nessus", "Burp"], 1, "Ghidra."),
      Q("Dynamic malware analysis should be done...", ["On your work laptop", "In an isolated, snapshotted VM without real network access", "On a production server", "In the cloud without controls"], 1, "Isolation is essential."),
      Q("Which register holds the next instruction address on x86-64?", ["RAX", "RSP", "RIP", "RBX"], 2, "RIP (instruction pointer).")]),
  ],
  ["AD review", "Cloud IAM review", "Container review", "API token analysis", "Code review", "Binary/malware triage concepts with safe sample data"],
  "p11",
  "Complete all 6 lessons, pass the module assessment and complete at least three of the AD, Cloud, Container and Code Review labs.",
  ["Explain tiered admin", "List 6 cloud misconfigurations", "List 6 container hardening steps", "Explain JWT validation", "Name SAST/SCA/secrets tools", "List x86-64 key registers"],
  ["AD architecture", "Domain controllers", "Kerberos", "LDAP", "Group Policy", "Trusts", "Misconfigurations", "Identity security", "Cloud fundamentals", "AWS", "Azure", "Google Cloud",
   "IAM", "Storage security", "Cloud logging", "Shared responsibility", "Cloud threat modelling", "Docker", "Images", "Registries", "Secrets", "Kubernetes", "Container hardening",
   "API architecture", "Token security", "API discovery", "Static analysis", "Secure coding", "Dependency security", "Secrets detection", "Supply chain", "Binary fundamentals",
   "Assembly", "Debugging", "Reverse engineering", "Malware analysis foundations"])
