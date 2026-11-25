# Testing report

## Automated (run on every push by GitHub Actions)
| Suite | What it checks |
|---|---|
| `tools/validate_content.py` | 16 modules in order; every lesson has all 20 parts and >=3 quiz questions; every quiz answer index valid; every terminal-lab task can be completed by at least one command; regexes compile; decision-lab steps valid; 14 projects with tasks and rubrics; 17 capstone stages; final exam exactly 100 questions covering all 16 modules; glossary/tool links resolve to real lessons and labs |
| `SecToolsTest` (JUnit) | CVSS v3.1 maths (9.8, 10.0, 5.4, 0.0 reference vectors), subnet maths (/27, mask notation, private/public, invalid input), base conversion, SHA-256, password estimator |
| `ContentTest` (JUnit) | Bundled JSON assets: module order, lesson completeness, final exam size, capstone size, lab regex compilation |
| Gradle build | Kotlin compilation of all screens, Room schema generation (KSP), R8 release shrinking |

## In-app
Settings > Content validation runs the same structural checks on the installed content (including navigation routes and drawable assets) and shows a per-module content checklist.

## Manual test checklist (recommended on a device)
- Intro plays and can be skipped; Settings > Animation = Off makes it instant.
- Onboarding saves level/goals/pace; Module 1 open, Module 2 locked until Module 1 exam >= 70%.
- Lesson: all 8 modes, Explain this again, bookmark, notes, mark complete.
- Terminal lab: `help`, `hint`, `tasks`, completing all checkpoints, debrief, badge.
- Quiz: instant feedback; timed exam auto-submits; results show module breakdown.
- Report Builder: add findings, attach lab evidence, generate summaries, export PDF.
- Certificates: issued after module lessons + exam; PDF export.
- Airplane mode: every screen except AI tutor online mode keeps working; tutor falls back to the offline assistant.
- Backup to Google Drive via the picker; restore on a fresh install.
