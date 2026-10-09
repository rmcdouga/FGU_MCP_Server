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

Fantasy Grounds application can be found here: `/Applications/SmiteWorks/Fantasy Grounds/FantasyGrounds.app`

## Finding Fantasy Grounds PID

Fantasy Grounds PID can be determined by executing this script:

```bash
ps -A | grep SmiteWorks | grep -v 'grep'
```

## Controlling Fantasy Grounds

- Fantasy Grounds is a Unity application and does not have any accessibility information.
- Use the peekaboo tool to control Fantasy Grounds
- All interactions must be accomplished using the following steps:
	- Capture a screenshot
	- Use a subagent to locate the bounding box of the target UI element
	- Send a click element to the coordinates at the center of the bounding box to activate the target UI element
   