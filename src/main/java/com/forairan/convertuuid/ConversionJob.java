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
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * ConversionJob handles conversion of a single username.
 *
 * @author Devin Ryan
 */
public class ConversionJob implements Runnable {

    private static final String AGENT = "minecraft";
    private final Converter converter;
    private final String username;
    private String uuid;
    private final AtomicBoolean complete = new AtomicBoolean(false);

    public ConversionJob(Converter converter, String username) {
        this.converter = converter;
        this.username = username;
    }

    public void run() {
        Profile[] profiles = converter.getRepository().findProfilesByCriteria(new ProfileCriteria(username, AGENT));
        if (profiles.length == 1) {
            uuid = profiles[0].getId();
        } else {
            uuid = "";
        }
        
        complete.set(true);
        profiles = null;
    }

    public String getUsername() {
        return username;
    }

    public String getUUID() {
        return uuid;
    }

    public boolean isComplete() {
        return complete.get();
    }

}
