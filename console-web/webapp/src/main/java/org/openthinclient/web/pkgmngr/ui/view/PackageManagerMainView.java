package org.openthinclient.web.pkgmngr.ui.view;

import org.openthinclient.web.pkgmngr.ui.design.PackageManagerMainDesign;

import com.vaadin.ui.Component;

/**
 * PackageManagerMainView
 */
public class PackageManagerMainView extends PackageManagerMainDesign {

  /** serialVersionUID */
  private static final long serialVersionUID = 9193433664185414165L;
  
  public PackageListMasterDetailsView getAvailablePackagesView() {
    return availablePackages;
  }

  public PackageListMasterDetailsView getInstalledPackagesView() {
    return installedPackages;
  }
  
  /**
   * Set localized caption to tabs<br/>
   * NOTE: this method relies on Component-structure at file PackageManagerMainDesign.html
   * @param c the child component of Tab
   * @param caption the caption to set
   */
  public void setTabCaption(Component c, String caption) {
     for (int i=0; i<getComponentCount(); i++) {
        Component component = getTab(i).getComponent();
        if (component != null && c != null && component.equals(c.getParent())) {
           getTab(i).setCaption(caption);   
        }
     }
  }
}
