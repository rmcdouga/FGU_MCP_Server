# Contributing

Thanks for your interest in contributing to Fantasy Grounds MCP Server.

This project provides an MCP server for Fantasy Grounds Virtual Tabletop. Contributions are welcome, especially for bug fixes, documentation improvements, tests, and new tools that support Fantasy Grounds workflows.

## Table of Contents

- [Ways to Contribute](#ways-to-contribute)
- [Before You Start](#before-you-start)
- [Development Notes](#development-notes)
- [Building and Testing](#building-and-testing)
- [Contribution Guidelines](#contribution-guidelines)
- [Pull Requests](#pull-requests)
- [Reporting Issues](#reporting-issues)
- [Feature Requests](#feature-requests)
- [Be Respectful](#be-respectful)

## Ways to Contribute

Contributions are welcome in several forms:

- Bug fixes
- New MCP tools related to Fantasy Grounds workflows
- Documentation improvements
- Configuration examples for additional AI agents
- Tests and validation improvements
- Usability improvements for existing tools

If you are planning a larger change, it is best to open an issue first so the approach can be discussed before implementation begins.

## Before You Start

Please:

- Read `README.md`
- Check for an existing issue before starting larger work
- Prefer small, focused pull requests

If you want to add a significant feature or make a larger design change, opening an issue first is recommended.

## Development Notes
This Spring AI project uses Java 25 with Maven to build and is distributed/run using JBang.

The main Java implementation lives in:

- `Java/fg_mcp_server/`

Other useful files:

- `README.md`
- `Docs/AiAgentConfiguration.md`
- `jbang-catalog.json`

## Building and Testing

From `Java/fg_mcp_server/`, typical Maven commands are:

`mvn verify` to build the application and run unit tests

If your change affects user-facing behavior, please also verify that the documentation and configuration examples still make sense.

## Contribution Guidelines

When contributing:

- Keep changes focused and easy to review
- Update documentation when behavior changes
- Add or update tests when practical
- Avoid unrelated refactoring in the same pull request
- Follow the existing style in the codebase

## Pull Requests

Please include:

- A clear description of what changed
- Why the change was needed
- How you tested it
- Links to any related issues

Smaller pull requests are easier to review and merge.

## Reporting Issues

When reporting a bug, please include:

- What you expected
- What happened instead
- Steps to reproduce
- Relevant error messages or logs
- Which AI agent or MCP client you were using, if relevant

## Feature Requests

Feature requests are welcome. It helps if you describe:

- The workflow or problem
- Why the current tools are not enough
- The expected behavior
- An example prompt or use case

## Be Respectful

Please keep discussions constructive and respectful.

Thanks for helping improve Fantasy Grounds MCP Server.
