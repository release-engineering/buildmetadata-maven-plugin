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
package de.smartics.maven.plugin.buildmetadata.util;

import java.io.File;

import org.sonatype.plexus.components.sec.dispatcher.DefaultSecDispatcher;
import org.sonatype.plexus.components.sec.dispatcher.SecDispatcher;
import org.sonatype.plexus.components.sec.dispatcher.SecDispatcherException;

/**
 * Helper to decrypt passwords from the settings.
 */
public final class SettingsDecrypter
{
  // ********************************* Fields *********************************

  // --- constants ------------------------------------------------------------

  // --- members --------------------------------------------------------------

  /**
   * The Maven infrastructure to decrypt passwords from the settings.
   */
  private final SecDispatcher securityDispatcher;

  /**
   * The location of the <code>settings-security.xml</code>.
   */
  private final String settingsSecurityLocation;

  // ****************************** Initializer *******************************

  // ****************************** Constructors ******************************

  /**
   * Default constructor.
   *
   * @param securityDispatcher the Maven infrastructure to decrypt passwords
   *          from the settings.
   * @param settingsSecurityLocation the location of the
   *          <code>settings-security</code>.
   */
  public SettingsDecrypter(final SecDispatcher securityDispatcher,
      final String settingsSecurityLocation)
  {
    this.securityDispatcher = securityDispatcher;
    this.settingsSecurityLocation = init(settingsSecurityLocation);
  }

  // ****************************** Inner Classes *****************************

  // ********************************* Methods ********************************

  // --- init -----------------------------------------------------------------

  private static String init(final String settingsSecurityLocation)
  {
    final File file = new File(settingsSecurityLocation);
    if (!file.canRead())
    {
      return null;
    }

    System.setProperty(DefaultSecDispatcher.SYSTEM_PROPERTY_SEC_LOCATION,
        settingsSecurityLocation);
    return settingsSecurityLocation;
  }

  // --- get&set --------------------------------------------------------------

  // --- business -------------------------------------------------------------

  /**
   * Decrypts the given value if the security dispatcher is initialized with a
   * valid configuration.
   *
   * @param encrypted the value to decrypt.
   * @return the decrypted value or the unchanged {@code encrypted}.
   * @throws SecDispatcherException if the decryption failed.
   */
  public String decrypt(final String encrypted) throws SecDispatcherException
  {
    if (settingsSecurityLocation != null)
    {
      return securityDispatcher.decrypt(encrypted);
    }
    return encrypted;
  }

  // --- object basics --------------------------------------------------------

}
