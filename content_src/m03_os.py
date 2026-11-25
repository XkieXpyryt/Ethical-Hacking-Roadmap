from lib import *

M("m03", 3, "Linux & Windows Mastery", "terminal",
  "Security professionals live in two worlds: Linux, which powers most servers and security tools, and Windows, which runs most corporate desktops and identity systems. You will learn to navigate, administer, audit and harden both.",
  ["Navigate and administer Linux from the terminal", "Manage Linux permissions, users, processes, services and logs", "Administer Windows accounts, services, logs and the firewall",
   "Use PowerShell for auditing", "Explain Active Directory and Group Policy basics", "Harden Linux and Windows systems"],
  ["Module 1: Computer Fundamentals", "Module 2: Networking (recommended)"],
  [
   L("m03l1", "Linux Architecture, Distributions and Installation", "Beginner", 20,
     "Most servers, cloud workloads, containers and security tools run Linux.",
     ["Describe the Linux kernel, shell and userland", "Compare Kali, Parrot, Ubuntu and Debian", "Plan a Linux VM installation"],
     """Linux is a free, open-source operating system kernel. A distribution ('distro') packages the kernel with tools, a package manager and often a desktop. Debian is known for stability; Ubuntu is built on Debian and is popular on servers and desktops; Kali Linux and Parrot Security OS are Debian-based distros pre-loaded with security testing tools. You interact through a shell (usually Bash or Zsh) in a terminal.""",
     """Layers: hardware -> kernel (processes, memory, drivers, filesystems) -> system libraries (glibc) -> shell and utilities (GNU coreutils) -> applications.

Distributions:
• Debian - stable, conservative, uses apt and .deb packages.
• Ubuntu - Debian-based, LTS releases every two years with five years of standard support.
• Kali Linux - maintained by OffSec; hundreds of security tools; intended for authorised testing, not daily use.
• Parrot Security OS - similar toolset with privacy tooling and a lighter 'Home' edition.
• Red Hat/Fedora/Rocky - use dnf and .rpm packages, common in enterprises.

Installation for this course: download the official image from the vendor website and verify its SHA-256 checksum before use (this protects you from tampered downloads). Kali offers ready-made VirtualBox and VMware images. Give the VM 2+ CPU cores, 4 GB RAM and 40 GB disk where possible. Create a normal user and use sudo for admin tasks - Kali has done this by default since 2020.""",
     "The kernel is an engine; a distribution is a complete car built around it - same engine, different body, dashboard and accessories.",
     "A cloud team standardises on Ubuntu LTS for servers so security patches are predictable for five years.",
     "You will attack and defend Linux targets and run your tools from Linux. Verifying download checksums is a supply-chain security habit.",
     ["Hardware", "Kernel", "Libraries", "Shell", "Applications"],
     ["Download a Kali or Ubuntu image and verify its SHA-256 hash", "Install it in your isolated lab VM", "Take a snapshot after first boot"],
     "Explain why Kali is not recommended as an everyday desktop operating system.",
     ["Running everything as root", "Skipping checksum verification", "Using a security distro for daily browsing and banking"],
     "Linux = kernel + userland. Debian-family distros use apt. Use Kali or Parrot only in your lab, and verify downloads.",
     ["Linux kernel", "Distribution", "Shell", "Kali Linux", "Checksum"],
     "Know that Kali and Parrot are Debian-based and that checksums verify integrity, not secrecy.",
     [Q("Kali Linux is based on which distribution?", ["Fedora", "Arch", "Debian", "Slackware"], 2, "Kali is Debian-based."),
      Q("Why verify an ISO's SHA-256 checksum?", ["To encrypt it", "To confirm it was not altered or corrupted", "To make it boot faster", "To license it"], 1, "Hashes verify integrity."),
      TF("You should use the root account for all daily work on Kali.", False, "Use a normal account and sudo only when needed - least privilege.")]),

   L("m03l2", "Terminal Fundamentals, Filesystem Structure and File Operations", "Beginner", 35,
     "The terminal is the professional's main tool. Speed here pays off in every lab.",
     ["Navigate the Linux directory tree", "Create, copy, move, view and delete files", "Search with find and grep"],
     """Linux has one directory tree starting at / (root). Important folders: /home (user files), /etc (configuration), /var/log (logs), /tmp (temporary), /usr/bin (programs), /root (root's home). Basic commands: pwd (where am I), ls (list), cd (change directory), cat and less (view files), touch (create empty file), mkdir (make directory), cp (copy), mv (move/rename), rm (remove). Use 'man command' or 'command --help' to read the manual.""",
     """Paths: absolute (/etc/passwd) or relative (../notes). ~ is your home; . is current; .. is parent.

Listing: ls -la shows hidden files (starting with .), permissions, owner, size and dates.

Viewing: cat, less, head -n 20, tail -n 50, tail -f (follow a log live).

Searching:
• find / -name "*.conf" 2>/dev/null - find files by name, hiding errors.
• grep -i "error" /var/log/syslog - search inside files.
• grep -rn "password" /etc/app/ - recursive with line numbers.

Pipes and redirection: | sends output to another command (cat access.log | grep 404 | wc -l); > writes to a file (overwrites); >> appends; 2> redirects errors.

Other essentials: wc (count), sort, uniq -c, cut, awk '{print $1}', history, Tab completion, Ctrl+C to stop, Ctrl+R to search history.

Danger: rm -rf deletes recursively without asking. Double-check paths, never run commands you do not understand - especially ones copied from the internet.""",
     "The filesystem is a building with one main entrance (/); cd moves you between rooms; ls shows what is in the current room.",
     "A sysadmin counts failed SSH attempts per IP with: grep 'Failed password' /var/log/auth.log | awk '{print $(NF-3)}' | sort | uniq -c | sort -nr",
     "Log analysis, configuration review and evidence gathering all happen in the terminal.",
     ["pwd", "ls -la", "cd /var/log", "grep pattern file", "| sort | uniq -c"],
     ["Open the Linux Navigation lab", "Find all .conf files under /etc in the simulator", "Count lines containing 'error' in a log using grep and wc"],
     "Write a single command pipeline that lists the five most common IP addresses in an Apache access log.",
     ["Using rm -rf on the wrong path", "Forgetting hidden files need ls -a", "Confusing > (overwrite) with >> (append)"],
     "Know the tree, move with cd, read with cat/less, search with find/grep and chain with pipes.",
     ["Root directory", "Absolute path", "grep", "Pipe", "Redirection"],
     "Expect questions on which directory holds configs (/etc) and logs (/var/log).",
     [Q("Which directory stores system configuration files?", ["/home", "/etc", "/var/log", "/bin"], 1, "/etc holds configuration."),
      Q("Which command searches for text inside files?", ["find", "grep", "locate", "ls"], 1, "grep searches file contents; find searches names and attributes."),
      Q("What does '>>' do?", ["Overwrites a file", "Appends to a file", "Pipes to grep", "Deletes a file"], 1, ">> appends output.")]),

   L("m03l3", "Linux Permissions, Users, Groups and sudo", "Intermediate", 35,
     "Misconfigured permissions are one of the most common privilege escalation paths in real assessments.",
     ["Read and set rwx permissions", "Manage users and groups", "Identify risky SUID files and sudo rules"],
     """Every file has an owner, a group and permissions for three classes: user (owner), group and others. Each class can have read (r), write (w) and execute (x). 'ls -l' shows them as -rwxr-x---. Numbers are a shortcut: r=4, w=2, x=1, so 750 means owner rwx, group r-x, others none. Users are listed in /etc/passwd and password hashes are stored in /etc/shadow, which only root can read. sudo lets permitted users run commands as root.""",
     """chmod changes permissions: chmod 640 file or chmod u+x script.sh. chown changes owner: chown alice:devs file.

Directories: r = list, w = create/delete entries, x = enter.

Special bits:
• SUID (4xxx) - the program runs with the file owner's privileges. /usr/bin/passwd is SUID root legitimately. Unexpected SUID root binaries are a classic escalation risk.
• SGID (2xxx) - runs with group privileges or makes new files inherit the directory group.
• Sticky bit (1xxx) - on /tmp, users can only delete their own files.

Audit commands:
• find / -perm -4000 -type f 2>/dev/null (SUID files)
• find / -perm -o+w -type f 2>/dev/null (world-writable files)
• sudo -l (what can I run as root?)

User management: useradd/adduser, usermod -aG group user, passwd, userdel, groups, id.

Hardening: least privilege, no world-writable sensitive files, restrict sudo to specific commands, lock unused accounts, use SSH keys, review /etc/sudoers with visudo only.""",
     "Permissions are keys: the owner, the team (group) and visitors (others) each get a different key ring with read, write or run keys.",
     "During an authorised audit, a backup script owned by root was world-writable and run by cron. Any user could add commands that would run as root - a critical finding fixed with chmod 700.",
     "Privilege escalation findings in Linux often come down to SUID binaries, writable scripts run by root, or overly broad sudo rules.",
     ["ls -l file", "Read owner/group/others", "Check SUID/SGID", "sudo -l", "Fix with chmod/chown"],
     ["Open the Linux Permissions Audit lab", "Convert rwxr-x--- to octal", "List SUID files in the simulator and decide which are expected"],
     "Explain why a world-writable script run by root via cron is dangerous, and write the command to fix it.",
     ["Using chmod 777 to 'fix' access problems", "Giving users 'ALL' in sudoers", "Ignoring group permissions"],
     "Read rwx for user, group and others; numbers are 4-2-1. SUID, writable root scripts and broad sudo are key risks.",
     ["chmod", "SUID", "sudo", "/etc/shadow", "Least privilege"],
     "Practise octal: 755, 644, 600, 700, 4755 - be able to read and write each.",
     [Q("What is the octal value of rw-r-----?", ["640", "644", "750", "600"], 0, "rw- = 6, r-- = 4, --- = 0."),
      Q("Which file stores Linux password hashes?", ["/etc/passwd", "/etc/shadow", "/etc/hosts", "/var/log/auth.log"], 1, "/etc/shadow, readable by root only."),
      Q("Which command lists the sudo rules for your user?", ["sudo -l", "sudo su", "id -a", "whoami"], 0, "sudo -l lists allowed commands.")]),

   L("m03l4", "Processes, Services, Packages, Bash Environment and Cron", "Intermediate", 35,
     "Malware hides in processes, persists in services and cron jobs, and exploits unpatched packages.",
     ["Inspect and manage processes", "Manage systemd services and packages", "Explain environment variables and cron jobs"],
     """Processes are running programs: list them with ps aux or the live view top/htop, and stop them with kill PID. Services are background programs managed by systemd: systemctl status ssh, systemctl start, stop, enable. Packages are installed and updated with apt on Debian-based systems (sudo apt update && sudo apt upgrade). Environment variables like PATH and HOME configure the shell, and files such as ~/.bashrc run when a shell starts. Cron runs commands on a schedule.""",
     """Process tools: ps aux, ps -ef --forest (parent/child tree), top, htop, pgrep, kill -15 (graceful), kill -9 (force), lsof -p PID (open files).

systemd: unit files in /etc/systemd/system and /lib/systemd/system. 'systemctl list-units --type=service --state=running' shows running services. 'journalctl -u ssh' shows a service's logs.

Packages: apt update (refresh lists), apt upgrade (install updates), apt install, apt remove, apt list --installed, dpkg -l. Enable unattended-upgrades for automatic security patches on servers.

Environment: echo $PATH, export VAR=value, env. If PATH includes a writable directory early in the list, an attacker can plant a fake command - a known escalation technique.

Shell configuration: ~/.bashrc, ~/.profile, /etc/profile. Aliases (alias ll='ls -la') live here - attackers sometimes abuse them for persistence, so defenders review them.

Cron: crontab -l (user jobs), /etc/crontab and /etc/cron.d (system). Format: minute hour day-of-month month day-of-week command, e.g. 0 2 * * * /opt/backup.sh runs daily at 02:00. Review cron for unknown entries during audits.""",
     "Processes are workers on shift, services are the permanent staff, cron is the rota board that tells staff when to do scheduled tasks.",
     "Cryptomining malware on a compromised server showed up as a process using 100% CPU and persisted with a cron job running every minute.",
     "Persistence and privilege escalation commonly involve services, cron and PATH. Patching packages removes known vulnerabilities.",
     ["ps aux / top", "systemctl status", "journalctl -u", "crontab -l", "apt update && apt upgrade"],
     ["In the simulator, find the process using the most CPU", "List running services", "Read the system crontab and explain each entry"],
     "Write the cron line that runs /opt/scripts/integrity.sh every day at 06:30.",
     ["Using kill -9 first instead of a graceful stop", "Never running updates in the lab VM", "Ignoring system-wide cron directories"],
     "Know your process, service, package and cron commands - they are both admin tools and investigation tools.",
     ["Process", "systemd", "apt", "PATH", "Cron"],
     "Learn the five cron fields in order: minute, hour, day of month, month, day of week.",
     [Q("Which command shows the status of the SSH service on systemd?", ["service-status ssh", "systemctl status ssh", "ps ssh", "cron ssh"], 1, "systemctl status <unit>."),
      Q("What does the cron entry '30 6 * * *' mean?", ["Every 30 minutes", "06:30 every day", "30 June", "Every 6 hours"], 1, "Minute 30, hour 6, every day."),
      Q("Why is a writable directory early in PATH dangerous?", ["It slows the shell", "An attacker can plant a fake command that runs instead of the real one", "It deletes files", "It disables sudo"], 1, "PATH hijacking runs the attacker's binary.")]),

   L("m03l5", "Linux Networking, SSH, Logs, Monitoring, Storage and Hardening", "Intermediate", 40,
     "Auditing a Linux server means checking what listens, who logs in, what was logged and how it is configured.",
     ["Inspect network configuration and listening ports", "Secure SSH", "Read key logs and apply a hardening checklist"],
     """Linux networking commands: ip addr (addresses), ip route (routes), ss -tulpn (listening ports and the processes behind them). SSH provides encrypted remote access; secure it with key-based authentication and by disabling root login. Logs live in /var/log (auth.log or secure for logins, syslog or messages for general events) and in the systemd journal. Monitoring tools like top, df -h (disk) and free -h (memory) show health. Hardening reduces attack surface: patch, remove unused services, enable a firewall and use least privilege.""",
     """SSH hardening (/etc/ssh/sshd_config): PermitRootLogin no, PasswordAuthentication no (after keys work), use ssh-keygen -t ed25519, restrict users with AllowUsers, consider changing nothing else 'clever' - focus on keys, patching and fail2ban.

Firewall: ufw (simple: ufw default deny incoming; ufw allow 22/tcp; ufw enable) or nftables/iptables.

Logs: /var/log/auth.log (Debian/Ubuntu logins, sudo), /var/log/secure (RHEL), /var/log/syslog, journalctl -xe, last (login history), lastb (failed logins).

Storage: df -h (usage), du -sh (folder size), lsblk (disks), mount, /etc/fstab. Full disks can stop logging - an availability and evidence problem.

Hardening checklist (CIS-inspired): apply updates; remove unneeded packages; disable unused services; enforce SSH keys; firewall default deny; strong password policy; audit SUID and world-writable files; enable auditd; time sync (accurate logs); back up configuration.

Troubleshooting: service will not start -> systemctl status and journalctl -u; no network -> ip addr, ip route, ping gateway, resolvectl/cat /etc/resolv.conf; disk full -> df -h, du.""",
     "Hardening is like securing a house: lock doors you do not use (services), change the locks to keys only (SSH keys), install a gate (firewall) and keep a visitor log (logs).",
     "An exposed SSH server with password authentication received thousands of brute-force attempts per day. Switching to key-only auth and fail2ban stopped successful guessing entirely.",
     "Listening services are attack surface; logs are evidence; SSH configuration is a frequent audit finding.",
     ["ss -tulpn", "Review sshd_config", "Check ufw status", "Read auth.log", "Apply checklist", "Verify"],
     ["Open the Linux Log Analysis lab and find the IP with most failed logins", "List listening ports in the simulator and match them to services", "Write three sshd_config changes that improve security"],
     "Produce a 10-item hardening checklist for an Ubuntu web server and order it by impact.",
     ["Disabling password auth before confirming key login works (lockout)", "Leaving unused services listening on all interfaces", "Not checking disk space for log partitions"],
     "Check what listens (ss), who logs in (auth.log), secure SSH with keys and harden with a checklist.",
     ["ss", "sshd_config", "auth.log", "ufw", "Hardening"],
     "PermitRootLogin no and key-based authentication are the classic SSH hardening answers.",
     [Q("Which command lists listening TCP/UDP ports with their processes?", ["ip route", "ss -tulpn", "df -h", "lsblk"], 1, "ss -tulpn: tcp, udp, listening, process, numeric."),
      Q("Which log records SSH logins on Ubuntu?", ["/var/log/auth.log", "/var/log/dpkg.log", "/var/log/boot.log", "/etc/ssh/log"], 0, "auth.log records authentication events."),
      Q("Which sshd_config setting blocks direct root logins?", ["AllowRoot false", "PermitRootLogin no", "RootLogin=0", "DenyUsers *"], 1, "PermitRootLogin no.")]),

   L("m03l6", "Windows Architecture, Accounts, Permissions, Services and Processes", "Beginner", 35,
     "Most corporate endpoints run Windows. Understanding its accounts and services is essential for both attack paths and defence.",
     ["Describe Windows architecture and account types", "Read NTFS permissions", "Inspect services and processes"],
     """Windows has a kernel and user mode like Linux. Accounts can be local (exist on one PC) or domain accounts (managed centrally by Active Directory). Administrators have full control; standard users do not. User Account Control (UAC) asks before admin actions. NTFS permissions (Full control, Modify, Read & execute, Read, Write) protect files. Services run in the background, often as powerful built-in accounts like LocalSystem. Task Manager shows processes, performance, startup apps and services.""",
     """Built-in identities: Administrator, Guest (disable it), SYSTEM (more powerful than Administrator locally), LocalService and NetworkService (reduced privilege). Every account and group has a SID (security identifier).

Groups: Administrators, Users, Remote Desktop Users, Backup Operators (can read all files - sensitive!).

NTFS permissions are inherited from parent folders; explicit Deny overrides Allow. Share permissions (network) and NTFS permissions both apply - the most restrictive wins.

Services: services.msc or 'Get-Service'. Each has a startup type and a log-on account. Risks: services running as SYSTEM from a folder ordinary users can write to, and unquoted service paths with spaces.

Processes: Task Manager or 'tasklist'; Sysinternals Process Explorer (free from Microsoft) shows signatures, parent processes and network activity. Suspicious signs: svchost.exe running from the wrong folder (it should be C:\\Windows\\System32), unsigned binaries in AppData, Office spawning PowerShell.

Installation concepts: editions (Home, Pro, Enterprise), BitLocker (Pro+), Windows Update channels, local vs Microsoft vs domain sign-in.""",
     "SYSTEM is the building's maintenance master key; Administrator is the manager; standard users are staff with their own office keys.",
     "An endpoint alert fired because winword.exe started powershell.exe - a classic sign of a malicious macro. Analysts trace process parent-child relationships to understand attacks.",
     "Windows privilege escalation often abuses services, weak folder permissions and over-privileged groups.",
     ["User logs on", "Token with SIDs created", "Process starts with token", "Access check vs NTFS ACL", "Allowed / Denied"],
     ["Open Task Manager and find which account runs three services", "Check the NTFS permissions on a folder you own", "In the Windows Admin simulator, list local administrators"],
     "List three signs that a process named svchost.exe might be malicious.",
     ["Daily use of an Administrator account", "Forgetting share and NTFS permissions combine", "Assuming every svchost.exe is legitimate"],
     "Windows uses accounts, groups and SIDs; NTFS ACLs protect files; services and processes run under identities that define their power.",
     ["SID", "UAC", "NTFS", "LocalSystem", "Service"],
     "Know that explicit Deny beats Allow and that SYSTEM outranks Administrator locally.",
     [Q("Which account has the highest local privilege on Windows?", ["Guest", "Standard user", "Administrator", "SYSTEM"], 3, "SYSTEM (LocalSystem) has the highest local privileges."),
      Q("Where should a legitimate svchost.exe run from?", ["C:\\Users\\Public", "C:\\Windows\\System32", "C:\\Temp", "AppData\\Roaming"], 1, "System32 (or SysWOW64 on 64-bit for 32-bit)."),
      TF("In NTFS, an explicit Deny permission overrides an Allow.", True, "Explicit deny entries take precedence.")]),

   L("m03l7", "PowerShell, Registry, Event Viewer, Defender and Firewall", "Intermediate", 40,
     "PowerShell and event logs are the core tools for auditing Windows, and attackers misuse PowerShell heavily - defenders must understand it.",
     ["Run PowerShell cmdlets for auditing", "Navigate the registry safely", "Find key security events and check Defender and firewall status"],
     """PowerShell is Windows' powerful shell and scripting language. Commands are cmdlets named Verb-Noun, like Get-Process or Get-Service, and they pass objects through pipelines. The Registry is a database of system and app settings organised into hives such as HKLM (machine) and HKCU (current user). Event Viewer shows Windows logs; the Security log records logons and account changes. Microsoft Defender Antivirus and Windows Defender Firewall provide built-in protection.""",
     """Audit cmdlets:
• Get-LocalUser, Get-LocalGroupMember Administrators
• Get-Service | Where-Object Status -eq Running
• Get-NetTCPConnection -State Listen
• Get-NetFirewallProfile | Select Name, Enabled
• Get-MpComputerStatus (Defender status)
• Get-HotFix (installed updates)
• Get-WinEvent -FilterHashtable @{LogName='Security'; Id=4625} -MaxEvents 20

Key Security event IDs: 4624 successful logon, 4625 failed logon, 4634 logoff, 4672 special privileges assigned, 4720 user created, 4728/4732 member added to a security group, 4740 account locked out, 1102 audit log cleared (very suspicious), 7045 (System log) new service installed.

Registry: HKLM\\SOFTWARE, HKLM\\SYSTEM, HKCU. Autorun persistence locations include HKCU and HKLM ...\\Microsoft\\Windows\\CurrentVersion\\Run. Back up before editing; never edit the registry on production without change control.

PowerShell security: execution policy is NOT a security boundary; enable PowerShell script block logging and transcription so defenders can see what ran; use Constrained Language Mode and AppLocker/WDAC where appropriate.""",
     "PowerShell is a Swiss army knife; event logs are the security camera footage; the registry is the building's master settings binder.",
     "A defender found event 1102 (audit log cleared) followed by 7045 (new service) on a server at 02:14 - strong indicators of intruder activity that started a full investigation.",
     "Event IDs are the foundation of Windows detection engineering and incident response.",
     ["Get-LocalGroupMember", "Get-Service", "Get-NetFirewallProfile", "Get-WinEvent 4625", "Report findings"],
     ["Open the Windows PowerShell Audit lab", "Find failed logons (4625) in the simulator", "Check whether all three firewall profiles are enabled"],
     "Write a short PowerShell one-liner that lists running services, and explain each part.",
     ["Thinking execution policy stops attackers", "Editing the registry without a backup", "Forgetting to enable PowerShell logging"],
     "Use Verb-Noun cmdlets to audit; know key event IDs; treat the registry with care; confirm Defender and firewall are on.",
     ["PowerShell", "Cmdlet", "Registry", "Event ID 4625", "Defender"],
     "Memorise 4624, 4625, 4720, 4732, 1102 and 7045.",
     [Q("Which event ID indicates a failed logon?", ["4624", "4625", "4720", "7045"], 1, "4625 = an account failed to log on."),
      Q("Which hive stores machine-wide settings?", ["HKCU", "HKLM", "HKCR only", "HKU only"], 1, "HKEY_LOCAL_MACHINE."),
      Q("Event ID 1102 in the Security log means...", ["Successful logon", "The audit log was cleared", "Firewall enabled", "User created"], 1, "Log clearing is a strong suspicious indicator.")]),

   L("m03l8", "Active Directory, Group Policy, Windows Hardening and Troubleshooting", "Intermediate", 35,
     "Active Directory is the identity heart of most organisations. Its misconfigurations are among the highest-impact findings in internal tests.",
     ["Describe domains, domain controllers, OUs and GPOs", "Apply Windows hardening baselines", "Troubleshoot common Windows issues"],
     """Active Directory (AD) stores users, computers and groups for an organisation in a domain, managed by servers called domain controllers. Objects are organised into Organisational Units (OUs). Group Policy Objects (GPOs) push settings - password policy, firewall rules, software restrictions - to many computers at once. Hardening Windows means applying secure baselines, patching, enabling protections like BitLocker and LAPS, and limiting admin rights.""",
     """AD basics: domain (example.local), forest (one or more domains), domain controllers run AD DS, DNS and Kerberos. Users authenticate with Kerberos (tickets) or older NTLM. Privileged groups: Domain Admins, Enterprise Admins, Administrators, Account Operators, Backup Operators - keep membership minimal.

Group Policy: linked to sites, domains and OUs; processed Local -> Site -> Domain -> OU (LSDOU), later wins unless enforced. 'gpresult /r' shows applied policies; 'gpupdate /force' refreshes.

Windows hardening:
• Apply Microsoft Security Baselines or CIS Benchmarks.
• Patch monthly (Patch Tuesday); remove unsupported OS versions.
• Use LAPS so each PC has a unique local admin password.
• Enable BitLocker, Credential Guard, Defender with tamper protection, ASR rules.
• Disable SMBv1, LLMNR and NetBIOS where not needed.
• Separate admin accounts from daily accounts; use tiered administration.

Troubleshooting: Event Viewer first; 'sfc /scannow' and 'DISM /Online /Cleanup-Image /RestoreHealth' repair system files; 'ipconfig /flushdns'; Safe Mode for driver issues; Reliability Monitor for crash history.""",
     "AD is the company's staff directory and key office; Group Policy is the rulebook sent to every desk automatically.",
     "A company with the same local admin password on every PC was fully compromised from one laptop. Deploying LAPS removed that lateral movement path.",
     "Most internal penetration tests focus on AD. Defenders who understand GPOs and privileged groups prevent the most damaging attacks.",
     ["User logon", "Domain controller (Kerberos)", "Ticket issued", "GPOs applied (LSDOU)", "Access resources"],
     ["Read the Active Directory review lab briefing", "List five hardening controls and what each prevents", "Run gpresult /r on a Windows machine you own (if available)"],
     "Explain why daily email use from a Domain Admin account is dangerous.",
     ["Too many Domain Admins", "Same local admin password everywhere", "Leaving SMBv1 enabled"],
     "AD centralises identity; GPOs push settings; harden with baselines, LAPS, patching and minimal privileged groups.",
     ["Active Directory", "Domain controller", "Group Policy", "LAPS", "Kerberos"],
     "Remember GPO order LSDOU and that LAPS solves shared local admin passwords.",
     [Q("In what order are GPOs applied?", ["OU, Domain, Site, Local", "Local, Site, Domain, OU", "Domain, Local, OU, Site", "Site, OU, Local, Domain"], 1, "LSDOU - the last applied (OU) wins by default."),
      Q("What problem does LAPS solve?", ["Slow logons", "Identical local administrator passwords across machines", "DNS failures", "Disk encryption"], 1, "LAPS gives each machine a unique, rotated local admin password."),
      Q("Which protocol does AD primarily use for authentication?", ["FTP", "Kerberos", "SNMP", "Telnet"], 1, "Kerberos, with NTLM as a legacy fallback.")]),
  ],
  ["Execute safe commands", "Navigate directories", "Read and create files", "Manage permissions", "Inspect processes", "Review logs", "Understand services",
   "Perform system audits", "Identify insecure configurations", "Fix security misconfigurations"],
  "p03",
  "Complete all 8 lessons, pass the module assessment and complete the Linux Navigation, Linux Permissions and Windows PowerShell labs.",
  ["Write 20 Linux commands from memory with their purpose", "Explain rwx and octal", "List 6 Windows event IDs", "Explain LSDOU"],
  ["Linux architecture", "Distributions", "Kali", "Parrot", "Ubuntu", "Debian", "Installation", "Terminal", "Filesystem", "Permissions", "Users and groups", "Processes", "Services", "Packages",
   "Bash", "Environment variables", "Cron", "Networking", "SSH", "Logs", "Monitoring", "Storage", "Hardening", "Windows architecture", "Accounts", "NTFS", "Task Manager", "Event Viewer",
   "Defender", "Firewall", "PowerShell", "Registry", "Active Directory", "Group Policy", "Windows hardening", "Troubleshooting"])
