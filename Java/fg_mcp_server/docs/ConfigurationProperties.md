# Configuration Properties Reference


## Properties

| Property | Type | Behavior |
|---|---|---|
| `fgumcpserver.text.enabled` | `boolean` | Disables text-related tools when not `true` (e.g. sanitizeText)|
| `fgumcpserver.data.enabled` | `boolean` | Disables FGU data-related tools when not `true` (e.g. ListCampaigns)|
| `fgumcpserver.data.fguBaseDir` | `String` (path) | If provided, it overrides default FGU directory location |


## Example `application.properties`

```properties
# Disable Text tools
fgumcpserver.text.enabled=false

# Disable FGU data tools
fgumcpserver.data.enabled=false

# Override default FGU data directory location
fgumcpserver.data.fguBaseDir=/path/to/fantasy-grounds-data
```
