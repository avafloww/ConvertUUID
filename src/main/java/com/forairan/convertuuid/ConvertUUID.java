/*
 * Copyright (C) 2013 Devin Ryan
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.forairan.convertuuid;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

/**
 * Username -> UUID conversion tool for Mojang accounts.
 *
 * @author Devin Ryan
 */
public class ConvertUUID {

    private static final int MAX_SIMULTANEOUS_JOBS = 10;

    public static void main(String[] args) throws Exception {
        OptionParser parser = new OptionParser() {
            {
                acceptsAll(asList("?", "help"), "Shows the help");

                acceptsAll(asList("i", "input"), "Allows you to convert a single username")
                        .withRequiredArg()
                        .ofType(String.class)
                        .describedAs("Minecraft username");

                acceptsAll(asList("I", "input-file"), "Specifies an input file of newline-separated usernames to convert")
                        .withRequiredArg()
                        .ofType(File.class)
                        .describedAs("Input filename");

                acceptsAll(asList("O", "output-file"), "Specifies an output file (if not specified, defaults to stdout)")
                        .withRequiredArg()
                        .ofType(File.class)
                        .describedAs("Output filename");

                acceptsAll(asList("j", "jobs"), "Amount of conversion jobs to run at once (defaults to 1, maximum " + MAX_SIMULTANEOUS_JOBS + ")")
                        .withRequiredArg()
                        .ofType(Integer.class)
                        .describedAs("Number of jobs to run simultaneously");
            }
        };

        OptionSet options = parser.parse(args);
        List<String> usernames = new ArrayList<String>();
        PrintWriter writer = new PrintWriter(System.out);
        
        if (options == null || args.length == 0 || options.has("?")) {
            parser.printHelpOn(System.out);
            return;
        } else if (options.has("i") && options.has("I")) {
            System.err.println("Cannot specify both an input file and input username!");
            return;
        } else if (options.has("i")) {
            usernames.add((String) options.valueOf("i"));
        } else if (options.has("I")) {
            File inputFile = (File) options.valueOf("I");
            if (!inputFile.exists()) {
                System.err.println("Input file does not exist!");
                return;
            } else {
                BufferedReader br = new BufferedReader(new FileReader(inputFile));

                String line = null;
                while ((line = br.readLine()) != null) {
                    if (line.startsWith("#")) {
                        // Just to allow for comments.
                        continue;
                    }

                    line = line.trim();
                    if (!usernames.contains(line)) {
                        usernames.add(line);
                    }
                }

                br.close();
            }
        } else if (options.has("O")) {
            File outputFile = (File) options.valueOf("O");
            if (!outputFile.exists()) {
                outputFile.createNewFile();
            }
            
            writer = new PrintWriter(new FileWriter(outputFile));
        }

        int jobs = 1;
        if (options.has("j")) {
            jobs = (Integer) options.valueOf("j");
            if (jobs > MAX_SIMULTANEOUS_JOBS) {
                System.err.println("Requested too many simultaneous jobs; maximum amount of jobs allowed is " + MAX_SIMULTANEOUS_JOBS);
                return;
            } else if (jobs < 1) {
                System.err.println("Requested too few simultaneous jobs; why are you trying to use less than 1 job?");
                return;
            }
        }

        new Converter(usernames, writer, jobs).run();
    }

    private static List<String> asList(String... params) {
        return Arrays.asList(params);
    }

}
