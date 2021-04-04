/*
 * Copyright 2021 Mohist-Community
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package red.mohist.sodionauth.core.utils.channel.proxy.clientPacket;

import red.mohist.sodionauth.core.utils.channel.proxy.BadSignException;

import java.nio.ByteBuffer;

public class HelloServerPacket extends ClientPacket {
    public static final int id = 0;
    public static final int size =
            Integer.SIZE // id
        ;
    public static final int bytes = size / Byte.SIZE;

    public HelloServerPacket(byte[] data) throws BadSignException {
        if(!verify(data)){
            throw new BadSignException();
        }
    }

    public HelloServerPacket(){

    }

    @Override
    protected byte[] encode() {
        ByteBuffer buffer = ByteBuffer.allocate(bytes);
        buffer.putInt(id);
        return buffer.array();
    }

    @Override
    public int getSize() {
        return size;
    }
}
