# Fantasy Grounds MCP Server

This project is an [MCP](https://modelcontextprotocol.io/docs/latest/getting-started/intro) Server for [Fantasy Grounds Virtual Tabletop](https://www.fantasygrounds.com).
It provides tools for an AI Agent (like Claude Code, Google AntiGravity, AnythingLLM, etc.) to perform tasks related to Fantasy Grounds.

It is expected that more tools will be added over time - including more sophisticated operations (like importing images, text, etc).

## Table of Contents

- [Features](#features)
- [Installation/Configuration](#installationconfiguration)
  - [JBang Installation/Configuration](#jbang-installationconfiguration)
  - [FGU MCP Server Configuration](#fgu-mcp-server-configuration)
- [Using the FGU MCP Server](#using-the-fgu-mcp-server)

## Features

The current feature set is very limited - just the following tool:

* Sanitize Text for Fantasy Grounds - Remove typographic characters that interfere with Fantasy Grounds text parsing, such as left/right quotes, non-breaking spaces, etc.

## Installation/Configuration

The installation and running of the FGU MCP Server requires two steps:
1. Install JBang
2. Configure the MCP server in an AI Agent (such as Claude Code, Google AntiGravity, AnythingLLM, etc.)

### JBang Installation/Configuration
[JBang](https://www.jbang.dev) is a utility that simplifies the running of Java programs.  It looks after managing the Java runtime for you.  It also manages the downloading of the FGU MCP Server code.

Go to the [JBang Downloads page](https://www.jbang.dev/download/), select your operating system and install using one of the commands provided on that page.  Installing JBang using one of the [package manager](https://en.wikipedia.org/wiki/Package_manager) options is recommended because that will make keeping JBang up to date easier.

On MacOS, [HomeBrew](https://brew.sh) is the defacto standard.  On Linux [SDKMan](https://sdkman.io/) is a good option and on Windows there are no good options, but [Scoop](https://scoop.sh) is probably the simplest. 

Once JBang is installed, I recommend executing a command in a terminal window to set the default Java version to 25 (this will save an extra download of Java 17).

On MacOS and Linux:

```bash
export JBANG_DEFAULT_JAVA_VERSION=25; jbang jdk default 25
```

On Windows:

```powershell
$env:JBANG_DEFAULT_JAVA_VERSION = "25"; jbang jdk default 25
```

### FGU MCP Server Configuration

AI Agents typically all use the same configuration JSON format.  How you get to the file will vary from one agent to the next, however the entry for launching the FGU MCP Server will be the same:

```json
{
	"mcpServers": {
		"fgu-local-mcp-server": {
			"command": "jbang",
			"args": [
				"fgu_mcp_server@rmcdouga/FGU_MCP_Server",
			],
			"autoStart": true
		}
	}
}
```

For instructions that are specific to particular Ai Agents, see the [AI Agent Configuration document](Docs/AiAgentConfiguration.md).

## Using the FGU MCP Server


Once the server is configured in your AI Agent, you can ask your agent to "sanitize text for Fantasy Grounds" and it will remove the typographic characters for you.

Here is an example of such a prompt:

> Sanitize the following text for Fantasy Grounds and print the result: This is some text that contains “left and right quotes” and some ‘left and right single-quotes’.


You may be able to get away with more concise requests (such as just "sanitize this text"), depending on your current context, but including "sanitize text for Fantasy Grounds" should ensure that the correct tool is called.
