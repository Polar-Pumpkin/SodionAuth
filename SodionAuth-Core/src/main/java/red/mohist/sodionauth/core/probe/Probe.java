/*
 * Copyright 2020 Mohist-Community
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

package red.mohist.sodionauth.core.probe;

import java.net.URL;

public class Probe {

    public static String getJarPath() {
        final URL probeResource = Probe.class.getClassLoader().getResource("probeResource");
        if (probeResource == null) throw new IllegalStateException("Could not find probeResource");
        return probeResource.getPath().split("!")[0].substring(6);
    }

}
