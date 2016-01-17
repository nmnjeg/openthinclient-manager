package org.openthinclient.wizard.install;

import org.openthinclient.pkgmgr.PackageManagerConfiguration;
import org.openthinclient.pkgmgr.PackageManagerFactory;
import org.openthinclient.util.dpkg.DPKGPackageManager;

public class CreatePackageManagerUpdatedPackageListInstallStep extends AbstractInstallStep {

  @Override
  public String getName() {
    return "Download the latest packages lists";
  }

  @Override
  protected void doExecute(InstallContext installContext) throws Exception {

    if (true)
      // FIXME remove this line once the database setup has been implemented and the startup is actually possible.
      throw new UnsupportedOperationException("Unsupported right now.");

    final PackageManagerConfiguration packageManagerConfiguration = installContext.getManagerHome().getConfiguration(PackageManagerConfiguration.class);

    // create a new package manager instance
    final DPKGPackageManager packageManager = PackageManagerFactory.createPackageManager(packageManagerConfiguration, null);

    installContext.setPackageManager(packageManager);

    log.info("Downloading the latest packages list");
    // download the packages.gz and update our local database
    packageManager.updateCacheDB();


  }
}
