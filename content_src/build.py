"""Compile content_src/*.py into app/src/main/assets/content/*.json"""
import json, os, sys, importlib, datetime
HERE = os.path.dirname(os.path.abspath(__file__))
sys.path.insert(0, HERE)
OUT = os.path.join(HERE, "..", "app", "src", "main", "assets", "content")
os.makedirs(OUT, exist_ok=True)
import lib
for m in ["m01_computers", "m02_networking", "m03_os", "m04_programming", "m05_secfund", "m06_08", "m09_10", "m11_13", "m14_16"]:
    importlib.import_module(m)
from labs import LABS
from projects import PROJECTS, CHALLENGES, CAPSTONE
from reference import GLOSSARY, TOOLS, CAREER
from final_exam import FINAL
mods = sorted(lib.MODULES, key=lambda m: m["number"])
def dump(name, obj):
    with open(os.path.join(OUT, name), "w", encoding="utf-8") as f:
        json.dump(obj, f, ensure_ascii=False, separators=(",", ":"))
dump("modules.json", mods)
dump("labs.json", LABS)
dump("projects.json", PROJECTS)
dump("challenges.json", CHALLENGES)
dump("capstone.json", CAPSTONE)
dump("glossary.json", sorted(GLOSSARY, key=lambda g: g["term"].lower()))
dump("tools.json", TOOLS)
dump("career.json", CAREER)
dump("final_exam.json", FINAL)
meta = {"courseVersion": "2026.1", "contentVersion": "1.0.0", "updated": "2026-11-25",
        "history": [{"version": "1.0.0", "date": "2026-11-25", "notes": "Initial release: 16 modules, %d lessons, %d labs, %d projects, %d challenges, 17-stage capstone, %d-question final exam, %d glossary terms, %d tools." %
                     (sum(len(m["lessons"]) for m in mods), len(LABS), len(PROJECTS), len(CHALLENGES), len(FINAL), len(GLOSSARY), len(TOOLS))}]}
dump("meta.json", meta)
print(meta["history"][0]["notes"])
