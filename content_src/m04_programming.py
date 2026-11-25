from lib import *

M("m04", 4, "Programming & Scripting", "code",
  "Code turns a security professional from a tool user into a problem solver. You will learn Python, Bash, PowerShell, JavaScript and SQL with a defensive, automation-first mindset.",
  ["Write Python scripts with functions, files, JSON and regex", "Automate tasks in Bash and PowerShell", "Understand JavaScript and browser security basics",
   "Query databases with SQL and prevent SQL injection", "Build safe defensive automation"],
  ["Module 3: Linux & Windows (terminal comfort)"],
  [
   L("m04l1", "Python Fundamentals: Types, Collections, Conditions and Loops", "Beginner", 40,
     "Python is the most widely used language in security tooling, automation and data analysis.",
     ["Use variables, strings and numbers", "Store data in lists, tuples, dictionaries and sets", "Control flow with if, for and while"],
     """Python is a readable programming language. A variable stores a value: port = 443. Data types include int (whole numbers), float (decimals), str (text), bool (True/False). Collections hold many values: a list [22, 80, 443] is ordered and changeable, a tuple (10, 20) cannot change, a dict {"ssh": 22} maps keys to values, and a set {"admin", "guest"} holds unique items. if/elif/else make decisions; for and while loops repeat work.""",
     """Example - classify ports:

ports = [21, 22, 80, 443, 3389]
risky = {21: "FTP cleartext", 23: "Telnet cleartext", 3389: "RDP exposed"}
for p in ports:
    if p in risky:
        print(f"Port {p}: review - {risky[p]}")
    elif p < 1024:
        print(f"Port {p}: well-known service")
    else:
        print(f"Port {p}: high port")

Strings: methods like .lower(), .split(","), .strip(), .startswith(); f-strings for formatting; slicing s[0:5].
Lists: .append(), .sort(), len(), list comprehensions [p for p in ports if p < 1024].
Dicts: d.get("key", default), d.items() for loops.
Sets: fast membership tests and de-duplication: unique_ips = set(ip_list).
While loops need a condition that eventually becomes False to avoid infinite loops.
Indentation (four spaces) defines code blocks - it is part of the syntax.""",
     "Variables are labelled boxes; a list is a row of boxes; a dictionary is a filing cabinet with labelled drawers.",
     "A SOC analyst uses a set to de-duplicate 50,000 IP addresses from logs down to 312 unique sources in one line.",
     "Every later security script - log parsers, report generators, API clients - is built from these basics.",
     ["Data in", "Store in variables/collections", "Decide (if)", "Repeat (loop)", "Output result"],
     ["Create a dict of five services and their ports and print them", "Write a loop that prints only even numbers 1-20", "Use a set to remove duplicates from a list of IPs"],
     "Write a script that takes a list of usernames and prints any that appear more than once.",
     ["Wrong indentation", "Modifying a list while looping over it", "Using == to compare to None instead of 'is None'"],
     "Variables, types, collections, conditions and loops are the building blocks of every script.",
     ["Variable", "List", "Dictionary", "Set", "Loop"],
     "Know which collection is ordered, which is immutable and which removes duplicates.",
     [Q("Which Python type maps keys to values?", ["list", "tuple", "dict", "set"], 2, "Dictionaries store key-value pairs."),
      Q("Which collection automatically removes duplicates?", ["list", "set", "tuple", "str"], 1, "Sets hold unique items only."),
      Q("What defines a code block in Python?", ["Curly braces", "Indentation", "Semicolons", "BEGIN/END"], 1, "Indentation is part of Python syntax.")]),

   L("m04l2", "Python Functions, Modules, Classes, Files, Errors, JSON, CSV and Regex", "Intermediate", 45,
     "Real tools read files, parse formats and handle errors gracefully.",
     ["Write reusable functions and import modules", "Read and write files, JSON and CSV", "Handle exceptions and use regular expressions"],
     """Functions package reusable logic: def is_private(ip): ... return True. Modules are files of code you import, like import json or import re. Classes bundle data and behaviour into objects. Files are opened with 'with open("log.txt") as f:' which closes them automatically. Exceptions are errors you can catch with try/except so a script does not crash. JSON and CSV are common data formats, and regular expressions (regex) find patterns such as IP addresses in text.""",
     """Example - extract IPs from a log safely:

import re, json
IP_RE = re.compile(r"\\b(?:\\d{1,3}\\.){3}\\d{1,3}\\b")

def extract_ips(path):
    found = {}
    try:
        with open(path, encoding="utf-8") as f:
            for line in f:
                for ip in IP_RE.findall(line):
                    found[ip] = found.get(ip, 0) + 1
    except FileNotFoundError:
        print("Log not found:", path)
    return found

counts = extract_ips("auth.log")
with open("ips.json", "w") as out:
    json.dump(counts, out, indent=2)

CSV: import csv; csv.DictReader(f) gives each row as a dict.
Classes: class Finding: with __init__(self, title, severity) to model report findings.
Standard library highlights: os, pathlib, sys, argparse (command-line options), datetime, hashlib, subprocess (use with care - never pass untrusted input to a shell), logging.""",
     "A function is a recipe you can reuse; a module is a recipe book; exceptions are the fire blanket you keep ready in case something goes wrong.",
     "Incident responders write small Python parsers to turn messy firewall exports into JSON that can be loaded into a dashboard.",
     "Parsing logs, normalising data and handling bad input safely are everyday security engineering tasks.",
     ["Open file (with)", "Read line by line", "Regex match", "Count in dict", "Write JSON", "Handle errors"],
     ["Write a function that returns True if a password is at least 12 characters", "Load a CSV of assets and print those marked 'critical'", "Write a regex that matches an email address"],
     "Build a Finding class with title, severity and remediation, then export a list of findings to JSON.",
     ["Bare 'except:' that hides real errors", "Forgetting encoding when reading files", "Using subprocess with shell=True on untrusted input"],
     "Functions, modules and classes organise code; with-blocks handle files; try/except handles errors; JSON, CSV and regex process data.",
     ["Function", "Module", "Exception", "JSON", "Regular expression"],
     "Exam tasks often ask you to spot a bug - check indentation, exception handling and file closing.",
     [Q("Why use 'with open(...) as f'?", ["It encrypts the file", "It closes the file automatically", "It makes reading faster", "It is required for JSON"], 1, "The context manager guarantees the file is closed."),
      Q("Which module parses JSON?", ["re", "csv", "json", "os"], 2, "The json module."),
      Q("What is risky about subprocess with shell=True and user input?", ["Nothing", "Command injection", "Slower output", "Memory leaks only"], 1, "Untrusted input can inject extra shell commands.")]),

   L("m04l3", "Python for Security: HTTP, APIs, Automation, Logging, Testing and Secure Coding", "Intermediate", 45,
     "Most modern security work touches APIs. Secure coding stops you creating the vulnerabilities you will later learn to find.",
     ["Make HTTP requests and call APIs", "Log and test your scripts", "Apply secure coding principles"],
     """HTTP is how programs talk to web servers. In Python you can use the built-in urllib or the popular requests library to send GET and POST requests and read JSON responses from APIs. Good scripts log what they do (the logging module) and are tested (unittest or pytest). Secure coding means validating input, never hardcoding secrets, handling errors without leaking details, and using safe libraries.""",
     """Example - query a lab API with a timeout and API key from the environment:

import os, requests, logging
logging.basicConfig(level=logging.INFO)
API = "http://api.lab.local/v1/assets"
key = os.environ.get("LAB_API_KEY")   # never hardcode secrets
resp = requests.get(API, headers={"Authorization": f"Bearer {key}"}, timeout=10)
resp.raise_for_status()
for asset in resp.json():
    logging.info("Asset %s owner=%s", asset["name"], asset["owner"])

Status codes: 200 OK, 201 Created, 301/302 redirect, 400 bad request, 401 unauthenticated, 403 forbidden, 404 not found, 429 too many requests, 500 server error.

Testing: def test_is_private(): assert is_private("10.0.0.1"). Run with pytest.

Secure coding checklist: validate and whitelist input; parameterise database queries; encode output; least privilege; secrets in environment variables or a vault; pin and update dependencies; set timeouts; handle exceptions without printing stack traces to users; log security events but never log passwords or tokens.

Automation ethics: only send requests to systems you are authorised to test, respect rate limits and never automate attacks against third parties.""",
     "An API is a restaurant menu for programs: you order (request) in a defined format and the kitchen returns a dish (response).",
     "A team automated their weekly vulnerability export with a 40-line Python script calling their scanner's API - saving four hours of manual work each week.",
     "APIs are both an attack surface and an automation tool. Secure coding knowledge is required for AppSec and DevSecOps roles.",
     ["Read config/secret from env", "Send request with timeout", "Check status code", "Parse JSON", "Log", "Test"],
     ["Write a function that returns a description for an HTTP status code", "Add logging to a previous script", "Write two pytest tests for is_private()"],
     "Review a script that has an API key hardcoded on line 3 and rewrite it securely.",
     ["Hardcoding API keys in code or Git", "No timeouts on network requests", "Logging full request bodies containing passwords"],
     "Use HTTP and APIs carefully, log and test everything, keep secrets out of code and validate all input.",
     ["HTTP status code", "API", "Logging", "Unit test", "Secure coding"],
     "Know 401 vs 403: 401 = not authenticated, 403 = authenticated but not allowed.",
     [Q("Which status code means 'authenticated but not allowed'?", ["200", "401", "403", "500"], 2, "403 Forbidden."),
      Q("Where should an API key for a script be stored?", ["In the source code", "In an environment variable or secrets vault", "In a public Git repo", "In a comment"], 1, "Keep secrets out of code."),
      TF("Network requests in scripts should set a timeout.", True, "Without a timeout a script can hang forever.")]),

   L("m04l4", "Bash and PowerShell Scripting", "Intermediate", 40,
     "Shell scripts glue tools together on servers and endpoints - fast wins for audits and administration.",
     ["Write Bash scripts with variables, conditions, loops and functions", "Write PowerShell scripts using objects and pipelines", "Automate safe system audits"],
     """A Bash script is a text file of shell commands starting with #!/bin/bash. It can use variables (NAME="lab"), conditions (if [ -f file ]; then ... fi), loops (for f in *.log; do ... done) and functions. PowerShell scripts (.ps1) use cmdlets that pass objects, not plain text, through pipelines, which makes filtering and sorting powerful: Get-Service | Where-Object Status -eq 'Running' | Sort-Object Name.""",
     """Bash audit example:

#!/bin/bash
# quick-audit.sh - read-only checks for your own lab VM
echo "== Listening ports =="; ss -tulpn
echo "== SUID files =="; find / -perm -4000 -type f 2>/dev/null
echo "== Users with shells =="; grep -E "bash|zsh" /etc/passwd | cut -d: -f1
if grep -q "^PermitRootLogin yes" /etc/ssh/sshd_config; then
  echo "WARNING: root SSH login allowed"
fi

Key Bash features: $1 (first argument), $? (last exit code), $(command) (command substitution), read (input), set -euo pipefail (safer scripts), quoting variables "$var" to avoid word splitting.

PowerShell audit example:

$admins = Get-LocalGroupMember -Group Administrators
$fw = Get-NetFirewallProfile | Select-Object Name, Enabled
$report = [PSCustomObject]@{ Host=$env:COMPUTERNAME; Admins=$admins.Count; Firewall=$fw }
$report | ConvertTo-Json | Out-File audit.json

PowerShell features: variables start with $, objects have properties (.Name), Where-Object/Select-Object/Sort-Object, functions with param(), Export-Csv and ConvertTo-Json for reports, Try/Catch for errors.""",
     "Bash passes notes (text) between workers; PowerShell passes labelled parcels (objects) so the next worker knows exactly what is inside.",
     "A Windows admin used a PowerShell script to check 300 machines for disabled firewalls and produced a CSV for the security team in minutes.",
     "Audit scripts make checks repeatable and provide evidence; attackers also use these shells, so defenders monitor them.",
     ["#!/bin/bash or .ps1", "Collect data", "Filter", "Decide (if)", "Report (CSV/JSON)"],
     ["Write a Bash script that counts files in a directory passed as $1", "Write a PowerShell command that lists stopped services", "Export a PowerShell result to CSV"],
     "Extend quick-audit.sh to write its output to a timestamped file.",
     ["Unquoted variables in Bash", "Treating PowerShell output as plain text instead of objects", "Running audit scripts on systems you do not own"],
     "Bash chains text-based tools; PowerShell pipes objects. Both turn manual checks into repeatable, evidenced audits.",
     ["Shebang", "Exit code", "Pipeline", "Cmdlet", "Script"],
     "Remember $? holds the last exit code in Bash, and 0 means success.",
     [Q("In Bash, what does $1 contain?", ["The script name", "The first argument", "The exit code", "The user ID"], 1, "$1 is the first positional parameter."),
      Q("What does PowerShell pass through a pipeline?", ["Plain text", "Objects", "Binary only", "Files only"], 1, "PowerShell pipelines pass .NET objects."),
      Q("What does an exit code of 0 mean in Bash?", ["Failure", "Success", "Permission denied", "Not found"], 1, "Zero indicates success.")]),

   L("m04l5", "JavaScript, the DOM and Browser Security", "Intermediate", 35,
     "Web application testing is impossible without reading JavaScript; client-side flaws like XSS live here.",
     ["Read basic JavaScript", "Explain the DOM and web requests", "Describe the same-origin policy and client-side security limits"],
     """JavaScript runs in the browser and makes pages interactive. The DOM (Document Object Model) is the browser's tree of page elements that JavaScript can read and change. fetch() sends web requests from the page. Browsers enforce security rules like the same-origin policy, which stops one website's scripts from reading another site's data. Anything in the browser can be viewed and changed by the user, so security checks must always be repeated on the server.""",
     """Basics: let/const variables, functions, arrays, objects, events (button.addEventListener("click", ...)), JSON.

DOM: document.getElementById("msg").textContent = userInput; (safe - treats input as text)
versus .innerHTML = userInput; (dangerous - interprets input as HTML, enabling XSS).

Requests: fetch("/api/profile").then(r => r.json()). Cookies with the HttpOnly flag cannot be read by JavaScript; Secure means HTTPS only; SameSite limits cross-site sending (helps against CSRF).

Browser security model: same-origin policy (scheme + host + port must match); CORS lets servers relax it deliberately - misconfigured CORS (e.g. reflecting any origin with credentials) is a vulnerability; Content Security Policy (CSP) restricts where scripts can load from and reduces XSS impact.

Client-side security concepts: hidden form fields, disabled buttons and JavaScript validation are for user experience only - an attacker can bypass them with developer tools or a proxy. Never store secrets (API keys, admin passwords) in front-end code.

Secure development: use textContent or framework escaping, avoid eval(), set CSP, HttpOnly/Secure/SameSite cookies, validate on the server.""",
     "Client-side validation is a 'please wipe your feet' sign; server-side validation is the security guard at the door.",
     "A shop calculated prices in JavaScript and trusted the total sent by the browser. Testers changed the price in a proxy and 'bought' items for £0.01 - a business logic flaw.",
     "XSS, CSRF, CORS issues and exposed secrets in JavaScript are core web findings.",
     ["User input", "JavaScript", "DOM update (textContent vs innerHTML)", "fetch to server", "Server validates"],
     ["Open browser developer tools on a site you own and inspect the DOM", "Find which cookies a lab site sets and their flags", "Rewrite an innerHTML line using textContent"],
     "Explain why a 'maxlength' attribute on a form field is not a security control.",
     ["Trusting client-side validation", "Using innerHTML with user data", "Hiding API keys in minified JavaScript"],
     "JavaScript controls the page via the DOM, but the browser belongs to the user. Validate on the server and use browser protections.",
     ["DOM", "Same-origin policy", "CORS", "CSP", "HttpOnly"],
     "Same origin = same scheme, host and port. HttpOnly stops JavaScript reading a cookie.",
     [Q("Which DOM property safely inserts untrusted text?", ["innerHTML", "outerHTML", "textContent", "document.write"], 2, "textContent does not parse HTML."),
      Q("Which cookie flag prevents JavaScript from reading the cookie?", ["Secure", "HttpOnly", "SameSite", "Path"], 1, "HttpOnly."),
      TF("Client-side JavaScript validation is enough to stop malicious input.", False, "Users control the browser; validate on the server.")]),

   L("m04l6", "SQL: Queries, Joins, Permissions and SQL Injection Prevention", "Intermediate", 45,
     "Databases hold the crown jewels. SQL injection remains one of the most damaging web vulnerabilities.",
     ["Write SELECT, INSERT, UPDATE and DELETE queries", "Join tables and apply database permissions", "Explain SQL injection and prevent it with parameterised queries"],
     """A relational database stores data in tables of rows and columns. SQL (Structured Query Language) reads and changes it: SELECT reads, INSERT adds, UPDATE changes and DELETE removes. JOIN combines related tables. Database accounts should have only the permissions they need. SQL injection happens when an application builds a query by pasting user input into SQL text, letting an attacker change the query's meaning. The fix is parameterised queries, which keep data separate from code.""",
     """Examples:
SELECT name, email FROM users WHERE role = 'admin';
INSERT INTO assets (name, owner) VALUES ('web01', 'ops');
UPDATE users SET locked = 1 WHERE failed_logins > 5;
DELETE FROM sessions WHERE expires < CURRENT_TIMESTAMP;
SELECT u.name, o.total FROM users u JOIN orders o ON o.user_id = u.id;

Vulnerable pattern (never do this):
query = "SELECT * FROM users WHERE name = '" + username + "'"
If username is  ' OR '1'='1  the WHERE clause becomes always true.

Safe pattern (parameterised):
cur.execute("SELECT * FROM users WHERE name = ?", (username,))

Types of SQLi you will study in Module 9: in-band (error/union), blind (boolean/time) and out-of-band.

Defence in depth: parameterised queries or safe ORMs; input validation; least-privilege DB accounts (the web app should not be db owner); no detailed DB errors shown to users; encrypt sensitive columns; WAF as an extra layer, not the fix.

Permissions: GRANT SELECT ON orders TO reporting_user; REVOKE DELETE ...; separate accounts for app, admin and reporting.""",
     "A parameterised query is a form with fixed boxes: whatever the user writes stays inside its box and can never rewrite the form itself.",
     "Many large data breaches started with a single injectable parameter in a login or search form, exposing entire customer tables.",
     "SQLi is tested in nearly every web assessment, and secure database design is a core AppSec skill.",
     ["User input", "Parameter placeholder (?)", "Driver sends query + data separately", "Database treats input as data", "Safe result"],
     ["Write a query listing assets owned by 'finance'", "Write a JOIN between users and logins", "Rewrite a string-concatenated query as a parameterised one"],
     "Explain to a developer, in plain language, why escaping quotes manually is weaker than parameterised queries.",
     ["Building SQL with string concatenation", "Using a database admin account for the web app", "Showing raw SQL errors to users"],
     "SQL reads and changes tables; joins connect them; least-privilege accounts limit damage; parameterised queries stop SQL injection.",
     ["SQL", "JOIN", "SQL injection", "Parameterised query", "Least privilege"],
     "The primary fix for SQL injection is always parameterised queries (prepared statements).",
     [Q("Which statement reads data?", ["INSERT", "SELECT", "UPDATE", "DROP"], 1, "SELECT retrieves rows."),
      Q("What is the primary defence against SQL injection?", ["A WAF", "Parameterised queries", "Hiding error messages", "Using HTTPS"], 1, "Prepared statements separate code from data."),
      Q("The input ' OR '1'='1 is dangerous in a concatenated query because...", ["It is too long", "It makes the WHERE condition always true", "It deletes the table", "It encrypts data"], 1, "It alters query logic so every row matches.")]),

   L("m04l7", "Security Automation: Logs, Validation, Integrity and Reports", "Intermediate", 40,
     "Automation lets one analyst do the work of many and makes checks consistent and auditable.",
     ["Parse logs automatically", "Validate input safely", "Build file-integrity checks and automated reports"],
     """Security automation means writing scripts that do repetitive security checks for you: parsing logs for failed logins, validating input, checking files have not changed, and producing reports. A file integrity check stores a hash (fingerprint) of important files and alerts if a hash changes. Always build defensive scripts that read and report rather than change systems, unless the change is approved.""",
     """File integrity monitor (Python):

import hashlib, json, pathlib
def sha256(path):
    h = hashlib.sha256()
    with open(path, "rb") as f:
        for chunk in iter(lambda: f.read(65536), b""):
            h.update(chunk)
    return h.hexdigest()

watch = ["/etc/passwd", "/etc/ssh/sshd_config"]
baseline_file = pathlib.Path("baseline.json")
current = {p: sha256(p) for p in watch}
if baseline_file.exists():
    old = json.loads(baseline_file.read_text())
    for p, h in current.items():
        if old.get(p) != h:
            print("CHANGED:", p)
else:
    baseline_file.write_text(json.dumps(current, indent=2))

Input validation: allow-list expected formats (e.g. ipaddress.ip_address(value) raises ValueError on invalid IPs), check length and range, reject rather than 'clean' unexpected input.

Log parsing pipeline: read -> regex extract -> count/aggregate -> threshold -> alert -> report (CSV/JSON/Markdown).

Reports: generate a dated summary with counts, top offenders and recommended actions; keep raw evidence alongside.

Production tools that do this at scale: AIDE/Tripwire/OSSEC/Wazuh (integrity), SIEMs (log analysis), SOAR (orchestration).""",
     "A file integrity check is like photographing your room before leaving and comparing photos when you return.",
     "A web server's integrity monitor flagged a changed index.php at 01:12 - the first sign of a web shell, caught within minutes.",
     "Detection engineering and blue team roles rely on automating these exact checks.",
     ["Baseline hashes", "Re-hash on schedule", "Compare", "Alert on change", "Report + evidence"],
     ["Run the integrity script on files in your lab VM and then change one file", "Write a validator for port numbers (1-65535)", "Create a CSV report of failed logins per IP"],
     "Design an automated weekly report that lists new local admin accounts across your lab machines.",
     ["Using MD5 for integrity where tampering is a concern", "Cleaning bad input instead of rejecting it", "Scripts that change systems without approval"],
     "Automate read-only checks: parse logs, validate input with allow-lists, hash files for integrity and produce dated reports.",
     ["File integrity monitoring", "Hash", "Input validation", "Allow-list", "SIEM"],
     "Allow-listing (accept known good) is preferred over deny-listing (block known bad).",
     [Q("What does a file integrity monitor compare?", ["File sizes only", "Cryptographic hashes against a baseline", "File names only", "Owner names"], 1, "Hashes reveal any content change."),
      Q("Which validation approach is strongest?", ["Deny-list known bad input", "Allow-list known good input", "No validation", "Client-side only"], 1, "Allow-lists reject anything unexpected."),
      Q("Which tool category aggregates and analyses logs at scale?", ["IDE", "SIEM", "Hypervisor", "Compiler"], 1, "A SIEM collects and correlates logs.")]),
  ],
  ["Progressive coding challenges", "Mini projects", "Code quizzes", "Debugging exercises", "Safe security automation projects"],
  "p03",
  "Complete all 7 lessons, pass the module assessment and complete at least three coding challenges in the Challenge Arena.",
  ["Rewrite one vulnerable SQL query safely", "Explain the integrity monitor line by line", "List secure coding rules from memory"],
  ["Python fundamentals", "Variables", "Data types", "Strings", "Numbers", "Lists", "Tuples", "Dictionaries", "Sets", "Conditions", "Loops", "Functions", "Modules", "Classes",
   "File handling", "Exceptions", "JSON", "CSV", "Regex", "HTTP", "APIs", "Automation", "Secure coding", "Logging", "Testing", "Bash", "PowerShell", "JavaScript", "DOM",
   "Browser security", "SQL", "Joins", "Database permissions", "SQL injection", "Parameterised queries", "Security automation"])
