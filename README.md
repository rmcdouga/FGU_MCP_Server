# Fantasy Grounds MCP Server

This project is an [MCP](https://modelcontextprotocol.io/docs/latest/getting-started/intro) Server for [Fantasy Grounds Virtual Tabletop](https://www.fantasygrounds.com).  It provides tools for an AI Agent (like Claude Code, Google AntiGravity, AnythingLLM, etc.) to perform tasks related to Fantasy Grounds.

The current feature set is very limited - just the following tool:
* Sanitize Text for Fantasy Grounds - Remove typographic characters that interfere with Fantasy Grounds text parsing, such as left/right quotes, non-breaking spaces, etc.

It is expected that more tools will be added over time - including more sophisticated operations (like importing images, text, etc).

## Installation/Configuration

TBD

## Usage

Once the server is configured in your AI Agent, you can ask your agent to "sanitize text for Fantasy Grounds" and it will remove the typographic characters for you.

Here is an example of such a prompt:
> Sanitize the following text for Fantasy Grounds and print the result: This is some text that contains “left and right quotes” and some ‘left and right single-quotes’.

You may be able to get away with more concise requests (such as just "sanitize this text"), depending on your current context, but including "sanitize text for Fantasy Grounds" should ensure that the correct tool is called.


