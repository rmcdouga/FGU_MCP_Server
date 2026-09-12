# How to interface with Fantasy Grounds Unity

## Context and Problem Statement

The MCP server will be written in Java but will have to interface with FGU somehow (to create content, read content, etc.)
What mechanism will be used?

## Considered Options

* Direct reading/writing of FGU XML files (DB.xml)
* Communication with LUA scripts
  * via logs
  * via named pipes
* Applescript control of FGU

## Decision Outcome

Chosen option: Direct reading/writing of XML files was chosen because
this uses well understood methods and does not require learning any
new technologies (and accepting the inherent uncertainty involved in
using technologies that are unfamiliar).  

This approach is not without its challenges:
1. The files can be large and must be read and updated.  They may be too large for storing in a DOM.
2. The format is unknown and undocumented, so this is a challenge.
3. The format will vary by ruleset.  DnD5e will be the primary target.  Other rulesets can follow later.
