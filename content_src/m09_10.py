from lib import *

def vuln_detail(what, why, recognise, impact, demo, doc, fix, verify, prevent):
    return ("WHAT IT IS: " + what + "\n\nWHY IT HAPPENS: " + why + "\n\nHOW TO RECOGNISE IT: " + recognise +
            "\n\nIMPACT: " + impact + "\n\nSAFE DEMONSTRATION (lab only): " + demo + "\n\nHOW TO DOCUMENT IT: " + doc +
            "\n\nHOW DEVELOPERS FIX IT: " + fix + "\n\nHOW TO VERIFY THE FIX: " + verify + "\n\nPREVENTION: " + prevent)

M("m09", 9, "Web Application Hacking", "web",
  "Web applications are the most exposed part of most organisations. This academy module covers how the web works and the major vulnerability classes, each with what it is, why it happens, how to recognise it, safe lab demonstration, documentation, fixes and verification. All practice uses the built-in simulated 'Silverline Portal' or local deliberately vulnerable apps such as OWASP Juice Shop.",
  ["Explain HTTP, cookies, sessions and web architecture", "Test authentication, sessions and access control", "Identify and explain injection and XSS flaws",
   "Recognise CSRF, SSRF, file upload, traversal and misconfiguration issues", "Assess APIs and business logic", "Use Burp Suite, OWASP ZAP and developer tools in a lab"],
  ["Module 4 (JavaScript, SQL)", "Module 8"],
  [
   L("m09l1", "How the Web Works: HTTP, Headers, Cookies, Sessions and Architecture", "Intermediate", 40,
     "Every web vulnerability is a misuse of these building blocks.",
     ["Read raw HTTP requests and responses", "Explain cookies and session management", "Describe front-end/back-end architecture"],
     """A browser sends an HTTP request (method, path, headers, optional body) and the server returns a response (status code, headers, body). HTTPS wraps HTTP in TLS encryption. HTTP is stateless, so sites use cookies holding a session ID to remember who you are after you log in. Modern web apps have a front end (HTML, CSS, JavaScript in the browser), a back end (application server and business logic) and a database, often connected through APIs that exchange JSON.""",
     """Raw request:
POST /login HTTP/1.1
Host: portal.silverline.test
Content-Type: application/x-www-form-urlencoded
Cookie: lang=en

username=alice&password=********

Raw response:
HTTP/1.1 302 Found
Set-Cookie: SESSIONID=8f3c...; HttpOnly; Secure; SameSite=Lax
Location: /dashboard

Methods: GET (read), POST (create/submit), PUT/PATCH (update), DELETE, OPTIONS (CORS preflight). Important headers: Host, Cookie, Authorization, Content-Type, User-Agent, Referer, Origin; response security headers: Content-Security-Policy, Strict-Transport-Security, X-Content-Type-Options, X-Frame-Options/frame-ancestors, Referrer-Policy.

Sessions: the session ID must be long, random, sent only over HTTPS, HttpOnly, regenerated after login (prevents fixation) and invalidated on logout/timeout. Tokens such as JWTs carry signed claims - they must be verified server-side and must not use 'alg: none'.

Architecture: browser -> CDN/WAF -> load balancer -> web/app servers -> APIs/microservices -> databases, caches, object storage. Each hop is a trust boundary where input must be validated.""",
     "HTTP is a series of separate letters; the session cookie is a cloakroom ticket that lets the server remember which coat (account) is yours.",
     "A site that did not set the Secure flag sent session cookies over HTTP on public Wi-Fi, where they could be captured and reused.",
     "Burp and ZAP show exactly these requests; reading them fluently is the base skill of web testing.",
     ["Browser", "HTTPS request", "WAF/load balancer", "App server (logic)", "Database", "Response + Set-Cookie"],
     ["Open the Web lab and inspect a raw login request", "List the security headers missing from the sample response", "Explain what each cookie flag does"],
     "Explain session fixation and how regenerating the session ID after login prevents it.",
     ["Assuming HTTPS alone makes an app secure", "Storing session tokens in localStorage without considering XSS", "Forgetting HTTP is stateless"],
     "Requests and responses carry everything; sessions bridge statelessness; every architectural hop is a trust boundary.",
     ["HTTP", "HTTPS", "Cookie", "Session", "Security headers", "JWT"],
     "Know what HSTS (Strict-Transport-Security) does: forces browsers to use HTTPS for the domain.",
     [Q("Which flag stops a cookie being sent over plain HTTP?", ["HttpOnly", "Secure", "SameSite", "Domain"], 1, "Secure = HTTPS only."),
      Q("Why regenerate the session ID after login?", ["Speed", "Prevent session fixation", "Reduce cookie size", "Enable caching"], 1, "An attacker-planted pre-login ID becomes useless."),
      Q("Which header tells browsers to always use HTTPS for a site?", ["Content-Security-Policy", "Strict-Transport-Security", "X-Frame-Options", "Referrer-Policy"], 1, "HSTS.")]),

   L("m09l2", "Authentication, Sessions, Broken Access Control and IDOR", "Intermediate", 45,
     "Broken access control is ranked number one in the OWASP Top 10 (2021).",
     ["Assess authentication weaknesses", "Identify session weaknesses", "Test for broken access control and IDOR in a lab"],
     """Authentication weaknesses let attackers log in as someone else: no rate limiting, default credentials, weak password reset or missing MFA. Session weaknesses let them hijack logged-in users. Broken access control means users can do or see things they should not, such as a normal user opening an admin page. IDOR (Insecure Direct Object Reference) is a common form: changing /invoice?id=1001 to id=1002 shows someone else's invoice because the server never checks ownership.""",
     vuln_detail(
       "Access control failures let a user act outside their permissions: horizontal (another user's data) or vertical (admin functions).",
       "Developers rely on hiding links or on the client, or forget to check ownership on every request and API endpoint.",
       "Change object IDs in URLs, bodies and API paths; replay admin requests with a low-privilege session; check whether hidden buttons' endpoints still work.",
       "Data exposure of other customers, privilege escalation to admin, unauthorised changes - often critical.",
       "In the Silverline Portal lab, log in as test user 'learner', request /api/invoices/1001 (yours) then /api/invoices/1002 (belongs to fictional user 'bob') and observe the server returns it.",
       "Title, affected endpoint, roles tested, request/response evidence with personal data redacted, impact in business terms, CVSS.",
       "Enforce authorisation server-side on every request using the session identity; deny by default; check object ownership; use indirect references or UUIDs as defence in depth, not as the fix.",
       "Repeat the same requests with the low-privilege session after the fix; expect 403/404 and no data.",
       "Centralised authorisation middleware, automated tests per role, logging of access-control failures.") +
     "\n\nAuthentication checks: lockout/rate limiting, MFA availability, password policy, secure reset (single-use, short-lived tokens), username enumeration via different error messages, credentials only over HTTPS.",
     "Access control is a hotel key card: the door must check the card against the room every time, not just trust that guests stay on their own floor.",
     "A large telecoms breach involved an API that returned customer records for any sequential ID without checking authorisation.",
     "IDOR and privilege escalation are among the most frequent high and critical web findings.",
     ["Log in as user A", "Capture request for own object", "Change ID to user B's", "Server checks ownership?", "Data returned = IDOR"],
     ["Complete the IDOR task in the Web Security lab", "List three places object IDs can appear", "Write the fix in one sentence"],
     "A low-privilege user can call POST /admin/users/delete because the button is only hidden. Write the finding.",
     ["Testing with real customer accounts instead of test accounts", "Assuming random IDs are a fix", "Only testing the UI, not the API"],
     "Check authorisation on every request. Test horizontally (other users) and vertically (admin functions) with your own test accounts.",
     ["Broken access control", "IDOR", "Privilege escalation", "Username enumeration"],
     "Broken Access Control is A01 in the OWASP Top 10 2021.",
     [Q("Changing an order ID in a URL to view another customer's order is...", ["XSS", "IDOR", "CSRF", "SSRF"], 1, "Insecure Direct Object Reference."),
      Q("Where must access control be enforced?", ["In the browser", "Server-side on every request", "Only on the login page", "In CSS"], 1, "Client-side checks can be bypassed."),
      Q("A login page that says 'unknown user' vs 'wrong password' enables...", ["SQL injection", "Username enumeration", "Clickjacking", "SSRF"], 1, "Different messages reveal valid usernames.")]),

   L("m09l3", "SQL Injection in Depth", "Intermediate", 45,
     "Injection remains in the OWASP Top 10 and can expose entire databases.",
     ["Recognise SQL injection points and behaviour", "Explain in-band, blind and error-based SQLi conceptually", "Fix and verify with parameterised queries"],
     """SQL injection happens when user input is inserted into a database query as code. Signs include database errors after typing a quote, or results changing when you add logic like AND 1=1 versus AND 1=2. In the lab you will see how a vulnerable login can be bypassed and how the secure version, using parameterised queries, treats the same input as harmless text.""",
     vuln_detail(
       "Untrusted input changes the structure of an SQL query.",
       "Queries are built by string concatenation or unsafe ORM raw queries; input is not parameterised.",
       "Error messages after a single quote; differing responses to true/false conditions; time delays; values reflected from UNION queries (lab).",
       "Authentication bypass, reading or modifying any data, sometimes server compromise - typically high or critical.",
       "In the Silverline Portal lab /v1/login, entering a classic tautology as the username shows 'Logged in as admin'. The same input on /v2/login (parameterised) returns 'Invalid credentials'. Types: in-band (error/UNION), blind (boolean/time), out-of-band.",
       "Parameter, endpoint, payload type (described, not a weaponised dump), evidence of behaviour difference, data types exposed (redacted), CVSS, CWE-89.",
       "Parameterised queries/prepared statements everywhere; safe ORM methods; least-privilege DB user; generic error pages; input validation as defence in depth.",
       "Repeat the original test cases on the fixed endpoint; confirm identical behaviour for true/false conditions and no errors.",
       "Secure coding training, SAST rules for string-built SQL, code review, DAST in CI.") +
     "\n\nTools such as sqlmap automate detection and exploitation - only ever use them against lab targets or with explicit authorisation, because they can modify or extract large amounts of data.",
     "SQL injection is like a form that says 'Pay ___ pounds' where someone writes '10 to me, and also pay 1,000,000 to me' - and the bank clerk executes it as written.",
     "In real breaches, a single injectable search parameter exposed millions of customer records.",
     "SQLi tests appear in every web assessment and in exams such as eJPT, OSCP and BSCP.",
     ["Input field", "Concatenated query", "Attacker input changes logic", "Database executes", "Unauthorised data/action"],
     ["Complete the SQL Injection task in the Web Security lab (v1 vs v2)", "Explain why the v2 endpoint is safe", "Write the remediation for a developer"],
     "Describe how you would safely demonstrate SQLi impact to a client without extracting real personal data.",
     ["Running automated SQLi tools against production without approval", "Recommending 'escape quotes' instead of parameterisation", "Dumping real data as 'proof'"],
     "SQLi = input becomes code. Recognise behaviour changes, demonstrate minimally, fix with parameterised queries and verify.",
     ["SQL injection", "Blind SQL injection", "UNION", "Prepared statement", "CWE-89"],
     "Blind SQLi reveals no data directly - inference comes from true/false or time differences.",
     [Q("Which SQLi type infers data from response delays?", ["Error-based", "Union-based", "Time-based blind", "Stored"], 2, "Time-based blind uses delays."),
      Q("The correct primary fix for SQLi is...", ["Input length limits", "Parameterised queries", "Hiding the login page", "Using POST instead of GET"], 1, "Prepared statements."),
      Q("Which CWE identifies SQL injection?", ["CWE-79", "CWE-89", "CWE-352", "CWE-918"], 1, "CWE-89. (79 = XSS, 352 = CSRF, 918 = SSRF)")]),

   L("m09l4", "Cross-Site Scripting (Reflected, Stored, DOM), CSRF and Security Headers", "Intermediate", 45,
     "XSS lets attackers run script in victims' browsers; CSRF makes victims perform actions unknowingly.",
     ["Distinguish reflected, stored and DOM XSS", "Explain CSRF and its defences", "Use output encoding, CSP and headers as mitigations"],
     """Cross-site scripting (XSS) happens when a site includes untrusted input in a page without encoding it, so the browser runs it as script. Reflected XSS comes back immediately from the request (e.g. a search term). Stored XSS is saved (e.g. a comment) and hits every visitor. DOM XSS happens entirely in client-side JavaScript. Cross-site request forgery (CSRF) tricks a logged-in user's browser into sending an unwanted request, like changing their email.""",
     vuln_detail(
       "XSS: attacker-controlled script executes in another user's browser in the site's origin. CSRF: forged state-changing requests ride on the victim's cookies.",
       "XSS: missing context-aware output encoding, innerHTML, unsafe templates. CSRF: state-changing requests rely only on cookies with no anti-CSRF token or SameSite protection.",
       "XSS: input reflected unencoded in HTML, attributes or scripts; in the lab a harmless test string like <b>test</b> rendering bold. CSRF: POST forms with no token and cookies without SameSite.",
       "Session/account takeover, actions as the victim, defacement, credential prompts; CSRF can change passwords or emails.",
       "In the lab comment board (v1), a harmless marker like <i>xss-test</i> renders in italics for every viewer (stored). In v2 it displays as text because output is encoded. For CSRF, the lab shows an email-change form without a token and the fixed version with a token.",
       "Context (HTML body, attribute, JS), XSS type, affected page/parameter, harmless proof (e.g. alert of document.domain in lab), impact, CWE-79/CWE-352.",
       "XSS: context-aware output encoding, framework auto-escaping, textContent, sanitise rich HTML with a vetted library, CSP. CSRF: anti-CSRF tokens, SameSite=Lax/Strict cookies, re-authentication for sensitive actions, check Origin.",
       "Re-test the same inputs; confirm they render as text and that CSP blocks inline script; confirm forged requests are rejected.",
       "Secure templates, CSP in report-only then enforce, security headers, developer training.") +
     "\n\nSecurity headers: Content-Security-Policy, X-Content-Type-Options: nosniff, frame-ancestors / X-Frame-Options (clickjacking), Referrer-Policy, Permissions-Policy, HSTS.",
     "XSS is graffiti that turns into instructions for everyone who reads the wall; CSRF is someone forging your signature on a form while you are logged in at the bank.",
     "Stored XSS in a support ticket system let attackers hijack help-desk agents' sessions when they opened tickets.",
     "XSS (A03 injection category) and CSRF are core findings and core BSCP/OSCP knowledge.",
     ["Attacker input stored", "Victim views page", "Unencoded output", "Browser executes script", "Session/actions abused"],
     ["Complete the XSS and CSRF tasks in the Web Security lab", "Classify five scenarios as reflected, stored or DOM", "Write a basic CSP header"],
     "A profile 'bio' field accepts HTML. Recommend a fix that still allows bold and italics.",
     ["Relying on input blacklists like removing '<script>'", "Using HttpOnly as an XSS 'fix' (it only limits cookie theft)", "Forgetting GET requests must never change state"],
     "Encode output by context, use CSP and safe DOM APIs for XSS; tokens and SameSite for CSRF; verify both.",
     ["XSS", "Stored XSS", "DOM XSS", "CSRF", "Output encoding", "CSP"],
     "Stored XSS is usually higher severity than reflected because it affects every visitor without a crafted link.",
     [Q("XSS saved in a database and shown to all visitors is...", ["Reflected", "Stored", "DOM-based", "Blind SQLi"], 1, "Stored (persistent) XSS."),
      Q("Which is the primary defence against XSS?", ["Context-aware output encoding", "HTTPS", "Strong passwords", "Rate limiting"], 0, "Encode untrusted data for its output context."),
      Q("Which control helps prevent CSRF?", ["Anti-CSRF tokens and SameSite cookies", "Longer passwords", "Disabling HTTPS", "Bigger session IDs"], 0, "Tokens and SameSite cookies.")]),

   L("m09l5", "SSRF, File Uploads, Directory Traversal, Deserialization, Misconfiguration and Information Disclosure", "Advanced", 50,
     "These server-side flaws often lead to internal network access or full server compromise.",
     ["Explain SSRF and its cloud impact", "Assess file upload and traversal risks", "Recognise deserialization, misconfiguration and disclosure issues"],
     """Server-side request forgery (SSRF) tricks a server into making requests to places the attacker chooses, such as internal services or cloud metadata endpoints. Insecure file uploads let attackers upload dangerous files. Directory (path) traversal uses sequences like ../ to read files outside the intended folder. Insecure deserialization turns attacker-controlled data into objects in dangerous ways. Misconfigurations and information disclosure (stack traces, debug pages, backup files) hand attackers useful details.""",
     """SSRF - why: features that fetch URLs (webhooks, image-from-URL, PDF generators) without restricting destinations. Recognise: a parameter containing a URL; in the lab, url=http://127.0.0.1:8080/admin returns internal content. Impact: internal scanning, reaching admin services, cloud credential theft via metadata (AWS IMDSv1). Fix: allow-list destinations, block internal/link-local ranges after DNS resolution, disable redirects, use IMDSv2, network egress controls.

File upload - why: trusting extension/Content-Type, storing uploads in the web root. Recognise: upload features that serve files back from the same domain. Fix: allow-list types, verify content (magic bytes), rename files, store outside the web root or in object storage, set Content-Disposition, scan with antivirus, size limits.

Directory traversal - why: building file paths from input. Recognise: parameters like file=report.pdf; in the lab file=../../etc/hostname returns the simulated host file. Fix: map IDs to files, canonicalise and check the resolved path stays in the base directory.

Insecure deserialization (concept): native object formats (Java serialization, Python pickle, PHP unserialize) on untrusted data can trigger code execution. Fix: use simple data formats like JSON with schema validation, never deserialize untrusted native objects, sign data.

Misconfiguration & disclosure: directory listing, default pages, debug mode, verbose errors, exposed .git or backup files, unnecessary HTTP methods, outdated components (OWASP A05/A06). Fix: hardened baselines, remove samples, generic error pages, automated config checks.

Document each with endpoint, parameter, harmless evidence, impact, CWE (918 SSRF, 434 upload, 22 traversal, 502 deserialization, 200 exposure) and verification steps.""",
     "SSRF is persuading the company receptionist to phone an internal extension for you; traversal is asking for 'the file in the cupboard behind the cupboard'.",
     "A major cloud breach involved SSRF against a misconfigured WAF that let the attacker reach the instance metadata service and obtain credentials.",
     "These flaws commonly chain into the highest-impact findings, especially in cloud-hosted apps.",
     ["URL parameter", "Server fetches", "Internal target (127.0.0.1 / metadata)", "Response leaks", "Fix: allow-list + egress control"],
     ["Complete the SSRF and traversal tasks in the Web Security lab", "List five checks for a secure upload feature", "Map each flaw to its CWE"],
     "An app converts a user-supplied URL into a PDF. Write three tests and three defences.",
     ["Blocking only the string '127.0.0.1' (alternative encodings and DNS bypass it)", "Trusting Content-Type for uploads", "Leaving debug mode on in production"],
     "Restrict server-side fetches, uploads and file paths with allow-lists; avoid native deserialization; harden configuration and hide internals.",
     ["SSRF", "Directory traversal", "File upload", "Deserialization", "Information disclosure"],
     "IMDSv2 on AWS requires a session token, which mitigates many SSRF metadata attacks.",
     [Q("Which flaw makes the server request an attacker-chosen URL?", ["CSRF", "SSRF", "XSS", "IDOR"], 1, "Server-side request forgery."),
      Q("'../' sequences in a file parameter suggest testing for...", ["SQLi", "Directory traversal", "CSRF", "Clickjacking"], 1, "Path traversal."),
      Q("The safest way to store user uploads is...", ["In the web root with original names", "Outside the web root, renamed, type-validated", "In the database as executable", "Anywhere with Content-Type check only"], 1, "Rename, validate and store outside the web root.")]),

   L("m09l6", "APIs: REST, JSON, API Vulnerabilities, Rate Limiting and Business Logic", "Advanced", 45,
     "APIs power mobile apps and modern sites; they expose data directly and are frequently under-protected.",
     ["Test REST APIs methodically", "Apply the OWASP API Security Top 10", "Find rate limiting and business logic flaws"],
     """APIs let programs talk to each other. REST APIs use URLs for resources (/api/v1/users/42) and HTTP methods for actions, usually exchanging JSON. API flaws include broken object-level authorisation (BOLA - the API version of IDOR), excessive data exposure, missing rate limiting and mass assignment. Business logic flaws abuse the intended workflow - like applying a discount code twice or buying a negative quantity - and scanners rarely find them.""",
     """OWASP API Security Top 10 (2023) highlights: API1 Broken Object Level Authorization; API2 Broken Authentication; API3 Broken Object Property Level Authorization (excessive exposure + mass assignment); API4 Unrestricted Resource Consumption (rate limits); API5 Broken Function Level Authorization; API6 Unrestricted Access to Sensitive Business Flows; API7 SSRF; API8 Security Misconfiguration; API9 Improper Inventory Management (old v1 endpoints); API10 Unsafe Consumption of APIs.

Testing approach (lab): obtain documentation (OpenAPI/Swagger) -> map endpoints and roles -> test each endpoint with each role -> change IDs (BOLA) -> add unexpected JSON fields like "role":"admin" (mass assignment) -> check responses for extra fields -> send repeated requests to check rate limits (lab only, small volumes) -> test old versions.

Business logic examples: negative quantities, skipping workflow steps (paying after shipping), race conditions on gift cards, unlimited coupon reuse.

Secure API design: authenticate every call (OAuth 2.0/OIDC), authorise per object and function, return only needed fields (response schemas), validate input schemas, rate limit and set quotas, version and retire old APIs, log and monitor.""",
     "An API is a vending machine for data; BOLA is pressing someone else's button number and getting their snack; mass assignment is slipping in a request for 'free' and the machine obeying.",
     "A fitness app API returned every field of a user object, including private location data, though the app only displayed a name - excessive data exposure.",
     "API testing is a fast-growing specialty and features heavily in modern penetration tests and bug bounty programmes.",
     ["Read OpenAPI spec", "Map endpoints x roles", "Test BOLA/BFLA", "Test mass assignment", "Check rate limits", "Check logic flows"],
     ["Complete Project 8 (API security) using the supplied mock API", "Find the extra field the lab API returns", "Write a rate-limiting recommendation"],
     "Design three business logic test cases for an online voucher system.",
     ["High-volume rate-limit tests against production", "Only testing documented endpoints", "Ignoring old API versions"],
     "Test every endpoint for every role; watch for BOLA, excessive data, mass assignment, rate limits and logic abuse.",
     ["API", "REST", "BOLA", "Mass assignment", "Rate limiting", "Business logic flaw"],
     "BOLA is the number one API risk in the OWASP API Top 10 (2023).",
     [Q("Which is the top risk in the OWASP API Security Top 10 2023?", ["Injection", "Broken Object Level Authorization", "XSS", "Logging failures"], 1, "API1:2023 BOLA."),
      Q("Adding \"isAdmin\": true to a JSON profile update and having it accepted is...", ["Mass assignment", "SSRF", "CSRF", "Clickjacking"], 0, "Mass assignment."),
      Q("Which flaw are automated scanners worst at finding?", ["Missing headers", "Business logic flaws", "Outdated TLS", "Open ports"], 1, "Logic flaws require understanding the workflow.")]),

   L("m09l7", "Web Testing Tools and Methodology: Burp Suite, OWASP ZAP, DevTools and OWASP WSTG", "Intermediate", 40,
     "Tools amplify methodology; methodology ensures coverage.",
     ["Describe intercepting proxies", "Use browser devtools and API clients", "Follow the OWASP Web Security Testing Guide structure"],
     """An intercepting proxy sits between your browser and the target so you can see and modify every request. Burp Suite (free Community Edition; paid Professional) and OWASP ZAP (free, open source) are the standard tools. Browser developer tools show the DOM, network requests, storage and console. API clients like Postman or the open-source Bruno and Insomnia send crafted API requests. The OWASP Web Security Testing Guide (WSTG) gives a checklist so no area is missed.""",
     """Burp Suite components: Proxy (intercept/history), Repeater (resend and modify single requests), Intruder (automated variations - rate-limited in Community), Decoder, Comparer, Target site map. Pro adds the Scanner. ZAP offers Proxy, Spider/AJAX Spider, Active/Passive Scan, Fuzzer, and automation via scripts and Docker.

Set-up (lab): configure the browser to use 127.0.0.1:8080, install the tool's CA certificate in a separate testing browser profile only, set scope to the lab domain so out-of-scope traffic is not recorded.

WSTG categories: Information Gathering, Configuration & Deployment, Identity Management, Authentication, Authorization, Session Management, Input Validation, Error Handling, Cryptography, Business Logic, Client-side, API testing.

Methodology: map the app (all features, roles, endpoints) -> test configuration -> authentication -> session -> authorisation -> input handling -> logic -> client-side -> report. Keep a test log; capture request/response evidence; remove sensitive data before reporting.

Practice platforms: PortSwigger Web Security Academy (free labs), OWASP Juice Shop (run locally), DVWA, TryHackMe/Hack The Box web rooms.""",
     "Burp Repeater is a rehearsal room where you replay one line of dialogue with tiny changes to see how the actor reacts.",
     "Testers using a structured WSTG checklist found a password-reset logic flaw that an automated scan had missed entirely.",
     "Every professional web test uses a proxy and a methodology. BSCP is Burp-focused.",
     ["Configure proxy", "Set scope", "Map app", "Repeater testing", "Evidence capture", "WSTG checklist coverage"],
     ["Read the Burp Suite and ZAP entries in the Toolkit", "Map the Silverline Portal features and roles", "Tick WSTG categories you covered in the Web lab"],
     "Write a one-page web test plan for the Silverline Portal using WSTG categories.",
     ["Installing the proxy CA in your everyday browser", "No scope set - recording unrelated traffic", "Running active scans on production without approval"],
     "Use a proxy with tight scope, devtools and an API client, and follow WSTG so your testing is complete and repeatable.",
     ["Burp Suite", "OWASP ZAP", "Intercepting proxy", "WSTG", "Repeater"],
     "Remember: Burp Community is free but Intruder is throttled and there is no automated scanner.",
     [Q("Which free, open-source tool is an alternative to Burp Suite?", ["Nessus", "OWASP ZAP", "Maltego", "Hashcat"], 1, "ZAP is OWASP's free proxy/scanner."),
      Q("Which Burp tool is best for manually modifying and resending one request?", ["Intruder", "Repeater", "Decoder", "Comparer"], 1, "Repeater."),
      Q("The OWASP WSTG is...", ["A firewall", "A web security testing guide/checklist", "A programming language", "A CVE database"], 1, "Web Security Testing Guide.")]),
  ],
  ["Progressive web labs from beginner to advanced using the simulated Silverline Portal and OWASP Juice Shop"],
  "p07",
  "Complete all 7 lessons, pass the module assessment and complete the Web Security lab and Project 7.",
  ["Explain each OWASP Top 10 2021 category", "Write one finding each for IDOR, SQLi and XSS", "List cookie flags and security headers"],
  ["How the web works", "Browser architecture", "Web servers", "HTTP/HTTPS", "Requests/Responses", "Headers", "Cookies", "Sessions", "Authentication", "Authorisation", "APIs", "REST", "JSON",
   "SQL injection", "XSS (reflected, stored, DOM)", "CSRF", "SSRF", "Broken access control", "Session weaknesses", "File uploads", "Directory traversal", "Misconfiguration",
   "Information disclosure", "API vulnerabilities", "Rate limiting", "Business logic", "IDOR", "Deserialization", "Security headers", "Input validation", "Output encoding",
   "Secure API design", "Burp Suite", "OWASP ZAP", "DevTools", "Postman"])


M("m10", 10, "System & Network Penetration Testing", "target",
  "This module brings scanning, vulnerability assessment and operating-system knowledge together into a professional network penetration testing methodology. Tools are taught for purpose, limits, detection and countermeasures, and used only against isolated lab systems.",
  ["Follow a recognised penetration testing methodology", "Assess Linux, Windows and network services for misconfiguration", "Explain AD, Kerberos and NTLM from an assessment view",
   "Audit password strength safely in a lab", "Explain privilege escalation and lateral movement concepts and their detection", "Recommend and verify remediation"],
  ["Modules 7 and 8", "Module 3 (Windows/AD)"],
  [
   L("m10l1", "Penetration Testing Methodology and Rules of Engagement", "Intermediate", 35,
     "Methodology makes testing safe, legal, repeatable and defensible.",
     ["Describe the phases of a penetration test", "Compare common methodologies", "Plan an engagement within rules of engagement"],
     """A penetration test is an authorised, time-boxed simulated attack to find and demonstrate security weaknesses so they can be fixed. Common phases are: pre-engagement (scope, rules, authorisation), reconnaissance, scanning and enumeration, vulnerability analysis, controlled exploitation/validation, post-exploitation analysis, reporting and retesting. Methodologies such as PTES, NIST SP 800-115 and the OSSTMM give structure.""",
     """Pre-engagement: statement of work, scope (IPs, domains, apps, accounts), exclusions, testing windows, permitted techniques, data handling, communication plan, emergency contacts, stop conditions, legal authorisation letter.

Test types: black box (no info), grey box (some info/credentials), white box (full info, code); external vs internal; network, web, wireless, social engineering, physical (each separately authorised).

Pen test vs vulnerability assessment: the assessment lists weaknesses; the pen test validates and demonstrates impact and chains.
Pen test vs red team: red teams emulate a specific adversary with stealth objectives over longer periods and test detection and response; pen tests aim for breadth of findings.

During testing: keep a timestamped activity log (which IP, what, when) so the client can correlate with their monitoring; pause on instability; report critical issues immediately if agreed.""",
     "A penetration test is a fire drill with a professional inspector: agreed date, agreed rooms, safety rules, and a report afterwards.",
     "A testing team's activity log let the client's SOC confirm that suspicious traffic at 14:02 was the tester - and that a separate alert at 03:00 was a real intruder.",
     "Methodology and RoE compliance are graded in the capstone and expected in every professional role.",
     ["Pre-engagement", "Recon", "Scan/Enumerate", "Vuln analysis", "Controlled validation", "Post-exploitation analysis", "Report", "Retest"],
     ["List the contents of a rules-of-engagement document", "Classify three engagements as black, grey or white box", "Write a sample activity log entry"],
     "Draft the stop conditions and emergency contact plan for testing a manufacturing plant.",
     ["Starting before written authorisation", "No activity log", "Confusing vulnerability scans with penetration tests"],
     "Agree scope and rules first, follow phased methodology, log activity, report and retest.",
     ["Penetration test", "PTES", "Rules of engagement", "Grey box", "Red team"],
     "Know the difference between a vulnerability assessment, a penetration test and a red team exercise.",
     [Q("Which phase comes first?", ["Exploitation", "Pre-engagement", "Reporting", "Scanning"], 1, "Scope and authorisation come first."),
      Q("A test where the tester receives user credentials and network diagrams is...", ["Black box", "Grey box", "Red team", "Physical"], 1, "Partial knowledge = grey box."),
      Q("Why keep a timestamped activity log?", ["To slow testing", "So the client can correlate activity and distinguish real attacks", "It is optional decoration", "To store passwords"], 1, "It supports correlation and accountability.")]),

   L("m10l2", "Network, Service, Linux and Windows Security Assessment", "Intermediate", 45,
     "Most internal compromises begin with ordinary misconfigurations, not exotic exploits.",
     ["Assess common network services for misconfiguration", "Apply Linux and Windows assessment checklists", "Relate findings to network architecture and segmentation"],
     """Assessing systems means checking services, configuration and patch levels against good practice. Network services often have weak settings: SMB without signing, anonymous FTP, SNMP with default community strings, exposed databases or management interfaces. Linux checks include SSH configuration, sudo rules, SUID files and services. Windows checks include patch level, local admins, SMB settings, firewall and credential protections. Results are mapped back to the network architecture to see how far an attacker could move.""",
     """Service checklist (lab):
• SMB (445): signing required? SMBv1 disabled? anonymous/guest shares?
• FTP (21): anonymous login? cleartext?
• SNMP (161/udp): default 'public'/'private' communities? v3 used?
• RDP (3389): NLA enabled? exposed beyond management networks?
• Databases (1433/3306/5432/27017): listening on all interfaces? default accounts?
• Management (SSH/Telnet/web admin of switches, iLO/iDRAC): default credentials? exposed to user VLANs?

Linux: kernel and package patch level, sshd_config, sudo -l, SUID/SGID, world-writable files, cron scripts, services listening, stored credentials in config files.

Windows: missing patches (Get-HotFix), local admin membership, LAPS, SMB signing, LLMNR/NBT-NS enabled, Credential Guard, AV/EDR status, unquoted service paths, weak service permissions, AlwaysInstallElevated policy.

Network architecture: flat vs segmented, management network isolation, firewall rules between zones, egress filtering.

Tools for evidence: Nmap NSE safe scripts, Netcat banners, configuration reviews with credentials (agreed in scope).""",
     "Assessment is a building inspection: check each lock, window and fire door against the safety code, then check whether the corridors let fire spread.",
     "An internal test found SNMP 'public' on core switches exposing full configuration - including network diagrams and hashed passwords.",
     "These checks generate the majority of real internal pen test findings.",
     ["Inventory services", "Check config vs baseline", "Linux checklist", "Windows checklist", "Map to architecture", "Findings"],
     ["Complete the Linux Permissions Audit and Windows PowerShell Audit labs", "Run the service checklist against the Scanning Simulator results", "Identify which finding enables movement between zones"],
     "Write findings for 'SMB signing not required' and 'SNMP default community' including remediation.",
     ["Testing only for CVEs and ignoring configuration", "Not mapping findings to segmentation", "Reading credentials from configs and reusing them outside scope"],
     "Check every service, OS and zone boundary against secure baselines; configuration findings are often the most impactful.",
     ["SMB signing", "SNMP community string", "LLMNR", "Unquoted service path", "Misconfiguration"],
     "LLMNR/NBT-NS poisoning risk is mitigated by disabling those protocols via GPO.",
     [Q("Which SNMP setting is a classic misconfiguration?", ["SNMPv3 with auth", "Default 'public' community string", "SNMP disabled", "ACL-restricted SNMP"], 1, "Default communities expose data."),
      Q("SMB signing helps prevent...", ["SQL injection", "Relay/tampering attacks on SMB traffic", "XSS", "DNS spoofing only"], 1, "Signing protects integrity and blocks relay."),
      Q("An 'unquoted service path' is a risk on...", ["Linux cron", "Windows services", "DNS servers", "Routers"], 1, "Windows may execute an attacker-placed binary in a path with spaces.")]),

   L("m10l3", "Active Directory, Identity, Authentication Protocols and Credential Protection", "Advanced", 45,
     "Identity is the new perimeter; in most internal tests, AD is the route to full compromise.",
     ["Explain Kerberos and NTLM at assessment level", "Identify common AD identity risks", "Recommend credential protection controls"],
     """In a Windows domain, users prove who they are to domain controllers using Kerberos (ticket-based) or the older NTLM (challenge-response). Attackers target weaknesses in these processes and in how credentials are stored on machines. Common AD risks include too many privileged accounts, service accounts with weak passwords, legacy protocols and admins logging in to ordinary workstations where their credentials can be stolen. Defences include tiered administration, Credential Guard, LAPS, strong service account management and monitoring.""",
     """Kerberos (simplified): user requests a Ticket Granting Ticket (TGT) from the KDC on the domain controller -> uses the TGT to request service tickets (TGS) -> presents the service ticket to the server. Tickets are encrypted with account-derived keys.

Assessment-level risk concepts (defensive framing):
• Service accounts with SPNs and weak passwords - service tickets can be cracked offline (Kerberoasting). Defence: long random passwords, gMSAs, AES-only.
• Accounts without Kerberos pre-authentication (AS-REP roasting). Defence: require pre-auth.
• NTLM relay where SMB/LDAP signing is not enforced. Defence: enforce signing, disable NTLM where possible, EPA.
• Credentials cached in memory (LSASS) on machines where admins log in. Defence: Credential Guard, LSA protection, tiering, Protected Users group.
• Excessive delegation, dangerous ACLs (GenericAll/WriteDACL on privileged objects), stale admin accounts.

Tools for authorised defensive assessment: BloodHound (graph of AD attack paths - widely used by both red and blue teams), PingCastle (free AD health report), PowerShell AD module.

Detection: event IDs 4768/4769 (Kerberos tickets, watch RC4 requests), 4624 logon type 3/10, 4672, unusual admin logons.""",
     "Kerberos is a festival wristband system: the main gate (DC) gives you a wristband (TGT), which you show at each tent (service) to get a tent pass (service ticket).",
     "A PingCastle report showed 42 Domain Admins and a service account with a 2014 password; reducing both removed the main attack paths found in the following pen test.",
     "AD assessment skills are heavily tested in PNPT and OSCP-style exams and are central to internal pen tests.",
     ["User", "KDC: AS-REQ -> TGT", "TGS-REQ -> service ticket", "Service validates", "Access"],
     ["Complete the Active Directory Review lab", "Match each AD risk to its defence", "List four Kerberos/logon event IDs and what they show"],
     "Write the executive-summary paragraph for 'Kerberoastable service account with weak password'.",
     ["Running AD attack tools in a production domain without explicit scope", "Ignoring service accounts", "Treating 'Domain Admin achieved' as the whole report"],
     "Know how Kerberos and NTLM work, the classic identity weaknesses and the controls that stop them.",
     ["Kerberos", "NTLM", "Kerberoasting", "LSASS", "Credential Guard", "BloodHound"],
     "gMSA (group managed service accounts) give automatically rotated, long passwords for services.",
     [Q("Which component issues Kerberos tickets?", ["DHCP server", "KDC on the domain controller", "Web server", "DNS resolver only"], 1, "The Key Distribution Center."),
      Q("Kerberoasting targets...", ["User browsers", "Service accounts with SPNs and weak passwords", "Firewalls", "Printers only"], 1, "Service tickets can be cracked offline if passwords are weak."),
      Q("Which control best protects credentials stored in LSASS?", ["SMBv1", "Credential Guard", "Telnet", "Guest account"], 1, "Credential Guard isolates secrets with virtualisation-based security.")]),

   L("m10l4", "Password Security Auditing and Privilege Escalation Concepts", "Advanced", 45,
     "Weak passwords and local misconfigurations turn a foothold into full control.",
     ["Explain offline vs online password auditing and its ethics", "Describe John the Ripper, Hashcat and Hydra at concept level", "Explain common Linux and Windows privilege escalation paths and fixes"],
     """Password auditing checks whether passwords are strong enough. Online auditing tries passwords against a live login (Hydra is a known tool) - this can lock accounts and must be tightly controlled. Offline auditing tests password hashes obtained in an authorised engagement against wordlists and rules (John the Ripper, Hashcat). Privilege escalation means going from a normal user to administrator or root by abusing misconfigurations or vulnerabilities. You will study the categories and, crucially, how to detect and fix them.""",
     """Password auditing rules: written approval for each technique; agree lockout thresholds; never test real users' online logins without explicit permission; handle hashes and cracked passwords as highly sensitive (encrypt, never put plaintext in reports - report counts and patterns); delete at engagement end.

Hash types and speed: NTLM and unsalted MD5/SHA-1 are fast to test; bcrypt/Argon2 are deliberately slow - which is why they are recommended.

Concept-level tools: John the Ripper (format auto-detection, wordlist + rules), Hashcat (GPU-accelerated, mode numbers per hash type), Hydra (online, many protocols - throttled and scoped). In this app's lab you analyse simulated output only.

Linux privilege escalation categories: sudo misconfigurations, SUID binaries, writable cron/scripts run by root, PATH abuse, kernel vulnerabilities, credentials in files, capabilities, container breakouts.
Windows categories: unquoted service paths, weak service/file permissions, AlwaysInstallElevated, stored credentials, token privileges (SeImpersonate), missing patches, scheduled tasks.

Detection and fixes: least privilege, patching, file permission audits, LAPS, removing stored credentials, monitoring new admin group members (4732), sudo logging, EDR alerts.""",
     "Offline cracking is trying keys on a copied lock at home (no alarms); online guessing is trying keys on the real door while the alarm counts attempts.",
     "An authorised audit found 31% of domain passwords matched a common wordlist plus the year. The client introduced a banned-password list and passphrase guidance.",
     "Password auditing results drive identity improvements; privilege escalation knowledge underpins both offensive certifications and system hardening.",
     ["Authorised hash set", "Wordlist + rules (offline)", "Stats: % weak, patterns", "Report without plaintext", "Securely delete"],
     ["Complete the Password Audit Analysis lab (simulated output)", "Classify five escalation paths as Linux or Windows and name the fix", "Write password findings using statistics only"],
     "Write remediation for 'sudo rule allows a user to run a text editor as root'.",
     ["Locking out real users with online guessing", "Putting cracked passwords in the report", "Using kernel exploits on production systems"],
     "Audit passwords only with explicit approval and handle results as secrets. Understand escalation paths so you can detect and close them.",
     ["Password auditing", "John the Ripper", "Hashcat", "Hydra", "Privilege escalation", "Wordlist"],
     "Offline cracking avoids lockouts; online guessing risks them - a common exam distinction.",
     [Q("Which is an offline password auditing tool?", ["Hydra", "Hashcat", "Nmap", "Wireshark"], 1, "Hashcat tests hashes offline."),
      Q("How should cracked passwords appear in a report?", ["In full plaintext", "As statistics and patterns, not plaintext", "Emailed separately unencrypted", "In the executive summary"], 1, "Report patterns, protect secrets."),
      Q("Which is a Windows privilege escalation category?", ["Unquoted service paths", "SUID binaries", "Cron jobs", "Sticky bit"], 0, "Unquoted paths are Windows-specific.")]),

   L("m10l5", "Lateral Movement, Segmentation, Detection, Remediation and Tool Education", "Advanced", 45,
     "Clients need to know how far an attacker could go - and whether they would notice.",
     ["Explain lateral movement concepts and segmentation's role", "Describe detection and logging for network attacks", "Explain the purpose and limits of Metasploit, Searchsploit and Netcat"],
     """Lateral movement is how an attacker moves from one compromised machine to others, typically by reusing credentials or remote administration features (SMB, RDP, WinRM, SSH). Network segmentation and least privilege limit how far they can go. Detection means logging and alerting on suspicious authentication and remote execution. Metasploit is an exploitation framework used to validate vulnerabilities in controlled conditions; Searchsploit searches a local copy of Exploit-DB; Netcat reads and writes raw network connections.""",
     """Lateral movement concepts: credential reuse (same local admin password), remote services (PsExec-style SMB execution, WMI, WinRM, RDP, SSH), pass-the-hash/ticket (using credential material instead of passwords). Defensive mapping: MITRE ATT&CK TA0008.

Segmentation testing: from each zone, check what can be reached (e.g. can the guest VLAN reach the server VLAN on 445?). Document allowed paths vs policy.

Detection: Windows 4624 type 3/10, 4648 (explicit credentials), 7045 (service install), Sysmon event 1 (process creation) and 3 (network); Linux auth logs; network IDS (Suricata, Zeek - free); EDR telemetry. Good tests record which actions were or were not detected - valuable feedback for the blue team.

Tool education (lab only):
• Metasploit Framework (open source): modules (exploit, auxiliary, post), payload choice, check commands to verify vulnerability without exploitation where available. Limits: can crash services; noisy; detected by EDR.
• Searchsploit: offline Exploit-DB search; always review code before running anything - public exploits can be broken or malicious.
• Netcat: banner grabbing, simple port checks, file transfer in labs.

Remediation and verification: fix root causes (patch, harden, unique credentials, segmentation rules), then retest the same paths to prove closure.""",
     "Lateral movement is a burglar using a stolen master key to open every door; segmentation means each wing needs a different key and there is a guard (monitoring) in each corridor.",
     "Replacing a shared local administrator password with LAPS and enforcing firewall rules between workstation subnets stopped a red team from spreading beyond the first laptop.",
     "Reports that explain attack paths and detection gaps give clients the most value.",
     ["Foothold", "Credential reuse / remote service", "Next host", "Segmentation blocks?", "Detected?", "Remediate + retest"],
     ["Map lateral movement techniques to detection event IDs", "Use the Toolkit to compare Metasploit, Searchsploit and Netcat", "Design a segmentation test plan"],
     "Write a 'detection gap' finding for remote service installation that generated no alert.",
     ["Running public exploits without reading them", "Testing lateral movement outside agreed scope", "Forgetting to report what was detected"],
     "Lateral movement relies on credentials and remote services; segmentation and monitoring contain it; tools validate - carefully - and retesting proves fixes.",
     ["Lateral movement", "Pass-the-hash", "Metasploit", "Searchsploit", "Netcat", "Sysmon"],
     "Event 7045 (service installed) often accompanies remote execution tools.",
     [Q("Which control most directly limits lateral movement between workstations?", ["Bigger screens", "Segmentation and unique local admin passwords", "Disabling DNS", "Longer session cookies"], 1, "Segmentation + LAPS break common paths."),
      Q("Before running a public exploit from Searchsploit you should...", ["Run it immediately", "Read and understand the code and run it only in an authorised lab", "Email it to the client", "Disable antivirus everywhere"], 1, "Public code can be broken or malicious."),
      Q("Which free tool is a network IDS?", ["Suricata", "Excel", "Maltego", "Hashcat"], 0, "Suricata (and Zeek) are free network monitoring tools.")]),
  ],
  ["Methodology drills", "Service misconfiguration review", "Linux and Windows audits", "AD review lab", "Password audit analysis (simulated)", "Detection mapping"],
  "p05",
  "Complete all 5 lessons, pass the module assessment and complete the AD Review and Password Audit labs.",
  ["List methodology phases", "Match AD risks to defences", "Name 5 privilege escalation categories per OS"],
  ["Methodology", "Rules of engagement", "Network assessment", "Architecture", "Service misconfiguration", "Linux assessment", "Windows assessment", "Windows domains", "Active Directory",
   "Identity", "Privilege escalation", "Password auditing", "Credential protection", "Authentication protocols", "Security configuration", "Lateral movement", "Segmentation",
   "Detection and logging", "Remediation", "Verification", "Metasploit", "Nmap", "John the Ripper", "Hashcat", "Hydra", "Netcat", "Searchsploit"])
