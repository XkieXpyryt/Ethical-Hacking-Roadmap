from lib import *

M("m01", 1, "Computer Fundamentals", "computer",
  "Every attack and every defence happens on a computer. This module builds the foundation: what is inside a machine, how it represents data, how an operating system runs programs, and how to build a safe virtual lab for the rest of the course.",
  ["Describe the main hardware components and what each one does",
   "Convert between binary, decimal and hexadecimal",
   "Explain processes, threads, memory and filesystems",
   "Build an isolated virtual machine lab with snapshots",
   "Explain client-server communication and cloud basics"],
  ["None - this is the starting point"],
  [
   L("m01l1", "What Computers Do: Hardware, Software and I/O", "Beginner", 15,
     "You cannot secure something you do not understand. Every security concept later in this course sits on top of these basics.",
     ["Define a computer as an input-process-output machine", "Tell hardware and software apart", "Name common input and output devices"],
     """A computer is a machine that takes input, processes it according to instructions, stores results and produces output. Hardware is the physical part you can touch: the processor, memory, disks, keyboard and screen. Software is the set of instructions that tells the hardware what to do: the operating system, apps and drivers. Input devices (keyboard, mouse, microphone, camera, sensors) send data in; output devices (screen, speakers, printer) send results out. Some devices do both, such as a touchscreen or a network card.""",
     """The classic model is Input -> Process -> Storage -> Output. The CPU fetches instructions from memory, decodes them and executes them, millions or billions of times per second.

Software comes in layers:
• Firmware - low-level code stored on chips (BIOS/UEFI, device firmware).
• Operating system - manages hardware and gives programs a safe way to use it.
• Drivers - translate between the OS and a specific piece of hardware.
• Applications - the programs people use (browsers, office apps, games).

Each layer trusts the one beneath it. That is why attacks against firmware or drivers are so serious: code that runs lower in the stack can see and change everything above it. A keyboard is an input device, but a malicious USB device can pretend to be a keyboard and type commands - a reminder that "input" is a trust decision.""",
     "A computer is like a kitchen: ingredients come in (input), the chef follows a recipe (software) using appliances (hardware), dishes are stored or served (storage and output).",
     "When you type a web address, the keyboard sends input, the browser (software) processes it, the network card sends and receives data, and the screen shows the page.",
     "Attackers target every layer: firmware rootkits, malicious drivers, vulnerable apps and fake input devices. Defenders need to know which layer they are protecting.",
     ["Input", "Process (CPU)", "Storage", "Output"],
     ["List five pieces of hardware in the device you are using", "Name the operating system and two applications running right now", "Classify a touchscreen, printer and webcam as input, output or both"],
     "Draw the software stack of your phone from firmware to apps and mark which layer a malicious app would live in.",
     ["Thinking software and the operating system are the same thing", "Forgetting that firmware is software too", "Assuming input devices are always trustworthy"],
     "Computers turn input into output using hardware directed by layered software. Lower layers are more privileged and more valuable to attackers.",
     ["Hardware", "Software", "Firmware", "Driver", "Operating system"],
     "Exams like to ask you to classify a device or a program into the right category - remember firmware counts as software.",
     [Q("Which of these is software?", ["RAM module", "UEFI firmware", "Keyboard", "Power supply"], 1, "Firmware is code stored on a chip, so it is software even though it lives close to the hardware."),
      Q("A touchscreen is best described as...", ["Input only", "Output only", "Both input and output", "Storage"], 2, "It displays output and accepts touch input."),
      TF("Code that runs lower in the software stack generally has more control over the system.", True, "Lower layers like firmware and kernel drivers sit beneath everything else and can see or modify it.")]),

   L("m01l2", "Inside the Machine: CPU, Memory, Storage and Boot", "Beginner", 25,
     "Memory attacks, disk forensics and boot-level malware all require knowing how these components work together.",
     ["Explain the role of the CPU, RAM and storage", "Compare SSDs and HDDs", "Describe what BIOS/UEFI does and the boot sequence"],
     """The CPU (central processing unit) is the brain: it executes instructions. RAM is fast, temporary working memory - it is wiped when power is lost. Storage (SSD or HDD) keeps data permanently. The motherboard connects everything. When you press the power button, firmware called BIOS or UEFI tests the hardware, finds a boot device and starts the operating system's boot loader.""",
     """CPU: contains cores (independent execution units), registers (tiny, very fast storage), caches (L1/L2/L3) and a clock. Architectures include x86-64 (most PCs and servers) and ARM (phones, many laptops, cloud servers).

Memory hierarchy (fastest to slowest): registers -> cache -> RAM -> SSD -> HDD -> network storage. Faster is smaller and more expensive.

Storage:
• HDD - spinning magnetic platters, cheaper, slower, deleted data often recoverable.
• SSD - flash memory, much faster, uses TRIM and wear levelling, which complicates forensic recovery.

Motherboard: hosts the CPU socket, RAM slots, chipset, buses (PCIe, USB, SATA) and firmware chip.

Boot process (UEFI): power on -> firmware initialises hardware (POST) -> Secure Boot checks the boot loader's signature -> boot loader loads the kernel -> kernel starts system services -> login screen. Legacy BIOS uses the MBR; UEFI uses a GPT disk and an EFI System Partition. Secure Boot exists precisely to stop "bootkits" that load before the OS and its defences.""",
     "The CPU is a chef, RAM is the kitchen counter (fast but cleared at closing time), storage is the pantry (slow but permanent) and the motherboard is the kitchen itself.",
     "Incident responders capture RAM before shutting a compromised machine down, because running malware and encryption keys may exist only in memory.",
     "Memory forensics, cold-boot attacks, Secure Boot bypasses and disk recovery all depend on these components.",
     ["Power on", "POST", "UEFI + Secure Boot", "Boot loader", "Kernel", "Services", "Login"],
     ["Open your system information tool (Windows: msinfo32, Linux: lscpu and free -h) and record CPU, cores and RAM", "Find whether your disk uses GPT or MBR", "Check whether Secure Boot is enabled"],
     "Explain to a non-technical friend why pulling the plug on an infected computer can destroy evidence.",
     ["Confusing RAM (memory) with storage", "Assuming deleted files on an SSD are as recoverable as on an HDD", "Thinking BIOS and UEFI are identical"],
     "The CPU executes, RAM holds working data, storage persists it and UEFI starts the system securely. Volatile memory matters greatly in investigations.",
     ["CPU", "RAM", "SSD", "UEFI", "Secure Boot", "POST"],
     "Remember the order of volatility: CPU registers and cache, RAM, then disk. Collect evidence from most to least volatile.",
     [Q("Which component loses its contents when power is removed?", ["SSD", "HDD", "RAM", "UEFI chip"], 2, "RAM is volatile memory."),
      Q("What is Secure Boot designed to prevent?", ["Slow boot times", "Unsigned or tampered boot loaders running", "Disk fragmentation", "Network attacks"], 1, "Secure Boot verifies boot components are signed by trusted keys."),
      Q("Why is evidence collection from RAM done before shutdown?", ["RAM is cheaper", "Volatile data is lost on power-off", "Disks cannot be imaged", "It is required by UEFI"], 1, "Running processes, network connections and keys live in RAM and vanish on shutdown.")]),

   L("m01l3", "Binary, Hexadecimal and Data Representation", "Beginner", 25,
     "Packet captures, memory dumps, hashes and file signatures are all shown in hex. Reading them is a core skill.",
     ["Convert decimal to binary and back", "Convert binary to hexadecimal", "Explain bits, bytes, ASCII and encoding"],
     """Computers store everything as bits: 0 or 1. Eight bits make a byte, which can hold 256 values (0-255). Binary is base 2, decimal is base 10 and hexadecimal is base 16 (digits 0-9 then A-F). One hex digit equals exactly four bits, so a byte is always two hex digits - for example 255 = 11111111 = FF. Text is stored by mapping characters to numbers: in ASCII, "A" is 65 (0x41).""",
     """Place values: binary columns are 128, 64, 32, 16, 8, 4, 2, 1. To convert 200 to binary: 128 fits (72 left), 64 fits (8 left), 32 no, 16 no, 8 fits (0 left) -> 11001000.

Binary to hex: split into groups of four: 1100 1000 -> C 8 -> 0xC8.

Units: 1 KB = 1000 bytes (KiB = 1024). Network speeds are in bits per second; file sizes in bytes.

Encodings: ASCII covers English characters in 7 bits; UTF-8 extends this to every language and is the web standard. Base64 turns binary data into printable text - it is encoding, NOT encryption, and anyone can decode it.

File signatures ("magic bytes") identify file types regardless of extension: PDF files begin with 25 50 44 46 ("%PDF"), PNG with 89 50 4E 47, Windows executables with 4D 5A ("MZ"). Analysts use this to spot a "photo.jpg" that is really a program.""",
     "Hex is shorthand for binary, like writing a phone number in groups so it is easier to read.",
     "A malware analyst opens a suspicious invoice.pdf in a hex viewer, sees it starts with 4D 5A and immediately knows it is a Windows executable in disguise.",
     "Hashes, MAC addresses, IPv6, memory addresses and packet bytes are written in hex. Base64 is often mistaken for encryption in poor security designs.",
     ["Decimal 200", "Binary 11001000", "Split 1100|1000", "Hex C8"],
     ["Convert 10, 172 and 255 to binary", "Convert 0x2F and 0xA0 to decimal", "Use the in-app Number Converter to check your answers"],
     "Decode the hex string 48 61 63 6B using an ASCII table. What word is it?",
     ["Treating Base64 as encryption", "Forgetting that one hex digit is exactly 4 bits", "Mixing up KB (1000) and KiB (1024)"],
     "Everything is bits. Hex groups bits in fours, bytes are two hex digits, and encodings map numbers to text. Base64 hides nothing.",
     ["Bit", "Byte", "Hexadecimal", "ASCII", "Base64", "Magic bytes"],
     "Practise the 128-64-32-16-8-4-2-1 table until you can convert a byte in your head - subnetting depends on it.",
     [Q("What is 0xFF in decimal?", ["15", "128", "255", "256"], 2, "F = 15, so 15 x 16 + 15 = 255."),
      Q("Base64 is best described as...", ["Strong encryption", "A hashing algorithm", "A reversible encoding", "A compression method"], 2, "Base64 is just encoding - trivially reversible."),
      Q("A file named report.jpg begins with the bytes 4D 5A. What is it most likely?", ["A JPEG image", "A Windows executable", "A PDF", "A text file"], 1, "4D 5A ('MZ') is the signature of Windows PE executables.")]),

   L("m01l4", "Operating Systems: Processes, Memory and Filesystems", "Beginner", 30,
     "Privilege escalation, malware persistence and forensic analysis all happen inside the operating system.",
     ["Explain what an OS kernel does", "Distinguish processes and threads", "Describe filesystems, files and directories"],
     """The operating system (OS) manages hardware and runs programs safely side by side. Its core is the kernel, which runs with full privileges. User programs run in user mode and must ask the kernel (via system calls) to open files or use the network. A running program is a process; each process has its own memory space and one or more threads that do the work. A filesystem organises data on disk into files and directories with names, sizes, timestamps and permissions.""",
     """Kernel mode vs user mode: the CPU enforces privilege rings. A bug in user mode crashes one app; a bug in kernel mode can compromise the whole system.

Processes: identified by a PID, owned by a user, with a parent process. Threads share their process's memory, which makes them efficient but also allows race conditions.

Memory management: each process sees virtual memory, which the OS maps to physical RAM in pages. Protections include ASLR (randomising memory addresses) and DEP/NX (marking data as non-executable) - both exist to make memory-corruption exploits harder. When RAM runs out, pages are swapped to disk.

Filesystems: NTFS (Windows), ext4 and XFS (Linux), APFS (Apple), FAT32/exFAT (removable media). They store metadata such as owner, permissions and MAC times (modified, accessed, changed/created). Investigators build timelines from these timestamps. Linux uses a single tree starting at /; Windows uses drive letters such as C:\\.""",
     "The kernel is a building manager with every key. Tenants (processes) live in separate flats (memory spaces) and must ask the manager to open shared doors (system calls).",
     "Task Manager on Windows and top or ps on Linux list processes. A security analyst who spots an unknown process running as SYSTEM or root investigates it first.",
     "Privilege escalation means moving from user mode toward kernel or administrator control. Understanding processes and permissions is how you find and stop it.",
     ["App requests file", "System call", "Kernel checks permissions", "Filesystem reads disk", "Data returned"],
     ["Open Task Manager or run ps aux and find the process ID of your browser", "Identify which user owns three different processes", "Look at the properties of a file and note its timestamps"],
     "Find a process on your machine you do not recognise and research what it is before assuming it is harmful.",
     ["Assuming every process with an odd name is malware", "Confusing a program (file on disk) with a process (running instance)", "Ignoring which user a process runs as"],
     "The kernel controls everything; processes run isolated in user mode; filesystems hold files plus metadata that matter for security and forensics.",
     ["Kernel", "Process", "Thread", "PID", "ASLR", "Filesystem"],
     "Know the difference between a process and a thread, and name at least two memory protections (ASLR, DEP).",
     [Q("Which runs with the highest privilege?", ["A web browser", "The kernel", "A text editor", "A user script"], 1, "The kernel runs in the most privileged CPU mode."),
      Q("What does ASLR do?", ["Encrypts the disk", "Randomises memory addresses", "Blocks network traffic", "Compresses RAM"], 1, "Address Space Layout Randomisation makes memory addresses unpredictable for attackers."),
      Q("Threads within the same process...", ["Have separate memory", "Share the process memory", "Cannot run at the same time", "Always run as root"], 1, "Threads share their parent process's memory space.")]),

   L("m01l5", "Virtualization, VMs, Containers and Your Safe Lab", "Beginner", 30,
     "Every practical exercise in this course must happen in an isolated lab. Virtual machines are how professionals practise safely.",
     ["Explain hypervisors and virtual machines", "Compare VMs and containers", "Plan an isolated lab with snapshots"],
     """Virtualization lets one physical computer run several virtual computers (virtual machines, or VMs). A hypervisor shares the real hardware between them. Each VM thinks it has its own CPU, RAM and disk. Snapshots save a VM's exact state so you can roll back after an experiment. Containers are lighter: they share the host's kernel but isolate applications. For security practice you build VMs on an isolated virtual network so nothing you do can reach real systems.""",
     """Hypervisor types:
• Type 1 (bare metal) - runs directly on hardware: VMware ESXi, Microsoft Hyper-V, KVM, Proxmox.
• Type 2 (hosted) - runs as an app on your OS: VirtualBox (free), VMware Workstation Pro (free for personal use), UTM on Apple Silicon.

VM vs container: a VM includes a full guest OS and kernel, giving stronger isolation. A container (Docker, Podman) packages an app and its libraries but shares the host kernel - faster and smaller, but a kernel bug can break the isolation.

Virtual network modes:
• NAT - VM reaches the internet through the host.
• Host-only / Internal - VMs talk to each other and the host only. Use this for vulnerable practice machines.
• Bridged - VM appears directly on your real network. Avoid this for vulnerable targets.

A good beginner lab: one attacker VM (Kali or Parrot), one or more deliberately vulnerable targets (for example Metasploitable 2 or OWASP Juice Shop) on an internal-only network, with a clean snapshot of each so you can restore after every session. Check your CPU supports virtualization (Intel VT-x / AMD-V) and that it is enabled in UEFI.""",
     "A VM is a flat inside a building; a container is a room inside one flat - cheaper, but it shares the flat's walls and plumbing.",
     "Penetration testers keep a clean snapshot of their testing VM, then revert to it between clients so no data from one engagement leaks into another.",
     "Isolation is a security control. Mis-configured bridged networking is one of the most common ways learners accidentally expose vulnerable lab machines.",
     ["Hardware", "Hypervisor", "VM: Kali (attacker)", "Internal network", "VM: Vulnerable target", "Snapshot"],
     ["Check that VT-x/AMD-V is enabled", "Install VirtualBox (free)", "Create a VM, install a Linux distribution and take a snapshot named 'clean-install'", "Create an internal network and attach the VM to it"],
     "Design a lab diagram with an attacker VM and two targets that cannot reach the internet or your home network.",
     ["Putting vulnerable VMs on bridged networking", "Forgetting to take a clean snapshot before experiments", "Thinking containers isolate as strongly as VMs"],
     "Hypervisors run isolated VMs; containers share the kernel. Build labs on internal networks and always keep a clean snapshot.",
     ["Hypervisor", "Virtual machine", "Container", "Snapshot", "Host-only network"],
     "Expect a question on which network mode is safest for vulnerable targets: host-only or internal.",
     [Q("Which virtual network mode is safest for a deliberately vulnerable VM?", ["Bridged", "NAT", "Internal / host-only", "Public"], 2, "Internal or host-only keeps the target off real networks."),
      Q("Containers differ from VMs because containers...", ["Include a full guest kernel", "Share the host kernel", "Cannot run Linux", "Always need a Type 1 hypervisor"], 1, "Containers share the host kernel, so isolation is weaker."),
      Q("VirtualBox is an example of a...", ["Type 1 hypervisor", "Type 2 hypervisor", "Container runtime", "Firmware"], 1, "VirtualBox runs as an application on a host OS.")]),

   L("m01l6", "Servers, Client-Server, Cloud and Troubleshooting", "Beginner", 25,
     "Almost every target you assess is a server talking to clients. Troubleshooting skills keep your labs running.",
     ["Describe the client-server model", "Explain IaaS, PaaS and SaaS", "Apply a structured troubleshooting method"],
     """Computers communicate by sending messages over networks. In the client-server model, a client (your browser) requests something and a server (a web server) responds. A server is just a computer running software that provides a service, usually with more power and reliability. Cloud computing means renting servers and services from a provider such as AWS, Azure or Google Cloud instead of owning them. When something breaks, troubleshoot methodically: identify the problem, form a theory, test it, fix it, verify and document.""",
     """Client-server vs peer-to-peer: in client-server, servers hold the data and clients request it; in peer-to-peer, devices share directly.

Common servers: web (Apache, Nginx, IIS), database (MySQL, PostgreSQL, SQL Server), file (SMB/NFS), mail, DNS and directory (Active Directory).

Cloud service models:
• IaaS - you rent virtual machines and networks (EC2, Azure VMs). You patch the OS.
• PaaS - you deploy code; the provider runs the platform (App Service, App Engine).
• SaaS - you use finished software (Microsoft 365, Gmail).
The shared responsibility model says the provider secures the cloud itself, you secure what you put in it.

Troubleshooting method (CompTIA style): 1) identify the problem and gather information, 2) establish a theory of probable cause, 3) test the theory, 4) plan and implement a fix, 5) verify full functionality and apply preventive measures, 6) document findings. Hardware issues: power, cables, overheating, failing disks. Software issues: updates, drivers, configuration, malware.

Computer security fundamentals preview: keep systems updated, use least privilege, enable firewalls and endpoint protection, back up data and use strong authentication.""",
     "A restaurant: the customer (client) orders, the kitchen (server) prepares and returns the meal. Cloud is renting a professional kitchen by the hour.",
     "A misconfigured cloud storage bucket set to public has exposed millions of records in real incidents - the provider was secure, the customer configuration was not.",
     "You will assess servers, and much of modern infrastructure is cloud-based. Most cloud breaches come from customer misconfiguration under the shared responsibility model.",
     ["Client request", "Network", "Server processes", "Response", "Client displays"],
     ["Identify three services you use and classify them as IaaS, PaaS or SaaS", "Use the six-step method on a real or imagined 'no internet' problem", "Write down one preventive measure for that problem"],
     "Your Linux VM cannot reach the internet. Write the six troubleshooting steps you would follow and what you would check at each step.",
     ["Skipping documentation after fixing a problem", "Assuming the cloud provider secures your configuration", "Changing several things at once while troubleshooting"],
     "Clients request, servers respond. Cloud shifts some responsibility to the provider but not your configuration. Troubleshoot one change at a time and document.",
     ["Client-server", "IaaS", "PaaS", "SaaS", "Shared responsibility model"],
     "Memorise the six troubleshooting steps in order - they appear in many entry-level exams.",
     [Q("Under the shared responsibility model for IaaS, who patches the guest operating system?", ["The cloud provider", "The customer", "Nobody", "The hypervisor vendor"], 1, "In IaaS the customer manages the OS and above."),
      Q("Microsoft 365 is an example of...", ["IaaS", "PaaS", "SaaS", "On-premises"], 2, "It is finished software delivered as a service."),
      Q("What is the first step of structured troubleshooting?", ["Implement a fix", "Identify the problem", "Document findings", "Reboot everything"], 1, "Always start by identifying the problem and gathering information.")]),
  ],
  ["Identify computer hardware", "Explore system specifications", "Explore storage and memory", "Understand processes", "Explore filesystems",
   "Create a virtual machine", "Install a Linux virtual machine", "Compare operating systems", "Understand client/server communication", "Complete the computer fundamentals troubleshooting challenge"],
  "p01",
  "Complete all 6 lessons, score 70% or more on the module assessment and complete Project 1 (Cybersecurity Workstation).",
  ["Draw the memory hierarchy from memory", "Convert five random bytes between binary and hex", "Explain kernel vs user mode in two sentences", "Sketch your lab network"],
  ["Introduction to computers", "Hardware and software", "Computer architecture", "CPU", "RAM", "Storage, SSD, HDD", "Motherboards", "BIOS and UEFI", "Input/output", "Binary", "Hexadecimal", "Data representation", "Operating systems", "Processes and threads", "Memory management", "Filesystems", "Boot process", "Virtualization", "Virtual machines", "Containers", "Troubleshooting", "Servers", "Client-server", "Cloud fundamentals", "Security fundamentals"])
