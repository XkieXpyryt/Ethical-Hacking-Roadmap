from lib import *

M("m05", 5, "Cybersecurity Fundamentals", "shield",
  "This module gives you the vocabulary and mental models of the profession: the CIA triad, identity, cryptography, risk, controls, incident response and - most importantly - the ethics and law that separate an ethical hacker from a criminal.",
  ["Apply the CIA triad and AAA", "Explain encryption, hashing, signatures and PKI", "Assess threats, vulnerabilities and risk",
   "Choose controls using defence in depth, zero trust and least privilege", "Describe incident response phases", "Work within ethics, law, privacy and rules of engagement"],
  ["Modules 1-4"],
  [
   L("m05l1", "CIA Triad, AAA, Identity, MFA and Password Security", "Beginner", 30,
     "Every security decision protects confidentiality, integrity or availability - and almost every breach involves identity.",
     ["Define confidentiality, integrity and availability", "Explain authentication, authorisation and accounting", "Describe MFA and modern password guidance"],
     """The CIA triad lists what security protects: Confidentiality (only authorised people see data), Integrity (data is accurate and unaltered) and Availability (systems work when needed). AAA describes access control: Authentication proves who you are, Authorisation decides what you may do, and Accounting (auditing) records what you did. Multi-factor authentication (MFA) combines two or more factor types - something you know, have or are - so a stolen password alone is not enough.""",
     """Examples: ransomware attacks availability (and often confidentiality via data theft); a tampered bank transfer attacks integrity; a leaked database attacks confidentiality.

Identity management covers the lifecycle: joiner (create account with correct access), mover (adjust access when roles change), leaver (disable promptly). Orphaned accounts of former staff are a frequent finding.

Authentication factors: knowledge (password, PIN), possession (phone app, hardware key), inherence (fingerprint, face). Two passwords = still one factor.

MFA strength (strongest first): FIDO2/passkeys and hardware security keys (phishing-resistant) -> authenticator app with number matching -> app push -> SMS codes (vulnerable to SIM swap) . Watch for MFA fatigue attacks (spamming push requests).

Password guidance (NIST SP 800-63B, NCSC): favour length over complexity (passphrases), check new passwords against breached-password lists, do not force periodic changes without evidence of compromise, allow password managers and paste, rate-limit and lock out guessing. Store passwords only as salted, slow hashes (bcrypt, scrypt, Argon2).""",
     "CIA is a bank vault: only the right people get in (confidentiality), nobody can secretly change the ledger (integrity), and the bank opens on time (availability).",
     "Many account takeovers come from reused passwords stolen in other breaches. Organisations that enforced phishing-resistant MFA saw these attacks fail.",
     "Your findings will be described by their impact on CIA, and identity weaknesses are among the most common critical issues.",
     ["Identify (username)", "Authenticate (factors)", "Authorise (permissions)", "Account (logs)"],
     ["Classify five incidents by the CIA property they affect", "List the MFA methods you use and rank them", "Check a password idea in the in-app strength analyser (do not use real passwords)"],
     "Write a short password policy for a small business that follows NIST/NCSC guidance.",
     ["Thinking two passwords is MFA", "Forcing 90-day password changes by default", "Confusing authentication with authorisation"],
     "Protect C, I and A. Authenticate, authorise, account. Prefer phishing-resistant MFA and long passphrases stored as slow salted hashes.",
     ["CIA triad", "Authentication", "Authorisation", "MFA", "Passphrase"],
     "If asked which MFA is most phishing-resistant: FIDO2/hardware security keys.",
     [Q("A DDoS attack mainly affects...", ["Confidentiality", "Integrity", "Availability", "Accounting"], 2, "It makes services unavailable."),
      Q("Checking whether a logged-in user may delete a record is...", ["Authentication", "Authorisation", "Accounting", "Encryption"], 1, "Authorisation decides permitted actions."),
      Q("Which MFA method is most resistant to phishing?", ["SMS code", "Email code", "FIDO2 security key", "Security questions"], 2, "FIDO2 binds authentication to the genuine site.")]),

   L("m05l2", "Cryptography: Encryption, Hashing, Signatures, Certificates and PKI", "Intermediate", 45,
     "Cryptography protects data at rest and in transit. Testers routinely find weak or misused crypto.",
     ["Compare symmetric and asymmetric encryption", "Explain hashing and why it is not encryption", "Describe digital signatures, certificates and PKI"],
     """Encryption scrambles data so only someone with the key can read it. Symmetric encryption uses one shared key (AES) and is fast. Asymmetric encryption uses a key pair: a public key anyone can have and a private key you keep secret (RSA, elliptic curve). Hashing turns data into a fixed-length fingerprint that cannot be reversed (SHA-256); it proves integrity. A digital signature uses your private key to prove you created something. Certificates bind a public key to an identity, and a PKI (public key infrastructure) of trusted certificate authorities vouches for them.""",
     """Symmetric: AES-128/256 (use GCM mode for authenticated encryption), ChaCha20-Poly1305. Problem: securely sharing the key.
Asymmetric: RSA (2048+ bits), ECC (P-256, Curve25519). Slower; used to exchange symmetric keys and to sign.
TLS combines both: an asymmetric key exchange (ECDHE) agrees a symmetric session key, then AES/ChaCha20 encrypts the traffic.

Hashing: SHA-256/SHA-3 for integrity. MD5 and SHA-1 are broken for collision resistance - do not use them for security. Password storage needs SLOW salted hashes (bcrypt, scrypt, Argon2), not fast SHA-256.
Encoding (Base64) vs encryption (needs a key) vs hashing (one-way).

Digital signature: hash the message, sign the hash with the private key; anyone verifies with the public key. Provides integrity, authentication and non-repudiation.

Certificates (X.509): subject, issuer, validity dates, public key, signature of the CA. Chain of trust: root CA -> intermediate CA -> server certificate. Browsers trust roots in their trust store. Revocation via CRL/OCSP. Common findings: expired certificates, self-signed certificates on public services, weak protocols (SSLv3, TLS 1.0/1.1), weak ciphers.

Future: post-quantum algorithms (ML-KEM, ML-DSA standardised by NIST in 2024) are being rolled out to replace RSA/ECC for long-term security.""",
     "Symmetric crypto is one key for a padlock both people copy; asymmetric is a post box: anyone can drop a letter in (public key), only you can open it (private key).",
     "A tester found an internal portal still accepting TLS 1.0 with weak ciphers and a certificate that expired 14 months earlier - two findings in one service.",
     "You will assess TLS configurations, password storage, signatures and certificate hygiene.",
     ["Client hello", "Server certificate", "Key exchange (ECDHE)", "Shared session key", "AES-GCM encrypted data"],
     ["Hash the same text twice in the in-app Hash Lab and then change one character", "Inspect a website certificate chain in your browser", "Explain which algorithm you would use for files, passwords and signatures"],
     "A developer stores passwords as unsalted SHA-256. Write the finding and recommended fix.",
     ["Calling hashing 'encryption'", "Using MD5/SHA-1 for security", "Storing passwords with fast hashes"],
     "Symmetric for speed, asymmetric for key exchange and signatures, hashing for integrity, slow salted hashes for passwords, PKI for trust.",
     ["AES", "RSA", "Hash", "Digital signature", "Certificate", "PKI", "TLS"],
     "A classic question: which key signs (private) and which verifies (public)?",
     [Q("Which is a symmetric algorithm?", ["RSA", "AES", "ECDSA", "Diffie-Hellman"], 1, "AES uses one shared key."),
      Q("To create a digital signature you use...", ["The recipient's public key", "Your private key", "A shared password", "MD5"], 1, "Sign with your private key; others verify with your public key."),
      Q("Which is the best choice for storing user passwords?", ["MD5", "SHA-256 unsalted", "Argon2 or bcrypt", "Base64"], 2, "Slow, salted password hashing functions.")]),

   L("m05l3", "Threats, Vulnerabilities, Exploits, Risk and Attack Surface", "Beginner", 30,
     "Clear terminology makes your reports credible and your risk ratings defensible.",
     ["Distinguish threat, vulnerability, exploit and risk", "Calculate risk from likelihood and impact", "Map an attack surface"],
     """A vulnerability is a weakness (an unpatched server). A threat is something that could exploit it (a criminal group). An exploit is the method or code that takes advantage of the weakness. Risk is the chance and consequence of harm: commonly risk = likelihood x impact. The attack surface is everything an attacker could interact with: open ports, web forms, APIs, email, staff and suppliers.""",
     """Threat actors: cybercriminals (financial), nation-state groups (espionage), hacktivists (ideology), insiders (malicious or careless), script kiddies. Threat vectors: phishing, exposed services, stolen credentials, supply chain, removable media, misconfiguration.

Risk calculation example (1-5 scales): internet-facing VPN with a critical known vulnerability: likelihood 5, impact 5 = 25 (critical). Internal printer with default password: likelihood 3, impact 2 = 6 (low-medium).

Risk treatment: mitigate (apply controls), transfer (insurance, contracts), avoid (stop the activity), accept (documented, by an accountable owner).

Attack surface categories: digital (servers, apps, APIs, cloud), physical (offices, devices), human (staff, social engineering), third-party (vendors, libraries). Reducing attack surface - removing unused services, accounts and exposure - is one of the most effective controls.

Frameworks: MITRE ATT&CK catalogues real adversary tactics and techniques; the Cyber Kill Chain describes attack stages.""",
     "A vulnerability is an unlocked window; the threat is a burglar in the area; the exploit is climbing through the window; risk is how likely and how bad a burglary would be.",
     "Two organisations with the same vulnerability had different risk: one exposed it to the internet, the other only to an isolated lab.",
     "Reports rate findings by risk, not just by how clever the technique was.",
     ["Asset", "Vulnerability", "Threat", "Likelihood x Impact", "Risk rating", "Treatment"],
     ["Classify five statements as threat, vulnerability, exploit or risk", "Rate three scenarios with a 5x5 matrix", "List your home's digital attack surface"],
     "Build a risk register with five risks for a small online shop (see Practice Centre template).",
     ["Using 'threat' and 'vulnerability' interchangeably", "Rating severity without considering exposure", "Accepting risk without an owner"],
     "Weakness + threat + exploit = risk. Rate by likelihood and impact, treat by mitigate/transfer/avoid/accept, and shrink the attack surface.",
     ["Threat", "Vulnerability", "Exploit", "Risk", "Attack surface"],
     "Know the four risk treatments and who may accept risk (an accountable owner).",
     [Q("An unpatched web server is a...", ["Threat", "Vulnerability", "Exploit", "Control"], 1, "It is a weakness."),
      Q("Buying cyber insurance is which risk treatment?", ["Avoid", "Accept", "Transfer", "Mitigate"], 2, "Insurance transfers financial impact."),
      Q("Which framework catalogues adversary tactics and techniques?", ["OSI", "MITRE ATT&CK", "PCI DSS", "ITIL"], 1, "MITRE ATT&CK.")]),

   L("m05l4", "Security Controls, Defence in Depth, Zero Trust, Policies and Governance", "Intermediate", 35,
     "Recommendations in your reports are controls. Knowing control types lets you recommend fixes that actually fit.",
     ["Classify controls by type and function", "Apply defence in depth, zero trust and least privilege", "Explain policies, standards and governance"],
     """Security controls reduce risk. They can be technical (firewalls, MFA), administrative (policies, training) or physical (locks, badges), and they can prevent, detect or correct problems. Defence in depth layers several controls so one failure does not cause a breach. Zero trust means never trusting a request just because it comes from inside the network - always verify identity, device and context. Least privilege means giving only the access needed. Policies and governance set the rules and accountability that keep this consistent.""",
     """Control functions: preventive (MFA, firewall), detective (IDS, logs, CCTV), corrective (backups restore, patching), deterrent (warning banners), compensating (extra monitoring when a fix is impossible).

Defence in depth example for email: SPF/DKIM/DMARC -> email filtering -> link sandboxing -> user training -> endpoint protection -> MFA -> least privilege -> monitoring -> backups.

Zero trust principles (NIST SP 800-207): verify explicitly, least-privilege access, assume breach; enforce per-request decisions using identity, device health and context; micro-segment.

Secure configuration: baselines such as CIS Benchmarks; remove defaults; configuration management to prevent drift.

Policy hierarchy: policy (high-level intent, e.g. 'all access requires MFA') -> standards (mandatory specifics) -> procedures (step-by-step) -> guidelines (recommended).

Governance: roles and accountability (board, CISO, asset owners), frameworks (NIST CSF 2.0 functions: Govern, Identify, Protect, Detect, Respond, Recover; ISO/IEC 27001), metrics and audits.""",
     "Defence in depth is a castle: moat, wall, gate, guards and a keep - attackers must beat all of them.",
     "An organisation without MFA but with good logging detected a credential-stuffing attack (detective control) and then deployed MFA (preventive) - showing why both types matter.",
     "Your remediation advice must name practical controls, and many audits map findings to NIST CSF or ISO 27001.",
     ["Govern", "Identify", "Protect", "Detect", "Respond", "Recover"],
     ["Classify ten controls as preventive, detective or corrective", "Design a five-layer defence for a laptop", "Write a one-line policy and a matching standard"],
     "Write an access-control policy for a fictional clinic using least privilege and MFA (Practice Centre task).",
     ["Relying on a single control", "Trusting all internal traffic", "Writing policies nobody can follow"],
     "Layer preventive, detective and corrective controls; verify everything (zero trust); grant minimal access; govern with policies and frameworks.",
     ["Defence in depth", "Zero trust", "Least privilege", "Preventive control", "NIST CSF"],
     "NIST CSF 2.0 added 'Govern' as a sixth function - expect it in modern exams.",
     [Q("A backup restore is which type of control?", ["Preventive", "Detective", "Corrective", "Deterrent"], 2, "It corrects after an incident."),
      Q("Which principle says never trust based on network location?", ["Defence in depth", "Zero trust", "Separation of duties", "Security through obscurity"], 1, "Zero trust verifies every request."),
      Q("Which NIST CSF 2.0 function was newly added?", ["Detect", "Govern", "Recover", "Protect"], 1, "Govern was added in CSF 2.0 (2024).")]),

   L("m05l5", "Incident Response, Awareness, Ethics, Law, Privacy and Rules of Engagement", "Beginner", 40,
     "Ethics and authorisation are what make hacking legal. This lesson is the most important in the course.",
     ["Describe the incident response lifecycle", "Explain the legal and ethical boundaries of security testing", "Define rules of engagement and scope"],
     """Incident response is the organised way to handle security incidents: prepare, detect and analyse, contain, eradicate, recover and learn lessons. Security awareness trains people to spot phishing and report problems. Ethical hacking is only legal with explicit, written authorisation from the system owner. Laws such as the UK Computer Misuse Act 1990 and the US Computer Fraud and Abuse Act make unauthorised access a crime, even if you 'meant well'. Privacy laws like UK/EU GDPR govern how personal data is handled - including any you see during testing.""",
     """Incident response (NIST SP 800-61): Preparation -> Detection & Analysis -> Containment, Eradication & Recovery -> Post-Incident Activity (lessons learned). Preserve evidence and keep a timeline.

Legal essentials (informational, not legal advice):
• UK Computer Misuse Act 1990: unauthorised access, access with intent to commit further offences, unauthorised acts impairing operation, and making/supplying tools for these offences.
• US CFAA; EU directive on attacks against information systems; Nigeria Cybercrimes Act 2015; Ghana Cybersecurity Act 2020; similar laws worldwide.
• GDPR: lawful basis, data minimisation, security of processing, breach notification (72 hours to the regulator where required).

Rules of engagement (RoE) define: scope (in-scope IPs, domains, apps), out-of-scope systems, testing windows, permitted techniques (e.g. no denial of service, no social engineering unless agreed), data handling, emergency contacts and stop conditions. Authorisation must come from someone who actually has authority over the systems - not just any employee. Cloud and third-party hosted systems may need the provider's rules too.

Professional ethics (e.g. (ISC)2 and EC-Council codes): act honourably and legally, protect confidentiality, avoid harm, report honestly, disclose responsibly.""",
     "Testing without authorisation is like a locksmith breaking into houses 'to show they are insecure' - it is still burglary.",
     "Security researchers have faced prosecution for accessing systems beyond what they were permitted, even with good intentions. Clear written scope protects everyone.",
     "No authorisation, no test. Every lab and project in this app enforces scope, and your capstone is scored on scope compliance.",
     ["Preparation", "Detection & Analysis", "Containment", "Eradication", "Recovery", "Lessons learned"],
     ["List the six items every rules-of-engagement document should contain", "Read the Lab Rules in the Labs centre", "Identify one cybercrime law in your country"],
     "A friend asks you to 'check' their employer's website for vulnerabilities. Write your reply explaining what authorisation would be needed.",
     ["Testing 'just a little' outside scope", "Accepting verbal permission", "Keeping client data after an engagement"],
     "Respond to incidents in phases, train people, and test only with written authorisation, clear scope and respect for privacy law.",
     ["Incident response", "Rules of engagement", "Scope", "Computer Misuse Act", "GDPR"],
     "Any exam scenario without written authorisation has one correct answer: do not test.",
     [Q("What is the first phase of the NIST incident response lifecycle?", ["Containment", "Preparation", "Eradication", "Lessons learned"], 1, "Preparation comes first."),
      Q("Before testing a system you need...", ["A fast internet connection", "Written authorisation from the system owner", "A Kali VM only", "Verbal agreement from any employee"], 1, "Written authorisation from someone with authority."),
      TF("Good intentions make unauthorised access legal.", False, "Intent does not create authorisation; unauthorised access is an offence.")]),
  ],
  ["Identify risks", "Classify assets", "Build a threat model", "Assess a mock business", "Create an access-control policy", "Review sample security configurations",
   "Analyse sample password-strength indicators", "Compare hashing and encryption", "Develop a risk register"],
  "p06",
  "Complete all 5 lessons and pass the module assessment with 70% or more. The ethics lesson quiz must be passed before Module 6 unlocks.",
  ["Explain CIA with your own examples", "Draw a TLS handshake", "Write the four risk treatments", "Recite the contents of rules of engagement"],
  ["CIA triad", "Authentication", "Authorisation", "Accounting", "Identity management", "MFA", "Password security", "Encryption", "Symmetric", "Asymmetric", "Hashing",
   "Digital signatures", "Certificates", "PKI", "Threats", "Vulnerabilities", "Exploits", "Risk", "Attack surface", "Controls", "Defence in depth", "Zero trust",
   "Least privilege", "Secure configuration", "Policies", "Governance", "Incident response", "Awareness", "Ethics", "Law", "Privacy", "Rules of engagement"])
