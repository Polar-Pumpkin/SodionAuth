/*
 * This file is part of XenforoLogin, licensed under the GNU Lesser General Public License v3.0 (LGPLv3).
 *
 * You are not permitted to interfere any protection that prevents loading in CatServer
 *
 * Copyright (c) 2020 Mohist-Community.
 *
 */

package red.mohist.xenforologin.core.hasher.implementations;

import red.mohist.xenforologin.core.hasher.HasherTool;


public class PlainSaltHasherTool extends HasherTool {
    public PlainSaltHasherTool(int saltLength) {
        super(saltLength);
    }

    @Override
    public boolean needSalt() {
        return true;
    }

    public boolean verify(String hash, String data, String salt) {
        return hash(hash(data) + salt).equals(hash);
    }
}
