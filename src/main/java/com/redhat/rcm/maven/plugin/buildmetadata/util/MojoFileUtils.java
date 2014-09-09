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
package com.redhat.rcm.maven.plugin.buildmetadata.util;

import java.io.File;

import org.apache.maven.plugin.MojoExecutionException;

/**
 * Utilities for Mojos working with files.
 */
public final class MojoFileUtils
{
  // ********************************* Fields *********************************

  // --- constants ------------------------------------------------------------

  // --- members --------------------------------------------------------------

  // ****************************** Initializer *******************************

  // ****************************** Constructors ******************************

  private MojoFileUtils()
  {
  }

  // ****************************** Inner Classes *****************************

  // ********************************* Methods ********************************

  // --- init -----------------------------------------------------------------

  // --- get&set --------------------------------------------------------------

  // --- business -------------------------------------------------------------

  /**
   * Ensures that the given directory exists.
   *
   * @param directory the directory to check. It is not checked, if this is a
   *          directory or a file.
   * @throws MojoExecutionException if the directory does not exist and cannot
   *           be created.
   */
  public static void ensureExists(final File directory)
    throws MojoExecutionException
  {
    if (!directory.exists())
    {
      final boolean created = directory.mkdirs();
      if (!created)
      {
        throw new MojoExecutionException("Cannot create output directory '"
                                         + directory + "'.");
      }
    }
  }

  // --- object basics --------------------------------------------------------

}
