#!/usr/bin/env python3
r"""
biome_feature_order.py  --  biome feature-order toolkit
=================================================================

Keeps the vanilla (minecraft:) features in every biome's `features` array in the
canonical vanilla order (per generation step) so the mod doesn't trigger a
"Feature order cycle found" crash alongside other biome mods.

USAGE
-----
    python biome_feature_order.py verify   [files...]      # report ordering problems
    python biome_feature_order.py diagnose [files...]      # show current vs proposed order
    python biome_feature_order.py fix      [files...]      # dry-run the slot-method reorder
    python biome_feature_order.py fix      [files...] --apply
    python biome_feature_order.py cycle    [--no-baseline] # global FeatureSorter cycle check

WHICH FILES GET PROCESSED
-------------------------
1. Any files you pass on the command line (see "file forms" below), else
2. the FILES list in the EDIT-HERE frame below, else
3. every biome under the mod (use this for a full audit; `cycle` always does this).

File forms accepted (mix freely):
    plains                         bare biome name (searched in all biome dirs)
    minecraft:plains               namespace:name
    data/minecraft/worldgen/biome/plains.json    path relative to MOD_ROOT
    C:\full\path\to\plains.json    absolute path

NOTES
-----
* `fix` auto-detects which steps are out of order; you never list step indices.
* `fix` uses the SLOT METHOD: custom (non-`minecraft:`) features stay pinned at
  their exact index; only vanilla features are sorted into the vanilla slots.
  (Moving custom features around can create NEW cross-biome cycles.)
* `fix` only reorders WITHIN a step. It WARNS about (but never auto-moves) a
  vanilla feature sitting in the wrong generation step, or one not present in
  sorted_features.txt -- handle those by hand.
* Formatting is preserved: only entry lines are permuted; blank lines inside a
  reordered array are dropped.
"""

import argparse
import glob
import json
import os
import sys
from collections import defaultdict, OrderedDict, deque

# ============================== EDIT-HERE FRAME ==============================
# Mod root (folder that contains `data/`) and the canonical vanilla order file.
MOD_ROOT      = r"C:/Users/Iaske/Desktop/Rawlands/src/main/resources"
FEATURES_FILE = "./sorted_features.txt"   # relative paths resolve against this script's folder

# Drop the biomes you want to act on here (any of the "file forms" above).
# Leave empty to fall back to command-line args, or a full scan if none given.
FILES = [
    # "plains",
    # "mymod:custom_biome",
    # "data/minecraft/worldgen/biome/swamp.json",
]
# ===========================================================================

# Resolve relative config paths against this script's folder (so it works from any CWD).
_HERE = os.path.dirname(os.path.abspath(__file__))
if not os.path.isabs(MOD_ROOT):
    MOD_ROOT = os.path.normpath(os.path.join(_HERE, MOD_ROOT))
if not os.path.isabs(FEATURES_FILE):
    FEATURES_FILE = os.path.normpath(os.path.join(_HERE, FEATURES_FILE))

STEP_NAMES = [
    "raw_generation", "lakes", "local_modifications", "underground_structures",
    "surface_structures", "strongholds", "underground_ores",
    "underground_decoration", "fluid_springs", "vegetal_decoration",
    "top_layer_modification",
]
STEP_INDEX = {n: i for i, n in enumerate(STEP_NAMES)}


# --------------------------------------------------------------------------- #
# Canonical vanilla order
# --------------------------------------------------------------------------- #
def parse_canonical(path):
    """rank[step][feature] = position-within-step ; feat_step[feature] = {steps}.
    A feature may legitimately appear in more than one step (e.g. spring_lava is in
    fluid_springs in the overworld but vegetal_decoration in the Nether)."""
    rank = defaultdict(dict)
    feat_step = {}
    cur = None
    with open(path, encoding="utf-8") as f:
        for line in f:
            s = line.strip()
            if not s.startswith("- "):
                continue
            tok = s[2:].strip()
            indent = len(line) - len(line.lstrip())
            if indent <= 1 and tok in STEP_INDEX:
                cur = STEP_INDEX[tok]
            elif ":" in tok and cur is not None:
                rank[cur][tok] = len(rank[cur])
                feat_step.setdefault(tok, set()).add(cur)
    return rank, feat_step


# --------------------------------------------------------------------------- #
# File resolution (the "frame")
# --------------------------------------------------------------------------- #
def all_biome_files():
    return sorted(glob.glob(os.path.join(MOD_ROOT, "data", "*", "worldgen", "biome", "*.json")))


def resolve_one(item, index):
    item = item.strip()
    if not item:
        return None
    # absolute path
    if os.path.isabs(item) and os.path.isfile(item):
        return os.path.normpath(item)
    # path relative to MOD_ROOT
    cand = os.path.join(MOD_ROOT, item)
    if os.path.isfile(cand):
        return os.path.normpath(cand)
    # path relative to cwd
    if os.path.isfile(item):
        return os.path.normpath(os.path.abspath(item))
    # namespace:name
    if ":" in item and "/" not in item and "\\" not in item:
        ns, name = item.split(":", 1)
        cand = os.path.join(MOD_ROOT, "data", ns, "worldgen", "biome", name + ".json")
        if os.path.isfile(cand):
            return os.path.normpath(cand)
    # bare name -> search all biome dirs
    name = os.path.basename(item)
    if not name.endswith(".json"):
        name += ".json"
    matches = [p for p in index if os.path.basename(p) == name]
    if len(matches) == 1:
        return matches[0]
    if len(matches) > 1:
        print(f"  ! '{item}' is ambiguous: {', '.join(matches)}", file=sys.stderr)
        return None
    print(f"  ! could not resolve '{item}'", file=sys.stderr)
    return None


def selected_files(cli_files):
    index = all_biome_files()
    raw = list(cli_files) if cli_files else [f for f in FILES if f.strip()]
    if not raw:
        return index  # full scan
    out = []
    for it in raw:
        r = resolve_one(it, index)
        if r and r not in out:
            out.append(r)
    return out


def rel(path):
    """data/<ns>/.../<file> -> '<ns>/<file>' for compact display."""
    parts = path.replace("\\", "/").split("/")
    try:
        i = parts.index("data")
        return parts[i + 1] + "/" + parts[-1]
    except (ValueError, IndexError):
        return os.path.basename(path)


# --------------------------------------------------------------------------- #
# Slot-method target order
# --------------------------------------------------------------------------- #
def slot_target(step, rank_for_step):
    def movable(e):
        return isinstance(e, str) and e.startswith("minecraft:") and e in rank_for_step
    ordered = iter(sorted([e for e in step if movable(e)], key=lambda e: rank_for_step[e]))
    result = list(step)
    for i, e in enumerate(step):
        if movable(e):
            result[i] = next(ordered)
    return result


# --------------------------------------------------------------------------- #
# verify
# --------------------------------------------------------------------------- #
def biome_problems(path, rank, feat_step):
    data = json.load(open(path, encoding="utf-8"))
    problems = []
    for si, step in enumerate(data.get("features", [])):
        label = STEP_NAMES[si] if si < len(STEP_NAMES) else f"index{si}"
        if not isinstance(step, list):
            continue
        seq = []
        for e in step:
            if not (isinstance(e, str) and e.startswith("minecraft:")):
                continue
            if e not in feat_step:
                problems.append(f"  [step {si} {label}] UNKNOWN vanilla feature: {e}")
            elif si not in feat_step[e]:
                allowed = ", ".join(f"{s} ({STEP_NAMES[s]})" for s in sorted(feat_step[e]))
                problems.append(f"  [step {si} {label}] WRONG STEP: {e} belongs in step {allowed}")
            else:
                seq.append((e, rank[si][e]))
        for i in range(1, len(seq)):
            if seq[i][1] < seq[i - 1][1]:
                problems.append(f"  [step {si} {label}] OUT OF ORDER: '{seq[i][0]}' "
                                f"(#{seq[i][1]}) after '{seq[i-1][0]}' (#{seq[i-1][1]})")
    return problems


def cmd_verify(files, rank, feat_step):
    bad = 0
    for p in files:
        probs = biome_problems(p, rank, feat_step)
        if probs:
            bad += 1
            print(f"### {rel(p)}")
            print("\n".join(probs))
            print()
    print("=" * 60)
    print(f"Summary: {bad} of {len(files)} biomes have ordering problems.")
    return bad


# --------------------------------------------------------------------------- #
# diagnose
# --------------------------------------------------------------------------- #
def annotate(e, rank_for_step, feat_step, si):
    if isinstance(e, str) and e.startswith("minecraft:"):
        if e in rank_for_step:
            return f"#{rank_for_step[e]}"
        if e in feat_step:
            return "WRONGSTEP->" + "/".join(str(s) for s in sorted(feat_step[e]))
        return "UNKNOWN"
    return "custom"


def cmd_diagnose(files, rank, feat_step):
    for p in files:
        data = json.load(open(p, encoding="utf-8"))
        shown = False
        for si, step in enumerate(data.get("features", [])):
            if not isinstance(step, list):
                continue
            tgt = slot_target(step, rank[si])
            if tgt == step:
                continue
            if not shown:
                print("=" * 70)
                print(rel(p))
                shown = True
            print(f"  --- step {si} ({STEP_NAMES[si]}) ---")
            print("  CURRENT:")
            for e in step:
                print(f"      {e:52s} [{annotate(e, rank[si], feat_step, si)}]")
            print("  PROPOSED:")
            for e in tgt:
                print(f"      {e:52s} [{annotate(e, rank[si], feat_step, si)}]")


# --------------------------------------------------------------------------- #
# fix (formatting-preserving, slot method)
# --------------------------------------------------------------------------- #
def find_step_char_spans(text):
    idx = text.index('"features"')
    bracket = text.index('[', idx)
    depth = 0
    in_str = esc = False
    cur_start = None
    spans = []
    i, n = bracket, len(text)
    while i < n:
        c = text[i]
        if in_str:
            if esc:
                esc = False
            elif c == '\\':
                esc = True
            elif c == '"':
                in_str = False
        else:
            if c == '"':
                in_str = True
            elif c == '[':
                depth += 1
                if depth == 1:
                    cur_start = i + 1
            elif c == ']':
                depth -= 1
                if depth == 0:
                    spans.append((cur_start, i))
                    break
            elif c == ',' and depth == 1:
                spans.append((cur_start, i))
                cur_start = i + 1
        i += 1
    return spans


def reorder_text(text, step_targets):
    """step_targets: {step_index: target_list}. Returns new text or None if no change."""
    lines = text.split('\n')
    spans = find_step_char_spans(text)
    changed = False
    for si in sorted(step_targets, reverse=True):  # high index first keeps offsets valid
        tgt = step_targets[si]
        s_start, s_end = spans[si]
        open_off = text.index('[', s_start, s_end)
        close_off = text.rindex(']', s_start, s_end)
        ln_open = text.count('\n', 0, open_off)
        ln_close = text.count('\n', 0, close_off)
        block = lines[ln_open + 1:ln_close]
        bucket = OrderedDict()
        present = []
        for bl in block:
            if bl.strip() == "":
                continue
            raw = bl.rstrip()
            if raw.endswith(','):
                raw = raw[:-1]
            val = raw.strip().strip('"')
            bucket.setdefault(val, []).append(raw)
            present.append(val)
        if sorted(present) != sorted(tgt):
            raise AssertionError(f"step {si}: line/JSON mismatch\n{present}\n{tgt}")
        new_block = [bucket[v].pop(0) for v in tgt]
        for k in range(len(new_block) - 1):
            new_block[k] += ","
        lines[ln_open + 1:ln_close] = new_block
        changed = True
    return '\n'.join(lines) if changed else None


def cmd_fix(files, rank, feat_step, apply):
    touched = 0
    for p in files:
        text = open(p, encoding="utf-8").read()
        data = json.loads(text)
        feats = data.get("features", [])
        step_targets = {}
        warns = []
        for si, step in enumerate(feats):
            if not isinstance(step, list):
                continue
            # warn about un-fixable placements
            for e in step:
                if isinstance(e, str) and e.startswith("minecraft:"):
                    if e not in feat_step:
                        warns.append(f"unknown vanilla feature {e} in step {si}")
                    elif si not in feat_step[e]:
                        allowed = ", ".join(f"{s} ({STEP_NAMES[s]})" for s in sorted(feat_step[e]))
                        warns.append(f"{e} is in step {si} but vanilla uses step {allowed} - move by hand")
            tgt = slot_target(step, rank[si])
            if tgt != step:
                step_targets[si] = tgt
        if warns:
            print(f"!  {rel(p)}")
            for w in warns:
                print(f"     WARN: {w}")
        if not step_targets:
            continue
        new_text = reorder_text(text, step_targets)
        json.loads(new_text)  # validate
        touched += 1
        steps_str = ", ".join(f"{si}({STEP_NAMES[si]})" for si in sorted(step_targets))
        print(f"{'FIX ' if apply else 'WOULD FIX '}{rel(p)} -> steps {steps_str}")
        if apply:
            open(p, "w", encoding="utf-8", newline="").write(new_text)
    print("=" * 60)
    print(f"{touched} file(s) {'reordered' if apply else 'need reordering'}"
          f"{'' if apply else ' (dry run; pass --apply to write)'}.")
    return touched


# --------------------------------------------------------------------------- #
# cycle  (always scans ALL biomes -- cycles are global)
# --------------------------------------------------------------------------- #
def cmd_cycle(rank_canon_lists, no_baseline):
    edges = defaultdict(set)
    nodes = defaultdict(set)
    origin = defaultdict(list)

    def add_seq(si, seq, src):
        prev = None
        for f in seq:
            nodes[si].add(f)
            if prev is not None and prev != f:
                edges[si].add((prev, f))
                origin[(si, prev, f)].append(src)
            prev = f

    if not no_baseline:
        for si, seq in rank_canon_lists.items():
            add_seq(si, seq, "VANILLA_BASELINE")

    for p in all_biome_files():
        data = json.load(open(p, encoding="utf-8"))
        for si, step in enumerate(data.get("features", [])):
            if isinstance(step, list):
                add_seq(si, [e for e in step if isinstance(e, str)], rel(p))

    any_cycle = False
    for si in range(11):
        adj = defaultdict(set)
        indeg = {n: 0 for n in nodes[si]}
        for a, b in edges[si]:
            if b not in adj[a]:
                adj[a].add(b)
                indeg[b] += 1
        q = deque([n for n in nodes[si] if indeg[n] == 0])
        seen = 0
        while q:
            x = q.popleft()
            seen += 1
            for y in adj[x]:
                indeg[y] -= 1
                if indeg[y] == 0:
                    q.append(y)
        if seen != len(nodes[si]):
            any_cycle = True
            print(f"CYCLE in step {si} ({STEP_NAMES[si]}):")
            for nleft in sorted(n for n in nodes[si] if indeg[n] > 0):
                for b in adj[nleft]:
                    if indeg[b] > 0:
                        print(f"   {nleft} -> {b}   (from: {', '.join(origin[(si, nleft, b)])})")
    if not any_cycle:
        tn = sum(len(nodes[si]) for si in range(11))
        te = sum(len(edges[si]) for si in range(11))
        mode = "real adjacencies only" if no_baseline else "mod biomes + vanilla baseline"
        print(f"OK: no cycles. {tn} feature-nodes, {te} ordering edges across 11 steps "
              f"are consistent ({mode}).")
    return any_cycle


# --------------------------------------------------------------------------- #
def main():
    ap = argparse.ArgumentParser(description="biome feature-order toolkit")
    sub = ap.add_subparsers(dest="cmd", required=True)
    for name in ("verify", "diagnose"):
        sp = sub.add_parser(name)
        sp.add_argument("files", nargs="*")
    spf = sub.add_parser("fix")
    spf.add_argument("files", nargs="*")
    spf.add_argument("--apply", action="store_true", help="write changes (default: dry run)")
    spc = sub.add_parser("cycle")
    spc.add_argument("--no-baseline", action="store_true",
                     help="model only real biome adjacencies (no synthetic vanilla chain)")
    args = ap.parse_args()

    rank, feat_step = parse_canonical(FEATURES_FILE)

    if args.cmd == "cycle":
        canon_lists = {si: [f for f, _ in sorted(rank[si].items(), key=lambda kv: kv[1])]
                       for si in rank}
        sys.exit(1 if cmd_cycle(canon_lists, args.no_baseline) else 0)

    files = selected_files(args.files)
    if not files:
        print("No files resolved. Fill the FILES frame or pass files on the CLI.")
        sys.exit(2)
    print(f"Targeting {len(files)} biome(s).\n")

    if args.cmd == "verify":
        sys.exit(1 if cmd_verify(files, rank, feat_step) else 0)
    if args.cmd == "diagnose":
        cmd_diagnose(files, rank, feat_step)
    if args.cmd == "fix":
        cmd_fix(files, rank, feat_step, args.apply)


if __name__ == "__main__":
    main()
