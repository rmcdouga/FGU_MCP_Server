# AI Agent Configuration Examples 

In general, all AI Agents use the same JSON configuration file format to configure MCP servers. 
In order to enable the FGU MCP Server, you need to add the following entry into the `mcpServers` object:

```json
		"fgu-local-mcp-server": {
			"command": "jbang",
			"args": [
				"fgu_mcp_server@rmcdouga/FGU_MCP_Server",
			],
			"autoStart": true
		}
```

This means a properly configured file with just the FGU MCP Server looks like this:

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

A properly configured file with multiple MCP servers might look like this:

```json
{
  "mcpServers": {
    "filesystem-server": {
      "command": "npx",
      "args": ["-y", "@modelcontextprotocol/server-filesystem", "/Users/your-user/workspace"],
      "env": {
        "NODE_ENV": "production"
      }
    },
    "sqlite-helper": {
      "command": "python3",
      "args": ["-m", "mcp_server_sqlite", "--db-path", "./data.db"]
    },
    "github": {
      "command": "npx",
      "args": ["-y", "@modelcontextprotocol/server-github"],
      "env": {
        "GITHUB_PERSONAL_ACCESS_TOKEN": "your_token_here"
      }
    },
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

## AI Agent Specific Instructions

### Google Anti-Gravity

Create `~/.gemini/config/mcp_config.json` and add the following:

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

If the file already exists, then just adding the `fgu-local-mcp-server` entry above to the `mcpservers` object will enable the FGU MCP Server.

### AnythingLLM

Select `Open Settings` (wrench icon) and then `Agent Skills`.
Scroll to the bottom to see the `MCP Servers` heading and select the `Edit MCP config` (wrench icon) to edit the configuration.
Insert the `fgu-local-mcp-server` entry above into the `mcpservers` object will enable the FGU MCP Server.

## Alternative Configurations

The default configuration outlined above uses a JBang catalog which will always point to the latest FGU MCP Server release.
This means that later versions of the FGU MCP Server will be automatically downloaded and used whenever new
versions become available.

If that is not desired, then it is also possible to pin the FGU MCP Server to a specific version by using an alternative configuration.

### Pinning by explicitly specifying a version

Here is an example of bypassing the JBang catalog and instead downloading directly from Maven Central.  

```json
{
	"mcpServers": {
		"fgu-local-mcp-server" : {
			"command" : "jbang",
			"args" : [
				"run",
				"--java=25",
				"io.github.rmcdouga.fgumcpserver:fg_mcp_server:0.1.0"
			],
			"env": {
				"JBANG_DEFAULT_JAVA_VERSION" : "25"
			},
			"autoStart": true
		}
	}
}
```

It has to explicitly set the Java version.  The environment variable does not have to be set if the user has already set the JBang default JDK version, but that's been included for illustrative purposes.

