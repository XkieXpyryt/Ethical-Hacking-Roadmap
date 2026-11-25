from lib import *

M("m02", 2, "Networking Fundamentals", "network",
  "Hacking and defending are mostly about networks: how data moves, how devices find each other and where traffic can be seen or changed. This module takes you from 'what is a network' to reading packets.",
  ["Explain network types and devices", "Address hosts with IPv4 and IPv6 and calculate subnets", "Use the OSI and TCP/IP models to describe traffic",
   "Explain DNS, DHCP, ARP and common application protocols", "Describe routing, VLANs, NAT, firewalls and VPNs", "Analyse packets with Wireshark concepts"],
  ["Module 1: Computer Fundamentals (binary especially)"],
  [
   L("m02l1", "Networks, Network Types and Devices", "Beginner", 20,
     "Before you can map or protect a network you must recognise its parts.",
     ["Define LAN, WAN, MAN and PAN", "Explain what routers, switches and access points do", "Identify common cable types"],
     """A network is two or more devices connected to share data. A PAN (personal area network) covers a few metres, like Bluetooth earbuds. A LAN (local area network) covers a home or office. A MAN covers a city and a WAN (wide area network) connects distant sites; the internet is the largest WAN. A switch connects devices inside a LAN, a router connects different networks together, and a wireless access point lets Wi-Fi devices join the LAN.""",
     """Devices and the layer they mainly work at:
• Hub (legacy, layer 1) - repeats every signal to every port. Anyone connected can see all traffic.
• Switch (layer 2) - learns MAC addresses and forwards frames only to the right port.
• Router (layer 3) - forwards packets between networks using IP addresses; your home router also does NAT, DHCP and Wi-Fi.
• Access point - bridges wireless clients into the wired LAN.
• Firewall - filters traffic according to rules.
• Modem - converts between your ISP's line and Ethernet.

Cabling: twisted-pair copper (Cat5e/Cat6, RJ45 connectors) for most LANs, fibre optic for long distances and high speed, coaxial for some broadband.

The internet is a network of networks run by ISPs that exchange routes with each other using BGP. Your traffic typically crosses many routers owned by different organisations, which is why encryption in transit matters.""",
     "Switches are the corridors inside one building; routers are the roads between buildings; the access point is a door that lets wireless visitors in.",
     "On a shared hub or a misconfigured switch port, an attacker can capture colleagues' traffic. Modern switched networks make this harder but not impossible.",
     "Recognising devices tells you where traffic can be observed, filtered or intercepted.",
     ["Laptop", "Wi-Fi access point", "Switch", "Router/Firewall", "ISP", "Internet"],
     ["Draw your home network and label every device", "Find your router's model and what functions it performs", "Identify which devices connect by cable and which by Wi-Fi"],
     "Explain why replacing hubs with switches improved security in office networks.",
     ["Calling every box a 'router'", "Thinking switches route between networks (that is a layer 3 function)", "Assuming traffic crossing the internet stays private"],
     "Networks range from PAN to WAN. Switches move frames inside a LAN, routers move packets between networks and access points add wireless.",
     ["LAN", "WAN", "Router", "Switch", "Access point", "Hub"],
     "Associate devices with OSI layers: hub=1, switch=2, router=3.",
     [Q("Which device forwards traffic between different IP networks?", ["Switch", "Hub", "Router", "Repeater"], 2, "Routers work at layer 3 and connect networks."),
      Q("A network covering one office building is a...", ["PAN", "LAN", "MAN", "WAN"], 1, "A LAN covers a local site."),
      TF("A hub sends every frame out of every port.", True, "Hubs simply repeat signals, which is why they are insecure and obsolete.")]),

   L("m02l2", "MAC Addresses, IPv4, IPv6 and Gateways", "Beginner", 30,
     "Every scan, firewall rule and log entry refers to addresses. Misreading them leads to scanning the wrong target.",
     ["Distinguish MAC and IP addresses", "Recognise private and public IPv4 ranges", "Explain static vs dynamic addressing and the default gateway"],
     """A MAC address is a 48-bit hardware address burned into a network card, written in hex like 3C:22:FB:1A:9E:04. It is used inside a local network. An IP address identifies a device across networks. IPv4 addresses are 32 bits written as four numbers 0-255, like 192.168.1.20. IPv6 addresses are 128 bits written in hex, like 2001:db8::1. Private ranges are used inside homes and companies and are not routed on the internet. The default gateway is the router your device sends traffic to when the destination is on another network.""",
     """Private IPv4 ranges (RFC 1918):
• 10.0.0.0/8
• 172.16.0.0/12 (172.16.0.0 - 172.31.255.255)
• 192.168.0.0/16
Special: 127.0.0.0/8 loopback (localhost), 169.254.0.0/16 link-local (APIPA - appears when DHCP fails), 100.64.0.0/10 carrier-grade NAT.

IPv6: written as eight groups of four hex digits; leading zeros may be dropped and one run of zero groups may be replaced with '::'. Link-local addresses start with fe80::, global unicast typically with 2 or 3. IPv6 is often enabled by default and forgotten - a common gap where firewalls only filter IPv4.

Static addressing: manually configured (servers, printers, network devices). Dynamic: assigned by DHCP (laptops, phones).

The OUI (first three bytes) of a MAC identifies the manufacturer, which helps when inventorying devices. MAC addresses can be changed in software, so they are not proof of identity.

Check your settings: Windows 'ipconfig /all'; Linux 'ip addr' and 'ip route'.""",
     "A MAC address is like a person's name inside one office; an IP address is like a full postal address that works across the city; the gateway is the post room that sends mail outside.",
     "Seeing 169.254.x.x on a workstation tells a support analyst immediately that the PC could not reach a DHCP server.",
     "Scopes in penetration tests are defined by IP ranges. Scanning one digit wrong can mean attacking a system you have no permission to touch.",
     ["Host 192.168.1.20", "Same subnet? -> deliver via switch", "Different network? -> default gateway 192.168.1.1", "Router forwards"],
     ["Run ipconfig /all or ip addr and record your IPv4, IPv6, MAC and gateway", "Classify 10.4.5.6, 172.32.1.1, 192.168.50.3 and 8.8.8.8 as private or public", "Look up the manufacturer from your MAC OUI"],
     "Is 172.20.10.5 private? Is 172.40.10.5? Explain using the 172.16.0.0/12 range.",
     ["Thinking 172.0.0.0 - 172.255.255.255 is all private", "Ignoring IPv6 when securing a host", "Treating a MAC address as a trustworthy identity"],
     "MAC = local hardware address, IP = network address. Know the RFC 1918 ranges, loopback and APIPA, and never forget IPv6.",
     ["MAC address", "IPv4", "IPv6", "Private IP", "Default gateway", "APIPA"],
     "172.16.0.0/12 is the range people get wrong. It ends at 172.31.255.255.",
     [Q("Which address is private?", ["172.32.0.5", "10.200.1.1", "8.8.4.4", "192.169.1.1"], 1, "10.0.0.0/8 is a private range."),
      Q("A PC shows 169.254.33.7. What is the likely cause?", ["It is hacked", "It could not get an address from DHCP", "It is using IPv6", "It is the gateway"], 1, "APIPA addresses appear when DHCP fails."),
      Q("How many bits are in an IPv6 address?", ["32", "48", "64", "128"], 3, "IPv6 addresses are 128 bits.")]),

   L("m02l3", "Subnetting, CIDR and Network Masks", "Intermediate", 40,
     "Subnetting defines scan scopes, firewall rules and segmentation. It appears in nearly every networking and security exam.",
     ["Read CIDR notation", "Calculate network, broadcast and usable host range", "Choose a subnet size for a requirement"],
     """A subnet mask splits an IP address into a network part and a host part. CIDR notation writes the mask as a slash and the number of network bits: 192.168.1.0/24 means the first 24 bits are the network, leaving 8 bits (256 addresses) for hosts. The first address is the network address, the last is the broadcast address, and the rest are usable: a /24 has 254 usable hosts. Smaller networks use more network bits: a /26 has 64 addresses and 62 usable hosts.""",
     """Formula: addresses = 2^(32 - prefix); usable hosts = addresses - 2 (except /31 point-to-point and /32 single host).

Common sizes:
• /24 = 255.255.255.0 -> 256 addresses, 254 usable
• /25 = 255.255.255.128 -> 128, 126 usable
• /26 = 255.255.255.192 -> 64, 62 usable
• /27 = 255.255.255.224 -> 32, 30 usable
• /28 = 255.255.255.240 -> 16, 14 usable
• /30 = 255.255.255.252 -> 4, 2 usable

Worked example: 10.10.10.77/27. Block size = 32. Multiples of 32 in the last octet: 0, 32, 64, 96. 77 falls in the 64 block. Network = 10.10.10.64, broadcast = 10.10.10.95, usable = 10.10.10.65 - 10.10.10.94.

To find the block size quickly: 256 minus the interesting octet of the mask (256 - 224 = 32).

Choosing a size: need 50 hosts? 2^6 = 64 gives 62 usable -> /26.

Security use: scopes like 'in scope: 10.10.10.64/27 only' mean 10.10.10.96 is OUT of scope. Firewall rules and segmentation are written in CIDR.""",
     "A /24 is a street with 256 house numbers; number 0 is the street sign (network) and 255 is the loudspeaker for everyone (broadcast).",
     "A tester's authorisation covered 192.168.20.0/25. They scanned /24 by mistake and touched 128 hosts belonging to another department - a scope breach that had to be reported.",
     "Getting CIDR right is a legal and ethical requirement, not just a maths exercise: scope is defined in CIDR.",
     ["IP + mask", "Block size = 256 - mask octet", "Find the block containing the IP", "Network = block start", "Broadcast = next block - 1", "Usable = between"],
     ["Calculate network, broadcast and usable range for 192.168.5.130/25", "Do the same for 172.16.8.45/28", "Check both with the in-app Subnet Calculator"],
     "A client says scope is 10.0.4.0/22. List the first and last usable addresses and how many hosts are in scope.",
     ["Forgetting to subtract network and broadcast addresses", "Using the wrong octet for block size on masks shorter than /24", "Assuming /24 whenever no mask is given"],
     "CIDR tells you how many bits are network. Block size = 256 minus the mask octet. Scope, firewall rules and segmentation all use it.",
     ["Subnet mask", "CIDR", "Broadcast address", "Network address"],
     "Memorise /24 to /30 masks and host counts; the block-size trick is the fastest exam method.",
     [Q("How many usable hosts are in a /27?", ["30", "32", "62", "14"], 0, "2^5 = 32 addresses minus 2 = 30 usable."),
      Q("What is the broadcast address of 192.168.1.70/26?", ["192.168.1.63", "192.168.1.127", "192.168.1.255", "192.168.1.64"], 1, "Block size 64: 70 is in 64-127, so broadcast is .127."),
      Q("Which mask equals /28?", ["255.255.255.224", "255.255.255.240", "255.255.255.248", "255.255.255.192"], 1, "/28 leaves 4 host bits: mask octet 240.")]),

   L("m02l4", "OSI and TCP/IP Models, TCP, UDP, ICMP and Ports", "Beginner", 35,
     "The OSI model is the shared language of networking. Ports and protocols tell you which services a scan has found.",
     ["Name the seven OSI layers and the four TCP/IP layers", "Compare TCP and UDP", "Explain ports and the three-way handshake"],
     """The OSI model splits networking into seven layers: Physical, Data Link, Network, Transport, Session, Presentation and Application. The TCP/IP model groups these into four: Link, Internet, Transport and Application. Each layer wraps the data from the layer above (encapsulation). TCP is a reliable, connection-based transport protocol; UDP is faster but connectionless. A port number (0-65535) identifies which service on a host should receive the data, for example 443 for HTTPS.""",
     """OSI layers (7 to 1): Application (HTTP, DNS), Presentation (encoding, TLS), Session (session management), Transport (TCP/UDP, ports), Network (IP, routing), Data Link (Ethernet, MAC, switches), Physical (cables, radio). Mnemonic from layer 1 up: 'Please Do Not Throw Sausage Pizza Away'.

Data units: bits (L1), frames (L2), packets (L3), segments/datagrams (L4).

TCP: three-way handshake SYN -> SYN-ACK -> ACK, then sequenced, acknowledged delivery, ending with FIN or RST. Used by HTTP(S), SSH, SMTP, databases.
UDP: no handshake or guaranteed delivery. Used by DNS, DHCP, VoIP, streaming, QUIC.
ICMP: control and error messages (ping uses echo request/reply; traceroute uses TTL-exceeded messages). Often filtered, so 'no ping reply' does not mean 'host is down'.

Ports: well-known 0-1023, registered 1024-49151, dynamic/ephemeral 49152-65535. Key ones: 20/21 FTP, 22 SSH, 23 Telnet, 25 SMTP, 53 DNS, 67/68 DHCP, 80 HTTP, 110 POP3, 143 IMAP, 443 HTTPS, 445 SMB, 3306 MySQL, 3389 RDP.

Port states seen in scans: open (service listening), closed (reachable, nothing listening, RST returned), filtered (no answer - probably a firewall).""",
     "Sending a letter: you write it (application), put it in an envelope with a room number (transport/port), add the street address (network/IP), hand it to the local courier (data link) who walks it along the road (physical).",
     "A SOC analyst sees thousands of SYN packets to many ports with no completed handshakes from one source - a classic sign of a port scan.",
     "Scan results, firewall rules and IDS alerts are expressed in layers, protocols and ports.",
     ["Application data", "+ TCP header (ports)", "+ IP header (addresses)", "+ Ethernet frame (MACs)", "Bits on the wire"],
     ["Write the seven OSI layers from memory", "Match 22, 53, 443, 445 and 3389 to services", "Open the in-app OSI animation and follow a packet down and up the stack"],
     "Explain why a host might not answer ping but still have an open web server on port 443.",
     ["Saying DNS only uses UDP (zone transfers and large replies use TCP)", "Assuming 'no ping' means 'host offline'", "Mixing up the order of the handshake"],
     "Layers wrap data; TCP is reliable with a handshake, UDP is fast and connectionless, ICMP carries control messages, ports identify services.",
     ["OSI model", "TCP", "UDP", "ICMP", "Port", "Three-way handshake"],
     "Be ready to place protocols and devices at the correct OSI layer and to list the handshake flags in order.",
     [Q("At which OSI layer do routers operate?", ["Layer 2", "Layer 3", "Layer 4", "Layer 7"], 1, "Routers use IP addresses at the network layer."),
      Q("What is the correct TCP handshake order?", ["ACK, SYN, SYN-ACK", "SYN, ACK, SYN-ACK", "SYN, SYN-ACK, ACK", "SYN-ACK, SYN, ACK"], 2, "Client SYN, server SYN-ACK, client ACK."),
      Q("Which port is used by RDP?", ["22", "445", "3389", "8080"], 2, "Remote Desktop Protocol listens on TCP 3389 by default.")]),

   L("m02l5", "DNS, DHCP, ARP and Application Protocols", "Intermediate", 35,
     "These services are what attackers abuse and defenders monitor every day: DNS for reconnaissance, ARP for interception, cleartext protocols for credential exposure.",
     ["Explain DNS resolution and record types", "Describe DHCP DORA and ARP", "Compare cleartext and encrypted protocols"],
     """DNS turns names like example.com into IP addresses. DHCP automatically gives devices an IP address, mask, gateway and DNS server. ARP finds the MAC address that belongs to an IP address on the local network. Application protocols then carry real work: HTTP and HTTPS for the web, SSH for secure remote shells, SMTP for sending email, IMAP and POP3 for reading it, and FTP for file transfer. Older protocols like FTP and Telnet send passwords in cleartext; secure alternatives encrypt traffic.""",
     """DNS resolution: your device asks its resolver; the resolver asks a root server, then the TLD server (.com), then the authoritative server for the domain, and caches the answer for its TTL.
Record types: A (IPv4), AAAA (IPv6), CNAME (alias), MX (mail server), NS (name servers), TXT (verification, SPF, DMARC), PTR (reverse lookup), SOA (zone info).

DHCP 'DORA': Discover (client broadcasts), Offer (server proposes), Request (client accepts), Acknowledge (server confirms the lease). A rogue DHCP server can hand out a malicious gateway or DNS server - mitigated by DHCP snooping on switches.

ARP: 'Who has 192.168.1.1? Tell 192.168.1.20.' ARP has no authentication, so ARP spoofing can redirect traffic through an attacker. Mitigation: Dynamic ARP Inspection, static ARP for critical hosts, encryption.

Cleartext vs secure:
• FTP (21) -> SFTP (over SSH, 22) or FTPS
• Telnet (23) -> SSH (22)
• HTTP (80) -> HTTPS (443, TLS)
• SMTP (25) -> SMTP with STARTTLS (587) / SMTPS (465)
• IMAP (143) -> IMAPS (993); POP3 (110) -> POP3S (995)""",
     "DNS is the phone book, DHCP is the receptionist who hands out desk numbers, and ARP is shouting across the office 'who sits at desk 20?'.",
     "During an authorised internal test, a tester found an old FTP server; a packet capture showed staff passwords crossing the network in cleartext. The fix was migrating to SFTP and resetting passwords.",
     "DNS records feed reconnaissance, ARP and DHCP weaknesses enable interception, and cleartext protocols leak credentials - all common findings.",
     ["Browser asks resolver", "Resolver -> Root", "Root -> .com TLD", "TLD -> Authoritative", "Answer cached (TTL)", "Browser connects to IP"],
     ["Use the in-app DNS Lookup Simulator to resolve lab.silverline.test", "Write the DORA steps in order", "List three cleartext protocols and their secure replacements"],
     "A client's mail server only supports POP3 on port 110. Write a one-paragraph finding explaining the risk and the fix.",
     ["Thinking HTTPS hides which domain you visit (SNI and DNS can reveal it)", "Forgetting ARP only works inside a local network", "Assuming email is encrypted end-to-end by default"],
     "DNS resolves names, DHCP assigns addresses, ARP maps IP to MAC locally. Replace cleartext protocols with encrypted ones.",
     ["DNS", "DHCP", "ARP", "MX record", "SSH", "TLS"],
     "Know the DNS record types and the secure port numbers (22, 443, 465/587, 993, 995).",
     [Q("Which DNS record identifies a domain's mail servers?", ["A", "MX", "CNAME", "PTR"], 1, "MX = mail exchanger."),
      Q("What does the 'O' in DHCP DORA stand for?", ["Open", "Offer", "Order", "Override"], 1, "Discover, Offer, Request, Acknowledge."),
      Q("Which is the secure replacement for Telnet?", ["FTP", "SSH", "SNMPv1", "HTTP"], 1, "SSH encrypts remote shell sessions.")]),

   L("m02l6", "Routing, VLANs, NAT, Firewalls, VPNs, Proxies and Segmentation", "Intermediate", 35,
     "These are the controls that decide what can reach what. Testers map them; defenders design them.",
     ["Explain routing and switching decisions", "Describe VLANs and NAT", "Compare firewalls, VPNs and proxies and explain segmentation"],
     """Routers decide where packets go using a routing table. Switches decide which port a frame leaves through using a MAC address table. VLANs split one physical switch into separate logical networks. NAT lets many private addresses share one public address. Firewalls allow or block traffic using rules. VPNs create encrypted tunnels across untrusted networks. Proxies make requests on behalf of clients. Segmentation divides a network into zones so a compromise in one zone does not spread everywhere.""",
     """Routing: each router checks the destination IP against its routing table, choosing the most specific match (longest prefix); 0.0.0.0/0 is the default route. Dynamic protocols include OSPF inside organisations and BGP between them.

VLANs: tagged with 802.1Q; traffic between VLANs must pass through a router or layer 3 switch, where it can be filtered. Weaknesses include VLAN hopping via misconfigured trunk ports.

NAT/PAT: your router rewrites private source addresses to its public IP and tracks connections by port. NAT hides internal addressing but is NOT a firewall by itself.

Firewalls: packet filters (ports/IPs), stateful firewalls (track connections), next-generation firewalls (application awareness, IPS), and host firewalls (Windows Defender Firewall, nftables/ufw). Best practice: default deny, allow only what is needed, log denials.

VPNs: site-to-site (office to office) and remote access (user to company). Protocols: IPsec, WireGuard, OpenVPN, TLS-based. Proxies: forward proxies filter user browsing; reverse proxies sit in front of web servers (load balancing, WAF).

Segmentation and DMZ: public servers live in a DMZ separated from the internal LAN; sensitive systems (payments, medical devices, OT) get their own zones. Micro-segmentation applies this down to individual workloads - the network side of zero trust.""",
     "VLANs are separate floors in one building; the router is the lift with a security guard deciding who can go between floors; segmentation is fire doors that stop a fire spreading.",
     "Many ransomware outbreaks spread across flat networks in hours. Organisations with segmented networks contained infections to one zone.",
     "Testers check whether segmentation actually blocks traffic as documented; findings like 'guest Wi-Fi can reach the finance server' are high severity.",
     ["Internet", "Edge firewall", "DMZ (web server)", "Internal firewall", "User VLAN", "Server VLAN"],
     ["Run 'route print' or 'ip route' and find your default route", "Use the in-app Routing Simulator to send a packet between two VLANs", "Design a three-zone network for a small clinic"],
     "A café puts its card terminals on the same network as guest Wi-Fi. Write the risk and a segmentation fix.",
     ["Treating NAT as a firewall", "Forgetting to filter traffic between VLANs", "Allowing 'any-any' rules for convenience"],
     "Routing and switching move traffic; VLANs, firewalls and segmentation control it; VPNs encrypt it across untrusted networks.",
     ["Routing table", "VLAN", "NAT", "Firewall", "VPN", "DMZ", "Segmentation"],
     "Remember: inter-VLAN traffic needs layer 3. Default-deny is the expected best-practice answer.",
     [Q("What must traffic pass through to go from one VLAN to another?", ["A hub", "A layer 3 device", "A repeater", "An access point"], 1, "VLANs are separate broadcast domains; a router or L3 switch connects them."),
      Q("Which statement about NAT is correct?", ["NAT encrypts traffic", "NAT is a complete firewall", "NAT maps private addresses to a public address", "NAT replaces DNS"], 2, "NAT translates addresses; it is not a firewall by itself."),
      Q("A DMZ is used to...", ["Store backups", "Host public-facing servers separately from the internal network", "Speed up Wi-Fi", "Assign IP addresses"], 1, "The DMZ isolates internet-facing services.")]),

   L("m02l7", "Packet Analysis, Wireshark and Network Troubleshooting", "Intermediate", 40,
     "Packets do not lie. Reading captures is how analysts confirm attacks and how testers prove cleartext exposure.",
     ["Use capture and display filters conceptually", "Follow a TCP stream and spot suspicious patterns", "Troubleshoot connectivity layer by layer"],
     """A packet capture (PCAP) records traffic so you can inspect it later. Wireshark is the most popular free tool for this. You can filter by protocol, address or port, follow a conversation and see exactly what was sent. Only capture traffic on networks you own or are authorised to monitor - captures can contain private data. For troubleshooting, work up the layers: is the cable or Wi-Fi connected, is there an IP address, can you reach the gateway, does DNS resolve, does the service respond?""",
     """Display filters (Wireshark):
• ip.addr == 10.10.10.15
• tcp.port == 443
• http.request.method == "POST"
• dns
• tcp.flags.syn == 1 && tcp.flags.ack == 0 (connection attempts)
• ftp || telnet (cleartext protocols)
Capture filters (BPF, set before capturing): host 10.10.10.15, port 53.

Useful features: Statistics > Conversations (who talks to whom), Protocol Hierarchy, Follow TCP Stream, Export Objects (files transferred over HTTP/SMB).

Suspicious patterns: many SYNs to many ports (scan); repeated failed logins; DNS queries to random-looking long domains (possible tunnelling or malware); large uploads at night; cleartext credentials; ARP replies nobody asked for (spoofing).

Troubleshooting commands: ping (reachability), traceroute/tracert (path), ipconfig/ip addr (addressing), nslookup/dig (DNS), netstat/ss (listening ports and connections), curl (application test). Work bottom-up (physical -> application) or top-down, but change one thing at a time.

Network security fundamentals: encrypt in transit, segment, monitor, default-deny firewalls, disable unused services, patch network devices and change default credentials.""",
     "A packet capture is CCTV for the network: you can rewind and see exactly who said what to whom.",
     "An analyst investigating a suspected data leak filtered a capture by large outbound flows and found an unexpected 2 GB upload to an unknown IP at 03:00.",
     "PCAP analysis is used in incident response, threat hunting and to evidence findings such as cleartext credentials.",
     ["Capture (authorised)", "Filter", "Conversations", "Follow stream", "Identify anomaly", "Document evidence"],
     ["Open the PCAP Analysis lab in the Labs centre", "Write filters for DNS traffic and for traffic to port 21", "Use Statistics > Conversations conceptually to find the top talker"],
     "Given a capture with 1,000 SYN packets from one IP to 1,000 different ports in 10 seconds, explain what happened and what the defender should do.",
     ["Capturing on networks without permission", "Confusing capture filters with display filters", "Changing several settings at once when troubleshooting"],
     "Captures show the truth. Filter, follow streams and look for patterns. Troubleshoot layer by layer and only with authorisation.",
     ["PCAP", "Wireshark", "Display filter", "Traceroute", "Netstat"],
     "Know the difference between capture (BPF) and display filters, and which command tests which layer.",
     [Q("Which Wireshark display filter shows only DNS traffic?", ["port 53", "dns", "udp.dns", "show dns"], 1, "'dns' is the display filter; 'port 53' is BPF capture syntax."),
      Q("Many SYN packets to different ports from one host with no completed handshakes most likely indicates...", ["A backup job", "A port scan", "A DNS outage", "A VPN"], 1, "This is the classic SYN scan pattern."),
      Q("Which command shows the path packets take to a destination?", ["ping", "traceroute / tracert", "nslookup", "arp"], 1, "Traceroute reveals each router hop.")]),
  ],
  ["Build a basic LAN diagram", "Assign IP addresses", "Calculate subnets", "Identify ports and protocols", "Simulate DNS resolution", "Observe safe sample packet captures",
   "Analyse a supplied PCAP file", "Identify suspicious patterns in a simulated network", "Configure a virtual network"],
  "p02",
  "Complete all 7 lessons, pass the module assessment (70%+), complete the Network Basics and PCAP labs.",
  ["Recite OSI layers and one protocol per layer", "Subnet three random /25-/29 networks", "List 15 common ports", "Explain DORA and the DNS lookup chain"],
  ["Network types", "Network devices", "Cables", "MAC", "IPv4", "IPv6", "Public/private", "Static/dynamic", "Subnetting", "CIDR", "Masks", "Gateways", "DNS", "DHCP", "ARP",
   "TCP", "UDP", "ICMP", "OSI", "TCP/IP", "Ports", "HTTP/HTTPS", "FTP", "SSH", "SMTP", "IMAP", "POP3", "Routing", "Switching", "VLANs", "NAT", "Firewalls", "VPNs", "Proxies",
   "Segmentation", "Packet analysis", "Wireshark", "Troubleshooting", "Network security"])
