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

package red.mohist.sodionauth.core.services;

import com.google.common.eventbus.Subscribe;
import org.apache.http.impl.client.CloseableHttpClient;
import org.knownspace.minitask.ITaskFactory;
import org.knownspace.minitask.TaskFactory;
import org.knownspace.minitask.locks.UniqueFlag;
import red.mohist.sodionauth.core.events.BootEvent;
import red.mohist.sodionauth.core.events.DownEvent;

import javax.annotation.Nonnull;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

public class AsyncService {
    public ScheduledExecutorService globalScheduledExecutor;
    public ExecutorService executor;
    public ITaskFactory startup;
    public UniqueFlag dbUniqueFlag;
    @Subscribe
    public void onBoot(BootEvent event){
        executor = new ThreadPoolExecutor(1, Runtime.getRuntime().availableProcessors(),
                60L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(),
                new ThreadFactory() {

                    private final AtomicLong serial = new AtomicLong(0L);

                    @Override
                    public Thread newThread(@Nonnull Runnable runnable) {
                        final Thread thread = new Thread(runnable);
                        thread.setName("SodionAuthWorker - " + serial.getAndIncrement());
                        return thread;
                    }
                });
        globalScheduledExecutor = Executors.newScheduledThreadPool(
                Math.max(Runtime.getRuntime().availableProcessors() / 4, 1),
                new ThreadFactory() {

                    private final AtomicLong serial = new AtomicLong(0L);

                    @Override
                    public Thread newThread(@Nonnull Runnable runnable) {
                        final Thread thread = new Thread(runnable);
                        thread.setName("SodionAuthScheduler - " + serial.getAndIncrement());
                        return thread;
                    }
                });
        startup = new TaskFactory(executor);
        dbUniqueFlag = startup.makeUniqueFlag();
    }
    @Subscribe
    public void onDown(DownEvent event){
        executor.shutdown();
        globalScheduledExecutor.shutdown();
        try {
            executor.awaitTermination(30, TimeUnit.SECONDS);
            globalScheduledExecutor.awaitTermination(30, TimeUnit.SECONDS);
        } catch (InterruptedException ignored) {
        }
    }
}
