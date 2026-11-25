# Ethical Hacking Roadmap 2026/2027
### by Bernportal System Innovations — *Learn. Practice. Secure. Become.*

A premium, offline-first Android cybersecurity academy that takes a complete beginner to job-ready ethical hacker / security professional.
Kotlin · Jetpack Compose · Material 3 · MVVM · Room · DataStore · WorkManager · 100% free and open tooling (no paid SDKs).

## What's inside
| Area | Contents |
|---|---|
| Curriculum | 16 modules, 84 lessons (20-part lesson format), 252 lesson quiz questions, "Explain this again" (7 styles), 8 teaching modes |
| Assessments | Lesson quizzes, module exams, timed exams, revision tests, mock certification exam, **100-question final exam** (objective, practical output-analysis and lab-scenario questions) |
| Labs | 23 labs at 5 difficulty levels: 11 simulated-terminal labs (Linux, PowerShell, Nmap, tshark, curl web lab, Docker, password-audit analysis, IR logs) + 12 scenario labs. Debrief, knowledge test, reflection, solution, defensive lesson, badge |
| Practice Centre | 14 interactive simulators: animated OSI, TCP handshake, packet journey, subnet/CIDR visualiser, DNS, DHCP, routing, topology builder, ports, converter, hashing, password analyser, CVSS v3.1, SQLi visualiser |
| Projects | 14 projects with tasks, deliverables and rubrics (safety is a gating criterion) |
| Challenge Arena | 20 timed (optional) challenges with hints, scoring and replay |
| Capstone | *Silverline Enterprise Security Assessment* — 17 stages scored on 10 professional criteria; generates a draft report, portfolio entry and certificate |
| Report Builder | Client/scope/methodology/limitations, findings with CVSS, lab evidence attachment, auto executive & technical summaries, remediation table, **PDF export** |
| AI Tutor | 11 modes, quick actions, lesson context, sessions, voice input, attachments, save to notes/flashcards. Uses the learner's **own free key** (Gemini / Groq / OpenRouter / self-hosted OpenAI-compatible). Local safety filter + offline course assistant |
| Knowledge | Offline search with filters, 151-term glossary, 27-entry toolkit + command library, notes, bookmarks, Leitner flashcards |
| Progress | XP, levels, streaks, 40+ badges, skill radar, weak/strong areas, study-time chart, career readiness |
| Career | 15 role paths with comparison, 7 certification pathways (informational), interview simulator (20 Qs), CV builder (PDF), LinkedIn checklist, readiness score, progression planner, portfolio PDF |
| Platform | Animated 3D intro, onboarding & personalised plan, 4 themes, reduced-motion, 3D toggle, font scaling, dyslexia mode, high contrast, reminders, backup/restore (Google Drive via the system picker), offline content-update packs, data export/deletion, in-app content validation |

## Build
**Easiest (no local setup):** push to `main` — GitHub Actions builds debug and release APKs. Download from the workflow run's *Artifacts*, or from the `ci-results` branch.

**Android Studio:** open the folder (Koala or newer), let Gradle sync, then *Run* or *Build > Build APK(s)*.

**Command line:** JDK 17 + Android SDK 34:
```
./gradlew testDebugUnitTest assembleDebug        # app/build/outputs/apk/debug/
./gradlew assembleRelease                        # signed with debug key unless secrets are set
```
**Release signing (optional):** add repository secrets `EHR_KEYSTORE_BASE64`, `EHR_KEYSTORE_PASSWORD`, `EHR_KEY_ALIAS`, `EHR_KEY_PASSWORD`. No secrets are stored in the code.

## Editing course content
Content is authored in `content_src/*.py` and compiled to `app/src/main/assets/content/*.json`:
```
python3 content_src/build.py && python3 tools/validate_content.py
```
The validator fails the build on missing lessons, empty content, missing quizzes/lab instructions, broken links or wrong module order.

## Safety & ethics
All labs are simulations of fictional organisations; no command executes on the device and labs cannot reach real systems. The AI tutor refuses requests that would harm real people or systems. Certificates certify this app's curriculum only and are not third-party certifications. Not affiliated with or endorsed by any university, certification body or organisation mentioned.

© 2026 Bernportal System Innovations.
