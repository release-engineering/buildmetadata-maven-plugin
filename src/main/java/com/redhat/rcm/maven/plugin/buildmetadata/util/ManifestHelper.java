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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.Properties;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.CharUtils;
import org.apache.commons.lang.ObjectUtils;
import org.codehaus.plexus.util.StringUtils;

/**
 * Helper to write the Manifest to.
 */
public class ManifestHelper
{
  // ********************************* Fields *********************************

  // --- constants ------------------------------------------------------------

  // --- members --------------------------------------------------------------

  /**
   * The maximum length of a key in a Manifest file.
   */
  private static final int MANIFEST_KEY_MAX_LENGTH = 70;

  /**
   * The location to write the manifest to.
   */
  private final File manifestFile;

  /**
   * The section to add the keys in the manifest.
   */
  private final String manifestSection;

  // ****************************** Initializer *******************************

  // ****************************** Constructors ******************************

  /**
   * Default constructor.
   *
   * @param manifestFile the location to write the manifest to.
   * @param manifestSection the section to add the keys in the manifest.
   */
  public ManifestHelper(final File manifestFile, final String manifestSection)
  {
    this.manifestFile = manifestFile;
    this.manifestSection = manifestSection;
  }

  // ****************************** Inner Classes *****************************

  // ********************************* Methods ********************************

  // --- init -----------------------------------------------------------------

  // --- get&set --------------------------------------------------------------

  // --- business -------------------------------------------------------------

  /**
   * Creates a Manifest file based on the given properties.
   *
   * @param buildMetaDataProperties the properties to add to the Manifest file
   *          to be written.
   * @throws IOException on any problem writing the file.
   */
  public void createManifest(final Properties buildMetaDataProperties)
    throws IOException
  {
    final Manifest manifest = createManifestInstance(buildMetaDataProperties);

    OutputStream out = null;
    try
    {
      out = new BufferedOutputStream(new FileOutputStream(manifestFile));
      manifest.write(out);
    }
    finally
    {
      IOUtils.closeQuietly(out);
    }
  }

  private Manifest createManifestInstance(
      final Properties buildMetaDataProperties)
  {
    final Manifest manifest = new Manifest();
    final Attributes attributes = fetchAttributes(manifest);
    manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
    for (final Map.Entry<Object, Object> entry : buildMetaDataProperties
        .entrySet())
    {
      final String key = ObjectUtils.toString(entry.getKey(), null);
      if (key.length() > MANIFEST_KEY_MAX_LENGTH)
      {
        continue;
      }

      final String normalizedKey = normalize(key);
      final String value = ObjectUtils.toString(entry.getValue(), null);

      attributes.putValue(normalizedKey, value);
    }

    return manifest;
  }

  private Attributes fetchAttributes(final Manifest manifest)
  {
    if (StringUtils.isBlank(manifestSection) || "Main".equals(manifestSection))
    {
      return manifest.getMainAttributes();
    }

    Attributes attributes = manifest.getAttributes(manifestSection);
    if (attributes == null)
    {
      attributes = new Attributes();
      manifest.getEntries().put(manifestSection, attributes);
    }
    return attributes;
  }

  private static String normalize(final String key)
  {
    final int length = key.length();
    final StringBuilder buffer = new StringBuilder(length);
    for (int i = 0; i < length; i++)
    {
      final char ch = key.charAt(i);
      if (CharUtils.isAsciiAlphanumeric(ch) || ch == '-' || ch == '_')
      {
        buffer.append(ch);
      }
      else
      {
        buffer.append('_');
      }
    }

    return buffer.toString();
  }

  // --- object basics --------------------------------------------------------

}
