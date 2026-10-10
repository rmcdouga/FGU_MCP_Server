---
name: control_fantasy_grounds
description: User-invoked workflow for safe Fantasy Grounds session control.
disable-model-invocation: true
---

Run a strict, safety-first workflow to control a Fantasy Grounds session.

## Scope

Use this skill only for session control commands:

- `status`
- `start_session`
- `load_campaign`
- `pause_session`
- `resume_session`
- `stop_session`

Reject requests outside this allowlist and suggest the closest valid command.

## Workflow

For every run, follow this sequence:

1. `preflight`
2. `plan`
3. `execute` (only when confirmed)

### 1) preflight

Verify prerequisites before planning or execution:

- Required tools or bridge are reachable.
- Required config values are present.
- Target campaign/session identifiers are available when needed.

Timeout: 30 seconds total.

If preflight fails, stop immediately and return:

- failed check
- why it failed
- next safe step

### 2) plan

Produce a dry-run plan with no side effects.

Include:

- requested canonical command
- resolved target (if any)
- exact actions that would run
- expected success signal per action

### 3) execute

For side-effecting commands (`start_session`, `load_campaign`, `pause_session`, `resume_session`, `stop_session`), require this exact confirmation format:

`CONFIRM EXECUTE <command> <target>`

Example:

`CONFIRM EXECUTE load_campaign "Keoland"`

Rules:

- `status` is read-only and does not need execute confirmation.
- Each execute step has a 60 second timeout.
- On any failure or timeout, fail fast and stop.
- Do not auto-retry.
- Retry is manual only, maximum one retry per failed step, and must rerun preflight first.

## Output contract

Return a compact structured report with these sections:

- `preflight`
- `planned_actions`
- `executed_actions`
- `results`
- `next_safe_step`

## Non-goals

Do not perform these in v1:

- character sheet or rules adjudication
- lore generation or campaign writing
- arbitrary shell/tool execution outside this workflow
- autonomous multi-command batching without per-command confirmation

## Completion criterion

The run is complete only when one of these is true:

- A read-only `status` report is returned.
- A side-effect command finishes and the full structured report is returned.
- A failure path returns a fail-fast structured report with `next_safe_step`.

## Launching Fantasy Grounds Application

- Fantasy Grounds application can be found here: `/Applications/SmiteWorks/Fantasy Grounds/FantasyGrounds.app`
- If, after launching, the application shows an "UPDATE AVAILABLE" button, click on the button to perform an update.  Wait for the update to finish and the Fantasy Grounds application to re-launch itself.

## Finding Fantasy Grounds PID

Fantasy Grounds PID can be determined by executing this script:

```bash
ps -A | grep SmiteWorks | grep -v 'grep'
```

## Controlling Fantasy Grounds

- Fantasy Grounds is a Unity application and does not have any accessibility information.
- Use the peekaboo tool to control Fantasy Grounds.
- All interactions must be accomplished using the following steps:
	- Capture a screenshot using the peekaboo-image tool with retina true, saving to an explicit path.
	- Resolve the target ONCE using the mandatory Target resolution method below (a vision-capable subagent that returns a structured bounding box). Do NOT read the PNG inline and guess coordinates.
	- Send a click to the coordinates at the center of the resolved bounding box to activate the target UI element.

## Grounding (mandatory)

- Every target name and coordinate MUST be observed from the current screenshot.
- Never treat any name, label, or example that appears in this skill's documentation as an observed UI element. A name that appears only in the docs is NOT a valid target.
- The plan must quote the observed target with its source, e.g. "resolved target: '<label>' observed at (x,y) in <screenshot path>".

## Target resolution (mandatory)

- Resolve the target ONCE from the current screenshot using a vision-capable subagent that returns a structured box {x, y, w, h}.
- Do NOT read the PNG inline and guess coordinates.
- Do NOT call peekaboo-analyze (no API key configured; it will always fail).
- Do NOT infer the target from the currently highlighted/selected row. A highlighted row is never a valid target for start_session.
- If no coordinate can be produced from a verifiable source, FAIL FAST. Never guess. Never click a highlighted row. Ask the user for coordinates.

## Loop Prevention (mandatory)

- Max attempts: at most 3 locate-click-observe attempts per action. After 3 attempts without the success signal, stop and return a fail-fast report.
- No-progress detection: capture a before- and after-snapshot for each click. If the after-state (screenshot hash, located element id, or window title) is unchanged, treat it as a no-progress failure and stop — do not click again.
- Single target lock: resolve the target UI element ONCE at the start of the cycle and record its bounding box. Do not re-locate the same element on every iteration. If the target cannot be resolved, fail fast instead of guessing.
- Idempotency: a click whose post-state equals its pre-state is not a success. Never repeat such a click.
- No AI analysis: never call peekaboo-analyze. It requires an API key that is not configured and will always fail.
   