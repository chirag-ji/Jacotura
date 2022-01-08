/**
 * Jacotura Gradle Plugin
 * <p>
 * BSD 3-Clause License
 * <p>
 * Copyright (c) 2022, Chirag Gupta
 * All rights reserved.
 * <p>
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * <p>
 * 1. Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * <p>
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * <p>
 * 3. Neither the name of the copyright holder nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 * <p>
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package io.github.chiragji.jacotura;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.internal.ConventionMapping;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.plugins.JavaBasePlugin;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.testing.jacoco.plugins.JacocoPlugin;
import org.gradle.testing.jacoco.tasks.JacocoReport;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A class which takes care of integrating the {@code Jacotura} task in the {@code gradle} tasks of the project
 *
 * @author Chirag (chirag-ji)
 * @since 0.0.1
 */
public class JacoturaPlugin implements Plugin<Project> {
    private static final Logger LOGGER = Logging.getLogger(JacoturaPlugin.class);

    @Override
    public void apply(Project project) {
        LOGGER.info("Adding {} task to {}", JacoturaConstants.JACOTURA_TASK_NAME, project);
        Map<String, ActionBroadcast<JacoturaProperties>> actionBroadcastMap = new HashMap<>();
        addExtensions(project, actionBroadcastMap);
        JacoturaTask jacoturaTask = project.getTasks().create(JacoturaConstants.JACOTURA_TASK_NAME, JacoturaTask.class);
        jacoturaTask.setGroup(JavaBasePlugin.VERIFICATION_GROUP);

        configureTask(jacoturaTask, project, actionBroadcastMap);
    }

    private ActionBroadcast<JacoturaProperties> addBroadcaster(Map<String, ActionBroadcast<JacoturaProperties>> actionBroadcastMap, Project project) {
        ActionBroadcast<JacoturaProperties> actionBroadcast = new ActionBroadcast<>();
        actionBroadcastMap.put(project.getPath(), actionBroadcast);
        return actionBroadcast;
    }

    private void addExtensions(Project project, Map<String, ActionBroadcast<JacoturaProperties>> actionBroadcastMap) {
        project.getAllprojects().forEach(p -> {
            ActionBroadcast<JacoturaProperties> broadcast = addBroadcaster(actionBroadcastMap, p);
            p.getExtensions().create(JacoturaConstants.JACOTURA_EXTENSION_NAME, JacoturaExtensions.class, broadcast);
        });
    }

    private void configureTask(JacoturaTask jacoturaTask, Project project, Map<String, ActionBroadcast<JacoturaProperties>> actionBroadcastMap) {
        ConventionMapping mapping = jacoturaTask.getConventionMapping();
        mapping.map("properties", () -> new JacoturaPropertyExtractor(actionBroadcastMap, project).extractProperties());
        jacoturaTask.mustRunAfter(getJavaTestTasks(project));
        jacoturaTask.dependsOn(getJavaCompileTasks(project));
        jacoturaTask.dependsOn(getJacocoTasks(project));
    }

    private static Callable<Iterable<? extends Task>> getJacocoTasks(Project project) {
        return () -> project.getAllprojects().stream()
                .filter(p -> p.getPlugins().hasPlugin(JacocoPlugin.class))
                .map(p -> p.getTasks().withType(JacocoReport.class))
                .flatMap(Collection::stream).collect(Collectors.toList());
    }

    private static Callable<Iterable<? extends Task>> getJavaTestTasks(Project project) {
        return () -> project.getAllprojects().stream().filter(p -> p.getPlugins().hasPlugin(JavaPlugin.class))
                .map(p -> p.getTasks().getByName(JavaPlugin.TEST_TASK_NAME)).collect(Collectors.toList());
    }

    private static Callable<Iterable<? extends Task>> getJavaCompileTasks(Project project) {
        return () -> project.getAllprojects().stream()
                .filter(p -> p.getPlugins().hasPlugin(JavaPlugin.class))
                .flatMap(p -> Stream.of(p.getTasks().getByName(JavaPlugin.COMPILE_JAVA_TASK_NAME),
                        p.getTasks().getByName(JavaPlugin.COMPILE_TEST_JAVA_TASK_NAME)))
                .collect(Collectors.toList());
    }
}
