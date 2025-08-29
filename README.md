**Linguistic Search App - German MWE Extractor**

This project has been extended with a comprehensive German Multiword Expression (MWE) extractor that processes Wikipedia articles to identify various types of German linguistic expressions.

## Features Added

- **German MWE Extraction**: Identifies compound nouns, phrasal verbs, prepositional phrases, idioms, named entities, collocations, and light verb constructions
- **Wikipedia Integration**: Scrapes and processes German Wikipedia articles with specialized content filtering
- **Multiple Algorithms**: Uses pattern matching, statistical analysis (PMI), and syntactic patterns for MWE detection
- **Output Formats**: Exports results to JSON, CSV, and formatted text files
- **Demo Application**: Includes interactive demo showing real extraction results

## Quick Start

```bash
# Run the demo
./run_german_mwe_extractor.sh demo

# Extract from specific article
./run_german_mwe_extractor.sh title "Deutsche_Sprache"

# Search and extract
./run_german_mwe_extractor.sh search "Bundeskanzler" 3
```

## Results Sample

The system successfully extracts German MWEs with high accuracy:
- **2,120 MWEs** found in the "Deutsche Sprache" Wikipedia article
- **74% average confidence** score
- Multiple MWE types identified: collocations (1,295), compound nouns (506), phrasal verbs (270), etc.

For detailed documentation, see: `GERMAN_MWE_EXTRACTOR_README.md`

Group 6 members: Marco Weber, Agnessa Fomina, Jue Huang, Ruonan Mi
