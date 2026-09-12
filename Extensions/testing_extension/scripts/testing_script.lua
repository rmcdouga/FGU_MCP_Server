-- =============================================================================
-- MCP Server for Fantasy Grounds Unity 5.x (D&D 5e)
-- scripts/testing_script.lua
--
-- COMMANDS (GM only):
--   /testscript                — run test script
-- =============================================================================

function onInit()
    Debug.console("[Claude Bridge] Request sent.")
	    if not User.isHost() then return end

    Comm.registerSlashHandler("testscript",     cmdTestScript,        "- [moduleName] list all module NPCs")
	Comm.registerSlashHandler("listall",        listAllModules,       "- list all modules")
	Comm.registerSlashHandler("listnpcs",       cmdListAllNpcs,    "- [moduleName] list all module NPCs")
--	Comm.registerSlashHandler("listactive",     listActiveModules,    "- list active modules")

    ChatManager.SystemMessage("[Testing Script] Loaded.")
    ChatManager.SystemMessage("[Testing Script] /testscript")
end

-- ---------------------------------------------------------------------------
-- Command handlers
-- ---------------------------------------------------------------------------

-- /testscript 
function cmdTestScript(sCommand, sParams)
	ChatManager.SystemMessage("Test script beginning.")

	local dbnRoot = DB.getRoot();
	ChatManager.SystemMessage("[Testing Script] " .. tostring(dbnRoot.getChildCount()) .. " root objects found in database.")
	ChatManager.SystemMessage("[Testing Script] " .. tostring(DB.getChildCount(dbnRoot)) .. " root objects found in database.")
	ChatManager.SystemMessage("[Testing Script] NodeName '" .. dbnRoot.getNodeName() .. "', Name '" .. dbnRoot.getName() .. "', Module '" .. dbnRoot.getModule() .. "', Path '" .. DB.getPath(dbnRoot) .. "'.")
	ChatManager.SystemMessage("[Testing Script] Name '" .. DB.getName(DB.getRoot()) .. "', Module '" .. DB.getModule(DB.getRoot()) .. "', Path '" .. DB.getPath(DB.getRoot()) .. "'.")
	ChatManager.SystemMessage("[Testing Script] Value '" .. tostring(DB.getText(DB.getRoot())) .. "'.")
	local tModuleNPCs = dbnRoot.getChildren();
	ChatManager.SystemMessage("[Testing Script] " .. #tModuleNPCs .. " root children found in database.")
	local tRootNodeChildren = DB.getChildren(dbnRoot);
	ChatManager.SystemMessage("[Testing Script] " .. #tRootNodeChildren .. " root children found in database via DB.")
	local tModuleNpcCats = dbnRoot.getChildCategories();
	ChatManager.SystemMessage("[Testing Script] " .. #tModuleNpcCats .. " root child categories found in database.")

	ChatManager.SystemMessage("Test script executed.")
end

-- /testscript 
function cmdListAllNpcs(sCommand, sParams)
	ChatManager.SystemMessage("List All NPCs beginning.")
	-- Logic goes here
	listAllModuleNPCs(sParams)
	ChatManager.SystemMessage("List All NPCs executed.")
end

-- List all module NPCs in the console
function listAllModuleNPCs(sModuleName)
	ChatManager.SystemMessage("[Testing Script] Listing NPCs.")
	print("[Testing Script] Listing NPCs.")
    -- Look directly inside the global reference path where modules unpack lists
    local tModuleNPCs = DB.getChildren(sModuleName .. "npc.@dndsrd521bestiary");
	ChatManager.SystemMessage("[Testing Script] " .. #tModuleNPCs .. " NPCs found in module " .. sModuleName .. ".npc.")
    
    for sKey, nodeNPC in pairs(tModuleNPCs) do
        local sNPCName = DB.getValue(nodeNPC, "name", "Unknown Module NPC");
		ChatManager.SystemMessage("[Testing Script] Module Path: " .. nodeNPC.getPath() .. " | Name: " .. sNPCName)
        print("Module Path: " .. nodeNPC.getPath() .. " | Name: " .. sNPCName);
    end
end

function listAllModules()
	ChatManager.SystemMessage("[Testing Script] Listing All Modules.")
	print("[Testing Script] Listing All Modules.")
	-- Fetch the list of all module names
	local aModules = Module.getModules();

	-- Iterate through the list
	for iNum, sModule in ipairs(aModules) do
  	  -- Print each module name to the FGU console
   	 Debug.chat("Found module: '" .. sModule .. "' (" .. iNum .. "/" .. #aModules .. ")");
	 local tInfo = Module.getModuleInfo(sModule);

	 if tInfo then
--	     Debug.chat("Title: " .. tostring(tInfo.title));
	     Debug.chat("Author: " .. tostring(tInfo.author));
	     Debug.chat("Installed? " .. tostring(tInfo.installed));
	     Debug.chat("Loaded? " .. tostring(tInfo.loaded));
		 Debug.chat("Name: " .. tostring(tInfo.name));
		 Debug.chat("Has Data? " .. tostring(tInfo.hasData));
	 end

	end
end

function listActiveModules()
	ChatManager.SystemMessage("[Testing Script] Listing Active Modules.")
	print("[Testing Script] Listing Active Modules.")
	local aActiveModules = Module.getActiveModules();

	for _, sModule in ipairs(aActiveModules) do
	    Debug.chat("Active module: " .. sModule);
	end
end
