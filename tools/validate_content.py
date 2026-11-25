"""Content validation: fails the build if lessons, quizzes, labs or links are missing or broken."""
import json, os, re, sys
D = os.path.join(os.path.dirname(os.path.abspath(__file__)), "..", "app", "src", "main", "assets", "content")
def load(n): return json.load(open(os.path.join(D, n), encoding="utf-8"))
mods, labs, projects, ch, cap, glo, tools, career, final, meta = [load(n) for n in
    ["modules.json", "labs.json", "projects.json", "challenges.json", "capstone.json", "glossary.json", "tools.json", "career.json", "final_exam.json", "meta.json"]]
errors, warns = [], []
lesson_ids = set(); mod_ids = [m["id"] for m in mods]; lab_ids = {l["id"] for l in labs}; proj_ids = {p["id"] for p in projects}
if len(mods) != 16: errors.append("Expected 16 modules, found %d" % len(mods))
if [m["number"] for m in mods] != list(range(1, 17)): errors.append("Incorrect module sequence")
req = ["title", "why", "objectives", "simple", "detailed", "analogy", "example", "relevance", "flow", "practice", "challenge", "mistakes", "summary", "terms", "examTip", "quiz"]
def check_q(q, where):
    if len(q["options"]) < 2: errors.append(where + ": fewer than 2 options")
    for a in q["answer"]:
        if a < 0 or a >= len(q["options"]): errors.append(where + ": answer index out of range")
    if not q.get("explain"): errors.append(where + ": missing explanation")
for m in mods:
    if not m["lessons"]: errors.append(m["id"] + ": no lessons")
    if m["project"] not in proj_ids: errors.append(m["id"] + ": broken project link " + m["project"])
    for l in m["lessons"]:
        if l["id"] in lesson_ids: errors.append("Duplicate lesson id " + l["id"])
        lesson_ids.add(l["id"])
        for k in req:
            if not l.get(k): errors.append(l["id"] + ": empty " + k)
        if len(l["quiz"]) < 3: errors.append(l["id"] + ": fewer than 3 quiz questions")
        if len(l["detailed"]) < 400: warns.append(l["id"] + ": detailed explanation is short")
        for i, q in enumerate(l["quiz"]): check_q(q, l["id"] + " q" + str(i + 1))
for lab in labs:
    if lab["module"] not in mod_ids: errors.append(lab["id"] + ": unknown module")
    if lab["type"] == "terminal":
        task_ids = {t["id"] for t in lab["tasks"]}
        covered = {c["task"] for c in lab["commands"] if c["task"]}
        if not lab["tasks"]: errors.append(lab["id"] + ": no tasks")
        for t in task_ids - covered: errors.append(lab["id"] + ": task %s has no completing command" % t)
        for c in lab["commands"]:
            try: re.compile(c["match"])
            except re.error as e: errors.append(lab["id"] + ": bad regex " + c["match"] + " " + str(e))
    else:
        if not lab["steps"]: errors.append(lab["id"] + ": no steps")
        for i, s in enumerate(lab["steps"]): check_q({"options": s["options"], "answer": [s["answer"]], "explain": s["explain"]}, lab["id"] + " step" + str(i + 1))
    for k in ["scenario", "objectives", "solution", "defensive", "badge", "reflection", "questions"]:
        if not lab.get(k): errors.append(lab["id"] + ": missing " + k)
    for i, q in enumerate(lab["questions"]): check_q(q, lab["id"] + " kq" + str(i + 1))
for p in projects:
    if not p["tasks"] or not p["rubric"]: errors.append(p["id"] + ": unfinished project")
    for lid in p["labs"]:
        if lid not in lab_ids: errors.append(p["id"] + ": broken lab link " + lid)
if len(projects) != 14: errors.append("Expected 14 projects")
for c in ch: check_q({"options": c["options"], "answer": [c["answer"]], "explain": c["explain"]}, c["id"])
if len(cap["stages"]) != 17: errors.append("Capstone must have 17 stages")
for s in cap["stages"]: check_q({"options": s["options"], "answer": [s["answer"]], "explain": s["explain"]}, "capstone " + str(s["n"]))
if len(final) != 100: errors.append("Final exam must have 100 questions, found %d" % len(final))
for i, q in enumerate(final):
    check_q(q, "final q" + str(i + 1))
    if q["module"] not in mod_ids: errors.append("final q%d: unknown module" % (i + 1))
covered_mods = {q["module"] for q in final}
for mid in mod_ids:
    if mid not in covered_mods: errors.append("Final exam does not cover " + mid)
for g in glo:
    if g["lesson"] not in lesson_ids: errors.append("Glossary '%s' links to missing lesson %s" % (g["term"], g["lesson"]))
    if g["lab"] and g["lab"] not in lab_ids: errors.append("Glossary '%s' links to missing lab %s" % (g["term"], g["lab"]))
for t in tools:
    for lid in t["lessons"]:
        if lid not in lesson_ids: errors.append("Tool %s: missing lesson %s" % (t["id"], lid))
    for lid in t["labs"]:
        if lid not in lab_ids: errors.append("Tool %s: missing lab %s" % (t["id"], lid))
cats = {"objective": 0, "practical": 0, "lab": 0}
for q in final: cats[q["category"]] += 1
print("Modules: %d | Lessons: %d | Lesson quiz Qs: %d | Labs: %d | Projects: %d | Challenges: %d | Capstone stages: %d | Final exam: %d %s | Glossary: %d | Tools: %d" % (
    len(mods), len(lesson_ids), sum(len(l["quiz"]) for m in mods for l in m["lessons"]), len(labs), len(projects), len(ch), len(cap["stages"]), len(final), cats, len(glo), len(tools)))
for w in warns: print("WARN", w)
for e in errors: print("ERROR", e)
sys.exit(1 if errors else 0)
