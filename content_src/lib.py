"""Authoring helpers for Ethical Hacking Roadmap course content.
Content is written in Python for readability and compiled to JSON in app/src/main/assets/content.
"""

MODULES = []


def Q(q, options, answer, explain, kind="single"):
    """Quiz question. answer = index (single/truefalse) or list of indexes (multi)."""
    return {"q": q, "options": options, "answer": answer if isinstance(answer, list) else [answer],
            "explain": explain, "type": kind}


def TF(q, true, explain):
    return Q(q, ["True", "False"], 0 if true else 1, explain, "truefalse")


def L(id, title, difficulty, minutes, why, objectives, simple, detailed, analogy, example,
      relevance, flow, practice, challenge, mistakes, summary, terms, exam, quiz):
    return {
        "id": id, "title": title, "difficulty": difficulty, "minutes": minutes, "why": why,
        "objectives": objectives, "simple": simple.strip(), "detailed": detailed.strip(),
        "analogy": analogy, "example": example, "relevance": relevance, "flow": flow,
        "practice": practice, "challenge": challenge, "mistakes": mistakes, "summary": summary,
        "terms": terms, "examTip": exam, "quiz": quiz,
    }


def M(id, number, title, icon, intro, objectives, prereq, lessons, exercises, project, completion, revision, topics):
    m = {"id": id, "number": number, "title": title, "icon": icon, "intro": intro, "objectives": objectives,
         "prerequisites": prereq, "lessons": lessons, "exercises": exercises, "project": project,
         "completion": completion, "revision": revision, "topics": topics}
    for i, l in enumerate(lessons):
        l["module"] = id
        l["order"] = i
    MODULES.append(m)
    return m
