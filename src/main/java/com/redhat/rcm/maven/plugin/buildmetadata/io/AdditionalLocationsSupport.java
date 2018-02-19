/*
 * Copyright 2006-2014 smartics, Kronseder & Reiner GmbH
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
package com.redhat.rcm.maven.plugin.buildmetadata.io;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

import com.redhat.rcm.maven.plugin.buildmetadata.util.MojoFileUtils;

/**
 * Helper to copy buildmetadata files to additional locations to be grabbed by
 * secondary packers. For instance this will copy the file to
 * <code>generated-sources</code>.
 */
public class AdditionalLocationsSupport
{
  // ********************************* Fields *********************************

  // --- constants ------------------------------------------------------------

  // --- members --------------------------------------------------------------

  /**
   * The Maven project.
   */
  private final MavenProject project;

  /**
   * The configuration from the plugin.
   */
  private final Config config;

  // ****************************** Initializer *******************************

  // ****************************** Constructors ******************************

  /**
   * Default constructor.
   *
   * @param project the Maven project.
   * @param config the configuration from the plugin.
   */
  public AdditionalLocationsSupport(final MavenProject project,
      final Config config)
  {
    this.project = project;
    this.config = config;
  }

  // ****************************** Inner Classes *****************************

  /**
   * Collects the configuration for the plugin.
   */
  public static class Config
  {
    /**
     * The flag to control whether or not to copy to
     * <code>generated-sources</code>.
     */
    private boolean addToGeneratedSources;

    /**
     * The list of locations the report files are to be copied to. If it is not
     * absolute, the subfolder <code>META-INF</code> is appended.
     */
    private List<String> addToLocations;

    /**
     * Sets the flag to control whether or not to copy to
     * <code>generated-sources</code>.
     *
     * @param addToGeneratedSources the flag to control whether or not to copy
     *          to <code>generated-sources</code>.
     * @return a reference to this configuration.
     */
    public Config setAddToGeneratedSources(final boolean addToGeneratedSources)
    {
      this.addToGeneratedSources = addToGeneratedSources;
      return this;
    }

    /**
     * Sets the list of locations the report files are to be copied to. If it is
     * not absolute, the subfolder <code>META-INF</code> is appended.
     *
     * @param addToLocations the list of locations the report files are to be
     *          copied to.
     * @return a reference to this configuration.
     */
    public Config setAddToLocations(final List<String> addToLocations)
    {
      this.addToLocations = addToLocations;
      return this;
    }
  }

  // ********************************* Methods ********************************

  // --- init -----------------------------------------------------------------

  // --- get&set --------------------------------------------------------------

  // --- business -------------------------------------------------------------

  /**
   * Handles the given file to be copied to the configured locations.
   *
   * @param file the file to copy.
   * @throws MojoExecutionException on any copy problem.
   */
  public void handle(final File file) throws MojoExecutionException
  {
    if (isToAttachSources())
    {
      addFileToSources(file, "generated-sources", true);
    }

    if (config.addToLocations != null)
    {
      for (final String location : config.addToLocations)
      {
        addFileToSources(file, location, false);
      }
    }
  }

  @SuppressWarnings("unchecked")
  private boolean isToAttachSources()
  {
    if (!config.addToGeneratedSources)
    {
      return false;
    }

    final Map<String, Plugin> plugins = project.getBuild().getPluginsAsMap();

    final Plugin sourcesPlugin =
        plugins.get(Plugin.constructKey("org.apache.maven.plugins",
            "maven-source-plugin"));
    if (sourcesPlugin != null)
    {
      // TODO: Should we check if the plugin is to be run?
      return true;
    }

    return false;
  }

  private void addFileToSources(final File propertiesFile,
      final String targetLocation, final boolean attach)
    throws MojoExecutionException
  {
    final File testFile = new File(targetLocation);
    final File targetLocationDir;
    final File generatedSourcesMetaInfDir;

    if (testFile.isAbsolute())
    {
      targetLocationDir = null;
      generatedSourcesMetaInfDir = testFile;
    }
    else
    {
      targetLocationDir =
          new File(project.getBuild().getDirectory(), targetLocation);
      generatedSourcesMetaInfDir = new File(targetLocationDir, "META-INF");
    }

    MojoFileUtils.ensureExists(generatedSourcesMetaInfDir);
    try
    {
      final File propertiesFileInSources =
          new File(generatedSourcesMetaInfDir, propertiesFile.getName());
      if ( ! propertiesFile.getAbsolutePath().equals(propertiesFileInSources.getAbsolutePath()))
      {
        FileUtils.copyFile(propertiesFile, propertiesFileInSources);
      }
      if (attach && targetLocationDir != null)
      {
        project.addCompileSourceRoot(targetLocationDir.getAbsolutePath());
      }
    }
    catch (final IOException e)
    {
      throw new MojoExecutionException(
          "Cannot copy properties to generated sources.", e);
    }
  }

  // --- object basics --------------------------------------------------------

}
