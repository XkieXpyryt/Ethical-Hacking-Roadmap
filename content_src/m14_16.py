from lib import *

M("m14", 14, "Reporting & Professional Practice", "report",
  "The report is the product. Clients rarely see your testing - they read your report. This module teaches professional conduct and how to write clear, accurate, actionable penetration testing reports using the in-app Report Builder.",
  ["Operate within ethics, law, authorisation and scope", "Communicate with clients professionally", "Handle evidence and confidential data correctly",
   "Write executive and technical summaries", "Write findings with severity, evidence, impact and remediation", "Plan retesting and verification"],
  ["Modules 5 and 8"],
  [
   L("m14l1", "Professional Ethics, Authorisation, Scope, Boundaries and Client Communication", "Intermediate", 35,
     "Trust is the ethical hacker's licence to operate.",
     ["Apply professional ethics in practice", "Manage scope changes and boundaries", "Communicate clearly with clients before, during and after testing"],
     """Professional practice means behaving so that clients can trust you with access to their most sensitive systems. You work only within written authorisation and scope, respect testing boundaries, communicate promptly about critical issues or incidents, and keep everything confidential. If something unexpected happens - a system crashes, you find evidence of a real attacker, or you are unsure whether something is in scope - you stop and talk to the client.""",
     """Authorisation chain: the signatory must have authority over the systems (and third-party hosting may need its own approval). Keep the signed authorisation accessible during testing.

Scope management: any request to add targets mid-test needs written amendment. Discovered assets outside scope are reported as observations, not tested.

Boundaries: testing windows, prohibited techniques (DoS, social engineering, physical), data limits, rate limits.

Communication plan: kick-off meeting (objectives, contacts, logistics), daily or milestone status updates if agreed, immediate notification of critical findings and of any signs of real compromise (pre-existing breach), wrap-up meeting, report delivery through a secure channel, debrief/readout.

Professional conduct: honesty about what was and was not tested, no exaggeration of findings, no disparaging the client's staff, conflicts of interest declared, continuous learning. Codes of ethics: (ISC)2, EC-Council, CREST and many employers have them.""",
     "Testers are like trusted locksmiths given a client's keys: their reputation depends entirely on doing exactly what was agreed and nothing more.",
     "During an external test a team noticed signs of a real intruder's web shell. They stopped, called the emergency contact and helped the client's incident response - preserving trust and evidence.",
     "Professionalism and communication are scored in the capstone executive presentation.",
     ["Kick-off", "Authorisation confirmed", "Testing + status updates", "Critical finding? -> notify now", "Wrap-up", "Report + readout"],
     ["Write a kick-off meeting agenda", "Draft a critical-finding notification email", "List what you do if you find evidence of a real attacker"],
     "A client's IT manager verbally asks you to 'also have a quick look' at the HR system. Write your professional response.",
     ["Accepting scope changes verbally", "Waiting until the report to mention a critical issue", "Overstating findings to look impressive"],
     "Stay within written scope, communicate early and honestly, stop when unsure and protect confidentiality.",
     ["Authorisation", "Scope amendment", "Communication plan", "Professional ethics"],
     "If you find signs of an existing compromise, stop and notify the client's emergency contact immediately.",
     [Q("A client asks verbally to add a new server to scope. You should...", ["Test it immediately", "Request a written scope amendment first", "Test it quietly", "Refuse to talk to them"], 1, "Changes need written authorisation."),
      Q("When should a critical finding be communicated?", ["Only in the final report", "Promptly, as agreed in the communication plan", "Never", "On social media"], 1, "Critical issues are notified immediately."),
      TF("Professional testers should clearly state what was not tested.", True, "Honest limitations protect the client and tester.")]),

   L("m14l2", "Evidence Handling, Confidentiality and Data Protection", "Intermediate", 30,
     "Your evidence is sensitive client data. Mishandling it can cause the very breach you were hired to prevent.",
     ["Collect sufficient, minimal evidence", "Protect evidence in storage and transit", "Apply data protection principles to testing data"],
     """Evidence proves your findings: screenshots, request/response pairs, command output and logs. It must be accurate, timestamped and minimal - enough to prove the issue, not more. Store it encrypted, share it only through secure channels and delete it when the contract says. Personal data you encounter is protected by data protection law, so redact it in reports and never keep it longer than necessary.""",
     """Good evidence: shows the target (host/URL), the action, the result and the time; is cropped to the relevant part; has sensitive values redacted (show 'password: ********' or the first characters of a hash only); has a caption explaining what it shows; can be reproduced by the client.

Storage: encrypted disks/containers (BitLocker, LUKS, VeraCrypt), separate per client, access-controlled, no personal cloud storage, no evidence on unencrypted USB sticks.

Transmission: encrypted portals or encrypted archives with passwords sent via a different channel; never plain email attachments for full reports with sensitive data.

Retention: follow the contract (e.g. delete raw data 30-90 days after acceptance); keep a deletion record.

GDPR principles applied: lawfulness, purpose limitation (only for the test), data minimisation, accuracy, storage limitation, integrity and confidentiality, accountability. Report personal data exposure as a finding without copying the data itself.""",
     "Evidence is a forensic sample: labelled, sealed, stored safely and only as large as needed for the test.",
     "A consultancy lost an unencrypted laptop containing client reports; the incident damaged several client relationships. Full-disk encryption would have made it a non-event.",
     "Evidence handling and confidentiality are part of every professional contract and certification code of ethics.",
     ["Capture minimal evidence", "Redact + caption", "Encrypt at rest", "Secure transfer", "Retain per contract", "Delete + record"],
     ["Redact a sample screenshot description", "Write an evidence caption", "List storage and transfer rules"],
     "Write the evidence-handling section of a statement of work.",
     ["Unredacted passwords or personal data in reports", "Sending reports as plain email attachments", "Keeping client data indefinitely"],
     "Capture minimal, captioned, redacted evidence; encrypt it; transfer securely; delete on schedule.",
     ["Evidence", "Redaction", "Data minimisation", "Retention", "Encryption at rest"],
     "Data minimisation and storage limitation are GDPR principles directly relevant to testers.",
     [Q("How should a discovered password appear in evidence?", ["In full", "Redacted", "In bold", "In the title"], 1, "Redact secrets."),
      Q("Which is a secure way to deliver a report?", ["Public file share link", "Encrypted portal or archive with password sent separately", "Plain email", "USB in the post unencrypted"], 1, "Encrypt and separate the key."),
      Q("Storage limitation means...", ["Use small files", "Keep data no longer than necessary", "Compress evidence", "Store only images"], 1, "GDPR storage limitation.")]),

   L("m14l3", "Report Structure: Executive Summary, Scope, Methodology and Limitations", "Intermediate", 40,
     "Executives decide budgets from the first two pages; engineers fix issues from the rest.",
     ["Structure a professional report", "Write an executive summary for non-technical readers", "Document scope, methodology and limitations"],
     """A penetration testing report usually contains: an executive summary (business-level findings and risk), a technical summary, scope, methodology, limitations, detailed findings, a remediation table and appendices. The executive summary avoids jargon, states overall risk, the most important issues and the top recommendations. The scope, methodology and limitations sections explain exactly what was tested, how and what was not covered.""",
     """Executive summary template:
Purpose: 'Silverline Ltd engaged [Firm] to assess its external web portal between 3-7 March 2026.'
Overall posture: 'Overall risk: HIGH. Two issues could allow an internet attacker to access other customers' data.'
Key findings (2-4 bullets, business language).
Positive observations (what was done well - builds trust and balance).
Top recommendations (prioritised, with timeframes).

Technical summary: counts by severity, key attack chains, themes (e.g. access control weaknesses across APIs).

Scope: targets (URLs, IPs, apps, roles/accounts), dates and times, test type (black/grey/white box), exclusions.

Methodology: standards followed (PTES, OWASP WSTG, NIST SP 800-115), phases, tools (by category), severity rating method (CVSS v3.1 / v4.0 plus contextual risk).

Limitations: time-boxed testing, accounts not provided, systems unavailable, rate limits, production constraints, point-in-time nature.

Use the in-app Report Builder: it generates executive and technical summary drafts from your findings, which you then edit.""",
     "The executive summary is the newspaper headline and first paragraph; the technical sections are the full investigative article.",
     "A board approved funding for an MFA rollout within a week because the executive summary explained, in one paragraph, how a single stolen password could expose all customer records.",
     "Report quality is graded in Project 14 and the capstone; it is what employers ask to see in interviews.",
     ["Executive summary", "Technical summary", "Scope", "Methodology", "Limitations", "Findings", "Remediation table", "Appendices"],
     ["Create a new report in the Report Builder for the fictional Silverline engagement", "Write an executive summary under 200 words", "List three limitations for a five-day test"],
     "Rewrite a jargon-filled executive summary into plain business language.",
     ["Technical jargon in the executive summary", "No limitations section", "Missing positive observations"],
     "Structure: executive -> technical -> scope -> methodology -> limitations -> findings -> remediation -> appendices. Write for each audience.",
     ["Executive summary", "Methodology", "Limitations", "Scope"],
     "Executive summaries should state overall risk and business impact without technical jargon.",
     [Q("Who is the main audience of the executive summary?", ["Developers", "Senior management/non-technical decision makers", "Attackers", "Auditors only"], 1, "Leaders who decide priorities and budget."),
      Q("Which section explains what was NOT tested?", ["Appendix", "Limitations", "Executive summary only", "Findings"], 1, "Limitations."),
      TF("Including positive observations in a report is unprofessional.", False, "Balanced reports note what is done well.")]),

   L("m14l4", "Writing Findings: Severity, CVSS, Evidence, Impact, Reproduction and Remediation", "Advanced", 45,
     "A finding that cannot be understood or reproduced will not be fixed.",
     ["Write complete, consistent findings", "Assign severity with CVSS and context", "Write specific, verifiable remediation"],
     """Each finding should have a clear title, severity, affected assets, description, evidence, impact, reproduction context, remediation and references. Severity combines a standard score like CVSS with business context. Impact explains what could happen to the organisation. Remediation must be specific and verifiable, and the remediation table summarises all findings with owners and due dates.""",
     """Finding template:
Title: Insecure Direct Object Reference in Invoice API
Severity: High (CVSS 3.1: 7.7 AV:N/AC:L/PR:L/UI:N/S:C/C:H/I:N/A:N) - adjusted for customer financial data
Affected: https://portal.silverline.test/api/invoices/{id}
Description: The API returns invoices without checking that the requesting user owns them.
Evidence: Request by test user 'learner' for /api/invoices/1002 (owned by test user 'bob') returned HTTP 200 with invoice data (redacted screenshot, Appendix B, 2026-03-04 10:42 UTC).
Impact: Any authenticated customer can view other customers' invoices, exposing names, addresses and purchase history - a likely reportable personal data breach.
Reproduction context: authenticated customer session; change the numeric ID.
Remediation: Enforce ownership checks server-side in the invoice controller using the session user ID; return 404 for objects not owned; add automated authorisation tests per role.
Verification: Retest with two test accounts; expect 404 for cross-account IDs.
References: OWASP API1:2023, CWE-639.

Severity scale: Critical / High / Medium / Low / Informational - define it in the methodology. Remediation table columns: ID, finding, severity, owner, recommended action, target date, status.

Reproduction context is written for defenders to verify, not as a step-by-step weapon - describe conditions and evidence rather than publishing a ready-made attack script.""",
     "A finding is a medical report: diagnosis (title), how serious (severity), test results (evidence), what could happen (impact) and the prescription (remediation).",
     "A client fixed 18 of 20 findings within a month after a tester rewrote vague advice ('improve input validation') as specific, code-level guidance with verification steps.",
     "This is the core professional output of the job and is scored in every project rubric.",
     ["Title", "Severity (CVSS + context)", "Evidence", "Impact (business)", "Remediation (specific)", "Verification"],
     ["Add three findings to your Silverline report in the Report Builder", "Assign CVSS using the calculator", "Generate the remediation table and export the PDF"],
     "Convert three raw lab observations into complete findings using the template.",
     ["Vague remediation", "Severity copied from a scanner without context", "No verification method"],
     "Every finding: clear title, justified severity, minimal evidence, business impact, specific remediation and a verification method.",
     ["Finding", "Severity", "Impact", "Remediation", "Remediation table"],
     "Remediation should be specific enough that a developer knows what to change and how to test it.",
     [Q("Which remediation is best?", ["Improve security", "Enforce server-side ownership checks in the invoice controller and add per-role tests", "Use a firewall", "Be careful"], 1, "Specific and verifiable."),
      Q("Impact should be written in terms of...", ["Tool output", "Business consequences", "Number of packets", "Font size"], 1, "Business consequences."),
      Q("A remediation table typically includes...", ["Owner and target date", "Tester's salary", "Raw packet captures", "Unredacted passwords"], 0, "Tracking fields.")]),

   L("m14l5", "Retesting, Verification and Responsible Disclosure", "Intermediate", 30,
     "The engagement is only successful when the risks are actually reduced.",
     ["Plan and perform retests", "Write retest results", "Apply responsible disclosure outside client engagements"],
     """After the client fixes findings, a retest confirms whether each fix works. You repeat the original test conditions and record each finding as Fixed, Partially fixed, Not fixed or Risk accepted. A retest report or appendix shows the updated status. Outside client work - for example in bug bounties or independent research - responsible disclosure governs how you report vulnerabilities to organisations.""",
     """Retest process: confirm retest scope (only original findings unless agreed) -> obtain fix details from the client -> reproduce the original conditions -> try reasonable variations (fixes are often partial, e.g. one endpoint fixed but another still vulnerable) -> record evidence -> update status -> report.

Status definitions:
• Fixed - original issue and reasonable variants no longer present.
• Partially fixed - some instances or variants remain.
• Not fixed - issue still present.
• Risk accepted - client formally accepted (documented).
• Mitigated - compensating control reduces risk; root cause remains.

Verification also covers configuration drift: recommend continuous checks so fixed issues do not return.

Responsible disclosure (independent research): stay within published bug bounty scope and rules; access the minimum data; report privately via security.txt, VDP or bounty platform; give reasonable time; do not demand payment outside programmes; coordinate publication.""",
     "A retest is the follow-up doctor's appointment: check the treatment worked, and that the illness has not simply moved somewhere else.",
     "A retest found an IDOR fixed on /invoices but still present on /invoices/export - a partial fix the client was glad to catch before customers did.",
     "Retests close the loop and are part of the capstone's final stages.",
     ["Retest scope", "Fix details", "Reproduce original", "Try variants", "Status", "Updated report"],
     ["Retest your Silverline findings in the Report Builder and set their statuses", "Write definitions for each status", "Draft a retest summary paragraph"],
     "Write a retest note for a partially fixed XSS finding.",
     ["Testing only the exact URL from the original finding", "Marking 'fixed' without evidence", "Retesting outside agreed scope"],
     "Retest with original conditions plus variants, record clear statuses with evidence, and disclose responsibly in independent research.",
     ["Retest", "Verification", "Partially fixed", "Vulnerability disclosure programme"],
     "Partial fixes are common - always test variants of the original issue.",
     [Q("A finding fixed on one endpoint but present on another is...", ["Fixed", "Partially fixed", "Risk accepted", "Informational"], 1, "Partially fixed."),
      Q("What should a retest normally cover?", ["Any system the tester likes", "The original findings within agreed retest scope", "Only new features", "Nothing"], 1, "Agreed retest scope."),
      TF("You can mark a finding 'fixed' based on the client's word alone.", False, "Status needs verification evidence.")]),
  ],
  ["Create a report", "Enter client details", "Define scope", "Add findings", "Attach evidence from lab simulations", "Assign severity", "Write remediation advice",
   "Generate executive and technical summaries", "Create a remediation table", "Save drafts offline", "Export a professional PDF report"],
  "p14",
  "Complete all 5 lessons, pass the module assessment and export at least one complete report PDF from the Report Builder.",
  ["Recite report structure", "Write a finding from memory", "Define retest statuses"],
  ["Ethics", "Legal responsibilities", "Rules of engagement", "Authorisation", "Scope", "Testing boundaries", "Client communication", "Evidence handling", "Confidentiality",
   "Data protection", "Risk communication", "Vulnerability classification", "CVSS", "Reporting", "Retesting", "Verification", "Responsible disclosure", "Executive summaries",
   "Technical summaries", "Methodology", "Limitations", "Findings", "Severity", "Evidence", "Impact", "Reproduction context", "Remediation", "Recommendations", "Appendices"])


M("m15", 15, "Practical Labs & Projects", "flask",
  "This module is your practice engine. It explains how to get the most from labs, external practice platforms and projects, and links to the Lab Centre, Project Centre, Challenge Arena and the Silverline capstone.",
  ["Run a safe home lab and use practice platforms legally", "Work through labs methodically with notes and reflection", "Produce project evidence for your portfolio"],
  ["Modules 1-14 (labs unlock progressively)"],
  [
   L("m15l1", "Your Practice Ecosystem: Home Lab and Practice Platforms", "Beginner", 25,
     "Skills come from deliberate, legal practice. Knowing where to practise safely matters.",
     ["Maintain a safe home lab", "Choose suitable practice platforms", "Follow platform rules"],
     """Besides the simulated labs in this app, you can practise on your own isolated virtual lab and on legal training platforms. TryHackMe offers guided rooms for beginners; Hack The Box offers more challenging machines and an academy; PortSwigger Web Security Academy offers free web labs; OWASP Juice Shop is a deliberately insecure web app you run locally. Each platform provides targets you are authorised to attack - never use your skills against systems outside them.""",
     """Home lab maintenance: keep attacker and target VMs on an internal network, snapshot before each session, update your attacker VM, keep notes per machine, never expose vulnerable VMs to the internet, and power them off when not in use.

Platforms (free tiers exist; features and prices change - check current details):
• TryHackMe - browser-based guided learning paths (e.g. Pre-Security, Jr Penetration Tester, SOC Level 1).
• Hack The Box - Machines, Challenges, Academy modules and certification paths.
• PortSwigger Web Security Academy - free, high-quality web labs that map to BSCP.
• OWASP Juice Shop - run locally via Docker: a modern vulnerable shop with a score board.
• Others: VulnHub (downloadable VMs), OverTheWire (wargames), PicoCTF (beginner CTF), Blue Team Labs Online and CyberDefenders (defensive).

Rules: follow each platform's terms; do not share flags/solutions publicly where prohibited; use VPN connections only as instructed; never pivot from a platform to the internet.""",
     "Practice platforms are driving schools with closed tracks: you can practise emergency stops safely - but not on the public road.",
     "Many people hired into junior roles cite consistent practice on legal platforms, with public write-ups of retired machines, as key interview evidence.",
     "Consistent practice builds the skills assessed in practical certifications like eJPT, PNPT and OSCP.",
     ["Pick a goal", "Choose platform/room", "Snapshot + start", "Notes as you go", "Reflection", "Write-up (where allowed)"],
     ["Set up OWASP Juice Shop locally in your lab (optional)", "Create an account on one free platform", "Plan a 4-week practice schedule"],
     "Write your personal lab rules (8-10 rules) and pin them in Notes.",
     ["Practising against random internet hosts", "Posting active-machine solutions publicly", "Exposing vulnerable VMs via bridged networking"],
     "Practise only on your isolated lab and authorised platforms, follow their rules and keep notes.",
     ["Home lab", "TryHackMe", "Hack The Box", "PortSwigger Academy", "OWASP Juice Shop", "CTF"],
     "No exam will reward practice on unauthorised systems - and the law will not either.",
     [Q("Which platform provides free web security labs aligned to BSCP?", ["Hack The Box", "PortSwigger Web Security Academy", "VirusTotal", "Shodan"], 1, "PortSwigger's Academy."),
      Q("OWASP Juice Shop is...", ["A real online shop", "A deliberately insecure training web app", "A firewall", "A password manager"], 1, "A training target."),
      TF("Practising on random internet hosts is acceptable if you do not cause damage.", False, "Unauthorised access is illegal regardless of damage.")]),

   L("m15l2", "Lab Methodology: Notes, Checkpoints, Hints and Reflection", "Beginner", 25,
     "How you practise determines how much you learn from each hour.",
     ["Use a consistent lab workflow", "Use hints without losing learning", "Reflect to convert practice into skill"],
     """Every lab in this app follows the same structure: scenario, objectives, rules, environment, tasks with checkpoints, hints, a knowledge test, reflection questions, a solution explanation and a defensive lesson. Work methodically: read the rules, make a plan, take notes of every command and result, try before using hints, and always finish with the reflection and the defensive lesson - that is where understanding is cemented.""",
     """Recommended workflow:
1. Read scenario, rules and objectives - restate the goal in one sentence.
2. Plan your approach (which phases, which tools).
3. Execute one step at a time; record command, output and interpretation in Notes.
4. Stuck for 15-20 minutes? Use the first hint. Hints are graded: nudge -> direction -> near-solution.
5. Check checkpoints - each confirms a milestone.
6. Complete the knowledge test.
7. Answer reflection questions honestly (what worked, what did not, what would you do differently?).
8. Read the defensive lesson: how would a defender prevent or detect what you did?
9. Write a short finding as if for a report.

Scoring in this app: tasks completed, hints used (small deductions), knowledge test score and completion. Replay labs later for spaced repetition.

Note-taking tools (free): the in-app Notes, Obsidian, Joplin, CherryTree - organised per lab with screenshots.""",
     "Labs without notes are like gym sessions without tracking - you work hard but cannot see progress or repeat what worked.",
     "Candidates who kept structured notes found practical exams far easier because they had a personal, searchable playbook.",
     "Reflection and defensive lessons turn offensive practice into the balanced skills employers want.",
     ["Read + plan", "Execute + note", "Hints if stuck", "Checkpoints", "Knowledge test", "Reflect + defend", "Write a finding"],
     ["Complete Linux Navigation lab using the full workflow", "Write a reflection for that lab", "Write a mini-finding from it"],
     "Create a reusable note template for labs and save it in Notes.",
     ["Jumping to solutions immediately", "Skipping reflection", "Notes without outputs or interpretation"],
     "Plan, note, try before hints, reflect and learn the defence - every time.",
     ["Checkpoint", "Reflection", "Hint", "Spaced repetition"],
     "Spaced repetition - reviewing material at increasing intervals - improves long-term retention.",
     [Q("When should you use a hint?", ["Immediately", "After a genuine attempt when stuck", "Never", "Only after finishing"], 1, "Struggle productively first."),
      Q("What is the purpose of the defensive lesson?", ["Decoration", "To learn how to prevent and detect the technique", "To add time", "To unlock badges only"], 1, "Balanced, defensive understanding."),
      Q("Replaying labs later supports...", ["Spaced repetition", "Scope creep", "Lateral movement", "Data exfiltration"], 0, "Spaced retrieval practice.")]),

   L("m15l3", "Project-Based Learning, Rubrics and Portfolio Evidence", "Intermediate", 25,
     "Employers hire evidence of skill. Projects create that evidence.",
     ["Understand project rubrics", "Produce portfolio-quality deliverables", "Prepare for the Silverline capstone"],
     """Projects combine several skills into a realistic task - for example auditing a Linux server or assessing a mock API - and produce a deliverable such as a checklist, network map or report. Each project in this app has tasks, a deliverable and a grading rubric. Completed projects are added to your Portfolio automatically, and the final project is the Silverline Enterprise capstone, a full simulated engagement from client briefing to retest.""",
     """Rubric dimensions used across projects: Accuracy, Methodology, Safety & scope compliance, Technical reasoning, Evidence quality, Risk classification, Remediation quality, Report quality, Professional communication.

Rubric levels: Excellent (4), Good (3), Developing (2), Insufficient (1). A project passes at an average of 2.5+ with no 'Insufficient' in Safety & scope.

Portfolio-quality deliverables: clear title and objective, scope statement (fictional organisation), method, findings, remediation, lessons learned; screenshots from labs (no real third-party data); consistent formatting. Export from the Portfolio centre as PDF.

Public portfolio tips: GitHub repository with project write-ups and scripts (no secrets, no real targets), a blog for retired-machine write-ups where platform rules allow, a one-page summary for your CV.

Capstone preparation: revise Modules 6-14, practise the Report Builder, review your weak areas in the Progress centre.""",
     "Projects are the apprentice's 'show pieces' - proof you can build the whole cabinet, not just cut wood.",
     "Hiring managers frequently ask candidates to walk through a report or project from their portfolio in interviews.",
     "The Portfolio and Career centres use your completed projects to calculate job readiness.",
     ["Read brief + rubric", "Plan", "Do tasks", "Produce deliverable", "Self-assess vs rubric", "Add to portfolio"],
     ["Open Project 1 and read its rubric", "Self-assess a previous lab write-up against the rubric", "Plan your capstone revision"],
     "Write a portfolio summary (150 words) of any project you have completed.",
     ["Ignoring the rubric", "Including real third-party data in a public portfolio", "Unfinished projects in the portfolio"],
     "Projects + rubrics + reflection = portfolio evidence that gets interviews.",
     ["Rubric", "Portfolio", "Capstone", "Deliverable"],
     "Safety and scope compliance is a gating criterion in every project rubric in this app.",
     [Q("Which rubric dimension is a gating criterion in this app?", ["Report formatting", "Safety and scope compliance", "Speed", "Tool count"], 1, "Insufficient safety fails the project."),
      Q("What belongs in a public portfolio?", ["Real client data", "Write-ups of fictional/lab projects without secrets", "Passwords", "Active-machine flags"], 1, "Lab and fictional work only."),
      TF("The capstone is a simulated engagement against a fictional company.", True, "Silverline Enterprise is fully simulated.")]),
  ],
  ["All lab categories at five difficulty levels in the Lab Centre", "14 core projects in the Project Centre", "Challenge Arena", "Silverline capstone"],
  "p14",
  "Complete the 3 lessons and at least 8 labs across categories. Projects are tracked in the Project Centre.",
  ["Recite your lab workflow", "List rubric dimensions", "Plan capstone revision"],
  ["Lab categories", "Difficulty levels", "Lab structure", "Core projects", "Capstone", "Rubrics"])


M("m16", 16, "Career & Certifications", "briefcase",
  "Turn skills into a career. Explore roles, map certifications (informational - always verify current details with the provider), build your CV and portfolio, and practise interviews.",
  ["Compare cybersecurity roles and pathways", "Map certifications to your goals", "Build a CV, LinkedIn profile and portfolio", "Prepare for technical and practical interviews"],
  ["None - can be studied at any time"],
  [
   L("m16l1", "Cybersecurity Career Paths and Roles", "Beginner", 30,
     "Knowing the roles helps you choose a direction and target your learning.",
     ["Describe offensive, defensive and engineering roles", "Map roles to skills and modules", "Plan a realistic entry route"],
     """Cybersecurity has many roles. Offensive roles (ethical hacker, penetration tester, red team operator) find weaknesses. Defensive roles (SOC analyst, blue team analyst, incident responder, digital forensics analyst) detect and respond to attacks. Engineering and architecture roles (security engineer, cloud security engineer, application security engineer, DevSecOps engineer, security architect) build secure systems. Advisory roles (security consultant, vulnerability analyst) assess and guide. Many people enter through IT support, networking or SOC roles.""",
     """Role snapshots (skills -> key modules):
• SOC Analyst (entry): logs, SIEM, alert triage, networking -> M2, M3, M5, M7, M12.
• Vulnerability Analyst: scanning, CVSS, prioritisation -> M7, M8.
• Penetration Tester / Ethical Hacker: methodology, web, network, AD, reporting -> M6-M14.
• Red Team Operator (senior): adversary emulation, stealth tradecraft (beyond this course's scope), detection awareness.
• Blue Team / Incident Responder / Digital Forensics: IR lifecycle, forensics, malware triage -> M5, M12, M13.
• Application Security Engineer: secure code, SAST/DAST, threat modelling -> M4, M9, M13.
• Cloud Security Engineer: IAM, cloud config, containers -> M13.
• DevSecOps Engineer: CI/CD security, supply chain -> M4, M13.
• Security Engineer / Architect: design controls, zero trust -> M5, M13.
• Security Consultant: assessments plus communication -> M14.

Entry routes: IT support/helpdesk -> SOC; networking/sysadmin -> security engineer/pen tester; developer -> AppSec. Apprenticeships and graduate schemes exist in many countries; public sector and NHS-type organisations hire security analysts too.""",
     "Cybersecurity is like a hospital: there are surgeons (pen testers), emergency doctors (incident responders), infection-control teams (security engineers) and administrators who keep everything compliant (GRC).",
     "Many senior penetration testers started on a service desk, moved to a SOC, then specialised after building a lab and earning a practical certification.",
     "The Career Centre's role comparison and job readiness score use these mappings.",
     ["Foundation (IT/networking)", "Entry security role (SOC/vuln analyst)", "Specialise (pen test/cloud/AppSec)", "Senior/lead", "Architect/manager"],
     ["Open the Career Centre and compare three roles", "Pick a target role and list its top five skills", "Map your completed modules to that role"],
     "Write a 12-month career plan from your current position to your first security role.",
     ["Aiming only at 'hacker' roles without foundations", "Ignoring defensive roles", "Collecting certifications without practical skills"],
     "Choose a direction (offence, defence, engineering, advisory), build foundations, enter via a realistic route and specialise.",
     ["SOC analyst", "Penetration tester", "Incident responder", "AppSec engineer", "DevSecOps"],
     "SOC analyst and vulnerability analyst are common entry-level security roles.",
     [Q("Which role mainly monitors alerts and triages incidents?", ["Security architect", "SOC analyst", "Red team operator", "AppSec engineer"], 1, "Security Operations Centre analyst."),
      Q("Which role focuses on secure code and SAST/DAST?", ["Application security engineer", "Network engineer", "Help desk", "Forensics analyst"], 0, "AppSec engineer."),
      TF("Many security professionals enter through IT support or networking roles.", True, "Common, realistic entry routes.")]),

   L("m16l2", "Certification Pathways (Informational)", "Beginner", 30,
     "Certifications help pass CV filters and structure learning, but only practical skill sustains a career.",
     ["Compare entry, intermediate and advanced certifications", "Distinguish knowledge-based and practical exams", "Plan a certification route"],
     """Certifications show employers you have reached a standard. Some are multiple-choice knowledge exams (CompTIA Security+, CEH multiple-choice), others are practical, hands-on exams (eJPT, PNPT, OSCP, Burp Suite Certified Practitioner). This app maps its modules to common certifications for guidance only. Exam formats, prerequisites and prices change, so always check the official provider's website before booking. This app is not affiliated with or endorsed by any certification body.""",
     """Overview (verify current details with each provider):
• CompTIA Security+ - foundational security knowledge; multiple-choice and performance-based questions; widely requested in job adverts. Modules 1-8, 14.
• eJPT (INE) - entry-level practical penetration testing exam. Modules 2, 3, 6-10.
• PNPT (TCM Security) - practical external/internal test including AD, plus a written report and debrief. Modules 6-14.
• CEH (EC-Council) - broad ethical hacking knowledge; a separate practical exam exists. Modules 5-13.
• OSCP (OffSec, course PEN-200) - demanding hands-on exam requiring a professional report. Modules 6-14 plus extensive practice.
• Burp Suite Certified Practitioner (PortSwigger) - practical web application exam. Module 9 + PortSwigger Academy.
Other relevant: CompTIA Network+/CySA+/PenTest+, ISC2 CC (entry) and CISSP (senior, experience required), GIAC certifications (GSEC, GPEN, GWAPT, GCIH), cloud provider security certifications (AWS Security Specialty, Microsoft SC-200/AZ-500), CREST CPSA/CRT (UK-recognised for penetration testing).

Route examples: beginner -> Security+ -> eJPT -> PNPT/OSCP; web focus -> PortSwigger Academy -> BSCP; defensive -> Security+ -> CySA+/SC-200 -> GCIH.

The Career Centre shows each certification card with an 'Informational - verify with provider' label and links that open in your browser when you choose.""",
     "Certifications are like driving test passes: they prove a standard at a moment in time, but your driving record (portfolio and experience) shows what you can do every day.",
     "Candidates with Security+ and a strong lab portfolio often progress to interviews where practical tasks decide the outcome.",
     "Mock exams in the Examination Centre help prepare for knowledge-based exams; labs and projects build practical-exam skills.",
     ["Pick target role", "Choose foundation cert", "Add practical cert", "Specialise", "Maintain (CPE)"],
     ["Open the Certification pathway screen and pick two target certifications", "Check the official website for current exam details", "Take the mock certification exam in the Examination Centre"],
     "Build a certification plan with timelines and a study budget, noting which details you verified and when.",
     ["Trusting outdated price or format information", "Collecting certificates without hands-on practice", "Assuming one certification guarantees a job"],
     "Certifications structure learning and open doors; verify details with providers and pair them with practical skill.",
     ["Security+", "eJPT", "PNPT", "OSCP", "CEH", "BSCP"],
     "Practical certifications (OSCP, PNPT, eJPT, BSCP) require hands-on exploitation and/or reporting under exam conditions.",
     [Q("Which certification is a practical web application exam from PortSwigger?", ["CEH", "BSCP", "Security+", "CISSP"], 1, "Burp Suite Certified Practitioner."),
      Q("Which is generally considered a foundational knowledge certification?", ["OSCP", "CompTIA Security+", "PNPT", "OSEP"], 1, "Security+."),
      TF("Certification prices and formats should be verified with the provider before booking.", True, "They change over time.")]),

   L("m16l3", "CV, LinkedIn and Portfolio", "Beginner", 30,
     "Your CV and portfolio get you the interview; make them evidence-based.",
     ["Write a security-focused CV", "Optimise a LinkedIn profile", "Present a portfolio responsibly"],
     """A strong cybersecurity CV is short (one to two pages), tailored to the role and full of evidence: skills demonstrated through projects, labs, certifications and work achievements with results. LinkedIn should match your CV, with a clear headline, summary and featured projects. Your portfolio - exported from this app or hosted on GitHub - shows real work such as reports and scripts based on fictional or lab environments.""",
     """CV structure: name and contact (no full address needed), headline (e.g. 'Aspiring Penetration Tester | Security+ | Home Lab & 14 Projects'), profile (3-4 lines), key skills (grouped: networking, Linux/Windows, web security, scripting, reporting), experience (achievement bullets: action + tool + result, e.g. 'Automated weekly patch compliance reporting with PowerShell, cutting manual effort by 4 hours/week'), projects (2-4 with one-line outcomes), certifications, education.

Transferable experience counts: IT support (troubleshooting, users, AD), healthcare systems, networking, development.

LinkedIn: professional photo, headline with target role, About section with story and skills, Featured (portfolio PDF, write-ups), skills endorsed, follow and engage with the community, post lab learnings responsibly.

Portfolio: 3-6 best projects; each with objective, scope (fictional), method, key findings, remediation, lessons; capstone report; scripts on GitHub with README; no secrets, no real targets, no active-platform solutions.

The in-app CV Builder generates a formatted PDF from your entries and pulls in completed projects and certificates.""",
     "A CV is a movie trailer: show the best scenes (achievements) that make the recruiter want to see the whole film (interview).",
     "A career changer from hospital IT support highlighted EPR access-control work, an AD clean-up and home lab projects - and secured a SOC analyst role.",
     "Recruiters often filter CVs in seconds; evidence and keywords matter.",
     ["Target role", "Tailored headline + profile", "Achievement bullets", "Projects + certs", "Portfolio link", "Proofread"],
     ["Fill in the CV Builder in the Career Centre", "Rewrite three duties as achievement bullets", "Export your portfolio PDF"],
     "Tailor your CV to a real junior security job advert and highlight five matching keywords.",
     ["Listing tools without evidence", "Generic CVs sent to every job", "Publishing real client or employer data"],
     "Tailor, quantify, evidence and link a responsible portfolio.",
     ["CV", "Portfolio", "LinkedIn", "Achievement bullet"],
     "Use the action + tool + result format for experience bullets.",
     [Q("Which CV bullet is strongest?", ["Responsible for security", "Used PowerShell", "Automated patch reporting with PowerShell, saving 4 hours/week", "Did IT things"], 2, "Action + tool + result."),
      Q("What should a public portfolio exclude?", ["Lab write-ups", "Real client data and secrets", "Scripts with READMEs", "Capstone report on a fictional company"], 1, "Never publish real sensitive data."),
      TF("Transferable IT experience is relevant on a cybersecurity CV.", True, "Troubleshooting, AD and networking are valuable.")]),

   L("m16l4", "Interviews, Practical Simulations, Communication and Career Progression", "Intermediate", 35,
     "Interviews test how you think, communicate and behave - not just what you know.",
     ["Answer technical interview questions with structure", "Handle practical interview tasks", "Plan long-term career progression"],
     """Security interviews usually combine behavioural questions (teamwork, ethics, handling pressure), technical questions (networking, operating systems, web vulnerabilities, incident handling) and sometimes a practical task (analyse logs, review a report, solve a small lab). Use structure: explain your reasoning, say what you would check next and admit what you do not know - then explain how you would find out. Communication, curiosity and ethics are as important as technical depth.""",
     """Answer frameworks:
• STAR for behavioural questions: Situation, Task, Action, Result.
• Technical questions: define -> explain how it works -> security relevance -> example -> mitigation. E.g. 'What is XSS?' -> definition, types, impact, fix.
• Scenario questions ('You see 500 failed logins from one IP...'): clarify -> triage -> investigate -> contain -> communicate -> document.

Common technical topics: OSI/TCP handshake, DNS, common ports, HTTP status codes, OWASP Top 10, SQLi vs XSS, hashing vs encryption, Kerberos basics, privilege escalation concepts, incident response phases, CVSS, how you would test a login page, how you would explain a finding to a manager.

Practical simulations: log analysis, PCAP triage, small web app, report critique. The in-app Interview Simulator gives timed questions with model answers.

Professional communication: tailor detail to the audience, be concise, use risk language with managers, be precise with engineers.

Career progression: junior -> mid (independent engagements, mentoring) -> senior (lead tests, scoping, quality review) -> principal/manager/architect. Keep learning: conferences (BSides events are low-cost), communities, research, CPE credits.""",
     "An interview is a short lab where the target is your reasoning: interviewers want to watch how you approach the problem.",
     "A candidate who said 'I am not sure, but I would check the Security event log for 4625 events and correlate the source IP' impressed more than one who guessed confidently and was wrong.",
     "The Interview Simulator and job readiness score in the Career Centre bring this lesson together.",
     ["Prepare stories (STAR)", "Revise core topics", "Practise simulator", "Interview: reason aloud", "Follow up", "Reflect + improve"],
     ["Complete a session in the Interview Simulator", "Write three STAR stories", "Answer 'What is the difference between hashing and encryption?' aloud in under 60 seconds"],
     "Prepare a 2-minute explanation of a finding from your capstone for a non-technical manager.",
     ["Bluffing answers", "Ignoring ethics questions", "Not asking the interviewer any questions"],
     "Structure answers, reason aloud, be honest about gaps, communicate for the audience and keep learning.",
     ["STAR method", "Technical interview", "Practical assessment", "CPE"],
     "If you do not know, say so and explain how you would find out - interviewers value honesty and method.",
     [Q("STAR stands for...", ["Scan, Test, Attack, Report", "Situation, Task, Action, Result", "Start, Think, Act, Rest", "Security, Trust, Access, Risk"], 1, "Situation, Task, Action, Result."),
      Q("Asked something you do not know, the best response is...", ["Guess confidently", "Admit it and explain how you would find out", "Change the subject", "End the interview"], 1, "Honesty plus method."),
      Q("When explaining a finding to a manager, focus on...", ["Packet-level detail", "Business risk and recommended action", "Tool versions", "Your hacking speed"], 1, "Business risk and action.")]),
  ],
  ["CV builder", "Portfolio", "LinkedIn preparation", "Interview questions", "Technical interviews", "Practical interview simulations", "Career roadmap", "Job role comparison",
   "Skills assessment", "Portfolio project tracker", "Job readiness score", "Career progression planner"],
  "p14",
  "Complete all 4 lessons, build your CV in the CV Builder and complete one Interview Simulator session.",
  ["Name 10 roles", "Map 4 certifications to modules", "Write 3 STAR stories"],
  ["Career paths", "Certification pathways", "CV builder", "Portfolio", "LinkedIn", "Interview questions", "Technical interviews", "Interview simulations", "Professional communication",
   "Report writing", "Career roadmap", "Role comparison", "Skills assessment", "Job readiness", "Career progression"])
