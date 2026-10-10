# control_fantasy_grounds

A user-invoked skill for safe, explicit Fantasy Grounds session control.

## Canonical commands

- `status`
- `start_session`
- `load_campaign`
- `pause_session`
- `resume_session`
- `stop_session`

Natural-language aliases are allowed in requests, but the skill normalizes to canonical snake_case command names in the plan.

## Workflow contract

Every run follows:

1. `preflight` (30s total timeout)
2. `plan` (dry-run, no side effects)
3. `execute` (requires explicit confirmation for side effects)

Side-effect confirmation format:

`CONFIRM EXECUTE <command> <target>`

Example:

`CONFIRM EXECUTE load_campaign "Keoland"`

`status` is read-only and does not require execute confirmation.

## Example prompts

### 1) Status only (read-only)

"Use `control_fantasy_grounds` to run `status` for the current session."

### 2) Safe dry-run plan

"Use `control_fantasy_grounds` to plan `load_campaign` for `Keoland`, but do not execute yet."

### 3) Confirmed execute

"Use `control_fantasy_grounds` to load campaign `Keoland`. CONFIRM EXECUTE load_campaign \"Keoland\""

## Failure behavior

- Fail fast on first failed step or timeout.
- No automatic retries.
- Manual retry only, max one retry per failed step, after rerunning preflight.

## Mandatory safety rules

These are enforced on every side-effecting command (see `SKILL.md` for full text):

### Grounding
- Every target name and coordinate MUST be observed from the current screenshot.
- Never treat any name, label, or example in this skill's documentation as an observed UI element. A name that appears only in the docs is NOT a valid target.
- The plan must quote the observed target with its source, e.g. "resolved target: '<label>' observed at (x,y) in <screenshot path>".

### Target resolution
- Resolve the target ONCE from the current screenshot using a vision-capable subagent that returns a structured box {x, y, w, h}.
- Do NOT read the PNG inline and guess coordinates.
- Do NOT call peekaboo-analyze (no API key configured; it will always fail).
- Do NOT infer the target from the currently highlighted/selected row. A highlighted row is never a valid target for start_session.
- If no coordinate can be produced from a verifiable source, FAIL FAST. Never guess. Never click a highlighted row. Ask the user for coordinates.

### Loop prevention
- Max 3 locate-click-observe attempts per action; stop and return a fail-fast report if the success signal is not met.
- No-progress detection: if the after-state (screenshot hash, element id, or window title) is unchanged, stop — do not click again.
- Single target lock: resolve the target ONCE and record its bounding box; do not re-locate on every iteration.
- Idempotency: a click whose post-state equals its pre-state is not a success. Never repeat it.

## Troubleshooting

- If preflight fails, fix the reported prerequisite and rerun.
- If command is rejected, switch to one of the six canonical commands.
- If execution times out, reassess environment health before manual retry.
