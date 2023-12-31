# FoundryVtt-LevelDB-Creator
PoC to create a valid LevelDB from json files for FoundryVTT, including folders in compendia

***Only works with FoundryVtt V11***

## Assumptions

- `packs/src` contains your json files in any filestructure you want
- `packs/lvldb` is where the output dbs are written. These dirs will contain a *.log file and some other lvldb files. *The .log file IS THE DB* 
- each object contains a flag with its compendium folder like this: `"LvlDbCompendiumFolder": "charcreation.Classes.Class Features"`
  - configurable in org.bytewright.foundrytools.json.PackNameExtractor#FOLDER_FLAG
- you can set a global id prefix to easily identify stuff from your compendium: 
  - org.bytewright.foundrytools.util.IdGenerator#GLOBAL_ID_PREFIX
- Your `module.json` contains several packs and their names are prex+"-name", see here and configure:
  - org.bytewright.foundrytools.config.AppSettings

# DB Unpacker
Here is a script which will unpack existing LevelDBs or *.db files into json files:
`org.bytewright.foundrytools.LevelDBUnpacker`
- this will look into dir `unpack` and write files to `unpacked` in main repo dir

# Architecture Overview
 - App uses Spring Boot, which contains an embedded webserver, but it is not used at the moment
 - App uses Events to control flow
   - Everything start in org.bytewright.foundrytools.config.ChainStarter with Spring boot ContextRefreshedEvent
   - Once all jsonfiles are loaded, event FullContentLoadFinishedEvent is published
   - After that all data scrubbers and datagenerators are called
     - see here org.bytewright.foundrytools.datascrubbing.DataGenerationAndScrubberService
     - Add your own logic in custom implementations of DataScrubber or DataGenerator
     - Event DataScrubbingFinishedEvent is Published
   - Updated data is written back to source json files to persist changes, see JsonFileUpdateComponent
     - Publishes event DataExportReadyEvent
   - DataExportService writes all data in memory to LevelDbs. Where a specific item is added depends on:
     - org.bytewright.foundrytools.json.PackNameExtractor#FOLDER_FLAG
   - Leveldb needs some time to process all writes but once it's done DataExportFinishedEvent is published
   - App is shut down once DataExportFinishedEvent is published

# Future ideas
Feel free to add PRs for features:
 - Web frontend to guide the process instead of events
 - Datamigrations from foundry/dnd5e system
 - better pojos of foundry objects, maybe autogenerated from template.json?
