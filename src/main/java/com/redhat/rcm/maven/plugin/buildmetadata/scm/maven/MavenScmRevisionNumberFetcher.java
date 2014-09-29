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
package com.redhat.rcm.maven.plugin.buildmetadata.scm.maven;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.apache.maven.scm.ChangeSet;
import org.apache.maven.scm.ScmFile;
import org.apache.maven.scm.ScmFileSet;
import org.apache.maven.scm.ScmResult;
import org.apache.maven.scm.ScmVersion;
import org.apache.maven.scm.command.changelog.ChangeLogScmResult;
import org.apache.maven.scm.command.diff.DiffScmResult;
import org.apache.maven.scm.command.status.StatusScmResult;
import org.apache.maven.scm.manager.NoSuchScmProviderException;
import org.apache.maven.scm.manager.ScmManager;
import org.apache.maven.scm.provider.ScmProvider;
import org.apache.maven.scm.repository.ScmRepository;
import org.codehaus.plexus.util.StringUtils;
import com.redhat.rcm.maven.plugin.buildmetadata.AbstractBuildMojo;
import com.redhat.rcm.maven.plugin.buildmetadata.scm.LocallyModifiedInfo;
import com.redhat.rcm.maven.plugin.buildmetadata.scm.Revision;
import com.redhat.rcm.maven.plugin.buildmetadata.scm.RevisionNumberFetcher;
import com.redhat.rcm.maven.plugin.buildmetadata.scm.ScmException;

/**
 * Implementation on the Maven SCM implementation to fetch the latest revision
 * number.
 *
 * @author <a href="mailto:robert.reiner@smartics.de">Robert Reiner</a>
 */
public final class MavenScmRevisionNumberFetcher extends AbstractBuildMojo implements
    RevisionNumberFetcher
{
  // ********************************* Fields *********************************

  // --- constants ------------------------------------------------------------

  // --- members --------------------------------------------------------------

  /**
   * The SCM manager to access to SCM system.
   */
  private final ScmManager scmManager;

  /**
   * The information to connect to the SCM system.
   */
  private final ScmConnectionInfo scmConnectionInfo;

  /**
   * Information to retrieve the revision information from the SCM after the
   * connection is established.
   */
  private final ScmAccessInfo scmAccessInfo;

  // ****************************** Initializer *******************************

  // ****************************** Constructors ******************************

  /**
   * Default constructor.
   *
   * @param scmManager the SCM manager to access to SCM system.
   * @param scmConnectionInfo the information to connect to the SCM system.
   * @param scmAccessInfo the value for scmAccessInfo.
   */
  public MavenScmRevisionNumberFetcher(final ScmManager scmManager,
      final ScmConnectionInfo scmConnectionInfo,
      final ScmAccessInfo scmAccessInfo)
  {
    this.scmManager = scmManager;
    this.scmConnectionInfo = scmConnectionInfo;
    this.scmAccessInfo = scmAccessInfo;
  }

  // ****************************** Inner Classes *****************************

  /**
   * Wrapper to access the changed files.
   */
  private interface ScmResultWrapper
  {
    boolean isSuccess();

    List<ScmFile> getChangedFiles();
  }

  /**
   * Default base implementation.
   */
  private abstract static class AbstractScmResultWrapper implements
      ScmResultWrapper
  {
    /**
     * Reference to the result.
     */
    private final ScmResult result;

    protected AbstractScmResultWrapper(final ScmResult result)
    {
      this.result = result;
    }

    @Override
    public boolean isSuccess()
    {
      return result.isSuccess();
    }

    @Override
    public String toString()
    {
      return result.getProviderMessage() + ": " + result.getCommandOutput();
    }

  }

  // ********************************* Methods ********************************

  // --- init -----------------------------------------------------------------

  // --- get&set --------------------------------------------------------------

  // --- business -------------------------------------------------------------

  /**
   * {@inheritDoc}
   */
  @Override
public Revision fetchLatestRevisionNumber() throws ScmException
  {
      if (getLog().isDebugEnabled())
      {
          getLog().debug("  Fetching latest revision number.\n    "
                  + this.scmConnectionInfo + "\n    " + this.scmAccessInfo);
      }

      final ScmRepository repository =
              scmConnectionInfo.createRepository(scmManager);
      final ScmProvider provider = createScmProvider(repository);
      final ChangeLogScmResult result =
              scmAccessInfo.fetchChangeLog(repository, provider);

      Revision revision = null;

      if (result != null && result.isSuccess())
      {
          final List<ChangeSet> changeLogSets = result.getChangeLog().getChangeSets();

          if (changeLogSets.size() > 0)
          {
              revision = new StringRevision (changeLogSets.get(0).getRevision(),
                      changeLogSets.get(0).getDate());
              if (getLog().isDebugEnabled())
              {
                  getLog().debug("Found revision '" + revision + "'.");
              }
          }
      }
      else if (getLog().isDebugEnabled())
      {
          getLog().debug("No revision information found.");
      }
      return revision;
  }

  /**
   * {@inheritDoc}
   */
  @Override
public LocallyModifiedInfo containsModifications(final ScmFileSet fileSet)
    throws ScmException
  {
    if (getLog().isDebugEnabled())
    {
      getLog().debug("  Fetching modification information.\n    "
                + this.scmConnectionInfo + "\n    " + this.scmAccessInfo);
    }

    try
    {
      final ScmRepository repository =
          scmConnectionInfo.createRepository(scmManager);
      final ScmProvider provider = createScmProvider(repository);

      final ScmResultWrapper result = execute(fileSet, repository, provider);
      if (result.isSuccess())
      {
        return createLocallyModifiedInfo(result);
      }
      else
      {
        final String message = result.toString();
        if (getLog().isDebugEnabled())
        {
          getLog().debug(message);
        }

        throw new ScmException(message);
      }
    }
    catch (final org.apache.maven.scm.ScmException e)
    {
      throw new ScmException(e);
    }
  }

  private ScmResultWrapper execute(final ScmFileSet fileSet,
      final ScmRepository repository, final ScmProvider provider)
    throws org.apache.maven.scm.ScmException
  {
    final ScmVersion remoteVersion = scmConnectionInfo.getRemoteVersion();
    final ScmResultWrapper result;
    if (remoteVersion != null)
    {
      final ScmVersion localVersion = null;
      result =
          createScmDiffResultWrapper(provider.diff(repository, fileSet,
              remoteVersion, localVersion));
    }
    else
    {
      result =
          createScmStatusResultWrapper(provider.status(repository, fileSet));
    }
    return result;
  }

  private ScmResultWrapper createScmDiffResultWrapper(final DiffScmResult diff)
  {
    return new AbstractScmResultWrapper(diff)
    {
      @Override
    public List<ScmFile> getChangedFiles()
      {
        return diff.getChangedFiles();
      }
    };
  }

  private ScmResultWrapper createScmStatusResultWrapper(
      final StatusScmResult diff)
  {
    return new AbstractScmResultWrapper(diff)
    {
      @Override
    public List<ScmFile> getChangedFiles()
      {
        return diff.getChangedFiles();
      }
    };
  }

  private LocallyModifiedInfo createLocallyModifiedInfo(
      final ScmResultWrapper result)
  {
    final List<ScmFile> changedFiles = filter(result.getChangedFiles());
    final boolean locallyModified = !changedFiles.isEmpty();
    if (getLog().isDebugEnabled())
    {
      getLog().debug("  Modifications have" + (locallyModified ? "" : " not")
                + " been found.");
    }
    return new LocallyModifiedInfo(locallyModified, locallyModified
        ? toString(createSortedFiles(changedFiles)) : null);
  }

  private static Set<ScmFile> createSortedFiles(final List<ScmFile> changedFiles)
  {
    final Set<ScmFile> set = new TreeSet<ScmFile>(new Comparator<ScmFile>()
    {
      @Override
    public int compare(final ScmFile o1, final ScmFile o2)
      {
        return o2.compareTo(o1);
      }
    });

    set.addAll(changedFiles);

    return set;
  }

  private List<ScmFile> filter(final List<ScmFile> files)
  {
    if (this.scmAccessInfo.isIgnoreDotFilesInBaseDir())
    {
      filterDotFiles(files);
    }
    return files;
  }

  private void filterDotFiles(final List<ScmFile> files)
  {
    for (final Iterator<ScmFile> i = files.iterator(); i.hasNext();)
    {
      final ScmFile file = i.next();
      final String path = file.getPath();
      if (path.length() > 0 && path.charAt(0) == '.')
      {
        i.remove();
      }
    }
  }

  /**
   * Renders the files to a blank separated list of file names.
   *
   * @param items the file items.
   * @return the string representation of the files.
   */
  private String toString(final Collection<?> items)
  {
    final StringBuilder buffer = new StringBuilder(512);
    for (final Object item : items)
    {
      buffer.append(item).append(' ');
    }
    return StringUtils.chomp(buffer.toString());
  }


  /**
   * Creates the provider instance to access the given repository.
   *
   * @param repository the repository to access with the provider to be created.
   * @return the provider to access the given repository.
   * @throws ScmException if the provider cannot be created.
   */
  private ScmProvider createScmProvider(final ScmRepository repository)
    throws ScmException
  {
    try
    {
      final ScmProvider provider =
          scmManager.getProviderByRepository(repository);
      return provider;
    }
    catch (final NoSuchScmProviderException e)
    {
      throw new ScmException("Cannot create SCM provider.", e);
    }
  }

  // --- object basics --------------------------------------------------------

}
