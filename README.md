ConvertUUID
===========

ConvertUUID is a tool for converting Minecraft usernames to Mojang account UUIDs in bulk. It outputs in a standard Java .properties file format, where username=uuid.

Compiling
=========
1. Grab the latest tested AccountsClient source code [from my fork here](https://github.com/forairan/AccountsClient).
2. Build AccountsClient.
3. Build ConvertUUID.

Usage
=====
Use --help for general usage help.

Mass Conversion
===============
Using the -I command-line option, ConvertUUID will accept an input file of usernames seperated by a new line (one username per line).
