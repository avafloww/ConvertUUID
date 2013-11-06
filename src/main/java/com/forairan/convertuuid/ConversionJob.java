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

import com.mojang.api.profiles.Profile;
import com.mojang.api.profiles.ProfileCriteria;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * ConversionJob handles conversion of a batch of usernames.
 *
 * @author Devin Ryan
 */
public class ConversionJob implements Runnable {

    public static final int MAX_USERNAMES = 100;
    private static final String AGENT = "minecraft";
    private final Converter converter;
    private final List<String> usernames;
    private final Map<String, String> results = new HashMap<String, String>();
    private final AtomicBoolean complete = new AtomicBoolean(false);

    public ConversionJob(Converter converter, List<String> usernames) {
        this.converter = converter;
        this.usernames = usernames;
    }

    public void run() {
        // Create a ProfileCriteria for each username
        ProfileCriteria[] criteriaList = new ProfileCriteria[usernames.size()];
        for (int i = 0; i < criteriaList.length; i++) {
            criteriaList[i] = new ProfileCriteria(usernames.get(i), AGENT);
        }

        // Query Mojang's servers and store the results
        Profile[] profiles = converter.getRepository().findProfilesByCriteria(criteriaList);
        for (Profile profile : profiles) {
            results.put(profile.getName(), profile.getId());
        }
        
        complete.set(true);
    }

    public Map<String, String> getResults() {
        return results;
    }

    public boolean isComplete() {
        return complete.get();
    }

}
