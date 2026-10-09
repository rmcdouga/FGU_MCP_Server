# Router Readiness Checklist

Use this checklist to decide when to add a router skill.

## Create a router when

- [ ] You have at least 3 active skills in `Skills/`.
- [ ] Skill boundaries are stable (low churn in names/scope).
- [ ] You are starting to forget exact skill names.
- [ ] Requests frequently begin broad and need routing.

## Prep before creating the router

- [ ] Confirm each target skill has a clear one-line purpose.
- [ ] Confirm each target skill has explicit non-goals.
- [ ] Document trigger phrases for each target skill.
- [ ] Identify ambiguous requests and default route behavior.
- [ ] Decide whether router is user-invoked or model-invoked.

## Router behavior requirements

- [ ] Router selects exactly one destination skill per request.
- [ ] Router explains the choice in one short sentence.
- [ ] Router returns a copy/paste-ready next prompt.
- [ ] Router does not execute side-effecting actions itself.
- [ ] Router rejects out-of-scope requests with safe alternatives.

## Minimum acceptance criteria

- [ ] `Skills/fantasy_grounds_router/SKILL.md` exists.
- [ ] `Skills/fantasy_grounds_router/README.md` exists with examples.
- [ ] At least 5 routing examples are documented.
- [ ] At least 3 ambiguous prompts are documented with expected routes.
- [ ] Router routes to `control_fantasy_grounds` correctly.

## Starter prompts for manual validation

1. "I need to load my campaign and continue tonight's session."
2. "FG won't connect and start_session keeps failing."
3. "Can you write lore for a new NPC?"
4. "Pause now, then resume in 10 minutes."
5. "What should I run to check if everything is healthy first?"
