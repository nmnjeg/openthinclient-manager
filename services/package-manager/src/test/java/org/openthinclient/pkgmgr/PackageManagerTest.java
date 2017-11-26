package org.openthinclient.pkgmgr;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openthinclient.pkgmgr.db.Package;
import org.openthinclient.pkgmgr.db.PackageRepository;
import org.openthinclient.pkgmgr.db.Source;
import org.openthinclient.pkgmgr.db.SourceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.openthinclient.pkgmgr.PackageTestUtils.createPackage;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {SimpleTargetDirectoryPackageManagerConfiguration.class})
public class PackageManagerTest {

  private static final Logger logger = LoggerFactory.getLogger(PackageManagerTest.class);

  @Autowired
  ObjectFactory<PackageManagerConfiguration> packageManagerConfigurationFactory;
  @Autowired
  SourceRepository sourceRepository;
  @Autowired
  PackageRepository packageRepository;
  @Autowired
  PackageManager packageManager;

  @Before
  public void clean() {
      packageRepository.deleteAll();
  }

  @Test
  public void testInstalledPackages() throws PackageManagerException {

    Source source = createSource();

    Package foo  = createPackage("foo", "2.0-1", true, source);
    Package foo1 = createPackage("foo", "2.1-1", false, source);

    packageRepository.saveAndFlush(foo);
    packageRepository.saveAndFlush(foo1);

    Collection<Package> installedPackages = packageManager.getInstalledPackages();
    assertNotNull(installedPackages);
    assertEquals(1, installedPackages.size());
    assertEquals(foo, installedPackages.iterator().next());

    Collection<Package> installablePackages = packageManager.getInstallablePackages();
    assertNotNull(installablePackages);
    assertEquals(0, installablePackages.size());

    Collection<Package> allInstallablePackages = packageManager.getAllInstallablePackages();
    assertNotNull(allInstallablePackages);
    assertEquals(1, allInstallablePackages.size());
    assertEquals(foo1, allInstallablePackages.iterator().next());

    Collection<Package> withoutInstalled = packageManager.getInstallablePackagesWithoutInstalledOfSameVersion();
    assertEquals(1, withoutInstalled.size());
    assertEquals(foo1, withoutInstalled.iterator().next());

    Collection<Package> updateablePackages = packageManager.getUpdateablePackages();
    assertEquals(1, updateablePackages.size());
    assertEquals(foo1, updateablePackages.iterator().next());

  }

    @Test
    public void testPackageVersionMatrixA() throws PackageManagerException {

        Source source = createSource();

        Package foo  = createPackage("foo", "2.0-1", true, source);
        Package foo1 = createPackage("foo", "2.1-1", false, source);
        Package foo2 = createPackage("foo", "2.1-2", false, source);

        packageRepository.saveAndFlush(foo);
        packageRepository.saveAndFlush(foo1);
        packageRepository.saveAndFlush(foo2);

        Collection<Package> installedPackages = packageManager.getInstalledPackages();
        assertNotNull(installedPackages);
        assertEquals(1, installedPackages.size());
        assertEquals(foo, installedPackages.iterator().next());

        Collection<Package> installablePackages = packageManager.getInstallablePackages();
        assertNotNull(installablePackages);
        assertEquals(0, installablePackages.size());

        Collection<Package> updateablePackages = packageManager.getUpdateablePackages();
        assertEquals(2, updateablePackages.size());
        assertTrue(updateablePackages.contains(foo1));
        assertTrue(updateablePackages.contains(foo2));

    }

    @Test
    public void testPackageVersionMatrixA1() throws PackageManagerException {

        Source source = createSource();

        Package foo  = createPackage("foo", "2.0-1", false, source);
        Package foo1 = createPackage("foo", "2.1-1", true, source);
        Package foo2 = createPackage("foo", "2.1-2", false, source);

        packageRepository.saveAndFlush(foo);
        packageRepository.saveAndFlush(foo1);
        packageRepository.saveAndFlush(foo2);

        Collection<Package> installedPackages = packageManager.getInstalledPackages();
        assertNotNull(installedPackages);
        assertEquals(1, installedPackages.size());
        assertEquals(foo1, installedPackages.iterator().next());

        Collection<Package> installablePackages = packageManager.getInstallablePackages();
        assertNotNull(installablePackages);
        assertEquals(0, installablePackages.size());

        Collection<Package> updateablePackages = packageManager.getUpdateablePackages();
        assertEquals(1, updateablePackages.size());
        assertEquals(foo2, updateablePackages.iterator().next());
    }

    @Test
    public void testPackageVersionMatrixA2() throws PackageManagerException {

        Source source = createSource();

        Package foo  = createPackage("foo", "2.0-1", false, source);
        Package foo1 = createPackage("foo", "2.1-1", false, source);
        Package foo2 = createPackage("foo", "2.1-2", true, source);

        packageRepository.saveAndFlush(foo);
        packageRepository.saveAndFlush(foo1);
        packageRepository.saveAndFlush(foo2);

        Collection<Package> installedPackages = packageManager.getInstalledPackages();
        assertNotNull(installedPackages);
        assertEquals(1, installedPackages.size());
        assertEquals(foo2, installedPackages.iterator().next());

        Collection<Package> installablePackages = packageManager.getInstallablePackages();
        assertNotNull(installablePackages);
        assertEquals(0, installablePackages.size());

        Collection<Package> updateablePackages = packageManager.getUpdateablePackages();
        assertEquals(0, updateablePackages.size());
    }

  /**
   * Creates a Source for packages
   * @return the source
   */
  private Source createSource() {
    Source source = new Source();
    source.setDescription("description");
    source.setEnabled(true);
    try {
      source.setUrl(new URL("http://localhost"));
    } catch (MalformedURLException exception) {
      exception.printStackTrace();
    }
    sourceRepository.saveAndFlush(source);
    return source;
  }

}
