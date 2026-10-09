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

## Troubleshooting

- If preflight fails, fix the reported prerequisite and rerun.
- If command is rejected, switch to one of the six canonical commands.
- If execution times out, reassess environment health before manual retry.
